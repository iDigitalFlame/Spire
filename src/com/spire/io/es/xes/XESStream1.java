package com.spire.io.es.xes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.PermissionException;
import com.spire.io.Stream;
import com.spire.util.Constants;

public class XESStream1 extends Stream
{
	private static final String STREAM_TYPE = "xes";
	
	protected final XESContainer streamXESIn;
	protected final XESContainer streamXESOut;
	
	protected Stream I;

	public void flush() throws IOException
	{
		if(streamXESOut.containerBlock != null)
			endBlock();
		super.flush();
	}
	public void writeByte(int ByteValue) throws IOException
	{
		writeData(ByteValue);
	}
	public void writeLong(long LongValue) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		streamXESOut.conatinerBuffer[0] = (byte)((LongValue >> 56) & 0xFF); 
		streamXESOut.conatinerBuffer[1] = (byte)((LongValue >> 48) & 0xFF); 
		streamXESOut.conatinerBuffer[2] = (byte)((LongValue >> 40) & 0xFF); 
		streamXESOut.conatinerBuffer[3] = (byte)((LongValue >> 32) & 0xFF); 
		streamXESOut.conatinerBuffer[4] = (byte)((LongValue >> 24) & 0xFF); 
		streamXESOut.conatinerBuffer[5] = (byte)((LongValue >> 16) & 0xFF); 
		streamXESOut.conatinerBuffer[6] = (byte)((LongValue >> 8) & 0xFF); 
		streamXESOut.conatinerBuffer[7] = (byte)(LongValue & 0xFF); 
		writeData(streamXESOut.conatinerBuffer, 0, 8);
	}
	public void writeShort(int ShortValue) throws IOException
	{
		writeData((ShortValue >> 8) & 0xFF);
		writeData(ShortValue & 0xFF);
	}
	public void writeInteger(int IntegerValue) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		streamXESOut.conatinerBuffer[0] = (byte)((IntegerValue >> 24) & 0xFF); 
		streamXESOut.conatinerBuffer[1] = (byte)((IntegerValue >> 16) & 0xFF); 
		streamXESOut.conatinerBuffer[2] = (byte)((IntegerValue >> 8) & 0xFF); 
		streamXESOut.conatinerBuffer[3] = (byte)(IntegerValue & 0xFF);
		writeData(streamXESOut.conatinerBuffer, 0, 4);
	}
	public void writeBoolean(boolean BoolValue) throws IOException
	{	
		write(BoolValue ? 1 : 0);
	}
	public void writeString(String StringValue) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		if(StringValue == null) 
		{
			writeData(-1);
			return;
		}
		else if(StringValue.isEmpty())
		{
			writeData(0);
			return;
		}
		else if(StringValue.length() < 255)
		{
			streamXESOut.conatinerBuffer[0] = 2;
			streamXESOut.conatinerBuffer[1] = (byte)StringValue.length();
			writeData(streamXESOut.conatinerBuffer, 0, 2);
		}
		else if(StringValue.length() < Constants.MAX_USHORT_SIZE)
		{
			streamXESOut.conatinerBuffer[0] = 4;
			streamXESOut.conatinerBuffer[0] = (byte)((StringValue.length() >> 8) & 0xFF);
			streamXESOut.conatinerBuffer[1] = (byte)(StringValue.length() & 0xFF);
			writeData(streamXESOut.conatinerBuffer, 0, 3);
		}
		else
		{
			streamXESOut.conatinerBuffer[0] = 6;
			streamXESOut.conatinerBuffer[1] = (byte)((StringValue.length() >> 24) & 0xFF); 
			streamXESOut.conatinerBuffer[2] = (byte)((StringValue.length() >> 16) & 0xFF); 
			streamXESOut.conatinerBuffer[3] = (byte)((StringValue.length() >> 8) & 0xFF); 
			streamXESOut.conatinerBuffer[4] = (byte)(StringValue.length() & 0xFF);
			writeData(streamXESOut.conatinerBuffer, 0, 5);
		}
		writeData(StringValue.getBytes());
	}
	
	public int readUnsignedByte() throws IOException
	{	
		return readData() & 0xFF;
	}
	public int readUnsignedShort() throws IOException
	{	
		return ((readData() & 0xFF) << 8) | (readData() & 0xFF);
	}
	public int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		return 0;
	}
	public int readDirectByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		return 0;
	}

	public long readLong() throws IOException
	{	
		return (((long)readData()<< 56) + ((long)(readData() & 255) << 48) +
				((long)(readData() & 255) << 40) + ((long)(readData() & 255) << 32) +
				((long)(readData() & 255) << 24) + ((readData() & 255) << 16) +
				((readData() & 255) <<  8) + ((readData() & 255) <<  0));
	}

	public String readString() throws IOException
	{	
		if(streamInput == null) notInputStream();
		byte a = readByte();
		int b = 0;
		switch(a)
		{
		case -1:
			return null;
		case 0:
			return Constants.EMPTY_STRING;
		case 1:
			b = readUnsignedByte();
			break;
		case 2:
			b = readUnsignedByte();
			break;
		case 3:
			b = readUnsignedShort();
			break;
		case 4:
			b = readUnsignedShort();
			break;
		case 5:
			b = readInteger();
			break;
		case 6:
			b = readInteger();
			break;
		default:
			b = 0;
			break;
		}
		if(a %2 == 0)
		{
			byte[] c = new byte[b];
			for(; b > 0; b--)
				c[c.length - b] = readData();
			return new String(c, 0, c.length);
		}
		char[] d = new char[b];
		for(; b > 0; b--)
			d[d.length - b] = (char)readShort();
		return new String(d);
	}
	
	protected XESStream1(InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		super(StreamInput, StreamOutput);
		streamXESIn = new XESContainer();
		streamXESOut = new XESContainer();
		streamXESIn.containerCipher = new XESCipher();
		streamXESOut.containerCipher = new XESCipher();
	}

	protected final String streamType()
	{
		return STREAM_TYPE;
	}
	
	private final void endBlock() throws IOException
	{
		streamXESOut.containerBlock.writeFooter(streamXESOut.containerCipher, I);
	}
	private final void readNextBlock() throws IOException
	{
		streamXESIn.containerBlock = new XESBlock();
		streamXESIn.containerBlock.readHeader(streamXESIn.containerCipher, I);
	}
	private final void createNewBlock() throws IOException
	{
		streamXESOut.containerBlock = new XESBlock();
		streamXESOut.containerBlock.blockFlags.setTagA(true);
		streamXESOut.containerBlock.writeHeader(streamXESOut.containerCipher, I);
	}
	private final void readData(int Length) throws IOException
	{
		
	}
	private final void writeData(int ByteValue) throws IOException
	{
		writeData((byte)ByteValue);
	}
	private final void writeData(byte ByteValue) throws IOException
	{
		if(streamXESOut.containerBlock == null) createNewBlock();
		streamXESOut.containerCipher.writeCipher(ByteValue, I);
	}
	private final void writeData(byte[] ByteArray) throws IOException
	{
		if(streamXESOut.containerBlock == null) createNewBlock();
		streamXESOut.containerCipher.writeCipher(ByteArray, I);
	}
	private final void writeData(byte[] ByteArray, int Start) throws IOException
	{
		if(streamXESOut.containerBlock == null) createNewBlock();
		streamXESOut.containerCipher.writeCipher(ByteArray, Start, I);
	}
	private final void writeData(byte[] ByteArray, int Start, int Length) throws IOException
	{
		if(streamXESOut.containerBlock == null) createNewBlock();
		streamXESOut.containerCipher.writeCipher(ByteArray, Start, Length, I);
	}
	
	private final byte readData() throws IOException
	{
		if(streamXESIn.containerBlock == null)
			readNextBlock();
		byte a = streamXESIn.containerCipher.readCipher(I);
		if(streamXESIn.containerBlock.blockFootIndex > 0)
		{
			if(a == XESBlock.BLOCK_FOOTER_HEAD[streamXESIn.containerBlock.blockFootIndex])
			{
				streamXESIn.containerBlock.blockFootIndex++;
				if(streamXESIn.containerBlock.blockFootIndex >= XESBlock.BLOCK_FOOTER_HEAD.length)
				{
					streamXESIn.containerBlock.readFooter(streamXESIn.containerCipher, I);
					streamXESIn.containerBlock = null;
				}
			}
			else
				streamXESIn.containerBlock.blockFootIndex = 0;
		}
		else if(a == XESBlock.BLOCK_FLAG)
			streamXESIn.containerBlock.blockFootIndex = 1;
		return a;
	}
}