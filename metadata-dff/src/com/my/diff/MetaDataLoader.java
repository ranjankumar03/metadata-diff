package com.my.diff;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

class MetaDataLoader
{
	private static MetaDataConfigReader	reader	= null;
	private static Loader				loader	= null;
	private String LEFT_SIDE = "lhs";
	private String RIGHT_SIDE = "rhs";

	public void generateCSV(String metaConfig, String property, boolean ifOneSided)
	{
		System.out.println("Metadata Loading started...");
		if (metaConfig == null || property == null)
		{
			throw new IllegalArgumentException("Invalid arguments provided...");
		}
		reader = new MetaDataConfigReaderImpl();
		List<MetaDataConfig> metaConfigList = reader.getMetaConfig(metaConfig);
	
			Properties properties = new MetaDataPropertyLoader().getProperty(property);
			reader = new MetaDataConfigReaderImpl();
			loader = new MetaDataLoaderImpl();
			try
			{
				for (MetaDataConfig config : metaConfigList)
				{	
					loader.load(config , properties , LEFT_SIDE);
					if(ifOneSided != true){
					   loader.load(config , properties , RIGHT_SIDE);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			System.out.println("Data Load Process Completed...");
		
		//System.exit(0);
	}
}
