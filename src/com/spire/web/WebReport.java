package com.spire.web;

import java.io.IOException;
import java.util.ArrayList;
import com.spire.log.Report;
import com.spire.sec.Security;
import com.spire.util.Constants;
import com.spire.log.ReporterHandle;
import com.spire.ex.NumberException;
import com.spire.ex.PermissionException;

public final class WebReport extends WebSpirePage implements ReporterHandle
{
	private final ArrayList<Report> reportList;

	private byte reportLevel;
	private short reportPageSize;
	
	public WebReport() throws PermissionException
	{
		this(1, 50);
	}
	public WebReport(int ReportLevel) throws NumberException, PermissionException
	{
		this(ReportLevel, 50);
	}
	public WebReport(int ReportLevel, int ReportPageSize) throws NumberException, PermissionException
	{
		Security.check("io.web.report");
		if(ReportLevel < 0) throw new NumberException("ReportLevel", ReportLevel, false);
		if(ReportPageSize < 0) throw new NumberException("ReportPageSize", ReportPageSize, false);
		if(ReportLevel > Byte.MAX_VALUE) throw new NumberException("ReportLevel", ReportLevel, 0, Byte.MAX_VALUE);
		if(ReportPageSize > Short.MAX_VALUE) throw new NumberException("ReportPageSize", ReportPageSize, 0, Short.MAX_VALUE);
		reportLevel = (byte)ReportLevel;
		reportList = new ArrayList<Report>();
		reportPageSize = (short)ReportPageSize;
	}
	
	public final void clearReports()
	{
		
	}
	public final void processReport(Report ReportData)
	{
		reportList.add(ReportData);
	}

	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebReport && ((WebReport)CompareObject).reportLevel == reportLevel && ((WebReport)CompareObject).reportList.equals(reportList) && ((WebReport)CompareObject).reportPageSize == reportPageSize;
	}
	public final boolean canProcessReport(byte ReportLevel)
	{
		return ReportLevel >= reportLevel;
	}
	
	public final byte getReportLevel()
	{
		return reportLevel;
	}
	
	public final int hashCode()
	{
		return reportList.hashCode() + reportLevel + reportPageSize;
	}
	public final int getReportSize()
	{
		return reportList.size();
	}
	public final int getReportPageSize()
	{
		return reportLevel;
	}
	
	public final String toString()
	{
		return "WebReport(WP) [L:" + reportLevel + ";S:" + reportPageSize + "]";
	}
	
	public final WebReport clone()
	{
		WebReport a = new WebReport();
		a.reportLevel = reportLevel;
		a.reportPageSize = reportPageSize;
		return a;
	}
	
	protected final void getPage(WebState PageState) throws IOException
	{
		int a = 0;
		if(PageState.containsRequest("pclear"))
		{
			reportList.clear();
		}
		if(PageState.containsRequest("p")) try
		{
			a = Integer.parseInt(PageState.getRequest("p"));
		}
		catch (NumberFormatException Exception) { }
		if(PageState.containsRequest("psize")) try
		{
			short b = (short)Integer.parseInt(PageState.getRequest("psize"));
			if(b > 0) reportPageSize = b;
		}
		catch (NumberFormatException Exception) { }
		if(PageState.containsRequest("spire_log_type")) try
		{
			byte b = (byte)Integer.parseInt(PageState.getRequest("spire_log_type"));
			if(b >= 0) reportLevel = b;
		}
		catch (NumberFormatException Exception) { }
		StringBuilder c = new StringBuilder();
		c.append("<div style=\"padding: 5px 10px 5px 10px;\"><form method=\"get\" style=\"display:inline; padding-right: 5px;\">Report Level: <select name=\"spire_log_type\">");
		c.append("<option value=\"0\"");
		if(reportLevel == 0) c.append(" selected");
		c.append(">Debug and Above</option><option value=\"1\"");
		if(reportLevel == 1) c.append(" selected");
		c.append(">Info and Above</option><option value=\"2\"");
		if(reportLevel == 2) c.append(" selected");
		c.append(">Warning and Above</option><option value=\"3\"");
		if(reportLevel == 3) c.append(" selected");
		c.append(">Error and Above</option><option value=\"4\"");
		if(reportLevel == 4) c.append(" selected");
		c.append(">Critical and Above</option><option value=\"5\"");
		if(reportLevel == 5) c.append(" selected");
		c.append(">Severe and Above</option><option value=\"6\"");
		if(reportLevel == 6) c.append(" selected");
		c.append(">Failure and Above</option></select><input type=\"submit\" name=\"save\" value=\"Save\" /><input type=\"submit\" name=\"pclear\" value=\"Clear Log\" /></form>");
		c.append(getPager(PageState, a));
		c.append("</div><hr/><div style=\"background: white; padding: 10px; margin: 5px; white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space:-o-pre-wrap; word-wrap: break-word;\">");
		int d = reportList.size() - reportPageSize - (reportPageSize * a);
		if(d < 0 || reportList.size() < reportPageSize) d = 0;
		for(int e = (reportList.size() - 1) - (reportPageSize * a); e > d && e > 0; e--)
		{
			c.append("<div class=\"report_d\">");
			c.append(reportList.get(e).toString());
			c.append("</div>");
		}
		c.append("</div>");
		PageState.appendText(c);
	}
	
	protected final String getPageTitle()
	{
		return "Spire Web Log";
	}

	private final String getPager(WebState PageState, int Page)
	{
		int a = Page - 4, b = reportList.size()/reportPageSize, c = Page + 4;
		if(b > 0)
		{
			if(a < 0) a = 0;
			if(c > b) c = b;
			StringBuilder d = new StringBuilder();
			d.append("<style type=\"text/css\">#spire_pager { display: inline; } #spire_pager a:hover { color: black; } #spire_pager a:visited { color: black; } #spire_pager a, span { color: black; padding-left: 5px; padding-right: 5px; }</style><div id=\"spire_pager\">");
			if(b >= 3)
			{
				d.append("<a href=\"");
				d.append(PageState.convertFullPath(PageState.getLocation() + "?p=0"));
				d.append("\">&lt;&lt; First</a>");
			}
			for(; a <= c; a++)
			{
				if(a == Page)
				{
					d.append("<span>");
					d.append(a + 1);
					d.append("</span>");
				}
				else
				{
					d.append("<a href=\"");
					d.append(PageState.convertFullPath(PageState.getLocation() + "?p=" + a));
					d.append("\">");
					d.append(a + 1);
					d.append("</a>");
				}
			}
			if(b >= 3)
			{
				d.append("<a href=\"");
				d.append(PageState.convertFullPath(PageState.getLocation() + "?p=" + b));
				d.append("\">Last &gt;&gt;</a>");
			}
			d.append("</div>");
			return d.toString();
		}
		return Constants.EMPTY_STRING;
	}
}