package com.spire.ui;

import com.spire.util.BoolTag;

public enum InterfaceEvent
{
	KEY_PRESS(0),
	KEY_RELEASED(1),
	WINDOW_OPENED(2),
	WINDOW_CLOSED(3),
	WINDOW_CLOSING(4),
	BUTTON_PRESSED(5),
	WINDOW_MINIMIZED(6),
	WINDOW_ACTIVATED(7),
	WINDOW_NORMALISED(0),
	WINDOW_INCACTIVATED(1);
	
	public final byte eventID;
	
	public final boolean canProcessEvent(BoolTag ActionKey)
	{
		return ActionKey.getTag(eventID);
	}
	
	private InterfaceEvent(int EventID)
	{
		eventID = (byte)EventID;
	}
}