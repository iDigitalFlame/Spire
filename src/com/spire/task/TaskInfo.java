package com.spire.task;

import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.InternalException;
import com.spire.ex.PermissionException;

public final class TaskInfo
{
	protected final Task infoTask;
	
	protected TaskRule infoRule;
	
	private boolean infoRemove;
	private boolean infoRunning;
	private TaskThread infoThread;
	
	private volatile boolean infoExecuting;
	
	public final void stopTask() throws PermissionException, InternalException
	{
		Security.check("task.stop", infoTask.getClass());
		if(!infoRunning) throw new InternalException("The task is not currently running!");
		infoRunning = false;
		if(infoThread != null && infoThread.isAlive())
		{
			infoThread.interrupt();
			infoThread = null;
		}
		infoExecuting = false;
	}
	public final void startTask() throws PermissionException, InternalException
	{
		Security.check("task.start", infoTask.getClass());
		if(infoRunning) throw new InternalException("The task is already started!");
		infoRunning = true;
	}
	public final void removeTask() throws PermissionException, InternalException
	{
		Security.check("task.remove", infoTask.getClass());
		if(infoRemove) throw new InternalException("The task has already been removed!");
		if(infoRunning) stopTask();
		infoRemove = true;
	}
	public final void changeRule(TaskRule NewRule) throws PermissionException, NullException
	{
		Security.check("task.crule", infoTask.getClass());
		if(NewRule == null) throw new NullException("NewRule");
		infoRule = NewRule;
	}
	
	public final boolean isRunning()
	{
		return infoRunning;
	}
	public final boolean isExecuting()
	{
		return infoExecuting;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof TaskInfo && CompareObject.hashCode() == hashCode();
	}
	
	public final int hashCode()
	{
		return infoTask.hashCode() + infoRule.hashCode();
	}
	
	public final String toString()
	{
		return "TaskInfo(S) [" + infoTask.getClass().getSimpleName() + "] R: [" + infoRule.getClass().getSimpleName() + "] " + (infoRunning ? "R" : "S") + (infoExecuting ? "E" : "I");
	}
	
	protected TaskInfo(Task TaskInstance, TaskRule TaskRule)
	{
		infoRule = TaskRule;
		infoTask = TaskInstance;
		infoRunning = true;
	}
	
	protected final void runTask()
	{
		Reporter.debug(Reporter.REPORTER_TASK, "Running Task \"" + infoTask.getClass().getName() + "\"");
		if(infoTask.runAsThread())
		{
			infoThread = new TaskThread(this);
			infoThread.start();
		}
		else
		{
			infoExecuting = true;
			infoTask.runTask();
			infoExecuting = false;
		}
	}

	protected final boolean canRunTask()
	{
		return !infoRemove && infoRunning && infoRule.canRun();
	}
	protected final boolean canRemoveTask()
	{
		return infoRemove || infoRule.canRemove();
	}
	
	protected final TaskInfo clone() throws CloneException
	{
		throw new CloneException("Cannot clone a Task Reference!");
	}
	
	private static final class TaskThread extends Thread
	{
		private final TaskInfo threadInfo;
		
		public final void run()
		{
			threadInfo.infoExecuting = true;
			threadInfo.infoTask.runTask();
			threadInfo.infoExecuting = false;
		}
		
		private TaskThread(TaskInfo TaskInfo)
		{
			threadInfo = TaskInfo;
			setPriority(1);
			setName("TaskThread C: " + TaskInfo.infoTask.getClass().getSimpleName());
		}
	}
}