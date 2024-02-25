package elements.solid;

import java.awt.Color;

import elements.Element;

public class Stone extends Element {

	public Stone() {
		id = 3;
		displayName = "Stone";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(142, 148, 148);
			break;
		case 1:
			color = new Color(152, 160, 167);
			break;
		case 2:
			color = new Color(152, 160, 167);
			break;
		}
		heatAmount = 5;
	}
}
