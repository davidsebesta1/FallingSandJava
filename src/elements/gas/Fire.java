package elements.gas;

import java.awt.Color;

import elements.Element;

public class Fire extends Element {

	public Fire() {
		id = 10;
		color = Color.red;
		heatAmount = random.nextInt(5) + 400;
		displayName = "Fire";

		int rnd = random.nextInt(3);
		maxMoveAmount = (short) (10 + random.nextInt(25));
		switch (rnd) {
		case 0:
			color = new Color(226, 72, 34);
			break;
		case 1:
			color = new Color(226, 120, 34);
			break;
		case 2:
			color = new Color(235, 161, 102);
			break;
		}
	}
}
