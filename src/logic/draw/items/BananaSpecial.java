package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import logic.Game;
import logic.draw.snakes.Snake;

public class BananaSpecial extends SpecialItem {
	
	
	private static ImageIcon image;
	static {
		image = new ImageIcon("images/BananaSpecial.png");
	}
	private int width = image.getIconWidth(), height = image.getIconHeight();
	
	public BananaSpecial(double x0, double y0, Game g) {
		if (image == null) {
			throw new UnsupportedOperationException("Image BananaSpecial.png could not be loaded!");
		}
		game = g;
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
	}
	
	
	@Override
	public void activate(Snake s) {
		// TODO Auto-generated method stub
		Pointd pos = s.getTail();
		Banana b = new Banana(pos.x, pos.y);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, (int)width, (int)height, null);
	}

}
