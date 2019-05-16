package com.spire.mail;

import javax.mail.Flags;

import com.spire.io.Item;

import javax.mail.Folder;
import javax.mail.Message;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Flags.Flag;

import com.spire.log.Reporter;
import com.spire.util.BoolTag;
import com.spire.util.HashKey;
import com.spire.sec.Security;
import com.spire.io.Streamable;
import com.spire.util.Constants;

import javax.mail.search.AndTerm;

import com.spire.ex.NullException;

import javax.mail.search.SearchTerm;

import com.sun.mail.imap.IMAPFolder;
import com.spire.ex.NumberException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.spire.ex.PermissionException;

import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;

public final class EmailFolder extends Item implements HashKey<String>, MessageCountListener
{
	public static final byte ITEM_CLASS_ID = 22;
	
	private static final SearchTerm UNREAD_FLAG = new javax.mail.search.FlagTerm(new Flags(Flag.SEEN), false);
	
	private final BoolTag folderFlags;
	private final EmailLock folderLock;
	private final ArrayList<Email> folderEmails;
	private final ArrayList<EmailFolderListener> folderListeners;
	
	protected short folderRate;
	protected boolean folderIsIdle;
	protected boolean folderInQueue;
	protected Folder folderInstance;
	protected EmailTerms folderTerms;
	
	private String folderName;
	private long folderNextFetch;
	private String folderAccount;
	private EmailFolderDownloader folderDownloader;
	
 	public final void openFolder() throws PermissionException
	{
		Security.check("io.mail.folder.close", folderName);
		folderFlags.setTagE(false);
	}
	public final void clearEmail() throws PermissionException
	{
		Security.check("io.mail.folder.clear", folderName);
		folderEmails.clear();
	}
	public final void clearTerms() throws PermissionException
	{
		Security.check("io.mail.folder.clear", folderName);
		folderTerms = null;
	}
	public final void closeFolder() throws PermissionException
	{
		Security.check("io.mail.folder.close", folderName);
		folderFlags.setTagE(true);
	}
	public final void messagesAdded(MessageCountEvent MessageEvent)
	{
		folderLock.lockCounts++;
		Message[] a = MessageEvent.getMessages();
		try
		{
			addAndPassToListeners(a, true);
			folderLock.lockCounts--;
			if(folderLock.lockCounts == 0 && folderInstance.isOpen() && !folderIsIdle)
				folderInstance.close(true);
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
		}
		a = null;
	}
	public final void messagesRemoved(MessageCountEvent MessageEvent) { }
	public final void stopDownloadingEmail() throws PermissionException
	{
		Security.check("io.mail.folder.sdown", folderName);
		if(folderDownloader != null)
		{
			folderDownloader.interrupt();
			folderDownloader = null;
		}
	}
	public final void expungeFolder() throws PermissionException, IOException
	{
		Security.check("io.mail.folder.expunge", folderName);
		try
		{
			if(!folderInstance.isOpen())
				folderInstance.open(Folder.READ_WRITE);
			addAndPassToListeners(folderInstance.expunge(), false);
			for(int a = 0; a < folderEmails.size(); a++)
				if(folderEmails.get(a).isDeleted())
				{
					folderEmails.remove(a);
					a--;
				}
			Reporter.debug(Reporter.REPORTER_EMAIL, "Expunged folder \"" + folderName + "\"");
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setDeleteAfterRead(boolean DeleteAfterRead) throws PermissionException
	{
		Security.check("io.mail.folder.expunge-read", folderName);
		folderFlags.setTagB(DeleteAfterRead);
	}
	public final void removeEmail(Email RemoveEmail) throws NullException, PermissionException
	{
		Security.check("io.mail.folder.rem", folderName);
		if(RemoveEmail == null) throw new NullException("RemoveEmail");
		if(folderEmails.remove(RemoveEmail))
			RemoveEmail.setDeleted(true);
	}
	public final void setNoIMAPFetch(boolean NoIMAPFetch) throws IOException, PermissionException
	{
		Security.check("io.mail.folder.nofetchimap", folderName);
		if(!canIDLE() && !folderFlags.getTagC())
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "Folder does not have IDLE ability!");
			throw new IOException("Folder does not have IDLE ability!");
		}
		folderFlags.setTagC(true);
	}
	public final void setDownloadSeperateThread(boolean SeperateThread) throws PermissionException
	{
		Security.check("io.mail.folder.down-st", folderName);
		folderFlags.setTagF(SeperateThread);
	}
	public final void deleteFolder(boolean DeleteSubfolders) throws IOException, PermissionException
	{
		Security.check("io.mail.folder.del", folderName);
		try
		{
			if(folderInstance.isOpen())
				folderInstance.close(true);
			if(!folderInstance.delete(DeleteSubfolders))
			{
				Reporter.error(Reporter.REPORTER_EMAIL, "Cannot delete folder!");
				throw new IOException("Cannot delete folder!");
			}
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setFolderDownloadAll(boolean DownloadAll) throws IOException, PermissionException
	{
		Security.check("io.mail.folder.downall", folderName);
		if(folderFlags.getTagD())
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "Messages already are being downloaded or have been downloaded!");
			throw new IOException("Messages already are being downloaded or have been downloaded!");
		}
		folderFlags.setTagA(true);
	}
	public final void removeFolderListener(int ListenerIndex) throws NumberException, PermissionException
	{
		if(ListenerIndex < 0) throw new NumberException("ListenerIndex", ListenerIndex, true);
		if(ListenerIndex > folderListeners.size()) throw new NumberException("ListenerIndex", ListenerIndex, 0, folderListeners.size());
		EmailFolderListener a = folderListeners.get(ListenerIndex);
		Security.check("io.mail.folder.lister.rem", a.getClass());
		folderListeners.remove(ListenerIndex);
		a = null;
	}
	public final void setFolderFetchRate(int FetchRateSeconds) throws PermissionException, NumberException
	{
		Security.check("io.mail.folder.rate", folderName);
		if(FetchRateSeconds < 0) throw new NumberException("FetchRateSeconds", FetchRateSeconds, true);
		if(FetchRateSeconds > Constants.MAX_USHORT_SIZE) throw new NumberException("FetchRateSeconds", FetchRateSeconds, 0, Constants.MAX_USHORT_SIZE);
		folderRate = (short)FetchRateSeconds;
	}
	public final void setFolderSearchTerms(EmailTerms FolderTerms) throws NullException, PermissionException
	{
		Security.check("io.mail.folder.terms", folderName);
		if(FolderTerms == null) throw new NullException("FolderTerms");
		folderTerms = FolderTerms;
	}
	public final void addFolderListener(EmailFolderListener Listener) throws NullException, PermissionException
	{
		if(Listener == null) throw new NullException("Listener");
		Security.check("io.mail.folder.lister.add", Listener.getClass());
		folderListeners.add(Listener);
	}
	public final void removeFolderListener(EmailFolderListener Listener) throws NullException, PermissionException
	{
		if(Listener == null) throw new NullException("Listener");
		Security.check("io.mail.folder.lister.rem", Listener.getClass());
		folderListeners.remove(Listener);
	}
	
	public final boolean isConnected()
	{
		return folderInstance != null;
	}
	public final boolean canIMAPFetch()
	{
		return folderFlags.getTagC() || !canIDLE();
	}
	public final boolean containsTerms()
	{
		return folderTerms != null;
	}
	public final boolean isDownloading()
	{
		return folderDownloader != null;
	}
	public final boolean containsEmails()
	{
		return !folderEmails.isEmpty();
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailFolder && ((EmailFolder)CompareObject).folderName.equals(folderName) && ((EmailFolder)CompareObject).folderEmails.equals(folderEmails) &&
			   ((EmailFolder)CompareObject).folderFlags.equals(folderFlags) && ((((EmailFolder)CompareObject).folderTerms == null && folderTerms == null) || ((EmailFolder)CompareObject).folderTerms != null &&
			   folderTerms != null && ((EmailFolder)CompareObject).folderTerms.equals(folderTerms)) && ((EmailFolder)CompareObject).folderAccount.equals(folderAccount);
	}
	public final boolean containsEmail(Email SearchEmail)
	{
		return folderEmails.contains(SearchEmail);
	}
	public final boolean containsListener(EmailFolderListener Listener)
	{
		return folderListeners.contains(Listener);
	}
	
	public final int hashCode()
	{
		return folderName.hashCode() + folderEmails.hashCode() + (folderTerms != null ? folderTerms.hashCode() : 0) + folderFlags.hashCode() + folderAccount.hashCode();
	}
	public final int getFetchRate()
	{
		return folderRate < 0 ? folderRate & 0xFFFF : folderRate;
	}
	public final int getEmailCount()
	{
		return folderEmails.size();
	}
	public final int getDownloadCount()
	{
		return folderDownloader != null ? folderDownloader.threadMessages.length : 0;
	}
	public final int getListenersCount()
	{
		return folderListeners.size();
	}
	
	public final String getKey()
	{
		return folderName + EmailReceiver.FOLDER_SEPERTOR_CHAR + folderAccount;
	}
	public final String toString()
	{
		return "EmailFolder(" + getItemID() + ") " + folderName + " @ " + folderAccount + " C:" + folderEmails.size() + " " + (folderTerms != null ? "T" : "N") + (canIDLE() ? "I" : "S");
	}
	public final String getFolderName()
	{
		return folderName;
	}
	public final String getAccountName()
	{
		return folderAccount;
	}
	
	public final Email getEmail(int EmailIndex) throws NumberException, PermissionException
	{
		Security.check("io.mail.folder.get", folderName);
		if(EmailIndex < 0) throw new NumberException("EmailIndex", EmailIndex, true);
		if(EmailIndex > folderEmails.size()) throw new NumberException("EmailIndex", EmailIndex, 0, folderEmails.size());
		return folderEmails.get(EmailIndex);
	}
	public final Email removeEmail(int EmailIndex) throws NumberException, PermissionException
	{
		Security.check("io.mail.folder.rem", folderName);
		if(EmailIndex < 0) throw new NumberException("EmailIndex", EmailIndex, true);
		if(EmailIndex > folderEmails.size()) throw new NumberException("EmailIndex", EmailIndex, 0, folderEmails.size());
		Email a =  folderEmails.remove(EmailIndex);
		a.setDeleted(true);
		return a;
	}
	
	public final Email[] getAllEmails()
	{
		return folderEmails.toArray(new Email[folderEmails.size()]);
	}
	
	public final EmailTerms getEmailTerms()
	{
		return folderTerms;
	}
	
	protected EmailFolder()
	{
		super(ITEM_CLASS_ID);
		folderFlags = new BoolTag();
		folderLock = new EmailLock();
		folderEmails = new ArrayList<Email>();
		folderListeners = new ArrayList<EmailFolderListener>();
	}
	protected EmailFolder(String FolderName, EmailAccount FolderAccount, Folder FolderInstance, EmailReceiver ReceiverHost)
	{
		this();
		folderName = FolderName;
		folderInstance = FolderInstance;
		folderRate = ReceiverHost.receiverRate;
		folderTerms = ReceiverHost.receiverTerms;
		folderAccount = FolderAccount.accountAddress;
		folderFlags.setTagData(ReceiverHost.receiverFlags.getTagData());
		if(folderInstance instanceof IMAPFolder)
			((IMAPFolder)folderInstance).addMessageCountListener(this);
		else if(folderFlags.getTagC())
			folderFlags.setTagC(true);
	}
	
	protected final void resetIDLETime()
	{
		folderNextFetch = System.currentTimeMillis() + (getFetchRate() * 1000);
	}
	protected final void readItemFailure()
	{
		folderName = null;
		folderTerms = null;
		folderAccount = null;
		folderEmails.clear();
	}
	protected final void interruptIdle() throws MessagingException
	{
		folderInstance.setSubscribed(folderInstance.isSubscribed());
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		folderRate = itemEncoder.readShort(InStream);
		folderName = itemEncoder.readString(InStream);
		folderFlags.readStorage(InStream, itemEncoder);
		folderAccount = itemEncoder.readString(InStream);
		int a = 0;
		switch(itemEncoder.readByte(InStream))
		{
		case 0:
			a = itemEncoder.readUnsignedByte(InStream);
			break;
		case 1:
			a = itemEncoder.readUnsignedShort(InStream);
			break;
		case 2:
			a = itemEncoder.readInteger(InStream);
			break;
		}
		folderEmails.ensureCapacity(a);
		for(; a > 0; a--)
			folderEmails.add((Email)Item.getNextItemByID(InStream, Email.ITEM_CLASS_ID));
		folderTerms = (EmailTerms)Item.getNextItemByID(InStream, EmailTerms.ITEM_CLASS_ID);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeShort(OutStream, folderRate);
		itemEncoder.writeString(OutStream, folderName);
		folderFlags.writeStorage(OutStream, itemEncoder);
		itemEncoder.writeString(OutStream, folderAccount);
		if(folderEmails.size() < 255)
		{
			itemEncoder.writeByte(OutStream, 0);
			itemEncoder.writeByte(OutStream, folderEmails.size());
		}
		else if(folderEmails.size() < Constants.MAX_USHORT_SIZE)
		{
			itemEncoder.writeByte(OutStream, 1);
			itemEncoder.writeShort(OutStream, folderEmails.size());
		}
		else
		{
			itemEncoder.writeByte(OutStream, 2);
			itemEncoder.writeInteger(OutStream, folderEmails.size());
		}
		for(int a = 0; a < folderEmails.size(); a++)
			folderEmails.get(a).writeStream(OutStream);
		writeNullItem(folderTerms, OutStream);
	}
	protected final void fetchEmails(boolean NoIdle) throws MessagingException, IOException
	{
		if(!folderInstance.isOpen())
			folderInstance.open(Folder.READ_WRITE);
		if(folderFlags.getTagA())
		{
			Message[] a = folderTerms != null ? folderInstance.search(folderTerms.getTerm()) : folderInstance.getMessages();
			folderDownloader = new EmailFolderDownloader(this, a);
			folderDownloader.start();
			folderFlags.setTagA(false);
			folderFlags.setTagD(true);
			a = null;
		}
		else
		{
			System.out.println("IDLE: " + canIDLE() + " : " + folderInstance.getUnreadMessageCount() + " : " + NoIdle + " : " + folderLock.lockCounts);
			if(canIDLE() && (folderInstance.getUnreadMessageCount() == folderLock.lockCounts || folderInstance.getUnreadMessageCount() == 0) && !NoIdle)
			{
				((IMAPFolder)folderInstance).idle();
				if(!folderIsIdle) folderNextFetch = 0;
				if(folderLock.lockCounts > 0) return;
			}
			else if(!folderFlags.getTagC())
			{
				Message[] b = folderInstance.search(folderTerms != null ? new AndTerm(UNREAD_FLAG, folderTerms.getTerm()) : UNREAD_FLAG);
				if(folderFlags.getTagF())
					new EmailFolderDownloader(this, b).start();
				else
					addAndPassToListeners(b, true);
				b = null;
			}
		}
		if(folderDownloader != null) return;
		if(folderInstance.isOpen() && folderLock.lockCounts == 0)
			folderInstance.close(true);
	}
	
	protected final boolean canIDLE()
	{
		return folderInstance instanceof IMAPFolder && folderTerms == null;// && folderFlags.getTagC();
	}
	protected final boolean canFetch()
	{
		if(folderFlags.getTagE()) return false;
		if(folderNextFetch < System.currentTimeMillis())
		{
			folderNextFetch = System.currentTimeMillis() + (getFetchRate() * 1000);
			return true;
		}
		return false;
	}
	
	private final void addAndPassToListeners(Message[] Messages, boolean AddPass) throws MessagingException, IOException
	{
		if(Messages != null && Messages.length > 0)
		{
			Email a = null;
			Email[] b = new Email[Messages.length];
			for(int c = 0; c < Messages.length; c++)
			{
				Messages[c].setFlag(Flag.SEEN, true);
				if(folderFlags.getTagB())
					Messages[c].setFlag(Flag.DELETED, true);
				a = new Email((MimeMessage)Messages[c], folderLock);
				b[c] = a;
				if(AddPass)
					folderEmails.add(a);
				a = null;
			}
			if(!folderListeners.isEmpty())
			{
				System.out.println("SIZE: " + folderListeners.size());
				for(int d = 0; d < folderListeners.size(); d++)
				{
					if(folderListeners.get(d).runAsThread())
						new EmailFolderListenerThread(folderListeners.get(d), this, b, AddPass).start();
					else try
					{
						if(AddPass)
							folderListeners.get(d).folderUpdated(this, b);
						else
							folderListeners.get(d).folderExpunged(this, b);
					}
					catch (Throwable Exception)
					{
						Reporter.error(Reporter.REPORTER_EMAIL, Exception);
					}
				}
			}
			b = null;
		}
	}
	
	protected static final class EmailLock
	{
		protected int lockCounts;
		
		private EmailLock() { }
	}
	
	private static final class EmailFolderDownloader extends Thread
	{
		private final Message[] threadMessages;
		private final EmailFolder threadFolder;
		
		public final void run()
		{
			try
			{
				threadFolder.addAndPassToListeners(threadMessages, true);
			}
			catch (IOException Exception)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			}
			catch (MessagingException Exception)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			}
			threadFolder.folderDownloader = null;
		}
		
		private EmailFolderDownloader(EmailFolder FolderInstance, Message[] MessagesToAdd)
		{
			threadFolder = FolderInstance;
			threadMessages = MessagesToAdd;
			setName("EmailFolderDownloaderThread");
			setPriority(1);
		}
	}
	private static final class EmailFolderListenerThread extends Thread
	{
		private final boolean threadAdd;
		private final Email[] threadEmails;
		private final EmailFolder threadFolder;
		private final EmailFolderListener threadInstance;
		
		public final void run()
		{
			try
			{
				if(threadAdd)
					threadInstance.folderUpdated(threadFolder, threadEmails);
				else
					threadInstance.folderExpunged(threadFolder, threadEmails);
			}
			catch (Throwable Exception)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			}
		}
		
		private EmailFolderListenerThread(EmailFolderListener Instance, EmailFolder Folder, Email[] Updates, boolean AddThread)
		{
			threadFolder = Folder;
			threadAdd = AddThread;
			threadEmails = Updates;
			threadInstance = Instance;
			setName("EmailFolderListenerThread: " + Instance.getClass().getName());
			setPriority(1);
		}
	}
}