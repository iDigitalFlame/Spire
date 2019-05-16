package com.spire.sec.per;

import com.spire.ex.NullException;

public final class ByteRule extends ArrayRule<Byte>
{
	public static final byte ITEM_CLASS_ID = 33;
	
	public ByteRule()
	{
		super(ITEM_CLASS_ID, false);
	}
	public ByteRule(byte... ByteArray)
	{
		this(false, ByteArray);
	}
	public ByteRule(boolean ListOnly, byte... ByteArray)
	{
		super(ITEM_CLASS_ID, ListOnly);
		if(ByteArray != null) setPermissionRules(ByteArray);
	}
	
	public final void setPermissionRules(byte... PermissionArguments) throws NullException
	{
		if(PermissionArguments == null) throw new NullException("PermissionArguments");
		super.setPermissionRules(byteToByte(PermissionArguments));
	}
	
	public static final ByteRule createEmptyList()
	{
		return new ByteRule();
	}
	public static final ByteRule createOnlyList(byte... ByteArray) throws NullException
	{
		return new ByteRule(true, ByteArray);
	}
	public static final ByteRule createExcludeList(byte... ByteArray) throws NullException
	{
		return new ByteRule(false, ByteArray);
	}
	
	private static final Byte[] byteToByte(byte[] ByteArray)
	{
		Byte[] a = new Byte[ByteArray.length];
		for(int b = 0; b < a.length; b++) a[b] = Byte.valueOf(ByteArray[b]);
		return a;
	}
}