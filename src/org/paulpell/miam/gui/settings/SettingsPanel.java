package org.paulpell.miam.gui.settings;

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
	public void displayMessage(String message)
	{
		// do nothing
	}
	
}
