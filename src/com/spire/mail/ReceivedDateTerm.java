package com.spire.mail;

import java.util.Date;
import java.io.IOException;
import com.spire.util.Stamp;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import javax.mail.search.SearchTerm;

public class ReceivedDateTerm extends EmailTerm
{
	public static final byte COMPARE_EQUALS = 3;
	public static final byte COMPARE_LESS_THAN = 2;
	public static final byte COMPARE_NOT_EQUALS = 4;
	public static final byte COMPARE_LESS_EQUALS = 1;
	public static final byte COMPARE_GREATER_THAN = 5;
	public static final byte COMPARE_GREATER_EQUALS = 6;
	
	protected final Stamp termDate;
	
	protected byte termType;
	
	public ReceivedDateTerm(byte CompareType, long TermDate) throws NumberException
	{
		this(CompareType);
		termDate.updateStamp(TermDate);
	}
	public ReceivedDateTerm(byte CompareType, Date TermDate) throws NullException, NumberException
	{
		this(CompareType);
		if(TermDate == null) throw new NullException("TermDate");
		termDate.updateStamp(TermDate);
	}
	public ReceivedDateTerm(byte CompareType, Stamp TermDate) throws NullException, NumberException
	{
		this(CompareType);
		if(TermDate == null) throw new NullException("TermDate");
		termDate.updateStamp(TermDate);
	}
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		termType = StorageEncoder.readByte(InStream);
		termDate.readStorage(InStream, StorageEncoder);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByte(OutStream, termType);
		termDate.writeStorage(OutStream, StorageEncoder);
	}
	
	public final int hashCode()
	{
		return termDate.hashCode() + termType;
	}
	
	protected ReceivedDateTerm()
	{
		termDate = new Stamp();
	}
	protected ReceivedDateTerm(byte CompareType) throws NumberException
	{
		if(CompareType <= 0) throw new NumberException("CompareType", CompareType, true);
		if(CompareType > COMPARE_GREATER_EQUALS) throw new NumberException("CompareType", CompareType, COMPARE_LESS_EQUALS, COMPARE_GREATER_EQUALS);
		termDate = new Stamp();
		termType = CompareType;
	}
	
	protected SearchTerm getTerm()
	{
		return new javax.mail.search.ReceivedDateTerm(termType, termDate.getDate());
	}

	protected ReceivedDateTerm clone()
	{
		return new ReceivedDateTerm(termType, termDate);
	}
}