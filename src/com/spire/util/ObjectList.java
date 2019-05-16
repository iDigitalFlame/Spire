package com.spire.util;

import com.spire.ex.FormatException;
import com.spire.ex.NumberException;

public final class ObjectList extends ControlList<Object>
{
	public static final byte ITEM_CLASS_ID = 8;

	public ObjectList()
	{
		super(ITEM_CLASS_ID, 10, -1, false);
	}
	public ObjectList(int StartingSize) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, -1, false);
	}
	public ObjectList(int StartingSize, int MaxSize) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, MaxSize, false);
	}
	public ObjectList(int StartingSize, boolean Synced) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, -1, Synced);
	}
	public ObjectList(int StartingSize, int MaxSize, boolean Synced) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, MaxSize, Synced);
	}

	protected final void canClearList() throws FormatException { }
	protected final void canRemoveElement() throws FormatException { }
	protected final void canAddElement(Object NewElement) throws FormatException { }

	protected final ObjectList getCopy()
	{
		ObjectList a = new ObjectList();
		a.controlSize = controlSize;
		a.controlSync = controlSync;
		a.dataElements.addAll(dataElements);
		return a;
	}
	protected final ObjectList getNewCopy()
	{
		return new ObjectList();
	}
}