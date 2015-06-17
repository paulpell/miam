package org.paulpell.miam.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.paulpell.miam.logic.draw.items.AllTheItems;

import org.paulpell.miam.gui.editor.LevelFileManager;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.levels.DefaultLevel;

@SuppressWarnings("serial")
public class LevelChooserFrame
		extends JFrame
		implements KeyListener
{

	JButton chooseButton_;
	JComboBox <String>  levelChoice_;
	JComboBox<Integer> snakeNoList_;
	
	boolean closed_ = false; // to indicate that user pressed ESC
	
	public LevelChooserFrame(final Frame f)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createGUI(f);
			}
		});
		
	}
	
	private void createGUI(Frame f)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c;
		
		// number of snakes
		JPanel snakeNoPanel = new JPanel();
		snakeNoPanel.add(new JLabel("Snakes:"), BorderLayout.WEST);
		Vector<Integer> ss = new Vector<Integer>();
		for (int i=1; i<=Constants.MAX_NUMBER_OF_SNAKES; ++i)
			ss.add(i);

		snakeNoList_ = new JComboBox<Integer>(ss);
		snakeNoList_.setSelectedIndex(Globals.NUMBER_OF_SNAKES - 1);
		snakeNoList_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateNumSnakes();
			}
		});
		snakeNoPanel.add(snakeNoList_, BorderLayout.EAST);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);
		add(snakeNoPanel, c);
		
		// level list
		JPanel levelPanel = new JPanel();
		levelPanel.add(new JLabel("Level:"), BorderLayout.WEST);
		levelChoice_ = new JComboBox<String> ();
		String[] s = listLevels();
		DefaultComboBoxModel <String> model =
				new DefaultComboBoxModel <String> (s);
		levelChoice_.setModel(model);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);
		levelPanel.add(levelChoice_, BorderLayout.EAST);
		add(levelPanel, c);
		
		// start button
		//Icon lightningIcon = AllTheItems.items[AllTheItems.INDEX_LIGHTNING].getImageIcon();
		Icon lightningIcon = AllTheItems.getImageIcon(AllTheItems.INDEX_LIGHTNING);
		chooseButton_ = new JButton("Start", lightningIcon);
		chooseButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				endWait();
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0;
		c.insets = new Insets(5, 5, 5, 5);
		add(chooseButton_, c);
		
		Dimension size = new Dimension(300, 200);
		setLocationRelativeTo(f);
		setPreferredSize(size);
		setSize(size);
		doLayout();
		setVisible(true);
	}
	
	private void updateNumSnakes()
	{
		ListModel<Integer> lm = snakeNoList_.getModel();
		Globals.NUMBER_OF_SNAKES = lm.getElementAt(snakeNoList_.getSelectedIndex());
	}
	
	private String[] listLevels()
	{
		ArrayList <String> levelNames = new ArrayList <String> ();
		levelNames.add("default");
		
		try
		{
			File folder = new File("saves");
			for (String s : folder.list())
				levelNames.add(s);
		}
		catch (Exception e)
		{}

		String[] s = new String[]{};
		s = levelNames.toArray(s);
		return s;
	}
	
	private void endWait()
	{
		synchronized (this)
		{
			notify();
		}
		
	}
	
	// returns null if default level is chosen
	public Level getLevel(GameSettings settings)
			throws Exception
	{
		Level l;
		if (closed_)
			l = null;
		
		else if (levelChoice_.getSelectedIndex() == 0)
			l = new DefaultLevel(settings);
		
		else
		{
			File f = new File(
					"." + File.separatorChar
					+ "saves" + File.separatorChar
					+ levelChoice_.getSelectedItem());
			l = LevelFileManager.readLevelFromFile(f);
		}

		setVisible(false);
		dispose();
		
		return l;
	}
	
	private void incrementLevel(int di)
	{
		int c = levelChoice_.getItemCount();
		int i = (levelChoice_.getSelectedIndex() + di + c) % c;
		levelChoice_.setSelectedIndex(i);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_ENTER:
			endWait();
			break;
			
		case KeyEvent.VK_UP:
			incrementLevel(1);
			break;

		case KeyEvent.VK_DOWN:
			incrementLevel(-1);
			break;
		case KeyEvent.VK_ESCAPE:
			closed_ = true;
			endWait();
			break;
		default:
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

}
