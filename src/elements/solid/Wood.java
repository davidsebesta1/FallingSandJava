package elements.solid;

import java.awt.Color;

import elements.Element;

public class Wood extends Element {
	public Wood() {
		id = 9;
		displayName = "Wood";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(133, 94, 66);
			break;
		case 1:
			color = new Color(130, 94, 66);
			break;
		case 2:
			color = new Color(133, 94, 66);
			break;
		}
		heatAmount = 5;
	}
}
