package com.my.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.diffkit.common.DKDistProperties;
import org.diffkit.common.DKRuntime;
import org.diffkit.common.DKUserException;
import org.diffkit.db.DKDBFlavor;
import org.diffkit.diff.conf.DKAutomaticTableComparison;
import org.diffkit.diff.conf.DKDemoDB;
import org.diffkit.diff.conf.DKPassthroughPlan;
import org.diffkit.diff.conf.DKPlan;
import org.diffkit.diff.conf.DKTestBridge;
import org.diffkit.diff.engine.DKColumnModel;
import org.diffkit.diff.engine.DKColumnModel.Type;
import org.diffkit.diff.engine.DKContext;
import org.diffkit.diff.engine.DKContext.UserKey;
import org.diffkit.diff.engine.DKDiff;
import org.diffkit.diff.engine.DKDiffEngine;
import org.diffkit.diff.engine.DKSink;
import org.diffkit.diff.engine.DKSource;
import org.diffkit.diff.engine.DKTableComparison;
import org.diffkit.diff.engine.DKTableModel;
import org.diffkit.util.DKMapUtil;
import org.diffkit.util.DKStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class MyDKApplication
{
	private static final String		APPLICATION_NAME						= "diffkit-app";
	private static final String		VERSION_OPTION_KEY						= "version";
	private static final String		HELP_OPTION_KEY							= "help";
	private static final String		TEST_OPTION_KEY							= "test";
	private static final String		PLAN_FILE_OPTION_KEY					= "planfiles";
	private static final String		METADATADIFF_OPTION_KEY					= "metadatadiff";
	private static final String		GENERATECSV_OPTION_KEY					= "generatecsv";
	private static final String		DIFFCSV_OPTION_KEY					    = "diffcsv";
	private static final String		CONFIG_OPTION_KEY					    = "config";
	private static final String		PROPERTIES_OPTION_KEY					= "properties";
	private static final String		ERROR_ON_DIFF_OPTION_KEY				= "errorOnDiff";
	private static final String		DEMO_DB_OPTION_KEY						= "demoDB";
	private static final Options	OPTIONS									=  new Options();
	private static final String		LOGBACK_FILE_NAME						= "logback.xml";
	private static final String		LOGBACK_CONFIGURATION_FILE_PROPERTY_KEY	= "logback.configurationFile";
	private static final String		SOURCE_DELIMITER	= ",";
	private static Logger			_systemLog;
	static
	{
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(new Option(VERSION_OPTION_KEY, "print the version information and exit"));
		optionGroup.addOption(new Option(HELP_OPTION_KEY, "print this message"));
		OptionBuilder.hasOptionalArgs(2);
		OptionBuilder.withArgName("[cases=?,] [flavors=?,]");
		OptionBuilder.withDescription("run TestCases");
		OPTIONS.addOption(OptionBuilder.create(TEST_OPTION_KEY));
		OptionBuilder.withArgName("file1[,file2...]");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("perform diff using given file(s) for plan");
		optionGroup.addOption(OptionBuilder.create(PLAN_FILE_OPTION_KEY));
		
		OptionGroup optionGroup1 = new OptionGroup();
		OptionBuilder.withArgName("file1");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("-config argument");
		optionGroup1.addOption(OptionBuilder.create(CONFIG_OPTION_KEY));

        OptionGroup optionGroup2 = new OptionGroup();
        OptionBuilder.withArgName("file2");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("-properties argument");
        optionGroup2.addOption(OptionBuilder.create(PROPERTIES_OPTION_KEY));
		
		optionGroup.addOption(OptionBuilder.create(METADATADIFF_OPTION_KEY));
		optionGroup.addOption(OptionBuilder.create(GENERATECSV_OPTION_KEY));
		optionGroup.addOption(OptionBuilder.create(DIFFCSV_OPTION_KEY));
		optionGroup.addOption(new Option(ERROR_ON_DIFF_OPTION_KEY,
					"exit with error status code (-1) if diffs are detected. otherwise will always exit with 0 unless an operating Exception was encountered"));
		optionGroup.addOption(new Option(DEMO_DB_OPTION_KEY, "run embedded demo H2 database"));
		OPTIONS.addOptionGroup(optionGroup);
		OPTIONS.addOptionGroup(optionGroup1);
		OPTIONS.addOptionGroup(optionGroup2);
	}

	public static void main(String[] args_)
	{
		initialize();
		Logger systemLog = getSystemLog();
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(OPTIONS, args_);

			if (line.hasOption(VERSION_OPTION_KEY))
				printVersion();
			else if (line.hasOption(HELP_OPTION_KEY))
				printHelp();
			else if (line.hasOption(TEST_OPTION_KEY))
				runTestCases(line.getOptionValues(TEST_OPTION_KEY));
			else if (line.hasOption(PLAN_FILE_OPTION_KEY))
				runPlan(line.getOptionValue(PLAN_FILE_OPTION_KEY), line.hasOption(ERROR_ON_DIFF_OPTION_KEY));
			else if (line.hasOption(METADATADIFF_OPTION_KEY))
				medataDiffPlan(line.getOptionValue(CONFIG_OPTION_KEY), line.getOptionValue(PROPERTIES_OPTION_KEY), line.hasOption(ERROR_ON_DIFF_OPTION_KEY));
			else if (line.hasOption(GENERATECSV_OPTION_KEY))
				generateCSVPlan(line.getOptionValue(CONFIG_OPTION_KEY), line.getOptionValue(PROPERTIES_OPTION_KEY), line.hasOption(ERROR_ON_DIFF_OPTION_KEY));
			else if (line.hasOption(DIFFCSV_OPTION_KEY))
				diffCSVPlan(line.getOptionValue(CONFIG_OPTION_KEY), line.getOptionValue(PROPERTIES_OPTION_KEY), line.hasOption(ERROR_ON_DIFF_OPTION_KEY));
			else if (line.hasOption(DEMO_DB_OPTION_KEY))
				runDemoDB();
			else
				printInvalidArguments(args_);
		}
		catch (ParseException e_)
		{
			System.err.println(e_.getMessage());
		}
		catch (Throwable e_)
		{
			Throwable rootCause = ExceptionUtils.getRootCause(e_);
			if (rootCause == null)
				rootCause = e_;
			if ((rootCause instanceof DKUserException) || (rootCause instanceof FileNotFoundException))
			{
				systemLog.info(null, e_);
				DKRuntime.getInstance().getUserLog().info("error->{}", rootCause.getMessage());
			}
			else
				systemLog.error(null, e_);
		}
	}

	private static void diffCSVPlan(String metaConfig, String properties, boolean errorOnDiff_) throws Exception
	{
		runMetadataPlan(metaConfig, properties, errorOnDiff_);	
	}

	// Will do source loading for LHS only in case of upgrade
	private static void generateCSVPlan(String metaConfig, String properties, boolean errorOnDiff_)
	{
		new MetaDataLoader().generateCSV(metaConfig, properties, true);	
	}

	private static void medataDiffPlan(String metaConfig, String properties, boolean errorOnDiff_) throws Exception
	{
		new MetaDataLoader().generateCSV(metaConfig, properties, false);
		runMetadataPlan(metaConfig, properties, errorOnDiff_);
	}

	private static void printVersion()
	{
		DKRuntime.getInstance().getUserLog().info("version->" + DKDistProperties.getPublicVersionString());
		System.exit(0);
	}
	
	private static void printInvalidArguments(String[] args_)
	{
		DKRuntime.getInstance().getUserLog().info(String.format("Invalid command line arguments: %s", Arrays.toString(args_)));
		printHelp();
	}

	private static void printHelp()
	{
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar diffkit-app.jar", OPTIONS);
	}

	private static void runPlan(String planFilesString_, boolean errorOnDiff_) throws Exception
	{
		System.out.println("Skipped");
	}

	private static void runMetadataPlan(String metaConfig, String properties, boolean errorOnDiff_) throws Exception
	{
		System.out.println("Diff Engine started...");
		Logger systemLog = getSystemLog();
		Logger userLog = DKRuntime.getInstance().getUserLog();
		if (metaConfig == null || properties == null)
		{
			throw new IllegalArgumentException("Invalid argumnets provided...");
		}
		Properties metadataProperties = new MetaDataPropertyLoader().getProperty(properties);
		String lhsFilePath = metadataProperties.getProperty("diff.lhs.metadata.output.file.location");
		String rhsFilePath = metadataProperties.getProperty("diff.rhs.metadata.output.file.location");
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(metaConfig));
			CSVReader reader = new CSVReader(bf);
			reader.readNext();  //skip header?
			String[] row = null;
			while ((row = reader.readNext()) != null && row.length > 1)
			{
				MetaDataPlan planObject = preparePlanObject(row, lhsFilePath, rhsFilePath);
				DKPlan plan = fetchPlan(planObject);
				systemLog.info("plan->{}", plan);
				DKSource lhsSource = plan.getLhsSource();
				DKSource rhsSource = plan.getRhsSource();
				DKSink sink = (DKSink) plan.getSink();
				DKTableComparison tableComparison = plan.getTableComparison();
				/*userLog.info("lhsSource->{}", lhsSource);
				userLog.info("rhsSource->{}", rhsSource);
				userLog.info("sink->{}", sink);
				userLog.info("tableComparison->{}", tableComparison);*/
				//System.exit(0);
				DKContext diffContext = doDiff(lhsSource, rhsSource, sink, tableComparison, null);
				MyDKFileSink mySink = (MyDKFileSink)sink;
				mySink.recordResults(diffContext, metadataProperties);
				//userLog.info(sink.generateSummary(diffContext));
				//if (plan.getSink().getDiffCount() == 0)
					//System.exit(0);
				if (errorOnDiff_)
					System.exit(-1);
			}
			reader.close();
			System.out.println("Diff process completed....");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if (bf != null)
			{
				bf.close();
			}
		}
		System.exit(0);
	}

	private static MetaDataPlan preparePlanObject(String[] row, String lhsFilePath, String rhsFilePath)
	{
		MetaDataPlan plan = new MetaDataPlan();
		plan.setTableName(row[1]);
		plan.setUniqueKeys(row[2]);
		plan.setIgnoreColumns(row[3]);
		CSVReader lhsReader = null;
		CSVReader rhsReader = null;
		String[] lhsHeader = null;
		String[] rhsHeader = null;
		plan.setLhsFile(lhsFilePath+plan.getTableName()+".csv");
		plan.setRhsFile(rhsFilePath+plan.getTableName()+".csv");
		try
		{
			lhsReader = new CSVReader(new FileReader(plan.getLhsFile()));
			rhsReader = new CSVReader(new FileReader(plan.getRhsFile()));
			lhsHeader = lhsReader.readNext();
			rhsHeader = rhsReader.readNext();
		}
		
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				lhsReader.close();
				rhsReader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		plan.setLhsHeader(lhsHeader);
		plan.setRhsHeader(rhsHeader);
		return plan;
	}

	private static DKPlan fetchPlan(MetaDataPlan planObject) throws IOException
	{
		String[] header = planObject.getLhsHeader();
		int[] _lhskeys = generateUniqueKeys(planObject,planObject.getLhsHeader());
		int[] _rhskeys = generateUniqueKeys(planObject,planObject.getRhsHeader());
		if(!Arrays.equals(_lhskeys,_rhskeys))
		{
			System.out.println("Unique-key mismatch in lhs and rhs");
			System.exit(0);
		}

		int lhsCount = -1;
		DKColumnModel[] ldkColumnModel = new DKColumnModel[header.length];
		for (String column : header)
		{
			lhsCount++;
			ldkColumnModel[lhsCount] = new DKColumnModel(lhsCount, column, Type.STRING, null);
		}
		DKTableModel modelLHS_ = new DKTableModel("GENERIC_STRING_MODEL", ldkColumnModel, _lhskeys);
		DKSource lhsSource = new MyDKCSVFileSource(planObject.getLhsFile(), modelLHS_, null, null, SOURCE_DELIMITER, true, false);
		String[] rheader = planObject.getRhsHeader();
		int rhsCount = -1;
		DKColumnModel[] rdkColumnModel = new DKColumnModel[rheader.length];
		for (String column : rheader)
		{
			rhsCount++;
			rdkColumnModel[rhsCount] = new DKColumnModel(rhsCount, column, Type.STRING, null);
		}
		DKTableModel modelRHS_ = new DKTableModel("GENERIC_STRING_MODEL", rdkColumnModel, _rhskeys);
		DKSource rhsSource = new MyDKCSVFileSource(planObject.getRhsFile(), modelRHS_, null, null, SOURCE_DELIMITER, true, false);
		DKSink sink = new MyDKFileSink(MyDKFormatter.getInstance());
		/* [TODO] DKAutomaticTableComparison(DKSource lhsSource_, DKSource rhsSource_,
		            DKDiff.Kind kind_, String[] diffColumnNames_,
		            String[] ignoreColumnNames_,
		            String[] displayColumnNames_, Long maxDiffs_,
		            Float numberTolerance_,
		            Map<String, String[]> toleranceMap_) */
		String[] ignoreColumns = planObject.getIgnoreColumns().split("\\|");
		String[] displayColumns = generateDisplayColumn(planObject, ignoreColumns);
		DKAutomaticTableComparison tableComparison = new DKAutomaticTableComparison(lhsSource, rhsSource, DKDiff.Kind.BOTH, null, ignoreColumns, displayColumns, new Long(9223372), null, null);
		DKPassthroughPlan _providedPlan = new DKPassthroughPlan();
		_providedPlan.setLhsSource(lhsSource);
		_providedPlan.setRhsSource(rhsSource);
		_providedPlan.setSink(sink);
		_providedPlan.setTableComparison(tableComparison);
		return _providedPlan;
	}

	private static int[] generateUniqueKeys(MetaDataPlan planObject, String[] header)
	{
		String[] uniqueKeys = planObject.getUniqueKeys().split("\\|");
		List<String> keysList = Arrays.asList(uniqueKeys);
		if (keysList == null || keysList.contains("") || keysList.size() < 1)
		{
			System.out.println("There is issue with Unique-Keys configuration..");
			System.exit(0);
		}
		
		int[] _keys = new int[uniqueKeys.length];
		int headerIndex = -1;
		int keyIndex = -1;
		for(String str:header){
			headerIndex++;
			if(keysList.contains(str)){
				 keyIndex++;
				_keys[keyIndex] = headerIndex;				
			}
		}
		return _keys;
	}

	private static String[] generateDisplayColumn(MetaDataPlan planObject, String[] ignoreColumns)
	{
		List<String> lhsHeader = Arrays.asList(planObject.getLhsHeader());
		List<String> rhsHeader = Arrays.asList(planObject.getRhsHeader());
		List<String> ignoreList = Arrays.asList(ignoreColumns);
		List<String> result = new LinkedList<String>();
		for(String str : lhsHeader){
			if(rhsHeader.contains(str) && !ignoreList.contains(str)){
				result.add(str);
			}
		}
		String[] displayColumns = new String[result.size()];
		return result.toArray(displayColumns);
	}

	@SuppressWarnings("unchecked")
	private static DKContext doDiff(DKSource lhsSource_, DKSource rhsSource_, DKSink sink_, DKTableComparison tableComparison_, Map<UserKey, Object> userDictionary_) throws Exception
	{
		Logger systemLog = getSystemLog();
		DKDiffEngine engine = new DKDiffEngine();
		userDictionary_ = DKMapUtil.combine(userDictionary_, tableComparison_.getUserDictionary());
		systemLog.info("engine->{}", engine);
		return engine.diff(lhsSource_, rhsSource_, sink_, tableComparison_, userDictionary_);
	}

	private static void runDemoDB() throws Exception
	{
		DKDemoDB.run();
	}

	@SuppressWarnings("unchecked")
	private static void runTestCases(String[] args_)
	{
		Logger systemLog = getSystemLog();
		systemLog.info("args_->{}", Arrays.toString(args_));
		DKRuntime.getInstance().getUserLog().info("running TestCases");
		Map<String, ?> testCaseParams = parseTestCaseArgs(args_);
		systemLog.debug("testCaseParams->{}", testCaseParams);
		DKTestBridge.runTestCases((String) testCaseParams.get("cases"), (List<DKDBFlavor>) testCaseParams.get("flavors"));
	}

	/**
	 * @return guaranteed to be non-null. <br/>
	 *         keys: cases, flavors
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, ?> parseTestCaseArgs(String[] args_)
	{
		if (ArrayUtils.isEmpty(args_))
			return new HashMap<String, Object>();
		HashMap<String, Object> parms = new HashMap<String, Object>();
		for (String arg : args_)
		{
			if (arg.startsWith("cases="))
			{
				String[] elements = arg.split("=");
				if (elements.length != 2)
					throw new DKUserException(String.format("unrecognized argument value->%s", arg));
				String caseRangeString = elements[1];
				parms.put(elements[0], caseRangeString);
			}
			else if (arg.startsWith("flavors="))
			{
				String[] elements = arg.split("=");
				if (elements.length != 2)
					throw new DKUserException(String.format("unrecognized argument value->%s", arg));
				List<DKDBFlavor> flavors = (List<DKDBFlavor>) DKStringUtil.parseEnumList(elements[1], DKDBFlavor.class);
				parms.put(elements[0], flavors);
			}
			else
				throw new DKUserException(String.format("unrecognized argument value->%s", arg));
		}
		return parms;
	}

	private static void initialize()
	{
		DKRuntime.getInstance().setApplicationName(APPLICATION_NAME);
		configureLogging();
		DKRuntime.getInstance().getUserLog().info("DiffKit home->" + DKRuntime.getInstance().getDiffKitHome());
		loadDropinJars();
		DKRuntime.getInstance().getUserLog().info("\n");
	}

	private static void configureLogging()
	{
		File logbackConfFile = new File(DKRuntime.getInstance().getConfDir(), LOGBACK_FILE_NAME);
		String logConfPath = null;
		if (!logbackConfFile.canRead())
		{
			System.out.printf("no logging configuration file->%s.\n", logbackConfFile);
			// there is a default conf file in the jar that should get picked up
			// with this entry
			logConfPath = "conf/" + LOGBACK_FILE_NAME;
		}
		else
		{
			logConfPath = logbackConfFile.getAbsolutePath();
		}
		if (System.getProperty(LOGBACK_CONFIGURATION_FILE_PROPERTY_KEY) == null)
			System.setProperty(LOGBACK_CONFIGURATION_FILE_PROPERTY_KEY, logConfPath);
	}

	@SuppressWarnings("unchecked")
	private static void loadDropinJars()
	{
		Logger userLog = DKRuntime.getInstance().getUserLog();
		File dropinDir = DKRuntime.getInstance().getDropinDir();
		if ((dropinDir == null) || !dropinDir.isDirectory())
		{
			//userLog.info("no dropin dir");
			return;
		}
		userLog.info("dropin dir->{}", dropinDir);
		Collection<File> jarFiles = FileUtils.listFiles(dropinDir, new String[] { "jar" }, false);
		if (CollectionUtils.isEmpty(jarFiles))
		{
			userLog.info("no jar files in dropin dir");
			return;
		}
		Logger systemLog = getSystemLog();
		Object jarClassLoader = MyDKApplication.class.getClassLoader();
		systemLog.debug("jarClassLoader->{}", jarClassLoader);
		try
		{
			MethodUtils.invokeMethod(jarClassLoader, "prependJarFiles", new Object[] { new ArrayList<File>(jarFiles) });
			// jarClassLoader.prependJarFiles(new ArrayList<File>(jarFiles));
			userLog.info("loaded dropin jars->{}", jarFiles);
			systemLog.debug("loaded dropin jars->{}", jarFiles);
		}
		catch (Exception e_)
		{
			systemLog.error(null, e_);
		}
	}

	private static Logger getSystemLog()
	{
		if (_systemLog != null)
			return _systemLog;
		_systemLog = LoggerFactory.getLogger(MyDKApplication.class);
		return _systemLog;
	}
}
