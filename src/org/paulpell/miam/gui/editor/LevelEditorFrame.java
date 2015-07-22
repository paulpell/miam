package org.paulpell.miam.gui.editor;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.paulpell.miam.gui.DrawImagePanel;
import org.paulpell.miam.gui.MessagePainter;
import org.paulpell.miam.gui.editor.tools.EditorToolsPanel;
import org.paulpell.miam.logic.levels.LevelEditorControl;


@SuppressWarnings("serial")
public class LevelEditorFrame
	extends JFrame
	implements KeyListener
{
	
	LevelEditorControl leControl_;

	final DrawImagePanel drawPanel_;
	final DrawPanelMouseHandler mouseHandler_;
	final EditorToolsPanel toolsPanel_;
	
	LevelEditorMenuBar menubar_;
	
	public LevelEditorFrame(LevelEditorControl control)
	{
		super("Snakesss - Level editor");
		
		leControl_ = control;
		
		// menu: open, save, save as
		menubar_ = new LevelEditorMenuBar(leControl_);
		setJMenuBar(menubar_);
		
		

		// draw stuff
		int width = leControl_.getWidth();
		int height = leControl_.getHeight();
		drawPanel_ = new DrawImagePanel();
		drawPanel_.setPreferredSize(new Dimension(width, height));
		add(drawPanel_, BorderLayout.WEST);
		
		
		// mouse move, mouse click
		mouseHandler_ = new DrawPanelMouseHandler(leControl_);
		drawPanel_.addMouseListener(mouseHandler_);
		drawPanel_.addMouseMotionListener(mouseHandler_);
		
		
		// and the available tools
		
		toolsPanel_ = new EditorToolsPanel(leControl_);
		add(toolsPanel_, BorderLayout.EAST);
		
		pack();
		setLocationRelativeTo(null);
		
	}
	
	public void setImage(BufferedImage image)
	{
		drawPanel_.setImage(image);
		drawPanel_.repaint();
	}
	
	public void setPositionText(String pos)
	{
		toolsPanel_.setPositionText(pos);
	}
	

	public void displayMessage(String msg)
	{
		leControl_.displayMessage(msg);
	}
	
	public void keyPressed(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_BACK_SPACE:
			leControl_.deleteSelected();
			break;
		}
	}

	public void keyReleased(KeyEvent arg0)
	{}

	public void keyTyped(KeyEvent arg0)
	{
		
	}

	public void tryLevel()
	{
		leControl_.playEditedLevel();
	}

}
