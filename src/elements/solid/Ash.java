package elements.solid;

import java.awt.Color;

import elements.Element;

public class Ash extends Element {
	public Ash() {
		id = 19;
		displayName = "Ash";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(178, 180, 181);
			break;
		case 1:
			color = new Color(170, 190, 181);
			break;
		case 2:
			color = new Color(178, 190, 175);
			break;
		}
		heatAmount = 50;
	}
}
