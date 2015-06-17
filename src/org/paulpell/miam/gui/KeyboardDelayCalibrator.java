package org.paulpell.miam.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class KeyboardDelayCalibrator
	extends JFrame
	implements KeyListener
{
	
	JLabel countLabel_;
	
	JLabel resFirstLabel_;
	JLabel resMeanLabel_;
	JLabel resMaxLabel_;
	JLabel resMinLabel_;
	
	long keyPressed_;
	long keyReleased_ = -1;
	
	LinkedList<Long> delays_;
	LinkedList<Long> delays2_;

	public KeyboardDelayCalibrator()
	{
		super("Calibrate key repeatition delay");
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints constr = new GridBagConstraints();
		add(new JLabel("Please keep any key pressed to 50"), constr);
		
		countLabel_ = new JLabel("Count : 0");
		constr = new GridBagConstraints();
		constr.gridy = 1;
		add(countLabel_, constr);
		

		resFirstLabel_ = new JLabel();
		constr = new GridBagConstraints();
		constr.gridy = 2;
		add(resFirstLabel_, constr);
		resMeanLabel_ = new JLabel();
		constr = new GridBagConstraints();
		constr.gridy = 3;
		add(resMeanLabel_, constr);
		resMaxLabel_ = new JLabel();
		constr = new GridBagConstraints();
		constr.gridy = 4;
		add(resMaxLabel_, constr);
		resMinLabel_ = new JLabel();
		constr = new GridBagConstraints();
		constr.gridy = 5;
		add(resMinLabel_, constr);
		
		delays_ = new LinkedList <Long> ();
		delays2_ = new LinkedList <Long> ();
		
		validate();
		setPreferredSize(new Dimension(200, 400));
		setLocationRelativeTo(null);
		setVisible(true);
		
		addKeyListener(this);
		
	}
	
	private void calibrate()
	{
		long sum = 0;
		long max = 0;
		long min = Long.MAX_VALUE;
		for (long l: delays_)
		{
			sum += l;
			if (l > max)
				max = l;
			if (l < min)
				min = l;
		}
		long sum2 = 0;
		long max2 = 0;
		long min2 = Long.MAX_VALUE;
		for (long l: delays2_)
		{
			sum2 += l;
			if (l > max2)
				max2 = l;
			if (l < min2)
				min2 = l;
		}
		
		resFirstLabel_.setText(		"First : " + delays_.get(0) + " - " + delays2_.get(0));
		resMeanLabel_.setText(		"Mean  : " + (sum / delays_.size()) + " - " + (sum2 / delays2_.size()));
		resMaxLabel_.setText(		"Max   : " + max + " - " + max2);
		resMinLabel_.setText(		"Min   : " + min + " - " + min2);
		
		delays_ = new LinkedList<Long>();		
		delays2_ = new LinkedList<Long>();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		keyPressed_ = System.nanoTime();
		if (keyReleased_ > -1)
		{
			
			delays_.add(keyPressed_ - keyReleased_);
			
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		keyReleased_ = System.nanoTime();
		
		if (delays_.size() < 50)
			delays2_.add(keyReleased_ - keyPressed_);
		else
			calibrate();
		
		countLabel_.setText("Count : " + delays_.size());

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
