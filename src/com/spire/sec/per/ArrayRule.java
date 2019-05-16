package com.spire.sec.per;

import com.spire.io.Item;
import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.ex.IDException;
import com.spire.util.SyncedList;
import com.spire.ex.NullException;
import com.spire.sec.SecurityItem;

public class ArrayRule<T> extends SecurityItem<T>
{
	protected final SyncedList<T> arrayOptions;
	
	protected boolean validOnlyList;
	
	public final void clearPermissionRules()
	{
		arrayOptions.clear();
		validOnlyList = false;
	}
	public final void setPermissionListOnly(boolean ListOnly)
	{
		validOnlyList = ListOnly;
	}
	@SuppressWarnings("unchecked")
	public final void setPermissionRules(T... PermissionArguments) throws NullException
	{
		if(PermissionArguments == null) throw new NullException("PermissionArguments");
		arrayOptions.clear();
		for(int a = 0; a < PermissionArguments.length; a++) if(PermissionArguments[a] != null)
			arrayOptions.add(PermissionArguments[a]);
	}
	
	protected ArrayRule(int ItemID) throws IDException
	{
		this(ItemID, false);
	}
	protected ArrayRule(int ItemID, boolean ValidListOnly) throws IDException
	{
		super(ItemID);
		validOnlyList = ValidListOnly;
		arrayOptions = new SyncedList<T>();
	}
	
	protected final void readItemFailure()
	{
		arrayOptions.clear();
		validOnlyList = false;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		validOnlyList = itemEncoder.readBoolean(InStream);
		Item.readFinalItem(arrayOptions, InStream);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeBoolean(OutStream, validOnlyList);
		arrayOptions.writeStream(OutStream);
	}
	
	protected final boolean isValid(Object PermissionArgument)
	{
		if(PermissionArgument == null) return !validOnlyList;
		if(arrayOptions.contains(PermissionArgument)) return validOnlyList;
		return !validOnlyList;
	}
}