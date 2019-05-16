package com.spire.io.es.xes;

import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.io.es.CryptoCipher;

final class XESSecCipher extends XESCipher
{
	protected CryptoCipher cipherSecondary;
	
	public final void writeCipher(byte ByteValue, Streamable DataStream) throws IOException
	{
		DataStream.writeByte(ByteValue);
	}
	public final void writeCipher(byte[] ByteArray, Streamable DataStream) throws IOException
	{
		DataStream.writeByteArray(ByteArray);
	}
	public final void writeCipher(byte[] ByteArray, int ByteStart, Streamable DataStream) throws IOException
	{
		DataStream.writeByteArray(ByteArray, ByteStart);
	}
	public final void writeCipher(byte[] ByteArray, int ByteStart, int ByteLength, Streamable DataStream) throws IOException
	{
		DataStream.writeByteArray(ByteArray, ByteStart, ByteLength);
	}

	public final byte readCipher(Streamable DataStream) throws IOException
	{
		return DataStream.readByte();
	}

	public final int readCipher(byte[] ByteArray, Streamable DataStream) throws IOException
	{
		return DataStream.readByteArray(ByteArray);
	}
	public final int readCipher(byte[] ByteArray, int ByteStart, Streamable DataStream) throws IOException
	{
		return DataStream.readByteArray(ByteArray, ByteStart);
	}
	public final int readCipher(byte[] ByteArray, int ByteStart, int ByteEnd, Streamable DataStream) throws IOException
	{
		return DataStream.readByteArray(ByteArray, ByteStart, ByteEnd);
	}
	
	public XESSecCipher() { }
}