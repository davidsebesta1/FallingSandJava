package elements.liquid;

import java.awt.Color;

import elements.Element;

public class Lava extends Element {

	public Lava() {
		id = 5;
		heatAmount = random.nextInt(100) + 2300;
		displayName = "Lava";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(255, 92, 0);
			break;
		case 1:
			color = new Color(217, 81, 49);
			break;
		case 2:
			color = new Color(250, 103, 52);
			break;
		}
	}
}
