package com.derp.io;

import java.util.Vector;

import org.netcom.net.NetworkOutput;
import org.netcom.stat.Reporter;
import org.netcom.types.Computer;
import org.netcom.ui.ITempleteBase;
import org.netcom.ui.Interface;
import org.netcom.ui.InterfaceAction;
import org.netcom.ui.InterfaceBase;

import com.derp.fm.FileRecord;

public final class FileViewer
{
	private final Interface fileWindow;
	private final Interface fileControls;
	private final Vector<FileRecord> filesLoaded;

	private String fileDirectory;
	private String fileLastDirectory;
	private String filePastDirectory;
	private Computer fileConnected;
	private NetworkOutput fileNetwork;

	public FileViewer()
	{
		this(null, null);
	}
	public FileViewer(Computer Connect, NetworkOutput Network)
	{
		if(Connect != null && Network == null) throw Reporter.throwFormat("Network cannot be null when trying to connect to a client!");
		fileWindow = new Interface("File Window", 500, 450);
		fileControls = new Interface("File Manager", 500, 80);
		filesLoaded = new Vector<FileRecord>();
		fileConnected = Connect;
		fileNetwork = Network;
		fileWindow.setTemplete(new ViewerWindow());
		fileControls.setTemplete(new ViewerControls(this));
		fileControls.showWindow();
		fileWindow.showWindow();
	}

	private final void loadFiles()
	{
		filesLoaded.clear();

	}
	private final void loadWindow()
	{
		if(filesLoaded.isEmpty()) loadFiles();

	}

	private static final class ViewerExit extends InterfaceAction
	{
		private final FileViewer Viewer;

		public final void processAction(InterfaceBase Window)
		{

		}

		protected final ViewerExit getAction()
		{
			return new ViewerExit(Viewer);
		}

		private ViewerExit(FileViewer Window)
		{
			super(105);
			Viewer = Window;
		}
	}
	private static final class ViewerCon extends InterfaceAction
	{
		private final FileViewer Viewer;

		public final void processAction(InterfaceBase Window)
		{

		}

		protected final ViewerCon getAction()
		{
			return new ViewerCon(Viewer);
		}

		private ViewerCon(FileViewer Window)
		{
			super(104);
			Viewer = Window;
		}
	}
	private static final class ViewerGo extends InterfaceAction
	{
		private final FileViewer Viewer;

		public final void processAction(InterfaceBase Window)
		{

		}

		protected final ViewerGo getAction()
		{
			return new ViewerGo(Viewer);
		}

		private ViewerGo(FileViewer Window)
		{
			super(103);
			Viewer = Window;
		}
	}
	private static final class ViewerUp extends InterfaceAction
	{
		private final FileViewer Viewer;

		public final void processAction(InterfaceBase Window)
		{

		}

		protected final ViewerUp getAction()
		{
			return new ViewerUp(Viewer);
		}

		private ViewerUp(FileViewer Window)
		{
			super(102);
			Viewer = Window;
		}
	}
	private static final class ViewerBack extends InterfaceAction
	{
		private final FileViewer Viewer;

		public final void processAction(InterfaceBase Window)
		{

		}

		protected final ViewerBack getAction()
		{
			return new ViewerBack(Viewer);
		}

		private ViewerBack(FileViewer Window)
		{
			super(101);
			Viewer = Window;
		}
	}
	private static final class ViewerFoward extends InterfaceAction
	{
		private final FileViewer Viewer;

		public final void processAction(InterfaceBase Window)
		{

		}

		protected final ViewerFoward getAction()
		{
			return new ViewerFoward(Viewer);
		}

		private ViewerFoward(FileViewer Window)
		{
			super(100);
			Viewer = Window;
		}
	}
	private static final class ViewerWindow extends ITempleteBase
	{
		public final void paintWindow(InterfaceBase Window)
		{
			Window.setWindowBackground(java.awt.Color.WHITE);
			Window.setGridLayout(20, 20, 10, 10);
		}
	}
	private static final class ViewerControls extends ITempleteBase
	{
		private final FileViewer Viewer;

		public final void paintWindow(InterfaceBase Window)
		{
			Window.setFlowLayout("Left");
			Window.setResizable(false);
			Window.setStopOnClose();
			Window.setWindowChild(Viewer.fileWindow);
			((Interface)Window).addButton("c_Back", "<<", 60, 20);
			((Interface)Window).addButton("c_Forward", ">>", 60, 20);
			Window.setEnabled("c_Back", false);
			Window.setEnabled("c_Forward", false);
			((Interface)Window).addTextbox("c_Client", "LOCAL", 140, 20);
			((Interface)Window).addButton("c_Conn", "C/D", 60, 20);
			((Interface)Window).addButton("c_Sel", "Select", 80, 20);
			((Interface)Window).addButton("c_Run", "Run", 60, 20);
			((Interface)Window).addButton("c_Up", "UP", 60, 20);
			((Interface)Window).addTextbox("c_Address", "", 355, 20);
			((Interface)Window).addButton("c_Go", "Go", 60, 20);
		}

		private ViewerControls(FileViewer Window)
		{
			Viewer = Window;
		}
	}
}