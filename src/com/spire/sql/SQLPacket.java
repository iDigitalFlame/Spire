package com.spire.sql;

import java.sql.Time;
import java.util.Date;
import java.util.Arrays;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.io.IOException;
import com.spire.util.Stamp;
import com.spire.net.Packet;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.FormatException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

public final class SQLPacket extends Packet
{
	public static final byte ITEM_CLASS_ID = 15;
	public static final byte SQLPACKET_QUERY = 0;
	public static final byte SQLPACKET_RESULTS = 1;
	public static final byte SQLPACKET_ERROR = 2;
	public static final byte SQLPACKET_URESULTS = 3;
	
	protected static final byte VALUE_NULL = 0;
	protected static final byte VALUE_BYTE = 1;
	protected static final byte VALUE_BOOL = 2;
	protected static final byte VALUE_DATE = 3;
	protected static final byte VALUE_BYTES = 4;
	protected static final byte VALUE_FLOAT = 5;
	protected static final byte VALUE_LONG = 6;
	protected static final byte VALUE_DOUBLE = 7;
	protected static final byte VALUE_INTEGER = 8;
	protected static final byte VALUE_STRING = 9;
	protected static final byte VALUE_NSTRING = 10;
	protected static final byte VALUE_SHORT = 11;
	protected static final byte VALUE_TIME = 12;
	protected static final byte VALUE_TIMESTAMP = 13;
	protected static final byte CHARACTER_PARAM = '?';
	
	private String sqlQuery;
	private SQLPacketParam[] sqlParams;
	private SQLResultsReader sqlResults;
	
	public SQLPacket(int RowsAffected)
	{
		super(ITEM_CLASS_ID, SQLPACKET_URESULTS);
		sqlQuery = RowsAffected + " Rows Affected";
	}
	public SQLPacket(Exception SQLException) throws NullException
	{
		super(ITEM_CLASS_ID, SQLPACKET_ERROR);
		sqlQuery = SQLException.getMessage();
	}
	public SQLPacket(String SQLQuery) throws NullException, StringException
	{
		super(ITEM_CLASS_ID, SQLPACKET_QUERY);
		if(SQLQuery == null) throw new NullException("SQLQuery");
		if(SQLQuery.isEmpty()) throw new StringException("SQLQuery");
		sqlQuery = SQLQuery;
		int a = 0;
		for(int b = 0; b < sqlQuery.length(); b++)
			if(sqlQuery.charAt(b) == CHARACTER_PARAM) a++;
		sqlParams = new SQLPacketParam[a];
	}
	public SQLPacket(ResultSet SQLResults) throws NullException, IOException
	{
		super(ITEM_CLASS_ID, SQLPACKET_RESULTS);
		if(SQLResults == null) throw new NullException("SQLResults");
		sqlResults = new SQLResultsReader(SQLResults);
	}
	
	public final void clearParamaters()
	{
		Arrays.fill(sqlParams, null);
	}
	public final void setNull(int ParameterIndex, int NullType) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_NULL, (short)NullType);
	}
	public final void setLong(int ParameterIndex, long LongValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_LONG, LongValue);
	}
	public final void setByte(int ParameterIndex, byte ByteValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_BYTE, ByteValue);
	}
	public final void setDate(int ParameterIndex, Date DateValue) throws NumberException, FormatException
	{
		setDate(ParameterIndex, new Stamp(DateValue));
	}
	public final void setTime(int ParameterIndex, Time TimeValue) throws NumberException, FormatException
	{
		setTime(ParameterIndex, new Stamp(TimeValue.getTime()));
	}
	public final void setTime(int ParameterIndex, Stamp TimeValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_TIME, TimeValue);
	}
	public final void setDate(int ParameterIndex, Stamp DateValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_DATE, DateValue);
	}
	public final void setBytes(int ParameterIndex, byte[] ByteArray) throws NumberException, FormatException
	{
		setBytes(ParameterIndex, ByteArray, 0, ByteArray == null ? 0 : ByteArray.length);
	}
	public final void setFloat(int ParameterIndex, float FloatValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_FLOAT, FloatValue);
	}
	public final void setShort(int ParameterIndex, short ShortValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_SHORT, ShortValue);
	}
	public final void setInteger(int ParameterIndex, int IntegerValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_INTEGER, IntegerValue);
	}
	public final void setBoolean(int ParameterIndex, boolean BoolValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_BOOL, BoolValue);
	}
	public final void setDouble(int ParameterIndex, double DoubleValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_DOUBLE, DoubleValue);
	}
	public final void setString(int ParameterIndex, String StringValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_STRING, StringValue);
	}
	public final void setNString(int ParameterIndex, String NStringValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_NSTRING, NStringValue);
	}
	public final void setTimestamp(int ParameterIndex, Stamp TimestampValue) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_TIMESTAMP, TimestampValue);
	}
	public final void setTimestamp(int ParameterIndex, Timestamp TimestampValue) throws NumberException, FormatException
	{
		setTimestamp(ParameterIndex, new Stamp(TimestampValue.getTime()));
	}
	public final void setBytes(int ParameterIndex, byte[] ByteArray, int StartIndex) throws NumberException, FormatException
	{
		setBytes(ParameterIndex, ByteArray, StartIndex, ByteArray == null ? 0 : ByteArray.length);
	}
	public final void setBytes(int ParameterIndex, byte[] ByteArray, int StartIndex, int EndIndex) throws NumberException, FormatException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ParameterIndex > sqlParams.length) throw new NumberException("ParameterIndex", ParameterIndex, 0, sqlParams.length);
		if(ByteArray != null)
		{
			if(StartIndex < 0) throw new NumberException("StartIndex", StartIndex, false);
			if(StartIndex > EndIndex) throw new NumberException("StartIndex", StartIndex, 0, ByteArray.length);
			if(EndIndex > ByteArray.length) throw new NumberException("EndIndex", EndIndex, StartIndex, ByteArray.length);
		}
		byte[] a = ByteArray != null && StartIndex == 0 && EndIndex == ByteArray.length ? ByteArray : ByteArray != null ? Arrays.copyOfRange(ByteArray, StartIndex, EndIndex) : null;
		sqlParams[ParameterIndex] = new SQLPacketParam(VALUE_BYTES, a);
	}
	
	public final boolean isError()
	{
		return getID() == SQLPACKET_ERROR || getID() == -1;
	}
	public final boolean isQuery()
	{
		return getID() == SQLPACKET_QUERY;
	}
	public final boolean isResults()
	{
		return getID() == SQLPACKET_RESULTS;
	}
	public final boolean isMessage()
	{
		return getID() == SQLPACKET_URESULTS || getID() == SQLPACKET_ERROR;
	}
	public final boolean isSelectQuery()
	{
		return isQuery() && (sqlQuery.startsWith("select") || sqlQuery.startsWith("SELECT"));
	}
	
	public final int getPacketHash()
	{
		return (isQuery() || isMessage() || isError() ? sqlQuery.hashCode() : 0) + (isResults() ? sqlResults.hashCode() : isQuery() ? getParamsHash() : 0);
	}
	public final int getParamsHash()
	{
		int a = 0;
		for(int b = 0; b < sqlParams.length; b++)
			a += sqlParams[b].hashCode();
		return a;
	}
	
	public final String toString()
	{
		return "SQLPacket(" + getItemID() + ") " + (isResults() ? "R" : isQuery() ? "Q" : isMessage() ? "M" : "E");
	}
	public final String getMessage()
	{
		return isError() || isMessage() ? sqlQuery : null;
	}
	public final String getErrorMessage()
	{
		return isError() ? sqlQuery : null;
	}
	
	public final SQLResults getResults() throws FormatException
	{
		if(!isResults()) throw new FormatException("This is not a ResultsPacket!");
		return new SQLResults(sqlResults);
	}
	
	public final SQLCommand createCommand(DataConnection Connection) throws NullException, FormatException, IOException
	{
		if(!isQuery()) throw new FormatException("This is not a QueryPacket!");
		if(Connection == null) throw new NullException("Connection");
		SQLCommand a = new SQLCommand(sqlQuery, Connection);
		for(int b = 0; b < sqlParams.length; b++)
			sqlParams[b].addToCommand(b, a);
		return a;
	}
	
	protected SQLPacket()
	{
		super(ITEM_CLASS_ID, SQLPACKET_QUERY);
	}
	
	protected final void readItemFailure()
	{
		sqlQuery = null;
		sqlParams = null;
		sqlResults = null;
		super.readItemFailure();
	}
	protected final void readPacket(Streamable InStream) throws IOException
	{
		sqlQuery = itemEncoder.readString(InStream);
		if(isResults())
		{
			sqlResults = new SQLResultsReader();
			sqlResults.readStorage(InStream, itemEncoder);
		}
		else if(isQuery())
		{
			switch(itemEncoder.readByte(InStream))
			{
			case 0:
				sqlParams = new SQLPacketParam[itemEncoder.readUnsignedByte(InStream)];
				break;
			case 1:
				sqlParams = new SQLPacketParam[itemEncoder.readUnsignedShort(InStream)];
				break;
			case 2:
				sqlParams = new SQLPacketParam[itemEncoder.readInteger(InStream)];
				break;
			default:
				sqlParams = new SQLPacketParam[0];
				break;
			}
			for(int a = 0; a < sqlParams.length; a++)
			{
				sqlParams[a] = new SQLPacketParam();
				sqlParams[a].readStorage(InStream, itemEncoder);
			}
		}
	}
	protected final void writePacket(Streamable OutStream) throws IOException
	{
		itemEncoder.writeString(OutStream, sqlQuery);
		if(isResults())
			sqlResults.writeStorage(OutStream, itemEncoder);
		else if(isQuery())
		{
			if(sqlParams.length < 255)
			{
				itemEncoder.writeByte(OutStream, 0);
				itemEncoder.writeByte(OutStream, sqlParams.length);
			}
			else if(sqlParams.length < Constants.MAX_USHORT_SIZE)
			{
				itemEncoder.writeByte(OutStream, 1);
				itemEncoder.writeShort(OutStream, sqlParams.length);
			}
			else
			{
				itemEncoder.writeByte(OutStream, 2);
				itemEncoder.writeInteger(OutStream, sqlParams.length);
			}
			for(int a = 0; a < sqlParams.length; a++)
				if(sqlParams[a] != null)
					sqlParams[a].writeStorage(OutStream, itemEncoder);
				else
				{
					sqlParams[a] = new SQLPacketParam(VALUE_NULL, 0);
					sqlParams[a].writeStorage(OutStream, itemEncoder);
				}
		}
	}
}