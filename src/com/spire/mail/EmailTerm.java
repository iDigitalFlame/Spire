package com.spire.mail;

import java.util.List;
import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.io.Streamable;
import javax.mail.search.OrTerm;
import javax.mail.search.AndTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.SearchTerm;

public abstract class EmailTerm implements Storage
{
	public abstract void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException;
	public abstract void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailTerm && CompareObject.getClass().equals(getClass()) && hashCode() == CompareObject.hashCode();
	}
	
	public abstract int hashCode();
	
	public final String toString()
	{
		return "EmailTerm " + getClass().getSimpleName() + " (" + hashCode() + ")";
	}
	
	protected abstract SearchTerm getTerm();
	
	protected abstract EmailTerm clone();
	
	protected static final SearchTerm createTermFromList(List<EmailTermFlags> TermList)
	{
		SearchTerm a = null, b = null;
		if(!TermList.isEmpty())
		{
			for(int c = 0; c < TermList.size(); c++)
			{
				b = TermList.get(c).termBase.getTerm();
				if(b != null)
				{
					if(c == 0 || a == null)
						a = TermList.get(c).termFlags.getTagB() ? new NotTerm(b) : b;
					else
					{
						if(TermList.get(c).termFlags.getTagA())
							a = new AndTerm(a, TermList.get(c).termFlags.getTagB() ? new NotTerm(b) : b);
						else
							a = new OrTerm(a, TermList.get(c).termFlags.getTagB() ? new NotTerm(b) : b);
					}
				}
			}
		}
		return a;
	}
}