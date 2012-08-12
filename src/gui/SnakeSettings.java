package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logic.Constants;
import logic.Control;
import logic.Globals;

@SuppressWarnings("serial")

public class SnakeSettings extends Box {
	

	final JTabbedPane keyControlsPane;

	public SnakeSettings(final Control control) {
		super(BoxLayout.Y_AXIS);
		
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
				//Control c = sw.control;
				control.snakeSpeedChanged(speedSlider.getValue());
				speedLabel2.setText("" + Globals.SNAKE_NORMAL_SPEED);
			}
		});
		speedSlider.setValue(Globals.SNAKE_NORMAL_SPEED);
		speedSlider.setMinimum(1);
		speedSlider.setMaximum(10);
		speedSlider.setPreferredSize(new Dimension(100,20));
		speedSliderPanel.add(speedSlider);
		speedPanel.add(speedSliderPanel);
		add(speedPanel, BorderLayout.NORTH);
		
		// ******************* classic mode
		JPanel classicModePanel = new JPanel();
		classicModePanel.add(new JLabel("Classic mode (square):"));
		final JCheckBox classicModeCB = new JCheckBox();
		classicModeCB.setSelected(Globals.USE_CLASSIC_SNAKE);
		classicModeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Globals.USE_CLASSIC_SNAKE = classicModeCB.isSelected();
			}
		});
		classicModePanel.add(classicModeCB);
		add(classicModePanel);
		

		// ************************* one panel to control nbr of snakes and their controls
		JPanel snakesPanel = new JPanel();
		
		// ******************** number of snakes
		JPanel snakeNoPanel = new JPanel();
		snakeNoPanel.add(new JLabel("Number of snakes:"));
		Vector<Integer> ss = new Vector<Integer>();
		for (int i=1; i<=Constants.MAX_NUMBER_OF_SNAKES; ++i) {
			ss.add(i);
		}
		final JComboBox<Integer> snakeNoList = new JComboBox<Integer>(ss);
		snakeNoList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListModel<Integer> lm = snakeNoList.getModel();
				Globals.NUMBER_OF_SNAKES =  lm.getElementAt(snakeNoList.getSelectedIndex());
				adjustControlsPane();
			}
		});
		snakeNoPanel.add(snakeNoList);
		snakesPanel.add(snakeNoPanel, BorderLayout.CENTER);
		
		// controls for each snake
		keyControlsPane = new JTabbedPane();
		adjustControlsPane();
		snakesPanel.add(keyControlsPane, BorderLayout.SOUTH);
		
		add(snakesPanel, BorderLayout.SOUTH);
		
	}
	
	

	private void adjustControlsPane() {
		keyControlsPane.removeAll();
		int ctrlsNo = 3;
		int 	act1 = Constants.SNAKE_ACTION_TURN_LEFT,
				act2 = Constants.SNAKE_ACTION_TURN_RIGHT,
				act3 = Constants.SNAKE_ACTION_SPEEDUP;
		
		for (int i=0; i<Globals.NUMBER_OF_SNAKES; ++i) {
			JPanel controlPanel = new JPanel(new GridLayout(ctrlsNo, 2));
			
			controlPanel.add(new JLabel("Turn left:"));
			final JTextField leftTF =
					new JTextField(Globals.getSnakeActionKeyRepr(i, act1));
			leftTF.addKeyListener(new SetActionKeyListener(i, act1, leftTF));
			controlPanel.add(leftTF);
			
			controlPanel.add(new JLabel("Turn right:"));
			final JTextField rightTF =
					new JTextField(Globals.getSnakeActionKeyRepr(i, act2));
			rightTF.addKeyListener(new SetActionKeyListener(i, act2, rightTF));
			controlPanel.add(rightTF);
			
			controlPanel.add(new JLabel("Speed up:"));
			final JTextField speedTF =
					new JTextField(Globals.getSnakeActionKeyRepr(i, act3));
			speedTF.addKeyListener(new SetActionKeyListener(i, act3, speedTF));
			controlPanel.add(speedTF);
			
			keyControlsPane.addTab("Snake" + i,controlPanel);
		}
	}
}
