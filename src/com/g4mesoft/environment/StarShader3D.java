package com.g4mesoft.environment;

import com.g4mesoft.Camera3D;
import com.g4mesoft.graphics3d.Fragment3D;
import com.g4mesoft.graphics3d.IShader3D;
import com.g4mesoft.graphics3d.Triangle3D;
import com.g4mesoft.graphics3d.Vertex3D;
import com.g4mesoft.math.Mat4f;

public class StarShader3D implements IShader3D {

	private final Camera3D camera;
	
	private final Mat4f projViewModlMat;
	
	public StarShader3D(Camera3D camera) {
		this.camera = camera;
		
		projViewModlMat = new Mat4f();
	}

	@Override
	public void prepareShader() {
		camera.getViewMatrix().copy(projViewModlMat);
		projViewModlMat.setTranslation(0.0f, 0.0f, 0.0f);
		
		camera.getProjectionMatrix().mul(projViewModlMat, projViewModlMat);
		
		projViewModlMat.mul(camera.getModelMatrix());
	}

	@Override
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		projViewModlMat.mul(v0.pos, result.v0.pos);
		projViewModlMat.mul(v1.pos, result.v1.pos);
		projViewModlMat.mul(v2.pos, result.v2.pos);
	}

	@Override
	public boolean fragment(Vertex3D vert, Fragment3D fragment) {
		fragment.setRGB(0xFFFFFF);
		return true;
	}

	@Override
	public int getOutputSize() {
		return 0;
	}
}
