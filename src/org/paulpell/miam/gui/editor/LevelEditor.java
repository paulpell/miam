package org.paulpell.miam.gui.editor;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.paulpell.miam.geom.Circle;
import org.paulpell.miam.geom.GeomObjectEnum;
import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Line;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.gui.DrawPanel;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.draw.walls.WallElement;


@SuppressWarnings("serial")
public class LevelEditor extends JFrame
{

	DrawPanel drawPanel_;
	DrawPanelMouseHandler mouseHandler_;
	ToolsPanel toolsPanel_;
	

	int width_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_WIDTH;
	int	height_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_HEIGHT;
	
	BufferedImage image_;
	Color defaultElementColor_ = Color.blue;
	
	GeomObjectEnum nextObject_ = GeomObjectEnum.NONE;
	GeometricObject shape_ = null;
	Pointd firstPoint_ = null;
	Pointd mousePoint_ = null;
	
	Wall wall_ = new Wall(width_, height_);
	
	public LevelEditor() {
		super("Snakesss - Level editor");

		
		// the stuff to draw the level
		image_ = new BufferedImage(width_, height_, BufferedImage.TYPE_INT_ARGB);
		
		
		drawPanel_ = new DrawPanel();
		drawPanel_.setPreferredSize(new Dimension(width_, height_));
		drawPanel_.setImage(image_);
		add(drawPanel_, BorderLayout.WEST);
		drawImage();
		
		mouseHandler_ = new DrawPanelMouseHandler(this);
		drawPanel_.addMouseListener(mouseHandler_);
		drawPanel_.addMouseMotionListener(mouseHandler_);
		
		
		// and the available tools
		
		toolsPanel_ = new ToolsPanel(this);
		add(toolsPanel_, BorderLayout.EAST);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void cancelLast()
	{
		wall_.removeLastElement();
		drawImage();
	}
	public void cancelCurrent()
	{
		firstPoint_ = null;
		shape_ = null;
		drawImage();
	}
	
	
	
	private void drawImage()
	{
		Graphics imGr = image_.createGraphics();
		imGr.setColor(Color.black);
		imGr.fillRect(0, 0, width_, height_);
		
		
		wall_.draw(imGr);
		
		
		// if the user is drawing a shape_, we dynamically do it
		if (firstPoint_ != null) {
			double leastX = firstPoint_.x_ < mousePoint_.x_ ? firstPoint_.x_ : mousePoint_.x_;
			double leastY = firstPoint_.y_ < mousePoint_.y_ ? firstPoint_.y_ : mousePoint_.y_;
			double dx = firstPoint_.x_ - mousePoint_.x_;
			double dy = firstPoint_.y_ - mousePoint_.y_;
			if (dx < 0) dx = -dx;
			if (dy < 0) dy = -dy;
			
			
			System.out.println("shape_ = " + nextObject_);
			
			switch (nextObject_) {
			case LINE:
				shape_ =  new Line(firstPoint_, mousePoint_);
				break;
			case RECTANGLE:
				shape_ = new Rectangle(leastX, leastY, (int)dx, (int)dy);
				break;
			case CIRCLE:
				double rad = (dx > dy ? dx : dy) / 2.;
				Pointd center = new Pointd(leastX + rad, leastY + rad);
				shape_ = new Circle(center, rad);
			default:
				return; // nullpointerexception
			}
			
			imGr.setColor(defaultElementColor_);
			shape_.draw(imGr);
		}
		drawPanel_.setImage(image_);
		drawPanel_.repaint();
	}
	
	// called when one and only one point is already fixed
	public void mouseMoved(Pointd p)
	{
		mousePoint_ = p;
		drawImage();
	}
	
	/*
	 * The user has to place stuff using the mouse, this is how we add a clicked point
	 */
	public void nextPoint(Pointd p) {
		
		
		if (firstPoint_ == null) {
			firstPoint_ = p;
			mouseHandler_.reportMouseMotion(true);
		}
		else {
			mouseHandler_.reportMouseMotion(false);
			firstPoint_ = null;
			wall_.addElement(new WallElement(shape_, defaultElementColor_));
		}
	}
	
	public void addLine() {
		firstPoint_ = null;
		nextObject_ = GeomObjectEnum.LINE;
	}
	
	public void addRect() {
		firstPoint_ = null;
		nextObject_ = GeomObjectEnum.RECTANGLE;
	}
	
	public void addCircle() {
		firstPoint_ = null;
		nextObject_ = GeomObjectEnum.CIRCLE;
	}
}
