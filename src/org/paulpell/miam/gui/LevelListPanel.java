package org.paulpell.miam.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.paulpell.miam.logic.levels.LevelFileManager;

@SuppressWarnings("serial")
public class LevelListPanel extends JPanel
{
	JComboBox <String>  levelChoice_;
	
	
	public enum Orientation { HORIZONTAL, VERTICAL };

	public LevelListPanel(Orientation o)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout (layout);
		GridBagConstraints c = new GridBagConstraints();
		if (o == Orientation.HORIZONTAL)
			c.gridx = 0;
		else 
			c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 0);
		add(new JLabel("Level:"), c);
		
		levelChoice_ = new JComboBox<String> ();
		String[] levelsList = LevelFileManager.listLevels();
		DefaultComboBoxModel <String> model =
				new DefaultComboBoxModel <String> (levelsList);
		levelChoice_.setModel(model);

		c = new GridBagConstraints();

		if (o == Orientation.HORIZONTAL)
			c.gridx = 1;
		else 
			c.gridy = 1;
		add(levelChoice_, c);
	}
	
	public String getLevelName()
	{
		return (String)levelChoice_.getSelectedItem();
	}
	
	
	public void incrementLevel(int di)
	{
		int c = levelChoice_.getItemCount();
		int i = (levelChoice_.getSelectedIndex() + di + c) % c;
		levelChoice_.setSelectedIndex(i);
	}
}
