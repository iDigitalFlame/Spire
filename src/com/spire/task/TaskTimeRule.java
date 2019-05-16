package com.spire.task;

import com.spire.ex.NumberException;

public final class TaskTimeRule implements TaskRule
{
	private final int ruleMaxCount;
	private final int ruleTimeInterval;
	
	private int ruleCount;
	private long ruleTimer;
	
	public TaskTimeRule(int RuleMilliseconds) throws NumberException
	{
		this(-1, RuleMilliseconds);
	}
	public TaskTimeRule(int RuleCount, int RuleMilliseconds) throws NumberException
	{
		if(RuleCount != -1 && RuleCount <= 0) throw new NumberException("RuleCount", RuleCount, true);
		if(RuleMilliseconds <= 250) throw new NumberException("RuleMilliseconds", RuleMilliseconds, 250, Integer.MAX_VALUE);
		ruleMaxCount = RuleCount;
		ruleTimeInterval = RuleMilliseconds;
		ruleTimer = System.currentTimeMillis() + RuleMilliseconds;
	}

	public final boolean canRun()
	{
		if((ruleMaxCount == -1 || ruleCount < ruleMaxCount) && ruleTimer < System.currentTimeMillis())
		{
			ruleCount++;
			ruleTimer = System.currentTimeMillis() + ruleTimeInterval;
			return true;
		}
		return false;
	}
	public final boolean canRemove()
	{
		return ruleMaxCount != -1 && ruleCount >= ruleMaxCount;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof TaskTimeRule && ((TaskTimeRule)CompareObject).ruleTimeInterval == ruleTimeInterval && ((TaskTimeRule)CompareObject).ruleMaxCount == ruleMaxCount &&
				((TaskTimeRule)CompareObject).ruleTimer == ruleTimer && ((TaskTimeRule)CompareObject).ruleCount == ruleCount;
	}
	
	public final int hashCode()
	{
		return ruleMaxCount + ruleTimeInterval + ruleCount + (int)(ruleTimer / 100);
	}
	
	public final String toString()
	{
		return "TaskTimeRule(SR) S:" + ruleTimeInterval + (ruleMaxCount == -1 ? "" : "C:" + ruleCount + "/" + ruleMaxCount);
	}

	protected final TaskTimeRule clone()
	{
		return new TaskTimeRule(ruleMaxCount, ruleTimeInterval);
	}
}