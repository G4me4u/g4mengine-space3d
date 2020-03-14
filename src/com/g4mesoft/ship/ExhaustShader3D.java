package com.g4mesoft.ship;

import com.g4mesoft.Camera3D;
import com.g4mesoft.graphics3d.Fragment3D;
import com.g4mesoft.graphics3d.IShader3D;
import com.g4mesoft.graphics3d.Triangle3D;
import com.g4mesoft.graphics3d.Vertex3D;
import com.g4mesoft.math.Mat4f;

public class ExhaustShader3D implements IShader3D {

	private static final int FRAG_OPACITY_LOCATION = 0;
	
	private final Camera3D camera;

	private final Mat4f projViewMat;
	
	private int exhaustR;
	private int exhaustG;
	private int exhaustB;
	
	public ExhaustShader3D(Camera3D camera) {
		this.camera = camera;
		
		projViewMat = new Mat4f();
	}

	@Override
	public void prepareShader() {
		camera.getProjectionMatrix().mul(camera.getViewMatrix(), projViewMat);
	}

	@Override
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		result.v0.storeFloat(FRAG_OPACITY_LOCATION, v0.loadFloat(ShipExhaust.OPACITY_LOCATION));
		result.v1.storeFloat(FRAG_OPACITY_LOCATION, v1.loadFloat(ShipExhaust.OPACITY_LOCATION));
		result.v2.storeFloat(FRAG_OPACITY_LOCATION, v2.loadFloat(ShipExhaust.OPACITY_LOCATION));
		
		projViewMat.mul(v0.pos, result.v0.pos);
		projViewMat.mul(v1.pos, result.v1.pos);
		projViewMat.mul(v2.pos, result.v2.pos);
	}

	@Override
	public boolean fragment(Vertex3D vert, Fragment3D fragment) {
		float opacity = vert.loadFloat(FRAG_OPACITY_LOCATION);
		if (opacity <= 0.0f)
			return false;
		int a = (int)(0xFF * opacity);
		fragment.blend(a, exhaustR, exhaustG, exhaustB);
		return true;
	}

	@Override
	public int getOutputSize() {
		return 1;
	}
	
	public void setExhaustColor(int exhaustColor) {
		exhaustR = (exhaustColor >>> 16) & 0xFF;
		exhaustG = (exhaustColor >>>  8) & 0xFF;
		exhaustB = (exhaustColor >>>  0) & 0xFF;
	}
}
