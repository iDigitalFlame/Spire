package com.spire.web.mocha;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.spire.io.Streamable;
import com.spire.web.WebState;

final class MochaReader
{
	private final byte[] readerBuffer;
	private final Streamable readerInput;
	private final MochaPage readerInstance;
	private final WebState readerCurrentState;
	
	private int readerReads;
	private short readerIndex;
	private byte readerSubCount;
	private short readerSubIndex;
	private short readerReadIndex;
	private byte[] readerSubBuffer;
	
	protected MochaReader(MochaPage Page, WebState Current, Streamable InStream)
	{
		readerSubIndex = -1;
		readerInstance = Page;
		readerInput = InStream;
		readerBuffer = new byte[850];
		readerCurrentState = Current;
	}
	
	protected final void createSub(short StartIndex)
	{
		readerSubIndex = StartIndex;
		readerSubCount = 0;
	}
	protected final void processStatememts(byte[] ByteArray, int StartIndex)
	{
		
	}
	
	protected final byte peekNext() throws IOException
	{
		return readerIndex >= readerReadIndex ? -1 : readerBuffer[readerIndex];
	}
	protected final byte readNext() throws IOException
	{
		readerReads++;
		if(readerIndex >= readerReadIndex)
		{
			if(readerSubBuffer != null)
			{
				int a = readerSubBuffer.length;
				readerSubBuffer = Arrays.copyOf(readerSubBuffer, a + readerIndex - 1);
				System.arraycopy(readerBuffer, 0, readerSubBuffer, a, readerIndex - 1);
				readerSubIndex = -1;
			}
			else if(readerSubIndex > -1)
			{
				readerSubBuffer = Arrays.copyOfRange(readerBuffer, readerSubIndex, readerIndex - 1);
				readerSubIndex = -1;
			}
			readerReadIndex = (short)readerInput.readByteArray(readerBuffer);
			if(readerReadIndex <= 0) throw new EOFException();
			readerIndex = 0;
		}
		return readerBuffer[readerIndex++];
	}

	protected final int getReads()
	{
		return readerReads;
	}
	
	protected final byte[] finishSub(short EndIndex)
	{
		if(readerSubBuffer != null)
		{
			int a = readerSubBuffer.length;
			readerSubBuffer = Arrays.copyOf(readerSubBuffer, a + EndIndex);
			System.arraycopy(readerBuffer, 0, readerSubBuffer, a, EndIndex);
		}
		else
			readerSubBuffer = Arrays.copyOfRange(readerBuffer, readerSubIndex, EndIndex);
		readerSubIndex = -1;
		byte[] b = readerSubBuffer;
		readerSubBuffer = null;
		return b;
	}
}