package org.paulpell.miam.gui.ptab;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.paulpell.miam.gui.net.ChoosePlayerColor;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.net.PlayerInfo;

@SuppressWarnings("serial")
public class PlayerChoiceTable
	extends JTable
	implements TableCellRenderer
{
	static final Class<?> NAME_CELL_EDITOR_CLASS = String.class;
	static final Class<?> COLOR_CELL_EDITOR_CLASS = Integer.class;
	
	PlayerChoiceTableModel model_;

	// the components for choosing the color
	Vector <ChoosePlayerColor> chooseColorsComboboxes_;
	PlayerInfoChangeListener playerInfoChangeListener_;
	
	PlayerChoiceNameEditor nameCellEditor_;
	PlayerChoiceColorEditor colorCellEditor_;

	public PlayerChoiceTable()
	{
		chooseColorsComboboxes_ = new Vector <ChoosePlayerColor> ();

		model_ = new PlayerChoiceTableModel(NAME_CELL_EDITOR_CLASS, COLOR_CELL_EDITOR_CLASS);
		setModel(model_);
		setDefaultRenderer(Integer.class, this);
		
		createDefaultEditors();
		TableCellEditor editor = getDefaultEditor(NAME_CELL_EDITOR_CLASS);
		nameCellEditor_ = new PlayerChoiceNameEditor(editor);
		setDefaultEditor(NAME_CELL_EDITOR_CLASS, nameCellEditor_);
		
		colorCellEditor_ = new PlayerChoiceColorEditor();
		setDefaultEditor(COLOR_CELL_EDITOR_CLASS, colorCellEditor_);
	}
	
	public void reset(int rowCount)
	{
		// remove old listener if present
		if (null != playerInfoChangeListener_)
			getDefaultEditor(NAME_CELL_EDITOR_CLASS).removeCellEditorListener(playerInfoChangeListener_);
		playerInfoChangeListener_ = null;
		model_.setPlayerInfos(null);
		model_.setRowCount(rowCount);
	}
	
	public void setPlayerNumber (int n)
	{
		model_.setRowCount(n);
	}
	
	public void setPlayerInfos(Vector <PlayerInfo> playerInfos,
			Vector<Integer> unusedColors,
			Control control)
	{
		model_.setPlayerInfos(playerInfos);
		
		// remove old listener if present
		if (null != playerInfoChangeListener_)
			nameCellEditor_.removeCellEditorListener(playerInfoChangeListener_);
		
		
		chooseColorsComboboxes_.removeAllElements();
		// update color choices
		for (PlayerInfo pi : playerInfos)
		{
			unusedColors.insertElementAt(pi.getSnakeId(), 0); // first color: the selected one
			ChoosePlayerColor cpc = new ChoosePlayerColor(unusedColors, playerInfoChangeListener_);
			cpc.selectColorFromTableIndex(pi.getSnakeId());
			chooseColorsComboboxes_.add(cpc);
		}
		
		colorCellEditor_.setChooseColorComboboxes(chooseColorsComboboxes_);
		playerInfoChangeListener_ = new PlayerInfoChangeListener(control, this, chooseColorsComboboxes_);
		nameCellEditor_.addCellEditorListener(playerInfoChangeListener_);
		colorCellEditor_.addCellEditorListener(playerInfoChangeListener_);
		
	}
	
	public String getPlayerName (int row)
	{
		return model_.getPlayerName(row);
	}

	
	//************************** Renderer interface
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		if ( null == value)
			return null;
		
		if ( 1 == column )
		{
			assert value instanceof Integer : "bad value!";
			int sid = (Integer)value;
			assert sid < chooseColorsComboboxes_.size() : "should receive null value!";
			ChoosePlayerColor cpc = chooseColorsComboboxes_.get (row);
			cpc.selectColorFromTableIndex(sid);
			return cpc;
		}
		Log.logErr("TableCellRenderer(" + row + ","+column+ "):  received v = " + value);
		return null;
	}

	
	//************************** Editor interface
	/*@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCellEditorListener(CellEditorListener l)
	{
		cellEditorListeners_.add(l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l)
	{
		cellEditorListeners_.remove(l);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}*/

}
