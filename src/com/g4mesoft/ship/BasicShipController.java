package com.g4mesoft.ship;

import java.awt.event.KeyEvent;

import com.g4mesoft.Application;
import com.g4mesoft.input.key.KeyInput;
import com.g4mesoft.input.key.KeySingleInput;
import com.g4mesoft.input.mouse.MouseInputListener;
import com.g4mesoft.math.Vec3f;

public class BasicShipController implements IShipController {

	private final Application app;
	
	private final KeyInput yawIncKey;
	private final KeyInput yawDecKey;
	
	private final KeyInput pitchIncKey;
	private final KeyInput pitchDecKey;

	private final KeyInput rollIncKey;
	private final KeyInput rollDecKey;

	private final KeyInput upKey;
	private final KeyInput downKey;
	
	private final KeyInput forwardKey;
	private final KeyInput backwardKey;
	private final KeyInput strafeLeftKey;
	private final KeyInput strafeRightKey;

	private final KeyInput slowKey;
	
	private float rotSpeedX;
	private float rotSpeedY;
	private float rotSpeedZ;
	
	private final Vec3f velocity;
	
	public BasicShipController(Application app) {
		this.app = app;
		
		yawIncKey = new KeySingleInput("yawInc", KeyEvent.VK_Q);
		yawDecKey = new KeySingleInput("yawDec", KeyEvent.VK_E);

		pitchIncKey = new KeySingleInput("pitchInc", KeyEvent.VK_R);
		pitchDecKey = new KeySingleInput("pitchDec", KeyEvent.VK_F);

		rollIncKey = new KeySingleInput("rollInc", KeyEvent.VK_Z);
		rollDecKey = new KeySingleInput("rollDec", KeyEvent.VK_X);
		
		upKey = new KeySingleInput("up", KeyEvent.VK_SPACE);
		downKey = new KeySingleInput("down", KeyEvent.VK_CONTROL);

		forwardKey = new KeySingleInput("forward", KeyEvent.VK_W);
		backwardKey = new KeySingleInput("backward", KeyEvent.VK_S);
		strafeLeftKey = new KeySingleInput("strafeLeft", KeyEvent.VK_A);
		strafeRightKey = new KeySingleInput("strafeRight", KeyEvent.VK_D);

		slowKey = new KeySingleInput("slow", KeyEvent.VK_SHIFT);
	
		Application.addKeys(yawIncKey, yawDecKey, pitchIncKey, pitchDecKey, rollIncKey, rollDecKey);
		Application.addKeys(upKey, downKey, forwardKey, backwardKey, strafeLeftKey, strafeRightKey);
		Application.addKey(slowKey);
		
		rotSpeedX = rotSpeedY = rotSpeedZ = 0.0f;
	
		velocity = new Vec3f();
	}
	
	public void checkInput(SpaceShip owner) {
		float rs = 1.5f;
		if (pitchIncKey.isPressed()) {
			rotSpeedX += rs;
		} else if (pitchDecKey.isPressed()) {
			rotSpeedX -= rs;
		}
		if (yawIncKey.isPressed()) {
			rotSpeedY += rs;
		} else if (yawDecKey.isPressed()) {
			rotSpeedY -= rs;
		}
		if (rollIncKey.isPressed()) {
			rotSpeedZ += rs;
		} else if (rollDecKey.isPressed()) {
			rotSpeedZ -= rs;
		}
		
		if (app.getDisplay().isFocused()) {
			rotSpeedX -= MouseInputListener.getInstance().getDeltaY() * 0.01f;
			rotSpeedZ -= MouseInputListener.getInstance().getDeltaX() * 0.01f;
		}

		owner.rotateShip(rotSpeedX, rotSpeedY, rotSpeedZ);
		
		rotSpeedX *= 0.9f;
		rotSpeedY *= 0.9f;
		rotSpeedZ *= 0.9f;
		
		float modifier = 20.0f;
		if (slowKey.isPressed())
			modifier = 1.0f;

		float speed = 0.1f * modifier;
		Vec3f sideward = owner.getRotationMatrix().getCol0(new Vec3f()).mul(speed);
		Vec3f upward   = owner.getRotationMatrix().getCol1(new Vec3f()).mul(speed);
		Vec3f forward  = owner.getRotationMatrix().getCol2(new Vec3f()).mul(speed);
		
		if (upKey.isPressed()) {
			velocity.add(upward);
		} else if (downKey.isPressed()) {
			velocity.sub(upward);
		}
		
		if (backwardKey.isPressed()) {
			velocity.add(forward);
		} else if (forwardKey.isPressed() || MouseInputListener.MOUSE_RIGHT.isPressed()) {
			velocity.sub(forward);
		}
		
		if (strafeRightKey.isPressed()) {
			velocity.add(sideward);
		} else if (strafeLeftKey.isPressed()) {
			velocity.sub(sideward);
		}
		
		owner.move(velocity);

		velocity.mul(0.9f);
	}
	
	public float getRotMagn() {
		return rotSpeedX * rotSpeedX + 
		       rotSpeedY * rotSpeedY + 
		       rotSpeedZ * rotSpeedZ;
	}
}
