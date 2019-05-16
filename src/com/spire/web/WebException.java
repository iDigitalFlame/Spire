package com.spire.web;

import com.spire.log.Reporter;

final class WebException extends Exception
{
	private static final long serialVersionUID = 20120413L;
	
	protected final WebMessage exceptionMessage;
	
	public final String getMessage()
	{
		return exceptionMessage.messageText;
	}
	
	protected WebException(WebMessage Message, String WebReport)
	{
		exceptionMessage = Message;
		Reporter.debug(Reporter.REPORTER_WEB, WebReport);
	}
}