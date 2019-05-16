package com.spire.sec.per;

import com.spire.ex.NullException;

public final class ClassRule extends ArrayRule<Class<?>>
{
	public static final byte ITEM_CLASS_ID = 31;
	
	public ClassRule()
	{
		super(ITEM_CLASS_ID, false);
	}
	public ClassRule(Class<?>... ClassList)
	{
		this(false, ClassList);
	}
	public ClassRule(boolean ListOnly, Class<?>... ClassList)
	{
		super(ITEM_CLASS_ID, ListOnly);
		if(ClassList != null) setPermissionRules(ClassList);
	}
	
	public static final ClassRule createEmptyList()
	{
		return new ClassRule();
	}
	public static final ClassRule createOnlyList(Class<?>... ClassList) throws NullException
	{
		return new ClassRule(true, ClassList);
	}
	public static final ClassRule createExcludeList(Class<?>... ClassList) throws NullException
	{
		return new ClassRule(false, ClassList);
	}
}