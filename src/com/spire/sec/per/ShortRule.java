package com.spire.sec.per;

import com.spire.ex.NullException;

public final class ShortRule extends ArrayRule<Short>
{
	public static final byte ITEM_CLASS_ID = 34;
	
	public ShortRule()
	{
		super(ITEM_CLASS_ID, false);
	}
	public ShortRule(short... ShortArray)
	{
		this(false, ShortArray);
	}
	public ShortRule(boolean ListOnly, short... ShortArray)
	{
		super(ITEM_CLASS_ID, ListOnly);
		if(ShortArray != null) setPermissionRules(ShortArray);
	}
	
	public final void setPermissionRules(short... PermissionArguments) throws NullException
	{
		if(PermissionArguments == null) throw new NullException("PermissionArguments");
		super.setPermissionRules(shortToshort(PermissionArguments));
	}
	
	public static final ShortRule createEmptyList()
	{
		return new ShortRule();
	}
	public static final ShortRule createOnlyList(short... ShortArray) throws NullException
	{
		return new ShortRule(true, ShortArray);
	}
	public static final ShortRule createExcludeList(short... ShortArray) throws NullException
	{
		return new ShortRule(false, ShortArray);
	}
	
	private static final Short[] shortToshort(short[] shortArray)
	{
		Short[] a = new Short[shortArray.length];
		for(int b = 0; b < a.length; b++) a[b] = Short.valueOf(shortArray[b]);
		return a;
	}
}