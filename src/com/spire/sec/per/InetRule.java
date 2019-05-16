package com.spire.sec.per;

import java.net.InetAddress;
import com.spire.ex.NullException;

public final class InetRule extends ArrayRule<InetAddress>
{
	public static final byte ITEM_CLASS_ID = 36;
	
	public InetRule()
	{
		super(ITEM_CLASS_ID, false);
	}
	public InetRule(InetAddress... InetAddressList)
	{
		this(false, InetAddressList);
	}
	public InetRule(boolean ListOnly, InetAddress... InetAddressList)
	{
		super(ITEM_CLASS_ID, ListOnly);
		if(InetAddressList != null) setPermissionRules(InetAddressList);
	}
	
	public static final InetRule createEmptyList()
	{
		return new InetRule();
	}
	public static final InetRule createOnlyList(InetAddress... InetAddressList) throws NullException
	{
		return new InetRule(true, InetAddressList);
	}
	public static final InetRule createExcludeList(InetAddress... InetAddressList) throws NullException
	{
		return new InetRule(false, InetAddressList);
	}
}