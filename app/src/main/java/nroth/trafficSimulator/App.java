package nroth.trafficSimulator;

import java.util.Map;

public class App {
	private static String CONFIGFILE = "config.properties";

	static Map <String, Integer>  getConfig(){
		ConfigReader cr = null;
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



	public static void main(String[] args) {

		System.out.println("Traffic Simulator started.");

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


		jc.start(15);

		jc.printJunction();



	}
}