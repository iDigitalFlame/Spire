package com.spire.web;

import java.io.IOException;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public final class WebRedirect implements WebPage
{
	private final String redirectPath;

	public WebRedirect(String RedirectPath) throws NullException, StringException
	{
		if(RedirectPath == null) throw new NullException("RedirectPath");
		if(RedirectPath.isEmpty()) throw new StringException("RedirectPath");
		redirectPath = RedirectPath;
	}
	
	public final void onPageGet(WebState PageState) throws IOException
	{
		PageState.setRedirect(redirectPath);
	}
	public final void onPagePost(WebState PageState) throws IOException
	{
		onPageGet(PageState);
	}
}