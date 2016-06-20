package org.paulpell.miam.gui.playertab;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.paulpell.miam.gui.GlobalAnimationTimer;

@SuppressWarnings("serial")
public class AddPlayerPanel
	extends JPanel
{
	JTextField nameTextField_;
	JComboBox<Integer> snakeIdChoice_;
	IPopupCloser super_;
	
	public AddPlayerPanel(IPopupCloser popupCloser)
	{
		super_ = popupCloser;
		
		FlowLayout layout = new FlowLayout();
		setLayout(layout);

		add ( new JLabel("name:"));
		
		nameTextField_ = new JTextField(13);
		add ( nameTextField_ );
		nameTextField_.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					super_.onPopupOK();
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
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
		
		JButton add = new JButton("ok");
		add(add);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				super_.onPopupOK();
			}
		});
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
	
	public void reset(String name, int snakeId, Vector <Integer> unusedIds)
	{
		nameTextField_.setText(name);
		setDisplayedSnakeIds (unusedIds);
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
		int i = (Integer)snakeIdChoice_.getSelectedItem();
		return i;
	}
}
