package com.spire.io.es.xes;

import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.io.es.CryptoCipher;

class XESCipher implements CryptoCipher
{
	public void writeCipher(byte ByteValue, Streamable DataStream) throws IOException
	{
		DataStream.writeByte(ByteValue);
	}
	public void writeCipher(byte[] ByteArray, Streamable DataStream) throws IOException
	{
		DataStream.writeByteArray(ByteArray);
	}
	public void writeCipher(byte[] ByteArray, int ByteStart, Streamable DataStream) throws IOException
	{
		DataStream.writeByteArray(ByteArray, ByteStart);
	}
	public void writeCipher(byte[] ByteArray, int ByteStart, int ByteLength, Streamable DataStream) throws IOException
	{
		DataStream.writeByteArray(ByteArray, ByteStart, ByteLength);
	}

	public byte readCipher(Streamable DataStream) throws IOException
	{
		return DataStream.readByte();
	}

	public int readCipher(byte[] ByteArray, Streamable DataStream) throws IOException
	{
		return DataStream.readByteArray(ByteArray);
	}
	public int readCipher(byte[] ByteArray, int ByteStart, Streamable DataStream) throws IOException
	{
		return DataStream.readByteArray(ByteArray, ByteStart);
	}
	public int readCipher(byte[] ByteArray, int ByteStart, int ByteEnd, Streamable DataStream) throws IOException
	{
		return DataStream.readByteArray(ByteArray, ByteStart, ByteEnd);
	}
	
	public XESCipher() { }
}