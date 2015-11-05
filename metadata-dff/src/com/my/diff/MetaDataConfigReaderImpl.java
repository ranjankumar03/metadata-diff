package com.my.diff;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MetaDataConfigReaderImpl implements MetaDataConfigReader
{
	private final static List<MetaDataConfig>	configList	= new ArrayList<MetaDataConfig>();

	public  List<MetaDataConfig> getMetaConfig(String metaConfigFile)//static
	{
		CSVReader reader = null;
		try
		{
			reader = new CSVReader(new FileReader(metaConfigFile), ',', '"', 1);
			for (String[] str : reader.readAll())
			{
				MetaDataConfig configObject = new MetaDataConfig();
				configObject.setSchemaName(str[0]);
				configObject.setTableName(str[1]);
				configObject.setUniqueColumnNames(str[2]);
				configObject.setIgnoreColumnNames(str[3]);
				configObject.setQuery(str[4]);
				configList.add(configObject);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.exit(0);
		return configList;
	}
}
