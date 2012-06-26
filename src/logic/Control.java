package logic;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


import static java.awt.event.KeyEvent.*;

import gui.GUI;
import gui.SettingsWindow;
import logic.actions.SnakeAction;
import logic.draw.Drawable;

public class Control {

	GAME_STATE state = GAME_STATE.WELCOME;
	Game game;
	Timer timer;
	TimerTask task;
	GUI gui;
	SettingsWindow settingsWin;
	
	
	Vector<Drawable> drawables;
	
	public Control() {
		gui = new GUI(this);
		settingsWin = new SettingsWindow(this); // is invisible
	}
	
	public void log(String message) {
		System.out.println(message);
	}
	
	
	public GAME_STATE state() {
		return state;
	}
	
	public boolean gameRunning() {
		return state == GAME_STATE.GAME;
	}
	
	/* Drawable methods ***************/
	public void addDrawables(Collection<? extends Drawable> ds) {
		drawables.addAll(ds);
	}
	
	public void addDrawable(Drawable d) {
		drawables.add(d);
	}
	
	public boolean removeDrawable(Drawable d) {
		return drawables.remove(d);
	}
	
	public Iterator<Drawable> getDrawablesIterator() {
		return drawables.iterator();
	}
	

	/* game methods ***********************/
	
	public void endGame() {
		timer.cancel();
		state = GAME_STATE.GAME_OVER;
		game = null;
	}
	
	private void newGame() {
		state = GAME_STATE.GAME;
		drawables = new Vector<Drawable>();
		game = new Game(this);
		task = newTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 1000 / Globals.FPS);
	}
	
	private TimerTask newTask() {
		return new TimerTask() {
			public void run() {
				if (gameRunning()) {
					game.update();
					gui.repaint();
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
	
	public void fpsChanged(int fps) {
		Globals.FPS = fps;

		if (game != null) {
			timer = new Timer();
			task = newTask();
			timer.scheduleAtFixedRate(task, 0, 1000 / Globals.FPS);
			System.out.println("new fps");
		}
	}
	
	public void snakeSpeedChanged(int speed) {
		Globals.SNAKE_NORMAL_SPEED = speed;
		Globals.SNAKE_ANGLE_DIFF = 4 * speed;
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
		gui.repaint();
	}
	
	public void newPressed() {
		if (state == GAME_STATE.WELCOME || state == GAME_STATE.GAME_OVER) {
			newGame();	
		}
	}
	
	/* Pause *******************************/
	/*private void setPause() {
		if (state == GAME_STATE.GAME) {
			state = GAME_STATE.PAUSE;
			gui.setPause(true);
		}
	}*/
	private void togglePause() {
		if (state == GAME_STATE.PAUSE) {
			state = GAME_STATE.GAME;
			gui.setPause(false);
		}
		else if (state == GAME_STATE.GAME) {
			state = GAME_STATE.PAUSE;
			gui.setPause(true);
		}
	}
	
	public void keyPressed(int key) {
		/* Game time interaction */
		if (game != null) {
			SnakeAction action = Globals.getPressedAction(key);
			if (action != null) {
				action.perform(game.getSnake(action.getSnakeIndex()));
			}
			else { // add other stuff to handle during the game
				if (key == VK_P) {
					togglePause();
				}
			}
		}
		
		/* All time ****/
		if (key == VK_S) {
			toggleSettingsWindowVisible();
		}
		if (key == VK_ESCAPE) {
			escPressed();
		}
		if (key == VK_SPACE || key == VK_N) {
			newPressed();
		}
	}
	
	public void keyReleased(int key) {
		if (game != null) {
			SnakeAction action = Globals.getReleasedAction(key);
			if (action != null) {
				action.perform(game.getSnake(action.getSnakeIndex()));
			}
		}
	}

}
