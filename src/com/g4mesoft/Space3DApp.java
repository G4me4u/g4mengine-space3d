package com.g4mesoft;

import java.awt.event.KeyEvent;
import java.io.IOException;

import com.g4mesoft.environment.Skybox;
import com.g4mesoft.graphic.Display;
import com.g4mesoft.graphic.DisplayMode;
import com.g4mesoft.graphic.GColor;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.filter.FastGaussianBlurPixelFilter;
import com.g4mesoft.graphics3d.PixelRenderer3D;
import com.g4mesoft.input.key.KeyInput;
import com.g4mesoft.input.key.KeySingleInput;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.model.ModelManager;
import com.g4mesoft.ship.BasicShipController;
import com.g4mesoft.ship.DummyShipController;
import com.g4mesoft.ship.IShipController;
import com.g4mesoft.ship.PlayerSpaceShip;
import com.g4mesoft.ship.SpaceShip;
import com.g4mesoft.world.SpaceWorld;

public class Space3DApp extends Application {

	private static final float GLOW_BLUR_FACTOR = 4.0f;
	
	private Camera3D camera;
	private Skybox skybox;
	private SpaceWorld world;
	
	private IShipController controller;
	private PlayerSpaceShip playerShip;
	
	private KeyInput fullscreen;
	
	private PixelRenderer3D glowRenderer;
	private FastGaussianBlurPixelFilter glowBlurFilter;
	private GlowOverlayFilter glowOverlayFilter;
	
	@Override
	protected void init() {
		super.init();
		
		try {
			ModelManager.loadModels();
			Sounds.loadSounds();
		} catch (IOException e) {
			e.printStackTrace();
			stopRunning();
			return;
		}

		camera = new Camera3D(this);
		skybox = new Skybox(camera);
		world = new SpaceWorld(camera);

		controller = new BasicShipController(this);
		
		SpaceShip quarkShip = new SpaceShip(ModelManager.QUARK_MODEL, DummyShipController.getInstance());
		quarkShip.move(new Vec3f(200, 0, 0));
		world.addSpaceShip(quarkShip);
		
		playerShip = new PlayerSpaceShip(ModelManager.FIGHTER_MODEL, controller);
		world.addSpaceShip(playerShip);
		
		fullscreen = new KeySingleInput("fullscreen", KeyEvent.VK_F11);
		addKeys(fullscreen);

		setMouseGrabbed(true);
	}
	
	@Override
	protected void displayResized(int newWidth, int newHeight) {
		super.displayResized(newWidth, newHeight);

		Display d = getDisplay();

		int shift = 1;
		int width = newWidth >>> shift;
		int height = newHeight >>> shift;
		
		d.setRenderer(new PixelRenderer3D(d, width, height));
		
		int glowWidth = width;
		int glowHeight = height;
		while (glowWidth > 720 || glowHeight > 720) {
			glowWidth >>>= 1;
			glowHeight >>>= 1;
		}

		glowRenderer = new PixelRenderer3D(null, glowWidth, glowHeight);
		glowOverlayFilter = new GlowOverlayFilter(glowRenderer);
		
		float glowRadius = MathUtils.min(glowWidth, glowHeight) / 270.0f * GLOW_BLUR_FACTOR;
		glowBlurFilter = new FastGaussianBlurPixelFilter(glowRadius);
		
		camera.viewportResized(width, height);
	}
	
	@Override
	protected void tick() {
		camera.update();
		world.update();
		
		if (fullscreen.isClicked()) {
			Display display = getDisplay();
			if (display.isFullscreen()) {
				display.setDisplayMode(DisplayMode.NORMAL);
			} else {
				display.setDisplayMode(DisplayMode.FULLSCREEN_BORDERLESS);
			}
		}
	}
	
	@Override
	protected void render(IRenderer2D renderer, float dt) {
		renderer.setColor(GColor.BLACK);
		renderer.clear();

		camera.prepareRender(dt);
		
		glowRenderer.setColor(GColor.BLACK);
		glowRenderer.clear();

		if (renderer instanceof PixelRenderer3D) {
			PixelRenderer3D renderer3D = ((PixelRenderer3D)renderer);
			
			skybox.drawSkybox(renderer3D, glowRenderer, dt);
			renderer3D.clearDepth();
			glowRenderer.clearDepth();
			
			world.renderWorld(renderer3D, glowRenderer, dt);
			
			glowRenderer.applyFilter(glowBlurFilter);
			renderer3D.applyFilter(glowOverlayFilter);
		}
	}

	public PlayerSpaceShip getPlayerShip() {
		return playerShip;
	}
	
	public static void main(String[] args) throws Exception {
		Application.start(args, Space3DApp.class);
	}
}
