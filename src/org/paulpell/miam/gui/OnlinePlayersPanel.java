package org.paulpell.miam.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

import org.paulpell.miam.logic.Control;

public class OnlinePlayersPanel extends AbstractDisplayPanel
{

	Control control_;
	
	JButton leaveServerButton_;
	JList <String> playersList_;
	ListModel <String> playersListModel_;
	JTextArea chatArea_;
	JTextField chatMsgField_;
	JButton startButton_;

	// this list contains the components to disble when slave
	ArrayList <JComponent> hostingComponents_ = new ArrayList <JComponent> ();
	

	public OnlinePlayersPanel(Control control)
	{
		super("Online game");
		
		control_ = control;
		
		initPlayersPanel();
	}

	@Override
	public void displayMessage(String message)
	{
		chatArea_.append(message + "\n");
	}

	private void initPlayersPanel()
	{
		final int CHAT_COLS = 37;
		final int CHAT_ROWS = 13;
		
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] {20, 70, 20};
		layout.columnWidths = new int[] {70, 100};
		setLayout(layout);


		GridBagConstraints constr;
		
		
		// leave the server, return to servers panel
		leaveServerButton_ = new JButton("Leave server");
		leaveServerButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				control_.leaveServer();
			}
		});
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		add(leaveServerButton_, constr);
		
		
		
		
		// left: player list
		JPanel playerListPanel = new JPanel();
		GridBagLayout playerListLayout = new GridBagLayout();
		playerListLayout.rowHeights = new int[] {20, 60};
		playerListLayout.columnWidths = new int[] {80};
		playerListPanel.setLayout(playerListLayout);
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(new JLabel("Players:"), constr);
		
		playersListModel_ = 
				new DefaultComboBoxModel <String> (new String[]{"me", "other", "foo"});
		playersList_ = new JList <String> (playersListModel_);
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 1;
		constr.fill = GridBagConstraints.BOTH;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(playersList_, constr);

		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 1;
		constr.fill = GridBagConstraints.BOTH;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		add(playerListPanel, constr);
		

		
		
		// right: chat area
		JPanel chatPanel = new JPanel();
		GridBagLayout chatLayout = new GridBagLayout();
		chatLayout.rowHeights = new int[] {60, 20};
		chatPanel.setLayout(chatLayout);
		chatArea_ = new JTextArea(CHAT_ROWS, CHAT_COLS);
		JScrollPane chatScroll = new JScrollPane(chatArea_);
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		chatPanel.add(chatScroll, constr);

		JPanel sendChatMsgPanel = new JPanel(new FlowLayout());
		chatMsgField_ = new JTextField(35);
		sendChatMsgPanel.add(chatMsgField_);
		JButton chatSendButton = new JButton("send");
		chatSendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control_.sendChatMessage(chatMsgField_.getText());
			}
		});
		sendChatMsgPanel.add(chatSendButton);
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 1;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		chatPanel.add(sendChatMsgPanel, constr);

		constr = new GridBagConstraints();
		constr.gridx = 1;
		constr.gridy = 1;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		add(chatPanel, constr);
		
		
		// bottom: start button
		startButton_ = new JButton("Start!");
		startButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				control_.startMasterGame();
			}
		});

		constr = new GridBagConstraints();
		constr.gridy = 2;
		constr.gridwidth = 2;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		add(startButton_, constr);
		doLayout();

		hostingComponents_.add(startButton_);
	}

	public void setIsHosting(boolean isHosting)
	{
		if (isHosting)
			leaveServerButton_.setText("Stop hosting");
		else
			leaveServerButton_.setText("Leave server");
		for (JComponent c: hostingComponents_)
			c.setEnabled(isHosting);
	}
	
}
