package com.spire.util;

import java.io.IOException;
import java.util.Collection;
import com.spire.io.Streamable;
import com.spire.ex.IDException;
import com.spire.ex.NumberException;

public abstract class ControlList<T> extends DataList<T>
{
	protected int controlSize;
	protected boolean controlSync;
	
	public final boolean isReadOnly()
	{
		return controlSize == -2;
	}
	public final boolean canAddElement()
	{
		return controlSize != -2 && (controlSize == -1 || controlSize > dataElements.size());
	}
	public final boolean isLimitedSize()
	{
		return controlSize >= 0;
	}
	public final boolean isSynchronized()
	{
		return controlSync;
	}
	
	protected ControlList(int ItemID) throws IDException
	{
		this(ItemID, 10, -1, false);
	}
	protected ControlList(int ItemID, int StartingSize) throws IDException, NumberException
	{
		this(ItemID, StartingSize, -1, false);
	}
	protected ControlList(int ItemID, int StartingSize, int MaxSize) throws IDException, NumberException
	{
		this(ItemID, StartingSize, MaxSize, false);
	}
	protected ControlList(int ItemID, int StartingSize, boolean Synced) throws IDException, NumberException
	{
		this(ItemID, StartingSize, -1, Synced);
	}
	protected ControlList(int ItemID, int StartingSize, int MaxSize, boolean Synced) throws IDException, NumberException
	{
		super(ItemID, StartingSize);
		controlSync = Synced;
		controlSize = MaxSize;
	}
	
	protected final void addElement(int AddIndex, T NewElement)
	{
		if(controlSync) synchronized(dataElements)
		{
			dataElements.add(AddIndex, NewElement);
		}
		else dataElements.add(AddIndex, NewElement);
	}
	protected final void readItemMore(Streamable InStream) throws IOException
	{
		controlSync = itemEncoder.readBoolean(InStream);
		controlSize = itemEncoder.readInteger(InStream);
	}
	protected final void writeItemMore(Streamable OutStream) throws IOException
	{
		itemEncoder.writeBoolean(OutStream, controlSync);
		itemEncoder.writeInteger(OutStream, controlSize);
	}
	
	protected final boolean removeElement(T RemoveElement)
	{
		if(controlSync) synchronized(dataElements)
		{
			return dataElements.remove(RemoveElement);
		}
		return dataElements.remove(RemoveElement);
	}
	protected final boolean retainAndRemoveCollection(Collection<?> Collection, boolean Remove)
	{
		if(controlSync) synchronized(dataElements)
		{
			if(Remove) return dataElements.removeAll(Collection);
			return dataElements.retainAll(Collection);
		}
		if(Remove) return dataElements.removeAll(Collection);
		return dataElements.retainAll(Collection);
	}
	
	protected final T removeElement(int RemoveIndex)
	{
		if(controlSync) synchronized(dataElements)
		{
			return dataElements.remove(RemoveIndex);
		}
		return dataElements.remove(RemoveIndex);
	}
}