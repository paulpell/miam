package org.paulpell.miam.gui.ptab_oldpkg;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.paulpell.miam.logic.players.PlayerInfo;

/**
 * This table model will render <code>PlayerInfo</code>s. <p>
 * 2 Columns:
 *  - name@host (String)
 *  - choosePlayerColor (we use Integer to encode the current color)
 * @author paul
 *
 */
@SuppressWarnings("serial")
public class PlayerChoiceTableModel extends AbstractTableModel
{

	Vector <PlayerInfo> playerInfos_;
	int rowNumber_;
	Class<?> nameClass_;
	Class<?> colorClass_;

	public PlayerChoiceTableModel(Class<?> nameClass, Class<?> colorClass)
	{
		nameClass_ = nameClass;
		colorClass_ = colorClass;
		rowNumber_ = 0;
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
	
	public void setPlayerInfos(Vector <PlayerInfo> playerInfos)
	{
		updateRows (rowNumber_, playerInfos);
	}
	
	private void updateRows (int newRowNum, Vector <PlayerInfo> playerInfos)
	{
		//if (playerInfos != null)
		//	assert newRowNum >= playerInfos.size() : "Not enough rows";


		// adding/removing rows (ie. potential player number)
		if (newRowNum != rowNumber_)
		{
			int oldRowNum = rowNumber_;
			rowNumber_ = newRowNum;
			boolean addR = newRowNum > oldRowNum;
			int firstRow = addR ? oldRowNum : newRowNum;
			int lastRow = addR ? newRowNum - 1 : oldRowNum - 1;
			
			if (addR)
				fireTableRowsInserted(firstRow, lastRow);
			else
				fireTableRowsDeleted(firstRow, lastRow);
		}
		
		// let's update player infos anyway
		int oldSize = 0;
		if ( null != playerInfos_) oldSize = playerInfos_.size();
		playerInfos_ = playerInfos;
		int newSize = 0;
		if ( null != playerInfos_ ) newSize = playerInfos_.size() - 1;
		int lastRow = newSize > oldSize ? newSize : oldSize;
		fireTableRowsUpdated(0, lastRow);
	}
	
	public String getPlayerName (int row)
	{
		return playerInfos_.get(row).getName();
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if ( 0 == columnIndex )
			return nameClass_;
		if ( 1 == columnIndex )
			return colorClass_;
		return null;
	}
	
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		if (0 == columnIndex)
			return true;
		return rowIndex < playerInfos_.size();
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
		
		// handle unfilled rows
		if (rowIndex >= playerInfos_.size())
		{
			if ( 0 == columnIndex)
				return "-";
			return null;
		}
		
		PlayerInfo pi = playerInfos_.get(rowIndex);
		if ( 0 == columnIndex )
			return pi.getName() + "@" + pi.getClientLetter();
		if ( 1 == columnIndex )
			return pi.getSnakeId();
		
		assert false : "HUH? Bad programmer!";
		return null;
	}

}
