package org.paulpell.miam.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.draw.items.AllTheItems;
import org.paulpell.miam.logic.draw.items.Item;


@SuppressWarnings("serial")
public class ItemSettingsPanel extends Box {

	// this list stores the buttons to disable when score_only mode is activated:
	// we don't want to use the probabilities per item.
	LinkedList<JComponent> compsToDisable = new LinkedList<JComponent>();

	final JCheckBox itemsOnlyCB = new JCheckBox();
	OneItemProbBoxes[] itemBoxes;
	
	
	public ItemSettingsPanel() {

		super(BoxLayout.Y_AXIS);
		
		
		addItemsOnly();
		addItemsProbs();
		addItemOccurences();

		addApplyButton();
		
		
		setItemSettingsEnabled(!Globals.SCORE_ITEMS_ONLY);
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
	
	
	// adds a checkbox to set whether only score_ items are used
	void addItemsOnly() {
		JPanel scoreItemsOnlyPanel = new JPanel();
		scoreItemsOnlyPanel.add(new JLabel("Score items only"));
		itemsOnlyCB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				boolean b = ! itemsOnlyCB.isSelected();
				setItemSettingsEnabled(b);
			}
		});
		itemsOnlyCB.setSelected(Globals.SCORE_ITEMS_ONLY);
		scoreItemsOnlyPanel.add(itemsOnlyCB);
		add(scoreItemsOnlyPanel);//, BorderLayout.NORTH);
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
		add(itemProbsPanel);//, BorderLayout.CENTER);
	}
	
	void addApplyButton() {
		JButton appB = new JButton("Apply");
		add(appB);//, BorderLayout.SOUTH);
		appB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				apply();
			}
		});
	}
	
	void setItemSettingsEnabled(boolean b) {
		for (int i=0; i<compsToDisable.size(); ++i)
			compsToDisable.get(i).setEnabled(b);
	}

	private void addItemOccurences() {
		Box occBox = new Box(BoxLayout.Y_AXIS);
		JLabel title = new JLabel("Items occurence");
		occBox.add(title);
		int maxVal = (int)Constants.DEFAULT_TIME_BETWEEN_ITEMS_MAX;
		int minVal = (int)Constants.DEFAULT_TIME_BETWEEN_ITEMS_MIN;
		final JLabel labMin = new JLabel("min [" + minVal + " ms]: ");
		final JSlider slideMin = new JSlider(100, 10000, minVal);
		final JLabel labMax = new JLabel("max [" + maxVal + " ms]: ");
		final JSlider slideMax = new JSlider(100, 10000, maxVal);
		Box minBox = Box.createHorizontalBox();
		minBox.add(labMin);
		minBox.add(slideMin);
		occBox.add(minBox);
		Box maxBox = Box.createHorizontalBox();
		maxBox.add(labMax);
		maxBox.add(slideMax);
		occBox.add(maxBox);
		add(occBox);
		
		// create the two change listeners for the sliders
		slideMin.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e) {
				int minVal = slideMin.getValue();
				int maxVal = slideMax.getValue();
				if (minVal > maxVal)
				{
					slideMax.setValue(minVal);
					Globals.TIME_BETWEEN_ITEMS_MAX = minVal;
				}
				Globals.TIME_BETWEEN_ITEMS_MIN = minVal;
				labMin.setText("max [" + minVal + " ms]: ");
			}
		});
		slideMax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e)
			{
				int minVal = slideMin.getValue();
				int maxVal = slideMax.getValue();
				if (maxVal < minVal)
				{
					slideMin.setValue(maxVal);
					Globals.TIME_BETWEEN_ITEMS_MIN = maxVal;
				}
				Globals.TIME_BETWEEN_ITEMS_MAX = maxVal;
				labMax.setText("max [" + maxVal + " ms]: ");
			}
		});
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

