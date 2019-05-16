package com.spire.mail;

import javax.mail.Store;
import javax.mail.Session;
import java.io.IOException;
import javax.mail.Transport;
import com.spire.util.HashKey;
import javax.mail.MessagingException;

final class EmailAccountInternal implements HashKey<String>
{
	protected final EmailAccount accountInstance;
	protected final EmailSettings accountSettings;
	
	protected Store accountStore;
	protected Session accountSession;
	protected Transport accountTransport;
	
	public final String getKey()
	{
		return accountInstance.getKey();
	}
	
	protected EmailAccountInternal(EmailAccount Account, EmailSettings Settings)
	{
		accountInstance = Account;
		accountSettings = Settings;
	}
	
	protected final void openTransport() throws MessagingException, IOException
	{
		try
		{
			if(accountSettings.settingsType == EmailSettings.EMAIL_SMTP)
				accountTransport.connect();
			else
				accountTransport.connect(accountSettings.settingsServer, accountInstance.accountDetails.getUserName(), accountInstance.accountDetails.getUserPassword());
		}
		catch (Exception Exception)
		{
			if(Exception instanceof MessagingException || Exception instanceof IOException)
				throw Exception;
			throw new IOException(Exception);
		}
	}
}