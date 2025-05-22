package nroth.trafficSimulator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JunctionController {

	public enum PhaseValue {
		NS_GREEN, EW_GREEN
	}

	private final Map<String, Integer> _config;
	private final int[] _carArrivals;
	private final Road[] _roads;
	private final JunctionPhase _currentPhase;
	private final Object threadLock = new Object();

	private int _totalCarsPassed;
	private static int _elapsedTime = 0;


	public Road[] getRoads() {return _roads;}
	public Map<String, Integer> getConfig() {return (_config == null ? null : Collections.unmodifiableMap(_config));}

	public JunctionController(Map<String, Integer> config) {
		_config = new HashMap<>(config);
		_currentPhase = new JunctionPhase();

		_roads = new Road[4];
		for (int i = 0; i < 4; i++)
			_roads[i] = new Road(_config.get("S"));

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
			case 'W' -> _roads[1];
			case 'S' -> _roads[2];
			case 'E' -> _roads[3];
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
	 * prints a formatted version of msg with time
	 * Is static to allow printing using the correct time value without instantiating
	 * @param msg
	 */
	public static void print (String msg)
	{
		System.out.printf("[%ds]\t%s\n", _elapsedTime, msg);
	}

	/**
	 * Prints a summary of the junction including queues and statistics.
	 */
	@SuppressWarnings("unchecked") // map is created consistently with above function
	public void printJunction() {

		Map<String, Object> js = getJunctionState();
		Map<String, Integer> queues = (Map<String, Integer>) js.get("roadQueues");

		System.out.println("\n---- JUNCTION SUMMARY ----");
		System.out.printf("Elapsed time: %d\n", js.get("elapsedTime"));
		System.out.printf("Cars on road: %d\n", js.get("carsOnRoad"));
		System.out.println("Current Queues:");
		System.out.printf("  North: %d\n", queues.get("North"));
		System.out.printf("  East:  %d\n", queues.get("East"));
		System.out.printf("  South: %d\n", queues.get("South"));
		System.out.printf("  West:  %d\n", queues.get("West"));
		System.out.printf("Total cars in junction: %d\n", queues.get("Total"));
		System.out.printf("Total cars passed in junction: %d\n", js.get("totalCarsPassed"));
		System.out.println("--------------------------");

	}

	/**
	 * A function to be run every second(1 tick) of the function
	 * handles car passage, phase switching, car arrivals
	 * @param timeLimit_sec
	 */
	public void tick(int timeLimit_sec) {
		_elapsedTime++;

		Map<String, Integer> res1;
		Map<String, Integer> res2;

		int roadOffset = (_currentPhase.phase == PhaseValue.NS_GREEN ? 0 : 1);
		res1 = _roads[0 + roadOffset].greenLight_tick(_currentPhase.len - _currentPhase.phaseTimer);
		res2 = _roads[2 + roadOffset].greenLight_tick(_currentPhase.len - _currentPhase.phaseTimer);

		// sets carsOnRoad, carsPassed, phasetimer
		_currentPhase.update(res1, res2);

		// handle phase switching
		if (_currentPhase.phaseTimer >= _currentPhase.len) {

			JunctionController.print("--------Phase switch!");
			System.out.println("\tphase overview: " + _currentPhase);
			this._totalCarsPassed += _currentPhase.carsPassed;

			_currentPhase.switchPhase();

			System.out.println("\tNew phase: " + _currentPhase);

			System.out.printf("\tCar Queues: \n\t\tNorth(%d) ; East(%d) ; South(%d) ; West:(%d)\n",
					_roads[0].getQueueLen(),
					_roads[1].getQueueLen(),
					_roads[2].getQueueLen(),
					_roads[3].getQueueLen());

		}

		// handle car arrivals
		for (int idx = 0; idx < _carArrivals.length; idx++) {
			if (_carArrivals[idx] > 0 && _elapsedTime % _carArrivals[idx] == 0) {
				Car car = _roads[idx].addCar();
				String[] dirs = { "North", "East", "South", "West" };
				JunctionController.print("Car Arrived from " + dirs[idx] +": " + car.plate);
			}
		}
	}

	/**
	 * Starts the ssimulation, runs {@link tick} every second.
	 * @param timeLimit_sec	simulation time limit in seconds. -1 for indefinite
	 *
	 */
	public void start(int timeLimit_sec) {

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		// the synchronized block makes sure only 1 thread can run this runnable at a
		// time
		// Any other thread approaching this, and using the same *monitoring object*
		// (this.threadLock) will be blocked.
		// note: if another JunctionController is initialized, it will have a different
		// threadLock instantiation and would be able to run.

		Runnable task = () -> {
			synchronized (this.threadLock) {
				if (timeLimit_sec != -1 && this._elapsedTime < timeLimit_sec)
					this.tick(timeLimit_sec);
				else
					scheduler.shutdown();
			}
		};

		int tickInterval = 1; // 1sec

		scheduler.scheduleAtFixedRate(task, 0, tickInterval, TimeUnit.SECONDS);
		try {
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
		PhaseValue phase;
		int len;
		int phaseTimer;
		int carsPassed;
		int carsOnRoad;

		/**
		 * initializes to NS_GREEN, loads values
		 */
		public JunctionPhase() {
			phase = PhaseValue.NS_GREEN;
			len = getPhaseLen();
			phaseTimer = carsPassed = carsOnRoad = 0;
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
			this.phaseTimer = this.carsPassed = this.carsOnRoad = 0;
			this.len = getPhaseLen();
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
			return String.format("Phase: %s; len: %d;timer: %d; carsPassed: %d; carsOnRoad: %d", this.phase.name(),
					this.len, this.phaseTimer, this.carsPassed, this.carsOnRoad);
		}

	}
}
