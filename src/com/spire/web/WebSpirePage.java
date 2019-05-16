package com.spire.web;

import java.io.IOException;
import com.spire.util.Constants;

abstract class WebSpirePage implements WebPage
{	
	public final void onPageGet(WebState PageState) throws IOException
	{
		StringBuilder a = new StringBuilder();
		a.append("<html><head><title>");
		a.append(getPageTitle());
		a.append("</title><style type=\"text/css\" media=\"all\">body { margin: 0; padding: 0; background: #999999; } #sp_base_tabs { padding-top: 10px; } #sp_base_tabs ul { list-style: none; margin: 0px; } ");
		a.append("#sp_base_tabs_sep { width: 100%; height: 30px; background: #d4d5dc; border-top: 1px solid black; border-bottom: 1px solid black; } #sp_base_tabs li { font-size: 16pt; display: inline-block; padding: 10px 20px 10px 20px; border-left: 2px solid black; border-top: 2px solid black; border-right: 2px solid black; -webkit-border-top-left-radius: 5px; -webkit-border-top-right-radius: 5px; -moz-border-radius-topleft: 5px; -moz-border-radius-topright: 5px; border-top-left-radius: 5px; border-top-right-radius: 5px; background: #d4d5dc; } ");
		a.append("#sp_base_header_c { background: #999999; left: 0; position:fixed; width: 100%; top: 0; } #sp_base_header { margin: 0 auto; width: 100%; } #sp_base_content_c { margin: 0 auto; overflow: auto; padding-top: 110px; padding-bottom: 50px;  } ");
		a.append("#sp_base_content { padding: 5px; } #sp_base_footer_c { bottom: 0; height: 25px; left: 0; position: fixed; width: 100%; background: #d4d5dc; text-align: right; padding-top: 5px; padding-bottom: 5px; border-top: 1px solid black; } ");
		a.append("#sp_base_footer { margin: 0 auto; width: 100%; }</style><style type=\"text/css\" media=\"screen\">#sp_base_content { border: 1px solid black; background: #d4d5dc; } ");
		a.append("#sp_base_content_c { padding-left: 10%; padding-right: 10%; }</style></head><body><div id=\"sp_base_header_c\"><div id=\"sp_base_header\"><div id=\"sp_base_tabs\"><ul><li>");
		a.append(getPageTitle());
		a.append("</li></ul><div id=\"sp_base_tabs_sep\"></div></div></div></div><div id=\"sp_base_content_c\"><div id=\"sp_base_content\">");
		PageState.appendText(a);
		a.delete(0, a.length());
		getPage(PageState);
		a.append("</div></div><div id=\"sp_base_footer_c\"><div id=\"sp_base_footer\"><b>");
		a.append(Constants.SPIRE_VERSION);
        a.append("</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></div></body></html>");
        PageState.appendText(a);
		a.delete(0, a.length());
		a = null;
	}
	public final void onPagePost(WebState PageState) throws IOException
	{
		onPageGet(PageState);
	}
	
	protected WebSpirePage() { }
	
	protected abstract void getPage(WebState PageState) throws IOException;
	
	protected abstract String getPageTitle();
}