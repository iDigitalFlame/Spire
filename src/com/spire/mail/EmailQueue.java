package com.spire.mail;

import java.util.ArrayList;
import com.spire.util.HashKey;
import com.spire.ex.CloneException;

public final class EmailQueue implements HashKey<String>
{
	protected final EmailAccount queueAccount;
	protected final ArrayList<Email> queueEmail;
	
	protected boolean queueSent;
	protected Throwable queueError;
	protected boolean queueAttempted;
	
	public final boolean isSent()
	{
		return queueSent && queueAttempted;
	}
	public final boolean isError()
	{
		return !queueSent && queueAttempted;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailQueue && CompareObject.hashCode() == hashCode();
	}
	
	public final int hashCode()
	{
		return queueAccount.hashCode() + queueEmail.hashCode() + (queueAttempted ? 1 : 5) + (queueSent ? 2 : 10);
	}
	
	public final String getKey()
	{
		return queueAccount.accountAddress;
	}
	public final String toString()
	{
		return "EmailQueue(ES) " + queueEmail.size() + " " + (isSent() ? "S" : isError() ? "E" : "W");
	}

	public final Throwable getError()
	{
		return queueError;
	}
	
	protected EmailQueue(EmailAccount Account, Email QueueEmail)
	{
		queueAccount = Account;
		queueEmail = new ArrayList<Email>();
		queueEmail.add(QueueEmail);
	}
	
	protected final EmailQueue clone() throws CloneException
	{
		throw new CloneException("EmailQueue references cannot be cloned!");
	}
}