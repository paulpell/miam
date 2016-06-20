package org.paulpell.miam.net;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.gameactions.SnakeAction;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.players.PlayerInfo;
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
	protected int clientId_ = -1;
	
	protected int gameTimestamp_; // controlled by server or control: this value is the last received, or increased by one on a game step
	
	// keeps the thread alive
	boolean listening_ = true;
	
	private final Control control_;
	private final NetworkControl netControl_;
	
	public Client(Control control, NetworkControl netControl)
	{
		super("NetClient");
		control_ = control;
		netControl_ = netControl;
	}
	
	public InetAddress getAddr()
	{
		return addr_;
	}
	
	public int getClientId()
	{
		return clientId_;
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
					sendMessage(MsgTypes.CLIENT_LEAVES, "quit".getBytes());
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
				Log.logMsg("Client " + clientId_+ " finishing");
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
	
	// all the messages are handled here, whether they are sent by or to the server
	public void handleMessage(TimestampedMessage tmsg)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Client("+clientId_+") handling msg=|"+tmsg+"|");
		
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
			
		case GAME_LEVEL:
			control_.receiveNetworkLevel(payload);
			break;
			
		case GAME_START:
			control_.startSlaveGame();
			break;
			
		case GAME_STEP:
			control_.stepSlaveGame(new String(payload));
			break;
			
		case ITEM_SPAWN:
			control_.onReceiveItem(payload);
			break;
			
		case SNAKE_DIED:
			receiveSnakeDeath(new String(payload));
			break;
			
		case GAME_VICTORY:
			receiveSnakesWon(payload);
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
			control_.receiveChatMessage(msg);
			break;
			
		case CLIENT_LEAVES:
			receiveClientLeaves(msg.from_, payload);
			break;
			
		case CLIENT_LIST:
			receiveClientsList(payload);
			break;
			
		case ADD_PLAYER_REQUEST:
			receivePlayerAddRequest(payload);
			break;
			
		case ADD_PLAYER_DENY:
			receivePlayerAddDeny(payload);
			break;
			
		case PLAYER_LIST:
			receivePlayersList (payload);
			break;
			
		case CLIENT_REJECTED:
			control_.onConnectionLost(true);
			break;
			
		case ERROR:
			Log.logErr("TODO: error");
			break;
			
			
		case SERVER_STOPS:
			control_.onConnectionLost(false);
			break;
			
		case SET_ID:
			setId(0xFF & payload[0]);
			break;

		default:
			if (Globals.NETWORK_DEBUG)
				Log.logErr("Unknown control command: |" + msg + "|");
			
		}
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
			sendMessage(MsgTypes.ITEM_SPAWN, ItemEncoder.encodeItem(item));
		}
		catch (IOException e)
		{
			control_.networkFeedback("Could not send item: " + e.getLocalizedMessage());
		}
	}
	
	private void setId(int id)
	{
		clientId_ = id;
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Client sets id = " + clientId_);
	}
	
	
	protected void sendMessage(MsgTypes type, byte[] payload)
			throws IOException
	{
		TimestampedMessage msg = new TimestampedMessage(gameTimestamp_, clientId_, type, payload);

		if (Globals.NETWORK_DEBUG)
			Log.logErr("Client (" + clientId_ + ") sends message: " + msg);
		
		NetMethods.sendMessage(socket_, msg);
	}
	

	public void sendStartCommand()
			throws IOException 
	{
		sendMessage(MsgTypes.GAME_START, null);
	}
	
	public void sendLevel (Level level)
			throws IOException, Exception
	{
		sendMessage(MsgTypes.GAME_LEVEL, LevelEncoder.encodeLevel(level));
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
	
	public void receiveSnakesWon(byte[] bs)
	{
		control_.onSnakesWon(bs);
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

	public void sendClientsList(Collection<ClientInfo> clients)
	{
		String msg = "" + (char)clients.size();
		for (ClientInfo ci: clients)
		{
			byte[] id = NetMethods.int2bytes(ci.getClientId());
			//String cname = ci.getName();
			byte[] namebs = ci.getName().getBytes();
			int namebslen = namebs.length;

			//byte[] bs = new byte[5 + cname.length()];
			byte[] bs = new byte[5 + namebslen];
			NetMethods.setSubBytes(id, bs, 0, 4);
			bs[4] = (byte)namebslen;
			//NetMethods.setSubBytes(cname.getBytes(), bs, 5, 5 + cname.length());
			NetMethods.setSubBytes(namebs, bs, 5, 5 + namebslen);
			msg += new String(bs);
		}
		
		try
		{
			sendMessage(MsgTypes.CLIENT_LIST, msg.getBytes());
		}
		catch (Exception e)
		{
			control_.networkFeedback("Can not send clients list: " + e.getLocalizedMessage());
		}
	}

	public void sendAddPlayerRequest ( PlayerInfo pi )
	{
		MsgTypes type = MsgTypes.ADD_PLAYER_REQUEST;
		try
		{
			byte[] buf = OnlineInfoEncoder.encodePlayerInfo(pi);
			sendMessage(type, buf);
		}
		catch (IOException e)
		{
			Log.logErr ( "cannot send " + type + ": " + e.getMessage());
			Log.logException(e);
		}
	}
	
	private void receivePlayerAddRequest ( byte[] buf )
	{
		PlayerInfo pi = OnlineInfoEncoder.decodePlayerInfo(buf);
		control_.playerAddRequested(pi);
	}
	
	private void receivePlayerAddDeny( byte[] buf )
	{
		String denyReason = new String(buf);
		control_.playerAddDenied(denyReason);//(pi)
	}
	
	public void sendPlayersList (Vector<PlayerInfo> players, Vector<Integer> unusedIds, int numSeats)
	{
		byte[] buf1 = new byte[2];
		assert players.size() <= Byte.MAX_VALUE;
		assert numSeats <= Byte.MAX_VALUE;
		buf1[0] = (byte)players.size();
		buf1[1] = (byte)numSeats;
		for (PlayerInfo pi : players)
		{
			int i0 = buf1.length;
			byte[] pibs = OnlineInfoEncoder.encodePlayerInfo(pi);
			byte[] buf2 = new  byte[i0 + 1 + pibs.length];
			System.arraycopy(buf1, 0, buf2, 0, i0);
			buf2[i0] = (byte)pibs.length;
			System.arraycopy(pibs, 0, buf2, i0 + 1, pibs.length);
			
			buf1 = buf2;
		}
		try
		{
			sendMessage(MsgTypes.PLAYER_LIST, buf1);
		}
		catch (Exception e)
		{
			control_.networkFeedback("Can not send players list: " + e.getLocalizedMessage());
		}
	}
	
	private void receivePlayersList (byte[] buf)
	{
		int nPlayers = 0xFF & buf[0];
		int nSeats = 0xFF & buf[1];
		Vector<PlayerInfo> pis = new Vector<PlayerInfo>();
		int i0 = 2;
		for (int i=0; i<nPlayers; ++i)
		{
			int pilen = 0xFF & buf[i0];
			byte[] pibs = NetMethods.getSubBytes(buf, i0+1, i0+1+pilen);
			pis.add ( OnlineInfoEncoder.decodePlayerInfo(pibs));
			i0 += 1+pilen;
		}
		control_.setRemotePlayerInfos(pis, nSeats);
	}
	
	private void receiveClientLeaves(int cid, byte[] payload)
	{
		String msg = null == payload ? "" : new String(payload);
		netControl_.clientLeft(cid, msg);
	}
	
	private void receiveClientsList(byte[] payload)
	{
		HashMap <Integer, ClientInfo> clientId2Infos = ClientInfo.makeNetClientList(payload);
		netControl_.setRemoteClientInfos(clientId2Infos);
	}

	public void sendSnakesWon(Vector<Snake> ss)
	{
		try
		{
			byte[] bs = new byte[ss.size()];
			for (int i=0; i<bs.length; ++i)
				bs[i] = (byte)ss.get(i).getId();
			sendMessage(MsgTypes.GAME_VICTORY, bs);
		}
		catch (Exception e)
		{
			control_.networkFeedback("Can not send victory: " + e.getLocalizedMessage());
		}
	}
}
