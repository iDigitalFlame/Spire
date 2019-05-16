package com.spire.web;

import com.spire.io.Stream;
import java.io.IOException;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.activation.FileTypeMap;

public final class WebContent implements WebPage
{
	private final String contentPath;
	private final String contentType;

	public WebContent(String ContentPath) throws NullException, StringException
	{
		if(ContentPath == null) throw new NullException("ContentPath");
		if(ContentPath.isEmpty()) throw new StringException("ContentPath");
		contentPath = ContentPath;
		contentType = getMIMEType(ContentPath);
	}
	public WebContent(String ContentType, String ContentPath) throws NullException, StringException
	{
		if(ContentPath == null) throw new NullException("ContentPath");
		if(ContentType == null) throw new NullException("ContentType");
		if(ContentPath.isEmpty()) throw new StringException("ContentPath");
		if(ContentType.isEmpty()) throw new StringException("ContentType");
		contentPath = ContentPath;
		contentType = ContentType;
	}
	
	public final void onPageGet(WebState PageState) throws IOException
	{
		PageState.setMIMEType(contentType);
		Stream a = Stream.getFileInputStream(contentPath);
		a.writeToStream(PageState.getOutputStream());
		a.close();
	}
	public final void onPagePost(WebState PageState) throws IOException
	{
		onPageGet(PageState);
	}
		
	public static final String getMIMEType(String FileName)
	{
		int a = FileName.lastIndexOf('.');
		if(a == -1) return "text/html";
		String b = FileName.substring(a + 1);
		if(b.equalsIgnoreCase("jpg") || b.equalsIgnoreCase("jpeg"))
			return "image/jpeg";
		if(b.equalsIgnoreCase("png"))
			return "image/png";
		if(b.equalsIgnoreCase("docx"))
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		if(b.equalsIgnoreCase("zip"))
			return "application/zip";
		if(b.equalsIgnoreCase("html"))
			return "text/html";
		if(b.equalsIgnoreCase("htm"))
			return "text/html";
		return FileTypeMap.getDefaultFileTypeMap().getContentType(FileName);
	}
}