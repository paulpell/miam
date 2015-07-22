package org.paulpell.miam.gui.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.paulpell.miam.logic.Globals;


@SuppressWarnings("serial")
public class GeneralSettingsSubPanel extends JPanel
{
	final JLabel FPSLabel_;
	final JSlider FPSSlider_;

	public GeneralSettingsSubPanel()
	{
		GridBagLayout layout = new GridBagLayout(); 
		setLayout(layout);
		
		GridBagConstraints endLineConstr = new GridBagConstraints();
		endLineConstr.gridwidth = GridBagConstraints.REMAINDER;
		
		final int gapx = 15;
		
		// ******************** FPS
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, gapx);// 15 to give space for number
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		//c.fill = GridBagConstraints.HORIZONTAL;
		FPSLabel_ = new JLabel();
		FPSLabel_.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		add(FPSLabel_, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		JLabel dummy = new JLabel();
		dummy.setMinimumSize(new Dimension(15, 10));
		//add(dummy, c);
		
		FPSSlider_ = new JSlider(Globals.FPS_MIN, Globals.FPS_MAX, Globals.FPS);
		FPSSlider_.setPaintTicks(true);
		FPSSlider_.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		FPSSlider_.addChangeListener(new ChangeListener()
		{	
			@Override
			public void stateChanged(ChangeEvent e)
			{
				adjustFPS();
			}
		});
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
		add(FPSSlider_, c); 
		
		
		// ******************* classic mode
		JLabel clModeLabel = new JLabel("Classic mode (square):");
		clModeLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, gapx);
		add(clModeLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		JLabel dummy2 = new JLabel();
		dummy2.setMinimumSize(new Dimension(15, 10));
		//add(dummy2, c);
		
		final JCheckBox classicModeCB = new JCheckBox();
		classicModeCB.setSelected(Globals.USE_CLASSIC_SNAKE);
		classicModeCB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Globals.USE_CLASSIC_SNAKE = classicModeCB.isSelected();
			}
		});
		classicModeCB.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		add(classicModeCB, c);
		
		adjustFPS();
	}
	
	private void adjustFPS()
	{
		int fps = FPSSlider_.getValue();
		Globals.FPS = fps;
		FPSLabel_.setText("FPS ["+fps+"]:");
		//FPSPanel_.setMinimumSize(minFPSPanelSize_);
	}
}
