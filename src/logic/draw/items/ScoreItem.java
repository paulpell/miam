package logic.draw.items;
import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;
import java.awt.Color;

import javax.swing.ImageIcon;

import logic.Game;
import logic.draw.snakes.Snake;



public class ScoreItem extends Item {
	
	// snake-related
	int score, growth;
	
	public ScoreItem(double x0, double y0) {
		score = 1;
		growth = 20;
		shape = new Rectangle(x0, y0, 10, 10);
		position = new Pointd(x0, y0);
	}

	
	public void draw(Graphics g) {
		g.setColor(new Color(10, 180, 50));
		shape.draw(g);
	}

	@Override
	public boolean effectStep(Snake s) {
		return true; // immediately finished
	}
	public void startEffect(Snake s) {
		s.addScore(score);
		s.growBy(growth);
	}
	
	@Override
	public Object clone(Game g) {
		return new ScoreItem(position.x, position.y);
	}
	@Override
	public ImageIcon getImageIcon() {
		return null;
	}
	
	public String getTextDescription() {
		return "Score";
	}
	
}
