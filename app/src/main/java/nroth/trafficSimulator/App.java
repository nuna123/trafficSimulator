package nroth.trafficSimulator;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {


	private static final String CONFIGFILE = "config.properties";
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	static Map <String, Integer>  getConfig(){
		ConfigReader cr;
		try {
			cr = new ConfigReader(CONFIGFILE);
			cr.readConfigFile();
		} catch (Exception e)
		{
			System.out.print(e);
			return null;
		}

		return cr.getMappedConfig();
	}

	public static void log_info (String msg)
	{
		logger.info(msg);
	}

	public static void main(String[] args) {


		Map <String, Integer> config = getConfig();
		if (config == null){
			System.out.println("\nconfig is null, exiting....");
			return;
		}
		System.out.println("CONFIG:" + config);


		JunctionController jc = new JunctionController(config);

		// try{
		// 	jc.addCar('N', 3);
		// 	jc.addCar('E', 2);
		// 	jc.addCar('S', 8);
		// 	jc.addCar('W', 13);
		// }
		// catch (Exception e){System.out.println(e);}


		JunctionController.printToLog("Traffic Simulator starting.");
		jc.start();
		jc.printJunction();

		JunctionController.printToLog("Traffic Simulator finished");



	}
}