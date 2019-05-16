package com.spire.net;

import java.util.List;
import com.spire.io.Item;
import com.spire.io.Stream;
import java.io.IOException;
import java.net.InetAddress;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.cred.HashCheck;

public final class Header extends Item
{
	public static final byte ITEM_CLASS_ID = 5;
	
	protected final HashCheck headerCheck;
	
	protected byte headerDataSize;
	protected Computer headerSender;
	protected InetAddress headerDestination;
	
	public final int hashCode()
	{
		return ((headerCheck.hashCode() + 1) * headerDataSize) + headerSender.hashCode();
	}
	
	public final String toString()
	{
		return "Header(" + getItemID() + ") >> " + headerSender.computerName + " >> " + headerDestination.getHostAddress();
	}
	
	protected Header()
	{
		super(ITEM_CLASS_ID);
		headerCheck = new HashCheck();
	}
	protected Header(InetAddress Destination)
	{
		this();
		headerSender = Constants.LOCAL;
		headerDestination = Destination;
	}
	
	protected final void readItemFailure()
	{
		headerDataSize = 0;
		headerSender = null;
		headerDestination = null;
		headerCheck.createCheck(-1);
	}
	protected final void prepareForSend(List<Item> Data)
	{
		int a = 0;
		for(byte b = 0; b < Data.size(); b++)
		{
			Data.get(b).changeLocalAddress(headerDestination);
			a += Data.get(b).hashCode();
		}
		headerCheck.createCheck(a);
		headerDataSize = (byte)Data.size();
		headerSender.changeLocalAddress(headerDestination);
	} 
	protected final void readItem(Streamable InStream) throws IOException
	{
		headerDataSize = itemEncoder.readByte(InStream);
		headerCheck.readStorage(InStream, itemEncoder);
		headerSender = (Computer)Item.getNextItemByID(InStream, 1);
		headerDestination = Stream.readInetAddress(itemEncoder, InStream);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeByte(OutStream, headerDataSize);
		headerCheck.writeStorage(OutStream, itemEncoder);
		headerSender.writeStream(OutStream);
		Stream.writeInetAddress(itemEncoder, OutStream, headerDestination);
	}
	
	protected final boolean checkHash(int HashData, int HeaderSize)
	{
		return headerCheck.isValid(HashData) && HeaderSize == headerDataSize;
	}
	
	protected final Header getCopy()
	{
		return null;
	}
}