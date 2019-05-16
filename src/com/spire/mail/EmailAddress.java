package com.spire.mail;

import javax.mail.Address;
import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.util.HashKey;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.ex.FormatException;
import com.spire.ex.InternalException;
import javax.mail.internet.NewsAddress;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import java.io.UnsupportedEncodingException;

public final class EmailAddress implements Storage, HashKey<String>
{
	public static final byte ADDRESS_TO = 0;
	public static final byte ADDRESS_CC = 1;
	public static final byte ADDRESS_BCC = 2;
	public static final byte ADDRESS_NEWS = 3;
	public static final byte ADDRESS_REPLY = 4;
	
	protected Address addressInstance;
	
	public EmailAddress() { }
	public EmailAddress(String EmailAddress) throws NullException, StringException, FormatException
	{
		this(EmailAddress, null);
	}
	public EmailAddress(String EmailAddress, String EmailName) throws NullException, StringException, FormatException
	{
		if(EmailAddress == null) throw new NullException("EmailAddress");
		if(EmailAddress.isEmpty()) throw new StringException("EmailAddress");
		if(EmailName != null && EmailName.isEmpty()) throw new StringException("EmailName");
		try
		{
			addressInstance = new InternetAddress(EmailAddress, true);
			if(EmailName != null)
				((InternetAddress)addressInstance).setPersonal(EmailName);
		}
		catch (AddressException Exception)
		{
			throw new FormatException(Exception.getMessage(), Exception);
		}
		catch (UnsupportedEncodingException Exception)
		{
			throw new FormatException(Exception.getMessage(), Exception);
		}
	}
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		try
		{
			if(StorageEncoder.readBoolean(InStream))
				addressInstance = new InternetAddress(StorageEncoder.readString(InStream));
			else
				addressInstance = new NewsAddress(StorageEncoder.readString(InStream));
		}
		catch (AddressException Exception)
		{
			throw new IOException(Exception);
		}
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeBoolean(OutStream, !isNewsAddress());	
		StorageEncoder.writeString(OutStream, addressInstance.toString());
	}
	public final void setAddressName(String AddressName) throws StringException, InternalException, FormatException
	{
		if(!(addressInstance instanceof InternetAddress))
			throw new InternalException("This is not an Internet Address!");
		try
		{
			((InternetAddress)addressInstance).setPersonal(AddressName);
		}
		catch (UnsupportedEncodingException Exception)
		{
			throw new FormatException(Exception.getMessage(), Exception);
		}
	}
	
	public final boolean isNewsAddress()
	{
		return addressInstance instanceof NewsAddress;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailAddress && ((EmailAddress)CompareObject).addressInstance.equals(addressInstance);
	}
	
	public final int hashCode()
	{
		return addressInstance.toString().hashCode();
	}
	
	public final String getKey()
	{
		return getName();
	}
	public final String getName()
	{
		return addressInstance instanceof InternetAddress ? ((InternetAddress)addressInstance).getPersonal() : ((NewsAddress)addressInstance).getHost();
	}
	public final String toString()
	{
		return addressInstance instanceof InternetAddress ? ((InternetAddress)addressInstance).getAddress() : ((NewsAddress)addressInstance).getNewsgroup();
	}
	
	public static final EmailAddress getNewsAddress(String NewsAddress, String NewsHost)  throws NullException, StringException, FormatException
	{
		if(NewsAddress == null) throw new NullException("NewsAddress");
		if(NewsAddress.isEmpty()) throw new StringException("NewsAddress");
		if(NewsHost != null && NewsHost.isEmpty()) throw new StringException("NewsHost");
		try
		{
			return new EmailAddress(new NewsAddress(NewsAddress, NewsHost));
		}
		catch (Exception Exception)
		{
			throw new FormatException(Exception.getMessage(), Exception);
		}
	}
	
	protected EmailAddress(Address AddressInstance)
	{
		addressInstance = AddressInstance;
	}

	protected final EmailAddress clone()
	{
		return new EmailAddress(addressInstance);
	}
}