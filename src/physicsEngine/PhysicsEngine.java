package physicsEngine;

import java.awt.Color;
import java.util.Random;

import elements.Element;
import elements.EmptyElement;
import elements.gas.Fire;
import elements.gas.Smoke;
import elements.gas.Steam;
import elements.liquid.Lava;
import elements.liquid.SaltWater;
import elements.liquid.Water;
import elements.solid.Ash;
import elements.solid.Dirt;
import elements.solid.Foiliage;
import elements.solid.Glass;
import elements.solid.Grass;
import elements.solid.Ice;
import elements.solid.Mud;
import elements.solid.Salt;
import elements.solid.Stone;
import elements.solid.Wood;

/**
 * An instance of this class is used to handle all physics calculation, heat
 * management and element interactions.
 *
 * @see Element
 *
 * @author David Šebesta
 */
public class PhysicsEngine extends Thread {

	/**
	 * Width of used JFrame
	 */
	private short WIDTH;

	/**
	 * Height of used JFrame
	 */
	private short HEIGHT;

	// Rendering area
	private short activeWidthStart = 0;
	private short activeWidthEnd = WIDTH;

	private short activeHeightStart = 0;
	private short activeHeightEnd = HEIGHT;

	// 2D array matrixes
	/**
	 * Matrix with numbers representing elements
	 **/
	private short[][] matrix;

	/**
	 * Matrix with Element instances containing informations
	 * 
	 * @see Element
	 **/
	private Element[][] elementMatrix;

	// stuff
	private Random random = new Random();

	private boolean running = false;
	private boolean ready = false;
	private boolean canRun = true;

	/**
	 * Returns smallest Integer in given array.
	 * 
	 * @param width
	 * @param height
	 **/
	public PhysicsEngine(short WIDTH, short HEIGHT) {
//		System.out.println(WIDTH + " " + HEIGHT);
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;

		this.setName("PhysicsEngineUnit");
//		System.out.println("PhysicsEngineUnit online");
	}

	/**
	 * Thread run method of PhysicsEngine.
	 * @throws InterruptedException
	 */
	@Override
	public synchronized void run() {
//		System.out.println(Thread.currentThread().getName());

		while (running) {
			if (canRun) {
				physics();
			} else {
				try {
					this.sleep(4);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * Sets canRun to by given parameter.
	 * 
	 * @param boolean canRun
	 */
	public void setCanRun(boolean canRun) {
		this.canRun = canRun;

	}

	/**
	 * Returns boolean ready
	 * 
	 * @return boolean ready
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Sets running by given parameter, also sets boolean ready to false.
	 * 
	 * @param boolean running
	 */
	public void setRunning(boolean running) {
		this.running = running;
		this.ready = false;
	}

	/**
	 * Synchronization method to synchronize matrixes between all classes that use
	 * it.
	 * 
	 * @param short[][]   matrix
	 * @param Element[][] elementMatrix
	 */
	public void syncMatrix(short[][] matrix, Element[][] elementMatrix) {
		this.matrix = matrix;
		this.elementMatrix = elementMatrix;
	}

	/**
	 * Returns matrix
	 * 
	 * @return short[][] matrix
	 */
	public short[][] getMatrix() {
		return matrix;
	}

	/**
	 * Returns elementMatrix
	 * 
	 * @return elementMatrix[][]
	 */
	public Element[][] getElementMatrix() {
		return elementMatrix;
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
	public void syncRenderArea(short activeWidthStart, short activeWidthEnd, short activeHeightStart,
			short activeHeightEnd) {
		this.activeWidthStart = activeWidthStart;
		this.activeWidthEnd = activeWidthEnd;
		this.activeHeightStart = activeHeightStart;
		this.activeHeightEnd = activeHeightEnd;
	}

	/**
	 * Determinates if cell with coordinates given in params is within array
	 * boundary and is empty (has EmptyElement in it)
	 * 
	 * @param int x
	 * @param int y
	 * @return boolean true if cell is empty, otherwise false
	 * @see EmptyElement
	 */
	private boolean isEmptyCell(int x, int y) {
		if ((x < WIDTH && x > 0) && (y < HEIGHT && y > 0 && matrix[x][y] == 0)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if element in given parameters isnt out of bounds and returns it
	 * 
	 * @param int x
	 * @param int y
	 * @Element element
	 * @see Element
	 */
	private Element getCell(int x, int y) {
		if ((x < WIDTH && x > 0) && (y < HEIGHT && y > 0)) {
			return elementMatrix[x][y];
		}
		return null;
	}

	/**
	 * Checks if ID in cell in given parameters is out of bound and returns it
	 * 
	 * @param int x
	 * @param int y
	 * @return short ID
	 */
	private short getIDCell(int x, int y) {
		if ((x < WIDTH && x > 0) && (y < HEIGHT && y > 0)) {
			return matrix[x][y];
		}
		return 0;
	}

	/**
	 * Switches array IDs and Elements in elementMatrix in given paremeters
	 * 
	 * @param int x1
	 * @param int y1
	 * @param int x1
	 * @param int y2
	 */
	private void switchCells(int x1, int y1, int x2, int y2) {

		// Element matrix switch
		Element target = getCell(x2, y2);
		if (target != null) {
			Element tempElement = elementMatrix[x1][y1];
			elementMatrix[x1][y1] = elementMatrix[x2][y2];
			elementMatrix[x2][y2] = tempElement;

			// int matrix switch
			short tempMatrix = matrix[x1][y1];
			matrix[x1][y1] = matrix[x2][y2];
			matrix[x2][y2] = tempMatrix;
		}
	}

	/**
	 * Disposes heat into surrounding cells
	 * 
	 * @param int    x
	 * @param int    y
	 * @param double removeHeatAmount
	 * @param double disposeHeatAmount
	 */
	private void disposeHeat(int x, int y, double removeHeatAmount, double disposeHeatAmount) {
		if (y + 1 < HEIGHT) {
			elementMatrix[x][y].setHeatAmount(removeHeatAmount);
			elementMatrix[x][y + 1].setHeatAmount(disposeHeatAmount);
		}

		if (y - 1 > 0) {
			elementMatrix[x][y].setHeatAmount(removeHeatAmount);
			elementMatrix[x][y - 1].setHeatAmount(disposeHeatAmount);
		}

		if (x + 1 < WIDTH) {
			elementMatrix[x][y].setHeatAmount(removeHeatAmount);
			elementMatrix[x + 1][y].setHeatAmount(disposeHeatAmount);
		}

		if (x - 1 > 0) {
			elementMatrix[x][y].setHeatAmount(removeHeatAmount);
			elementMatrix[x - 1][y].setHeatAmount(disposeHeatAmount);
		}
	}

	/**
	 * Grows tree at given coordinates
	 * 
	 * @param int x
	 * @param int y
	 */
	private void growTree(int x, int y) {
		if (isEmptyCell(x, y - 1) && isEmptyCell(x, y - 2) && isEmptyCell(x, y - 3) && isEmptyCell(x, y - 4)
				&& isEmptyCell(x, y - 5) && isEmptyCell(x, y - 6)) {
			for (int i = -3; i <= 3; i++) {
				for (int j = -9; j <= -2; j++) {
					if (isEmptyCell(x + i, y + j)) {
						elementMatrix[x + i][y + j] = new Foiliage();
						matrix[x + i][y + j] = 18;
					}
				}
			}

			for (int i = 1; i <= 6; i++) {
				elementMatrix[x][y - i] = new Wood();
				matrix[x][y - i] = 9;
			}
		}
	}

	/**
	 * Switches cell with one below given coordinates if condition is met
	 * 
	 * @param int     x
	 * @param int     y
	 * @param Element target
	 * @param short   id
	 */
	private void switchIf(int x, int y, Element target, short id) {
		if (target != null && target.getID() == id) {
			switchCells(x, y, x, y + 1);
		}
	}

	/**
	 * Switches cell with one below given coordinates if condition is met
	 * 
	 * @param int     x
	 * @param int     y
	 * @param Element target
	 * @param short   id
	 */
	private boolean sidesDown(boolean left, boolean right) {
		boolean rnd = random.nextBoolean();
		left = rnd ? true : false;
		right = rnd ? false : true;

		return left;
	}

	/**
	 * Method that does all the job
	 */
	private void physics() {
		canRun = false;
		ready = false;

		for (int x = activeWidthStart - 50; x < activeWidthEnd + 50; x++) {
			for (int y = activeHeightStart - 50; y < activeHeightEnd + 50; y++) {
				if (((x < WIDTH && x > 0) && (y < HEIGHT && y > 0)) && !elementMatrix[x][y].getMovedThisTick()) {
					int id = matrix[x][y];
					switch (id) {
					case 0: // air
						break;
					case 1: // sand
						performanceSandPhysics(x, y);
						break;
					case 2: // water
						waterPhysics(x, y);
						break;
					case 3: // stone
						stonePhysics(x, y);
						break;
					case 4: // steam
						steamPhysics(x, y);
						break;
					case 5: // lava
						lavaPhysics(x, y);
						break;
					case 6: // dirt
						performanceDirtPhysics(x, y);
						break;
					case 7: // nitrogen
						liquidNitrogenPhysics(x, y);
						break;
					case 8: // ice
						icePhysics(x, y);
						break;
					case 9: // wood
						woodPhysics(x, y);
						break;
					case 10: // fire
						firePhysics(x, y);
						break;
					case 11: // snow
						snowPhysics(x, y);
						break;
					case 12: // glass
						glassPhysics(x, y);
						break;
					case 13: // grass
						grassPhysics(x, y);
						break;
					case 14: // smoke
						smokePhysics(x, y);
						break;
					case 15: // tree seed
						treeSeedPhysics(x, y);
						break;
					case 16: // salt
						saltPhysics(x, y);
						break;
					case 17: // salt
						saltWaterPhysics(x, y);
						break;
					case 18: // foiliage
						foiliagePhysics(x, y);
						break;
					case 19: // ash
						ashPhysics(x, y);
						break;
					case 20: // bedrock
						break;
					case 21: // mud
						mudPhysics(x, y);
						break;
					default: // default
						System.out.println("Unable to find element ID " + id
								+ " , this application will self destruct in T-1 second");
						System.exit(1);
						break;
					}
				}
			}
		}

		ready = true;
	}

	private void performanceSandPhysics(int x, int y) { // performance sand
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean leftDown = isEmptyCell(x - 1, y + 1);
		boolean rightDown = isEmptyCell(x + 1, y + 1);

		boolean sidesDown = (leftDown && rightDown) ? true : false;

		if (sidesDown) {
			leftDown = sidesDown(leftDown, rightDown);
			rightDown = !leftDown;
		}

		// Switching with water
//		if (target != null && target.getID() == 2) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 2);

		// switching with fire
//		if (target != null && target.getID() == 10) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 10);

		// changing to glass if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() >= 1400) {
			elementMatrix[x][y] = new Glass();
			matrix[x][y] = 12;
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (leftDown) {
			matrix[x - 1][y + 1] = matrix[x][y];
			elementMatrix[x - 1][y + 1] = elementMatrix[x][y];
		} else if (rightDown) {
			matrix[x + 1][y + 1] = matrix[x][y];
			elementMatrix[x + 1][y + 1] = elementMatrix[x][y];
		}

		if (down || leftDown || rightDown) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void performanceDirtPhysics(int x, int y) { // performance dirt
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean leftDown = isEmptyCell(x - 1, y + 1);
		boolean rightDown = isEmptyCell(x + 1, y + 1);

		boolean sidesDown = (leftDown && rightDown) ? true : false;

		if (sidesDown) {
			leftDown = sidesDown(leftDown, rightDown);
			rightDown = !leftDown;
		}

		// grass
		if (matrix[x][y - 1] == 2 && (matrix[x][y - 2] == 13 || matrix[x][y - 2] == 21)) {
			matrix[x][y] = 21;
			elementMatrix[x][y] = new Mud();

			matrix[x][y - 1] = 0;
			elementMatrix[x][y - 1] = new EmptyElement();
		} else if (matrix[x][y - 1] == 2) {

			matrix[x][y] = 13;
			elementMatrix[x][y] = new Grass();

			matrix[x][y - 1] = 0;
			elementMatrix[x][y - 1] = new EmptyElement();
		}

		// Switching with water
//		if (target != null && target.getID() == 2) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 2);

		// switching with fire
//		if (target != null && target.getID() == 10) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 10);

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (leftDown) {
			matrix[x - 1][y + 1] = matrix[x][y];
			elementMatrix[x - 1][y + 1] = elementMatrix[x][y];
		} else if (rightDown) {
			matrix[x + 1][y + 1] = matrix[x][y];
			elementMatrix[x + 1][y + 1] = elementMatrix[x][y];
		}

		if (down || leftDown || rightDown) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void mudPhysics(int x, int y) { // performance dirt
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean leftDown = isEmptyCell(x - 1, y + 1);
		boolean rightDown = isEmptyCell(x + 1, y + 1);

		boolean sidesDown = (leftDown && rightDown) ? true : false;

		if (sidesDown) {
			leftDown = sidesDown(leftDown, rightDown);
			rightDown = !leftDown;
		}

		// Switching with water
		if (target != null && target.getID() == 2) {
			switchCells(x, y, x, y + 1);
		} else if (getIDCell(x, y - 1) == 2) {
			switchCells(x, y, x, y - 1);
		}

		switchIf(x, y, target, (short) 10);

		if (elementMatrix[x][y].getHeatAmount() > 1) {
			disposeHeat(x, y, -0.01, 0.01);
		} else {
			matrix[x][y] = 6;
			elementMatrix[x][y] = new Dirt();
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (leftDown) {
			matrix[x - 1][y + 1] = matrix[x][y];
			elementMatrix[x - 1][y + 1] = elementMatrix[x][y];
		} else if (rightDown) {
			matrix[x + 1][y + 1] = matrix[x][y];
			elementMatrix[x + 1][y + 1] = elementMatrix[x][y];
		}

		if (down || leftDown || rightDown) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void saltPhysics(int x, int y) { // salt

		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean leftDown = isEmptyCell(x - 1, y + 1);
		boolean rightDown = isEmptyCell(x + 1, y + 1);

		boolean sidesDown = (leftDown && rightDown) ? true : false;

		if (sidesDown) {
			leftDown = sidesDown(leftDown, rightDown);
			rightDown = !leftDown;
		}

		// changing water to salt water
		if (target != null && target.getID() == 2) {
			matrix[x][y + 1] = 17;
			elementMatrix[x][y + 1] = new SaltWater();

			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		} else if (getIDCell(x + 1, y) == 2) {
			matrix[x + 1][y] = 17;
			elementMatrix[x + 1][y] = new SaltWater();

			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		} else if (getIDCell(x - 1, y) == 2) {
			matrix[x - 1][y] = 17;
			elementMatrix[x - 1][y] = new SaltWater();

			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}

		// switching with fire
//		if (target != null && target.getID() == 10) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 10);

		// switching with salt water
//		if (target != null && target.getID() == 17) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 17);

		if (target != null) {
			if (target.getID() == 8 || target.getID() == 11) {
				elementMatrix[x][y + 1] = new Water();
				matrix[x][y + 1] = 2;
			}
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (leftDown) {
			matrix[x - 1][y + 1] = matrix[x][y];
			elementMatrix[x - 1][y + 1] = elementMatrix[x][y];
		} else if (rightDown) {
			matrix[x + 1][y + 1] = matrix[x][y];
			elementMatrix[x + 1][y + 1] = elementMatrix[x][y];
		}

		if (down || leftDown || rightDown) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void ashPhysics(int x, int y) { // ash

		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean leftDown = isEmptyCell(x - 1, y + 1);
		boolean rightDown = isEmptyCell(x + 1, y + 1);

		boolean sidesDown = (leftDown && rightDown) ? true : false;

		if (sidesDown) {
			leftDown = sidesDown(leftDown, rightDown);
			rightDown = !leftDown;
		}

		// mixing with water
		if (target != null) {
			if ((target.getID() == 2 || getCell(x, y - 1).getID() == 2)) {
				target.setColor(new Color(172, 197, 201));

				matrix[x][y] = 0;
				elementMatrix[x][y] = new EmptyElement();
			}
		}

		// decreasing heat
		if (elementMatrix[x][y].getHeatAmount() > 0) {
			disposeHeat(x, y, -0.1, 0.1);
		}

		// switching with fire
//		if (target != null && target.getID() == 10) {
//			switchCells(x, y, x, y + 1);
//		}

		switchIf(x, y, target, (short) 10);

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (leftDown) {
			matrix[x - 1][y + 1] = matrix[x][y];
			elementMatrix[x - 1][y + 1] = elementMatrix[x][y];
		} else if (rightDown) {
			matrix[x + 1][y + 1] = matrix[x][y];
			elementMatrix[x + 1][y + 1] = elementMatrix[x][y];
		}

		if (down || leftDown || rightDown) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void woodPhysics(int x, int y) {// wood

		// finish burn
		if (elementMatrix[x][y].getHeatAmount() > 1800) {
			if (random.nextDouble() > 0.99) {
				matrix[x][y] = 19;
				elementMatrix[x][y] = new Ash();
			} else {
				matrix[x][y] = 10;
				elementMatrix[x][y] = new Fire();
			}
		}

		// burning
		if (elementMatrix[x][y].getHeatAmount() > 350 && isEmptyCell(x, y - 1) && random.nextBoolean()) {
			matrix[x][y - 1] = 10;
			elementMatrix[x][y - 1] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 350 && isEmptyCell(x + 1, y) && random.nextBoolean()) {
			matrix[x + 1][y] = 10;
			elementMatrix[x + 1][y] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 350 && isEmptyCell(x - 1, y) && random.nextBoolean()) {
			matrix[x - 1][y] = 10;
			elementMatrix[x - 1][y] = new Fire();
		}
	}

	private void treeSeedPhysics(int x, int y) { // tree seeb
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);

		// burning
		if (elementMatrix[x][y].getHeatAmount() >= 300) {
			matrix[x][y] = 10;
			elementMatrix[x][y] = new Fire();
		}

		// growing
		if (target != null && target.getID() == 13) {
			growTree(x, y);
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];

			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void waterPhysics(int x, int y) { // water
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sides = (left && right) ? true : false;

		if (sides) {
			left = sidesDown(left, right);
			right = !left;
		}

		// changing to steam if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() >= 100) {
			elementMatrix[x][y] = new Steam();
			matrix[x][y] = 4;
		}

		// changing to water if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() < 0) {
			elementMatrix[x][y] = new Ice();
			matrix[x][y] = 8;
		}

		// killing fire
		if (down && target.getID() == 10) {
			elementMatrix[x][y + 1] = new EmptyElement();
			matrix[x][y + 1] = 0;

			switchCells(x, y, x, y + 1);
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];
		} else if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];
		}

		if (down || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void saltWaterPhysics(int x, int y) { // salt water
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sides = (left && right) ? true : false;

		if (sides) {
			left = sidesDown(left, right);
			right = !left;
		}

		// changing to steam if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() >= 100 && isEmptyCell(x, y - 1)) {
			elementMatrix[x][y - 1] = new Steam();
			matrix[x][y - 1] = 4;

			elementMatrix[x][y] = new Salt();
			matrix[x][y] = 16;
		}

		// killing fire
		if (down && target.getID() == 10) {
			elementMatrix[x][y + 1] = new EmptyElement();
			matrix[x][y + 1] = 0;

			switchCells(x, y, x, y + 1);
		}

		// switching with water
		if (target != null && target.getID() == 2) {
			switchCells(x, y, x, y + 1);
		} else if (getIDCell(x + 1, y) == 2) {
			switchCells(x, y, x + 1, y);
		} else if (getIDCell(x - 1, y) == 2) {
			switchCells(x, y, x - 1, y);
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];
		} else if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];
		}

		if (down || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void stonePhysics(int x, int y) {// stone
		elementMatrix[x][y].setMovedThisTick(true);
		// changing to lava if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() > 1200) {
			elementMatrix[x][y] = new Lava();
			matrix[x][y] = 5;
		}
	}

	private void glassPhysics(int x, int y) {// glass
		elementMatrix[x][y].setMovedThisTick(true);
		// changing to lava if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() > 1) {

			// decreasing heat
			disposeHeat(x, y, -1, 1 + random.nextInt(2));
		}
	}

	private void grassPhysics(int x, int y) {// grass
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y - 1);

		boolean down = isEmptyCell(x, y + 1);

		// Switching with water
//		if (target != null && target.getID() == 2) {
//			switchCells(x, y, x, y - 1);
//		}

		switchIf(x, y, target, (short) 2);

		// changing to lava if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() > 400) {
			matrix[x][y] = 10;
			elementMatrix[x][y] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 270 && isEmptyCell(x, y - 1) && random.nextBoolean()) {
			matrix[x][y - 1] = 10;
			elementMatrix[x][y - 1] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 270 && isEmptyCell(x + 1, y) && random.nextBoolean()) {
			matrix[x + 1][y] = 10;
			elementMatrix[x + 1][y] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 270 && isEmptyCell(x - 1, y) && random.nextBoolean()) {
			matrix[x - 1][y] = 10;
			elementMatrix[x - 1][y] = new Fire();
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void foiliagePhysics(int x, int y) {// grass
		elementMatrix[x][y].setMovedThisTick(true);

		// changing to lava if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() > 400) {
			matrix[x][y] = 10;
			elementMatrix[x][y] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 270 && isEmptyCell(x, y - 1) && random.nextBoolean()) {
			matrix[x][y - 1] = 10;
			elementMatrix[x][y - 1] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 270 && isEmptyCell(x + 1, y) && random.nextBoolean()) {
			matrix[x + 1][y] = 10;
			elementMatrix[x + 1][y] = new Fire();
		} else if (elementMatrix[x][y].getHeatAmount() > 270 && isEmptyCell(x - 1, y) && random.nextBoolean()) {
			matrix[x - 1][y] = 10;
			elementMatrix[x - 1][y] = new Fire();
		}
	}

	private void firePhysics(int x, int y) { // steam
		elementMatrix[x][y].setMovedThisTick(true);
		elementMatrix[x][y].setMovedAmount((short) (elementMatrix[x][y].getMovedAmount() + 1));

//		boolean down = isEmptyCell(x, y + 1);
		boolean up = isEmptyCell(x, y - 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sidesDown = (left && right) ? true : false;

		if (sidesDown) {
			left = sidesDown(left, right);
			right = !left;
		}

		// changing to smoke
		if (elementMatrix[x][y].getHeatAmount() < 400) {
			elementMatrix[x][y] = new Smoke();
			matrix[x][y] = 14;
		}

		// changing to empty
		if (elementMatrix[x][y].getMovedAmount() > elementMatrix[x][y].getMaxMoveAmount()) {
			elementMatrix[x][y] = new EmptyElement();
			matrix[x][y] = 0;
		}

		// decreasing heat
		disposeHeat(x, y, -10 + random.nextInt(5), 30 + random.nextInt(10));

		if (up) {
			matrix[x][y - 1] = matrix[x][y];
			elementMatrix[x][y - 1] = elementMatrix[x][y];

		}
		if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];

		}

		else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];

		}

		if (up || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void smokePhysics(int x, int y) { // steam
		elementMatrix[x][y].setMovedThisTick(true);
		elementMatrix[x][y].setMovedAmount((short) (elementMatrix[x][y].getMovedAmount() + 1));

		Element target = getCell(x, y - 1);

//		boolean down = isEmptyCell(x, y + 1);
		boolean up = isEmptyCell(x, y - 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sidesDown = (left && right) ? true : false;

		if (sidesDown) {
			left = sidesDown(left, right);
			right = !left;
		}

		// changing to nothing

		if (elementMatrix[x][y].getMovedAmount() > elementMatrix[x][y].getMaxMoveAmount()) {
			elementMatrix[x][y] = new EmptyElement();
			matrix[x][y] = 0;
		}

		// switching with fire
		if (target != null && target.getID() == 10) {
			switchCells(x, y, x, y - 1);
		}

		// decreasing heat
		disposeHeat(x, y, -1 + random.nextInt(5), 1 + random.nextInt(5));

		if (up) {
			matrix[x][y - 1] = matrix[x][y];
			elementMatrix[x][y - 1] = elementMatrix[x][y];

		} else if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];

		}

		else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];

		}

		if (up || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void steamPhysics(int x, int y) { // steam
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y - 1);

//		boolean down = isEmptyCell(x, y + 1);
		boolean up = isEmptyCell(x, y - 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sidesDown = (left && right) ? true : false;

		if (sidesDown) {
			left = sidesDown(left, right);
			right = !left;
		}

		// changing to water of heat is low enough
		if (elementMatrix[x][y].getHeatAmount() < 100) {
			elementMatrix[x][y] = new Water();
			matrix[x][y] = 2;
		}

		// decreasing heat
		disposeHeat(x, y, -0.5, 0.5);

		// switching with water
		if (target != null && (target.getID() == 2 || target.getID() == 5)) {
			switchCells(x, y, x, y - 1);
		}

		if (up) {
			matrix[x][y - 1] = matrix[x][y];
			elementMatrix[x][y - 1] = elementMatrix[x][y];
		} else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];
		} else if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];
		}

		if (up || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void lavaPhysics(int x, int y) { // lava
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sidesDown = (left && right) ? true : false;

		if (sidesDown) {
			left = sidesDown(left, right);
			right = !left;
		}

		// decreasing heat
		disposeHeat(x, y, -10, 50 + random.nextInt(10));

		if(down) {
			switchIf(x, y, target, (short) 4);
			switchIf(x, y, target, (short) 10);
		}

		// changing to stone of heat is high enough
		if (elementMatrix[x][y].getHeatAmount() <= 1000) {
			elementMatrix[x][y] = new Stone();
			matrix[x][y] = 3;
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];
		} else if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];
		}

		if (down || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void liquidNitrogenPhysics(int x, int y) { // liquid Nitrogen
		elementMatrix[x][y].setMovedThisTick(true);

		Element target = getCell(x, y + 1);

		boolean down = isEmptyCell(x, y + 1);
		boolean left = isEmptyCell(x - 1, y);
		boolean right = isEmptyCell(x + 1, y);

		boolean sidesDown = (left && right) ? true : false;

		if (sidesDown) {
			left = sidesDown(left, right);
			right = !left;
		}

		// decreasing heat
		disposeHeat(x, y, 4, -40 - random.nextInt(10));

		// changing to nothing if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() >= -196) {
			elementMatrix[x][y] = new EmptyElement();
			matrix[x][y] = 0;
		}

		// killing fire
		if (down && target.getID() == 10) {
			elementMatrix[x][y + 1] = new EmptyElement();
			matrix[x][y + 1] = 0;

			switchCells(x, y, x, y + 1);
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (left) {
			matrix[x - 1][y] = matrix[x][y];
			elementMatrix[x - 1][y] = elementMatrix[x][y];
		} else if (right) {
			matrix[x + 1][y] = matrix[x][y];
			elementMatrix[x + 1][y] = elementMatrix[x][y];
		}

		if (down || left || right) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}

	private void icePhysics(int x, int y) {// ice
		elementMatrix[x][y].setMovedThisTick(true);
		// changing to water if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() > 0) {
			elementMatrix[x][y] = new Water();
			matrix[x][y] = 2;
		}

		// decreasing heat
		disposeHeat(x, y, 0.1, -0.1);
	}

	private void snowPhysics(int x, int y) { // snow
		elementMatrix[x][y].setMovedThisTick(true);

		boolean down = isEmptyCell(x, y + 1);
		boolean leftDown = isEmptyCell(x - 1, y + 1);
		boolean rightDown = isEmptyCell(x + 1, y + 1);

		boolean sidesDown = (leftDown && rightDown) ? true : false;

		if (sidesDown) {
			leftDown = sidesDown(leftDown, rightDown);
			rightDown = !leftDown;
		}

		// changing to water if heat is high enough
		if (elementMatrix[x][y].getHeatAmount() >= 0) {
			elementMatrix[x][y] = new Water();
			matrix[x][y] = 2;
		}

		if (down) {
			matrix[x][y + 1] = matrix[x][y];
			elementMatrix[x][y + 1] = elementMatrix[x][y];
		} else if (leftDown) {
			matrix[x - 1][y + 1] = matrix[x][y];
			elementMatrix[x - 1][y + 1] = elementMatrix[x][y];
		} else if (rightDown) {
			matrix[x + 1][y + 1] = matrix[x][y];
			elementMatrix[x + 1][y + 1] = elementMatrix[x][y];
		}

		if (down || leftDown || rightDown) {
			matrix[x][y] = 0;
			elementMatrix[x][y] = new EmptyElement();
		}
	}
}