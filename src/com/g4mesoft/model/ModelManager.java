package com.g4mesoft.model;

import java.io.IOException;

import javax.imageio.ImageIO;

import com.g4mesoft.Space3DApp;
import com.g4mesoft.graphics3d.IVertexProvider;
import com.g4mesoft.graphics3d.Texture3D;
import com.g4mesoft.math.Vec3f;

public class ModelManager {

	private static final Vec3f[] QUARK_EXHAUST = new Vec3f[] {
		new Vec3f( 26.0f, 21.5f, 55.0f),
		new Vec3f(-26.0f, 21.5f, 55.0f)
	};

	private static final Vec3f[] QUARK_LASERS = new Vec3f[] {
	};
	
	private static final Vec3f[] FIGHTER_EXHAUST = new Vec3f[] {
		new Vec3f( 15.25f, 2.5f, 40.0f),
		new Vec3f(-15.25f, 2.5f, 40.0f)
	};

	private static final Vec3f[] FIGHTER_LASERS = new Vec3f[] {
		new Vec3f( 47.25f, 3.25f, -13.0f),
		new Vec3f(-47.25f, 3.25f, -13.0f)
	};
	private static final int QUARK_EXHAUST_COLOR = 0xFFF67C46;
	private static final int QUARK_LASER_COLOR = 0x00000000;
	private static final int FIGHTER_EXHAUST_COLOR = 0xFF1FE479;
	private static final int FIGHTER_LASER_COLOR = 0xFFFF0000;
	
	private static final ShipMaterial QUARK_SHIP_MATERIAL = new ShipMaterial(QUARK_EXHAUST_COLOR, QUARK_LASER_COLOR);
	private static final ShipMaterial FIGHTER_SHIP_MATERIAL = new ShipMaterial(FIGHTER_EXHAUST_COLOR, FIGHTER_LASER_COLOR);
	
	public static ShipModel QUARK_MODEL = null;
	public static ShipModel FIGHTER_MODEL = null;
	
	public static void loadModels() throws IOException {
		QUARK_MODEL = loadModel("QuarkShuttle", QUARK_EXHAUST, QUARK_LASERS, QUARK_SHIP_MATERIAL);
		FIGHTER_MODEL = loadModel("BlargStarfighter", FIGHTER_EXHAUST, FIGHTER_LASERS, FIGHTER_SHIP_MATERIAL);
	}
	
	private static ShipModel loadModel(String name, Vec3f[] exhaust, Vec3f[] lasers, ShipMaterial material) throws IOException {
		TexturedModel model = loadTexturedModel(name);
		return new ShipModel(model, exhaust, lasers, material);
	}

	private static TexturedModel loadTexturedModel(String modelName) throws IOException {
		IVertexProvider model = ObjVertexProvider.loadFromOBJ("/" + modelName + ".obj");
		Texture3D texture = readTexture("/" + modelName + "Texture.png");
		Texture3D glowTexture = readTexture("/" + modelName + "GlowTexture.png");
	
		return new TexturedModel(model, texture, glowTexture);
	}
	
	private static Texture3D readTexture(String path) throws IOException {
		return new Texture3D(ImageIO.read(Space3DApp.class.getResource(path)));
	}
}
