package com.g4mesoft.ship;

import com.g4mesoft.Sounds;
import com.g4mesoft.graphics3d.PixelRenderer3D;
import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.model.ShipModel;
import com.g4mesoft.world.SpaceWorld;

public class SpaceShip {

	protected final ShipModel model;
	protected final IShipController controller;
	
	protected final Mat4f rotMat;
	protected final Mat4f prevRotMat;
	protected final Vec3f pos;
	protected final Vec3f prevPos;
	
	private int laserIndex;
	private final ShipExhaust[] shipExhausts;
	
	protected SpaceWorld world;
	
	public SpaceShip(ShipModel model, IShipController controller) {
		this.model = model;
		this.controller = controller;
		
		rotMat = new Mat4f();
		prevRotMat = new Mat4f();
		pos = new Vec3f();
		prevPos = new Vec3f();
		
		laserIndex = 0;
		
		Vec3f[] exhaustPositions = model.getExhaustPositions();
		shipExhausts = new ShipExhaust[exhaustPositions.length];
		for (int i = 0; i < exhaustPositions.length; i++)
			shipExhausts[i] = new ShipExhaust(this, exhaustPositions[i]);
		
		world = null;
	}
	
	public void onAdded(SpaceWorld world) {
		this.world = world;
	}

	public void onRemoved() {
		world = null;
	}
	
	public void update() {
		prevRotMat.set(rotMat);
		prevPos.set(pos);
		
		controller.checkInput(this);
		
		Vec3f moveVector = getMoveVector();
		if (rotMat.getCol2(new Vec3f()).dot(moveVector) < -0.1f) {
			for (ShipExhaust exhaust : shipExhausts)
				exhaust.addExhaust();
		} else {
			for (ShipExhaust exhaust : shipExhausts)
				exhaust.removeExhaust();
		}
		
		float movedDistance = moveVector.length();
		for (ShipExhaust exhaust : shipExhausts)
			exhaust.setSizeScale(movedDistance / 15.0f);
		
	}
	
	protected void shootLaser() {
		Vec3f[] laserPositions = model.getLaserPositions();
		if (laserPositions.length > 0) {
			Vec3f laserPos = rotMat.mul(laserPositions[laserIndex]).add(pos);
			
			laserIndex++;
			if (laserIndex >= laserPositions.length)
				laserIndex = 0;
			
			Vec3f forward = rotMat.getCol2(new Vec3f()).mul(-1.0f);
			Vec3f upward = rotMat.getCol1(new Vec3f());
			world.addProjectile(laserPos, forward, upward, model.getMaterial().getLaserColor());
			
			Sounds.play(Sounds.LASER_SOUND, 1.0f + world.getRandom().nextFloat() * 0.1f);
		}
	}
	
	public void rotateShip(float rx, float ry, float rz) {
		rotMat.rotateZ(rz).rotateY(ry).rotateX(rx);
	}

	public void move(Vec3f velocity) {
		pos.add(velocity);
	}
	
	public void render(PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		world.renderShip(this, renderer3D, glowRenderer, dt);
		world.renderExhaust(this, shipExhausts, glowRenderer, dt);
	}
	
	public ShipModel getShipModel() {
		return model;
	}
	
	public Mat4f getPrevRotationMatrix() {
		return prevRotMat;
	}
	
	public Mat4f getRotationMatrix() {
		return rotMat;
	}

	public Vec3f getPrevPos() {
		return prevPos;
	}
	
	public Vec3f getPos() {
		return pos;
	}

	public Vec3f getMoveVector() {
		return pos.copy().sub(prevPos);
	}
}
