package com.spire.sec;

import com.spire.io.Item;
import com.spire.ex.IDException;

public abstract class SecurityItem<T> extends Item
{
	public abstract void clearPermissionRules();
	@SuppressWarnings("unchecked")
	public abstract void setPermissionRules(T... PermissionArguments);
	
	public final int hashCode()
	{
		return getClass().getSimpleName().hashCode() + getClass().hashCode();
	}
	
	public final String toString()
	{
		return "SecurityItem " + getClass().getSimpleName() + "(" + (itemID & 0xFF) + ")";
	}

	protected SecurityItem(int ItemID) throws IDException
	{
		super(ItemID);
	}
	
	protected abstract boolean isValid(Object PermissionArgument);
	
	protected final SecurityItem<T> getCopy()
	{
		return null;
	}
}