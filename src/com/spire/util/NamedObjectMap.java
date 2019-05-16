package com.spire.util;

import com.spire.ex.FormatException;
import com.spire.ex.NumberException;

public final class NamedObjectMap extends ControlMap<String, Object>
{
	public static final byte ITEM_CLASS_ID = 13;
	
	public NamedObjectMap()
	{
		super(ITEM_CLASS_ID, 16, 0.75F, -1, false);
	}
	public NamedObjectMap(int StartingSize) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, 0.75F, -1, false);
	}
	public NamedObjectMap(int StartingSize, float LoadFactor) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, 0.75F, -1, false);
	}
	public NamedObjectMap(int StartingSize, float LoadFactor, int MaxSize) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, 0.75F, MaxSize, false);
	}
	public NamedObjectMap(int StartingSize, float LoadFactor, boolean Synced) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, 0.75F, -1, Synced);
	}
	public NamedObjectMap(int StartingSize, float LoadFactor, int MaxSize, boolean Synced) throws NumberException
	{
		super(ITEM_CLASS_ID, StartingSize, 0.75F, MaxSize, Synced);
	}

	protected final void canClearMap() throws FormatException
	{
	}
	protected final void canRemoveElements() throws FormatException
	{
	}
	protected final void canPutElement(String ElementKey, Object ElementValue) throws FormatException
	{	
	}

	protected final NamedObjectMap getNewCopy()
	{
		NamedObjectMap a = new NamedObjectMap();
		a.controlSize = controlSize;
		a.controlSync = controlSync;
		return a;
	}
}