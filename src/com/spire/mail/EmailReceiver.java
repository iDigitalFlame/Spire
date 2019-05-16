package com.spire.mail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;

import com.spire.ex.CloneException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.PermissionException;
import com.spire.ex.StringException;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.BoolTag;
import com.spire.util.Constants;
import com.spire.util.HashList;

public final class EmailReceiver
{
	protected static final char FOLDER_SEPERTOR_CHAR = '#';
	
	public static final short DEFAULT_FETCH_RATE = 300;
	public static final short RECOMMENDED_LOWEST_RATE = 5;
	
	private final EmailReceiverThread receiverMainThread;
	private final HashList<String, EmailFolder> receiverFolders;
	private final HashList<String, EmailFolder> receiverIdleFolders;
	private final HashList<String, EmailAccountInternal> receiverAccounts;
	
	private boolean receiverActive;
	private EmailReceiverIdleThread[] receiverIdleThreads;
	
	protected final BoolTag receiverFlags;
	
	protected short receiverRate;
	protected EmailTerms receiverTerms;
	
	public EmailReceiver() throws PermissionException
	{
		this(DEFAULT_FETCH_RATE, null, 1);
	}
	public EmailReceiver(int DefaultFetchSeconds) throws PermissionException, NumberException
	{
		this(DefaultFetchSeconds, null, 1);
	}
	public EmailReceiver(int DefaultFetchSeconds, EmailTerms DefaultTerms) throws PermissionException, NumberException
	{
		this(DefaultFetchSeconds, DefaultTerms, 1);
	}
	public EmailReceiver(int DefaultFetchSeconds, EmailTerms DefaultTerms, int IdleThreadCount) throws PermissionException, NumberException
	{
		if(IdleThreadCount < 0) throw new NumberException("IdleThreadCount", IdleThreadCount, true);
		if(DefaultFetchSeconds <= 0) throw new NumberException("DefaultFetchSeconds", DefaultFetchSeconds, false);
		if(IdleThreadCount > Byte.MAX_VALUE) throw new NumberException("IdleThreadCount", IdleThreadCount, 0, Byte.MAX_VALUE);
		if(DefaultFetchSeconds > Constants.MAX_USHORT_SIZE) throw new NumberException("DefaultFetchSeconds", DefaultFetchSeconds, 1, Constants.MAX_USHORT_SIZE);
		if(DefaultFetchSeconds < RECOMMENDED_LOWEST_RATE) Reporter.warning(Reporter.REPORTER_EMAIL, "Fetch rates this low might incur network issues!");
		Security.check("io.mail.receiver.idlec", Integer.valueOf(IdleThreadCount));
		Security.check("io.mail.receiver.rate", Integer.valueOf(DefaultFetchSeconds));
		receiverActive = true;
		receiverTerms = DefaultTerms;
		receiverFlags = new BoolTag();
		receiverRate = (short)DefaultFetchSeconds;
		receiverMainThread = new EmailReceiverThread(this);
		receiverFolders = new HashList<String, EmailFolder>();
		receiverIdleFolders = new HashList<String, EmailFolder>();
		receiverAccounts = new HashList<String, EmailAccountInternal>();
		receiverIdleThreads = IdleThreadCount > 0 ? new EmailReceiverIdleThread[IdleThreadCount] : null;
		receiverMainThread.start();
	}

	public final void clearFolders() throws PermissionException
	{
		Security.check("io.mail.receiver.clearf");
		for(int a = 0; a < receiverFolders.size(); a++)
			receiverFolders.get(a).closeFolder();
		receiverFolders.clear();
	}
	public final void stopReceiver() throws PermissionException
	{
		Security.check("io.mail.receiver.stop");
		receiverActive = false;
	}	
	public final void clearAccounts() throws PermissionException
	{
		Security.check("io.mail.receiver.clearf");
		for(int a = 0; a < receiverAccounts.size(); a++) try
		{
			receiverAccounts.get(a).accountStore.close();
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
		}
		receiverAccounts.clear();
	}
	public final void setFoldersNoImapFetch(boolean NoImapFetch) throws PermissionException
	{
		Security.check("io.mail.receiver.nofetchimap");
		receiverFlags.setTagC(NoImapFetch);
	}
	public final void setFoldersDownloadAll(boolean DownloadAll) throws PermissionException
	{
		Security.check("io.mail.receiver.downall");
		receiverFlags.setTagA(DownloadAll);
	}
	public final void setTerms(EmailTerms SearchTerms) throws NullException, PermissionException
	{
		Security.check("io.mail.receiver.terms");
		receiverTerms = SearchTerms;
	}
	public final void setDownloadSeperateThread(boolean SeperateThread) throws PermissionException
	{
		Security.check("io.mail.folder.down-st");
		receiverFlags.setTagF(SeperateThread);
	}
	public final void setFetchRate(int FetchRateSeconds) throws NumberException, PermissionException
	{
		Security.check("io.mail.receiver.rate", Integer.valueOf(FetchRateSeconds));
		if(FetchRateSeconds <= 0) throw new NumberException("FetchRateSeconds", FetchRateSeconds, false);
		if(FetchRateSeconds > Constants.MAX_USHORT_SIZE) throw new NumberException("FetchRateSeconds", FetchRateSeconds, 1, Constants.MAX_USHORT_SIZE);
		if(FetchRateSeconds < RECOMMENDED_LOWEST_RATE) Reporter.warning(Reporter.REPORTER_EMAIL, "Fetch rates this low might incur network issues!");
		receiverRate = (short)FetchRateSeconds;
	}
	public final void setDefaultTerms(EmailTerms SearchTerms) throws NumberException, PermissionException
	{
		setTerms(SearchTerms);
		for(int a = 0; a < receiverFolders.size(); a++)
			receiverFolders.get(a).folderTerms = receiverTerms;
	}
	public final void setIdleThreadCount(int IdleThreadCount) throws NumberException, PermissionException
	{
		if(IdleThreadCount < 0) throw new NumberException("IdleThreadCount", IdleThreadCount, true);
		if(IdleThreadCount > Constants.MAX_USHORT_SIZE) throw new NumberException("IdleThreadCount", IdleThreadCount, 0, Byte.MAX_VALUE);
		Security.check("io.mail.receiver.idlec", Integer.valueOf(IdleThreadCount));
		if(receiverIdleThreads == null) receiverIdleThreads = new EmailReceiverIdleThread[IdleThreadCount];
		else if(IdleThreadCount < receiverIdleThreads.length)
		{
			for(byte a = (byte)IdleThreadCount; a < receiverIdleThreads.length; a++)
				if(receiverIdleThreads[a] != null)
				{
					receiverIdleThreads[a].threadEnable = false;
					receiverIdleThreads[a] = null;
				}
			receiverIdleThreads = Arrays.copyOf(receiverIdleThreads, IdleThreadCount);
		}
		else if(IdleThreadCount > receiverIdleThreads.length)
			receiverIdleThreads = Arrays.copyOf(receiverIdleThreads, IdleThreadCount);
	}
	public final void setDefaultFetchRate(int FetchRateSeconds) throws NumberException, PermissionException
	{
		setFetchRate(FetchRateSeconds);
		for(int a = 0; a < receiverFolders.size(); a++)
			receiverFolders.get(a).folderRate = receiverRate;
	}
	public final void addAccount(EmailAccount Account) throws NullException, IOException, PermissionException
	{
		if(Account == null) throw new NullException("Account");
		addAccount(Account, Account.accountSettings);
	}
	public final void connectFolder(EmailFolder AddFolder) throws NullException, PermissionException, IOException
	{
		if(AddFolder == null) throw new NullException("AddFolder");
		Security.check("io.mail.folder.con", AddFolder.getKey());
		if(!receiverAccounts.containsKey(AddFolder.getAccountName()))
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The account \"" + AddFolder.getAccountName() + "\" does not exist!");
			throw new IOException("The account \"" + AddFolder.getAccountName() + "\" does not exist!");
		}
		EmailAccountInternal a = receiverAccounts.get(AddFolder.getAccountName());
		try
		{
			if(!a.accountStore.isConnected()) a.accountStore.connect(a.accountInstance.accountDetails.getUserName(), a.accountInstance.accountDetails.getUserPassword());
			AddFolder.folderInstance = a.accountStore.getFolder(AddFolder.getFolderName());
			receiverFolders.add(AddFolder);
			Reporter.info(Reporter.REPORTER_EMAIL, "Registered folder \"" + AddFolder.getFolderName() + "\" from Account \"" + a.accountInstance.accountAddress + "\" to EmailReceiver \"" + toString() + "\"");
			a = null;
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	public final void removeAccount(String AccountName) throws NullException, StringException, PermissionException, IOException
	{
		if(AccountName == null) throw new NullException("AccountName");
		if(AccountName.isEmpty()) throw new StringException("AccountName");
		if(!receiverAccounts.containsKey(AccountName))
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The account \"" + AccountName + "\" does not exist!");
			throw new IOException("The account \"" + AccountName + "\" does not exist!");
		}
		Security.check("io.mail.receiver.rem", AccountName);
		EmailAccountInternal a = receiverAccounts.removeElement(AccountName);
		try
		{
			a.accountStore.close();
			for(int b = 0; b < receiverFolders.size(); b++)
				if(receiverFolders.get(b).getAccountName().equals(AccountName))
				{
					receiverFolders.get(b).closeFolder();
					receiverFolders.remove(b);
				}
			a = null;
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	public final void removeAccount(EmailAccount Account) throws NullException, StringException, PermissionException, IOException
	{
		if(Account == null) throw new NullException("Account");
		removeAccount(Account.accountAddress);
	}
	public final void addAccount(EmailAccount Account, EmailSettings Settings) throws NullException, IOException, PermissionException
	{
		if(Account == null) throw new NullException("Account");
		if(Settings == null) throw new NullException("Settings");
		Security.check("io.mail.acc.add", Account.accountAddress);
		EmailAccountInternal a = new EmailAccountInternal(Account, Settings);
		activateAccount(a);
		receiverAccounts.add(a);
		Reporter.info(Reporter.REPORTER_EMAIL, "Added Account \"" + Account.accountAddress + "\" to EmailReceiver \"" + toString() + "\"");
	}
	public final void connectFolder(EmailAccount AddAccount, EmailFolder AddFolder) throws NullException, PermissionException, IOException
	{
		if(AddFolder == null) throw new NullException("AddFolder");
		if(AddAccount == null) throw new NullException("AddAccount");
		connectFolder(AddAccount, AddAccount.accountSettings, AddFolder);
	}
	public final void removeFolder(String AccountName, String FolderName) throws NullException, StringException, PermissionException, IOException
	{
		if(FolderName == null) throw new NullException("FolderName");
		if(FolderName.isEmpty()) throw new StringException("FolderName");
		if(AccountName == null) throw new NullException("AccountName");
		if(AccountName.isEmpty()) throw new StringException("AccountName");
		if(!receiverAccounts.containsKey(AccountName))
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The account \"" + AccountName + "\" does not exist!");
			throw new IOException("The account \"" + AccountName + "\" does not exist!");
		}
		Security.check("io.mail.receiver.remf", FolderName);
		Security.check("io.mail.receiver.remaf", AccountName);
		if(!receiverFolders.containsKey(FolderName + FOLDER_SEPERTOR_CHAR + AccountName))
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The folder \"" + FolderName + "\" on \"" + AccountName + "\" does not exist!");
			throw new IOException("The folder \"" + FolderName + "\" on \"" + AccountName + "\" does not exist!");
		}
		EmailFolder a = receiverFolders.removeElement(FolderName + FOLDER_SEPERTOR_CHAR + AccountName);
		a.closeFolder();
		a = null;
	}
	public final void removeFolder(EmailAccount Account, String FolderName) throws NullException, StringException, PermissionException, IOException
	{
		if(Account == null) throw new NullException("Account");
		removeFolder(Account.accountAddress, FolderName);
	}
	public final void connectFolder(EmailAccount AddAccount, EmailSettings AddSettings, EmailFolder AddFolder) throws NullException, PermissionException, IOException
	{
		if(AddFolder == null) throw new NullException("AddFolder");
		if(AddAccount == null) throw new NullException("AddAccount");
		if(AddSettings == null) throw new NullException("AddSettings");
		Security.check("io.mail.folder.con", AddFolder.getKey());
		EmailAccountInternal a = null;
		if(!receiverAccounts.containsKey(AddFolder.getAccountName()))
		{
			a = new EmailAccountInternal(AddAccount, AddSettings);
			activateAccount(a);
		}
		else a = receiverAccounts.get(AddFolder.getAccountName());
		try
		{
			if(!a.accountStore.isConnected()) a.accountStore.connect(a.accountInstance.accountDetails.getUserName(), a.accountInstance.accountDetails.getUserPassword());
			AddFolder.folderInstance = a.accountStore.getFolder(AddFolder.getFolderName());
			receiverFolders.add(AddFolder);
			Reporter.info(Reporter.REPORTER_EMAIL, "Registered folder \"" + AddFolder.getFolderName() + "\" from Account \"" + a.accountInstance.accountAddress + "\" to EmailReceiver \"" + toString() + "\"");
			a = null;
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	
	public final boolean isRunning()
	{
		return receiverActive;
	}
	public final boolean containsTerms()
	{
		return receiverTerms != null;
	}
	public final boolean isUsingIdleThreads()
	{
		return receiverIdleThreads != null && receiverIdleThreads.length > 0;
	}
	public final boolean canIMAPFoldersFetch()
	{
		return receiverFlags.getTagC();
	}
	public final boolean isFoldersDownloadAll()
	{
		return receiverFlags.getTagA();
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailReceiver && ((EmailReceiver)CompareObject).receiverActive == receiverActive && ((EmailReceiver)CompareObject).receiverAccounts.equals(receiverAccounts) &&
			   ((EmailReceiver)CompareObject).receiverFlags.equals(receiverFlags) && ((EmailReceiver)CompareObject).receiverFolders.equals(receiverFolders) &&
			   ((EmailReceiver)CompareObject).receiverRate == receiverRate && (((EmailReceiver)CompareObject).receiverTerms == null && receiverTerms == null || 
			   ((EmailReceiver)CompareObject).receiverTerms != null && receiverTerms != null && ((EmailReceiver)CompareObject).receiverTerms.equals(receiverTerms)) &&
			   (((EmailReceiver)CompareObject).receiverIdleThreads == null && receiverIdleThreads == null || ((EmailReceiver)CompareObject).receiverIdleThreads != null && receiverIdleThreads != null &&
			   ((EmailReceiver)CompareObject).receiverIdleThreads.length == receiverIdleThreads.length);
	}
	public final boolean containsAccount(String ContainsAccount)
	{
		return ContainsAccount != null && receiverAccounts.containsKey(ContainsAccount);
	}
	public final boolean containsFolder(EmailFolder ContainsFolder)
	{
		return ContainsFolder != null && receiverFolders.containsKey(ContainsFolder.getKey());
	}
	public final boolean containsAccount(EmailAccount ContainsAccount)
	{
		return ContainsAccount != null && receiverAccounts.containsKey(ContainsAccount.accountAddress);
	}
		
	public final int hashCode()
	{
		return receiverFolders.hashCode() + receiverAccounts.hashCode() + receiverFlags.hashCode() + receiverRate + (receiverTerms != null ? receiverTerms.hashCode() : 1);
	}
	public final int getFetchRate()
	{
		return receiverRate < 0 ? receiverRate & 0xFFFF : receiverRate;
	}
	public final int getFolderCount()
	{
		return receiverFolders.size();
	}
	public final int getAccountCount()
	{
		return receiverAccounts.size();
	}
	public final int getIdleThreadCount()
	{
		return receiverIdleThreads != null ? receiverIdleThreads.length : 0;
	}
	
	public final String toString()
	{
		return "EmailReceiver(ER) A:" + receiverAccounts.size() + " F:" + receiverFolders.size();
	}
	
	public final EmailTerms getTerms()
	{
		return receiverTerms;
	}
	
	public final EmailFolder getFolder(String AccountName, String FolderName) throws NullException, StringException, IOException
	{
		return getFolder(AccountName, FolderName);
	}
	public final EmailFolder getFolder(EmailAccount Account, String FolderName) throws NullException, StringException, IOException
	{
		return getFolder(Account, FolderName);
	}
	public final EmailFolder getFolder(String AccountName, String FolderName, boolean CreateFolder) throws NullException, StringException, IOException
	{		
		if(FolderName == null) throw new NullException("FolderName");
		if(FolderName.isEmpty()) throw new StringException("FolderName");
		if(AccountName == null) throw new NullException("AccountName");
		if(AccountName.isEmpty()) throw new StringException("AccountName");
		if(receiverFolders.containsKey(FolderName + FOLDER_SEPERTOR_CHAR + AccountName))
			return receiverFolders.get(FolderName + FOLDER_SEPERTOR_CHAR + AccountName);			
		if(!receiverAccounts.containsKey(AccountName))
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The account \"" + AccountName + "\" does not exist!");
			throw new IOException("The account \"" + AccountName + "\" does not exist!");
		}
		try
		{
			return getInternalFolder(AccountName, FolderName, CreateFolder);
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	public final EmailFolder getFolder(EmailAccount Account, String FolderName, boolean CreateFolder) throws NullException, StringException, IOException
	{
		if(Account == null) throw new NullException("Account");
		if(FolderName == null) throw new NullException("FolderName");
		if(FolderName.isEmpty()) throw new StringException("FolderName");
		if(receiverFolders.containsKey(FolderName + FOLDER_SEPERTOR_CHAR + Account.accountAddress))
			return receiverFolders.get(FolderName + FOLDER_SEPERTOR_CHAR + Account.accountAddress);			
		if(!receiverAccounts.containsKey(Account.accountAddress))
		{
			if(Account.accountSettings == null)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, "Cannot add new account with no settings!");
				throw new IOException("Cannot add new account with no settings!");
			}
			addAccount(Account, Account.accountSettings);
		}
		try
		{
			return getInternalFolder(Account.accountAddress, FolderName, CreateFolder);
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}
	
	public static final void setEmailDownloadOnDemand(boolean DownloadOnDemand) throws PermissionException
	{
		Security.check("io.mail.dod");
		Email.DOWNLOAD_OND = DownloadOnDemand;
	}
	public static final void setEmailDownloadAllOnDemand(boolean DownloadAllOnDemand) throws PermissionException
	{
		Security.check("io.mail.dod.all");
		Email.DOWNLOAD_ALL_THREAD = DownloadAllOnDemand;
	}
	
	protected final EmailSender clone() throws CloneException
	{
		throw new CloneException("Cannot clone EmailAccount data!");
	}
	
	protected final void fetchAllFolders() throws IOException, MessagingException
	{
		EmailFolder a = null;
		for(int b = 0; b < receiverFolders.size(); b++)
		{
			a = receiverFolders.get(b);
			if(a.canFetch())
			{
				if(a.canIDLE() && receiverIdleThreads != null && receiverIdleThreads.length > 0)
				{
					if(a.folderIsIdle)
					{
						a.folderIsIdle = false;
						a.folderInQueue = false;
						a.interruptIdle();
						if(!receiverIdleFolders.isEmpty())
							addIDLEFolder(receiverIdleFolders.remove(0));
					}
					else if(!a.folderInQueue)
						addIDLEFolder(a);
					else if(a.folderInQueue && !a.folderIsIdle)
					{
						Reporter.debug(Reporter.REPORTER_EMAIL, "Running fetch for queued idle folder \"" + a.getKey() + "\"!");
						a.fetchEmails(true);
					}
				}
				else
				{
					Reporter.debug(Reporter.REPORTER_EMAIL, "Running fetch for folder \"" + a.getKey() + "\"!");
					a.fetchEmails(false);
				}
			}
			a = null;
		}
	}
	
	private final void addIDLEFolder(EmailFolder IDLEFolder)
	{
		if(receiverIdleThreads == null || receiverIdleThreads.length == 0) return;
		if(!receiverIdleFolders.isEmpty() && !IDLEFolder.folderInQueue)
		{
			IDLEFolder.folderInQueue = true;
			receiverIdleFolders.add(IDLEFolder);
			return;
		}
		for(byte a = 0; a < receiverIdleThreads.length; a++)
		{
			if(receiverIdleThreads[a] == null)
			{
				IDLEFolder.resetIDLETime();
				IDLEFolder.folderIsIdle = true;
				IDLEFolder.folderInQueue = false;
				receiverIdleThreads[a] = new EmailReceiverIdleThread(IDLEFolder);
				receiverIdleThreads[a].start();
				return;
			}
			else if(receiverIdleThreads[a].threadFolder == null || !receiverIdleThreads[a].threadFolder.folderIsIdle)
			{
				IDLEFolder.resetIDLETime();
				IDLEFolder.folderIsIdle = true;
				IDLEFolder.folderInQueue = false;
				receiverIdleThreads[a].threadFolder = IDLEFolder;
				receiverIdleThreads[a].interrupt();
				return;
			}
		}
		IDLEFolder.folderInQueue = true;
		receiverIdleFolders.add(IDLEFolder);
	}
	
	private final EmailFolder getInternalFolder(String AccountName, String FolderName, boolean CreateFolder) throws MessagingException, IOException
	{
		EmailAccountInternal a = receiverAccounts.get(AccountName);
		if(!a.accountStore.isConnected()) a.accountStore.connect(a.accountInstance.accountDetails.getUserName(), a.accountInstance.accountDetails.getUserPassword());
		Folder b = a.accountStore.getFolder(FolderName);
		if(!CreateFolder && !b.exists())
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The folder \"" + FolderName + "\" on \"" + a.accountInstance.accountAddress + "\" does not exist!");
			throw new IOException("The folder \"" + FolderName + "\" on \"" + a.accountInstance.accountAddress + "\" does not exist!");
		}
		if(CreateFolder && !b.exists() && !b.create(3))
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "The folder \"" + FolderName + "\" on \"" + a.accountInstance.accountAddress + "\" cannot be created!");
			throw new IOException("The folder \"" + FolderName + "\" on \"" + a.accountInstance.accountAddress + "\" cannot be created!");
		}
		EmailFolder c = new EmailFolder(FolderName, a.accountInstance, b, this);
		receiverFolders.add(c);
		if(receiverFolders.size() == 1)
			receiverMainThread.interrupt();
		Reporter.info(Reporter.REPORTER_EMAIL, "Added folder \"" + FolderName + "\" from Account \"" + a.accountInstance.accountAddress + "\" to EmailReceiver \"" + toString() + "\"");
		a = null;
		b = null;
		return c;
	}
	
	private static final void activateAccount(EmailAccountInternal Account) throws IOException
	{
		if(Account.accountSession != null) return;
		Properties a = new Properties();
		Account.accountSettings.addSettings(a);
		Session b = Session.getInstance(a, Account.accountInstance.getEmailAuth());
		try
		{
			Account.accountStore = b.getStore(Account.accountSettings.getSettingsType());
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
		Account.accountSession = b;
		a = null;
	}
	
	private static final class EmailReceiverThread extends Thread
	{
		private final EmailReceiver threadReceiver;
	
		public final void run()
		{
			while(threadReceiver.receiverActive)
			{
				if(threadReceiver.receiverFolders.isEmpty()) try
				{
					Thread.sleep(0x7FFFFFFFL);
				}
				catch (InterruptedException Exception) { }
				try
				{
					threadReceiver.fetchAllFolders();
				}
				catch (IOException Exception)
				{
					Reporter.error(Reporter.REPORTER_EMAIL, Exception);
				}
				catch (MessagingException Exception)
				{
					Reporter.error(Reporter.REPORTER_EMAIL, Exception);
				}
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException Exception) { }
			}
		}
		
		private EmailReceiverThread(EmailReceiver ReceiverInstance)
		{
			setPriority(2);
			setName("ReceiverThreadBase");
			threadReceiver = ReceiverInstance;
		}
	}
	private static final class EmailReceiverIdleThread extends Thread
	{		
		private boolean threadEnable;
		private EmailFolder threadFolder;
		
		public final void run()
		{
			while(threadEnable)
			{
				if(threadFolder == null) try
				{
					Thread.sleep(0x7FFFFFFFL);
				}
				catch (InterruptedException Exception) { }
				try
				{
					Reporter.debug(Reporter.REPORTER_EMAIL, "Starting IDLE for folder \"" + threadFolder.getKey() + "\"!");
					threadFolder.fetchEmails(false);
				}
				catch (IOException Exception)
				{
					Reporter.error(Reporter.REPORTER_EMAIL, Exception);
				}
				catch (MessagingException Exception)
				{
					Reporter.error(Reporter.REPORTER_EMAIL, Exception);
				}
			}
		}
		
		private EmailReceiverIdleThread(EmailFolder Folder)
		{
			setPriority(1);
			threadEnable = true;
			threadFolder = Folder;
			setName("ReceiverThreadIdleBase");
		}
	}
}