package org.paulpell.miam.gui.editor;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.levels.LevelEditorControl;

class DrawPanelMouseHandler
	implements MouseListener, MouseMotionListener
{
	
	LevelEditorControl leControl_;
	
	public DrawPanelMouseHandler(LevelEditorControl lec) {
		leControl_ = lec;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Point p = arg0.getPoint();
		leControl_.clickedPoint(new Pointd(p.x, p.y));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {
		leControl_.mouseLeftDrawPanel();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		Point p = arg0.getPoint();
		leControl_.mouseMoved(new Pointd(p.x, p.y));
	}
}