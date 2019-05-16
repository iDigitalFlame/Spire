package com.spire.log;

import java.io.PrintStream;
import java.util.ArrayList;
import java.io.OutputStream;
import com.spire.sec.Security;
import com.spire.ex.NullException;
import com.spire.ex.BasicException;
import com.spire.ex.PermissionException;
import java.util.concurrent.ConcurrentHashMap;

public final class Reporter
{
	public static final byte LEVEL_FINE = 0;
	public static final byte LEVEL_INFO = 1;
	public static final byte LEVEL_NORMAL = 2;
	public static final byte LEVEL_ERRORONLY = 3;
	public static final byte LEVEL_CRITICALONLY = 4;
	public static final byte LEVEL_SEVEREONLY = 5;
	public static final byte LEVEL_FAILUREONLY = 6;
	
	public static final String REPORTER_IO = "IO";
	public static final String REPORTER_GUI = "GUI";
	public static final String REPORTER_WEB = "Web";
	public static final String REPORTER_TASK = "TASK";
	public static final String REPORTER_EMAIL = "Email";
	public static final String REPORTER_GLOBAL = "Global";
	public static final String REPORTER_NETWORK = "Network";
	public static final String REPORTER_SECURITY = "Security";
	
	private static final String R_ERROR = "ERROR";
	private static final String R_SEVERE = "SEVERE";
	private static final String R_FAILURE = "FAILURE";
	private static final String R_WARNING = "WARNING";
	private static final String R_CRITICAL = "CRITICAL";	
	
	private static final ConcurrentHashMap<String, Reporter> reporterSessions = new ConcurrentHashMap<String, Reporter>();
	
	private static byte reporterDefaultLevel = LEVEL_NORMAL;
	private static PrintStream reporterUncaught = System.out;
	
	private final String reporterName;
	private final ArrayList<Report> reporterLogs;
	private final ArrayList<ReporterHandle> reporterHandles;
	
	private byte reporterLevel;
	
	public final void setLogLevel(int LogLevel)
	{
		reporterLevel = (byte)(LogLevel >= 0 && LogLevel < 10 ? LogLevel : reporterDefaultLevel);
	}
	public final void clearReporterHandles() throws PermissionException
	{
		Security.check("log.handles", reporterName);
		Security.check("log.clear");
		synchronized(reporterHandles)
		{
			reporterHandles.clear();
		}
		logInfo("The handles for this Reporter were cleared!");
		if(!reporterName.equals(REPORTER_GLOBAL))
			getGlobal().logInfo("Reporter \"" + reporterName + "\" was cleared of all handles!");
	}
	public final void addReporterHandle(ReporterHandle Handle) throws NullException, PermissionException
	{
		addReporterHandle(Handle, false);
	}
	public final void removeReporterHandle(ReporterHandle Handle) throws NullException, PermissionException
	{
		if(Handle == null) throw new NullException("Handle");
		Security.check("log.handles", reporterName);
		Security.check("log.remove", Handle.getClass());
		synchronized(reporterHandles)
		{
			reporterHandles.remove(Handle);
		}
		logInfo("A new handle " + Handle.getClass().getName() + " \"" + Handle.toString() + "\" was removed!");
		if(!reporterName.equals(REPORTER_GLOBAL))
			getGlobal().logInfo("Reporter \"" + reporterName + "\" removed the handle \"" + Handle.getClass().getName() + "\"!");
	}
	public final void addReporterHandle(ReporterHandle Handle, boolean PushExisting) throws NullException, PermissionException
	{
		if(Handle == null) throw new NullException("Handle");
		Security.check("log.handles", reporterName);
		Security.check("log.add", Handle.getClass());
		synchronized(reporterHandles)
		{
			reporterHandles.add(Handle);
		}
		if(PushExisting)
			for(int a = 0; a < reporterLogs.size(); a++)
				if(Handle.canProcessReport(reporterLogs.get(a).reportLevel))
					Handle.processReport(reporterLogs.get(a));
		logInfo("A new handle " + Handle.getClass().getName() + " \"" + Handle.toString() + "\" was added!");
		if(!reporterName.equals(REPORTER_GLOBAL))
			getGlobal().logInfo("Reporter \"" + reporterName + "\" added a new handle \"" + Handle.getClass().getName() + "\"!");
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Reporter && ((Reporter)CompareObject).reporterName.equals(reporterName);
	}
	
	public final int hashCode()
	{
		return reporterName.hashCode() + (reporterLevel * 2);
	}
	
	public final String toString()
	{
		return "Reporter(" + reporterName + ") @" + reporterLevel;
	}
	
	public final Report logDebug(Throwable DebugException)
	{
		return logEvent(LEVEL_FINE, null, DebugException);
	}
	public final Report logDebug(final String DebugMessage)
	{
		return logEvent(LEVEL_FINE, DebugMessage, null);
	}
	public final Report logDebug(final String DebugMessage, Throwable DebugException)
	{
		return logEvent(LEVEL_FINE, DebugMessage, DebugException);
	}
	public final Report logInfo(final String InformationMessage)
	{
		return logEvent(LEVEL_INFO, InformationMessage, null);
	}
	public final Report logWarning(Throwable WarningException)
	{
		return logEvent(LEVEL_NORMAL, R_WARNING, WarningException);
	}
	public final Report logWarning(final String WarningMessage)
	{
		return logEvent(LEVEL_NORMAL, R_WARNING + ": " + WarningMessage, null);
	}
	public final Report logWarning(final String WarningMessage, Throwable WarningException)
	{
		return logEvent(LEVEL_NORMAL, R_WARNING + ": " + WarningMessage, WarningException);
	}
	public final Report logError(Throwable ErrorException)
	{
		return logEvent(LEVEL_ERRORONLY, R_ERROR, ErrorException);
	}
	public final Report logError(final String ErrorMessage)
	{
		return logEvent(LEVEL_ERRORONLY, R_ERROR + ": " + ErrorMessage, null);
	}
	public final Report logError(final String ErrorMessage, Throwable ErrorException)
	{
		return logEvent(LEVEL_ERRORONLY, R_ERROR + ": " + ErrorMessage, ErrorException);
	}
	public final Report logCritical(Throwable CriticalException)
	{
		return logEvent(LEVEL_CRITICALONLY, R_CRITICAL, CriticalException);
	}
	public final Report logCritical(final String CriticalMessage)
	{
		return logEvent(LEVEL_CRITICALONLY, R_CRITICAL + ": " + CriticalMessage, null);
	}
	public final Report logCritical(final String CriticalMessage, Throwable CriticalException)
	{
		return logEvent(LEVEL_CRITICALONLY, R_CRITICAL + ": " + CriticalMessage, CriticalException);
	}
	public final Report logSevere(Throwable SevereException)
	{
		return logEvent(LEVEL_SEVEREONLY, R_SEVERE + ": " + null, SevereException);
	}
	public final Report logSevere(final String SevereMessage)
	{
		return logEvent(LEVEL_SEVEREONLY, R_SEVERE + ": " + SevereMessage, null);
	}
	public final Report logSevere(final String SevereMessage, Throwable SevereException)
	{
		return logEvent(LEVEL_SEVEREONLY, R_SEVERE + ": " + SevereMessage, SevereException);
	}
	public final Report logFailure(Throwable FailureException)
	{
		return logEvent(LEVEL_FAILUREONLY, R_FAILURE, FailureException);
	}
	public final Report logFailure(final String FailureMessage)
	{
		return logEvent(LEVEL_FAILUREONLY, R_FAILURE + ": " + FailureMessage, null);
	}
	public final Report logFailure(final String FailureMessage, Throwable FailureException)
	{
		return logEvent(LEVEL_FAILUREONLY, R_FAILURE + ": " + FailureMessage, FailureException);
	}
	public final Report logSpecial(byte SpecialLevel, String SpecialMessage, Throwable SpecialException)
	{
		return logEvent(SpecialLevel, SpecialMessage, SpecialException);
	}
	
	public final Reporter clone()
	{
		return this;
	}
	public final Reporter setLoggingLevel(int LogLevel)
	{
		setLogLevel(LogLevel);
		return this;
	}
	
	public static final void setDefaultLogLevel(int LogLevel)
	{
		reporterDefaultLevel = (byte)(LogLevel >= 0 && LogLevel < 10 ? LogLevel : LEVEL_NORMAL);
	}
	public static final void reportUncaught(String UncaughtMessage)
	{
		reporterUncaught.println("There was an unhandled message!");
		reporterUncaught.println(UncaughtMessage);
		reporterUncaught.flush();
	}
	public static final void setUncaughtStream(PrintStream ErrorStream)
	{
		reporterUncaught = ErrorStream != null ? ErrorStream : System.out;
	}
	public static final void setUncaughtStream(OutputStream ErrorStream)
	{
		setUncaughtStream(new PrintStream(ErrorStream));
	}
	public static final void reportUncaught(Throwable UncaughtException)
	{
		reporterUncaught.println("There was an unhandled exception!");
		UncaughtException.printStackTrace(reporterUncaught);
		reporterUncaught.flush();
	}
	public static final void reportUncaught(String UncaughtMessage, Throwable UncaughtException)
	{
		reporterUncaught.println("There was an unhandled exception!");
		reporterUncaught.println(UncaughtMessage);
		UncaughtException.printStackTrace(reporterUncaught);
		reporterUncaught.flush();
	}
	public static final void addReporterHandleToAll(ReporterHandle Handle, boolean PushExisting) throws NullException, PermissionException
	{
		if(Handle == null) throw new NullException("Handle");
		Reporter[] a = reporterSessions.values().toArray(new Reporter[reporterSessions.size()]);
		for(int b = 0; b < a.length; b++) a[b].addReporterHandle(Handle, PushExisting);
	}
	
	public static final Report debug(String ReporterName, Throwable DebugException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_FINE, null, DebugException);
	}
	public static final Report debug(String ReporterName, final String DebugMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_FINE, DebugMessage, null);
	}
	public static final Report debug(String ReporterName, final String DebugMessage, Throwable DebugException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_FINE, DebugMessage, DebugException);
	}
	public static final Report info(String ReporterName, final String InformationMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_INFO, InformationMessage, null);
	}
	public static final Report warning(String ReporterName, Throwable WarningException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_NORMAL, R_WARNING, WarningException);
	}
	public static final Report warning(String ReporterName, final String WarningMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_NORMAL, R_WARNING + ": " + WarningMessage, null);
	}
	public static final Report warning(String ReporterName, final String WarningMessage, Throwable WarningException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_NORMAL, R_WARNING + ": " + WarningMessage, WarningException);
	}
	public static final Report error(String ReporterName, Throwable ErrorException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_ERRORONLY, R_ERROR, ErrorException);
	}
	public static final Report error(String ReporterName, final String ErrorMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_ERRORONLY, R_ERROR + ": " + ErrorMessage, null);
	}
	public static final Report error(String ReporterName, final String ErrorMessage, Throwable ErrorException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_ERRORONLY, R_ERROR + ": " + ErrorMessage, ErrorException);
	}
	public static final Report critical(String ReporterName, Throwable CriticalException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_CRITICALONLY, R_CRITICAL, CriticalException);
	}
	public static final Report critical(String ReporterName, final String CriticalMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_CRITICALONLY, R_CRITICAL + ": " + CriticalMessage, null);
	}
	public static final Report critical(String ReporterName, final String CriticalMessage, Throwable CriticalException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_CRITICALONLY, R_CRITICAL + ": " + CriticalMessage, CriticalException);
	}
	public static final Report severe(String ReporterName, Throwable SevereException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_SEVEREONLY, R_SEVERE, SevereException);
	}
	public static final Report severe(String ReporterName, final String SevereMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_SEVEREONLY,  R_SEVERE + ": " + SevereMessage, null);
	}
	public static final Report severe(String ReporterName, final String SevereMessage, Throwable SevereException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_SEVEREONLY,  R_SEVERE + ": " + SevereMessage, SevereException);
	}
	public static final Report failure(String ReporterName, Throwable FailureException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_FAILUREONLY,  R_FAILURE, FailureException);
	}
	public static final Report failure(String ReporterName, final String FailureMessage)
	{
		return getReporter(ReporterName).logEvent(LEVEL_FAILUREONLY, R_FAILURE + ": " +FailureMessage, null);
	}
	public static final Report failure(String ReporterName, final String FailureMessage, Throwable FailureException)
	{
		return getReporter(ReporterName).logEvent(LEVEL_FAILUREONLY, R_FAILURE + ": " +FailureMessage, FailureException);
	}
	
	public static final Reporter getGlobal()
	{
		return getReporter(REPORTER_GLOBAL, -1);
	}
	public static final Reporter getReporter(String ReporterName)
	{
		return getReporter(ReporterName, -1);
	}
	public static final Reporter getReporter(String ReporterName, int LogLevel)
	{
		if(ReporterName == null || ReporterName.isEmpty()) return getReporter(REPORTER_GLOBAL, LogLevel);
		if(!ReporterName.equals(REPORTER_GLOBAL) && !ReporterName.equals(REPORTER_SECURITY) && !Security.valid("log.create", ReporterName)) return getReporter(REPORTER_GLOBAL, LogLevel);
		if(!reporterSessions.containsKey(ReporterName))
		{
			Reporter a = new Reporter(ReporterName, LogLevel);
			synchronized(reporterSessions)
			{
				reporterSessions.put(ReporterName, a);
			}
			if(!ReporterName.equals(REPORTER_GLOBAL)) getGlobal().logInfo("Reporter \"" + ReporterName + "\" was created!");
			return a;
		}
		return LogLevel == -1 ? reporterSessions.get(ReporterName) : reporterSessions.get(ReporterName).setLoggingLevel(LogLevel);
	}
	
	private Reporter(String ReporterName, int ReporterLevel)
	{
		reporterName = ReporterName;
		reporterLogs = new ArrayList<Report>();
		reporterHandles = new ArrayList<ReporterHandle>();
		reporterLevel = ReporterLevel != -1 ? (byte)ReporterLevel : reporterDefaultLevel;
	}
	
	@SuppressWarnings({ "restriction", "deprecation" })
	private final Report logEvent(int LogLevel, String LogDetail, Throwable LogException)
	{
		if(LogLevel >= reporterLevel)
		{
			Class<?> a = null;
			for(byte b = 4; b < 8 && (a == null || a == Reporter.class || a == Security.class || BasicException.class.isAssignableFrom(a)); b++)
				a = sun.reflect.Reflection.getCallerClass(b);
			if(a == null) a = sun.reflect.Reflection.getCallerClass(3);
			Report c = new Report(reporterLevel, a, reporterName + ": " + LogDetail, LogException);
			synchronized(reporterLogs)
			{
				reporterLogs.add(c);
			}
			if(!reporterHandles.isEmpty()) for(int d = 0; d < reporterHandles.size(); d++)
				if(reporterHandles.get(d).canProcessReport(reporterLevel))
					reporterHandles.get(d).processReport(c);
			return c;
		}
		return null;
	}
}