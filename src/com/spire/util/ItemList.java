package com.spire.util;

import com.spire.io.Item;
import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.ex.NumberException;

public final class ItemList<T extends Item> extends SyncedList<T>
{
	public static final byte ITEM_CLASS_ID = 12;
	
	public ItemList()
	{
		super(ITEM_CLASS_ID, 10);
	}
	public ItemList(int StartingSize) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize);
	}
	
	@SuppressWarnings("unchecked")
	protected final void readItem(Streamable InStream) throws IOException
	{
		readItemMore(InStream);
		int a = 0;
		switch(itemEncoder.readByte(InStream))
		{
		case 0:
			a = itemEncoder.readUnsignedByte(InStream);
			break;
		case 1:
			a = itemEncoder.readUnsignedShort(InStream);
			break;
		case 2:
			a = itemEncoder.readInteger(InStream);
			break;
		default:
			a = 0;
			break;
		}
		Item b;
		dataElements.ensureCapacity(a);
		for(; a > 0; a--)
		{
			b = Item.getNextItem(InStream);
			if(b != null) dataElements.add((T)b);
		}
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{	
		writeItemMore(OutStream);
		if(dataElements.size() < 255)
		{
			itemEncoder.writeByte(OutStream, 0);
			itemEncoder.writeByte(OutStream, dataElements.size());
		}
		else if(dataElements.size() < Constants.MAX_USHORT_SIZE)
		{
			itemEncoder.writeByte(OutStream, 1);
			itemEncoder.writeShort(OutStream, dataElements.size());
		}
		else
		{
			itemEncoder.writeByte(OutStream, 2);
			itemEncoder.writeInteger(OutStream, dataElements.size());
		}
		for(int a = 0; a < dataElements.size(); a++)
			dataElements.get(a).writeStream(OutStream);
	}
	
	protected final ItemList<T> getCopy()
	{
		ItemList<T> a = new ItemList<T>();
		a.addAll(this);
		return a;
	}
	protected final ItemList<T> getNewCopy()
	{
		return new ItemList<T>();
	}
}