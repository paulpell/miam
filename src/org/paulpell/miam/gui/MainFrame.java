package org.paulpell.miam.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class MainFrame
	extends JFrame
	implements KeyListener, WindowListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8026416994513756565L;

	Control control_;
	
	AbstractDisplayPanel currentPanel_; // reference to one of the following panels
	
	//NetworkSettingsPanel networkPanel_;
	GamePanel gamePanel_;
	SettingsPanel settingsPanel_;
	WelcomePanel welcomePanel_;
	
	OnlineServersPanel serversPanel_;
	OnlinePlayersPanel playersPanel_;
	
	private static String title_ = "Snakesss";
	
	private JPanel topPanel_ = null;
	private boolean isTopPanelVisible_ = false;
	
	public MainFrame(Control control)
	{

		super(title_);
		control_ = control;
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new MainKeyDispatcher(this));
		
		welcomePanel_ = new WelcomePanel("welcome :)");
		
		gamePanel_ = new GamePanel(control_);
		
		currentPanel_ = welcomePanel_;
		setNewPanel(currentPanel_);

		addKeyListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private JPanel createTopPanel()
	{
		JPanel panel = new JPanel();
		JButton gamePanelButton = new JButton("Back");
		gamePanelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showWelcomePanel();
			}
		});
		panel.add(gamePanelButton);
		panel.doLayout();
		panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(10, 10, 10)));
		return panel;
	}


	// we want to pass the key events to control when welcome and game panels are active 
	public boolean shouldGetKeyEvents()
	{
		return currentPanel_ == gamePanel_ || currentPanel_ == welcomePanel_;
	}


	public void showSettings()
	{
		if (null == settingsPanel_)
			settingsPanel_ = new SettingsPanel(control_);
		
		isTopPanelVisible_ = true;
		setNewPanel(settingsPanel_);
	}
	
	
	public void showServerSettings()
	{
		if (null == serversPanel_)
			serversPanel_ = new OnlineServersPanel(control_);
		
		isTopPanelVisible_ = true;
		setNewPanel(serversPanel_);
	}
	public void showPlayersSettings(boolean isHosting)
	{
		if (null == playersPanel_)
			playersPanel_ = new OnlinePlayersPanel(control_);
		
		playersPanel_.setIsHosting(isHosting);
		
		isTopPanelVisible_ = true;
		setNewPanel(playersPanel_);
	}
	public void showGamePanel()
	{
		isTopPanelVisible_ = false;

		gamePanel_.setPause(false);
		gamePanel_.setGameover(false);
		setNewPanel(gamePanel_);
	}
	public void showWelcomePanel()
	{
		isTopPanelVisible_ = false;
		setNewPanel(welcomePanel_);
	}
	
	private void setNewPanel(AbstractDisplayPanel panel)
	{
		remove(currentPanel_);
		currentPanel_.setVisible(false);
		currentPanel_ = panel;

		if (isTopPanelVisible_)
		{
			if (null == topPanel_)
				topPanel_ = createTopPanel();
			add(topPanel_, BorderLayout.NORTH);
		}
		else if (null != topPanel_)
			remove(topPanel_);

		add(currentPanel_);
		currentPanel_.setVisible(true);
		
		setMinimumSize(currentPanel_.getPreferredSize());
		
		pack();
		repaint();
	}
	
	
	public void paint(Graphics g)
	{
		if (isTopPanelVisible_)
			topPanel_.paint(g);
		currentPanel_.paint(g);
	}
	
	
	
	
	
	public void resetGameInfoPanel(Vector<Snake> v, Dimension gamePanelSize)
	{
		gamePanel_.resetForNewGame(v, gamePanelSize);
	}
	
	public void paintIsGameInPause(boolean inPause)
	{
		gamePanel_.setPause(inPause);
	}

	public void paintIsGameover(boolean gameover)
	{
		gamePanel_.setGameover(gameover);
	}
	
	public void displayMessage(String message)
	{
		if (null != currentPanel_)
			currentPanel_.displayMessage(message);
	}

	/* User keyboard interface ***********************************/

	public void keyPressed(KeyEvent arg0)
	{
		control_.keyPressed(arg0.getKeyCode());
	}

	public void keyReleased(KeyEvent arg0)
	{
		control_.keyReleased(arg0.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0)
	{}
	
	
	/* Window events interface ***********************************/

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent e) {}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		control_.onClose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}
