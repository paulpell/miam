package org.paulpell.miam.gui.net;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.paulpell.miam.gui.GlobalAnimationTimer;

@SuppressWarnings("serial")
public class AddPlayerPanel extends JPanel
{
	JTextField nameTextField_;
	JComboBox<Integer> snakeIdChoice_;
	
	public AddPlayerPanel()
	{
		FlowLayout layout = new FlowLayout();
		setLayout(layout);

		add ( new JLabel("name:"));
		
		nameTextField_ = new JTextField(20);
		add ( nameTextField_ );
		
		snakeIdChoice_ = new JComboBox<Integer>();
		add ( snakeIdChoice_ );
		snakeIdChoice_.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				focusNameField();
			}
		});
		focusNameField();
	}
	
	private void focusNameField()
	{
		nameTextField_.requestFocus();
	}
	
	public void flashRed()
	{
		Border b = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red);
		setBorder(b);
		repaint();
		
		GlobalAnimationTimer.scheduleSingleTask(new TimerTask()
		{
			@Override
			public void run()
			{
				AddPlayerPanel.this.setBorder(null);
				AddPlayerPanel.this.repaint();
			}
		}, 200); // remove in 200 ms
	}
	
	public void reset()
	{
		nameTextField_.setText("");
		setDisplayedSnakeIds (new Vector <Integer> ());
		setBorder(null);
		repaint();
	}

	public void setDisplayedSnakeIds(Vector<Integer> validids)
	{
		DefaultComboBoxModel <Integer> model =
				new DefaultComboBoxModel<Integer>(validids);
		snakeIdChoice_.setModel(model);
		repaint();
	}
	
	public String getPlayerName()
	{
		return nameTextField_.getText();
	}
	
	public int getSnakeId()
	{
		if ( null != snakeIdChoice_ )
			return -1;
		return (Integer)snakeIdChoice_.getSelectedItem();
	}
}
