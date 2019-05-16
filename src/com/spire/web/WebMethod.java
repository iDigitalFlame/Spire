package com.spire.web;

enum WebMethod
{
	GET("GET", true),
	PUT("PUT", false),
	POST("POST", true),
	HEAD("HEAD", true),
	TRACE("TRACE", true),
	DELETE("DELETE", false),
	CONNECT("CONNECT", false),
	OPTIONS("COPTIONS", true);	
	
	protected final String methodName;
	protected final byte[] methodBytes;
	
	protected boolean methodSupported;
	
	public final void setSupported(boolean IsSupported)
	{
		methodSupported = IsSupported;
	}
	
	protected static final WebMethod getMethod(byte[] ByteArray)
	{
		if(GET.methodBytes[0] == ByteArray[0] && 
		   GET.methodBytes[1] == ByteArray[1] &&
		   GET.methodBytes[2] == ByteArray[2]) return GET;
		if(POST.methodBytes[0] == ByteArray[0] && 
		   POST.methodBytes[1] == ByteArray[1] &&
		   POST.methodBytes[2] == ByteArray[2] &&
		   POST.methodBytes[3] == ByteArray[3]) return POST;
		if(PUT.methodBytes[0] == ByteArray[0] && 
		   PUT.methodBytes[1] == ByteArray[1] &&
	       PUT.methodBytes[2] == ByteArray[2]) return PUT;
		if(HEAD.methodBytes[0] == ByteArray[0] && 
	       HEAD.methodBytes[1] == ByteArray[1] &&
	       HEAD.methodBytes[2] == ByteArray[2] &&
	       HEAD.methodBytes[3] == ByteArray[3]) return HEAD;
		if(TRACE.methodBytes[0] == ByteArray[0] && 
	       TRACE.methodBytes[1] == ByteArray[1] &&
	       TRACE.methodBytes[2] == ByteArray[2] &&
	       TRACE.methodBytes[3] == ByteArray[3] &&
	       TRACE.methodBytes[4] == ByteArray[4]) return TRACE;
		if(DELETE.methodBytes[0] == ByteArray[0] && 
		   DELETE.methodBytes[1] == ByteArray[1] &&
		   DELETE.methodBytes[2] == ByteArray[2] &&
		   DELETE.methodBytes[3] == ByteArray[3] &&
		   DELETE.methodBytes[4] == ByteArray[4] &&
	       DELETE.methodBytes[5] == ByteArray[5] &&
		   DELETE.methodBytes[6] == ByteArray[6]) return DELETE;
		if(CONNECT.methodBytes[0] == ByteArray[0] && 
		   CONNECT.methodBytes[1] == ByteArray[1] &&
		   CONNECT.methodBytes[2] == ByteArray[2] &&
		   CONNECT.methodBytes[3] == ByteArray[3] &&
		   CONNECT.methodBytes[4] == ByteArray[4] &&
		   CONNECT.methodBytes[5] == ByteArray[5] &&
		   CONNECT.methodBytes[6] == ByteArray[6] &&
		   CONNECT.methodBytes[7] == ByteArray[7]) return CONNECT;
		if(OPTIONS.methodBytes[0] == ByteArray[0] && 
		   OPTIONS.methodBytes[1] == ByteArray[1] &&
		   OPTIONS.methodBytes[2] == ByteArray[2] &&
		   OPTIONS.methodBytes[3] == ByteArray[3] &&
		   OPTIONS.methodBytes[4] == ByteArray[4] &&
		   OPTIONS.methodBytes[5] == ByteArray[5] &&
		   OPTIONS.methodBytes[6] == ByteArray[6] &&
		   OPTIONS.methodBytes[7] == ByteArray[7]) return OPTIONS;
		return null;
	}
	
	private WebMethod(String MethodName, boolean MethodSupported)
	{
		methodName = MethodName;
		methodSupported = MethodSupported;
		methodBytes = MethodName.getBytes();
	}
}