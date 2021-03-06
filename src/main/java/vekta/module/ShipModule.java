package vekta.module;

import vekta.module.station.ComponentModule;
import vekta.object.ship.ModularShip;
import vekta.object.ship.SpaceStation;
import vekta.world.RenderLevel;

import static vekta.Vekta.*;

public abstract class ShipModule implements ComponentModule {
	private ModularShip ship;

	public final ModularShip getShip() {
		return ship;
	}

	@Override
	public boolean isApplicable(ModularShip ship) {
		return true;
	}

	@Override
	public final void onInstall(ModularShip ship) {
		this.ship = ship;
		onInstall();
	}

	@Override
	public final void onUninstall(ModularShip ship) {
		onUninstall();
		this.ship = null;
	}

	public void onInstall() {
	}

	public void onUninstall() {
	}

	protected final int chooseInclusive(int min, int max) {
		return round(v.random(min, max));
	}

	protected final float chooseInclusive(float min, float max, float interval) {
		return roundEpsilon(interval * chooseInclusive((int)(min / interval), (int)(max / interval)));
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public boolean hasAttachmentPoint(SpaceStation.Direction dir) {
		return true;
	}

	@Override
	public void draw(RenderLevel dist, float tileSize) {
		v.rect(0, 0, getWidth() * tileSize / 2, getHeight() * tileSize);
		v.rect(0, 0, getWidth() * tileSize, getHeight() * tileSize / 2);
	}
}
