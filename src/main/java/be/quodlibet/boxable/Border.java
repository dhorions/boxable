package be.quodlibet.boxable;

import java.awt.Color;

public class Border {

	private Color color;
	
	private float width;

	public Border(Color color, float width) {
		this.color = color;
		this.width = width;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	
}
