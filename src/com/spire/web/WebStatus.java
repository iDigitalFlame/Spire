package com.spire.web;

import java.io.IOException;
import java.text.DecimalFormat;
import com.spire.util.Constants;

public final class WebStatus extends WebSpirePage
{
	private static WebStatus pageInstance;
	private static DecimalFormat pageFormatter;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebStatus;
	}
	
	public final int hashCode()
	{
		return getPageTitle().hashCode();
	}
	
	public final String toString()
	{
		return "WebStatus(WP) Status/M/T";
	}
	
	public final WebStatus clone()
	{
		return this;
	}
	
	public static final WebStatus getStatusPage()
	{
		if(pageInstance == null) pageInstance = new WebStatus();
		return pageInstance;
	}
	
	protected final void getPage(WebState PageState) throws IOException
	{
		if(PageState.isPost() && PageState.containsFormValue("start_gc")) System.gc();
		StringBuilder a = new StringBuilder();
		a.append("<style type=\"text/css\">#spire_membar { width: 100%; height: 35px; border: 1px solid black; padding: 0px; } .spire_thread_odd { background: #c1c1c1; } ");
		a.append("#spire_memebar_table { width: 100%; height: 34px; padding: 0px; margin: 0px; } .spire_div { padding: 10px; border: 2px solid black; } ");
		a.append("#spire_mem_info { padding-left: 15px; } #spire_mem_info b { font-size: 12.5pt; } .spire_thread_top { border-bottom: 1px solid black; }</style><h2>");
		a.append(Constants.LOCAL.getName());
		a.append("</h2><div><div class=\"spire_div\"><h3>Memory Stats</h3><hr/>");
		a.append("<form method=\"post\"><input type=\"submit\" value=\"Start GC\" /></form>");
		long b = Runtime.getRuntime().totalMemory(), c = Runtime.getRuntime().freeMemory(), d = b - c, e = Runtime.getRuntime().maxMemory();
		a.append("<div id=\"spire_mem_info\"><div><b>Allocated Memory: </b>");
		a.append(getFormatedNumber((b / 1024) / 1024));
		a.append(" MB (");
		a.append(getFormatedNumber(b / 1024));
		a.append(" KB; ");
		a.append(getFormatedNumber(b));
		a.append(" Bytes)</div>");
		a.append("<div><b>Free Memory: </b>");
		a.append(getFormatedNumber((c / 1024) / 1024));
		a.append(" MB (");
		a.append(getFormatedNumber(c / 1024));
		a.append(" KB; ");
		a.append(getFormatedNumber(c));
		a.append(" Bytes)</div>");
		a.append("<div><b>Used Memory: </b>");
		a.append(getFormatedNumber((d / 1024) / 1024));
		a.append(" MB (");
		a.append(getFormatedNumber(d / 1024));
		a.append(" KB; ");
		a.append(getFormatedNumber(d));
		a.append(" Bytes)</div><div><b>Max Memory: </b>");
		a.append(getFormatedNumber((e / 1024) / 1024));
		a.append(" MB (");
		a.append(getFormatedNumber(e / 1024));
		a.append("  KB; ");
		a.append(getFormatedNumber(e));
		a.append(" Bytes)</div></div><hr/>");
		a.append("<div id=\"spire_membar\"><table id=\"spire_memebar_table\"><tr><td style=\"padding: 0px; background: Cyan; width: ");
		a.append(Math.round(((double)c / (double)b) * 100D));
		a.append("%;\"><b>Free (");
		a.append(Math.round(((double)c / (double)b) * 100D));
		a.append("%)</b></td><td style=\"padding: 0px; background: Yellow; width: ");
		a.append(Math.round(((double)d / (double)b) * 100D));
		a.append("%;\"><b>Used (");
		a.append(Math.round(((double)d / (double)b) * 100D));
		a.append("%)</b></td></tr></table></div></div><hr/>");
		a.append("<div class=\"spire_div\"><h3>Thread Status</h3><hr/><table style=\"width: 100%;\"><tr><td class=\"spire_thread_top\" style=\"width: 40%;\"><b>Thread Name</b></td><td class=\"spire_thread_top\">Thread Priority</td><td class=\"spire_thread_top\">Thread ID</td><td class=\"spire_thread_top\">Thread State</td></tr>");
		Thread[] f = new Thread[Thread.activeCount()];
		Thread.enumerate(f);
		for(int g = 0; g < f.length; g++)
		{
			a.append("<tr");
			if(g %2 != 0)
				a.append(" class=\"spire_thread_odd\"");
			a.append("><td>");
			a.append(f[g].getName());
			a.append("</td><td>");
			a.append(f[g].getPriority());
			a.append("</td><td>");
			a.append(f[g].getId());
			a.append("</td><td>");
			a.append(f[g].getState().toString());
			a.append("</td></tr>");
		}
		a.append("</table></div>");
		PageState.appendText(a);
	}
	
	protected final String getPageTitle()
	{
		return "Spire Status (" + Constants.LOCAL.getName() + ")";
	}
	
	private WebStatus() { }
	
	private static final String getFormatedNumber(long Number)
	{
		if(pageFormatter == null) pageFormatter = new DecimalFormat("#,###");
		return pageFormatter.format(Number);
	}
}