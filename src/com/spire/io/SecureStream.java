package com.spire.io;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.PermissionException;
/**
 * @deprecated
 */
public final class SecureStream extends Stream
{
	private static final String STREAM_TYPE = "secure";

	private final Encoder secureEncoder;

	protected volatile boolean secureEnabled;

	public final void flush() throws IOException
	{
		super.flush();
	}
	public final void writeByte(int ByteValue) throws IOException
	{
		if(secureEnabled) secureEncoder.writeByte(this, ByteValue);
		else super.writeByte(ByteValue);
	}
	public final void writeLong(long LongValue) throws IOException
	{
		secureEncoder.writeLong(this, LongValue);
	}
	public final void writeShort(int ShortValue) throws IOException
	{
		secureEncoder.writeShort(this, ShortValue);
	}
	public final void writeInteger(int IntegerValue) throws IOException
	{
		secureEncoder.writeInteger(this, IntegerValue);
	}
	public final void writeBoolean(boolean BoolValue) throws IOException
	{
		secureEncoder.writeBoolean(this, BoolValue);
	}
	public final void writeString(String StringValue) throws IOException
	{
		secureEncoder.writeString(this, StringValue);
	}
	public final void writeUnicodeString(String StringValue) throws IOException
	{
		secureEncoder.writeUnicodeString(this, StringValue);
	}
	public final void writeBytes(String StringValue) throws IOException, NullException
	{
		secureEncoder.writeBytes(this, StringValue);
	}
	public final void writeChars(String StringValue) throws IOException, NullException
	{
		secureEncoder.writeChars(this, StringValue);
	}
	public final void readFromStream(InputStream InStream) throws IOException, NullException
	{
		secureEncoder.readFromStream(this, InStream);
	}
	public final void writeToStream(OutputStream OutStream) throws IOException, NullException
	{
		secureEncoder.writeToStream(this, OutStream);
	}
	public final void readStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{
		secureEncoder.readStringList(this, StringList, StartIndex);
	}
	public final void readLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readLongArray(this, LongArray, StartIndex, Length);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeByteArray(this, ByteArray, StartIndex, Length);
	}
	public final void writeLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeLongArray(this, LongArray, StartIndex, Length);
	}
	public final void readShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readShortArray(this, ShortArray, StartIndex, Length);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readFloatArray(this, FloatArray, StartIndex, Length);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeShortArray(this, ShortArray, StartIndex, Length);
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeFloatArray(this, FloatArray, StartIndex, Length);
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readIntegerArray(this, IntegerArray, StartIndex, Length);
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeIntegerArray(this, IntegerArray, StartIndex, Length);
	}
	public final void readStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readStringArray(this, StringArray, StartIndex, Length);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readDoubleArray(this, DoubleArray, StartIndex, Length);
	}
	public final void writeStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeStringArray(this, StringArray, StartIndex, Length);
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeDoubleArray(this, DoubleArray, StartIndex, Length);
	}
	public final void writeStringList(List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.writeStringList(this, StringList, StartIndex, Length);
	}

	public final int hashCode()
	{
		return secureEncoder.hashCode() + super.hashCode();
	}
	public final int readInteger() throws IOException
	{
		return secureEncoder.readInteger(this);
	}
	public final int readUnsignedByte() throws IOException
	{
		return secureEnabled ? secureEncoder.readUnsignedByte(this) : super.readUnsignedByte();
	}
	public final int readUnsignedShort() throws IOException
	{
		return secureEncoder.readUnsignedShort(this);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		secureEncoder.readByteArray(this, ByteArray, StartIndex, Length);
		return (Length - StartIndex);
	}

	public final long readLong() throws IOException
	{
		return secureEncoder.readLong(this);
	}

	public final String readString() throws IOException
	{
		return secureEncoder.readString(this);
	}

	public final long[] readLongArray(int ReadAmount) throws IOException, NumberException
	{
		return secureEncoder.readLongArray(this, ReadAmount);
	}

	public final byte[] readByteArray(int ReadAmount) throws IOException, NumberException
	{
		return secureEncoder.readByteArray(this, ReadAmount);
	}

	public final short[] readShortArray(int ReadAmount) throws IOException, NumberException
	{
		return secureEncoder.readShortArray(this, ReadAmount);
	}

	public final int[] readIntegerArray(int ReadAmount) throws IOException, NumberException
	{
		return secureEncoder.readIntegerArray(this, ReadAmount);
	}

	public final float[] readFloatArray(int ReadAmount) throws IOException, NumberException
	{
		return secureEncoder.readFloatArray(this, ReadAmount);
	}

	public final double[] readDoubleArray(int ReadAmount) throws IOException, NumberException
	{
		return secureEncoder.readDoubleArray(this, ReadAmount);
	}

	public final String[] readStringArray(int ReadAmount) throws IOException,	NumberException
	{
		return secureEncoder.readStringArray(this, ReadAmount);
	}

	public final Encoder getEncoder()
	{
		return secureEncoder;
	}

	protected final String streamType()
	{
		return STREAM_TYPE;
	}


	private SecureStream(byte EncoderByte, InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		super(StreamInput, StreamOutput);
		secureEncoder = new Encoder(EncoderByte);
		secureEnabled = true;
	}
	private SecureStream(Encoder Encoder, InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		super(StreamInput, StreamOutput);
		secureEncoder = Encoder;
		secureEnabled = true;
	}
}