package org.paulpell.miam.gui.editor.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.paulpell.miam.logic.levels.LevelEditorControl;

public enum EditorDrawToolsEnum
	implements IEditorTool
{
	HAND("Hand"),
	LINE ("Line"),
	CIRCLE("Circle"),
	RECTANGLE("Rectangle")
	;
	
	public final String name_;
	
	private EditorDrawToolsEnum(String name)
	{
		name_ = name;
	}
	

	public JToggleButton createToggleButton(final LevelEditorControl leControl)
	{
		JToggleButton b = new JToggleButton(name_);
		b.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				leControl.setTool(EditorDrawToolsEnum.this);
			}
		});
		return b;
	}

}
