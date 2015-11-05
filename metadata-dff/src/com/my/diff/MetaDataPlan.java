package com.my.diff;

import java.util.Arrays;

public class MetaDataPlan
{
	private String		metadataName;
	private String		tableName;
	private String		uniqueKeys;
	private String		ignoreColumns;
	private String		displayColumns;
	private String[]	lhsHeader;
	private String[]	rhsHeader;
	private String		lhsFile;
	private String		rhsFile;

	public String getUniqueKeys()
	{
		return uniqueKeys;
	}

	public void setUniqueKeys(String uniqueKeys)
	{
		this.uniqueKeys = uniqueKeys;
	}

	public String getIgnoreColumns()
	{
		return ignoreColumns;
	}

	public void setIgnoreColumns(String ignoreColumns)
	{
		this.ignoreColumns = ignoreColumns;
	}

	public String getDisplayColumns()
	{
		return displayColumns;
	}

	public void setDisplayColumns(String displayColumns)
	{
		this.displayColumns = displayColumns;
	}

	public String getLhsFile()
	{
		return lhsFile;
	}

	public void setLhsFile(String lhsFile)
	{
		this.lhsFile = lhsFile;
	}

	public String getRhsFile()
	{
		return rhsFile;
	}

	public void setRhsFile(String rhsFile)
	{
		this.rhsFile = rhsFile;
	}

	public String[] getLhsHeader()
	{
		return lhsHeader;
	}

	public void setLhsHeader(String[] lhsHeader)
	{
		this.lhsHeader = lhsHeader;
	}

	public String[] getRhsHeader()
	{
		return rhsHeader;
	}

	public void setRhsHeader(String[] rhsHeader)
	{
		this.rhsHeader = rhsHeader;
	}

	@Override
	public String toString()
	{
		return "MetaDataPlan [uniqueKeys=" + uniqueKeys + ", ignoreColumns=" + ignoreColumns + ", displayColumns=" + "displayColumns" + ", lhsFile=" + lhsFile + ", rhsFile=" + rhsFile
					+ ", lhsHeader=" + Arrays.toString(lhsHeader) + ", rhsHeader=" + Arrays.toString(rhsHeader) + "]";
	}

	public String getMetadataName()
	{
		return metadataName;
	}

	public void setMetadataName(String metadataName)
	{
		this.metadataName = metadataName;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
}
