package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.ex.FormatException;
import javax.mail.search.SearchTerm;
import javax.mail.Message.RecipientType;

public final class RecipientTerm extends FromTerm
{
	public static final byte ADDRESS_TO = 0;
	public static final byte ADDRESS_CC = 1;
	public static final byte ADDRESS_BCC = 2;
	public static final byte ADDRESS_NEWS = 3;
	
	private byte termType;
	
	public RecipientTerm(byte AddressType, EmailAddress RecipientAddress) throws NullException, NumberException
	{
		super(RecipientAddress);
		if(AddressType < 0) throw new NumberException("AddressType", AddressType, true);
		if(AddressType > ADDRESS_NEWS) throw new NumberException("AddressType", AddressType, 0, ADDRESS_NEWS);
		termType = AddressType;
	}
	public RecipientTerm(byte AddressType, String RecipientAddress) throws NullException, StringException, FormatException, NumberException
	{
		this(AddressType, new EmailAddress(RecipientAddress));
	}
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		super.readStorage(InStream, StorageEncoder);
		termType = StorageEncoder.readByte(InStream);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		super.writeStorage(OutStream, StorageEncoder);
		StorageEncoder.writeByte(OutStream, termType);
	}
	
	public final int hashCode()
	{
		return super.hashCode() + termType;
	}
	
	public static final RecipientTerm getRecipientToTerm(EmailAddress RecipientAddress) throws NullException
	{
		return new RecipientTerm(ADDRESS_TO, RecipientAddress);
	}
	public static final RecipientTerm getRecipientCCTerm(EmailAddress RecipientAddress) throws NullException
	{
		return new RecipientTerm(ADDRESS_CC, RecipientAddress);
	}
	public static final RecipientTerm getRecipientBCCTerm(EmailAddress RecipientAddress) throws NullException
	{
		return new RecipientTerm(ADDRESS_BCC, RecipientAddress);
	}
	public static final RecipientTerm getRecipientNewsTerm(EmailAddress RecipientAddress) throws NullException
	{
		return new RecipientTerm(ADDRESS_NEWS, RecipientAddress);
	}
	public static final RecipientTerm getRecipientToTerm(String RecipientAddress) throws NullException, StringException
	{
		return new RecipientTerm(ADDRESS_TO, RecipientAddress);
	}
	public static final RecipientTerm getRecipientCCTerm(String RecipientAddress) throws NullException, StringException
	{
		return new RecipientTerm(ADDRESS_CC, RecipientAddress);
	}
	public static final RecipientTerm getRecipientBCCTerm(String RecipientAddress) throws NullException, StringException
	{
		return new RecipientTerm(ADDRESS_BCC, RecipientAddress);
	}
	public static final RecipientTerm getRecipientNewsTerm(String RecipientAddress) throws NullException, StringException
	{
		return new RecipientTerm(ADDRESS_NEWS, RecipientAddress);
	}
	
	protected RecipientTerm() { }
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.RecipientTerm(getType(termType), termAddress.addressInstance);
	}
	
	protected final RecipientTerm clone()
	{
		return new RecipientTerm(termType, termAddress);
	}
	
	protected static final RecipientType getType(byte TermType)
	{
		switch(TermType)
		{
		case ADDRESS_TO:
			return RecipientType.TO;
		case ADDRESS_CC:
			return RecipientType.CC;
		case ADDRESS_BCC:
			return RecipientType.BCC;
		case ADDRESS_NEWS:
			return javax.mail.internet.MimeMessage.RecipientType.NEWSGROUPS;
		default:
			return RecipientType.TO;
		}
	}
}