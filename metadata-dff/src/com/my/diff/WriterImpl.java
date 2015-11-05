package com.my.diff;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.diffkit.diff.engine.DKSource;

public class WriterImpl
{
	
	private String				leftStr		= "";
	private String				rightStr	= "";
	private String				diffStr		= "";
	private String				header		= "";
	private String   	      STRING_QUOTE  = "\"";
	private String              sinkFile    = null;
	private FileWriter	        writer		= null;
	private static boolean      first       = true;
	


	public void write(List<String> list, DKSource tableSource, Properties metadataProperties) throws IOException
	{
		sinkFile = metadataProperties.getProperty("diff.diff.output.summary.location");
		try
		{
			if (first) {
				System.out.println("Writing diff started...");
				writer = new FileWriter(sinkFile);
				first = false;
				writer.write("[ Metadata Diff Report ]\n");
				String lhsdbInstance = metadataProperties.getProperty("diff.lhs.metadata.database.schema.url");
				String rhsdbInstance = metadataProperties.getProperty("diff.rhs.metadata.database.schema.url");
				String lhsInstance = lhsdbInstance.substring(lhsdbInstance.indexOf("@") + 1);
				String rhsInstance = rhsdbInstance.substring(rhsdbInstance.indexOf("@") + 1);
				writer.write(lhsInstance + " => " + rhsInstance +"\n");
				writer.write(metadataProperties.getProperty("diff.lhs.metadata.output.file.location")+" => "+metadataProperties.getProperty("diff.rhs.metadata.output.file.location")+"\n\n");
			}
			else {
				writer = new FileWriter(sinkFile, true);
			}

		String tableName = getTableName(tableSource);
		for (String str : list)
		{
			if (str.startsWith("L"))
			{
				String line = "";
				int count = 0;
				String[] strNew = str.substring(2, str.length() - 3).split("\\##!,");
				for (String str1 : strNew)
				{
					count++;
					int index = str1.indexOf("=");
					if (count != strNew.length)
						line += STRING_QUOTE + str1.split("=")[1] + STRING_QUOTE + ",";
					else
					{
						//Done to work fine with '=' in column cell
						String intermediate = str1.substring(index + 1);
						line += STRING_QUOTE + intermediate.substring(0, intermediate.length() - 3) + STRING_QUOTE;
					}
				}
				leftStr += line + "\n";
			}
			else if (str.startsWith("R"))
			{
				String line = "";
				int count = 0;
				String[] strNew = str.substring(2, str.length() - 3).split("\\##!,");
				for (String str1 : strNew)
				{
					count++;
					int index = str1.indexOf("=");
					if (count != strNew.length)
						line += STRING_QUOTE + str1.split("=")[1] + STRING_QUOTE + ",";
					else
					{
						//Done to work fine with '=' in column cell
						String intermediate = str1.substring(index + 1);
						line += STRING_QUOTE + intermediate.substring(0, intermediate.length() - 3) + STRING_QUOTE;
					}
				}
				rightStr += line + "\n";
			}
			else if (str.startsWith("D"))
			{
				String line = "";
				int count = 0;
				String[] strNew = str.substring(2, str.length() - 3).split("\\##!,");
				for (String str1 : strNew)
				{
					count++;
					int index = str1.indexOf("=");
					if (count != strNew.length)
					{
						String intermediate = str1.substring(index + 1);
						line += STRING_QUOTE + intermediate.substring(0) + STRING_QUOTE + ",";
					}
					else
					{
						//Done to work fine with '=' in column cell
						String intermediate = str1.substring(index + 1);
						line += STRING_QUOTE + intermediate.substring(0, intermediate.length() - 2) + STRING_QUOTE;
					}
				}
				diffStr += line + "\n";
			}
		}
		writer.write("==========================\n");
		writer.write("[ "+tableName + " ]\n");
		writer.write("==========================\n");
		if (list != null && list.size() > 0)
		{
			String header = getHeader(list.get(0));
			if(!"".equalsIgnoreCase(leftStr)) {
			  writer.write("LEFT ONLY" + "\n");
			  writer.write(header + "\n" + leftStr);
			  writer.write("\n\n");
			}
			
			if(!"".equalsIgnoreCase(rightStr)) {
			  writer.write("RIGHT ONLY" + "\n");
			  writer.write(header + "\n" + rightStr);
			  writer.write("\n\n");
			}
			if(!"".equalsIgnoreCase(diffStr)){
			  writer.write("DIFFERENCES" + "\n");
			  writer.write(header + "\n" + diffStr);
			}
		}
		else
		{
			writer.write("No Difference Found\n");
		}
		writer.write("==================================================\n");
		writer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		finally {
			writer.close();
		}
	}

	private String getTableName(DKSource tableSource)
	{
		int lastIndex = tableSource.toString().lastIndexOf("lhs");
		return tableSource.toString().substring(lastIndex + 4, tableSource.toString().length() - 5);
	}

	private String getHeader(String header1)
	{
		int count = 0;
		String[] strNew1 = header1.substring(2).split("\\##!, ");
		for (String str1 : strNew1)
		{
			count++;
			if (count != strNew1.length)
			{
				header += str1.split("=")[0] + ",";
			}
			else
				header += str1.split("=")[0];
		}
		return header;
	}
}
