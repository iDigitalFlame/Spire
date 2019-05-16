package com.spire.util;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.JFrame;
import java.util.ArrayList;
import javax.swing.JButton;
import com.spire.log.Report;
import com.spire.log.Reporter;

import javax.swing.JTextArea;
import com.spire.sec.Security;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import com.spire.ex.NullException;
import com.spire.ui.InterfaceBase;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import com.spire.log.ReporterHandle;
import com.spire.ex.NumberException;
import java.awt.event.ActionListener;
import javax.swing.text.DefaultCaret;
import javax.swing.ScrollPaneConstants;
import com.spire.ex.PermissionException;

public final class Console extends InterfaceBase implements ReporterHandle
{
	private static final short CONSOLE_DEF_WIDTH = 750;
	private static final short CONSOLE_DEF_HEIGHT = 500;
	private static final String CONSOLE_NAME = "Console";
	
	private final JTextArea consoleOutput;
	private final ConsoleActionInset consoleAction; 
	private final ArrayList<ConsoleAction> consoleListeners;
	
	private byte consoleLevel;
	private boolean consoleEcho;
	private JButton consoleSubmit;
	private JTextField consoleInput;
	
	public Console()
	{
		this(null, -1, -1, 0, 0, null, null, null);
		interfaceWindow.setLocationRelativeTo(null);
	}
	public Console(String ConsoleTitle)
	{
		this(ConsoleTitle, -1, -1, 0, 0, null, null, null);
		interfaceWindow.setLocationRelativeTo(null);
	}
	public Console(String ConsoleTitle, int ConsoleWidth, int ConsoleHeight)
	{
		this(ConsoleTitle, ConsoleWidth, ConsoleHeight, 0, 0, null, null, null);
		interfaceWindow.setLocationRelativeTo(null);
	}
	public Console(String ConsoleTitle, int ConsoleWidth, int ConsoleHeight, int ConsolePosX, int ConsolePosY)
	{
		this(ConsoleTitle, ConsoleWidth, ConsoleHeight, ConsolePosX, ConsolePosY, null, null, null);
	}
	public Console(String ConsoleTitle, int ConsoleWidth, int ConsoleHeight, int ConsolePosX, int ConsolePosY, Color ConsoleTextColor)
	{
		this(ConsoleTitle, ConsoleWidth, ConsoleHeight, ConsolePosX, ConsolePosY, ConsoleTextColor, null, null);
	}
	public Console(String ConsoleTitle, int ConsoleWidth, int ConsoleHeight, int ConsolePosX, int ConsolePosY, Color ConsoleTextColor, Color ConsoleTextBackground)
	{
		this(ConsoleTitle, ConsoleWidth, ConsoleHeight, ConsolePosX, ConsolePosY, ConsoleTextColor, ConsoleTextBackground, null);
	}
	public Console(String ConsoleTitle, int ConsoleWidth, int ConsoleHeight, int ConsolePosX, int ConsolePosY, Color ConsoleTextColor, Color ConsoleTextBackground, Color ConsoleWindowBackground)
	{
		super(new JFrame(ConsoleTitle != null ? ConsoleTitle : CONSOLE_NAME));
		consoleOutput = new JTextArea();
		consoleAction = new ConsoleActionInset(this);
		consoleListeners = new ArrayList<ConsoleAction>();
		if(ConsoleWindowBackground != null) interfaceWindow.setBackground(ConsoleWindowBackground);
		interfaceWindow.setLocation(Monitor.getScreenAbsWidth(0, ConsolePosX), Monitor.getScreenAbsHeight(0, ConsolePosY));
		interfaceWindow.setSize(ConsoleWidth != -1 ? ConsoleWidth : CONSOLE_DEF_WIDTH, ConsoleHeight != -1 ? ConsoleHeight : CONSOLE_DEF_HEIGHT);
		if(ConsoleWindowBackground != null) ((JFrame)interfaceWindow).getContentPane().setBackground(ConsoleWindowBackground);
		if(ConsoleTextBackground != null) consoleOutput.setBackground(ConsoleTextBackground);
		if(ConsoleTextColor != null) consoleOutput.setForeground(ConsoleTextColor);
		((DefaultCaret)consoleOutput.getCaret()).setUpdatePolicy(2);
		consoleOutput.setWrapStyleWord(true);
		consoleOutput.setAutoscrolls(true);
		consoleOutput.setEditable(false);
		consoleOutput.setLineWrap(true);
		createWindow(this);
	}
	
	public final void println()
	{
		consoleOutput.append(System.lineSeparator());
	}
	public final void print(int ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(byte ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(char ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(long ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void println(int ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void print(float ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(short ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(double ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(Object ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void print(String ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void println(byte ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(char ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(long ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void print(boolean ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput));
	}
	public final void println(float ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(short ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(double ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(Object ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(String ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void println(boolean ConsoleOutput)
	{
		consoleOutput.append(String.valueOf(ConsoleOutput) + System.lineSeparator());
	}
	public final void processReport(Report ReportData)
	{
		try
		{	
			consoleOutput.append(ReportData.toString() + System.lineSeparator());
		}
		catch (Throwable Exception)
		{
			Reporter.reportUncaught(Exception);
		}
	}
	public final void setConsoleHasInput(boolean HasInput)
	{
		if(consoleInput != null && !HasInput)
		{
			interfaceWindow.remove(consoleInput);
			interfaceWindow.remove(consoleSubmit);
			consoleInput = null;
			consoleSubmit = null;
			interfaceWindow.validate();
		}
		else if(consoleInput == null && HasInput)
		{
			consoleInput = new JTextField();
			interfaceWindow.invalidate();
			createWindow(this);
			interfaceWindow.validate();
		}
	}
	public final void setConsoleInputEcho(boolean InputEcho)
	{
		consoleEcho = InputEcho;
	}
	public final void resetWindowSize() throws PermissionException
	{
		setWindowSize(CONSOLE_DEF_WIDTH, CONSOLE_DEF_HEIGHT);
	}
	public final void clearConsoleText() throws PermissionException
	{
		Security.check("io.con.clear", Thread.currentThread().getName());
		consoleOutput.setText(Constants.EMPTY_STRING);
	}
	public final void setConsoleTextColor(Color TextColor) throws PermissionException
	{
		Security.check("io.con.text", Thread.currentThread().getName());
		consoleOutput.setForeground(TextColor);
		if(consoleInput != null) consoleInput.setForeground(TextColor);
	}
	public final void setConsoleWindowColor(Color WindowColor) throws PermissionException
	{
		Security.check("io.con.window", Thread.currentThread().getName());
		((JFrame)interfaceWindow).getContentPane().setBackground(WindowColor);
	}
	public final void setConsoleBackgroundColor(Color BackgroundColor) throws PermissionException
	{
		Security.check("io.con.back", Thread.currentThread().getName());
		consoleOutput.setBackground(BackgroundColor);
		if(consoleInput != null) consoleInput.setBackground(BackgroundColor);
	}
	public final void addConsoleListener(Reflect Listener) throws NullException, PermissionException
	{
		if(Listener == null) throw new NullException("Listener");
		Security.check("io.con.add-ref", Listener.getReflectClass());
		consoleListeners.add(new ConsoleActionReflect(Listener));
	}
	public final void removeConsoleListener(Reflect Listener) throws NullException, PermissionException
	{
		if(Listener == null) throw new NullException("Listener");
		Security.check("io.con.rem-ref", Listener.getReflectClass());
		consoleListeners.remove(Listener);
	}
	public final void setConsoleReportLevel(byte ReportLevel) throws NumberException, PermissionException
	{
		if(ReportLevel < 0) throw new NumberException("ReportLevel", ReportLevel, false);
		Security.check("io.con.level", Byte.valueOf(ReportLevel));
		consoleLevel = ReportLevel;
	}
	public final void removeConsoleListener(int ListenerIndex) throws NumberException, PermissionException
	{
		if(ListenerIndex < 0) throw new NumberException("ListenerIndex", ListenerIndex, false);
		if(ListenerIndex > consoleListeners.size()) throw new NumberException("ListenerIndex", ListenerIndex, 0, consoleListeners.size());
		ConsoleAction a = consoleListeners.get(ListenerIndex);
		if(a instanceof ConsoleActionReflect) Security.check("io.con.rem", a.actionInstance.getClass());
		else Security.check("io.con.rem-ref", ((ConsoleActionReflect)a).actionInstance.getReflectClass());
		consoleListeners.remove(ListenerIndex);
		a = null;
	}
	public final void addConsoleListener(ConsoleListener Listener) throws NullException, PermissionException
	{
		if(Listener == null) throw new NullException("Listener");
		Security.check("io.con.add", Listener.getClass());
		consoleListeners.add(new ConsoleAction(Listener));
	}
	public final void removeConsoleListener(ConsoleListener Listener) throws NullException, PermissionException
	{
		if(Listener == null) throw new NullException("Listener");
		Security.check("io.con.rem", Listener.getClass());
		consoleListeners.remove(Listener);
	}	
	
	public final boolean isInputAvalible()
	{
		return consoleInput != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Console && ((Console)CompareObject).interfaceID.equals(interfaceWindow);
	}
	public final boolean canProcessReport(byte ReportLevel)
	{
		return ReportLevel >= consoleLevel;
	}
	
	public final byte getConsoleReportLevel()
	{
		return consoleLevel;
	}
	
	public final int hashCode()
	{
		return consoleLevel + interfaceID.intValue() + consoleListeners.hashCode() + consoleOutput.hashCode();
	}
	
	public final String toString()
	{
		return "Console(TE) "  + Long.toHexString(getWindowID());
	}
	public final String getConsoleText()
	{
		return consoleOutput.getText();
	}
	
	public final Console clone()
	{
		return this;
	}
	
	private static final void createWindow(Console Window)
	{
		GridBagConstraints a = new GridBagConstraints();
		if(Window.consoleInput == null)
		{
			JScrollPane b = new JScrollPane(Window.consoleOutput);
			b.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			b.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			Window.interfaceWindow.setLayout(new GridBagLayout());
			a.anchor = 19;
			a.gridx = 0;
			a.gridy = 0;
			a.weightx = 3;
			a.weighty = 3;
			a.gridheight = 1;
			a.gridwidth = 2;
			a.fill = 1;
			a.insets = new Insets(5, 5, 5, 5);
			Window.interfaceWindow.add(b, a);
			clearConstraints(a);
		}
		else
		{
			a.anchor = 20;
			a.gridx = 0;
			a.gridy = 1;
			a.gridwidth = 1;
			a.gridheight = 1;
			a.fill = 1;
			a.weightx = 3;
			a.insets = new Insets(0, 5, 5, 5);
			Window.interfaceWindow.add(Window.consoleInput, a);
			Window.consoleInput.addActionListener(Window.consoleAction);
			clearConstraints(a);
			Window.consoleSubmit = new JButton("Send");
			Window.consoleSubmit.addActionListener(Window.consoleAction);
			a.anchor = 13;
			a.gridx = 1;
			a.gridy = 1;
			a.gridheight = 1;
			a.gridwidth = 1;
			a.fill = 2;
			a.weightx = 0.25;
			a.insets = new Insets(0, 0, 5, 5);
			Window.interfaceWindow.add(Window.consoleSubmit, a);
		}
		a = null;
	}
	private static final void clearConstraints(GridBagConstraints Constraints)
	{
		Constraints.gridx = -1;
		Constraints.gridy = -1;
		Constraints.gridwidth = 1;
		Constraints.gridheight = 1;
		Constraints.weightx = 0;
		Constraints.weighty = 0;
		Constraints.anchor = 10;
		Constraints.fill = 0;
		Constraints.insets = new Insets(0, 0, 0, 0);
		Constraints.ipadx = 0;
		Constraints.ipady = 0;
	}
	
 	private static class ConsoleAction
	{
		private final ConsoleListener actionInstance;
		
		public boolean equals(Object CompareObject)
		{
			return actionInstance.equals(CompareObject);
		}
		
		public int hashCode()
		{
			return actionInstance.hashCode();
		}
		
		protected void triggerAction(String TextInput, Console ConsoleWindow)
		{
			actionInstance.onConsoleSubmit(TextInput, ConsoleWindow);
		}
		
		private ConsoleAction(ConsoleListener Listener)
		{
			actionInstance = Listener;
		}
	}
	private static final class ConsoleEvent extends Thread
	{
		private final String eventText;
		private final Console eventConsole;
		
		public final void run()
		{
			for(int a = 0; a < eventConsole.consoleListeners.size(); a++)
				eventConsole.consoleListeners.get(a).triggerAction(eventText, eventConsole);
		}

		private ConsoleEvent(Console EventHost, String EventText)
		{
			eventText = EventText;
			eventConsole = EventHost;
			setName("ConsoleEventHandler");
			setPriority(1);
		}
	}
 	private static final class ConsoleActionReflect extends ConsoleAction
	{
		private final boolean actionTrue;
		private final Reflect actionInstance;
		
		public final boolean equals(Object CompareObject)
		{
			return actionInstance.equals(CompareObject);
		}
		
		public final int hashCode()
		{
			return actionInstance.hashCode();
		}
		
		protected final void triggerAction(String TextInput, Console ConsoleWindow)
		{
			if(actionTrue) actionInstance.inokeReflect(new Object[] { TextInput, ConsoleWindow });
			else actionInstance.inokeReflect();
		}
		
		private ConsoleActionReflect(Reflect ReflectListener)
		{
			super(null);
			actionInstance = ReflectListener;
			actionTrue = ReflectListener.hasParamaters(String.class, Console.class);
		}
	}
	private static final class ConsoleActionInset implements ActionListener
	{
		private final Console insetConsole;
		
		public final void actionPerformed(ActionEvent ActionEvent)
		{
			if(!insetConsole.isInputAvalible()) return;
			String a = insetConsole.consoleInput.getText();
			insetConsole.consoleInput.setText(Constants.EMPTY_STRING);
			if(insetConsole.consoleEcho) insetConsole.consoleOutput.append(a + System.lineSeparator());
			if(!insetConsole.consoleListeners.isEmpty())
			{
				ConsoleEvent b = new ConsoleEvent(insetConsole, a);
				b.start();
				b = null;
			}
		}
		
		private ConsoleActionInset(Console ConsoleWindow)
		{
			insetConsole = ConsoleWindow;
		}
	}
}