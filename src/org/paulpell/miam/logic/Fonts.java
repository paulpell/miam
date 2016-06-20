package org.paulpell.miam.logic;

import java.awt.Font;
import java.awt.Graphics;

public class Fonts
{

	public static Font normalFont_;
	public static Font medFont_;
	public static Font bigFont_;

	public static void setupFonts(Graphics g)
	{

		if (normalFont_ == null
				|| bigFont_ == null)
		{
			Font font = g.getFont();
			normalFont_ = font;
			medFont_ = font.deriveFont(font.getSize2D() * 2);
			bigFont_ = font.deriveFont(font.getSize2D() * 4);
		}
	}

}
