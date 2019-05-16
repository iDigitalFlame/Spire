package com.spire.io;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

public interface SecurityProvider
{
	String getProviderName();
	
	Socket createSocket() throws IOException;
	Socket createSocket(InetAddress ConnectAddress, int PortNumber) throws IOException, NumberException, NullException;
	Socket createSocket(String ConnectAddress, int PortNumber) throws IOException, NumberException, NullException, StringException;
	Socket createSocket(InetAddress ConnectAddress, int ConnectPort, InetAddress LocalAddress, int LocalPort) throws IOException, NumberException, NullException;
	Socket createSocket(String ConnectAddress, int ConnectPort, InetAddress LocalAddress, int LocalPort) throws IOException, NumberException, NullException, StringException;
	
	ServerSocket createServerSocket() throws IOException;
	ServerSocket createServerSocket(int PortNumber) throws IOException, NumberException;
	ServerSocket createServerSocket(int PortNumber, int QueueBacklog) throws IOException, NumberException;
	ServerSocket createServerSocket(int PortNumber, int QueueBacklog, InetAddress LocalAddress) throws IOException, NumberException, NullException;
	
	InputStream modifyInputStream(InputStream StreamInput) throws IOException, NullException;
	
	OutputStream modifyOuputStream(OutputStream StreamOutput) throws IOException, NullException;
}