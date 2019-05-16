package com.spire.log;

import com.spire.util.Stamp;
import com.spire.ex.CloneException;

public final class Report
{
	public final byte reportLevel;
	public final Stamp reportStamp;
	public final String reportDetail;
	public final Thread reportThread;
	public final Class<?> reportClass;
	public final Throwable reportException;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Report && ((Report)CompareObject).reportLevel == reportLevel &&
			   ((Report)CompareObject).reportClass.equals(reportClass) &&
			   ((Report)CompareObject).reportStamp.equals(CompareObject) &&
			   ((Report)CompareObject).reportThread.equals(reportThread) &&
			   ((Report)CompareObject).reportDetail.hashCode() == reportDetail.hashCode();
	}
	
	public final int hashCode()
	{
		return (int)Math.floor(reportStamp.getLongTime() / (1 + reportLevel)) + reportClass.hashCode() + reportStamp.hashCode() +
			   reportLevel + reportDetail.hashCode();
	}
	
	public final String toString()
	{
		return reportStamp.toString() + " (" + reportClass.getName() + ": " + reportThread.getName() + ") " +
			   (reportDetail != null ? reportDetail : "") + (reportException != null ? " [" + reportException.getClass().getSimpleName() + "/" + reportException.getMessage() + "]" : "");
	}
	
	protected Report(byte ReportLevel, Class<?> ReportClass, String ReportDetail, Throwable ReportException)
	{
		reportLevel = ReportLevel;
		reportStamp = new Stamp();
		reportClass = ReportClass;
		reportDetail = ReportDetail;
		reportException = ReportException;
		reportThread = Thread.currentThread();
	}

	protected final Report clone() throws CloneException
	{
		throw new CloneException("Cannot Clone a Report!");
	}
}