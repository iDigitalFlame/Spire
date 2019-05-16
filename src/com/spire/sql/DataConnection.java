package com.spire.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.ex.PermissionException;

public abstract class DataConnection
{
	protected final Connection connectionInstance;
	
	public SQLCommand createCommand(String DataQuery) throws NullException, StringException, IOException
	{
		return new SQLCommand(DataQuery, this);
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof DataConnection && ((DataConnection)CompareObject).connectionInstance.equals(connectionInstance);
	}
	
	public final int hashCode()
	{
		return connectionInstance.hashCode();
	}
	
	public final String toString()
	{
		return "SQLConnection(DC) [" + getSQLType() + ":" + hashCode() + "]";
	}
	
	public final Connection getConnection()
	{
		return connectionInstance;
	}
	
	protected DataConnection(String ConnectionString, String UserName, String UserPassword) throws NullException, StringException, PermissionException, IOException
	{
		if(!isDriverLoaded()) throw new IOException("Cannot start a DataConnection without the " + getJDBCName() + " Driver!");
		Security.check("io.net.sql." + getSQLType());
		if(ConnectionString == null) throw new NullException("ConnectionString");
		if(ConnectionString.isEmpty()) throw new StringException("ConnectionString");
		connectionInstance = getDataConnectionFromString(ConnectionString, UserName, UserPassword);
		Reporter.debug(Reporter.REPORTER_IO, "Opened a " + getJDBCName() + " DataConnection!");
	}
	protected DataConnection(String DatabaseServer, String DatabaseName, String UserName, String UserPassword, int DatabasePort, String DatabaseInstance) throws NullException, StringException, PermissionException, IOException, NumberException
	{
		if(!isDriverLoaded()) throw new IOException("Cannot start a DataConnection without the " + getJDBCName() + " Driver!");
		Security.check("io.net.sql." + getSQLType());
		if(DatabaseServer == null) throw new NullException("DatabaseServer");
		if(DatabaseName == null) throw new NullException("DatabaseName");
		if(DatabaseServer.isEmpty()) throw new StringException("DatabaseServer");
		if(DatabaseName.isEmpty()) throw new StringException("DatabaseName");
		if(DatabasePort <= 0) throw new NumberException("DatabasePort", DatabasePort, true);
		if(DatabasePort > Constants.MAX_USHORT_SIZE) throw new NumberException("DatabasePort", DatabasePort, 0, Constants.MAX_USHORT_SIZE);
		connectionInstance = getDataConnection(DatabaseServer, DatabaseName, UserName, UserPassword, DatabasePort, DatabaseInstance);
		Reporter.debug(Reporter.REPORTER_IO, "Opened a " + getJDBCName() + " DataConnection to server \"" + DatabaseServer + "\" on database \"" + DatabaseName + "\"!");
	}
	
	protected abstract boolean isDriverLoaded();
	
	protected abstract String getSQLType();
	protected abstract String getJDBCName();
	
	@SuppressWarnings("static-method")
	protected Connection getConnectionFromString(String ConnectionString, String DatabaseUser, String DatabasePassword) throws SQLException
	{
		return DriverManager.getConnection(ConnectionString, DatabaseUser, DatabasePassword);
	}
	protected abstract Connection getConnection(String DatabaseServer, String DatabaseName, String DatabaseUser, String DatabasePassword, int DatabasePort, String DatabaseInstance) throws SQLException;
	
	protected final DataConnection clone() throws CloneException
	{
		throw new CloneException("Cannot clone a DataConnection!");
	}
	
	private final Connection getDataConnectionFromString(String ConnectionString, String DatabaseUser, String DatabasePassword) throws IOException
	{
		try
		{
			return getConnectionFromString(ConnectionString, DatabaseUser, DatabasePassword);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
	private final Connection getDataConnection(String DatabaseServer, String DatabaseName, String DatabaseUser, String DatabasePassword, int DatabasePort, String DatabaseInstance) throws IOException
	{
		try
		{
			return getConnection(DatabaseServer, DatabaseName, DatabaseUser, DatabasePassword, DatabasePort, DatabaseInstance);
		}
		catch (SQLException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, Exception);
			throw new IOException(Exception);
		}
	}
}