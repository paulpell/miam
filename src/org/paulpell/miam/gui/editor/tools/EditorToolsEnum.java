package org.paulpell.miam.gui.editor.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import org.paulpell.miam.gui.editor.LevelEditor;
import org.paulpell.miam.logic.draw.items.AllTheItems;

public enum EditorToolsEnum
{
	NONE ("NONE", null),
	LINE ("Line", null),
	CIRCLE("Circle", null),
	RECTANGLE("Rectangle", null),
	HAND("Hand", null),
	SCORE("Score", AllTheItems.getImageIcon(AllTheItems.INDEX_SCORE)),
	BANANA("Banana", AllTheItems.getImageIcon(AllTheItems.INDEX_BANANA)),
	BANANA_SP("Banana special", AllTheItems.getImageIcon(AllTheItems.INDEX_BANANA_SPECIAL)),
	LIGHTNING("Lightning", AllTheItems.getImageIcon(AllTheItems.INDEX_LIGHTNING)),
	REVERSE("Reverse", AllTheItems.getImageIcon(AllTheItems.INDEX_REVERSO))
	//ARROW("Snake start")
	;
	
	public final String name_;
	public final Icon icon_;
	
	private EditorToolsEnum(String name, Icon icon)
	{
		name_ = name;
		icon_ = icon;
	}
	
	public JToggleButton createToggleButton(final LevelEditor editor)
	{
		JToggleButton b;
		if (icon_ == null)
			b = new JToggleButton(name_);
		else
			b = new JToggleButton(icon_);
		
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				editor.setTool(EditorToolsEnum.this);
			}
		});
		return b;
	}
}
