package org.paulpell.miam.logic;

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
import org.paulpell.miam.gui.MainFrame;
import org.paulpell.miam.gui.editor.LevelEditor;
import org.paulpell.miam.logic.PAINT_STATE;
import org.paulpell.miam.logic.actions.EndAction;
import org.paulpell.miam.logic.actions.SnakeAction;
import org.paulpell.miam.logic.actions.StartAction;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.ItemFactory;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.net.ActionEncoder;
import org.paulpell.miam.net.Client;
import org.paulpell.miam.net.ItemEncoder;
import org.paulpell.miam.net.MasterClient;
import org.paulpell.miam.net.NetMethods;
import org.paulpell.miam.net.Server;
import org.paulpell.miam.net.SettingsEncoder;



import static java.awt.event.KeyEvent.*;


public class Control
{

	PAINT_STATE state_ = PAINT_STATE.WELCOME;
	Game game_;
	
	Timer timer_;
	TimerTask task_;
	long lastSchedule_;
	
	MainFrame mainFrame_;

	// the server may be hosted in this instance
	Server gameServer_;
	boolean isOnline_;
	boolean isServer_ = false; // whether game is hosted here
	HashSet<SnakeAction> slaveActions_ = new HashSet <SnakeAction>(); // received at each turn by the slaves, sent out to all of them
	Vector<Item> pendingItems_ = new Vector <Item>(); // stored to add them on next round synchronously
	
	// the local player will command this client
	Client gameClient_;

	private String playerName_;
	private HashMap<Integer, String> clientId2Name_;
	
	
	
	private ItemFactory itemFactory_;
	
	
	
	
	public Control()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				mainFrame_ = new MainFrame(Control.this); // will appear and stay
			}
		});
		Globals.control = this;
		clientId2Name_ = new HashMap<Integer, String> ();
	}
	


	public void onClose()
	{
		gameClient_.end();
		if (gameServer_ != null)
			gameServer_.end();
	}
	
	
	public PAINT_STATE state()
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
	
	/* Drawable methods ***************/
	
	public Iterator<Drawable> getDrawablesIterator() {
		return game_ == null ? null : game_.getDrawablesIterator();
	}
	

	/* game methods ***********************/
	
	public void endGame()
	{
		if (!isOnline_ || isServer_)
		{
			timer_.cancel();
			itemFactory_.shutdown();
		}
		
		if (isOnline_ && isServer_)
			gameClient_.sendGameEnd();
		
		state_ = PAINT_STATE.GAME_OVER;
		mainFrame_.paintIsGameover(true);
	}
	
	
	private void createGame()
	{
		// create settings from Globals
		GameSettings settings = new GameSettings();
		game_ = new Game(this, settings);
		state_ = PAINT_STATE.GAME;
	}
	
	
	
	// Local game

	private void startLocalGame()
	{
		isOnline_ = false;
		isServer_ = false;
		
		startItemFactory();
		for (int i=0; i<10; ++i)
		{
			Item item = itemFactory_.createItem(game_.getSnakes(), game_.getItems());
			game_.addItem(item);
		}
		
		// updating, drawing thread
		task_ = createLocalGameTimerTask();
		timer_ = new Timer();
		 
		timer_.scheduleAtFixedRate(task_, 0, 1000 / Globals.FPS);

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
	{if (game_ == null)
		{
			networkFeedback("No game loaded now, sorry");
			gameClient_.sendErrorMessage();
			return;
		}
		
		state_ = PAINT_STATE.GAME;

		mainFrame_.paintIsGameover(false);
		showGameWindow();
	}
	

	// hosting game
	public void startMasterGame()
	{

		
		int numSlaves = gameServer_.getSlaveNumber();
		
		createGame();
	/*	int numSnakes = game_.getNumberOfSnakes();
	/	pendingStartTurnLeft_ = new StartTurnLeftAction[numSnakes];
		pendingEndTurnLeft_ = new EndTurnLeftAction[numSnakes];
		pendingStartTurnRight_ = new StartTurnRightAction[numSnakes];
		pendingEndTurnRight_ = new EndTurnRightAction[numSnakes];
		for (int i=0; i<numSnakes; ++i)
		{
			pendingStartTurnLeft_[i] = null;
			pendingEndTurnLeft_[i] = null;
			pendingStartTurnRight_[i] = null;
			pendingEndTurnRight_[i] = null;
		}*/
		
		if (numSlaves > 0)
		{
			try
			{
				sendNetworkSettings();
				if (Globals.NETWORK_DEBUG)
					Log.logMsg("Send START, id="+gameClient_.getServerId());
				gameClient_.sendStartCommand();
			}
			catch (IOException e)
			{
				networkFeedback("Could not start the game: "+e.getLocalizedMessage());
			}
		}
		
		showGameWindow();
		
		startItemFactory();
		for (int i=0; i<Globals.ITEM_START_NUMBER; ++i)
		{
			Item item = itemFactory_.createItem(game_.getSnakes(), game_.getItems());
			addItem(item);
		}
		
		// updating, drawing thread
		task_ = createMasterGameTimerTask();
		timer_ = new Timer();
		timer_.scheduleAtFixedRate(task_, 0, 1000 / Globals.FPS);
		
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
			// remove previous end action if present
			for (SnakeAction a : slaveActions_)
			{
				if (a.getActionType() == action.getActionType() // same action type
						&& a.getSnakeIndex() == action.getSnakeIndex() // same snake
						&& EndAction.class.isAssignableFrom(a.getClass())) // end action
				{
					slaveActions_.remove(a);
					break;
				}
			}
			slaveActions_.add(action);
		}
	}
	
	// called by game
	public void requestNewItem()
	{
		Item item = itemFactory_.createItem(game_.getSnakes(), game_.getItems());
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
		new LevelEditor();
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
			state_ = PAINT_STATE.WELCOME;
			mainFrame_.showWelcomePanel();
			break;
			
		case GAME:
		case PAUSE:
			endGame();
			break;
		}
		
		mainFrame_.repaint();
	}
	
	public void newPressed()
	{
		if (state_ == PAINT_STATE.WELCOME || state_ == PAINT_STATE.GAME_OVER)
		{
			state_ = PAINT_STATE.GAME;
			if (isServer_)
				startMasterGame();
			else if (!isOnline_)
			{
				createGame();
				startLocalGame();
			}
		}
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
				if (StartAction.class.isAssignableFrom(a.getClass()))
				{
					start[a.getSnakeIndex()][a.getActionType()] = true;
					validActions.add(a);
				}
			}
			
			// then all end actions
			for (SnakeAction a : slaveActions_)
			{
				if (! StartAction.class.isAssignableFrom(a.getClass()))
				{
					if (! start[a.getSnakeIndex()][a.getActionType()])
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
			networkFeedback("Already hosting a game!");
			return;
		}
		
		if (host == null || host.equals(""))
		{
			networkFeedback("Please choose a server");
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
			mainFrame_.showPlayersSettings(false);
			mainFrame_.displayMessage("Connected to " + addr);
		}
		catch (IllegalThreadStateException ite)
		{
			assert false : "IllegalThreadException in Control.joinGame()";
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Could not join the game: " + e.getLocalizedMessage());
		}
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
			gameServer_.setPlayerMaxNumber(Globals.ONLINE_DEFAULT_PLAYER_NUMBER);
			isServer_ = true;
			isOnline_ = true;
			
			playerName_ = "The_boss";
			
			clientId2Name_ = new HashMap<Integer, String> ();
			clientId2Name_.put(0, playerName_);
			
			mainFrame_.showPlayersSettings(true);
		}
		catch (IOException e)
		{
			mainFrame_.displayMessage("Could not start server: "+e.getLocalizedMessage());
		}
	}
	
	public void setPlayerName(int id, String name)
	{
		clientId2Name_.put(id, name);
	}
	
	public HashMap<Integer, String> getPlayerNamesMap()
	{
		return clientId2Name_;
	}



	public void networkFeedback(String message)
	{
		mainFrame_.displayMessage(message);
	}
	
	public void displayChatMessage(String message, int from)
	{
		String name = clientId2Name_.get(from);
		if (null == name)
			name = "" + from;
		networkFeedback(name + ": " + message);
	}
	
	public void displayServerMessage(String message)
	{
		networkFeedback(message);
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
	
	public void onReceiveItem(String itemRepr)
	{
		if (isOnline_)
			game_.addItem(ItemEncoder.decodeItem(itemRepr, game_));
	}
	
	public void sendNetworkSettings() throws IOException
	{
		if (isOnline_)
			gameClient_.sendGameSettings(game_);
	}

	public void receiveNetworkSettings(String settings)
	{
		if (isOnline_ && !isServer_)
		{
			if (game_ != null)
				endGame();
	
			game_ = SettingsEncoder.decodeSettings(this, gameClient_, settings);
		}
	}
	
	public void stepGame(String stepString)
	{
		Log.logErr("Game step, stepString.length() = " + stepString.length() + "stepString= " + stepString);
		int actionsNr = stepString.charAt(0);
		stepString = stepString.substring(1);
		
		for (int i=0; i<actionsNr; ++i)
		{
			String action = stepString.substring(0,6);
			stepString = stepString.substring(6);
			
			SnakeAction a = ActionEncoder.decodeAction(action.getBytes());
			a.perform(game_.getSnake(a.getSnakeIndex()));
			Log.logErr("Action: " + a);
		}
		
		
		game_.update();
		mainFrame_.repaint();
	}



	public void snakeDied(Snake s, Pointd collision)
	{
		game_.kill(s, collision);
		if (isOnline_)
			gameClient_.sendSnakeDeath(s,collision);
	}

	public void onSnakeDeath(int s, Pointd p)
	{
		if (isOnline_)
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
		if (isOnline_)
		{
			Item i = game_.getItems().remove(itemIndex);
			Snake s = game_.getSnake(snakeIndex);
			s.acceptItem(i);
		}
	}

	
}
