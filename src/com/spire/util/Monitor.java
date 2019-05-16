package com.spire.util;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.util.ArrayList;
import com.spire.log.Reporter;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import com.spire.ex.NumberException;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;

public final class Monitor
{
	private static final ArrayList<MonitorInfo> monitorConfig = new ArrayList<MonitorInfo>();
	
	public static final void refreshMonitors()
	{
		if(Constants.ISMOBILE) return;
		if(!monitorConfig.isEmpty()) monitorConfig.clear();
		try
		{
			Reporter.debug(Reporter.REPORTER_GUI, "Refreshing Monitor Index");
			GraphicsDevice[] a = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			for(byte b = 0; b < a.length && b < 120; b++) monitorConfig.add(new MonitorInfo(a[b].getDefaultConfiguration()));
		}
		catch (HeadlessException Exception)
		{
			Reporter.error(Reporter.REPORTER_GUI, Exception);
		}
	}
	
	public static final boolean isAnyMonitors()
	{
		return monitorConfig.size() > 0;
	}
	public static final boolean windowFullScreen(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoGraphics.getDevice().getFullScreenWindow() != null;
	}
	public static final boolean supportsFullScreen(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoGraphics.getDevice().isFullScreenSupported();
	}
	public static final boolean supportsAcceleration(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoAccle;
	}
	public static final boolean supportsTransperency(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoTrans;
	}
	
	public static final int getScreenCount()
	{
		return monitorConfig.size();
	}
	public static final int getScreenPosX(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoBounds.x;
	}
	public static final int getScreenPosY(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoBounds.y;
	}
	public static final int getScreenWidth(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoBounds.width;
	}
	public static final int getScreenHeight(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoBounds.height;
	}
	public static final int getScreenAbsWidth(int ScreenIndex, int ScreenWidth) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		if(ScreenWidth < 0)
			return monitorConfig.get(ScreenIndex).infoBounds.width - (ScreenWidth * -1);
		return ScreenWidth;
	}
	public static final int getScreenAbsHeight(int ScreenIndex, int ScreenHeight) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		if(ScreenHeight < 0)
			return monitorConfig.get(ScreenIndex).infoBounds.height - (ScreenHeight * -1);
		return ScreenHeight;
	}
	
	public static final Dimension getScreenSize(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoBounds.getSize();
	}
	
	public static final GraphicsDevice getScreenDevice(int ScreenIndex) throws NumberException
	{
		if(ScreenIndex < 0) throw new NumberException("ScreenIndex", ScreenIndex, false);
		if(ScreenIndex > monitorConfig.size()) throw new NumberException("ScreenIndex", ScreenIndex, 0, monitorConfig.size());
		return monitorConfig.get(ScreenIndex).infoGraphics.getDevice();
	}
	
	private Monitor() { }
	
	static
	{
		refreshMonitors();
	}
	
	private static final class MonitorInfo
	{
		private final boolean infoAccle;
		private final boolean infoTrans;
		private final Rectangle infoBounds;
		private final GraphicsConfiguration infoGraphics;
		
		private MonitorInfo(GraphicsConfiguration Graphics)
		{
			infoGraphics = Graphics;
			infoBounds = Graphics.getBounds();
			infoTrans = Graphics.isTranslucencyCapable();
			infoAccle = Graphics.getImageCapabilities().isAccelerated();
		}
	}
}