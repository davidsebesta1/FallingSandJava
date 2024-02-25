package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import elements.Element;
import elements.EmptyElement;
import elements.gas.Fire;
import elements.gas.Smoke;
import elements.gas.Steam;
import elements.liquid.Lava;
import elements.liquid.LiquidNitrogen;
import elements.liquid.SaltWater;
import elements.liquid.Water;
import elements.other.TreeSeed;
import elements.solid.Ash;
import elements.solid.Bedrock;
import elements.solid.Dirt;
import elements.solid.Foiliage;
import elements.solid.Glass;
import elements.solid.Grass;
import elements.solid.Ice;
import elements.solid.Mud;
import elements.solid.Salt;
import elements.solid.Sand;
import elements.solid.Snow;
import elements.solid.Stone;
import elements.solid.Wood;
import physicsEngine.PhysicsEngine;

/**
 * Instance of this class is responsible for handling image drawing, getting
 * keyboard and mouse input from user.
 *
 * @see PhysicsEngine
 *
 * @author David �ebesta
 */
public class Panel extends JPanel implements KeyListener, MouseWheelListener {
	private static final long serialVersionUID = 1;

	// width and height of a panel
	private static short WIDTH = 500;
	private static short HEIGHT = 500;

	// Rendering area coordinates
	private short activeWidthStart = 0;
	private short activeWidthEnd = WIDTH;

	private short activeHeightStart = 0;
	private short activeHeightEnd = HEIGHT;

	// 2D array matrixes
	private short[][] matrix;
	private Element[][] elementMatrix;

	// Choosed element id
	private short choosedElementID;

	private String displayText = "Empty";

	// Brush sizes
	private static int brushSizeX = 1;
	private static int brushSizeY = 1;

	// Mouse coordinates
	private static int xPos = 0;
	private static int yPos = 0;

	// Black color in Integer format
	private int backgroundColor = new Color(0, 0, 0).getRGB();

	// Debug booleans
	private boolean drawDebugRect = false;
	private boolean drawDebugBrushSize = true;
	private boolean debugClickAndDragBrush = true;
	private boolean debugDrawSelectedElementText = true;
	private boolean isControlDown = false;

	private boolean holdingMouseButton = false;
	private boolean needToReCalculate = false;
	
	private static boolean isDoneDrawing = false;

	// Buffered Image
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // creating buffered
																								// image

	/**
	 * Constructor of this class
	 *
	 * @param short width of the panel
	 * @param short height of the panel
	 */
	public Panel(short width, short height) {
		image.setAccelerationPriority(1);
		WIDTH = width;
		HEIGHT = height;

		this.setSize(WIDTH, HEIGHT);
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		this.addMouseListener(new MouseListener() { // Mouse Input

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				holdingMouseButton = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) { // https://stackoverflow.com/questions/12396066/how-to-get-location-of-a-mouse-click-relative-to-a-swing-window
				holdingMouseButton = false;

				xPos = e.getX() - brushSizeX / 2;
				yPos = e.getY() - brushSizeY / 2;
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

		});
		this.addKeyListener(this);
		this.addMouseWheelListener(this);
		this.setFocusable(true); // https://www.daniweb.com/programming/software-development/threads/384681/keylistener-not-working
		this.requestFocus();
		
	}

	/**
	 * Fills area with given coordinates to given coordinates with specific given
	 * elements.
	 * 
	 * @throws IllegalArgumentException
	 */
	private void fillBrushRect() {
		for (int i = 0 - brushSizeX / 2; i <= brushSizeX / 2; i++) {
			for (int j = 0 - brushSizeY / 2; j <= brushSizeY / 2; j++) {
				if ((xPos + i < WIDTH && xPos + i > 0) && (yPos + j < HEIGHT && yPos + j > 0)) {
					matrix[xPos + i][yPos + j] = choosedElementID;

					elementMatrix[xPos + i][yPos + j] = switch (choosedElementID) {
					case 0 -> new EmptyElement();
					case 1 -> new Sand();
					case 2 -> new Water();
					case 3 -> new Stone();
					case 4 -> new Steam();
					case 5 -> new Lava();
					case 6 -> new Dirt();
					case 7 -> new LiquidNitrogen();
					case 8 -> new Ice();
					case 9 -> new Wood();
					case 10 -> new Fire();
					case 11 -> new Snow();
					case 12 -> new Glass();
					case 13 -> new Grass();
					case 14 -> new Smoke();
					case 15 -> new TreeSeed();
					case 16 -> new Salt();
					case 17 -> new SaltWater();
					case 18 -> new Foiliage();
					case 19 -> new Ash();
					case 20 -> new Bedrock();
					case 21 -> new Mud();
					default -> throw new IllegalArgumentException("Unexpected value: " + choosedElementID);
					};
				}
			}
		}

		needToReCalculate = true;
	}

	/**
	 * Synchronization method to synchronize matrixes between all classes that use
	 * it.
	 * 
	 * @param short[][]   matrix
	 * @param Element[][] elementMatrix
	 */
	protected void syncMatrix(short[][] matrix, Element[][] elementMatrix) {
		this.matrix = matrix;
		this.elementMatrix = elementMatrix;
	}

	/**
	 * Synchronization method to synchronize rendering area between PhysicsEngine
	 * units and drawing units.
	 * 
	 * @param short activeWidthStart
	 * @param short activeWidthEnd
	 * @param short activeHeightStart
	 * @param short activeHeightEnd
	 */
	protected void syncRenderArea(short activeWidthStart, short activeWidthEnd, short activeHeightStart,
			short activeHeightEnd) {
		this.activeWidthStart = activeWidthStart;
		this.activeWidthEnd = activeWidthEnd;
		this.activeHeightStart = activeHeightStart;
		this.activeHeightEnd = activeHeightEnd;
	}

	/**
	 * Synchronization method to synchronize mouse positions; units and drawing
	 * units.
	 * 
	 * @param int x
	 * @param int y
	 */
	protected void syncMousePosition(int x, int y) {
		xPos = x;
		yPos = y;
	}
	
	public boolean isDoneDrawing() {
		return isDoneDrawing;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int inputKeyCode = e.getKeyCode();

		switch (inputKeyCode) {
		case KeyEvent.VK_ESCAPE:
			System.out.println("Exiting game");
			System.exit(0);
			break;
		case KeyEvent.VK_ADD: // +
			if (brushSizeX + 1 > 0) {
				brushSizeX++;
				brushSizeY++;
				System.out.println("Changed brush size to " + brushSizeX);
			} else {
				System.out.println("Unable to change brush size below zero");
			}
			break;
		case KeyEvent.VK_SUBTRACT: // -
			if (brushSizeX - 1 > 0) {
				brushSizeX--;
				brushSizeY--;
				System.out.println("Changed brush size to " + brushSizeX);
			} else {
				System.out.println("Unable to change brush size below zero");
			}
			break;
		case KeyEvent.VK_B: // -
			if (drawDebugRect) {
				drawDebugRect = false;
			} else {
				drawDebugRect = true;
			}
			break;
		case KeyEvent.VK_CONTROL:// holding ctrl
			isControlDown = true;
			break;
		case KeyEvent.VK_NUMPAD0: // empty
			System.out.println("Changed to empty");
			choosedElementID = 0;
			break;
		case KeyEvent.VK_NUMPAD1: // sand
			System.out.println("Changed to sand");
			choosedElementID = 1;
			break;
		case KeyEvent.VK_NUMPAD2: // water
			System.out.println("Changed to water");
			choosedElementID = 2;
			break;
		case KeyEvent.VK_NUMPAD3: // stone
			System.out.println("Changed to stone");
			choosedElementID = 3;
			break;
		case KeyEvent.VK_NUMPAD4: // salt
			System.out.println("Changed to salt");
			choosedElementID = 16;
			break;
		case KeyEvent.VK_NUMPAD5: // lava
			System.out.println("Changed to lava");
			choosedElementID = 5;
			break;
		case KeyEvent.VK_NUMPAD6: // dirt
			System.out.println("Changed to dirt");
			choosedElementID = 6;
			break;
		case KeyEvent.VK_NUMPAD7: // liquid nitrogen
			System.out.println("Changed to liquid nitrogen");
			choosedElementID = 7;
			break;
		case KeyEvent.VK_NUMPAD8: // snow
			System.out.println("Changed to snow");
			choosedElementID = 11;
			break;
		case KeyEvent.VK_NUMPAD9: // tree seeb
			System.out.println("Changed to tree seed");
			choosedElementID = 15;
			break;
		}
		changeDisplayText();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int inputKeyCode = e.getKeyCode();

		switch (inputKeyCode) {
		case KeyEvent.VK_CONTROL:
			isControlDown = false;
			break;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		if (!isControlDown) {
			if ((e.getWheelRotation() > 0 || e.getWheelRotation() < 0) && (choosedElementID - e.getWheelRotation() >= 0
					&& choosedElementID - e.getWheelRotation() <= 20)) {
				choosedElementID -= e.getWheelRotation();

				changeDisplayText();
			}
		} else {
			if ((e.getWheelRotation() > 0 || e.getWheelRotation() < 0) && (brushSizeX - e.getWheelRotation() > 0)) {
				brushSizeX -= e.getWheelRotation();
				brushSizeY -= e.getWheelRotation();

				System.out.println("x: " + brushSizeX);
			} else {
				System.out.println("Unable to change brush size below zero");
			}
		}
	}

	/**
	 * Changes display text of selected element up-right of cursor
	 * 
	 *  @throws IllegalArgumentException
	 */
	private void changeDisplayText() {
		displayText = switch (choosedElementID) {
		case 0 -> "Air";
		case 1 -> "Sand";
		case 2 -> "Water";
		case 3 -> "Stone";
		case 4 -> "Steam";
		case 5 -> "Lava";
		case 6 -> "Dirt";
		case 7 -> "Liquid Nitrogen";
		case 8 -> "Ice";
		case 9 -> "Wood";
		case 10 -> "Fire";
		case 11 -> "Snow";
		case 12 -> "Glass";
		case 13 -> "Grass";
		case 14 -> "Smoke";
		case 15 -> "Tree Seed";
		case 16 -> "Salt";
		case 17 -> "Salt Water";
		case 18 -> "Foiliage";
		case 19 -> "Ash";
		case 20 -> "Bedrock";
		case 21 -> "Mud";
		default -> throw new IllegalArgumentException("Unexpected value: " + choosedElementID);
		};
//		System.out.println(displayText);
	}

	/**
	 * Returns boolean needToReCalculate
	 * 
	 * @return boolean needToReCalculate
	 */
	protected boolean isNeedToReCalculate() {
		return needToReCalculate;
	}

	/**
	 * Sets boolean needToReCalculate
	 * 
	 * @param boolean needToReCalculate
	 */
	protected void setNeedToReCalculate(boolean needToReCalculate) {
		this.needToReCalculate = needToReCalculate;
	}
	
	/**
	 * Paints pixels
	 * 
	 * @params Graphics g
	 * @see Graphics
	 */
	@Override
	public synchronized void paintComponent(Graphics g) {
		isDoneDrawing = false;
		Graphics2D g2D = (Graphics2D) g;

		if (holdingMouseButton && debugClickAndDragBrush) {
			fillBrushRect();
		}

		imageDrawThread1.run();

		g2D.drawImage(image, 0, 0, null); // drawing buffered image at 0,0

		g2D.setColor(Color.red);

//		System.out.println("activeWidthStart: " + activeWidthStart + "\nactiveHeightStart: " + activeHeightStart + "\n x size: " + (activeWidthEnd - activeWidthStart) + "\n y size:" + (activeHeightEnd - activeHeightStart));

		if (drawDebugRect) {
			g2D.drawRect(activeWidthStart - 50, activeHeightStart - 50, (activeWidthEnd - activeWidthStart) + 100,
					(activeHeightEnd - activeHeightStart) + 100);
		}

		if (drawDebugBrushSize) {
			g2D.drawRect(xPos - brushSizeX / 2, yPos - brushSizeY / 2, brushSizeX, brushSizeY);
		}

		if (debugDrawSelectedElementText) {
			g2D.setFont(new Font("SansSerif", Font.PLAIN, 14));
			g2D.setColor(Color.black);
			g2D.drawString(displayText, xPos + brushSizeX / 2 + 4, yPos - brushSizeY / 2 + 4);

			g2D.setColor(Color.gray);
			g2D.drawString(displayText, xPos + brushSizeX / 2 + 5, yPos - brushSizeY / 2 + 5);
		}
		isDoneDrawing = true;
	}

	Runnable imageDrawThread1 = new Runnable() {

		public synchronized void run() {
//					 Getting color of each pixel and adding them to buffered image
			for (int i = activeWidthStart - 50; i < activeWidthEnd + 50; i++) {
				for (int j = activeHeightStart - 50; j < activeHeightEnd + 50; j++) {
					if (((i < WIDTH && i > 0) && (j < HEIGHT && j > 0)) && matrix[i][j] != 0) {
						image.setRGB(i, j, elementMatrix[i][j].getColor());
					} else if ((i < WIDTH && i > 0) && (j < HEIGHT && j > 0)) {
						image.setRGB(i, j, backgroundColor);
					}
				}
			}
		}
	};

}
