package com.spire.ui;

import com.spire.io.Item;
import java.awt.AWTEvent;
import java.io.IOException;
import com.spire.log.Reporter;
import com.spire.util.BoolTag;
import com.spire.io.Streamable;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import com.spire.ex.ObjectIDException;
import com.spire.io.ItemControllerObject;

public abstract class InterfaceAction extends Item implements ItemControllerObject, IInterfaceAction
{
	public static final byte ITEM_CLASS_ID = 14;
	
	protected short actionTriggerKey;
	protected char actionTriggerKeyChar;
	protected InterfaceEvent actionTrigger;
	
	private final byte actionID;
	private final BoolTag actionKey;
	
	private Interface actionWindow;
	
	public final void keyTyped(KeyEvent KeyTyped)
	{
		if(InterfaceEvent.KEY_PRESS.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.KEY_PRESS;
			actionTriggerKeyChar = KeyTyped.getKeyChar();
			actionTriggerKey = (short)KeyTyped.getKeyCode();
			processAction(actionWindow, KeyTyped);
			actionTrigger = null;
		}
	}
	public final void keyPressed(KeyEvent KeyPressed)
	{
		if(InterfaceEvent.KEY_PRESS.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.KEY_PRESS;
			actionTriggerKeyChar = KeyPressed.getKeyChar();
			actionTriggerKey = (short)KeyPressed.getKeyCode();
			processAction(actionWindow, KeyPressed);
			actionTrigger = null;
		}
	}
	public final void keyReleased(KeyEvent KeyReleased)
	{
		if(InterfaceEvent.KEY_RELEASED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.KEY_RELEASED;
			actionTriggerKeyChar = KeyReleased.getKeyChar();
			actionTriggerKey = (short)KeyReleased.getKeyCode();
			processAction(actionWindow, KeyReleased);
			actionTrigger = null;
		}
	}
	public final void windowClosed(WindowEvent WindowClosed)
	{
		if(InterfaceEvent.WINDOW_CLOSED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_CLOSED;
			processAction(actionWindow, WindowClosed);
			actionTrigger = null;
		}
	}
	public final void windowOpened(WindowEvent WindowOpened)
	{
		if(InterfaceEvent.WINDOW_OPENED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_OPENED;
			processAction(actionWindow, WindowOpened);
			actionTrigger = null;
		}
	}
	public final void windowClosing(WindowEvent WindowClosing)
	{
		if(InterfaceEvent.WINDOW_CLOSING.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_CLOSING;
			processAction(actionWindow, WindowClosing);
			actionTrigger = null;
		}
	}
	public final void actionPerformed(ActionEvent BunntonPressed)
	{
		if(InterfaceEvent.BUTTON_PRESSED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.BUTTON_PRESSED;
			processAction(actionWindow, BunntonPressed);
			actionTrigger = null;
		}
	}
	public final void windowActivated(WindowEvent WindowActivated)
	{
		if(InterfaceEvent.WINDOW_ACTIVATED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_ACTIVATED;
			processAction(actionWindow, WindowActivated);
			actionTrigger = null;
		}
	}
	public final void windowIconified(WindowEvent WindowMinimized)
	{
		if(InterfaceEvent.WINDOW_MINIMIZED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_MINIMIZED;
			processAction(actionWindow, WindowMinimized);
			actionTrigger = null;
		}
	}
	public final void windowDeiconified(WindowEvent WindowNormalised)
	{
		if(InterfaceEvent.WINDOW_NORMALISED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_NORMALISED;
			processAction(actionWindow, WindowNormalised);
			actionTrigger = null;
		}
	}
	public final void windowDeactivated(WindowEvent WindowDeactivated)
	{
		if(InterfaceEvent.WINDOW_INCACTIVATED.canProcessEvent(actionKey))
		{
			actionTrigger = InterfaceEvent.WINDOW_INCACTIVATED;
			processAction(actionWindow, WindowDeactivated);
			actionTrigger = null;
		}
	}
	public final void processAction(Interface ActionWindow, AWTEvent InterfaceEvent)
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_GUI, "InterfaceAction " + getClass().getSimpleName() + " was triggered!");
			processIAction(ActionWindow, InterfaceEvent);
		}
		catch (Throwable Exception)
		{
			Reporter.error(Reporter.REPORTER_GUI, "Interface Action(" + getClass().getSimpleName() + ") threw an error on trigger! Exception: " + Exception.toString());
		}
	}
	
	public final int getItemSubID()
	{
		return actionID & 0xFF;
	}
	
	public final String toString()
	{
		return "InterfaceAction(" + getItemID() + ":" + getItemSubID() + ") " + getClass().getSimpleName();
	}
	
	protected InterfaceAction(int ActionID) throws ObjectIDException
	{
		super(ITEM_CLASS_ID);
		actionID = (byte)ActionID;
		actionKey = new BoolTag();
		Item.addItemObjectMapping(ITEM_CLASS_ID, ActionID, getClass());
	}

	protected final void registerWindow(Interface ActionWindow)
	{
		actionWindow = ActionWindow;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		actionKey.setTagData(itemEncoder.readByte(InStream));
		readAction(InStream);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeByte(OutStream, actionKey.getTagData());
		writeAction(OutStream);
	}
	protected abstract void readAction(Streamable InStream) throws IOException;
	protected abstract void writeAction(Streamable OutStream) throws IOException;
	protected abstract void processIAction(Interface ActionWindow, AWTEvent InterfaceEvent);
}