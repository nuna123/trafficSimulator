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
	private static List<String> _requiredKeys = Arrays.asList(
			"X1",
			"X2",
			"S",
			"A1",
			"A2",
			"A3",
			"A4");

	public record Config (Integer X1, Integer X2, Integer S, Integer A1, Integer A2, Integer A3, Integer A4){};

	public String getFilePath (){return _filePath;}

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
			throw new FileNotFoundException("%s: Config file not found!".formatted(path));
		}
		_filePath = path;
		inputStream.close();
	}

	/**
	 * fills the config record from Properties file.
	 * By this point we know properties file has all fields and they are integers
	 * @param properties
	 * @return Config variable
	 */
	public Config fillConfig(Properties properties)
	{
		Config c = new Config(Integer.parseInt(
			properties.getProperty("X1")),
			Integer.parseInt (properties.getProperty("X2")),
			Integer.parseInt (properties.getProperty("S")),
			Integer.parseInt (properties.getProperty("A1")),
			Integer.parseInt (properties.getProperty("A2")),
			Integer.parseInt (properties.getProperty("A3")),
			Integer.parseInt (properties.getProperty("A4"))
			);

		return c;
	}

	/**
	 * makes sure all required values are present and are integers
	 * validates value are valid
	 * @param config	config Map to read from
	 * @throws MissingKeyException
	 * @throws InvalidValueException
	 */
	private Config validateValues (Properties properties)
				throws MissingKeyException, InvalidValueException
	{
		// check that all needed values exist, all are integers
		for (String key : _requiredKeys) {
			if (!properties.containsKey(key))
				throw new MissingKeyException("Missing Key %s in properties file!".formatted(key));
			try{
				Integer.valueOf(properties.getProperty(key));
			}
			catch(NumberFormatException e){
				throw new NumberFormatException( "%s could not be converted to integer!\n".formatted(properties.getProperty(key)));
			}
		}
		Config c = fillConfig(properties);

		//only A[1-4] can be smaller than 1
		// A[1-4] can be a positive integer, or -1.
		Integer value;
		for (String key : _requiredKeys) {
			value = Integer.parseInt((String) properties.get(key));

			if (!key.startsWith("A") && value < 1)
				throw new InvalidValueException("Value of %s cannot less than 1!".formatted(key));
			else if (key.startsWith("A") && value != -1 && value < 1)
				throw new InvalidValueException("Value of %s can be a positive integer, or -1.".formatted(key));
		}

		// S cannot be larger than X1/X2, how would a car cross the road?
		if (c.S() > c.X1() || c.S() > c.X2())
			throw new InvalidValueException ("S cannot be larger than X1/X2!");

		return c;
	}

	/**
	 * Reads the config file into a Properties object,
	 * from Properties to Map<String, Integer>
	 * Keys are validated to make sure nothing is missing
	 *
	 * @return Map<String, Integer>
	 */
	public Config readConfigFile()
		throws MissingKeyException,
		InvalidValueException,
		NumberFormatException,
		FileNotFoundException,
		IOException
	{
		InputStream propsInput = App.class.getClassLoader().getResourceAsStream(_filePath);
		Properties prop = new Properties();
		prop.load(propsInput);

		Config config = validateValues(prop);

		propsInput.close();

		return config;
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
