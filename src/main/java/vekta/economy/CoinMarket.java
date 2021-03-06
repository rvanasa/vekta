package vekta.economy;

import vekta.item.ItemType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static vekta.Vekta.register;
import static vekta.Vekta.v;

public class CoinMarket implements Serializable, Economy.Container, ProductivityModifier {
	private static final float PUMP_CHANCE = 0.01F;
	private static final float DOWN_SCALE = 2F;

	private final Economy economy;
	private final List<Coin> coins = new ArrayList<>();

	public CoinMarket() {
		this.economy = register(new Economy(this, 100));
		economy.addModifier(new NoiseModifier(2));
		economy.addModifier(this);
		economy.fillHistory();
	}

	public Economy getEconomy() {
		return economy;
	}

	public List<Coin> getCoins() {
		return coins;
	}

	@Override
	public String getName() {
		return "Coin Market";
	}

	@Override
	public int getColor() {
		return ItemType.ECONOMY.getColor();
	}

	@Override
	public boolean isEconomyAlive() {
		return true;
	}

	@Override
	public String getModifierName() {
		return getName();
	}

	@Override
	public float updateModifier(Economy economy) {
		return v.chance(PUMP_CHANCE) ? 1 / PUMP_CHANCE : -DOWN_SCALE * PUMP_CHANCE;
	}
}
