import logic.Control;



public class Snakesss {

	/**
	 * @param args
	 */
	
	// we have several threads:
	// - main exits quickly,
	// - then GUI thread, that does the usual rendering, painting, .. job
	// - one timer to update the game state
	// - one timer for creating items (see logic.draw.items.ItemCreator, logic.draw.items.*)
	
	
	public static void main(String[] args) {
		// start the beast: a gameWindow reacting to user input
		Control control = new Control();
		
		
	}

}
