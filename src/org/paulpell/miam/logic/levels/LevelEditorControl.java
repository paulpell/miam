package org.paulpell.miam.logic.levels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JFrame;
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
import org.paulpell.miam.gui.GlobalColorTable;
import org.paulpell.miam.gui.MessagePainter;
import org.paulpell.miam.gui.editor.LevelEditorPanel;
import org.paulpell.miam.gui.editor.tools.EditorDrawToolsEnum;
import org.paulpell.miam.gui.editor.tools.EditorItemsToolsEnum;
import org.paulpell.miam.gui.editor.tools.IEditorTool;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.draw.walls.WallElement;
import org.paulpell.miam.logic.levels.undo.UndoManager;
import org.paulpell.miam.logic.levels.undo.UndoableAction;
import org.paulpell.miam.logic.levels.undo.UndoableAddElement;
import org.paulpell.miam.logic.levels.undo.UndoableMove;

public class LevelEditorControl
{
	Control control_;
	LevelEditorPanel levelEditor_;
	JFrame displayFrame_;

	MessagePainter msgPainter_;
	

	int width_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_WIDTH;
	int	height_ = org.paulpell.miam.logic.Constants.DEFAULT_IMAGE_HEIGHT;
	
	BufferedImage image_;
	static final Color defaultElementColor_ = Color.blue;
	static final Color selectedPointColor_ = Color.red;
	static public final Color selectedItemColor_ = Color.red;
	
	IEditorTool tool_ = EditorDrawToolsEnum.HAND;
	Pointd firstPoint_ = null;
	static Pointd s_dummyPoint = new Pointd(0,0);
	
	Vector <EditorDisplayElement> displayElements_;
	Vector <EditorArrow> startPositions_ = null;
	Vector <EditorItem> items_ = null;
	
	EditorDisplayElement selectedElement_ = null;
	
	final LevelFileManager levelFileManager_;
	
	UndoManager undoManager_;
	
	Level level_= null;
	
	
	public LevelEditorControl(Control control, JFrame parent)
	{
		control_ = control;
		
		// handles read and write to and from files
		levelFileManager_ = new LevelFileManager(this);
		
		displayFrame_ = parent;
		
		levelEditor_ = new LevelEditorPanel(this, displayFrame_);
		
		onNew();
	}
	
	
	public LevelEditorPanel getLevelEditorPanel()
	{
		return levelEditor_;
	}
	
	
	private void reset()
	{
		undoManager_ = new UndoManager();
		msgPainter_ = new MessagePainter(15, height_ - 15, -15);
		image_ = new BufferedImage(width_, height_, BufferedImage.TYPE_INT_ARGB);
		firstPoint_ = null;
		displayElements_ = new Vector <EditorDisplayElement> ();
		startPositions_ = new Vector <EditorArrow> ();
		items_ = new Vector <EditorItem> ();
		selectedElement_ = null;
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
		
		int initialNumSnakes = level_.getMaxNumberSnakes();
		levelEditor_.getToolsPanel().setNumSnakes(initialNumSnakes);
		updateNumSnakes(initialNumSnakes);
		
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
		
		assert null != levelEditor_ : "null LevelEditor";
		drawImage();
	}
	
	private void drawImage()
	{
		assert firstPoint_ == null : "first point should be null when using dummy point!";
		drawImage(s_dummyPoint);
	}
	
	private void drawImage(Pointd pMouse)
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
			GeometricObject shape = makeShape(pMouse);
			
			if (null != shape)
			{
				imGr.setColor(defaultElementColor_);
				shape.draw(imGr);
			}
		}
		
		if (null != selectedElement_)
		{
			Color c = selectedElement_.getColor();
			Color c2 = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
			imGr.setColor(c2);
			selectedElement_.getGeometricObject().draw(imGr);
			
			Pointd pSel = selectedElement_.getSelectedPoint();
			imGr.setColor(selectedPointColor_);
			imGr.drawRect((int)pSel.x_, (int)pSel.y_, 1, 1);
		}
		
		msgPainter_.paintMessages(imGr);
		
		levelEditor_.setImage(image_);
	}
	
	private GeometricObject makeShape(Pointd p2)
	{
		assert null != firstPoint_ : "firstPoint_ must be defined for makeShape!";
		
		double biggestX = Arith.maxd(p2.x_, firstPoint_.x_);
		double biggestY = Arith.maxd(p2.y_, firstPoint_.y_);
		double dx = Arith.absd(firstPoint_.x_ - p2.x_);
		double dy = Arith.absd(firstPoint_.y_ - p2.y_);
		
		if (tool_ == EditorDrawToolsEnum.LINE) {
			return new Segment(firstPoint_, p2);
		}
		if (tool_ == EditorDrawToolsEnum.RECTANGLE) {
			return new Rectangle(biggestX, biggestY, (int)dx, (int)dy, false);
		}
		if (tool_ == EditorDrawToolsEnum.CIRCLE) {
			double rad = Arith.maxd(dx, dy) / 2.;
			Pointd center = new Pointd(biggestX + rad, biggestY + rad);
			return new Circle(center, rad);
		}
		return null;
	}
	
	
	public void updateNumSnakes(int numSnakes)
	{
		level_.setMaxNumberSnakes(numSnakes);
		initialiseStartPositions(numSnakes, startPositions_.size());
	}
	
	private void initialiseStartPositions(int numMax, int numInitialized)
	{
		// if too many, remove
		while (startPositions_.size() > numMax)
			startPositions_.remove(numMax-1);
		
		// or add the needed ones
		for (int i=numInitialized; i<numMax; ++i)
		{
			int a = level_.getSnakeStartAngle(i);
			Pointd p = level_.getSnakeStartPosition(i);
			Arrow arrow = new Arrow(p, Arith.deg2rad(a), 20);
			Color c = GlobalColorTable.getSnakeColor(i);
			EditorArrow pos = new EditorArrow(arrow, c);
			startPositions_.add(pos);
		}
	}
	

	
	public void mouseMoved(Pointd p)
	{
		levelEditor_.setPositionText("(" + (int)p.x_ + "," + (int)p.y_ + ")");
		if (tool_ == EditorDrawToolsEnum.HAND)
		{
			if (null != selectedElement_)
				selectedElement_.move(p);
		}

		drawImage(p);
	}
	

	/*
	 * The user has to place stuff using the mouse,
	 * this function is where we handle a clicked point
	 */
	public void clickedPoint(Pointd p)
	{
		if (null == tool_)
			return;
		
			// draw tools
		if (tool_ == EditorDrawToolsEnum.CIRCLE
				|| tool_ == EditorDrawToolsEnum.LINE
				 || tool_ == EditorDrawToolsEnum.RECTANGLE
		) {
			if ( null == firstPoint_ )
				firstPoint_ = p;
			else
				addDisplayElementAction(p);
		} else if (tool_ == EditorDrawToolsEnum.HAND) {
			handleHandClick(p);
			// item tools
		} else if (tool_ instanceof EditorItemsToolsEnum) {
			addItemAction(((EditorItemsToolsEnum)tool_).getItem(p.x_, p.y_));
		} else {
			throw new UnsupportedOperationException("Unknown tool type");
		}
	}
	
	public void mouseLeftDrawPanel ()
	{
		cancelCurrent();
	}
	
	private void addDisplayElementAction(Pointd p2)
	{
		GeometricObject shape = makeShape(p2);
		firstPoint_ = null;
		EditorDisplayElement ede = EditorDisplayElement.createElement(shape, defaultElementColor_, true);
		undoManager_.actionTaken(
				new UndoableAddElement <EditorDisplayElement> (ede, displayElements_));
	}
	
	private void addItemAction(Item i)
	{
		EditorItem ei = new EditorItem(selectedItemColor_, i);
		undoManager_.actionTaken(
				new UndoableAddElement <EditorItem> (ei, items_));
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

	public void setTool(IEditorTool tool)
	{
		firstPoint_ = null;
		tool_ = tool;
		if (tool_ != EditorDrawToolsEnum.HAND)
			unselectElement();
		drawImage();
	}
	
	public void onEscPressed()
	{
		control_.showWelcomePanel();
//		cancelCurrent();
	}
	
	// DEL or BACKSP
	public void onErasePressed()
	{
		deleteSelected();
	}

	private void cancelCurrent()
	{
			// draw tools
		if (tool_ == EditorDrawToolsEnum.CIRCLE
				|| tool_ == EditorDrawToolsEnum.LINE
				 || tool_ == EditorDrawToolsEnum.RECTANGLE
		) {
			firstPoint_ = null;
		} else if (tool_ == EditorDrawToolsEnum.HAND) {
			if ( null != selectedElement_ )
			{
				unselectElement();
				undoManager_.cancelCurrent();
			}
		} else { // others don't influence
			return;
		}
		
		drawImage();
	}
	
	private void deleteSelected()
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
		setLevel(new Level("unnamed"));
	}

	public void onOpen()
	{
		try
		{
			Level l = levelFileManager_.openLevel(displayFrame_);
			setLevel(l);
			levelEditor_.requestFocus();
		}
		catch (Exception e)
		{
			levelEditor_.displayMessage("Cannot open level: " + e.getMessage(), true);
			Log.logErr("Cannot open level!");
			Log.logException(e);
		}
	}
	
	public void onSave()
	{
		updateLevel();
		levelFileManager_.saveLevelToCurrentFile(level_, displayFrame_);
		levelEditor_.requestFocus();
	}
	
	public void onSaveAs() 
	{
		updateLevel();
		levelFileManager_.saveLevelToNewFile(level_, displayFrame_);
		levelEditor_.requestFocus();
	}
	
	public void onUndo()
	{
		cancelCurrent();
		undoManager_.undo();
		drawImage();
	}
	
	public void onRedo()
	{
		cancelCurrent();
		undoManager_.redo();
		drawImage();
	}
	

	private void updateLevel()
	{
		level_.setWall(makeWall());
		int numSnakes = startPositions_.size();
		for (int i=0; i<numSnakes; ++i)
		{
			int a = Arith.rad2deg(startPositions_.get(i).getAngleRad());
			level_.setSnakeStartAngle(i, a);
			Pointd p = startPositions_.get(i).getPoint();
			level_.setSnakeStartPosition(i, p);
		}
		level_.setNumberSnakes(numSnakes);
		Vector <Item> its = new Vector <Item> ();
		for (EditorItem i : items_)
			its.add(i.getItem());
		level_.setInitialItems(its);
	}

	public void displayMessage(String msg)
	{
		msgPainter_.addMessage(msg);
	}
	
	public KeyListener getKeyListener()
	{
		return levelEditor_;
	}

}
