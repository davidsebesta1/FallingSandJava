package elements.liquid;

import java.awt.Color;

import elements.Element;

public class LiquidNitrogen extends Element {

	public LiquidNitrogen() {
		id = 7;
		heatAmount = random.nextInt(100) - 400;
		displayName = "Liquid Nitrogen";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(23, 64, 146);
			break;
		case 1:
			color = new Color(25, 65, 140);
			break;
		case 2:
			color = new Color(24, 64, 149);
			break;
		}
	}
}
