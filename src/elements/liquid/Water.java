package elements.liquid;

import java.awt.Color;

import elements.Element;

public class Water extends Element {

	public Water() {
		id = 2;
		displayName = "Water";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(9, 195, 219);
			break;
		case 1:
			color = new Color(8, 190, 219);
			break;
		case 2:
			color = new Color(9, 198, 218);
			break;
		}
		heatAmount = 5;
	}
}
