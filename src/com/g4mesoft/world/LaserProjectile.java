package com.g4mesoft.world;

import com.g4mesoft.math.Vec3f;

public class LaserProjectile {

	private static final float LASER_SPEED = 80.0f;
	private static final long MAX_TICKS_ALIVE = 100L;
	
	private final Vec3f pos;
	private final Vec3f prevPos;

	private final Vec3f vel;
	
	private final Vec3f forward;
	private final Vec3f upward;
	
	private final int color;
	
	private long ticksAlive;
	private boolean dead;
	
	public LaserProjectile(Vec3f position, Vec3f forward, Vec3f upward, int color) {
		pos = position.copy();
		prevPos = new Vec3f(pos);
		
		vel = forward.copy().mul(LASER_SPEED);
		
		this.forward = forward.copy();
		this.upward = upward.copy();
		
		this.color = color;
	
		ticksAlive = 0;
	}
	
	public void tick() {
		prevPos.set(pos);
		pos.add(vel);
	
		ticksAlive++;
		
		if (ticksAlive > MAX_TICKS_ALIVE)
			kill();
	}
	
	public void getRenderPosition(Vec3f dest, float dt) {
		dest.set((pos.x - prevPos.x) * dt + prevPos.x,
		         (pos.y - prevPos.y) * dt + prevPos.y,
		         (pos.z - prevPos.z) * dt + prevPos.z);
	}

	public Vec3f getForward() {
		return forward;
	}

	public Vec3f getUpward() {
		return upward;
	}

	public int getColor() {
		return color;
	}
	
	public void kill() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
}
