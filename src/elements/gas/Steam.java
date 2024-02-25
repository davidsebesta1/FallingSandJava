package elements.gas;

import java.awt.Color;

import elements.Element;

public class Steam extends Element {

	public Steam() {
		id = 4;
		color = Color.lightGray;
		heatAmount = random.nextInt(50) + 250;
		displayName = "Steam";
	}
}
