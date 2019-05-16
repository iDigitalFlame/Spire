package com.spire.io;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import javax.crypto.Cipher;
import com.spire.os.UnixOS;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.io.OutputStream;
import java.io.EOFException;
import com.spire.log.Report;
import com.spire.util.Stamp;
import com.spire.log.Reporter;
import com.spire.os.WindowsOS;
import com.spire.util.BoolTag;
import com.spire.sec.Security;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.spire.util.Constants;
import com.spire.net.FilePacket;
import com.spire.ex.NullException;
import com.spire.ex.SizeException;
import java.io.BufferedInputStream;
import com.spire.ex.CloneException;
import com.spire.ex.StringException;
import java.io.BufferedOutputStream;
import com.spire.ex.NumberException;
import com.spire.ex.FormatException;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.net.MalformedURLException;
import com.spire.ex.PermissionException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public abstract class Stream implements Streamable
{
	public static final UnixOS UNIX = new UnixOS();
	public static final WindowsOS WINDOWS = new WindowsOS();

	protected static final Cipher SECURE_CIPHER = createCipher();

	private static File[] SYSTEM_DRIVES;

	private final byte[] streamInputBuffer;
	private final byte[] streamOutputBuffer;

	protected final StreamBufferInput streamInput;
	protected final StreamBufferOutput streamOutput;

	public void flush() throws IOException
	{
		if(streamOutput != null) streamOutput.flush();
	}
	public void close() throws IOException
	{
		try
		{
			flush();
			if(streamInput != null) streamInput.close();
			if(streamOutput != null) streamOutput.close();
		}
		catch (NullPointerException Exception)
		{
			Reporter.warning(Reporter.REPORTER_IO, "Closing the stream threw a NullPointer which was supressed!", Exception);
		}
	}
	public void processReport(Report ReportData)
	{
		try
		{
			writeBytes(ReportData.toString() + '\r' + '\n');
			flush();
		}
		catch (IOException Exception)
		{
			Reporter.reportUncaught("There was an error writing this Report to a Stream!", Exception);
		}
	}
	public final void resetMark() throws IOException
	{
		if(streamInput == null) notInputStream();
		streamInput.reset();
	}
	public void writeByte(int ByteValue) throws IOException
	{
		write(ByteValue);
	}
	public void writeLong(long LongValue) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		streamOutputBuffer[0] = (byte)((LongValue >> 56) & 0xFF);
		streamOutputBuffer[1] = (byte)((LongValue >> 48) & 0xFF);
		streamOutputBuffer[2] = (byte)((LongValue >> 40) & 0xFF);
		streamOutputBuffer[3] = (byte)((LongValue >> 32) & 0xFF);
		streamOutputBuffer[4] = (byte)((LongValue >> 24) & 0xFF);
		streamOutputBuffer[5] = (byte)((LongValue >> 16) & 0xFF);
		streamOutputBuffer[6] = (byte)((LongValue >> 8) & 0xFF);
		streamOutputBuffer[7] = (byte)(LongValue & 0xFF);
		streamOutput.write(streamOutputBuffer, 0, 8);
	}
	public void writeShort(int ShortValue) throws IOException
	{
		write((ShortValue >> 8) & 0xFF);
		write(ShortValue & 0xFF);
	}
	public void writeInteger(int IntegerValue) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		streamOutputBuffer[0] = (byte)((IntegerValue >> 24) & 0xFF);
		streamOutputBuffer[1] = (byte)((IntegerValue >> 16) & 0xFF);
		streamOutputBuffer[2] = (byte)((IntegerValue >> 8) & 0xFF);
		streamOutputBuffer[3] = (byte)(IntegerValue & 0xFF);
		streamOutput.write(streamOutputBuffer, 0, 4);
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
			write(-1);
			return;
		}
		else if(StringValue.isEmpty())
		{
			write(0);
			return;
		}
		else if(StringValue.length() < 255)
		{
			streamOutputBuffer[0] = 2;
			streamOutputBuffer[1] = (byte)StringValue.length();
			streamOutput.write(streamOutputBuffer, 0, 2);
		}
		else if(StringValue.length() < Constants.MAX_USHORT_SIZE)
		{
			streamOutputBuffer[0] = 4;
			streamOutputBuffer[0] = (byte)((StringValue.length() >> 8) & 0xFF);
			streamOutputBuffer[1] = (byte)(StringValue.length() & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 3);
		}
		else
		{
			streamOutputBuffer[0] = 6;
			streamOutputBuffer[1] = (byte)((StringValue.length() >> 24) & 0xFF);
			streamOutputBuffer[2] = (byte)((StringValue.length() >> 16) & 0xFF);
			streamOutputBuffer[3] = (byte)((StringValue.length() >> 8) & 0xFF);
			streamOutputBuffer[4] = (byte)(StringValue.length() & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 5);
		}
		streamOutput.write(StringValue.getBytes());
	}
	public final void writeChar(char CharValue) throws IOException
	{
		writeShort((short)CharValue);
	}
	public final void skipBytes(long BytesToSkip) throws IOException
	{
		if(streamInput == null) notInputStream();
		streamInput.skip(BytesToSkip);
	}
	public final void markPosition(int MarkLimit) throws IOException
	{
		if(streamInput == null) notInputStream();
		if(MarkLimit > 0) streamInput.mark(MarkLimit);
	}
	public final void writeFloat(float FloatValue) throws IOException
	{
		writeInteger(Float.floatToIntBits(FloatValue));
	}
	public final void writeDouble(double DoubleValue) throws IOException
	{
		writeLong(Double.doubleToLongBits(DoubleValue));
	}
	public void writeUnicodeString(String StringValue) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		if(StringValue == null)
		{
			write(-1);
			return;
		}
		else if(StringValue.isEmpty())
		{
			write(0);
			return;
		}
		else if(StringValue.length() < 255)
		{
			streamOutputBuffer[0] = 1;
			streamOutputBuffer[1] = (byte)StringValue.length();
			streamOutput.write(streamOutputBuffer, 0, 2);
		}
		else if(StringValue.length() < Constants.MAX_USHORT_SIZE)
		{
			streamOutputBuffer[0] = 3;
			streamOutputBuffer[0] = (byte)((StringValue.length() >> 8) & 0xFF);
			streamOutputBuffer[1] = (byte)(StringValue.length() & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 3);
		}
		else
		{
			streamOutputBuffer[0] = 5;
			streamOutputBuffer[1] = (byte)((StringValue.length() >> 24) & 0xFF);
			streamOutputBuffer[2] = (byte)((StringValue.length() >> 16) & 0xFF);
			streamOutputBuffer[3] = (byte)((StringValue.length() >> 8) & 0xFF);
			streamOutputBuffer[4] = (byte)(StringValue.length() & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 5);
		}
		writeChars(StringValue);
	}
	public void writeBytes(String StringValue) throws IOException, NullException
	{
		if(streamOutput == null) notOutputStream();
		if(StringValue == null) throw new NullException("StringValue");
		streamOutput.write(StringValue.getBytes());
	}
	public void writeChars(String StringValue) throws IOException, NullException
	{
		if(streamOutput == null) notOutputStream();
		if(StringValue == null) throw new NullException("StringValue");
		if(StringValue.length() %2 == 0) for(int a = 0; a < StringValue.length(); a += 2)
		{
			streamOutputBuffer[0] = (byte)((StringValue.charAt(a) >> 8) & 0xFF);
			streamOutputBuffer[1] = (byte)(StringValue.charAt(a) & 0xFF);
			streamOutputBuffer[2] = (byte)((StringValue.charAt(a + 1) >> 8) & 0xFF);
			streamOutputBuffer[3] = (byte)(StringValue.charAt(a + 1) & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 4);
		}
		else for(int b = 0; b < StringValue.length(); b++)
		{
			streamOutputBuffer[0] = (byte)((StringValue.charAt(b) >> 8) & 0xFF);
			streamOutputBuffer[1] = (byte)(StringValue.charAt(b) & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 2);
		}
	}
	public void readFromStream(InputStream InStream) throws IOException, NullException
	{
		if(streamOutput == null) notOutputStream();
		if(InStream == null) throw new NullException("InStream");
		byte[] a = new byte[8192];
		for(int b = 0; (b = InStream.read(a)) > -1;) writeByteArray(a, 0, b);
		a = null;
	}
	public void writeToStream(OutputStream OutStream) throws IOException, NullException
	{
		if(streamInput == null) notInputStream();
		if(OutStream == null) throw new NullException("OutStream");
		byte[] a = new byte[8192];
		for(int b = 0; (b = readByteArray(a)) > 0;) OutStream.write(a, 0, b);
		a = null;
	}
	public final void readLongArray(long[] LongArray) throws IOException, NullException
	{
		if(LongArray == null) throw new NullException("LongArray");
		readLongArray(LongArray, 0, LongArray.length);
	}
	public final void writeByteArray(byte[] ByteArray) throws IOException, NullException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		writeByteArray(ByteArray, 0, ByteArray.length);
	}
	public final void writeLongArray(long[] LongArray) throws IOException, NullException
	{
		if(LongArray == null) throw new NullException("LongArray");
		writeLongArray(LongArray, 0, LongArray.length);
	}
	public final void readFloatArray(float[] FloatArray) throws IOException, NullException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		readFloatArray(FloatArray, 0, FloatArray.length);
	}
	public final void readShortArray(short[] ShortArray) throws IOException, NullException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		readShortArray(ShortArray, 0, ShortArray.length);
	}
	public final void writeShortArray(short[] ShortArray) throws IOException, NullException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		writeShortArray(ShortArray, 0, ShortArray.length);
	}
	public final void writeFloatArray(float[] FloatArray) throws IOException, NullException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		writeFloatArray(FloatArray, 0, FloatArray.length);
	}
	public final void readFromStream(Streamable InStream) throws IOException, NullException
	{
		//if(InStream == null) throw new NullException("InStream");
		//readFromStream(InStream.getStreamInput());
		// ^ Test This
		if(streamOutput == null) notOutputStream();
		if(InStream == null) throw new NullException("InStream");
		byte[] a = new byte[8192];
		for(int b = 0; (b = InStream.readByteArray(a)) > 0;) writeByteArray(a, 0, b);
		a = null;
	}
	public final void writeToStream(Streamable OutStream) throws IOException, NullException
	{
		if(streamInput == null) notInputStream();
		if(OutStream == null) throw new NullException("OutStream");
		byte[] a = new byte[8192];
		for(int b = 0; (b = readByteArray(a)) > 0;) OutStream.writeByteArray(a, 0, b);
		a = null;
	}
	public final void readIntegerArray(int[] IntegerArray) throws IOException, NullException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		readIntegerArray(IntegerArray, 0, IntegerArray.length);
	}
	public final void writeIntegerArray(int[] IntegerArray) throws IOException, NullException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		writeIntegerArray(IntegerArray, 0, IntegerArray.length);
	}
	public final void readStringArray(String[] StringArray) throws IOException, NullException
	{
		if(StringArray == null) throw new NullException("StringArray");
		readStringArray(StringArray, 0, StringArray.length);
	}
	public final void readDoubleArray(double[] DoubleArray) throws IOException, NullException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		readDoubleArray(DoubleArray, 0, DoubleArray.length);
	}
	public final void writeDoubleArray(double[] DoubleArray) throws IOException, NullException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		writeDoubleArray(DoubleArray, 0, DoubleArray.length);
	}
	public final void writeStringArray(String[] StringArray) throws IOException, NullException
	{
		if(StringArray == null) throw new NullException("StringArray");
		writeStringArray(StringArray, 0, StringArray.length);
	}
	public final void readStringList(List<String> StringList) throws IOException, NullException
	{
		if(StringList == null) throw new NullException("StringList");
		readStringList(StringList, StringList.size());
	}
	public final void writeStringList(List<String> StringList) throws IOException, NullException
	{
		if(StringList == null) throw new NullException("StringList");
		writeStringList(StringList, 0, StringList.size());
	}
	public final void writeBooleanTags(boolean... BoolValues) throws IOException, NullException, SizeException
	{
		if(BoolValues == null) throw new NullException("BoolValues");
		writeByte(BoolTag.getTagList(BoolValues));
	}
	public final void readLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(LongArray == null) throw new NullException("LongArray");
		readLongArray(LongArray, StartIndex, LongArray.length);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		writeByteArray(ByteArray, StartIndex, ByteArray.length);
	}
	public final void writeLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(LongArray == null) throw new NullException("LongArray");
		writeLongArray(LongArray, StartIndex, LongArray.length);
	}
	public void readStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringList == null) throw new NullException("StringList");
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(StartIndex > StringList.size()) throw new NumberException("StartIndex", StartIndex, 0, StringList.size());
		byte a = readByte();
		int b = 0, c = StartIndex;
		switch(a)
		{
		case 0:
			b = readUnsignedByte();
			break;
		case 1:
			b = readUnsignedShort();
			break;
		case 2:
			b = readInteger();
			break;
		default:
			b = 0;
			break;
		}
		if(StringList instanceof Vector<?>) ((Vector<String>)StringList).ensureCapacity(a);
		if(StringList instanceof ArrayList<?>) ((ArrayList<String>)StringList).ensureCapacity(a);
		for(; b > 0; b--) StringList.add(c++, readString());
	}
	public final void readShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		readShortArray(ShortArray, StartIndex, ShortArray.length);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		readFloatArray(FloatArray, StartIndex, FloatArray.length);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		writeShortArray(ShortArray, StartIndex, ShortArray.length);
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		writeFloatArray(FloatArray, StartIndex, FloatArray.length);
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		readIntegerArray(IntegerArray, StartIndex, IntegerArray.length);
	}
	public void readLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(LongArray == null) throw new NullException("LongArray");
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(StartIndex > Length) throw new NumberException("StartIndex", StartIndex, 0, Length);
		if(Length > LongArray.length) throw new NumberException("Length", Length, StartIndex, LongArray.length);
		for(int a = StartIndex; a < Length; a++) LongArray[a] = readLong();
	}
	public final void readStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringArray == null) throw new NullException("StringArray");
		readStringArray(StringArray, StartIndex, StringArray.length);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		readDoubleArray(DoubleArray, StartIndex, DoubleArray.length);
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		writeIntegerArray(IntegerArray, StartIndex, IntegerArray.length);
	}
	public final void writeStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringArray == null) throw new NullException("StringArray");
		writeStringArray(StringArray, StartIndex, StringArray.length);
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		writeDoubleArray(DoubleArray, StartIndex, DoubleArray.length);
	}
	public void writeByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(streamOutput == null) notOutputStream();
		if(ByteArray == null) throw new NullException("ByteArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		streamOutput.write(ByteArray, StartIndex, Length);
	}
	public void writeLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(LongArray == null) throw new NullException("LongArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) writeLong(LongArray[a]);
	}
	public final void writeStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringList == null) throw new NullException("StringList");
		writeStringList(StringList, StartIndex, StringList.size());
	}
	public void readShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) ShortArray[a] = readShort();
	}
	public void readFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) FloatArray[a] = readFloat();
	}
	public void writeShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) writeShort(ShortArray[a]);
	}
	public void writeFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) writeFloat(FloatArray[a]);
	}
	public void readIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) IntegerArray[a] = readInteger();
	}
	public void writeIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) writeInteger(IntegerArray[a]);
	}
	public void readStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(StringArray == null) throw new NullException("StringArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) StringArray[a] = readString();
	}
	public void readDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) DoubleArray[a] = readDouble();
	}
	public void writeStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(StringArray == null) throw new NullException("StringArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) writeString(StringArray[a]);
	}
	public void writeDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		for(int a = StartIndex; a < Length; a++) writeDouble(DoubleArray[a]);
	}
	public void writeStringList(List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(StringList == null) throw new NullException("StringList");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StringList.size()) throw new NumberException("Length", Length, StartIndex, StringList.size());
		if(Length < 255)
		{
			streamOutputBuffer[0] = 0;
			streamOutputBuffer[1] = (byte)Length;
			streamOutput.write(streamOutputBuffer, 0, 2);
		}
		else if(Length < Constants.MAX_USHORT_SIZE)
		{
			streamOutputBuffer[0] = 1;
			streamOutputBuffer[0] = (byte)((Length >> 8) & 0xFF);
			streamOutputBuffer[1] = (byte)(Length & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 3);
		}
		else
		{
			streamOutputBuffer[0] = 2;
			streamOutputBuffer[1] = (byte)((Length >> 24) & 0xFF);
			streamOutputBuffer[2] = (byte)((Length >> 16) & 0xFF);
			streamOutputBuffer[3] = (byte)((Length >> 8) & 0xFF);
			streamOutputBuffer[4] = (byte)(Length & 0xFF);
			streamOutput.write(streamOutputBuffer, 0, 5);
		}
		for(int a = 0; a < Length; a++) writeString(StringList.get(a));
	}

	public final boolean isStreamInput()
	{
		return streamInput != null;
	}
	public final boolean isStreamOutput()
	{
		return streamOutput != null;
	}
	public boolean canProcessReport(byte ReportLevel)
	{
		return true;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Stream && (streamInput != null && ((Stream)CompareObject).streamInput != null ?
			   ((Stream)CompareObject).streamInput.hashCode() == streamInput.hashCode() :
			   streamOutput != null && ((Stream)CompareObject).streamOutput != null && streamOutput.hashCode() == ((Stream)CompareObject).hashCode());
	}
	public final boolean readBoolean() throws IOException
	{
		return readUnsignedByte() == 1;
	}

	public final byte readByte() throws IOException
	{
		return (byte)readUnsignedByte();
	}

	public final char readChar() throws IOException
	{
		return (char)readShort();
	}

	public final short readShort() throws IOException
	{
		return (short)readUnsignedShort();
	}

	public int hashCode()
	{
		return (streamInput != null ? streamInput.hashCode() : 0) + (streamOutput != null ? streamOutput.hashCode() : 0);
	}
	public int readInteger() throws IOException
	{
		readBuffer(4);
		return (((streamInputBuffer[0] & 0xFF) << 24) | ((streamInputBuffer[1] & 0xFF) << 16) |
			   ((streamInputBuffer[2] & 0xFF) << 8) | (streamInputBuffer[3] & 0xFF));
	}
	public int readUnsignedByte() throws IOException
	{
		readBuffer(1);
		return streamInputBuffer[0] & 0xFF;
	}
	public int readUnsignedShort() throws IOException
	{
		readBuffer(2);
		return ((streamInputBuffer[0] & 0xFF) << 8) | (streamInputBuffer[1] & 0xFF);
	}
	public final int getAvailable() throws IOException
	{
		if(streamInput == null) notInputStream();
		return streamInput.available();
	}
	public final int readByteArray(byte[] ByteArray) throws IOException, NullException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		return readByteArray(ByteArray, 0, ByteArray.length);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		return readByteArray(ByteArray, StartIndex, ByteArray.length);
	}
	public int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(streamInput == null) notInputStream();
		if(ByteArray == null) throw new NullException("ByteArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		return readBuffer(ByteArray, StartIndex, Length, true);
	}
	public int readDirectByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(streamInput == null) notInputStream();
		if(ByteArray == null) throw new NullException("ByteArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		return streamInput.read(ByteArray, StartIndex, Length);
	}

	public final float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInteger());
	}

	public long readLong() throws IOException
	{
		readBuffer(8);
		return (((long)streamInputBuffer[0] << 56) + ((long)(streamInputBuffer[1] & 255) << 48) +
				((long)(streamInputBuffer[2] & 255) << 40) + ((long)(streamInputBuffer[3] & 255) << 32) +
				((long)(streamInputBuffer[4] & 255) << 24) + ((streamInputBuffer[5] & 255) << 16) +
				((streamInputBuffer[6] & 255) <<  8) + ((streamInputBuffer[7] & 255) <<  0));
	}

	public final double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	public final String toString()
	{
		return "Stream(" + streamType() + ")";
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
		if(a %2 == 0 && a < streamInputBuffer.length)
		{
			readBuffer(b);
			return new String(streamInputBuffer, 0, b);
		}
		if(a %2 == 0)
		{
			byte[] c = new byte[b];
			readBuffer(c, 0, c.length);
			return new String(c, 0, c.length);
		}
		char[] d = new char[b];
		for(; b > 0; b--)
			d[d.length - b] = (char)readShort();
		return new String(d);
	}

	public final InputStream getStreamInput() throws IOException
	{
		if(streamInput == null) notInputStream();
		return streamInput;
	}

	public final OutputStream getStreamOutput() throws IOException
	{
		if(streamOutput == null) notOutputStream();
		return streamOutput.getStream();
	}

	public final BoolTag readBooleanTags() throws IOException
	{
		return new BoolTag(readUnsignedByte());
	}

	public long[] readLongArray(int ReadAmount) throws IOException, NumberException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		long[] a = new long[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readLong();
		return a;
	}

	public byte[] readByteArray(int ReadAmount) throws IOException, NumberException
	{
		if(streamInput == null) notInputStream();
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		byte[] a = new byte[ReadAmount];
		readBuffer(a, 0, a.length);
		return a;
	}

	public short[] readShortArray(int ReadAmount) throws IOException, NumberException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		short[] a = new short[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readShort();
		return a;
	}

	public int[] readIntegerArray(int ReadAmount) throws IOException, NumberException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		int[] a = new int[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readInteger();
		return a;
	}

	public float[] readFloatArray(int ReadAmount) throws IOException, NumberException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		float[] a = new float[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readFloat();
		return a;
	}

	public double[] readDoubleArray(int ReadAmount) throws IOException, NumberException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		double[] a = new double[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readDouble();
		return a;
	}

	public String[] readStringArray(int ReadAmount) throws IOException,	NumberException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		String[] a = new String[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readString();
		return a;
	}

	public static final void writeInetAddress(Streamable OutStream, InetAddress Address) throws NullException, IOException
	{
		writeInetAddress(null, OutStream, Address);
	}
	public static final void fillFromStream(InputStream InStream, OutputStream OutStream) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		if(OutStream == null) throw new NullException("OutStream");
		byte[] a = new byte[8192];
		for(int b = 0; (b = InStream.read(a)) > -1;) OutStream.write(a, 0, b);
		a = null;
	}
	public static final void readEncryptedFileFromStream(String FilePath, Streamable InStream) throws StringException, IOException
	{
		FilePacket a = (FilePacket)Item.getNextItemByID(InStream, FilePacket.ITEM_CLASS_ID);
		if(FilePath != null) a.setDestination(FilePath);
		a.readFileFromStream(InStream);
	}
	public static final void writeInetAddress(Encoder Encoder, Streamable OutStream, InetAddress Address) throws NullException, IOException
	{
		if(Address == null) throw new NullException("Address");
		if(OutStream == null) throw new NullException("OutStream");
		byte[] a = Address.getAddress();
		if(Encoder != null)
		{
			Encoder.writeBoolean(OutStream, a.length == 4);
			Encoder.writeByteArray(OutStream, a, 0, a.length);
		}
		else
		{
			OutStream.writeBoolean(a.length == 4);
			OutStream.writeByteArray(a, 0, a.length);
		}
	}
	public static final void writeFileToEncryptedStream(String FilePath, String FileDestination, Streamable OutStream) throws NullException, StringException, IOException
	{
		FilePacket a = new FilePacket(FilePath);
		a.writeStream(OutStream);
		a.writeFileToStream(OutStream);
	}

	public static final boolean fileExists(String FilePath) throws NullException, StringException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Checking for existance of file \"" + FilePath + "\"");
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).exists();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean FileIsFile(String FilePath) throws NullException, StringException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Checking for file type of file \"" + FilePath + "\"");
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).isFile();
		}
		catch (SecurityException E) { return false; }
	}
	public static final boolean deleteFile(String FilePath) throws NullException, StringException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to delete file \"" + FilePath + "\"");
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).delete();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean isDirectory(String FilePath) throws NullException, StringException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Checking for file type of file \"" + FilePath + "\"");
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).isDirectory();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean canReadFile(String FilePath) throws NullException, StringException
	{
		Reporter.debug(Reporter.REPORTER_IO, "Checking for permissions of file \"" + FilePath + "\"");
		try
		{
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).canRead();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean canWriteFile(String FilePath) throws NullException, StringException
	{
		Reporter.debug(Reporter.REPORTER_IO, "Checking for permissions of file \"" + FilePath + "\"");
		try
		{
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).canWrite();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean canExecuteFile(String FilePath) throws NullException, StringException
	{
		Reporter.debug(Reporter.REPORTER_IO, "Checking for permissions of file \"" + FilePath + "\"");
		try
		{
			return new File(Constants.CURRENT_OS.phrasePath(FilePath)).canExecute();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean markFileForDelete(String FilePath) throws NullException, StringException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to mark file for delete \"" + FilePath + "\"");
			new File(Constants.CURRENT_OS.phrasePath(FilePath)).deleteOnExit();
			return true;
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean canReadWriteExecute(String FilePath) throws NullException, StringException
	{
		File a = new File(Constants.CURRENT_OS.phrasePath(FilePath));
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Checking for permissions of file \"" + FilePath + "\"");
			return a.canExecute() && a.canRead() && a.canWrite();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean createFileDirectories(String FilePath) throws NullException, StringException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to create directories for file \"" + FilePath + "\"");
			return new File(Constants.CURRENT_OS.phrasePath(FilePath).substring(0, FilePath.lastIndexOf(Constants.CURRENT_OS.systemSeperator))).mkdirs();
		}
		catch (SecurityException Exception) { }
		return false;
	}
	public static final boolean copyFile(String FilePath, String NewFilePath) throws NullException, StringException, PermissionException
	{
		return copyFile(FilePath, NewFilePath, false);
	}
	public static final boolean moveFile(String FilePath, String NewFilePath) throws NullException, StringException, PermissionException
	{
		return moveFile(FilePath, NewFilePath, false);
	}
	public static final boolean copyFile(String FilePath, String NewFilePath, boolean Overwrite) throws NullException, StringException, PermissionException
	{
		if(fileExists(NewFilePath) && !Overwrite) return false;
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to copy \"" + FilePath + "\" to \"" + NewFilePath + "\"");
			Stream a = getFileInputStream(FilePath), b = Stream.getFileOutputStream(NewFilePath, true);
			b.readFromStream(a);
			b.close();
			a.close();
			return true;
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "FILE_OP_COPY", Exception);
		}
		return false;
	}
	public static final boolean moveFile(String FilePath, String NewFilePath, boolean Overwrite) throws NullException, StringException, PermissionException
	{
		if(copyFile(FilePath, NewFilePath, Overwrite)) return deleteFile(FilePath);
		return false;
	}
	public static final boolean renameFile(String FilePath, String NewFilePath) throws NullException, StringException, PermissionException
	{
		if(NewFilePath.indexOf(Constants.CURRENT_OS.systemSeperator) >= 0) return moveFile(FilePath, NewFilePath);
		File a = new File(Constants.CURRENT_OS.phrasePath(FilePath)), b = new File(a.getParent(), NewFilePath);
		try
		{
			if(a.renameTo(b))
			{
				Reporter.debug(Reporter.REPORTER_IO, "Renamed file \"" + FilePath + "\" to \"" + NewFilePath + "\"");
				return true;
			}
		}
		catch (SecurityException Exception) { }
		return moveFile(a.getAbsolutePath(), b.getAbsolutePath());
	}
	public static final boolean killProcess(int ProcessPID) throws NumberException, IOException
	{
		return Constants.CURRENT_OS.killProcess(ProcessPID, true);
	}
	public static final boolean killProcess(String ProcessName) throws NullException, StringException, IOException
	{
		return Constants.CURRENT_OS.killProcess(ProcessName, true);
	}

	public static final int getByteArrayIndex(byte[] SearchArray, byte[] FindBlock) throws NullException, SizeException
	{
		return 00;
	}

	public static final long getDriveFreeSpace(File FileOnDrive) throws NullException
	{
		if(FileOnDrive == null) throw new NullException("FileOnDrive");
		return FileOnDrive.getFreeSpace();
	}
	public static final long getDriveFreeSpace(int DriveIndex) throws NumberException
	{
		if(DriveIndex < 0) throw new NumberException("DriveIndex", DriveIndex, false);
		if(SYSTEM_DRIVES == null) SYSTEM_DRIVES = File.listRoots();
		if(DriveIndex > SYSTEM_DRIVES.length) throw new NumberException("DriveIndex", DriveIndex, 0, SYSTEM_DRIVES.length);
		return SYSTEM_DRIVES[DriveIndex].getFreeSpace();
	}
	public static final long getDriveTotalSpace(int DriveIndex) throws NumberException
	{
		if(DriveIndex < 0) throw new NumberException("DriveIndex", DriveIndex, false);
		if(SYSTEM_DRIVES == null) SYSTEM_DRIVES = File.listRoots();
		if(DriveIndex > SYSTEM_DRIVES.length) throw new NumberException("DriveIndex", DriveIndex, 0, SYSTEM_DRIVES.length);
		return SYSTEM_DRIVES[DriveIndex].getTotalSpace();
	}
	public static final long getDriveTotalSpace(File FileOnDrive) throws NullException
	{
		if(FileOnDrive == null) throw new NullException("FileOnDrive");
		return FileOnDrive.getTotalSpace();
	}
	public static final long getDriveUseableSpace(File FileOnDrive) throws NullException
	{
		if(FileOnDrive == null) throw new NullException("FileOnDrive");
		return FileOnDrive.getUsableSpace();
	}
	public static final long getDriveUseableSpace(int DriveIndex) throws NumberException
	{
		if(DriveIndex < 0) throw new NumberException("DriveIndex", DriveIndex, false);
		if(SYSTEM_DRIVES == null) SYSTEM_DRIVES = File.listRoots();
		if(DriveIndex > SYSTEM_DRIVES.length) throw new NumberException("DriveIndex", DriveIndex, 0, SYSTEM_DRIVES.length);
		return SYSTEM_DRIVES[DriveIndex].getUsableSpace();
	}
	public static final long getDriveFreeSpace(String DrivePath) throws NullException, StringException
	{
		if(DrivePath == null) throw new NullException("DrivePath");
		if(DrivePath.isEmpty()) throw new StringException("DrivePath");
		if(SYSTEM_DRIVES == null) SYSTEM_DRIVES = File.listRoots();
		for(byte a = 0; a < SYSTEM_DRIVES.length; a++)
			if(SYSTEM_DRIVES[a].getName().contains(DrivePath)) return SYSTEM_DRIVES[a].getFreeSpace();
		return -1;
	}
	public static final long getDriveTotalSpace(String DrivePath) throws NullException, StringException
	{
		if(DrivePath == null) throw new NullException("DrivePath");
		if(DrivePath.isEmpty()) throw new StringException("DrivePath");
		if(SYSTEM_DRIVES == null) SYSTEM_DRIVES = File.listRoots();
		for(byte a = 0; a < SYSTEM_DRIVES.length; a++)
			if(SYSTEM_DRIVES[a].getName().contains(DrivePath)) return SYSTEM_DRIVES[a].getTotalSpace();
		return -1;
	}
	public static final long getDriveUsableSpace(String DrivePath) throws NullException, StringException
	{
		if(DrivePath == null) throw new NullException("DrivePath");
		if(DrivePath.isEmpty()) throw new StringException("DrivePath");
		if(SYSTEM_DRIVES == null) SYSTEM_DRIVES = File.listRoots();
		for(byte a = 0; a < SYSTEM_DRIVES.length; a++)
			if(SYSTEM_DRIVES[a].getName().contains(DrivePath)) return SYSTEM_DRIVES[a].getUsableSpace();
		return -1;
	}

	public static final String createRandomName()
	{
		return createRandomName(8);
	}
	public static final String createRandomName(int Length) throws NumberException
	{
		if(Length <= 0) throw new NumberException("Length", Length, true);
		if(Length > Short.MAX_VALUE) throw new NumberException("Length", Length, 0, Short.MAX_VALUE);
		StringBuilder a = new StringBuilder(Length);
		for(short c = 0; c < Length; c++)
		{
			switch(Constants.RNG.nextInt(3))
			{
			case 0:
				a.append((char)(65 + Constants.RNG.nextInt(26)));
				break;
			case 1:
				a.append((char)(97 + Constants.RNG.nextInt(26)));
				break;
			case 2:
				a.append((char)(48 + Constants.RNG.nextInt(10)));
				break;
			}
		}
		return a.toString();
	}
	public static final String getFileName(String FilePath) throws NullException, StringException
	{
		return new File(Constants.CURRENT_OS.phrasePath(FilePath)).getName();
	}

	public static final File createTempFile()
	{
		return createTempFile(createRandomName(7), null, null);
	}
	public static final File createTempDirectory()
	{
		try
		{
			File a = File.createTempFile(createRandomName(7), Constants.EMPTY_STRING, null);
			a.delete();
			a.mkdir();
			Reporter.debug(Reporter.REPORTER_IO, "Created temp directory \"" + a.getAbsolutePath() + "\"");
			return a;
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Temp directory!", Exception);
		}
		catch (SecurityException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Temp directory!", Exception);
		}
		return null;
	}
	public static final File getFileParent(File FilePath) throws NullException
	{
		if(FilePath == null) throw new NullException("FilePath");
		return FilePath.getParentFile();
	}
	public static final File getFileParent(String FilePath) throws NullException, StringException
	{
		return getFileParent(Constants.CURRENT_OS.phrasePath(FilePath));
	}
	public static final File createTempFile(String FileName) throws NullException, FormatException
	{
		return createTempFile(FileName, null, null);
	}
	public static final File createTempFile(String FileName, String FileSuffix) throws NullException, FormatException
	{
		return createTempFile(FileName, FileSuffix, null);
	}
	public static final File createTempFile(String FileName, String FileSuffix, String FileDirectory) throws NullException, FormatException
	{
		if(FileName == null) throw new NullException("FilePath");
		if(FileName.length() < 3) throw new FormatException("\"FileName\" must be at least 3 characters!");
		try
		{
			File a = File.createTempFile(FileName, FileSuffix, FileDirectory != null ? new File(FileDirectory) : null);
			Reporter.debug(Reporter.REPORTER_IO, "Created temp file \"" + a.getAbsolutePath() + "\"");
			return a;
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Temp file!", Exception);
		}
		catch (SecurityException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Temp file!", Exception);
		}
		return null;
	}

	public static final Stamp getModifiedDate(File FilePath) throws NullException
	{
		if(FilePath == null) throw new NullException("FilePath");
		return new Stamp(FilePath.lastModified());
	}
	public static final Stamp getModifiedDate(String FilePath) throws NullException, StringException
	{
		return getModifiedDate(new File(Constants.CURRENT_OS.phrasePath(FilePath)));
	}

	public static final File[] getAllDrives()
	{
		return getAllDrives(false);
	}
	public static final File[] getAllDrives(boolean RefreshDrives)
	{
		if(SYSTEM_DRIVES == null || RefreshDrives)
		{
			Reporter.debug(Reporter.REPORTER_IO, "Gathering system drive information");
			SYSTEM_DRIVES = File.listRoots();
		}
		return SYSTEM_DRIVES;
	}
	public static final File[] getFilesInDir(String DirectoryPath) throws NullException, StringException
	{
		try
		{
			return new File(Constants.CURRENT_OS.phrasePath(DirectoryPath)).listFiles();
		}
		catch (SecurityException Exception) { }
		return null;
	}

	public static final InetAddress readInetAddress(Streamable InStream) throws NullException, IOException
	{
		return readInetAddress(null, InStream);
	}
	public static final InetAddress readInetAddress(Encoder Encoder, Streamable InStream) throws NullException, IOException
	{
		if(InStream == null) throw new NullException("InStream");
		byte[] a = null;
		if(Encoder != null) a = Encoder.readByteArray(InStream, Encoder.readBoolean(InStream) ? 4 : 16);
		else a = InStream.readByteArray(InStream.readBoolean() ? 4 : 16);
		if(a != null) try
		{
			return InetAddress.getByAddress(a);
		}
		catch (UnknownHostException Exception) { }
		return null;
	}

	public static final Stream createSecureStream(InputStream StreamInput) throws NullException, PermissionException, IOException
	{
		return new GZIPStream(StreamInput);
	}
	public static final Stream createSecureStream(OutputStream StreamOutput) throws NullException, PermissionException, IOException
	{
		return new GZIPStream(StreamOutput);
	}

	public static final Stream getFileInputStream(String FilePath) throws IOException, NullException, StringException, PermissionException
	{
		return getFileInputStream(FilePath, false);
	}
	public static final Stream getFileOutputStream(String FilePath) throws IOException, NullException, StringException, PermissionException
	{
		return getFileOutputStream(FilePath, false, false);
	}
	public static final Stream getZIPFileInputStream(String FilePath) throws IOException, NullException, StringException, PermissionException
	{
		return getZIPFileInputStream(FilePath, false);
	}
	public static final Stream getZIPFileOutputStream(String FilePath) throws IOException, NullException, StringException, PermissionException
	{
		return getZIPFileOutputStream(FilePath, false, false);
	}
	public static final Stream getInternetFileOutputStream(String InternetURL) throws IOException, NullException, StringException, PermissionException
	{
		if(InternetURL == null) throw new NullException("InternetURL");
		if(InternetURL.isEmpty()) throw new StringException("InternetURL");
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to get an InputStream from URL \"" + InternetURL + "\"");
			return new DataStream(new URL(InternetURL).openStream());
		}
		catch (MalformedURLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create URL Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create URL Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}

	public static final Stream getFileInputStream(String FilePath, boolean CreateNew) throws IOException, NullException, StringException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		try
		{
			File a = new File(Constants.CURRENT_OS.phrasePath(FilePath));
			if(!a.exists() && CreateNew)
			{
				new File(a.getAbsolutePath().substring(0, a.getAbsolutePath().lastIndexOf(Constants.CURRENT_OS.systemSeperator))).mkdirs();
				a.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + a.getAbsolutePath() + "\" for Stream");
			}
			if(a.exists())
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an InputStream from file \"" + a.getAbsolutePath() + "\"");
				return new DataStream(new FileInputStream(a));
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}
	public static final Stream getFileOutputStream(String FilePath, boolean CreateNew) throws IOException, NullException, StringException, PermissionException
	{
		return getFileOutputStream(FilePath, CreateNew, false);
	}
	public static final Stream getZIPFileInputStream(String FilePath, boolean CreateNew) throws IOException, NullException, StringException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		try
		{
			File a = new File(Constants.CURRENT_OS.phrasePath(FilePath));
			if(!a.exists() && CreateNew)
			{
				new File(a.getAbsolutePath().substring(0, a.getAbsolutePath().lastIndexOf(Constants.CURRENT_OS.systemSeperator))).mkdirs();
				a.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + a.getAbsolutePath() + "\"");
			}
			if(a.exists())
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an GZIP InputStream from file \"" + a.getAbsolutePath() + "\"");
				return new GZIPStream(new FileInputStream(a));
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}
	public static final Stream getZIPFileOutputStream(String FilePath, boolean CreateNew) throws IOException, NullException, StringException, PermissionException
	{
		return getZIPFileOutputStream(FilePath, CreateNew, false);
	}
	public static final Stream getFileOutputStream(String FilePath, boolean CreateNew, boolean AppendData) throws IOException, NullException, StringException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		try
		{
			File a = new File(Constants.CURRENT_OS.phrasePath(FilePath));
			if(!a.exists() && CreateNew)
			{
				new File(a.getAbsolutePath().substring(0, a.getAbsolutePath().lastIndexOf(File.separatorChar))).mkdirs();
				a.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + a.getAbsolutePath() + "\" for Stream");
			}
			if(a.exists())
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an OutputStream from file \"" + a.getAbsolutePath() + "\"");
				return new DataStream(new FileOutputStream(a, AppendData));
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}
	public static final Stream getZIPFileOutputStream(String FilePath, boolean CreateNew, boolean AppendData) throws IOException, NullException, StringException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		try
		{
			File a = new File(Constants.CURRENT_OS.phrasePath(FilePath));
			if(!a.exists() && CreateNew)
			{
				new File(a.getAbsolutePath().substring(0, a.getAbsolutePath().lastIndexOf(File.separatorChar))).mkdirs();
				a.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + a.getAbsolutePath() + "\"!");
			}
			if(a.exists() && !AppendData)
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an GZIP OutputStream from file \"" + a.getAbsolutePath() + "\"");
				return new GZIPStream(new FileOutputStream(a));
			}
			else if(AppendData && a.exists())
			{
				Stream b = new DataStream(new GZIPInputStream(new FileInputStream(a))),
						   c = new DataStream(new GZIPOutputStream(new FileOutputStream(a)));
				b.writeToStream(c);
				Reporter.debug(Reporter.REPORTER_IO, "Created an GZIP OutputStream from file \"" + a.getAbsolutePath() + "\"");
				return c;
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}

	public static final Stream getFileInputStream(File FilePath) throws IOException, NullException, PermissionException
	{
		return getFileInputStream(FilePath, false);
	}
	public static final Stream getFileOutputStream(File FilePath) throws IOException, NullException, PermissionException
	{
		return getFileOutputStream(FilePath, false, false);
	}
	public static final Stream getZIPFileInputStream(File FilePath) throws IOException, NullException, PermissionException
	{
		return getZIPFileInputStream(FilePath, false);
	}
	public static final Stream getZIPFileOutputStream(File FilePath) throws IOException, NullException, PermissionException
	{
		return getZIPFileOutputStream(FilePath, false, false);
	}
	public static final Stream getFileInputStream(File FilePath, boolean CreateNew) throws IOException, NullException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		try
		{
			if(!FilePath.exists() && CreateNew)
			{
				new File(FilePath.getAbsolutePath().substring(0, FilePath.getAbsolutePath().lastIndexOf(Constants.CURRENT_OS.systemSeperator))).mkdirs();
				FilePath.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + FilePath.getAbsolutePath() + "\" for Stream");
			}
			if(FilePath.exists())
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an InputStream from file \"" + FilePath.getAbsolutePath() + "\"");
				return new DataStream(new FileInputStream(FilePath));
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}
	public static final Stream getFileOutputStream(File FilePath, boolean CreateNew) throws IOException, NullException, PermissionException
	{
		return getFileOutputStream(FilePath, CreateNew, false);
	}
	public static final Stream getZIPFileInputStream(File FilePath, boolean CreateNew) throws IOException, NullException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		try
		{
			if(!FilePath.exists() && CreateNew)
			{
				new File(FilePath.getAbsolutePath().substring(0, FilePath.getAbsolutePath().lastIndexOf(Constants.CURRENT_OS.systemSeperator))).mkdirs();
				FilePath.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + FilePath.getAbsolutePath() + "\"");
			}
			if(FilePath.exists())
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an GZIP InputStream from file \"" + FilePath.getAbsolutePath() + "\"");
				return new GZIPStream(new FileInputStream(FilePath));
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}
	public static final Stream getZIPFileOutputStream(File FilePath, boolean CreateNew) throws IOException, NullException, PermissionException
	{
		return getZIPFileOutputStream(FilePath, CreateNew, false);
	}
	public static final Stream getFileOutputStream(File FilePath, boolean CreateNew, boolean AppendData) throws IOException, NullException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		try
		{
			if(!FilePath.exists() && CreateNew)
			{
				new File(FilePath.getAbsolutePath().substring(0, FilePath.getAbsolutePath().lastIndexOf(File.separatorChar))).mkdirs();
				FilePath.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + FilePath.getAbsolutePath() + "\" for Stream");
			}
			if(FilePath.exists())
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an OutputStream from file \"" + FilePath.getAbsolutePath() + "\"");
				return new DataStream(new FileOutputStream(FilePath, AppendData));
			}
			Reporter.error(Reporter.REPORTER_IO, "Stream creation failure!");
			throw new IOException("Stream creation failure!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}
	public static final Stream getZIPFileOutputStream(File FilePath, boolean CreateNew, boolean AppendData) throws IOException, NullException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		try
		{
			if(!FilePath.exists() && CreateNew)
			{
				new File(FilePath.getAbsolutePath().substring(0, FilePath.getAbsolutePath().lastIndexOf(File.separatorChar))).mkdirs();
				FilePath.createNewFile();
				Reporter.debug(Reporter.REPORTER_IO, "Created file \"" + FilePath.getAbsolutePath() + "\"!");
			}
			if(FilePath.exists() && !AppendData)
			{
				Reporter.debug(Reporter.REPORTER_IO, "Created an GZIP OutputStream from file \"" + FilePath.getAbsolutePath() + "\"");
				return new GZIPStream(new FileOutputStream(FilePath));
			}
			else if(AppendData && FilePath.exists())
			{
				Stream b = new DataStream(new GZIPInputStream(new FileInputStream(FilePath))),
						   c = new DataStream(new GZIPOutputStream(new FileOutputStream(FilePath)));
				b.writeToStream(c);
				Reporter.debug(Reporter.REPORTER_IO, "Created an GZIP OutputStream from file \"" + FilePath.getAbsolutePath() + "\"");
				return c;
			}
			Reporter.error(Reporter.REPORTER_IO, "This FilePath does not contain a file!");
			throw new IOException("This FilePath does not contain a file!");
		}
		catch (SecurityException Exception)
		{
			if(Exception instanceof PermissionException)
				throw Exception;
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
		catch (NullPointerException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot create Stream due to error", Exception);
			throw new IOException(Exception);
		}
	}

	protected Stream(InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		if(!isSystemStream(this, StreamInput, StreamOutput))
			Security.check("io.stream." + streamType());
		if(StreamOutput == null && StreamInput == null)
		{
			throw new NullException("StreamOutput and StreamInput");
		}
		streamInputBuffer = new byte[40];
		streamOutputBuffer = new byte[40];
		streamInput = StreamInput != null ? new StreamBufferInput(StreamInput) : null;
		streamOutput = StreamOutput != null ? new StreamBufferOutput(StreamOutput) : null;
	}

	@SuppressWarnings("unused")
	protected void checkRead() throws IOException { }
	protected final void write(int Value) throws IOException
	{
		if(streamOutput == null) notOutputStream();
		streamOutput.write(Value);
	}

	protected final int readBuffer(int Number) throws IOException
	{
		return readBuffer(streamInputBuffer, 0, Number);
	}
	protected final int readBuffer(byte[] Buffer, int Number) throws IOException
	{
		return readBuffer(Buffer, 0, Number);
	}
	protected final int readBuffer(byte[] Buffer, int Start, int End) throws IOException
	{
		return readBuffer(Buffer, Start, End, false);
	}
	protected final int readBuffer(byte[] Buffer, int Start, int End, boolean NoEOF) throws IOException
	{
		System.out.println("read!!");
		if(streamInput == null) notInputStream();
		checkRead();
		int a = 0, b = 0, c = End;
		for(; a < c; )
		{
			if(streamInput.available() > 0 && streamInput.available() < c)
				c = streamInput.available();
			//System.out.println("d" + a + " aa" + streamInput.available());
			//if(streamInput.available() == 76) throw new RuntimeException();
			b = streamInput.read(Buffer, Start + a, End - a);
			//System.out.println("\nBA: Index: " + b + ", " + a + " Start: " + (Start + a) + " End: " + (End - a));
			//System.out.println("BR: > " + streamInput.available() + " : " + a);
			/*if(streamInput.available() <= 0)
			{
				System.out.println("HIT AVL!:");
				if(a == 0) return -1;
				return a;
			}*/
			if(b < 0)
			{
				if(NoEOF)
				{
					// This should work,
					// Lets watch this just in case it trun to bit us in the ass,
					// Should act like native InputStream::read so noobs can use it correctly (adoption rates?)
					a--;
					break;
				}
				throw new EOFException();
			}
			a += b;
		}
		return a;
	}

	protected abstract String streamType();

	protected final Stream clone() throws CloneException
	{
		throw new CloneException("Cannot clone a Stream!");
	}

	protected final InputStream getInternalInput() throws IOException
	{
		if(streamInput == null) notInputStream();
		return streamInput.getStream();
	}

	protected final OutputStream getInternalOutput() throws IOException
	{
		if(streamOutput == null) notOutputStream();
		return streamOutput.getStream();
	}

	protected static final void notInputStream() throws IOException
	{
		Reporter.error(Reporter.REPORTER_IO, "This does not conatin an InputStream!");
		throw new IOException("This does not conatin an InputStream!");
	}
	protected static final void notOutputStream() throws IOException
	{
		Reporter.error(Reporter.REPORTER_IO, "This does not conatin an OutputStream!");
		throw new IOException("This does not conatin an OutputStream!");
	}

	private static final boolean isSystemStream(Stream Existing, InputStream Input, OutputStream Output)
	{
		if(Input == null || Output == null) return false;
		if(!(Existing instanceof DataStream)) return false;
		if(Input.equals(System.in) && Output.equals(System.out)) return true;
		return false;
	}

	private static final Cipher createCipher()
	{
		try
		{
			return Cipher.getInstance("AES/ECB/PKCS5Padding");
		}
		catch (NoSuchPaddingException Exception) { }
		catch (NoSuchAlgorithmException Exception) { }
		return null;
	}

	static final class StreamBufferInput extends BufferedInputStream
	{
		public StreamBufferInput(InputStream StreamInput)
		{
			super(StreamInput);
		}

		protected final InputStream getStream()
		{
			return in;
		}
	}
	static final class StreamBufferOutput extends BufferedOutputStream
	{
		public StreamBufferOutput(OutputStream StreamOutput)
		{
			super(StreamOutput);
		}

		protected final OutputStream getStream()
		{
			return out;
		}
	}
}