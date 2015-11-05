package com.my.diff;

public class MetaDataConfig
{
	private String	schemaName;
	private String	tableName;
	private String	uniqueColumnNames;
	private String	ignoreColumnNames;
	private String	displayColumns;
	private String	query;

	public String getSchemaName()
	{
		return schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getUniqueColumnNames()
	{
		return uniqueColumnNames;
	}

	public void setUniqueColumnNames(String uniqueColumnNames)
	{
		this.uniqueColumnNames = uniqueColumnNames;
	}

	public String getIgnoreColumnNames()
	{
		return ignoreColumnNames;
	}

	public void setIgnoreColumnNames(String ignoreColumnNames)
	{
		this.ignoreColumnNames = ignoreColumnNames;
	}

	public String getDisplayColumns()
	{
		return displayColumns;
	}

	public void setDisplayColumns(String displayColumns)
	{
		this.displayColumns = displayColumns;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayColumns == null) ? 0 : displayColumns.hashCode());
		result = prime * result + ((ignoreColumnNames == null) ? 0 : ignoreColumnNames.hashCode());
		result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((uniqueColumnNames == null) ? 0 : uniqueColumnNames.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDataConfig other = (MetaDataConfig) obj;
		if (displayColumns == null)
		{
			if (other.displayColumns != null)
				return false;
		}
		else if (!displayColumns.equals(other.displayColumns))
			return false;
		if (ignoreColumnNames == null)
		{
			if (other.ignoreColumnNames != null)
				return false;
		}
		else if (!ignoreColumnNames.equals(other.ignoreColumnNames))
			return false;
		if (schemaName == null)
		{
			if (other.schemaName != null)
				return false;
		}
		else if (!schemaName.equals(other.schemaName))
			return false;
		if (tableName == null)
		{
			if (other.tableName != null)
				return false;
		}
		else if (!tableName.equals(other.tableName))
			return false;
		if (uniqueColumnNames == null)
		{
			if (other.uniqueColumnNames != null)
				return false;
		}
		else if (!uniqueColumnNames.equals(other.uniqueColumnNames))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "MetaDataConfig [schemaName=" + schemaName + ", tableName=" + tableName + ", uniqueColumnNames=" + uniqueColumnNames + ", ignoreColumnNames=" + ignoreColumnNames + ", displayColumns="
					+ displayColumns + "]";
	}
}
