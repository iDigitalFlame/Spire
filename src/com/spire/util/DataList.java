package com.spire.util;

import java.util.List;
import com.spire.io.Item;
import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import com.spire.log.Reporter;
import com.spire.io.Streamable;
import com.spire.io.DataObject;
import com.spire.ex.IDException;
import com.spire.ex.NullException;
import com.spire.ex.FormatException;
import com.spire.ex.NumberException;

public abstract class DataList<T> extends Item implements List<T>
{
	protected final ArrayList<T> dataElements;
	
	public final void clear() throws FormatException
	{
		canClearList();
		dataElements.clear();
	}
	public final void addElement(T NewElement) throws FormatException
	{
		add(dataElements.size(), NewElement);
	}
	public final void add(int AddIndex, T NewElement) throws FormatException, NumberException
	{
		if(AddIndex < 0) throw new NumberException("AddIndex", AddIndex, false);	
		if(AddIndex > dataElements.size()) throw new NumberException("AddIndex", AddIndex, 0, dataElements.size());
		if(!DataObject.supportsObjectType(NewElement))
			throw new FormatException("This is not an Object type that DataObject supports!");
		canAddElement(NewElement);
		addElement(AddIndex, NewElement);
	}
	
	public final boolean isEmpty()
	{
		return dataElements.isEmpty();
	}
	public final boolean add(T NewElement)
	{
		if(!DataObject.supportsObjectType(NewElement)) return false;
		try
		{
			canAddElement(NewElement);
		}
		catch (FormatException Exception)
		{
			return false;
		}
		addElement(dataElements.size(), NewElement);
		return true;
	}
	public boolean contains(Object SearchElement)
	{
		return dataElements.contains(SearchElement);
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof DataList && ((DataList<?>)CompareObject).dataElements.equals(dataElements);
	}
	@SuppressWarnings("unchecked")
	public final boolean remove(Object RemoveElement) throws FormatException
	{
		canRemoveElement();
		return removeElement((T)RemoveElement);
	}
	public boolean containsAll(Collection<?> SearchElements) throws NullException
	{
		if(SearchElements == null) throw new NullException("SearchElements");
		return dataElements.containsAll(SearchElements);
	}
	public final boolean addAll(Collection<? extends T> ElementCollection) throws NullException
	{
		if(ElementCollection == null) throw new NullException("ElementCollection");
		return addAll(dataElements.size(), ElementCollection);
	}
	public boolean removeAll(Collection<?> RemoveCollection) throws FormatException, NullException
	{
		if(RemoveCollection == null) throw new NullException("RemoveCollection");
		canRemoveElement();
		return retainAndRemoveCollection(RemoveCollection, true);
	}
	public boolean retainAll(Collection<?> RetainCollection) throws FormatException, NullException
	{
		if(RetainCollection == null) throw new NullException("RetainCollection");
		canRemoveElement();
		return retainAndRemoveCollection(RetainCollection, false);
	}
	@SuppressWarnings("unchecked")
	public final boolean addAll(int AddIndex, Collection<? extends T> ElementCollection) throws NullException, NumberException
	{
		if(ElementCollection == null) throw new NullException("ElementCollection");
		if(AddIndex < 0) throw new NumberException("AddIndex", AddIndex, false);	
		if(AddIndex > dataElements.size()) throw new NumberException("AddIndex", AddIndex, 0, dataElements.size());
		Object[] a = ElementCollection.toArray();
		boolean b = false;
		for(int c = 0, d = AddIndex; c < a.length; c++)
		{
			if(DataObject.supportsObjectType(a[c])) try
			{
				canAddElement((T)a[c]);
				addElement(d + c, (T)a[c]);
				b = true;
			}
			catch (FormatException Exception) { }
		}
		return b;
	}
	
	public final int size()
	{
		return dataElements.size();
	}
	public final int hashCode()
	{
		return dataElements.hashCode();
	}
	public int indexOf(Object SearchElement)
	{
		return dataElements.indexOf(SearchElement);
	}
	public int lastIndexOf(Object SearchElement)
	{
		return dataElements.lastIndexOf(SearchElement);
	}
	
	public final String toString()
	{
		return getClass().getSimpleName() + "(" + getItemID() + ") s:" + dataElements.size();
	}
	
	public final T get(int ElementIndex) throws NumberException
	{
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > dataElements.size()) throw new NumberException("ElementIndex", ElementIndex, 0, dataElements.size());
		return dataElements.get(ElementIndex);
	}
	public final T remove(int RemoveIndex) throws FormatException, NumberException
	{
		if(RemoveIndex < 0) throw new NumberException("RemoveIndex", RemoveIndex, false);	
		if(RemoveIndex > dataElements.size()) throw new NumberException("RemoveIndex", RemoveIndex, 0, dataElements.size());
		canRemoveElement();
		return removeElement(RemoveIndex);
	}
	public final T set(int ElementIndex, T NewElement) throws FormatException, NumberException
	{
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > dataElements.size()) throw new NumberException("ElementIndex", ElementIndex, 0, dataElements.size());
		canAddElement(NewElement);
		T a = removeElement(ElementIndex);
		if(!DataObject.supportsObjectType(NewElement))
			throw new FormatException("This is not an Object type that DataObject supports!");
		addElement(ElementIndex, NewElement);
		return a;
	}
	
	public final Iterator<T> iterator()
	{
		return dataElements.iterator();
	}
	
	public final ListIterator<T> listIterator()
	{
		return dataElements.listIterator();
	}
	public final ListIterator<T> listIterator(int StartIndex) throws NumberException
	{
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);	
		if(StartIndex > dataElements.size()) throw new NumberException("StartIndex", StartIndex, 0, dataElements.size());
		return dataElements.listIterator(StartIndex);
	}
	
	public final Object[] toArray()
	{
		return dataElements.toArray();
	}
	
	@SuppressWarnings("hiding")
	public final <T> T[] toArray(T[] ArrayType) throws NullException
	{
		if(ArrayType == null) throw new NullException("ArrayType");
		return dataElements.toArray(ArrayType);
	}
	
	public final DataList<T> subList(int StartIndex, int EndIndex) throws NumberException
	{
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);	
		if(StartIndex > EndIndex) throw new NumberException("StartIndex", StartIndex, 0, EndIndex);
		if(EndIndex < StartIndex) throw new NumberException("EndIndex", EndIndex, StartIndex, dataElements.size());	
		if(EndIndex > dataElements.size()) throw new NumberException("EndIndex", EndIndex, StartIndex, dataElements.size());
		DataList<T> a = getNewCopy();
		a.dataElements.addAll(dataElements.subList(StartIndex, EndIndex));
		return a;
	}
	
	protected DataList(int ItemID) throws IDException
	{
		this(ItemID, 10);
	}
	protected DataList(int ItemID, int StartingSize) throws IDException, NumberException
	{
		super(ItemID);
		if(StartingSize <= 0) throw new NumberException("StartingSize", StartingSize, true);	
		dataElements = new ArrayList<T>();
	}
	
	protected void readItemFailure()
	{
		dataElements.clear();
	}
	protected void addElement(int AddIndex, T NewElement)
	{
		dataElements.add(AddIndex, NewElement);
	}
	protected abstract void canClearList() throws FormatException;
	@SuppressWarnings("unchecked")
	protected void readItem(Streamable InStream) throws IOException
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
		DataObject b;
		dataElements.ensureCapacity(a);
		for(; a > 0; a--)
		{
			b = (DataObject)Item.getNextItemByID(InStream, DataObject.ITEM_CLASS_ID);
			if(b != null) try
			{
				dataElements.add((T)b.getObject());
			}
			catch (ClassCastException Exception)
			{
				Reporter.error(Reporter.REPORTER_IO, "DATALIST_CAST_ERROR", Exception);
			}
			b = null;
		}
	}
	protected void writeItem(Streamable OutStream) throws IOException
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
		DataObject a;
		for(int b = 0; b < dataElements.size(); b++)
		{
			a = DataObject.createFromObject(dataElements.get(b));
			a.writeStream(OutStream);
			a = null;
		}
	}
	protected abstract void canRemoveElement() throws FormatException;
	@SuppressWarnings("unused")
	protected void readItemMore(Streamable InStream) throws IOException
	{
		
	}
	@SuppressWarnings("unused")
	protected void writeItemMore(Streamable OutStream) throws IOException
	{
		
	}
	protected abstract void canAddElement(T NewElement) throws FormatException;
	
	protected boolean removeElement(T RemoveElement)
	{
		return dataElements.remove(RemoveElement);
	}
	protected boolean retainAndRemoveCollection(Collection<?> Collection, boolean Remove)
	{
		if(Remove) return dataElements.removeAll(Collection);
		return dataElements.retainAll(Collection);
	}
	
	protected T removeElement(int RemoveIndex)
	{
		return dataElements.remove(RemoveIndex);
	}

	protected abstract DataList<T> getCopy();
	protected abstract DataList<T> getNewCopy();
}