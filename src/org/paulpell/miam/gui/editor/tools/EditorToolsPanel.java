package org.paulpell.miam.gui.editor.tools;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

	public EditorToolsPanel(LevelEditorControl lec)
	{
		
		leControl_ = lec;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		ButtonGroup toolButtonsGroup = new ButtonGroup(); // to select only one tool at a time
		
		for (EditorToolsEnum et : EditorToolsEnum.values())
		{
			
			JToggleButton b = et.createToggleButton(leControl_);
			toolButtonsGroup.add(b);
			add(b);
		}
		
		// select hand
		((JToggleButton)getComponent(0)).setSelected(true);
		
		
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
		
		
		/*JButton cancelBut =new JButton("Cancel");
		cancelBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				levelEditor_.cancelLast();
			}
		});
		add(cancelBut);*/
		add(Box.createVerticalGlue());
		
		positionLabel_ = new JLabel();
		add(positionLabel_);
	}
	
	public void setPositionText(String s)
	{
		positionLabel_.setText(s);
	}
}
