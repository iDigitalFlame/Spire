package com.spire.io.es.xes;

final class XESContainer
{
	private static final byte BUFFER_SIZE = 8;
	
	protected final byte[] conatinerBuffer;
	
	protected long conatinerCount;
	protected XESBlock containerBlock;
	protected XESCipher containerCipher;
	
	protected XESContainer()
	{
		conatinerBuffer = new byte[BUFFER_SIZE];
	}
}