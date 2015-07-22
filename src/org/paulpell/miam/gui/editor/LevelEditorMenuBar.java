package org.paulpell.miam.gui.editor;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.paulpell.miam.logic.levels.LevelEditorControl;

@SuppressWarnings("serial")
public class LevelEditorMenuBar extends JMenuBar
{

	public LevelEditorMenuBar(final LevelEditorControl lec)
	{
		addFileMenu(lec);
		addEditMenu(lec);
	}
	
	private void addFileMenu(final LevelEditorControl lec)
	{
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem newItem = new JMenuItem("New");
		newItem.setMnemonic(KeyEvent.VK_N);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		newItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				lec.onNew();
			}
		});
		fileMenu.add(newItem);
		
		JMenuItem openItem = new JMenuItem("Open...");
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		openItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				lec.onOpen();
			}
		});
		fileMenu.add(openItem);
		
		JMenuItem saveItem = new JMenuItem("Save...");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		saveItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				lec.onSave();
			}
		});
		fileMenu.add(saveItem);
		
		JMenuItem saveAsItem = new JMenuItem("Save as...");
		saveAsItem.setMnemonic(KeyEvent.VK_A);
		saveAsItem.setAccelerator(
				KeyStroke.getKeyStroke(
						KeyEvent.VK_S,
						Event.CTRL_MASK | Event.SHIFT_MASK));
		saveAsItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				lec.onSaveAs();
			}
		});
		fileMenu.add(saveAsItem);
		
		
		add(fileMenu);
	}
	
	private void addEditMenu(final LevelEditorControl lec)
	{
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		JMenuItem undoItem = new JMenuItem("Undo");
		undoItem.setMnemonic(KeyEvent.VK_Z);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		undoItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				lec.onUndo();
			}
		});
		editMenu.add(undoItem);
		
		JMenuItem redoItem = new JMenuItem("Redo");
		redoItem.setMnemonic(KeyEvent.VK_Y);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		redoItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				lec.onRedo();
			}
		});
		editMenu.add(redoItem);

		add(editMenu);
	}

}
