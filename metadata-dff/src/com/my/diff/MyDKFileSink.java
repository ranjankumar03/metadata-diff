package com.my.diff;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.diffkit.diff.engine.DKContext;
import org.diffkit.diff.engine.DKDiff;
import org.diffkit.diff.sns.DKAbstractSink;
import org.diffkit.diff.sns.DKDiffFormatter;

public class MyDKFileSink extends DKAbstractSink {
	
	@SuppressWarnings("rawtypes")
	private List results = new ArrayList();
	private DKDiffFormatter formatter = null;

	public MyDKFileSink(MyDKFormatter formatter)
			throws IOException {
		super(null);
		this.formatter = formatter; 
	}
	
	public MyDKFileSink(Writer writer_, DKDiffFormatter formatter_) throws IOException {
		      super(null);
		      
    }

	public void record(DKDiff diff_, DKContext context_) throws IOException
	{
		if (diff_ == null)
			return;
		String diffString = formatter.format(diff_, context_);
		if (diffString == null)
		{
			return;
		}
		if (diffString != null && !diffString.equals(""))
		{
			results.add(diffString);
		}
	}
   
	public void recordResults(DKContext diffContext, Properties metadataProperties)
	{
		try
		{
			new WriterImpl().write(results, diffContext._lhs, metadataProperties);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Kind getKind()
	{
		return Kind.FILE;
	}
}
