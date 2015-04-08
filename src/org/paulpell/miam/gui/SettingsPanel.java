package org.paulpell.miam.gui;

import javax.swing.JTabbedPane;

import org.paulpell.miam.logic.Control;

@SuppressWarnings("serial")
public class SettingsPanel extends AbstractDisplayPanel
{

	final Control control;
	private JTabbedPane tabs;
	
	public SettingsPanel(Control control)
	{
		super("Snakesss - Settings");
		this.control = control;
		
		tabs = new JTabbedPane(JTabbedPane.LEFT);
		
		tabs.add("General", new GeneralSettingsPanel());
		tabs.add("Snakes", new SnakeSettingsPanel());
		tabs.add("Items", new ItemSettingsPanel());
		
		add(tabs);
	}

	@Override
	public void displayMessage(String message)
	{
		// do nothing
	}
	
}
