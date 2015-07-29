package org.paulpell.miam.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.paulpell.miam.gui.editor.LevelEditorMenuBar;
import org.paulpell.miam.gui.editor.LevelEditorPanel;
import org.paulpell.miam.gui.net.OnlinePlayersPanel;
import org.paulpell.miam.gui.net.OnlineServersPanel;
import org.paulpell.miam.gui.settings.SettingsPanel;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.levels.LevelEditorControl;


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
	
	GamePanel gamePanel_;
	SettingsPanel settingsPanel_;
	WelcomePanel welcomePanel_;
	LevelEditorPanel levelEditorPanel_;
	
	// menu is only used in level editor
	LevelEditorMenuBar menuBar_;
	
	OnlineServersPanel serversPanel_;
	OnlinePlayersPanel playersPanel_;
	
	private static String title_ = "Snakesss";
	
	private JPanel topPanel_ = null;
	private boolean isTopPanelVisible_ = false;
	
	public MainFrame(Control control)
	{

		super(title_);
		
		control_ = control;

		setUndecorated(true);
		
		GraphicsDevice gd =
				GraphicsEnvironment
				.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int screenwidth = gd.getDisplayMode().getWidth();
		int screenheight = gd.getDisplayMode().getHeight();
		int appearx = (screenwidth - Constants.DEFAULT_IMAGE_WIDTH) / 2;
		int appeary = (screenheight - Constants.DEFAULT_IMAGE_HEIGHT) / 2;
		setLocation(appearx, appeary);
		
		
		
		welcomePanel_ = new WelcomePanel("welcome :)", this);
		gamePanel_ = new GamePanel(control_, this);
		currentPanel_ = welcomePanel_;
		setNewPanel(currentPanel_);

		addKeyListener(this);
		addWindowListener(this);
		
		setVisible(true);
		
		showWelcomePanel(); // do it after setVisible, to enable animation
	}
	
	public void setLevelEditorControl(LevelEditorControl leControl)
	{
		menuBar_ = new LevelEditorMenuBar(leControl);
		levelEditorPanel_ = leControl.getLevelEditorPanel();
	}
	
	/*private JPanel createTopPanel()
	{
		JPanel panel = new JPanel();
		JButton gamePanelButton = new JButton("Main menu");
		gamePanelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showWelcomePanel();
			}
		});
		panel.add(gamePanelButton);
		panel.doLayout();
		panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(10, 10, 10)));
		return panel;
	}*/


	// we want to pass the key events to control when welcome and game panels are active 
	public boolean shouldGetKeyEvents()
	{
		return currentPanel_ == gamePanel_ || currentPanel_ == welcomePanel_;
	}


	public void showSettings()
	{
		if (null == settingsPanel_)
			settingsPanel_ = new SettingsPanel(control_, this);
		
		isTopPanelVisible_ = true;
		setNewPanel(settingsPanel_);
	}
	
	public void showLevelEditor()
	{

		isTopPanelVisible_ = false;
		
		setJMenuBar(menuBar_);
		setNewPanel(levelEditorPanel_);
		
		repaint();
	}
	
	public void showServerSettings()
	{
		if (null == serversPanel_)
			serversPanel_ = new OnlineServersPanel(control_, this);
		
		isTopPanelVisible_ = true;
		setNewPanel(serversPanel_);
	}
	
	public void showPlayersSettings(boolean isHosting)
	{
		if (null == playersPanel_)
			playersPanel_ = new OnlinePlayersPanel(control_, this);
		
		playersPanel_.prepare(isHosting);
		
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
		boolean b = setNewPanel(welcomePanel_);
		if (b)
			welcomePanel_.startAnimating();
	}
	
	private boolean setNewPanel(AbstractDisplayPanel panel)
	{
		if ( ! currentPanel_.canRemovePanel() )
			return false;
		
		if ( currentPanel_ == levelEditorPanel_ 
				&& panel != levelEditorPanel_)
			setJMenuBar(null);
			
		
		remove(currentPanel_);
		currentPanel_.setVisible(false);
		currentPanel_ = panel;

		if (isTopPanelVisible_)
		{
			if (null == topPanel_)
				topPanel_ = new TopPanel(this);
			add(topPanel_, BorderLayout.NORTH);
		}
		else if (null != topPanel_)
			remove(topPanel_);

		add(currentPanel_);
		currentPanel_.setVisible(true);
		
		Dimension size = currentPanel_.getPreferredSize();
		setMinimumSize(size);
		setMaximumSize(size);
		
		pack();
		repaint();
		return true;
	}
	
	
	public void paint(Graphics g)
	{
		if (isTopPanelVisible_)
			topPanel_.paint(g);
		currentPanel_.paint(g);
	}
	
	
	
	public boolean isCurrentPanelWelcome()
	{
		return currentPanel_ == welcomePanel_;
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
	
	public void paintVictory(Vector <Color> colors)
	{
		gamePanel_.setVictoryColors(colors);
	}
	
	public void stopPaintVictory()
	{
		gamePanel_.stopVictoryColors();
	}
	
	public void displayMessage(String message)
	{
		if (null != currentPanel_)
			currentPanel_.displayMessage(message);
	}
	
	public void displayNetworkMessage(String message)
	{
		if (currentPanel_ == playersPanel_
				|| currentPanel_ == serversPanel_)
			currentPanel_.displayMessage(message);
	}
	
	public void resetNetworkPanels()
	{
		if (null != playersPanel_)
			playersPanel_.reset();
		if (null != serversPanel_)
			serversPanel_.reset();
	}
	
	public void setConnectedClients(String[] clients)
	{
		if (null == playersPanel_)
			return;
		playersPanel_.setConnectedClients(clients);
	}
	
	public void setPlayers(String[] players)
	{
		if (null == playersPanel_)
			return;
		playersPanel_.setPlayers(players);
	}
	
	public void displayActualFPS(int fps)
	{
		if (currentPanel_ == gamePanel_)
			gamePanel_.displayActualFPS(fps);
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
