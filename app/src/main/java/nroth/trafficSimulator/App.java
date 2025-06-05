package nroth.trafficSimulator;

import java.util.Map;


public class App {
	private static final String CONFIGFILE = "config.properties";

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

 public static void main(String[] args) {


		Map <String, Integer> config = getConfig();
		if (config == null){
			System.out.println("\nconfig is null, exiting....");
			return;
		}
		JunctionController.log("MAIN: CONFIG:" + config);

		JunctionController jc = new JunctionController(config);

		try{
			jc.addCar('N', 3);
			jc.addCar('E', 2);
			jc.addCar('S', 8);
			jc.addCar('W', 13);
		}
		catch (Exception e){System.out.println(e); return;}


		jc.start(-1);
		JunctionController.log("MAIN: Back in main. exiting ...");
	}
}