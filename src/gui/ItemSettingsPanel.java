package gui;

import java.awt.BorderLayout;
import java.awt.CheckboxGroup;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import logic.Constants;
import logic.Globals;
import logic.draw.items.AllTheItems;
import logic.draw.items.Item;

@SuppressWarnings("serial")
public class ItemSettingsPanel extends JPanel {

	// this list stores the buttons to disable when score_only mode is activated:
	// we don't want to use the probabilities per item.
	LinkedList<JComponent> compsToDisable = new LinkedList<JComponent>();

	final JCheckBox itemsOnlyCB = new JCheckBox();
	OneItemProbBoxes[] itemBoxes;
	
	public ItemSettingsPanel() {
		
		
		
		addItemsOnly();
		addItemsProbs();
		addApplyButton();
		
		setSettingEnabled(!Globals.SCORE_ITEMS_ONLY);
	}
	
	void apply() {
		Globals.SCORE_ITEMS_ONLY = itemsOnlyCB.isSelected();
		if (!Globals.SCORE_ITEMS_ONLY) {
			int weights[] = new int[itemBoxes.length];
			for (int i=0; i<weights.length; ++i)
				weights[i] = itemBoxes[i].getSelectedWeight();
			AllTheItems.setProbabilities(weights);
		}
	}
	
	
	// adds a checkbox to set whether only score items are used
	void addItemsOnly() {
		JPanel scoreItemsOnlyPanel = new JPanel();
		scoreItemsOnlyPanel.add(new JLabel("Score items only"));
		itemsOnlyCB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				boolean b = ! itemsOnlyCB.isSelected();
				setSettingEnabled(b);
			}
		});
		itemsOnlyCB.setSelected(Globals.SCORE_ITEMS_ONLY);
		scoreItemsOnlyPanel.add(itemsOnlyCB);
		add(scoreItemsOnlyPanel, BorderLayout.NORTH);
	}
	
	
	// adds boxes to specify the appearance probability for each item
	void addItemsProbs() {
		Item[] is = AllTheItems.getItems();
		
		JPanel itemProbsPanel = new JPanel();
		itemProbsPanel.setLayout(new GridLayout(is.length + 1, 5));
		
		itemBoxes = new OneItemProbBoxes[is.length];
		
		// header of the table
		itemProbsPanel.add(new JLabel(""));
		itemProbsPanel.add(new JLabel("null"));
		itemProbsPanel.add(new JLabel("low"));
		itemProbsPanel.add(new JLabel("mid"));
		itemProbsPanel.add(new JLabel("high"));
		
		for (int i=0; i<is.length; ++i) {
			itemProbsPanel.add(new JLabel(is[i].getTextDescription()));
			itemBoxes[i] = new OneItemProbBoxes(is[i]);
			JCheckBox cbs[] = itemBoxes[i].getCBs();
			for (int j=0; j<cbs.length; ++j) {
				compsToDisable.add(cbs[j]);
				itemProbsPanel.add(cbs[j]);
			}
		}
		//compsToDisable.add(itemProbsPanel);
		add(itemProbsPanel, BorderLayout.CENTER);
	}
	
	void addApplyButton() {
		JButton appB = new JButton("Apply");
		add(appB, BorderLayout.SOUTH);
		appB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				apply();
			}
		});
	}
	
	void setSettingEnabled(boolean b) {
		for (int i=0; i<compsToDisable.size(); ++i)
			compsToDisable.get(i).setEnabled(b);
	}
}

class OneItemProbBoxes {
	JCheckBox[] cbs = new JCheckBox[4];
	
	public OneItemProbBoxes(Item item) {
		int selIndex = AllTheItems.getWeightIndex(item);
		for (int i=0; i<4; ++i) {
			cbs[i] = new JCheckBox();
			
			// action listener: we can only select boxes, not deselect
			final int j = i;
			cbs[i].addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						select(j);
					}
				}
			);
			// default selection
			if (i == selIndex) cbs[i].setSelected(true);
		}
	}
	
	// select an item, and unselects the others
	private void select(int index) {
		cbs[index].setSelected(true);
		for (int i=0; i<cbs.length; ++i) {
			if (index != i)
				cbs[i].setSelected(false);
		}
	}
	
	public int getSelectedWeight() {
		if (cbs[0].isSelected()) return Constants.ITEM_NULL_PROB_WEIGHT;
		if (cbs[1].isSelected()) return Constants.ITEM_LOW_PROB_WEIGHT;
		if (cbs[2].isSelected()) return Constants.ITEM_MID_PROB_WEIGHT;
		if (cbs[3].isSelected()) return Constants.ITEM_HIGH_PROB_WEIGHT;
		
		return 0;
	}
	
	public JCheckBox[] getCBs() {
		return cbs;
	}
}

