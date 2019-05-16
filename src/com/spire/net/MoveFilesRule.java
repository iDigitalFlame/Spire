package com.spire.net;

import com.spire.io.Item;
import java.util.ArrayList;
import java.net.InetAddress;
import com.spire.io.PathMatcher;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public final class MoveFilesRule implements TunnelRule
{
	private final ArrayList<PathMatcher> ruleNotAllowed;
	
	private String ruleDestination;
	
	public MoveFilesRule(String FileDestination) throws NullException, StringException
	{
		this(FileDestination, (PathMatcher[])null);
	}
	public MoveFilesRule(PathMatcher... NotAllowed) throws NullException, StringException
	{
		this(null, NotAllowed);
	}
	public MoveFilesRule(String FileDestination, PathMatcher... NotAllowed) throws NullException, StringException
	{
		if(NotAllowed == null && FileDestination == null)
			throw new NullException("FileDestination");
		if(FileDestination != null && FileDestination.isEmpty())
			throw new StringException("FileDestination");
		ruleDestination = FileDestination;
		ruleNotAllowed = new ArrayList<PathMatcher>();
		if(NotAllowed != null)
			for(int a = 0; a < NotAllowed.length; a++)
				ruleNotAllowed.add(NotAllowed[a]);
	}

	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof MoveFilesRule && CompareObject.hashCode() == hashCode();
	}
	public final boolean canReceive(Computer Sender, InetAddress SenderAddress, Item ReceivedItem)
	{
		if(ReceivedItem instanceof FilePacket)
		{
			if(!ruleNotAllowed.isEmpty())
				for(int a = 0; a < ruleNotAllowed.size(); a++)
					if(ruleNotAllowed.get(a).isMatch(((FilePacket)ReceivedItem).getFileDestination()))
						return false;
			if(ruleDestination != null)
				((FilePacket)ReceivedItem).setDestination(ruleDestination + Constants.CURRENT_OS.systemSeperator + ((FilePacket)ReceivedItem).getFileName()); 
		}
		return true;
	}
	
	public final int hashCode()
	{
		return (ruleDestination != null ? ruleDestination.hashCode() : 100) + ruleNotAllowed.hashCode();
	}
	
	public final String toString()
	{
		return "MoveFilesRule(TR) " + (ruleDestination != null ? ruleDestination : "") + "NA: " + ruleNotAllowed.size();
	}
	
	protected final MoveFilesRule clone()
	{
		MoveFilesRule a = new MoveFilesRule(Constants.SPIRE_VERSION);
		a.ruleDestination = ruleDestination;
		a.ruleNotAllowed.addAll(ruleNotAllowed);
		return a;
	}
}