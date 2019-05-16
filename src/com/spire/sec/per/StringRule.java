package com.spire.sec.per;

import com.spire.ex.NullException;

public final class StringRule extends ArrayRule<String>
{
	public static final byte ITEM_CLASS_ID = 32;
	
	public StringRule()
	{
		super(ITEM_CLASS_ID, false);
	}
	public StringRule(String... StringList)
	{
		this(false, StringList);
	}
	public StringRule(boolean ListOnly, String... StringList)
	{
		super(ITEM_CLASS_ID, ListOnly);
		if(StringList != null) setPermissionRules(StringList);
	}
	
	public static final StringRule createEmptyList()
	{
		return new StringRule();
	}
	public static final StringRule createOnlyList(String... StringList) throws NullException
	{
		return new StringRule(true, StringList);
	}
	public static final StringRule createExcludeList(String... StringList) throws NullException
	{
		return new StringRule(false, StringList);
	}
}