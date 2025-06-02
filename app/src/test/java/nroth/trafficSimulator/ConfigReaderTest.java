package nroth.trafficSimulator;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ConfigReaderTest {

	// defining properties file names
	private static final String VALID_CONFIG = "test_valid.properties";
	private static final String MISSING_KEY_CONFIG = "test_missing_key.properties";
	private static final String INVALID_VALUE_CONFIG = "test_invalid_value.properties";
	private static final String NON_INTEGER_CONFIG = "test_non_integer.properties";
	private static final String NEGATIVE_VALUE_CONFIG = "test_negative_value.properties";


	@Test
	public void testValidConfig() throws Exception {
		System.out.println(VALID_CONFIG);
		ConfigReader reader = new ConfigReader(VALID_CONFIG);
		Map<String, Integer> config = reader.readConfigFile();

		assertNotNull(config);
		assertEquals(7, config.size());

		assertEquals(Integer.valueOf(10), config.get("X1"));
		assertEquals(Integer.valueOf(12), config.get("X2"));
		assertEquals(Integer.valueOf(5), config.get("S"));
		assertEquals(Integer.valueOf(1), config.get("A1"));
		assertEquals(Integer.valueOf(2), config.get("A2"));
		assertEquals(Integer.valueOf(3), config.get("A3"));
		assertEquals(Integer.valueOf(4), config.get("A4"));
	}

	//file is missing A4 entry
	@Test
	public void testMissingKey() throws Exception {
		ConfigReader reader = new ConfigReader(MISSING_KEY_CONFIG);
		assertThrows(ConfigReader.MissingKeyException.class, () -> {
			reader.readConfigFile();
		});
	}

	@Test
	public void testInvalidValue() throws Exception {
		ConfigReader reader = new ConfigReader(INVALID_VALUE_CONFIG);
		assertThrows(ConfigReader.InvalidValueException.class, () -> {
			reader.readConfigFile();
		});
	}

	@Test
	public void testNonIntegerValue() throws Exception {
		ConfigReader reader = new ConfigReader(NON_INTEGER_CONFIG);
		assertThrows(NumberFormatException.class, () -> {
			reader.readConfigFile();
		});
	}

	@Test
	public void testNegativeValue() throws Exception {
		assertThrows(ConfigReader.InvalidValueException.class, () -> {
			ConfigReader cr = new ConfigReader(NEGATIVE_VALUE_CONFIG);
			cr.readConfigFile();
		});
	}

	@Test
	public void testFileNotFound() throws Exception {
		assertThrows(FileNotFoundException.class, () -> {
			new ConfigReader("NON_EXISTENT_CONFIG");
		});
	}


}