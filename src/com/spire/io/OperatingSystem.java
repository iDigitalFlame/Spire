package com.spire.io;

import java.io.File;
import java.io.IOException;
import com.spire.exc.Program;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public abstract class OperatingSystem
{
	public final String systemName;
	public final char systemSeperator;
	
	protected final String systemCMD;
	protected final float systemVersion;
	
	protected File[] systemDrives;
	protected Program systemPIDKiller;
	
	public abstract String phrasePath(String FilePath) throws NullException, StringException;
	
	protected OperatingSystem(String Name, String ComSpec, char Seperator) throws NullException
	{
		if(Name == null) throw new NullException("Name");
		if(ComSpec == null) throw new NullException("ComSpec");
		systemName = Name;
		systemCMD = ComSpec;
		systemSeperator = Seperator;
		systemVersion = getSystemVersion();
	}
	
	protected abstract boolean killProcess(int ProcessPID, boolean Wait)throws IOException;
	protected abstract boolean killProcess(String ProcessName, boolean Wait) throws IOException, NullException, StringException;
	
	private static final float getSystemVersion()
	{
		try
		{
			return Float.parseFloat(System.getProperty("os.version"));
		}
		catch (NumberFormatException Exception) { }
		return -1F;
	}
}