package org.paulpell.miam;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.paulpell.miam.logic.Control;



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
	{// TODO: Proper config dir handling (non-existing, read-only,..)
		String logpattern =
			"%h/.Snakesss/"
			+ "log_"
			+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) ; 
		Log.initLog(logpattern);
		
		// start the beast: a gameWindow reacting to user input
		new Control();
		//LevelEditorControl lec = new LevelEditorControl(null);
		//lec.setVisible(true);
	}

}
