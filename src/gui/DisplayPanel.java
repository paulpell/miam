package gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DisplayPanel extends JPanel {
	
	Image i;
	
	public void setImage(Image i) {
		this.i = i;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(i, 0, 0, null);
	}
}
