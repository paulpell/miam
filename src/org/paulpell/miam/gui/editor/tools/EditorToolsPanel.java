package org.paulpell.miam.gui.editor.tools;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.paulpell.miam.logic.draw.items.AllTheItems;
import org.paulpell.miam.logic.levels.LevelEditorControl;

@SuppressWarnings("serial")
public class EditorToolsPanel extends JPanel
{
	
	final LevelEditorControl leControl_;
	
	JLabel positionLabel_;
	JComboBox<Integer> numSnakesCombobox_;

	public EditorToolsPanel(LevelEditorControl lec)
	{
		
		leControl_ = lec;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		ButtonGroup toolButtonsGroup = new ButtonGroup(); // to select only one tool at a time
		
		
		add(makeNumSnakesPanel());

		add(Box.createVerticalStrut(5));
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createVerticalStrut(5));
		
		add(makeItemsPanel(toolButtonsGroup));
		
		add(Box.createVerticalStrut(5));
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createVerticalStrut(5));
		
		add(makeDrawToolsPanel(toolButtonsGroup));
		
		add(Box.createVerticalStrut(5));
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createVerticalStrut(5));
		
		ImageIcon lightIcon = AllTheItems.getImageIcon(AllTheItems.INDEX_LIGHTNING);
		JButton tryButton = new JButton ("Try", lightIcon);
		tryButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				leControl_.playEditedLevel();
			}
		});
		tryButton.setMnemonic(KeyEvent.VK_T);
		add(tryButton);
		
		add(Box.createVerticalGlue());
		
		positionLabel_ = new JLabel();
		add(positionLabel_);
	}
	
	private JPanel makeItemsPanel(ButtonGroup toolButtonsGroup)
	{
		FlowLayout fl = new FlowLayout();
		JPanel itemsPanel = new JPanel(fl);
		fl.setHgap(9);
		JToggleButton handBut = null;
		for (EditorItemsToolsEnum et : EditorItemsToolsEnum.values())
		{
			JToggleButton b = et.createToggleButton(leControl_);
			if (handBut == null)
				handBut = b;
			toolButtonsGroup.add(b);
			itemsPanel.add(b);
		}
		// select hand
		handBut.setSelected(true);

		// combine with a label
		fl = new FlowLayout();
		JPanel totPanel = new JPanel(fl);
		fl.setHgap(31);
		totPanel.add(new JLabel("Items:"));
		totPanel.add(itemsPanel);
		return totPanel;
	}
	
	private JPanel makeDrawToolsPanel(ButtonGroup toolButtonsGroup)
	{
		JPanel itemsPanel = new JPanel(new FlowLayout());
		JToggleButton handBut = null;
		for (EditorDrawToolsEnum et : EditorDrawToolsEnum.values())
		{
			JToggleButton b = et.createToggleButton(leControl_);
			if (handBut == null)
				handBut = b;
			toolButtonsGroup.add(b);
			itemsPanel.add(b);
		}
		// select hand
		handBut.setSelected(true);
		
		return itemsPanel;
	}
	
	private JPanel makeNumSnakesPanel()
	{
		JPanel nSnakesPanel = new JPanel(new FlowLayout());
		
		nSnakesPanel.add(new JLabel("#Snakes:"));
		
		numSnakesCombobox_ = new JComboBox<Integer>(new Integer[]{1,2,3,4});
		numSnakesCombobox_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numSnakes = (Integer)numSnakesCombobox_.getSelectedItem();
				leControl_.updateNumSnakes(numSnakes);
			}
		});
		nSnakesPanel.add(numSnakesCombobox_);
		
		return nSnakesPanel;
	}
	
	public void setPositionText(String s)
	{
		positionLabel_.setText(s);
	}
	
	public void setNumSnakes(int numSnakes)
	{
		numSnakesCombobox_.setSelectedItem(numSnakes);
	}
}
