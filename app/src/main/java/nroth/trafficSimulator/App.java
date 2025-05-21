package nroth.trafficSimulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

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
		}

		return cr.getMappedConfig();
	}



	public static void main(String[] args) {

		System.out.println("Traffic Simulator started.");

		Map <String, Integer> config = getConfig();
		if (config == null){
			System.out.println("config is null, exiting....");
			return;
		}
		System.out.println("CONFIG:" + config);


		JunctionController jc = new JunctionController(config);

		try{
			jc.addCar('N', 3);
			jc.addCar('E', 2);
			jc.addCar('S', 8);
			jc.addCar('W', 13);
		}
		catch (Exception e){System.out.println(e);}


		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> jc.tick();

		int tickInterval = 1; //1sec

		scheduler.scheduleAtFixedRate(task, 0, tickInterval, TimeUnit.SECONDS); 
	}
}