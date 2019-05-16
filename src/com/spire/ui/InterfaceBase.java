package com.spire.ui;

import java.awt.Window;
import javax.swing.JFrame;
import com.spire.util.HashKey;
import com.spire.util.Monitor;
import com.spire.sec.Security;
import com.spire.util.Constants;
import com.spire.ex.UIException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import java.awt.event.ComponentEvent;
import com.spire.ex.InternalException;
import java.awt.event.ComponentAdapter;
import com.spire.ex.PermissionException;

public abstract class InterfaceBase implements HashKey<Long>, Interface
{
	protected final Long interfaceID;
	protected final Window interfaceWindow;
	
	private byte interfaceScreen;
	private boolean interfaceFull;
	private boolean interfaceClosed;
	private InterfacePos interfacePos;
	private InterfaceFreezer interfaceHold;

	public final void toBack() throws PermissionException
	{
		Security.check("ui.set.back", interfaceID);
		interfaceWindow.toBack();
	}
	public final void toFront() throws PermissionException
	{
		Security.check("ui.set.front", interfaceID);
		interfaceWindow.toFront();
	}
	public final void hideWindow() throws PermissionException
	{
		Security.check("ui.hide", interfaceID);
		if(!interfaceClosed) interfaceWindow.setVisible(false);
	}
	public final void closeWindow() throws PermissionException
	{
		Security.check("ui.close", interfaceID);
		if(!interfaceClosed)
		{
			interfaceClosed = true;
			interfaceWindow.setVisible(false);
			interfaceWindow.dispose();
		}
	}
	public final void setWindowCenter() throws PermissionException
	{
		Security.check("ui.set.loc", interfaceID);
		setWindowFullScreen(false);
		interfaceWindow.setLocationRelativeTo(null);
	}
	public final void setWindowHideOnClose() throws PermissionException
	{
		setWindowCloseOperation(1);
	}
	public final void setWindowStopOnClose() throws PermissionException
	{
		setWindowCloseOperation(3);
	}
	public final void setWindowDisposeOnClose() throws PermissionException
	{
		setWindowCloseOperation(2);
	}
	public final void setWindowNothingOnClose() throws PermissionException
	{
		setWindowCloseOperation(0);
	}
	public final void showWindow() throws PermissionException, UIException
	{
		Security.check("ui.show", interfaceID);
		if(interfaceClosed) throw new UIException("The Interface was already closed!");
		if(!interfaceClosed)
		{
			interfaceWindow.setVisible(true);
			if(interfaceWindow instanceof JFrame && ((JFrame)interfaceWindow).getState() != 0)
				((JFrame)interfaceWindow).setState(0);
		}
	}
	public final void setWindowOnDefaultScreen() throws PermissionException
	{
		setWindowOnScreen(0);
	}
	public final void restoreWindow() throws PermissionException, UIException
	{
		Security.check("ui.restore", interfaceID);
		if(interfaceClosed) throw new UIException("The Interface was already closed!");
		if(!interfaceClosed && interfaceWindow instanceof JFrame) ((JFrame)interfaceWindow).setState(0);
	}
	public final void minimizeWindoe() throws PermissionException, UIException
	{
		Security.check("ui.minimize", interfaceID);
		if(interfaceClosed) throw new UIException("The Interface was already closed!");
		if(!interfaceClosed && interfaceWindow instanceof JFrame) ((JFrame)interfaceWindow).setState(1);
	}
	public final void setWindowOnTop(boolean OnTop) throws PermissionException
	{
		Security.check("ui.set.top", interfaceID);
		interfaceWindow.setAlwaysOnTop(OnTop);
	}
	public final void setWindowResizable(boolean CanResize) throws PermissionException
	{
		Security.check("ui.set.resize", interfaceID);
		if(interfaceWindow instanceof JFrame) ((JFrame)interfaceWindow).setResizable(CanResize);
	}
	public final void setWindowLocation(int PosX, int PosY) throws PermissionException
	{
		Security.check("ui.set.loc", interfaceID);
		setWindowFullScreen(false);
		interfaceWindow.setLocation(PosX, PosY);
	}
	public final void setWindowFrozen(boolean FreezeWindow) throws PermissionException
	{
		Security.check("ui.set.frozen", interfaceID);
		if(!isWindowFrozen() && FreezeWindow)
		{
			if(interfaceHold == null)
				interfaceHold = new InterfaceFreezer(interfaceWindow);
			interfaceHold.setWindowFrozen();
			interfaceWindow.addComponentListener(interfaceHold);
		}
		else if(isWindowFrozen() && !FreezeWindow)
		{
			interfaceWindow.removeComponentListener(interfaceHold);
			interfaceHold.clear();
		}
	}
	public final void setWindowTitle(String WindowTitle) throws PermissionException, NullException
	{
		Security.check("ui.set.title", interfaceID);
		if(WindowTitle == null) throw new NullException("WindowTitle");
		if(interfaceWindow instanceof JFrame) ((JFrame)interfaceWindow).setTitle(WindowTitle);
	}
	public final void setWindowOnScreen(int ScreenIndex) throws NumberException, PermissionException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > Monitor.getScreenCount()) throw new NumberException("ScreenIndex", ScreenIndex, 0, Monitor.getScreenCount());
		Security.check("ui.screen", interfaceID);
		Security.check("ui.screen.index", Byte.valueOf((byte)ScreenIndex));
		setWindowFullScreen(false);
		int a = interfaceWindow.getX(), b = interfaceWindow.getY();
		a -= Monitor.getScreenPosX(interfaceScreen);
		b -= Monitor.getScreenPosY(interfaceScreen);
		a += Monitor.getScreenPosX(ScreenIndex);
		b += Monitor.getScreenPosY(ScreenIndex);
		interfaceScreen = (byte)ScreenIndex;
		interfaceWindow.setLocation(a, b);
	}
	public final void setWindowSize(int Width, int Height) throws PermissionException, NumberException
	{
		Security.check("ui.set.size", interfaceID);
		if(Width < 0) throw new NumberException("Width", Width, false);
		if(Height < 0) throw new NumberException("Height", Height, false);
		setWindowFullScreen(false);
		interfaceWindow.setSize(Width, Height);
	}
	public final void setWindowFullScreen(boolean FullScreen) throws InternalException, PermissionException
	{
		Security.check("ui.full", interfaceID);
		Security.check("ui.full.index", Byte.valueOf(interfaceScreen));
		if(FullScreen && !interfaceFull)
		{
			interfacePos = new InterfacePos(interfaceWindow.getX(), interfaceWindow.getY());
			Monitor.getScreenDevice(interfaceScreen).setFullScreenWindow(interfaceWindow);
			interfaceFull = true;
		}
		else if(!FullScreen && interfaceFull)
		{
			Monitor.getScreenDevice(interfaceScreen).setFullScreenWindow(null);
			interfaceWindow.setLocation(interfacePos.posX, interfacePos.posY);
			interfacePos = null;
			interfaceFull = false;
		}
	}
	public final void setWindowCloseOperation(int CloseOperation) throws PermissionException, NumberException
	{
		Security.check("ui.set.close", interfaceID);
		if(CloseOperation < 0) throw new NumberException("CloseOperation", CloseOperation, false);
		if(CloseOperation > 3) throw new NumberException("CloseOperation", CloseOperation, 0, 3);
		if(interfaceWindow instanceof JFrame) ((JFrame)interfaceWindow).setDefaultCloseOperation(CloseOperation);
	}
	
	public final boolean isOnTop()
	{
		return interfaceWindow.isAlwaysOnTop();
	}
	public final boolean isDisplayed()
	{
		return !interfaceClosed && interfaceWindow.isVisible();
	}
	public final boolean isWindowFrozen()
	{
		return interfaceHold != null && interfaceHold.freezerActive;
	}
	public final boolean isWindowFullScreen()
	{
		return interfaceFull;
	}
	
	public final byte getWindowScreenIndex()
	{
		return interfaceScreen;
	}
	
	public final long getWindowID()
	{
		return interfaceID.longValue();
	}
	
	public final Long getKey()
	{
		return interfaceID;
	}
	
	protected InterfaceBase(Window InterfaceWindow) throws NullException
	{
		this(Constants.RNG.nextLong(), InterfaceWindow);
	}
	protected InterfaceBase(long InterfaceID, Window InterfaceWindow) throws NullException
	{
		if(InterfaceWindow == null) throw new NullException("InterfaceWindow");
		interfaceWindow = InterfaceWindow;
		interfaceID = Long.valueOf(InterfaceID);
	}
	
	protected static final class InterfacePos
	{
		protected int posX;
		protected int posY;
		
		protected InterfacePos(int PosX, int PosY)
		{
			posX = PosX;
			posY = PosY;
		}
	}
	protected static final class InterfaceFreezer extends ComponentAdapter
	{
		private final Window freezerWindow;
		
		private int freezerPosX;
		private int freezerPosY;
		
		protected boolean freezerActive;
		
		public final void componentMoved(ComponentEvent Event)
		{
			freezerWindow.setLocation(freezerPosX, freezerPosY);
		}
		
		protected InterfaceFreezer(Window WindowBase)
		{
			freezerWindow = WindowBase;
		}
		
		protected final void clear()
		{
			freezerActive = false;
		}
		protected final void setWindowFrozen()
		{
			freezerPosX = freezerWindow.getX();
			freezerPosY = freezerWindow.getY();
			freezerActive = true;
		}
	}
}