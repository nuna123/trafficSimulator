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
	private int _elapsedTime = 0;


	public Road[] getRoads() {
		return _roads;
	}

	public Map<String, Integer> getConfig() {
		return (_config == null ? null : Collections.unmodifiableMap(_config));
	}

	public Map<String, Object> getCurrPhase() {
		return (_currentPhase == null ? null
				: Map.ofEntries(Map.entry("phase", _currentPhase.phase.name()),
						Map.entry("carsPassed", _currentPhase.carsPassed),
						Map.entry("carsOnRoad", _currentPhase.carsOnRoad),
						Map.entry("phaseTimer", _currentPhase.phaseTimer),
						Map.entry("len", _currentPhase.len)));
	}

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

	public void addCar(char roadChar)
			throws Exception {
		this.addCar(roadChar, 1);
	}

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


	public void print (String msg)
	{
		System.out.printf("[%ds]\t%s\n", _elapsedTime, msg);
	}

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

	// FUNCTION TO RUN EVERY SECOND
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

			this.print("--------Phase switch!");
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
				_roads[idx].addCar();
				String[] dirs = { "North", "East", "South", "West" };
				this.print("Car Arrived from " + dirs[idx]);
			}
		}
	}

	// FUNCTION TO TIME TICKS
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

	public class JunctionPhase {
		PhaseValue phase;
		int len;
		int phaseTimer;
		int carsPassed;
		int carsOnRoad;

		public JunctionPhase() {
			phase = PhaseValue.NS_GREEN;
			len = getPhaseLen();
			phaseTimer = carsPassed = carsOnRoad = 0;
		}

		private int getPhaseLen() {
			return (this.phase == PhaseValue.NS_GREEN ? _config.get("X1") : _config.get("X2"));
		}

		public void switchPhase() {
			this.phase = (phase == PhaseValue.NS_GREEN ? PhaseValue.EW_GREEN : PhaseValue.NS_GREEN);
			this.phaseTimer = this.carsPassed = this.carsOnRoad = 0;
			this.len = getPhaseLen();
		}

		public void update(Map<String, Integer> res1, Map<String, Integer> res2) {
			carsPassed += res1.get("carsPassed");
			carsPassed += res2.get("carsPassed");

			carsOnRoad = res1.get("carsOnRoad") + res2.get("carsOnRoad");
			this.phaseTimer++;

		}

		@Override
		public String toString() {
			return String.format("Phase: %s; len: %d;timer: %d; carsPassed: %d; carsOnRoad: %d", this.phase.name(),
					this.len, this.phaseTimer, this.carsPassed, this.carsOnRoad);
		}

	}
}
