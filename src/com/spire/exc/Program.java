package com.spire.exc;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import com.spire.io.Item;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.spire.io.Stream;
import java.io.OutputStream;
import com.spire.log.Report;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.BoolTag;
import com.spire.io.Streamable;
import com.spire.io.DataStream;
import com.spire.util.Constants;
import com.spire.cred.HashCheck;
import com.spire.io.StringStream;
import com.spire.cred.Credentials;
import com.spire.ex.NullException;
import com.spire.ex.SizeException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.ex.PermissionException;

public class Program extends Item implements Streamable
{
	public static final byte ITEM_CLASS_ID = 10;
	
	private final HashCheck programCheck;
	private final ArrayList<String> programParams;
	private final ArrayList<String> programCommands;

	private volatile boolean programRunning;
	private Map<String, String> programEnviorment;
	
	private short programExit;
	private int programTimeout;
	private String programPath;
	private BoolTag programState;
	private Stream programStream;
	private String programDirectory;
	private Process programInstance;
	private Credentials programCred;
	private PrintWriter programWriter;
	private ProgramThread programThread;
	private StringStream programStreamS;
	
	public Program(String ProgramPath) throws NullException, StringException
	{
		this(ProgramPath, null, (String[])null);		
	}
	public Program(String ProgramPath, String ProgramDirectory) throws NullException, StringException
	{
		this(ProgramPath, ProgramDirectory, (String[])null);
	}
	public Program(String ProgramPath, String[] ProgramParamaters) throws NullException, StringException
	{
		this(ProgramPath, null, ProgramParamaters);
	}
	public Program(String ProgramPath, String ProgramDirectory, String... ProgramParamaters) throws NullException, StringException
	{
		this();
		if(ProgramPath == null) throw new NullException("ProgramPath");
		if(ProgramPath.isEmpty()) throw new StringException("ProgramPath");
		programPath = ProgramPath;
		programState = new BoolTag();
		programCheck.createCheck(programPath.hashCode());
		programDirectory = ProgramDirectory;
		if(ProgramParamaters != null) addParamaters(ProgramParamaters);
	}
	
	public final void clearData()
	{
		programExit = -1;
		programStream = null;
	}
	public final void clearCommands()
	{
		programCommands.clear();
	}
	public final void cleatParamaters()
	{
		programParams.clear();
	}
	public final void clearEnviorment()
	{
		if(programEnviorment != null) programEnviorment.clear();
	}
	public final void close() throws IOException
	{	
		getCurrentStream().close();
	}
	public final void flush() throws IOException
	{	
		getCurrentStream().flush();
	}
	public final void resetMark() throws IOException
	{	
		getCurrentStream().resetMark();
	}
	public final void processReport(Report ReportData)
	{
		if(programStream != null) try
		{
			getCurrentStream().processReport(ReportData);
		}
		catch (IOException Exception) { }
	}
	public final void flushStringStream() throws IOException
	{
		if(programStream == null)
		{
			Reporter.error(Reporter.REPORTER_IO, "StringStream not created!, use getStringStream()!");
			throw new IOException("StringStream not created!, use getStringStream()!");
		}
		if(programWriter == null) programWriter = ((DataStream)programStream).getWriter();
		programWriter.println(programStreamS.getString());
		programWriter.flush();
	}
	public final void setProgramCredentials(Credentials UserCred)
	{
		programCred = UserCred;
	}
	public final void writeByte(int ByteValue) throws IOException
	{	
		getCurrentStream().writeByte(ByteValue);
	}
	public final void writeChar(char CharValue) throws IOException
	{	
		getCurrentStream().writeChar(CharValue);
	}
	public final void writeLong(long LongValue) throws IOException
	{	
		getCurrentStream().writeLong(LongValue);
	}
	public final void writeShort(int ShortValue) throws IOException
	{	
		getCurrentStream().writeShort(ShortValue);
	}
	public final void skipBytes(long BytesToSkip) throws IOException
	{		
		getCurrentStream().skipBytes(BytesToSkip);
	}
	public final void markPosition(int MarkLimit) throws IOException
	{	
		getCurrentStream().markPosition(MarkLimit);
	}
	public final void writeFloat(float FloatValue) throws IOException
	{	
		getCurrentStream().writeFloat(FloatValue);
	}
	public final void setProgramClearEnviormentOnRun(boolean ClearEnv)
	{
		programState.setTagC(ClearEnv);
	}
	public final void writeInteger(int IntegerValue) throws IOException
	{
		getCurrentStream().writeInteger(IntegerValue);
	}
	public final void writeDouble(double DoubleValue) throws IOException
	{		
		getCurrentStream().writeDouble(DoubleValue);
	}
	public final void setProgramUseStringStream(boolean UseStringStream)
	{
		programState.setTagG(UseStringStream);
	}
	public final void writeBoolean(boolean BoolValue) throws IOException
	{
		getCurrentStream().writeBoolean(BoolValue);
	}
	public final void writeString(String StringValue) throws IOException
	{	
		getCurrentStream().writeString(StringValue);
	}
	public final void stopProgram() throws IOException, PermissionException
	{
		Security.check("io.program.stop", programPath);
		if(isRunning())
		{
			if(programThread != null) 
				programThread.interrupt();
			else
			{
				try
				{
					programExit = (short)programInstance.exitValue();
				}
				catch (IllegalThreadStateException Exception) { }
				programInstance.destroy();
				programWriter = null;
			}
			Reporter.debug(Reporter.REPORTER_IO, "Program at \"" + programPath + "\" was stopped");
		}
		else
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot stop a non running Program!");
			throw new IOException("Cannot stop a non running Program!");
		}
	}
	public final void startProgram() throws IOException, PermissionException
	{
		Security.check("io.program.start", programPath);
		if(!isRunning())
		{
			if(!programCheck.isValid(programPath.hashCode()))
			{
				Reporter.error(Reporter.REPORTER_IO, "Program hash check is not valid!");
				throw new IOException("Program hash check is not valid!");
			}
			programInstance = buildProgram().start();
			programStream = new DataStream(programInstance.getInputStream(), programInstance.getOutputStream());
			Reporter.debug(Reporter.REPORTER_IO, "Program at \"" + programPath + "\" was started!");
			programRunning = true;
			if(!programCommands.isEmpty())
			{
				PrintWriter a = ((DataStream)programStream).getWriter();
				if(programStreamS != null)
				{
					programStreamS.flush();
					a.println(programStreamS.getString());
					a.flush();
				}
				for(int b = 0; b < programCommands.size(); b++)
				{
					a.println(programCommands.get(b));
					a.flush();
				}
				a.flush();
				a.close();
			}
			
			if(programState.getTagB() && programState.getTagA())
				programThread = new ProgramThread(this, programInstance);
			else if(programState.getTagB()) try
			{
				programExit = (short)programInstance.waitFor();
				programRunning = false;
			}
			catch (InterruptedException Exception) { } 
			else
				programRunning = false;
		}
		else
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot start an already running Program!");
			throw new IOException("Cannot start an already running Program!");
		}
	}
	public final void removeCommand(int CommandIndex) throws NumberException
	{
		if(CommandIndex < 0) throw new NumberException("CommandIndex", CommandIndex, false);
		if(CommandIndex > programCommands.size()) throw new NumberException("CommandIndex", CommandIndex, 0, programCommands.size());
		programCommands.remove(CommandIndex);
	}
	public final void setProgramWait(boolean Wait) throws PermissionException
	{
		Security.check("io.program.wait");
		programState.setTagB(Wait);
	}
	public final void writeUnicodeString(String StringValue) throws IOException
	{
		getCurrentStream().writeUnicodeString(StringValue);
	}
	public final void removeParamater(int ParamaterIndex) throws NumberException
	{
		if(ParamaterIndex < 0) throw new NumberException("ParamaterIndex", ParamaterIndex, false);
		if(ParamaterIndex > programParams.size()) throw new NumberException("ParamaterIndex", ParamaterIndex, 0, programParams.size());
		programParams.remove(ParamaterIndex);
	}
	public final void setProgramTimeout(int TimeoutSeconds) throws NumberException
	{
		setProgramTimeout(0, TimeoutSeconds);
	}
	public final void setProgramDirectory(String Directory) throws StringException
	{
		if(Directory != null && Directory.isEmpty()) throw new StringException("Directory");
		programDirectory = Directory;
	}	
	public final void writeBytes(String StringValue) throws IOException, NullException
	{	
		getCurrentStream().writeBytes(StringValue);
	}
	public final void writeChars(String StringValue) throws IOException, NullException
	{
		getCurrentStream().writeChars(StringValue);
	}
	public final void readLongArray(long[] LongArray) throws IOException, NullException
	{		
		getCurrentStream().readLongArray(LongArray);
	}
	public final void setProgramUseThread(boolean UseThread) throws PermissionException
	{
		Security.check("io.program.thread");
		programState.setTagA(UseThread);
		if(UseThread) programState.setTagB(true);
	}	
	public final void writeByteArray(byte[] ByteArray) throws IOException, NullException
	{
		getCurrentStream().writeByteArray(ByteArray);
	}
	public final void writeLongArray(long[] LongArray) throws IOException, NullException
	{	
		getCurrentStream().writeLongArray(LongArray);
	}
	public final void addCommand(String NewCommand) throws NullException, StringException
	{
		if(NewCommand == null) throw new NullException("NewCommand");
		if(NewCommand.isEmpty()) throw new StringException("NewCommand");
		programCommands.add(NewCommand);
	}
	public final void readFloatArray(float[] FloatArray) throws IOException, NullException
	{		
		getCurrentStream().readFloatArray(FloatArray);
	}
	public final void readShortArray(short[] ShortArray) throws IOException, NullException
	{	
		getCurrentStream().readShortArray(ShortArray);
	}
	public final void writeShortArray(short[] ShortArray) throws IOException, NullException
	{
		getCurrentStream().writeShortArray(ShortArray);
	}
	public final void writeFloatArray(float[] FloatArray) throws IOException, NullException
	{	
		getCurrentStream().writeFloatArray(FloatArray);
	}
	public final void readFromStream(Streamable InStream) throws IOException, NullException
	{
		getCurrentStream().readFromStream(InStream);
	}
	public final void writeToStream(Streamable OutStream) throws IOException, NullException
	{		
		getCurrentStream().writeToStream(OutStream);
	}
	public final void readIntegerArray(int[] IntegerArray) throws IOException, NullException
	{	
		getCurrentStream().readIntegerArray(IntegerArray);
	}
	public final void readFromStream(InputStream InStream) throws IOException, NullException
	{	
		getCurrentStream().readFromStream(InStream);
	}
	public final void writeToStream(OutputStream OutStream) throws IOException, NullException
	{	
		getCurrentStream().writeToStream(OutStream);
	}
	public final void writeIntegerArray(int[] IntegerArray) throws IOException, NullException
	{		
		getCurrentStream().writeIntegerArray(IntegerArray);
	}
	public final void readStringArray(String[] StringArray) throws IOException, NullException
	{
		getCurrentStream().readStringArray(StringArray);
	}
	public final void readDoubleArray(double[] DoubleArray) throws IOException, NullException
	{		
		getCurrentStream().readDoubleArray(DoubleArray);
	}
	public final void addParamater(String NewParamater) throws NullException, StringException
	{
		if(NewParamater == null) throw new NullException("NewParamater");
		if(NewParamater.isEmpty()) throw new StringException("NewParamater");
		programParams.add(NewParamater);
	}
	public final void addCommands(String... NewCommands) throws NullException, StringException
	{
		if(NewCommands == null) throw new NullException("NewCommand");
		for(int a = 0; a < NewCommands.length; a++)
		{
			if(NewCommands[a] == null) throw new NullException("NewCommands");
			if(NewCommands[a].isEmpty()) throw new StringException("NewCommands");
			programCommands.add(NewCommands[a]);
		}
	}
	public final void removeCommand(String CommandValue) throws NullException, StringException
	{
		if(CommandValue == null) throw new NullException("CommandValue");
		if(CommandValue.isEmpty()) throw new StringException("CommandValue");
		programCommands.remove(CommandValue);
	}
	public final void writeDoubleArray(double[] DoubleArray) throws IOException, NullException
	{
		getCurrentStream().writeDoubleArray(DoubleArray);
	}
	public final void writeStringArray(String[] StringArray) throws IOException, NullException
	{	
		getCurrentStream().writeStringArray(StringArray);
	}
	public final void readStringList(List<String> StringList) throws IOException, NullException
	{	
		getCurrentStream().readStringList(StringList);
	}
	public final void writeStringList(List<String> StringList) throws IOException, NullException
	{	
		getCurrentStream().writeStringList(StringList);
	}
	public final void addParamaters(String... NewParamaters) throws NullException, StringException
	{
		if(NewParamaters == null) throw new NullException("NewParamater");
		for(int a = 0; a < NewParamaters.length; a++)
		{
			if(NewParamaters[a] == null) throw new NullException("NewParamaters");
			if(NewParamaters[a].isEmpty()) throw new StringException("NewParamaters");
			programParams.add(NewParamaters[a]);
		}
	}
	public final void removeParamater(String ParamaterValue) throws NullException, StringException
	{
		if(ParamaterValue == null) throw new NullException("ParamaterValue");
		if(ParamaterValue.isEmpty()) throw new StringException("ParamaterValue");
		programParams.remove(ParamaterValue);
	}
	public final void pushCommand(String NewCommand) throws NullException, StringException, IOException
	{
		if(NewCommand == null) throw new NullException("NewCommand");
		if(NewCommand.isEmpty()) throw new StringException("NewCommand");
		if(isRunning())
		{
			if(programWriter == null) programWriter = ((DataStream)programStream).getWriter();
			programWriter.println(NewCommand);
			programWriter.flush();
		}
		else
		{
			Reporter.error(Reporter.REPORTER_IO, "Cannot push to a non running Program!");
			throw new IOException("Cannot push to a non running Program!");
		}
	}
	public final void writeBooleanTags(boolean... BoolValues) throws IOException, NullException, SizeException
	{		
		getCurrentStream().writeBooleanTags(BoolValues);
	}
	public final void setProgramTimeout(int TimeoutMins, int TimeoutSeconds) throws NumberException, PermissionException
	{
		if(TimeoutMins < 0) throw new NumberException("TimeoutMins", TimeoutMins, false);
		if(TimeoutSeconds < 0) throw new NumberException("TimeoutSeconds", TimeoutSeconds, false);
		if((TimeoutMins > 0 || TimeoutSeconds > 0) && !programUsesThread()) setProgramUseThread(true);
		programTimeout = (TimeoutMins * 60) + TimeoutSeconds;
	}
	public final void readLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readLongArray(LongArray, StartIndex);
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeByteArray(ByteArray, StartIndex);
	}
	public final void writeLongArray(long[] LongArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeLongArray(LongArray, StartIndex);
	}
	public final void readShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readShortArray(ShortArray, StartIndex);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readFloatArray(FloatArray, StartIndex);
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeShortArray(ShortArray, StartIndex);
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeFloatArray(FloatArray, StartIndex);
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readIntegerArray(IntegerArray, StartIndex);	
	}
	public final void readStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readStringArray(StringArray, StartIndex);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex)	throws IOException, NullException, NumberException
	{
		getCurrentStream().readDoubleArray(DoubleArray, StartIndex);		
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeIntegerArray(IntegerArray, StartIndex);	
	}
	public final void writeStringArray(String[] StringArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeStringArray(StringArray, StartIndex);
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeDoubleArray(DoubleArray, StartIndex);	
	}
	public final void readStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readStringList(StringList, StartIndex);	
	}
	public final void writeStringList(List<String> StringList, int StartIndex) throws IOException, NullException, NumberException
	{
		getCurrentStream().writeStringList(StringList, StartIndex);	
	}
	public final void readLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readLongArray(LongArray, StartIndex, Length);	
	}
	public final void writeByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeByteArray(ByteArray, StartIndex, Length);	
	}
	public final void writeLongArray(long[] LongArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeLongArray(LongArray, StartIndex, Length);	
	}
	public final void readShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readShortArray(ShortArray, StartIndex, Length);
	}
	public final void readFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		getCurrentStream().readFloatArray(FloatArray, StartIndex, Length);	
	}
	public final void writeShortArray(short[] ShortArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeShortArray(ShortArray, StartIndex, Length);
	}
	public final void writeFloatArray(float[] FloatArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{
		getCurrentStream().writeFloatArray(FloatArray, StartIndex, Length);
	}
	public final void readIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readIntegerArray(IntegerArray, StartIndex, Length);	
	}
	public final void writeIntegerArray(int[] IntegerArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeIntegerArray(IntegerArray, StartIndex, Length);
	}
	public final void readStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readStringArray(StringArray, StartIndex, Length);
	}
	public final void readDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().readDoubleArray(DoubleArray, StartIndex, Length);
	}
	public final void writeStringArray(String[] StringArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeStringArray(StringArray, StartIndex, Length);	
	}
	public final void writeDoubleArray(double[] DoubleArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		getCurrentStream().writeDoubleArray(DoubleArray, StartIndex, Length);
	}
	public final void writeStringList(List<String> StringList, int StartIndex, int Length) throws IOException, NullException, NumberException
	{		
		getCurrentStream().writeStringList(StringList, StartIndex, Length);
	}
	
	public final boolean isValid()
	{
		return programCheck.isValid(hashCode());
	}
	public final boolean isRunning()
	{
		return programRunning && programInstance != null;
	}
	public final boolean programWaits()
	{
		return programState.getTagB();
	}
	public final boolean isStreamInput()
	{	
		return programStream != null && programStream.isStreamInput();
	}
	public final boolean isStreamOutput()
	{	
		return (programStream != null && programStream.isStreamOutput()) || (programState.getTagG() || programStreamS != null);
	}
	public final boolean programCanTimeout()
	{
		return programState.getTagB() && programTimeout > 0;
	}
	public final boolean programUsesThread()
	{
		return programState.getTagA();
	}
	public final boolean programHasCredentials()
	{
		return programCred != null;
	}
	public final boolean programClearsEnviormentOnRun()
	{
		return programState.getTagC();
	}
	public final boolean readBoolean() throws IOException
	{
		return getCurrentStream().readBoolean();
	}
	public final boolean canProcessReport(byte ReportLevel)
	{
		return programState.getTagG() ? (programStream != null && programStream.canProcessReport(ReportLevel)) : (programStreamS != null && programStreamS.canProcessReport(ReportLevel));
	}
	
	public final byte readByte() throws IOException
	{
		return getCurrentStream().readByte();
	}
	
	public final char readChar() throws IOException
	{	
		return getCurrentStream().readChar();
	}
	
	public final short getExitCode()
	{
		if(isRunning())
		{
			if(programState.getTagB() && !programState.getTagA()) try
			{
				programExit = (short)programInstance.waitFor();
			}
			catch (InterruptedException Exception) { }
			try
			{
				programExit = (short)programInstance.exitValue();
			}
			catch (IllegalThreadStateException Exception) { }
		}
		return programExit;
	}
	public final short readShort() throws IOException
	{	
		return getCurrentStream().readShort();
	}
	
	public final int hashCode()
	{
		return programTimeout + programPath.hashCode() + programCommands.hashCode() + programState.hashCode() + (programEnviorment != null ? programEnviorment.hashCode() : 0) +
			   (programCred != null ? programCred.hashCode() : 0);
	}
	public final int getCommandsSize()
	{
		return programCommands.size();
	}
	public final int getParamatersSize()
	{
		return programParams.size();
	}
	public final int getProgramTimeout()
	{
		return programTimeout;
	}
	public final int readInteger() throws IOException
	{	
		return getCurrentStream().readInteger();
	}
	public final int getAvailable() throws IOException
	{	
		return getCurrentStream().getAvailable();
	}
	public final int readUnsignedByte() throws IOException
	{	
		return getCurrentStream().readUnsignedByte();
	}
	public final int readUnsignedShort() throws IOException
	{	
		return getCurrentStream().readUnsignedShort();
	}
	public final int readByteArray(byte[] ByteArray) throws IOException, NullException
	{		
		return getCurrentStream().readByteArray(ByteArray);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex) throws IOException, NullException, NumberException
	{
		return getCurrentStream().readByteArray(ByteArray, StartIndex);
	}
	public final int readByteArray(byte[] ByteArray, int StartIndex, int Length) throws IOException, NullException, NumberException
	{	
		return getCurrentStream().readByteArray(ByteArray, StartIndex, Length);	
	}
	
	public final float readFloat() throws IOException
	{	
		return getCurrentStream().readFloat();
	}
	
	public final long readLong() throws IOException
	{	
		return getCurrentStream().readLong();
	}
	
	public final double readDouble() throws IOException
	{	
		return getCurrentStream().readDouble();
	}
	
	public final String toString()
	{
		return "Program(" + getItemID() + ")" + programState.getTagData() + " " + (isRunning() ? 'R' : 'W');
	}
	public final String getProgramPath()
	{
		return programPath;
	}
	public final String getProgramDirectory()
	{
		return programDirectory;
	}
	public final String readString() throws IOException
	{	
		return getCurrentStream().readString();
	}
	public final String readAllOutput() throws IOException
	{
		StringBuilder a = new StringBuilder();
		byte[] b = new byte[250];
		for(short c = 0; (c = (short)getCurrentStream().readByteArray(b)) > 0; )
			a.append(new String(b, 0, c));
		return a.toString();
	}
	
	public final Map<String, String> getEnviorment()
	{
		if(programEnviorment == null)
		{
			programState.setTagH(true);
			programEnviorment = new HashMap<String, String>();
		}
		return programEnviorment;
	}
	
	public final List<String> getCommands()
	{
		return programCommands;
	}
	public final List<String> getParamaters()
	{
		return programParams;
	}
	
	public final Stream getStream()
	{
		return programState.getTagG() ? programStreamS : programStream;
	}
	
	public final StringStream getStringStream()
	{
		if(programStreamS == null) programStreamS = new StringStream();
		return programStreamS;
	}
	
	public final InputStream getStreamInput() throws IOException
	{
		return getCurrentStream().getStreamInput();
	}
	
	public final OutputStream getStreamOutput() throws IOException
	{	
		return getCurrentStream().getStreamOutput();
	}
	
	public final BoolTag readBooleanTags() throws IOException
	{	
		return getCurrentStream().readBooleanTags();
	}
	
	public final byte[] readByteArray(int ReadAmount) throws IOException, NumberException
	{	
		return getCurrentStream().readByteArray(ReadAmount);
	}
	
	public final short[] readShortArray(int ReadAmount) throws IOException, NumberException
	{	
		return getCurrentStream().readShortArray(ReadAmount);
	}
	
	public final int[] readIntegerArray(int ReadAmount) throws IOException, NumberException
	{	
		return getCurrentStream().readIntegerArray(ReadAmount);
	}
	
	public final float[] readFloatArray(int ReadAmount) throws IOException, NumberException
	{	
		return getCurrentStream().readFloatArray(ReadAmount);
	}
	
	public final long[] readLongArray(int ReadAmount) throws IOException, NumberException
	{	
		return getCurrentStream().readLongArray(ReadAmount);
	}
	
	public final double[] readDoubleArray(int ReadAmount) throws IOException, NumberException
	{	
		return getCurrentStream().readDoubleArray(ReadAmount);
	}
	
	public final String[] readStringArray(int ReadAmount) throws IOException, NumberException
	{
		return getCurrentStream().readStringArray(ReadAmount);
	}
	
	protected Program()
	{
		super(ITEM_CLASS_ID);
		programCheck = new HashCheck();
		programParams = new ArrayList<String>();
		programCommands = new ArrayList<String>();
	}
	protected final void readItemFailure()
	{
		programExit = -1;
		programPath = null;
		programCred = null;
		programTimeout = 0;
		programParams.clear();
		programCommands.clear();
		programDirectory = null;
		programState = new BoolTag();
	}
	protected final void threadFinished(short ExitCode)
	{
		programExit = ExitCode;
		programRunning = false;
		programThread = null;
		programWriter = null;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		programExit = itemEncoder.readShort(InStream);
		programPath = itemEncoder.readString(InStream);
		programCheck.readStorage(InStream, itemEncoder);
		programTimeout = itemEncoder.readInteger(InStream);
		programDirectory = itemEncoder.readString(InStream);
		programState = itemEncoder.readBooleanTags(InStream);
		programCred = null;
		itemEncoder.readStringList(InStream, programParams);
		itemEncoder.readStringList(InStream, programCommands);
		if(programState.getTagH())
		{
			int a = 0;
			switch(itemEncoder.readByte(InStream))
			{
			case 0:
				a = itemEncoder.readUnsignedByte(InStream);
				break;
			case 1:
				a = itemEncoder.readUnsignedShort(InStream);
				break;
			case 2:
				a = itemEncoder.readInteger(InStream);
				break;
			}
			programEnviorment = new HashMap<String, String>(a);
			for(; a > 0; a--)
				programEnviorment.put(itemEncoder.readString(InStream), itemEncoder.readString(InStream));
		}
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeShort(OutStream, programExit);
		itemEncoder.writeString(OutStream, programPath);
		programCheck.writeStorage(OutStream, itemEncoder);
		itemEncoder.writeInteger(OutStream, programTimeout);
		itemEncoder.writeString(OutStream, programDirectory);
		itemEncoder.writeByte(OutStream, programState.getTagData());
		programCred = null;
		itemEncoder.writeStringList(OutStream, programParams);
		itemEncoder.writeStringList(OutStream, programCommands);
		if(programEnviorment != null)
		{
			if(programEnviorment.size() < 255)
			{
				itemEncoder.writeByte(OutStream, 0);
				itemEncoder.writeByte(OutStream, programEnviorment.size());
			}
			else if(programEnviorment.size() < Short.MAX_VALUE)
			{
				itemEncoder.writeByte(OutStream, 1);
				itemEncoder.writeShort(OutStream, programEnviorment.size());
			}
			else
			{
				itemEncoder.writeByte(OutStream, 2);
				itemEncoder.writeInteger(OutStream, programEnviorment.size());
			}
			String[] a = programEnviorment.keySet().toArray(new String[programEnviorment.size()]),
					 b = programEnviorment.values().toArray(new String[programEnviorment.size()]);
			for(int c = 0; c < a.length; c++)
			{
				itemEncoder.writeString(OutStream, a[c]);
				itemEncoder.writeString(OutStream, b[c]);
			}
		}
	}
	
	protected final ProcessBuilder buildProgram()
	{
		ArrayList<String> a = new ArrayList<String>();
		a.add(programPath);
		if(!programParams.isEmpty())
			a.addAll(programParams);
		ProcessBuilder b = new ProcessBuilder(a);
		if(programDirectory != null && !programDirectory.isEmpty())
			b.directory(new File(Constants.CURRENT_OS.phrasePath(programDirectory)));
		if(programState.getTagC())
			b.environment().clear();
		if(programEnviorment != null)
			b.environment().putAll(programEnviorment);
		b.redirectErrorStream(true);
		return b;
	}
	
	private final Stream getCurrentStream() throws IOException
	{
		if(programState.getTagG())
		{
			if(programStreamS == null) programStreamS = new StringStream();
			return programStreamS;
		}
		else if(programStream != null)
			return programStream;
		Reporter.error(Reporter.REPORTER_IO, "The program stream is not currently open!");
		throw new IOException("The program stream is not currently open!");
	}
}