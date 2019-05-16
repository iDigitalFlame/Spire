package com.derp.manager;

import java.io.IOException;
import org.netcom.io.DataVector;
import org.netcom.io.Stream;
import org.netcom.net.NetworkPacket;

public class ManagerCommand extends NetworkPacket
{
	protected final DataVector command_data;

	public ManagerCommand(Stream inStream)
	{
		this(0);
		readItemData(inStream);
	}

	protected ManagerCommand(int CommandID)
	{
		super(false, 85 + CommandID, 80);
		command_data = new DataVector();
	}
	protected ManagerCommand(int CommandID, Object Command)
	{
		this(CommandID);
		command_data.add(Command);
	}

	protected final void readSubItem(Stream inStream) throws IOException
	{
		readFinalItem(inStream, command_data);
	}
	protected final void writeSubItem(Stream outStream) throws IOException
	{
		command_data.writeStream(outStream);
	}

	protected final byte getCommand()
	{
		return (byte)(getID() - 85);
	}

	protected final ManagerCommand doClone()
	{
		ManagerCommand a = new ManagerCommand(getCommand());
		a.command_data.addAll(command_data);
		return a;
	}
}