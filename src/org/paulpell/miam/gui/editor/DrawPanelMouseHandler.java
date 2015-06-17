package org.paulpell.miam.gui.editor;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.paulpell.miam.geom.Pointd;

class DrawPanelMouseHandler implements MouseListener, MouseMotionListener
{
	
	LevelEditor levelEditor_;
	
	public DrawPanelMouseHandler(LevelEditor le) {
		levelEditor_ = le;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Point p = arg0.getPoint();
		levelEditor_.clickedPoint(new Pointd(p.x, p.y));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {
		levelEditor_.cancelCurrent();
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
		levelEditor_.mouseMoved(new Pointd(p.x, p.y));
	}
}