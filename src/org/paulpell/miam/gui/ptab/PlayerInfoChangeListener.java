package org.paulpell.miam.gui.ptab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.paulpell.miam.gui.net.ChoosePlayerColor;
import org.paulpell.miam.logic.Control;

public class PlayerInfoChangeListener
	implements ActionListener,
		CellEditorListener 
{

	Control control_;
	Vector <ChoosePlayerColor> chooseColorsComboboxes_; // to find the index from where the change came
	PlayerChoiceTable table_;
	
	public PlayerInfoChangeListener(Control control, PlayerChoiceTable table, Vector <ChoosePlayerColor> chooseColorsComboboxes)
	{
		control_ = control;
		table_ = table;
		chooseColorsComboboxes_ = chooseColorsComboboxes;
	}

	@Override
	public void editingStopped(ChangeEvent e)
	{
		if (e.getSource() instanceof PlayerChoiceNameEditor)
		{
			PlayerChoiceNameEditor editor = (PlayerChoiceNameEditor)e.getSource();
			int row = editor.getEditingRow();
			String name = editor.getEditedName();
			control_.playerChangedName(row, name);
		}
		else if (e.getSource() instanceof PlayerChoiceColorEditor)
		{
			PlayerChoiceColorEditor editor = (PlayerChoiceColorEditor)e.getSource();
			int row = editor.getEditingRow();
			Integer sid = (Integer)chooseColorsComboboxes_.get(row).getSelectedItem();
			if ( null != sid)
				control_.playerChoseColor(row, sid);
		}
	}

	@Override
	public void editingCanceled(ChangeEvent e)
	{
		// do nothing?
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		assert null != e.getSource() : "bad event!";
		assert e.getSource() instanceof ChoosePlayerColor : "bad event 2!";
		ChoosePlayerColor cpc = (ChoosePlayerColor)e.getSource();
		for (int i=0; i<chooseColorsComboboxes_.size(); ++i)
		{
			if (cpc == chooseColorsComboboxes_.get(i))
			{
				int coli = (Integer)cpc.getSelectedItem();
				control_.playerChoseColor(i, coli);
				return;
			}
		}
	}

}
