package com.g4mesoft.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.g4mesoft.Camera3D;
import com.g4mesoft.graphics3d.FloatArrayVertexProvider;
import com.g4mesoft.graphics3d.IVertexProvider;
import com.g4mesoft.graphics3d.PixelRenderer3D;
import com.g4mesoft.graphics3d.Shape3D;
import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.model.TexturedModel;
import com.g4mesoft.ship.ExhaustShader3D;
import com.g4mesoft.ship.ShipExhaust;
import com.g4mesoft.ship.ShipShader3D;
import com.g4mesoft.ship.SpaceShip;

public class SpaceWorld {

	private static final float[] LASER_VERTICES = new float[] {
		// FRONT
		1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f,

		// BACK
		0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		1.0f, 1.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		
		// BOTTOM
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 1.0f,
		
		// TOP 
		0.0f, 1.0f, 0.0f,
		0.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 0.0f,
		
		// LEFT
		0.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 0.0f,
		
		// RIGHT
		1.0f, 0.0f, 0.0f,
		1.0f, 1.0f, 0.0f,
		1.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f
	};
	
	private final Camera3D camera;
	
	private final List<LaserProjectile> projectiles;
	private final List<SpaceShip> ships;
	
	private final Random random;
	
	private final IVertexProvider laserVertices;
	private final ProjectileShader3D projectileShader;
	
	private final ShipShader3D shipShader;
	private final ShipGlowShader3D shipGlowShader;

	private final ExhaustShader3D exhaustShader;
	
	private float shipGlow;
	private float prevShipGlow;
	
	public SpaceWorld(Camera3D camera) {
		this.camera = camera;

		projectiles = new ArrayList<LaserProjectile>();
		ships = new ArrayList<SpaceShip>();
		
		random = new Random();
		
		laserVertices = new FloatArrayVertexProvider(LASER_VERTICES, 3, Shape3D.QUADS);
		projectileShader = new ProjectileShader3D(camera);
		
		shipShader = new ShipShader3D(camera);
		shipGlowShader = new ShipGlowShader3D(camera);
		
		exhaustShader = new ExhaustShader3D(camera);
	}
	
	public void addProjectile(Vec3f pos, Vec3f forward, Vec3f upward, int color) {
		projectiles.add(new LaserProjectile(pos, forward, upward, color));
	}
	
	public void addSpaceShip(SpaceShip ship) {
		ships.add(ship);
		
		ship.onAdded(this);
	}

	public void removeSpaceShip(SpaceShip ship) {
		if (ships.remove(ship))
			ship.onRemoved();
	}
	
	public void update() {
		for (SpaceShip ship : ships)
			ship.update();

		Iterator<LaserProjectile> itr = projectiles.iterator();
		while (itr.hasNext()) {
			LaserProjectile projectile = itr.next();
			projectile.tick();
			if (projectile.isDead())
				itr.remove();
		}
		
		if (shipGlow >= MathUtils.PI * 2.0f)
			shipGlow -= MathUtils.PI * 2.0f;
		prevShipGlow = shipGlow;
		shipGlow += 0.25f;
	}
	
	public void renderWorld(PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		for (SpaceShip ship : ships)
			ship.render(renderer3D, glowRenderer, dt);
		
		renderProjectiles(renderer3D, glowRenderer, dt);
	}
	
	public void renderProjectiles(PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		Mat4f laserRot = new Mat4f();
		Vec3f laserPos = new Vec3f();

		Vec3f sideward = new Vec3f();
		
		Mat4f modlMatrix = camera.getModelMatrix();
		for (LaserProjectile laser : projectiles) {
			laser.getRenderPosition(laserPos, dt);
			
			Vec3f forward = laser.getForward();
			Vec3f upward = laser.getUpward();
			upward.cross(forward, sideward);
			
			laserRot.set(sideward, upward, forward, false);
			
			modlMatrix.toIdentity();
			modlMatrix.setTranslation(laserPos);
			modlMatrix.mul(laserRot);
			modlMatrix.scale(1.0f, 1.0f, 70.0f);
			modlMatrix.translate(-0.5f);
	
			projectileShader.setProjectileColor(laser.getColor());
			
			renderer3D.setShader(projectileShader);
			renderer3D.drawVertices(laserVertices);
			
			// Draw the same thing to the glow shader
			modlMatrix.translate(0.5f);
			modlMatrix.scale(6.0f, 6.0f, 1.0f);
			modlMatrix.translate(-0.5f);

			glowRenderer.setShader(projectileShader);
			glowRenderer.drawVertices(laserVertices);
		}
	}
	
	private void updateShipModelMatrix(SpaceShip ship, Mat4f modlMatrix, float dt) {
		Vec3f pos = ship.getPos();
		Vec3f prevPos = ship.getPrevPos();
		
		Mat4f rotMat = ship.getRotationMatrix();
		Mat4f prevRotMat = ship.getPrevRotationMatrix();
		
		float sx = prevPos.x + dt * (pos.x - prevPos.x);
		float sy = prevPos.y + dt * (pos.y - prevPos.y);
		float sz = prevPos.z + dt * (pos.z - prevPos.z);
		Mat4f shipRotMat = prevRotMat.interpolate(rotMat, dt);

		modlMatrix.toIdentity().translate(sx, sy, sz).mul(shipRotMat);
	}
	
	public void renderShip(SpaceShip ship, PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		Mat4f modlMatrix = camera.getModelMatrix();
		updateShipModelMatrix(ship, modlMatrix, dt);
		
		modlMatrix.translate(0.0f, -20.0f, -15.0f).rotateY(180.0f);
		
		renderer3D.setShader(shipShader);
		glowRenderer.setShader(shipGlowShader);
		
		float shipGlowTimer = prevShipGlow + (shipGlow - prevShipGlow) * dt;
		float brightness = MathUtils.cos(shipGlowTimer) * 0.1f + 0.4f;
		brightness += ship.getMoveVector().length() * 0.02f;
		shipGlowShader.setBrightness(Math.min(brightness, 1.0f));
		
		TexturedModel model = ship.getShipModel().getModel();
		shipShader.setTexture(model.getTexture());
		shipGlowShader.setTexture(model.getGlowTexture());
		
		IVertexProvider vertices = model.getVertices();
		renderer3D.drawVertices(vertices);
		glowRenderer.drawVertices(vertices);
	}
	
	public void renderExhaust(SpaceShip ship, ShipExhaust[] shipExhausts, PixelRenderer3D glowRenderer, float dt) {
		Mat4f shipRotViewMat = new Mat4f();
		updateShipModelMatrix(ship, shipRotViewMat, dt);
		
		float sx = shipRotViewMat.m30;
		float sy = shipRotViewMat.m31;
		float sz = shipRotViewMat.m32;
		
		shipRotViewMat.setTranslation(0.0f);
		
		glowRenderer.setShader(exhaustShader);
		exhaustShader.setExhaustColor(ship.getShipModel().getMaterial().getExhaustColor());
		for (ShipExhaust exhaust : shipExhausts)
			exhaust.render(glowRenderer, sx, sy, sz, shipRotViewMat, dt);
	}

	public Random getRandom() {
		return random;
	}
}
