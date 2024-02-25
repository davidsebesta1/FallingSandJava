package main;

import java.util.Timer;
import java.util.TimerTask;

import physicsEngine.PhysicsEngine;

/**
 * Instance of this class is used to calculate ticks which happened per one second. One tick happens once every physics calculations are done and image is rendered onto screen
 *
 * @see PhysicsEngine
 * @see Panel
 *
 * @author David Šebesta
 */
public class TPSCounter {
	private Timer timer = new Timer();
	
	/**
	 * Ticks Per Second
	 **/
	private short TPS;

	public void runCounter() {
		TimerTask tick = new TimerTask() {
			@Override
			public void run() {
				System.out.println(TPS);
				TPS = 0;
			}
		};

		timer.scheduleAtFixedRate(tick, 1000, 1000);
	}

	/**
	 * Returns TPS amount
	 * 
	 * @return TPS
	 **/
	protected short getTPS() {
		return TPS;
	}

	/**
	 * Adds one TPS
	 **/
	protected void addTPS() {
		TPS++;
	}
}
