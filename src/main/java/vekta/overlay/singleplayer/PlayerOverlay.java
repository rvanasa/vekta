package vekta.overlay.singleplayer;

import vekta.player.Player;
import vekta.player.PlayerListener;
import vekta.object.ship.ModularShip;
import vekta.overlay.Overlay;

import static processing.core.PConstants.LEFT;
import static vekta.Vekta.BODY_FONT;
import static vekta.Vekta.v;

public class PlayerOverlay implements Overlay, PlayerListener {
	private final Player player;

	private Overlay[] overlays;
	private NotificationOverlay notifications;
	private DebugOverlay debug;

	public PlayerOverlay(Player player) {
		this.player = player;
		reset();
	}

	public void reset() {
		overlays = new Overlay[] {
				new NavigationOverlay(player),
				new MissionOverlay(player),
				new ShipComputerOverlay(50, -150, player.getShip()),
				//				new ShipMoneyOverlay(-300, -90, player.getShip()),
				new ShipMassOverlay(-300, -90, player.getShip()),
				new ShipEnergyOverlay(-300, -60, player.getShip()),
				new ShipTemperatureOverlay(-300, -30, player.getShip()),
				new PlayerScoreOverlay(30, -30, player),
				new DirectoryOverlay(player),
				new TimeScaleOverlay(),
				notifications = new NotificationOverlay(-20, 80),
		};
	}

	@Override
	public void render() {
		// Set overlay text settings
		v.textFont(BODY_FONT);
		v.textAlign(LEFT);
		v.textSize(16);

		for(Overlay overlay : overlays) {
			overlay.render();
		}
	}

	@Override
	public void onChangeShip(ModularShip ship) {
		reset();
	}

	@Override
	public void onNotification(Notification notification) {
		notifications.add(notification);
	}
}
