package com.spire.cred;

import java.util.Arrays;
import com.spire.io.Item;
import java.io.IOException;
import com.spire.util.HashKey;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public class Credentials extends Item implements HashKey<String>
{
	public static final byte ITEM_CLASS_ID = 16; 
	
	private final HashCheck credHash;
	
	private String credName;
	private String credDomain;
	private byte[] credPassword;
	private boolean credShuffled;
	
	public Credentials(String UserName) throws NullException, StringException
	{
		this(UserName, null, null);
	}
	public Credentials(String UserName, String UserPassword) throws NullException, StringException
	{
		this(UserName, null, UserPassword);
	}
	public Credentials(String UserName, String UserDomain, String UserPassword) throws NullException, StringException
	{
		this();
		if(UserName == null) throw new NullException("UserName");
		if(UserName.isEmpty()) throw new StringException("UserName");
		if(UserDomain != null && UserDomain.isEmpty()) throw new StringException("UserDomain");
		if(UserPassword != null && UserPassword.isEmpty()) throw new StringException("UserPassword");
		credName = UserName;
		credDomain = UserDomain;
		credPassword = UserPassword != null ? UserPassword.getBytes() : new byte[0];
	}
	
	public final boolean isValid()
	{
		return credHash.isValid(generateHash(this));
	}
	public final boolean hasDomain()
	{
		return credDomain != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Credentials && ((Credentials)CompareObject).credName.equals(credName) &&
			   ((((Credentials)CompareObject).credPassword == null && credPassword == null) || ((Credentials)CompareObject).credPassword != null && credPassword != null && 
			   ((Credentials)CompareObject).credPassword.equals(credPassword)) &&
			   ((((Credentials)CompareObject).credDomain == null && credDomain == null) || ((Credentials)CompareObject).credDomain != null && credDomain != null && 
			   ((Credentials)CompareObject).credDomain.equals(credDomain)) && credHash.equals(credHash);
	}
	
	public final int hashCode()
	{
		return credName.hashCode() + (credDomain != null ? credDomain.hashCode() : 1024) * credHash.hashCode();
	}
	
	public final String getKey()
	{
		return credName;
	}
	public final String toString()
	{
		return "Credentials(" + getItemID() + ") " + credName;
	}
	public final String getUserName()
	{
		return credName;
	}
	public final String getUserDomain()
	{
		return credDomain;
	}
	public final String getUserPassword()
	{
		synchronized(credPassword)
		{
			if(credShuffled)
				itemEncoder.deshuffleByteArray(credPassword);
			byte[] a = Arrays.copyOf(credPassword, credPassword.length);
			itemEncoder.shuffleByteArray(credPassword);
			if(!credShuffled) credShuffled = true;
			return new String(a, 0, a.length);
		}
	}
	
	public final char[] getUserPasswordArray()
	{
		synchronized(credPassword)
		{
			if(credShuffled)
				itemEncoder.deshuffleByteArray(credPassword);
			byte[] a = Arrays.copyOf(credPassword, credPassword.length);
			itemEncoder.shuffleByteArray(credPassword);
			if(!credShuffled) credShuffled = true;
			return new String(a, 0, a.length).toCharArray();
		}
	}
	
	protected Credentials()
	{
		super(ITEM_CLASS_ID);
		credHash = new HashCheck();
	}
	
	protected final void readItemFailure()
	{
		credDomain = null;
		credPassword = new byte[0];
		credName = Constants.EMPTY_STRING;
		credHash.createCheck(credName.hashCode());
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		credHash.readStorage(InStream, itemEncoder);
		credName = itemEncoder.readString(InStream);
		credDomain = itemEncoder.readString(InStream);
		credPassword = new byte[itemEncoder.readShort(InStream)];
		itemEncoder.readByteArray(InStream, credPassword);
		itemEncoder.deshuffleByteArray(credPassword);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		credHash.writeStorage(OutStream, itemEncoder);
		itemEncoder.writeString(OutStream, credName);
		itemEncoder.writeString(OutStream, credDomain);
		itemEncoder.writeShort(OutStream, credPassword.length);
		if(!credShuffled)
		{
			credShuffled = true;
			itemEncoder.shuffleByteArray(credPassword);
		}
		itemEncoder.writeByteArray(OutStream, credPassword);
	}
	
	protected final Credentials getCopy()
	{
		return null;
	}
	
	private static final int generateHash(Credentials CredData)
	{
		int a = CredData.credDomain.hashCode() * CredData.credName.hashCode(); //needs null check
		for(short b = 0; b < CredData.credPassword.length; b++) a += (a * CredData.credPassword[b]);
		return a;
	}
}