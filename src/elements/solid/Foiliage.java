package elements.solid;

import java.awt.Color;

import elements.Element;

public class Foiliage extends Element {

	public Foiliage() {
		id = 18;
		displayName = "Foiliage";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(0, 148, 15);
			break;
		case 1:
			color = new Color(0, 148, 10);
			break;
		case 2:
			color = new Color(0, 148, 14);
			break;
		}
	}
}
