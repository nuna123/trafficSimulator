import java.io.*;
import java.util.*;

/**
 * The ConfigReader class is responsible for reading and validating configuration files.
 * It loads key-value pairs from a properties file, ensures that all required keys are present,
 * and converts the values to Floats.
 */
public class ConfigReader {

	private String _filePath;
	private Map<String, Float> _mappedConfig = null;
	private static List<String> _requiredKeys = Arrays.asList(
			"X1",
			"X2",
			"S",
			"A1",
			"A2",
			"A3",
			"A4");


	public String getFilePath (){return _filePath;}
	public Map<String, Float> getMappedConfig (){return (_mappedConfig == null ? null : Collections.unmodifiableMap(_mappedConfig));}
	public List<String> getRequiredKeys (){ return Collections.unmodifiableList(_requiredKeys);}

	/**
	 * Constructor for ConfigReader. Accepts filepath to a config file, makes sure the file exists.
	 * @param path	Path to the config file
	 * @throws FileNotFoundException
	 */
	public ConfigReader(String path) throws FileNotFoundException {
		_filePath = path;

		File f = new File(path);
		if (!f.exists() || f.isDirectory()) {
			throw new FileNotFoundException(path + ": Config file not found!");
		}
	}

	/**
	 * makes sure all required values are present.
	 * validates value are valid
	 * @param config	config Map to read from
	 * @throws MissingKeyException
	 * @throws InvalidValueException
	 */
	private void validateValues (Map<String, Float> config)
				throws MissingKeyException, InvalidValueException
	{
		// check that all needed values exist
		for (String key : _requiredKeys) {
			if (!config.containsKey(key))
				throw new MissingKeyException("Missing Key " + key + " in config file!");
		}

		// S cannot be larger than X1/X2, how would a car cross the road?
		if (config.get("S") > config.get("X1")
			|| config.get("S") > config.get("X2"))
		{ throw new InvalidValueException ("S cannot be larger than X1/X2!"); }

	}

	/**
	 * Converts the given Properties object to Map <String, Float>.
	 * Validates values using validateValues() function
	 * 
	 * @param properties The Properties object to convert from
	 * @return Map<String, Float>
	 * @throws MissingKeyException (from ValidateValues)
	 * @throws InvalidValueException (from ValidateValues)
	 * @throws NumberFormatException
	 */
	private Map<String, Float> propertiesToMap(Properties properties)
			throws MissingKeyException, InvalidValueException, NumberFormatException {
		Map<String, Float> floatMap = new HashMap<>();

		
		for (String key : properties.stringPropertyNames()) {
			Float f;
			try{f = Float.valueOf(properties.getProperty(key));}
			catch(NumberFormatException e){
				throw new NumberFormatException(properties.getProperty(key) + " could not be converted to float!\n");
			}
			floatMap.put(key, f);
		}
		validateValues(floatMap);
		return floatMap;
	}

	/**
	 * Reads the config file into a Properties object,
	 * from Properties to Map<String, Float>
	 * Keys are validated to make sure nothing is missing
	 * 
	 * @return
	 */
	public Map<String, Float> readConfigFile() {
		try {
			FileInputStream propsInput = new FileInputStream(_filePath);
			Properties prop = new Properties();
			prop.load(propsInput);
			_mappedConfig = propertiesToMap(prop);
			propsInput.close();
		} catch (FileNotFoundException e) {
			System.out.print(e);
			return Collections.emptyMap();
		} catch (Exception e) {
			System.out.print(e);
			return Collections.emptyMap();
		}
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
			super("A key is missing in the data set!");
		}
	}

}
