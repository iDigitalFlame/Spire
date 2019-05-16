package com.spire.sec.per;

import com.spire.ex.NullException;

public final class IntegerRule extends ArrayRule<Integer>
{
	public static final byte ITEM_CLASS_ID = 35;
	
	public IntegerRule()
	{
		super(ITEM_CLASS_ID, false);
	}
	public IntegerRule(int... IntArray)
	{
		this(false, IntArray);
	}
	public IntegerRule(boolean ListOnly, int... IntArray)
	{
		super(ITEM_CLASS_ID, ListOnly);
		if(IntArray != null) setPermissionRules(IntArray);
	}
	
	public final void setPermissionRules(int... PermissionArguments) throws NullException
	{
		if(PermissionArguments == null) throw new NullException("PermissionArguments");
		super.setPermissionRules(intToint(PermissionArguments));
	}
	
	public static final IntegerRule createEmptyList()
	{
		return new IntegerRule();
	}
	public static final IntegerRule createOnlyList(int... IntArray) throws NullException
	{
		return new IntegerRule(true, IntArray);
	}
	public static final IntegerRule createExcludeList(int... IntArray) throws NullException
	{
		return new IntegerRule(false, IntArray);
	}
	
	private static final Integer[] intToint(int[] intArray)
	{
		Integer[] a = new Integer[intArray.length];
		for(int b = 0; b < a.length; b++) a[b] = Integer.valueOf(intArray[b]);
		return a;
	}
}