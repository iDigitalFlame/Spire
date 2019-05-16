package com.spire.util;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.io.Streamable;
import com.spire.ex.NumberException;

public final class BoolTag implements Storage
{
	private byte tagData;
	
	public BoolTag()
	{
		this(0);
	}
	public BoolTag(int TagData)
	{
		tagData = (byte)TagData;
	}
	public BoolTag(boolean... TagList)
	{
		this(getTagList(TagList));
	}
	
	public final void setTagA(boolean TagData)
	{
		tagData = setTag(tagData, 0, TagData);
	}
	public final void setTagB(boolean TagData)
	{
		tagData = setTag(tagData, 1, TagData);
	}	
	public final void setTagC(boolean TagData)
	{
		tagData = setTag(tagData, 2, TagData);
	}	
	public final void setTagD(boolean TagData)
	{
		tagData = setTag(tagData, 3, TagData);
	}	
	public final void setTagE(boolean TagData)
	{
		tagData = setTag(tagData, 4, TagData);
	}	
	public final void setTagF(boolean TagData)
	{
		tagData = setTag(tagData, 5, TagData);
	}	
	public final void setTagG(boolean TagData)
	{
		tagData = setTag(tagData, 6, TagData);
	}	
	public final void setTagH(boolean TagData)
	{
		tagData = setTag(tagData, 7, TagData);
	}
	public final void setTagData(byte ByteValue)
	{
		tagData = ByteValue;
	}
	public final void setTag(int TagIndex, boolean TagData) throws NumberException
	{
		if(TagIndex < 0) throw new NumberException("AddressIndex", TagIndex, false);	
		if(TagIndex > 7) throw new NumberException("AddressIndex", TagIndex, 0, 7);
		tagData = setTag(tagData, TagIndex, TagData);
	}
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		tagData = StorageEncoder.readByte(InStream);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByte(OutStream, tagData);
	}
	
	public final boolean getTagA()
	{
		return getBit(0, tagData);
	}
	public final boolean getTagB()
	{
		return getBit(1, tagData);
	}	
	public final boolean getTagC()
	{
		return getBit(2, tagData);
	}	
	public final boolean getTagD()
	{
		return getBit(3, tagData);
	}	
	public final boolean getTagE()
	{
		return getBit(4, tagData);
	}	
	public final boolean getTagF()
	{
		return getBit(5, tagData);
	}	
	public final boolean getTagG()
	{
		return getBit(6, tagData);
	}	
	public final boolean getTagH()
	{
		return getBit(7, tagData);
	}
	public final boolean equals(Object compareObject)
	{
		return compareObject instanceof BoolTag && ((BoolTag)compareObject).tagData == tagData;
	}
	public final boolean getTag(int TagIndex) throws NumberException
	{
		if(TagIndex < 0) throw new NumberException("AddressIndex", TagIndex, false);	
		if(TagIndex > 7) throw new NumberException("AddressIndex", TagIndex, 0, 7);
		return getBit(TagIndex, tagData);
	}
	
	public final byte getTagData()
	{
		return tagData;
	}
	
	public final int hashCode()
	{
		return tagData * tagData + tagData -1;
	}
	
	public final String toString()
	{
		return "BoolTag(DT) [Tag: " + tagData + "]";
	}
	
	public final BoolTag clone()
	{
		return new BoolTag(tagData);
	}
	
	public static final byte getTagList(boolean... TagList)
	{
		return TagList != null ? getTag(TagList) : 0;
	}
	
	private static final byte getTag(boolean... Tags)
	{
		byte a = 0;
		for(byte b = 0; b < 8 && b < Tags.length; b++)
		{
			a = (byte)(a | ((Tags[b] ? 1 : 0) << b));
		}
		return a;
	}
	private static final byte setTag(byte TagData, int TagIndex, boolean Tag)
	{
		return (byte)(TagData | ((Tag ? 1 : 0) << TagIndex));
	}
	
	private static final boolean getBit(int Index, byte ByteValue)
	{
		return (ByteValue & (1 << Index)) != 0;
	}
}