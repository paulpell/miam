package org.paulpell.miam.gui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.items.AllTheItems;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.ItemFactory;


@SuppressWarnings("serial")
public class ItemSettingsSubPanel
	extends Box
	implements ActionListener
{

	// this list stores the buttons to disable when score_only mode is activated:
	// we don't want to use the probabilities per item.
	LinkedList<JComponent> compsToDisable = new LinkedList<JComponent>();

	final JCheckBox itemsOnlyCB = new JCheckBox();
	OneItemProbBoxes[] itemBoxes;
	JLabel labelOccurence_;
	JSlider sliderOccurence_;
	
	
	public ItemSettingsSubPanel()
	{

		super(BoxLayout.Y_AXIS);
		
		addItemsOnly();
		addItemsProbs();
		addItemOccurences();

		//addApplyButton();
		
		setItemSettingsEnabled(!Globals.SCORE_ITEMS_ONLY);
	}
	

	void apply()
	{
		Globals.SCORE_ITEMS_ONLY = itemsOnlyCB.isSelected();
		String weightsDbg = "";
		if (!Globals.SCORE_ITEMS_ONLY) {
			int weights[] = new int[itemBoxes.length];
			for (int iItemType=0; iItemType<weights.length; ++iItemType) {
				weights[iItemType] = itemBoxes[iItemType].getSelectedWeight();
				weightsDbg += "        " + AllTheItems.getItems()[iItemType].getTextDescription() + " = " + weights[iItemType] + "\n";
			}
			AllTheItems.setProbabilities(weights);
		}
		// handle items creation frequency
		int occurIndex = sliderOccurence_.getValue();
		ItemFactory.computeItemsCreationOccurence(occurIndex);
		long occurMax = ItemFactory.TIME_BETWEEN_EXTRA_ITEMS_MAX;
		long occurMin = ItemFactory.TIME_BETWEEN_EXTRA_ITEMS_MIN;
		String occurStr = occurMin + "-" + occurMax + " ms";
		labelOccurence_.setText("Items occurence: " + occurStr);
		
		Log.logMsg(
				"Settings changed\n:" +
				"    Score_only=" + Globals.SCORE_ITEMS_ONLY + "\n" +
				"    Item probabilities:" + "\n" +
				weightsDbg +
				"    Occurences = " + occurStr
				);
	}
	
	
	// adds a checkbox to set whether only score_ items are used
	void addItemsOnly()
	{
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		JPanel scoreItemsOnlyPanel = new JPanel(layout);
		scoreItemsOnlyPanel.add(new JLabel("Score items only"));
		itemsOnlyCB.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
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
		Item[] items = AllTheItems.getItems();
		
		JPanel itemProbsPanel = new JPanel();
		itemProbsPanel.setLayout(new GridLayout(items.length + 1, 5));
		
		itemBoxes = new OneItemProbBoxes[items.length];
		
		// header of the table
		itemProbsPanel.add(new JLabel(""));
		itemProbsPanel.add(new JLabel("null"));
		itemProbsPanel.add(new JLabel("low"));
		itemProbsPanel.add(new JLabel("mid"));
		itemProbsPanel.add(new JLabel("high"));
		
		for (int iItemType=0; iItemType<items.length; ++iItemType) {
			//itemProbsPanel.add(new JLabel(is[i].getTextDescription()));
			JLabel label = new JLabel(items[iItemType].getImageIcon());
			label.setToolTipText(items[iItemType].getTextDescription());
			itemProbsPanel.add(label);
			itemBoxes[iItemType] = new OneItemProbBoxes(items[iItemType], this);
			JCheckBox cbs[] = itemBoxes[iItemType].getCBs();
			for (int iItemCB=0; iItemCB<cbs.length; ++iItemCB) {
				compsToDisable.add(cbs[iItemCB]);
				itemProbsPanel.add(cbs[iItemCB]);
				cbs[iItemCB].addActionListener(this);
			}
		}
		//compsToDisable.add(itemProbsPanel);
		add(itemProbsPanel);//, BorderLayout.CENTER);
	}
	
	/*void addApplyButton() {
		JButton appB = new JButton("Apply");
		add(appB);//, BorderLayout.SOUTH);
		appB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				apply();
			}
		});
	}*/
	
	void setItemSettingsEnabled(boolean b)
	{
		for (int i=0; i<compsToDisable.size(); ++i)
			compsToDisable.get(i).setEnabled(b);
		apply();
	}

	private void addItemOccurences()
	{
		JPanel panel = new JPanel(new GridLayout());
		GridBagConstraints constr = new GridBagConstraints();
		//int maxVal = (int)Constants.DEFAULT_TIME_BETWEEN_ITEMS_MAX;
		//int minVal = (int)Constants.DEFAULT_TIME_BETWEEN_ITEMS_MIN;
		labelOccurence_ = new JLabel("Items occurence: ");
		panel.add(labelOccurence_, constr);
		
		int numVals = Constants.TIMES_BETWEEN_ITEMS_OCCURENCES.length;
		int val = Constants.DEFAULT_TIME_BETWEEN_ITEMS_OCCURENCE_INDEX;
		sliderOccurence_ = new JSlider(1, numVals, val);
		sliderOccurence_.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e) {
				apply();
			}
		});
		constr.gridx = 1;
		panel.add(sliderOccurence_, constr);
		add(panel);
		
		/*Box occBox = new Box(BoxLayout.Y_AXIS);
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
					Globals.TIME_BETWEEN_EXTRA_ITEMS_MAX = minVal;
				}
					Globals.TIME_BETWEEN_EXTRA_ITEMS_MAX = minVal;
				Globals.TIME_BETWEEN_EXTRA_ITEMS_MIN = minVal;
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
					Globals.TIME_BETWEEN_EXTRA_ITEMS_MIN = maxVal;
				}
				Globals.TIME_BETWEEN_EXTRA_ITEMS_MAX = maxVal;
				labMax.setText("max [" + maxVal + " ms]: ");
			}
		});*/
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		apply();
	}
}

class OneItemProbBoxes {
	JCheckBox[] cbs = new JCheckBox[4];
	
	public OneItemProbBoxes(Item item, final ItemSettingsSubPanel itemsPanel) {
		int selIndex = AllTheItems.getWeightIndex(item);
		for (int i=0; i<4; ++i) {
			cbs[i] = new JCheckBox();
			
			// action listener: we can only select boxes, not deselect
			final int j = i;
			cbs[i].addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						select(j);
						itemsPanel.apply();
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

