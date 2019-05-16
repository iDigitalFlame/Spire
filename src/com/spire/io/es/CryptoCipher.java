package com.spire.io.es;

import java.io.IOException;
import com.spire.io.Streamable;

public interface CryptoCipher
{
	void writeCipher(byte ByteValue, Streamable DataStream) throws IOException;
	void writeCipher(byte[] ByteArray, Streamable DataStream) throws IOException;
	void writeCipher(byte[] ByteArray, int ByteStart, Streamable DataStream) throws IOException;
	void writeCipher(byte[] ByteArray, int ByteStart, int ByteLength, Streamable DataStream) throws IOException;
	
	byte readCipher(Streamable DataStream) throws IOException;
	
	int readCipher(byte[] ByteArray, Streamable DataStream) throws IOException;
	int readCipher(byte[] ByteArray, int ByteStart, Streamable DataStream) throws IOException;
	int readCipher(byte[] ByteArray, int ByteStart, int ByteEnd, Streamable DataStream) throws IOException;
}