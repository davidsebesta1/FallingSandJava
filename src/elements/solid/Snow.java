package elements.solid;

import java.awt.Color;

import elements.Element;

public class Snow extends Element {

	public Snow() {
		id = 11;
		maxMoveAmount = 5;
		displayName = "Snow";

		heatAmount = -3;

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(255, 252, 250);
			break;
		case 1:
			color = new Color(253, 250, 250);
			break;
		case 2:
			color = new Color(255, 250, 252);
			break;
		}
	}
}
