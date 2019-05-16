package com.spire.mail;

import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.mail.search.SearchTerm;

public final class BodyTerm extends SubjectTerm
{
	public BodyTerm(String SearchExpression) throws NullException, StringException
	{
		super(SearchExpression);
	}
	
	protected BodyTerm() { }
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.BodyTerm(termString);
	}

	protected final BodyTerm clone()
	{
		return new BodyTerm(termString);
	}
}