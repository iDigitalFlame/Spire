package com.spire.io;

import java.util.Random;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import com.spire.cred.Credentials;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public final class KeyChain extends Item
{
	public static final byte ITEM_CLASS_ID = 21;
	
	private static final char ZERO_CHAR = '0';
	private static final SecureRandom SEC_RNG = new SecureRandom();
	
	private final Random chainRNG;
	private final long[] chainKeys;
	private final long[] chainBaseKeys;
	
	private long chainBase;
	private boolean chainSet;
	private KeyChain chainPair;
	
	public KeyChain()
	{
		super(ITEM_CLASS_ID);
		chainRNG = new Random();
		chainBaseKeys = new long[4];
		chainKeys = new long[Item.MAX_ITEMS];
	}
	public KeyChain(Credentials CredentialChain) throws NullException
	{
		this(0, CredentialChain);
	}
	public KeyChain(String KeyChainString) throws NullException, StringException
	{
		this();
		setChainKeys(KeyChainString);
	}
	public KeyChain(long KeyChainBase, Credentials CredentialChain) throws NullException
	{
		this();
		if(CredentialChain == null) throw new NullException("CredentialChain");
		chainBase = KeyChainBase;
		setChainKeys(CredentialChain);
	}
	public KeyChain(long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		this();
		chainBaseKeys[0] = KeyChainOne;
		chainBaseKeys[1] = KeyChainTwo;
		chainBaseKeys[2] = KeyChainThree;
		chainBaseKeys[3] = KeyChainFour;
	}
	public KeyChain(long KeyChainBase, long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		this(KeyChainOne, KeyChainTwo, KeyChainThree, KeyChainFour);
		chainBase = KeyChainBase;
	}
	
	public final void resetKeyChain()
	{
		chainRNG.setSeed(getChainSeed());
		for(int a = 0; a < Item.MAX_ITEMS; a++) chainKeys[a] = 0;
	}
	public final void setChainBase(long ChainBase)
	{
		chainBase = ChainBase;
	}
	public final void setChainPair(KeyChain ChainPair)
	{
		chainPair = ChainPair;
	}
	public final void setChainKeys(KeyChain ChainKey) throws NullException
	{
		if(ChainKey == null) throw new NullException("ChainKey");
		chainBase = ChainKey.chainBase;
		chainBaseKeys[0] = ChainKey.chainBaseKeys[0];
		chainBaseKeys[1] = ChainKey.chainBaseKeys[1];
		chainBaseKeys[2] = ChainKey.chainBaseKeys[2];
		chainBaseKeys[3] = ChainKey.chainBaseKeys[3];
	}
	public final void setChainKeys(Credentials CredentialChain) throws NullException
	{
		if(CredentialChain == null) throw new NullException("CredentialChain");
		chainBaseKeys[0] = (CredentialChain.hashCode() * CredentialChain.getKey().hashCode()) ^ CredentialChain.getKey().hashCode();
		chainBaseKeys[1] = CredentialChain.getUserPassword().hashCode() ^ CredentialChain.hashCode();
		chainBaseKeys[2] = (CredentialChain.getKey().hashCode() * CredentialChain.hashCode()) + CredentialChain.getUserPassword().hashCode();
		chainBaseKeys[3] = chainBaseKeys[1] ^ chainBaseKeys[2] * (CredentialChain.hasDomain() ? CredentialChain.getUserDomain().hashCode() : CredentialChain.hashCode()) ^ chainBaseKeys[0];
	}
	public final void setChainKeys(String KeyChainString) throws NullException, StringException
	{
		if(KeyChainString == null) throw new NullException("KeyChainString");
		if(KeyChainString.length() != 80) throw new StringException("KeyChainString is not a valid key string size!");		
		try
		{
			chainBaseKeys[0] = Long.parseLong(KeyChainString.substring(0, 16), 16);
			chainBaseKeys[1] = Long.parseLong(KeyChainString.substring(16, 32), 16);
			chainBaseKeys[2] = Long.parseLong(KeyChainString.substring(32, 48), 16);
			chainBaseKeys[3] = Long.parseLong(KeyChainString.substring(48, 64), 16);
			chainBase = Long.parseLong(KeyChainString.substring(64, 80), 16);
		}
		catch (NumberFormatException Exception)
		{
			throw new StringException(Exception);
		}
	}
	public final void setChainKeys(long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		chainBaseKeys[0] = KeyChainOne;
		chainBaseKeys[1] = KeyChainTwo;
		chainBaseKeys[2] = KeyChainThree;
		chainBaseKeys[3] = KeyChainFour;
	}
	public final void setChainKeys(long KeyChainBase, long KeyChainOne, long KeyChainTwo, long KeyChainThree, long KeyChainFour)
	{
		chainBase = KeyChainBase;
		chainBaseKeys[0] = KeyChainOne;
		chainBaseKeys[1] = KeyChainTwo;
		chainBaseKeys[2] = KeyChainThree;
		chainBaseKeys[3] = KeyChainFour;
	}
	
	public final int hashCode()
	{
		int a = 1;
		for(short b = 0; b < chainBaseKeys.length; b++) a *= (1 + chainBaseKeys[b]);
		return (int)(a ^ (1 + chainBase));
	}
	
	public final long getNextKey()
	{
		return getNextKey(chainRNG.nextInt());
	}
	public final long getNextKey(long KeyID)
	{
		if(!chainSet)
		{
			chainRNG.setSeed(getChainSeed());
			chainSet = true;
		}
		short a = (short)Math.abs(KeyID % Item.MAX_ITEMS);
		if(chainKeys[a] == 0)
		{
			chainKeys[a] = BigInteger.probablePrime(Long.SIZE, chainRNG).longValue();
			chainKeys[a] ^= (chainKeys[a % 4] * (chainBase + 1));
			chainKeys[a] *= (chainBaseKeys[Math.abs((int)chainKeys[a]) % 4] + chainBase);
			if(chainPair != null)
			{
				chainKeys[a] ^= chainPair.getNextKey();
				chainKeys[a] -= (chainPair.chainBase ^ chainBase); 
			}
			return chainKeys[a];
		}
		chainKeys[a] *= BigInteger.probablePrime(Long.SIZE, chainRNG).longValue();
		chainKeys[a] ^= BigInteger.probablePrime(Integer.SIZE, chainRNG).intValue();
		chainKeys[a] *= (chainBaseKeys[a % 4] * (chainBase + 1)) ^ chainBaseKeys[(a + 1) % 4];
		chainKeys[a] += chainBase + (chainBaseKeys[a % 4] * chainKeys[a]);
		if(chainPair != null)
		{
			chainKeys[a] ^= chainPair.getNextKey();
			chainKeys[a] -= (chainPair.chainBase ^ chainBase); 
		}
		return chainKeys[a];
	}
	
	public final String toString()
	{
		return "KeyChain(" + getItemID() + ") [" + (chainBaseKeys[0] & chainBaseKeys[1] & chainBaseKeys[2] & chainBaseKeys[3]) + "]";
	}
	public final String getKeyString()
	{
		return expandLong(chainBaseKeys[0]) + expandLong(chainBaseKeys[1]) + expandLong(chainBaseKeys[2]) + expandLong(chainBaseKeys[3]) + expandLong(chainBase);
	}
	
	public static final KeyChain randomKeyChain()
	{
		return new KeyChain(BigInteger.probablePrime(Long.SIZE, SEC_RNG).longValue(), SEC_RNG.nextLong(), SEC_RNG.nextLong(), BigInteger.probablePrime(Long.SIZE, SEC_RNG).longValue(), BigInteger.probablePrime(Long.SIZE, SEC_RNG).longValue());
	}
	
	protected final void readItemFailure()
	{
		chainBase = 0;
		chainBaseKeys[0] = 0;
		chainBaseKeys[1] = 0;
		chainBaseKeys[2] = 0;
		chainBaseKeys[3] = 0;
	}
	protected final void readKeySet(int StartIndex, byte[] KeySet)
	{
		chainBase = readLongFromArray(KeySet, StartIndex);
		chainBaseKeys[0] = readLongFromArray(KeySet, StartIndex + 8);
		chainBaseKeys[1] = readLongFromArray(KeySet, StartIndex + 16);
		chainBaseKeys[2] = readLongFromArray(KeySet, StartIndex + 24);
		chainBaseKeys[3] = readLongFromArray(KeySet, StartIndex + 32);
	}
	protected final void writeKeySet(int StartIndex, byte[] KeySet)
	{
		writeLongToArray(KeySet, StartIndex, chainBase);
		writeLongToArray(KeySet, StartIndex + 8, chainBaseKeys[0]);
		writeLongToArray(KeySet, StartIndex + 16, chainBaseKeys[1]);
		writeLongToArray(KeySet, StartIndex + 24, chainBaseKeys[2]);
		writeLongToArray(KeySet, StartIndex + 32, chainBaseKeys[3]);
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		chainBase = itemEncoder.readLong(InStream);
		chainBaseKeys[0] = itemEncoder.readLong(InStream);
		chainBaseKeys[1] = itemEncoder.readLong(InStream);
		chainBaseKeys[2] = itemEncoder.readLong(InStream);
		chainBaseKeys[3] = itemEncoder.readLong(InStream);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeLong(OutStream, chainBase);
		itemEncoder.writeLong(OutStream, chainBaseKeys[0]);
		itemEncoder.writeLong(OutStream, chainBaseKeys[1]);
		itemEncoder.writeLong(OutStream, chainBaseKeys[2]);
		itemEncoder.writeLong(OutStream, chainBaseKeys[3]);
	}
	protected final void readKeySet(Encoder SetEncoder, Streamable InStream) throws IOException
	{
		chainBase = SetEncoder.readLong(InStream);
		chainBaseKeys[0] = SetEncoder.readLong(InStream);
		chainBaseKeys[1] = SetEncoder.readLong(InStream);
		chainBaseKeys[2] = SetEncoder.readLong(InStream);
		chainBaseKeys[3] = SetEncoder.readLong(InStream);
	}
	protected final void writeKeySet(Encoder SetEncoder, Streamable OutStream) throws IOException
	{
		SetEncoder.writeLong(OutStream, chainBase);
		SetEncoder.writeLong(OutStream, chainBaseKeys[0]);
		SetEncoder.writeLong(OutStream, chainBaseKeys[1]);
		SetEncoder.writeLong(OutStream, chainBaseKeys[2]);
		SetEncoder.writeLong(OutStream, chainBaseKeys[3]);
	}
	
	protected final KeyChain getCopy()
	{
		return new KeyChain(getChainSeed(), chainBaseKeys[0], chainBaseKeys[1], chainBaseKeys[2], chainBaseKeys[3]);
	}
	
	private final long getChainSeed()
	{
		return (chainBase + 1) * (chainBaseKeys[0] * (chainBaseKeys[1] ^ chainBaseKeys[2]) * chainBaseKeys[3]) + (chainBaseKeys[2] + chainBaseKeys[3]);
	}
	
	private static final void writeLongToArray(byte[] ByteArray, int StartIndex, long LongValue)
	{
		ByteArray[StartIndex] = (byte)((LongValue >> 56) & 0xFF);
		ByteArray[StartIndex + 1] = (byte)((LongValue >> 48) & 0xFF);
		ByteArray[StartIndex + 2] = (byte)((LongValue >> 40) & 0xFF);
		ByteArray[StartIndex + 3] = (byte)((LongValue >> 32) & 0xFF);
		ByteArray[StartIndex + 4] = (byte)((LongValue >> 24) & 0xFF);
		ByteArray[StartIndex + 5] = (byte)((LongValue >> 16) & 0xFF);
		ByteArray[StartIndex + 6] = (byte)((LongValue >> 8) & 0xFF);
		ByteArray[StartIndex + 7] = (byte)(LongValue & 0xFF);
	}
	
	private static final long readLongFromArray(byte[] ByteArray, int StartIndex)
	{
		return (((long)ByteArray[StartIndex] << 56) + ((long)(ByteArray[StartIndex + 1] & 255) << 48) +
				((long)(ByteArray[StartIndex + 2] & 255) << 40) + ((long)(ByteArray[StartIndex + 3] & 255) << 32) +
				((long)(ByteArray[StartIndex + 4] & 255) << 24) + ((ByteArray[StartIndex + 5] & 255) << 16) +
				((ByteArray[StartIndex + 6] & 255) <<  8) + ((ByteArray[StartIndex + 7] & 255) <<  0));
	}
	
	private static final String expandLong(long LongValue)
	{
		String a = Long.toHexString(LongValue);
		if(a.length() < 16)
		{
			StringBuilder b = new StringBuilder();
			for(byte c = (byte)a.length(); c < Short.SIZE; c++)
				b.append(ZERO_CHAR);
			b.append(a);
			return b.toString();
		}
		return a;
	}
}