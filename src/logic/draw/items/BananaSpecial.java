package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import logic.Constants;
import logic.Game;
import logic.Globals;
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
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
	}
	
	
	@Override
	public boolean activate(Snake s) {
		Pointd pos = s.getTail();
		Globals.currentGame.addItem(new Banana(pos.x, pos.y));
		return true; // the object is usable only once
	}

	@Override
	public Object clone(Game g) {
		double 	x 	= Math.random() * Constants.IMAGE_WIDTH,
				y 	= Math.random() * Constants.IMAGE_HEIGHT;
		return new BananaSpecial(x, y, g);
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, (int)width, (int)height, null);
	}

	@Override
	public ImageIcon getImageIcon() {
		return image;
	}
	
	public String getTextDescription() {
		return "Banana (carry it)";
	}
}
