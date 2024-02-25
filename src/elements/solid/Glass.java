package elements.solid;

import java.awt.Color;

import elements.Element;

public class Glass extends Element {

	public Glass() {
		id = 12;
		displayName = "Glass";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(199, 220, 225);
			break;
		case 1:
			color = new Color(199, 227, 229);
			break;
		case 2:
			color = new Color(191, 227, 228);
			break;
		}
		heatAmount = 1;
	}
}
