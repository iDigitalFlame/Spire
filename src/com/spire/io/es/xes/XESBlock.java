package com.spire.io.es.xes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.spire.util.BoolTag;
import com.spire.io.Streamable;
import com.spire.io.es.CryptoCipher;

public final class XESBlock
{
	/*
	 * 	Block Structure
	 *
	 * 	HEADER 1 Byte
	 * 		BYTE BOUNDRY 255
	 * 	HEADER START 6-261 Bytes
	 * 		HEADER SALT INT 4 Bytes
	 * 		HEADER NAME 1-256 Bytes
	 * 			HEADER NAME BCOUNT BYTE 1 Byte
	 * 			HEADER NAME STRING BYTES 0-255 Bytes
	 * 		HEADER FLAGS 1 Byte
	 * 		HEADER END 255
	 * 	HEADER END
	 * 	DATA START
	 * 		DATA
	 * 	DATA END
	 * 	FOOTER START 21 Bytes
	 * 		FOOTER BOUNDRY 255 0 255 255 0 0 0 255
	 * 		FOOTER DATA CHECKSUM INT 4 Bytes
	 * 		FOOTER DATA LENGTH LONG 8 Bytes
	 * 		FOOTER END 255
	 * 	FOOTER END
	 *
	 * New Note
	 *  Put the stream in charge of the read buffer
	 *
	 *  always read 8 bytes ahead if possible, call to the stream to read data
	 *  ex,
	 *  	call stream.readArray(byte[], int len);
	 *
	 *  	stream checks buffer, if empty,
	 *  	read 8 bytes until len is reached
	 *
	 *  	for each 8, look for the (255) flag
	 *  			if found look for (255) again
	 *  				if pass
	 *
	 */

	protected static final byte BLOCK_FLAG = (byte)0xFF;
	protected static final byte[] BLOCK_FOOTER_HEAD = new byte[] { (byte)0xFF, 0, (byte)0xFF, (byte)0xFF, 0, 0, 0, (byte)0xFF };

	protected final BoolTag blockFlags;

	protected int blockSalt;
	protected String blockName;
	protected long blockLength;
	protected int blockChecksum; /* Do we need this? */
	protected byte blockFootIndex;
	protected CryptoCipher blockCipher;

	protected XESBlock()
	{
		blockFlags = new BoolTag();
	}

	protected final void readHeader(XESCipher Cipher, Streamable DataStream) throws IOException
	{
		byte a = Cipher.readCipher(DataStream);
		if(a != BLOCK_FLAG) throw new XESException("No valid block start detected!");
		byte[] b = new byte[4];
		if(Cipher.readCipher(b, DataStream) != 4) throw new XESException("Block Header invalid! Header salt index failed!");
		blockSalt = (((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF));
		a = Cipher.readCipher(DataStream);
		if(a != 0)
		{
			b = new byte[a & 0xFF];
			if(Cipher.readCipher(b, DataStream) != b.length) throw new XESException("Block Header name invalid! Invalid length read!");
			blockName = new String(b, 0, b.length);
		}
		blockFlags.setTagData(Cipher.readCipher(DataStream));
		a = Cipher.readCipher(DataStream);
		if(a != BLOCK_FLAG) throw new XESException("No valid block end detected!");
	}
	protected final void readFooter(XESCipher Cipher, Streamable DataStream) throws IOException
	{
		byte[] a = new byte[8];
		if(Cipher.readCipher(a, 0, 4, DataStream) != 4) throw new XESException("Footer checksum invalid!");
		blockChecksum = (((a[0] & 0xFF) << 24) | ((a[1] & 0xFF) << 16) | ((a[2] & 0xFF) << 8) | (a[3] & 0xFF));
		if(Cipher.readCipher(a, DataStream) != 8) throw new XESException("Footer block length invalid!");
		blockLength = (((long)a[0] << 56) + ((long)(a[1] & 255) << 48) + ((long)(a[2] & 255) << 40) + ((long)(a[3] & 255) << 32) +
				      ((long)(a[4] & 255) << 24) + ((a[5] & 255) << 16) + ((a[6] & 255) <<  8) + ((a[7] & 255) <<  0));
		if(Cipher.readCipher(DataStream) != BLOCK_FLAG) throw new XESException("Foot end invalid!");
	}
	protected final void writeFooter(XESCipher Cipher, Streamable DataStream) throws IOException
	{
		Cipher.writeCipher(BLOCK_FOOTER_HEAD, DataStream);
		Cipher.writeCipher((byte)((blockChecksum >> 24) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockChecksum >> 16) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockChecksum >> 8) & 0xFF), DataStream);
		Cipher.writeCipher((byte)(blockChecksum & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 56) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 48) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 40) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 32) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 24) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 16) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockLength >> 8) & 0xFF), DataStream);
		Cipher.writeCipher((byte)(blockLength & 0xFF), DataStream);
		Cipher.writeCipher(BLOCK_FLAG, DataStream);
	}
	protected final void writeHeader(XESCipher Cipher, Streamable DataStream) throws IOException
	{
		Cipher.writeCipher(BLOCK_FLAG, DataStream);
		Cipher.writeCipher((byte)((blockSalt >> 24) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockSalt >> 16) & 0xFF), DataStream);
		Cipher.writeCipher((byte)((blockSalt >> 8) & 0xFF), DataStream);
		Cipher.writeCipher((byte)(blockSalt & 0xFF), DataStream);
		if(blockName != null && blockName.length() > 0)
		{
			Cipher.writeCipher((byte)blockName.length(), DataStream);
			Cipher.writeCipher(blockName.getBytes(), DataStream);
		}
		else
			Cipher.writeCipher((byte)0, DataStream);
		Cipher.writeCipher(blockFlags.getTagData(), DataStream);
		Cipher.writeCipher(BLOCK_FLAG, DataStream);
	}
}