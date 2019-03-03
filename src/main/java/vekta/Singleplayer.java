package vekta;

import processing.core.PVector;
import processing.sound.LowPass;
import vekta.context.PauseMenuContext;
import vekta.context.World;
import vekta.item.ModuleItem;
import vekta.object.*;
import vekta.object.module.*;
import vekta.overlay.Overlay;
import vekta.overlay.singleplayer.SingleplayerOverlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static vekta.Vekta.*;

public class Singleplayer implements World {
	private static int nextID = 0;

	// Low pass filter
	private LowPass lowPass;

	// Camera position tracking
	private PVector cameraPos;
	private float cameraSpd;

	private PlayerShip playerShip;

	private float zoom = 1; // Camera zoom factor

	private Counter targetCt = new Counter(30); // Counter for periodically updating Targeter instances
	private Counter spawnCt = new Counter(100); // Counter for periodically cleaning/spawning objects

	private final List<SpaceObject> objects = new ArrayList<>();
	private final List<SpaceObject> markedForDeath = new ArrayList<>();
	private final List<SpaceObject> markedForAddition = new ArrayList<>();

	private Overlay overlay;

	@Override
	public void start() {
		Vekta v = getInstance();
		v.frameCount = 0;

		lowPass = new LowPass(v);

		Resources.setMusic("atmosphere");

		WorldGenerator.createSystem(PVector.random2D().mult(v.random(1000, 2000)));

		playerShip = new PlayerShip(
				"VEKTA I",
				PVector.fromAngle(0), // Heading
				new PVector(), // Position
				new PVector(),    // Velocity
				v.color(0, 255, 0)
		);
		playerShip.getInventory().add(50); // Starting money
		addObject(playerShip);

//		Ship ship = new CargoShip(
//				"Test Ship",
//				PVector.random2D(), // Heading
//				new PVector(500, 500), // Position
//				new PVector(),    // Velocity
//				v.color(255)
//		);
//		ship.getInventory().add(new ModuleItem(new RCSModule(2)));
//		addObject(ship);

		//// TEMP
		playerShip.addModule(new AutopilotModule());
		playerShip.getInventory().add(new ModuleItem(new DrillModule(2)));
		playerShip.getInventory().add(new ModuleItem(new TorpedoModule(2)));
		playerShip.getInventory().add(new ModuleItem(new HyperdriveModule(1)));
		playerShip.getInventory().add(new ModuleItem(new TractorBeamModule(1)));
		////

		// Configure UI overlay
		overlay = new SingleplayerOverlay(playerShip);
	}

	@Override
	public void focus() {
	}

	@Override
	public void render() {
		Vekta v = getInstance();
		v.background(0);

		if(!playerShip.isDestroyed()) {
			// Camera follow
			cameraPos = playerShip.getPosition();
			cameraSpd = playerShip.getVelocity().mag();
		}
		
		// Set up world camera
		v.hint(ENABLE_DEPTH_TEST);
		v.camera(cameraPos.x, cameraPos.y, min(MAX_CAMERA_Y, (.07F * cameraSpd + .7F) * (v.height / 2F) / tan(PI * 30 / 180) * zoom), cameraPos.x, cameraPos.y, 0F,
				0F, 1F, 0F);
		
		cameraPos = playerShip.getPosition();

		boolean targeting = targetCt.cycle();
		boolean spawning = spawnCt.cycle();

		objects.addAll(markedForAddition);
		markedForAddition.clear();

		int planetCount = 0;
		for(SpaceObject s : objects) {
			if(markedForDeath.contains(s)) {
				continue;
			}

			// Run on targeting loop
			if(targeting) {
				Collection<Targeter> ts = s.getTargeters();
				if(ts != null) {
					for(Targeter t : ts) {
						if(t.shouldUpdateTarget()) {
							updateTargeters(s);
						}
					}
				}
			}

			// Run on spawning loop
			if(spawning) {
				if(playerShip.getPosition().sub(s.getPosition()).magSq() > WorldGenerator.getRadius() * WorldGenerator.getRadius()) {
					removeObject(s);
					continue;
				}
			}

			if(s instanceof Planet) {
				planetCount++;
			}

			s.update();
			s.applyInfluenceVector(objects);
			for(SpaceObject other : objects) {
				if(s != other) {
					// Check both collisions before firing events (prevents race conditions)
					boolean collides1 = s.collidesWith(other);
					boolean collides2 = other.collidesWith(s);
					if(collides1) {
						s.onCollide(other);
					}
					if(collides2) {
						other.onCollide(s);
					}
				}
			}
			s.draw();
			s.drawTrail();
		}

		objects.removeAll(markedForDeath);
		markedForDeath.clear();

		if(planetCount < MAX_PLANETS) {
			WorldGenerator.spawnOccasional(playerShip.getPosition());
		}

		// GUI setup
		v.camera();
		v.noLights();
		v.hint(DISABLE_DEPTH_TEST);
		if(!playerShip.isDestroyed()) {
			overlay.draw();
		}
		else {
			v.textFont(headerFont);
			v.textAlign(CENTER, CENTER);

			// Header text
			v.stroke(0);
			v.fill(255, 0, 0);
			v.text("You died.", v.width / 2F, v.height / 2F - 100);

			// Body text
			v.stroke(0);
			v.fill(255);
			v.textFont(bodyFont);
			v.text("X TO RETRY", v.width / 2F, (v.height / 2F) + 97);
		}
	}

	@Override
	public void keyPressed(char key) {
		if(playerShip.isDestroyed()) {
			if(key == 'x') {
				restart();
			}
		}
		else {
			if(key == ESC) {
				setContext(new PauseMenuContext(this));
			}
			if(key == 'k') {
				playerShip.destroyBecause(playerShip);
			}
			playerShip.onKeyPress(key);
		}
	}

	@Override
	public void keyReleased(char key) {
		playerShip.onKeyRelease(key);
	}

	@Override
	public void mouseWheel(int amount) {
		zoom = max(.1F, min(3, zoom * (1 + amount * .1F)));
	}

	public void setDead() {
		if(Resources.getMusic() != null)
			lowPass.process(Resources.getMusic(), 800);
		Resources.stopAllSounds();
		Resources.playSound("death");
	}

	@Override
	public void restart() {
		lowPass.stop();
		startWorld(new Singleplayer());
	}

	public PlayerShip getPlayerShip() {
		return playerShip;
	}

	@Override
	public void addObject(Object object) {
		if(object instanceof SpaceObject) {
			SpaceObject s = (SpaceObject)object;
			s.setID(nextID++);
			markedForAddition.add(s);
		}
		else {
			throw new RuntimeException("Cannot add object: " + object);
		}
	}

	@Override
	public void removeObject(Object object) {
		if(object instanceof SpaceObject) {
			markedForDeath.add((SpaceObject)object);
		}
		else {
			throw new RuntimeException("Cannot remove object: " + object);
		}
	}

	@Override
	public void updateTargeters(SpaceObject s) {
		for(Targeter t : s.getTargeters()) {
			SpaceObject target = t.getTarget();
			float minDistSq = Float.POSITIVE_INFINITY;
			// Search for new targets
			for(SpaceObject other : objects) {
				if(s != other && t.isValidTarget(other)) {
					float distSq = s.getPosition().sub(other.getPosition()).magSq();
					if(distSq < minDistSq) {
						minDistSq = distSq;
						target = other;
					}
				}
			}
			t.setTarget(target);
		}
	}

	@Override
	public void playSound(String sound, PVector location) {
		float distance = getPlayerShip().getPosition().dist(location);
		float distanceX = getPlayerShip().getPosition().x - location.x;

		// Pan
		float pan = (MAX_PAN_DISTANCE - distanceX) / MAX_PAN_DISTANCE;
		if(pan < -1)
			pan = -1;
		if(pan > 1)
			pan = 1;

		// Volume
		float volume = (MAX_AUDITORY_DISTANCE - distance) / MAX_AUDITORY_DISTANCE;
		if(volume < 0)
			volume = 0;
		if(volume > 1)
			volume = 1;

		Resources.setSoundVolume(sound, volume);
		Resources.setSoundPan(sound, pan);
		Resources.playSound(sound);
		Resources.resetSoundVolumeAndPan(sound);
	}
}
