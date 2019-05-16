package com.spire.exc;

import com.spire.log.Reporter;

class ProgramThread extends Thread
{
	private final long threadTimeout;
	private final Program threadParent;
	private final Process threadProcess;
	
	public final void run()
	{
		short a = 0;
		try
		{
			a = (short)threadProcess.waitFor();
		}
		catch (InterruptedException Exception)
		{
			try
			{
				a = (short)threadProcess.exitValue();
			}
			catch (IllegalThreadStateException Exception1) { }
			threadProcess.destroy();
		}
		threadParent.threadFinished(a);
		ProgramController.removeThis(this);
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof ProgramThread && CompareObject.hashCode() == hashCode();
	}
	
	public final int hashCode()
	{
		return threadParent.hashCode();
	}
	
	public final String toString()
	{
		return "ProgramThread(T) " + getName();
	}
	
	protected ProgramThread(Program Program, Process ProgramProcess)
	{
		threadParent = Program;
		threadProcess = ProgramProcess;
		threadTimeout = System.currentTimeMillis() + (Program.getProgramTimeout() * 1000);
		setPriority(1);
		setName("PT:" + Program.toString());
		if(Program.getProgramTimeout() > 0) ProgramController.addThis(this);
		start();
	}
	
	protected final void triggerCheck()
	{
		if(System.currentTimeMillis() > threadTimeout)
		{
			Reporter.info(Reporter.REPORTER_IO, "ProgramThread \"" + getName() + "\" was stopped because it exceeded its timeout");
			interrupt();
		}
	}
}
