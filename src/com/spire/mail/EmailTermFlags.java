package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.util.BoolTag;
import com.spire.io.Streamable;

public final class EmailTermFlags implements Storage
{
	protected final BoolTag termFlags;
	
	protected EmailTerm termBase;
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		termFlags.setTagData(StorageEncoder.readByte(InStream));
		termBase = (EmailTerm)StorageEncoder.readStorage(InStream);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByte(OutStream, termFlags.getTagData());
		StorageEncoder.writeStorage(OutStream, termBase);
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailTerm && CompareObject.equals(termBase);
	}
	
	public final int hashCode()
	{
		return termBase.hashCode();
	}
	
	protected EmailTermFlags(EmailTerm TermBase, boolean TermNOT, boolean TermAND)
	{
		termBase = TermBase;
		termFlags = new BoolTag();
		termFlags.setTagA(TermAND);
		termFlags.setTagB(TermNOT);
	}
}