package org.paulpell.miam.logic.levels;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.paulpell.miam.geom.Arrow;
import org.paulpell.miam.geom.Circle;
import org.paulpell.miam.geom.EditorArrow;
import org.paulpell.miam.geom.EditorDisplayElement;
import org.paulpell.miam.geom.EditorItem;
import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.gui.MessagePainter;
import org.paulpell.miam.gui.editor.LevelEditorFrame;
import org.paulpell.miam.gui.editor.tools.EditorToolsEnum;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.items.Banana;
import org.paulpell.miam.logic.draw.items.BananaSpecial;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.Lightning;
import org.paulpell.miam.logic.draw.items.ReversingItem;
import org.paulpell.miam.logic.draw.items.ScoreItem;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.draw.walls.WallElement;
import org.paulpell.miam.logic.levels.undo.UndoManager;
import org.paulpell.miam.logic.levels.undo.UndoableAction;
import org.paulpell.miam.logic.levels.undo.UndoableAddItem;
import org.paulpell.miam.logic.levels.undo.UndoableDisplayElement;
import org.paulpell.miam.logic.levels.undo.UndoableMove;

public class LevelEditorControl
{
	Control control_;
	LevelEditorFrame levelEditor_;

	MessagePainter msgPainter_;
	

	int width_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_WIDTH;
	int	height_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_HEIGHT;
	
	BufferedImage image_;
	static final Color defaultElementColor_ = Color.blue;
	static final Color selectedPointColor_ = Color.red;
	static public final Color selectedItemColor_ = Color.red;
	
	EditorToolsEnum tool_ = EditorToolsEnum.HAND;
	GeometricObject shape_ = null;
	Pointd firstPoint_ = null;
	Pointd mousePoint_ = null;
	
	Vector <EditorDisplayElement> displayElements_;
	Vector <EditorArrow> startPositions_ = null;
	Vector <EditorItem> items_ = null;
	
	EditorDisplayElement selectedElement_ = null;
	
	final LevelFileManager levelFileManager_;
	
	UndoManager undoManager_;
	
	Level level_= null ;
	
	
	public LevelEditorControl(Control control)
	{
		control_ = control;
		
		levelEditor_ = new LevelEditorFrame(this);
		
		// handles read and write to and from files
		levelFileManager_ = new LevelFileManager(this);
		
		setLevel(new Level()); // should come late, to avoid null pointers =)
	}
	
	
	private void reset()
	{
		undoManager_ = new UndoManager();

		msgPainter_ = new MessagePainter(15, height_ - 15, -15);
		
		image_ = new BufferedImage(width_, height_, BufferedImage.TYPE_INT_ARGB);
		
		shape_ = null;
		firstPoint_ = null;
		mousePoint_ = null;
		

		
		displayElements_ = new Vector <EditorDisplayElement> ();
		startPositions_ = new Vector <EditorArrow> ();
		items_ = new Vector <EditorItem> ();
		
		selectedElement_ = null;
	}
	
	
	public Frame getFrame()
	{
		return levelEditor_;
	}
	
	public int getWidth()
	{
		return width_;
	}
	public int getHeight()
	{
		return height_;
	}

	public void playEditedLevel()
	{
		if (null == control_)
		{
			JOptionPane.showMessageDialog(null, "Editor only - impossible to try");
			return;
		}
		updateLevel();
		control_.playEditedGame(level_);
	}
	

	
	public void setLevel(Level l)
	{
		if (null == l)
			return;

		
		reset();
		
		
		level_ = l;
		
		displayElements_ = new Vector <EditorDisplayElement> ();
		
		initialiseStartPositions();
		
		items_ = new Vector <EditorItem> ();
		Vector <Item> its = level_.getInitialItems();
		for (Item i : its)
			items_.add(new EditorItem(selectedItemColor_, i));
		
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

		// TODO: encode in level? 
		undoManager_ = new UndoManager();
		
		drawImage();
	}
	

	
	public void drawImage()
	{
		Graphics2D imGr = image_.createGraphics();
		imGr.setColor(Color.black);
		imGr.fillRect(0, 0, width_, height_);

		
		for (EditorDisplayElement ede : displayElements_)
			ede.draw(imGr);
		
		for (EditorArrow ea : startPositions_)
			ea.draw(imGr);
		
		for (EditorItem i: items_)
			i.draw(imGr);
		
		// if the user is drawing a shape, we dynamically do it
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
				shape_ = new Rectangle(biggestX, biggestY, (int)dx, (int)dy, false);
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
		
		msgPainter_.paintMessages(imGr);
		
		levelEditor_.setImage(image_);
	}
	
	
	private void initialiseStartPositions()
	{
		startPositions_ = new Vector <EditorArrow> ();
		
		for (int i=0; i<level_.getMaxNumberSnakes(); ++i)
		{
			int a = level_.getSnakeStartAngle(i);
			Pointd p = level_.getSnakeStartPosition(i);
			Arrow arrow = new Arrow(p, Arith.deg2rad(a), 20);
			Color c = Snake.s_snakesColors[i];
			EditorArrow pos = new EditorArrow(arrow, c);
			startPositions_.add(pos);
		}
	}
	

	
	public void mouseMoved(Pointd p)
	{
		levelEditor_.setPositionText("(" + (int)p.x_ + "," + (int)p.y_ + ")");
		if (tool_ == EditorToolsEnum.HAND)
		{
			if (null != selectedElement_)
				selectedElement_.move(p);
		}
		else if (null != firstPoint_)
			mousePoint_ = p;

		drawImage();
	}
	

	/*
	 * The user has to place stuff using the mouse,
	 * this function is where we handle a clicked point
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
				addDisplayElementAction();
			break;
			
		case HAND:
			handleHandClick(p);
			break;
			
		case SCORE:
			addItemAction(new ScoreItem(p.x_, p.y_));
			break;
		case BANANA:
			addItemAction(new Banana(p.x_, p.y_));
			break;
		case BANANA_SP:
			addItemAction(new BananaSpecial(p.x_, p.y_));
			break;
		case LIGHTNING:
			addItemAction(new Lightning(p.x_, p.y_));
			break;
		case REVERSE:
			addItemAction(new ReversingItem(p.x_, p.y_));
			break;
			
			
		default:
			throw new UnsupportedOperationException("Unknown tool type");
		}
	}
	private void addDisplayElementAction()
	{
		firstPoint_ = null;
		EditorDisplayElement ede = EditorDisplayElement.createElement(shape_, defaultElementColor_, true);
		undoManager_.actionTaken(new UndoableDisplayElement(ede, displayElements_));
	}
	
	private void addItemAction(Item i)
	{
		EditorItem ei = new EditorItem(selectedItemColor_, i);
		undoManager_.actionTaken(new UndoableAddItem(ei, items_));
	}
	
	private void handleHandClick(Pointd where)
	{
		if ( null != selectedElement_ )
		{
			unselectElement();
			drawImage();
			return;
		}
		
		
		// find the closest element in the wall elements, items and start positions
		Vector <EditorDisplayElement> tocheck = new Vector <EditorDisplayElement> ();
		tocheck.addAll(displayElements_);
		tocheck.addAll(startPositions_);
		tocheck.addAll(items_);
		EditorDisplayElement selected = null;
		double dist = Double.MAX_VALUE;
		for (EditorDisplayElement ede : tocheck)
		{
			double d = ede.getGeometricObject().minDistanceToPoint(where);
			if (d < dist)
			{
				dist = d;
				selected = ede;
			}
		}
		
		if (null != selected)
		{
			selectElement(selected, where);
			drawImage();
		}
	}
	
	private void selectElement(EditorDisplayElement selected, Pointd where)
	{
		if (selected != null)
		{
			selectedElement_ = selected.clone();
			selectedElement_.select(where);
			UndoableAction a;
			if ( selected instanceof EditorArrow )
				a = new UndoableMove(selectedElement_, selected, startPositions_);
			else if ( selected instanceof EditorItem )
				a = new UndoableMove(selectedElement_, selected, items_);
			else
				a = new UndoableMove(selectedElement_, selected, displayElements_);
			undoManager_.actionTaken(a);
		}
	}
	private void unselectElement()
	{
		if (null != selectedElement_)
		{
			selectedElement_.unselect();
			selectedElement_ = null;
		}
	}

	public void setTool(EditorToolsEnum tool)
	{
		firstPoint_ = null;
		tool_ = tool;
		if (tool_ != EditorToolsEnum.HAND)
			unselectElement();
		drawImage();
	}

	public void cancelCurrent()
	{
		firstPoint_ = null;
		shape_ = null;
		drawImage();
	}
	
	public void deleteSelected()
	{
		if (selectedElement_ != null)
		{
			displayElements_.remove(selectedElement_);
			selectedElement_ = null;
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
	
	public void onNew()
	{
		setLevel(new Level());
	}

	public void onOpen()
	{
		try
		{
			Level l = levelFileManager_.openLevel();
			setLevel(l);
			levelEditor_.requestFocus();
		}
		catch (Exception e)
		{
			levelEditor_.displayMessage("Cannot open level: " + e.getMessage());
			Log.logErr("Cannot open level!");
			Log.logException(e);
		}
	}
	
	public void onSave()
	{
		updateLevel();
		levelFileManager_.saveLevelToCurrentFile(level_);
		levelEditor_.requestFocus();
	}
	
	public void onSaveAs() 
	{
		updateLevel();
		levelFileManager_.saveLevelToNewFile(level_);
		levelEditor_.requestFocus();
	}
	
	public void onUndo()
	{
		undoManager_.undo();
		drawImage();
	}
	
	public void onRedo()
	{
		undoManager_.redo();
		drawImage();
	}
	

	private void updateLevel()
	{
		level_.setWall(makeWall());
		for (int i=0; i<startPositions_.size(); ++i)
		{
			int a = Arith.rad2deg(startPositions_.get(i).getAngle());
			level_.setSnakeStartAngle(i, a);
			Pointd p = startPositions_.get(i).getPoint();
			level_.setSnakeStartPosition(i, p);
		}
		Vector <Item> its = new Vector <Item> ();
		for (EditorItem i : items_)
			its.add(i.getItem());
		level_.setInitialItems(its);
	}
	
	public void focus()
	{
		levelEditor_.toFront();
		levelEditor_.requestFocus();
	}
	
	public void displayMessage(String msg)
	{
		msgPainter_.addMessage(msg);
	}
	
	public void setVisible(boolean b)
	{
		levelEditor_.setVisible(b);
	}
	
	public boolean isVisible()
	{
		return levelEditor_.isVisible();
	}
	
	public KeyListener getKeyListener()
	{
		return levelEditor_;
	}

}