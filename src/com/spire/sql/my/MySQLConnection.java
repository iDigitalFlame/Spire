package com.spire.sql.my;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import com.spire.log.Reporter;
import com.spire.cred.Credentials;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.ex.NumberException;
import com.spire.sql.DataConnection;
import com.spire.ex.PermissionException;

public final class MySQLConnection extends DataConnection
{
	private static final short SQL_PORT = 3306;
	private static final String SQL_JDBC_TYPE = "mysql";
	private static final String SQL_JDBC_NAME = "MySQL Server";
	private static final Class<?> SQL_CONNECTION_DRIVER = getDatabaseDriver();
	
	public MySQLConnection(String SQLConnectionString) throws NullException, StringException, PermissionException, IOException
	{
		super(SQLConnectionString, null, null);
	}
	public MySQLConnection(String SQLServer, String SQLDatabase) throws NullException, StringException, PermissionException, IOException
	{
		super(SQLServer, SQLDatabase, null, null, SQL_PORT, null);
	}
	public MySQLConnection(String SQLConnectionString, Credentials SQLCredentials) throws NullException, StringException, PermissionException, IOException
	{
		super(SQLConnectionString, SQLCredentials != null ? SQLCredentials.getUserName() : null, SQLCredentials != null ? SQLCredentials.getUserPassword() : null);
	}
	public MySQLConnection(String SQLServer, String SQLDatabase, Credentials SQLCredentials) throws NullException, StringException, PermissionException, IOException
	{
		super(SQLServer, SQLDatabase, SQLCredentials != null ? SQLCredentials.getUserName() : null, SQLCredentials != null ? SQLCredentials.getUserPassword() : null, SQL_PORT, null);
	}
	public MySQLConnection(String SQLConnectionString, String UserName, String UserPassword) throws NullException, StringException, PermissionException, IOException
	{
		super(SQLConnectionString, UserName, UserPassword);
	}
	public MySQLConnection(String SQLServer, String SQLDatabase, int SQLPort) throws NullException, StringException, PermissionException, IOException, NumberException
	{
		super(SQLServer, SQLDatabase, null, null, SQLPort, null);
	}
	public MySQLConnection(String SQLServer, String SQLDatabase, String UserName, String UserPassword) throws NullException, StringException, PermissionException, IOException
	{
		super(SQLServer, SQLDatabase, UserName, UserPassword, SQL_PORT, null);
	}
	public MySQLConnection(String SQLServer, String SQLDatabase, Credentials SQLCredentials, int SQLPort) throws NullException, StringException, PermissionException, IOException, NumberException
	{
		super(SQLServer, SQLDatabase, SQLCredentials != null ? SQLCredentials.getUserName() : null, SQLCredentials != null ? SQLCredentials.getUserPassword() : null, SQLPort, null);
	}
	public MySQLConnection(String SQLServer, String SQLDatabase, int SQLPort, String UserName, String UserPassword) throws NullException, StringException, PermissionException, IOException, NumberException
	{
		super(SQLServer, SQLDatabase, UserName, UserPassword, SQLPort, null);
	}
	
	protected final boolean isDriverLoaded()
	{
		return SQL_CONNECTION_DRIVER != null;
	}
	
	protected final String getSQLType()
	{
		return SQL_JDBC_TYPE;
	}
	protected final String getJDBCName()
	{
		return SQL_JDBC_NAME;
	}

	protected final Connection getConnection(String DatabaseServer, String DatabaseName, String DatabaseUser, String DatabasePassword, int DatabasePort, String DatabaseInstance) throws SQLException
	{
		return DriverManager.getConnection("jdbc:mysql://" + DatabaseServer + ":" + DatabasePort + "/" + DatabaseName, DatabaseUser, DatabasePassword);
	}
	
	private static final Class<?> getDatabaseDriver()
	{
		try
		{
			return Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException Exception)
		{
			Reporter.severe(Reporter.REPORTER_IO, "Cannot find or load the MySQL Database driver!");
		}
		return null;
	}
}