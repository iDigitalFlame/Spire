package com.spire.sql;

import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.util.Stamp;
import com.spire.io.Streamable;

public class SQLPacketParam implements Storage
{
	protected byte paramType;
	protected Object paramData;
	
	public void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		paramType = StorageEncoder.readByte(InStream);
		switch(paramType)
		{
		case SQLPacket.VALUE_NULL:
			paramData = Short.valueOf(StorageEncoder.readShort(InStream));
			break;
		case SQLPacket.VALUE_BYTE:
			paramData = Byte.valueOf(StorageEncoder.readByte(InStream));
			break;
		case SQLPacket.VALUE_BOOL:
			paramData = Boolean.valueOf(StorageEncoder.readBoolean(InStream));
			break;
		case SQLPacket.VALUE_DATE:
			paramData = new Stamp();
			((Stamp)paramData).readStorage(InStream, StorageEncoder);
			break;
		case SQLPacket.VALUE_BYTES:
			byte[] a = new byte[StorageEncoder.readInteger(InStream)];
			StorageEncoder.readByteArray(InStream, a);
			paramData = a;
			break;
		case SQLPacket.VALUE_FLOAT:
			paramData = Float.valueOf(StorageEncoder.readFloat(InStream));
			break;
		case SQLPacket.VALUE_LONG:
			paramData = Long.valueOf(StorageEncoder.readLong(InStream));
			break;
		case SQLPacket.VALUE_DOUBLE:
			paramData = Double.valueOf(StorageEncoder.readDouble(InStream));
			break;
		case SQLPacket.VALUE_INTEGER:
			paramData = Integer.valueOf(StorageEncoder.readInteger(InStream));
			break;
		case SQLPacket.VALUE_STRING:
			paramData = StorageEncoder.readString(InStream);
			break;
		case SQLPacket.VALUE_NSTRING:
			paramData = StorageEncoder.readString(InStream);
			break;
		case SQLPacket.VALUE_SHORT:
			paramData = Short.valueOf(StorageEncoder.readShort(InStream));
			break;
		case SQLPacket.VALUE_TIME:
			paramData = new Stamp();
			((Stamp)paramData).readStorage(InStream, StorageEncoder);
			break;
		case SQLPacket.VALUE_TIMESTAMP:
			paramData = new Stamp();
			((Stamp)paramData).readStorage(InStream, StorageEncoder);
			break;
		default:
			break;
		}
	}
	public void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByte(OutStream, paramType);
		switch(paramType)
		{
		case SQLPacket.VALUE_NULL:
			StorageEncoder.writeShort(OutStream, ((Short)paramData).shortValue());
			break;
		case SQLPacket.VALUE_BYTE:
			StorageEncoder.writeByte(OutStream, ((Byte)paramData).byteValue());
			break;
		case SQLPacket.VALUE_BOOL:
			StorageEncoder.writeBoolean(OutStream, ((Boolean)paramData).booleanValue());
			break;
		case SQLPacket.VALUE_DATE:
			((Stamp)paramData).writeStorage(OutStream, StorageEncoder);
			break;
		case SQLPacket.VALUE_BYTES:
			byte[] a = (byte[])paramData;
			StorageEncoder.writeInteger(OutStream, a.length);
			StorageEncoder.writeByteArray(OutStream, a);
			break;
		case SQLPacket.VALUE_FLOAT:
			StorageEncoder.writeFloat(OutStream, ((Float)paramData).floatValue());
			break;
		case SQLPacket.VALUE_LONG:
			StorageEncoder.writeLong(OutStream, ((Long)paramData).longValue());
			break;
		case SQLPacket.VALUE_DOUBLE:
			StorageEncoder.writeDouble(OutStream, ((Double)paramData).doubleValue());
			break;
		case SQLPacket.VALUE_INTEGER:
			StorageEncoder.writeInteger(OutStream, ((Integer)paramData).intValue());
			break;
		case SQLPacket.VALUE_STRING:
			StorageEncoder.writeString(OutStream, (String)paramData);
			break;
		case SQLPacket.VALUE_NSTRING:
			StorageEncoder.writeString(OutStream, (String)paramData);
			break;
		case SQLPacket.VALUE_SHORT:
			StorageEncoder.writeShort(OutStream, ((Short)paramData).shortValue());
			break;
		case SQLPacket.VALUE_TIME:
			((Stamp)paramData).writeStorage(OutStream, StorageEncoder);
			break;
		case SQLPacket.VALUE_TIMESTAMP:
			((Stamp)paramData).writeStorage(OutStream, StorageEncoder);
			break;
		default:
			break;
		}
	}

	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof SQLPacketParam && ((SQLPacketParam)CompareObject).paramData == paramData && ((SQLPacketParam)CompareObject).paramData.equals(paramData);
	}
	
	public final int hashCode()
	{
		return paramType + (paramData != null ? paramData.hashCode() : 0);
	}
	
	public final String toString()
	{
		return "SQLPacketParam(S) T:" + paramType + ", D(" + paramData.toString() + ")";
	}
	
	protected SQLPacketParam() { }
	protected SQLPacketParam(byte ParamType, long LongValue)
	{
		paramType = ParamType;
		paramData = Long.valueOf(LongValue);
	}
	protected SQLPacketParam(byte ParamType, byte ByteValue)
	{
		paramType = ParamType;
		paramData = Byte.valueOf(ByteValue);
	}
	protected SQLPacketParam(byte ParamType, Stamp StampValue)
	{
		paramType = ParamType;
		paramData = StampValue;
	}
	protected SQLPacketParam(byte ParamType, int IntegerValue)
	{
		paramType = ParamType;
		paramData = Integer.valueOf(IntegerValue);
	}
	protected SQLPacketParam(byte ParamType, short ShortValue)
	{
		paramType = ParamType;
		paramData = Short.valueOf(ShortValue);
	}
	protected SQLPacketParam(byte ParamType, float FloatValue)
	{
		paramType = ParamType;
		paramData = Float.valueOf(FloatValue);
	}
	protected SQLPacketParam(byte ParamType, byte[] ByteArray)
	{
		paramType = ParamType;
		paramData = ByteArray;
	}
	protected SQLPacketParam(byte ParamType, boolean BoolValue)
	{
		paramType = ParamType;
		paramData = Boolean.valueOf(BoolValue);
	}
	protected SQLPacketParam(byte ParamType, double DoubleValue)
	{
		paramType = ParamType;
		paramData = Double.valueOf(DoubleValue);
	}
	protected SQLPacketParam(byte ParamType, String StringValue)
	{
		paramType = ParamType;
		paramData = StringValue;
	}
	
	protected final void addToCommand(int ParamIndex, SQLCommand CommandInstance) throws IOException
	{
		try
		{
			switch(paramType)
			{
			case SQLPacket.VALUE_NULL:
				CommandInstance.setNull(ParamIndex, ((Short)paramData).shortValue());
				break;
			case SQLPacket.VALUE_BYTE:
				CommandInstance.setByte(ParamIndex, ((Byte)paramData).byteValue());
				break;
			case SQLPacket.VALUE_BOOL:
				CommandInstance.setBoolean(ParamIndex, ((Boolean)paramData).booleanValue());
				break;
			case SQLPacket.VALUE_DATE:
				CommandInstance.setDate(ParamIndex, (Stamp)paramData);
				break;
			case SQLPacket.VALUE_BYTES:
				CommandInstance.setBytes(ParamIndex, (byte[])paramData);
				break;
			case SQLPacket.VALUE_FLOAT:
				CommandInstance.setFloat(ParamIndex, ((Float)paramData).floatValue());
				break;
			case SQLPacket.VALUE_LONG:
				CommandInstance.setLong(ParamIndex, ((Long)paramData).longValue());
				break;
			case SQLPacket.VALUE_DOUBLE:
				CommandInstance.setDouble(ParamIndex, ((Double)paramData).doubleValue());
				break;
			case SQLPacket.VALUE_INTEGER:
				CommandInstance.setInteger(ParamIndex, ((Integer)paramData).intValue());
				break;
			case SQLPacket.VALUE_STRING:
				CommandInstance.setString(ParamIndex, (String)paramData);
				break;
			case SQLPacket.VALUE_NSTRING:
				CommandInstance.setNString(ParamIndex, (String)paramData);
				break;
			case SQLPacket.VALUE_SHORT:
				CommandInstance.setShort(ParamIndex, ((Short)paramData).shortValue());
				break;
			case SQLPacket.VALUE_TIME:
				CommandInstance.setTime(ParamIndex, (Stamp)paramData);
				break;
			case SQLPacket.VALUE_TIMESTAMP:
				CommandInstance.setTimestamp(ParamIndex, (Stamp)paramData);
				break;
			default:
				break;
			}
		}
		catch (ClassCastException Exception) { }
	}

	protected final SQLPacketParam clone()
	{
		SQLPacketParam a = new SQLPacketParam(paramType, (String)null);
		a.paramData = paramData;
		return a;
	}
}