package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import elements.Element;
import elements.EmptyElement;
import elements.solid.Sand;
import physicsEngine.PhysicsEngine;

public class Main {
	private static JFrame frame = new JFrame();
	private static Panel panel;

	public static short WIDTH = 500;
	public static short HEIGHT = 500;

	private static short activeWidthStart = WIDTH;
	private static short activeWidthEnd = 0;

	private static short activeHeightStart = HEIGHT;
	private static short activeHeightEnd = 0;

	// 2D array matrixes
	private static short[][] matrix;
	private static Element[][] elementMatrix;

//	private static PhysicsEngine physEngine = new PhysicsEngine(WIDTH, HEIGHT);
	private static PhysicsEngine[] engineThreads = new PhysicsEngine[1];
	private static boolean canTick = true;
	private static TPSCounter tpsCounter = new TPSCounter();

	// stuff
	private static Timer timer = new Timer();

	public static void main(String[] args) {
		Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");

		frame.setIconImage(icon);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setSize(new Dimension(WIDTH, HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.black);
		frame.setTitle("Falling Sand : 50 TPS");
		
		// Changing width and height to match window size with decorations
		WIDTH -= 16;
		HEIGHT -= 39;
		panel = new Panel(WIDTH, HEIGHT);
		panel.setFocusable(true);


		System.out.println(WIDTH + " : " + HEIGHT);

		startup();
		//GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].setFullScreenWindow(frame);
	}

	// Startup methods sets up matrixes, fills whole game with air, spawns on sand
	// at 400 400, starts tick thread and other stuff

	/**
	 * Startup method that firstly synchronize stuff, instantiate other stuff,
	 * creates two first elements in array and finally starts tick timer.
	 */
	private static void startup() {

		matrix = new short[WIDTH][HEIGHT]; // x y
		elementMatrix = new Element[WIDTH][HEIGHT]; // x y

		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				matrix[i][j] = 0;
				elementMatrix[i][j] = new EmptyElement();
			}
		}

		matrix[400][400] = 1;
		elementMatrix[400][400] = new Sand();

		matrix[410][410] = 1;
		elementMatrix[410][410] = new Sand();

		frame.add(panel);
		frame.setVisible(true);

		// engines threads
		engineThreads[0] = new PhysicsEngine(WIDTH, HEIGHT);

		syncMatrix();

		for (int i = 0; i < engineThreads.length; i++) {
			engineThreads[i].start();
			engineThreads[i].setRunning(true);
//			System.out.println(engineThreads[i].getName());
		}

		syncMatrix();
		tpsCounter.runCounter();
		calculateRenderAreaTimer();
		setTitleUpdate();
		tick();
	}

	// Tick at 1x speed game runs at 50 Ticks Per Second (TPS)

	/**
	 * Method that calls all other methods to do stuff. Very important. Do not
	 * remove.
	 */
	private static synchronized void tick() {
		runPhysicsTick();
		TimerTask tick = new TimerTask() {
			@Override
			public void run() {
				canTick();
				if (canTick) {
					canTick = false;

					syncMatrix(); // running physics to all elements which arent EmptyElement

					runPhysicsTick();

					Point p = MouseInfo.getPointerInfo().getLocation();
					SwingUtilities.convertPointFromScreen(p, panel);

					syncMousePosition(p.x, p.y);

					if (panel.isNeedToReCalculate()) {
						calculateRenderArea();
						panel.setNeedToReCalculate(false);
					}

					panel.repaint();

					resetMovedBool(); // resetting moved property of elements

					tpsCounter.addTPS();
				}
			}
		};

		timer.scheduleAtFixedRate(tick, 20, 20);
	}

	/**
	 * Sets JFrame title to match current TPS once every second
	 */
	public static void setTitleUpdate() {
		TimerTask titleTimer = new TimerTask() {

			@Override
			public void run() {
				frame.setTitle("Falling Sand : " + tpsCounter.getTPS() + " TPS");
			}

		};
		timer.scheduleAtFixedRate(titleTimer, 999, 999);

//		System.out.println("activeWidthStart: " + activeWidthStart + "\nactiveHeightStart: " + activeHeightStart + "\n x size: " + (activeWidthEnd - activeWidthStart) + "\n y size:" + (activeHeightEnd - activeHeightStart));
	}

	private static void canTick() {
		for (int i = 0; i < engineThreads.length; i++) {
//			System.out.println(panel.isDoneDrawing());
			if(engineThreads[i].isReady() && panel.isDoneDrawing()) {
				canTick = true;
			} else {
				canTick = false;
			}
		}

	}

	/**
	 * Synchronization method to synchronize mouse positions; units and drawing
	 * units.
	 * 
	 * @param int x
	 * @param int y
	 */
	private static void syncMousePosition(int x, int y) {
		panel.syncMousePosition(x, y);
	}

	/**
	 * Calculates rendering area for PhysicsEngine and drawing threads once every
	 * 990 miliseconds.
	 * 
	 * @see PhysicsEngine
	 * @see Panel
	 */
	public static synchronized void calculateRenderAreaTimer() {
		TimerTask calculate = new TimerTask() {

			@Override
			public void run() {
				activeWidthStart = WIDTH;
				activeWidthEnd = 0;

				activeHeightStart = HEIGHT;
				activeHeightEnd = 0;
				for (short i = 0; i < WIDTH; i++) {
					for (short j = 0; j < HEIGHT; j++) {

						if (matrix[i][j] != 0) {

							if (i < activeWidthStart) {
								activeWidthStart = i;
							} else if (i > activeWidthEnd) {
								activeWidthEnd = i;
							}

							if (j < activeHeightStart) {
								activeHeightStart = j;
							} else if (j > activeHeightEnd) {
								activeHeightEnd = j;
							}

						}
					}
				}
				panel.syncRenderArea(activeWidthStart, activeWidthEnd, activeHeightStart, activeHeightEnd);

				engineThreads[0].syncRenderArea((short) (activeWidthStart), (short) (activeWidthEnd),
						(short) (activeHeightStart), (short) (activeHeightEnd));
			}

		};
		timer.scheduleAtFixedRate(calculate, 990, 990);
	}

	public static synchronized void calculateRenderArea() {
		activeWidthStart = WIDTH;
		activeWidthEnd = 0;

		activeHeightStart = HEIGHT;
		activeHeightEnd = 0;
		for (short i = 0; i < WIDTH; i++) {
			for (short j = 0; j < HEIGHT; j++) {

				if (matrix[i][j] != 0) {

					if (i < activeWidthStart) {
						activeWidthStart = i;
					} else if (i > activeWidthEnd) {
						activeWidthEnd = i;
					}

					if (j < activeHeightStart) {
						activeHeightStart = j;
					} else if (j > activeHeightEnd) {
						activeHeightEnd = j;
					}

				}
			}
		}
		panel.syncRenderArea(activeWidthStart, activeWidthEnd, activeHeightStart, activeHeightEnd);

		engineThreads[0].syncRenderArea((short) (activeWidthStart), (short) (activeWidthEnd),
				(short) (activeHeightStart), (short) (activeHeightEnd));

	}

	/**
	 * Resets moved boolean property of all elements in array.
	 */
	private static void resetMovedBool() {
		for (int x = activeWidthStart - 50; x < activeWidthEnd + 50; x++) {
			for (int y = activeHeightStart - 50; y < activeHeightEnd + 50; y++) {
				if (((x < WIDTH && x > 0) && (y < HEIGHT && y > 0))) {
					elementMatrix[x][y].setMovedThisTick(false);
				}
			}
		}
	}

	/**
	 * Synchronization method to synchronize rendering area between PhysicsEngine
	 * units and drawing units.
	 */
	private static void syncMatrix() {
		for (int i = 0; i < engineThreads.length; i++) {
			engineThreads[i].syncMatrix(matrix, elementMatrix);
		}
		panel.syncMatrix(matrix, elementMatrix);
	}

	/**
	 * Method that starts rendering tick on all PhysicsEngine units.
	 */
	private static void runPhysicsTick() {
		for (int i = 0; i < engineThreads.length; i++) {
			engineThreads[i].setCanRun(true);
		}
	}
}