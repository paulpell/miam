package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class GameServer extends Thread{
	

	public final static int MESSAGE_BUFFER_SIZE = 256;
	
	
	
	private final int port = 13913;
	private final ServerSocket socket;
	private boolean listening = true;
	
	private int n = 0;
	
	private LinkedList<ServerWorker> clients;
	
	public GameServer() throws IOException {
		socket = new ServerSocket(port);
		start();
	}
	
	public void run() {
		while (listening) {
			try {
				Socket newSocket = socket.accept();
				if (clients.size() >= n) {
					reject(new ServerWorker(newSocket, this));
					newSocket.close(); // mouhahaha
				}
				else {
					clients.add(new ServerWorker(newSocket, this));
				}
			} catch (IOException e) {
				System.err.println("Server accept failed: "+e.getLocalizedMessage());
			}
		}
	}
	
	
	public void end() {
		listening = false;
		for (ServerWorker c: clients) {
			if (c != null) {
				try {
					c.sendMessage("STOP");
				} catch (IOException e) {
					System.err.println("Could not send STOP to client: "+c+": "+e.getMessage());
				}
				c.end();
			}
		}
		interrupt(); // stop server thread
	}
	
	public void setPlayerNumber(int n) {
		this.n = n;
		for (int i=n; i<clients.size(); ++i) {
			reject(clients.get(i));
		}
	}
	
	protected void received(byte[] bytes, ServerWorker ch) {
		for (ServerWorker c: clients) {
			if (c != ch) {
				c.sendMessage(bytes);
			}
		}
	}
	
	private void reject(ServerWorker ch) {
		try {
			if (ch != null) {
				ch.sendMessage("REJECT");
			}
		} catch (IOException e) {
			System.err.println("Could not send REJECT to client: "+ch+": "+e.getMessage());
		}
	}
}