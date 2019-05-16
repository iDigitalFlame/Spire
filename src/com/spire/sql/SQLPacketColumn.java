package com.spire.sql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.IOException;
import java.math.BigDecimal;

import com.spire.io.Encoder;
import com.spire.util.HashKey;
import com.spire.util.Stamp;
import com.spire.io.Streamable;

public class SQLPacketColumn extends SQLPacketParam implements HashKey<String>
{
	protected String columnName;
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		columnName = StorageEncoder.readString(InStream);
		super.readStorage(InStream, StorageEncoder);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeString(OutStream, columnName);
		super.writeStorage(OutStream, StorageEncoder);
	}
	
	public final String getKey()
	{
		return columnName;
	}

	protected SQLPacketColumn() { }
	protected SQLPacketColumn(String ColumnName, Object ColumnData)
	{
		columnName = ColumnName;
		if(ColumnData instanceof Boolean)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_BOOL;
		}
		else if(ColumnData instanceof Byte)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_BYTE;
		}
		else if(ColumnData instanceof byte[])
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_BYTES;
		}
		else if(ColumnData instanceof Double)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_DOUBLE;
		}
		else if(ColumnData instanceof Float)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_FLOAT;
		}
		else if(ColumnData instanceof Integer)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_INTEGER;
		}
		else if(ColumnData instanceof Long)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_LONG;
		}
		else if(ColumnData instanceof Short)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_SHORT;
		}
		else if(ColumnData instanceof Stamp)
		{
			paramData = ColumnData;
			paramType = SQLPacket.VALUE_DATE;
		}
		else if(ColumnData instanceof Date)
		{
			paramData = new Stamp((Date)ColumnData);
			paramType = SQLPacket.VALUE_DATE;
		}
		else if(ColumnData instanceof Time)
		{
			paramData = new Stamp((Time)ColumnData);
			paramType = SQLPacket.VALUE_TIME;
		}
		else if(ColumnData instanceof Timestamp)
		{
			paramData = new Stamp((Timestamp)ColumnData);
			paramType = SQLPacket.VALUE_TIMESTAMP;
		}
		else if(ColumnData instanceof String)
		{
			paramData = ((String)ColumnData).trim();
			paramType = SQLPacket.VALUE_STRING;
		}
		else if(ColumnData instanceof BigDecimal)
		{
			paramData = Double.valueOf(((BigDecimal)ColumnData).doubleValue());
			paramType = SQLPacket.VALUE_DOUBLE;
		}
		else
		{
			paramData = Short.valueOf((short)0);
			paramType = SQLPacket.VALUE_NULL;
		}
	}
}