package com.spire.util;

import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.ex.IDException;
import com.spire.ex.NumberException;

public abstract class ControlMap<K, V> extends DataMap<K, V>
{
	protected int controlSize;
	protected boolean controlSync;
	
	public final boolean isReadOnly()
	{
		return controlSize == -2;
	}
	public final boolean canAddElement()
	{
		return controlSize != -2 && (controlSize == -1 || controlSize > dataMap.size());
	}
	public final boolean isLimitedSize()
	{
		return controlSize >= 0;
	}
	public final boolean isSynchronized()
	{
		return controlSync;
	}
	
	protected ControlMap(int ItemID) throws IDException
	{
		this(ItemID, 16, 0.75F, -1, false);
	}
	protected ControlMap(int ItemID, int StartingSize) throws IDException, NumberException
	{
		this(ItemID, StartingSize, 0.75F, -1, false);
	}
	protected ControlMap(int ItemID, int StartingSize, float LoadFactor) throws IDException, NumberException
	{
		this(ItemID, StartingSize, LoadFactor, -1, false);
	}
	protected ControlMap(int ItemID, int StartingSize, float LoadFactor, int MaxSize) throws IDException, NumberException
	{
		this(ItemID, StartingSize, LoadFactor, MaxSize, false);
	}
	protected ControlMap(int ItemID, int StartingSize, float LoadFactor, boolean Synced) throws IDException, NumberException
	{
		this(ItemID, StartingSize, LoadFactor, -1, Synced);
	}
	protected ControlMap(int ItemID, int StartingSize, float LoadFactor, int MaxSize, boolean Synced) throws IDException, NumberException
	{
		super(ItemID, StartingSize, LoadFactor);
		controlSync = Synced;
		controlSize = MaxSize;
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
	
	protected final V removeElement(Object ElementKey)
	{
		if(controlSync) synchronized(dataMap)
		{
			return dataMap.remove(ElementKey);
		}
		return dataMap.remove(ElementKey);
	}
	protected final V putElement(K ElementKey, V ElementValue)
	{
		if(controlSync) synchronized(dataMap)
		{
			return dataMap.put(ElementKey, ElementValue);
		}
		return dataMap.put(ElementKey, ElementValue);
	}
}