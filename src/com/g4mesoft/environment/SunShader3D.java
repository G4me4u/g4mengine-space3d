package com.g4mesoft.environment;

import com.g4mesoft.Camera3D;
import com.g4mesoft.graphics3d.Fragment3D;
import com.g4mesoft.graphics3d.Triangle3D;
import com.g4mesoft.graphics3d.Vertex3D;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec2f;

public class SunShader3D extends StarShader3D {

	private final Vec2f tmp;
	
	public SunShader3D(Camera3D camera) {
		super(camera);
		
		tmp = new Vec2f();
	}

	@Override
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		result.v0.storeFloat(0, v0.pos.x);
		result.v0.storeFloat(1, v0.pos.y);

		result.v1.storeFloat(0, v1.pos.x);
		result.v1.storeFloat(1, v1.pos.y);

		result.v2.storeFloat(0, v2.pos.x);
		result.v2.storeFloat(1, v2.pos.y);
		
		super.projectVertices(result, v0, v1, v2);
	}

	@Override
	public boolean fragment(Vertex3D vert, Fragment3D fragment) {
		vert.loadVec2f(0, tmp);
		float rSqr = tmp.sub(0.5f).lengthSqr();
		if (rSqr > 0.25f)
			return false;
		
		float b = 1.0f / MathUtils.exp(rSqr);
		b *= b;
		
		if (rSqr > 0.20f) {
			float a = 1.0f + (0.20f - rSqr) * 20.0f;
			a *= a;
			a *= a;
			fragment.blend(a, 1.0f, 1.0f, b);
		} else {
			fragment.setRGB(1.0f, 1.0f, b);
		}
		
		return true;
	}

	@Override
	public int getOutputSize() {
		return 2;
	}
}
