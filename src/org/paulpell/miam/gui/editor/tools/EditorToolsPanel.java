package org.paulpell.miam.gui.editor.tools;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.paulpell.miam.gui.editor.LevelEditor;

@SuppressWarnings("serial")
public class EditorToolsPanel extends JPanel
{
	
	final LevelEditor levelEditor_;
	
	JLabel positionLabel_;

	public EditorToolsPanel(LevelEditor le)
	{
		
		levelEditor_ = le;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		ButtonGroup toolButtonsGroup = new ButtonGroup(); // to select only one tool at a time
		
		for (EditorToolsEnum et : EditorToolsEnum.values())
		{
			JToggleButton b = et.createToggleButton(levelEditor_);
			toolButtonsGroup.add(b);
			add(b);
		}
		
		/*add(Box.createVerticalGlue());
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.LINE, null);
		toolButtonsGroup.add(button);
		add(button);

		button = new EditorToolButton(levelEditor_, EditorToolsEnum.RECTANGLE, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.CIRCLE, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.HAND, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.SCORE, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.BANANA, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.BANANA_SP, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.REVERSE, null);
		toolButtonsGroup.add(button);
		add(button);
		
		button = new EditorToolButton(levelEditor_, EditorToolsEnum.LIGHTNING, null);
		toolButtonsGroup.add(button);
		add(button);*/
		
		/*button = new EditorToolButton(levelEditor_, EditorToolsEnum.ARROW, null);
		toolButtonsGroup.add(button);
		add(button);
*/
		add(Box.createVerticalStrut(5));
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createVerticalStrut(5));
		
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
	
	public void setPosition(String s)
	{
		positionLabel_.setText(s);
	}
}
