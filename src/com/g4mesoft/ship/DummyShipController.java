package com.g4mesoft.ship;

import com.g4mesoft.math.Vec3f;

public final class DummyShipController implements IShipController {

	private static final DummyShipController INSTANCE = new DummyShipController();
	
	private DummyShipController() {
	}
	
	@Override
	public void checkInput(SpaceShip owner) {
		owner.rotateShip(2.0f, 0.0f, 0.0f);
		owner.move(owner.getRotationMatrix().getCol2(new Vec3f()).mul(-18.0f));
	}

	public static IShipController getInstance() {
		return INSTANCE;
	}
}
