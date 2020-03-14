package com.g4mesoft.ship;

import com.g4mesoft.Camera3D;
import com.g4mesoft.environment.Skybox;
import com.g4mesoft.graphics3d.BasicShader3D;
import com.g4mesoft.graphics3d.Fragment3D;
import com.g4mesoft.graphics3d.Texture3D;
import com.g4mesoft.graphics3d.Triangle3D;
import com.g4mesoft.graphics3d.Vertex3D;
import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.math.Vec4f;

public class ShipShader3D extends BasicShader3D {

	private static final int TEXTURE_UV_LOCATION = 0;
	private static final int BRIGHTNESS_LOCATION = 2;
	private static final int REFLECTED_LIGHT_LOCATION = 3;
	private static final int TO_CAMERA_LOCATION = 6;

	private static final int LIGHT_R = 0xFF;
	private static final int LIGHT_G = 0xE2;
	private static final int LIGHT_B = 0xBA;
	
	private static final float AMBIENT_LIGHT = 0.3f;
	
	private final Camera3D camera;

	private final Mat4f invViewMat;
	
	private final Mat4f projViewMat;
	
	private final Vec4f t0;
	private final Vec4f t1;
	private final Vec4f t2;
	
	private final Vec3f line1;
	private final Vec3f line2;
	
	private final Vec3f normal;
	private final Vec2f texCoords;
	private final Vec3f toCamera;
	private final Vec3f reflectedLight;
	
	private Texture3D texture;
	
	public ShipShader3D(Camera3D camera) {
		this.camera = camera;

		this.invViewMat = new Mat4f();
	
		projViewMat = new Mat4f();

		t0 = new Vec4f();
		t1 = new Vec4f();
		t2 = new Vec4f();
	
		line1 = new Vec3f();
		line2 = new Vec3f();
		
		normal = new Vec3f();
		texCoords = new Vec2f();
		reflectedLight = new Vec3f();
		toCamera = new Vec3f();
	}
	
	public void setTexture(Texture3D texture) {
		this.texture = texture;
	}

	@Override
	public void prepareShader() {
		camera.getProjectionMatrix().copy(projViewMat);
		projViewMat.mul(camera.getViewMatrix());

		camera.getViewMatrix().inverseCopy(invViewMat);
	}
	
	@Override
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		camera.getModelMatrix().mul(v0.pos, t0);
		camera.getModelMatrix().mul(v1.pos, t1);
		camera.getModelMatrix().mul(v2.pos, t2);

		v0.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		result.v0.storeVec2f(TEXTURE_UV_LOCATION, texCoords);

		v1.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		result.v1.storeVec2f(TEXTURE_UV_LOCATION, texCoords);
		
		v2.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		result.v2.storeVec2f(TEXTURE_UV_LOCATION, texCoords);

		line1.set(t1.x - t0.x, t1.y - t0.y, t1.z - t0.z);
		line2.set(t2.x - t0.x, t2.y - t0.y, t2.z - t0.z);
		
		line1.cross(line2, normal);
		normal.normalize();
		
		float b = Math.max(AMBIENT_LIGHT, -Skybox.SUN_DIR.dot(normal));
		result.v0.storeFloat(BRIGHTNESS_LOCATION, b);
		result.v1.storeFloat(BRIGHTNESS_LOCATION, b);
		result.v2.storeFloat(BRIGHTNESS_LOCATION, b);

		reflect(Skybox.SUN_DIR, normal, reflectedLight);
		result.v0.storeVec3f(REFLECTED_LIGHT_LOCATION, reflectedLight);
		result.v1.storeVec3f(REFLECTED_LIGHT_LOCATION, reflectedLight);
		result.v2.storeVec3f(REFLECTED_LIGHT_LOCATION, reflectedLight);
		
		toCamera.set(invViewMat.m30 - t0.x, invViewMat.m31 - t0.y, invViewMat.m32 - t0.z);
		result.v0.storeVec3f(TO_CAMERA_LOCATION, toCamera.normalize());

		toCamera.set(invViewMat.m30 - t1.x, invViewMat.m31 - t1.y, invViewMat.m32 - t1.z);
		result.v1.storeVec3f(TO_CAMERA_LOCATION, toCamera.normalize());

		toCamera.set(invViewMat.m30 - t2.x, invViewMat.m31 - t2.y, invViewMat.m32 - t2.z);
		result.v2.storeVec3f(TO_CAMERA_LOCATION, toCamera.normalize());
		
		projViewMat.mul(t0, result.v0.pos);
		projViewMat.mul(t1, result.v1.pos);
		projViewMat.mul(t2, result.v2.pos);
	}
	
	@Override
	public boolean fragment(Vertex3D vert, Fragment3D fragment) {
		vert.loadVec2f(TEXTURE_UV_LOCATION, texCoords);
		int pixel = texture.samplePixel(texCoords);

		float b = vert.loadFloat(2);
		int rr = (int)(b * ((pixel >>> 16) & 0xFF));
		int gg = (int)(b * ((pixel >>>  8) & 0xFF));
		int bb = (int)(b * ((pixel >>>  0) & 0xFF));
		vert.loadVec3f(REFLECTED_LIGHT_LOCATION, reflectedLight);

		vert.loadVec3f(TO_CAMERA_LOCATION, toCamera);
		toCamera.normalize();

		float specularFactor = MathUtils.max(toCamera.dot(reflectedLight), 0.0f);
		specularFactor *= specularFactor;
		specularFactor *= specularFactor;
		specularFactor *= specularFactor;
		specularFactor *= specularFactor;
		rr = MathUtils.min(rr + (int)(specularFactor * LIGHT_R), 255);
		gg = MathUtils.min(gg + (int)(specularFactor * LIGHT_G), 255);
		bb = MathUtils.min(bb + (int)(specularFactor * LIGHT_B), 255);
		
		int a = pixel >>> 24;
		if (a != 0xFF) {
			fragment.blend(a, rr, gg, bb);
		} else {
			fragment.setRGB(rr, gg, bb);
		}
		
		return true;
	}
	
	public int getOutputSize() {
		return 9;
	}
}
