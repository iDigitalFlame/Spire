package com.spire.mail;

import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.mail.search.SearchTerm;

public final class StringFromTerm extends SubjectTerm
{	
	public StringFromTerm(String SearchExpression) throws NullException, StringException
	{
		super(SearchExpression);
	}
	
	protected StringFromTerm() { }
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.FromStringTerm(termString);
	}

	protected final StringFromTerm clone()
	{
		return new StringFromTerm(termString);
	}
}