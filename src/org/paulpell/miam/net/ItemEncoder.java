package org.paulpell.miam.net;


import java.util.HashMap;
import java.util.Map;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.items.Banana;
import org.paulpell.miam.logic.draw.items.BananaSpecial;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.Lightning;
import org.paulpell.miam.logic.draw.items.ReversingItem;
import org.paulpell.miam.logic.draw.items.ScoreItem;


public class ItemEncoder
{
	
	private enum ItemEncodingEnum
	{
		BANANA(new Banana(0,0)),
		BANANA_SPECIAL(new BananaSpecial(0,0,null)),
		LIGHTNING(new Lightning(0,0)),
		REVERSE(new ReversingItem(0,0)),
		SCORE(new ScoreItem(0,0));
		
		private static char nextEnc = 0;
		
		private char enc_;
		private Item instance_;
		
		private ItemEncodingEnum(Item instance)
		{
			enc_ = getNextEnc();
			instance_ = instance;
		}
		private char getNextEnc()
		{
			return nextEnc++;
		}
		
		public char getEncoding() { return enc_; }
		
		public Item getInstance()
		{
			return instance_;
		}
		
	};
	
	static Map<Class<?>, ItemEncodingEnum> class2encoded_ = new HashMap<Class<?>, ItemEncodingEnum>();
	static Map<Character, ItemEncodingEnum> encoded2class_ = new HashMap<Character, ItemEncodingEnum>();
	
	static
	{
		class2encoded_.put(Banana.class, ItemEncodingEnum.BANANA);
		class2encoded_.put(BananaSpecial.class, ItemEncodingEnum.BANANA_SPECIAL);
		class2encoded_.put(Lightning.class, ItemEncodingEnum.LIGHTNING);
		class2encoded_.put(ReversingItem.class, ItemEncodingEnum.REVERSE);
		class2encoded_.put(ScoreItem.class, ItemEncodingEnum.SCORE);
		
		encoded2class_.put(ItemEncodingEnum.BANANA.getEncoding(), ItemEncodingEnum.BANANA);
		encoded2class_.put(ItemEncodingEnum.BANANA_SPECIAL.getEncoding(), ItemEncodingEnum.BANANA_SPECIAL);
		encoded2class_.put(ItemEncodingEnum.LIGHTNING.getEncoding(), ItemEncodingEnum.LIGHTNING);
		encoded2class_.put(ItemEncodingEnum.REVERSE.getEncoding(), ItemEncodingEnum.REVERSE);
		encoded2class_.put(ItemEncodingEnum.SCORE.getEncoding(), ItemEncodingEnum.SCORE);
	}
	

	public static String encodeItem(Item item)
	{
		String repr = "";
		Pointd position = item.getPointd();
		
		char classStr = class2encoded_.get(item.getClass()).getEncoding();
		repr += classStr;
		
		// position: 1 point
		repr += NetMethods.point2str(position);
		
		String extra = item.getExtraParamsDescription();
		assert extra.length() <= (0xFFFF & Character.MAX_VALUE);
		repr += (char)extra.length() + extra;
		
		if (item instanceof Banana && Globals.NETWORK_DEBUG)
			Log.logMsg("Banana to network: " + item);
		
		
		return repr;
	}
	
	public static Item decodeItem(String repr, Game game)
	{
		Item instance = encoded2class_.get(repr.charAt(0)).getInstance();
		Class<?> theClass = instance.getClass();
		
		
		// TODO: generic point reading
		char xlen = repr.charAt(1);
		double x = NetMethods.str2double(repr.substring(2, 2 + xlen));
		
		repr = repr.substring((2 + xlen));
		char ylen = repr.charAt(0);
		double y = NetMethods.str2double(repr.substring(1, 1 + ylen));
		
		repr = repr.substring(1 + ylen);
		char extralen = repr.charAt(0);
		String extraParams = repr.substring(1, 1 + extralen);
		
		assert Item.class.isAssignableFrom(theClass) : "Decoded bad item";
		
		Class<?>[] paramsTypes = new Class[]{double.class, double.class, Game.class};
		Object[] params = new Object[]{x, y, game};
		try
		{
			Item i = (Item)theClass.getMethod("newItem", paramsTypes).invoke(instance, params);
			i.applyExtraParamsDescription(extraParams); 
			if (i instanceof Banana && Globals.NETWORK_DEBUG)
				Log.logMsg("Banana from network: " + i);
				
			return i;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new UnsupportedOperationException("Decoded Item could not be created");
		}
	}

}
