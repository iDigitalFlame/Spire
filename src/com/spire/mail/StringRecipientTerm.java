package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import javax.mail.search.SearchTerm;

public final class StringRecipientTerm extends SubjectTerm
{
	private byte termType;
	
	public StringRecipientTerm(byte AddressType, String SearchExpression) throws NullException, NumberException, StringException
	{
		super(SearchExpression);
		if(AddressType < 0) throw new NumberException("AddressType", AddressType, true);
		if(AddressType > RecipientTerm.ADDRESS_NEWS) throw new NumberException("AddressType", AddressType, 0, RecipientTerm.ADDRESS_NEWS);
		termType = AddressType;
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
	
	public static final StringRecipientTerm getRecipientToTerm(String SearchExpression) throws NullException, StringException
	{
		return new StringRecipientTerm(RecipientTerm.ADDRESS_TO, SearchExpression);
	}
	public static final StringRecipientTerm getRecipientCCTerm(String SearchExpression) throws NullException, StringException
	{
		return new StringRecipientTerm(RecipientTerm.ADDRESS_CC, SearchExpression);
	}
	public static final StringRecipientTerm getRecipientBCCTerm(String SearchExpression) throws NullException, StringException
	{
		return new StringRecipientTerm(RecipientTerm.ADDRESS_BCC, SearchExpression);
	}
	
	protected StringRecipientTerm() { }
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.RecipientStringTerm(RecipientTerm.getType(termType), termString);
	}

	protected final StringRecipientTerm clone()
	{
		return new StringRecipientTerm(termType, termString);
	}
}