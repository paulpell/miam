package net;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import logic.Control;
import logic.Globals;
import logic.actions.SnakeAction;

public class GameClient extends Thread {

	private final int PORT = 13913;
	private Socket socket = null;
	
	private int id;
	
	boolean listening;
	
	private Control control;
	
	public GameClient(Control control) throws IOException {
		this(control, -1); // no id for now
	}
	
	public GameClient(Control control, int id) throws IOException {
		this.id = id;
		this.control = control;
		connect();
	}
	
	
	public void connect() throws IOException {
		// first find the server, (localhost for now)
		InetAddress addr;
		addr = InetAddress.getLocalHost();
		socket = new Socket(addr, PORT);
		listening = true;
		start();
	}
	
	public void run() {
		while (listening) {
			if (socket == null || socket.isClosed()) {
				listening = false;
				System.out.println("Client " + id+ " finishing");
			}
			try {
				control.onNetworkAction(
						ActionEncoder.decodeAction(
								NetMethods.receiveMessage(socket)));
			} catch (IOException e) {
				System.err.println("Client could not receive message: " + e.getLocalizedMessage());
			}
		}
	}
	
	public void sendAction(SnakeAction a) {
		if (socket != null) {
			try {
				NetMethods.sendMessage(socket, ActionEncoder.encodeAction(a));
			} catch (IOException e) {
				System.err.println("sendAction could not perform:" + e.getLocalizedMessage());
			}
		}
	}
	
}
