package com.spire.log;

public interface ReporterHandle
{
	void processReport(Report ReportData);
	
	boolean canProcessReport(byte ReportLevel);
}