package com.spire.mail;

import javax.mail.Message;
import javax.mail.Session;
import java.io.IOException;
import java.util.Properties;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.HashList;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.StringException;
import javax.mail.MessagingException;
import com.spire.ex.PermissionException;

public final class EmailSender
{
	private final EmailSenderThread senderThread;
	private final HashList<String, EmailQueue> senderQueue;
	private final HashList<String, EmailAccountInternal> senderAccounts;
	
	private boolean senderRunning;
	private EmailAccountInternal senderDefault;

	public EmailSender() throws PermissionException
	{
		Security.check("io.mail.sender");
		senderRunning = true;
		senderThread = new EmailSenderThread(this);
		senderQueue = new HashList<String, EmailQueue>();
		senderAccounts = new HashList<String, EmailAccountInternal>();
		senderThread.start();
	}
	public EmailSender(EmailAccount DefaultAccount) throws PermissionException, NullException, IOException
	{
		this();
		setDefaultAccount(DefaultAccount);
	}
	public EmailSender(EmailAccount DefaultAccount, EmailSettings DefaultSettings) throws PermissionException, NullException, IOException
	{
		this();
		setDefaultAccount(DefaultAccount, DefaultSettings);
	}

	public final void stopSender() throws PermissionException
	{
		Security.check("io.mail.sender.stop", Thread.currentThread().getName());
		senderRunning = false;
		senderThread.interrupt();
		Reporter.debug(Reporter.REPORTER_EMAIL, "Stopping EmailSender \"" + toString() + "\"");
	}
	public final void clearAccounts() throws PermissionException
	{
		Security.check("io.mail.acc.clear");
		for(int a = 0; a < senderAccounts.size(); a++)
		{
			senderAccounts.get(a).accountSession = null;
			if(senderAccounts.get(a).accountTransport.isConnected()) try
			{
				senderAccounts.get(a).accountTransport.close();
			}
			catch (MessagingException Exception) { }
			senderAccounts.get(a).accountTransport = null;
		}
		senderAccounts.clear();
		Reporter.info(Reporter.REPORTER_EMAIL, "Cleared all Accounts on EmailSender \"" + toString() + "\"");
	}
	public final void clearDefaultAccount() throws PermissionException
	{
		Security.check("io.mail.def.clear");
		senderDefault.accountSession = null;
		if(senderDefault.accountTransport.isConnected()) try
		{
			senderDefault.accountTransport.close();
		}
		catch (MessagingException Exception) { }
		senderDefault.accountTransport = null;
		senderDefault = null;
		Reporter.info(Reporter.REPORTER_EMAIL, "Cleared Default Account on EmailSender \"" + toString() + "\"");
	}
	public final void removeAccount(EmailAddress RemoveAccount) throws NullException
	{
		if(RemoveAccount == null) throw new NullException("RemoveAccount");
		removeAccount(RemoveAccount.toString());
	}
	public final void removeAccount(EmailAccount RemoveAccount) throws NullException
	{
		if(RemoveAccount == null) throw new NullException("RemoveAccount");
		removeAccount(RemoveAccount.accountAddress);
	}
	public final void removeAccount(String RemoveAccount) throws NullException, StringException
	{
		if(RemoveAccount == null) throw new NullException("RemoveAccount");
		if(RemoveAccount.isEmpty()) throw new StringException("RemoveAccount");
		EmailAccountInternal a = senderAccounts.get(RemoveAccount);
		if(a != null)
		{
			a.accountSession = null;
			if(a.accountTransport.isConnected()) try
			{
				a.accountTransport.close();
			}
			catch (MessagingException Exception) { }
			a.accountTransport = null;
			Reporter.info(Reporter.REPORTER_EMAIL, "Removed Account \"" + a.accountInstance.accountAddress + "\" from EmailSender \"" + toString() + "\"");
			a = null;
		}
	}
	public final void addAccount(EmailAccount Account) throws NullException, IOException, PermissionException
	{
		if(Account == null) throw new NullException("Account");
		addAccount(Account, Account.accountSettings);
	}
	public final void setDefaultAccount(EmailAccount Account) throws NullException, IOException, PermissionException
	{
		if(Account == null) throw new NullException("Account");
		setDefaultAccount(Account, Account.accountSettings);
	}
	public final void addAccount(EmailAccount Account, EmailSettings Settings) throws NullException, IOException, PermissionException
	{
		if(Account == null) throw new NullException("Account");
		if(Settings == null) throw new NullException("Settings");
		Security.check("io.mail.acc.add", Account.accountAddress);
		EmailAccountInternal a = new EmailAccountInternal(Account, Settings);
		activateAccount(a);
		senderAccounts.add(a);
		Reporter.info(Reporter.REPORTER_EMAIL, "Added Account \"" + Account.accountAddress + "\" to EmailSender \"" + toString() + "\"");
	}
	public final void setDefaultAccount(EmailAccount Account, EmailSettings Settings) throws NullException, IOException, PermissionException
	{
		if(Account == null) throw new NullException("Account");
		if(Settings == null) throw new NullException("Settings");
		Security.check("io.mail.def.set", Account.accountAddress);
		senderDefault = new EmailAccountInternal(Account, Settings);
		activateAccount(senderDefault);
		Reporter.info(Reporter.REPORTER_EMAIL, "Set Account \"" + Account.accountAddress + "\" as the Default Account on EmailSender \"" + toString() + "\"");
	}
	
	public final boolean isRunning()
	{
		return senderRunning;
	}
	public final boolean containsMail()
	{
		return !senderQueue.isEmpty();
	}
	public final boolean hasDefaultAccount()
	{
		return senderDefault != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailSender && ((EmailSender)CompareObject).senderAccounts.equals(senderAccounts) && ((EmailSender)CompareObject).senderQueue.equals(senderQueue) &&
			   hashCode() == CompareObject.hashCode();
	}
	public final boolean containsAccount(String ContainAccount)
	{
		return ContainAccount != null && (senderDefault.accountInstance.accountAddress.equals(ContainAccount) || senderAccounts.containsKey(ContainAccount));
	}
	public final boolean containsAccount(EmailAddress ContainAccount)
	{
		return ContainAccount != null && (senderDefault.accountInstance.accountAddress.equals(ContainAccount.toString()) || senderAccounts.containsKey(ContainAccount.toString()));
	}
	public final boolean containsAccount(EmailAccount ContainAccount)
	{
		return ContainAccount != null && (senderDefault.accountInstance.equals(ContainAccount) || senderAccounts.containsKey(ContainAccount.accountAddress));
	}
	
	public final int hashCode()
	{
		return (senderDefault != null ? senderDefault.hashCode() : 0) + senderQueue.hashCode() + senderAccounts.hashCode();
	}
	public final int getQueueSize()
	{
		return senderQueue.size();
	}
	public final int getAccountsSize()
	{
		return senderAccounts.size();
	}
	
	public final String toString()
	{
		return "EmailSender(ES) Q:" + senderQueue.size() + " " + (senderDefault != null ? senderDefault.accountInstance.accountAddress : "NDS");
	}
	
	public final EmailAccount getDefaultAccount()
	{
		return senderDefault.accountInstance;
	}
	
	public final EmailQueue sendEmail(Email SendEmail) throws NullException, IOException, PermissionException
	{
		if(SendEmail == null) throw new NullException("SendEmail");
		if(senderDefault == null)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "There is not default account set!");
			throw new IOException("There is not default account set!");
		}
		return sendEmail(senderDefault.accountInstance, senderDefault.accountSettings, SendEmail);
	}
	public final EmailQueue sendEmail(EmailAddress SendAccount, Email SendEmail) throws NullException, IOException, PermissionException
	{
		if(SendAccount == null) throw new NullException("SendAccount");
		return sendEmail(SendAccount.toString(), SendEmail);
	}
	public final EmailQueue sendEmail(EmailAccount SendAccount, Email SendEmail) throws NullException, IOException, PermissionException
	{
		if(SendEmail == null) throw new NullException("SendEmail");
		if(SendAccount == null) throw new NullException("SendAccount");
		return sendEmail(SendAccount, SendAccount.accountSettings, SendEmail);
	}
	public final EmailQueue sendEmail(String SendAccount, Email SendEmail) throws NullException, StringException, IOException, PermissionException
	{
		Security.check("io.mail.send", Thread.currentThread().getName());
		if(SendEmail == null) throw new NullException("SendEmail");
		if(SendAccount == null) throw new NullException("SendAccount");
		if(SendAccount.isEmpty()) throw new StringException("SendAccount");
		EmailQueue a = senderQueue.get(SendAccount);
		if(a != null)
		{
			a.queueEmail.add(SendEmail);
			return a;
		}
		EmailAccountInternal b = senderAccounts.get(SendAccount);
		if(b != null)
		{
			a = new EmailQueue(b.accountInstance, SendEmail);
			senderQueue.add(a);
			senderThread.interrupt();
			return a;
		}
		return sendEmail(SendEmail);
	}
	public final EmailQueue sendEmail(EmailAccount SendAccount, EmailSettings SendSettings, Email SendEmail) throws NullException, IOException, PermissionException
	{
		Security.check("io.mail.send", Thread.currentThread().getName());
		if(SendEmail == null) throw new NullException("SendEmail");
		if(SendAccount == null) throw new NullException("SendAccount");
		EmailQueue a = senderQueue.get(SendAccount.accountAddress);
		if(a != null)
		{
			a.queueEmail.add(SendEmail);
			return a;
		}
		EmailAccountInternal b = senderAccounts.get(SendAccount.accountAddress);
		if(b != null)
		{
			a = new EmailQueue(b.accountInstance, SendEmail);
			senderQueue.add(a);
			senderThread.interrupt();
			return a;
		}
		if(SendSettings == null) throw new NullException("SendSettings");
		b = new EmailAccountInternal(SendAccount, SendSettings);
		activateAccount(b);
		senderAccounts.add(b);
		a = new EmailQueue(SendAccount, SendEmail);
		senderQueue.add(a);
		senderThread.interrupt();
		return a;
	}
	
	protected final EmailSender clone() throws CloneException
	{
		throw new CloneException("Cannot clone EmailAccount data!");
	}
	
	private static final void activateAccount(EmailAccountInternal Account) throws IOException
	{
		if(Account.accountSession != null) return;
		Properties a = new Properties();
		Account.accountSettings.addSettings(a);
		Session b = Account.accountSettings.settingsType == EmailSettings.EMAIL_SMTP_AUTH ? Session.getInstance(a, Account.accountInstance.getEmailAuth()) : Session.getInstance(a);
		try
		{
			Account.accountTransport = b.getTransport(Account.accountSettings.getSettingsType());
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
		Account.accountSession = b;
		a = null;
	}
	
	private static final class EmailSenderThread extends Thread
	{
		private final EmailSender threadInstance;
		
		public final void run()
		{
			while(threadInstance.senderRunning)
			{
				if(threadInstance.senderQueue.isEmpty()) try
				{
					Thread.sleep(0x7FFFFFFFL);
				}
				catch (InterruptedException Exception) { }
				else
					sendEmail(threadInstance.senderQueue.remove(0));
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException Exception) { }
			}
		}
		
		private EmailSenderThread(EmailSender Sender)
		{
			threadInstance = Sender;
			setPriority(2);
			setName("EmailSenderThread");
		}
	
		private final void sendEmail(EmailQueue Queue)
		{
			EmailAccountInternal a = threadInstance.senderAccounts.get(Queue.queueAccount.accountAddress);
			if(a == null)
			{
				if(threadInstance.senderDefault == null)
				{
					Reporter.error(Reporter.REPORTER_EMAIL, "Cannot send email as account specified does not exist and there is no default account set!");
					Queue.queueError = new IOException("Cannot send email as account specified does not exist and there is no default account set!");
					Queue.queueAttempted = true;
					Queue.queueSent = false;
					return;
				}
				a = threadInstance.senderDefault;
			}
			if(a.accountSession == null || a.accountTransport == null)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, "Cannot send email as account specified is not connected!");
				Queue.queueError = new IOException("Cannot send email as account specified is not connected!");
				Queue.queueAttempted = true;
				Queue.queueSent = false;
				a = null;
				return;
			}
			try
			{
				if(!a.accountTransport.isConnected()) a.openTransport();
				Message b = null;
				for(int c = 0; c < Queue.queueEmail.size(); c++)
				{
					b = Queue.queueEmail.get(c).getMessage(a.accountSession);
					a.accountTransport.sendMessage(b, b.getAllRecipients());
					Reporter.debug(Reporter.REPORTER_EMAIL, "Sent email \"" + Queue.queueEmail.get(c).toString() + "\"");
					b = null;
				}
				a.accountTransport.close();
				Queue.queueAttempted = true;
				Queue.queueSent = true;
				Reporter.info(Reporter.REPORTER_EMAIL, "Sent " + Queue.queueEmail.size() + " emails using account \"" + a.accountInstance.accountAddress + "\"!");
				a = null;
			}
			catch (IOException Exception)
			{
				a = null;
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
				if(!Queue.queueAttempted)
				{
					Queue.queueAttempted = true;
					threadInstance.senderQueue.add(Queue);
					return;
				}
				Queue.queueSent = false;
				Queue.queueAttempted = true;
				Queue.queueError = Exception;
			}
			catch (MessagingException Exception)
			{
				a = null;
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
				if(!Queue.queueAttempted)
				{
					Queue.queueAttempted = true;
					threadInstance.senderQueue.add(Queue);
					return;
				}
				Queue.queueSent = false;
				Queue.queueAttempted = true;
				Queue.queueError = Exception;
			}
		}
	}
}