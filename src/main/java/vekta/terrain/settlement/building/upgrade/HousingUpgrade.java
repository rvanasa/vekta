package vekta.terrain.settlement.building.upgrade;

import vekta.economy.TemporaryModifier;
import vekta.player.Player;
import vekta.spawner.PersonGenerator;
import vekta.terrain.settlement.Settlement;
import vekta.terrain.settlement.building.HouseBuilding;
import vekta.terrain.settlement.building.MarketBuilding;

public class HousingUpgrade implements SettlementUpgrade {
	@Override
	public String getName() {
		return "Expand Housing";
	}

	@Override
	public boolean isAvailable(Player player, Settlement settlement) {
		return settlement.find(MarketBuilding.class) == null;
	}

	@Override
	public int getCost(Player player, Settlement settlement) {
		return 100 * (1 + settlement.count(HouseBuilding.class));
	}

	@Override
	public void upgrade(Player player, Settlement settlement) {
		PersonGenerator.createPerson(settlement);

		settlement.getEconomy().addModifier(new TemporaryModifier("Increasing Population", .25F, .01F));
	}
}
