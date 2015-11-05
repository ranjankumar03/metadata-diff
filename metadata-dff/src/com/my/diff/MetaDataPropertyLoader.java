package com.my.diff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MetaDataPropertyLoader
{
	/*String		propFileName	= "metadata_db.properties";*/
	Properties	properties		= null;
	InputStream	inputStream		= null;

	public MetaDataPropertyLoader()
	{
	}

	public Properties getProperty(String arg)
	{
		try
		{
			inputStream = new FileInputStream(arg);
			properties = new Properties();
			if (inputStream != null)
			{
				properties.load(inputStream);
				//inputStream.close();
			}
			else
			{
				throw new FileNotFoundException("property file '" + arg + "' not found in the classpath");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return properties;
	}
}
