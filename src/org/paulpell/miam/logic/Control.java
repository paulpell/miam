package org.paulpell.miam.logic;

import java.awt.Color;
import java.awt.KeyboardFocusManager;
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
import org.paulpell.miam.gui.GlobalColorTable;
import org.paulpell.miam.gui.KeyMapping;
import org.paulpell.miam.gui.MainFrame;
import org.paulpell.miam.gui.MainKeyDispatcher;
import org.paulpell.miam.gui.net.OnlinePlayersPanel;
import org.paulpell.miam.logic.PAINT_STATE;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.ItemFactory;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.gameactions.SnakeAction;
import org.paulpell.miam.logic.levels.DefaultLevel;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.levels.LevelChoiceInfo;
import org.paulpell.miam.logic.levels.LevelEditorControl;
import org.paulpell.miam.logic.levels.LevelFileManager;
import org.paulpell.miam.logic.players.PlayerInfo;
import org.paulpell.miam.logic.players.PlayersManager;
import org.paulpell.miam.net.ActionEncoder;
import org.paulpell.miam.net.ClientInfo;
import org.paulpell.miam.net.ItemEncoder;
import org.paulpell.miam.net.NetMethods;
import org.paulpell.miam.net.NetworkControl;
import org.paulpell.miam.net.TimestampedMessage;
import org.paulpell.miam.net.LevelEncoder;



import static java.awt.event.KeyEvent.*;


public class Control
{

	PAINT_STATE state_ = PAINT_STATE.WELCOME;
	Game game_;
	boolean isEditorLevelPlayed_;
	
	Timer gameTimer_;
	
	MainFrame mainFrame_;

	LevelEditorControl leControl_;
	
	
	NetworkControl networkControl_;
	
	LevelChoiceInfo onlineLastLevelChoice_;

	Vector<Item> pendingItems_ = new Vector <Item>(); // stored to add them on next round synchronously
	
	HashSet<SnakeAction> slaveActions_ = new HashSet <SnakeAction>(); // received at each turn by the slaves, sent out to all of them
	
	
	long lastFrameTime_;

	// connected clients
	private HashMap <Integer, ClientInfo> clientId2Infos_;
	
	// players actually, can be 0..n on each client
	//private int currentPlayerSeats_; // max number of players online
	//private HashMap <Integer, PlayerInfo> playerId2Infos_; // actual players online
	//private Vector<PlayerInfo> playerInfos_;
	PlayersManager playerMgr_;
	
	private ItemFactory itemFactory_;
	
	
	
	
	public Control()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				mainFrame_ = new MainFrame(Control.this); // will appear and stay
				leControl_ = new LevelEditorControl(Control.this, mainFrame_);
				mainFrame_.setLevelEditorControl(leControl_);
				networkControl_ = new NetworkControl(Control.this, mainFrame_.getPlayersPanel(), mainFrame_.getServersPanel());

				KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
				//kfm.addKeyEventDispatcher(new MainKeyDispatcher(Control.this));
				kfm.addKeyEventDispatcher(new MainKeyDispatcher(mainFrame_));
				
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
		
	}
	
	@Override
	public String toString ()
	{
		String sep = " - ";
		String name ="Snakesss' HQ" + sep;
		
		name += networkControl_.getNetStatusStr() + sep;
		
		switch (state_)
		{
		case GAME:
			name += "play";
			break;
		case GAME_OVER:
			name += "gameover";
			break;
		case OTHER_PANELS:
			name += "??";
			break;
		case PAUSE:
			name += "pause";
			break;
		case VICTORY:
			name += "victory";
			break;
		case WELCOME:
			name += "start";
			break;
		}
		name += sep;
		
		return name;
	}


	public void onClose()
	{
		// announce we leave
		stopServer();
		stopClient();
		System.exit(0);
	}
	
	public boolean isEditorLevelPlayed ()
	{
		return isEditorLevelPlayed_;
	}
	
	public boolean isGameRunning()
	{
		return state_ == PAINT_STATE.GAME
				|| state_ == PAINT_STATE.PAUSE;
	}
	
	private boolean isHosting()
	{
		return networkControl_.isHosting();
	}
	private boolean isOffline()
	{
		return networkControl_.isOffline();
	}
	private boolean isClient()
	{
		return networkControl_.isClient();
	}
	
	private void showGamePanel()
	{
		mainFrame_.stopPaintVictory();
		mainFrame_.resetOnNewGame(game_);
		if (mainFrame_.showGamePanel())
			state_ = PAINT_STATE.GAME;
	}
	
	public void showNetworkPanel(final String msg)
	{
		boolean showing = false;
		if ( isOffline() )
			showing = mainFrame_.showServerSettings();
		else
			showing = mainFrame_.showPlayersSettings(isHosting());
		
		if (showing)
		{
			state_ = PAINT_STATE.OTHER_PANELS;
			if (msg != null)
				mainFrame_.displayMessage(msg, false);
		}
	}
	
	public void showWelcomePanel()
	{
		if (mainFrame_.showWelcomePanel())
			state_ = PAINT_STATE.WELCOME;
	}
	/* Interaction methods (keyboard) ***************************/
	/*public void escPressed()
	{
		switch (state_)
		{
		case WELCOME:
			quit();
			break;
			
		case GAME_OVER:
		case VICTORY:
			if ( isOffline() )
				showWelcomePanel();
			else
				showNetSettingsPanel();
			break;
			
		case GAME:
		case PAUSE:
			if (isOffline() || isHosting()) // client cannot stop game
				endGame();
			break;
			
		case OTHER_PANELS:
			showWelcomePanel();
			break;
		}
		
		mainFrame_.repaint();
	}*/
	public void onGamePanelEsc()
	{
		if (isGameRunning()) {
			endGame();
		} else if (isOffline()) {
			showWelcomePanel();
		} else {
			if ( isHosting() )
				onlineLastLevelChoice_ = null;
			showNetworkPanel("Try again =)");
		}
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
		

		
		if (isHosting())
			networkControl_.sendGameEnd();

		mainFrame_.paintIsGameover(true);

		state_ = PAINT_STATE.GAME_OVER;
		
		endGameDetails();
	}
	
	
	// also used in case of victory
	private void endGameDetails()
	{
		if (isEditorLevelPlayed_ || isOffline() || isHosting())
		{
			gameTimer_.cancel();
			itemFactory_.shutdown();
			mainFrame_.repaint();
		}

		if (isEditorLevelPlayed_)
		{
			isEditorLevelPlayed_ = false;
			mainFrame_.displayMessage("Back to editor in 3 seconds..", true);
			Utils.threadSleep(1000);
			mainFrame_.displayMessage("  2 seconds..", true);
			Utils.threadSleep(1000);
			mainFrame_.displayMessage("  1 second..", true);
			Utils.threadSleep(1000);
			mainFrame_.showLevelEditor();
		}
	}
	
	private boolean setLevelForNewGame(LevelChoiceInfo linfo)
	{
		GameSettings settings = GameSettings.getCurrentSettings();
		settings.numberOfSnakes_ = linfo.snakesNumber_;
		
		Level level;
		if (linfo.levelName_.equals(Constants.DEFAULT_LEVEL_NAME))
			level = new DefaultLevel(settings);
		else
		{
			try
			{
				level = LevelFileManager.readLevelFromDefaultDir(linfo.levelName_);
				level.setGameSettings(settings);
			}
			catch (Exception e)
			{
				Log.logErr("Cannot open level: " + e.getMessage());
				Log.logException(e);
				mainFrame_.displayMessage("Cannot open level: " + e.getMessage(), false);
				return false;
			}
		}
		
		game_ = new Game(this, level);
		
		return true;
		
	}
	
	public void playEditedGame(Level l)
	{
		mainFrame_.stopPaintVictory();
		isEditorLevelPlayed_ = true;
		
		game_ = new Game(Control.this, l);

		mainFrame_.toFront();
		mainFrame_.requestFocus();
		

		startItemFactory();

		startGameTimer(createLocalGameTimerTask());
		
		showGamePanel();
	}
	
	public Game getCurrentGame()
	{
		return game_;
	}
	
	public PlayersManager getPlayersManager()
	{
		return playerMgr_;
	}
	
	// Local game

	private void startLocalGame()
	{	
		startItemFactory();

		startGameTimer(createLocalGameTimerTask());
		
		showGamePanel();
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
	
	private void fireLevelChooser()
	{
		boolean shouldbehere =
					state_ == PAINT_STATE.WELCOME
				|| state_ == PAINT_STATE.GAME_OVER
				|| state_ == PAINT_STATE.VICTORY;
		assert shouldbehere : "Shuold not fire level chooser";
		
		// thread is used to block the level chooser
		new Thread("level-chooser")
		{
			public void run()
			{
				LevelChoiceInfo linfo = mainFrame_.chooseLevel();
				if (null == linfo) // cancelled
					return;
				
				boolean loaded = setLevelForNewGame(linfo);
				if (loaded)
					startLocalGame();
				else
					mainFrame_.displayMessage("Cannot create game", false);
			}
		}.start();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// SERVER/CLIENT game: One machine/game instance is the master, receives a signal
	// from all the slaves at each step, and forwards to every one.
	// Then a step can be taken in the game
	/////////////////////////////////////////////////////////////////////////////////////////
	
	private void startItemFactory()
	{
		if ( null != itemFactory_ )
			assert ! itemFactory_.isWorking() : "ItemFactory still working!!";
		
		itemFactory_ = new ItemFactory(this, Globals.SCORE_ITEMS_ONLY);
	}
	
	// start a game hosted remotely (this is a slave)
	public void startSlaveGame()
	{
		if (game_ == null)
		{
			mainFrame_.displayMessage("No game loaded now, sorry", false);
			networkControl_.sendErrorMessage();
			return;
		}
		
		boolean gamePanel = mainFrame_.showGamePanel();
		assert gamePanel;
		mainFrame_.paintIsGameover(false);
		mainFrame_.stopPaintVictory();
		showGamePanel();
	}
	

	// hosting game
	
	public void startMasterGame(LevelChoiceInfo linfo)
	{
		boolean loaded = setLevelForNewGame(linfo);
		if (loaded)
		{
			onlineLastLevelChoice_ = linfo;
			boolean gamePanel = mainFrame_.showGamePanel();
			assert gamePanel;
			startMasterGame();
		}
		else
			mainFrame_.displayMessage("Cannot create game", false);
		
		
	}
	
	private void startMasterGame()
	{
		assert isHosting() : "starting master game only on host";
		
		//int numSlaves = gameServer_.getSlaveNumber();
		//boolean hasSlaveClients = networkControl_.hasSlaveClients();
		pendingItems_.removeAllElements();
		slaveActions_.clear();
		
		//String errMsg = null;
		//Exception ex = null;

		if (networkControl_.hasSlaveClients())
		{
			try
			{
				sendNetworkLevel();
				networkControl_.sendStartCommand();
			}
			catch (IOException e)
			{
				//ex = e;
				String errMsg = "Cannot send game data: " + e.getMessage();
				mainFrame_.displayMessage(errMsg, false);
				Log.logErr(errMsg);
				Log.logException(e);
			}
		}

		startItemFactory();
		startGameTimer(createMasterGameTimerTask());
		showGamePanel();

	}
	
	// create and launch the updating, drawing thread
	private void startGameTimer(TimerTask task)
	{
		if (null != gameTimer_)
			gameTimer_.cancel();
		gameTimer_ = new Timer("game-update");

		lastFrameTime_ = System.currentTimeMillis();
		
		gameTimer_.scheduleAtFixedRate(task, 0, 1000 / Globals.FPS);

	}
	
	
	private TimerTask createMasterGameTimerTask()
	{
		final boolean hasSlaveClients = networkControl_.hasSlaveClients();
		return new TimerTask()
		{
			@Override
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

							if ( hasSlaveClients )
								networkControl_.sendItem(i);
						}
						pendingItems_ = new Vector<Item>();
					}
					
					// send actions
					String stepString = handleSlaveActions();
					
					if (hasSlaveClients)
					{
						try
						{
							networkControl_.sendStep(stepString);
						}
						catch (IOException e)
						{
							if (Globals.NETWORK_DEBUG)
								Log.logErr("ERROR: Could not send game data: "+e.getLocalizedMessage());
							showNetworkPanel("ERROR: Could not send game data: "+e.getLocalizedMessage());
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
	
	public void onGameKeyPressed(int key)
	{
		if (state_ == PAINT_STATE.GAME)
		{
			SnakeAction action = KeyMapping.getPressedAction(key);
			if (action != null) {
				int sid = action.getSnakeIndex();
				if (isOffline() || playerMgr_.isSnakeHere(sid)) {
					handleActionTaken(action);
				} else {
					Log.logErr("Key ignored; Remote snake");
				}
			}
		}
	}
	
	public void onGameKeyReleased(int key)
	{
		if (isGameRunning())
		{
			SnakeAction action = KeyMapping.getReleasedAction(key);
			if (action != null) {
				handleActionTaken(action);
			}
		}
	}
	
	private void handleActionTaken(SnakeAction action)
	{
		if ( isOffline() )
		{
			int snakeI = action.getSnakeIndex();
			action.perform(game_.getSnake(snakeI));
		}
		else if ( isHosting() )
			addSlaveAction(action);
		else
			networkControl_.sendAction(action);
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
		Item item = itemFactory_.createItem();
		if (item != null) // can be null if it's too hard to find a new place
			addItem(item);
	}
	

	// called by itemCreator
	public void addItem(Item item)
	{
		if (item != null && game_ != null)
		{
			if (isOffline())
				game_.addItem(item);
			else
			{
				assert isHosting() : "Non gameMaster creating Item!";
				synchronized (pendingItems_)
				{
					pendingItems_.add(item);
				}
			}
		}
	}
	
	/* Settings ******************************************/
	
	public void showSettingsPanel()
	{
		if (mainFrame_.showSettingsPanel())
			state_ = PAINT_STATE.OTHER_PANELS;
	}
	/*private void showNetSettingsPanel()
	{
		if ( isOffline() )
			assert false : "Network settings called when offline";
		else if ( isHosting() )
		{
			onlineLastLevelChoice_ = null;
			mainFrame_.showPlayersSettings(true);
		}
		else
			mainFrame_.showPlayersSettings(false);
	}*/
	

	/* Fire up the level editor *********************************/
	public void showLevelEditor()
	{
		if (mainFrame_.showLevelEditor())
			state_ = PAINT_STATE.OTHER_PANELS;
	}
	
	
	
	public void newPressed()
	{
		if (isGameRunning())
			return;
		if ( isOffline() )
			fireLevelChooser();
		else if ( isHosting() )
		{
			if ( null != onlineLastLevelChoice_)
				startMasterGame(onlineLastLevelChoice_);
		}
		// no power for client

	}
	
	public void quit()
	{
		System.out.println("ESC => exit");
		mainFrame_.setVisible(false);
		mainFrame_.dispose();
		System.exit(0);	
	}
	

	/* Pause *******************************/
	public void togglePause()
	{
		if ( ! isOffline()) // no pause in online mode
			return;
		
		if (state_ == PAINT_STATE.PAUSE)
		{
			state_ = PAINT_STATE.GAME;
			mainFrame_.paintIsGameInPause(false);

			// wake up factory
			itemFactory_.wakeup();
		}
		else if (state_ == PAINT_STATE.GAME)
		{
			itemFactory_.scheduleSleep();
			state_ = PAINT_STATE.PAUSE;
			mainFrame_.paintIsGameInPause(true);
		}
	}
	/*
	public void keyPressed(int key)
	{
		
		/* Always valid * /
		switch(key)
		{
		case VK_ESCAPE:
			escPressed();
			return;
			
		default:
			break;
		}
		
		/* Game time interaction */
		/*if (state_ == PAINT_STATE.GAME)
		{
			onKeyPressedAction(key);
		}* /
	

		/*if (state_ == PAINT_STATE.GAME || state_ == PAINT_STATE.PAUSE)
		{
			if (key == VK_P && isOffline()) // no pause in online mode
				togglePause();
		}* /
	}
	*/
	/*public void keyReleased(int key)
	{
		onKeyReleasedAction(key);
	}*/
	
	public void receiveSlaveAction(SnakeAction action)
	{
		addSlaveAction(action);
	}
	
	// return step String
	private String handleSlaveActions()
	{
		int n_snakes = game_.getAllSnakes().size();
		boolean[][] start = new boolean[n_snakes][4];
		Vector<SnakeAction> validActions = new Vector<SnakeAction>();
		
		// invalid actions will be kept for the next round
		HashSet<SnakeAction> invalidActions = new HashSet<SnakeAction>();
		
		

		synchronized (slaveActions_)
		{
			// first handle all start actions, which we validate anyways
			for (SnakeAction a : slaveActions_)
			{
				if (a.isStartAction())
				{
					start[a.getSnakeIndex()][a.getSimpleActionType()] = true;
					validActions.add(a);
				}
			}
			
			// then all end actions
			for (SnakeAction a : slaveActions_)
			{
				if (!a.isStartAction())
				{
					if (! start[a.getSnakeIndex()][a.getSimpleActionType()])
						validActions.add(a);
					else
						invalidActions.add(a);
				}
			}
			
			
			// finally apply the valid actions and send them
			//if (Globals.ACTION_DEBUG)
			//	Log.logErr("Action: (Step master game)" + validActions.size() + " actions");
			String stepString = "" + (char)validActions.size();
			for (SnakeAction a: validActions)
			{
				stepString += new String(ActionEncoder.encodeAction(a));
				a.perform(game_.getSnake(a.getSnakeIndex()));
				Log.logErr("Action: " + a);
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
		if (isHosting())
		{
			networkFeedback("A server is already running, you cannot join another!");
			return;
		}
		
		if (host == null || host.equals(""))
		{
			networkFeedback("No specified server");
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
			mainFrame_.displayMessage("Unknown host: "+ host, false);
			return;
		}
	}
	
	private void joinGame(InetAddress addr)
	{
		
		try
		{
			stopClient();
			
			OnlinePlayersPanel playersPanel = mainFrame_.getPlayersPanel();
			playerMgr_ = new PlayersManager(networkControl_, -1, playersPanel);
			playersPanel.setPlayersManager(playerMgr_);
			networkControl_.joinGame(addr);

			resetConnectedInfo();

			mainFrame_.resetPlayersNetworkPanel(0, networkControl_.onlineState());
			mainFrame_.showPlayersSettings(false);
			mainFrame_.displayMessage("Connected to " + addr, false);
		}
		catch (IllegalThreadStateException ite)
		{
			assert false : "IllegalThreadException in Control.joinGame()";
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Error: " + e.getLocalizedMessage(), false);
		}
	}
	
	public void clientJoined(ClientInfo info)
	{
		assert isHosting() : "clientJoined() only for server!";
		int cid = info.getClientId();
		clientId2Infos_.put(cid, info);
		if ( 0 != cid )
			displayServerMessage("New client accepted : " + info);
		updateConnectedInfo();
	}
	
	// this is announced by the server itself
	public void clientError(int cid, String reason)
	{
		assert isHosting() : "clientError() only for server!";
		goneClientHandling(cid, reason, true);
	}
	
	public void clientLeft(int cid, String msg)
	{
		assert isHosting() : "clientLeft() only for server!";
		goneClientHandling(cid, msg, false);
	}
	
	private void goneClientHandling(int cid, String msg, boolean isError)
	{
		ClientInfo ci = clientId2Infos_.remove(cid);
		
		assert null != ci : "null client to remove??";
		
		networkControl_.removeClient(cid);
		
		String s = ci + " " + (isError ? "error" : "left")
								+ ": " + msg;
		networkFeedback(s);
		
		playerMgr_.removeByCliendIt(cid);

		updateConnectedInfo();
	}
	
	/*public Vector<Integer> getValidPlayerIds(int totalnum)
	{
		Vector<Integer> validids = new Vector<Integer>();
		for (int i=0; i<totalnum; ++i)
			validids.add(i);
		
		for ( PlayerInfo pi : playerId2Infos_.values() )
		{
			Integer id = pi.getSnakeId();
			validids.remove(id);
		}
		
		return validids;
	}*/
	
	// called from GUI
	/*public void playersNumberFixed ( int n )
	{
		currentPlayerSeats_ = n;
	}*/
	
	/*public boolean canHaveMorePlayers(int snakesNumber)
	{
		assert ! isOffline() : "canHaveMorePlayers only online!";
		return snakesNumber > playerInfos_.size();
	}*/
	
	/*private boolean isPlayerNameUsed(String name)
	{
		for (PlayerInfo pi : playerId2Infos_.values() )
			if (pi.getName().equals(name))
				return true;
		return false;
	}*/
	
	// check if the new PlayerInfo is allowed..
	public void playerAddRequested (PlayerInfo pi)
	{
		assert isHosting() : "Only host should receive player add request";
		String msg = playerMgr_.checkAddPlayer(pi);
		if ( null == msg )// accepted
			playerMgr_.addPlayer(pi);
		else // rejected
			networkControl_.sendDenyAddPlayerRequest(pi.getClientId(), msg);
	}

	public void playerAddDenied(String denyReason) {
		playerMgr_.playerAddRequestDenied(denyReason);
	}
	
	public void onConnectionLost (boolean kicked)
	{
		stopClient();
		String msg = kicked ?
				"The server is stopping" :
				"Kicked by the server";
		showNetworkPanel(msg);
	}
	
	private void stopClient()
	{
		networkControl_.stopClient();
	}
	
	public void stopServer()
	{
		networkControl_.stopServer();
	}
	
	public void serverDied()
	{
		if ( isClient() )
		{
			mainFrame_.displayMessage("Server died... Sorry", false);
			if (state_ == PAINT_STATE.GAME)
				endGame();
			stopClient();
		}
	}
	
	public void hostGame()
	{
		int maxNumPlayers = Globals.ONLINE_DEFAULT_MAX_PLAYER_NUMBER;
		OnlinePlayersPanel playersPanel = mainFrame_.getPlayersPanel();
		playerMgr_ = new PlayersManager(networkControl_, maxNumPlayers, playersPanel);
		playersPanel.setPlayersManager(playerMgr_);
		boolean hosting = networkControl_.startHosting();
		if (hosting) {
			resetConnectedInfo();
	
			mainFrame_.resetPlayersNetworkPanel(maxNumPlayers, networkControl_.onlineState());
			mainFrame_.showPlayersSettings(true);
			
			displayServerMessage("Server started");
	
			String name =  "server";
			ClientInfo masterClientInfo = new ClientInfo(0, name);
			clientJoined(masterClientInfo);
		}
	}

	public void networkFeedback(String message)
	{
		mainFrame_.displayMessage(message, false);
	}
	
	public char getClientLetter(int clientId)
	{
		ClientInfo info = clientId2Infos_.get(clientId);
		if ( null == info )
			return '?';
		return info.getLetter();
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
	
	public void setRemoteClientInfos(HashMap <Integer, ClientInfo> clientId2Infos)
	{
		assert isClient() : "Only client should receive clients list";
		clientId2Infos_ = clientId2Infos;
		updateConnectedInfoGUI();
	}
	
	public void setRemotePlayerInfos(Vector <PlayerInfo> playerInfos, int numSeats)
	{
		assert isClient() : "Only client should receive players list";
		playerMgr_.setRemotePlayersInfo(playerInfos, numSeats);
		updateConnectedInfoGUI();
		//mainFrame_.getPlayersPanel().repaint();
		//mainFrame_.repaint();
	}
	
	
	private void resetConnectedInfo()
	{
		playerMgr_.reset();
		clientId2Infos_ = new HashMap <Integer, ClientInfo> ();
	}

	private void updateConnectedInfo()
	{
		assert isHosting() : "Connected info update only on server";
		updateConnectedInfoGUI();

		networkControl_.sendClientsList(clientId2Infos_.values());
		int numSeats = playerMgr_.getNumberSeats();
		Vector<Integer> unusedIds = playerMgr_.getUnusedColors();
		networkControl_.sendPlayersInfo(playerMgr_.getPlayerList(), unusedIds, numSeats);
	}
	
	private void updateConnectedInfoGUI()
	{
		Vector <String> clientNames = new Vector <String> ();
		
		for (ClientInfo ci : clientId2Infos_.values())
			clientNames.add(ci.toString());
		
		mainFrame_.setConnectedClients(clientNames);
		Vector<Integer> unusedIds = playerMgr_.getUnusedColors();
		mainFrame_.setPlayers(playerMgr_.getPlayerList(), unusedIds, this);
		
	}
	
	public void sendChatMessage(String message)
	{
		assert ! isOffline() : "Offline chat??";

		try
		{
			networkControl_.sendChatMessage(message);
			displayChatMessage(message, networkControl_.getClientId());
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Could not send message: "+e.getLocalizedMessage(), false);
		}
	}
	
	public void receiveChatMessage (TimestampedMessage msg)
	{
		int from = msg.from_;
		if ( isHosting() ) // we forward it to other if this is the server
			networkControl_.broadcastToSlavesExcept(msg, from);
		displayChatMessage(new String(msg.payload_), from);
	}
	
	public void onReceiveItem(byte[] itemRepr)
	{
		assert isClient() : "onReceiveItem when not client!";
		assert null != game_ : "Null game!!";

		game_.addItem(ItemEncoder.decodeItem(itemRepr));
	}
	
	public void sendNetworkLevel() throws IOException
	{
		assert isHosting() : "sendNetworkLevel() when not hosting!";

		try
		{
			networkControl_.sendLevel(game_.getLevel());
		} catch (Exception e) 
		{
			mainFrame_.displayMessage("Could not send network level: "+e.getMessage(), false);
			Log.logErr("Could not send network level: " + e.getMessage());
			Log.logException(e);
		}
	}

	public void receiveNetworkLevel(byte[] encoded)
	{
		assert isClient() : "receiveNetworkLevel when not client!";

		if (game_ != null)
			endGame();

		try
		{
			Level level = LevelEncoder.decodeLevel(encoded);
			game_ = new RemoteGame(this, level);
		} catch (Exception e)
		{
			mainFrame_.displayMessage("Could not receive network level: "+e.getMessage(), false);
			Log.logErr("Could not receive network level: " + e.getMessage());
			Log.logException(e);
		}
	}
	
	public void stepSlaveGame(String stepString)
	{
		assert null != game_ : "Null game!!";
		
		int actionSize = Constants.ENCODED_ACTION_SIZE;
		int actionsNr = stepString.charAt(0);
		stepString = stepString.substring(1);
		
		//if (Globals.ACTION_DEBUG)
		//	Log.logErr("Action (Step slave game): " + actionsNr + " actions");
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
	
	// there can be a tie
	public void snakesWon(Vector <Snake> ss)
	{
		assert ! isClient() : "snakesWon() when client!";
		
		state_ = PAINT_STATE.VICTORY;
		Vector <Color> colors = new Vector <Color> ();
		for (Snake s : ss)
			colors.add(s.getColor());
		mainFrame_.paintVictory(colors);
		
		if ( isHosting() )
			networkControl_.sendSnakesWon(ss);

		endGameDetails();
	}
	
	public void onSnakesWon(byte[] bs)
	{
		assert isClient() : "snakesWon() when not client!";
		if ( game_ != null )
		{
			Vector <Color> colors = new Vector <Color> ();
			for (byte b: bs)
			{
				Color color = GlobalColorTable.getSnakeColor(0xFF & b);
				colors.add(color);
			}
			mainFrame_.paintVictory(colors);
		}
	}

	public void snakeDied(Snake s, Pointd collision)
	{
		if ( isHosting() )
			networkControl_.sendSnakeDeath(s,collision);
	}

	public void onSnakeDeath(int s, Pointd p)
	{
		assert isClient() : "snakesWon() when not client!";
		assert null != game_ : "Null game!!";

		game_.kill(game_.getSnake(s), p);
	}
	
	public void snakeEncounteredItem(int snakeid, int itemid)
	{
		// TODO: accepted items should be taken at STEP
		if (isHosting())
			networkControl_.sendSnakeAcceptedItem(snakeid, itemid);

		// replace by a new item
		if ( isHosting() || isOffline() )
			requestNewItem();
	}
	
	public void onAcceptItem(int snakeIndex, int itemIndex)
	{
		assert isClient() : "onAcceptItem() when not client!";
		assert null != game_ : "Null game!!";

		game_.snakeTakesItem(snakeIndex, itemIndex);
	}

	
}
