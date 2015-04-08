package org.paulpell.miam.net;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.net.TimestampedMessage.MsgTypes;


public class NetMethods
{

	private static final int TIMESTAMP_START_POS 	= 0;
	private static final int TYPE_START_POS 		= 4; 
	private static final int FROM_START_POS			= 8;
	private static final int LENGTH_START_POS		= 12;
	private static final int PAYLOAD_START_POS		= 16;
	
	public static void sendMessage(Socket socket, TimestampedMessage msg)
			throws IOException
	{
		int length = 4 + // timestamp
				4 + // msg type (int)
				4 + // from (byte)
				4 + // length (int)
				msg.length_;
		
		int type = TimestampedMessage.type2int_.get(msg.type_);
		
		byte[] packet = new byte[length];
		System.arraycopy(int2bytes(msg.timestamp_), 0, packet, TIMESTAMP_START_POS, 4);
		System.arraycopy(int2bytes(type), 0, packet, TYPE_START_POS, 4);
		System.arraycopy(int2bytes(msg.from_), 0, packet, FROM_START_POS, 4);
		System.arraycopy(int2bytes(msg.length_), 0, packet, LENGTH_START_POS, 4);
		
		if (null != msg.payload_)
			System.arraycopy(msg.payload_, 0, packet, PAYLOAD_START_POS, msg.length_);
		
		OutputStream outStream = socket.getOutputStream();
		outStream.write(packet);
	}
	

	public static TimestampedMessage receiveMessage(final Socket socket)
			throws IOException
	{	
		InputStream inStream = socket.getInputStream();

		int timestamp = readInt(inStream);
		int type = readInt(inStream);
		int from = readInt(inStream);
		int length = readInt(inStream);
		
		byte[] payload = null;
		if (length > 0)
		{
			payload = new byte[length];
			int n = inStream.read(payload, 0, length);
			if (n != length)
				throw new IOException("Could not read expected number of bytes for payload!");
		}
		
		MsgTypes t = TimestampedMessage.int2type_.get(type);
		return new TimestampedMessage(timestamp, from, t, payload);
	
	}
	
	private static int readInt(InputStream inStream)
			throws IOException
	{
		byte[] bytes = new byte[4];
		int n = inStream.read(bytes, 0, 4);
		if (n != 4)
			throw new IOException("EOF");
		return bytes2int(bytes);
	}

	
	protected static byte[] int2bytes(int i)
	{
		return new byte[]
				{(byte)(i >> 24),
				(byte)(i >> 16),
				(byte)(i >> 8),
		 		(byte)i};
	}
	
	protected static int bytes2int (byte[] b)
	{
		return ((0xFF & b[0]) << 24)
				| ((0xFF & b[1]) << 16)
				| ((0xFF & b[2]) << 8)
				| ((0xFF & b[3])); 
	}
	
	protected static String point2str(Pointd p)
	{
		String x = double2str(p.x_);
		String y = double2str(p.y_);
		String repr = "";
		repr += (char)x.length() + x;
		repr += (char)y.length() + y;
		return repr;
	}

	// TODO: how to do str2point? Outside, we need the remaining string, if it is not the end of the float repr 

	protected static String double2str(double d)
	{
		return ""+Double.doubleToRawLongBits(d);
	}
	
	protected static double str2double(String s)
	{
		return Double.longBitsToDouble(new Long(s));
	}
	
	public static byte[] parseIP(String s)
	{
		String[] ss = s.split("\\.");
		if (ss.length != 4)
			return null;
		byte[] bs = new byte[4];
		for (int i=0; i<4; ++i)
		{
			try
			{
				int b = Integer.parseInt(ss[i]);
				bs[i] = (byte)b;
			} catch (NumberFormatException e)
			{
				return null;
			}
		}
		return bs;
	}
	
}
