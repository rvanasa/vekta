package vekta.menu;

import processing.event.KeyEvent;
import vekta.KeyBinding;
import vekta.context.Context;
import vekta.menu.handle.MenuHandle;
import vekta.menu.option.BackButton;
import vekta.menu.option.ButtonOption;
import vekta.menu.option.MenuOption;
import vekta.player.Player;
import vekta.player.PlayerEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static vekta.Vekta.getContext;

public class Menu implements Context {
	private final Player player;
	private final MenuHandle handle;

	private MenuOption defaultOption;
	private final List<MenuOption> options = new ArrayList<>();
	private final Map<KeyBinding, MenuOption> hotkeys = new HashMap<>();
	private final List<MenuListener> listeners = new ArrayList<>();

	private boolean hasAutoOption;
	private MenuOption autoOption;

	private int index;

	public Menu(Menu parent, MenuHandle handle) {
		this(parent.getPlayer(), new BackButton(getContext() instanceof Menu ? parent : getContext()/*TODO verify*/), handle);
	}

	public Menu(Player player, MenuOption def, MenuHandle handle) {
		this.player = player;
		this.handle = handle;
		this.defaultOption = def;

		handle.setMenu(this);
		handle.init();

		if(getPlayer() != null) {
			getPlayer().emit(PlayerEvent.MENU, this);
		}
	}

	public MenuHandle getHandle() {
		return handle;
	}

	public Player getPlayer() {
		return player;
	}

	public MenuOption getCursor() {
		return get(getIndex());
	}

	public List<MenuOption> getOptions() {
		return options;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if(this.index != index) {
			this.index = index;
			MenuOption cursor = getCursor();
			for(MenuListener listener : listeners) {
				listener.onHover(cursor);
			}
			handle.onChange();
		}
	}

	public int size() {
		return options.size();
	}

	public MenuOption get(int i) {
		if(size() == 0) {
			return getDefault();
		}
		else if(i >= size()) {
			i = size() - 1;
		}
		return options.get(i);
	}

	public MenuOption getDefault() {
		return defaultOption;
	}

	public void setDefault(MenuOption defaultOption) {
		this.defaultOption = defaultOption;
	}

	public void clear() {
		options.clear();
	}

	public void add(MenuOption item) {
		options.add(item);
		//		handle.addElement(item);
		//		item.init(this);
	}

	public void add(int index, MenuOption item) {
		options.add(index, item);
		//		handle.addElement(item);
		//		item.init(this);
	}

	public void addDefault() {
		add(defaultOption);
	}

	public boolean remove(MenuOption item) {
		if(options.remove(item)) {
			//			handle.removeElement(item);
			if(size() == 0) {
				close();
			}
			else if(index >= size()) {
				index = size() - 1;
			}
			handle.onChange();
			return true;
		}
		return false;
	}

	public void scroll(int n) {
		int next = max(0, min(size() - 1, index + n));
		if(index != next) {
			setIndex(next);
		}
	}

	public void setAuto(ButtonOption option) {
		// Disambiguate multiple automatic options by adding both to the menu
		if(autoOption != null) {
			add(autoOption);
			this.autoOption = null;
			hasAutoOption = true;
		}

		if(!hasAutoOption) {
			autoOption = option;
		}
		else {
			add(option);
		}
	}

	public void selectCursor() {
		select(getCursor());
	}

	public void select(MenuOption option) {
		option.onSelect(this);
		for(MenuListener listener : listeners) {
			listener.onSelect(option);
		}
	}

	public void addSelectListener(Callback callback) {
		addListener(new MenuListener() {
			@Override
			public void onSelect(MenuOption option) {
				callback.accept(option);
			}
		});
	}

	public void addListener(MenuListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener(MenuListener listener) {
		return listeners.remove(listener);
	}

	@Override
	public void focus() {
		handle.focus();
		if(autoOption != null) {
			select(autoOption);
			autoOption = null;
		}
		for(MenuListener listener : listeners) {
			listener.onFocus();
		}
	}

	@Override
	public void unfocus() {
		handle.unfocus();
	}

	@Override
	public void render() {
		handle.render();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		handle.keyPressed(event);
	}

	@Override
	public void keyPressed(KeyBinding key) {
		handle.keyPressed(key);
	}

	@Override
	public void keyReleased(KeyBinding key) {
	}

	@Override
	public void mouseWheel(int amount) {
		scroll(amount);
	}

	public void close() {
		if(getDefault() != null) {
			select(getDefault());
		}
	}

	public interface Callback extends Consumer<MenuOption>, Serializable {
	}
}
