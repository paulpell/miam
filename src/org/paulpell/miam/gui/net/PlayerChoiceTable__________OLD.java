package org.paulpell.miam.gui.net;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.paulpell.miam.logic.Log;
import org.paulpell.miam.net.PlayerInfo;

@SuppressWarnings("serial")
public class PlayerChoiceTable__________OLD
	extends JTable
	implements TableCellRenderer
{
	
	/**
	 * This table model will render <code>PlayerInfo</code>s. <p>
	 * 2 Columns: name@host (String), color (ChoosePlayerColor)
	 * @author paul
	 *
	 */
	private class PlayerChoiceTableModel
			extends AbstractTableModel
	{
		
		Vector <PlayerInfo> playerInfos_;
		Vector <TableModelListener> tableModelListeners_;
		int rowNumber_;
		
		
		public PlayerChoiceTableModel()
		{
			rowNumber_ = 0;
			tableModelListeners_ = new Vector <TableModelListener> ();
		}
		
		public void setPlayerInfos(Vector <PlayerInfo> playerInfos)
		{
			updateRows (rowNumber_, playerInfos);
		}
		
		private void updateRows (int newRowNum, Vector <PlayerInfo> playerInfos)
		{
			if (playerInfos != null)
				assert newRowNum >= playerInfos.size() : "Not enough rows";

			int allCols = TableModelEvent.ALL_COLUMNS;

			// adding/removing rows (ie. potential player number)
			if (newRowNum != rowNumber_)
			{
				int oldRowNum = rowNumber_;
				rowNumber_ = newRowNum;
				boolean addR = newRowNum > oldRowNum;
				int type = addR ? TableModelEvent.INSERT : TableModelEvent.DELETE;
				int firstRow = addR ? oldRowNum : newRowNum;
				int lastRow = addR ? newRowNum - 1 : oldRowNum - 1;
				TableModelEvent e = new TableModelEvent (this, firstRow, lastRow, allCols, type);
				for (TableModelListener l : tableModelListeners_)
					l.tableChanged(e);
			}
			
			// let's update player infos anyway
			int oldSize = 0;
			if ( null != playerInfos_ )
				oldSize = playerInfos_.size();
			playerInfos_ = playerInfos;
			int type = TableModelEvent.UPDATE;
			int firstRow = 0;
			int lastRow = playerInfos_ == null ? oldSize : playerInfos.size() - 1;
			TableModelEvent e = new TableModelEvent (this, firstRow, lastRow, allCols, type);
			for (TableModelListener l : tableModelListeners_)
				l.tableChanged(e);
		}

		@Override
		public int getRowCount()
		{
			return rowNumber_;
		}
		
		public void setRowCount (int n)
		{
			updateRows (n, playerInfos_);
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex)
		{
			if ( 0 == columnIndex)
				return "Player";
			if ( 1 == columnIndex)
				return "Snake color";
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if ( 0 == columnIndex )
				return String.class;
			if ( 1 == columnIndex )
				return ChoosePlayerColor.class;
			return null;
		}

		// edit only name, column 0
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return 0 == columnIndex;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if ( null == playerInfos_ )
				return null;
			if (rowIndex < 0
					|| columnIndex < 0
					|| rowIndex >= rowNumber_
					|| columnIndex > 1)
				return null;
			// handle not filled rows
			if (rowIndex >= playerInfos_.size())
			{
				if ( 0 == columnIndex)
					return "-";
				return null;
			}
			PlayerInfo pi = playerInfos_.get(rowIndex);
			if ( 0 == columnIndex )
			{
				return pi.getName() + "@" + pi.getClientLetter();
			}
			if ( 1 == columnIndex )
			{
				return pi.getSnakeId();
			}
			assert false : "HUH? Bad programmer!";
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			throw new UnsupportedOperationException("Cannot change values!");
		}

		@Override
		public void addTableModelListener(TableModelListener l)
		{
			tableModelListeners_.add(l);
		}

		@Override
		public void removeTableModelListener(TableModelListener l)
		{
			tableModelListeners_.remove(l);
		}
		
	}
	
	PlayerChoiceTableModel model_;
	Vector <ChoosePlayerColor> chooseColorsComboboxes_;
	
	public PlayerChoiceTable__________OLD()
	{
		model_ = new PlayerChoiceTableModel ();
		setModel(model_);
		setDefaultRenderer(ChoosePlayerColor.class, this);
	}
	
	public void setPlayerInfos(Vector <PlayerInfo> playerInfos)
	{
		model_.setPlayerInfos(playerInfos);
		chooseColorsComboboxes_ = new Vector <ChoosePlayerColor> ();
		if ( null != playerInfos )
		{
			for (PlayerInfo pi : playerInfos)
			{
				assert false : "unused code";
//				ChoosePlayerColor cpc = new ChoosePlayerColor();
//				cpc.setSelectedIndex(pi.getSnakeId());
//				chooseColorsComboboxes_.add (cpc);
			}
		}
	}
	
	public void setRowNumber ( int n )
	{
		model_.setRowCount(n);
	}

	// only for ChoosePlayerColor.class
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
			cpc.setSelectedIndex(sid);
			return cpc;
		}
		Log.logErr("TableCellRenderer(" + row + ","+column+ "):  received v = " + value);
		return null;
	}

}
