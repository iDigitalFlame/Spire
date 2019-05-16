package com.spire.mail;

import com.spire.io.Item;

import java.io.IOException;
import java.util.Properties;

import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

public final class EmailSettings extends Item
{
	public static final byte EMAIL_SMTP = 0;
	public static final byte EMAIL_IMAP = 1;
	public static final byte EMAIL_POP3 = 2;
	public static final byte ITEM_CLASS_ID = 17;
	public static final byte EMAIL_IMAP_SSL = 3;
	public static final byte EMAIL_POP3_SSL = 4;
	public static final byte EMAIL_SMTP_AUTH = 5;
	//public static final byte EMAIL_SMTP_IMAP = 6;
	//public static final byte EMAIL_SMTP_POP3 = 7;
	//public static final byte EMAIL_SMTP_IMAP_SSL = 8;
	//public static final byte EMAIL_SMTP_POP3_SSL = 9;
	// ^ Maybe later a little complicated
	
	public static final short EMAIL_IMAP_SSL_PORT = 587;
	
	public static final EmailSettings EMAIL_GMAIL_IMAP = new EmailSettings(EMAIL_IMAP_SSL, "imap.gmail.com", 993, true);
	public static final EmailSettings EMAIL_GMAIL_SMTP = new EmailSettings(EMAIL_SMTP_AUTH, "smtp.gmail.com", 587, true);
	
	protected byte settingsType;
	protected short settingsPort;
	protected boolean settingsSTLS;
	protected String settingsServer;
	
	public final boolean getSettingsUseStartTLS()
	{
		return settingsSTLS;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailSettings && CompareObject.hashCode() == hashCode();
	}
	
	public final int hashCode()
	{
		return settingsType + 1 * (getSettingsPort()) + settingsServer.hashCode() + (settingsSTLS ? 1 : 8);
	}
	public final int getSettingsPort()
	{
		return settingsPort & 0xFFFF;
	}
	
	public final String toString()
	{
		return "EmailSettings(" + getItemID() + ") " + settingsServer;
	}
	public final String getSettingsServer()
	{
		return settingsServer;
	}
	
	public static final EmailSettings getSMTPSettings(String ServerAddress) throws NullException, StringException
	{
		return getSMTPSettings(ServerAddress, 25, false);
	}
	public static final EmailSettings getIMAPSettings(String ServerAddress) throws NullException, StringException
	{
		return getIMAPSettings(ServerAddress, 143, false);
	}
	public static final EmailSettings getPOP3Settings(String ServerAddress) throws NullException, StringException
	{
		return getPOP3Settings(ServerAddress, 110, false);
	}
	public static final EmailSettings getIMAPSecureSettings(String ServerAddress) throws NullException, StringException
	{
		return getIMAPSecureSettings(ServerAddress, 993, true);
	}
	public static final EmailSettings getPOP3SecureSettings(String ServerAddress) throws NullException, StringException
	{
		return getPOP3SecureSettings(ServerAddress, 995, true);
	}
	public static final EmailSettings getSTMPAuthSettings(String ServerAddress) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_SMTP_AUTH, ServerAddress, 587, true);
	}
	public static final EmailSettings getSMTPSettings(String ServerAddress, int ServerPort) throws NullException, StringException, NumberException
	{
		return getSMTPSettings(ServerAddress, ServerPort, false);
	}
	public static final EmailSettings getIMAPSettings(String ServerAddress, int ServerPort) throws NullException, StringException, NumberException
	{
		return getIMAPSettings(ServerAddress, ServerPort, false);
	}
	public static final EmailSettings getPOP3Settings(String ServerAddress, int ServerPort) throws NullException, StringException, NumberException
	{
		return getPOP3Settings(ServerAddress, ServerPort, false);
	}
	public static final EmailSettings getSTMPAuthSettings(String ServerAddress, int ServerPort) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_SMTP_AUTH, ServerAddress, ServerPort, true);
	}
	public static final EmailSettings getIMAPSecureSettings(String ServerAddress, int ServerPort) throws NullException, StringException, NumberException
	{
		return getIMAPSecureSettings(ServerAddress, ServerPort, true);
	}
	public static final EmailSettings getPOP3SecureSettings(String ServerAddress, int ServerPort) throws NullException, StringException, NumberException
	{
		return getPOP3SecureSettings(ServerAddress, ServerPort, true);
	}
	public static final EmailSettings getSMTPSettings(String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_SMTP, ServerAddress, ServerPort, UseTLS);
	}
	public static final EmailSettings getIMAPSettings(String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_IMAP, ServerAddress, ServerPort, UseTLS);
	}
	public static final EmailSettings getPOP3Settings(String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_POP3, ServerAddress, ServerPort, UseTLS);
	}
	public static final EmailSettings getSTMPAuthSettings(String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_SMTP_AUTH, ServerAddress, ServerPort, UseTLS);
	}
	public static final EmailSettings getIMAPSecureSettings(String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_IMAP_SSL, ServerAddress, ServerPort, UseTLS);
	}
	public static final EmailSettings getPOP3SecureSettings(String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		return new EmailSettings(EMAIL_POP3_SSL, ServerAddress, ServerPort, UseTLS);
	}
	
	protected EmailSettings()
	{
		super(ITEM_CLASS_ID);
	}
	
	protected final void readItemFailure()
	{
		settingsType = -1;
		settingsServer = null;
	}
	protected final void addSettings(Properties Settings)
	{
		switch(settingsType)
		{
		case EMAIL_SMTP:
			Settings.put("mail.smtp.auth", "false");
			Settings.put("mail.smtp.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.smtp.host", settingsServer);
			Settings.put("mail.smtp.port", String.valueOf(getSettingsPort()));
			break;
		case EMAIL_IMAP:
			Settings.put("mail.store.protocol", "imap");
			Settings.put("mail.imap.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.imap.host", settingsServer);
			Settings.put("mail.imap.port", String.valueOf(getSettingsPort()));
			break;
		case EMAIL_POP3:
			Settings.put("mail.store.protocol", "pop3");
			Settings.put("mail.pop3.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.pop3.host", settingsServer);
			Settings.put("mail.pop3.port", String.valueOf(getSettingsPort()));
			break;
		case EMAIL_IMAP_SSL:
			Settings.put("mail.store.protocol", "imaps");
			Settings.put("mail.imap.port", String.valueOf(getSettingsPort()));
			Settings.put("mail.imaps.port", String.valueOf(getSettingsPort()));
			Settings.put("mail.imap.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.imap.host", settingsServer);
			Settings.put("mail.imap.port", String.valueOf(getSettingsPort()));
			Settings.put("mail.imaps.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.imaps.host", settingsServer);
			Settings.put("mail.imaps.port", String.valueOf(getSettingsPort()));
			break;
		case EMAIL_POP3_SSL:
			Settings.put("mail.store.protocol", "pop3s");
			Settings.put("mail.pop3.port", String.valueOf(getSettingsPort()));
			Settings.put("mail.pop3s.port", String.valueOf(getSettingsPort()));
			Settings.put("mail.pop3.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.pop3.host", settingsServer);
			Settings.put("mail.pop3.port", String.valueOf(getSettingsPort()));
			Settings.put("mail.pop3s.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.pop3s.host", settingsServer);
			Settings.put("mail.pop3s.port", String.valueOf(getSettingsPort()));
			break;
		case EMAIL_SMTP_AUTH:
			Settings.put("mail.smtp.auth", "true");
			Settings.put("mail.smtp.starttls.enable", String.valueOf(settingsSTLS));
			Settings.put("mail.smtp.host", settingsServer);
			Settings.put("mail.smtp.port", String.valueOf(getSettingsPort()));
			break;
		}
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		settingsType = itemEncoder.readByte(InStream);
		settingsPort = itemEncoder.readShort(InStream);
		settingsSTLS = itemEncoder.readBoolean(InStream);
		settingsServer = itemEncoder.readString(InStream);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeByte(OutStream, settingsType);
		itemEncoder.writeShort(OutStream, settingsPort);
		itemEncoder.writeBoolean(OutStream, settingsSTLS);
		itemEncoder.writeString(OutStream, settingsServer);
	}
	
	protected final String getSettingsType()
	{
		switch(settingsType)
		{
		case EMAIL_SMTP:
			return "smtp";
		case EMAIL_IMAP:
			return "imap";
		case EMAIL_POP3:
			return "pop3";
		case EMAIL_IMAP_SSL:
			return "imaps";
		case EMAIL_POP3_SSL:
			return "pop3s";
		case EMAIL_SMTP_AUTH:
			return "smtps";
		default:
			return "stmp";
		}
	}
	
	protected final EmailSettings getCopy()
	{
		return new EmailSettings(settingsType, settingsServer, getSettingsPort(), settingsSTLS);
	}

	private EmailSettings(byte SettingsType, String ServerAddress, int ServerPort, boolean UseTLS) throws NullException, StringException, NumberException
	{
		super(ITEM_CLASS_ID);
		if(ServerAddress == null) throw new NullException("ServerAddress");
		if(ServerAddress.isEmpty()) throw new StringException("ServerAddress");
		if(ServerPort <= 0) throw new NumberException("ServerPort", ServerPort, true);
		if(ServerPort > Constants.MAX_USHORT_SIZE) throw new NumberException("ServerPort", ServerPort, 1, Constants.MAX_USHORT_SIZE);
		settingsSTLS = UseTLS;
		settingsType = SettingsType;
		settingsServer = ServerAddress;
		settingsPort = (short)ServerPort;
	}
}