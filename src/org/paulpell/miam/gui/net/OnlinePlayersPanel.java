package org.paulpell.miam.gui.net;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Vector;

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
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.ONLINE_STATE;
import org.paulpell.miam.logic.draw.items.AllTheItems;
import org.paulpell.miam.logic.levels.LevelChoiceInfo;
import org.paulpell.miam.logic.players.PlayersManager;
import org.paulpell.miam.net.NetworkControl;

import org.paulpell.miam.gui.playertab.PlayerChoiceTable;

@SuppressWarnings("serial")
public class OnlinePlayersPanel
	extends AbstractDisplayPanel
	implements KeyListener
{

	Control control_;
	NetworkControl netControl_;
	PlayersManager playersMgr_ = null;
	
	JButton leaveServerButton_;
	
	JCheckBox classicCheckBox_;
	JComboBox <Integer> snakesNumberBox_;
	LevelListPanel levelPanel_;
	
	PlayerChoiceTable playerChoiceTable_;
	JList <String> clientsList_;
	DefaultListModel <String> clientsListModel_;
	JTextArea chatArea_;
	JTextField chatMsgField_;
	
	JButton startButton_;
	boolean startedGameMyself_;
	
	boolean isAskingUserConfirmation_; // used in canRemovePanel to avoid duplicate msgbox
	
	
	// this list contains the components to disable when slave
	ArrayList <JComponent> hostingComponents_ = new ArrayList <JComponent> ();
	

	public OnlinePlayersPanel(Control control, JFrame parent)
	{
		super("Online game", parent);
		
		control_ = control;
		
		initPlayersPanel();
		
		startedGameMyself_ = false;
		isAskingUserConfirmation_ = false;
	}
	
	public void setPlayersManager(PlayersManager playersMgr)
	{
		playersMgr_ = playersMgr;
		playerChoiceTable_.setPlayersManager(playersMgr_);
	}
	
	public void setNetworkControl(NetworkControl netControl)
	{
		netControl_ = netControl;
	}
	
	// return false if we can't close
	@Override
	public boolean canRemovePanel()
	{
		playerChoiceTable_.closePopup();
		
		if ( netControl_.isHosting() && ! startedGameMyself_ )
		{
			isAskingUserConfirmation_ = true;
			String msg = "The server will be stopped, are you sure?";
			int answer = JOptionPane.showConfirmDialog(
					this, msg, "Stop server?", JOptionPane.YES_NO_OPTION);
			isAskingUserConfirmation_ = false;
			if (answer == JOptionPane.YES_OPTION) {
				netControl_.stopServer();
				playerChoiceTable_.closePopup();
				return true;
			}

			return false;
		}
		return true;
	}

	@Override
	public void displayMessage(String message, boolean immediately)
	{
		chatArea_.append(message + "\n");
	}

	private void initPlayersPanel()
	{
		
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] {20, 70, 20};
		layout.columnWidths = new int[] {70, 100};
		setLayout(layout);


		GridBagConstraints constr;
		
		
		// on top, stop/leave server and some master game parameters
		
		JPanel topPanel = makeTopPanel();

		constr = new GridBagConstraints();
		constr.gridwidth = GridBagConstraints.REMAINDER;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		add(topPanel, constr);
		
		
		/// left : lists of clients and players
		constr = new GridBagConstraints();
		constr.gridy = 1;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(3, 13, 3, 3); // 13 pixels on the left
		add(makeClientsPlayersPanel(), constr);
		
		
		// right: chat area
		JPanel chatPanel = makeChatPanel();
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

		
		// to be enabled only when hosting
		hostingComponents_.add(startButton_);
		hostingComponents_.add(classicCheckBox_);
		hostingComponents_.add(snakesNumberBox_);
		hostingComponents_.add(levelPanel_);
	}
	
	private JPanel makeChatPanel()
	{
		final int CHAT_COLS = 30;
		final int CHAT_ROWS = 13;
		
		JPanel chatPanel = new JPanel();
		GridBagLayout chatLayout = new GridBagLayout();
		chatLayout.rowHeights = new int[] {60, 20};
		chatPanel.setLayout(chatLayout);
		chatArea_ = new JTextArea(CHAT_ROWS, CHAT_COLS);
		chatArea_.setEditable(false);
		JScrollPane chatScroll = new JScrollPane(chatArea_);

		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		chatPanel.add(chatScroll, constr);

		JPanel sendChatMsgPanel = new JPanel(new FlowLayout());
		chatMsgField_ = new JTextField(25);
		chatMsgField_.requestFocusInWindow();
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
		
		return chatPanel;
	}
	
	private JPanel makeClientsPlayersPanel()
	{

		// left: clients and players list
		JPanel playerListPanel = new JPanel();
		GridBagLayout playerListLayout = new GridBagLayout();
		playerListLayout.rowHeights = new int[] {20, 60, 20, 60};
		playerListLayout.columnWidths = new int[] {80};
		playerListPanel.setLayout(playerListLayout);
		

		// clients
		GridBagConstraints constr = new GridBagConstraints();
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(new JLabel("Clients:"), constr);
		
		clientsListModel_ = new DefaultListModel <String> ();
		JList <String> clientsList = new JList <String> (clientsListModel_);
		constr = new GridBagConstraints();
		constr.gridy = 1;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 10, 3);
		playerListPanel.add(clientsList, constr);
		
		// players
		constr = new GridBagConstraints();
		constr.gridy = 2;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(new JLabel("Players:"), constr);

		constr = new GridBagConstraints();
		constr.gridy = 3;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		playerChoiceTable_ = new PlayerChoiceTable();
		playerListPanel.add(playerChoiceTable_, constr);

		constr = new GridBagConstraints();
		constr.gridy = 4;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		playerListPanel.add(new JLabel("Test label"), constr);

		return playerListPanel;
	}
	
	private JPanel makeTopPanel ()
	{
		JPanel topPanel = new JPanel();
		GridBagLayout layoutTop = new GridBagLayout();
		GridBagConstraints constr;
		topPanel.setLayout(layoutTop);
		leaveServerButton_ = new JButton("Leave server");
		leaveServerButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				netControl_.leaveServer();
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
		snakesNumberBox_ = new JComboBox <Integer> (new Integer[]{2, 3, 4});
		snakesNumberBox_.addItemListener( new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if ( ItemEvent.SELECTED == e.getStateChange() )
					snakesNumberUpdated();
			}
		});
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
		
		return topPanel;
	}
	
	//public void init
	
	private void startGame()
	{
		int numPlayers = playersMgr_.getPlayerList().size();
		if (numPlayers == 0) {
			displayMessage("No players for the game!", true);
			return;
		}
		//int sNo = getNumberSeats();
		int nSnakes = playersMgr_.getNumberSnakes();
		startedGameMyself_ = true;
		String lname = levelPanel_.getLevelName();
		LevelChoiceInfo linfo = new LevelChoiceInfo(lname, nSnakes);
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
	
	public void setConnectedClients(Vector <String> clientNames)
	{
		clientsListModel_.removeAllElements();
		for (int i=0; i<clientNames.size(); ++i)
			clientsListModel_.add(i, clientNames.get(i));
	}
	
	public void setMaxPlayerNumReached(boolean b)
	{
		/*if (b && showPopup_)
			hidePopup();
		addPlayerButton_.setEnabled(b);*/
	}
	
	
	private void snakesNumberUpdated()
	{
		assert netControl_.isHosting() : "Only server can change this";
		int sNo = getNumberSeats();
		playersMgr_.setNumberSeats(sNo);
		playerChoiceTable_.repaint();
	}
	
	public int getNumberSeats()
	{
		return (Integer)snakesNumberBox_.getSelectedItem();
		/*try
		{
			return Integer.parseInt((String)snakesNumberBox_.getSelectedItem());
		}
		catch (NumberFormatException e)
		{
			assert false : "Only numbers; so should be parsed correctly";
			return 0;
		}*/
	}
	
	public void reset(int defaultPlayerNumber, ONLINE_STATE onlineState)
	{
		chatArea_.setText("");
		chatMsgField_.setText("");
		setConnectedClients(new Vector<String>());
		snakesNumberBox_.setSelectedItem(defaultPlayerNumber);

		startedGameMyself_ = false;
		boolean isHosting = onlineState == ONLINE_STATE.SERVER;
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
	
	// we only want to use enter and escape
	@Override
	public KeyListener getCurrentKeyListener(KeyEvent e)
	{
		if (isAskingUserConfirmation_)
			return null;
		
		//else if (playerChoiceTable_.isAddingPlayer())
		//	return playerChoiceTable_.;
		
		int kc = e.getKeyCode();
		if (kc == KeyEvent.VK_ENTER
				|| 	kc == KeyEvent.VK_ESCAPE)
			return this;
		
		return null;	
	}

	@Override
	public void keyTyped(KeyEvent e)
	{}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int kc = e.getKeyCode();
		Log.logErr("Key in online players panel: " + kc);
		
		if (kc == KeyEvent.VK_ENTER)
		{
			//if ( showPopup_ )
			//	popupEnterPressed();
			//else if (chatMsgField_.hasFocus())

			if (playerChoiceTable_.isAddingPlayer())
				playerChoiceTable_.onPopupOK();
			if (chatMsgField_.hasFocus())
				sendChatMsg();
			//else if ( -1 != playerChoiceTable_.getEditingRow() )
			//	playerChoiceTable_.getCellEditor().stopCellEditing();
		}
		
		else if (kc == KeyEvent.VK_ESCAPE)
		{
			//if (showPopup_)
			//	hidePopup();
			//else if ( -1 != playerChoiceTable_.getEditingRow() )
			//	playerChoiceTable_.getCellEditor().cancelCellEditing();
			//else
			if ( playerChoiceTable_.isAddingPlayer() )
				playerChoiceTable_.closePopup();
			else if ( ! isAskingUserConfirmation_ )
				control_.showWelcomePanel();
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{}
}
