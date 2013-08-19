package logic;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import net.GameClient;
import net.GameServer;


import static java.awt.event.KeyEvent.*;

import gui.GameWindow;
import gui.NetworkSettingsFrame;
import gui.SettingsWindow;
import gui.editor.LevelEditor;
import logic.actions.SnakeAction;
import logic.draw.Drawable;

public class Control {

	GAME_STATE state = GAME_STATE.WELCOME;
	Game game;
	Timer timer;
	TimerTask task;
	GameWindow gameWindow;
	SettingsWindow settingsWin;
	
	GameClient gameClient;
	
	int nextSnakeIndex;
	
	
	
	public Control() {
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				gameWindow = new GameWindow(Control.this); // exits on close
				settingsWin = new SettingsWindow(Control.this); // is invisible
			}
		});
		Globals.control = this;
	}
	
	public void log(String message) {
		System.out.println(message);
	}
	
	
	public GAME_STATE state() {
		return state;
	}
	
	public boolean isGameRunning() {
		return state == GAME_STATE.GAME;
	}
	
	
	/* Drawable methods ***************/
	
	public Iterator<Drawable> getDrawablesIterator() {
		return game.getDrawablesIterator();
	}
	

	/* game methods ***********************/
	
	public void endGame() {
		timer.cancel();
		state = GAME_STATE.GAME_OVER;
		game = null;
	}
	
	private void newGame() {
		nextSnakeIndex = 0;
		state = GAME_STATE.GAME;
		//drawables = new Vector<Drawable>();
		game = new Game(this);
		gameWindow.resetInfoPanel(game.getSnakes());
		
		// updating, drawing thread
		task = createTimerTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 1000 / Globals.FPS);
	}
	
	private TimerTask createTimerTask() {
		return new TimerTask() {
			public void run() {
				if (isGameRunning()) {
					game.update();
					gameWindow.repaint();
				}
			}
		};
	}
	
	
	/* Settings ******************************************/
	
	private void toggleSettingsWindowVisible() {
		boolean vis = settingsWin.isVisible();
		if (!vis && state != GAME_STATE.PAUSE) {
			togglePause();
		}
		settingsWin.setVisible(!vis);
	}
	

	/* Fire up the level editor *********************************/
	private void levelEditor() {
		new LevelEditor();
	}
	
	private void networkSettings() {
		new NetworkSettingsFrame();
	}
	
	/* Interaction methods (keyboard) ***************************/
	public void escPressed() {
		switch (state) {
		case WELCOME:
			System.out.println("ESC => exit");
			System.exit(0);	
			break;
		case GAME_OVER:
			state = GAME_STATE.WELCOME;
			break;
		case GAME:
			endGame();
			break;
		case PAUSE:
			state = GAME_STATE.GAME_OVER;
			break;
		}
		gameWindow.repaint();
	}
	
	public void newPressed() {
		if (state == GAME_STATE.WELCOME || state == GAME_STATE.GAME_OVER) {
			newGame();	
		}
	}
	
	/* Pause *******************************/
	private void togglePause() {
		if (state == GAME_STATE.PAUSE) {
			state = GAME_STATE.GAME;
			gameWindow.setPause(false);
		}
		else if (state == GAME_STATE.GAME) {
			state = GAME_STATE.PAUSE;
			gameWindow.setPause(true);
		}
	}
	
	public void keyPressed(int key) {
		/* Game time interaction */
		if (game != null) {
			SnakeAction action = Globals.getPressedAction(key);
			if (action != null) {
				//action.perform(game.getSnake(action.getSnakeIndex()));
				localAction(action);
			}
			else { // add other stuff to handle during the game
				if (key == VK_P) {
					togglePause();
				}
			}
		}
		
		else {
		/* outside game ****/
			switch(key) {
			case VK_S:
				toggleSettingsWindowVisible();
				break;
			case VK_ESCAPE:
				escPressed();
				break;
			case VK_SPACE: case VK_N:
				newPressed();
				break;
			case VK_E:
				levelEditor();
				break;
			case VK_O:
				networkSettings();
				break;
			default:
				break;
			}
		}
		
		/* Always valid */
		switch(key) {
		case VK_ESCAPE:
			escPressed();
			break;
		default:
			break;
		}
	}
	
	public void keyReleased(int key) {
		if (game != null) {
			/*SnakeAction action = Globals.getReleasedAction(key);
			if (action != null) {
				action.perform(game.getSnake(action.getSnakeIndex()));
			}*/
			localAction(Globals.getReleasedAction(key));
		}
	}
	
	public void localAction(SnakeAction action) {
		if (action != null) {
			action.perform(game.getSnake(action.getSnakeIndex()));
			if (Globals.online) {
				gameClient.sendAction(action);
			}
		}
	}
	
	public void onNetworkAction(SnakeAction action) {
		action.perform(game.getSnake(action.getSnakeIndex()));
	}

	public int getNextSnakeIndex() {
		return nextSnakeIndex++;
	}

	////////////////////////////////////////////
	// 
	// Network methods
	public void joinGame() {
		try {
			gameClient = new GameClient(this);
		} catch (IOException e) {
			System.err.println("Could not join a game: " + e.getLocalizedMessage());
		}
	}
	
	public void hostGame() {
		try {
			new GameServer();
			// snake 0 is the snake on host
			gameClient = new GameClient(this, 0);
		} catch (IOException e) {
			System.err.println("Could not start server: "+e.getLocalizedMessage());
		}
	}

}
