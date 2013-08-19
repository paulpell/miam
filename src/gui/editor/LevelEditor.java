package gui.editor;

import geom.Circle;
import geom.GeomObjectEnum;
import geom.GeometricObject;
import geom.Line;
import geom.Pointd;
import geom.Rectangle;
import gui.DrawPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JFrame;

import logic.draw.walls.Wall;
import logic.draw.walls.WallElement;

@SuppressWarnings("serial")
public class LevelEditor extends JFrame {

	DrawPanel drawPanel;
	DrawPanelMouseHandler mouseHandler;
	ToolsPanel toolsPanel;
	

	int width = logic.Constants.IMAGE_WIDTH,
			height = logic.Constants.IMAGE_HEIGHT;
	
	BufferedImage image;
	Color defaultElementColor = Color.blue;
	
	GeomObjectEnum nextObject = GeomObjectEnum.NONE;
	GeometricObject shape = null;
	Pointd firstPoint = null, mousePoint = null;
	
	Wall wall = new Wall();
	
	public LevelEditor() {
		super("Snakesss - Level editor");

		
		// the stuff to draw the level
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		
		drawPanel = new DrawPanel();
		drawPanel.setPreferredSize(new Dimension(width, height));
		drawPanel.setImage(image);
		add(drawPanel, BorderLayout.WEST);
		drawImage();
		
		mouseHandler = new DrawPanelMouseHandler(this);
		drawPanel.addMouseListener(mouseHandler);
		drawPanel.addMouseMotionListener(mouseHandler);
		
		
		// and the available tools
		
		toolsPanel = new ToolsPanel(this);
		add(toolsPanel, BorderLayout.EAST);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void cancelLast() {
		wall.removeLastElement();
		drawImage();
	}
	public void cancelCurrent() {
		firstPoint = null;
		shape = null;
		drawImage();
	}
	
	
	
	private void drawImage() {
		Graphics imGr = image.createGraphics();
		imGr.setColor(Color.black);
		imGr.fillRect(0, 0, width, height);
		
		
		wall.draw(imGr);
		
		
		// if the user is drawing a shape, we dynamically do it
		if (firstPoint != null) {
			double leastX = firstPoint.x < mousePoint.x ? firstPoint.x : mousePoint.x;
			double leastY = firstPoint.y < mousePoint.y ? firstPoint.y : mousePoint.y;
			double dx = firstPoint.x - mousePoint.x;
			double dy = firstPoint.y - mousePoint.y;
			if (dx < 0) dx = -dx;
			if (dy < 0) dy = -dy;
			
			
			System.out.println("shape = " + nextObject);
			
			switch (nextObject) {
			case LINE:
				shape =  new Line(firstPoint, mousePoint);
				break;
			case RECTANGLE:
				shape = new Rectangle(leastX, leastY, (int)dx, (int)dy);
				break;
			case CIRCLE:
				double rad = (dx > dy ? dx : dy) / 2.;
				Pointd center = new Pointd(leastX + rad, leastY + rad);
				shape = new Circle(center, rad);
			default:
				return; // nullpointerexception
			}
			
			imGr.setColor(defaultElementColor);
			shape.draw(imGr);
		}
		drawPanel.setImage(image);
		drawPanel.repaint();
	}
	
	// called when one and only one point is already fixed
	public void mouseMoved(Pointd p) {
		mousePoint = p;
		drawImage();
	}
	
	/*
	 * The user has to place stuff using the mouse, this is how we add a clicked point
	 */
	public void nextPoint(Pointd p) {
		
		
		if (firstPoint == null) {
			firstPoint = p;
			mouseHandler.reportMouseMotion(true);
		}
		else {
			mouseHandler.reportMouseMotion(false);
			firstPoint = null;
			wall.addElement(new WallElement(shape, defaultElementColor));
		}
	}
	
	public void addLine() {
		firstPoint = null;
		nextObject = GeomObjectEnum.LINE;
	}
	
	public void addRect() {
		firstPoint = null;
		nextObject = GeomObjectEnum.RECTANGLE;
	}
	
	public void addCircle() {
		firstPoint = null;
		nextObject = GeomObjectEnum.CIRCLE;
	}
}

class DrawPanelMouseHandler implements MouseListener, MouseMotionListener {
	
	LevelEditor levelEditor;
	boolean reportMouseMotion = false; // to report the mouse motion when one point is fixed already
	
	public DrawPanelMouseHandler(LevelEditor le) {
		levelEditor = le;
	}
	
	public void reportMouseMotion(boolean b) {
		reportMouseMotion = b;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Point p = arg0.getPoint();
		levelEditor.nextPoint(new Pointd(p.x, p.y));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {
		levelEditor.cancelCurrent();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (reportMouseMotion) {
			Point p = arg0.getPoint();
			levelEditor.mouseMoved(new Pointd(p.x, p.y));
		}
	}
}
