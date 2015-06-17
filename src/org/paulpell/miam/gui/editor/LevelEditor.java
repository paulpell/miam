package org.paulpell.miam.gui.editor;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JFrame;

import org.paulpell.miam.geom.Arrow;
import org.paulpell.miam.geom.Circle;
import org.paulpell.miam.geom.EditorArrow;
import org.paulpell.miam.geom.EditorDisplayElement;
import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.gui.DrawImagePanel;
import org.paulpell.miam.gui.editor.tools.EditorToolsEnum;
import org.paulpell.miam.gui.editor.tools.EditorToolsPanel;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.draw.walls.WallElement;
import org.paulpell.miam.logic.levels.Level;


@SuppressWarnings("serial")
public class LevelEditor
	extends JFrame
	implements KeyListener
{
	
	//Control control_;

	final DrawImagePanel drawPanel_;
	final DrawPanelMouseHandler mouseHandler_;
	final EditorToolsPanel toolsPanel_;
	

	int width_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_WIDTH;
	int	height_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_HEIGHT;
	
	BufferedImage image_;
	static final Color defaultElementColor_ = Color.blue;
	static final Color selectedPointColor_ = Color.red;
	
	EditorToolsEnum tool_ = EditorToolsEnum.NONE;
	GeometricObject shape_ = null;
	Pointd firstPoint_ = null;
	Pointd mousePoint_ = null;
	
	Vector <EditorDisplayElement> displayElements_;
	
	EditorDisplayElement selectedElement_;
	
	final LevelFileManager levelFileManager_;
	
	Level level_;
	
	public LevelEditor(Control control)
	{
		super("Snakesss - Level editor");
		
		//control_ = control;
		
		
		level_ = new Level();
		
		// handles read and write to and from files
		levelFileManager_ = new LevelFileManager(this);

		
		// menu: open, save, save as
		LevelEditorMenuBar menu = new LevelEditorMenuBar(this);
		setJMenuBar(menu);
		
		

		// draw stuff
		
		displayElements_ = new Vector<EditorDisplayElement> ();
		
		image_ = new BufferedImage(width_, height_, BufferedImage.TYPE_INT_ARGB);

		
		drawPanel_ = new DrawImagePanel();
		drawPanel_.setPreferredSize(new Dimension(width_, height_));
		drawPanel_.setImage(image_);
		add(drawPanel_, BorderLayout.WEST);
		drawImage();
		
		
		// mouse move, mouse click
		mouseHandler_ = new DrawPanelMouseHandler(this);
		drawPanel_.addMouseListener(mouseHandler_);
		drawPanel_.addMouseMotionListener(mouseHandler_);
		
		
		// and the available tools
		
		toolsPanel_ = new EditorToolsPanel(this);
		add(toolsPanel_, BorderLayout.EAST);
		
		pack();
		setLocationRelativeTo(null);
		//setVisible(true);
		

		setLevel(level_); // should come late, to avoid null pointers =)
	}
	
	public void cancelCurrent()
	{
		firstPoint_ = null;
		shape_ = null;
		drawImage();
	}
	
	private void deleteSelected()
	{
		if (selectedElement_ != null)
			displayElements_.remove(selectedElement_);
		drawImage();
	}
	
	
	private void drawImage()
	{
		Graphics2D imGr = image_.createGraphics();
		imGr.setColor(Color.black);
		imGr.fillRect(0, 0, width_, height_);

		
		for (EditorDisplayElement ede : displayElements_)
			ede.draw(imGr);
		
		// if the user is drawing a shape_, we dynamically do it
		if (firstPoint_ != null)
		{
			double biggestX = Arith.maxd(mousePoint_.x_, firstPoint_.x_);
			double biggestY = Arith.maxd(mousePoint_.y_, firstPoint_.y_);
			double dx = Arith.absd(firstPoint_.x_ - mousePoint_.x_);
			double dy = Arith.absd(firstPoint_.y_ - mousePoint_.y_);
			
			switch (tool_)
			{
			case LINE:
				shape_ =  new Segment(firstPoint_, mousePoint_);
				break;
			case RECTANGLE:
				shape_ = new Rectangle(biggestX, biggestY, (int)dx, (int)dy);
				break;
			case CIRCLE:
				double rad = Arith.maxd(dx, dy) / 2.;
				Pointd center = new Pointd(biggestX + rad, biggestY + rad);
				shape_ = new Circle(center, rad);
				break;
			default:
				shape_ = null;
			}
			
			if (null != shape_)
			{
				imGr.setColor(defaultElementColor_);
				shape_.draw(imGr);
			}
		}
		
		if (null != selectedElement_)
		{
			Color c = selectedElement_.getColor();
			Color c2 = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
			imGr.setColor(c2);
			selectedElement_.getGeometricObject().draw(imGr);
			
			Pointd p = selectedElement_.getSelectedPoint();
			imGr.setColor(selectedPointColor_);
			imGr.drawRect((int)p.x_, (int)p.y_, 1, 1);
		}
		
		drawPanel_.setImage(image_);
		drawPanel_.repaint();
		
	}
	
	public void mouseMoved(Pointd p)
	{
		toolsPanel_.setPosition("(" + (int)p.x_ + "," + (int)p.y_ + ")");
		if (tool_ == EditorToolsEnum.HAND)
		{
			if (null != selectedElement_)
				selectedElement_.move(p);
		}
		else if (null != firstPoint_)
		{
			mousePoint_ = p;
		}
		drawImage();
	}
	
	/*
	 * The user has to place stuff using the mouse, this is how we add a clicked point
	 */
	public void clickedPoint(Pointd p)
	{
		if (null == tool_)
			return;
		
		switch (tool_)
		{
		case LINE:
		case RECTANGLE:
		case CIRCLE:
			if (firstPoint_ == null)
				firstPoint_ = p;
			else
			{
				firstPoint_ = null;
				EditorDisplayElement ede = EditorDisplayElement.createElement(shape_, defaultElementColor_, true);
				displayElements_.add(ede);
			}
			break;
			
		case HAND:
			handleHandClick(p);
			break;
			
		case SCORE:
		
		case NONE:
			break;
			
			
		default:
			throw new UnsupportedOperationException("Unknown tool type");
		}
	}
	
	private void handleHandClick(Pointd where)
	{
		// first find the closest element
		EditorDisplayElement selected = null;
		double dist = Double.MAX_VALUE;
		for (EditorDisplayElement ede : displayElements_)
		{
			double d = ede.getGeometricObject().minDistanceToPoint(where);
			if (d < dist)
			{
				dist = d;
				selected = ede;
			}
		}
		
		if (null == selected)
			return;
		
		// if no selection, select the one
		if (null == selectedElement_)
			selectElement(selected, where);
		else
		{
			if (selected == selectedElement_)
				selectedElement_ = null;
			else
				selectElement(selected, where);
		}
		drawImage();
	}
	
	private void selectElement(EditorDisplayElement selected, Pointd where)
	{
		selectedElement_ = selected;
		selectedElement_.select(where);
	}

	public void setTool(EditorToolsEnum tool)
	{
		firstPoint_ = null;
		tool_ = tool;
		if (tool_ != EditorToolsEnum.HAND)
			selectedElement_ = null;
		drawImage();
	}
	
	private void setLevel(Level l)
	{
		level_ = l;
		
		displayElements_ = new Vector <EditorDisplayElement> ();
		for (int i=0; i<level_.getMaxNumberSnakes(); ++i)
		{
			double a = level_.getSnakeStartAngle(i) * Math.PI / 180.;
			Pointd p = level_.getSnakeStartPosition(i);
			Arrow arrow = new Arrow(p, a);
			Color c = Snake.s_snakesColors[i];
			EditorDisplayElement pos = new EditorArrow(arrow, c);
			displayElements_.add(pos);
		}
		
		Wall w = level_.getWall();
		if (null != w)
		{
			for (WallElement we : w.getElements())
			{
				GeometricObject obj = we.getGeometricObject();
				Color c = we.getColor();
				EditorDisplayElement ede = EditorDisplayElement.createElement(obj, c, true);
				displayElements_.add(ede);
			}
		}
		
		drawImage();
	}
	
	private Wall makeWall()
	{
		Wall w = new Wall(width_, height_);
		for (EditorDisplayElement ede: displayElements_)
		{
			if (ede.isWallElement_)
			{
				GeometricObject obj = ede.getGeometricObject();
				Color c = ede.getColor();
				WallElement we = new WallElement(obj, c);
				w.pushElement(we);
			}
		}
		return w;
	}
	
	public void onOpen()
	{
		setLevel(levelFileManager_.openLevel());
		requestFocus();
	}

	public void onSave()
	{
		level_.setWall(makeWall());
		levelFileManager_.saveLevelToCurrentFile(level_);
		requestFocus();
	}
	
	public void onSaveAs() 
	{
		level_.setWall(makeWall());
		levelFileManager_.saveLevelToNewFile(level_);
		requestFocus();
	}
	
	public void displayMessage(String msg)
	{
		// TODO display message in editor!
	}
	
	public void keyPressed(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_BACK_SPACE:
			deleteSelected();
			break;
		}
		
	}

	public void keyReleased(KeyEvent arg0)
	{}

	public void keyTyped(KeyEvent arg0)
	{
		
	}
}
