package com.spire.net;

import java.util.List;
import com.spire.io.Stream;
import java.io.IOException;
import java.io.InputStream;
import com.spire.io.Storage;
import java.io.OutputStream;
import java.net.InetAddress;
import com.spire.log.Report;
import com.spire.log.Reporter;
import com.spire.util.BoolTag;
import com.spire.util.HashKey;
import com.spire.io.Streamable;
import com.spire.ex.SizeException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.ReflectException;

public final class NetworkStream implements Streamable, HashKey<NetAddress>
{
	private final Computer streamHost;
	private final Stream streamStream;
	private final NetAddress streamAddress;
	private final StreamTunnel streamAdapter;
	private final StreamEncoder streamEncoder;
	
	private boolean streamAutoFlush;
	
	public final void close() throws IOException
	{
		streamEncoder.flush(streamStream);
		streamStream.flush();
		streamStream.close();
		streamAdapter.streamStreams.removeElement(streamAddress);
		Reporter.info(Reporter.REPORTER_NETWORK, "Closing connection to \"" + streamHost.computerName + "\"");
	}
	public final void flush() throws IOException
	{
		streamEncoder.flush(streamStream);
		streamStream.flush();
	}
	public final void resetMark() throws IOException
	{
		streamStream.resetMark();
	}
	public final void setAutoFlush(boolean AutoFlush)
	{
		streamAutoFlush = AutoFlush;
	}
	public final void processReport(Report ReportData)
	{
		try
		{
			streamEncoder.writeString(streamStream, ReportData.toString() + '\r' + '\n');
			flushOutput();
		}
		catch (IOException Exception)
		{
			Reporter.reportUncaught("There was an error writing this Report to a Stream!", Exception);
		}
	}
	public final void writeByte(int ByteValue) throws IOException
	{
		streamEncoder.writeByte(streamStream, ByteValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeChar(char CharValue) throws IOException
	{	
		streamEncoder.writeChar(streamStream, CharValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeLong(long LongValue) throws IOException
	{	
		streamEncoder.writeLong(streamStream, LongValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeShort(int ShortValue) throws IOException
	{
		streamEncoder.writeShort(streamStream, ShortValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void skipBytes(long BytesToSkip) throws IOException
	{	
		streamStream.skipBytes(BytesToSkip);
	}
	public final void markPosition(int MarkLimit) throws IOException
	{	
		streamStream.markPosition(MarkLimit);
	}
	public final void writeFloat(float FloatValue) throws IOException
	{
		streamEncoder.writeFloat(streamStream, FloatValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeInteger(int IntegerValue) throws IOException
	{	
		streamEncoder.writeInteger(streamStream, IntegerValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeDouble(double DoubleValue) throws IOException
	{	
		streamEncoder.writeDouble(streamStream, DoubleValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeBoolean(boolean BoolValue) throws IOException
	{
		streamEncoder.writeBoolean(streamStream, BoolValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeString(String StringValue) throws IOException
	{	
		streamEncoder.writeString(streamStream, StringValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeUnicodeString(String StringValue) throws IOException
	{
		streamEncoder.writeUnicodeString(streamStream, StringValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeBytes(String StringValue) throws IOException, NullException
	{	
		streamEncoder.writeBytes(streamStream, StringValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeChars(String StringValue) throws IOException, NullException
	{
		streamEncoder.writeChars(streamStream, StringValue);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStorage(Storage Storeable) throws IOException, NullException
	{
		streamEncoder.writeStorage(streamStream, Storeable);
		if(streamAutoFlush) flushOutput();
	}
	public final void readLongArray(long[] LongArray) throws IOException, NullException
	{
		streamEncoder.readLongArray(streamStream, LongArray);
	}
	public final void writeByteArray(byte[] ByteArray) throws IOException, NullException
	{	
		streamEncoder.writeByteArray(streamStream, ByteArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeLongArray(long[] LongArray) throws IOException, NullException
	{	
		streamEncoder.writeLongArray(streamStream, LongArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void readFloatArray(float[] FloatArray) throws IOException, NullException
	{
		streamEncoder.readFloatArray(streamStream, FloatArray);
	}
	public final void readShortArray(short[] ShortArray) throws IOException, NullException
	{
		streamEncoder.readShortArray(streamStream, ShortArray);
	}
	public final void writeShortArray(short[] ShortArray) throws IOException, NullException
	{
		streamEncoder.writeShortArray(streamStream, ShortArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeFloatArray(float[] FloatArray) throws IOException, NullException
	{	
		streamEncoder.writeFloatArray(streamStream, FloatArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void readFromStream(Streamable InStream) throws IOException, NullException
	{	
		streamEncoder.readFromStream(streamStream, InStream);
	}
	public final void writeToStream(Streamable OutStream) throws IOException, NullException
	{	
		streamEncoder.writeToStream(streamStream, OutStream);
		if(streamAutoFlush) flushOutput();
	}
	public final void readIntegerArray(int[] IntegerArray) throws IOException, NullException
	{
		streamEncoder.readIntegerArray(streamStream, IntegerArray);	
	}	
	public final void readFromStream(InputStream InStream) throws IOException, NullException
	{	
		streamEncoder.readFromStream(streamStream, InStream);
	}
	public final void writeToStream(OutputStream OutStream) throws IOException, NullException
	{
		streamEncoder.writeToStream(streamStream, OutStream);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeIntegerArray(int[] IntegerArray) throws IOException, NullException
	{	
		streamEncoder.writeIntegerArray(streamStream, IntegerArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStringArray(String[] StringArray) throws IOException, NullException
	{
		streamEncoder.readStringArray(streamStream, StringArray);
	}
	public final void readDoubleArray(double[] DoubleArray) throws IOException, NullException
	{	
		streamEncoder.readDoubleArray(streamStream, DoubleArray);
	}
	public final void writeDoubleArray(double[] DoubleArray) throws IOException, NullException
	{
		streamEncoder.writeDoubleArray(streamStream, DoubleArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStringArray(String[] StringArray) throws IOException, NullException
	{
		streamEncoder.writeStringArray(streamStream, StringArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStringList(List<String> StringList) throws IOException, NullException
	{
		streamEncoder.readStringList(streamStream, StringList);
	}
	public final void writeStringList(List<String> StringList) throws IOException, NullException
	{	
		streamEncoder.writeStringList(streamStream, StringList);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStorageList(List<? extends Storage> StorageList) throws IOException, NullException
	{
		streamEncoder.writeStorageList(streamStream, StorageList);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeBooleanTags(boolean... BoolValues) throws IOException, NullException, SizeException
	{	
		streamEncoder.writeBooleanTags(streamStream, BoolValues);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStorageArray(Storage[] StorageArray) throws IOException, NullException, NumberException
	{
		streamEncoder.writeStorageArray(streamStream, StorageArray);
		if(streamAutoFlush) flushOutput();
	}
	public final void readLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{
		streamEncoder.readLongArray(streamStream, LongArray, StartIndex);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeByteArray(streamStream, ByteArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeLongArray(streamStream, LongArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void readShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.readShortArray(streamStream, ShortArray, StartIndex);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.readFloatArray(streamStream, FloatArray, StartIndex);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeShortArray(streamStream, ShortArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeFloatArray(streamStream, FloatArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStorageList(List<? extends Storage> StorageList, int StartIndex) throws IOException, NullException
	{
		streamEncoder.writeStorageList(streamStream, StorageList, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{
		streamEncoder.readIntegerArray(streamStream, IntegerArray, StartIndex);
	}
	public final void readStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.readStringArray(streamStream, StringArray, StartIndex);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.readDoubleArray(streamStream, DoubleArray, StartIndex);
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeIntegerArray(streamStream, IntegerArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeStringArray(streamStream, StringArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeDoubleArray(streamStream, DoubleArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.readStringList(streamStream, StringList, StartIndex);
	}
	public final void writeStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeStringList(streamStream, StringList, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStorageArray(Storage[] StorageArray, int StartIndex) throws IOException, NullException, NumberException
	{
		streamEncoder.writeStorageArray(streamStream, StorageArray, StartIndex);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStorageArray(Storage[] StorageArray) throws IOException, NullException, NumberException, ReflectException
	{
		streamEncoder.readStorageArray(streamStream, StorageArray);
	}
	public final void readLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.readLongArray(streamStream, LongArray, StartIndex, Length);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeByteArray(streamStream, ByteArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeLongArray(streamStream, LongArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void readShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.readShortArray(streamStream, ShortArray, StartIndex, Length);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.readFloatArray(streamStream, FloatArray, StartIndex, Length);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.writeShortArray(streamStream, ShortArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.writeFloatArray(streamStream, FloatArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.readIntegerArray(streamStream, IntegerArray, StartIndex, Length);
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeIntegerArray(streamStream, IntegerArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.readStringArray(streamStream, StringArray, StartIndex, Length);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.readDoubleArray(streamStream, DoubleArray, StartIndex, Length);
	}
	public final void writeStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.writeStringArray(streamStream, StringArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeDoubleArray(streamStream, DoubleArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStringList(List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		streamEncoder.writeStringList(streamStream, StringList, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void writeStorageArray(Storage[] StorageArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.writeStorageArray(streamStream, StorageArray, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStorageList(List<? extends Storage> StorageList) throws IOException, NullException, NumberException, ReflectException
	{
		streamEncoder.readStorageList(streamStream, StorageList);
	}
	public final void readStorageArray(Storage[] StorageArray, int StartIndex) throws IOException, NullException, NumberException, ReflectException
	{
		streamEncoder.readStorageArray(streamStream, StorageArray, StartIndex);
	}
	public final void writeStorageList(List<? extends Storage> StorageList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		streamEncoder.writeStorageList(streamStream, StorageList, StartIndex, Length);
		if(streamAutoFlush) flushOutput();
	}
	public final void readStorageArray(Storage[] StorageArray, int StartIndex, int Length) throws IOException, NullException, NumberException, ReflectException
	{
		streamEncoder.readStorageArray(streamStream, StorageArray, StartIndex, Length);
	}
	public final void readStorageList(List<? extends Storage> StorageList, int StartIndex) throws IOException, NullException, NumberException, ReflectException
	{
		streamEncoder.readStorageList(streamStream, StorageList, StartIndex);
	}
	
	public final boolean isStreamInput()
	{	
		return streamStream.isStreamInput();
	}
	public final boolean isStreamOutput()
	{	
		return streamStream.isStreamOutput();
	}
	public final boolean getSteamAutoFlush()
	{
		return streamAutoFlush;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof NetworkStream && ((NetworkStream)CompareObject).streamAddress.equals(streamAddress) && ((NetworkStream)CompareObject).streamStream.equals(streamStream);
	}
	public final boolean readBoolean() throws IOException
	{	
		return streamEncoder.readBoolean(streamStream);
	}
	public final boolean canProcessReport(byte ReportLevel)
	{	
		return true;
	}
	
	public final byte readByte() throws IOException
	{
		return streamEncoder.readByte(streamStream);
	}
	
	public final char readChar() throws IOException
	{
		return streamEncoder.readChar(streamStream);
	}
	
	public final short readShort() throws IOException
	{	
		return streamEncoder.readShort(streamStream);
	}
	
	public final int getPort()
	{
		return streamAddress.getPort();
	}
	public final int hashCode()
	{
		return streamEncoder.hashCode() + streamStream.hashCode();
	}
	public final int readInteger() throws IOException
	{	
		return streamEncoder.readInteger(streamStream);
	}
	public final int getAvailable() throws IOException
	{	
		if(!streamStream.isStreamInput())
		{
			Reporter.error(Reporter.REPORTER_IO, "This does not conatin an InputStream!");
			throw new IOException("This does not conatin an InputStream!");
		}
		if(streamStream.getAvailable() > 0)
			return streamStream.getAvailable();
		return streamEncoder.getAvailable();
	}
	public final int readUnsignedByte() throws IOException
	{	
		return streamEncoder.readUnsignedByte(streamStream);
	}
	public final int readUnsignedShort() throws IOException
	{	
		return streamEncoder.readUnsignedShort(streamStream);
	}
	public final int readByteArray(byte[] ByteArray) throws IOException, NullException
	{	
		return streamEncoder.readByteArray(streamStream, ByteArray);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		return streamEncoder.readByteArray(streamStream, ByteArray, StartIndex);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		return streamEncoder.readByteArray(streamStream, ByteArray, StartIndex, Length);
	}

	public final float readFloat() throws IOException
	{	
		return streamEncoder.readFloat(streamStream);
	}

	public final long readLong() throws IOException
	{	
		return streamEncoder.readLong(streamStream);
	}

	public final double readDouble() throws IOException
	{	
		return streamEncoder.readDouble(streamStream);
	}
	
	public final String toString()
	{
		return "NetworkStream Stream[" + streamAddress.getPort() + "@" + streamHost.computerName + "]";
	}
	public final String readString() throws IOException
	{	
		return streamEncoder.readString(streamStream);
	}
	
	public final InputStream getStreamInput() throws IOException
	{	
		return streamStream.getStreamInput();
	}
	
	public final OutputStream getStreamOutput() throws IOException
	{	
		return streamStream.getStreamOutput();
	}
	
	public final BoolTag readBooleanTags() throws IOException
	{	
		return streamEncoder.readBooleanTags(streamStream);
	}
	
	public final NetAddress getKey()
	{
		return streamAddress;
	}
	public final InetAddress getHostAddress()
	{
		return streamAddress.addressInstance;
	}
	
	public final Storage readStorage(Streamable InStream) throws IOException, NullException, ReflectException
	{
		return streamEncoder.readStorage(streamStream);
	}
	
	public final Computer getHost()
	{
		return streamHost;
	}
	
	public final StreamTunnel getTunnel()
	{
		return streamAdapter;
	}
	
	public final byte[] readByteArray(int ReadAmount) throws IOException, NumberException
	{	
		return streamEncoder.readByteArray(streamStream, ReadAmount);
	}

	public final short[] readShortArray(int ReadAmount) throws IOException, NumberException
	{	
		return streamEncoder.readShortArray(streamStream, ReadAmount);
	}

	public final int[] readIntegerArray(int ReadAmount) throws IOException,	NumberException
	{	
		return streamEncoder.readIntegerArray(streamStream, ReadAmount);
	}

	public final float[] readFloatArray(int ReadAmount) throws IOException,	NumberException
	{	
		return streamEncoder.readFloatArray(streamStream, ReadAmount);
	}
	
	public final long[] readLongArray(int ReadAmount) throws IOException, NumberException
	{	
		return streamEncoder.readLongArray(streamStream, ReadAmount);
	}

	public final double[] readDoubleArray(int ReadAmount) throws IOException, NumberException
	{	
		return streamEncoder.readDoubleArray(streamStream, ReadAmount);
	}

	public final String[] readStringArray(int ReadAmount) throws IOException, NumberException
	{	
		return streamEncoder.readStringArray(streamStream, ReadAmount);
	}
	
	public final Storage[] readStorageArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException, ReflectException
	{
		return streamEncoder.readStorageArray(streamStream, ReadAmount);
	}
	
	protected NetworkStream(StreamTunnel StreamAdapter, NetAddress StreamAddress, StreamEncoder StreamEncoder, Computer StreamHost, Stream StreamInstance)
	{
		streamHost = StreamHost;
		streamStream = StreamInstance;
		streamEncoder = StreamEncoder;
		streamAddress = StreamAddress;
		streamAdapter = StreamAdapter;
		streamAutoFlush = StreamAdapter.getSteamAutoFlush();
	}
	
	protected final void flushOutput() throws IOException
	{
		streamEncoder.flushOutput(streamStream);
		streamStream.flush();
	}
}