package com.spire.io;

import java.io.IOException;
import java.net.InetAddress;
import com.spire.util.Constants;
import com.spire.ex.SizeException;
import com.spire.ex.FormatException;

public final class DataObject extends Item
{
	public static final byte ITEM_CLASS_ID = 2;
	
	private Object dataValue;
	
	public DataObject()
	{
		super(ITEM_CLASS_ID);
		dataValue = null;
	}
	public DataObject(Item ItemValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = ItemValue;
	}
	public DataObject(long LongValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Long.valueOf(LongValue);
	}
	public DataObject(byte ByteValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Byte.valueOf(ByteValue);
	}
	public DataObject(char CharValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Character.valueOf(CharValue);
	}
	public DataObject(short ShortValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Short.valueOf(ShortValue);
	}
	public DataObject(int IntegerValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Integer.valueOf(IntegerValue);
	}
	public DataObject(float FloatValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Float.valueOf(FloatValue);
	}
	public DataObject(double DoubleValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Double.valueOf(DoubleValue);
	}
	public DataObject(String StringValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = StringValue;
	}
	public DataObject(boolean BooleanValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = Boolean.valueOf(BooleanValue);
	}
	public DataObject(Storage StorageValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = StorageValue;
	}
	public DataObject(InetAddress InetAddressValue)
	{
		super(ITEM_CLASS_ID);
		dataValue = InetAddressValue;
	}
	public DataObject(String StringValue, boolean Unicode)
	{
		super(ITEM_CLASS_ID);
		dataValue = Unicode ? StringValue : new UnicodeString(StringValue);
	}
	public DataObject(Item[] ItemArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(ItemArray != null && ItemArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("ItemArray", true, ItemArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = ItemArray;
	}
	public DataObject(long[] LongArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(LongArray != null && LongArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("LongArray", true, LongArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = LongArray;
	}
	public DataObject(byte[] ByteArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(ByteArray != null && ByteArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("ByteArray", true, ByteArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = ByteArray;
	}
	public DataObject(char[] CharArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(CharArray != null && CharArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("CharArray", true, CharArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = CharArray;
	}
	public DataObject(short[] ShortArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(ShortArray != null && ShortArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("ShortArray", true, ShortArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = ShortArray;
	}
	public DataObject(int[] IntegerArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(IntegerArray != null && IntegerArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("IntegerArray", true, IntegerArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = IntegerArray;
	}
	public DataObject(float[] FloatArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(FloatArray != null && FloatArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("FloatArray", true, FloatArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = FloatArray;
	}	
	public DataObject(double[] DoubleArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(DoubleArray != null && DoubleArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("DoubleArray", true, DoubleArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = DoubleArray;
	}
	public DataObject(String[] StringArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(StringArray != null && StringArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("StringArray", true, StringArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = StringArray;
	}
	public DataObject(boolean[] BooleanArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(BooleanArray != null && BooleanArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("BooleanArray", true, BooleanArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = BooleanArray;
	}
	public DataObject(Storage[] StorageArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(StorageArray != null && StorageArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("StorageArray", true, StorageArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = StorageArray;
	}
	public DataObject(InetAddress[] InetAddressArray) throws SizeException
	{
		super(ITEM_CLASS_ID);
		if(InetAddressArray != null && InetAddressArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("InetAddressArray", true, InetAddressArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = InetAddressArray;
	}
	
	public final void setDataValue(Item ItemValue)
	{
		dataValue = ItemValue;
	}
	public final void setDataValue(long LongValue)
	{
		dataValue = Long.valueOf(LongValue);
	}
	public final void setDataValue(byte ByteValue)
	{
		dataValue = Byte.valueOf(ByteValue);
	}
	public final void setDataValue(char CharValue)
	{
		dataValue = Character.valueOf(CharValue);
	}
	public final void setDataValue(short ShortValue)
	{
		dataValue = Short.valueOf(ShortValue);
	}
	public final void setDataValue(int IntegerValue)
	{
		dataValue = Integer.valueOf(IntegerValue);
	}
	public final void setDataValue(float FloatValue)
	{
		dataValue = Float.valueOf(FloatValue);
	}
	public final void setDataValue(double DoubleValue)
	{
		dataValue = Double.valueOf(DoubleValue);
	}
	public final void setDataValue(String StringValue)
	{
		dataValue = StringValue;
	}
	public final void setDataValue(boolean BooleanValue)
	{
		dataValue = Boolean.valueOf(BooleanValue);
	}
	public final void setDataValue(Storage StorageValue)
	{
		dataValue = StorageValue;
	}
	public final void setDataValue(InetAddress InetAddressValue)
	{
		dataValue = InetAddressValue;
	}
	public final void setDataValue(String StringValue, boolean Unicode)
	{
		dataValue = Unicode ? StringValue : new UnicodeString(StringValue);
	}
	public final void setDataValue(Item[] ItemArray) throws SizeException
	{
		if(ItemArray != null && ItemArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("ItemArray", true, ItemArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = ItemArray;
	}
	public final void setDataValue(long[] LongArray) throws SizeException
	{
		if(LongArray != null && LongArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("LongArray", true, LongArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = LongArray;
	}
	public final void setDataValue(byte[] ByteArray) throws SizeException
	{
		if(ByteArray != null && ByteArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("ByteArray", true, ByteArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = ByteArray;
	}
	public final void setDataValue(char[] CharArray) throws SizeException
	{
		if(CharArray != null && CharArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("CharArray", true, CharArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = CharArray;
	}
	public final void setDataValue(short[] ShortArray) throws SizeException
	{
		if(ShortArray != null && ShortArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("ShortArray", true, ShortArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = ShortArray;
	}
	public final void setDataValue(int[] IntegerArray) throws SizeException
	{
		if(IntegerArray != null && IntegerArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("IntegerArray", true, IntegerArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = IntegerArray;
	}
	public final void setDataValue(float[] FloatArray) throws SizeException
	{
		if(FloatArray != null && FloatArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("FloatArray", true, FloatArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = FloatArray;
	}	
	public final void setDataValue(double[] DoubleArray) throws SizeException
	{
		if(DoubleArray != null && DoubleArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("DoubleArray", true, DoubleArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = DoubleArray;
	}
	public final void setDataValue(String[] StringArray) throws SizeException
	{
		if(StringArray != null && StringArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("StringArray", true, StringArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = StringArray;
	}
	public final void setDataValue(boolean[] BooleanArray) throws SizeException
	{
		if(BooleanArray != null && BooleanArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("BooleanArray", true, BooleanArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = BooleanArray;
	}
	public final void setDataValue(Storage[] StorageArray) throws SizeException
	{
		if(StorageArray != null && StorageArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("StorageArray", true, StorageArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = StorageArray;
	}
	public final void setDataValue(InetAddress[] InetAddressArray) throws SizeException
	{
		if(InetAddressArray != null && InetAddressArray.length > Constants.MAX_USHORT_SIZE)
			throw new SizeException("InetAddressArray", true, InetAddressArray.length, Constants.MAX_USHORT_SIZE);
		dataValue = InetAddressArray;
	}
	public final void setDataValue(Object DataValue) throws FormatException, SizeException
	{
		if(DataValue == null) dataValue = null;
		else if(DataValue instanceof Byte) setDataValue(((Byte)DataValue).byteValue());
		else if(DataValue instanceof Short) setDataValue(((Short)DataValue).shortValue());
		else if(DataValue instanceof Integer) setDataValue(((Integer)DataValue).intValue());
		else if(DataValue instanceof Float) setDataValue(((Float)DataValue).floatValue());
		else if(DataValue instanceof Long) setDataValue(((Long)DataValue).longValue());
		else if(DataValue instanceof Double) setDataValue(((Double)DataValue).doubleValue());
		else if(DataValue instanceof String) setDataValue((String)DataValue);
		else if(DataValue instanceof Boolean) setDataValue(((Boolean)DataValue).booleanValue());
		else if(DataValue instanceof Character) setDataValue(((Character)DataValue).charValue());
		else if(DataValue instanceof Item && !(DataValue instanceof DataObject)) setDataValue((Item)DataValue);
		else if(DataValue instanceof Storage) setDataValue((Storage)DataValue);
		else if(DataValue instanceof InetAddress) setDataValue((InetAddress)DataValue);
		else if(DataValue instanceof InetAddress[]) setDataValue((InetAddress[])DataValue);
		else if(DataValue instanceof byte[]) setDataValue((byte[])DataValue);
		else if(DataValue instanceof short[]) setDataValue((short[])DataValue);
		else if(DataValue instanceof int[]) setDataValue((int[])DataValue);
		else if(DataValue instanceof float[]) setDataValue((float[])DataValue);
		else if(DataValue instanceof long[]) setDataValue((long[])DataValue);
		else if(DataValue instanceof double[]) setDataValue((double[])DataValue);
		else if(DataValue instanceof String[]) setDataValue((String[])DataValue);
		else if(DataValue instanceof boolean[]) setDataValue((boolean[])DataValue);
		else if(DataValue instanceof char[]) setDataValue((char[])DataValue);
		else if(DataValue instanceof Item[]) setDataValue((Item[])DataValue);
		else if(DataValue instanceof Storage[])  setDataValue((Storage[])DataValue);
		throw new FormatException("This is not an Object type that DataObject supports!");
	}
	
	public final boolean getBoolean()
	{
		return dataValue instanceof Boolean ? ((Boolean)dataValue).booleanValue() : false;
	}
	public final boolean equals(Object CompareObject)
	{
		return (dataValue != null && dataValue.equals(CompareObject)) || (dataValue == null && CompareObject == null);
	}
	public final boolean canBeCastBy(Class<?> CompareClass)
	{
		return CompareClass != null && getValueClass() != null && getValueClass().isAssignableFrom(CompareClass);
	}
	
	public final byte getByte()
	{
		return dataValue instanceof Byte ? ((Byte)dataValue).byteValue() : 0;
	}
	
	public final char getChar()
	{
		return dataValue instanceof Character ? ((Character)dataValue).charValue() : (char)0;
	}
	
	public final short getShort()
	{
		return dataValue instanceof Short ? ((Short)dataValue).shortValue() : 0;
	}
	
	public final int hashCode()
	{
		return dataValue != null ? (int)Math.pow(dataValue.hashCode(), 2) : -1;
	}
	public final int getInteger()
	{
		return dataValue instanceof Integer ? ((Integer)dataValue).intValue() : 0;
	}

	public final float getFloat()
	{
		return dataValue instanceof Float ? ((Float)dataValue).floatValue() : 0F;
	}
	
	public final long getLong()
	{
		return dataValue instanceof Long ? ((Long)dataValue).longValue() : 0L;
	}
	
	public final double getDouble()
	{
		return dataValue instanceof Double ? ((Double)dataValue).doubleValue() : 0D;
	}
	
	public final String toString()
	{
		return "DataObject(" + getItemID() + ") " + (dataValue != null ? dataValue : ".");
	}
	public final String getString()
	{
		return dataValue instanceof String ? (String)dataValue : dataValue instanceof UnicodeString ? ((UnicodeString)dataValue).stringData : null;
	}
	
	public final InetAddress getInetAddress()
	{
		return dataValue instanceof InetAddress ? (InetAddress)dataValue : null;
	}

	public final Class<?> getValueClass()
	{
		return dataValue != null ? dataValue instanceof UnicodeString ? ((UnicodeString)dataValue).stringData.getClass() : dataValue.getClass() : null;
	}
	
	public final Object getObject()
	{
		return dataValue;
	}
	
	public final Item getItem()
	{
		return dataValue instanceof Item ? (Item)dataValue : null;
	}
	
	public final Storage getStorage()
	{
		return dataValue instanceof Storage ? (Storage)dataValue : null;
	}
	
	public final boolean[] getBooleanArray()
	{
		return dataValue instanceof boolean[] ? (boolean[])dataValue : null;
	}
	
	public final byte[] getByteArray()
	{
		return dataValue instanceof byte[] ? (byte[])dataValue : null;
	}
	
	public final char[] getCharArray()
	{
		return dataValue instanceof char[] ? (char[])dataValue : null;
	}
	
	public final short[] getShortArray()
	{
		return dataValue instanceof short[] ? (short[])dataValue : null;
	}
	
	public final int[] getIntegerArray()
	{
		return dataValue instanceof int[] ? (int[])dataValue : null;
	}
	
	public final float[] getFloatArray()
	{
		return dataValue instanceof float[] ? (float[])dataValue : null;
	}
	
	public final long[] getLongArray()
	{
		return dataValue instanceof long[] ? (long[])dataValue : null;
	}
	
	public final double[] getDoubleArray()
	{
		return dataValue instanceof double[] ? (double[])dataValue : null;
	}
	
	public final String[] getStringArray()
	{
		return dataValue instanceof String[] ? (String[])dataValue : null;
	}
	
	public final InetAddress[] getInetAddressArray()
	{
		return dataValue instanceof InetAddress[] ? (InetAddress[])dataValue : null;
	}
	
	public final Item[] getItemArray()
	{
		return dataValue instanceof Item[] ? (Item[])dataValue : null;
	}
	
	public static final boolean supportsObjectType(Object DataValue)
	{
		if(DataValue instanceof Byte) return true;
		if(DataValue instanceof Short) return true;
		if(DataValue instanceof Integer) return true;
		if(DataValue instanceof Float) return true;
		if(DataValue instanceof Long) return true;
		if(DataValue instanceof Double) return true;
		if(DataValue instanceof String) return true;
		if(DataValue instanceof Boolean) return true;
		if(DataValue instanceof Character) return true;
		if(DataValue instanceof Item && !(DataValue instanceof DataObject)) return true;
		if(DataValue instanceof Storage) return true;
		if(DataValue instanceof InetAddress) return true;
		if(DataValue instanceof InetAddress[]) return true;
		if(DataValue instanceof byte[]) return true;
		if(DataValue instanceof short[]) return true;
		if(DataValue instanceof int[]) return true;
		if(DataValue instanceof float[]) return true;
		if(DataValue instanceof long[]) return true;
		if(DataValue instanceof double[]) return true;
		if(DataValue instanceof String[]) return true;
		if(DataValue instanceof boolean[]) return true;
		if(DataValue instanceof char[]) return true;
		if(DataValue instanceof Item[]) return true;
		if(DataValue instanceof Storage[])  return true;
		return DataValue == null;
	}
	
	public static final DataObject createFromObject(Object DataValue) throws FormatException, SizeException
	{
		if(DataValue == null) return new DataObject();
		if(DataValue instanceof Byte) return new DataObject(((Byte)DataValue).byteValue());
		if(DataValue instanceof Short) return new DataObject(((Short)DataValue).shortValue());
		if(DataValue instanceof Integer) return new DataObject(((Integer)DataValue).intValue());
		if(DataValue instanceof Float) return new DataObject(((Float)DataValue).floatValue());
		if(DataValue instanceof Long) return new DataObject(((Long)DataValue).longValue());
		if(DataValue instanceof Double) return new DataObject(((Double)DataValue).doubleValue());
		if(DataValue instanceof String) return new DataObject((String)DataValue);
		if(DataValue instanceof Boolean) return new DataObject(((Boolean)DataValue).booleanValue());
		if(DataValue instanceof Character) return new DataObject(((Character)DataValue).charValue());
		if(DataValue instanceof Item && !(DataValue instanceof DataObject)) return new DataObject((Item)DataValue);
		if(DataValue instanceof Storage) return new DataObject((Storage)DataValue);
		if(DataValue instanceof InetAddress) return new DataObject((InetAddress)DataValue);
		if(DataValue instanceof InetAddress[]) return new DataObject((InetAddress[])DataValue);
		if(DataValue instanceof byte[]) return new DataObject((byte[])DataValue);
		if(DataValue instanceof short[]) return new DataObject((short[])DataValue);
		if(DataValue instanceof int[]) return new DataObject((int[])DataValue);
		if(DataValue instanceof float[]) return new DataObject((float[])DataValue);
		if(DataValue instanceof long[]) return new DataObject((long[])DataValue);
		if(DataValue instanceof double[]) return new DataObject((double[])DataValue);
		if(DataValue instanceof String[]) return new DataObject((String[])DataValue);
		if(DataValue instanceof boolean[]) return new DataObject((boolean[])DataValue);
		if(DataValue instanceof char[]) return new DataObject((char[])DataValue);
		if(DataValue instanceof Item[]) return new DataObject((Item[])DataValue);
		if(DataValue instanceof Storage[])  return new DataObject((Storage[])DataValue);
		throw new FormatException("This is not an Object type that DataObject supports!");
	}
	
	protected final void readItemFailure()
	{
		dataValue = null;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		byte a = itemEncoder.readByte(InStream);
		switch(a)
		{
		case 0:
			dataValue = null;
			break;
		case 1:
			dataValue = Byte.valueOf(itemEncoder.readByte(InStream));
			break;
		case 2:
			dataValue = Short.valueOf(itemEncoder.readShort(InStream));
			break;
		case 3:
			dataValue = Integer.valueOf(itemEncoder.readInteger(InStream));
			break;
		case 4:
			dataValue = Float.valueOf(itemEncoder.readFloat(InStream));
			break;
		case 5:
			dataValue = Long.valueOf(itemEncoder.readLong(InStream));
			break;
		case 6:
			dataValue = Double.valueOf(itemEncoder.readDouble(InStream));
			break;
		case 7:
			dataValue = itemEncoder.readString(InStream);
			break;
		case 8:
			dataValue = new UnicodeString(itemEncoder.readString(InStream));
			break;
		case 9:
			dataValue = Boolean.valueOf(itemEncoder.readBoolean(InStream));
			break;
		case 10:
			dataValue = Character.valueOf(itemEncoder.readChar(InStream));
			break;
		case 11:
			dataValue = Item.getNextItem(InStream);
			break;
		case 12:
			dataValue = itemEncoder.readStorage(InStream);
			break;
		case 13:
			dataValue = itemEncoder.readByteArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 14:
			dataValue = itemEncoder.readShortArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 15:
			dataValue = itemEncoder.readIntegerArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 16:
			dataValue = itemEncoder.readFloatArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 17:
			dataValue = itemEncoder.readLongArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 18:
			dataValue = itemEncoder.readDoubleArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 19:
			dataValue = itemEncoder.readStringArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 20:
			int b = itemEncoder.readUnsignedShort(InStream);
			boolean[] c = new boolean[b];
			for(; b > 0; b--) c[c.length - b] = itemEncoder.readBoolean(InStream);
			dataValue = c;
			break;
		case 21:
			int d = itemEncoder.readUnsignedShort(InStream);
			char[] e = new char[d];
			for(; d > 0; d--) e[e.length - d] = itemEncoder.readChar(InStream);
			dataValue = e;
			break;
		case 22:
			int f = itemEncoder.readUnsignedShort(InStream);
			Item[] g = new Item[f];
			for(; f > 0; f--) g[g.length - f] = Item.getNextItem(InStream);
			dataValue = g;
			break;
		case 23:
			dataValue = itemEncoder.readStorageArray(InStream, itemEncoder.readUnsignedShort(InStream));
			break;
		case 24:
			dataValue = Stream.readInetAddress(itemEncoder, InStream);
			break;
		case 25:
			int h = itemEncoder.readUnsignedShort(InStream);
			InetAddress[] i = new InetAddress[h];
			for(; h > 0; h--)
			{
				i[i.length - h] = Stream.readInetAddress(itemEncoder, InStream);
			}
			dataValue = i;
			break;
		}
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		if(dataValue == null || dataValue instanceof DataObject)
			itemEncoder.writeByte(OutStream, 0);
		else if(dataValue instanceof Byte)
		{
			itemEncoder.writeByte(OutStream, 1);
			itemEncoder.writeByte(OutStream, ((Byte)dataValue).byteValue());
		}
		else if(dataValue instanceof Short)
		{
			itemEncoder.writeByte(OutStream, 2);
			itemEncoder.writeShort(OutStream, ((Short)dataValue).shortValue());
		}
		else if(dataValue instanceof Integer)
		{
			itemEncoder.writeByte(OutStream, 3);
			itemEncoder.writeInteger(OutStream, ((Integer)dataValue).intValue());
		}
		else if(dataValue instanceof Float)
		{
			itemEncoder.writeByte(OutStream, 4);
			itemEncoder.writeFloat(OutStream, ((Float)dataValue).floatValue());
		}
		else if(dataValue instanceof Long)
		{
			itemEncoder.writeByte(OutStream, 5);
			itemEncoder.writeLong(OutStream, ((Long)dataValue).longValue());
		}
		else if(dataValue instanceof Double)
		{
			itemEncoder.writeByte(OutStream, 6);
			itemEncoder.writeDouble(OutStream, ((Double)dataValue).doubleValue());
		}
		else if(dataValue instanceof String)
		{
			itemEncoder.writeByte(OutStream, 7);
			itemEncoder.writeString(OutStream, (String)dataValue);
		}
		else if(dataValue instanceof UnicodeString)
		{
			itemEncoder.writeByte(OutStream, 8);
			itemEncoder.writeUnicodeString(OutStream, ((UnicodeString)dataValue).stringData);
		}
		else if(dataValue instanceof Boolean)
		{
			itemEncoder.writeByte(OutStream, 9);
			itemEncoder.writeBoolean(OutStream, ((Boolean)dataValue).booleanValue());
		}
		else if(dataValue instanceof Character)
		{
			itemEncoder.writeByte(OutStream, 10);
			itemEncoder.writeChar(OutStream, ((Character)dataValue).charValue());
		}
		else if(dataValue instanceof Item && !(dataValue instanceof DataObject))
		{
			itemEncoder.writeByte(OutStream, 11);
			((Item)dataValue).writeStream(OutStream);
		}
		else if(dataValue instanceof Storage)
		{
			itemEncoder.writeByte(OutStream, 12);
			itemEncoder.writeStorage(OutStream, (Storage)dataValue);
		}
		else if(dataValue instanceof byte[])
		{
			itemEncoder.writeByte(OutStream, 13);
			byte[] a = (byte[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeByteArray(OutStream, a);
		}
		else if(dataValue instanceof short[])
		{
			itemEncoder.writeByte(OutStream, 14);
			short[] a = (short[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeShortArray(OutStream, a);
		}
		else if(dataValue instanceof int[])
		{
			itemEncoder.writeByte(OutStream, 15);
			int[] a = (int[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeIntegerArray(OutStream, a);
		}
		else if(dataValue instanceof float[])
		{
			itemEncoder.writeByte(OutStream, 16);
			float[] a = (float[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeFloatArray(OutStream, a);
		}
		else if(dataValue instanceof long[])
		{
			itemEncoder.writeByte(OutStream, 17);
			long[] a = (long[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeLongArray(OutStream, a);
		}
		else if(dataValue instanceof double[])
		{
			itemEncoder.writeByte(OutStream, 18);
			double[] a = (double[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeDoubleArray(OutStream, a);
		}
		else if(dataValue instanceof String[])
		{
			itemEncoder.writeByte(OutStream, 19);
			String[] a = (String[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			itemEncoder.writeStringArray(OutStream, a);
		}
		else if(dataValue instanceof boolean[])
		{
			itemEncoder.writeByte(OutStream, 20);
			boolean[] a = (boolean[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			for(short b = 0; b < a.length; b++) itemEncoder.writeBoolean(OutStream, a[b]);
		}
		else if(dataValue instanceof char[])
		{
			itemEncoder.writeByte(OutStream, 21);
			char[] a = (char[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			for(short b = 0; b < a.length; b++) itemEncoder.writeChar(OutStream, a[b]);
		}
		else if(dataValue instanceof Item[])
		{
			itemEncoder.writeByte(OutStream, 22);
			Item[] a = (Item[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			for(short b = 0; b < a.length; b++) if(a[b] instanceof DataObject)
				new DataObject().writeStream(OutStream);
			else a[b].writeStream(OutStream);
		}
		else if(dataValue instanceof Storage[])
		{
			itemEncoder.writeByte(OutStream, 23);
			Storage[] a = (Storage[])dataValue;
			itemEncoder.writeStorageArray(OutStream, a);
		}
		else if(dataValue instanceof InetAddress)
		{
			itemEncoder.writeByte(OutStream, 24);
			Stream.writeInetAddress(itemEncoder, OutStream, (InetAddress)dataValue);
		}
		else if(dataValue instanceof InetAddress[])
		{
			itemEncoder.writeByte(OutStream, 25);
			InetAddress[] a = (InetAddress[])dataValue;
			itemEncoder.writeShort(OutStream, (short)a.length);
			for(short b = 0; b < a.length; b++)
				Stream.writeInetAddress(itemEncoder, OutStream, a[b]);
		}
		else itemEncoder.writeByte(OutStream, (byte)0);
	}

	private static final class UnicodeString
	{
		private final String stringData;
		
		private UnicodeString(String StringValue)
		{
			stringData = StringValue;
		}
	}
}