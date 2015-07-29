package org.paulpell.miam.gui.net;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;

import org.paulpell.miam.gui.AbstractDisplayPanel;
import org.paulpell.miam.logic.Control;

@SuppressWarnings("serial")
public class OnlineServersPanel extends AbstractDisplayPanel
{
	
	Control control_;
	

	// servers panel components
	JButton hostNewGameButton_;
	ListModel <String> hostsListModel_;
	JList <String> hostsList_;
	JTextField newHostField_;
	JButton newHostButton_;
	JButton joinButton_;
	JLabel msgArea_;
	
	ArrayList<String> hostNames_;

	public OnlineServersPanel(Control control, JFrame parent)
	{
		super("Hosting or joining a server", parent);
		control_ = control;
		
		hostNames_ = new ArrayList<String> ();
		
		initServersPanel();

		addHost("localhost"); // TODO : remove this
	}
	

	@Override
	public void displayMessage(String message)
	{
		msgArea_.setText(message);
	}
	

	private void hostGame()
	{
		control_.hostGame();
	}

	private void joinGame()
	{
		String host = hostsList_.getSelectedValue();
		control_.joinGame(host);
	}
	

	private void initServersPanel()
	{
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] {30, 25, 60, 30};
		setLayout(layout);

		GridBagConstraints constr;
		
		hostNewGameButton_ = new JButton("Host a game");
		hostNewGameButton_.setMnemonic(VK_H);
		hostNewGameButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				hostGame();
			}
		});
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(3, 3, 3, 3);
		add(hostNewGameButton_, constr);

		
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 1;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		add(new JLabel("Servers:"), constr);
		
		// empty model
		hostsListModel_ = 
				new DefaultComboBoxModel <String>(
						new String[]{});
		hostsList_ = new JList <String>();
		hostsList_.setModel(hostsListModel_);
		hostsList_.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if (arg0.getClickCount() >= 2)
					joinGame();
			}
		});
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 2;
		constr.gridheight = 2;
		constr.fill = GridBagConstraints.BOTH;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		add(hostsList_, constr);
		
		joinButton_ = new JButton("join");
		joinButton_.setMnemonic(VK_J);
		joinButton_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				joinGame();
			}
		});
		constr = new GridBagConstraints();
		constr.gridx = 1;
		constr.gridy = 2;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(3, 3, 3, 3);
		add(joinButton_, constr);
		
		int newHostTopInset = 10;

		newHostField_ = new JTextField(20);
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 4;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(newHostTopInset, 3, 3, 3);
		add(newHostField_, constr);
		
		newHostButton_ = new JButton("Add");
		newHostButton_.setMnemonic(VK_A);
		newHostButton_.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String host = newHostField_.getText();
				if (host != "")
				{
					addHost(host);
					newHostField_.setText("");
				}
			}
		});
		constr = new GridBagConstraints();
		constr.gridx = 1;
		constr.gridy = 4;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(newHostTopInset, 3, 3, 3);
		add(newHostButton_, constr);
		
		
		msgArea_ = new JLabel("Not connected");
		msgArea_.setForeground(new Color(190, 23, 21));
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 5;
		constr.gridwidth = GridBagConstraints.REMAINDER;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.WEST;
		constr.insets = new Insets(newHostTopInset, 3, 3, 3);
		add(msgArea_, constr);
		
		doLayout();
		
	}
	
	private void addHost(String host)
	{
		hostNames_.add(host);
		String[] names = new String[]{};
		hostsListModel_ = 
				new DefaultComboBoxModel <String>(
						hostNames_.toArray(names));
		hostsList_.setModel(hostsListModel_);
		if (hostsList_.getSelectedIndex() == -1
				&& hostsListModel_.getSize() > 0)
			hostsList_.setSelectedIndex(0);
	}
	
	public void reset()
	{
		msgArea_.setText("");
	}

}
