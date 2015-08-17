/*
 * Title: SQL
 * Author: Matthew Boyette
 * Date: 11/05/2014
 * 
 * This class is merely a collection of useful static methods that support code recycling. Specifically, this
 * class offers methods and classes which provide a means to execute and process SQL queries.
 */

package api.util.networking;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;

public final class SQL
{
	public static class Script
	{
		private Connection	connection			= null;
		private String		delimiter			= ";";
		private boolean		isDebugging			= false;
		private boolean		isFullLineDelimiter	= false;
		
		public Script(final Connection connection, final boolean isDebugging)
		{
			this.setConnection(connection);
			this.setDebugging(isDebugging);
		}
		
		public Script(final Connection connection, final boolean isDebugging, final Reader reader) throws IOException, SQLException
		{
			this(connection, isDebugging);
			
			try
			{
				this.execute(reader);
			}
			catch (final IOException exception)
			{
				throw exception;
			}
			catch (final SQLException exception)
			{
				throw exception;
			}
		}
		
		private void execute(final Reader reader) throws IOException, SQLException
		{
			StringBuffer query = null;
			
			try
			{
				LineNumberReader lineReader = new LineNumberReader(reader);
				String line = null;
				
				while ((line = lineReader.readLine()) != null)
				{
					if (query == null)
					{
						query = new StringBuffer();
					}
					
					/*
					 * String trimmedLine = line.trim();
					 * Leads to [http://stackoverflow.com/questions/3499483/error-unterminated-quoted-string-at-or-near]
					 */
					
					String trimmedLine = line.trim().replaceAll(";$", Matcher.quoteReplacement("; \\"));
					
					if (trimmedLine.startsWith("--"))
					{
						if (this.isDebugging())
						{
							System.out.println(trimmedLine);
						}
					}
					else
						if ((trimmedLine.length() < 1) || trimmedLine.startsWith("//"))
						{
							// Do nothing.
						}
						else
							if ((trimmedLine.length() < 1) || trimmedLine.startsWith("--"))
							{
								// Do nothing.
							}
							else
								if ((!this.isFullLineDelimiter() && trimmedLine.endsWith(this.getDelimiter()))
									|| (this.isFullLineDelimiter() && trimmedLine.equals(this.getDelimiter())))
								{
									query.append(line.substring(0, line.lastIndexOf(this.getDelimiter())));
									query.append(" ");
									
									ResultSet results = SQL.executeQuery(this.getConnection(), query.toString());
									
									if (this.isDebugging() && (results != null))
									{
										System.out.println(query);
										ResultSetMetaData meta = results.getMetaData();
										int cols = meta.getColumnCount();
										
										for (int i = 1; i <= cols; i++)
										{
											String colName = meta.getColumnLabel(i);
											System.out.print(colName + "\t");
										}
										
										System.out.println("");
										
										while (results.next())
										{
											for (int i = 1; i <= cols; i++)
											{
												String colValue = results.getString(i);
												System.out.print(colValue + "\t");
											}
											
											System.out.println("");
										}
									}
									
									query = null;
								}
								else
								{
									query.append(line);
									query.append(" ");
								}
				}
				if (this.getConnection().getAutoCommit())
				{
					this.getConnection().commit();
				}
			}
			catch (final SQLException exception)
			{
				exception.fillInStackTrace();
				throw exception;
			}
			catch (final IOException exception)
			{
				exception.fillInStackTrace();
				throw exception;
			}
			finally
			{
				this.getConnection().rollback();
			}
		}
		
		public final Connection getConnection()
		{
			return this.connection;
		}
		
		public final String getDelimiter()
		{
			return this.delimiter;
		}
		
		public final boolean isDebugging()
		{
			return this.isDebugging;
		}
		
		public final boolean isFullLineDelimiter()
		{
			return this.isFullLineDelimiter;
		}
		
		protected final void setConnection(final Connection connection)
		{
			this.connection = connection;
		}
		
		protected final void setDebugging(final boolean isDebugging)
		{
			this.isDebugging = isDebugging;
		}
		
		protected final void setDelimiter(final String delimiter)
		{
			this.delimiter = delimiter;
		}
		
		protected final void setFullLineDelimiter(final boolean isFullLineDelimiter)
		{
			this.isFullLineDelimiter = isFullLineDelimiter;
		}
	}
	
	public static Connection createConnection(final Driver driver, final String url, final String username, final String password)
		throws SQLException
	{
		/*
		 * NOTE: You must include the JDBC driver JAR on the build path in order to get this code to work!!!
		 * http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html
		 */
		
		DriverManager.registerDriver(driver);
		return DriverManager.getConnection(url, username, password);
	}
	
	public static ResultSet executeQuery(final Connection connection, final String query) throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet results = null;
		
		try
		{
			if (statement.execute(query))
			{
				results = statement.getResultSet();
			}
			
			if (connection.getAutoCommit())
			{
				connection.commit();
			}
		}
		catch (final SQLException exception)
		{
			exception.fillInStackTrace();
			throw exception;
		}
		finally
		{
			if (statement != null)
			{
				try
				{
					statement.close();
				}
				catch (final Exception exception)
				{
					// Ignore to workaround a bug in Jakarta DBCP.
				}
			}
			
			connection.rollback();
		}
		
		Thread.yield();
		return results;
	}
}