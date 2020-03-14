package com.g4mesoft.ship;

import java.util.LinkedList;

import com.g4mesoft.graphics3d.FloatArrayVertexProvider;
import com.g4mesoft.graphics3d.PixelRenderer3D;
import com.g4mesoft.graphics3d.Shape3D;
import com.g4mesoft.graphics3d.VertexTessellator3D;
import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec3f;

public class ShipExhaust {

	private static final int MAX_EXHAUST_ENTRIES = 8;
	private static final float EXHAUST_SIZE = 6.5f;
	
	public static final int OPACITY_LOCATION = 0;
	
	private final SpaceShip ship;
	
	private final Vec3f offset;
	
	private final LinkedList<ExhaustEntry> exhaust;
	private final VertexTessellator3D tessellator;
	
	private float[] vertexBuffer;
	private FloatArrayVertexProvider vertices;
	
	private float sizeScale;
	
	public ShipExhaust(SpaceShip ship, Vec3f offset) {
		this.ship = ship;
		this.offset = offset;

		exhaust = new LinkedList<ExhaustEntry>();
		tessellator = new VertexTessellator3D(1);
	
		vertexBuffer = new float[0];
		vertices = null;
		
		sizeScale = 1.0f;
	}
	
	private Vec3f getExhaustPos() {
		return ship.getRotationMatrix().mul(offset).add(ship.getPos());
	}
	
	public void addExhaust() {
		while (exhaust.size() >= MAX_EXHAUST_ENTRIES)
			exhaust.removeLast();
		
		exhaust.addFirst(new ExhaustEntry(getExhaustPos(), ship.getRotationMatrix().getCol1(new Vec3f())));
	}
	
	public void removeExhaust() {
		if (!exhaust.isEmpty())
			exhaust.removeLast();
	}
	
	public void setSizeScale(float scale) {
		this.sizeScale = MathUtils.max(scale, 0.0f);
	}
	
	private void generateExhaust(float sx, float sy, float sz, Mat4f shipRotMat, float dt) {
		tessellator.clear();
		
		float size = EXHAUST_SIZE * sizeScale;
		Vec3f prevPos = shipRotMat.mul(offset).add(sx, sy, sz);
		
		Vec3f v0 = shipRotMat.getCol1(new Vec3f());
		Vec3f v1 = v0.copy().mul(size).add(prevPos);
		v0.mul(-size).add(prevPos);

		Vec3f v2 = new Vec3f();
		Vec3f v3 = new Vec3f();

		Vec3f h0 = shipRotMat.getCol0(new Vec3f());
		Vec3f h1 = h0.copy().mul(size).add(prevPos);
		h0.mul(-size).add(prevPos);

		Vec3f h2 = new Vec3f();
		Vec3f h3 = new Vec3f();

		int numExhaustEntries = exhaust.size();
		float opacity = 1.0f - dt / numExhaustEntries;

		for (ExhaustEntry entry : exhaust) {
			v2.set(entry.getUp()).mul( size).add(entry.getPos());
			v3.set(entry.getUp()).mul(-size).add(entry.getPos());

			// Calculate right vector
			h2.set(entry.getPos()).sub(prevPos);
			h2.cross(entry.getUp(), h2);
			h2.normalize();
			h3.set(h2);
			
			h2.mul( size).add(entry.getPos());
			h3.mul(-size).add(entry.getPos());

			float nextOpacity = opacity - 1.0f / numExhaustEntries;
			
			tessellator.addVertex(v1);
			tessellator.setExtraVertexData(OPACITY_LOCATION, opacity);
			tessellator.addVertex(v0);
			tessellator.setExtraVertexData(OPACITY_LOCATION, opacity);
			tessellator.addVertex(v2);
			tessellator.setExtraVertexData(OPACITY_LOCATION, nextOpacity);
			tessellator.addVertex(v3);
			tessellator.setExtraVertexData(OPACITY_LOCATION, nextOpacity);

			tessellator.addVertex(h1);
			tessellator.setExtraVertexData(OPACITY_LOCATION, opacity);
			tessellator.addVertex(h0);
			tessellator.setExtraVertexData(OPACITY_LOCATION, opacity);
			tessellator.addVertex(h2);
			tessellator.setExtraVertexData(OPACITY_LOCATION, nextOpacity);
			tessellator.addVertex(h3);
			tessellator.setExtraVertexData(OPACITY_LOCATION, nextOpacity);
			
			prevPos.set(entry.getPos());
			
			v0.set(v2);
			v1.set(v3);

			h0.set(h2);
			h1.set(h3);
			
			opacity = nextOpacity;
		}
		
		if (vertexBuffer.length != tessellator.getSize()) {
			vertexBuffer = new float[tessellator.getSize()];
			vertices = new FloatArrayVertexProvider(vertexBuffer, tessellator.getVertexSize(), Shape3D.QUADS);
		}
		
		tessellator.getVertexData(vertexBuffer, 0);
	}
	
	public void render(PixelRenderer3D glowRenderer, float sx, float sy, float sz, Mat4f shipRotMat, float dt) {
		generateExhaust(sx, sy, sz, shipRotMat, dt);
		
		if (vertices != null) {
			glowRenderer.setCullEnabled(false);
			glowRenderer.drawVertices(vertices);
			glowRenderer.setCullEnabled(true);
		}
	}
}
