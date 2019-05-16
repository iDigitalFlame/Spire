package com.spire.web;

import java.io.IOException;

public interface WebPage
{	
	void onPageGet(WebState PageState) throws IOException;
	void onPagePost(WebState PageState) throws IOException;
}