package org.paulpell.miam.gui.ptab_oldpkg;

import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.paulpell.miam.gui.net.ChoosePlayerColor;

@SuppressWarnings("serial")
public class PlayerChoiceColorEditor
	extends DefaultCellEditor
{
	int editedRow_;
	Vector <ChoosePlayerColor> chooseColorsComboboxes_;
	
	public PlayerChoiceColorEditor()
	{
		super (new JComboBox<Integer>());
	}
	
	public int getEditingRow()
	{
		return editedRow_;
	}
	
	public void setChooseColorComboboxes(Vector <ChoosePlayerColor> chooseColorsComboboxes)
	{
		chooseColorsComboboxes_ = chooseColorsComboboxes;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		editedRow_ = row;
		if (null == value
				|| null == chooseColorsComboboxes_
				|| editedRow_ >= chooseColorsComboboxes_.size())
			return new JPanel();
		
		return chooseColorsComboboxes_.get(editedRow_);
	}

}
