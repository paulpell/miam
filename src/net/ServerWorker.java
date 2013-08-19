package net;

import java.io.IOException;
import java.net.Socket;

public class ServerWorker extends Thread{
	
	private GameServer server;
	private Socket socket;
	private boolean listening;
	
	private Thread handlingThread;
	
	public ServerWorker(Socket s, GameServer server) {
		this.socket = s;
		this.server = server;
		start();
	}
	
	public void run () {
		handlingThread = this;
		/*byte[] sizeByte = new byte[4];
		int size;
		byte[] bytes = new byte[GameServer.MESSAGE_BUFFER_SIZE];
		int n;*/
		while (listening) {
			/*try {
				n = socket.getInputStream().read(sizeByte);
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
					server.received(strBs, this);
				}
				else {
					throw new IOException("Error: did not read expected size: size="+size+", read ="+n);
				}
			} catch (IOException e) {
				System.err.println("IOEXception: "+e.getMessage());
			}*/
			try {
				server.received(NetMethods.receiveMessage(socket), this);
			} catch (IOException e) {
				System.err.println("Server could not receive message: " + e.getLocalizedMessage());
			}
		}
	}
	
	public void end() {
		listening = false;
		handlingThread.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Could not close socket...:"+e.getMessage());
		}
	}
	
	public void sendMessage(byte[] bytes) {
		try {
			NetMethods.sendMessage(socket, bytes);
		} catch (IOException e) {
			System.err.println("Could not send message: "+e.getMessage());
		}
	}
	
	public void sendMessage(String message) throws IOException {
		sendMessage(message.getBytes());
	}
}
