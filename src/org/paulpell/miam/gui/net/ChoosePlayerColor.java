package org.paulpell.miam.gui.net;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.paulpell.miam.gui.GlobalColorTable;

@SuppressWarnings("serial")
public class ChoosePlayerColor
	extends JComboBox<Integer>
{
	
	class ColorListCellRenderer
			implements ListCellRenderer<Integer>
	{
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Integer> list, Integer value, int index,
				boolean isSelected, boolean cellHasFocus)
		{
			JPanel comp = new JPanel();
			if ( null == value )
				return comp;
			Color col = GlobalColorTable.getSnakeColor(value);
			comp.setBackground(col);
			comp.setPreferredSize(new Dimension(20, 20));
			comp.setMinimumSize(new Dimension(20, 20));
			comp.setSize(new Dimension(20, 20));
			return comp;
		}
		
	}
	
	DefaultComboBoxModel<Integer> model_;

	public ChoosePlayerColor(Vector<Integer> colIndexes)//, ActionListener colorChangeListener)
	{
		setRenderer(new ColorListCellRenderer());
		model_ = new DefaultComboBoxModel<Integer>(colIndexes);
		//addActionListener(colorChangeListener);
	}
	
/*	public void setAvailableColors(Vector<Integer> colIndexes)
	{
		Integer sel= (Integer)model_.getSelectedItem();
		int curr = (null == sel) ? 0 : sel;
		boolean isCurrOK = false;
		
		model_.removeAllElements();
		for (int i: colIndexes)
		{
			if ( i == curr )
				isCurrOK = true;
			model_.addElement(i);
		}
		
		if ( ! isCurrOK )
			curr = colIndexes.get(0);
		
		model_.setSelectedItem(curr);
	}*/
	
	public void selectColorFromTableIndex(int i)
	{
		model_.setSelectedItem(i);
	}

}
