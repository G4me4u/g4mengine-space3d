package com.g4mesoft;

import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.ship.SpaceShip;

public class Camera3D {

	private static final float BASE_FOV = 70.0f;
	private static final float NEAR = 0.1f;
	private static final float FAR = 5000.0f;
	
	private final Space3DApp app;
	
	private float aspectRatio;
	private float fov;
	private float prevFov;
	
	private final Mat4f projMatrix;
	private final Mat4f viewMatrix;
	private final Mat4f modlMatrix;
	
	private final Mat4f viewRotMat;
	private final Mat4f prevViewRotMat;
	private final Vec3f viewPos;
	private final Vec3f prevViewPos;

	private final Mat4f invViewRotMat;
	
	public Camera3D(Space3DApp app) {
		this.app = app;
		
		aspectRatio = 1.0f;
		prevFov = fov = BASE_FOV;
		
		projMatrix = new Mat4f();
		viewMatrix = new Mat4f();
		modlMatrix = new Mat4f();
		
		viewRotMat = new Mat4f();
		prevViewRotMat = new Mat4f();
		viewPos = new Vec3f();
		prevViewPos = new Vec3f();
		
		invViewRotMat = new Mat4f();
	}
	
	public void update() {
		prevViewRotMat.set(viewRotMat);
		prevViewPos.set(viewPos);
		
		prevFov = fov;
		
		SpaceShip ship = app.getPlayerShip();
		viewRotMat.interpolate(ship.getRotationMatrix().inverseCopy(), 0.5f, viewRotMat);

		Vec3f shipPos = ship.getPos();
		viewPos.x += (-shipPos.x - viewPos.x) * 0.2f;
		viewPos.y += (-shipPos.y - viewPos.y) * 0.2f;
		viewPos.z += (-shipPos.z - viewPos.z) * 0.2f;
		
		float velocityLength = ship.getMoveVector().length();
		float targetFov = BASE_FOV + velocityLength * 1.5f;
		fov += (targetFov - fov) * 0.1f;
	}
	
	public void viewportResized(int width, int height) {
		aspectRatio = (float)width / height;
	}
	
	public void prepareRender(float dt) {
		float fov = prevFov + dt * (this.fov - prevFov);
		projMatrix.toPerspective(fov, aspectRatio, NEAR, FAR);
		
		float vx = prevViewPos.x + dt * (viewPos.x - prevViewPos.x);
		float vy = prevViewPos.y + dt * (viewPos.y - prevViewPos.y);
		float vz = prevViewPos.z + dt * (viewPos.z - prevViewPos.z);
		Mat4f viewRotMat = prevViewRotMat.interpolate(this.viewRotMat, dt);
		viewMatrix.toIdentity();
		viewMatrix.rotateX(25.0f).translate(0, -60, -100);
		viewMatrix.mul(viewRotMat).translate(vx, vy, vz);
		
		viewRotMat.inverseCopy(invViewRotMat);
	}
	
	public Mat4f getModelMatrix() {
		return modlMatrix;
	}

	public Mat4f getViewMatrix() {
		return viewMatrix;
	}

	public Mat4f getProjectionMatrix() {
		return projMatrix;
	}
	
	public Mat4f getInverseViewRotMatrix() {
		return invViewRotMat;
	}
}
