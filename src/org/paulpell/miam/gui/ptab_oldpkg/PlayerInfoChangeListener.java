package org.paulpell.miam.gui.ptab_oldpkg;

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
			// BAD PlayerChoiceNameEditor editor = (PlayerChoiceNameEditor)e.getSource();
			// BAD int row = editor.getEditingRow();
			// BAD String name = editor.getEditedName();
			// BAD control_.playerChangedName(row, name);
		}
		else if (e.getSource() instanceof PlayerChoiceColorEditor)
		{
			// BAD PlayerChoiceColorEditor editor = (PlayerChoiceColorEditor)e.getSource();
			// BAD int row = editor.getEditingRow();
			// BAD Integer sid = (Integer)chooseColorsComboboxes_.get(row).getSelectedItem();
			// BAD if ( null != sid)
				// BAD control_.playerChoseColor(row, sid);
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
				// BAD int coli = (Integer)cpc.getSelectedItem();
				// BAD control_.playerChoseColor(i, coli);
				return;
			}
		}
	}

}
