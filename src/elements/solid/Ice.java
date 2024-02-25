package elements.solid;

import java.awt.Color;

import elements.Element;

public class Ice extends Element {

	public Ice() {
		id = 8;
		heatAmount = random.nextInt(10) - 50;
		displayName = "Ice";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(219, 241, 253);
			break;
		case 1:
			color = new Color(210, 241, 255);
			break;
		case 2:
			color = new Color(218, 245, 250);
			break;
		}
	}
}
