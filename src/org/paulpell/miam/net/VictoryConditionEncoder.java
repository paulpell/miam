package org.paulpell.miam.net;

import java.util.HashMap;
import java.util.Map;

import org.paulpell.miam.logic.levels.ScoreVictoryCondition;
import org.paulpell.miam.logic.levels.VictoryCondition;

public class VictoryConditionEncoder
{
	
	private enum VictoryConditionEnum
	{
		SCORE(new ScoreVictoryCondition(0));


		private static byte nextCode = 0;

		final byte conditionCode_;
		final VictoryCondition vc_;

		
		private VictoryConditionEnum(VictoryCondition vc)
		{
			conditionCode_ = getNextCode();
			vc_ = vc;
		}
		
		private static byte getNextCode()
		{
			return nextCode++;
		}
	}
	

	static Map<Class<?>, VictoryConditionEnum> class2encoded_ = new HashMap<Class<?>, VictoryConditionEnum>();
	static Map<Byte, VictoryConditionEnum> encoded2class_ = new HashMap<Byte, VictoryConditionEnum>();
	
	static
	{
		class2encoded_.put(ScoreVictoryCondition.class, VictoryConditionEnum.SCORE);
		
		encoded2class_.put(VictoryConditionEnum.SCORE.conditionCode_, VictoryConditionEnum.SCORE);
	}
	

	public static byte[] encodeVictoryCondition(VictoryCondition vc)
	{
		byte[] params = vc.getExtraParams().getBytes();
		byte[] plen = NetMethods.int2bytes(params.length);
		
		byte[] encoded = new byte[params.length + 5];
		
		Byte b = class2encoded_.get(vc.getClass()).conditionCode_;
		if ( null == b)
			throw new IllegalArgumentException("Unknown victory condition in encoder");
		
		encoded[0] = b;
		
		NetMethods.setSubBytes(plen, encoded, 1, 5);
		NetMethods.setSubBytes(params, encoded, 5, 5 + params.length);
		
		return encoded;
	}
	
	public static VictoryCondition decodeVictoryCondition(byte[] encoded)
	{
		VictoryConditionEnum vce = encoded2class_.get(encoded[0]); 
		if ( null == vce)
			throw new IllegalArgumentException("Unknown victory condition in encoder");
		
		VictoryCondition vc = vce.vc_;
		
		byte[] plenbs = NetMethods.getSubBytes(encoded, 1, 5);
		int plen = NetMethods.bytes2int(plenbs);
		assert encoded.length == (plen + 5) : "Bad victory condition length";
		
		byte[] paramsbs = NetMethods.getSubBytes(encoded, 5, plen + 5);
		vc.applyExtraParams(new String(paramsbs));
		
		return vc;
	}

}
