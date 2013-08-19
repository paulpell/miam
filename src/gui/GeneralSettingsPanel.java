package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import logic.Globals;

public class GeneralSettingsPanel extends JPanel {

	public GeneralSettingsPanel() {
		
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
		add(classicModePanel);
		
			
	}
}
