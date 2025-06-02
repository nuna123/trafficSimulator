package nroth.trafficSimulator;

import java.io.*;
import java.util.*;

/**
 * The ConfigReader class is responsible for reading and validating configuration files.
 * It loads key-value pairs from a properties file, ensures that all required keys are present,
 * and converts the values to Ints.
 */
public class ConfigReader {

	private String _filePath = null;
	private Map<String, Integer> _mappedConfig = null;
	private static List<String> _requiredKeys = Arrays.asList(
			"X1",
			"X2",
			"S",
			"A1",
			"A2",
			"A3",
			"A4");


	public String getFilePath (){return _filePath;}
	public Map<String, Integer> getMappedConfig (){return (_mappedConfig == null ? null : Collections.unmodifiableMap(_mappedConfig));}
	public List<String> getRequiredKeys (){ return Collections.unmodifiableList(_requiredKeys);}

	/**
	 * Constructor for ConfigReader. Accepts filepath to a config file, makes sure the file exists.
	 * @param path	Path to the config file
	 * @throws FileNotFoundException
	 */
	public ConfigReader(String path)
			throws FileNotFoundException, IOException
	{
		InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(path);
		// InputStream inputStream = getClass().getResource(path).openStream();
		if (inputStream == null) {
			throw new FileNotFoundException(path + ": Config file not found!");
		}
		_filePath = path;
		inputStream.close();
	}

	/**
	 * makes sure all required values are present.
	 * validates value are valid
	 * @param config	config Map to read from
	 * @throws MissingKeyException
	 * @throws InvalidValueException
	 */
	private void validateValues (Map<String, Integer> config)
				throws MissingKeyException, InvalidValueException
	{
		// check that all needed values exist
		for (String key : _requiredKeys) {
			if (!config.containsKey(key))
				throw new MissingKeyException("Missing Key " + key + " in config file!");
		}

		
		//only A[1-4] can be smaller than 1
		// A[1-4] can be a positive integer, or -1.
		for (String key : _requiredKeys) {
			if (!key.startsWith("A") && config.get(key) < 1)
				throw new InvalidValueException("Value of " + key + " cannot less than 1!");
			else if (key.startsWith("A") && config.get(key) != -1 && config.get(key) < 1)
				throw new InvalidValueException("Value of " + key + " can be a positive integer, or -1.");
		}

		// S cannot be larger than X1/X2, how would a car cross the road?
		if (config.get("S") > config.get("X1")
			|| config.get("S") > config.get("X2"))
		{ throw new InvalidValueException ("S cannot be larger than X1/X2!"); }

	}

	/**
	 * Converts the given Properties object to Map <String, Integer>.
	 * Validates values using validateValues() function
	 *
	 * @param properties The Properties object to convert from
	 * @return Map<String, Integer>
	 * @throws MissingKeyException (from ValidateValues)
	 * @throws InvalidValueException (from ValidateValues)
	 * @throws NumberFormatException
	 */
	private Map<String, Integer> propertiesToMap(Properties properties)
			throws MissingKeyException, InvalidValueException, NumberFormatException {
		Map<String, Integer> intMap = new HashMap<>();


		for (String key : properties.stringPropertyNames()) {
			Integer num;
			try{
				num = Integer.valueOf(properties.getProperty(key));
			}
			catch(NumberFormatException e){
				throw new NumberFormatException(properties.getProperty(key) + " could not be converted to integer!\n");
			}
			intMap.put(key, num);
		}
		validateValues(intMap);
		return intMap;
	}

	/**
	 * Reads the config file into a Properties object,
	 * from Properties to Map<String, Integer>
	 * Keys are validated to make sure nothing is missing
	 *
	 * @return Map<String, Integer>
	 */
	public Map<String, Integer> readConfigFile()
		throws MissingKeyException,
		InvalidValueException,
		NumberFormatException,
		FileNotFoundException,
		IOException
	{
		InputStream propsInput = App.class.getClassLoader().getResourceAsStream(_filePath);
		Properties prop = new Properties();
		prop.load(propsInput);
		_mappedConfig = propertiesToMap(prop);
		propsInput.close();

		return _mappedConfig;
	}


	// CUSTOM EXCEPTIONS
	class MissingKeyException extends Exception {
		public MissingKeyException(String message) {
			super(message);
		}
		public MissingKeyException() {
			super("A key is missing in the data set!");
		}
	}

	class InvalidValueException extends Exception {
		public InvalidValueException(String message) {
			super(message);
		}
		public InvalidValueException() {
			super("A key is invalid in the data set!");
		}
	}

}
