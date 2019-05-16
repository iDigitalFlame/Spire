package com.spire.util;

import java.util.Map;
import java.util.Set;
import com.spire.io.Item;
import java.util.HashMap;
import java.io.IOException;
import java.util.Collection;
import com.spire.log.Reporter;
import com.spire.io.DataObject;
import com.spire.io.Streamable;
import com.spire.ex.IDException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.FormatException;

public abstract class DataMap<K, V> extends Item implements Map<K, V>
{
	protected final HashMap<K, V> dataMap;
	
	public final void clear() throws FormatException
	{
		canClearMap();
		dataMap.clear();
	}
	
	public final boolean isEmpty()
	{
		return dataMap.isEmpty();
	}
	public final boolean containsKey(Object ElementKey)
	{
		return dataMap.containsKey(ElementKey);
	}
	public final boolean containsValue(Object ElementValue)
	{
		return dataMap.containsValue(ElementValue);
	}
	
	public final int size()
	{
		return dataMap.size();
	}
	public final int hashCode()
	{
		return dataMap.hashCode();
	}	
	
	public final V get(Object ElementKey) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		return dataMap.get(ElementKey);
	}
	public final V remove(Object ElementKey) throws NullException, FormatException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		canRemoveElements();
		return removeElement(ElementKey);
	}
	public final V put(K ElementKey, V ElementValue) throws NullException, FormatException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		if(ElementValue == null) throw new NullException("ElementValue");
		if(!DataObject.supportsObjectType(ElementKey))
			throw new FormatException("ElementKey is not an Object type that DataObject supports!");
		if(!DataObject.supportsObjectType(ElementValue))
			throw new FormatException("ElementValue is not an Object type that DataObject supports!");
		return putElement(ElementKey, ElementValue);
	}
	@SuppressWarnings("unchecked")
	public final void putAll(Map<? extends K, ? extends V> NewMap) throws NullException, FormatException
	{
		if(NewMap == null) throw new NullException("NewMap");
		Object[] a = NewMap.keySet().toArray(),
				 b = NewMap.values().toArray();
		for(int c = 0; c < a.length; c++)
		{
			if(!DataObject.supportsObjectType(a[c]))
				throw new FormatException("One of \"NewMap\"'s ElementKeys are not an Object type that DataObject supports!");
			if(!DataObject.supportsObjectType(b[c]))
				throw new FormatException("One of \"NewMap\"'s ElementValues are not an Object type that DataObject supports!");
			canPutElement((K)a[c], (V)b[c]);
			putElement((K)a[c], (V)b[c]);
		}
	}
	
	public final Set<K> keySet()
	{
		return dataMap.keySet();
	}
	public final Set<Map.Entry<K, V>> entrySet()
	{
		return dataMap.entrySet();
	}
	
	public final Collection<V> values()
	{
		return dataMap.values();
	}
	
	protected DataMap(int ItemID, int StartSize) throws IDException, NumberException
	{
		this(ItemID, StartSize, 0.75F);
	}
	protected DataMap(int ItemID, int StartSize, float LoadFactor) throws IDException, NumberException
	{
		super(ItemID);
		if(StartSize < 0) throw new NumberException("StartSize", StartSize, false);
		if(LoadFactor <= 0) throw new NumberException("LoadFactor", StartSize, true);
		dataMap = new HashMap<K, V>(StartSize, LoadFactor);
	}

	protected final void readItemFailure()
	{
		dataMap.clear();
	}
	protected abstract void canClearMap() throws FormatException;
	protected abstract void canRemoveElements() throws FormatException;
	@SuppressWarnings("unused")
	protected void readItemMore(Streamable InStream) throws IOException
	{
		
	}
	@SuppressWarnings("unused")
	protected void writeItemMore(Streamable OutStream) throws IOException
	{
		
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
		DataObject b, c;
		for(; a > 0; a--)
		{
			b = (DataObject)Item.getNextItemByID(InStream, DataObject.ITEM_CLASS_ID);
			c = (DataObject)Item.getNextItemByID(InStream, DataObject.ITEM_CLASS_ID);
			if(b != null && c != null) try
			{
				dataMap.put((K)b.getObject(), (V)c.getObject());
			}
			catch (ClassCastException Exception)
			{
				Reporter.error(Reporter.REPORTER_IO, "DATAMAP_CAST_ERROR", Exception);
			}
			b = null;
			c = null;
		}
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		writeItemMore(OutStream);
		if(dataMap.size() < 255)
		{
			itemEncoder.writeByte(OutStream, 0);
			itemEncoder.writeByte(OutStream, dataMap.size());
		}
		else if(dataMap.size() < Constants.MAX_USHORT_SIZE)
		{
			itemEncoder.writeByte(OutStream, 1);
			itemEncoder.writeShort(OutStream, dataMap.size());
		}
		else
		{
			itemEncoder.writeByte(OutStream, 2);
			itemEncoder.writeInteger(OutStream, dataMap.size());
		}
		DataObject a;
		Object[] b = dataMap.keySet().toArray(),
				 c = dataMap.values().toArray();
		for(int d = 0; d < b.length; d++)
		{
			a = DataObject.createFromObject(b[d]);
			a.writeStream(OutStream);
			a = null;
			a = DataObject.createFromObject(c[d]);
			a.writeStream(OutStream);
			a = null;
		}
	}
	protected abstract void canPutElement(K ElementKey, V ElementValue) throws FormatException;
	
	protected final DataMap<K, V> getCopy()
	{
		DataMap<K, V> a = getNewCopy();
		a.dataMap.putAll(dataMap);
		return a;
	}
	protected abstract DataMap<K, V> getNewCopy();
	
	protected V removeElement(Object ElementKey)
	{
		return dataMap.remove(ElementKey);
	}
	protected V putElement(K ElementKey, V ElementValue)
	{
		return dataMap.put(ElementKey, ElementValue);
	}
}