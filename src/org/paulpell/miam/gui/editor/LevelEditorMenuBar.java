package org.paulpell.miam.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class LevelEditorMenuBar extends JMenuBar
{

	public LevelEditorMenuBar(final LevelEditor le)
	{
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem openItem = new JMenuItem("Open...");
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				le.onOpen();
			}
		});
		fileMenu.add(openItem);
		
		JMenuItem saveItem = new JMenuItem("Save...");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				le.onSave();
			}
		});
		fileMenu.add(saveItem);
		
		JMenuItem saveAsItem = new JMenuItem("Save as...");
		saveAsItem.setMnemonic(KeyEvent.VK_S);
		saveAsItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				le.onSaveAs();
			}
		});
		fileMenu.add(saveAsItem);
		
		
		add(fileMenu);
	}

}
