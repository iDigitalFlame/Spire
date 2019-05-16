package com.spire.io;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.util.BoolTag;
import com.spire.ex.SizeException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.log.ReporterHandle;

public interface Streamable extends ReporterHandle
{
	/**
	 * <b>SYSTEM Stream</b><br/><br/>
	 *     Will always be available for use, no matter what access/security permissions are currently applied.  
	 *     The output and input are from the console.  This is mainly used for debugging / failures.
	 */
	Streamable SYSTEM = new DataStream();
	
	void close() throws IOException;
	void flush() throws IOException;
	void resetMark() throws IOException;
	void writeByte(int ByteValue) throws IOException;
	void writeChar(char CharValue) throws IOException;
	void writeLong(long LongValue) throws IOException;
	void writeShort(int ShortValue) throws IOException;
	void skipBytes(long BytesToSkip) throws IOException;
	void markPosition(int MarkLimit) throws IOException;
	void writeFloat(float FloatValue) throws IOException;
	void writeInteger(int IntegerValue) throws IOException;
	void writeDouble(double DoubleValue) throws IOException;
	void writeBoolean(boolean BoolValue) throws IOException;
	void writeString(String StringValue) throws IOException;
	void writeUnicodeString(String StringValue) throws IOException;
	void writeBytes(String StringValue) throws IOException, NullException;
	void writeChars(String StringValue) throws IOException, NullException;
	void readLongArray(long[] LongArray) throws IOException, NullException;
	void writeByteArray(byte[] ByteArray) throws IOException, NullException;
	void writeLongArray(long[] LongArray) throws IOException, NullException;
	void readFloatArray(float[] FloatArray) throws IOException, NullException;
	void readShortArray(short[] ShortArray) throws IOException, NullException;
	void writeShortArray(short[] ShortArray) throws IOException, NullException;
	void writeFloatArray(float[] FloatArray) throws IOException, NullException;
	void readFromStream(Streamable InStream) throws IOException, NullException;
	void writeToStream(Streamable OutStream) throws IOException, NullException;
	void readIntegerArray(int[] IntegerArray) throws IOException, NullException;
	void readFromStream(InputStream InStream) throws IOException, NullException;
	void writeToStream(OutputStream OutStream) throws IOException, NullException;
	void writeIntegerArray(int[] IntegerArray) throws IOException, NullException;
	void readStringArray(String[] StringArray) throws IOException, NullException;
	void readDoubleArray(double[] DoubleArray) throws IOException, NullException;
	void writeDoubleArray(double[] DoubleArray) throws IOException, NullException;
	void writeStringArray(String[] StringArray) throws IOException, NullException;
	void readStringList(List<String> StringList) throws IOException, NullException;
	void writeStringList(List<String> StringList) throws IOException, NullException;
	void writeBooleanTags(boolean... BoolValues) throws IOException, NullException, SizeException;
	void readLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException;
	void readShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException;
	void readFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException;
	void readIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException;
	void readStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException;
	void readDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException;
	void writeDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException;
	void readStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException;
	void writeStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException;
	void readLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void readShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void readFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void readIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void readStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void readDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	void writeStringList(List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException;
	
	boolean isStreamInput();
	boolean isStreamOutput();
	boolean readBoolean() throws IOException;
	
	byte readByte() throws IOException;
	
	char readChar() throws IOException;
	
	short readShort() throws IOException;
	
	int readInteger() throws IOException;
	int getAvailable() throws IOException;
	int readUnsignedByte() throws IOException;
	int readUnsignedShort() throws IOException;
	int readByteArray(byte[] ByteArray) throws IOException, NullException;
	int readByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException;
	int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException;
	
	float readFloat() throws IOException;
	
	long readLong() throws IOException;
	
	double readDouble() throws IOException;
	
	String readString() throws IOException;
	
	InputStream getStreamInput() throws IOException;
	
	OutputStream getStreamOutput() throws IOException;
	
	BoolTag readBooleanTags() throws IOException;
	
	byte[] readByteArray(int ReadAmount) throws IOException, NumberException;
	
	short[] readShortArray(int ReadAmount) throws IOException, NumberException;
	
	int[] readIntegerArray(int ReadAmount) throws IOException, NumberException;
	
	float[] readFloatArray(int ReadAmount) throws IOException, NumberException;
	
	long[] readLongArray(int ReadAmount) throws IOException, NumberException;
	
	double[] readDoubleArray(int ReadAmount) throws IOException, NumberException;
	
	String[] readStringArray(int ReadAmount) throws IOException, NumberException;
}