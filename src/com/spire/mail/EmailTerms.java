package com.spire.mail;

import com.spire.io.Item;
import java.io.IOException;
import java.util.ArrayList;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import javax.mail.search.SearchTerm;

public final class EmailTerms extends Item
{
	public static final byte ITEM_CLASS_ID = 20;
	
	private final ArrayList<EmailTermFlags> termItems;
	
	private SearchTerm termTemp;
	
	public EmailTerms()
	{
		super(ITEM_CLASS_ID);
		termItems = new ArrayList<EmailTermFlags>();
	}
	
	public final void removeTerm(int RemoveIndex) throws NumberException
	{
		if(RemoveIndex < 0) throw new NumberException("RemoveIndex", RemoveIndex, true);
		if(RemoveIndex > termItems.size()) throw new NumberException("RemoveIndex", RemoveIndex, 0, termItems.size());
		termItems.remove(RemoveIndex);
	}
	public final void addORTerm(EmailTerm TermBase) throws NullException
	{
		addORTerm(TermBase, false);
	}
	public final void addANDTerm(EmailTerm TermBase) throws NullException
	{
		addANDTerm(TermBase, false);
	}
	public final void removeTerm(EmailTerm RemoveTerm) throws NullException
	{
		if(RemoveTerm == null) throw new NullException("RemoveTerm");
		termItems.remove(RemoveTerm);
	}
	public final void addORTerm(EmailTerm TermBase, boolean NotTerm) throws NullException
	{
		if(TermBase == null) throw new NullException("TermBase");
		termItems.add(new EmailTermFlags(TermBase, NotTerm, false));
		termTemp = null;
	}
	public final void addANDTerm(EmailTerm TermBase, boolean NotTerm) throws NullException
	{
		if(TermBase == null) throw new NullException("TermBase");
		termItems.add(new EmailTermFlags(TermBase, NotTerm, true));
		termTemp = null;
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailTerms && ((EmailTerms)CompareObject).termItems.equals(termItems);
	}
	public final boolean containsTerm(EmailTerm ContainsTerm)
	{
		return termItems.contains(ContainsTerm);
	}
	
	public final int hashCode()
	{
		return termItems.hashCode();
	}
	public final int getTermsSize()
	{
		return termItems.size();
	}
	
	public final String toString()
	{
		return "EmailTerms(" + getItemID() + ") TS: " + termItems.size();
	}

	protected final void readItemFailure()
	{
		termItems.clear();
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		itemEncoder.writeStorageList(InStream, termItems);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeStorageList(OutStream, termItems);
	}

	protected final SearchTerm getTerm()
	{
		if(termTemp == null && !termItems.isEmpty())
			termTemp = EmailTerm.createTermFromList(termItems);
		return termTemp;
	}

	protected final EmailTerms getCopy()
	{
		EmailTerms a = new EmailTerms();
		a.termItems.addAll(termItems);
		return a;
	}
}