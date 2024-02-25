package elements.solid;

import java.awt.Color;

import elements.Element;

public class Dirt extends Element {

	public Dirt() {
		id = 6;
		maxMoveAmount = 5;
		displayName = "Dirt";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(155, 118, 83);
			break;
		case 1:
			color = new Color(143, 109, 76);
			break;
		case 2:
			color = new Color(161, 122, 85);
			break;
		}
	}
}
