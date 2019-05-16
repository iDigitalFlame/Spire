package com.spire.os;

import java.io.IOException;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.exc.Program;
import com.spire.io.OperatingSystem;

public final class WindowsOS extends OperatingSystem
{	
	public WindowsOS()
	{
		super("Windows", "cmd", '\\');
	}

	public final boolean supportsPowershell()
	{
		return systemVersion >= 6;
	}
	
	public final String phrasePath(String FilePath) throws NullException, StringException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		return FilePath.replace('/', systemSeperator);
	}
	
	protected boolean killProcess(int ProcessPID, boolean Wait) throws IOException
	{
		if(systemPIDKiller == null)
		{
			systemPIDKiller = new Program("taskkill");
			systemPIDKiller.addParamaters("/f");
		}
		else if(systemPIDKiller.getParamatersSize() >= 3)
		{
			systemPIDKiller.removeParamater(3);
			systemPIDKiller.removeParamater(2);
		}
		systemPIDKiller.addParamaters("/pid", String.valueOf(ProcessPID));
		systemPIDKiller.setProgramWait(Wait);
		systemPIDKiller.startProgram();
		return systemPIDKiller.getExitCode() == 0;
	}
	protected boolean killProcess(String ProcessName, boolean Wait) throws IOException, NullException, StringException
	{
		if(systemPIDKiller == null)
		{
			systemPIDKiller = new Program("taskkill");
			systemPIDKiller.addParamaters("/f");
		}
		else if(systemPIDKiller.getParamatersSize() >= 3)
		{
			systemPIDKiller.removeParamater(3);
			systemPIDKiller.removeParamater(2);
		}
		systemPIDKiller.addParamaters("/im", ProcessName);
		systemPIDKiller.setProgramWait(Wait);
		systemPIDKiller.startProgram();
		return systemPIDKiller.getExitCode() == 0;
	}
}