package com.spire.io;

import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.log.Report;
import com.spire.sec.Security;
import com.spire.util.BoolTag;
import com.spire.log.Reporter;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.cred.Credentials;
import com.spire.ex.SizeException;
import com.spire.ex.StringException;
import com.spire.ex.NumberException;
import com.spire.ex.ReflectException;
import com.spire.ex.PermissionException;

public final class EncoderStream implements Streamable
{
	private final Streamable streamStream;
	private final KeyEncoder streamInEncoder;
	private final KeyEncoder streamOutEncoder;
	
	private boolean streamRead;
	private boolean streamWritten;
	
	public EncoderStream(Streamable StreamData) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), false, StreamData);
	}
	public EncoderStream(InputStream StreamInput) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), false, new DataStream(StreamInput, null));
	}
	public EncoderStream(OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), false, new DataStream(null, StreamOutput));
	}
	public EncoderStream(int StreamTypeID, Streamable StreamData) throws NullException, PermissionException
	{
		this(StreamTypeID, false, StreamData);
	}
	public EncoderStream(int StreamTypeID, InputStream StreamInput) throws NullException, PermissionException
	{
		this(StreamTypeID, false, new DataStream(StreamInput, null));
	}
	public EncoderStream(int StreamTypeID, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(StreamTypeID, false, new DataStream(null, StreamOutput));
	}
	public EncoderStream(boolean StreamUseKeySet, Streamable StreamData) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), StreamUseKeySet, StreamData);
	}
	public EncoderStream(boolean StreamUseKeySet, InputStream StreamInput) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), StreamUseKeySet, new DataStream(StreamInput, null));
	}
	public EncoderStream(InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), false, new DataStream(StreamInput, StreamOutput));
	}
	public EncoderStream(boolean StreamUseKeySet, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), StreamUseKeySet, new DataStream(null, StreamOutput));
	}
	public EncoderStream(int StreamTypeID, boolean StreamUseKeySet, Streamable StreamData) throws NullException, PermissionException
	{
		Security.check("io.stream.enc");
		if(StreamData == null) throw new NullException("StreamData");
		streamStream = StreamData;
		streamInEncoder = new KeyEncoder(StreamTypeID & 255);
		streamOutEncoder = new KeyEncoder(StreamTypeID & 255);
		streamInEncoder.partWriteKeys = !StreamUseKeySet;
		streamOutEncoder.partWriteKeys = !StreamUseKeySet;
	}
	public EncoderStream(int StreamTypeID, boolean StreamUseKeySet, InputStream StreamInput) throws NullException, PermissionException
	{
		this(StreamTypeID, StreamUseKeySet, new DataStream(StreamInput, null));
	}
	public EncoderStream(int StreamTypeID,InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(StreamTypeID, false, new DataStream(StreamInput, StreamOutput));
	}
	public EncoderStream(int StreamTypeID, boolean StreamUseKeySet, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(StreamTypeID, StreamUseKeySet, new DataStream(null, StreamOutput));
	}
	public EncoderStream(boolean StreamUseKeySet, InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(Constants.RNG.nextInt(), StreamUseKeySet, new DataStream(StreamInput, StreamOutput));
	}
	public EncoderStream(int StreamTypeID, boolean StreamUseKeySet, InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(StreamTypeID, StreamUseKeySet, new DataStream(StreamInput, StreamOutput));
	}	

 	public final void resetStreamKeyChain()
	{
		streamInEncoder.partChain.resetKeyChain();
		streamOutEncoder.partChain.resetKeyChain();
	}
 	public final void resetInputStreamKeyChain()
	{
		streamInEncoder.partChain.resetKeyChain();
	}
	public final void close() throws IOException
	{
		if(isStreamInput()) streamInEncoder.flush(streamStream);
		if(isStreamOutput()) streamOutEncoder.flush(streamStream);
		streamStream.flush();
		streamStream.close();
	}
	public final void flush() throws IOException
	{
		if(isStreamInput()) streamInEncoder.flush(streamStream);
		if(isStreamOutput()) streamOutEncoder.flush(streamStream);
		streamStream.flush();
	}
	public final void resetMark() throws IOException
	{
		streamStream.resetMark();
	}
	public final void flushInput() throws IOException
	{
		if(isStreamInput()) streamInEncoder.flush(streamStream);
		streamStream.flush();
	}
	public final void flushOutput() throws IOException
	{
		if(isStreamOutput()) streamOutEncoder.flush(streamStream);
		streamStream.flush();
	}
	public final void processReport(Report ReportData)
	{
		try
		{
			writeString(ReportData.toString() + '\r' + '\n');
		}
		catch (IOException Exception)
		{
			Reporter.reportUncaught("There was an error writing this Report to a Stream!", Exception);
		}
	}
	public final void setInputStreamChainBase(long ChainBase)
	{
		streamInEncoder.partChain.setChainBase(ChainBase);
	}
	public final void setOutputStreamChainBase(long ChainBase)
	{
		streamOutEncoder.partChain.setChainBase(ChainBase);
	}
	public final void setInputStreamChainPair(KeyChain ChainPair)
	{
		streamInEncoder.partChain.setChainPair(ChainPair);
	}
	public final void writeByte(int ByteValue) throws IOException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeByte(streamStream, ByteValue);
	}
	public final void setOutputStreamChainPair(KeyChain ChainPair)
	{
		streamOutEncoder.partChain.setChainPair(ChainPair);
	}
	public final void writeChar(char CharValue) throws IOException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeChar(streamStream, CharValue);
	}
	public final void writeLong(long LongValue) throws IOException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeLong(streamStream, LongValue);
	}
	public final void writeShort(int ShortValue) throws IOException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeShort(streamStream, ShortValue);
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
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeFloat(streamStream, FloatValue);
	}
	public final void writeInteger(int IntegerValue) throws IOException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeInteger(streamStream, IntegerValue);
	}
	public final void writeDouble(double DoubleValue) throws IOException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeDouble(streamStream, DoubleValue);
	}
	public final void writeBoolean(boolean BoolValue) throws IOException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeBoolean(streamStream, BoolValue);
	}
	public final void writeString(String StringValue) throws IOException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeString(streamStream, StringValue);
	}
	public final void writeUnicodeString(String StringValue) throws IOException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeUnicodeString(streamStream, StringValue);
	}
	public final void setInputStreamChainKeys(KeyChain ChainKeys) throws NullException
	{
		streamInEncoder.partChain.setChainKeys(ChainKeys);
	}
	public final void writeBytes(String StringValue) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeBytes(streamStream, StringValue);
	}
	public final void writeChars(String StringValue) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeChars(streamStream, StringValue);
	}
	public final void setOutputStreamChainKeys(KeyChain ChainKeys) throws NullException
	{
		streamOutEncoder.partChain.setChainKeys(ChainKeys);
	}
	public final void writeStorage(Storage Storeable) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorage(streamStream, Storeable);
	}
	public final void readLongArray(long[] LongArray) throws IOException, NullException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readLongArray(streamStream, LongArray);
	}
	public final void writeByteArray(byte[] ByteArray) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeByteArray(streamStream, ByteArray);
	}
	public final void writeLongArray(long[] LongArray) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeLongArray(streamStream, LongArray);
	}
	public final void readFloatArray(float[] FloatArray) throws IOException, NullException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readFloatArray(streamStream, FloatArray);
	}
	public final void readShortArray(short[] ShortArray) throws IOException, NullException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readShortArray(streamStream, ShortArray);
	}
	public final void writeShortArray(short[] ShortArray) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeShortArray(streamStream, ShortArray);
	}
	public final void writeFloatArray(float[] FloatArray) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeFloatArray(streamStream, FloatArray);	
	}
	public final void readFromStream(Streamable InStream) throws IOException, NullException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readFromStream(streamStream, InStream);
	}
	public final void writeToStream(Streamable OutStream) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeToStream(streamStream, OutStream);
	}
	public final void readIntegerArray(int[] IntegerArray) throws IOException, NullException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readIntegerArray(streamStream, IntegerArray);	
	}	
	public final void readFromStream(InputStream InStream) throws IOException, NullException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readFromStream(streamStream, InStream);
	}
	public final void writeToStream(OutputStream OutStream) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeToStream(streamStream, OutStream);
	}
	public final void writeIntegerArray(int[] IntegerArray) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeIntegerArray(streamStream, IntegerArray);
	}
	public final void readStringArray(String[] StringArray) throws IOException, NullException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStringArray(streamStream, StringArray);
	}
	public final void readDoubleArray(double[] DoubleArray) throws IOException, NullException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readDoubleArray(streamStream, DoubleArray);
	}
	public final void writeDoubleArray(double[] DoubleArray) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeDoubleArray(streamStream, DoubleArray);
	}
	public final void writeStringArray(String[] StringArray) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStringArray(streamStream, StringArray);
	}
	public final void readStringList(List<String> StringList) throws IOException, NullException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStringList(streamStream, StringList);
	}
	public final void setInputStreamChainKeys(Credentials CredentialChain) throws NullException
	{
		streamInEncoder.partChain.setChainKeys(CredentialChain);
	}
	public final void setOutputStreamChainKeys(Credentials CredentialChain) throws NullException
	{
		streamOutEncoder.partChain.setChainKeys(CredentialChain);
	}
	public final void writeStringList(List<String> StringList) throws IOException, NullException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStringList(streamStream, StringList);
	}
	public final void setStreamUseKeySet(boolean UseKeySet) throws IOException, PermissionException
	{
		Security.check("io.stream.enc.set", Thread.currentThread().getName());
		if(streamWritten)
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot change Stream when data is already commited!");
			throw new IOException("Cannot change Stream when data is already commited!");
		}
		if(isStreamInput() && !isStreamOutput())
		{
			Reporter.error(Reporter.REPORTER_IO, "Not valid on an Input stream!");
			throw new IOException("Not valid on an Input stream!");
		}
		streamOutEncoder.partWriteKeys = !UseKeySet;
	}
	public final void setInputStreamChainKeys(String KeyChainString) throws NullException, StringException
	{
		streamInEncoder.partChain.setChainKeys(KeyChainString);
	}
	public final void setOutputStreamChainKeys(String KeyChainString) throws NullException, StringException
	{
		streamOutEncoder.partChain.setChainKeys(KeyChainString);
	}
	/**
	 * Sets input stream key set
	 * @param KeySet
	 * @throws NullException
	 * @throws IOException
	 * @throws PermissionException
	 */
	public final void setStreamKeySet(byte[] KeySet) throws NullException, IOException, PermissionException
	{
		Security.check("io.stream.enc.getset", Thread.currentThread().getName());
		if(KeySet == null) throw new NullException("KeySet");
		if(KeySet.length < KeyEncoder.BUFFER_SIZE)
		{
			Reporter.error(Reporter.REPORTER_IO, "Invalid KeySet length!");
			throw new IOException("Invalid KeySet length!");
		}
		if(isStreamOutput() && !isStreamInput())
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot set on OutputStream!");
			throw new IOException("Cannot set on OutputStream!");
		}
		streamInEncoder.partWriteKeys = false;
		streamInEncoder.partKeyData = Arrays.copyOf(KeySet, KeyEncoder.BUFFER_SIZE);
	}
	public final void writeStorageList(List<? extends Storage> StorageList) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorageList(streamStream, StorageList);
	}
	public final void writeBooleanTags(boolean... BoolValues) throws IOException, NullException, SizeException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeBooleanTags(streamStream, BoolValues);
	}
	public final void writeStorageArray(Storage[] StorageArray) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorageArray(streamStream, StorageArray);
	}
	public final void setInputStreamChainKeys(long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		streamInEncoder.partChain.setChainKeys(KeyChainOne, KeyChainTwo, KeyChainThree, KeyChainFour);
	}
	public final void setutputStreamChainKeys(long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		streamOutEncoder.partChain.setChainKeys(KeyChainOne, KeyChainTwo, KeyChainThree, KeyChainFour);
	}
	public final void readLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readLongArray(streamStream, LongArray, StartIndex);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeByteArray(streamStream, ByteArray, StartIndex);	
	}
	public final void writeLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeLongArray(streamStream, LongArray, StartIndex);
	}
	public final void readShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readShortArray(streamStream, ShortArray, StartIndex);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readFloatArray(streamStream, FloatArray, StartIndex);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeShortArray(streamStream, ShortArray, StartIndex);	
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeFloatArray(streamStream, FloatArray, StartIndex);
	}
	public final void writeStorageList(List<? extends Storage> StorageList, int StartIndex) throws IOException, NullException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorageList(streamStream, StorageList, StartIndex);	
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readIntegerArray(streamStream, IntegerArray, StartIndex);
	}
	public final void readStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStringArray(streamStream, StringArray, StartIndex);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readDoubleArray(streamStream, DoubleArray, StartIndex);
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeIntegerArray(streamStream, IntegerArray, StartIndex);
	}
	public final void writeStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStringArray(streamStream, StringArray, StartIndex);
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeDoubleArray(streamStream, DoubleArray, StartIndex);
	}
	public final void readStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStringList(streamStream, StringList, StartIndex);
	}
	public final void writeStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStringList(streamStream, StringList, StartIndex);
	}
	public final void writeStorageArray(Storage[] StorageArray, int StartIndex) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorageArray(streamStream, StorageArray, StartIndex);
	}
	public final void readStorageArray(Storage[] StorageArray) throws IOException, NullException, NumberException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStorageArray(streamStream, StorageArray);
	}
	public final void readLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readLongArray(streamStream, LongArray, StartIndex, Length);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeByteArray(streamStream, ByteArray, StartIndex, Length);
	}
	public final void writeLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeLongArray(streamStream, LongArray, StartIndex, Length);	
	}
	public final void readShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readShortArray(streamStream, ShortArray, StartIndex, Length);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readFloatArray(streamStream, FloatArray, StartIndex, Length);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeShortArray(streamStream, ShortArray, StartIndex, Length);
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeFloatArray(streamStream, FloatArray, StartIndex, Length);
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readIntegerArray(streamStream, IntegerArray, StartIndex, Length);
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeIntegerArray(streamStream, IntegerArray, StartIndex, Length);	
	}
	public final void readStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStringArray(streamStream, StringArray, StartIndex, Length);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readDoubleArray(streamStream, DoubleArray, StartIndex, Length);
	}
	public final void writeStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStringArray(streamStream, StringArray, StartIndex, Length);
	}
	public final void setInputStreamChainKeys(long KeyChainBase, long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		streamInEncoder.partChain.setChainKeys(KeyChainBase, KeyChainOne, KeyChainTwo, KeyChainThree, KeyChainFour);
	}
	public final void setOutputStreamChainKeys(long KeyChainBase, long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		streamOutEncoder.partChain.setChainKeys(KeyChainBase, KeyChainOne, KeyChainTwo, KeyChainThree, KeyChainFour);
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeDoubleArray(streamStream, DoubleArray, StartIndex, Length);	
	}
	public final void writeStringList(List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStringList(streamStream, StringList, StartIndex, Length);
	}
	public final void writeStorageArray(Storage[] StorageArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorageArray(streamStream, StorageArray, StartIndex, Length);
	}
	public final void readStorageList(List<? extends Storage> StorageList) throws IOException, NullException, NumberException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStorageList(streamStream, StorageList);
	}
	public final void readStorageArray(Storage[] StorageArray, int StartIndex) throws IOException, NullException, NumberException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStorageArray(streamStream, StorageArray, StartIndex);
	}
	public final void writeStorageList(List<? extends Storage> StorageList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		if(!streamWritten)
		{
			streamOutEncoder.openOutput(streamStream);
			streamWritten = true;
		}
		streamOutEncoder.writeStorageList(streamStream, StorageList, StartIndex, Length);	
	}
	public final void readStorageArray(Storage[] StorageArray, int StartIndex, int Length) throws IOException, NullException, NumberException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStorageArray(streamStream, StorageArray, StartIndex, Length);
	}
	public final void readStorageList(List<? extends Storage> StorageList, int StartIndex) throws IOException, NullException, NumberException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		streamInEncoder.readStorageList(streamStream, StorageList, StartIndex);
	}
	
	public final boolean isUsingKeySet()
	{
		return streamOutEncoder.partWriteKeys;
	}
	public final boolean isStreamInput()
	{	
		return streamStream.isStreamInput();
	}
	public final boolean isStreamOutput()
	{	
		return streamStream.isStreamOutput();
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EncoderStream && ((EncoderStream)CompareObject).streamInEncoder.equals(streamInEncoder) &&
			   ((EncoderStream)CompareObject).streamOutEncoder.equals(streamOutEncoder) && ((EncoderStream)CompareObject).streamStream.equals(streamStream);
	}
	public final boolean readBoolean() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readBoolean(streamStream);
	}
	public final boolean canProcessReport(byte ReportLevel)
	{	
		return true;
	}
	
	public final byte getStreamTypeID()
	{
		return (byte)(streamInEncoder.partTypeID & streamOutEncoder.partTypeID);
	}
	public final byte readByte() throws IOException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readByte(streamStream);
	}
	
	public final char readChar() throws IOException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readChar(streamStream);
	}
	
	public final short readShort() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readShort(streamStream);
	}
	
	public final int hashCode()
	{
		return streamInEncoder.hashCode() + streamOutEncoder.hashCode() + streamStream.hashCode();
	}
	public final int readInteger() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readInteger(streamStream);
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
		if(!streamRead)
		{
			Reporter.error(Reporter.REPORTER_IO, "The Input Stream has not been opened yet!");
			throw new IOException("The Input Stream has not been opened yet!");
		}
		return streamInEncoder.getAvailable();
	}
	public final int readUnsignedByte() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readUnsignedByte(streamStream);
	}
	public final int readUnsignedShort() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readUnsignedShort(streamStream);
	}
	public final int readByteArray(byte[] ByteArray) throws IOException, NullException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readByteArray(streamStream, ByteArray);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readByteArray(streamStream, ByteArray, StartIndex);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readByteArray(streamStream, ByteArray, StartIndex, Length);
	}

	public final float readFloat() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readFloat(streamStream);
	}

	public final long readLong() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readLong(streamStream);
	}

	public final double readDouble() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readDouble(streamStream);
	}
	
	public final String toString()
	{
		return "EncoderStream(Stream) " + (streamInEncoder.hashCode() + streamOutEncoder.hashCode());
	}
	public final String readString() throws IOException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readString(streamStream);
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
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readBooleanTags(streamStream);
	}
	
	public final Storage readStorage(Streamable InStream) throws IOException, NullException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readStorage(streamStream);
	}
	
	public final byte[] getStreamKeySet() throws IOException
	{
		if(!streamWritten && streamRead)
		{
			Reporter.error(Reporter.REPORTER_IO, "No KeySet was created");
			throw new IOException("No KeySet was created!");
		}
		if(streamWritten && streamOutEncoder.partWriteKeys)
		{
			Reporter.error(Reporter.REPORTER_IO, "Stream is not configured to use a KeySet!");
			throw new IOException("Stream is not configured to use a KeySet!");
		}
		return Arrays.copyOf(streamOutEncoder.partKeyData, KeyEncoder.BUFFER_SIZE);
	}
	public final byte[] readByteArray(int ReadAmount) throws IOException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readByteArray(streamStream, ReadAmount);
	}

	public final short[] readShortArray(int ReadAmount) throws IOException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readShortArray(streamStream, ReadAmount);
	}

	public final int[] readIntegerArray(int ReadAmount) throws IOException,	NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readIntegerArray(streamStream, ReadAmount);
	}

	public final float[] readFloatArray(int ReadAmount) throws IOException,	NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readFloatArray(streamStream, ReadAmount);
	}
	
	public final long[] readLongArray(int ReadAmount) throws IOException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readLongArray(streamStream, ReadAmount);
	}

	public final double[] readDoubleArray(int ReadAmount) throws IOException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readDoubleArray(streamStream, ReadAmount);
	}

	public final String[] readStringArray(int ReadAmount) throws IOException, NumberException
	{	
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readStringArray(streamStream, ReadAmount);
	}
	
	public final Storage[] readStorageArray(Streamable InStream, int ReadAmount) throws IOException, NullException, NumberException, ReflectException
	{
		if(!streamRead)
		{
			streamInEncoder.openInput(streamStream);
			streamRead = true;
		}
		return streamInEncoder.readStorageArray(streamStream, ReadAmount);
	}
}
