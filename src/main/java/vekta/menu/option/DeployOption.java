package vekta.menu.option;

import vekta.Player;
import vekta.item.Item;
import vekta.menu.Menu;
import vekta.object.SpaceObject;

import java.util.function.Supplier;

import static vekta.Vekta.addObject;

public class DeployOption implements MenuOption {
	private final String name;
	private final Player player;
	private final Item item;
	private final Supplier<SpaceObject> supplier;

	public DeployOption(String name, Player player, Item item, Supplier<SpaceObject> supplier) {
		this.name = name;
		this.player = player;
		this.item = item;
		this.supplier = supplier;
	}

	@Override
	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}

	public Supplier<SpaceObject> getSupplier() {
		return supplier;
	}

	@Override
	public void select(Menu menu) {
		addObject(getSupplier().get());
		getPlayer().getInventory().remove(getItem());
		menu.close();
	}
}