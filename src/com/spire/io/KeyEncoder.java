package com.spire.io;

import java.io.IOException;
import com.spire.log.Reporter;

final class KeyEncoder extends Encoder
{
	protected static final byte BUFFER_SIZE = 43;
	
	private final KeyEncoderKey[] partKeys;
	
	protected final byte partTypeID;
	protected final KeyChain partChain;
	
	protected byte[] partKeyData;
	protected boolean partWriteKeys;
	
	protected KeyEncoder(int EncoderType)
	{
		super(EncoderType);
		partTypeID = (byte)EncoderType;
		partChain = KeyChain.randomKeyChain();
		partKeys = new KeyEncoderKey[Item.MAX_ITEMS];
	}
	
	protected final void openInput(Streamable EncoderInStream) throws IOException
	{
		if(partWriteKeys)
		{
			byte[] a = new byte[BUFFER_SIZE];
			EncoderInStream.readByteArray(a);
			partChain.readKeySet(3, a);
			readEncoderABC(0, a);
		}
		else
		{
			if(partKeyData == null)
			{
				Reporter.error(Reporter.REPORTER_IO, "No KeySet to read from!");
				throw new IOException("No KeySet to read from!");
			}
			partChain.readKeySet(3, partKeyData);
			readEncoderABC(0, partKeyData);
		}
		setEncoderOpen(true, isOutputOpen());
	}
	protected final void openOutput(Streamable EncoderOutStream) throws IOException
	{
		if(partWriteKeys)
		{
			byte[] a = new byte[BUFFER_SIZE];
			partChain.writeKeySet(3, a);
			writeEncoderABC(0, a);
			EncoderOutStream.writeByteArray(a);
		}
		else
		{
			if(partKeyData == null)
				partKeyData = new byte[BUFFER_SIZE];
			partChain.writeKeySet(3, partKeyData);
			writeEncoderABC(0, partKeyData);
		}
		setEncoderOpen(isInputOpen(), true);
	}
	protected final void descrableBytes(byte[] ByteArray, byte Type, byte BlockIndex)
	{
		byte[] a = new byte[2];
		byte[][] b = new byte[6][2];
		for(byte c = 0; c < 6; c++)
		{
			b[c][0] = (byte)Math.abs(generateBlockIndex(this, true, (byte)(Type + c), (byte)(BlockIndex + c)) % 8);
		    b[c][1] = (byte)Math.abs(generateBlockIndex(this, false, (byte)(Type + c), (byte)(BlockIndex + c)) % 8);
		}
		for(byte d = 5; d >= 0; d--)
		{
			if(b[d][0] != b[d][1])
			{
				System.arraycopy(ByteArray, b[d][0] * 2, a, 0, a.length);
				System.arraycopy(ByteArray, b[d][1] * 2, ByteArray, b[d][0] * 2, a.length);
				System.arraycopy(a, 0, ByteArray, b[d][1] * 2, a.length);
			}
		}
	}
	
	protected final byte getThreadByte(byte ByteIndex)
	{
		if(partKeys[ByteIndex & 0xFF] == null)
			partKeys[ByteIndex & 0xFF] = new KeyEncoderKey();
		return partKeys[ByteIndex & 0xFF].getKey(ByteIndex, partChain);
	}
	
	private static final class KeyEncoderKey
	{	
		private byte keyData;
		private byte keyUses;
		
		private KeyEncoderKey()
		{
			keyUses = -1;
		}
		
		private final byte getKey(byte KeyID, KeyChain Chain)
		{
			if(keyUses == -1 || keyUses == 1)
			{
				keyUses = 0;
				keyData = (byte)((byte)(Chain.getNextKey(KeyID & 0xFF) >> (keyData % 8)) & 0xFF);
			}
			else keyUses++;
			return keyData;
		}
	}
}