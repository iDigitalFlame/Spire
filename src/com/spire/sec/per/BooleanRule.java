package com.spire.sec.per;

import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.sec.SecurityItem;

public final class BooleanRule extends SecurityItem<Boolean>
{
	public static final byte ITEM_CLASS_ID = 30;
	
	private boolean ruleData;
	
	public BooleanRule()
	{
		this(true);
	}
	public BooleanRule(boolean RuleData)
	{
		super(ITEM_CLASS_ID);
		ruleData = RuleData;
	}
	
	public final void clearPermissionRules()
	{
		ruleData = false;
	}
	public final void setPermissionRules(Boolean... PermissionArguments)
	{
		ruleData = PermissionArguments != null && PermissionArguments.length > 0 && PermissionArguments[0].booleanValue();
	}
	
	protected final void readItemFailure()
	{
		ruleData = false;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		ruleData = itemEncoder.readBoolean(InStream);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeBoolean(OutStream, ruleData);
	}
	
	protected final boolean isValid(Object PermissionArgument)
	{
		return ruleData;
	}
}