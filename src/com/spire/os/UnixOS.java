package com.spire.os;

import java.io.IOException;
import com.spire.exc.Program;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.io.OperatingSystem;

public class UnixOS extends OperatingSystem
{
	public UnixOS()
	{
		super("Unix", ".", '/');
	}
	
	public final String phrasePath(String FilePath) throws NullException, StringException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		return FilePath.replace('\\', systemSeperator);
	}
	
	protected boolean killProcess(int ProcessPID, boolean Wait) throws IOException
	{
		if(systemPIDKiller == null)
			systemPIDKiller = new Program("kill ");
		else if(systemPIDKiller.getParamatersSize() >= 2)
			systemPIDKiller.removeParamater(2);
		systemPIDKiller.addParamaters("/pid", String.valueOf(ProcessPID));
		systemPIDKiller.setProgramWait(Wait);
		systemPIDKiller.startProgram();
		return systemPIDKiller.getExitCode() == 0;
	}
	protected boolean killProcess(String ProcessName, boolean Wait) throws IOException, NullException, StringException
	{
		if(systemPIDKiller == null)
			systemPIDKiller = new Program("kill ");
		else if(systemPIDKiller.getParamatersSize() >= 2)
			systemPIDKiller.removeParamater(2);
		systemPIDKiller.addParamaters("/im", ProcessName);
		systemPIDKiller.setProgramWait(Wait);
		systemPIDKiller.startProgram();
		return systemPIDKiller.getExitCode() == 0;
	}
}