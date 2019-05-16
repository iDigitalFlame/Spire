package com.derp.manager;

import org.netcom.io.Item;

public final class FileManager
{
	protected static final int PORT_NUMBER = 7552;

	static
	{
		Item.addItemMapping(80, ManagerCommand.class);
		Item.addItemMapping(81, ManagerListing.class);
	}
}
