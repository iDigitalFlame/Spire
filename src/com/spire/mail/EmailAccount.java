package com.spire.mail;

import com.spire.io.Item;
import java.io.IOException;
import com.spire.util.HashKey;
import com.spire.io.Streamable;
import javax.mail.Authenticator;
import com.spire.cred.Credentials;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.mail.PasswordAuthentication;

public final class EmailAccount extends Item implements HashKey<String>
{
	public static final byte ITEM_CLASS_ID = 18;
	
	protected String accountAddress;
	protected Credentials accountDetails;
	protected EmailSettings accountSettings;

	public EmailAccount(String AccountAddress) throws NullException, StringException
	{
		this(AccountAddress, (EmailSettings)null);
	}
	public EmailAccount(Credentials AccountDetails) throws NullException, StringException
	{
		super(ITEM_CLASS_ID);
		if(AccountDetails == null) throw new NullException("AccountDetails");
		accountDetails = AccountDetails;
		accountAddress = AccountDetails.getUserName();
	}
	public EmailAccount(String AccountAddress, EmailSettings Settings) throws NullException, StringException
	{
		super(ITEM_CLASS_ID);
		if(AccountAddress == null) throw new NullException("AccountAddress");
		if(AccountAddress.isEmpty()) throw new StringException("AccountAddress");
		accountAddress = AccountAddress;
		accountSettings = Settings;
	}
	public EmailAccount(String AccountAddress, String AccountPassword) throws NullException, StringException
	{
		this(AccountAddress, AccountAddress, AccountPassword, null);
	}
	public EmailAccount(String AccountAddress, Credentials AccountDetails) throws NullException, StringException
	{
		this(AccountAddress, AccountDetails, null);
	}
	public EmailAccount(Credentials AccountDetails, EmailSettings Settings) throws NullException, StringException
	{
		this(AccountDetails);
		accountSettings = Settings;
	}
	public EmailAccount(String AccountAddress, String AccountUsername, String AccountPassword) throws NullException, StringException
	{
		this(AccountAddress, new Credentials(AccountUsername, AccountPassword), null);
	}
	public EmailAccount(String AccountAddress, String AccountPassword, EmailSettings Settings) throws NullException, StringException
	{
		this(AccountAddress, AccountAddress, AccountPassword, Settings);
	}
	public EmailAccount(String AccountAddress, Credentials AccountDetails, EmailSettings Settings) throws NullException, StringException
	{
		super(ITEM_CLASS_ID);
		if(AccountDetails == null) throw new NullException("AccountDetails");
		if(AccountAddress == null) throw new NullException("AccountAddress");
		if(AccountAddress.isEmpty()) throw new StringException("AccountAddress");
		accountDetails = AccountDetails;
		accountAddress = AccountAddress;
		accountSettings = Settings;
	}
	public EmailAccount(String AccountAddress, String AccountUsername, String AccountPassword, EmailSettings Settings) throws NullException, StringException
	{
		this(AccountAddress, new Credentials(AccountUsername, AccountPassword), Settings);
	}
	
	public final void setSttings(EmailSettings Settings)
	{
		accountSettings = Settings;
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailAccount && ((EmailAccount)CompareObject).accountAddress.equals(accountAddress) && (accountDetails == null ? true :((EmailAccount)CompareObject).accountDetails.equals(accountDetails));
	}
	
	public final int hashCode()
	{
		return accountAddress.hashCode() + accountDetails.hashCode();
	}

	public final String getKey()
	{
		return accountAddress;
	}
	public final String toString()
	{
		return "EmailAccount(" + getItemID() + ") " + accountAddress;
	}

	public final EmailSettings getSettings()
	{
		return accountSettings;
	}
	
	protected EmailAccount()
	{
		super(ITEM_CLASS_ID);
	}
	
	protected final void readItemFailure()
	{
		accountAddress = null;
		accountDetails = null;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		accountAddress = itemEncoder.readString(InStream);
		accountDetails = (Credentials)Item.getNextItemByID(InStream, Credentials.ITEM_CLASS_ID);
		accountSettings = (EmailSettings)Item.getNextItemByID(InStream, EmailSettings.ITEM_CLASS_ID);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeString(OutStream, accountAddress);
		writeNullItem(accountDetails, OutStream);
		writeNullItem(accountSettings, OutStream);
	}

	protected final Authenticator getEmailAuth()
	{
		return new EmailAccountAuth(accountDetails);
	}
	
	protected final EmailAccount getCopy()
	{
		return null;
	}
	
	private static final class EmailAccountAuth extends Authenticator
	{
		private final PasswordAuthentication accountAuth;
		
		protected final PasswordAuthentication getPasswordAuthentication()
		{
			return accountAuth;
		}
		
		private EmailAccountAuth(Credentials UserAuth)
		{
			accountAuth = new PasswordAuthentication(UserAuth.getUserName(), UserAuth.getUserPassword());
		}
	}
}