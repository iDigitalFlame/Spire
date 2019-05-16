package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.mail.search.SearchTerm;

public class SubjectTerm extends EmailTerm
{
	protected String termString;
	
	public SubjectTerm(String SearchExpression) throws NullException, StringException
	{
		if(SearchExpression == null) throw new NullException("SearchExpression");
		if(SearchExpression.isEmpty()) throw new StringException("SearchExpression");
		termString = SearchExpression;
	}
	
	public void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		termString = StorageEncoder.readString(InStream);
	}
	public void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeString(OutStream, termString);
	}

	public int hashCode()
	{
		return termString.hashCode();
	}
	
	protected SubjectTerm() { }
	
	protected SearchTerm getTerm()
	{
		return new javax.mail.search.SubjectTerm(termString);
	}

	protected SubjectTerm clone()
	{
		return new SubjectTerm(termString);
	}
}