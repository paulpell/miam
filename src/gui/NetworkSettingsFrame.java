package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import logic.Globals;

public class NetworkSettingsFrame extends JFrame {
	
	//JCheckBox serverBox = new JCheckBox();
	JButton serverButton;
	JTextArea textArea;
	
	public NetworkSettingsFrame() {
		super("Network settings");
		GridBagLayout layout = new GridBagLayout(); 
		setLayout(layout);
		GridBagConstraints constrRel = new GridBagConstraints();
		constrRel.gridx = GridBagConstraints.RELATIVE;
		GridBagConstraints constrRemain = new GridBagConstraints();
		constrRemain.gridx = GridBagConstraints.REMAINDER;
		
		serverButton = new JButton("Host a game");
		serverButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Globals.control.hostGame();	
			}
		});
		add(serverButton);
		layout.setConstraints(serverButton, constrRemain);
		
		textArea = new JTextArea(40, 60);
		add(textArea);
		layout.setConstraints(textArea, constrRemain);
		
		pack();
		setVisible(true);
		
	}

}
