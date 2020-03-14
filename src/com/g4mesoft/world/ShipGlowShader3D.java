package com.g4mesoft.world;

import com.g4mesoft.Camera3D;
import com.g4mesoft.graphics3d.BasicShader3D;
import com.g4mesoft.graphics3d.Fragment3D;
import com.g4mesoft.graphics3d.Texture3D;
import com.g4mesoft.graphics3d.Triangle3D;
import com.g4mesoft.graphics3d.Vertex3D;
import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.Vec2f;

public class ShipGlowShader3D extends BasicShader3D {

	private static final int TEXTURE_UV_LOCATION = 0;
	
	private final Camera3D camera;

	private final Mat4f projViewModlMat;
	private final Vec2f texCoords;
	
	private Texture3D texture;
	private float brightness;
	
	public ShipGlowShader3D(Camera3D camera) {
		this.camera = camera;

		projViewModlMat = new Mat4f();
		texCoords = new Vec2f();
	}
	
	public void setTexture(Texture3D texture) {
		this.texture = texture;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	
	@Override
	public void prepareShader() {
		camera.getProjectionMatrix().mul(camera.getViewMatrix(), projViewModlMat);
		projViewModlMat.mul(camera.getModelMatrix());
	}
	
	@Override
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		v0.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		result.v0.storeVec2f(TEXTURE_UV_LOCATION, texCoords);

		v1.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		result.v1.storeVec2f(TEXTURE_UV_LOCATION, texCoords);
		
		v2.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		result.v2.storeVec2f(TEXTURE_UV_LOCATION, texCoords);

		projViewModlMat.mul(v0.pos, result.v0.pos);
		projViewModlMat.mul(v1.pos, result.v1.pos);
		projViewModlMat.mul(v2.pos, result.v2.pos);
	}
	
	@Override
	public boolean fragment(Vertex3D vert, Fragment3D fragment) {
		vert.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		int pixel = texture.samplePixel(texCoords);
		if ((pixel >>> 24) < 0x80)
			return false;
		int r = (int)(((pixel >>> 16) & 0xFF) * brightness);
		int g = (int)(((pixel >>>  8) & 0xFF) * brightness);
		int b = (int)(((pixel >>>  0) & 0xFF) * brightness);
		fragment.setRGB(r, g, b);
		return true;
	}
	
	public int getOutputSize() {
		return 2;
	}
}
