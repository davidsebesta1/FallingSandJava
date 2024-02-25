package elements.gas;

import java.awt.Color;

import elements.Element;

public class Smoke extends Element {

	public Smoke() {
		id = 14;
		color = Color.gray;
		heatAmount = random.nextInt(300) + 100;

		int rnd = random.nextInt(3);
		maxMoveAmount = (short) (10 + random.nextInt(25));
		switch (rnd) {
		case 0:
			color = new Color(132, 136, 120);
			break;
		case 1:
			color = new Color(120, 136, 132);
			break;
		case 2:
			color = new Color(132, 145, 132);
			break;
		}
	}
}
