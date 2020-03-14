package com.g4mesoft.ship;

import com.g4mesoft.math.Vec3f;

public class ExhaustEntry {

	private final Vec3f pos;
	private final Vec3f up;
	
	public ExhaustEntry(Vec3f pos, Vec3f up) {
		this.pos = pos;
		this.up = up;
	}
	
	public Vec3f getPos() {
		return pos;
	}

	public Vec3f getUp() {
		return up;
	}
}
