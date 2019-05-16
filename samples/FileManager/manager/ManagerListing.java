package com.derp.manager;

import java.io.IOException;
import java.util.ArrayList;
import org.netcom.io.Stream;
import org.netcom.net.NetworkPacket;

public final class ManagerListing extends NetworkPacket
{
	protected final ArrayList<ManagerRecord> listing_Records;

	protected String listing_Dirctory;

	public ManagerListing(Stream inStream)
	{
		this((String)null);
		readItemData(inStream);
	}

	protected ManagerListing(String Directory)
	{
		super(false, 80, 81);
		listing_Dirctory = Directory;
		listing_Records = new ArrayList<ManagerRecord>();
	}

	protected final void readSubItem(Stream inStream) throws IOException
	{
		listing_Dirctory = itemEnc.readLongString(inStream);
		itemEnc.readStorageList(inStream, ManagerRecord.class, listing_Records);
	}
	protected final void writeSubItem(Stream outStream) throws IOException
	{
		itemEnc.writeLongString(outStream, listing_Dirctory);
		itemEnc.writeStorageList(outStream, listing_Records);
	}

	protected final ManagerListing doClone()
	{
		return new ManagerListing(listing_Dirctory);
	}
}