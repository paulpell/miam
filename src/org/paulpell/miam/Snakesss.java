package org.paulpell.miam;

import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.levels.LevelEditorControl;



public class Snakesss
{

	/**
	 * @param args
	 */
	
	// we have several threads:
	// - main exits quickly,
	// - then GUI thread, that does the usual rendering, painting, .. job
	// - one timer to update the game state
	// - one timer for creating items (see org.paulpell.miam.logic.draw.items.ItemFactory, org.paulpell.miam.logic.draw.items.*)
	// - one thread for "victory animation", see package org.paulpell.miam.logic.draw.particles
	

	public static void main(String[] args)
	{
		// start the beast: a gameWindow reacting to user input
		new Control();
		//LevelEditorControl lec = new LevelEditorControl(null);
		//lec.setVisible(true);
	}

}
