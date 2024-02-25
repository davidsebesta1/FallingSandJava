package elements.solid;

import java.awt.Color;

import elements.Element;

public class Grass extends Element {

	public Grass() {
		id = 13;
		displayName = "Grass";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(0, 154, 18);
			break;
		case 1:
			color = new Color(0, 158, 18);
			break;
		case 2:
			color = new Color(0, 146, 19);
			break;
		}
	}
}
