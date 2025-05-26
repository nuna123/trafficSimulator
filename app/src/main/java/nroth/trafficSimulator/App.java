package nroth.trafficSimulator;

import java.util.Map;
import java.util.concurrent.TimeUnit;


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
		System.out.println("MAIN: CONFIG:" + config);


		JunctionController jc = new JunctionController(config);

		// try{
		// 	jc.addCar('N', 3);
		// 	jc.addCar('E', 2);
		// 	jc.addCar('S', 8);
		// 	jc.addCar('W', 13);
		// }
		// catch (Exception e){System.out.println(e);}


		jc.start(-1);


		try {
			jc.scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			JunctionController.printToLog("FROM MAIN: Main thread interrupted.");
		}

		// System.out.print(jc.summary());
		JunctionController.printToLog("MAIN: Back in main. exiting ...");

	}
}