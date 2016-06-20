package org.paulpell.miam.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

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
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.ONLINE_STATE;
import org.paulpell.miam.logic.levels.LevelChoiceInfo;
import org.paulpell.miam.logic.levels.LevelEditorControl;
import org.paulpell.miam.logic.players.PlayerInfo;


public class MainFrame
	extends JFrame
	implements WindowListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8026416994513756565L;
	
	private enum TopDisplay
	{
		NONE,
		CUSTOM_PANEL,
		MENU_BAR
	}

	Control control_;
	
	AbstractDisplayPanel currentPanel_; // reference to one of the following panels
	
	GamePanel gamePanel_;
	SettingsPanel settingsPanel_;
	WelcomePanel welcomePanel_;
	
	// menu is only used in level editor
	//LevelEditorMenuBar menuBar_;
	LevelEditorPanel levelEditorPanel_;
	LevelEditorControl levelEditorControl_;
	
	OnlineServersPanel serversPanel_;
	OnlinePlayersPanel playersPanel_;

	LevelChooserFrame levelChooserFrame_;
	
	private static String title_ = "Snakesss";
	
	private JPanel topPanel_ = null;
	private TopDisplay topDisplay_ = TopDisplay.NONE;
	//private boolean isTopPanelVisible_ = false;
	
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
		
		
		
		welcomePanel_ = new WelcomePanel("welcome :)", control_, this);

		//addKeyListener(this);
		addWindowListener(this);

		topDisplay_ = TopDisplay.NONE;
		boolean ok = setNewPanel(welcomePanel_);
		
		setVisible(true);
		if (ok && Globals.USE_ANIMATIONS)
			welcomePanel_.startAnimating();
	}
	
	public GamePanel getGamePanel()
	{
		if (null == gamePanel_)
			gamePanel_ = new GamePanel(control_, this);
		return gamePanel_;
	}
	public OnlineServersPanel getServersPanel()
	{
		if (null == serversPanel_)
			serversPanel_ = new OnlineServersPanel(control_, this);
		return serversPanel_;
	}
	public OnlinePlayersPanel getPlayersPanel()
	{
		if (null == playersPanel_)
			playersPanel_ = new OnlinePlayersPanel(control_, this);
		return playersPanel_;
	}
	
	public void setLevelEditorControl(LevelEditorControl leControl)
	{
		levelEditorControl_ = leControl;
		levelEditorPanel_ = leControl.getLevelEditorPanel();
	}

	
	public LevelChoiceInfo chooseLevel()
	{
		levelChooserFrame_ = new LevelChooserFrame(this);
		// getLevelInfo is blocking: calls thread's wait and is notified
		// when user chooses a level
		LevelChoiceInfo lci = levelChooserFrame_.getLevelInfo(); 
		levelChooserFrame_ = null; // needed for correct keyevent dispatching
		
		return lci;
	}

	public boolean showSettingsPanel()
	{
		if (null == settingsPanel_)
			settingsPanel_ = new SettingsPanel(control_, this);
		
		topDisplay_ = TopDisplay.CUSTOM_PANEL;
		return setNewPanel(settingsPanel_);
	}
	
	public boolean showLevelEditor()
	{
		topDisplay_ = TopDisplay.MENU_BAR;
		return setNewPanel(levelEditorPanel_);
	}
	
	public boolean showServerSettings()
	{

		topDisplay_ = TopDisplay.CUSTOM_PANEL;
		return setNewPanel(getServersPanel());
	}
	
	public boolean showPlayersSettings(boolean isHosting)
	{
		topDisplay_ = TopDisplay.CUSTOM_PANEL;
		return setNewPanel(getPlayersPanel());
	}
	
	public boolean showGamePanel()
	{
		topDisplay_ = TopDisplay.NONE;

		getGamePanel().setPause(false);
		gamePanel_.setGameover(false);
		return setNewPanel(gamePanel_);
	}
	
	public boolean showWelcomePanel()
	{
		topDisplay_ = TopDisplay.NONE;
		boolean b = setNewPanel(welcomePanel_);
		if (b && Globals.USE_ANIMATIONS)
			welcomePanel_.startAnimating();
		return b;
	}
	
	private void updateTopDisplay()
	{
		if (topDisplay_ != TopDisplay.CUSTOM_PANEL) {
			if (null != topPanel_) {
				remove(topPanel_);
				topPanel_ = null;
			}
		}
			// remove menu bar anyway; put it back if needed
		setJMenuBar(null);
		
		switch (topDisplay_)
		{
		case CUSTOM_PANEL:
			if (null == topPanel_)
				topPanel_ = new TopPanel(this);
			add(topPanel_, BorderLayout.NORTH);
			break;
		case MENU_BAR:
			setJMenuBar(new LevelEditorMenuBar(levelEditorControl_));
			break;
		}
	}
	
	private boolean setNewPanel(AbstractDisplayPanel panel)
	{
		if (currentPanel_ != null) {
			if ( ! currentPanel_.canRemovePanel() )
				return false;
			else
				currentPanel_.panelRemoved();
			remove(currentPanel_);
			currentPanel_.setVisible(false);
		}
		
		currentPanel_ = panel;
			
		updateTopDisplay();

		add(currentPanel_);
		currentPanel_.setVisible(true);
		
		doLayout();
		
		Dimension size = currentPanel_.getPreferredSize();
		setMinimumSize(size);
		setMaximumSize(size);
		
		pack();
		repaint();
		return true;
	}
	
	public void paint(Graphics g)
	{
		if (topDisplay_ == TopDisplay.CUSTOM_PANEL)
			topPanel_.paint(g);
		currentPanel_.paint(g);
	}
	
	
	public void resetOnNewGame(Game g)
	{
		gamePanel_.resetForNewGame(g);
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
		getGamePanel().stopVictoryColors();
	}
	
	public void displayMessage(String message, boolean immediately)
	{
		if (null != currentPanel_)
			currentPanel_.displayMessage(message, immediately);
	}
	
	/*public void displayNetworkMessage(String message)
	{
		if (currentPanel_ == playersPanel_
				|| currentPanel_ == serversPanel_)
			currentPanel_.displayMessage(message, true);
	}*/

	public void resetPlayersNetworkPanel(int defaultPlayerNumber, ONLINE_STATE onlineState)
	{
		assert onlineState != ONLINE_STATE.OFFLINE;
		getPlayersPanel().reset(defaultPlayerNumber, onlineState);
	}
	/*public void resetServersNetworkPanel()
	{
		if (null == serversPanel_)
			showServerSettings();
		
		serversPanel_.reset();
	}*/
	
	public void setConnectedClients(Vector <String> clientNames)
	{
		if (null == playersPanel_)
			return;
		playersPanel_.setConnectedClients(clientNames);
	}
	
	public void setPlayers(Vector <PlayerInfo> playerInfos,
			Vector<Integer> unusedColors,
			Control control)
	{
		//getPlayersPanel().
		repaint();
	}
	
	public void displayActualFPS(int fps)
	{
		if (currentPanel_ == gamePanel_)
			gamePanel_.displayActualFPS(fps);
	}
	
	private void moveWindow(int dx, int dy) 
	{
		Point pos = getLocation();
		pos.x += dx;
		pos.y += dy;
		setLocation(pos);
	}

	/* User keyboard interface ***********************************/


	// we want to pass the key events to control when welcome and game panels are active 
	// and to level editor itself when it is active
	public KeyListener getCurrentKeyListener(KeyEvent e)
	{
		if (levelChooserFrame_ != null)
			return levelChooserFrame_;
		return currentPanel_.getCurrentKeyListener(e);
		//if (handlesKeyEvent(e))
		//	return this;
		/*if (currentPanel_ == playersPanel_ || currentPanel_ == serversPanel_)
			return playersPanel_.getKeyListener(e);
		else if (currentPanel_ == levelEditorPanel_)
			return levelEditorPanel_;
		return null;*/
	}

	/*private void moveWindowDetail(int keyCode)
	{
		String dir = "";
		
		//int maxX = 

		switch (keyCode) {
		case KeyEvent.VK_DOWN:
			dir = "Down";
			moveWindow(0, Globals.GUI_MOVE_WINDOW_STEP_PIXELS);
			break;
			
		case KeyEvent.VK_UP:
			dir = "Up";
			moveWindow(0, -Globals.GUI_MOVE_WINDOW_STEP_PIXELS);
			break;
		case KeyEvent.VK_LEFT:
			dir = "Left";
			moveWindow(-Globals.GUI_MOVE_WINDOW_STEP_PIXELS, 0);
			break;
		case KeyEvent.VK_RIGHT:
			dir = "Right";
			moveWindow(Globals.GUI_MOVE_WINDOW_STEP_PIXELS, 0);
			break;
		
		default:
			break;
		}
		Log.logMsg("Alt and direction " + dir);
	}
	
	private boolean handlesKeyEvent(KeyEvent e)
	{

		//int modctrl = Event.CTRL_MASK;
		int altctrl = Event.ALT_MASK;
		int mod = e.getModifiers();
		
		// Alt and an arrow moves MainFrame around
		if (0 != (mod & altctrl)) {
			int keycode = e.getKeyCode();
			switch (keycode) {
			case KeyEvent.VK_DOWN:
				
			case KeyEvent.VK_UP:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				moveWindowDetail(keycode);
				return true;
			
			default:
				break;
			}
			return false;
		}
		
		// or game and welcome panels are (for now, TODO) handled by Control 
		if (currentPanel_ == gamePanel_
				|| currentPanel_ == welcomePanel_
				|| currentPanel_ == settingsPanel_) {
			return true;
		}
		
		return false;
	}
	
	private boolean useKeyEvent(KeyEvent e)
	{
		int mod = e.getModifiers();
		if (0 != (mod & Event.ALT_MASK)) {
		}
		
		return false;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if ( ! useKeyEvent(arg0) ) {
			control_.keyPressed(arg0.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		if ( ! useKeyEvent(arg0) ) {
			control_.keyReleased(arg0.getKeyCode());
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{}*/
	
	
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
