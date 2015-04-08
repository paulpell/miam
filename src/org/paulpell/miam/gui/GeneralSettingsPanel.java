package org.paulpell.miam.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.paulpell.miam.logic.Globals;


public class GeneralSettingsPanel extends JPanel
{

	public GeneralSettingsPanel()
	{
		GridBagLayout layout = new GridBagLayout(); 
		setLayout(layout);
		
		GridBagConstraints endLineConstr = new GridBagConstraints();
		endLineConstr.gridwidth = GridBagConstraints.REMAINDER;
		
		// ******************** FPS
		JPanel FPSPanel = new JPanel(); 
		FPSPanel.add(new JLabel("FPS:"));
		final JSlider FPSSlider = new JSlider(5, 30, 25);
		FPSSlider.setPaintTicks(true);
		FPSSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Globals.FPS = FPSSlider.getValue();
			}
		});
		FPSPanel.add(FPSSlider);
		layout.addLayoutComponent(FPSPanel, endLineConstr);
		add(FPSPanel);
		
		
		// ******************* classic mode
		JPanel classicModePanel = new JPanel();
		classicModePanel.add(new JLabel("Classic mode (square):"));
		final JCheckBox classicModeCB = new JCheckBox();
		classicModeCB.setSelected(Globals.USE_CLASSIC_SNAKE);
		classicModeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Globals.USE_CLASSIC_SNAKE = classicModeCB.isSelected();
			}
		});
		classicModePanel.add(classicModeCB);
		layout.addLayoutComponent(classicModePanel, endLineConstr);
		add(classicModePanel);
		
			
	}
}
