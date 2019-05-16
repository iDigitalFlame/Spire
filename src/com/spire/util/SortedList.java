package com.spire.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedList<E extends Object, K extends Object>
{
	private static final Comparator<Object> HASH_SORTER = new SortedHashSorter();
	
	private final Comparator<Object> listSorter;
	private final ArrayList<E> listContents;
	
	private boolean listSorted;
	
	public SortedList()
	{
		this(10, HASH_SORTER);
	}
	public SortedList(int StartingSize)
	{
		this(StartingSize, HASH_SORTER);
	}
	public SortedList(int StartingSize, Comparator<Object> Sorter)
	{
		listSorter = Sorter;
		listContents = new ArrayList<E>();
	}
	
	public final void add(E NewElement)
	{
		if(NewElement != null)
		{
			listSorted = false;
			//synchronized(listContents)
			//{
				listContents.add(NewElement);
			//}
		}
	}
	public final void remove(K ElementKey)
	{
		int a = indexOf(ElementKey);
		if(a >= 0)
		{
			listSorted = false;
			//synchronized(listContents)
			//{
				listContents.remove(a);
			//}
		}
	}
	
	public final boolean isEmpty()
	{
		return listContents.isEmpty();
	}
	public final boolean isSorted()
	{
		return !listSorted;
	}
	public final boolean contains(K ElementKey)
	{
		return ElementKey != null ? indexOf(ElementKey) >= 0 : false;
	}
	
	public final int indexOf(K ElementKey)
	{
		sortList();
		if(ElementKey != null)
		{
			int a = Collections.binarySearch(listContents, ElementKey, listSorter);
			return a >= 0 ? a : -1;
		}
		return -1;
	}
	
	public final E get(K ElementKey)
	{
		int a = indexOf(ElementKey);
		return a >= 0 ? listContents.get(a) : null;
	}
	
	private final void sortList()
	{
		if(!listSorted)
		{
			Collections.sort(listContents, listSorter);
			listSorted = true;
		}
	}
	
	private static final class SortedHashSorter implements Comparator<Object>
	{
		public final int compare(Object Object1, Object Object2)
		{
			return Math.abs(Object1 != null ? Object1.hashCode() : 0) - Math.abs(Object2 != null ? Object2.hashCode() : 0);
		}
		
		private SortedHashSorter() { }
	}
}