package vekta.context;

import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.listener.SimpleXInputDeviceListener;
import com.github.strikerx3.jxinput.listener.XInputDeviceListener;
import processing.event.KeyEvent;
import vekta.KeyBinding;
import vekta.Settings;
import vekta.overlay.Overlay;
import com.github.strikerx3.jxinput.XInputDevice14;

public interface Context extends Overlay {
	/**
	 * Called whenever the context is activated
	 */
	default void focus() {
	}

	/**
	 * Called whenever the context is replaced
	 */
	default void unfocus() {
	}



	default void buttonPressed(XInputButton button) {
		for(KeyBinding key : KeyBinding.values())
		{
			if(key.getButton() == button)
			{
				keyPressed(key);
			}
		}
	}

	default void buttonReleased(XInputButton button) {
		for(KeyBinding key : KeyBinding.values())
		{
			if(key.getButton() == button)
			{
				keyReleased(key);
			}
		}
	}

	/**
	 * What to do when any key is pressed
	 */
	default void keyPressed(KeyEvent event) {
		for(KeyBinding key : KeyBinding.values()) {
			if(Settings.getKeyCode(key) == event.getKeyCode()) {
				keyPressed(key);
			}
		}
	}

	/**
	 * What to do when a mapped KeyBinding is pressed
	 */
	default void keyPressed(KeyBinding key) {
	}

	/**
	 * What to do when any key is released
	 */
	default void keyReleased(KeyEvent event) {
		for(KeyBinding ctrl : KeyBinding.values()) {
			if(Settings.getKeyCode(ctrl) == event.getKeyCode()) {
				keyReleased(ctrl);
			}
		}
	}

	/**
	 * What to do when a mapped KeyBinding is released
	 */
	default void keyReleased(KeyBinding key) {
	}

	/**
	 * What to do when any key is typed
	 */
	default void keyTyped(char key) {
	}

	/**
	 * What to do when the mouse wheel is scrolled
	 */
	default void mouseWheel(int amount) {
	}
}
