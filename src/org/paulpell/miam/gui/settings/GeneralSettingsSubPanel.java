package org.paulpell.miam.gui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.paulpell.miam.logic.Globals;


@SuppressWarnings("serial")
public class GeneralSettingsSubPanel extends JPanel
{
	final JLabel FPSLabel_;
	final JSlider FPSSlider_;
	
	final JCheckBox animationCB_ = new JCheckBox();
	final JCheckBox animationParticlesCB_ = new JCheckBox();
	final JCheckBox blurCB_ = new JCheckBox();

	public GeneralSettingsSubPanel()
	{
		GridBagLayout layout = new GridBagLayout(); 
		setLayout(layout);
		
		//GridBagConstraints endLineConstr = new GridBagConstraints();
		//endLineConstr.gridwidth = GridBagConstraints.REMAINDER;
		
		final int gapx = 15;
		
		// ******************** FPS
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, gapx);// 15 to give space for number
		//c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		FPSLabel_ = new JLabel();
		add(FPSLabel_, c);
		
		FPSSlider_ = new JSlider(Globals.FPS_MIN, Globals.FPS_MAX, Globals.FPS);
		FPSSlider_.setPaintTicks(true);
		FPSSlider_.addChangeListener(new ChangeListener()
		{	
			@Override
			public void stateChanged(ChangeEvent e)
			{
				adjustFPS();
			}
		});
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
		add(FPSSlider_, c);

		makeAnimationCBs (gapx);
		
		
		adjustFPS();
	}
	
	private void makeAnimationCBs(int gapx)
	{
		ActionListener checkboxesActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				adjustAnimationSettings();
			}
		};
		int gridy0 = 2;
		// ******************* general animation
		JLabel animationLabel = new JLabel("Visual animations:");
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = gridy0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, gapx);
		add(animationLabel, c);
		
		
		animationCB_.setSelected(Globals.USE_ANIMATIONS);
		animationCB_.addActionListener(checkboxesActionListener);
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = gridy0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 9, 0, 0);
		add(animationCB_, c);
		
		// ******************* particles animation
		JLabel animParticlesLabel = new JLabel("Particles animation:");
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = gridy0+1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, gapx);
		add(animParticlesLabel, c);
		
		
		animationParticlesCB_.setSelected(Globals.USE_PARTICLE_ANIMATIONS);
		animationParticlesCB_.addActionListener(checkboxesActionListener);
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = gridy0+1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 9, 0, 0);
		add(animationParticlesCB_, c);
		

		// ******************* filters
		JLabel blurLabel = new JLabel("Blur game");
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = gridy0+2;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, gapx);
		add(blurLabel, c);
		
		blurCB_.setSelected(Globals.USE_BLURRING);
		blurCB_.addActionListener(checkboxesActionListener);
		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = gridy0+2;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 9, 0, 0);
		add(blurCB_, c);
	}
	
	private void adjustAnimationSettings()
	{
		Globals.USE_ANIMATIONS = animationCB_.isSelected();
		
		// disable sub-checkboxes if animations are disabled
		animationParticlesCB_.setEnabled(Globals.USE_ANIMATIONS);
		blurCB_.setEnabled(Globals.USE_ANIMATIONS);
		
		// update individual settings
		if ( ! Globals.USE_ANIMATIONS )
		{
			Globals.USE_PARTICLE_ANIMATIONS = false;
			animationParticlesCB_.setSelected(false);
			Globals.USE_BLURRING = false;
			blurCB_.setSelected(false);
		}
		else
		{
			Globals.USE_PARTICLE_ANIMATIONS = animationParticlesCB_.isSelected();
			Globals.USE_BLURRING = blurCB_.isSelected();
		}
	}
	
	private void adjustFPS()
	{
		int fps = FPSSlider_.getValue();
		Globals.FPS = fps;
		String fpsText = "FPS [";
		if (fps < 10) fpsText += "0";
		fpsText += fps+"]:";
		FPSLabel_.setText(fpsText);
	}
}
