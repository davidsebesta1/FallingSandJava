package elements;

import java.awt.Color;
import java.util.Random;

/**
 * Element class
 *
 * @author David Šebesta
 */
public abstract class Element {

	protected Random random = new Random();

	/**
	 * Indentification number of Element
	 **/
	protected short id;
	
	/**
	 * Color of Element
	 **/
	protected Color color;
	
	/**
	 * Amount of heat that this element contains
	 **/
	protected double heatAmount = 1;

	/**
	 * Boolean MovedThisTick makes sure Element doesnt move multiple times during single tick.
	 **/
	protected boolean movedThisTick = false;

	/**
	 * Maximum move amount before Element stops moving or does something else
	 **/
	protected short maxMoveAmount = 10;
	
	/**
	 * Amount that Element moved
	 **/
	protected short movedAmount = 0;

	/**
	 * Display name of element near mouse cursor
	 **/
	protected String displayName = "default";

	public short getID() {
		return id;
	}

	public int getColor() {
		return color.getRGB();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public short getMovedAmount() {
		return movedAmount;
	}

	public void setMovedAmount(short movedAmount) {
		this.movedAmount = movedAmount;
	}

	public boolean getMovedThisTick() {
		return movedThisTick;
	}

	public void setMovedThisTick(boolean movedThisTick) {
		this.movedThisTick = movedThisTick;
	}

	public double getHeatAmount() {
		return heatAmount;
	}

	public void setHeatAmount(double heatAdd) {
		heatAmount += heatAdd;
	}

	public short getMaxMoveAmount() {
		return maxMoveAmount;
	}

	public String getDisplayName() {
		return displayName;
	}
}
