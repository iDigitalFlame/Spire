package com.spire.cred;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.io.Streamable;
import com.spire.ex.CloneException;

public final class HashCheck implements Storage
{
	private byte hashA;
	private byte hashB;
	private byte hashC;
	private byte hashD;
	
	public final void createCheck(int HashCode)
	{
		hashA = (byte)(HashCode & 0xFF);
		hashB = (byte)((HashCode >> 8) & 0xFF);
		hashC = (byte)((HashCode >> 24) & 0xFF);
		hashD = (byte)((HashCode >> 16) & 0xFF);
	}
	
	public final boolean isSet()
	{
		return hashA != 0 || hashB != 0 || hashC != 0 && hashD != 0;
	}
	public final boolean isValid(int HashCode)
	{
		return (((hashC & 0xFF) << 24) | (((byte)(hashD & 0xFF) & 0xFF) << 16) | ((hashB & 0xFF) << 8) | (hashA & 0xFF)) == HashCode;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof HashCheck && ((HashCheck)CompareObject).hashA == hashA &&
			   ((HashCheck)CompareObject).hashB == hashB && ((HashCheck)CompareObject).hashC == hashC &&
			   ((HashCheck)CompareObject).hashD == hashD;
	}
	
	public final int hashCode()
	{
		return hashA + hashB + hashC + hashD;
	}
	
	public final String toString()
	{
		return "HashCheck(S) " + hashCode();
	}
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		hashA = StorageEncoder.readByte(InStream);
		hashB = StorageEncoder.readByte(InStream);
		hashC = StorageEncoder.readByte(InStream);
		hashD = StorageEncoder.readByte(InStream);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByte(OutStream, hashA);
		StorageEncoder.writeByte(OutStream, hashB);
		StorageEncoder.writeByte(OutStream, hashC);
		StorageEncoder.writeByte(OutStream, hashD);
	}
	
	protected final HashCheck clone() throws CloneException
	{
		throw new CloneException("Cannot clone HashChecks!");
	}
}