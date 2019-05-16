package com.spire.util;

import java.util.Collection;
import com.spire.ex.IDException;
import com.spire.ex.FormatException;
import com.spire.ex.NumberException;

public class SyncedList<T> extends DataList<T>
{
	public static final byte ITEM_CLASS_ID = 3;
	
	public SyncedList()
	{
		super(ITEM_CLASS_ID, 10);
	}
	public SyncedList(int StartingSize) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize);
	}
	
	protected SyncedList(int ItemID, int StartingSize) throws IDException, NumberException
	{
		super(ItemID, StartingSize);
	}

	protected void addElement(int AddIndex, T NewElement)
	{
		synchronized(dataElements)
		{
			dataElements.add(AddIndex, NewElement);
		}
	}
	protected void canClearList() throws FormatException { }
	protected void canRemoveElement() throws FormatException { }
	protected void canAddElement(T NewElement) throws FormatException { }
	
	protected boolean removeElement(T RemoveElement)
	{
		synchronized(dataElements)
		{
			return dataElements.remove(RemoveElement);
		}
	}
	protected boolean retainAndRemoveCollection(Collection<?> Collection, boolean Remove)
	{
		synchronized(dataElements)
		{
			if(Remove) return dataElements.removeAll(Collection);
			return dataElements.retainAll(Collection);
		}
	}
	
	protected T removeElement(int RemoveIndex)
	{
		synchronized(dataElements)
		{
			return dataElements.remove(RemoveIndex);
		}
	}
	
	protected DataList<T> getCopy()
	{
		SyncedList<T> a = new SyncedList<T>();
		a.addAll(this);
		return a;
	}
	protected SyncedList<T> getNewCopy()
	{
		return new SyncedList<T>();
	}
}