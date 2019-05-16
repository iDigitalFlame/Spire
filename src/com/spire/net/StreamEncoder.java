package com.spire.net;

import java.util.Random;
import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Streamable;

public final class StreamEncoder extends Encoder
{
	private long encoderKeyA;
	private long encoderKeyB;
	private long encoderKeyC;
	
	protected StreamEncoder(int EncoderIndex)
	{
		super(EncoderIndex);
	}
	
	protected final void syncKeys(Streamable Stream) throws IOException
	{
		Random a = new Random(), b = new Random(), c = new Random();
		int d = Encoder.generateLargePrime(), e = Encoder.generateLargePrime(), f = Encoder.generateLargePrime();
		Stream.writeInteger(d);
		Stream.writeInteger(e);
		Stream.writeInteger(f);
		Stream.flush();
		d += Stream.readInteger();
		e *= Stream.readInteger(); 
		f *= Stream.readInteger();
		e += f;
		d += (f + (e * f));
		f *= (e + d);
		a.setSeed((f * d) + e + (f ^ 2));
		b.setSeed(((e + 1) / d) + (d ^ 2) + (d * e) / (f + 1));
		c.setSeed(a.nextLong() * b.nextLong() + (a.nextLong() + b.nextLong()));
		encoderKeyA = c.nextLong();
		encoderKeyB = c.nextLong();
		encoderKeyC = c.nextLong();
		a = null;
		b = null;
		c = null;
	}
	protected final void openEncoder(Streamable Stream, boolean IsServer) throws IOException
	{
		if(IsServer)
			writeEncoderABC(null, Stream);
		else
			readEncoderABC(null, Stream);
		Stream.flush();
		setEncoderOpen(true, true);
	}
	
	protected final byte getThreadByte(byte ByteIndex)
	{
		byte a = (byte)Math.abs(ByteIndex % 24);
		if(a < 8) return (byte)((encoderKeyA >> (8*a)) & 0xFF);
		if(a < 16) return (byte)((encoderKeyB >> (8*(8-a))) & 0xFF);
		return (byte)((encoderKeyC >> (8*(16-a))) & 0xFF);
	}
}