package org.paulpell.miam.logic;

import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.gui.KeyMapping;
import org.paulpell.miam.gui.LevelChooserFrame;
import org.paulpell.miam.gui.MainFrame;
import org.paulpell.miam.gui.MainKeyDispatcher;
import org.paulpell.miam.gui.net.OnlinePlayersPanel;
import org.paulpell.miam.logic.PAINT_STATE;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.ItemFactory;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.gameactions.SnakeAction;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.levels.LevelEditorControl;
import org.paulpell.miam.net.ActionEncoder;
import org.paulpell.miam.net.Client;
import org.paulpell.miam.net.ClientInfo;
import org.paulpell.miam.net.ItemEncoder;
import org.paulpell.miam.net.MasterClient;
import org.paulpell.miam.net.NetMethods;
import org.paulpell.miam.net.PlayerInfo;
import org.paulpell.miam.net.Server;
import org.paulpell.miam.net.LevelEncoder;



import static java.awt.event.KeyEvent.*;


public class Control
{

	PAINT_STATE state_ = PAINT_STATE.WELCOME;
	Game game_;
	boolean isEditedLevel_;
	
	Timer timer_;
	TimerTask task_;
	
	MainFrame mainFrame_;

	LevelEditorControl leControl_;
	LevelChooserFrame levelChooserFrame_;
	
	// the server may be hosted in this instance
	Server gameServer_;
	boolean isOnline_;
	boolean isServer_ = false; // whether game is hosted here
	Vector<Item> pendingItems_ = new Vector <Item>(); // stored to add them on next round synchronously
	
	HashSet<SnakeAction> slaveActions_ = new HashSet <SnakeAction>(); // received at each turn by the slaves, sent out to all of them
	// the local player will command this client
	Client gameClient_;
	
	
	long lastFrameTime_;


	private HashMap <Integer, ClientInfo> clientId2Infos_;
	
	
	
	private ItemFactory itemFactory_;
	
	
	
	
	public Control()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				mainFrame_ = new MainFrame(Control.this); // will appear and stay
				//levelEditor_ = new LevelEditorFrame(Control.this);
				leControl_ = new LevelEditorControl(Control.this);
				
				KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
				//kfm.addKeyEventDispatcher(new MainKeyDispatcher(mainFrame_, levelEditor_));
				kfm.addKeyEventDispatcher(new MainKeyDispatcher(Control.this));
				
				try
				{
					Thread.currentThread().setPriority(Constants.THREAD_PRIORITY_GUI);
				}
				catch (Exception e)
				{
					Log.logErr("Cannot set max priority to AWT thread!");
					Log.logException(e);
				}
			}
		});
		
		clientId2Infos_ = new HashMap <Integer, ClientInfo> ();
	}
	


	public void onClose()
	{
		if (null != gameClient_)
			gameClient_.end();
		if (gameServer_ != null)
			gameServer_.end();
		System.exit(0);
	}
	
	
	public PAINT_STATE getState()
	{
		return state_;
	}
	
	public boolean isGameRunning()
	{
		return state_ == PAINT_STATE.GAME;
	}
	
	private void showGameWindow()
	{
		mainFrame_.resetGameInfoPanel(game_.getSnakes(), game_.getPreferredSize());
		mainFrame_.showGamePanel();
	}
	
	private void showNetworkWindow(final String msg)
	{
		if (null == gameClient_)
			mainFrame_.showServerSettings();
		else
			mainFrame_.showPlayersSettings(isServer_);
		
		if (msg != null)
			mainFrame_.displayMessage(msg);
	}
	
	
	public void setLastFrameTime(long lastFrameTime)
	{
		long diff = lastFrameTime - lastFrameTime_;
		double actualFPS = 1000. / diff;
		mainFrame_.displayActualFPS((int)actualFPS);
		lastFrameTime_ = lastFrameTime;
	}
	
	/* Drawable methods ***************/
	
	public Iterator<Drawable> getDrawablesIterator()
	{
		return game_ == null ? null : game_.getDrawablesIterator();
	}
	

	/* game methods ***********************/
	
	public void endGame()
	{
		if (isEditedLevel_)
		{
			isEditedLevel_ = false;
			leControl_.focus();
		}

		if (isEditedLevel_ || !isOnline_ || isServer_)
		{
			timer_.cancel();
			itemFactory_.shutdown();
		}
		
		if (isOnline_ && isServer_)
			gameClient_.sendGameEnd();
		
		state_ = PAINT_STATE.GAME_OVER;
		mainFrame_.paintIsGameover(true);
	}
	
	private void setLevelForNewGame(Level level)
	{
		game_ = new Game(Control.this, level);
		state_ = PAINT_STATE.GAME;
		mainFrame_.stopPaintVictory();
		
	}
	
	private boolean createGame()
	{
		try
		{
			// constructor call thread's wait(),
			// and is notified when user chooses a level!
			levelChooserFrame_ = new LevelChooserFrame(mainFrame_);

			
			GameSettings settings = GameSettings.getCurrentSettings();
			Level level = levelChooserFrame_.getLevel(settings); // blocking
			levelChooserFrame_ = null; // needed for correct keyevent dispatching
			if (null == level)
				return false;
			
			setLevelForNewGame(level);
			isEditedLevel_ = false;
			return true;
		}
		catch (Exception e)
		{
			Log.logErr("Cannot open level: " + e.getMessage());
			Log.logException(e);
			return false;
		}
		
	}
	
	public void playEditedGame(Level l)
	{
		setLevelForNewGame(l);
		isEditedLevel_ = true;
		
		mainFrame_.toFront();
		mainFrame_.requestFocus();
		

		startItemFactory();

		startGameTimer(createLocalGameTimerTask());
		
		showGameWindow();
	}
	
	
	
	// Local game

	private void startLocalGame()
	{	
		startItemFactory();

		startGameTimer(createLocalGameTimerTask());
		
		showGameWindow();
	}
	

	private TimerTask createLocalGameTimerTask()
	{
		return new TimerTask()
		{
			public void run()
			{
				if (state_ == PAINT_STATE.GAME)
				{
					setLastFrameTime(System.currentTimeMillis());
					game_.update();
					mainFrame_.repaint();
				}
			}
		};
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// SERVER/CLIENT game: One machine/game instance is the master, receives a signal
	// from all the slaves at each step, and forwards to every one.
	// Then a step can be taken in the game
	/////////////////////////////////////////////////////////////////////////////////////////
	
	private void startItemFactory()
	{
		if (null != itemFactory_)
			itemFactory_.shutdown();
		itemFactory_ = new ItemFactory(this, game_, Globals.SCORE_ITEMS_ONLY);
	}
	
	// start a game hosted remotely (this is a slave)
	public void startSlaveGame()
	{
		if (game_ == null)
		{
			mainFrame_.displayMessage("No game loaded now, sorry");
			gameClient_.sendErrorMessage();
			return;
		}
		
		state_ = PAINT_STATE.GAME;

		mainFrame_.paintIsGameover(false);
		mainFrame_.stopPaintVictory();
		showGameWindow();
	}
	

	// hosting game
	
	// this function first reads the parameters from the GUI
	public void startMasterGame(OnlinePlayersPanel playersPanel)
	{
		int i = playersPanel.getSnakesNumber();
		if (i > 0)
			Globals.NUMBER_OF_SNAKES = i;
		
		startMasterGame();
	}
	
	public void startMasterGame()
	{

		int numSlaves = gameServer_.getSlaveNumber();
		pendingItems_.removeAllElements();
		slaveActions_.clear();
		
		if (numSlaves > 0)
		{
			try
			{
				sendNetworkLevel();
				for (Item i: game_.getItems())
					gameClient_.sendItem(i);
					
				if (Globals.NETWORK_DEBUG)
					Log.logMsg("Send START, id="+gameClient_.getServerId());
				gameClient_.sendStartCommand();
			}
			catch (IOException e)
			{
				networkFeedback("Could not start the game: "+e.getLocalizedMessage());
			}
		}

		startItemFactory();

		startGameTimer(createMasterGameTimerTask());
		
		showGameWindow();
	}
	
	// create and launch the updating, drawing thread
	private void startGameTimer(TimerTask task)
	{
		if (null != timer_)
			timer_.cancel();
		timer_ = new Timer("drawing");

		lastFrameTime_ = System.currentTimeMillis();
		
		task_ = task;
		timer_.scheduleAtFixedRate(task, 0, 1000 / Globals.FPS);

	}
	
	
	private TimerTask createMasterGameTimerTask()
	{
		return new TimerTask()
		{
			public void run()
			{
				if (state_ == PAINT_STATE.GAME)
				{	
					// send items
					synchronized (pendingItems_)
					{
						for (Item i: pendingItems_)
						{
							game_.addItem(i);

							if (gameServer_.getSlaveNumber() > 0)
								gameClient_.sendItem(i);
						}
						pendingItems_ = new Vector<Item>();
					}
					
					// send actions
					String stepString = handleSlaveActions();
					
					if (gameServer_.getSlaveNumber() > 0)
					{
						try
						{
							gameClient_.increaseTimestamp();
							gameClient_.sendStepCommand(stepString);
						}
						catch (IOException e)
						{
							if (Globals.NETWORK_DEBUG)
								Log.logErr("ERROR: Could not send game data: "+e.getLocalizedMessage());
							showNetworkWindow("ERROR: Could not send game data: "+e.getLocalizedMessage());
							return;
						}
					}
					
					setLastFrameTime(System.currentTimeMillis());
					game_.update();
					mainFrame_.repaint();
				}
			}
		};
	}
	
	private void onKeyPressedAction(int key)
	{
		if (state_ == PAINT_STATE.GAME)
		{
			SnakeAction action = KeyMapping.getPressedAction(key);
			if (action != null)
			{
				if (isOnline_)
				{
					if (isServer_)
						addSlaveAction(action);
					else
						sendAction(action);
				}
	
				else
					action.perform(game_.getSnake(action.getSnakeIndex()));
			}
		}
	}
	
	private void onKeyReleasedAction(int key)
	{
		if (state_ == PAINT_STATE.GAME)
		{
			SnakeAction action = KeyMapping.getReleasedAction(key);
			if (action != null)
			{
				if (isOnline_)
				{
					if (isServer_)
						addSlaveAction(action);
					else
						sendAction(action);
				}
				
				else
				{
					int i = action.getSnakeIndex();
					action.perform(game_.getSnake(i));
				}
			}
		}
	}
	
	private void addSlaveAction(SnakeAction action)
	{
		synchronized (slaveActions_)
		{
			if (Globals.ACTION_DEBUG)
				Log.logErr("addSlaveAction: action = " + action + ", actions = " + slaveActions_);
			
			if (action.isStartAction())
			{
				if (Globals.ACTION_DEBUG)
					Log.logErr("is a start action");
				// remove previous end action if present
				for (SnakeAction a : slaveActions_)
				{
					if (a.getSimpleActionType() == action.getSimpleActionType() // same action type
							&& a.getSnakeIndex() == action.getSnakeIndex() // same snake
							&& !a.isStartAction()) // end action
					{
						if (Globals.ACTION_DEBUG)
							Log.logErr("Found end action to remove: " + a);
						slaveActions_.remove(a);
						break;
					}
				}
			}
			slaveActions_.add(action);
			if (Globals.ACTION_DEBUG)
				Log.logErr("Adding action : " + action + ", new actions = " + slaveActions_);
		}
	}
	
	// called by game
	public void requestNewItem()
	{
		Item item = itemFactory_.createItem(game_.getSnakes(), game_.getItems(), game_.getLevel().getWall());
		addItem(item);
	}
	

	// called by itemCreator
	public void addItem(Item item)
	{
		if (item != null && game_ != null)
		{
			if (isOnline_)
			{
				assert isServer_ : "Non gameMaster creating Item!";
				synchronized (pendingItems_)
				{
					pendingItems_.add(item);
				}
			}
			else
			{
				game_.addItem(item);	
			}
		}
	}
	
	/* Settings ******************************************/
	
	private void toggleSettingsWindowVisible()
	{
		mainFrame_.showSettings();
	}
	

	/* Fire up the level editor *********************************/
	private void levelEditor()
	{
		leControl_.setVisible(true);
	}
	
	/* Interaction methods (keyboard) ***************************/
	public void escPressed()
	{
		switch (state_)
		{
		case WELCOME:
			System.out.println("ESC => exit");
			System.exit(0);	
			break;
			
		case GAME_OVER:
		case VICTORY:
			state_ = PAINT_STATE.WELCOME;
			mainFrame_.showWelcomePanel();
			break;
			
		case GAME:
		case PAUSE:
			if (!isOnline_ || isServer_) // only the master can stop an online game =)
				endGame();
			break;
		}
		
		mainFrame_.repaint();
	}
	
	public void newPressed()
	{
		// thread is used to block the level chooser
		new Thread("level-chooser")
		{
			public void run()
			{
				if (state_ == PAINT_STATE.WELCOME
						|| state_ == PAINT_STATE.GAME_OVER
						|| state_ == PAINT_STATE.VICTORY)
				{
					if (createGame())
					{
						if (isServer_)
							startMasterGame();
						else if (!isOnline_)
							startLocalGame();
					}
					else
						mainFrame_.displayMessage("Cannot create game");
				}
			}
		}.start();
	}
	

	/* Pause *******************************/
	private void togglePause()
	{
		if (state_ == PAINT_STATE.PAUSE)
		{
			state_ = PAINT_STATE.GAME;
			mainFrame_.paintIsGameInPause(false);

			// wake up factory
			synchronized (itemFactory_)
			{
				itemFactory_.notify();
			}
		}
		else if (state_ == PAINT_STATE.GAME)
		{
			state_ = PAINT_STATE.PAUSE;
			mainFrame_.paintIsGameInPause(true);
		}
	}
	
	// can return null
	public KeyListener whoShouldReceiveKeyEvents(boolean ctrl)
	{
		if (null != levelChooserFrame_)
		{
			return levelChooserFrame_;
		}
		
		if (!isEditedLevel_ && leControl_.isVisible())
		{
			if (ctrl)
				return null; // let the accelerators do the job
			return leControl_.getKeyListener();
		}
		
		if (isEditedLevel_ || mainFrame_.shouldGetKeyEvents())
		{
			return mainFrame_;
		}

		return null;
	}
	
	public void keyPressed(int key)
	{
		
		/* Always valid */
		switch(key)
		{
		case VK_ESCAPE:
			escPressed();
			return;
			
		default:
			break;
		}
		
		/* Game time interaction */
		if (state_ == PAINT_STATE.GAME)
		{
			onKeyPressedAction(key);
		}
	

		if (state_ == PAINT_STATE.GAME || state_ == PAINT_STATE.PAUSE)
		{
			if (key == VK_P && !isOnline_) // no pause in online mode
				togglePause();
		}
		
		else
		{
		/* outside game ****/
			switch(key)
			{
			case VK_S:
				toggleSettingsWindowVisible();
				break;
				
			case VK_SPACE:
			case VK_N:
				newPressed();
				break;
				
			case VK_E:
				levelEditor();
				break;
				
			case VK_O:
				showNetworkWindow(null);
				break;
				
			default:
				break;
			}
		}
	}
	
	public void keyReleased(int key)
	{
		onKeyReleasedAction(key);
	}
	
	public void sendAction(SnakeAction action)
	{
		gameClient_.sendAction(action);
	}
	
	public void receiveSlaveAction(SnakeAction action)
	{
		addSlaveAction(action);
	}
	
	// return step String
	private String handleSlaveActions()
	{
		int n_snakes = game_.getNumberOfSnakes();
		boolean[][] start = new boolean[n_snakes][4];
		Vector<SnakeAction> validActions = new Vector<SnakeAction>();
		
		// invalid actions will be kept for the next round
		HashSet<SnakeAction> invalidActions = new HashSet<SnakeAction>();
		
		

		synchronized (slaveActions_)
		{
			// first handle all start actions, which we validate anyways
			for (SnakeAction a : slaveActions_)
			{
				//if (StartAction.class.isAssignableFrom(a.getClass()))
				if (a.isStartAction())
				{
					start[a.getSnakeIndex()][a.getSimpleActionType()] = true;
					validActions.add(a);
				}
			}
			
			// then all end actions
			for (SnakeAction a : slaveActions_)
			{
				//if (! StartAction.class.isAssignableFrom(a.getClass()))
				if (!a.isStartAction())
				{
					if (! start[a.getSnakeIndex()][a.getSimpleActionType()])
						validActions.add(a);
					else
						invalidActions.add(a);
				}
			}
			
			
			// finally apply the valid actions and send them
			String stepString = "" + (char)validActions.size();
			for (SnakeAction a: validActions)
			{
				stepString += new String(ActionEncoder.encodeAction(a));
				a.perform(game_.getSnake(a.getSnakeIndex()));
			}
			slaveActions_ = invalidActions;
			if (Globals.ACTION_DEBUG)
				Log.logErr("Sending step: " + validActions);
			return stepString;
		}
	}

	////////////////////////////////////////////
	// 
	// Network methods
	public void joinGame(String host)
	{
		if (isServer_)
		{
			networkFeedback("Already hosting!");
			return;
		}
		
		if (host == null || host.equals(""))
		{
			networkFeedback("Choose a server");
			return;
		}
		InetAddress addr;
		
		try
		{
			byte[] bs = NetMethods.parseIP(host);
			if (bs != null)
				addr = InetAddress.getByAddress(bs);
			else
				addr = InetAddress.getByName(host);
			joinGame(addr);
		}
		catch (UnknownHostException e)
		{
			mainFrame_.displayMessage("Unknown host: "+ host);
			return;
		}
	}
	
	private void joinGame(InetAddress addr)
	{
		
		try
		{
			stopClient();
			gameClient_ = new Client(this);
			gameClient_.connect(addr);
			gameClient_.start();
			isOnline_ = true;

			mainFrame_.resetNetworkPanels();
			mainFrame_.showPlayersSettings(false);
			mainFrame_.displayMessage("Connected to " + addr);
		}
		catch (IllegalThreadStateException ite)
		{
			assert false : "IllegalThreadException in Control.joinGame()";
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Error: " + e.getLocalizedMessage());
		}
	}
	
	public void clientJoined(ClientInfo info)
	{
		clientId2Infos_.put(info.getClientId(), info);
		displayServerMessage("New client accepted : " + info);
		updateConnectedInfo();

		gameClient_.sendClientsList(clientId2Infos_.values());
	}
	
	public void clientLeft(int clientId)
	{
		ClientInfo ci = clientId2Infos_.remove(clientId);
		updateConnectedInfo();

		networkFeedback("Client left: " + ci);

		gameClient_.sendClientsList(clientId2Infos_.values());
	}
	
	public void rejected()
	{
		stopClient();
		showNetworkWindow("Client was rejected by the server");
	}
	
	public void leaveServer()
	{
		if (isOnline_)
		{
			stopClient();
			if (isServer_)
				stopServer();
			showNetworkWindow(null);
		}
	}
	
	private void stopClient()
	{
		if (null != gameClient_)
		{
			gameClient_.end();
			gameClient_ = null;
		}
		isOnline_ = false;
	}
	
	private void stopServer()
	{
		stopClient();
		if (null != gameServer_)
		{
			gameServer_.end();
			gameServer_ = null;
		}
		isServer_ = false;
	}
	
	// remote server stops activity
	public void serverStops()
	{
		endGame();
		leaveServer();
	}
	
	public void serverDied()
	{
		if (!isServer_)
		{
			if (state_ == PAINT_STATE.GAME_OVER)
				mainFrame_.displayMessage("The server died...");
			else
			{
				mainFrame_.displayMessage("Server died... Sorry");
				if (state_ == PAINT_STATE.GAME)
					endGame();
				
				stopClient();
			}
		}
	}
	
	public void hostGame()
	{
		try
		{
			gameClient_ = new MasterClient(this);
			gameServer_ = new Server(this, gameClient_);
			((MasterClient)gameClient_).setServer(gameServer_);
			gameServer_.setMaxClientNumber(Globals.ONLINE_DEFAULT_CLIENT_MAX_NUMBER);
			isServer_ = true;
			isOnline_ = true;

			mainFrame_.resetNetworkPanels();
			mainFrame_.showPlayersSettings(true);


			String name =  "localhost";
			ClientInfo masterClientInfo = new ClientInfo(0, name);

			clientJoined(masterClientInfo);
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Could not start server: "+e.getLocalizedMessage());
		}
	}

	public void networkFeedback(String message)
	{
		mainFrame_.displayNetworkMessage(message);
	}
	
	public void displayChatMessage(String message, int from)
	{
		ClientInfo info = clientId2Infos_.get(from);
		String name = "" + from;
		if (null != info)
			name = info.toString();
		networkFeedback(name + ": " + message);
	}
	
	public void displayServerMessage(String message)
	{
		networkFeedback(message);
	}
	
	public void setClientInfos(HashMap <Integer, ClientInfo> clientId2Infos)
	{
		clientId2Infos_ = clientId2Infos;
		updateConnectedInfo();
	}
	
	private void updateConnectedInfo()
	{
		Vector <String> clientNames = new Vector <String> ();
		Vector <String> playerNames = new Vector <String> ();
		
		for (ClientInfo ci : clientId2Infos_.values())
		{
			clientNames.add(ci.toString());
			for (PlayerInfo pi : ci.getPlayerInfos())
				playerNames.add(pi.getName() + "@" + ci.getLetter());
		}
		
		String[] cn = new String[]{};
		String[] pn = new String[]{};
		cn = clientNames.toArray(cn);
		pn = playerNames.toArray(pn);
		mainFrame_.setConnectedClients(cn);
		mainFrame_.setPlayers(pn);
		
	}
	
	public void sendChatMessage(String message)
	{
		if (!isOnline_)
		{
			mainFrame_.displayMessage("First connect to a server!");
			return;
		}
		try
		{
			gameClient_.sendChatMessage(message);
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Could not send message: "+e.getLocalizedMessage());
		}
	}
	
	public void onReceiveItem(byte[] itemRepr)
	{
		if (isOnline_ && null != game_)
			game_.addItem(ItemEncoder.decodeItem(itemRepr));
	}
	
	public void sendNetworkLevel() throws IOException
	{
		if (isOnline_)
		{
			try
			{
				gameClient_.sendLevel(game_.getLevel());
			} catch (Exception e) 
			{
				mainFrame_.displayMessage("Could not send network level: "+e.getMessage());
				Log.logErr("Could not send network level: " + e.getMessage());
				Log.logException(e);
			}
		}
	}

	public void receiveNetworkLevel(byte[] encoded)
	{
		if (isOnline_ && !isServer_)
		{
			if (game_ != null)
				endGame();
	
			try
			{
				Level level = LevelEncoder.decodeLevel(encoded);
				game_ = new RemoteGame(this, level);
			} catch (Exception e)
			{
				mainFrame_.displayMessage("Could not receive network level: "+e.getMessage());
				Log.logErr("Could not receive network level: " + e.getMessage());
				Log.logException(e);
			}
		}
	}
	
	public void stepGame(String stepString)
	{
		if (null == game_)
			return;
		int actionSize = Constants.ENCODED_ACTION_SIZE;
		int actionsNr = stepString.charAt(0);
		stepString = stepString.substring(1);
		
		for (int i=0; i<actionsNr; ++i)
		{
			String action = stepString.substring(0,actionSize);
			stepString = stepString.substring(actionSize);
			
			SnakeAction a = ActionEncoder.decodeAction(action.getBytes());
			a.perform(game_.getSnake(a.getSnakeIndex()));
			Log.logErr("Action: " + a);
		}
		
		
		game_.update();
		mainFrame_.repaint();
	}
	
	
	public void snakesWon(Vector <Snake> ss)
	{
		state_ = PAINT_STATE.VICTORY;
		Vector <Color> colors = new Vector <Color> ();
		for (Snake s : ss)
			colors.add(s.getColor());
		mainFrame_.paintVictory(colors);
		
		if (isOnline_)
			gameClient_.sendSnakesWon(ss);
	}
	
	public void onSnakesWon(byte[] bs)
	{
		if (isOnline_ && game_ != null)
		{
			Vector <Color> colors = new Vector <Color> ();
			for (byte b: bs)
				colors.add(Snake.s_snakesColors[0xFF & b]);
			mainFrame_.paintVictory(colors);
		}
	}

	public void snakeDied(Snake s, Pointd collision)
	{
		game_.kill(s, collision);
		if (isOnline_)
			gameClient_.sendSnakeDeath(s,collision);
	}

	public void onSnakeDeath(int s, Pointd p)
	{
		if (isOnline_ && null != game_)
			game_.kill(game_.getSnake(s), p);
	}
	
	public void snakeAcceptedItem(Snake s, Item item)
	{
		if (isOnline_)
			gameClient_.sendSnakeAcceptedItem(s.getId(), game_.getItems().indexOf(item));

		game_.getItems().remove(item);

		s.acceptItem(item);
		
		// replace by a new item
		requestNewItem();
	}
	
	public void onAcceptItem(int snakeIndex, int itemIndex)
	{
		if (isOnline_ && null != game_)
		{
			Item i = game_.getItems().remove(itemIndex);
			Snake s = game_.getSnake(snakeIndex);
			s.acceptItem(i);
		}
	}

	
}
