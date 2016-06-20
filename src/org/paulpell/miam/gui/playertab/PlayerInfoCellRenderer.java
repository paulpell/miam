package org.paulpell.miam.gui.playertab;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicArrowButton;

import org.paulpell.miam.gui.GlobalColorTable;
import org.paulpell.miam.logic.players.PlayerInfo;
import org.paulpell.miam.logic.players.PlayersManager;

public class PlayerInfoCellRenderer
	implements ListCellRenderer<PlayerInfo>
{

	private static Color selectedBorderColor = new Color(54, 87, 111);
	
	protected PlayersManager playersMgr_;
	

	
	private class NewPlayerChoice extends JPanel
	{
		private static final long serialVersionUID = -4989801359484832779L;

		NewPlayerChoice()
		{
			super();
			setLayout(new GridBagLayout());
			BasicArrowButton arrBut = new BasicArrowButton(SwingConstants.EAST);
			add(arrBut);
			arrBut.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(NewPlayerChoice.this, "player list -> action");
				}
			});
			add(getNobodyPanel());
		}
	}
	
	private JPanel getNobodyPanel()
	{
		Color col = new Color(187, 187, 187);
		JPanel panel = new JPanel();
		panel.add(new JLabel("<nobody>"));
		panel.setBackground(col);	
		return panel;
	}

	private JPanel getAddPlayerPanel()
	{
		return new NewPlayerChoice();
	}
	
	private JPanel getStandardPanel(PlayerInfo pi, int index, boolean isSelected)
	{
		JPanel panel = new JPanel();

		int pid = pi.getSnakeId();
		String text = index + ". " + pi.getName();
		panel.add(new JLabel(text));
		Color col = GlobalColorTable.getSnakeColor(pid)
			.brighter()
			.brighter();
		panel.setBackground(col);
		
		if (isSelected) {
			Border border = BorderFactory.createLineBorder(selectedBorderColor);
			panel.setBorder(border);
		}
		return panel;
	}
	
	@Override
	public Component getListCellRendererComponent(
			JList<? extends PlayerInfo> list,
			PlayerInfo pi,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
	{
		if ( pi == null && cellHasFocus) {
			return getAddPlayerPanel();
		}
		if ( pi == null ) {
			return getNobodyPanel();
		}
		
		return getStandardPanel(pi, index, isSelected);
	}

	public void setPlayersManager(PlayersManager playersMgr) {
		playersMgr_ = playersMgr;
	}

}
