package com.spire.exc;

import java.util.ArrayList;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.ex.PermissionException;

public class ProgramController extends Thread
{
	private static ProgramController CONTROLLER;
	
	private final ArrayList<ProgramThread> controllerList;
	
	private boolean controllerActive;
	
	public final void run()
	{
		while(controllerActive)
		{
			if(controllerList.isEmpty()) try
			{
				Reporter.debug(Reporter.REPORTER_IO, "No active programs detected! Entering sleep");
				Thread.sleep(0x7FFFFFFFL);
			}
			catch (InterruptedException Exception) { }
			else
			{
				Reporter.debug(Reporter.REPORTER_IO, "Polling active programs for a timeout check");
				for(int a = 0; a < controllerList.size(); a++) controllerList.get(a).triggerCheck();
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException Exception) { }
			}
		}
	}
	public final void interrupt()
	{
		controllerActive = false;
		super.interrupt();
	}
	
	public final boolean equals(Object CompareObject)
	{
		return false;
	}
	
	public final int hashCode()
	{
		return controllerList.hashCode();
	}
	
	public final String toString()
	{
		return "ProgramController(T) W:" + controllerList.size();
	}
	
	public final ProgramController clone()
	{
		return CONTROLLER;
	}
	
	public static final void stopAllPrograms() throws PermissionException
	{
		if(CONTROLLER == null) return;
		Security.check("io.program.stopall", Thread.currentThread().getName());
		if(!CONTROLLER.controllerList.isEmpty())
		{
			for(int a = 0; a < CONTROLLER.controllerList.size(); a++) CONTROLLER.controllerList.get(a).interrupt();
			CONTROLLER.controllerList.clear();
			Reporter.info(Reporter.REPORTER_IO, "All programs were cleared from the ProgramController and stopped!");
		}
	}
	
	public static final int getCurrentRunning()
	{
		return CONTROLLER != null ? CONTROLLER.controllerList.size() : 0;
	}
	
	protected static final void addThis(ProgramThread ProgramThread)
	{
		if(CONTROLLER == null) CONTROLLER = new ProgramController();
		else if(CONTROLLER.controllerList.isEmpty()) CONTROLLER.wakeController();
		CONTROLLER.controllerList.add(ProgramThread);
		Reporter.info(Reporter.REPORTER_IO, "A new ProgramThread \"" + ProgramThread.getName() + "\" has been added to the ControllerList");
	}
	protected static final void removeThis(ProgramThread ProgramThread)
	{
		if(CONTROLLER == null || CONTROLLER.controllerList.isEmpty()) return;
		CONTROLLER.controllerList.remove(ProgramThread);
		Reporter.info(Reporter.REPORTER_IO, "Pogram Thread \"" + ProgramThread.getName() + "\" has been removed from the controller");
	}
	
	private ProgramController()
	{
		setPriority(2);
		setDaemon(true);
		controllerActive = true;
		controllerList = new ArrayList<ProgramThread>();
		start();
	}
	
	private final void wakeController()
	{
		Reporter.debug(Reporter.REPORTER_IO, "Waking sleeping controller");
		super.interrupt();
	}
}