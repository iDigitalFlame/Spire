package com.spire.mail;

import java.io.IOException;
import java.util.ArrayList;
import com.spire.io.Encoder;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import javax.mail.search.SearchTerm;

public final class GroupTerm extends EmailTerm
{
	private final ArrayList<EmailTermFlags> termList;

	private SearchTerm termTemp;
	
	public GroupTerm()
	{
		termList = new ArrayList<EmailTermFlags>();
	}
	
	public final void removeTerm(int RemoveIndex) throws NumberException
	{
		if(RemoveIndex < 0) throw new NumberException("RemoveIndex", RemoveIndex, true);
		if(RemoveIndex > termList.size()) throw new NumberException("RemoveIndex", RemoveIndex, 0, termList.size());
		termList.remove(RemoveIndex);
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
		termList.remove(RemoveTerm);
	}
	public final void addORTerm(EmailTerm TermBase, boolean NotTerm) throws NullException
	{
		if(TermBase == null) throw new NullException("TermBase");
		termList.add(new EmailTermFlags(TermBase, NotTerm, false));
		termTemp = null;
	}
	public final void addANDTerm(EmailTerm TermBase, boolean NotTerm) throws NullException
	{
		if(TermBase == null) throw new NullException("TermBase");
		termList.add(new EmailTermFlags(TermBase, NotTerm, true));
		termTemp = null;
	}
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.readStorageList(InStream, termList);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeStorageList(OutStream, termList);
	}

	public final boolean containsTerm(EmailTerm ContainsTerm)
	{
		return termList.contains(ContainsTerm);
	}
	
	public final int hashCode()
	{
		return termList.hashCode();
	}
	public final int getTermsSize()
	{
		return termList.size();
	}

	protected final SearchTerm getTerm()
	{
		if(termTemp == null && !termList.isEmpty())
			termTemp = createTermFromList(termList);
		return termTemp;
	}

	protected final GroupTerm clone()
	{
		GroupTerm a = new GroupTerm();
		a.termList.addAll(termList);
		return a;
	}
}