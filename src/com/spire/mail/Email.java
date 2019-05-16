package com.spire.mail;

import java.io.File;

import javax.mail.Flags;

import com.spire.io.Item;

import javax.mail.Address;
import javax.mail.Session;

import com.spire.io.Stream;

import java.io.IOException;
import java.util.ArrayList;

import com.spire.util.Stamp;

import javax.mail.Flags.Flag;

import com.spire.sec.Security;
import com.spire.log.Reporter;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.ex.FormatException;

import javax.activation.FileTypeMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Message.RecipientType;

import com.spire.ex.PermissionException;

import javax.mail.internet.MimeMultipart;

import com.spire.mail.EmailFolder.EmailLock;

public final class Email extends Item
{
	protected static boolean DOWNLOAD_OND = true;
	protected static boolean DOWNLOAD_ALL_THREAD = false;
	
	protected static final String DEFAULT_TYPE = "text/plain";
	
	public static final byte ITEM_CLASS_ID = 19;
	
	private final Stamp emailSentDate;
	private final EmailFlags emailFlags;
	private final Stamp emailReceivedDate;
	private final ArrayList<EmailPart> emailParts;
	private final ArrayList<EmailAddressInternal> emailAddresses;
	
	private int emailFile;
	private String emailSubject;
	private EmailAddress emailFrom;
	private EmailDownloader emailDownload;
	
	protected MimeMessage emailTemp;
	
	public Email()
	{
		super(ITEM_CLASS_ID);
		emailFile = -1;
		emailSentDate = new Stamp();
		emailFlags = new EmailFlags();
		emailParts = new ArrayList<EmailPart>();
		emailReceivedDate = new Stamp((byte[])null);
		emailAddresses = new ArrayList<EmailAddressInternal>();
	}
	public Email(EmailAddress ReceiveAddress) throws NullException
	{
		this();
		addRecipientTo(ReceiveAddress);
	}
	public Email(EmailAddress ReceiveAddress, String Subject) throws NullException
	{
		this();
		addRecipientTo(ReceiveAddress);
		setSubject(Subject);
	}
	public Email(String ReceiveAddress) throws NullException, StringException, FormatException, NumberException
	{
		this();
		addRecipientTo(ReceiveAddress);
	}
	public Email(String ReceiveAddress, String Subject) throws NullException, StringException, FormatException, NumberException
	{
		this();
		addRecipientTo(ReceiveAddress);
		setSubject(Subject);
	}
	
	public final void clearFrom()
	{
		emailFrom = null;
		emailTemp = null;
	}
	public final void clearRecipients()
	{
		emailAddresses.clear();
		emailTemp = null;
	}
	public final void resetAttachments()
	{
		for(int b = 0; b < emailParts.size(); b++) try
		{
			if(emailParts.get(b).getFileName() != null)
			{
				emailFile = b;
				break;
			}
		}
		catch (MessagingException Exception) { emailFile = -1; }
	}
	public final void setRead(boolean IsRead)
	{
		if(IsRead) emailFlags.add(Flag.SEEN);
		else emailFlags.remove(Flag.SEEN);
		emailTemp = null;
	}
	public final void setDraft(boolean IsDraft)
	{
		if(IsDraft) emailFlags.add(Flag.DRAFT);
		else emailFlags.remove(Flag.DRAFT);
		emailTemp = null;
	}
	public final void setFlagged(boolean IsFlagged)
	{
		if(IsFlagged) emailFlags.add(Flag.FLAGGED);
		else emailFlags.remove(Flag.FLAGGED);
		emailTemp = null;
	}
	public final void setDeleted(boolean IsDeleted)
	{
		if(IsDeleted) emailFlags.add(Flag.DELETED);
		else emailFlags.remove(Flag.DELETED);
		if(emailTemp != null) try
		{
			emailTemp.setFlag(Flag.DELETED, true);
		}
		catch (MessagingException Exception) { }
		emailTemp = null;
	}
	public final void setAnswered(boolean IsAnswered)
	{
		if(IsAnswered) emailFlags.add(Flag.ANSWERED);
		else emailFlags.remove(Flag.ANSWERED);
		emailTemp = null;
	}
	public final void setUpdateSentOnSend(boolean UpdateOnSend)
	{
		emailFlags.flagData.setTagH(UpdateOnSend);
	}
	public final void setSubject(String Subject) throws NullException
	{
		if(Subject == null) throw new NullException("Subject");
		emailSubject = Subject;
		emailTemp = null;
	}
	public final void removePart(int PartIndex) throws NumberException
	{
		if(PartIndex < 0) throw new NumberException("PartIndex", PartIndex, true);
		if(PartIndex > emailParts.size()) throw new NumberException("PartIndex", PartIndex, 0, emailParts.size());
		emailParts.remove(PartIndex);
		emailTemp = null;
	}
	public final void setFrom(EmailAddress FromAddress) throws NullException
	{
		emailFrom = FromAddress;
		emailTemp = null;
	}
	public final void removeRecipient(int RecipientIndex) throws NumberException
	{
		if(RecipientIndex < 0) throw new NumberException("RecipientIndex", RecipientIndex, true);
		if(RecipientIndex > emailAddresses.size()) throw new NumberException("RecipientIndex", RecipientIndex, 0, emailAddresses.size());
		emailAddresses.remove(RecipientIndex);
		emailTemp = null;
	}
	public final void addReplyTo(EmailAddress ReceiveAddress) throws NullException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_TO, ReceiveAddress));
		emailTemp = null;
	}
	public final void addReplyTo(EmailAddress[] ReceiveAddress) throws NullException
	{
		addRecipient(EmailAddress.ADDRESS_REPLY, ReceiveAddress);
	}
	public final void addRecipientTo(EmailAddress ReceiveAddress) throws NullException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_TO, ReceiveAddress));
		emailTemp = null;
	}
	public final void addRecipientCC(EmailAddress ReceiveAddress) throws NullException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_CC, ReceiveAddress));
		emailTemp = null;
	}
	public final void removeRecipient(EmailAddress RemoveAddress) throws NullException
	{
		if(RemoveAddress == null) throw new NullException("RemoveAddress");
		emailAddresses.remove(RemoveAddress);
		emailTemp = null;
	}
	public final void addRecipientBCC(EmailAddress ReceiveAddress) throws NullException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_BCC, ReceiveAddress));
		emailTemp = null;
	}
	public final void addRecipientTo(EmailAddress[] ReceiveAddress) throws NullException
	{
		addRecipient(EmailAddress.ADDRESS_TO, ReceiveAddress);
	}
	public final void addRecipientCC(EmailAddress[] ReceiveAddress) throws NullException
	{
		addRecipient(EmailAddress.ADDRESS_CC, ReceiveAddress);
	}
	public final void addRecipientBCC(EmailAddress[] ReceiveAddress) throws NullException
	{
		addRecipient(EmailAddress.ADDRESS_BCC, ReceiveAddress);
	}
	public final void removeRecipient(String RemoveAddress) throws NullException, StringException
	{
		if(RemoveAddress == null) throw new NullException("RemoveAddress");
		if(RemoveAddress.isEmpty()) throw new StringException("RemoveAddress");
		for(int a = 0; a < emailAddresses.size(); a++)
			if(emailAddresses.get(a).addressInstance.toString().equalsIgnoreCase(RemoveAddress))
			{
				emailAddresses.remove(a);
				emailTemp = null;
				return;
			}
	}
	public final void setFrom(String FromAddress) throws NullException, StringException, FormatException
	{
		emailFrom = new EmailAddress(FromAddress);
		emailTemp = null;
	}
	public final void addAttachment(File FilePath) throws IOException, NullException, PermissionException
	{
		Stream a = Stream.getFileInputStream(FilePath);
		EmailPart b = new EmailPart();
		try
		{

			b.setFileName(FilePath.getName());
			b.setExternal();
			b.getStream().readFromStream(a);
			b.closePart();
			b.partContent = FileTypeMap.getDefaultFileTypeMap().getContentType(FilePath);
			Security.check("io.mail.attch", b.partContent);
			Reporter.debug(Reporter.REPORTER_EMAIL, "Added attachment \"" + FilePath.getAbsolutePath() + "\" to Email \"" + toString() + "\"");
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
		emailParts.add(b);
		if(emailFile == -1)
			emailFile = emailParts.size() - 1;
		emailTemp = null;
	}
	public final void addReplyTo(String ReceiveAddress) throws NullException, StringException, FormatException
	{
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_REPLY, new EmailAddress(ReceiveAddress)));
		emailTemp = null;
	}
	public final void addReplyTo(String[] ReceiveAddress) throws NullException, StringException, FormatException
	{
		addRecipient(EmailAddress.ADDRESS_REPLY, ReceiveAddress);
	}
	public final void addRecipientTo(String ReceiveAddress) throws NullException, StringException, FormatException
	{
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_TO, new EmailAddress(ReceiveAddress)));
		emailTemp = null;
	}
	public final void addRecipientCC(String ReceiveAddress) throws NullException, StringException, FormatException
	{
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_CC, new EmailAddress(ReceiveAddress)));
		emailTemp = null;
	}
	public final void addRecipientBCC(String ReceiveAddress) throws NullException, StringException, FormatException
	{
		emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_BCC, new EmailAddress(ReceiveAddress)));
		emailTemp = null;
	}
	public final void addRecipientTo(String[] ReceiveAddress) throws NullException, StringException, FormatException
	{
		addRecipient(EmailAddress.ADDRESS_TO, ReceiveAddress);
	}
	public final void addRecipientCC(String[] ReceiveAddress) throws NullException, StringException, FormatException
	{
		addRecipient(EmailAddress.ADDRESS_CC, ReceiveAddress);
	}
	public final void addRecipientBCC(String[] ReceiveAddress) throws NullException, StringException, FormatException
	{
		addRecipient(EmailAddress.ADDRESS_BCC, ReceiveAddress);
	}
	public final void addRecipient(byte ReceiveType, EmailAddress ReceiveAddress) throws NullException, NumberException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		if(ReceiveType < 0) throw new NumberException("ReceiveType", ReceiveType, true);
		if(ReceiveType > EmailAddress.ADDRESS_NEWS) throw new NumberException("ReceiveType", ReceiveType, 0, EmailAddress.ADDRESS_NEWS);
		emailAddresses.add(new EmailAddressInternal(ReceiveType, ReceiveAddress));
		emailTemp = null;
	}
	public final void addRecipient(byte ReceiveType, EmailAddress[] ReceiveAddress) throws NullException, NumberException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		if(ReceiveType < 0) throw new NumberException("ReceiveType", ReceiveType, true);
		if(ReceiveType > EmailAddress.ADDRESS_NEWS) throw new NumberException("ReceiveType", ReceiveType, 0, EmailAddress.ADDRESS_NEWS);
		for(int a = 0; a < ReceiveAddress.length; a++) if(ReceiveAddress[a] == null) throw new NullException("ReceiveAddress");
		for(int b = 0; b < ReceiveAddress.length; b++)
			emailAddresses.add(new EmailAddressInternal(ReceiveType, ReceiveAddress[b]));
		emailTemp = null;
	}
	public final void addAttachment(String FilePath) throws IOException, StringException, NullException, PermissionException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		addAttachment(new File(FilePath));
	}
	public final void addRecipient(byte ReceiveType, String ReceiveAddress) throws NullException, StringException, FormatException, NumberException
	{
		if(ReceiveType < 0) throw new NumberException("ReceiveType", ReceiveType, true);
		if(ReceiveType > EmailAddress.ADDRESS_NEWS) throw new NumberException("ReceiveType", ReceiveType, 0, EmailAddress.ADDRESS_NEWS);
		emailAddresses.add(new EmailAddressInternal(ReceiveType, new EmailAddress(ReceiveAddress)));
		emailTemp = null;
	}
	public final void addRecipient(byte ReceiveType, String[] ReceiveAddress) throws NullException, StringException, FormatException, NumberException
	{
		if(ReceiveAddress == null) throw new NullException("ReceiveAddress");
		if(ReceiveType < 0) throw new NumberException("ReceiveType", ReceiveType, true);
		if(ReceiveType > EmailAddress.ADDRESS_NEWS) throw new NumberException("ReceiveType", ReceiveType, 0, EmailAddress.ADDRESS_NEWS);
		EmailAddress[] a = new EmailAddress[ReceiveAddress.length];
		for(int b = 0; b < ReceiveAddress.length; b++) a[b] = new EmailAddress(ReceiveAddress[b]);
		for(int c = 0; c < ReceiveAddress.length; c++)
			emailAddresses.add(new EmailAddressInternal(ReceiveType, a[c]));
		emailTemp = null;
	}
	
	public final boolean isRead()
	{
		return emailFlags.flagData.getTagF();
	}
	public final boolean isDraft()
	{
		return emailFlags.flagData.getTagC();
	}
	public final boolean isFlagged()
	{
		return emailFlags.flagData.getTagD();
	}
	public final boolean isDeleted()
	{
		return emailFlags.flagData.getTagB();
	}
	public final boolean isAnswered()
	{
		return emailFlags.flagData.getTagA();
	}
	public final boolean isDownloading()
	{
		return emailDownload != null;
	}
	public final boolean containsAttachments()
	{
		return emailFile != -1;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Email && ((Email)CompareObject).emailAddresses.equals(emailAddresses) && ((Email)CompareObject).emailFlags.equals(emailFlags) &&
			   ((Email)CompareObject).emailParts.equals(emailParts) && ((Email)CompareObject).emailReceivedDate.equals(emailReceivedDate) &&
			   ((Email)CompareObject).emailSentDate.equals(emailSentDate) && ((Email)CompareObject).emailSubject.equals(emailSubject) &&
			   ((((Email)CompareObject).emailFrom == null && emailFrom == null) || (((Email)CompareObject).emailFrom != null && emailFrom != null && ((Email)CompareObject).emailFrom.equals(emailFrom)));
	}
	
	public final int hashCode()
	{
		return (emailSubject != null ? emailSubject.hashCode() : 0) + emailFile + emailAddresses.hashCode() + emailFlags.hashCode() +
			   emailParts.hashCode() + (emailFrom != null ? emailFrom.hashCode() : 0) + emailSentDate.hashCode() + emailReceivedDate.hashCode();
	}
	public final int getPartCount()
	{
		return emailParts.size();
	}
	public final int getDownloadCount()
	{
		return emailDownload != null ? emailDownload.downloaderTotal : -1;
	}
	public final int getRecipientCount()
	{
		return emailAddresses.size();
	}
	public final int getDownloadedCount()
	{
		return emailDownload != null ? emailDownload.downloaderCurrent : -1;
	}
	public final int getNextAttachmentIndex()
	{
		return emailFile;
	}
	
	public final String toString()
	{
		return "Email(" + getItemID() + ") " + emailSubject + " / " + (emailFrom != null ? emailFrom.toString() : "NS");
	}
	public final String getSubject()
	{
		return emailSubject;
	}
	
	public final Stamp getSentDate()
	{
		return emailSentDate;
	}
	public final Stamp getReceivedDate()
	{
		return emailReceivedDate;
	}
	
	public final Flags getEmailFlags()
	{
		return emailFlags;
	}
	
	public final EmailStream addNewPart()
	{
		EmailPart a = new EmailPart();
		a.partContent = DEFAULT_TYPE;
		emailParts.add(a);
		emailTemp = null;
		return a.getStream();
	}
	public final EmailStream getNextAttachment() throws IOException
	{
		if(emailFile == -1)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "This email does not have attachments!");
			throw new IOException("This email does not have attachments!");
		}
		int a = emailFile;
		for(int b = emailFile; b < emailParts.size(); b++)
		try
		{
			if(emailParts.get(b).getFileName() != null)
			{
				emailFile = b;
				break;
			}
		}
		catch (MessagingException Exception) { emailFile = -1; }
		if(emailFile == a) emailFile = -1;
		return getPart(a);
	}
	public final EmailStream getPart(int PartIndex) throws NumberException
	{
		if(PartIndex < 0) throw new NumberException("PartIndex", PartIndex, true);
		if(PartIndex > emailParts.size()) throw new NumberException("PartIndex", PartIndex, 0, emailParts.size());
		return emailParts.get(PartIndex).getStream();
	}
	public final EmailStream addNewPart(int AddIndex) throws NumberException
	{
		if(AddIndex < 0) throw new NumberException("AddIndex", AddIndex, true);
		if(AddIndex > emailParts.size()) throw new NumberException("AddIndex", AddIndex, 0, emailParts.size());
		EmailPart a = new EmailPart();
		emailParts.add(AddIndex, a);
		emailTemp = null;
		return a.getStream();
	}
	public final EmailStream addNewPart(String ContentType) throws NullException, StringException
	{
		if(ContentType == null) throw new NullException("ContentType");
		if(ContentType.isEmpty()) throw new StringException("ContentType");
		EmailPart a = new EmailPart();
		a.partContent = ContentType;
		emailParts.add(a);
		emailTemp = null;
		return a.getStream();
	}
	public final EmailStream addNewPart(int AddIndex, String ContentType) throws NullException, StringException, NumberException
	{
		if(ContentType == null) throw new NullException("ContentType");
		if(ContentType.isEmpty()) throw new StringException("ContentType");
		if(AddIndex < 0) throw new NumberException("AddIndex", AddIndex, true);
		if(AddIndex > emailParts.size()) throw new NumberException("AddIndex", AddIndex, 0, emailParts.size());
		EmailPart a = new EmailPart();
		a.partContent = ContentType;
		emailParts.add(AddIndex, a);
		emailTemp = null;
		return a.getStream();
	}
	
	public final EmailAddress getFrom()
	{
		return emailFrom;
	}
	public final EmailAddress getRecipient(int RecipientIndex) throws NumberException
	{
		if(RecipientIndex < 0) throw new NumberException("RecipientIndex", RecipientIndex, true);
		if(RecipientIndex > emailAddresses.size()) throw new NumberException("RecipientIndex", RecipientIndex, 0, emailAddresses.size());
		return emailAddresses.get(RecipientIndex).addressInstance;
	}
	
	public final Email getReply() throws IOException
	{
		return getReply(false);
	}
	public final Email getReply(boolean ReplyAll) throws IOException
	{
		if(emailFrom == null) throw new IOException("This email has no sender!");
		Email a = new Email();
		a.emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_TO, emailFrom));
		for(int b = 0; b < emailAddresses.size(); b++)
		{
			if(emailAddresses.get(b).addressType == EmailAddress.ADDRESS_REPLY)
				a.emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_TO, emailAddresses.get(b).addressInstance));
			else if(ReplyAll && emailAddresses.get(b).addressType != EmailAddress.ADDRESS_BCC)
				a.emailAddresses.add(new EmailAddressInternal(emailAddresses.get(b).addressType, emailAddresses.get(b).addressInstance));
		}
		setAnswered(true);
		for(int c = 0; c < emailParts.size(); c++) try
		{
			if(emailParts.get(c).getFileName() != null)
				a.emailParts.add(emailParts.get(c).clone());
		}
		catch (MessagingException Exception) { }
		a.emailSubject = "Re: " + emailSubject;
		return a;
	}
	
	public final EmailAddress[] getRecipients()
	{
		return emailAddresses.toArray(new EmailAddress[emailAddresses.size()]);
	}
	
	protected Email(MimeMessage Message, EmailLock MessageLock) throws IOException, MessagingException
	{
		this();
		emailTemp = Message;
		emailFlags.add(Message.getFlags());
		emailSubject = Message.getSubject();
		emailSentDate.updateStamp(Message.getSentDate());
		emailReceivedDate.updateStamp(Message.getReceivedDate());
		Address[] a = Message.getRecipients(RecipientType.TO);
		if(a != null && a.length > 0)
		{
			emailAddresses.ensureCapacity(a.length);
			for(int b = 0; b < a.length; b++)
				emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_TO, new EmailAddress(a[b])));
		}
		a = Message.getRecipients(RecipientType.CC);
		if(a != null && a.length > 0)
		{
			emailAddresses.ensureCapacity(emailAddresses.size() + a.length);
			for(int c = 0; c < a.length; c++)
				emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_CC, new EmailAddress(a[c])));
		}
		a = Message.getRecipients(RecipientType.BCC);
		if(a != null && a.length > 0)
		{
			emailAddresses.ensureCapacity(emailAddresses.size() + a.length);
			for(int d = 0; d < a.length; d++)
				emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_BCC, new EmailAddress(a[d])));
		}
		a = Message.getRecipients(MimeMessage.RecipientType.NEWSGROUPS);
		if(a != null && a.length > 0)
		{
			emailAddresses.ensureCapacity(emailAddresses.size() + a.length);
			for(int e = 0; e < a.length; e++)
				emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_NEWS, new EmailAddress(a[e])));
		}
		a = Message.getReplyTo();
		if(a != null && a.length > 0)
		{
			emailAddresses.ensureCapacity(emailAddresses.size() + a.length);
			for(int f = 0; f < a.length; f++)
				emailAddresses.add(new EmailAddressInternal(EmailAddress.ADDRESS_REPLY, new EmailAddress(a[f])));
		}
		a = Message.getFrom();
		if(a != null && a.length > 0)
			emailFrom = new EmailAddress(a[0]);
		Object g = Message.getContent();
		if(g instanceof MimeMultipart)
		{
			MimeMultipart h = (MimeMultipart)g;
			if(DOWNLOAD_ALL_THREAD)
				emailDownload = new EmailDownloader(this, h, MessageLock);
			else
			{
				if(h.getCount() == 2 && h.getBodyPart(0).getFileName() == null && h.getBodyPart(1).getFileName() == null)
					emailParts.add(new EmailPart(h.getBodyPart(1)));
				else
				{
					emailParts.add(new EmailPart(h.getBodyPart(0), true));
					if(h.getCount() > 1)
					{
						if(DOWNLOAD_OND)
							emailDownload = new EmailDownloader(this, h, MessageLock);
						else
						{
							emailParts.ensureCapacity(h.getCount());
							for(int i = 1; i < h.getCount(); i++)
							{
								if(h.getBodyPart(i).getFileName() != null && emailFile == -1)
									emailFile = i;
								emailParts.add(new EmailPart(h.getBodyPart(i)));
							}
						}
					}
				}
			}
		}
		else
			emailParts.add(new EmailPart(Message));
		g = null;
	}
	
	protected final void readItemFailure()
	{
		emailFile = -1;
		emailParts.clear();
		emailFrom = null;
		emailAddresses.clear();
		emailSubject = Constants.EMPTY_STRING;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		emailFile = itemEncoder.readInteger(InStream);
		emailSubject = itemEncoder.readString(InStream);
		emailFlags.readStorage(InStream, itemEncoder);
		emailSentDate.readStorage(InStream, itemEncoder);
		emailReceivedDate.readStorage(InStream, itemEncoder);
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
		EmailPart b = null;
		for( ; a > 0; a--)
		{
			b = new EmailPart();
			b.readStorage(InStream, itemEncoder);
			emailParts.add(b);
			b = null;
		}
		if(itemEncoder.readBoolean(InStream))
		{
			emailFrom = new EmailAddress();
			emailFrom.readStorage(InStream, itemEncoder);					
		}
		itemEncoder.readStorageList(InStream, emailAddresses);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeInteger(OutStream, emailFile);
		itemEncoder.writeString(OutStream, emailSubject);
		emailFlags.writeStorage(OutStream, itemEncoder);
		emailSentDate.writeStorage(OutStream, itemEncoder);
		emailReceivedDate.writeStorage(OutStream, itemEncoder);
		if(emailParts.size() < 255)
		{
			itemEncoder.writeByte(OutStream, 0);
			itemEncoder.writeByte(OutStream, emailParts.size());
		}
		else if(emailParts.size() < Constants.MAX_USHORT_SIZE)
		{
			itemEncoder.writeByte(OutStream, 1);
			itemEncoder.writeShort(OutStream, emailParts.size());
		}
		else
		{
			itemEncoder.writeByte(OutStream, 2);
			itemEncoder.writeInteger(OutStream, emailParts.size());
		}
		for(int a = 0; a < emailParts.size(); a++)
		{
			emailParts.get(a).closePart();
			emailParts.get(a).writeStorage(OutStream, itemEncoder);
		}
		itemEncoder.writeBoolean(OutStream, emailFrom != null);
		if(emailFrom != null) emailFrom.writeStorage(OutStream, itemEncoder);
		itemEncoder.writeStorageList(OutStream, emailAddresses);
	}	

	protected final MimeMessage getMessage(Session EmailSession) throws MessagingException, IOException
	{
		if(emailTemp == null)
		{
			emailTemp = new MimeMessage(EmailSession);
			if(emailFrom != null) emailTemp.setFrom(emailFrom.addressInstance);
			emailTemp.setSubject(emailSubject != null ? emailSubject : Constants.EMPTY_STRING);
			if(emailFlags.flagData.getTagH())
				emailSentDate.updateStamp();
			emailTemp.setSentDate(emailSentDate.getDate());
			emailTemp.getFlags().add(emailFlags);
			MimeMultipart a = new MimeMultipart();
			for(int b = 0; b < emailParts.size(); b++)
			{
				emailParts.get(b).closePart();
				a.addBodyPart(emailParts.get(b));
			}
			emailTemp.setContent(a);
			ArrayList<Address> c = new ArrayList<Address>();
			for(int d = 0; d < emailAddresses.size(); d++)
			{
				if(emailAddresses.get(d).addressType != EmailAddress.ADDRESS_REPLY)
					emailTemp.addRecipient(emailAddresses.get(d).getType(), emailAddresses.get(d).addressInstance.addressInstance);
				else
					c.add(emailAddresses.get(d).addressInstance.addressInstance);
			}
			emailTemp.setReplyTo(c.toArray(new Address[c.size()]));
			emailTemp.saveChanges();
		}
		return emailTemp;
	}

	protected final Email getCopy()
	{
		Email a = new Email();
		a.emailFile = emailFile;
		a.emailSubject = emailSubject;
		a.emailFlags.add(emailFlags);
		a.emailSentDate.updateStamp(emailSentDate);
		a.emailReceivedDate.updateStamp(emailReceivedDate);
		a.emailFrom = emailFrom;
		a.emailAddresses.addAll(emailAddresses);
		a.emailParts.ensureCapacity(a.emailParts.size());
		for(int b = 0; b < emailParts.size(); b++)
			a.emailParts.add(emailParts.get(b).clone());
		return a;
	}

	private static final class EmailDownloader extends Thread
	{
		private final Email downloaderEmail;
		private final EmailLock downloaderLock;
		private final MimeMultipart downloaderPart;
		
		private int downloaderTotal;
		private int downloaderCurrent;
		
		public final void run()
		{
			downloaderLock.lockCounts++;
			try
			{
				if(DOWNLOAD_ALL_THREAD)
				{
					if(downloaderPart.getCount() == 2 && downloaderPart.getBodyPart(0).getFileName() == null && downloaderPart.getBodyPart(1).getFileName() == null)
						downloaderEmail.emailParts.add(new EmailPart(downloaderPart.getBodyPart(1)));
					else
						downloaderEmail.emailParts.add(new EmailPart(downloaderPart.getBodyPart(0), true));
				}
				if(downloaderPart.getCount() > 1)
				{
					downloaderEmail.emailParts.ensureCapacity(downloaderPart.getCount());
					for(downloaderCurrent = 1; downloaderCurrent < downloaderPart.getCount(); downloaderCurrent++)
					{
						if(downloaderPart.getBodyPart(downloaderCurrent).getFileName() != null && downloaderEmail.emailFile == -1)
							downloaderEmail.emailFile = downloaderCurrent;
						downloaderEmail.emailParts.add(new EmailPart(downloaderPart.getBodyPart(downloaderCurrent)));
					}
				}
			}
			catch (Throwable Exception)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			}
			downloaderEmail.emailDownload = null;
			downloaderLock.lockCounts--;
		}
		
		private EmailDownloader(Email EmailDownload, MimeMultipart DownloadPart, EmailLock Lock)
		{
			setPriority(1);
			downloaderLock = Lock;
			downloaderPart = DownloadPart;
			downloaderEmail = EmailDownload;
			try
			{
				downloaderTotal = DownloadPart.getCount();
			}
			catch (MessagingException Exception)
			{
				Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			}
			setName("EmailDownloader for " + EmailDownload.getSubject());
			start();
		}
	}
}