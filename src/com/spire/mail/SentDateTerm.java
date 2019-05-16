package com.spire.mail;

import java.util.Date;
import com.spire.util.Stamp;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import javax.mail.search.SearchTerm;

public final class SentDateTerm extends ReceivedDateTerm
{
	public SentDateTerm(byte CompareType, long TermDate) throws NumberException
	{
		super(CompareType, TermDate);
	}
	public SentDateTerm(byte CompareType, Date TermDate) throws NullException, NumberException
	{
		super(CompareType, TermDate);
	}
	public SentDateTerm(byte CompareType, Stamp TermDate) throws NullException, NumberException
	{
		super(CompareType, TermDate);
	}
	
	protected final SearchTerm getTerm()
	{
		return new javax.mail.search.SentDateTerm(termType, termDate.getDate());
	}

	protected final SentDateTerm clone()
	{
		return new SentDateTerm(termType, termDate);
	}
}