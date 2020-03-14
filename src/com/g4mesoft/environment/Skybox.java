package com.g4mesoft.environment;

import java.util.Random;

import com.g4mesoft.Camera3D;
import com.g4mesoft.graphics3d.FloatArrayVertexProvider;
import com.g4mesoft.graphics3d.IVertexProvider;
import com.g4mesoft.graphics3d.PixelRenderer3D;
import com.g4mesoft.graphics3d.Shape3D;
import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.Vec3f;

public class Skybox {

	private static final int NUM_STARS = 5000;
	
	private static final float MAX_STAR_DIST = 800.0f;
	private static final float MIN_STAR_DIST = 400.0f;
	
	private static final float[] STAR_VERTICES = new float[] {
		0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f
	};
	
	public static final Vec3f SUN_DIR = new Vec3f(1.0f, 1.0f, 1.0f).normalize();
	
	private final Camera3D camera;
	
	private final StarShader3D starShader;
	private final SunShader3D sunShader;
	
	private final Vec3f[] starPositions;
	private final IVertexProvider squareVertices;
	
	private final Vec3f sunPos;
	
	public Skybox(Camera3D camera) {
		this.camera = camera;
		
		starShader = new StarShader3D(camera);
		sunShader = new SunShader3D(camera);
		
		starPositions = new Vec3f[NUM_STARS];

		Random random = new Random();
		for (int i = 0; i < starPositions.length; i++) {
			Vec3f pos = new Vec3f((float)random.nextGaussian(), 
			                      (float)random.nextGaussian(), 
			                      (float)random.nextGaussian()).normalize();
			pos.mul(random.nextFloat() * (MAX_STAR_DIST - MIN_STAR_DIST) + MIN_STAR_DIST);
			
			starPositions[i] = pos;
		}
		
		squareVertices = new FloatArrayVertexProvider(STAR_VERTICES, 3, Shape3D.QUADS);
	
		sunPos = new Vec3f(SUN_DIR).mul(-MIN_STAR_DIST);
	}
	
	public void drawSkybox(PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		drawStars(renderer3D, glowRenderer, dt);
		drawSun(renderer3D, glowRenderer, dt);
	}
	
	private void drawStars(PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		renderer3D.setShader(starShader);
		
		Mat4f modlMatrix = camera.getModelMatrix();
		for (Vec3f pos : starPositions) {
			modlMatrix.toIdentity();
			modlMatrix.setTranslation(pos);
			modlMatrix.mul(camera.getInverseViewRotMatrix());
			
			renderer3D.drawVertices(squareVertices);
		}
	}
	
	private void drawSun(PixelRenderer3D renderer3D, PixelRenderer3D glowRenderer, float dt) {
		renderer3D.setShader(sunShader);

		Mat4f modlMatrix = camera.getModelMatrix();
		modlMatrix.toIdentity();
		modlMatrix.setTranslation(sunPos);
		modlMatrix.mul(camera.getInverseViewRotMatrix());
		modlMatrix.scale(100.0f, 100.0f, 0.0f);
		modlMatrix.translate(-0.5f, -0.5f, 0.0f);
		
		renderer3D.setCullEnabled(false);
		renderer3D.drawVertices(squareVertices);
		renderer3D.setCullEnabled(true);

		glowRenderer.setShader(sunShader);
		glowRenderer.setCullEnabled(false);
		glowRenderer.drawVertices(squareVertices);
		glowRenderer.setCullEnabled(true);
	}
}
