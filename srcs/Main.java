import java.util.*;

public class Main {
	private static String CONFIGFILE = "./config.properties";

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

		Road road = new Road(config.get("S"));
		for (int i = 0;++i <= 10;){
			if (i%2 == 0)
				road.addCar(1);
			else
				road.addCar(2);
			}

		System.out.println("ROAD QUEUELEN: " + road.getQueueLen());
		System.out.println( road.greenLight(config.get("X1")));
		System.out.println("ROAD QUEUELEN: " + road.getQueueLen());



	}
}