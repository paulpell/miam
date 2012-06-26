package gui;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.util.Iterator;

import javax.swing.JFrame;


import logic.GAME_STATE;
import logic.Control;
import logic.draw.Drawable;


@SuppressWarnings("serial")
public class GUI extends JFrame implements KeyListener {
	
	Control control;
	
	DisplayPanel drawPanel;
	int width = logic.Constants.IMAGE_WIDTH,
			height = logic.Constants.IMAGE_HEIGHT;
	BufferedImage image;
	
	boolean pause = false;
	

	public GUI(Control gc) throws HeadlessException {
		super("Snakesss");
		control = gc;
		
		// initialize the graphical stuff
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		drawPanel = new DisplayPanel();
		drawPanel.setPreferredSize(new Dimension(width, height));
		add(drawPanel);
		pack();
		
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		setVisible(true);
	
	}
	
	/* Drawing methods ******************************************************
	 * We will paint on the BufferedImage image, which will be displayed on drawPanel
	 */
	
	public void setPause(boolean pause) {
		this.pause = pause;
		repaint();
	}
	
	// paints a menu
	private void welcome() {
		int x = 170, y = 170;
		int delta = 40;
		Graphics imGr = image.createGraphics();
		Color bgColor = new Color(120, 20, 80);
		Color textColor = new Color(10, 220, 10);
		imGr.setColor(bgColor);
		imGr.fillRect(0,0,width, height);
		imGr.setColor(textColor);
		Font font = imGr.getFont();
		font = font.deriveFont((font.getSize2D() * 2));
		imGr.setFont(font);
		imGr.drawString("Enjoy!", x, y);
		imGr.drawString("ESC to leave", x, y + delta);
		imGr.drawString("n/space for a new game", x, y + 2*delta);
		imGr.drawString("s to access the settings", x, y + 3*delta);
	}
	
	// paints the list of objects on a black background
	private void draw_game() {
		Graphics imGr = image.createGraphics();
		imGr.setColor(new Color(0,0,0));
		imGr.fillRect(0, 0, width, height);
		for (Iterator<Drawable> e = control.getDrawablesIterator(); e.hasNext();) {
			Drawable d = e.next();
			d.draw(imGr);
		}
		
		// pause
		if (pause) {
			for (int i=0; i < 5; i++) {
				imGr.setColor(new Color(100 + 10 * i, 20 + 20 * i, 30 + 30 * i));
				imGr.drawString("PAUSE", width / 2 - 40 + i, height / 2 - 100 + i);
			}
		}
	}
	
	public void paint(Graphics g) {
		GAME_STATE state = control.state();
		switch (state) {
		case PAUSE:
		case GAME:
			draw_game();
			break;
		case WELCOME:
			welcome();
			break;
		case GAME_OVER:
			Graphics gr = image.createGraphics();
			gr.setColor(new Color(255,20,30));
			gr.drawString("GAME OVER", width / 2 - 75, height / 2 - 100);
			gr.drawString("n/space for new", width / 2 - 75, height / 2 - 70);
			gr.drawString("ESC for the main menu", width / 2 - 75, height / 2 - 40);
			break;
		}
		drawPanel.setImage(image);
		drawPanel.repaint();
	}
	
	/* User keyboard interface ***********************************/

	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		//System.out.println("key pressed: " + keyCode);
		control.keyPressed(keyCode);
	}

	public void keyReleased(KeyEvent arg0) {
		control.keyReleased(arg0.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0) {}


}
