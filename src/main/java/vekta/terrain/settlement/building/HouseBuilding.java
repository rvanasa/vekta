package vekta.terrain.settlement.building;

import vekta.menu.Menu;
import vekta.menu.option.DialogButton;
import vekta.person.Dialog;
import vekta.person.Person;
import vekta.spawner.DialogGenerator;
import vekta.terrain.settlement.SettlementPart;

public class HouseBuilding implements SettlementPart {
	private final Person person;

	public HouseBuilding(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	@Override
	public String getName() {
		return getPerson().getFullName() + "'s House";
	}

	@Override
	public String getGenericName() {
		return "House";
	}

	@Override
	public BuildingType getType() {
		return BuildingType.RESIDENTIAL;
	}

	@Override
	public void setupMenu(Menu menu) {
		if(!getPerson().isDead()) {
			Dialog dialog = DialogGenerator.randomVisitDialog(menu.getPlayer(), getPerson());
			menu.add(new DialogButton("Visit " + getPerson().getName(), dialog));
		}
	}
}
