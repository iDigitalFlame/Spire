package com.derp.fm;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JButton;
import org.netcom.ai.Subroutine;
import org.netcom.io.Item;
import org.netcom.io.Stream;
import org.netcom.net.Tunnel;
import org.netcom.types.Computer;
import org.netcom.ui.Interface;
import org.netcom.ui.InterfaceCanvas;
import org.netcom.ui.InterfaceDialog;
import org.netcom.ui.objects.InterfaceIconButton;
import org.netcom.win.Program;

public final class FileManager
{
	private static final int PORT_NUMBER = 4735;
	private static final String FILE_ICON = "file_ico.jpg";
	private static final String FOLDER_ICON = "folder_ico.jpg";
	private static final String FOLDER_OVER_ICON = "folder_over_ico.jpg";
	private static final Program FILES_OPEN_PROC = new Program("cmd");

	protected static final String[] FILES_SELECT_MODE_N = { "Refresh", "New Folder" };
	protected static final String[] FILES_SELECT_MODE_NP = { "Refresh", "Paste", "New Folder" };
	protected static final String[] FILES_SELECT_MODE_FS = { "Refresh", "De-Select", "Copy", "Cut", "Delete", "Rename" };
	protected static final String[] FILES_SELECT_MODE_FSP = { "Refresh", "De-Select", "Copy", "Cut", "Paste", "Rename", "Delete" };

	protected byte manager_FileSelectMode;
	protected int manager_SelectedIndex;
	protected InterfaceCanvas manager_FileCanvas;

	private final String[] manager_Back;
	private final String[] manager_Forward;
	private final Interface manager_MainWindow;
	private final Vector<FileRecord> manager_FilesCache;

	private boolean manager_isWait;
	private boolean manager_isCopy;
	private byte manager_NavIndex;
	private byte manager_BackIndex;
	private byte manager_ForwardIndex;
	private String manager_ClipboardPath;
	private String manager_CurrentDirectory;
	private Tunnel manager_NetworkInterface;
	private Computer manager_ConnectedServer;

	public static final void main(String[] A) { new FileManager(); }

	public FileManager()
	{
		this(null);
	}
	public FileManager(Computer ConnectTo)
	{
		manager_Back = new String[20];
		manager_Forward = new String[20];
		manager_FilesCache = new Vector<FileRecord>();
		manager_MainWindow = new Interface("File Manager", 850, 700);
		manager_BackIndex = -1;
		manager_ForwardIndex = -1;
		manager_SelectedIndex = -1;
		if(ConnectTo != null)
		{
			manager_ConnectedServer = ConnectTo;
			manager_NetworkInterface = new Tunnel(PORT_NUMBER);
			Subroutine.getSubroutine().addPacketRule(80, new FileAbstract.ManagerView(this));
		}
		manager_MainWindow.setTemplete(new FileAbstract.ManagerTemplete(this));
		navagateTo(null, -1);
		manager_MainWindow.showWindow();
	}

	protected final void action_Go()
	{
		String a = manager_MainWindow.getText("w_Address");
		if(a == null || a.isEmpty()) navagateTo(null, 0);
		else navagateTo(a, 0);
	}
	protected final void action_Up()
	{
		navagateTo(Stream.FileParent(manager_CurrentDirectory), 0);
	}
	protected final void action_Back()
	{
		if(manager_BackIndex > -1) navagateTo(manager_Back[manager_BackIndex], 1);
	}
	protected final void action_Forward()
	{
		if(manager_ForwardIndex > -1) navagateTo(manager_Forward[manager_ForwardIndex], 2);
	}
	protected final void action_Connect()
	{
		if(manager_ConnectedServer == null)
		{
			String a = manager_MainWindow.getText("w_Connect");
			if(a == null || a.isEmpty())
			{
				InterfaceDialog.error(manager_MainWindow, "Connect Failure", "Cannot connect to an empty sevrer!");
				return;
			}
			Computer b = Computer.getByName(a);
			if(b == null)
			{
				InterfaceDialog.error(manager_MainWindow, "Connect Failure", "Server \"" + a + "\" does not exist or cannot be reached!");
				return;
			}
			manager_BackIndex = -1;
			manager_ForwardIndex = -1;
			manager_SelectedIndex = -1;
			manager_ConnectedServer = b;
			clearFilesListing();
			manager_MainWindow.setText("w_GoConnect", "Discon");
			if(manager_NetworkInterface == null) manager_NetworkInterface = new Tunnel(PORT_NUMBER);
			navagateTo(null, -1);
		}
		else
		{
			manager_ConnectedServer = null;
			manager_BackIndex = -1;
			manager_ForwardIndex = -1;
			manager_SelectedIndex = -1;
			manager_MainWindow.setText("w_GoConnect", "Connect");
			manager_MainWindow.setText("w_Connect", "[DISCONECTED]");
			navagateTo(null, -1);
		}
	}
	protected final void action_Select()
	{
		String a = manager_MainWindow.getText("w_Sel");
		System.out.println(a);
		if("Refresh".equals(a)) navagateTo(manager_CurrentDirectory, -1);
		if("New Folder".equals(a))
		{
			if(manager_CurrentDirectory == null)
			{
				InterfaceDialog.error(manager_MainWindow, "Directory Failure", "A new folder cannot be placed here. Please try a different location");
				return;
			}
			String b = InterfaceDialog.question_input(manager_MainWindow, "New Folder Name", "New Folder");
			if(b == null || b.isEmpty() ||
					(b.indexOf('/') >= 0 || b.indexOf('\\') >= 0 || b.indexOf('<') >= 0 || b.indexOf(':') >= 0 ||
					 b.indexOf('>') >= 0 || b.indexOf('|') >= 0 || b.indexOf('*') >= 0 || b.indexOf('?') >= 0 || b.indexOf('"') >= 0))
			{
				InterfaceDialog.error(manager_MainWindow, "Invalid", "The folder name \"" + b + "\" is not valid!");
				return;
			}
			if(manager_ConnectedServer != null)
			{
				manager_NetworkInterface.sendItem(manager_ConnectedServer, new FileRequest(8, Stream.PathRegular(manager_CurrentDirectory) + b));
				manager_MainWindow.setText("w_Status", "Requesting \"" + manager_ConnectedServer.getName() + "\" to create a new folder");
			}
			else
			{
				if(!new File(Stream.PathRegular(manager_CurrentDirectory) + b).mkdirs())
					InterfaceDialog.error(manager_MainWindow, "Folder Creation", "The folder \"" + b + "\" could not be created!");
				else navagateTo(manager_CurrentDirectory, -1);
			}
		}
		if("Upload".equals(a) && manager_ConnectedServer != null)
		{
			byte c = InterfaceDialog.question_YNC(manager_MainWindow, "Conform Upload", "Would you like to upload a file (Yes) or a folder (No)?");
			if(c == 2) return;
			manager_NetworkInterface.sendItem(manager_ConnectedServer, c == 1 ?
							 				  new FileRequest(4, InterfaceDialog.open_dir(manager_MainWindow, "Upload Dir")) :
							 			      new FileRequest(5, InterfaceDialog.open_file(manager_MainWindow, "Upload File")));
			manager_MainWindow.setText("w_Status", "Requesting \"" + manager_ConnectedServer.getName() + "\" to upload a " + (c == 1 ? "folder" : "file"));
		}
		if("De-Select".equals(a) && manager_SelectedIndex != -1)
		{
			manager_MainWindow.setBackground(String.valueOf(manager_SelectedIndex), java.awt.Color.white);
			manager_MainWindow.setForeground(String.valueOf(manager_SelectedIndex), java.awt.Color.black);
			manager_SelectedIndex = -1;
			manager_MainWindow.comboBox_SetElements("w_Sel", manager_ClipboardPath == null ? FILES_SELECT_MODE_N : FILES_SELECT_MODE_NP);
		}
		if("Copy".equals(a))
		{
			manager_ClipboardPath = manager_FilesCache.get(manager_SelectedIndex).file_Path;
			manager_isCopy = true;
			manager_MainWindow.comboBox_SetElements("w_Sel", FILES_SELECT_MODE_FSP);
		}
		if("Cut".equals(a))
		{
			manager_ClipboardPath = manager_FilesCache.get(manager_SelectedIndex).file_Path;
			manager_isCopy = false;
			manager_MainWindow.comboBox_SetElements("w_Sel", FILES_SELECT_MODE_FSP);
			manager_MainWindow.setBackground(String.valueOf(manager_SelectedIndex), java.awt.Color.cyan);
			manager_MainWindow.setForeground(String.valueOf(manager_SelectedIndex), java.awt.Color.black);
		}
		if("Paste".equals(a) && manager_ClipboardPath != null)
		{
			if(manager_isCopy)
			{
				if(manager_ConnectedServer != null)
					manager_NetworkInterface.sendItem(manager_ConnectedServer, new FileRequest(10, manager_ClipboardPath, manager_CurrentDirectory));
				else
				{
					manager_MainWindow.setText("w_Status", "Copying file(s), please wait");
					boolean xx = InterfaceDialog.question_YN(manager_MainWindow, "Replace File", "Replace file \"" +
		   					   Stream.FileName(manager_ClipboardPath) + "\"?");
					System.out.println(xx);
					if(!Stream.FileCopyTo(manager_ClipboardPath, Stream.PathRegular(manager_CurrentDirectory) +
														   Stream.FileName(manager_ClipboardPath),
														   Stream.FileExists(Stream.PathRegular(manager_CurrentDirectory) +
																   			 Stream.FileName(manager_ClipboardPath)) ?
														   xx : true))
					{
						InterfaceDialog.error(manager_MainWindow, "Copy Error", "\"" + Stream.FileName(manager_ClipboardPath) + "\" could not be copied to \"" +
											  manager_CurrentDirectory + "\"!");
					}
					else navagateTo(manager_CurrentDirectory, -1);
				}
			}
			else
			{
				if(manager_ConnectedServer != null)
					manager_NetworkInterface.sendItem(manager_ConnectedServer, new FileRequest(9, manager_ClipboardPath, manager_CurrentDirectory));
				else
				{
					manager_MainWindow.setText("w_Status", "Moving file(s), please wait");
					if(!Stream.FileMoveTo(manager_ClipboardPath, Stream.PathRegular(manager_CurrentDirectory) +
														   Stream.FileName(manager_ClipboardPath),
														   Stream.FileExists(Stream.PathRegular(manager_CurrentDirectory) +
																   			 Stream.FileName(manager_ClipboardPath)) ?
														   InterfaceDialog.question_YN(manager_MainWindow, "Replace File", "Replace file \"" +
																   					   Stream.FileName(manager_ClipboardPath) + "\"?") : true))
					{
						InterfaceDialog.error(manager_MainWindow, "Move Error", "\"" + Stream.FileName(manager_ClipboardPath) + "\" could not be moved to \"" +
											  manager_CurrentDirectory + "\"!");
					}
					else
					{
						manager_ClipboardPath = null;
						navagateTo(manager_CurrentDirectory, -1);
					}
				}
			}
		}
		if("Delete".equals(a))
		{
			if(manager_ConnectedServer != null)
				manager_NetworkInterface.sendItem(manager_ConnectedServer, new FileRequest(11, manager_FilesCache.get(manager_SelectedIndex).file_Path));
			else
			{
				if(!Stream.FileDelete(manager_FilesCache.get(manager_SelectedIndex).file_Path))
				{
					InterfaceDialog.error(manager_MainWindow, "Delete Error", "Cannot delete \"" + manager_FilesCache.get(manager_SelectedIndex).file_Name +
										  "\"!");
				}
				else navagateTo(manager_CurrentDirectory, -1);
			}
		}
		if("Rename".equals(a))
		{
			String d = InterfaceDialog.question_input(manager_MainWindow, "New File Name", manager_FilesCache.get(manager_SelectedIndex).file_Name);
			if(d == null || d.isEmpty() ||
					(d.indexOf('/') >= 0 || d.indexOf('\\') >= 0 || d.indexOf('<') >= 0 || d.indexOf(':') >= 0 ||
					 d.indexOf('>') >= 0 || d.indexOf('|') >= 0 || d.indexOf('*') >= 0 || d.indexOf('?') >= 0 || d.indexOf('"') >= 0))
			{
				InterfaceDialog.error(manager_MainWindow, "Invalid", "The file name \"" + d + "\" is not valid!");
				return;
			}
			if(manager_ConnectedServer != null)
				manager_NetworkInterface.sendItem(manager_ConnectedServer, new FileRequest(12, d, manager_FilesCache.get(manager_SelectedIndex).file_Path));
			else
			{
				if(!Stream.FileRenameTo(manager_FilesCache.get(manager_SelectedIndex).file_Path, d))
				{
					InterfaceDialog.error(manager_MainWindow, "Rename Error", "Cannot rename \"" + manager_FilesCache.get(manager_SelectedIndex).file_Name +
							  "\" to \"" + d + "\"!");
				}
				else navagateTo(manager_CurrentDirectory, -1);
			}
		}
	}
	protected final void selectFile(int FileIndex)
	{
		if(manager_SelectedIndex == FileIndex)
		{
			triggerFile();
			manager_MainWindow.setBackground(String.valueOf(manager_SelectedIndex), java.awt.Color.white);
			manager_MainWindow.setForeground(String.valueOf(manager_SelectedIndex), java.awt.Color.black);
			if(manager_ClipboardPath != null && manager_SelectedIndex != -1 && manager_ClipboardPath.equals(manager_FilesCache.get(manager_SelectedIndex)))
				manager_MainWindow.setBackground(String.valueOf(manager_SelectedIndex), java.awt.Color.cyan);
			manager_SelectedIndex = -1;
			manager_MainWindow.comboBox_SetElements("w_Sel", manager_ClipboardPath == null ? FILES_SELECT_MODE_N : FILES_SELECT_MODE_NP);
			if(manager_ConnectedServer != null) manager_MainWindow.comboBox_AddElement("w_Sel", 1, "Upload");
			return;
		}
		if(manager_SelectedIndex != -1)
		{
			manager_MainWindow.setBackground(String.valueOf(manager_SelectedIndex), java.awt.Color.white);
			manager_MainWindow.setForeground(String.valueOf(manager_SelectedIndex), java.awt.Color.black);
			if(manager_ClipboardPath != null && manager_SelectedIndex != -1 && manager_ClipboardPath.equals(manager_FilesCache.get(manager_SelectedIndex)))
				manager_MainWindow.setBackground(String.valueOf(manager_SelectedIndex), java.awt.Color.cyan);
		}
		else
		{
			manager_MainWindow.comboBox_SetElements("w_Sel", manager_ClipboardPath == null ? FILES_SELECT_MODE_FS : FILES_SELECT_MODE_FSP);
			if(manager_ConnectedServer != null) manager_MainWindow.comboBox_AddElement("w_Sel", 1, "Upload");
		}
		manager_MainWindow.setBackground(String.valueOf(FileIndex), java.awt.Color.blue);
		manager_MainWindow.setForeground(String.valueOf(FileIndex), java.awt.Color.white);
		if(!manager_FilesCache.get(FileIndex).file_Dir)
		{
			double a = manager_FilesCache.get(FileIndex).file_Size;
			byte b = 0;
			for(; a > 1024; b++) a /= 1024D;
			manager_MainWindow.setText("w_Status", manager_FilesCache.get(FileIndex).file_Name + " " +
									   (Math.round(a * 100) / 100D) +
									   (b == 0 ? " B" : (b == 1 ? " KB" : (b == 2 ? " MB" : (b == 3 ? " GB" : "TB")))));
		}
		else manager_MainWindow.setText("w_Status", manager_FilesCache.get(FileIndex).file_Name + " File Folder");
		manager_SelectedIndex = FileIndex;
	}

	protected final void navagateNetwork(FileRequest Request)
	{
		if(Request.request_Type == -1)
		{
			InterfaceDialog.error(manager_MainWindow, "Network Directory Error", "There was a problem getting a listing from \"" +
								  Request.request_Directory + "\" on \"" + Request.getSender().getName() + "\"!");
		}
		if(Request.request_Type == -2)
		{
			InterfaceDialog.error(manager_MainWindow, "Non-Existant Path", "The path \"" + Request.request_Directory + "\" does not exist!");
		}
		else
		{
			Request.getAllRecords(manager_FilesCache);
			manager_CurrentDirectory = Request.request_Directory;
			manager_isWait = false;
			triggerNav(manager_CurrentDirectory, manager_NavIndex);
			clearFilesListing();
			completeNavigation();
		}
	}

	private final void triggerFile()
	{
		if(manager_FilesCache.get(manager_SelectedIndex).file_Dir)
			navagateTo(manager_FilesCache.get(manager_SelectedIndex).file_Path, 0);
		else openFile(manager_FilesCache.get(manager_SelectedIndex).file_Path);
	}
	private final void clearFilesListing()
	{
		manager_MainWindow.setVisibility("w_Files", false);
		for(int a = 0; a < manager_FilesCache.size(); a++) manager_MainWindow.removeElement(String.valueOf(a));
		manager_FilesCache.clear();
		manager_MainWindow.setVisibility("w_Files", true);
	}
	private final void completeNavigation()
	{
		int a = 0, b = 0, c = 0;
		for(; c < manager_FilesCache.size(); c++)
		{
			if(manager_FilesCache.get(c).file_Dir)
			{
				manager_FileCanvas.addIconButton(String.valueOf(c), manager_FilesCache.get(c).file_Name, FOLDER_ICON, 90, 90);
				((InterfaceIconButton)manager_MainWindow.getElement(String.valueOf(c))).setOverIcon(FOLDER_OVER_ICON);
				((JButton)manager_MainWindow.getElement(String.valueOf(c)).getItemComponent()).addActionListener(new FileAbstract.ManagerFile(this, c));
				a++;
			}
			else
			{
				manager_FileCanvas.addIconButton(String.valueOf(c), manager_FilesCache.get(c).file_Name, FILE_ICON, 90, 90);
				((JButton)manager_MainWindow.getElement(String.valueOf(c)).getItemComponent()).addActionListener(new FileAbstract.ManagerFile(this, c));
				b++;
			}
			manager_MainWindow.setBackground(String.valueOf(c), java.awt.Color.white);
			if(manager_ClipboardPath != null && !manager_isCopy && manager_ClipboardPath.equals(manager_FilesCache.get(c).file_Path))
			{
				manager_MainWindow.setBackground(String.valueOf(c), java.awt.Color.cyan);
				manager_MainWindow.setForeground(String.valueOf(c), java.awt.Color.black);
			}
		}
		if(manager_CurrentDirectory == null)
		{
			manager_MainWindow.setEnabled("w_Sel", false);
			manager_MainWindow.setEnabled("w_DoSel", false);
		}
		else
		{
			manager_MainWindow.setEnabled("w_Sel", true);
			manager_MainWindow.setEnabled("w_DoSel", true);
		}
		manager_SelectedIndex = -1;
		manager_MainWindow.comboBox_SetElements("w_Sel", manager_ClipboardPath == null ? FILES_SELECT_MODE_N : FILES_SELECT_MODE_NP);
		if(manager_ConnectedServer != null) manager_MainWindow.comboBox_AddElement("w_Sel", 1, "Upload");
		manager_MainWindow.setText("w_Status", "Done. Viewing " + b + " Files and " + a + " Folders, " + c + " Total");
		manager_MainWindow.setText("w_Address", manager_CurrentDirectory != null ? manager_CurrentDirectory :
								   (manager_ConnectedServer != null ? manager_ConnectedServer.getName() : "Local Computer"));
	}
	private final void openFile(String FilePath)
	{
		if(manager_ConnectedServer != null)
		{

		}
		else try
		{
			FILES_OPEN_PROC.setParamaters("/c", "start", "\"\"", FilePath);
			FILES_OPEN_PROC.start();
		}
		catch (IOException E)
		{
			InterfaceDialog.error(manager_MainWindow, "Cant open file", "There was a problem opening file \"" +
								  FilePath + "\"!");
		}
	}
	private final void navagateTo(String NavagatePath, int NavType)
	{
		System.out.println("Nav \n\tDir: " + NavagatePath + " \n\tType: " + NavType + "\n\tFBS: " +
						   manager_ForwardIndex + ", " + manager_BackIndex + ", " + manager_SelectedIndex);
		if("Local Computer".equals(NavagatePath) ||
				(manager_ConnectedServer != null && manager_ConnectedServer.getName().equals(NavagatePath)))
			NavagatePath = null;
		if(manager_ConnectedServer != null)
		{
			if(manager_isWait) return;
			manager_MainWindow.setText("w_Status", "Requesting listing from \"" + manager_ConnectedServer.getName() + "\"");
			manager_NetworkInterface.sendItem(new FileRequest(NavagatePath), manager_ConnectedServer);
			manager_NavIndex = (byte)NavType;
			manager_isWait = true;
		}
		else
		{
			if(NavagatePath != null && !Stream.FileExists(NavagatePath))
			{
				InterfaceDialog.error(manager_MainWindow, "Non-Existant Path", "The path \"" + NavagatePath + "\" does not exist!");
				return;
			}
			if(NavagatePath != null && Stream.isFile(NavagatePath))
			{
				openFile(NavagatePath);
				return;
			}
			manager_MainWindow.setText("w_Status", "Requesting file listing.....");
			String[] a = NavagatePath != null ? Stream.getList(NavagatePath) : Stream.getDrivesPath();
			if(a == null)
			{
				InterfaceDialog.error(manager_MainWindow, "Cannot Navigate", "Cannot navigate to \"" + NavagatePath +
						              "\" You might not have premission to access this directory!");
				manager_MainWindow.setText("w_Status", "Failed to open \"" + Stream.getFileName(NavagatePath) + "\"");
				return;
			}
			triggerNav(NavagatePath, NavType);
			clearFilesListing();
			manager_CurrentDirectory = NavagatePath;
			for(int b = 0; b < a.length; b++)
				manager_FilesCache.add(new FileRecord(NavagatePath != null ? (Stream.ensurePath(NavagatePath) + a[b]) : a[b]));
			completeNavigation();
		}
	}
	private final void triggerNav(String NavagatePath, int NavType)
	{
		if(NavType == 0)
		{
			if(manager_BackIndex == -1) manager_MainWindow.setEnabled("w_Back", true);
			if(manager_BackIndex == 19) manager_BackIndex = -1;
			manager_Back[++manager_BackIndex] = manager_CurrentDirectory;
		}
		if(NavType == 1)
		{
			if(manager_BackIndex >= 0) manager_BackIndex--;
			if(manager_BackIndex == -1) manager_MainWindow.setEnabled("w_Back", false);
			if(manager_ForwardIndex == -1) manager_MainWindow.setEnabled("w_Forward", true);
			if(manager_ForwardIndex == 19) manager_ForwardIndex = -1;
			manager_Forward[++manager_ForwardIndex] = manager_CurrentDirectory;
		}
		if(NavType == 2)
		{
			if(manager_ForwardIndex >= 0) manager_ForwardIndex--;
			if(manager_ForwardIndex == -1) manager_MainWindow.setEnabled("w_Forward", false);
			if(manager_BackIndex == -1) manager_MainWindow.setEnabled("w_Back", true);
			if(manager_BackIndex == 19) manager_BackIndex = -1;
			manager_Back[++manager_BackIndex] = manager_CurrentDirectory;
		}
		if(NavagatePath != null) manager_MainWindow.setEnabled("w_Up", true);
		else manager_MainWindow.setEnabled("w_Up", false);
	}

	static
	{
		Item.addItemMapping(80, FileRequest.class);
	}
}