package org.paulpell.miam.gui.editor.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import org.paulpell.miam.logic.draw.items.AllTheItems;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.levels.LevelEditorControl;

public enum EditorItemsToolsEnum
	implements IEditorTool
{
	SCORE("Score", AllTheItems.INDEX_SCORE),
	BANANA("Banana", AllTheItems.INDEX_BANANA),
	BANANA_SP("Banana special", AllTheItems.INDEX_BANANA_SPECIAL),
	LIGHTNING("Lightning", AllTheItems.INDEX_LIGHTNING),
	REVERSE("Reverse", AllTheItems.INDEX_REVERSO)
	;
	
	public final String name_;
	public final int itemIndex_;
	
	private EditorItemsToolsEnum(String name, int itemIndex)
	{
		name_ = name;
		itemIndex_ = itemIndex;
	}
	
	
	public class ItemToggleButton
		extends JToggleButton
	{
		int margin = 2; // px
		int width_;
		int height_;
		Color defaultBorderColor_;
		public ItemToggleButton(Icon icon)
		{
			super(icon);
			width_ = icon.getIconWidth() + 2 * margin;
			height_ = icon.getIconHeight() + 2 * margin;
			Dimension dim = new Dimension(width_, height_);
			setPreferredSize(dim);
			setMaximumSize(dim);
			setMinimumSize(dim);
			defaultBorderColor_ = super.getBackground();
		}
		
		@Override
		public void paint(Graphics g)
		{
			Color borderCol = isSelected() ? new Color(56,98,155) : defaultBorderColor_;
			g.setColor(borderCol);
			g.fillRect(0, 0, width_, height_);
			super.getIcon().paintIcon(this, g, margin, margin);
		}
	}
	
	public JToggleButton createToggleButton(final LevelEditorControl leControl)
	{
		ItemToggleButton b = new ItemToggleButton(AllTheItems.getImageIcon(itemIndex_));
		b.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				leControl.setTool(EditorItemsToolsEnum.this);
			}
		});
		return b;
	}
	
	public Item getItem(double x, double y)
	{
		return AllTheItems.items[itemIndex_].newItem(x, y);
	}
}

