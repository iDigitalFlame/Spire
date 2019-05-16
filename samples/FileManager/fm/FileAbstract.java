package com.derp.fm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netcom.ai.AIRule;
import org.netcom.net.QeuePacket;
import org.netcom.ui.ITempleteBase;
import org.netcom.ui.Interface;
import org.netcom.ui.InterfaceAction;
import org.netcom.ui.InterfaceBase;

public final class FileAbstract
{
	private FileAbstract() { }

	static final class ManagerView extends AIRule
	{
		private final FileManager Instance;

		protected ManagerView(FileManager Manager)
		{
			Instance = Manager;
		}

		protected final void runRule(QeuePacket RecPacket)
		{
			if(RecPacket.isPacket() && RecPacket.getPacketID() == 75)
				Instance.navagateNetwork((FileRequest)RecPacket.packetData);
		}
	}
	static final class ManagerTemplete extends ITempleteBase
	{
		private final FileManager Instance;

		public final void paintWindow(InterfaceBase Window)
		{
			doCreateWindow((Interface)Window);
		}

		protected ManagerTemplete(FileManager Manager)
		{
			Instance = Manager;
		}

		private final void doCreateWindow(Interface Window)
		{
			Window.setStopOnClose();
			Window.setResizable(false);
			Window.setFlowLayout("Left");
			Window.addButton("w_Back", "<< Back", 80, 25);
			Window.addAction("w_Back", new ManagerControlsBack(Instance));
			Window.setEnabled("w_Back", false);
			Window.addButton("w_Forward", "Forw >>", 80, 25);
			Window.addAction("w_Forward", new ManagerControlsForward(Instance));
			Window.setEnabled("w_Forward", false);
			Window.addButton("w_Up", "^Up", 60, 25);
			Window.addAction("w_Up", new ManagerControlsUp(Instance));
			Window.setEnabled("w_Up", false);
			Window.addTextbox("w_Address", "", 435, 25);
			Window.addButton("w_Go", "Go", 60, 25);
			Window.addAction("w_Go", new ManagerControlsGo(Instance));
			Window.addComboBox("w_Sel", FileManager.FILES_SELECT_MODE_N, 75, 25);
			Window.addButton("w_DoSel", "", 15, 25);
			Window.addAction("w_DoSel", new ManagerControlsSelect(Instance));
			Instance.manager_FileCanvas = Window.addCanvas("w_Files", 835, 603);
			Window.setBackground("w_Files", java.awt.Color.white);
			Instance.manager_FileCanvas.setWrappedLayout("Left", 15, 15);
			Window.addTextbox("w_Connect", "[DISCONECTED]", 400, 25);
			Window.addButton("w_GoConnect", "Connect", 90, 25);
			Window.addAction("w_GoConnect", new ManagerControlsConnect(Instance));
			Window.addLabel("w_Status", "", 300, 25);
		}
	}
	public static final class ManagerControlsGo extends InterfaceAction
	{
		private final FileManager Instance;

		public ManagerControlsGo()
		{
			super(100);
			Instance = null;
		}

		public void processAction(InterfaceBase Window)
		{
			Instance.action_Go();
		}

		protected ManagerControlsGo(FileManager Manager)
		{
			super(100);
			Instance = Manager;
		}

		protected ManagerControlsGo getAction()
		{
			return new ManagerControlsGo(Instance);
		}
	}
	public static final class ManagerControlsUp extends InterfaceAction
	{
		private final FileManager Instance;

		public ManagerControlsUp()
		{
			super(101);
			Instance = null;
		}

		public void processAction(InterfaceBase Window)
		{
			Instance.action_Up();
		}

		protected ManagerControlsUp(FileManager Manager)
		{
			super(101);
			Instance = Manager;
		}

		protected ManagerControlsUp getAction()
		{
			return new ManagerControlsUp(Instance);
		}
	}
	public static final class ManagerControlsBack extends InterfaceAction
	{
		private final FileManager Instance;

		public ManagerControlsBack()
		{
			super(102);
			Instance = null;
		}

		public void processAction(InterfaceBase Window)
		{
			Instance.action_Back();
		}

		protected ManagerControlsBack(FileManager Manager)
		{
			super(102);
			Instance = Manager;
		}

		protected ManagerControlsBack getAction()
		{
			return new ManagerControlsBack(Instance);
		}
	}
	public static final class ManagerControlsForward extends InterfaceAction
	{
		private final FileManager Instance;

		public ManagerControlsForward()
		{
			super(103);
			Instance = null;
		}

		public void processAction(InterfaceBase Window)
		{
			Instance.action_Forward();
		}

		protected ManagerControlsForward(FileManager Manager)
		{
			super(103);
			Instance = Manager;
		}

		protected ManagerControlsForward getAction()
		{
			return new ManagerControlsForward(Instance);
		}
	}
	public static final class ManagerControlsConnect extends InterfaceAction
	{
		private final FileManager Instance;

		public ManagerControlsConnect()
		{
			super(104);
			Instance = null;
		}

		public void processAction(InterfaceBase Window)
		{
			Instance.action_Connect();
		}

		protected ManagerControlsConnect(FileManager Manager)
		{
			super(104);
			Instance = Manager;
		}

		protected ManagerControlsConnect getAction()
		{
			return new ManagerControlsConnect(Instance);
		}
	}
	public static final class ManagerControlsSelect extends InterfaceAction
	{
		private final FileManager Instance;

		public ManagerControlsSelect()
		{
			super(105);
			Instance = null;
		}

		public void processAction(InterfaceBase Window)
		{
			Instance.action_Select();
		}

		protected ManagerControlsSelect(FileManager Manager)
		{
			super(105);
			Instance = Manager;
		}

		protected ManagerControlsSelect getAction()
		{
			return new ManagerControlsSelect(Instance);
		}
	}
	static final class ManagerFile implements ActionListener
	{
		protected final int fileID;
		protected final FileManager Instance;

		public final void actionPerformed(ActionEvent Event)
		{
			Instance.selectFile(fileID);
		}

		protected ManagerFile(FileManager Manager, int ID)
		{
			fileID = ID;
			Instance = Manager;
		}
	}
}