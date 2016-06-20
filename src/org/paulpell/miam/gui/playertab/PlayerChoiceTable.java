package org.paulpell.miam.gui.playertab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.border.Border;

import org.paulpell.miam.gui.GlobalColorTable;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.players.PlayerInfo;
import org.paulpell.miam.logic.players.PlayersManager;


/**
 * This class simply holds and displays a list of the players
 */

public class PlayerChoiceTable extends JPanel
	implements MouseListener, IPopupCloser
{
	private static final long serialVersionUID = -2779879498234982630L;
	

	int printHeight_ = 20; // pixels per player
	int selectedIndex_ = -1;
	int numPlayers_ = -1;
	
	int width_ = 150;
	int height_ = 0;
	
	
	PlayersManager playersMgr_;
	Popup popup_;
	AddPlayerPanel addPlayerPanel_;// content of the popup
	
	public PlayerChoiceTable()
	{
		addPlayerPanel_ = new AddPlayerPanel(this);
		addMouseListener(this);

		// TODO: Remove this test
		/*Dimension size = new Dimension(1, 1);
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		
		Log.logErr("creating new PlayerChoiceTable: " + hashCode());
*/

		Border b = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red);
		setBorder(b);
	}
	
	private void updateNumPlayers(int numPlayers)
	{
		if (numPlayers_ != numPlayers) {
			numPlayers_ = numPlayers;
			
			height_ = 13 + printHeight_ * playersMgr_.getNumberSeats();
			Dimension size = new Dimension(150, height_);
			setSize(size);
			setPreferredSize(size);
			setMinimumSize(size);
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		
		if (playersMgr_ == null) {
			Log.logErr("PlayerChoiceTable.paint: playersManager is null");
			return;
		}
		
		updateNumPlayers(playersMgr_.getNumberSeats());
		
		g.setColor(Constants.WELCOME_COLOR);
		g.fillRect(0, 0, width_, height_);
		// paint players
		Vector<PlayerInfo> pis = playersMgr_.getPlayerList();
		for (int i=0; i<pis.size(); ++i) {
			PlayerInfo pi = pis.get(i);
			assert pi != null : "Null PlayerInfo";
			Color c = GlobalColorTable.getSnakeColor(pi.getSnakeId())
				.brighter();
			g.setColor(c);
			String str = pi.getName();
			int y = printHeight_ * (i+1) - 5;
			g.drawString(str, 3, y);
		}
		// paint empty
		for (int i=pis.size(); i<playersMgr_.getNumberSeats(); ++i) {
			g.setColor( i == selectedIndex_ ? Color.yellow : Color.green);
			int y = printHeight_ * (i+1) - 5;
			g.drawString("<nobody>", 3, y);
		}
	}

	public void setPlayersManager(PlayersManager playersMgr)
	{
		playersMgr_ = playersMgr;
	}
	
	public boolean isAddingPlayer()
	{
		return popup_ != null;
	}
	
	private void onSelected(int i)
	{
		if ( i != selectedIndex_ && popup_ != null )
			closePopup();
		selectedIndex_ = i;
		repaint();
	}
	
	private void onDoubleClicked(int i)
	{
		Log.logErr("DoubleClicked: " + i);

		int n = playersMgr_.getNumberSeats();
		if (i >= n)
			return;

		Vector<PlayerInfo> pis = playersMgr_.getPlayerList();
		Vector<Integer> unusedIds = playersMgr_.getUnusedColors();
		if (i < pis.size()) {
			PlayerInfo pi = pis.get(i);
			addPlayerPanel_.reset(pi.getName(), pi.getSnakeId(), unusedIds);
		} else {
			String defaultName = PlayersManager.getDefaultPlayerName();
			addPlayerPanel_.reset(defaultName, unusedIds.get(0), unusedIds);
		}
		Point p = getLocationOnScreen();
		p.x += 13;
		p.y += 14 + i * printHeight_;
		PopupFactory pf = PopupFactory.getSharedInstance();
		popup_ = pf.getPopup(this, addPlayerPanel_, p.x, p.y);
		popup_.show();
		repaint();
	}


	final long maxDoubleClickTime = 250;
	long lastClickTime_ = System.currentTimeMillis();
	@Override
	public void mouseClicked(MouseEvent e) {
		long clickTime = System.currentTimeMillis();
		int i = e.getY() / printHeight_;
		if ( clickTime - lastClickTime_ < maxDoubleClickTime) {
			onDoubleClicked(i);
		} else {
			onSelected(i);
		}
		lastClickTime_ = clickTime;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// ok
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// ok
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// ok
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// ok
	}
	
	@Override
	public void onPopupOK()
	{
		String name = addPlayerPanel_.getPlayerName();
		int sid = addPlayerPanel_.getSnakeId();
		boolean isgood = false;
		if (selectedIndex_ < playersMgr_.getNumberSnakes())
			isgood = playersMgr_.tryUpdatePlayer(selectedIndex_, name, sid);
		else
			isgood = playersMgr_.tryAddPlayer(name, sid);
		if (isgood)
			closePopup();
		else
			addPlayerPanel_.flashRed();
		repaint();
	}
	
	@Override
	public void closePopup()
	{
		if (popup_ != null) {
			popup_.hide();
			popup_ = null;
		}
	}
	
}
