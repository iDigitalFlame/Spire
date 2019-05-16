package com.spire.task;

import java.util.Arrays;

import com.spire.ex.NumberException;
import com.spire.util.BoolTag;
import com.spire.util.Constants;
import com.spire.util.Stamp;

public final class TaskCalRule implements TaskRule
{
	private final int ruleMaxCount = 0;
	private byte ruleSmallIndex;
	private final byte[] ruleTrigger;
	private final BoolTag ruleFilter;
	
	private int ruleCount;
	private Stamp ruleCompare;
	private boolean ruleWait;
	private byte rulelastread;
	
	public TaskCalRule(BoolTag a, Stamp c)
	{
		ruleFilter = a;
		ruleTrigger = Arrays.copyOf(c.getStamp(), c.getStamp().length);
		ruleCompare = Constants.getNow().clone();
		
		/*for(byte b = 5; b >=0; b++)
			if(a.getTag(b))
			{
				ruleSmallIndex = b;
				break;
			}
		if(ruleSmallIndex > 3 && a.getTag(6)) ruleSmallIndex = 6;*/
	}

	public final boolean canRun()
	{
		ruleCompare.updateStamp();
		
		boolean z = true, y = false;
		for (byte x = 0; x < ruleTrigger.length; x++)
			if(ruleFilter.getTag(x))
			{
				z &= ruleTrigger[x] == ruleCompare.getStamp()[x];
			}
		if(z)
		{
			if(ruleWait)
			{
				return false;
			}
			ruleWait = true;
			return true;
		}
		ruleWait = false;
		return false;
	}
	public final boolean canRemove()
	{
		return false; //ruleMaxCount != -1 && ruleCount >= ruleMaxCount;
	}
	public final boolean equals(Object CompareObject)
	{
		return false;
	}
	
	public final int hashCode()
	{
		return 3423892;
	}
	
	public final String toString()
	{
		return "TaskTimeRule(SR) S:" + 0 + (ruleMaxCount == -1 ? "" : "C:" + ruleCount + "/" + ruleMaxCount);
	}

	protected final TaskCalRule clone()
	{
		return null;//new TaskCalRule();
	}
}