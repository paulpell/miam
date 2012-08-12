package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import logic.Control;
import logic.Globals;

@SuppressWarnings("serial")
public class SettingsWindow extends JFrame {

	final Control control;
	private JTabbedPane tabs;
	
	public SettingsWindow(Control control) {
		super("Snakesss - Settings");
		this.control = control;
		
		tabs = new JTabbedPane(JTabbedPane.LEFT);
		
		tabs.add("Snakes", new SnakeSettings(control));
		tabs.add("Items", null); // TODO add items tab
		
		add(tabs);
		
		
		pack();
		setLocationRelativeTo(null);
	}
	
	public void setVisible(boolean v) {
		super.setVisible(v);
	}
}

class SetActionKeyListener implements KeyListener {
		int snakeIndex, action;
		JTextField tf;
		String originalKey;
		public SetActionKeyListener(int s, int a, JTextField tf) {
			snakeIndex = s; action = a;
			this.tf = tf;
		}
		public void keyPressed(KeyEvent arg0) {
			originalKey = tf.getText();
			tf.setText("");
		}
		public void keyReleased(KeyEvent arg0) {
			int key = arg0.getKeyCode();
			String keyText = KeyEvent.getKeyText(key);
			if (!Globals.setSnakeActionKey(snakeIndex, action, key)) {
				tf.setText(originalKey);
				String message = "The key " + keyText +
						" cannot be set, it is either reserved or already used by another snake.";
				JOptionPane.showMessageDialog(null, message, "Impossible", JOptionPane.ERROR_MESSAGE);
			}
			else {
				tf.setText(keyText);
			}
		}
		public void keyTyped(KeyEvent arg0) {}
}
