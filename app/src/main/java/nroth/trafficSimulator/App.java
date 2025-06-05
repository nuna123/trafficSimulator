package nroth.trafficSimulator;


import nroth.trafficSimulator.ConfigReader.Config;


public class App {
	private static final String CONFIGFILE = "config.properties";

	static Config getConfig(){
		ConfigReader cr;
		Config config = null;
		try {
			cr = new ConfigReader(CONFIGFILE);
			config = cr.readConfigFile();
		} catch (Exception e)
		{
			System.out.print(e);
			return null;
		}

		return config;
	}

 public static void main(String[] args) {


		var config = getConfig();
		if (config == null){
			System.out.println("\nconfig is null, exiting....");
			return;
		}
		JunctionController.log("MAIN: CONFIG:" + config);

		var jc = new JunctionController(config);

		try{
			jc.addCar('N', 3);
			jc.addCar('E', 2);
			jc.addCar('S', 8);
			jc.addCar('W', 13);
		}
		catch (Exception e){System.out.println(e); return;}


		jc.start(1);


		JunctionController.log("MAIN: Back in main. exiting ...");
	}
}