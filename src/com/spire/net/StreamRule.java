package com.spire.net;

import java.net.InetAddress;

public interface StreamRule
{
	boolean acceptConnection(Computer ConnectHost, InetAddress ConnectAddress);
}