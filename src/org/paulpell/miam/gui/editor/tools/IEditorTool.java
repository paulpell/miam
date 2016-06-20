package org.paulpell.miam.gui.editor.tools;

import javax.swing.JToggleButton;

import org.paulpell.miam.logic.levels.LevelEditorControl;

public interface IEditorTool
{

	public JToggleButton createToggleButton(final LevelEditorControl leControl);
	
}
