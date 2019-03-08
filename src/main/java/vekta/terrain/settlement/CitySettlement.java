package vekta.terrain.settlement;

import vekta.Faction;
import vekta.spawner.WorldGenerator;
import vekta.terrain.Terrain;

public class CitySettlement extends Settlement {

	public CitySettlement(Faction faction) {
		super(faction, "city");

		// TODO: add District settlement parts
		add(WorldGenerator.randomMarket(3));
	}

	@Override
	public String getTypeString() {
		return "City";
	}

	@Override
	public void onTerrain(Terrain terrain) {
		terrain.addFeature("Urban");
	}

}