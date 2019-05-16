package com.spire.sql;

import java.sql.Time;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.io.Reader;
import java.sql.NClob;
import java.sql.SQLXML;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import com.spire.util.Stamp;
import java.sql.SQLException;
import com.spire.log.Reporter;
import com.spire.io.Streamable;
import com.spire.ex.NullException;
import java.sql.PreparedStatement;
import com.spire.ex.CloneException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

public final class SQLCommand
{
	private final DataConnection cmdConnection;
	private final PreparedStatement cmdCommand;

	public SQLCommand(String SQLQuery, DataConnection Connection) throws NullException, StringException, IOException
	{
		if(SQLQuery == null) throw new NullException("SQLQuery");
		if(SQLQuery.isEmpty()) throw new StringException("SQLQuery");
		if(Connection == null) throw new NullException("Connection");
		cmdConnection = Connection;
		try
		{
			cmdCommand = cmdConnection.connectionInstance.prepareStatement(SQLQuery);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}

	public final void close() throws IOException
	{	
		try
		{
			cmdCommand.close();
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void clearParameters() throws IOException
	{
		try
		{
			cmdCommand.clearParameters();
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void closeOnCompletion() throws IOException
	{	
		try
		{
			cmdCommand.closeOnCompletion();
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setEscapeProcessing(boolean EscapeProcessing) throws IOException
	{	
		try
		{
			cmdCommand.setEscapeProcessing(EscapeProcessing);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setQueryTimeout(int QueryTimeout) throws IOException, NumberException
	{
		if(QueryTimeout < 0) throw new NumberException("QueryTimeout", QueryTimeout, false);
		try
		{
			cmdCommand.setQueryTimeout(QueryTimeout);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}	
	public final void setNull(int ParameterIndex, int NullType) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setNull(ParameterIndex + 1, NullType);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBlob(int ParameterIndex, Blob BlobValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setBlob(ParameterIndex + 1, BlobValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setByte(int ParameterIndex, byte ByteValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setByte(ParameterIndex + 1, ByteValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setClob(int ParameterIndex, Clob ClobValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setClob(ParameterIndex + 1, ClobValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setDate(int ParameterIndex, Date DateValue) throws IOException, NumberException
	{		
		setDate(ParameterIndex, DateValue, null);
	}
	public final void setLong(int ParameterIndex, long LongValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setLong(ParameterIndex + 1, LongValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setTime(int ParameterIndex, Time TimeValue) throws IOException, NumberException
	{
		setTime(ParameterIndex, TimeValue, null);
	}
	public final void setTime(int ParameterIndex, Stamp TimeValue) throws IOException, NumberException
	{
		setTime(ParameterIndex, TimeValue, null);
	}
	public final void setDate(int ParameterIndex, Stamp DateValue) throws IOException, NumberException
	{		
		setDate(ParameterIndex, DateValue, null);
	}
	public final void setClob(int ParameterIndex, Reader ClobReader) throws IOException, NumberException
	{
		setClob(ParameterIndex, ClobReader, -1L);
	}
	public final void setBytes(int ParameterIndex, byte[] ByteArray) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setBytes(ParameterIndex + 1, ByteArray);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setFloat(int ParameterIndex, float FloatValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setFloat(ParameterIndex + 1, FloatValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setNClob(int ParameterIndex, NClob NClobValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setNClob(ParameterIndex + 1, NClobValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}	
	public final void setSQLXML(int ParameterIndex, SQLXML XMLValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setSQLXML(ParameterIndex + 1, XMLValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setShort(int ParameterIndex, short ShortValue) throws IOException, NumberException
	{	
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setShort(ParameterIndex + 1, ShortValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setNClob(int ParameterIndex, Reader NClobReader) throws IOException, NumberException
	{	
		setNClob(ParameterIndex, NClobReader, -1L);
	}
	public final void setInteger(int ParameterIndex, int IntegerValue) throws IOException, NumberException
	{	
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setInt(ParameterIndex + 1, IntegerValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBoolean(int ParameterIndex, boolean BoolValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setBoolean(ParameterIndex + 1, BoolValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setDouble(int ParameterIndex, double DoubleValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setDouble(ParameterIndex + 1, DoubleValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}	
	}
	public final void setString(int ParameterIndex, String StringValue) throws IOException, NumberException
	{	
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setString(ParameterIndex + 1, StringValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setObject(int ParameterIndex, Object ObjectValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setObject(ParameterIndex + 1, ObjectValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setNString(int ParameterIndex, String StringValue) throws IOException, NumberException
	{	
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setNString(ParameterIndex + 1, StringValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setTimestamp(int ParameterIndex, Stamp TimeStampValue) throws IOException, NumberException
	{
		setTimestamp(ParameterIndex, TimeStampValue, null);
	}
	public final void setBlob(int ParameterIndex, Streamable BlobInputStream) throws IOException, NumberException
	{
		setBlob(ParameterIndex, BlobInputStream, -1L);
	}
	public final void setBlob(int ParameterIndex, InputStream BlobInputStream) throws IOException, NumberException
	{
		setBlob(ParameterIndex, BlobInputStream, -1L);
	}
	public final void setTimestamp(int ParameterIndex, Timestamp TimeStampValue) throws IOException, NumberException
	{
		setTimestamp(ParameterIndex, TimeStampValue, null);
	}
	public final void setAsciiStream(int ParameterIndex, Streamable AsciiStream) throws IOException, NumberException
	{
		setAsciiStream(ParameterIndex, AsciiStream, -1L);
	}
	public final void setAsciiStream(int ParameterIndex, InputStream AsciiStream) throws IOException, NumberException
	{
		setAsciiStream(ParameterIndex, AsciiStream, -1L);
	}
	public final void setBinaryStream(int ParameterIndex, Streamable BinaryStream) throws IOException, NumberException
	{
		setBinaryStream(ParameterIndex, BinaryStream, -1L);	
	}
	public final void setBigDecimal(int ParameterIndex, BigDecimal BigDecimalValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setBigDecimal(ParameterIndex + 1, BigDecimalValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBinaryStream(int ParameterIndex, InputStream BinaryStream) throws IOException, NumberException
	{
		setBinaryStream(ParameterIndex, BinaryStream, -1L);
	}
	public final void setCharacterStream(int ParameterIndex, Reader CharacterReader) throws IOException, NumberException
	{
		setCharacterStream(ParameterIndex, CharacterReader, -1L);
	}
	public final void setNull(int ParameterIndex, int NullType, String NullTypeName) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setNull(ParameterIndex + 1, NullType, NullTypeName);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setClob(int ParameterIndex, Reader ClobReader, long ClobLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ClobLength < 0 && ClobLength != -1) throw new NumberException("ClobLength", (int)ClobLength, false);
		try
		{
			cmdCommand.setClob(ParameterIndex + 1, ClobReader, ClobLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setNCharacterStream(int ParameterIndex, Reader CharacterReader) throws IOException, NumberException
	{	
		setNCharacterStream(ParameterIndex, CharacterReader, -1L);
	}
	public final void setObject(int ParameterIndex, Object ObjectValue, int ObjectType) throws IOException, NumberException
	{	
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setObject(ParameterIndex + 1, ObjectValue, ObjectType);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setNClob(int ParameterIndex, Reader NClobReader, long ReaderLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ReaderLength < 0 && ReaderLength != -1) throw new NumberException("StreamLength", (int)ReaderLength, false);
		try
		{
			cmdCommand.setNClob(ParameterIndex + 1, NClobReader);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}	
	}
	public final void setDate(int ParameterIndex, Date DateValue, Calendar CalendarValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setDate(ParameterIndex + 1, DateValue, CalendarValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setTime(int ParameterIndex, Time TimeValue, Calendar CalendarValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setTime(ParameterIndex + 1, TimeValue, CalendarValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setTime(int ParameterIndex, Stamp TimeValue, Calendar CalendarValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setTime(ParameterIndex + 1, TimeValue != null ? TimeValue.getTime() : null, CalendarValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}	
	}
	public final void setDate(int ParameterIndex, Stamp DateValue, Calendar CalendarValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setDate(ParameterIndex + 1, DateValue != null ? DateValue.getSQLDate() : null, CalendarValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBlob(int ParameterIndex, Streamable BlobInputStream, long StreamLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(StreamLength < 0 && StreamLength != -1) throw new NumberException("StreamLength", (int)StreamLength, false);
		if(BlobInputStream != null && !BlobInputStream.isStreamInput())
		{
			Reporter.error(Reporter.REPORTER_IO, "This does not contain an InputStream");
			throw new IOException("This does not contain an InputStream");
		}
		try
		{
			cmdCommand.setBlob(ParameterIndex + 1, BlobInputStream != null ? BlobInputStream.getStreamInput() : null, StreamLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBlob(int ParameterIndex, InputStream BlobInputStream, long StreamLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(StreamLength < 0 && StreamLength != -1) throw new NumberException("StreamLength", (int)StreamLength, false);
		try
		{
			cmdCommand.setBlob(ParameterIndex + 1, BlobInputStream, StreamLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setAsciiStream(int ParameterIndex, Streamable AsciiStream, int StreamLength) throws IOException, NumberException
	{
		setAsciiStream(ParameterIndex, AsciiStream, (long)StreamLength);		
	}
	public final void setAsciiStream(int ParameterIndex, InputStream AsciiStream, int StreamLength) throws IOException, NumberException
	{
		setAsciiStream(ParameterIndex, AsciiStream, (long)StreamLength);
	}
	public final void setAsciiStream(int ParameterIndex, Streamable AsciiStream, long StreamLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(StreamLength < 0 && StreamLength != -1) throw new NumberException("StreamLength", (int)StreamLength, false);
		if(AsciiStream != null && !AsciiStream.isStreamInput())
		{
			Reporter.error(Reporter.REPORTER_IO, "This does not contain an InputStream");
			throw new IOException("This does not contain an InputStream");
		}
		try
		{
			cmdCommand.setAsciiStream(ParameterIndex + 1, AsciiStream != null ? AsciiStream.getStreamInput() : null, StreamLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}		
	}
	public final void setAsciiStream(int ParameterIndex, InputStream AsciiStream, long StreamLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(StreamLength < 0 && StreamLength != -1) throw new NumberException("StreamLength", (int)StreamLength, false);
		try
		{
			cmdCommand.setAsciiStream(ParameterIndex + 1, AsciiStream, StreamLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}		
	}
	public final void setBinaryStream(int ParameterIndex, Streamable BinaryStream, int StreamLength) throws IOException, NumberException
	{
		setBinaryStream(ParameterIndex, BinaryStream, (long)StreamLength);
	}
	public final void setTimestamp(int ParameterIndex, Stamp TimeStampValue, Calendar CalendarValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setTimestamp(ParameterIndex + 1, TimeStampValue != null ? TimeStampValue.getTimestamp() : null, CalendarValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBinaryStream(int ParameterIndex, Streamable BinaryStream, long StreamLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(StreamLength < 0 && StreamLength != -1) throw new NumberException("StreamLength", (int)StreamLength, false);
		if(BinaryStream != null && !BinaryStream.isStreamInput())
		{
			Reporter.error(Reporter.REPORTER_IO, "This does not contain an InputStream");
			throw new IOException("This does not contain an InputStream");
		}
		try
		{
			cmdCommand.setAsciiStream(ParameterIndex + 1, BinaryStream != null ? BinaryStream.getStreamInput() : null, StreamLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setBinaryStream(int ParameterIndex, InputStream BinaryStream, int StreamLength) throws IOException, NumberException
	{
		setBinaryStream(ParameterIndex, BinaryStream, (long)StreamLength);
	}
	public final void setBinaryStream(int ParameterIndex, InputStream BinaryStream, long StreamLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(StreamLength < 0 && StreamLength != -1) throw new NumberException("StreamLength", (int)StreamLength, false);
		try
		{
			cmdCommand.setAsciiStream(ParameterIndex + 1, BinaryStream, StreamLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setCharacterStream(int ParameterIndex, Reader CharacterReader, int ReaderLength) throws IOException, NumberException
	{
		setCharacterStream(ParameterIndex, CharacterReader, (long)ReaderLength);
	}
	public final void setCharacterStream(int ParameterIndex, Reader CharacterReader, long ReaderLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ReaderLength < 0 && ReaderLength != -1) throw new NumberException("ReaderLength", (int)ReaderLength, false);
		try
		{
			cmdCommand.setCharacterStream(ParameterIndex + 1, CharacterReader, ReaderLength);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setNCharacterStream(int ParameterIndex, Reader CharacterReader, long ReaderLength) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ReaderLength < 0 && ReaderLength != -1) throw new NumberException("StreamLength", (int)ReaderLength, false);
		try
		{
			cmdCommand.setNCharacterStream(ParameterIndex + 1, CharacterReader);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}		
	}	
	public final void setTimestamp(int ParameterIndex, Timestamp TimeStampValue, Calendar CalendarValue) throws IOException, NumberException
	{
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		try
		{
			cmdCommand.setTimestamp(ParameterIndex + 1, TimeStampValue, CalendarValue);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	public final void setObject(int ParameterIndex, Object ObjectValue, int ObjectType, int ObjectScaleOrLength) throws IOException, NumberException
	{	
		if(ParameterIndex < 0) throw new NumberException("ParameterIndex", ParameterIndex, false);
		if(ObjectScaleOrLength < 0) throw new NumberException("ObjectScaleOrLength", ObjectScaleOrLength, false);
		try
		{
			cmdCommand.setObject(ParameterIndex + 1, ObjectValue, ObjectType);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof SQLCommand && ((SQLCommand)CompareObject).cmdCommand.hashCode() == cmdCommand.hashCode();
	}
	public final boolean isClosed() throws IOException
	{
		try
		{
			return cmdCommand.isClosed();
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	
	public final int hashCode()
	{
		return cmdCommand.hashCode() + cmdConnection.hashCode();
	}
	public final int executeNonQuery() throws IOException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to execute a Non-Query SQLCommand on \"" + cmdConnection.toString() + "\"!");
			return cmdCommand.executeUpdate();
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	
	public final String toString()
	{
		return "SQLCommand(DC) [" + hashCode() + "]";
	}

	public final ResultSet executeQuery() throws IOException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_IO, "Trying to execute a Query SQLCommand on \"" + cmdConnection.toString() + "\"!");
			return cmdCommand.executeQuery();
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	
	protected final SQLCommand clone() throws CloneException
	{
		throw new CloneException("SQLCommands cannot be cloned!");
	}
}