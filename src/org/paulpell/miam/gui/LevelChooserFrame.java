package org.paulpell.miam.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.paulpell.miam.logic.draw.items.AllTheItems;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.levels.LevelChoiceInfo;

@SuppressWarnings("serial")
public class LevelChooserFrame
		extends JFrame
		implements KeyListener,
					WindowFocusListener
{

	JButton chooseButton_;
	LevelListPanel levelPanel_;
	JComboBox<Integer> snakeNoList_;
	
	private boolean userCancel_ = false; // to indicate that user pressed ESC
	
	public LevelChooserFrame(final Frame f)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createGUI(f);
			}
		});
	}
	
	private void createGUI(final Frame parent)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c;
		
		// number of snakes
		GridBagLayout snakesNoLayout = new GridBagLayout();
		c = new GridBagConstraints();
		c.gridy = 0;
		JPanel snakeNoPanel = new JPanel(snakesNoLayout);
		snakeNoPanel.add(new JLabel("Snakes:"), c);
		
		Vector<Integer> ss = new Vector<Integer>();
		for (int i=1; i<=Constants.MAX_NUMBER_OF_SNAKES; ++i)
			ss.add(i);
		snakeNoList_ = new JComboBox<Integer>(ss);
		snakeNoList_.setSelectedIndex(Globals.NUMBER_OF_SNAKES - 1);
		c = new GridBagConstraints();
		c.gridy = 1;
		snakeNoPanel.add(snakeNoList_, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);
		add(snakeNoPanel, c);
		
		// level list
		LevelListPanel.Orientation o = LevelListPanel.Orientation.VERTICAL;
		levelPanel_ = new LevelListPanel(o);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);
		add(levelPanel_, c);
		
		// start button
		Icon lightningIcon = AllTheItems.getImageIcon(AllTheItems.INDEX_LIGHTNING);
		chooseButton_ = new JButton("Start", lightningIcon);
		chooseButton_.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				endWait();
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0;
		c.insets = new Insets(25, 5, 15, 5);
		add(chooseButton_, c);
		
		addWindowFocusListener(this);

		setLocationRelativeTo(parent);
		pack();
		setVisible(true);
	}
	
	
	private void endWait()
	{
		synchronized (this)
		{
			notify();
		}
	}
	
	// returns null if user cancels (esc)
	public LevelChoiceInfo getLevelInfo()
	{

		synchronized (this)
		{
			try
			{
				wait();
			} catch (InterruptedException e) 
			{
				Log.logException(e);
			}
		}
		
		LevelChoiceInfo linfo = null;
		
		if ( ! userCancel_ )
		{
			ListModel<Integer> lm = snakeNoList_.getModel();
			int sNo = lm.getElementAt(snakeNoList_.getSelectedIndex());
			String lname = levelPanel_.getLevelName();
			linfo = new LevelChoiceInfo(lname, sNo);
		}
		
		setVisible(false);
		dispose();
		
		return linfo;
	}


	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_ENTER:
			endWait();
			break;
			
		case KeyEvent.VK_UP:
			levelPanel_.incrementLevel(1);
			break;

		case KeyEvent.VK_DOWN:
			levelPanel_.incrementLevel(-1);
			break;
			
		case KeyEvent.VK_ESCAPE:
			userCancel_ = true;
			endWait();
			break;
			
		default:
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {}

	@Override
	public void windowLostFocus(WindowEvent arg0)
	{
		toFront();
		requestFocus();
	}

}
