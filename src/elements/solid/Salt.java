package elements.solid;

import java.awt.Color;

import elements.Element;

public class Salt extends Element {

	public Salt() {
		id = 16;
		displayName = "Salt";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(241, 230, 222);
			break;
		case 1:
			color = new Color(241, 225, 218);
			break;
		case 2:
			color = new Color(250, 230, 220);
			break;
		}
	}
}
