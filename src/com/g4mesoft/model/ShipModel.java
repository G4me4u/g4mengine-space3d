package com.g4mesoft.model;

import com.g4mesoft.math.Vec3f;

public class ShipModel {

	private final TexturedModel model;
	private final Vec3f[] exhaustPositions;
	private final Vec3f[] laserPositions;
	private final ShipMaterial material;
	
	public ShipModel(TexturedModel model, Vec3f[] exhaustPositions, Vec3f[] laserPositions, ShipMaterial material) {
		this.model = model;
		this.exhaustPositions = exhaustPositions;
		this.laserPositions = laserPositions;
		this.material = material;
	}
	
	public TexturedModel getModel() {
		return model;
	}
	
	public Vec3f[] getExhaustPositions() {
		return exhaustPositions;
	}
	
	public Vec3f[] getLaserPositions() {
		return laserPositions;
	}
	
	public ShipMaterial getMaterial() {
		return material;
	}
}
