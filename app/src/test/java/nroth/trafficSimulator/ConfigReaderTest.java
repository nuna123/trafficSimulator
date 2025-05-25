package nroth.trafficSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigReaderTest {

	private static final String VALID_CONFIG = "test_valid.properties";
	private static final String MISSING_KEY_CONFIG = "test_missing_key.properties";
	private static final String INVALID_VALUE_CONFIG = "test_invalid_value.properties";
	private static final String NON_INTEGER_CONFIG = "test_non_integer.properties";
	private static final String NON_EXISTENT_CONFIG = "does_not_exist.properties";

	@BeforeEach
	public void setUp() throws Exception {
		createPropertiesFile(VALID_CONFIG, new Properties() {{
			setProperty("X1", "10");
			setProperty("X2", "12");
			setProperty("S", "5");
			setProperty("A1", "1");
			setProperty("A2", "2");
			setProperty("A3", "3");
			setProperty("A4", "4");
		}});
		createPropertiesFile(MISSING_KEY_CONFIG, new Properties() {{
			setProperty("X1", "10");
			setProperty("X2", "12");
			setProperty("S", "5");
			setProperty("A1", "1");
			setProperty("A2", "2");
			setProperty("A3", "3");
			// A4 is missing
		}});
		createPropertiesFile(INVALID_VALUE_CONFIG, new Properties() {{
			setProperty("X1", "10");
			setProperty("X2", "12");
			setProperty("S", "15"); // S > X1 & X2
			setProperty("A1", "1");
			setProperty("A2", "2");
			setProperty("A3", "3");
			setProperty("A4", "4");
		}});
		createPropertiesFile(NON_INTEGER_CONFIG, new Properties() {{
			setProperty("X1", "7");
			setProperty("X2", "12");
			setProperty("S", "S");
			setProperty("A1", "1");
			setProperty("A2", "2");
			setProperty("A3", "3");
			setProperty("A4", "4");
		}});
	}

	@AfterEach
	public void deleteFiles() {
		deleteFileFromResources(VALID_CONFIG);
		deleteFileFromResources(MISSING_KEY_CONFIG);
		deleteFileFromResources(INVALID_VALUE_CONFIG);
		deleteFileFromResources(NON_INTEGER_CONFIG);
	}

	@Test
	public void testReadConfigFile_ValidConfig() throws Exception {
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

	@Test
	public void testReadConfigFile_MissingKey() throws Exception {
		ConfigReader reader = new ConfigReader(MISSING_KEY_CONFIG);
		assertThrows(ConfigReader.MissingKeyException.class, () -> {
			reader.readConfigFile();
		});
	}

	@Test
	public void testReadConfigFile_InvalidValue() throws Exception {
		ConfigReader reader = new ConfigReader(INVALID_VALUE_CONFIG);
		assertThrows(ConfigReader.InvalidValueException.class, () -> {
			reader.readConfigFile();
		});
	}

	@Test
	public void testReadConfigFile_NonIntegerValue() throws Exception {
		ConfigReader reader = new ConfigReader(NON_INTEGER_CONFIG);
		assertThrows(NumberFormatException.class, () -> {
			reader.readConfigFile();
		});
	}

	@Test
	public void testReadConfigFile_FileNotFound() throws Exception {
		assertThrows(FileNotFoundException.class, () -> {
			new ConfigReader(NON_EXISTENT_CONFIG);
		});
	}

	// Helper methods to create/delete test property files in test resources
	private void createPropertiesFile(String fileName, Properties props) throws IOException {
		File file = new File(getClass().getClassLoader().getResource(".").getFile(), fileName);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			props.store(fos, null);
		}
	}

	private void deleteFileFromResources(String fileName) {
		File file = new File(getClass().getClassLoader().getResource(".").getFile(), fileName);
		if (file.exists()) {
			file.delete();
		}
	}
}