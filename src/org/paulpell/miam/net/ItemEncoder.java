package org.paulpell.miam.net;


import java.util.HashMap;
import java.util.Map;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.items.Banana;
import org.paulpell.miam.logic.draw.items.BananaSpecial;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.Lightning;
import org.paulpell.miam.logic.draw.items.ResurrectAll;
import org.paulpell.miam.logic.draw.items.ReversingItem;
import org.paulpell.miam.logic.draw.items.ScoreItem;


public class ItemEncoder
{
	
	private enum ItemEncodingEnum
	{
		BANANA(new Banana(0,0)),
		BANANA_SPECIAL(new BananaSpecial(0,0)),
		LIGHTNING(new Lightning(0,0)),
		REVERSE(new ReversingItem(0,0)),
		SCORE(new ScoreItem(0,0)),
		RESURRECT_ALL(new ResurrectAll(0,0)),
		;
		
		private static byte nextEnc = 0;
		
		private byte enc_;
		private Item instance_;
		
		private ItemEncodingEnum(Item instance)
		{
			enc_ = getNextEnc();
			instance_ = instance;
		}
		private byte getNextEnc()
		{
			return nextEnc++;
		}
		
		public byte getEncoding() { return enc_; }
		
		public Item getInstance() { return instance_; }
		
	};
	
	static Map<Class<?>, ItemEncodingEnum> class2encoded_ = new HashMap<Class<?>, ItemEncodingEnum>();
	static Map<Byte, ItemEncodingEnum> encoded2class_ = new HashMap<Byte, ItemEncodingEnum>();
	
	static
	{
		class2encoded_.put(Banana.class, ItemEncodingEnum.BANANA);
		class2encoded_.put(BananaSpecial.class, ItemEncodingEnum.BANANA_SPECIAL);
		class2encoded_.put(Lightning.class, ItemEncodingEnum.LIGHTNING);
		class2encoded_.put(ReversingItem.class, ItemEncodingEnum.REVERSE);
		class2encoded_.put(ScoreItem.class, ItemEncodingEnum.SCORE);
		class2encoded_.put(ResurrectAll.class, ItemEncodingEnum.RESURRECT_ALL);
		
		encoded2class_.put(ItemEncodingEnum.BANANA.getEncoding(), ItemEncodingEnum.BANANA);
		encoded2class_.put(ItemEncodingEnum.BANANA_SPECIAL.getEncoding(), ItemEncodingEnum.BANANA_SPECIAL);
		encoded2class_.put(ItemEncodingEnum.LIGHTNING.getEncoding(), ItemEncodingEnum.LIGHTNING);
		encoded2class_.put(ItemEncodingEnum.REVERSE.getEncoding(), ItemEncodingEnum.REVERSE);
		encoded2class_.put(ItemEncodingEnum.SCORE.getEncoding(), ItemEncodingEnum.SCORE);
		encoded2class_.put(ItemEncodingEnum.RESURRECT_ALL.getEncoding(), ItemEncodingEnum.RESURRECT_ALL);
	}
	

	public static byte[] encodeItem(Item item)
	{	
		if (item instanceof Banana && Globals.NETWORK_DEBUG)
			Log.logMsg("Banana to network: " + item);
	
		Pointd position = item.getPosition();
		
		ItemEncodingEnum iee = class2encoded_.get(item.getClass());
		if ( null == iee )
			throw new IllegalArgumentException("Unknown item in encoder");
		
		byte classByte = iee.getEncoding();
		
		// position_: 1 point
		byte[] pos = NetMethods.point2bytes(position);
		assert pos.length <= (0xFFFF & Byte.MAX_VALUE);
		
		byte[] extra = item.getExtraParamsDescription().getBytes();
		assert extra.length <= (0xFFFF & Byte.MAX_VALUE);
		
		byte[] repr = new byte[3 + pos.length + extra.length];
		
		repr[0] = classByte;
		repr[1] = (byte)pos.length;
		NetMethods.setSubBytes(pos, repr, 2, pos.length + 2);
		repr[pos.length + 2] = (byte)extra.length;
		NetMethods.setSubBytes(extra, repr, pos.length + 3, extra.length + pos.length + 3);
		
		return repr;
	}
	
	public static Item decodeItem(byte[] repr)
	{
		ItemEncodingEnum iee = encoded2class_.get(repr[0]); 
		if ( null == iee )
			throw new IllegalArgumentException("Unknown item in encoder");
		
		Item instance = iee.getInstance();
		Class<?> theClass = instance.getClass();
		
		int poslen = 0xFF & repr[1];
		byte[] posbs = NetMethods.getSubBytes(repr, 2, 2 + poslen);
		Pointd pos = NetMethods.bytes2point(posbs);
		
		int extralen = 0xFF & repr[2 + poslen];
		byte[] extrabs = NetMethods.getSubBytes(repr, 3 + poslen, 3 + poslen + extralen);
		String extraParams = new String(extrabs);
		
		Class<?>[] paramsTypes = new Class[]{double.class, double.class};
		Object[] params = new Object[]{pos.x_, pos.y_};
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
