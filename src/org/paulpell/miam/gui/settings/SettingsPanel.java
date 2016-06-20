package org.paulpell.miam.gui.settings;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.paulpell.miam.gui.AbstractDisplayPanel;
import org.paulpell.miam.logic.Control;

@SuppressWarnings("serial")
public class SettingsPanel extends AbstractDisplayPanel
{

	final Control control;
	private JTabbedPane tabs;
	
	public SettingsPanel(Control control, JFrame parent)
	{
		super("Snakesss - Settings", parent);
		this.control = control;
		
		tabs = new JTabbedPane(JTabbedPane.LEFT);
		
		tabs.add("General", new GeneralSettingsSubPanel());
		tabs.add("Snakes", new SnakeSettingsSubPanel());
		tabs.add("Items", new ItemSettingsSubPanel());
		
		add(tabs);
	}

	@Override
	public void displayMessage(String message, boolean immediately)
	{
		assert false;
	}

	@Override
	public boolean canRemovePanel()
	{
		return true;
	}

	@Override
	public KeyListener getCurrentKeyListener(KeyEvent e) {
		return this;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			control.showWelcomePanel();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
