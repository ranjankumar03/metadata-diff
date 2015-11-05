package com.my.diff;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVWriter;

public class MetaDataLoaderImpl implements Loader
{
	public void load(MetaDataConfig config, Properties properties, String side) throws IOException, SQLException
	{
		try
		{
			Class.forName(properties.getProperty("diff."+side +".metadata.database.schema.classname"));
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		CSVWriter writer = null;
		try
		{
			connection = DriverManager.getConnection(properties.getProperty("diff."+side +".metadata.database.schema.url"), properties.getProperty("diff."+side + ".metadata.database.schema.username"),
						properties.getProperty("diff."+side +".metadata.database.schema.password"));
			connection.setAutoCommit(false);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = statement.executeQuery(config.getQuery());
			writer = new CSVWriter(new FileWriter(getFileName(config, properties.getProperty("diff."+side +".metadata.output.file.location"))));
			boolean includecolumnnames = true;
			writer.writeAll(resultSet, includecolumnnames);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{			
			if (resultSet != null)
			{
				resultSet.close();
			}
			if (statement != null)
			{
				statement.close();
			}
			if (connection != null)
			{
				connection.close();
			}
			writer.close();
		}
	}

	private String getFileName(MetaDataConfig config, String outputFilePath)
	{
		return outputFilePath + config.getTableName() + ".csv";
	}
}
