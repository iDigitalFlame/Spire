package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.mail.search.SearchTerm;

public final class HeaderTerm extends SubjectTerm
{
	private String termHeader;
	
	public HeaderTerm(String HeaderName, String SearchExpression) throws NullException, StringException
	{
		super(SearchExpression);
		if(HeaderName == null) throw new NullException("HeaderName");
		if(HeaderName.isEmpty()) throw new StringException("HeaderName");
		termHeader = HeaderName;
	}
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		super.readStorage(InStream, StorageEncoder);
		termHeader = StorageEncoder.readString(InStream);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		super.writeStorage(OutStream, StorageEncoder);
		StorageEncoder.writeString(OutStream, termHeader);
	}

	public final int hashCode()
	{
		return super.hashCode() + termHeader.hashCode();
	}
	
	protected HeaderTerm() { }
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.HeaderTerm(termHeader, termString);
	}

	protected final HeaderTerm clone()
	{
		return new HeaderTerm(termHeader, termString);
	}
}