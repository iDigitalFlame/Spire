package com.spire.util;

import java.sql.Time;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import java.io.IOException;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.io.Streamable;

public final class Stamp implements Storage
{
	private static final Calendar stampReceiver = Calendar.getInstance();
	private static final byte[] MONTHS = { 31, 27, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	private final byte[] stampData;
	
	private Date stampTempDate;
	
	public Stamp()
	{
		stampData = new byte[7];
		updateAllStamp(stampData);
	}
	public Stamp(long StampTime)
	{
		stampData = new byte[7];
		updateStamp(StampTime);
	}
	public Stamp(Date StampData)
	{
		stampData = new byte[7];
		updateStamp(StampData);
	}
	public Stamp(byte[] StampData)
	{
		stampData = new byte[7];
		updateStamp(StampData);
	}
		
	public final void updateStamp()
	{
		updateAllStamp(stampData);
	}
	public final void setDay(int DayIndex)
	{
		stampData[1] = (byte)Math.abs(DayIndex > 31 || DayIndex < 1 ? 1 : DayIndex);
	}
	public final void setYear(int YearIndex)
	{
		stampData[2] = (byte)Math.abs(YearIndex > 128 ? (2000 - YearIndex > 128 ? -128 : YearIndex - 2000) : YearIndex);
	}
	public final void setHour(int HourIndex)
	{
		stampData[3] = (byte)Math.abs(HourIndex > 23 || HourIndex < 0 ? 0 : HourIndex);
	}
	public final void addDays(int DaysToAdd)
	{
		short a = (short)(stampData[1] + DaysToAdd);
		while(a > MONTHS[stampData[0]] || (stampData[0] == 2 && a > (MONTHS[stampData[0]] + 1)))
		{
			if(stampData[0] == 2)
				a -= (MONTHS[stampData[0]] + 1);
			else
				a -= MONTHS[stampData[0]];
			addMonth(1);
		}
		stampData[1] = (byte)a;
	}
	public final void addYear(int YearsToAdd)
	{
		stampData[2] += YearsToAdd;
	}
	public final void setMonth(int MonthIndex)
	{
		stampData[0] = (byte)Math.abs(MonthIndex > 12 || MonthIndex < 1 ? 1 : MonthIndex);
	}
	public final void addMonth(int MonthsToAdd)
	{
		stampData[0] += MonthsToAdd;
		while(stampData[0] > 12)
		{
			stampData[0] -= 12;
			addYear(1);
		}
	}
	public final void setMinute(int MinuteIndex)
	{
		stampData[4] = (byte)Math.abs(MinuteIndex > 60 || MinuteIndex < 0 ? 0 : MinuteIndex);
	}
	public final void setSecond(int SecondIndex)
	{
		stampData[5] = (byte)Math.abs(SecondIndex > 60 || SecondIndex < 0 ? 0 : SecondIndex);
	}
	public final void updateStamp(long StampTime)
	{
		stampTempDate = null;
		updateStamp(new Date(StampTime));
	}
	public final void setDayOfWeek(int DayOfWeek)
	{
		stampData[6] = (byte)(DayOfWeek % 7);
	}
	@SuppressWarnings("deprecation")
	public final void updateStamp(Date StampData)
	{
		if(StampData == null)
		{
			updateStamp();
			return;
		}
		stampTempDate = StampData;
		setMonth(StampData.getMonth() + 1);
		setDay(StampData.getDate());
		setYear(StampData.getYear() - 100);
		setHour(StampData.getHours());
		setMinute(StampData.getMinutes());
		setSecond(StampData.getSeconds());
	}
	public final void updateStamp(Stamp StampData)
	{
		if(StampData == null) return;
		updateStamp(StampData.stampData);
	}
	public final void updateStamp(byte[] StampData)
	{
		if(StampData == null || StampData.length < 7) return;
		stampTempDate = null;
		stampData[0] = StampData[0];
		stampData[1] = StampData[1];
		stampData[2] = StampData[2];
		stampData[3] = StampData[3];
		stampData[4] = StampData[4];
		stampData[5] = StampData[5];
		stampData[6] = StampData[6];
	}
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.readByteArray(InStream, stampData);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeByteArray(OutStream, stampData);
	}
	
	public final boolean isTimeNewer(Stamp Stamp)
	{
		if(Stamp.stampData[2] > stampData[2]) return false;
		if(Stamp.stampData[0] > stampData[0]) return false;
		if(Stamp.stampData[1] > stampData[1]) return false;
		if(Stamp.stampData[3] > stampData[3]) return false;
		if(Stamp.stampData[4] > stampData[4]) return false;
		return Stamp.stampData[5] <= stampData[5];
	}
	public final boolean equals(Object CompareObject)
	{
		return stampData.equals(CompareObject instanceof Stamp ? ((Stamp)CompareObject).stampData : CompareObject);
	}
	
	public final byte getMonth()
	{
		return stampData[0];
	}
	public final byte getDay()
	{
		return stampData[1];
	}
	public final byte getYear()
	{
		return stampData[2];
	}
	public final byte getHour()
	{
		return stampData[3];
	}
	public final byte getMinute()
	{
		return stampData[4];
	}
	public final byte getSecond()
	{
		return stampData[5];
	}
	public final byte getDayOfWeek()
	{
		return stampData[6];
	}
	
	public final int hashCode()
	{
		return (stampData[0] + 1) *
			   (stampData[1] + 1) *
			   (stampData[2] + 1) *
			   (stampData[3] + 1) *
			   (stampData[4] + 1) *
			   (stampData[5] + 1) *
			   (stampData[6] + 1);
	}
	
	public final long getLongTime()
	{
		return getDate().getTime();
	}
	
 	public final String toString()
	{
		return stampData[0] + "/" + stampData[1] + "/" + (stampData[2] + 2000) + " " + stampData[3] + ":" + (stampData[4] < 10 ? expandString(2, Byte.valueOf(stampData[4])) : Byte.valueOf(stampData[4])) + ";" + expandString(2, Byte.valueOf(stampData[5]));
	}
 	public final String toStringStandard()
	{
		return stampData[0] + "/" + stampData[1] + "/" + (stampData[2] + 2000) + " " + (stampData[3] > 12 ? stampData[3] - 12 : stampData[3]) + ":" + expandString(2, Byte.valueOf(stampData[4])) + ";" + expandString(2, Byte.valueOf(stampData[5])) + (stampData[3] >= 12 ? " PM" : " AM");
	}
	
	public final byte[] getStamp()
	{
		return stampData;
	}
	
	@SuppressWarnings("deprecation")
	public final Date getDate()
	{
		if(stampTempDate == null)
		{
			stampTempDate = new Date(2000 + getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
		}
		return stampTempDate;
	}
	
	public final java.sql.Date getSQLDate()
	{
		return new java.sql.Date(getLongTime());
	}
	
	public final Time getTime()
	{
		return new Time(getLongTime());
	}
	
	public final Timestamp getTimestamp()
	{
		return new Timestamp(getLongTime());
	}
	
	public final Stamp clone()
	{
		Stamp a = new Stamp();
		a.updateStamp(stampData);
		return a;
	}
	
	private static final void updateAllStamp(byte[] StampArray)
	{
		stampReceiver.setTimeInMillis(System.currentTimeMillis());
		StampArray[0] = (byte)(1 + stampReceiver.get(Calendar.MONTH));
		StampArray[1] = (byte)stampReceiver.get(Calendar.DAY_OF_MONTH);
		StampArray[2] = (byte)(stampReceiver.get(Calendar.YEAR) - 2000);
		StampArray[3] = (byte)stampReceiver.get(Calendar.HOUR_OF_DAY);
		StampArray[4] = (byte)stampReceiver.get(Calendar.MINUTE);
		StampArray[5] = (byte)stampReceiver.get(Calendar.SECOND);
		StampArray[6] = (byte)stampReceiver.get(Calendar.DAY_OF_WEEK);
	}
	
	private static final String expandString(int Length, Object StringObject)
	{
		StringBuilder a = new StringBuilder(Length);
		for(int b = String.valueOf(StringObject).length(); b < Length; b++) a.append('0');
		return a.append(StringObject).toString();
	}
}