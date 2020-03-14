package com.g4mesoft.model;

public class ShipMaterial {

	private final int exhaustColor;
	private final int laserColor;
	
	public ShipMaterial(int exhaustColor, int laserColor) {
		this.exhaustColor = exhaustColor;
		this.laserColor = laserColor;
	}
	
	public int getExhaustColor() {
		return exhaustColor;
	}
	
	public int getLaserColor() {
		return laserColor;
	}
}
