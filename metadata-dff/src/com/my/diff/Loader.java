package com.my.diff;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public interface Loader
{
	public void load(MetaDataConfig config, Properties properties, String side) throws IOException, SQLException;
}
