package com.spire.net;

import java.util.List;
import com.spire.io.Item;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import com.spire.util.Stamp;
import java.util.Collection;
import java.util.ListIterator;
import com.spire.io.Streamable;
import com.spire.util.HashList;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

public final class Group extends Item implements List<Computer>
{
	public static final byte ITEM_CLASS_ID = 11;
	
	private static final HashMap<String, Short> NAME_TO_ID_LIST = new HashMap<String, Short>();
	
	private final Stamp groupCreation;
	private final HashList<Short, Computer> groupList;
	
	public Group()
	{
		this(16);
	}
	public Group(int StartSize) throws NumberException
	{
		super(11);
		groupCreation = new Stamp();
		groupList = new HashList<Short, Computer>(StartSize);
	}
	public Group(Group CloneGroup) throws NullException
	{
		this();
		if(CloneGroup == null) throw new NullException("CloneGroup");
		groupList.addAll(CloneGroup.groupList);
	}
	
	public final void clear()
	{
		groupCreation.updateStamp();
	}
	public final void addComputer(Computer NewComputer) throws NullException
	{
		if(NewComputer == null) throw new NullException("NewComputer");
		if(!NAME_TO_ID_LIST.containsKey(NewComputer.computerName))
			NAME_TO_ID_LIST.put(NewComputer.computerName, NewComputer.getKey());
		groupList.putElement(NewComputer.getKey(), NewComputer);
		groupCreation.updateStamp();
	}
	public final void add(int ElementIndex, Computer NewComputer) throws NullException, NumberException
	{
		groupList.add(ElementIndex, NewComputer);
		if(!NAME_TO_ID_LIST.containsKey(NewComputer.computerName))
			NAME_TO_ID_LIST.put(NewComputer.computerName, NewComputer.getKey());
	}
	
	public final boolean isEmpty()
	{
		return groupList.isEmpty();
	}
	public final boolean add(Computer NewComputer)
	{
		if(NewComputer == null) return false;
		if(!NAME_TO_ID_LIST.containsKey(NewComputer.computerName))
			NAME_TO_ID_LIST.put(NewComputer.computerName, NewComputer.getKey());
		boolean a = groupList.add(NewComputer);
		if(a) groupCreation.updateStamp();
		return a;
	}
	public final boolean contains(short ComputerID)
	{
		return contains(Short.valueOf(ComputerID));
	}
	public final boolean contains(Short ComputerID)
	{
		return groupList.containsKey(ComputerID);
	}
	public final boolean remove(Object RemoveObject)
	{
		if(RemoveObject instanceof Short) return remove((Short)RemoveObject) != null;
		if(RemoveObject instanceof String) return remove((String)RemoveObject) != null;
		return false;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Group && ((Group)CompareObject).groupList.equals(groupList);
	}
	public final boolean contains(String ComputerName)
	{
		if(ComputerName == null || ComputerName.isEmpty()) return false;
		if(NAME_TO_ID_LIST.containsKey(ComputerName))
			return groupList.containsKey(NAME_TO_ID_LIST.get(ComputerName));
		Short a = Short.valueOf(Computer.getComputerID(ComputerName));
		NAME_TO_ID_LIST.put(ComputerName, a);
		return groupList.containsKey(a);
	}
	public final boolean isNewThan(Stamp CompareStamp)
	{
		return groupCreation.isTimeNewer(CompareStamp);
	}
	public final boolean contains(Object SearchObject)
	{
		if(SearchObject instanceof Short) return contains((Short)SearchObject);
		if(SearchObject instanceof String) return contains((String)SearchObject);
		return false;
	}
	public final boolean removeAll(Collection<?> ComputerList) throws NullException
	{
		boolean a = groupList.removeAll(ComputerList);
		if(a) groupCreation.updateStamp();
		return a;
	}
	public final boolean retainAll(Collection<?> ComputerList) throws NullException
	{
		boolean a = groupList.retainAll(ComputerList);
		if(a) groupCreation.updateStamp();
		return a;
	}
	public final boolean containsAll(Collection<?> ComputerList) throws NullException
	{
		return groupList.containsAll(ComputerList);
	}
	public final boolean addAll(Collection<? extends Computer> ComputerList) throws NullException
	{
		boolean a = groupList.addAll(ComputerList);
		if(a) groupCreation.updateStamp();
		return a;
	}
	public final boolean addAll(int ElementIndex, Collection<? extends Computer> ComputerList) throws NullException
	{
		boolean a = groupList.addAll(ElementIndex, ComputerList);
		if(a) groupCreation.updateStamp();
		return a;
	}
	
	public final int size()
	{
		return groupList.size();
	}
	public final int hashCode()
	{
		return groupList.hashCode() + groupCreation.hashCode();
	}
	public final int indexOf(short ComputerID)
	{
		return indexOf(Short.valueOf(ComputerID));
	}
	public final int indexOf(Short ComputerID)
	{
		return groupList.indexOf(ComputerID);
	}
	public final int indexOf(String ComputerName)
	{
		if(ComputerName == null || ComputerName.isEmpty()) return -1;
		if(NAME_TO_ID_LIST.containsKey(ComputerName))
			return groupList.indexOf(NAME_TO_ID_LIST.get(ComputerName));
		Short a = Short.valueOf(Computer.getComputerID(ComputerName));
		NAME_TO_ID_LIST.put(ComputerName, a);
		return groupList.indexOf(a);
	}
	public final int indexOf(Object SearchObject)
	{
		if(SearchObject instanceof Short) return indexOf((Short)SearchObject);
		if(SearchObject instanceof String) return indexOf((String)SearchObject);
		return -1;
	}
	public final int lastIndexOf(Object SearchObject)
	{
		return groupList.lastIndexOf(SearchObject);
	}
	
	public final Stamp getStamp()
	{
		return groupCreation.clone();
	}
	
	public final Iterator<Computer> iterator()
	{
		return groupList.iterator();
	}
	
	public final ListIterator<Computer> listIterator()
	{
		return groupList.listIterator();
	}
	public final ListIterator<Computer> listIterator(int StartIndex) throws NumberException
	{
		return groupList.listIterator(StartIndex);
	}
	
	public final List<Computer> subList(int StartIndex, int EndIndex) throws NumberException
	{
		return groupList.subList(StartIndex, EndIndex);
	}
	
	public final Computer get(short ComputerID)
	{
		return get(Short.valueOf(ComputerID));
	}
	public final Computer remove(short ComputerID)
	{
		return remove(Short.valueOf(ComputerID));
	}
	public final Computer get(Short ComputerID) throws NullException
	{
		return groupList.get(ComputerID);
	}
	public final Computer remove(Short ComputerID) throws NullException
	{
		Computer a = groupList.removeElement(ComputerID);
		if(a != null) groupCreation.updateStamp();
		return a;
	}
	public final Computer get(int ComputerIndex) throws NumberException
	{
		if(ComputerIndex < 0) throw new NumberException("ComputerIndex", ComputerIndex, false);
		if(ComputerIndex > groupList.size()) throw new NumberException("ComputerIndex", ComputerIndex, 0, groupList.size());
		return groupList.get(ComputerIndex);
	}
	public final Computer remove(int ElementIndex) throws NumberException
	{
		Computer a = groupList.remove(ElementIndex);
		if(a != null) groupCreation.updateStamp();
		return a;
	}	
	public final Computer get(String ComputerName) throws NullException, StringException
	{
		if(ComputerName == null) throw new NullException("ComputerName");
		if(ComputerName.isEmpty()) throw new StringException("ComputerName");
		if(NAME_TO_ID_LIST.containsKey(ComputerName))
			return groupList.get(NAME_TO_ID_LIST.get(ComputerName));
		Short a = Short.valueOf(Computer.getComputerID(ComputerName));
		NAME_TO_ID_LIST.put(ComputerName, a);
		return groupList.get(a);
	}
	public final Computer remove(String ComputerName) throws NullException, StringException
	{
		if(ComputerName == null) throw new NullException("ComputerName");
		if(ComputerName.isEmpty()) throw new StringException("ComputerName");
		if(NAME_TO_ID_LIST.containsKey(ComputerName))
			return remove(NAME_TO_ID_LIST.get(ComputerName));
		Short a = Short.valueOf(Computer.getComputerID(ComputerName));
		NAME_TO_ID_LIST.put(ComputerName, a);
		return remove(a);
	}
	public final Computer set(int ElementIndex, Computer SetComputer) throws NullException, NumberException
	{
		Computer a = groupList.set(ElementIndex, SetComputer);
		groupCreation.updateStamp();
		return a;
	}
	
	public final  Object[] toArray()
	{
		return groupList.toArray();
	}
	public final <T> T[] toArray(T[] ArrayType)
	{
		return groupList.toArray(ArrayType);
	}
	
	protected final void readItemFailure()
	{
		groupList.clear();
		groupCreation.updateStamp(new byte[] { 0, 0, 0, 0, 0, 0 });
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		groupCreation.readStorage(InStream, itemEncoder);
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
		Computer b = null;
		for(; a > 0; a--)
		{
			b = (Computer)Item.getNextItemByID(InStream, 1);
			if(b != null) groupList.putElement(b.getKey(), b);
		}
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		groupCreation.writeStorage(OutStream, itemEncoder);
		if(groupList.size() < 255)
		{
			itemEncoder.writeByte(OutStream, 0);
			itemEncoder.writeByte(OutStream, groupList.size());
		}
		else if(groupList.size() < Constants.MAX_USHORT_SIZE)
		{
			itemEncoder.writeByte(OutStream, 1);
			itemEncoder.writeShort(OutStream, groupList.size());
		}
		else
		{
			itemEncoder.writeByte(OutStream, 2);
			itemEncoder.writeInteger(OutStream, groupList.size());
		}
		for(int a = 0; a < groupList.size(); a++)
			groupList.get(a).writeStream(OutStream);
	}
}