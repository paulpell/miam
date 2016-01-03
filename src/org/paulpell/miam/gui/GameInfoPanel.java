package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.levels.VictoryCondition;

@SuppressWarnings("serial")
public class GameInfoPanel extends JPanel
{

	JLabel FPSLabel_;
	JPanel snakeInfoPanels_;
	JPanel levelInfoPanel_;
	

	public GameInfoPanel(Game game)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		
		levelInfoPanel_ = makeLevelPanel(game.getLevel());
		c.weighty = 0;
		c.gridy = 0;
		add(levelInfoPanel_, c);

		snakeInfoPanels_ = makeSnakePanels (game.getAllSnakes());
		c.weighty = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(3, 3, 3, 3);
		add(snakeInfoPanels_, c);
		
		FPSLabel_ = new JLabel("");
		c.weighty = 0;
		c.gridy = 2;
		add(FPSLabel_, c);
		
		Color borderColor = new Color(20, 15, 160);
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
	}
	
	private JPanel makeSnakePanels (Vector<Snake> snakes)
	{
		JPanel infoPanels = new JPanel();
		infoPanels.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 5, 0);
		for (Iterator<Snake> it = snakes.iterator(); it.hasNext();)
			infoPanels.add(new SnakeInfoPanel(it.next()), c);
		return infoPanels;
	}
	
	private JPanel makeLevelPanel (Level l)
	{
		JPanel levelPanel = new JPanel ();
		levelPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 5, 0);
		
		levelPanel.add(new JLabel("Level: " + l.getName()), c);
		
		String condString = "Conditions:";
		Vector<VictoryCondition> conditions = l.getVictoryConditions();
		if ( conditions.isEmpty() ) condString += "\n <none>";
		else
		{
			for ( VictoryCondition vc : conditions )
				condString += "\n " + vc.toString();
		}
		JTextPane condLabel = new JTextPane();
		condLabel.setText(condString);
		levelPanel.add(condLabel, c);
		
		return levelPanel;
	}

	public void displayActualFPS(int fps)
	{
		FPSLabel_.setText("FPS: " + fps);
	}
	
	@Override
	public void paint(Graphics g)
	{
		Component[] comps = snakeInfoPanels_.getComponents();
		for (int i=0; i<comps.length; ++i)
			((SnakeInfoPanel)comps[i]).update();
		super.paint(g);
	}

}
