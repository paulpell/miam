package net;

import java.io.IOException;
import java.net.Socket;

public class NetMethods {

	public static void sendMessage(Socket socket, byte[] bytes) throws IOException {
		//if (bytes.length >= (1 << 8)) {
		long max_size = 1 << 32;
		if (bytes.length > max_size) {
			throw new IOException("Too long message: size = "+bytes.length);
		}
		int l = bytes.length;
		socket.getOutputStream().write(new byte[]
				{(byte)(l >> 24),
				(byte)(l >> 16)},
				(byte)(l >> 8),
				(byte)l);
		socket.getOutputStream().write(bytes);
	}
	
	public static byte[] receiveMessage(Socket socket) throws IOException {//, NetMethodCaller caller) {

		byte[] sizeByte = new byte[4];
		int size;
		byte[] bytes = new byte[GameServer.MESSAGE_BUFFER_SIZE];
		int n = socket.getInputStream().read(sizeByte);
		if (n != 4) {
			throw new IOException("Expected to read 4 bytes, not possible!");
		}
		size =  (0xFF & sizeByte[0]) << 24;
		size |= (0xFF & sizeByte[1]) << 16;
		size |= (0xFF & sizeByte[2]) << 8;
		size |= (0xFF & sizeByte[3]); 
		assert(size >= 0) : "size < 0:"+size;
		
		n = socket.getInputStream().read(bytes, 0, size);
		if (n == size) {
			byte[] strBs = new byte[n];
			System.arraycopy(bytes, 0, strBs, 0, n);
			return strBs;
			//caller.messageReceived(strBs);
		}
		else {
			throw new IOException("Error: did not read expected size: size="+size+", read ="+n);
		}
		
	}

	
}
