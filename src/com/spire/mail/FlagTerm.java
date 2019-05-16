package com.spire.mail;

import javax.mail.Flags;
import java.io.IOException;
import com.spire.io.Encoder;
import javax.mail.Flags.Flag;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import javax.mail.search.SearchTerm;

public final class FlagTerm extends EmailTerm
{
	private final EmailFlags termFlagData;
	
	private boolean termFlagSet;
	
	public FlagTerm()
	{
		termFlagData = new EmailFlags();
	}
	public FlagTerm(Flag EmailFlag, boolean FlagSet) throws NullException
	{
		this();
		if(EmailFlag == null) throw new NullException("EmailFlag");
		termFlagSet = FlagSet;
		termFlagData.add(EmailFlag);
	}
	public FlagTerm(Flags EmailFlags, boolean FlagsSet) throws NullException
	{
		this();
		if(EmailFlags == null) throw new NullException("EmailFlags");
		termFlagSet = FlagsSet;
		termFlagData.add(EmailFlags);
	}
	
	public final void setFlagsSet(boolean FlagsSet)
	{
		termFlagSet = FlagsSet;
	}
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		termFlagData.readStorage(InStream, StorageEncoder);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		termFlagData.writeStorage(OutStream, StorageEncoder);
	}
	
	public final boolean isFlagsSet()
	{
		return termFlagSet;
	}
	
	public final int hashCode()
	{
		return termFlagData.hashCode() + (termFlagSet ? 5 : 20);
	}
	
	public final Flags getFlags()
	{
		return termFlagData;
	}
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.FlagTerm(termFlagData, termFlagSet);
	}

	protected final FlagTerm clone()
	{
		return new FlagTerm(termFlagData, termFlagSet);
	}
}