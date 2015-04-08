package org.paulpell.miam.net;

import java.util.Iterator;
import java.util.Vector;

import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.RemoteGame;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.ClassicSnake;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.draw.walls.DefaultWall;


public class SettingsEncoder
{

	
	// items are sent at creation time
	// wall not encoded for now, using defaultWall
	public static String encodeSettings(Game game)
	{
		String encoded = "";
		
		// first, general settings: snake speed, classic mode?
		encoded += Globals.USE_CLASSIC_SNAKE ? '1' : '0';
		encoded += new String(NetMethods.int2bytes(Globals.SNAKE_NORMAL_SPEED));
		encoded += Globals.SNAKE_USE_WIDTH ?  '1' : '0';
		
		
		
		// then game stuff
		
		// send snakes
		Vector<Snake> snakes = game.getSnakes();
		encoded += (char)snakes.size();
		Iterator<Snake> it = snakes.iterator();
		while (it.hasNext()) {
			String s = it.next().getNetworkRepresentation();
			encoded += (char)s.length() + s;
		}
		
		// send items
		/*Vector<Item> items = game.getItems();
		encoded += (char)items.size();
		Iterator<Item> it2 = items.iterator();
		while (it2.hasNext()) {
			String s = new String(ItemEncoder.encodeItem(it2.next()));
			encoded += (char)s.length() + s;
		}*/
	
		return encoded;
	}
	
	
	public static RemoteGame decodeSettings(Control control, Client client, String encoded)
	{
		GameSettings settings = new GameSettings();
		settings.classicMode_ = encoded.charAt(0) == '1';
		settings.snakeSpeed_ = NetMethods.bytes2int(encoded.substring(1, 5).getBytes());
		settings.useWideSnakes_ = encoded.charAt(5) == '1';
		
		
		RemoteGame game = new RemoteGame(control, settings, client);
		
		int snakesNr = (int)(encoded.charAt(6));
		Vector<Snake> snakes = new Vector<Snake>(snakesNr);
		encoded = encoded.substring(7);
		for (int i=0; i<snakesNr; ++i) {
			int n = (int)(encoded.charAt(0));
			if (settings.classicMode_) {
				snakes.add(new ClassicSnake(encoded.substring(1,n+1), settings));
			}
			else {
				snakes.add(new Snake(encoded.substring(1, n+1), settings));
			}
			encoded = encoded.substring(n+1);
		}
		game.setSnakes(snakes);
		
		/*int itemsNr = (int)encoded.charAt(0);
		encoded = encoded.substring(1);
		Vector<Item> items = new Vector<Item>(itemsNr);
		for (int i=0; i<itemsNr; ++i)
		{
			int n = (int)(encoded.charAt(0));
			String repr = encoded.substring(1, n+1);
			encoded = encoded.substring(n+1);
			items.add(ItemEncoder.decodeItem(repr, game));
		}
		game.setItems(items);*/
		
		game.setItems(new Vector<Item>());
		
		// TODO: encode wall!
		game.setWall(new DefaultWall());
		
		return game;
		
	}
}
