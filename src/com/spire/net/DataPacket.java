package com.spire.net;

import java.util.List;
import java.util.Arrays;
import com.spire.io.Item;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.util.ListIterator;
import com.spire.io.Streamable;
import com.spire.util.ObjectList;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.FormatException;

public final class DataPacket extends Packet implements List<Object>
{
	public static final byte ITEM_CLASS_ID = 7;
	
	private final ObjectList dataArray;
	
	public DataPacket()
	{
		this(0);
	}
	public DataPacket(int PacketID) throws NumberException
	{
		super(ITEM_CLASS_ID, PacketID);
		dataArray = new ObjectList();
	}
	public DataPacket(Object... ObjectData) throws FormatException
	{
		this(0, ObjectData);
	}
	public DataPacket(int PacketID, Object... ObjectData) throws NumberException, FormatException
	{
		this(PacketID);
		addAll(Arrays.asList(ObjectData));
	}
	
	public final void clear()
	{
		dataArray.clear();
	}
	public final void add(int AddIndex, Object NewElement) throws FormatException, NumberException
	{
		dataArray.add(AddIndex, NewElement);
	}
	
	public final boolean isEmpty()
	{
		return dataArray.isEmpty();
	}
	public final boolean add(Object NewElement)
	{
		return dataArray.add(NewElement);
	}
	public final boolean contains(Object SearchElement)
	{
		return dataArray.contains(SearchElement);
	}
	public final boolean remove(Object RemoveElement) throws FormatException
	{
		return dataArray.remove(RemoveElement);
	}
	public final boolean containsAll(Collection<?> SearchElements) throws NullException
	{
		return dataArray.containsAll(SearchElements);
	}
	public final boolean addAll(Collection<? extends Object> ElementCollection) throws NullException
	{
		return dataArray.addAll(ElementCollection);
	}
	public final boolean removeAll(Collection<?> RemoveCollection) throws FormatException, NullException
	{
		return dataArray.removeAll(RemoveCollection);
	}
	public final boolean retainAll(Collection<?> RetainCollection) throws FormatException, NullException
	{
		return dataArray.retainAll(RetainCollection);
	}
	public final boolean addAll(int AddIndex, Collection<? extends Object> ElementCollection) throws NullException, NumberException
	{
		return dataArray.addAll(AddIndex, ElementCollection);
	}
	
	public final int size()
	{
		return dataArray.size();
	}
	public final int hashCode()
	{
		return super.hashCode() + dataArray.hashCode();
	}
	public final int getPacketHash()
	{
		return super.getPacketHash() + dataArray.hashCode();
	}
	public final int indexOf(Object SearchElement)
	{
		return dataArray.indexOf(SearchElement);
	}
	public final int lastIndexOf(Object SearchElement)
	{
		return dataArray.lastIndexOf(SearchElement);
	}
	
	public final String toString()
	{
		return "DataPacket(" + getItemID() + ") P" + getID() + "S" + dataArray.size();
	}
	
	public final Object get(int ElementIndex) throws NumberException
	{
		return dataArray.get(ElementIndex);
	}
	public final Object remove(int RemoveIndex) throws FormatException, NumberException
	{
		return dataArray.remove(RemoveIndex);
	}
	public final Object set(int ElementIndex, Object NewElement) throws FormatException, NumberException
	{
		return dataArray.set(ElementIndex, NewElement);
	}
	
	public final Iterator<Object> iterator()
	{
		return dataArray.iterator();
	}
	
	public final ListIterator<Object> listIterator()
	{
		return dataArray.listIterator();
	}
	public final ListIterator<Object> listIterator(int StartIndex) throws NumberException
	{
		return dataArray.listIterator(StartIndex);
	}
	
	public final Object[] toArray()
	{
		return dataArray.toArray();
	}
	public final <T> T[] toArray(T[] ArrayType) throws NullException
	{
		return dataArray.toArray(ArrayType);
	}
	
	public final List<Object> subList(int StartIndex, int EndIndex) throws NumberException
	{
		return dataArray.subList(StartIndex, EndIndex);
	}

	public final ObjectList getList()
	{
		return dataArray;
	}
	
	protected final void readItemFailure()
	{
		super.readItemFailure();
		dataArray.clear();
	}
	protected final void readPacket(Streamable InStream) throws IOException
	{
		Item.readFinalItem(dataArray, InStream);
	}
	protected final void writePacket(Streamable OutStream) throws IOException
	{
		dataArray.writeStream(OutStream);
	}
	
	protected final DataPacket getCopy()
	{
		DataPacket a = new DataPacket();
		a.dataArray.addAll(dataArray);
		return a;
	}
}