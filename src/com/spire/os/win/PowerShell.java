package com.spire.os.win;

import java.util.HashMap;
import java.io.IOException;
import com.spire.exc.Program;
import com.spire.util.Constants;
import com.spire.ex.InternalException;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public final class PowerShell
{
	private static Program powershellHost;
	
	public static final String runCommand(String PowerShellCommand) throws IOException, NullException, StringException, InternalException
	{
		if(!Constants.isWindows())
			throw new InternalException("PowerShell is only avalible on Windows!");
		if(PowerShellCommand == null) throw new NullException("PowerShellCommand");
		if(PowerShellCommand.isEmpty()) throw new StringException("PowerShellCommand");
		if(powershellHost == null)
		{
			powershellHost = new Program("powershell");
			powershellHost.addParamaters("-Command");
			powershellHost.setProgramWait(true);
		}
		powershellHost.addParamater("\"& { " + PowerShellCommand + " }\"");
		powershellHost.startProgram();
		powershellHost.removeParamater(1);
		StringBuffer a = new StringBuffer();
		byte[] b = new byte[1024];
		for(int c = 0; (c = powershellHost.getStreamInput().read(b)) > -1; ) a.append(new String(b, 0, c));
		return a.toString();
	}
	
	public static final HashMap<String, String> runObjectCommand(String PowerShellCommand) throws IOException, NullException, StringException, InternalException
	{
		return getObjectData(runCommand(PowerShellCommand));
	}
	
	protected static final HashMap<String, String> getObjectData(String PowerShellResults)
	{
		String[] d = PowerShellResults.replace(String.valueOf((char)10), "").trim().split(String.valueOf((char)13));
		if(d != null && d.length >= 1 && !d[1].contains("At line:"))
		{
			HashMap<String, String> e = new HashMap<String, String>();
			for(int f = 0, g = 0; f < d.length; f++)
			{
				g = d[f].indexOf(':');
				e.put(d[f].substring(0, g - 1).trim(), d[f].substring(g + 1).trim());
			}
			return e;
		}
		return null;
	}
}