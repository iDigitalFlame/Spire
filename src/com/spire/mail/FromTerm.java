package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.ex.FormatException;
import javax.mail.search.SearchTerm;

public class FromTerm extends EmailTerm
{
	protected EmailAddress termAddress;
	
	public FromTerm(EmailAddress FromAddress) throws NullException
	{
		if(FromAddress == null) throw new NullException("FromAddress");
		termAddress = FromAddress;
	}
	public FromTerm(String FromAddress) throws NullException, StringException, FormatException
	{
		termAddress = new EmailAddress(FromAddress);
	}
	
	public void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		termAddress = (EmailAddress)StorageEncoder.readStorage(InStream);
	}
	public void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeStorage(OutStream, termAddress);
	}
	
	public int hashCode()
	{
		return termAddress.hashCode();
	}
	
	protected FromTerm() { }
	
	protected SearchTerm getTerm()
	{
		return new javax.mail.search.FromTerm(termAddress.addressInstance);
	}

	protected FromTerm clone()
	{
		return new FromTerm(termAddress);
	}
}