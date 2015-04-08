package org.paulpell.miam.net;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.actions.SnakeAction;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.net.TimestampedMessage.MsgTypes;


/**
 * There are 2 types of clients: master and slave
 * Master client gives the info to the server (it's on the same machine) directly,
 * slaves communicate with the server by net sockets.
 * 
 * Every client (except MasterClient) communicates with a ServerWorker.
 */

public class Client
		extends Thread
{

	private InetAddress addr_;
	private Socket socket_ = null;
	
	private boolean leavingServer_ = false;
	
	// 0 for master client, given by server for slave clients
	protected int id_ = -1;
	
	protected int gameTimestamp_; // controlled by server or control: this value is the last received, or increased by one on a game step
	
	// keeps the thread alive
	boolean listening_ = true;
	
	private final Control control_;
	
	public Client(Control control)
	{
		super("NetClient");
		control_ = control;
	}
	
	public void connect(InetAddress addr)
			throws IOException
	{
		// first find the server, (localhost for now)
		end();
		this.addr_ = addr;
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Trying to connect to "+addr+":"+Globals.ONLINE_PORT+"....");
		
		socket_ = new Socket(addr, Globals.ONLINE_PORT);
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Client connected");
		listening_ = true;
	}
	
	public void end()
	{
		
		if (socket_ != null)
		{
			if (Globals.NETWORK_DEBUG)
				Log.logMsg("Client closing connection");
			
			try 
			{
				if (!socket_.isClosed())
					sendMessage(MsgTypes.CLIENT_LEAVES, null);
			}
			catch (IOException e) {
				// what can we do more?
			}
			try 
			{
				leavingServer_ = true;
				socket_.close();
			}
			catch (IOException e) {
				// what can we do more?
			}
			socket_ = null;
		}
		listening_ = false;
	}
	
	public void run()
	{
		while (listening_)
		{
			if (socket_ == null || socket_.isClosed())
			{
				listening_ = false;
				Log.logMsg("Client " + id_+ " finishing");
				control_.networkFeedback("Leaving server");
			}
			else
			{
				try
				{
					// blocking until a message arrives
					TimestampedMessage tmsg = NetMethods.receiveMessage(socket_);
					
					handleMessage(tmsg);
				}
				catch (IOException e)
				{
					// if we are leaving the server, the socket gets closed from outside.. We set the flag back again
					if (!leavingServer_)
					{
						end();
						control_.serverDied();
					}
					else
						leavingServer_ = false;
				}
				
			}
		}
	}
	
	public void handleMessage(TimestampedMessage tmsg)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Client("+id_+") handling msg=|"+tmsg+"|");
		
		if (0 != (tmsg.type_.msgType_ & TimestampedMessage.GAME_MSG_MASK))
			handleGameMessage(tmsg);
		else if (0 != (tmsg.type_.msgType_ & TimestampedMessage.CONTROL_MSG_MASK))
			handleControlMessage(tmsg);
		else
			throw new UnsupportedOperationException("Unknonw message type!!");
	}
	
	private void handleGameMessage(TimestampedMessage msg)
	{	
		byte[] payload = msg.payload_;
		
		switch (msg.type_)
		{ // parameter-dependent commands

		case ACCEPT_ITEM:
			control_.onAcceptItem(0xFF & payload[0], 0xFF & payload[1]);
			break;
			
		case ACTION_TAKEN:
			control_.receiveSlaveAction(ActionEncoder.decodeAction(payload));
			break;
			
		case GAME_OVER:
			control_.endGame();
			break;
			
		case GAME_SETTINGS:
			control_.receiveNetworkSettings(new String(payload));
			break;
			
		case GAME_START:
			control_.startSlaveGame();
			break;
			
		case GAME_STEP:
			control_.stepGame(new String(payload));
			break;
			
		case ITEM_SPAWN:
			String item = new String(payload);
			control_.onReceiveItem(item);
			break;
			
		case SNAKE_DIED:
			receiveSnakeDeath(new String(payload));
			break;
			
		default:
			if (Globals.NETWORK_DEBUG)
				Log.logErr("Unknown game command: |" + msg + "|");
			
		}
	}
	
	private void handleControlMessage(TimestampedMessage msg)
	{
		byte[] payload = msg.payload_;

		switch (msg.type_)
		{
		case CHAT_MESSAGE:
			control_.displayChatMessage(new String(payload), msg.from_);
			break;
			
		case CLIENT_LEAVES:
			Log.logErr("TODO: handle client exit"); // TODO client left!
			break;
			
		case CLIENT_REJECTED:
			control_.rejected();
			break;
			
		case ERROR:
			System.err.println("TODO: error");
			Log.logErr("TODO: error");
			break;
			
			
		case SERVER_STOPS:
			control_.serverStops();
			break;
			
		case SET_ID:
			setId(0xFF & payload[0]);
			break;
			
			// TODO: player names in client
		//case SET_NAME:
		//	control_.setPlayerName(msg.from_, new String(payload));
		//	break;
			
		//case NAME_LIST:
		//	break;
			
		default:
			if (Globals.NETWORK_DEBUG)
				Log.logErr("Unknown control command: |" + msg + "|");
			
		}
	}
	
	public InetAddress getAddr()
	{
		return addr_;
	}
	
	public void sendAction(SnakeAction a)
	{
		if (socket_ != null && a != null)
		{
			try
			{
				sendMessage(MsgTypes.ACTION_TAKEN, ActionEncoder.encodeAction(a));
			}
			catch (IOException e)
			{
				System.err.println("sendAction could not perform:" + e.getLocalizedMessage());
			}
		}
	}


	public void sendChatMessage(String message)
			throws IOException
	{
		sendMessage(MsgTypes.CHAT_MESSAGE, message.getBytes());
		control_.displayChatMessage(message, id_);
	}

	public void sendErrorMessage() 
	{
		try
		{
			sendMessage(MsgTypes.ERROR, null);
		}
		catch (IOException e)
		{
			// well....
		}
	}

	public void sendItem(Item item) {
		try
		{
			sendMessage(MsgTypes.ITEM_SPAWN, ItemEncoder.encodeItem(item).getBytes());
		}
		catch (IOException e)
		{
			control_.networkFeedback("Could not send item: " + e.getLocalizedMessage());
		}
	}
	
	private void setId(int id)
	{
		id_ = id;
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Client sets id = " + id_);
	}
	
	
	public int getServerId()
	{
		return id_;
	}
	
	protected void sendMessage(MsgTypes type, byte[] payload)
			throws IOException
	{
		TimestampedMessage msg = new TimestampedMessage(gameTimestamp_, id_, type, payload);

		if (Globals.NETWORK_DEBUG)
			Log.logErr("Client (" + id_ + ") sends message: " + msg);
		
		NetMethods.sendMessage(socket_, msg);
	}
	

	public void sendStartCommand()
			throws IOException 
	{
		sendMessage(MsgTypes.GAME_START, null);
	}
	
	public void sendGameSettings (Game game)
			throws IOException 
	{
		sendMessage(MsgTypes.GAME_SETTINGS, SettingsEncoder.encodeSettings(game).getBytes());
	}
	
	public void sendStepCommand (String stepString)
			throws IOException 
	{
		sendMessage(MsgTypes.GAME_STEP, stepString.getBytes());
	}
	

	public void sendSnakeDeath(Snake s, Pointd collision)
	{
		try
		{
			byte[] x = NetMethods.double2str(collision.x_).getBytes();
			byte[] y = NetMethods.double2str(collision.y_).getBytes();
			int xlen = x.length;
			int ylen = y.length;
			
			byte[] msg = new byte[3 + xlen + ylen];
			msg[0] = (byte)s.getId();
			msg[1] = (byte)xlen; 
			System.arraycopy(x, 0, msg, 2, xlen);
			msg[2 + xlen] = (byte)ylen;
			System.arraycopy(y, 0, msg, 3 + xlen, ylen);
			
			sendMessage(MsgTypes.SNAKE_DIED, msg);
		}
		catch (IOException e)
		{
			Log.logErr("Could not send death announcement: "+e.getLocalizedMessage());
		}
	}
	
	public void receiveSnakeDeath(String s)
	{
		byte[] bs = s.getBytes();
		int id = 0xFF & bs[0];
		int xlen = 0xFF & bs[1];
		byte[] xbs = new byte[xlen];
		System.arraycopy(bs, 2, xbs, 0, xlen);
		double x = NetMethods.str2double(new String(xbs));
		int ylen = 0xFF & bs[xlen + 2];
		byte[] ybs = new byte[ylen];
		System.arraycopy(bs, 3 + xlen, ybs, 0, ylen);
		double y = NetMethods.str2double(new String(ybs));
		control_.onSnakeDeath(id, new Pointd(x, y));
	}

	public void sendSnakeAcceptedItem(int snakeIndex, int itemIndex)
	{
		try 
		{
			sendMessage(MsgTypes.ACCEPT_ITEM, new byte[]{(byte)(0xFF & snakeIndex), (byte)(0xFF & itemIndex)});
		} catch (IOException e) {
			control_.networkFeedback("Could not send accepted item: "+e.getLocalizedMessage());
		}
	}

	public void sendGameEnd()
	{
		try
		{
			sendMessage(MsgTypes.GAME_OVER, null);
		} catch (IOException e)
		{
			control_.networkFeedback("Could not send game over: " + e.getLocalizedMessage());
		}
	}
	
	

	// Control tells us when a new step is taken
	public void increaseTimestamp()
	{
		++ gameTimestamp_;
	}
}
