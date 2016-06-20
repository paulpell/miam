package org.paulpell.miam.gui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
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
					WindowFocusListener,
					WindowListener
{

	JButton chooseButton_;
	LevelListPanel levelPanel_;
	JComboBox<Integer> snakeNoList_;
	
	int currentSelection_ = 0; // mod 2 to switch numSnakes - level
	
	private boolean userCancel_ = false; // to indicate that user pressed ESC
	
	public LevelChooserFrame(final JFrame f, GraphicsConfiguration gConf)
	{
		super(gConf);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createGUI(f);
			}
		});
	}
	
	private void createGUI(final JFrame parent)
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
		addWindowListener(this);

		pack();
		
		Rectangle bounds = getGraphicsConfiguration().getBounds();
		Point pLoc = parent.getLocation();		
		Dimension dim = getPreferredSize();
		Dimension parentDim = parent.getSize();
		
		int x = bounds.x + pLoc.x + (parentDim.width-dim.width)/2;
		int y = bounds.y + pLoc.y + (parentDim.height-dim.height)/2;
		setLocation(x, y);
		setVisible(true);
	}
	
	
	private void endWait()
	{
		synchronized (this)
		{
			notify();
		}
	}
	
	private void cancelDialog()
	{
		userCancel_ = true;
		endWait();
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
			Globals.NUMBER_OF_SNAKES = sNo;
			String lname = levelPanel_.getLevelName();
			linfo = new LevelChoiceInfo(lname, sNo);
		}
		
		setVisible(false);
		dispose();
		
		return linfo;
	}
	
	private void changeNumSnakes(int iDir)
	{
		int nItems = snakeNoList_.getItemCount();
		int iSel = snakeNoList_.getSelectedIndex();
		if (iSel <= 0 && iDir < 0)
			iSel = nItems - 1;
		else if (iSel >= nItems-1 && iDir > 0)
			iSel = 0;
		else
			iSel += iDir;
		snakeNoList_.setSelectedIndex(iSel);
	}
	
	// provide -1 for down, +1 for up
	private void keyArrow(int iDir)
	{
		switch (currentSelection_) {
		case 0:
			levelPanel_.incrementLevel(iDir);
			break;
		case 1: 
			changeNumSnakes(iDir);
			break;
		}
	}
	
	private void focusNextComponent()
	{
		currentSelection_ = 1 - currentSelection_;
		switch (currentSelection_) {
		case 0:
			levelPanel_.requestFocus();
			break;
		case 1: 
			snakeNoList_.requestFocus();
			break;
		}
	}


	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_TAB:
			focusNextComponent();
			break;
			
		case KeyEvent.VK_ENTER:
			endWait();
			break;
			
		case KeyEvent.VK_UP:
			keyArrow(1);
			break;

		case KeyEvent.VK_DOWN:
			keyArrow(-1);
			break;
			
		case KeyEvent.VK_ESCAPE:
			cancelDialog();
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

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		cancelDialog();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
