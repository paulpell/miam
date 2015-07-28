package org.paulpell.miam.gui.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.paulpell.miam.gui.KeyMapping;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Globals;



class SetActionKeyListener  implements KeyListener
{
		int snakeIndex_;
		int action_;
		JTextField textField_;
		String originalKey_;
		
		public SetActionKeyListener(int s, int a, JTextField tf)
		{
			snakeIndex_ = s;
			action_ = a;
			this.textField_ = tf;
		}
		
		public void keyPressed(KeyEvent arg0)
		{
			originalKey_ = textField_.getText();
			textField_.setText("");
		}
		
		public void keyReleased(KeyEvent arg0)
		{
			int key = arg0.getKeyCode();
			String keyText = KeyEvent.getKeyText(key);
			if (!KeyMapping.setSnakeActionKey(snakeIndex_, action_, key))
			{
				textField_.setText(originalKey_);
				String message = "The key " + keyText +
						" cannot be set, it is either reserved or already used by another snake.";
				JOptionPane.showMessageDialog(null, message, "Impossible", JOptionPane.ERROR_MESSAGE);
			}
			else
				textField_.setText(keyText);
		}
		
		public void keyTyped(KeyEvent arg0) {}
}


@SuppressWarnings("serial")
public class SnakeSettingsSubPanel extends Box
{
	
	
	/*
	 * This panel contains the settings concerning the snakes:
	 *  - their speed
	 *  - number of snakes for the next round
	 *  - control keys for each snake
	 */

	final JTabbedPane keyControlsPane;

	public SnakeSettingsSubPanel()
	{
		super(BoxLayout.Y_AXIS);
		

		// ******************** use width_ for snakes?
		JPanel useWidthPanel = new JPanel();
		useWidthPanel.add(new JLabel("Snakes are wide:"));
		final JCheckBox useWidthCB = new JCheckBox();
		useWidthCB.setSelected(Globals.SNAKE_USE_WIDTH);
		useWidthCB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Globals.SNAKE_USE_WIDTH = useWidthCB.isSelected();
			}
		});
		useWidthPanel.add(useWidthCB);
		add(useWidthPanel);
	
		
		// **************** slider for snake speed
		final JPanel speedPanel = new JPanel();
		final JLabel speedLabel1 = new JLabel("speed [1-10]");
		speedPanel.add(speedLabel1);
		JPanel speedSliderPanel = new JPanel();
		final JLabel speedLabel2 = new JLabel("(" + Globals.SNAKE_NORMAL_SPEED + ")");
		speedSliderPanel.add(speedLabel2);
		final JSlider speedSlider = new JSlider();
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int newSpeed = speedSlider.getValue();
				Globals.SNAKE_NORMAL_SPEED = newSpeed;
				speedLabel2.setText("" + newSpeed);
			}
		});
		speedSlider.setValue(Globals.SNAKE_NORMAL_SPEED);
		speedSlider.setMinimum(1);
		speedSlider.setMaximum(10);
		speedSlider.setPreferredSize(new Dimension(100,20));
		speedSliderPanel.add(speedSlider);
		speedPanel.add(speedSliderPanel);
		add(speedPanel);
		
		
		

		// ************************* one panel to control number of snakes and their controls
		JPanel snakesPanel = new JPanel();
		
		// controls for each snake
		keyControlsPane = new JTabbedPane();
		adjustControlsPane();
		snakesPanel.add(keyControlsPane, BorderLayout.SOUTH);
		
		add(snakesPanel);
		
	}
	
	

	private void adjustControlsPane()
	{
		keyControlsPane.removeAll();
		int ctrlsNo = 4;
		
		//for (int i=0; i<Globals.NUMBER_OF_SNAKES; ++i) {
		for (int i=0; i<Constants.MAX_NUMBER_OF_SNAKES; ++i)
		{
			JPanel controlPanel = new JPanel(new GridLayout(ctrlsNo, 2));
			
			controlPanel.add(new JLabel("Turn left:"));
			final JTextField leftTF =
					new JTextField(KeyMapping.getSnakeActionKeyRepr(i, Constants.SNAKE_ACTION_TURN_LEFT));
			leftTF.addKeyListener(
					new SetActionKeyListener(i, Constants.SNAKE_ACTION_TURN_LEFT, leftTF));
			controlPanel.add(leftTF);
			
			controlPanel.add(new JLabel("Turn right:"));
			final JTextField rightTF =
					new JTextField(KeyMapping.getSnakeActionKeyRepr(i, Constants.SNAKE_ACTION_TURN_RIGHT));
			rightTF.addKeyListener(
					new SetActionKeyListener(i, Constants.SNAKE_ACTION_TURN_RIGHT, rightTF));
			controlPanel.add(rightTF);
			
			controlPanel.add(new JLabel("Speed up:"));
			final JTextField speedTF =
					new JTextField(KeyMapping.getSnakeActionKeyRepr(i, Constants.SNAKE_ACTION_SPEEDUP));
			speedTF.addKeyListener(
					new SetActionKeyListener(i, Constants.SNAKE_ACTION_SPEEDUP, speedTF));
			controlPanel.add(speedTF);
			
			controlPanel.add(new JLabel("Special:"));
			final JTextField specialTF =
					new JTextField(KeyMapping.getSnakeActionKeyRepr(i, Constants.SNAKE_ACTION_SPECIAL));
			specialTF.addKeyListener(
					new SetActionKeyListener(i, Constants.SNAKE_ACTION_SPECIAL, specialTF));
			controlPanel.add(specialTF);
			
			keyControlsPane.addTab("Snake" + (i+1),controlPanel);
		}
		

		doLayout();
		
		Component p = getParent();
		while (null != p && !(p instanceof JFrame))
		{
			if (p instanceof JFrame)
				((JFrame)p).pack();
			else 
			{
				if (p instanceof JPanel)
					((JPanel)p).doLayout();
				p = p.getParent();
			}
		}
	}
}
