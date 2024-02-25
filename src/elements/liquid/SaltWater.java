package elements.liquid;

import java.awt.Color;

import elements.Element;

public class SaltWater extends Element {

	public SaltWater() {
		id = 17;
		displayName = "Salt Water";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(210, 245, 255);
			break;
		case 1:
			color = new Color(215, 250, 250);
			break;
		case 2:
			color = new Color(205, 255, 250);
			break;
		}
		heatAmount = 5;
	}
}
