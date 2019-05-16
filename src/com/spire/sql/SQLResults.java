package com.spire.sql;

import java.sql.ResultSet;
import java.io.IOException;
import com.spire.util.Stamp;
import com.spire.util.HashList;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.FormatException;
import com.spire.ex.NumberException;

public final class SQLResults
{
	private final SQLResultsRow[] resultsData;
	
	private int resultsIndex;
	
	public SQLResults(ResultSet SQLResults) throws NullException, IOException
	{
		this(new SQLResultsReader(SQLResults));
	}

	public final void setRow(int RowIndex) throws NumberException
	{
		if(RowIndex < 0) throw new NumberException("RowIndex", RowIndex, false);
		if(RowIndex > resultsData.length) throw new NumberException("RowIndex", RowIndex, 0, resultsData.length);
		resultsIndex = RowIndex;
	}
	
	public final boolean isEmpty()
	{
		return resultsData.length == 0;
	}
	public final boolean nextRow()
	{
		if((resultsIndex + 1) < resultsData.length)
		{
			resultsIndex++;
			return true;
		}
		return false;
	}
	public final boolean previousRow()
	{
		if((resultsIndex - 1) >= 0)
		{
			resultsIndex--;
			return true;
		}
		return false;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof SQLResults && ((SQLResults)CompareObject).resultsData.equals(resultsData);
	}
	public final boolean getBoolean(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? false : a.paramData instanceof Boolean ? ((Boolean)a.paramData).booleanValue() : false;
	}
	public final boolean getBoolean(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? false : a.paramData instanceof Boolean ? ((Boolean)a.paramData).booleanValue() : false;
	}
	public final boolean containsColumn(String ColumnName) throws NullException, FormatException
	{
		return getColumn(ColumnName) != null;
	}
	
	public final byte getByte(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? (byte)0 : a.paramType == SQLPacket.VALUE_BOOL ? ((Byte)a.paramData).byteValue() : (byte)0;
	}
	public final byte getByte(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? (byte)0 : a.paramType == SQLPacket.VALUE_BOOL ? ((Byte)a.paramData).byteValue() : (byte)0;
	}
	
	public final short getShort(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? (short)0 : a.paramType == SQLPacket.VALUE_SHORT ? ((Short)a.paramData).shortValue() : (short)0;
	}
	public final short getShort(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? (short)0 : a.paramType == SQLPacket.VALUE_SHORT ? ((Short)a.paramData).shortValue() : (short)0;
	}
	
	public final int getRows()
	{
		return resultsData.length;
	}
	public final int hashCode()
	{
		return resultsData.hashCode();
	}
	public final int getColumns()
	{
		return resultsData[0].rowData.size();
	}
	public final int getSelectedRow()
	{
		return resultsIndex;
	}
	public final int getInteger(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? 0 : a.paramType == SQLPacket.VALUE_INTEGER ? ((Integer)a.paramData).intValue() : 0;
	}
	public final int getInteger(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? 0 : a.paramType == SQLPacket.VALUE_INTEGER ? ((Integer)a.paramData).intValue() : 0;
	}
	
	public final float getFloat(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? 0F : a.paramType == SQLPacket.VALUE_FLOAT ? ((Float)a.paramData).floatValue() : 0F;
	}
	public final float getFloat(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? 0F : a.paramType == SQLPacket.VALUE_FLOAT ? ((Float)a.paramData).floatValue() : 0F;
	}
	
	public final long getLong(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? 0L : a.paramType == SQLPacket.VALUE_LONG ? ((Long)a.paramData).longValue() : 0L;
	}
	public final long getLong(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? 0L : a.paramType == SQLPacket.VALUE_LONG ? ((Long)a.paramData).longValue() : 0L;
	}
	
	public final double getDouble(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? 0D : a.paramType == SQLPacket.VALUE_DOUBLE ? ((Double)a.paramData).doubleValue() : 0D;
	}
	public final double getDouble(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? 0L : a.paramType == SQLPacket.VALUE_DOUBLE ? ((Double)a.paramData).doubleValue() : 0D;
	}

	public final String toString()
	{
		return "SQLResults(DR) " + resultsIndex + "/" + resultsData.length;
	}
	public final String getString(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? null : (a.paramType == SQLPacket.VALUE_STRING || a.paramType == SQLPacket.VALUE_NSTRING) ? (String)a.paramData : null;
	}
	public final String getString(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? null : (a.paramType == SQLPacket.VALUE_STRING || a.paramType == SQLPacket.VALUE_NSTRING) ? (String)a.paramData : null;
	}
	
	public final Stamp getDate(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? null : a.paramType == SQLPacket.VALUE_DATE ? (Stamp)a.paramData : null;
	}
	public final Stamp getDate(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? null : a.paramType == SQLPacket.VALUE_DATE ? (Stamp)a.paramData : null;
	}
	public final Stamp getTime(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? null : a.paramType == SQLPacket.VALUE_TIME ? (Stamp)a.paramData : null;
	}
	public final Stamp getTime(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? null : a.paramType == SQLPacket.VALUE_TIME ? (Stamp)a.paramData : null;
	}
	public final Stamp getTimestamp(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? null : a.paramType == SQLPacket.VALUE_TIMESTAMP ? (Stamp)a.paramData : null;
	}
	public final Stamp getTimestamp(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? null : a.paramType == SQLPacket.VALUE_TIMESTAMP ? (Stamp)a.paramData : null;
	}
		
	public final Object getObject(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? null : a.paramType == SQLPacket.VALUE_NULL ? null : a.paramData;
	}
	public final Object getObject(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? null : a.paramType == SQLPacket.VALUE_NULL ? null : a.paramData;
	}
	
	public final byte[] getByteArray(String ColumnName) throws NullException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnName);
		return a == null ? null : a.paramType == SQLPacket.VALUE_BYTES ? (byte[])a.paramData : null;
	}
	public final byte[] getByteArray(int ColumnIndex) throws NumberException, FormatException
	{
		SQLPacketColumn a = getColumn(ColumnIndex);
		return a == null ? null : a.paramType == SQLPacket.VALUE_BYTES ? (byte[])a.paramData : null;
	}
	
	protected SQLResults(SQLResultsReader ReaderData)
	{
		resultsIndex = -1;
		resultsData = new SQLResultsRow[ReaderData.resultsRows.size()];
		for(int a = 0; a < resultsData.length; a++)
		{
			resultsData[a] = new SQLResultsRow();
			for(int b = 0; b < ReaderData.resultsRows.get(a).length; b++)
				resultsData[a].rowData.addElement(ReaderData.resultsRows.get(a)[b]);
		}
	}
	
	protected final SQLPacketColumn getColumn(int ColumnIndex) throws NullException, FormatException
	{
		if(resultsIndex == -1) throw new FormatException("No Index is selected! Please use nextRow(), previousRow() or setRow(int)!");
		if(ColumnIndex < 0) throw new NumberException("ColumnIndex", ColumnIndex, false);
		if(ColumnIndex > resultsData[resultsIndex].rowData.size()) throw new NumberException("ColumnIndex", ColumnIndex, 0, resultsData[resultsIndex].rowData.size());
		return resultsData[resultsIndex].rowData.get(ColumnIndex);
	}
	protected final SQLPacketColumn getColumn(String ColumnName) throws NullException, FormatException
	{
		if(resultsIndex == -1) throw new FormatException("No Index is selected! Please use nextRow(), previousRow() or setRow(int)!");
		if(ColumnName == null) throw new NullException("ColumnName");
		return resultsData[resultsIndex].rowData.get(ColumnName);
	}

	protected final SQLResults clone() throws CloneException
	{
		throw new CloneException("Cannot clone a SQLResults object!");
	}
	
	private static final class SQLResultsRow
	{
		private final HashList<String, SQLPacketColumn> rowData;
		
		private SQLResultsRow()
		{
			rowData = new HashList<String, SQLPacketColumn>();
		}
	}
}