package org.paulpell.miam.gui.net;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.*;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.paulpell.miam.gui.AbstractDisplayPanel;
import org.paulpell.miam.gui.LevelListPanel;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.draw.items.AllTheItems;
import org.paulpell.miam.logic.levels.LevelChoiceInfo;

@SuppressWarnings("serial")
public class OnlinePlayersPanel
	extends AbstractDisplayPanel
{

	Control control_;
	
	JButton leaveServerButton_;
	
	JCheckBox classicCheckBox_;
	JComboBox <String> snakesNumberBox_;
	LevelListPanel levelPanel_;
	
	JList <String> playersList_;
	DefaultListModel <String> playersListModel_;
	JList <String> clientsList_;
	DefaultListModel <String> clientsListModel_;
	JButton addPlayerButton_;
	JTextArea chatArea_;
	JTextField chatMsgField_;
	
	JButton startButton_;
	boolean startedGameMyself_;
	
	
	// this list contains the components to disable when slave
	ArrayList <JComponent> hostingComponents_ = new ArrayList <JComponent> ();
	

	public OnlinePlayersPanel(Control control, JFrame parent)
	{
		super("Online game", parent);
		
		control_ = control;
		
		initPlayersPanel();
		
		startedGameMyself_ = false;
	}
	
	// return false if we can't close
	@Override
	public boolean canRemovePanel()
	{
		if ( control_.isHosting() && ! startedGameMyself_ )
		{
			String msg = "The server will be stopped, are you sure?";
			final int yes = JOptionPane.YES_OPTION;
			int answer = JOptionPane.showConfirmDialog(
					this, msg, "Stop server?", JOptionPane.YES_NO_OPTION);
			if (yes == answer)
				control_.stopServer();
			return yes == answer ;
		}
		return true;
	}

	@Override
	public void displayMessage(String message)
	{
		chatArea_.append(message + "\n");
	}

	private void initPlayersPanel()
	{
		final int CHAT_COLS = 30;
		final int CHAT_ROWS = 13;
		
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] {20, 70, 20};
		layout.columnWidths = new int[] {70, 100};
		setLayout(layout);


		GridBagConstraints constr;
		
		
		// on top, stop/leave server and some master game parameters
		JPanel topPanel = new JPanel();
		GridBagLayout layoutTop = new GridBagLayout();
		layout.columnWidths = new int[] {150};
		topPanel.setLayout(layoutTop);
		leaveServerButton_ = new JButton("Leave server");
		leaveServerButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				control_.leaveServer();
			}
		});
		
		constr = new GridBagConstraints();
		constr.insets = new Insets(3, 3, 3, 13); // 13 pixels free on the right =)
		topPanel.add(leaveServerButton_, constr);
		
		// now some settings in the top bar
		// classic mode?
		constr = new GridBagConstraints();
		constr.gridx = 1;
		constr.insets = new Insets(3, 3, 3, 3);
		topPanel.add(new JLabel("Classic"), constr);
		classicCheckBox_ = new JCheckBox();
		constr = new GridBagConstraints();
		constr.gridx = 2;
		constr.insets = new Insets(3, 3, 3, 13);
		topPanel.add(classicCheckBox_, constr);
		
		// numbers of snake
		constr = new GridBagConstraints();
		constr.gridx = 3;
		constr.insets = new Insets(3, 3, 3, 3);
		topPanel.add(new JLabel("Snakes"), constr);
		snakesNumberBox_ = new JComboBox <String> (new String[]{"2","3","4"});
		constr = new GridBagConstraints();
		constr.gridx = 4;
		constr.insets = new Insets(3, 3, 3, 13);
		topPanel.add(snakesNumberBox_, constr);
		
		// level to play
		LevelListPanel.Orientation o = LevelListPanel.Orientation.HORIZONTAL;
		levelPanel_ = new LevelListPanel(o);
		constr = new GridBagConstraints();
		constr.gridx = 5;
		constr.insets = new Insets(3, 3, 3, 13);
		topPanel.add(levelPanel_, constr);
		
		
		

		constr = new GridBagConstraints();
		constr.gridwidth = GridBagConstraints.REMAINDER;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		add(topPanel, constr);
		
		
		// left: clients and players list
		JPanel playerListPanel = new JPanel();
		GridBagLayout playerListLayout = new GridBagLayout();
		playerListLayout.rowHeights = new int[] {20, 60, 20, 60};
		playerListLayout.columnWidths = new int[] {80};
		playerListPanel.setLayout(playerListLayout);
		

		// clients
		constr = new GridBagConstraints();
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(new JLabel("Clients:"), constr);
		
		clientsListModel_ = new DefaultListModel <String> ();
		JList <String> clientsList_ = new JList <String> (clientsListModel_);
		constr = new GridBagConstraints();
		constr.gridy = 1;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 10, 3);
		playerListPanel.add(clientsList_, constr);
		
		// players
		constr = new GridBagConstraints();
		constr.gridy = 2;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(new JLabel("Players:"), constr);
		
		playersListModel_ = new DefaultListModel <String> ();
		playersList_ = new JList <String> (playersListModel_);
		constr = new GridBagConstraints();
		constr.gridy = 3;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(playersList_, constr);
		
		/*addPlayerButton_ = new JButton("Add..");
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 4;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(addPlayerButton_, constr);*/
		

		constr = new GridBagConstraints();
		constr.gridy = 1;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(3, 13, 3, 3); // 13 pixels on the left
		add(playerListPanel, constr);
		

		
		
		// right: chat area
		JPanel chatPanel = new JPanel();
		GridBagLayout chatLayout = new GridBagLayout();
		chatLayout.rowHeights = new int[] {60, 20};
		chatPanel.setLayout(chatLayout);
		chatArea_ = new JTextArea(CHAT_ROWS, CHAT_COLS);
		chatArea_.setEditable(false);
		JScrollPane chatScroll = new JScrollPane(chatArea_);
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		chatPanel.add(chatScroll, constr);

		JPanel sendChatMsgPanel = new JPanel(new FlowLayout());
		chatMsgField_ = new JTextField(25);
		chatMsgField_.requestFocusInWindow();
		chatMsgField_.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == VK_ENTER)
					sendChatMsg();
			}
		});
		sendChatMsgPanel.add(chatMsgField_);
		JButton chatSendButton = new JButton("send");
		chatSendButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sendChatMsg();
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
		Icon lightningIcon = AllTheItems.items[AllTheItems.INDEX_LIGHTNING].getImageIcon();
		startButton_ = new JButton("Start!", lightningIcon);
		startButton_.setMnemonic(VK_S);
		startButton_.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				startGame();
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
		hostingComponents_.add(classicCheckBox_);
		hostingComponents_.add(snakesNumberBox_);
	}
	
	private void startGame()
	{
		startedGameMyself_ = true;
		String lname = levelPanel_.getLevelName();
		int sNo = getSnakesNumber();
		LevelChoiceInfo linfo = new LevelChoiceInfo(lname, sNo);
		control_.startMasterGame(linfo);
	}
	
	private void sendChatMsg()
	{
		String msg = chatMsgField_.getText();
		if ( ! msg.equals("") )
		{
			control_.sendChatMessage(chatMsgField_.getText());
			chatMsgField_.setText("");
		}
	}

	public void prepare(boolean isHosting)
	{
		startedGameMyself_ = false;
		if (isHosting)
		{
			leaveServerButton_.setText("Stop hosting");
			leaveServerButton_.setMnemonic(VK_H);
		}
		else
		{
			leaveServerButton_.setText("Leave server");
			leaveServerButton_.setMnemonic(VK_L);
		}
		
		for (JComponent c: hostingComponents_)
			c.setEnabled(isHosting);
	}
	
	public void setConnectedClients(String[] clients)
	{
		clientsListModel_.removeAllElements();
		for (int i=0; i<clients.length; ++i)
			clientsListModel_.add(i, clients[i]);
	}
	
	public void setPlayers(String[] players)
	{
		playersListModel_.removeAllElements();
		for (int i=0; i<players.length; ++i)
			playersListModel_.add(i, players[i]);
	}
	
	private int getSnakesNumber()
	{
		try
		{
			return Integer.parseInt((String)snakesNumberBox_.getSelectedItem());
		}
		catch (NumberFormatException e)
		{
			assert false : "Only numbers; so should be parsed correctly";
			return 0;
		}
	}
	
	public void reset()
	{
		chatArea_.setText("");
		chatMsgField_.setText("");
		setPlayers(new String[]{});
		setConnectedClients(new String[]{});
	}
}
