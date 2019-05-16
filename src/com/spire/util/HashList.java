package com.spire.util;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractList;
import java.util.ListIterator;

import com.spire.ex.NullException;
import com.spire.ex.NumberException;

public class HashList<K, V extends HashKey<K>> implements List<V>
{
	private static final float HLIST_LOAD_FACTOR = 0.75F;
	private static final int HLIST_MAX_SIZE = 1073741824;
	
	private int hashSize;
	private int hashThreshold;
	private Object[] hashValues;
	private HashEntity[] hashData;
	
	public HashList()
	{
		hashThreshold = 12;
		hashValues = new Object[16];
		hashData = new HashEntity[16];
	}
	public HashList(int StartSize) throws NumberException
	{
		if(StartSize < 0) throw new NumberException("StartSize", StartSize, false);	
		if(StartSize > HLIST_MAX_SIZE) throw new NumberException("StartSize", StartSize, 0, HLIST_MAX_SIZE);
		int a = 1;
        for(; a < StartSize; a <<= 1) { }
		hashValues = new Object[a];
		hashData = new HashEntity[a];
		hashThreshold = (int)(a * HLIST_LOAD_FACTOR);
    }

	public final void clear()
	{
		hashSize = 0;
		hashThreshold = 12;
		synchronized(hashData)
        {
			Arrays.fill(hashData, null);
			hashData = new HashEntity[16];
        }
		synchronized(hashValues)
        {
			Arrays.fill(hashValues, null);
			hashValues = new Object[16];
        }
	}
	public final void addElement(V NewElement) throws NullException
	{
		if(NewElement == null) throw new NullException("NewElement");
		putElement(NewElement.getKey(), NewElement);
    }
	public final void putElement(K ElementKey, V NewElement) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		if(NewElement == null) throw new NullException("NewElement");
        int a = createHash(ElementKey.hashCode()), b = getHashIndex(a, hashData.length);
        synchronized(hashData)
        {
	        for(HashEntity c = hashData[b]; c != null; c = c.entityNext)
	        	synchronized(hashValues)
        		{
	        		if(hashValues[c.entityIndex] != null && c.entityHash == a && (c.entityKey == ElementKey || c.entityKey.equals(ElementKey)))
		        	{
			        	hashValues[c.entityIndex] = NewElement;
			        	return;
		        	}
        		}
	        synchronized(hashValues)
	        {
	        	hashValues[hashSize] = NewElement;
	        }
        	hashData[b] = new HashEntity(a, hashSize++, ElementKey, hashData[b]);
        	synchronized(hashValues)
	        {
        		if(hashSize >= hashThreshold) resizeList(hashData.length * 2);
	        }
        }
    }
	@SuppressWarnings("unchecked")
	public final void putAll(Map<? extends K, ? extends V> Map) throws NullException
	{
		if(Map == null) throw new NullException("Map");
		Object[] a = Map.keySet().toArray(), b = Map.values().toArray();
		for(int c = 0; c < a.length; c++) putElement((K)a[c], (V)b[c]);
	}
	public final void add(int ElementIndex, V NewElement) throws NullException, NumberException
	{
		if(NewElement == null) throw new NullException("NewElement");
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > hashValues.length) throw new NumberException("ElementIndex", ElementIndex, 0, hashValues.length);
        int a = createHash(NewElement.getKey().hashCode()), b = getHashIndex(a, hashData.length);
        synchronized(hashData)
        {
	        for(HashEntity c = hashData[b]; c != null; c = c.entityNext)
	        	synchronized(hashValues)
	            {
		        	if(hashValues[c.entityIndex] != null && c.entityHash == a && (c.entityKey == NewElement.getKey() || c.entityKey.equals(NewElement.getKey())))
		        	{
		        		hashValues[c.entityIndex] = NewElement;
		        		return;
		        	}
	            }
	        if(hashSize++ >= hashThreshold) resizeList(hashData.length * 2);
	        synchronized(hashValues)
            {
		        reHashArray(ElementIndex, false);
		        hashValues[ElementIndex] = NewElement;
            }
	        hashData[b] = new HashEntity(a, ElementIndex, NewElement.getKey(), hashData[b]);
        }
	}
	
	public final boolean isEmpty()
	{
		return hashSize == 0;
	}
	public final boolean add(V NewElement)
	{
		try
		{
			addElement(NewElement);
		}
		catch (NullException Exception)
		{
			return false;
		}
		return true;
	}
	public final boolean containsKey(K ElementKey)
	{
		return getIndexOf(ElementKey) >= 0;
	}
	@SuppressWarnings("unchecked")
	public final boolean contains(Object FindObject)
	{
		if(FindObject == null) return false;
		try
		{
			return getIndexOf((K)FindObject) >= 0;
		}
		catch (ClassCastException Exception) { }
		try
		{
			return getIndexOf(((V)FindObject).getKey()) >= 0;
		}
		catch (ClassCastException Exception) { }
		return false;
	}
	@SuppressWarnings("unchecked")
	public final boolean remove(Object RemoveObject)
	{
		if(RemoveObject == null) return false;
		try
		{
			return removeElement((K)RemoveObject) != null;
		}
		catch (ClassCastException Exception) { }
		try
		{
			return removeElement(((V)RemoveObject).getKey()) != null;
		}
		catch (ClassCastException Exception) { }
		return false;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof HashList<?,?> && ((CompareObject.hashCode() == hashCode()) || ((HashList<?,?>)CompareObject).hashValues.equals(hashValues));
	}
	public final boolean containsElement(K ElementKey)
	{
		return getIndexOf(ElementKey) >= 0;
	}
	public final boolean containsElement(V ElementValue)
	{
		return getIndexOf(ElementValue.getKey()) >= 0;
	}
	public final boolean removeAll(Collection<?> Collection) throws NullException
	{
		if(Collection == null) throw new NullException("Collection");
		boolean a = true;
		Object[] b = Collection.toArray();
		for(int c = 0; c < b.length; c++)
			if(!remove(b[c])) a = false;
		return a;
	}
	@SuppressWarnings("unchecked")
	public final boolean retainAll(Collection<?> Collection) throws NullException
	{
		if(Collection == null) throw new NullException("Collection");
		boolean a = false;
		for(int b = 0; b < hashSize; b++)
			if(!Collection.contains(hashValues[b]) &&
			   removeElement(((V)hashValues[b]).getKey()) != null) a = true;
		return a;
	}
	public final boolean containsAll(Collection<?> Collection) throws NullException
	{
		if(Collection == null) throw new NullException("Collection");
		Object[] a = Collection.toArray();
		for(int b = 0; b < a.length; b++)
			if(!contains(a[b])) return false;
		return true;
	}
	@SuppressWarnings("unchecked")
	public final boolean addAll(Collection<? extends V> Collection) throws NullException
	{
		if(Collection == null) throw new NullException("Collection");
		boolean a = false;
		Object[] b = Collection.toArray();
		for(int c = 0; c < b.length; c++) 
			if(add((V)b[c])) a = true;
		return a;
	}
	@SuppressWarnings("unchecked")
	public final boolean addAll(int ElementIndex, Collection<? extends V> Collection) throws NullException, NumberException
	{
		if(Collection == null) throw new NullException("Collection");
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > hashValues.length) throw new NumberException("ElementIndex", ElementIndex, 0, hashValues.length);
		boolean a = false;
		Object[] b = Collection.toArray();
		for(int c = 0, d = ElementIndex, e = hashSize; c < b.length; c++, d++)
		{
			add(d,(V)b[c]);
			if(hashSize > e)
			{
				a = true;
				e = hashSize;
			}
			else a = false;
		}
		return a;
	}
	
	public final int size()
	{
		return hashSize;
	}
	public final int hashCode()
	{
		int a = 0;
		for(int b = 0; b < hashSize; b++) a += hashValues[b].hashCode();
		return a;
	}
	public final int getIndexOf(K ElementKey)
	{
		if(ElementKey == null) return -1;
		int a = createHash(ElementKey.hashCode());
		for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
			if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementKey || b.entityKey.equals(ElementKey)))
				return b.entityIndex;
		return -1;
	}
	@SuppressWarnings("unchecked")
	public final int indexOf(Object FindObject)
	{
		if(FindObject == null) return -1;
		try
		{
			return getIndexOf((K)FindObject);
		}
		catch (ClassCastException Exception) { }
		try
		{
			return getIndexOf(((V)FindObject).getKey());
		}
		catch (ClassCastException Exception) { }
		return -1;
	}
	public final int getIndexOf(V ElementValue)
	{
		if(ElementValue == null) return -1;
		return indexOf(ElementValue.getKey());
	}
	public final int lastIndexOf(Object FindObject)
	{
		return indexOf(FindObject);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Un-synced get
	 * @param ElementKey
	 * @return
	 * @throws NullException
	 */
	public final V get(K ElementKey) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		int a = createHash(ElementKey.hashCode());
		for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
			if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementKey || b.entityKey.equals(ElementKey)))
				return (V)hashValues[b.entityIndex];
		return null;
	}
	@SuppressWarnings("unchecked")
	/**
	 * Synced get
	 * @param ElementKey
	 * @return
	 * @throws NullException
	 */
	public final V sget(K ElementKey) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		int a = createHash(ElementKey.hashCode());
		synchronized(hashData)
        {
			for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
				synchronized(hashValues)
		        {
					if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementKey || b.entityKey.equals(ElementKey)))
						return (V)hashValues[b.entityIndex];
		        }
        }
		return null;
	}
	@SuppressWarnings("unchecked")
	public final V get(int ElementIndex) throws NumberException
	{
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > hashSize) throw new NumberException("ElementIndex", ElementIndex, 0, hashSize);
		return (V)hashValues[ElementIndex];
	}
	@SuppressWarnings("unchecked")
	/**
	 * Un-synced get using similar hash to K
	 * @param ElementKey
	 * @return
	 * @throws NullException
	 */
	public final V oget(Object ElementKey) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		int a = createHash(ElementKey.hashCode());
		for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
			if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementKey || b.entityKey.equals(ElementKey)))
				return (V)hashValues[b.entityIndex];
		return null;
	}
	@SuppressWarnings("unchecked")
	/**
	 * Synced get using similar hash to K
	 * @param ElementKey
	 * @return
	 * @throws NullException
	 */
	public final V osget(Object ElementKey) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		int a = createHash(ElementKey.hashCode());
		synchronized(hashData)
        {
			for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
				synchronized(hashValues)
		        {
					if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementKey || b.entityKey.equals(ElementKey)))
						return (V)hashValues[b.entityIndex];
		        }
        }
		return null;
	}
	@SuppressWarnings("unchecked")
	public final V remove(int ElementIndex) throws NumberException
	{
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > hashSize) throw new NumberException("ElementIndex", ElementIndex, 0, hashSize);
		return removeElement(((V)hashValues[ElementIndex]).getKey());
	}
	@SuppressWarnings("unchecked")
	public final V removeElement(K ElementKey) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		int a = createHash(ElementKey.hashCode()), b = getHashIndex(a, hashData.length);
		HashEntity c, d;
		V e;
		synchronized(hashData)
        {
			for(c = hashData[b], d = c; d != null; c = d, d = d.entityNext)
				synchronized(hashValues)
		        {
					if(hashValues[d.entityIndex] != null && d.entityHash == a && (d.entityKey == ElementKey || d.entityKey.equals(ElementKey)))
					{
						hashSize--;
						if(c == d) hashData[b] = d.entityNext;
						else c.entityNext = d.entityNext;
						e = (V)hashValues[d.entityIndex];
						reHashArray(d.entityIndex, true);
						return e;
					}
		        }
        }
		return null;
	}
	public final V removeElement(V ElementValue) throws NullException
	{
		if(ElementValue == null) throw new NullException("ElementValue");
		return removeElement(ElementValue.getKey());
	}	
	@SuppressWarnings("unchecked")
	public final V set(K ElementKey, V ElementValue) throws NullException
	{
		if(ElementKey == null) throw new NullException("ElementKey");
		if(ElementValue == null) throw new NullException("ElementValue");
		int a = createHash(ElementKey.hashCode());
		synchronized(hashData)
        {
			for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
				synchronized(hashValues)
		        {
					if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementKey || b.entityKey.equals(ElementKey)))
					{
						Object c = hashValues[b.entityIndex];
						hashValues[b.entityIndex] = ElementValue;
						return (V)c;
					}
		        }
	    }
		putElement(ElementKey, ElementValue);
		return null;
	}
	@SuppressWarnings("unchecked")
	public final V set(int ElementIndex, V ElementValue) throws NullException, NumberException
	{
		if(ElementValue == null) throw new NullException("ElementValue");
		if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
		if(ElementIndex > hashSize) throw new NumberException("ElementIndex", ElementIndex, 0, hashSize);
		int a = createHash(ElementValue.getKey().hashCode());
		synchronized(hashData)
        {	
			for(HashEntity b = hashData[getHashIndex(a, hashData.length)]; b != null; b = b.entityNext)
				synchronized(hashValues)
		        {
					if(hashValues[b.entityIndex] != null && b.entityHash == a && (b.entityKey == ElementValue.getKey() || b.entityKey.equals(ElementValue.getKey())))
					{
						Object c = hashValues[b.entityIndex];
						hashSize--;
						reHashArray(b.entityIndex, true);
						hashSize++;
						reHashArray(ElementIndex, false);
						b.entityIndex = ElementIndex;
						hashValues[ElementIndex] = ElementValue;
						return (V)c;
					}
		        }
        }
		add(ElementIndex, ElementValue);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public final Iterator<V> iterator()
	{
		return new HashIterator<V>((HashList<Object, HashKey<Object>>)this);
	}
	@SuppressWarnings("unchecked")
	public final ListIterator<V> listIterator()
	{
		return new HashListIterator<V>((HashList<Object, HashKey<Object>>)this, 0);
	}
	@SuppressWarnings("unchecked")
	public final ListIterator<V> listIterator(int StartIndex) throws NumberException
	{
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);	
		if(StartIndex > hashValues.length) throw new NumberException("StartIndex", StartIndex, 0, hashValues.length);
		return new HashListIterator<V>((HashList<Object, HashKey<Object>>)this, 0);
	}
	
	public final List<V> subList(int StartIndex)
	{
		return subList(StartIndex, hashSize);
	}
	public final List<V> subList(int StartIndex, int EndIndex)
	{
		return new HashArrayList<V>(hashValues, StartIndex, EndIndex);
	}
	
	public final Object[] toArray()
	{
		return Arrays.copyOf(hashValues, hashSize);
	}
	
	@SuppressWarnings("hiding")
	public final <V> V[] toArray(V[] ArrayType)
	{
		@SuppressWarnings("unchecked")
		V[] a = ArrayType.length >= hashSize ? ArrayType :
			    (V[])java.lang.reflect.Array.newInstance(ArrayType.getClass().getComponentType(), hashSize);
		System.arraycopy(hashValues, 0, a, 0, hashSize);
		return a;
	}
	
	private final void resizeList(int NewSize)
	{
		if(hashData.length == HLIST_MAX_SIZE)
		{
			hashThreshold = Integer.MAX_VALUE;
			return;
		}
		Object[] a = new Object[NewSize];
		HashEntity[] b = new HashEntity[NewSize];
		System.arraycopy(hashValues, 0, a, 0, (NewSize >= hashValues.length) ? hashValues.length : NewSize);
		for(int c = 0, d = 0; c < hashData.length; c++)
		{
			HashEntity e = hashData[c], f;
			if(e != null)
			{
				hashData[c] = null;
				do
				{
					f = e.entityNext;
					d = getHashIndex(e.entityHash, NewSize);
					e.entityNext = b[d];
					b[d] = e;
					e = f;
				}
				while(e != null);
			}
		}
		hashData = b;
		hashValues = a;
		hashThreshold = (int)(NewSize * HLIST_LOAD_FACTOR);
	}
	@SuppressWarnings("unchecked")
	private final void reHashArray(int StartIndex, boolean Remove)
	{
		if(Remove) System.arraycopy(hashValues, StartIndex + 1, hashValues, StartIndex, (hashValues.length - StartIndex) - 1);
		else System.arraycopy(hashValues, StartIndex, hashValues, StartIndex + 1, (hashValues.length - StartIndex) - 1);
		for(int a = StartIndex, b = 0; a < hashSize; a++)
		{
			b = createHash(((V)hashValues[a]).getKey().hashCode());
			for(HashEntity c = hashData[getHashIndex(b, hashData.length)]; c != null; c = c.entityNext)
				if(c.entityIndex > StartIndex) c.entityIndex += (Remove ? -1 : 1);
		}
		if(hashSize > 16 && hashSize < (hashThreshold / 2))
		{
			int d = 1;
			for(; d < (hashSize * 1); d <<= 1) { }
			resizeList(d);
		}
	}
	
	private static final int createHash(int HashCode)
	{
		int a = (HashCode ^ (HashCode >>> 20) ^ (HashCode >>> 12)) * 3;
		return a ^ (a >>> 7) ^ (a >>> 4);
	}
	private static final int getHashIndex(int HashCode, int ListSize)
	{
		return HashCode & (ListSize - 1);
	}
	
	private static final class HashEntity
	{
		private final int entityHash;
		
		private int entityIndex;
		private Object entityKey;
		private HashEntity entityNext;
		
		private HashEntity(int EntityHash, int EntityIndex, Object EntityKey, HashEntity NextEntry)
		{
			entityKey = EntityKey;
			entityNext = NextEntry;
			entityHash = EntityHash;
			entityIndex = EntityIndex;
		}
	}
	private static class HashIterator<V> implements Iterator<V>
	{
		protected int iteratorIndex;
		protected Object[] iteratorData;
		protected HashList<Object, HashKey<Object>> iteratorOwner;
		
		public final void remove()
		{
			iteratorOwner.remove(iteratorIndex--);
			refreshData();
		}
		
		public final boolean hasNext()
		{
			return iteratorIndex < iteratorData.length;
		}

		@SuppressWarnings("unchecked")
		public final V next()
		{
			return (V)iteratorData[iteratorIndex++];
		}
		
		protected final void refreshData()
		{
			iteratorData = Arrays.copyOf(iteratorOwner.hashData, iteratorOwner.hashSize);
		}
		
		private HashIterator(HashList<Object, HashKey<Object>> HashList)
		{
			iteratorOwner = HashList;
			iteratorData = Arrays.copyOf(HashList.hashData, HashList.hashSize);
		}
	}
	private static final class HashArrayList<V> extends AbstractList<V>
	{
		private final Object[] listElements;

		public final int size()
		{
			return listElements.length;
		}
        public final int indexOf(Object FindObject)
        {
            if(FindObject == null)return -1;
            for(int a = 0; a < listElements.length; a++) if(FindObject.equals(listElements[a])) return a;
            return -1;
        }
		
		@SuppressWarnings("unchecked")
		public final V get(int ElementIndex) throws NumberException
        {
			if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
			if(ElementIndex > listElements.length) throw new NumberException("ElementIndex", ElementIndex, 0, listElements.length);
            return (V)listElements[ElementIndex];
        }
		@SuppressWarnings("unchecked")
        public final V set(int ElementIndex, V NewElement) throws NullException, NumberException
        {
        	if(NewElement == null) throw new NullException("NewElement");
        	if(ElementIndex < 0) throw new NumberException("ElementIndex", ElementIndex, false);	
    		if(ElementIndex > listElements.length) throw new NumberException("ElementIndex", ElementIndex, 0, listElements.length);
			V a = (V)listElements[ElementIndex];
            listElements[ElementIndex] = NewElement;
            return a;
        }
		
		public final Object[] toArray()
		{
            return Arrays.copyOf(listElements, listElements.length);
		}
		
		private HashArrayList(Object[] Array, int Start, int End)
		{
			listElements = new Object[End - Start];
			System.arraycopy(Array, Start, listElements, 0, End - Start);
		}
	}
	private static class HashListIterator<V> extends HashIterator<V> implements ListIterator<V>
	{
		@SuppressWarnings("unchecked")
		public final void add(V NewElement) throws NullException
		{
			iteratorOwner.add((HashKey<Object>)NewElement);
			refreshData();
		}
		@SuppressWarnings("unchecked")
		public final void set(V NewElement)
		{
			iteratorOwner.set(iteratorIndex, (HashKey<Object>)NewElement);
		}

		public final boolean hasPrevious()
		{
			return iteratorIndex > 0;
		}

		public final int nextIndex()
		{
			return iteratorIndex;
		}
		public final int previousIndex()
		{
			return iteratorIndex + 1;
		}

		@SuppressWarnings("unchecked")
		public final V previous()
		{
			return (V)iteratorOwner.get(--iteratorIndex);
		}
		
		private HashListIterator(HashList<Object, HashKey<Object>> HashList, int StartIndex)
		{
			super(HashList);
			iteratorIndex = StartIndex;
		}
	}
}