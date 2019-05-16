package com.spire.ui;

import com.spire.ex.UIException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.InternalException;
import com.spire.ex.PermissionException;

public interface Interface
{
	void toBack() throws PermissionException;
	void toFront() throws PermissionException;
	void hideWindow() throws PermissionException;
	void closeWindow() throws PermissionException;
	void setWindowCenter() throws PermissionException;
	void setWindowHideOnClose() throws PermissionException;
	void setWindowStopOnClose() throws PermissionException;
	void setWindowDisposeOnClose() throws PermissionException;
	void setWindowNothingOnClose() throws PermissionException;
	void showWindow() throws PermissionException, UIException;
	void setWindowOnDefaultScreen() throws PermissionException;
	void restoreWindow() throws PermissionException, UIException;
	void minimizeWindoe() throws PermissionException, UIException;
	void setWindowOnTop(boolean OnTop) throws PermissionException;
	void setWindowResizable(boolean CanResize) throws PermissionException;
	void setWindowLocation(int PosX, int PosY) throws PermissionException;
	void setWindowFrozen(boolean FreezeWindow) throws PermissionException;
	void setWindowTitle(String WindowTitle) throws PermissionException, NullException;
	void setWindowOnScreen(int ScreenIndex) throws NumberException, PermissionException;
	void setWindowSize(int Width, int Height) throws PermissionException, NumberException;
	void setWindowFullScreen(boolean FullScreen) throws InternalException, PermissionException;
	void setWindowCloseOperation(int CloseOperation) throws PermissionException, NumberException;
	
	boolean isOnTop();
	boolean isDisplayed();
	boolean isWindowFrozen();
	boolean isWindowFullScreen();
	
	byte getWindowScreenIndex();
}