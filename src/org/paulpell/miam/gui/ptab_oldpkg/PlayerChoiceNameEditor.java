package org.paulpell.miam.gui.ptab_oldpkg;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class PlayerChoiceNameEditor
	extends DefaultCellEditor
{
	int editedRow_;
	TableCellEditor defaultEditor_;
	JTextField editedTextField_;
	
	public PlayerChoiceNameEditor(TableCellEditor defaultEditor)
	{
		super (new JTextField());
		defaultEditor_ = defaultEditor;
	}
	
	public int getEditingRow()
	{
		return editedRow_;
	}
	
	public String getEditedName()
	{
		return editedTextField_.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		editedRow_ = row;
		Component c = defaultEditor_.getTableCellEditorComponent(table, value, isSelected, row, column);
		editedTextField_ = (JTextField)c;
		editedTextField_.setText("");
		return c;
	}

}
