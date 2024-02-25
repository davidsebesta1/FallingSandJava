package elements.solid;

import java.awt.Color;

import elements.Element;

public class Bedrock extends Element {

	public Bedrock() {
		id = 20;
		displayName = "Bedrock";

		int rnd = random.nextInt(3);
		switch (rnd) {
		case 0:
			color = new Color(52, 52, 52);
			break;
		case 1:
			color = new Color(85, 85, 85);
			break;
		case 2:
			color = new Color(34, 34, 34);
			break;
		}
		heatAmount = 0;
	}
}
