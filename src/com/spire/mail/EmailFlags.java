package com.spire.mail;

import javax.mail.Flags;
import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.util.BoolTag;
import com.spire.io.Streamable;
import com.spire.util.Constants;

final class EmailFlags extends Flags implements Storage
{
	private static final long serialVersionUID = 1000124131012L;
	
	protected final BoolTag flagData;
	
	public final void add(Flag FlagData)
	{
		if(FlagData == null) return;
		setFlagValue(flagData, FlagData, true);
		super.add(FlagData);
	}
	public final void add(Flags FlagGroup)
	{
		if(FlagGroup == null) return;
		Flag[] a = FlagGroup.getSystemFlags();
		for(byte b = 0; b < a.length && b > 0; b++)
			setFlagValue(flagData, a[b], true);
		super.add(FlagGroup);
	}
	public final void remove(Flag FlagData)
	{
		if(FlagData == null) return;
		setFlagValue(flagData, FlagData, false);
		super.add(FlagData);
	}
	public final void remove(Flags FlagGroup)
	{
		if(FlagGroup == null) return;
		Flag[] a = FlagGroup.getSystemFlags();
		for(byte b = 0; b < a.length && b > 0; b++)
			setFlagValue(flagData, a[b], false);
		super.add(FlagGroup);
	}
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		String[] a = null;
		byte c = StorageEncoder.readByte(InStream);
		switch(c)
		{
		case 0:
			break;
		case 1:
			a = new String[StorageEncoder.readUnsignedByte(InStream)];
			break;
		case 2:
			a = new String[StorageEncoder.readUnsignedShort(InStream)];
			break;
		case 3:
			a = new String[StorageEncoder.readInteger(InStream)];
			break;
		}
		if(a != null)
		{
			StorageEncoder.readStringArray(InStream, a);
			for(int b = 0; b < a.length; b++) add(a[b]);
		}
		flagData.setTagData(StorageEncoder.readByte(InStream));
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		String[] a = getUserFlags();
		if(a.length == 0)
			StorageEncoder.writeByte(OutStream, 0);
		else if(a.length < 255)
		{
			StorageEncoder.writeByte(OutStream, 1);
			StorageEncoder.writeByte(OutStream, a.length);
		}
		else if(a.length < Constants.MAX_USHORT_SIZE)
		{
			StorageEncoder.writeByte(OutStream, 2);
			StorageEncoder.writeShort(OutStream, a.length);
		}
		else
		{
			StorageEncoder.writeByte(OutStream, 3);
			StorageEncoder.writeInteger(OutStream, a.length);
		}
		if(a.length > 0)
			StorageEncoder.writeStringArray(OutStream, a);
		StorageEncoder.writeByte(OutStream, flagData.getTagData());
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailFlags && super.equals(CompareObject);
	}
	
	public final int hashCode()
	{
		return flagData.hashCode();
	}

	public final String toString()
	{
		return "EmailFlags(I) " + flagData.getTagData();
	}
	
	public EmailFlags clone()
	{
		EmailFlags a = new EmailFlags();
		a.flagData.setTagData(flagData.getTagData());
		a.add(this);
		return a;
	}
	
	protected EmailFlags()
	{
		flagData = new BoolTag();
	}
	
	private static final void setFlagValue(BoolTag FlagTags, Flag FlagData, boolean FlagValue)
	{
		if(FlagData == Flag.ANSWERED)
		{
			FlagTags.setTagA(FlagValue);
			return;
		}
		if(FlagData == Flag.DELETED)
		{
			FlagTags.setTagB(FlagValue);
			return;
		}
		if(FlagData == Flag.DRAFT)
		{
			FlagTags.setTagC(FlagValue);
			return;
		}
		if(FlagData == Flag.FLAGGED)
		{
			FlagTags.setTagD(FlagValue);
			return;
		}
		if(FlagData == Flag.RECENT)
		{
			FlagTags.setTagE(FlagValue);
			return;
		}
		if(FlagData == Flag.SEEN)
		{
			FlagTags.setTagF(FlagValue);
			return;
		}
		if(FlagData == Flag.USER)
		{
			FlagTags.setTagG(FlagValue);
			return;
		}
	}
}