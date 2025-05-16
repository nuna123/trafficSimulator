import java.io.*;
import java.util.*;

public class Main {
	private static String CONFIGFILE = "./config.properties";

	public static void readConfigFile ()
	{
		try{
			FileInputStream propsInput = new FileInputStream(CONFIGFILE);
			Properties prop = new Properties();
			prop.load(propsInput);
	
			System.out.print(prop);
		}
		catch ( FileNotFoundException e){System.out.print(e);}
		catch (Exception e){System.out.print(e);}
	}



	public static void main(String[] args) {

		System.out.println("Traffic Simulator started.");
		ConfigReader cr = null;
		try {
			cr = new ConfigReader(CONFIGFILE);
		} catch (Exception e)
		{
			System.out.println("HERE");
			System.out.print(e);
			System.exit(1);
		}
		
		cr.readConfigFile();
		Map<String, Float> immod_mappedConf = cr.getMappedConfig();

		System.out.print(immod_mappedConf);


	}
}