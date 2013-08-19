package gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ToolsPanel extends JPanel {
	
	final LevelEditor levelEditor;
	JToggleButton lastSelected;

	public ToolsPanel(LevelEditor le) {
		
		levelEditor = le;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		ButtonGroup toolButtons = new ButtonGroup(); // to select only one tool at a time
		
		add(Box.createVerticalGlue());
		// tool for a line
		final JToggleButton lineBut = new JToggleButton("line");
		lineBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				levelEditor.addLine();
			}
		});
		toolButtons.add(lineBut);
		add(lineBut);
		
		// tool for a rect
		final JToggleButton rectBut = new JToggleButton("rectangle");
		rectBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				levelEditor.addRect();
			}
		});
		toolButtons.add(rectBut);
		add(rectBut);
		
		// tool for a circle
		final JToggleButton circleBut = new JToggleButton("circle");
		circleBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				levelEditor.addCircle();
			}
		});
		toolButtons.add(circleBut);
		add(circleBut);

		add(Box.createVerticalStrut(5));
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createVerticalStrut(5));
		
		JButton cancelBut =new JButton("Cancel");
		cancelBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				levelEditor.cancelLast();
			}
		});
		add(cancelBut);
		add(Box.createVerticalGlue());
	}
	
}
