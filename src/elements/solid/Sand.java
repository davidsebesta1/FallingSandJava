package elements.solid;

import java.awt.Color;

import elements.Element;

public class Sand extends Element {

	public Sand() {
		id = 1;
		maxMoveAmount = 5;
		displayName = "Sand";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(246, 228, 173);
			break;
		case 1:
			color = new Color(250, 242, 195);
			break;
		case 2:
			color = new Color(255, 255, 227);
			break;
		}
	}
}
