package elements.solid;

import java.awt.Color;

import elements.Element;

public class Mud extends Element {

	public Mud() {
		id = 21;
		maxMoveAmount = 5;
		displayName = "Mud";
		heatAmount = random.nextInt(2) + 10;

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(96, 64, 30);
			break;
		case 1:
			color = new Color(102, 79, 34);
			break;
		case 2:
			color = new Color(100, 64, 39);
			break;
		}
	}
}
