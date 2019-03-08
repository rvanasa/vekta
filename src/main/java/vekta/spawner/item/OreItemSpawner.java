package vekta.spawner.item;

import vekta.Resources;
import vekta.item.Item;
import vekta.item.ItemType;
import vekta.item.OreItem;
import vekta.spawner.ItemGenerator;

import static vekta.Vekta.v;

public class OreItemSpawner implements ItemGenerator.ItemSpawner {
	@Override
	public float getWeight() {
		return 1;
	}

	@Override
	public boolean isValid(Item item) {
		return item instanceof OreItem;
	}

	@Override
	public Item create() {
		return randomOre(Resources.generateString("planet"));
	}

	public static Item randomOre(String planetName) {
		ItemType type = v.random(1) > .2F ? ItemType.COMMON : ItemType.RARE;
		return new OreItem(randomOreName(planetName, type), type);
	}

	public static String randomOreName(String planetName, ItemType type) {
		String key = "ore_" + (type == ItemType.COMMON ? "common" : "rare");
		String name = Resources.generateString(key);
		if(type == ItemType.LEGENDARY) {
			name += " of " + planetName;
		}
		return name;
	}
}
