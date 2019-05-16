package com.spire.net;

import java.io.IOException;

public interface StreamAdapter
{
	void onConnect(NetworkStream Stream, boolean IsServer) throws IOException;
	
	boolean runAsThread();
	boolean canReplaceAdapter(); 
}