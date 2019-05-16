package com.spire.sql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import java.sql.SQLException;
import com.spire.log.Reporter;
import com.spire.io.Streamable;
import java.lang.reflect.Field;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;

public final class SQLResultsReader implements Storage
{
	private static final String MYSQL_RESULTS_CLASS_ROOT = "com.mysql.";
	private static final String SQL_RESULTS_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerResultSet";
	
	protected final ArrayList<SQLPacketColumn[]> resultsRows;
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		int a = 0, b = 0;
		switch(StorageEncoder.readByte(InStream))
		{
		case 0:
			a = StorageEncoder.readUnsignedByte(InStream);
			break;
		case 1:
			a = StorageEncoder.readUnsignedShort(InStream);
			break;
		case 2:
			a = StorageEncoder.readInteger(InStream);
			break;
		default:
			a = 0;
			break;
		}
		if(StorageEncoder.readBoolean(InStream))
			b = StorageEncoder.readUnsignedByte(InStream);
		else b = StorageEncoder.readUnsignedShort(InStream);
		resultsRows.ensureCapacity(a);
		SQLPacketColumn[] c = null;
		for(; a > 0; a--)
		{
			c = new SQLPacketColumn[b];
			for(int d = 0; d < b; d++)
			{
				c[d] = new SQLPacketColumn();
				c[d].readStorage(InStream, StorageEncoder);
			}
			resultsRows.add(c);
			c = null;
		}
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		if(resultsRows.size() < 255)
		{
			StorageEncoder.writeByte(OutStream, 0);
			StorageEncoder.writeByte(OutStream, resultsRows.size());
		}
		else if(resultsRows.size() < Constants.MAX_USHORT_SIZE)
		{
			StorageEncoder.writeByte(OutStream, 1);
			StorageEncoder.writeShort(OutStream, resultsRows.size());
		}
		else
		{
			StorageEncoder.writeByte(OutStream, 2);
			StorageEncoder.writeInteger(OutStream, resultsRows.size());
		}
		int a = resultsRows.get(0).length;
		StorageEncoder.writeBoolean(OutStream, a < 255);
		if(a < 255) StorageEncoder.writeByte(OutStream, a);
		else StorageEncoder.writeShort(OutStream, a);
		for(int b = 0; b < resultsRows.size(); b++)
			for(int c = 0; c < a; c++)
				resultsRows.get(b)[c].writeStorage(OutStream, StorageEncoder);
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof SQLResultsReader && ((SQLResultsReader)CompareObject).resultsRows.equals(resultsRows);
	}
	
	public final int hashCode()
	{
		int a = 0;
		for(int b = 0; b < resultsRows.size(); b++)
			for(int c = 0; c < resultsRows.get(b).length; c++)
				a += resultsRows.get(b)[c].hashCode();
		return a;
	}
	
	public final String toString()
	{
		return "SQLResultsReader(S) " + resultsRows.size();
	}
	
	protected SQLResultsReader()
	{
		resultsRows = new ArrayList<SQLPacketColumn[]>();
	}
	protected SQLResultsReader(ResultSet SQLResults) throws IOException, NullException
	{
		this();
		if(SQLResults == null) throw new NullException("SQLResults");
		transferData(resultsRows, SQLResults);
	}
	
	protected final SQLResultsReader clone() throws CloneException
	{
		throw new CloneException("Cannot clone a SQLResultsReader!");
	}
	
	private static final void transferData(ArrayList<SQLPacketColumn[]> ArrayData, ResultSet Results) throws IOException
	{
		if(!Results.getClass().getName().equals(SQL_RESULTS_CLASS) && !Results.getClass().getName().contains(MYSQL_RESULTS_CLASS_ROOT))
			throw new IOException("Not compatible with this type of Result set!");
		Field a = null, b = null;
		Object c = null;
		int d = 0;
		SQLPacketColumn[] e = null;
		try
		{
			if(Results.getClass().getName().equals(SQL_RESULTS_CLASS))
			{
				while(Results.next())
				{
					a = Results.getClass().getDeclaredField("columns");
					a.setAccessible(true);
					c = a.get(Results);
					d = ((Object[])c).length;
					e = new SQLPacketColumn[d];
					for(int f = 0; f < d; f++)
					{
						b = ((Object[])c)[f].getClass().getDeclaredField("columnName");
						b.setAccessible(true);
						e[f] = new SQLPacketColumn((String)b.get(((Object[])c)[f]), Results.getObject(f + 1));
					}
					ArrayData.add(e);
					e = null;
				}
			}
			else
			{
				while(Results.next())
				{
					a = Results.getClass().getSuperclass().getDeclaredField("fields");
					a.setAccessible(true);
					c = a.get(Results);
					d = ((Object[])c).length;
					e = new SQLPacketColumn[d];
					for(int f = 0; f < d; f++)
						e[f] = new SQLPacketColumn(((com.mysql.jdbc.Field[])c)[f].getColumnLabel(), Results.getObject(f + 1));
					ArrayData.add(e);
					e = null;
				}
			}
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
		catch (SecurityException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
		catch (IllegalAccessException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
		catch (IllegalArgumentException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
		catch (NoSuchFieldException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
}