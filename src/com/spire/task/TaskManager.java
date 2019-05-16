package com.spire.task;

import java.util.ArrayList;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.InternalException;
import com.spire.ex.PermissionException;

public final class TaskManager
{
	private static TaskManager managerInstance;
	
	private final TaskManagerThread managerThread;
	private final ArrayList<TaskInfo> managerTasks;
	
	private boolean managerRunning;
	
	public final boolean equals(Object CompareObject)
	{
		return false;
	}
	
	public final int hashCode()
	{
		return -1;
	}
	
	public final String toString()
	{
		return "Spire TaskManager";
	}

	public static final void stopAllTasks() throws PermissionException
	{
		Security.check("task.stopall", Thread.currentThread().getName());
		if(managerInstance != null)
			for(int a = 0; a < managerInstance.managerTasks.size(); a++)
				managerInstance.managerTasks.get(a).stopTask();
	}
	public static final void removeAllTasks() throws PermissionException
	{
		Security.check("task.stopall", Thread.currentThread().getName());
		Security.check("task.removeall", Thread.currentThread().getName());
		if(managerInstance != null)
		{
			for(int a = 0; a < managerInstance.managerTasks.size(); a++)
			{
				managerInstance.managerTasks.get(a).stopTask();
				try
				{
					managerInstance.managerTasks.get(a).removeTask();
				}
				catch (InternalException Exception) { }
			}
			managerInstance.managerTasks.clear();
		}
	}
	
	public static final int getTaskCount()
	{
		return managerInstance != null ? managerInstance.managerTasks.size() : 0;
	}
	
	public static final TaskInfo addTask(Task RunTask, TaskRule RunRule) throws PermissionException, NullException
	{
		if(RunTask == null) throw new NullException("RunTask");
		if(RunRule == null) throw new NullException("RunRule");
		Security.check("task.add", RunTask.getClass());
		if(managerInstance == null) managerInstance = new TaskManager();
		boolean a = managerInstance.managerTasks.isEmpty();
		TaskInfo b = new TaskInfo(RunTask, RunRule);
		synchronized(managerInstance.managerTasks)
		{
			managerInstance.managerTasks.add(b);
		}
		managerInstance.managerThread.interrupt();
		Reporter.info(Reporter.REPORTER_TASK, "Task " + RunTask.getClass().getName() + " was added with rule " + RunRule.getClass().getName());
		return b;
	}
	public static final TaskInfo addTimedTask(Task RunTask, int TaskInterval) throws PermissionException, NullException, NumberException
	{
		return addTask(RunTask, new TaskTimeRule(-1, TaskInterval));
	}
	public static final TaskInfo addTimedTaskSec(Task RunTask, int TaskInterval) throws PermissionException, NullException, NumberException
	{
		return addTask(RunTask, new TaskTimeRule(-1, TaskInterval * 1000));
	}
	public static final TaskInfo addTimedTaskMin(Task RunTask, int TaskInterval) throws PermissionException, NullException, NumberException
	{
		return addTask(RunTask, new TaskTimeRule(-1, TaskInterval * 60000));
	}
	public static final TaskInfo addTimedTask(Task RunTask, int TaskCount, int TaskInterval) throws PermissionException, NullException, NumberException
	{
		return addTask(RunTask, new TaskTimeRule(TaskCount, TaskInterval));
	}
	public static final TaskInfo addTimedTaskSec(Task RunTask, int TaskCount, int TaskInterval) throws PermissionException, NullException, NumberException
	{
		return addTask(RunTask, new TaskTimeRule(TaskCount, TaskInterval * 1000));
	}
	public static final TaskInfo addTimedTaskMin(Task RunTask, int TaskCount, int TaskInterval) throws PermissionException, NullException, NumberException
	{
		return addTask(RunTask, new TaskTimeRule(TaskCount, TaskInterval * 60000));
	}
	
	protected final TaskManager clone()
	{
		return managerInstance;
	}
	
	private TaskManager()
	{
		managerRunning = true;
		managerTasks = new ArrayList<TaskInfo>();
		managerThread = new TaskManagerThread(this);
		Reporter.info(Reporter.REPORTER_TASK, "TaskManager was started");
	}
	
	private static final class TaskManagerThread extends Thread
	{
		private final TaskManager managerHost;
		
		public final void run()
		{
			while(managerHost.managerRunning)
			{
				if(managerHost.managerTasks.isEmpty()) try
				{
					Reporter.debug(Reporter.REPORTER_TASK, "No tasks found! Sleeping thread");
					Thread.sleep(0x7FFFFFFFL);
				}
				catch (InterruptedException Exception) { }
				for(int a = 0; a < managerHost.managerTasks.size(); a++)
				{
					if(managerHost.managerTasks.get(a).canRunTask())
						managerHost.managerTasks.get(a).runTask();
					if(managerHost.managerTasks.get(a).canRemoveTask())
					{
						synchronized(managerHost.managerTasks)
						{
							managerHost.managerTasks.remove(a);
							a--;
						}
					}
				}
				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException Exception) { }
			}
		}
		
		private TaskManagerThread(TaskManager HostInstance)
		{
			managerHost = HostInstance;
			setPriority(2);
			setName("TaskManager Watcher");
			start();
		}
	}
}