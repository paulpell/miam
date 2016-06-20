package org.paulpell.miam.net;

import java.io.IOException;

import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.players.PlayerInfo;
import org.paulpell.miam.net.TimestampedMessage.MsgTypes;

public class MasterClient extends Client
{
	
	Server server_;

	public MasterClient(Control control)
	{
		super(control);
		clientId_ = 0;
	}

	public void setServer(Server gameServer)
	{
		server_ = gameServer;
	}


	// we send the reason inside pi.name_
	public void sendDenyAddPlayerRequest ( PlayerInfo pi )
	{
		TimestampedMessage tmsg =
				new TimestampedMessage(-1, clientId_, MsgTypes.ADD_PLAYER_DENY, null);
		server_.sendIndividualMessage(pi.getClientId(), tmsg);
	}
	
	
	@Override
	protected void sendMessage(MsgTypes type, byte[] payload)
			throws IOException
	{
		TimestampedMessage msg =
				new TimestampedMessage(gameTimestamp_, clientId_, type, payload);

		if (Globals.NETWORK_DEBUG)
			Log.logMsg("MasterClient (" + clientId_ + ") sends message: " + msg);
		
		server_.broadcastToSlaves(msg);
	}
}
