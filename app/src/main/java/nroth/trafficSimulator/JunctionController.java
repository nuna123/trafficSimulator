package nroth.trafficSimulator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * JUNCTIONCONTROLER
 * controlls the whole junction and phase switching.
 *	- phase switching
 	- timing (scheduling)
	- car arrivals
	- logging
 **/
public class JunctionController {
	
	public enum PhaseValue { //pre-set values for each phase
		NS_GREEN, EW_GREEN
	}
	private final Map<String, Integer> _config;	// config map obtained from ConfigReader class
	private final int[] _carArrivals;			// timing of car arrival to roads
	private final Road[] _roads;				// Road class objects, array of 4 - one for each compass direction
	private final JunctionPhase _currentPhase;	// Custom class containing all information about the current phase
	private final Object threadLock = new Object(); // threadLock for thread running the task scheduler
	private static final Logger _logger = LoggerFactory.getLogger(App.class); // Logger - static so can be used without initialization
	private int _totalCarsPassed;				// counter for total cars that safely pass through the junction
	private static int _elapsedTime = 0;		// elapsed time since beginning of simulation


	public Road[] getRoads() {return _roads;}
	public Map<String, Integer> getConfig() {return (_config == null ? null : Collections.unmodifiableMap(_config));}
	public JunctionPhase getPhase(){return this._currentPhase;}

	public JunctionController(Map<String, Integer> config) {
		_config = new HashMap<>(config);
		_currentPhase = new JunctionPhase();

		_roads = new Road[4];
		String[] roadnames = {"North", "East", "South", "West"};
		for (int i = 0; i < 4; i++)
			_roads[i] = new Road(_config.get("S"), roadnames[i]);

		_carArrivals = new int[4];
		for (int i = 1; i <= 4; i++)
			_carArrivals[i - 1] = _config.get("A" + String.valueOf(i));
	}

	/**
	 * Add car to a specified road.
	 * @param roadChar		[N, E, S, W]
	 * @throws Exception	if roadchar is invalid
	 */
	public void addCar(char roadChar)
			throws Exception {
		this.addCar(roadChar, 1);
	}

	/**
	 * Adds a specific number of cars to the queue of the specified road.
	 *
	 * @param roadChar		[N, E, S, W]
	 * @param carNum		number of cars to add
	 * @throws Exception	if roadchar is invalid
	 */
	public void addCar(char roadChar, int carNum)
			throws Exception {
		Road myRoad = (switch (roadChar) {
			case 'N' -> _roads[0];
			case 'E' -> _roads[1];
			case 'S' -> _roads[2];
			case 'W' -> _roads[3];
			default -> null;
		});
		if (myRoad == null)
			throw new Exception("Invalid value: " + roadChar);

		for (int i = 0; i < carNum; i++)
			myRoad.addCar();
	}

	/**
	 * Returns the current state of the junction as a map.
	 *
	 * @return A map containing phase, elapsed time, cars on road, total cars passed, and road queue sizes.
	 */
	public Map<String, Object> getJunctionState() {
		Map<String, Object> junctionState = new HashMap<>();

		junctionState.put("currentPhase", _currentPhase.phase.name());
		junctionState.put("elapsedTime", _elapsedTime);
		junctionState.put("carsOnRoad", _currentPhase.carsOnRoad);
		junctionState.put("totalCarsPassed", _totalCarsPassed);

		junctionState.put("roadQueues", Map.of(
				"North", _roads[0].getQueueLen(),
				"East", _roads[1].getQueueLen(),
				"South", _roads[2].getQueueLen(),
				"West", _roads[3].getQueueLen(),
				"Total", Arrays.stream(_roads)
						.mapToInt(road -> road.getQueueLen())
						.sum()

		));

		return junctionState;

	}

	/**
	 * get Junction summary
	 * @return
	 */
	public String summary() {
		String out = "";

		Map<String, Object> js = getJunctionState();
		@SuppressWarnings("unchecked") //is safe! this mapis only created once. is always <String, Integer>
		Map<String, Integer> queues = (Map<String, Integer>) js.get("roadQueues");

		out += ("\n---- JUNCTION SUMMARY ----\n");
		out += String.format("Elapsed time: %d\n", js.get("elapsedTime"));
		out += String.format("Cars on road: %d\n", js.get("carsOnRoad"));
		out += ("Current Queues:\n");
		out += String.format("  North: %d\n", queues.get("North"));
		out += String.format("  East:  %d\n", queues.get("East"));
		out += String.format("  South: %d\n", queues.get("South"));
		out += String.format("  West:  %d\n", queues.get("West"));
		out += String.format("Total cars in junction: %d\n", queues.get("Total"));
		out += String.format("Total cars passed in junction: %d\n", js.get("totalCarsPassed"));
		out += ("--------------------------");

		return out;
	}

	/**
	 * logs and prints a formatted version of msg with time
	 * Is static to allow printing using the correct time value without instantiating JunctionController
	 * @param msg
	 */
	public static void log (String msg)
	{
		String fullMessage = String.format("[%ds]\t%s", _elapsedTime, msg);

		_logger.info(fullMessage);
		System.out.println(fullMessage);
	}

	public static void log (String msg, String style)
	{
		String fullMessage = String.format("[%ds]\t%s", _elapsedTime, msg);
		
		_logger.info(fullMessage);
		System.out.println(style + fullMessage + "\033[0m");
	}

	/**
	 * logs a formatted version of msg with time, as debug message
	 * Is static to allow printing using the correct time value without instantiating JunctionController
	 * @param msg
	 */
	public static void printDebug (String msg)
	{
		_logger.debug(String.format("[%ds]\t%s", _elapsedTime, msg));
	}

	/**
	 * handles phase switching, evaluates if switch is needed - if cars are waiting on the perpendicular road
	 */
	private void switchPhase ()
	{

		int secondRoadOffset;
		String phaseSwitchMsg = "--------Phase switch!-----------------\n";
		phaseSwitchMsg += ("\tlast phase overview: \n\t\t" + _currentPhase + "\n");
		this._totalCarsPassed += _currentPhase.carsPassed;

		secondRoadOffset = (_currentPhase.phase == PhaseValue.NS_GREEN ? 1 : 0);
		//if there are cars on the other side
		if (_roads[0 + secondRoadOffset].getQueueLen() + _roads[2 + secondRoadOffset].getQueueLen() > 0)
			_currentPhase.switchPhase();
		else
		{
			phaseSwitchMsg += ("[!] Phase not switched! no cars on other road.\n");
			_currentPhase.resetPhase();
		}

		phaseSwitchMsg += ("\tNew phase: " + _currentPhase.phase.name()+ "\n");

		phaseSwitchMsg += (String.format("\tCar Queues:\tN [%d] ; E [%d] ; S [%d] ; W :[%d]\n",
				_roads[0].getQueueLen(),
				_roads[1].getQueueLen(),
				_roads[2].getQueueLen(),
				_roads[3].getQueueLen()));
		
		JunctionController.log(phaseSwitchMsg, "\033[1m"); // add bold style

	}

	/**
	 * A function to be run every second(1 tick) of the function
	 * handles car passage, phase switching, car arrivals
	 */
	public void tick() {
		_elapsedTime++;

		Map<String, Integer> res1;
		Map<String, Integer> res2;

		int roadOffset = (_currentPhase.phase == PhaseValue.NS_GREEN ? 0 : 1);
		res1 = _roads[0 + roadOffset].greenLight_tick(_currentPhase.len - _currentPhase.phaseTimer);
		res2 = _roads[2 + roadOffset].greenLight_tick(_currentPhase.len - _currentPhase.phaseTimer);
		roadOffset = (roadOffset == 1 ? 0 : 1);
		_roads[0 + roadOffset].advancePassedCars();
		_roads[2 + roadOffset].advancePassedCars();

		JunctionController.printDebug(String.format("road states: {\n\t[%s], \n\t[%s]}", _roads[0 + roadOffset].toString(),_roads[2 + roadOffset].toString()));

		// sets carsOnRoad, carsPassed, phasetimer
		_currentPhase.update(res1, res2);

		// handle phase switching
		if (_currentPhase.phaseTimer >= _currentPhase.len)
			switchPhase();
		// handle car arrivals
		for (int idx = 0; idx < _carArrivals.length; idx++) {
			if (_carArrivals[idx] > 0 && _elapsedTime % _carArrivals[idx] == 0)
				_roads[idx].addCar();
		}
	}


	/**
	 * Starts the simulation, runs {@link tick} every second.
	 * @param timeLimit_sec	simulation time limit in seconds. -1 for indefinite
	 * @exception IllegalArgumentException timeLimit_sec cannot be smaller than -1
	 */
	public void start(int timeLimit_sec)
			throws IllegalArgumentException
	{
		if (timeLimit_sec < -1)
		{
			throw new IllegalArgumentException
				("timeLimit_sec must bea positive int, or -1 for indefinite run.");
		}

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Animator[] animator = new Animator[1];
		try {
			animator[0] = new Animator();
		} catch (Exception e) {
			System.exit(-1);
		}
		if (animator[0] != null)
			try {
				animator[0].configureFrame(this);
			} catch (IOException e) {
				JunctionController.log("ERR from Animator.configureFrame: "); 
				e.printStackTrace();
				System.exit(-1);
			}

		// Add shutdown hook to catch Ctrl+C
		// Note: Gradle runtime environment interferes with this, output will be shown in logs but not console
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (JunctionController._elapsedTime < timeLimit_sec)
			{
				JunctionController.log("[!] Non-peaceful termination: Ctrl+C");
				animator[0].shutdown();
				scheduler.shutdownNow();
			}
			JunctionController.log(this.summary());
		}));

		// the synchronized block makes sure only 1 thread can run this runnable at a
		// time
		// Any other thread approaching this, and using the same *monitoring object*
		// (this.threadLock) will be blocked.
		// note: if another JunctionController is initialized, it will have a different
		// threadLock instantiation and would be able to run.

		Runnable task = () -> {
			synchronized (this.threadLock) {
				if (timeLimit_sec == -1 || JunctionController._elapsedTime < timeLimit_sec)
					{
						this.tick();
						try {
							animator[0].configureFrame(this);
						} catch (IOException e) {
							JunctionController.log("ERR from Animator.configureFrame: "); 
							e.printStackTrace();
							System.exit(-1);
						}
					}
				else{
					scheduler.shutdown();
					animator[0].shutdown();
					JunctionController.log("[!] Time limit reached! Shutting down scheduler.");
				}
			}
		};

		JunctionController.log("From JunctionController: starting simulation...");
		int tickInterval = 1; // 1sec

		scheduler.scheduleAtFixedRate(task, 0, tickInterval, TimeUnit.SECONDS);

		try {
			if (timeLimit_sec == -1)
				scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			else
				scheduler.awaitTermination(timeLimit_sec + 2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.print(e);
		}
	}

	/**
	 * represents the current traffic phase.
	 * default is NS_GREEN
	 * contains phase timing, length, and information about cars on the road
	 */
	public class JunctionPhase {
		public PhaseValue phase;
		public int len;
		public int phaseTimer;
		public int carsPassed;
		public int carsOnRoad;

		public void resetPhase()
		{
			this.phaseTimer = this.carsPassed = this.carsOnRoad = 0;
			this.len = getPhaseLen();
		}
		/**
		 * initializes to NS_GREEN, loads values
		 */
		public JunctionPhase() {
			this.phase = PhaseValue.NS_GREEN;
			this.phaseTimer = this.carsPassed = this.carsOnRoad = 0;
			this.len = getPhaseLen();
		}
		/**
		 * gets the correct phase len for the current phase
		 * gets information from _config
		 * @return phase length
		 */
		private int getPhaseLen() {
			return (this.phase == PhaseValue.NS_GREEN ? _config.get("X1") : _config.get("X2"));
		}


		/**
		 * function to switch between NS_GREEN / EW_GREEN
		 * resets timer, cars information
		 */
		public void switchPhase() {
			this.phase = (phase == PhaseValue.NS_GREEN ? PhaseValue.EW_GREEN : PhaseValue.NS_GREEN);
			resetPhase();
		}

		/**
		 * updates car values based on information recieved from roads.
		 * @param road1
		 * @param road2
		 */
		public void update(Map<String, Integer> road1, Map<String, Integer> road2) {
			carsPassed += road1.get("carsPassed");
			carsPassed += road2.get("carsPassed");

			carsOnRoad = road1.get("carsOnRoad") + road2.get("carsOnRoad");
			this.phaseTimer++;

		}

		@Override
		public String toString() {
			return String.format("Phase: %s; len: %d; timer: %d; carsPassed: %d; carsOnRoad: %d", this.phase.name(),
					this.len, this.phaseTimer, this.carsPassed, this.carsOnRoad);
		}

	}



}
