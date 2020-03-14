package com.g4mesoft.ship;

import com.g4mesoft.Sounds;
import com.g4mesoft.input.mouse.MouseInputListener;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.model.ShipModel;
import com.g4mesoft.sound.processor.AudioSource;
import com.g4mesoft.world.SpaceWorld;

public class PlayerSpaceShip extends SpaceShip {

	private float rotMagn;
	
	private float enginePitch;
	private AudioSource engineSound;
	
	public PlayerSpaceShip(ShipModel model, IShipController controller) {
		super(model, controller);
		
		enginePitch = 0.25f;
		engineSound = null;
	}
	
	public void onAdded(SpaceWorld world) {
		super.onAdded(world);
		
		engineSound = Sounds.loopForever(Sounds.ENGINE_SOUND, enginePitch);
	}
	
	public void onRemoved() {
		super.onRemoved();
		
		if (engineSound != null)
			engineSound.stop();
	}

	@Override
	public void rotateShip(float rx, float ry, float rz) {
		super.rotateShip(rx, ry, rz);

		rotMagn += rx * rx + ry * ry + rz * rz;
	}
	
	@Override
	public void update() {
		rotMagn = 0.0f;
		
		super.update();
	
		float targetPitch = 0.9f;
		targetPitch += getMoveVector().length() * 0.05f;
		targetPitch += MathUtils.sqrt(rotMagn) * 0.05f;
		if (targetPitch > 2.0f)
			targetPitch = 2.0f;
		
		enginePitch += (targetPitch - enginePitch) * 0.5f;

		if (engineSound != null) {
			engineSound.setPitch(enginePitch);
			engineSound.setVolume(enginePitch * 0.5f);
		}
		
		if (MouseInputListener.MOUSE_LEFT.isClicked())
			shootLaser();
	}
}
