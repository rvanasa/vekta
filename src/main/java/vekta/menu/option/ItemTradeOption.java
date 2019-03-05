package vekta.menu.option;

import vekta.item.Inventory;
import vekta.item.Item;
import vekta.menu.Menu;

public class ItemTradeOption implements MenuOption {
	private final boolean buying;
	private final Inventory you, them;
	private final Item item;
	private final int price;
	private final boolean transfer;

	public ItemTradeOption(boolean buying, Inventory you, Inventory them, Item item) {
		this(buying, you, them, item, 0, true);
	}

	public ItemTradeOption(boolean buying, Inventory you, Inventory them, Item item, int price, boolean transfer) {
		this.buying = buying;
		this.you = you;
		this.them = them;
		this.item = item;
		this.price = price;
		this.transfer = transfer;
	}

	@Override
	public String getName() {
		String tag = price > 0 ? " [" + price + " G]" : "";
		return item.getName() + tag;
	}

	@Override
	public int getColor() {
		return getItem().getType().getColor();
	}

	public Item getItem() {
		return item;
	}

	public Inventory getFrom() {
		return buying ? them : you;
	}

	public Inventory getTo() {
		return buying ? you : them;
	}

	@Override
	public boolean isEnabled(Menu menu) {
		return getTo().has(price) && getFrom().has(item);
	}

	@Override
	public void select(Menu menu) {
		Inventory from = getFrom();
		Inventory to = getTo();
		to.remove(price);
		from.remove(item);
		from.add(price);
		if(transfer) {
			to.add(item);
		}
		menu.remove(this);
	}
}
