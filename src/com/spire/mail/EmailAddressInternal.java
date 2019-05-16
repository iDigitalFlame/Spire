package com.spire.mail;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.io.Streamable;
import javax.mail.Message.RecipientType;

public final class EmailAddressInternal implements Storage
{
	protected final EmailAddress addressInstance;
	
	protected byte addressType;
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		addressType = StorageEncoder.readByte(InStream);
		addressInstance.readStorage(InStream, StorageEncoder);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByte(OutStream, addressType);
		addressInstance.writeStorage(OutStream, StorageEncoder);
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailAddress && addressInstance.equals(addressInstance);
	}
	
	protected EmailAddressInternal()
	{
		addressInstance = new EmailAddress();
	}
	protected EmailAddressInternal(byte AddressType, EmailAddress AddressInstance)
	{
		addressType = AddressType;
		addressInstance = AddressInstance;
	}
	
	protected final RecipientType getType()
	{
		switch(addressType)
		{
		case EmailAddress.ADDRESS_TO:
			return RecipientType.TO;
		case EmailAddress.ADDRESS_CC:
			return RecipientType.CC;
		case EmailAddress.ADDRESS_BCC:
			return RecipientType.BCC;
		case EmailAddress.ADDRESS_NEWS:
			return javax.mail.internet.MimeMessage.RecipientType.NEWSGROUPS;
		case EmailAddress.ADDRESS_REPLY:
			return RecipientType.TO;
		default:
			return RecipientType.TO;
		}
	}
}