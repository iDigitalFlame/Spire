package com.spire.io.es.xes;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Adler32;

import com.spire.io.Streamable;

public final class XESStream
{
	private static final byte BUFFER_SIZE = 32;
	
	private final byte[] streamInBuf;
	private final byte[] streamOutBuf;
	private final XESCipher streamInCipher;
	private final Adler32 streamInChecksum;
	private final Adler32 streamOutChecksum;
	private final XESCipher streamOutCipher;
	private final Streamable streamInstance;
	
	private long streamInLen;
	private long streamOutLen;
	private byte streamInCount;
	private boolean streamInLock;
	private boolean streamOutLock;
	private XESBlock streamInBlock;
	private XESBlock streamOutBlock;
	private byte[] streamInReadAhead;
	
	public XESStream(XESCipher InputCipher, XESCipher OutputCipher, Streamable StreamData)
	{
		streamInstance = StreamData;
		streamInCipher = InputCipher;
		streamOutCipher = OutputCipher;
		streamInChecksum = StreamData.isStreamInput() ? new Adler32() : null;
		streamOutChecksum = StreamData.isStreamOutput() ? new Adler32() : null;
		streamInBuf = StreamData.isStreamInput() ? new byte[BUFFER_SIZE] : null;
		streamOutBuf = StreamData.isStreamOutput() ? new byte[BUFFER_SIZE + 8] : null;
	}
	
	private final void canRead() throws IOException
	{
		if(!streamInstance.isStreamInput())
			throw new IOException("This is not an input stream!");
		if(streamInLock)
			throw new XESException("The stream is locked by a block! Please dismount the block first!");
	}
	private final void canWrite() throws IOException
	{
		if(!streamInstance.isStreamOutput())
			throw new IOException("This is not an output stream!");
		if(streamOutLock)
			throw new XESException("The stream is locked by a block! Please dismount the block first!");
	}
	private final void readData(int ReadSize) throws IOException
	{
		if(ReadSize < BUFFER_SIZE)
		{
			if(streamInBlock == null)
			{
				streamInBlock = new XESBlock();
				streamInBlock.readHeader(streamInCipher, streamInstance);
			}
			byte a = 0;
			for(byte b = 0; b < ReadSize / 8; b++)
			{
				streamInCipher.readCipher(streamInBuf, b, b + 8, streamInstance);
				for(byte c = b; c < (b + 8); c++)
				{
					if(streamInBuf[c] == XESBlock.BLOCK_FLAG)
					{
						//if((c + 8) <)
						{
							//Compare
						}
					}
				}
			}
		}
	}
	private final void writeData(int ByteValue) throws IOException
	{
		if(streamOutBlock == null)
		{
			streamOutBlock = new XESBlock();
			streamOutBlock.blockFlags.setTagA(true);
			streamOutBlock.writeHeader(streamOutCipher, streamInstance);
		}
		streamOutCipher.writeCipher((byte)ByteValue, streamInstance);
		streamOutChecksum.update(ByteValue);
		streamOutLen++;
	}
	private final void writeData(byte[] ByteArray, int StartIndex, int Length) throws IOException
	{
		if(streamOutBlock == null)
		{
			streamOutBlock = new XESBlock();
			streamOutBlock.blockFlags.setTagA(true);
			streamOutBlock.writeHeader(streamOutCipher, streamInstance);
		}
		streamOutCipher.writeCipher(ByteArray, StartIndex, Length, streamInstance);
		streamOutChecksum.update(ByteArray, StartIndex, Length);
		streamOutLen += Length;
	}
	
	private final byte readData() throws IOException
	{
		return 0;
	}
	
	private final int readData(byte[] ByteArray, int StartIndex, int Length) throws IOException
	{
		if(streamInBlock == null)
		{
			streamInBlock = new XESBlock();
			streamInBlock.readHeader(streamInCipher, streamInstance);
		}
		int a = 0;
		if(streamInReadAhead != null)
		{
			if(streamInReadAhead.length > Length)
			{
				System.arraycopy(streamInReadAhead, 0, ByteArray, 0, Length);
				streamInReadAhead = Arrays.copyOfRange(streamInReadAhead, Length, streamInReadAhead.length);
				return Length;
			}
			else if(streamInReadAhead.length == Length)
			{
				System.arraycopy(streamInReadAhead, 0, ByteArray, 0, Length);
				streamInReadAhead = null;
				return Length;
			}
			else
			{
				System.arraycopy(streamInReadAhead, 0, ByteArray, 0, streamInReadAhead.length);
				a += streamInReadAhead.length;
				streamInReadAhead = null;
			}
		}
		for(; a < Length; a++) try
		{
			ByteArray[a] = streamInCipher.readCipher(streamInstance);
			if(ByteArray[a] == XESBlock.BLOCK_FLAG)
			{
				streamInBlock.blockFootIndex = 1;
				if((a + 7) < Length)
				{
					for(int b = a; b < (a + 7); b++)
					{
						ByteArray[b] = streamInCipher.readCipher(streamInstance);
						if(ByteArray[b] == XESBlock.BLOCK_FOOTER_HEAD[streamInBlock.blockFootIndex])
						{
							streamInBlock.blockFootIndex++;
							if(streamInBlock.blockFootIndex >= XESBlock.BLOCK_FOOTER_HEAD.length)
							{
								a -= 1;
								//new
							}
						}
						else
						{
							streamInBlock.blockFootIndex = 0;
							a = b;
							break;
						}
					}
				}
				else
				{
					int c = (a + 7) - Length;
					for(; a < Length; a++)
					{
						
					}
				}
			}
		}
		catch (EOFException Exception) { }
		return a;
	}
}