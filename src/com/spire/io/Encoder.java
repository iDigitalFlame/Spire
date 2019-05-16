package com.spire.io;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.io.EOFException;

import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.BoolTag;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.SizeException;

import java.security.SecureRandom;

import com.spire.ex.CloneException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.ex.ReflectException;
import com.spire.ex.InternalException;
import com.spire.ex.PermissionException;

import java.lang.reflect.InvocationTargetException;

public class Encoder
{
	protected static final byte ENCODER_SIZE = 16;
	protected static final EncoderController ENCODER_LIST = new EncoderController();
	
	private static final SecureRandom ENCODER_RANDOM = new SecureRandom();
	
	private static long ENCODER_PASSCODE = (Constants.SPIRE_VERSION.hashCode() ^ 2);
	
	private final byte encoderType;
	private final byte[] encoderInBuf;
	private final byte[] encoderOutBuf;
	
	private byte encoderA;
	private byte encoderB;
	private byte encoderC;
	private byte encoderBlockIn;
	private byte encoderInIndex;
	private byte encoderOutIndex;
	private byte encoderBlockOut;
	private byte[] encoderFeeder;
	private boolean encoderInOpen;
	private boolean encoderOutOpen;
	
	public final void flushInput()
	{
		encoderInIndex = 0;
	}
	public final void shuffleByteArray(byte[] ByteArray) throws NullException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		if(ByteArray.length > 1) ByteArray[0] += encoderA;
		for(int a = 0; a < ByteArray.length; a++)
		{
			if(a %encoderA == 0) ByteArray[a] += (encoderB - a);
			else if(encoderC %a == 0) ByteArray[a] += (encoderB - encoderType);
			else if(a == encoderType) ByteArray[a] -= (encoderA + a);
			else ByteArray[a] += (a %2 == 0?encoderB/3:encoderC/5);
		}
	}
	public final void deshuffleByteArray(byte[] ByteArray) throws NullException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		if(ByteArray.length > 1) ByteArray[0] -= encoderA;
		for(int a = 0; a < ByteArray.length; a++)
		{
			if(a %encoderA == 0) ByteArray[a] -= (encoderB - a);
			else if(encoderC %a == 0) ByteArray[a] -= (encoderB - encoderType);
			else if(a == encoderType) ByteArray[a] += (encoderA + a);
			else ByteArray[a] -= (a %2 == 0?encoderB/3:encoderC/5);
		}
	}
	public final void closeInputStream(Streamable InStream) throws NullException
	{
		if(InStream == null) throw new NullException("InStream");
		encoderInOpen = false;
		encoderInIndex = 0;
		encoderBlockIn = 0;
	}
	public final void flush(Streamable OutStream) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(encoderOutIndex > 0) flushOutputBuffer(OutStream);
		encoderInIndex = 0;
		encoderOutIndex = 0;
	}
	public final void closeStream(Streamable Stream) throws IOException, NullException
	{
		if(Stream == null) throw new NullException("Stream");
		if(Stream.isStreamOutput() && encoderOutIndex > 0)
			flushOutputBuffer(Stream);
		encoderInOpen = false;
		encoderOutOpen = false;
		encoderOutIndex = 0;
		encoderInIndex = 0;
		encoderBlockIn = 0;
		encoderBlockOut = 0;
	}
	public final void flushOutput(Streamable OutStream) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(encoderOutIndex > 0) flushOutputBuffer(OutStream);
		encoderOutIndex = 0;
	}
	public final void openInputStream(Streamable InStream) throws IOException, NullException
	{
		openInputStream(InStream, true);
	}
	public final void openOutputStream(Streamable OutStream) throws IOException, NullException
	{
		openOutputStream(OutStream, true);
	}
	public final void closeOutputStream(Streamable OutStream) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(encoderOutIndex > 0) flushOutputBuffer(OutStream);
		encoderOutOpen = false;
		encoderOutIndex = 0;
		encoderBlockOut = 0;
	}
	public final void writeByte(Streamable OutStream, int ByteValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		write(OutStream, ByteValue);		
	}
	public final void writeChar(Streamable OutStream, char CharValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		writeShort(OutStream, (short)CharValue);
	}
	public final void writeLong(Streamable OutStream, long LongValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		write(OutStream, (byte)((LongValue >> 56) & 0xFF));
		write(OutStream, (byte)((LongValue >> 48) & 0xFF));
		write(OutStream, (byte)((LongValue >> 40) & 0xFF));
		write(OutStream, (byte)((LongValue >> 32) & 0xFF));
		write(OutStream, (byte)((LongValue >> 24) & 0xFF));
		write(OutStream, (byte)((LongValue >> 16) & 0xFF));
		write(OutStream, (byte)((LongValue >> 8) & 0xFF));
		write(OutStream, (byte)(LongValue & 0xFF));
	}
	public final void writeShort(Streamable OutStream, int ShortValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		write(OutStream, ((ShortValue >> 8) & 0xFF));
		write(OutStream, (ShortValue & 0xFF));
	}
	public final void writeFloat(Streamable OutStream, float FloatValue) throws IOException, NullException
	{
		writeInteger(OutStream, Float.floatToIntBits(FloatValue));
	}
	public final void writeInteger(Streamable OutStream, int IntegerValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		write(OutStream, ((IntegerValue >> 24) & 0xFF));
		write(OutStream, ((IntegerValue >> 16) & 0xFF));
		write(OutStream, ((IntegerValue >> 8) & 0xFF));
		write(OutStream, (IntegerValue & 0xFF));
	}
	public final void writeBytes(Streamable OutStream, String StringValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StringValue == null) throw new NullException("StringValue");
		outputToBuffer(OutStream, StringValue.getBytes());
	}
	public final void writeChars(Streamable OutStream, String StringValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StringValue == null) throw new NullException("StringValue");
		if(StringValue.length() %2 == 0) for(int a = 0; a < StringValue.length(); a += 2)
		{
			write(OutStream, ((StringValue.charAt(a) >> 8) & 0xFF));
			write(OutStream, (StringValue.charAt(a) & 0xFF));
			write(OutStream, ((StringValue.charAt(a + 1) >> 8) & 0xFF));
			write(OutStream, (StringValue.charAt(a + 1) & 0xFF));
		}
		else for(int b = 0; b < StringValue.length(); b++)
		{
			write(OutStream, ((StringValue.charAt(b) >> 8) & 0xFF));
			write(OutStream, (StringValue.charAt(b) & 0xFF));
		}
	}
	public final void readLongArray(Streamable InStream, long[] LongArray) throws IOException, NullException
	{
		if(LongArray == null) throw new NullException("LongArray");
		readLongArray(InStream, LongArray, 0, LongArray.length);
	}
	public final void writeStorage(Streamable OutStream, Storage Storeable) throws IOException, NullException
	{
		if(Storeable == null) throw new NullException("Storeable");
		if(OutStream == null) throw new NullException("OutStream");
		writeBoolean(OutStream, true);
		writeString(OutStream, Storeable.getClass().getName());
		Storeable.writeStorage(OutStream, this);
	}
	public final void writeDouble(Streamable OutStream, double DoubleValue) throws IOException, NullException
	{
		writeLong(OutStream, Double.doubleToLongBits(DoubleValue));
	}
	public final void writeBoolean(Streamable OutStream, boolean BoolValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		write(OutStream, BoolValue ? 1 : 0);
	}
	public final void writeString(Streamable OutStream, String StringValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StringValue == null) 
		{
			write(OutStream, -1);
			return;
		}
		else if(StringValue.isEmpty())
		{
			write(OutStream, 0);
			return;
		}
		else if(StringValue.length() < 255)
		{
			write(OutStream, 2);
			write(OutStream, StringValue.length());
		}
		else if(StringValue.length() < Constants.MAX_USHORT_SIZE)
		{
			write(OutStream, 4);
			write(OutStream, ((StringValue.length() >> 8) & 0xFF));
			write(OutStream, (StringValue.length() & 0xFF));
		}
		else
		{
			write(OutStream, 6);
			write(OutStream, ((StringValue.length() >> 24) & 0xFF)); 
			write(OutStream, ((StringValue.length() >> 16) & 0xFF)); 
			write(OutStream, ((StringValue.length() >> 8) & 0xFF)); 
			write(OutStream, (StringValue.length() & 0xFF));
		}
		outputToBuffer(OutStream, StringValue.getBytes());
	}
	public final void writeByteArray(Streamable OutStream, byte[] ByteArray) throws IOException, NullException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		writeByteArray(OutStream, ByteArray, 0, ByteArray.length);
	}
	public final void writeLongArray(Streamable OutStream, long[] LongArray) throws IOException, NullException
	{
		if(LongArray == null) throw new NullException("LongArray");
		writeLongArray(OutStream, LongArray, 0, LongArray.length);
	}	
	public final void readShortArray(Streamable InStream, short[] ShortArray) throws IOException, NullException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		readShortArray(InStream, ShortArray, 0, ShortArray.length);
	}
	public final void readFloatArray(Streamable InStream, float[] FloatArray) throws IOException, NullException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		readFloatArray(InStream, FloatArray, 0, FloatArray.length);
	}
	public final void writeToStream(Streamable InStream, Streamable OutStream) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		writeToStream(InStream, OutStream.getStreamOutput());
	}
	public final void writeShortArray(Streamable OutStream, short[] ShortArray) throws IOException, NullException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		writeShortArray(OutStream, ShortArray, 0, ShortArray.length);
	}
	public final void writeFloatArray(Streamable OutStream, float[] FloatArray) throws IOException, NullException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		writeFloatArray(OutStream, FloatArray, 0, FloatArray.length);
	}
	public final void readFromStream(Streamable OutStream, Streamable InStream) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		readFromStream(OutStream, InStream.getStreamInput());
	}
	public final void readIntegerArray(Streamable InStream, int[] IntegerArray) throws IOException, NullException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		readIntegerArray(InStream, IntegerArray, 0, IntegerArray.length);
	}
	/**
	 * Reads a non-excoded stream to an encoded stream
	 */
	public final void readFromStream(Streamable OutStream, InputStream InStream) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		if(OutStream == null) throw new NullException("OutStream");
		if(!OutStream.isStreamOutput()) notOutputStream();
		byte[] a = new byte[4096];
		for(int b = 0; (b = InStream.read(a)) > -1;) outputToBuffer(OutStream, a, 0, b);
	}
	/**
	 * Reads an encoded stream to a non-encoded stream
	 */
	public final void writeToStream(Streamable InStream, OutputStream OutStream) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		if(OutStream == null) throw new NullException("OutStream");
		if(!InStream.isStreamInput()) notInputStream();
		while(encoderInIndex != -1)
		{
			try
			{
				inputToBuffer(InStream, 1024, false);
			}
			catch (EOFException Exception) { }
			OutStream.write(encoderFeeder);
		}
	}
	public final void readDoubleArray(Streamable InStream, double[] DoubleArray) throws IOException, NullException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		readDoubleArray(InStream, DoubleArray, 0, DoubleArray.length);
	}
	public final void readStringArray(Streamable InStream, String[] StringArray) throws IOException, NullException
	{
		if(StringArray == null) throw new NullException("StringArray");
		readStringArray(InStream, StringArray, 0, StringArray.length);
	}
	public final void writeIntegerArray(Streamable OutStream, int[] IntegerArray) throws IOException, NullException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		writeIntegerArray(OutStream, IntegerArray, 0, IntegerArray.length);
	}
	public final void writeDoubleArray(Streamable OutStream, double[] DoubleArray) throws IOException, NullException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		writeDoubleArray(OutStream, DoubleArray, 0, DoubleArray.length);
	}
	public final void writeStringArray(Streamable OutStream, String[] StringArray) throws IOException, NullException
	{
		if(StringArray == null) throw new NullException("StringArray");
		writeStringArray(OutStream, StringArray, 0, StringArray.length);
	}
	public final void writeUnicodeString(Streamable OutStream, String StringValue) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StringValue == null) 
		{
			write(OutStream, -1);
			return;
		}
		else if(StringValue.isEmpty())
		{
			write(OutStream, 0);
			return;
		}
		else if(StringValue.length() < 255)
		{
			write(OutStream, 1);
			write(OutStream, StringValue.length());
		}
		else if(StringValue.length() < Constants.MAX_USHORT_SIZE)
		{
			write(OutStream, 3);
			write(OutStream, ((StringValue.length() >> 8) & 0xFF));
			write(OutStream, (StringValue.length() & 0xFF));
		}
		else
		{
			write(OutStream, 5);
			write(OutStream, ((StringValue.length() >> 24) & 0xFF)); 
			write(OutStream, ((StringValue.length() >> 16) & 0xFF)); 
			write(OutStream, ((StringValue.length() >> 8) & 0xFF)); 
			write(OutStream, (StringValue.length() & 0xFF));
		}
		writeChars(OutStream, StringValue);
	}
	public final void readStringList(Streamable InStream, List<String> StringList) throws IOException, NullException
	{
		if(StringList == null) throw new NullException("StringList");
		readStringList(InStream, StringList, StringList.size());
	}
	public final void writeStringList(Streamable OutStream, List<String> StringList) throws IOException, NullException
	{
		if(StringList == null) throw new NullException("StringList");
		writeStringList(OutStream, StringList, 0, StringList.size());
	}
	public final void writeStorageList(Streamable OutStream, List<? extends Storage> StorageList) throws IOException, NullException
	{
		if(StorageList == null) throw new NullException("StorageList");
		writeStorageList(OutStream, StorageList, 0, StorageList.size());
	}
	public final void writeBooleanTags(Streamable OutStream, boolean... BoolValues) throws IOException, NullException, SizeException
	{
		if(BoolValues == null) throw new NullException("BoolValues");
		writeByte(OutStream, BoolTag.getTagList(BoolValues));
	}
	public final void writeStorageArray(Streamable OutStream, Storage[] StorageArray) throws IOException, NullException, NumberException
	{
		if(StorageArray == null) throw new NullException("StorageArray");
		writeStorageArray(OutStream, StorageArray, 0, StorageArray.length);
	}
	public final void readLongArray(Streamable InStream, long[] LongArray, int StartIndex)throws IOException, NullException, NumberException
	{
		if(LongArray == null) throw new NullException("LongArray");
		readLongArray(InStream, LongArray, StartIndex, LongArray.length);
	}
	public final void writeByteArray(Streamable OutStream, byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		writeByteArray(OutStream, ByteArray, StartIndex, ByteArray.length);
	}
	public final void writeLongArray(Streamable OutStream, long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(LongArray == null) throw new NullException("LongArray");
		writeLongArray(OutStream, LongArray, StartIndex, LongArray.length);
	}
	public final void readShortArray(Streamable InStream, short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		readShortArray(InStream, ShortArray, StartIndex, ShortArray.length);
	}
	public final void readFloatArray(Streamable InStream, float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		readFloatArray(InStream, FloatArray, StartIndex, FloatArray.length);
	}
	public final void writeShortArray(Streamable OutStream, short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ShortArray == null) throw new NullException("ShortArray");
		writeShortArray(OutStream, ShortArray, StartIndex, ShortArray.length);
	}
	public final void writeFloatArray(Streamable OutStream, float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(FloatArray == null) throw new NullException("FloatArray");
		writeFloatArray(OutStream, FloatArray, StartIndex, FloatArray.length);
	}
	public final void readIntegerArray(Streamable InStream, int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		readIntegerArray(InStream, IntegerArray, StartIndex, IntegerArray.length);
	}
	public final void readStringArray(Streamable InStream, String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringArray == null) throw new NullException("StringArray");
		readStringArray(InStream, StringArray, StartIndex, StringArray.length);
	}
	public final void readDoubleArray(Streamable InStream, double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		readDoubleArray(InStream, DoubleArray, StartIndex, DoubleArray.length);
	}
	public final void writeStorageList(Streamable OutStream, List<? extends Storage> StorageList, int StartIndex) throws IOException, NullException
	{
		if(StorageList == null) throw new NullException("StorageList");
		writeStorageList(OutStream, StorageList, StartIndex, StorageList.size());
	}
	public final void writeIntegerArray(Streamable OutStream, int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(IntegerArray == null) throw new NullException("IntegerArray");
		writeIntegerArray(OutStream, IntegerArray, StartIndex, IntegerArray.length);
	}
	public final void writeStringArray(Streamable OutStream, String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringArray == null) throw new NullException("StringArray");
		writeStringArray(OutStream, StringArray, StartIndex, StringArray.length);
	}
	public final void writeDoubleArray(Streamable OutStream, double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(DoubleArray == null) throw new NullException("DoubleArray");
		writeDoubleArray(OutStream, DoubleArray, StartIndex, DoubleArray.length);
	}
	public final void readStringList(Streamable InStream, List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(StringList == null) throw new NullException("StringList");
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(StartIndex > StringList.size()) throw new NumberException("Length", StartIndex, 0, StringList.size());
		byte a = readByte(InStream);
		int b = 0, c = StartIndex;
		switch(a)
		{
		case 0:
			b = readUnsignedByte(InStream);
			break;
		case 1:
			b = readUnsignedShort(InStream);
			break;
		case 2:
			b = readInteger(InStream);
			break;
		default:
			b = 0;
			break;
		}
		if(StringList instanceof Vector<?>) ((Vector<String>)StringList).ensureCapacity(a);
		if(StringList instanceof ArrayList<?>) ((ArrayList<String>)StringList).ensureCapacity(a);
		for(; b > 0; b--) StringList.add(c++, readString(InStream));
	}
	public final void writeStringList(Streamable OutStream, List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StringList == null) throw new NullException("StringList");
		writeStringList(OutStream, StringList, StartIndex, StringList.size());
	}
	public final void readStorageArray(Streamable InStream, Storage[] StorageArray) throws IOException, NullException, NumberException, ReflectException
	{
		if(StorageArray == null) throw new NullException("StorageArray");
		readStorageArray(InStream, StorageArray, 0, StorageArray.length);
	}
	public final void writeStorageArray(Streamable OutStream, Storage[] StorageArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(StorageArray == null) throw new NullException("StorageArray");
		writeStorageArray(OutStream, StorageArray, StartIndex, StorageArray.length);
	}
	public final void readLongArray(Streamable InStream, long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(LongArray == null) throw new NullException("LongArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > LongArray.length) throw new NumberException("Length", Length, StartIndex, LongArray.length);
		for(int a = StartIndex; a < Length; a++) LongArray[a] = readLong(InStream);
	}
	public final void writeByteArray(Streamable OutStream, byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(ByteArray == null) throw new NullException("ByteArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > ByteArray.length) throw new NumberException("Length", Length, StartIndex, ByteArray.length);
		outputToBuffer(OutStream, ByteArray, StartIndex, Length);
	}
	public final void writeLongArray(Streamable OutStream, long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(LongArray == null) throw new NullException("LongArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > LongArray.length) throw new NumberException("Length", Length, StartIndex, LongArray.length);
		for(int a = StartIndex; a < Length; a++) writeLong(OutStream, LongArray[a]);
	}
	public final void readShortArray(Streamable InStream, short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(ShortArray == null) throw new NullException("ShortArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > ShortArray.length) throw new NumberException("Length", Length, StartIndex, ShortArray.length);
		for(int a = StartIndex; a < Length; a++) ShortArray[a] = readShort(InStream);
	}
	public final void readFloatArray(Streamable InStream, float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(FloatArray == null) throw new NullException("FloatArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > FloatArray.length) throw new NumberException("Length", Length, StartIndex, FloatArray.length);
		for(int a = StartIndex; a < Length; a++) FloatArray[a] = readFloat(InStream);
	}
	public final void writeShortArray(Streamable OutStream, short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(ShortArray == null) throw new NullException("ShortArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > ShortArray.length) throw new NumberException("Length", Length, StartIndex, ShortArray.length);
		for(int a = StartIndex; a < Length; a++) writeShort(OutStream, ShortArray[a]);
	}	
	public final void writeFloatArray(Streamable OutStream, float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(FloatArray == null) throw new NullException("FloatArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > FloatArray.length) throw new NumberException("Length", Length, StartIndex, FloatArray.length);
		for(int a = StartIndex; a < Length; a++) writeFloat(OutStream, FloatArray[a]);
	}
	public final void readIntegerArray(Streamable InStream, int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(IntegerArray == null) throw new NullException("IntegerArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > IntegerArray.length) throw new NumberException("Length", Length, StartIndex, IntegerArray.length);
		for(int a = StartIndex; a < Length; a++) IntegerArray[a] = readInteger(InStream);
	}
	public final void readStringArray(Streamable InStream, String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(StringArray == null) throw new NullException("DoubleArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StringArray.length) throw new NumberException("Length", Length, StartIndex, StringArray.length);
		for(int a = StartIndex; a < Length; a++) StringArray[a] = readString(InStream);
	}
	public final void readDoubleArray(Streamable InStream, double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(DoubleArray == null) throw new NullException("DoubleArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > DoubleArray.length) throw new NumberException("Length", Length, StartIndex, DoubleArray.length);
		for(int a = StartIndex; a < Length; a++) DoubleArray[a] = readDouble(InStream);
	}
	public final void writeIntegerArray(Streamable OutStream, int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(IntegerArray == null) throw new NullException("IntegerArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > IntegerArray.length) throw new NumberException("Length", Length, StartIndex, IntegerArray.length);
		for(int a = StartIndex; a < Length; a++) writeInteger(OutStream, IntegerArray[a]);
	}
	public final void writeStringArray(Streamable OutStream, String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StringArray == null) throw new NullException("StringArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StringArray.length) throw new NumberException("Length", Length, StartIndex, StringArray.length);
		for(int a = StartIndex; a < Length; a++) writeString(OutStream, StringArray[a]);
	}
	public final void writeDoubleArray(Streamable OutStream, double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(DoubleArray == null) throw new NullException("DoubleArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > DoubleArray.length) throw new NumberException("Length", Length, StartIndex, DoubleArray.length);
		for(int a = StartIndex; a < Length; a++) writeDouble(OutStream, DoubleArray[a]);
	}
	public final void writeStringList(Streamable OutStream, List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StringList == null) throw new NullException("StringList");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StringList.size()) throw new NumberException("Length", Length, StartIndex, StringList.size());
		if(Length < 255)
		{
			write(OutStream, 0);
			write(OutStream, Length);
		}
		else if(Length < Constants.MAX_USHORT_SIZE)
		{
			write(OutStream, 1);
			write(OutStream, ((Length >> 8) & 0xFF));
			write(OutStream, (Length & 0xFF));
		}
		else
		{
			write(OutStream, 2);
			write(OutStream, ((Length >> 24) & 0xFF)); 
			write(OutStream, ((Length >> 16) & 0xFF)); 
			write(OutStream, ((Length >> 8) & 0xFF)); 
			write(OutStream, (Length & 0xFF));
		}
		for(int a = 0; a < Length; a++) writeString(OutStream, StringList.get(a));
	}
	public final void writeStorageArray(Streamable OutStream, Storage[] StorageArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StorageArray == null) throw new NullException("StorageArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StorageArray.length) throw new NumberException("Length", Length, StartIndex, StorageArray.length);
		boolean a = true;
		if(StorageArray[StartIndex] == null)
		{
			Reporter.error(Reporter.REPORTER_IO, "There cannot be a null element in a Storage Array!");
			throw new IOException("There cannot be a null element in a Storage List!");
		}
		for(int b = StartIndex + 1; b < Length; b++)
		{
			if(StorageArray[b] == null)
			{
				Reporter.error(Reporter.REPORTER_IO, "There cannot be a null element in a Storage Array!");
				throw new IOException("There cannot be a null element in a Storage List!");
			}
			if(!StorageArray[StartIndex].getClass().equals(StorageArray[b].getClass()))
			{
				a = false;
				break;
			}
		}
		writeBoolean(OutStream, a);
		if(a) writeString(OutStream, StorageArray[StartIndex].getClass().getName());
		for(int d = StartIndex; d < Length; d++)
		{
			if(!a) writeString(OutStream, StorageArray[d].getClass().getName());
			StorageArray[d].writeStorage(OutStream, this);
		}
	}
	public final void readStorageList(Streamable InStream, List<? extends Storage> StorageList) throws IOException, NullException, NumberException, ReflectException
	{
		if(StorageList == null) throw new NullException("StorageList");
		readStorageList(InStream, StorageList, StorageList.size());
	}
	public final void readStorageArray(Streamable InStream, Storage[] StorageArray, int StartIndex) throws IOException, NullException, NumberException, ReflectException
	{
		if(StorageArray == null) throw new NullException("StorageArray");
		readStorageArray(InStream, StorageArray, StartIndex, StorageArray.length);
	}
	public final void writeStorageList(Streamable OutStream, List<? extends Storage> StorageList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(StorageList == null) throw new NullException("StorageList");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StorageList.size()) throw new NumberException("Length", Length, StartIndex, StorageList.size());
		int a = Length;
		if(a < 255)
		{
			write(OutStream, 0);
			write(OutStream, a);
		}
		else if(a < Constants.MAX_USHORT_SIZE)
		{
			write(OutStream, 1);
			write(OutStream, ((a >> 8) & 0xFF));
			write(OutStream, (a & 0xFF));
		}
		else
		{
			write(OutStream, 2);
			write(OutStream, ((a >> 24) & 0xFF)); 
			write(OutStream, ((a >> 16) & 0xFF)); 
			write(OutStream, ((a >> 8) & 0xFF)); 
			write(OutStream, (a & 0xFF));
		}
		boolean b = true;
		if(StorageList.get(StartIndex) == null)
		{
			Reporter.error(Reporter.REPORTER_IO, "There cannot be a null element in a Storage List!");
			throw new IOException("There cannot be a null element in a Storage List!");
		}
		for(int c = StartIndex + 1; c < Length; c++)
		{
			if(StorageList.get(c) == null)
			{
				Reporter.error(Reporter.REPORTER_IO, "There cannot be a null element in a Storage List!");
				throw new IOException("There cannot be a null element in a Storage List!");
			}
			if(!StorageList.get(StartIndex).getClass().equals(StorageList.get(c).getClass()))
			{
				b = false;
				break;
			}
		}
		writeBoolean(OutStream, b);
		if(b) writeString(OutStream, StorageList.get(StartIndex).getClass().getName());
		for(; a > 0; a--)
		{
			if(!b) writeString(OutStream, StorageList.get(Length - a).getClass().getName());
			StorageList.get(Length - a).writeStorage(OutStream, this);
		}
	}
	public final void readStorageArray(Streamable InStream, Storage[] StorageArray, int StartIndex, int Length) throws IOException, NullException, NumberException, ReflectException
	{
		if(InStream == null) throw new NullException("InStream");
		if(StorageArray == null) throw new NullException("StorageArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > StorageArray.length) throw new NumberException("Length", Length, StartIndex, StorageArray.length);
		boolean a = readBoolean(InStream);
		String b = null;
		Class<?> c = null;
		Storage d = null;
		if(a)
		{
			b = readString(InStream);
			if(b == null || b.isEmpty())
			{
				Reporter.error(Reporter.REPORTER_IO, "Cannot Read Storage Class Name!");
				throw new IOException("Cannot Read Storage Class Name!");
			}
		}
		for(int e = StartIndex; e < Length; e++)
		{
			if(!a)
			{
				b = readString(InStream);
				if(b == null || b.isEmpty())
				{
					Reporter.error(Reporter.REPORTER_IO, "Cannot Read Storage Class Name!");
					throw new IOException("Cannot Read Storage Class Name!");
				}
			}
			try
			{
				if(!a || c == null)
					c = Class.forName(b);
				if(!Storage.class.isAssignableFrom(c))
				{
					Reporter.error(Reporter.REPORTER_IO, "This is not a type of Storage Class!");
					throw new IOException("This is not a type of Storage Class!");
				}
				try
				{
					d = (Storage)c.getDeclaredConstructor().newInstance();
				}
				catch (SecurityException Exception)
				{
					d = (Storage)c.getConstructor().newInstance();
				}
				d.readStorage(InStream, this);
				StorageArray[e] = d;
			}
			catch (ClassNotFoundException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (InstantiationException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (IllegalAccessException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (IllegalArgumentException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (InvocationTargetException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (NoSuchMethodException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (SecurityException Exception)
			{
				throw new ReflectException(Exception);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public final void readStorageList(Streamable InStream, List<? extends Storage> StorageList, int StartIndex) throws IOException, NullException, NumberException, ReflectException
	{
		if(InStream == null) throw new NullException("InStream");
		if(StorageList == null) throw new NullException("StorageList");
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(StartIndex > StorageList.size()) throw new NumberException("StartIndex", StartIndex, 0, StorageList.size());
		byte a = readByte(InStream);
		int b = 0, c = StartIndex;
		switch(a)
		{
		case 0:
			b = readUnsignedByte(InStream);
			break;
		case 1:
			b = readUnsignedShort(InStream);
			break;
		case 2:
			b = readInteger(InStream);
			break;
		default:
			b = 0;
			break;
		}
		if(StorageList instanceof Vector<?>) ((Vector<Storage>)StorageList).ensureCapacity(a);
		if(StorageList instanceof ArrayList<?>) ((ArrayList<Storage>)StorageList).ensureCapacity(a);
		boolean d = readBoolean(InStream);
		String e = null;
		Class<?> f = null;
		Storage g = null;
		if(d)
		{
			e = readString(InStream);
			if(e == null || e.isEmpty())
			{
				Reporter.error(Reporter.REPORTER_IO, "Cannot Read Storage Class Name!");
				throw new IOException("Cannot Read Storage Class Name!");
			}
		}
		for(; b > 0; b--)
		{
			if(!d)
			{
				e = readString(InStream);
				if(e == null || e.isEmpty())
				{
					Reporter.error(Reporter.REPORTER_IO, "Cannot Read Storage Class Name!");
					throw new IOException("Cannot Read Storage Class Name!");
				}
			}
			try
			{
				if(!d || f == null)
					f = Class.forName(e);
				if(!Storage.class.isAssignableFrom(f))
				{
					Reporter.error(Reporter.REPORTER_IO, "This is not a type of Storage Class!");
					throw new IOException("This is not a type of Storage Class!");
				}
				try
				{
					g = (Storage)f.getDeclaredConstructor().newInstance();
				}
				catch (SecurityException Exception)
				{
					g = (Storage)f.getConstructor().newInstance();
				}
				g.readStorage(InStream, this);
				((List<Storage>)StorageList).add(c++, g);
			}
			catch (ClassNotFoundException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (InstantiationException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (IllegalAccessException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (IllegalArgumentException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (InvocationTargetException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (NoSuchMethodException Exception)
			{
				throw new ReflectException(Exception);
			}
			catch (SecurityException Exception)
			{
				throw new ReflectException(Exception);
			}
		}
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Encoder && ((Encoder)CompareObject).encoderA == encoderA && ((Encoder)CompareObject).encoderB == encoderB &&
			   ((Encoder)CompareObject).encoderC == encoderC && ((Encoder)CompareObject).encoderType == encoderType;
	}
	public final boolean readBoolean(Streamable InStream) throws IOException, NullException
	{
		return readUnsignedByte(InStream) == 1;
	}

	public final byte readByte(Streamable InStream) throws IOException, NullException
	{
		return (byte)readUnsignedByte(InStream);
	}

	public final char readChar(Streamable InStream) throws IOException, NullException
	{
		return (char)readUnsignedShort(InStream);
	}
	
	public final short readShort(Streamable InStream) throws IOException, NullException
	{
		return (short)readUnsignedShort(InStream);
	}
	
	public final int hashCode()
	{
		return ((((encoderA * encoderB) + encoderC) + encoderType) * (encoderBlockIn + encoderBlockOut + 1));
	}
	public final int getAvailable()
	{	
		return encoderInIndex;
	}
	public final int readInteger(Streamable InStream) throws IOException, NullException
	{
		inputToBuffer(InStream, 4, false);
		return (((encoderFeeder[0] & 0xFF) << 24) | ((encoderFeeder[1] & 0xFF) << 16) | ((encoderFeeder[2] & 0xFF) << 8) | (encoderFeeder[3] & 0xFF));
	}
	public final int readUnsignedByte(Streamable InStream) throws IOException, NullException
	{
		inputToBuffer(InStream, 1, false);
		return encoderFeeder[0] & 0xFF;
	}
	public final int readUnsignedShort(Streamable InStream) throws IOException, NullException
	{
		inputToBuffer(InStream, 2, false);
		return ((encoderFeeder[0] & 0xFF) << 8) | (encoderFeeder[1] & 0xFF);
	}
	public final int readByteArray(Streamable InStream, byte[] ByteArray) throws IOException, NullException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		return readByteArray(InStream, ByteArray, 0, ByteArray.length);
	}
	public final int readByteArray(Streamable InStream, byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(ByteArray == null) throw new NullException("ByteArray");
		return readByteArray(InStream, ByteArray, StartIndex, ByteArray.length);
	}
	public final int readByteArray(Streamable InStream, byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(InStream == null) throw new NullException("InStream");
		if(ByteArray == null) throw new NullException("ByteArray");
		if(Length < 0) throw new NumberException("Length", Length, false);
		if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
		if(Length > ByteArray.length) throw new NumberException("Length", Length, StartIndex, ByteArray.length);
		int a = inputToBuffer(InStream, Length - StartIndex, true);
		System.arraycopy(encoderFeeder, 0, ByteArray, StartIndex, encoderFeeder.length);
		return a;
	}
	
	public final float readFloat(Streamable InStream) throws IOException, NullException
	{
		return Float.intBitsToFloat(readInteger(InStream));
	}

	public final long readLong(Streamable InStream) throws IOException, NullException
	{
		inputToBuffer(InStream, 8, false);
		return (((long)encoderFeeder[0] << 56) + ((long)(encoderFeeder[1] & 255) << 48) +
				((long)(encoderFeeder[2] & 255) << 40) + ((long)(encoderFeeder[3] & 255) << 32) +
				((long)(encoderFeeder[4] & 255) << 24) + ((encoderFeeder[5] & 255) << 16) +
				((encoderFeeder[6] & 255) <<  8) + ((encoderFeeder[7] & 255) <<  0));
	}
	
	public final double readDouble(Streamable InStream) throws IOException, NullException
	{
		return Double.longBitsToDouble(readLong(InStream));
	}
	
	public final String toString()
	{
		return "Encoder(" + encoderType + ") [" + hashCode() + "]"; 
	}
	public final String readString(Streamable InStream) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		byte a = readByte(InStream);
		int b = 0;
		switch(a)
		{
		case -1:
			return null;
		case 0:
			return Constants.EMPTY_STRING;
		case 1:
			b = readUnsignedByte(InStream);
			break;
		case 2:
			b = readUnsignedByte(InStream);
			break;
		case 3:
			b = readUnsignedShort(InStream);
			break;
		case 4:
			b = readUnsignedShort(InStream);
			break;
		case 5:
			b = readInteger(InStream);
			break;
		case 6:
			b = readInteger(InStream);
			break;
		default:
			b = 0;
			break;
		}
		if(a %2 == 0)
		{
			inputToBuffer(InStream, b, false);
			return new String(encoderFeeder, 0, b);
		}
		char[] c = new char[b];
		for(; b > 0; b--)
			c[c.length - b] = (char)readShort(InStream);
		return new String(c);
	}
	
	public final BoolTag readBooleanTags(Streamable InStream) throws IOException, NullException
	{
		return new BoolTag(readUnsignedByte(InStream));
	}

	public final Storage readStorage(Streamable InStream) throws IOException, NullException, ReflectException
	{
		if(InStream == null) throw new NullException("InStream");
		boolean a = readBoolean(InStream);
		if(!a)
		{
			encoderInIndex--;
			Reporter.error(Reporter.REPORTER_IO, "Cannot Read Combined Storage Classes from a written Array!");
			throw new IOException("Cannot Read Combined Storage Classes from a written Array!");
		}
		String b = readString(InStream);
		if(b == null || b.isEmpty())
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot Read Storage Class Name!");
			throw new IOException("Cannot Read Storage Class Name!");
		}
		try
		{
			Class<?> c = Class.forName(b);
			if(!Storage.class.isAssignableFrom(c))
			{
				Reporter.error(Reporter.REPORTER_IO, "This is not a type of Storage Class!");
				throw new IOException("This is not a type of Storage Class!");
			}
			Storage d = null;
			try
			{
				d = (Storage)c.getDeclaredConstructor().newInstance();
			}
			catch (SecurityException Exception)
			{
				d = (Storage)c.getConstructor().newInstance();
			}
			d.readStorage(InStream, this);
			return d;
		}
		catch (ClassNotFoundException Exception)
		{
			throw new ReflectException(Exception);
		}
		catch (InstantiationException Exception)
		{
			throw new ReflectException(Exception);
		}
		catch (IllegalAccessException Exception)
		{
			throw new ReflectException(Exception);
		}
		catch (IllegalArgumentException Exception)
		{
			throw new ReflectException(Exception);
		}
		catch (InvocationTargetException Exception)
		{
			throw new ReflectException(Exception);
		}
		catch (NoSuchMethodException Exception)
		{
			throw new ReflectException(Exception);
		}
		catch (SecurityException Exception)
		{
			throw new ReflectException(Exception);
		}
	}
	
	public final long[] readLongArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException
	{	
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		long[] a = new long[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readLong(InStream);
		return a;
	}
	
	public final byte[] readByteArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException
	{	
		if(InStream == null) throw new NullException("InStream");
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		byte[] a = new byte[ReadAmount];
		inputToBuffer(InStream, ReadAmount, false);
		System.arraycopy(encoderFeeder, 0, a, 0, ReadAmount);
		return a;
	}
	
	public final short[] readShortArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException
	{	
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		short[] a = new short[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readShort(InStream);
		return a;
	}
	
	public final int[] readIntegerArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException
	{	
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		int[] a = new int[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readInteger(InStream);
		return a;
	}
	
	public final float[] readFloatArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException
	{	
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		float[] a = new float[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readFloat(InStream);
		return a;
	}
	
	public final double[] readDoubleArray(Streamable InStream, int ReadAmount) throws IOException,NullException,  NumberException
	{	
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		double[] a = new double[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readDouble(InStream);
		return a;
	}
	
	public final String[] readStringArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException
	{	
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		String[] a = new String[ReadAmount];
		for(int b = 0; b < a.length; b++) a[b] = readString(InStream);
		return a;
	}
	
	public final Storage[] readStorageArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException, ReflectException
	{
		if(ReadAmount <= 0) throw new NumberException("ReadAmount", ReadAmount, true);
		Storage[] a = new Storage[ReadAmount];
		readStorageArray(InStream, a, 0, a.length);
		return a;
	}
	
	public static final void setEncryptionPasscode(long EncryptionPasscode) throws PermissionException
	{
		Security.check("io.encoder.password");
	    ENCODER_PASSCODE = EncryptionPasscode;
	}
	public static final void setEncryptionPasscode(String EncryptionPasscode) throws StringException, PermissionException
	{
		Security.check("io.encoder.password");
		if(EncryptionPasscode != null && EncryptionPasscode.isEmpty()) throw new StringException("EncryptionPassword");
	    ENCODER_PASSCODE = EncryptionPasscode != null ? (EncryptionPasscode.hashCode() ^ 2) : (Constants.SPIRE_VERSION.hashCode() ^ 2);
	}
	
	public static final int generateLargePrime()
	{
		return BigInteger.probablePrime(Integer.SIZE, ENCODER_RANDOM).intValue();
	}
	
	public static final long generateHugePrime()
	{
		return BigInteger.probablePrime(Long.SIZE, ENCODER_RANDOM).longValue();
	}
	
	protected Encoder(int EncoderType) throws InternalException
	{
		if(!getClass().equals(Encoder.class) && !getClass().equals(com.spire.net.StreamEncoder.class) && !getClass().equals(KeyEncoder.class))
			throw new InternalException("Cannot extend the Encoder class!");
		encoderInBuf = new byte[ENCODER_SIZE + 1];
		encoderOutBuf = new byte[ENCODER_SIZE + 1];
		encoderType = (byte)Math.abs((byte)EncoderType);
		encoderA = (byte)(3 + ENCODER_RANDOM.nextInt(38));
		encoderB = (byte)(5 + ENCODER_RANDOM.nextInt(75));
		encoderC = (byte)((encoderB * (encoderType + 1)) / encoderA);
		ENCODER_RANDOM.setSeed(ENCODER_RANDOM.generateSeed(10));
	}
	
	protected final void refreshEncoderABC()
	{
		encoderA = (byte)(3 + ENCODER_RANDOM.nextInt(38));
		encoderB = (byte)(5 + ENCODER_RANDOM.nextInt(75));
		encoderC = (byte)((encoderB * (encoderType + 1)) / encoderA);
		ENCODER_RANDOM.setSeed(ENCODER_RANDOM.generateSeed(10));
	}
	protected final void setEncoderOpen(boolean InputOpen, boolean OutputOpen)
	{
		encoderInOpen = InputOpen;
		encoderOutOpen = OutputOpen;
	}
	protected void descrableBytes(byte[] ByteArray, byte Type, byte BlockIndex)
	{
		byte a = 0, b = 0;
		byte[] c = new byte[2];
		for(byte d = 5; d >= 0; d--)
		{
			a = (byte)Math.abs(generateBlockIndex(this, true, (byte)(Type + d), (byte)(BlockIndex + d)) % 8);
			b = (byte)Math.abs(generateBlockIndex(this, false, (byte)(Type + d), (byte)(BlockIndex + d)) % 8);
			if(a != b)
			{
				System.arraycopy(ByteArray, a * 2, c, 0, c.length);
				System.arraycopy(ByteArray, b * 2, ByteArray, a * 2, c.length);
				System.arraycopy(c, 0, ByteArray, b * 2, c.length);
			}
		}
	}
	protected final void readEncoderABC(int StartIndex, byte[] EncoderABC) throws IOException
	{
		if(EncoderABC == null)
			throw new IOException("Cannot read from a null array!");
		if(EncoderABC.length < 3)
			throw new IOException("There must be at least 3 elements in this array!");
		if(EncoderABC.length < StartIndex || StartIndex < 0 || EncoderABC.length - StartIndex < 3)
			throw new IOException("Incorrect StartIndex!");
		byte a = EncoderABC[StartIndex], b = EncoderABC[StartIndex + 1], c = EncoderABC[StartIndex + 2];
		a = (byte)(a - encoderType - (encoderType / 2) - (encoderType - (2 + getThreadByte(encoderType))));
		b = (byte)(b - getThreadByte(encoderType) - (encoderType / 3) - ((1 + getThreadByte(encoderType)) * (encoderType + 1) * 2) - (encoderType + 5));
		c = (byte)(c - (encoderType / 5) - 7 - ((encoderType + 1) * 3 + getThreadByte(encoderType)) + 8 - encoderType);
		if(encoderType %2 == 0)
		{
			encoderA = switchHalfBytes(true, b, a);
			encoderB = switchHalfBytes(true, a, b);
		}
		else
		{
			encoderA = switchHalfBytes(false, a, b);
			encoderB = switchHalfBytes(false, b, a);
		}
		encoderC = c;
	}
	protected final void writeEncoderABC(int StartIndex, byte[] EncoderABC) throws IOException
	{
		if(EncoderABC == null)
			throw new IOException("Cannot read from a null array!");
		if(EncoderABC.length < 3)
			throw new IOException("There must be at least 3 elements in this array!");
		if(EncoderABC.length < StartIndex || StartIndex < 0 || EncoderABC.length - StartIndex < 3)
			throw new IOException("Incorrect StartIndex!");
		EncoderABC[StartIndex] = (byte)(switchHalfBytes(encoderType %2 == 0, encoderA, encoderB) + encoderType + (encoderType / 2) + (encoderType - (2 + getThreadByte(encoderType))));
		EncoderABC[StartIndex + 1] = (byte)(switchHalfBytes(encoderType %2 == 0, encoderB, encoderA) + getThreadByte(encoderType) + (encoderType / 3) + ((1 + getThreadByte(encoderType)) * (encoderType + 1) * 2) + (encoderType + 5));
		EncoderABC[StartIndex + 2] = (byte)(encoderC + (encoderType / 5) + 7 + ((encoderType + 1) * 3 + getThreadByte(encoderType)) - 8 + encoderType);
	}
	protected final void readEncoderABC(Encoder Encoder, Streamable InStream) throws IOException
	{
		byte a = 0, b = 0, c = 0;
		if(Encoder == null)
		{
			a = InStream.readByte();
			b = InStream.readByte();
			c = InStream.readByte();
		}
		else
		{
			a = Encoder.readByte(InStream);
			b = Encoder.readByte(InStream);
			c = Encoder.readByte(InStream);
		}
		a = (byte)(a - encoderType - (encoderType / 2) - (encoderType - (2 + getThreadByte(encoderType))));
		b = (byte)(b - getThreadByte(encoderType) - (encoderType / 3) - ((1 + getThreadByte(encoderType)) * (encoderType + 1) * 2) - (encoderType + 5));
		c = (byte)(c - (encoderType / 5) - 7 - ((encoderType + 1) * 3 + getThreadByte(encoderType)) + 8 - encoderType);
		if(encoderType %2 == 0)
		{
			encoderA = switchHalfBytes(true, b, a);
			encoderB = switchHalfBytes(true, a, b);
		}
		else
		{
			encoderA = switchHalfBytes(false, a, b);
			encoderB = switchHalfBytes(false, b, a);
		}
		encoderC = c;
	}
	protected final void writeEncoderABC(Encoder Encoder, Streamable OutStream) throws IOException
	{
		if(Encoder == null)
		{
			OutStream.writeByte((byte)(switchHalfBytes(encoderType %2 == 0, encoderA, encoderB) + encoderType + (encoderType / 2) + (encoderType - (2 + getThreadByte(encoderType)))));
			OutStream.writeByte((byte)(switchHalfBytes(encoderType %2 == 0, encoderB, encoderA) + getThreadByte(encoderType) + (encoderType / 3) + ((1 + getThreadByte(encoderType)) * (encoderType + 1) * 2) + (encoderType + 5)));
			OutStream.writeByte((byte)(encoderC + (encoderType / 5) + 7 + ((encoderType + 1) * 3 + getThreadByte(encoderType)) - 8 + encoderType));
		}
		else
		{
			Encoder.writeByte(OutStream, (byte)(switchHalfBytes(encoderType %2 == 0, encoderA, encoderB) + encoderType + (encoderType / 2) + (encoderType - (2 + getThreadByte(encoderType)))));
			Encoder.writeByte(OutStream, (byte)(switchHalfBytes(encoderType %2 == 0, encoderB, encoderA) + getThreadByte(encoderType) + (encoderType / 3) + ((1 + getThreadByte(encoderType)) * (encoderType + 1) * 2) + (encoderType + 5)));
			Encoder.writeByte(OutStream, (byte)(encoderC + (encoderType / 5) + 7 + ((encoderType + 1) * 3 + getThreadByte(encoderType)) - 8 + encoderType));
		}
	}
	protected final void openInputStream(Streamable InStream, boolean Flush) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		if(InStream.isStreamInput() && encoderInOpen) return;
		if(ENCODER_LIST.get().threadLastIn != null && !ENCODER_LIST.get().threadLastIn.equals(this))
		{
			if(InStream.isStreamInput() && !encoderInOpen)
			{
				readEncoderABC(ENCODER_LIST.get().threadLastIn, InStream);
				if(Flush)
				{
					ENCODER_LIST.get().threadLastIn.closeInputStream(InStream);
					ENCODER_LIST.get().threadLastIn.refreshEncoderABC();
					ENCODER_LIST.get().threadLastIn = this;
				}
				encoderInOpen = true;
			}
		}
		else if(!ENCODER_LIST.get().threadEncoder.equals(this))
		{
			if(InStream.isStreamInput() && !encoderInOpen)
			{
				readEncoderABC(ENCODER_LIST.get().threadEncoder, InStream);
				if(Flush)
				{	
					ENCODER_LIST.get().threadEncoder.closeInputStream(InStream);
					ENCODER_LIST.get().threadEncoder.refreshEncoderABC();
					ENCODER_LIST.get().threadLastIn = this;
				}
				encoderInOpen = true;
			}
		}
		else
		{
			if(InStream.isStreamInput())
			{
				readEncoderABC(null, InStream);
				encoderInOpen = true;
			}
		}
	}
	protected final void openOutputStream(Streamable OutStream, boolean Flush) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(OutStream.isStreamOutput() && encoderOutOpen) return;	
		if(ENCODER_LIST.get().threadLastOut != null && !ENCODER_LIST.get().threadLastOut.equals(this))
		{
			if(OutStream.isStreamOutput() && !encoderOutOpen)
			{
				writeEncoderABC(ENCODER_LIST.get().threadLastOut, OutStream);
				if(Flush)
				{
					ENCODER_LIST.get().threadLastOut.closeOutputStream(OutStream);
					ENCODER_LIST.get().threadLastOut.refreshEncoderABC();
					ENCODER_LIST.get().threadLastOut = this;
				}
				encoderOutOpen = true;
			}
		}
		else if(!ENCODER_LIST.get().threadEncoder.equals(this))
		{
			if(OutStream.isStreamOutput() && !encoderOutOpen)
			{
				writeEncoderABC(ENCODER_LIST.get().threadEncoder, OutStream);
				if(Flush)
				{
					ENCODER_LIST.get().threadEncoder.closeOutputStream(OutStream);
					ENCODER_LIST.get().threadEncoder.refreshEncoderABC();
					ENCODER_LIST.get().threadLastOut = this;
				}
				encoderOutOpen = true;
			}
		}
		else
		{
			if(OutStream.isStreamOutput())
			{
				writeEncoderABC(null, OutStream);
				encoderOutOpen = true;
			}
		}
	}
	
	protected final boolean isInputOpen()
	{
		return encoderInOpen;
	}
	protected final boolean isOutputOpen()
	{
		return encoderOutOpen;
	}
	
	@SuppressWarnings("static-method")
	protected byte getThreadByte(byte ByteIndex)
	{
		return ENCODER_LIST.get().getThreadByte(ByteIndex);
	}
	
	protected final Encoder clone() throws CloneException
	{
		throw new CloneException("Encoders cannot be cloned!");
	}
	
	protected static final byte generateBlockIndex(Encoder Instance, boolean SetA, byte Type, byte BlockIndex)
	{
		if(SetA)
		{
			switch(Type % 8)
			{
			case 0:
				return (byte)((((Type + 1) * (1 + BlockIndex + Instance.getThreadByte(Type)) + Type + 5) / 3) + 4 + (5 * Type) + (BlockIndex / 5));
			case 1:
				return (byte)((Type / 5) + BlockIndex + ((BlockIndex + 1) * 7) + ((1 + Type) * 3) + (BlockIndex / 2) + Type);
			case 2:
				return (byte)((((3 + Type + Instance.getThreadByte(BlockIndex)) / 4 + 1) + BlockIndex) / 2 + (3 * Type) + (Type / 5) + BlockIndex + 3);
			case 3:
				return (byte)(((Type / 2) * 3) + 7 + ((Type + BlockIndex) * 3) - 2 + ((Type * (BlockIndex + 5 + Instance.getThreadByte(BlockIndex))) * 3));
			case 4:
				return (byte)((((BlockIndex * 6) + 2) / 5) * 3 + ((4 * BlockIndex) / 5) + 3 + (Type / 4));
			case 5:
				return (byte)((((Type * 3) / 5) + (5 + BlockIndex)) * 3 + (Type * (2 - Instance.getThreadByte(BlockIndex))) + (BlockIndex / (Type + 1)) + (6 + Type));
			case 6:
				return (byte)((((((BlockIndex + 5) / 3) * 7) + 3 + Instance.getThreadByte(Type)) / (Type + 1)) + 3 + (Type / (BlockIndex + 1)) * 3);
			case 7:
				return (byte)(((((Type / (BlockIndex + 1) * 2) + 5) / 4) + 10) + (3 * Type) + ((BlockIndex / 2) + (Type * 3)) + 4);
			}
		}
		switch(Type % 8)
		{
		case 0:
			return (byte)((((3 / (2 + BlockIndex) + 3) / (Type + 1)) * 9) + 6 - Instance.getThreadByte(BlockIndex));
		case 1:
			return (byte)(((((4 * BlockIndex) / 3 + (Type * 2)) / 3) + 8) / 3);
		case 2:
			return (byte)(((((9 + BlockIndex + Instance.getThreadByte(BlockIndex)) / 4) + (Type / 2) + (2 * BlockIndex + 1 + Instance.getThreadByte(Type))) / (((BlockIndex + 3) / (5 + Type)) + 6)));
		case 3:
			return (byte)(((((4 + (Type - 5) / 2) / 6) + 3) * 2) * ((5 + BlockIndex) / 3) + 4);
		case 4:
			return (byte)((((((Type / 3) / (3 + BlockIndex) + Instance.getThreadByte(Type)) / 9) * 2) + 8) + (5 + BlockIndex) / (3 + Type));
		case 5:
			return (byte)(((BlockIndex * 4) + (Type / 3) - Instance.getThreadByte(Type) + (6 / (1 + BlockIndex))) + (6 / (3 + Type)) + (BlockIndex * 3));
		case 6:
			return (byte)((((((Type * 9) / 6) + (BlockIndex * 3) / 9) * 5 + BlockIndex) - Instance.getThreadByte((byte)(Type + BlockIndex))) + (Type + 2) / 4);
		case 7:
			return (byte)((((((BlockIndex / 3) * 7) + 3 - Instance.getThreadByte(Type)) * 5 + Type) * (Type + 3) / 7) + Instance.getThreadByte(BlockIndex));
		}
		return 0;
	}
	
	private final void readInputBuffer(Streamable InStream) throws IOException
	{
		encoderInIndex = (byte)InStream.readByteArray(encoderInBuf); //Keeping for Reference -- > (byte)InStream.getStreamInput().read(encoderInBuf); Never again
		if(encoderInIndex > 0)
		{
			encoderBlockIn++;
			if(encoderBlockIn > 30) encoderBlockIn = 0;
			byte[] a = createCipherTable(false);
			descrableBytes(encoderInBuf, encoderType, encoderBlockIn);
			byte[][] b = new byte[encoderInBuf.length][256];
			for(byte c = 0; c < b.length; c++)
				for(short d = 0; d < b[c].length; d++)
				{
					b[c][(a[c] & 0xFF)] = (byte)d;
					a[c]++;
				}
			for(byte e = 0; e < encoderInBuf.length; e++)
			{
				encoderInBuf[e] = b[e][encoderInBuf[e] & 0xFF];
			}
			encoderInIndex = encoderInBuf[ENCODER_SIZE];
			
			/*
			 * 
			 * Look into flip bit ^= 5A and number same divisions as in SCP
			 */
		}
	}
	private final void flushOutputBuffer(Streamable OutStream) throws IOException
	{
		encoderBlockOut++;
		if(encoderBlockOut > 30) encoderBlockOut = 0;
		encoderOutBuf[ENCODER_SIZE] = encoderOutIndex;
		byte[] a = createCipherTable(true);
		byte[][] b = new byte[encoderOutBuf.length][256];
		for(byte c = 0; c < b.length; c++)
			for(short d = 0; d < b[c].length; d++) b[c][d] = a[c]++;
		for(byte d = 0; d < encoderOutBuf.length; d++)
		{
			encoderOutBuf[d] = b[d][encoderOutBuf[d] & 0xFF];
		}
		scrambleBytes(this, encoderOutBuf, encoderType, encoderBlockOut);
		OutStream.writeByteArray(encoderOutBuf, 0, encoderOutBuf.length);
	}
	private final void write(Streamable OutStream, int ByteValue) throws IOException
	{
		if(!encoderOutOpen) openOutputStream(OutStream, true);
		if(!OutStream.isStreamOutput()) notOutputStream();
		if(encoderOutIndex >= ENCODER_SIZE)
		{
			flushOutputBuffer(OutStream);
			encoderOutIndex = 0;
		}
		encoderOutBuf[encoderOutIndex++] = (byte)ByteValue;
	}
	private final void outputToBuffer(Streamable OutStream, byte[] OutputBytes) throws IOException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(!OutStream.isStreamOutput()) notOutputStream();
		outputToBuffer(OutStream, OutputBytes, 0, OutputBytes.length);
	}
	private final void outputToBuffer(Streamable OutStream, byte[] OutputBytes, int StartIndex, int Length) throws IOException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(!OutStream.isStreamOutput()) notOutputStream();
		for(int a = outputBuffer(OutStream, OutputBytes, StartIndex, Length); a >= 0; a = outputBuffer(OutStream, OutputBytes, a, Length)) { }
	}
	
	private final int inputToBuffer(Streamable InStream, int TotalBytes, boolean NoEOF) throws IOException
	{
		if(InStream == null) throw new NullException("InStream");
		if(!encoderInOpen) openInputStream(InStream, true);
		if(!InStream.isStreamInput()) notInputStream();
		if(encoderInIndex <= 0) readInputBuffer(InStream);
		if(encoderInIndex <= 0)
		{
			if(NoEOF) return encoderInIndex;
			throw new EOFException();
		}
		if(TotalBytes > encoderInIndex)
		{
			encoderFeeder = new byte[TotalBytes];
			for(int a = 0, b = 0; a < TotalBytes; a += b, encoderInIndex -= b)
			{
				if(encoderInIndex <= 0)
				{
					readInputBuffer(InStream);
					if(encoderInIndex <= 0)
					{
						encoderFeeder = Arrays.copyOf(encoderFeeder, b + a);
						if(NoEOF) return encoderFeeder.length;
						throw new EOFException();
					}
				}
				b = Math.min(TotalBytes - a, encoderInIndex);
				System.arraycopy(encoderInBuf, encoderInBuf[ENCODER_SIZE] - encoderInIndex, encoderFeeder, a, b);
			}
			return encoderFeeder.length;
		}
		//else
		//{
			encoderFeeder = Arrays.copyOfRange(encoderInBuf, encoderInBuf[ENCODER_SIZE] - encoderInIndex,
											  (encoderInBuf[ENCODER_SIZE] - encoderInIndex) + TotalBytes);
			encoderInIndex -= TotalBytes;
			return encoderFeeder.length;
		//}
	}
	private final int outputBuffer(Streamable OutStream, byte[] OutputBytes, int StartIndex, int Length) throws IOException
	{
		if(encoderOutIndex >= ENCODER_SIZE)
		{
			flushOutputBuffer(OutStream);
			encoderOutIndex = 0;
		}
		int a = Length - StartIndex;
		if(a > (ENCODER_SIZE - encoderOutIndex))
		{
			System.arraycopy(OutputBytes, StartIndex, encoderOutBuf, encoderOutIndex, ENCODER_SIZE- encoderOutIndex);
			a = (ENCODER_SIZE - encoderOutIndex);
			encoderOutIndex += a;
			return StartIndex + a;
		}
		System.arraycopy(OutputBytes, StartIndex, encoderOutBuf, encoderOutIndex, Length - StartIndex);
		encoderOutIndex += (Length - StartIndex);
		return -1;
	}
	
	private final byte[] createCipherTable(boolean OutputTable)
	{
		byte[] a = new byte[ENCODER_SIZE + 1];
		a[0] = (byte)((OutputTable? (byte)((encoderBlockOut + 1) * (encoderType + 1)) : (byte)((encoderBlockIn + 1) * (encoderType + 1))) + getThreadByte(encoderType));
		for(byte b = 1; b < a.length - 1; b++)
		{
			if(b <= 6)
				a[b] = (byte)((OutputTable? encoderBlockOut : encoderBlockIn) - encoderA + (encoderB - (b % 2== 0 ? encoderC : 3)) + b - getThreadByte(encoderA));
			else if(b > 6 && b <= 11)
				a[b] = (byte)(encoderC - encoderB + (((OutputTable? encoderBlockOut : encoderBlockIn) + 1) * b) + getThreadByte(encoderC));
			else if(b > 11)
				a[b] = (byte)((OutputTable? encoderBlockOut : encoderBlockIn) + getThreadByte(b) + encoderA - (encoderC / (b % 2 == 0 ? 4 : 2)) - b);
		}
		a[a.length - 1] = (byte)(getThreadByte((byte)(encoderB + encoderC)) + (OutputTable? encoderBlockOut : encoderBlockIn) - (a.length - 1) - encoderType + (encoderA - encoderC));
		return a;
	}
	
	private static final void notInputStream() throws IOException
	{
		Reporter.error(Reporter.REPORTER_IO, "This does not conatin an InputStream!");
		throw new IOException("This does not conatin an InputStream!");
	}
	private static final void notOutputStream() throws IOException
	{
		Reporter.error(Reporter.REPORTER_IO, "This does not conatin an OutputStream!");
		throw new IOException("This does not conatin an OutputStream!");
	}
	private static final void scrambleBytes(Encoder Instance, byte[] ByteArray, byte Type, byte BlockIndex)
	{
		byte a = 0, b = 0;
		byte[] c = new byte[2];
		for(byte d = 0; d < 6; d++)
		{
			a = (byte)Math.abs(generateBlockIndex(Instance, true, (byte)(Type + d), (byte)(BlockIndex + d)) % 8);
			b = (byte)Math.abs(generateBlockIndex(Instance, false, (byte)(Type + d), (byte)(BlockIndex + d)) % 8);
			if(a != b)
			{
				System.arraycopy(ByteArray, a * 2, c, 0, c.length);
				System.arraycopy(ByteArray, b * 2, ByteArray, a * 2, c.length);
				System.arraycopy(c, 0, ByteArray, b * 2, c.length);
			}
		}
	}
	
	private static final byte switchHalfBytes(boolean reverseAB, byte byteA, byte byteB)
	{
		return reverseAB ? (byte)((((byteB & 0xF) & 0xF) << 4) | ((byteA >> 4) & 0xF)) : (byte)(((((byteB >> 4) & 0xF) & 0xF) << 4) | (byteA & 0xF));
	}
	
	protected static final class EncoderThread
	{
		protected final Random threadRandom;
		protected final Random threadRandomA;
		protected final Random threadRandomB;
		protected final Encoder threadEncoder;
		
		protected Encoder threadLastIn;
		protected Encoder threadLastOut;
		
		private long threadSeedA;
		private long threadSeedB;
		private long threadSeedC;
		
		protected final void generateNewSeed()
		{
			createThreadSeed(ENCODER_PASSCODE);
		}
		protected final void generateNewSeed(long ThreadPasscode)
		{
			createThreadSeed(ThreadPasscode);
		}
		protected final void close(Streamable Stream) throws IOException
		{
			if(Stream.isStreamInput() && threadLastIn != null)
			{
				threadLastIn.closeInputStream(Stream);
				threadLastIn.refreshEncoderABC();
				threadLastIn = null;
			}
			if(Stream.isStreamOutput() && threadLastOut != null)
			{
				threadLastOut.closeOutputStream(Stream);
				threadLastOut.refreshEncoderABC();
				threadLastOut = null;
			}
			threadEncoder.closeStream(Stream);
			threadEncoder.refreshEncoderABC();
		}
		
		protected final short readItemByte(Streamable InStream) throws IOException
		{
			getLastEncoder(true).openInputStream(InStream, false);
			return (short)getLastEncoder(true).readUnsignedByte(InStream);
		}
		
		protected final Encoder getLastEncoder(boolean Input)
		{
			return Input ? (threadLastIn != null ? threadLastIn : threadEncoder) : (threadLastOut != null ? threadLastOut : threadEncoder);
		}
		
		private EncoderThread()
		{
			threadRandom = new Random();
			threadRandomA = new Random();
			threadRandomB = new Random();
			threadEncoder = new Encoder(127);
			createThreadSeed(ENCODER_PASSCODE);
		}
		
		private final void createThreadSeed(long ThreadPassphrase)
		{
			threadRandomA.setSeed((ENCODER_PASSCODE * ThreadPassphrase) + 256);
			threadRandomB.setSeed((ThreadPassphrase ^ (ENCODER_PASSCODE == 0 ? 256 : ENCODER_PASSCODE)) + (ThreadPassphrase * 512) + (ThreadPassphrase ^ 2));
			threadRandom.setSeed((threadRandomA.nextLong() * threadRandomB.nextLong()) + ThreadPassphrase); 
			threadSeedA = threadRandom.nextLong();
			threadSeedB = threadRandom.nextLong();
			threadSeedC = threadRandom.nextLong();
		}
		
		private final byte getThreadByte(byte ByteIndex)
		{
			byte a = (byte)Math.abs(ByteIndex % 24);
			if(a < 8) return (byte)((threadSeedA >> (8*a)) & 0xFF);
			if(a < 16) return (byte)((threadSeedB >> (8*(8-a))) & 0xFF);
			return (byte)((threadSeedC >> (8*(16-a))) & 0xFF);
		}
	}
	protected static final class EncoderController extends ThreadLocal<EncoderThread>
	{
		public final void set(EncoderThread EncoderThread) throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}
		
		protected final EncoderThread initialValue()
		{
			return new EncoderThread();
		}
		
		private EncoderController() { }
	}
}