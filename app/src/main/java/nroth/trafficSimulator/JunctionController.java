package nroth.trafficSimulator;

import java.util.*;


public class JunctionController {
	private Road[] _roads;
	private Map<String, Integer> _config = null;

	public enum PhaseValue { NS_GREEN, EW_GREEN }
	private JunctionPhase _currentPhase;

	int time = 0;




	private int[] _carArrivals;

	public Road[] getRoads (){return _roads;}
	public Map<String, Integer> getConfig (){return (_config == null ? null : Collections.unmodifiableMap(_config));}
	public Map<String, Object> getCurrPhase (){
		return (_currentPhase == null ? null :
		Map.ofEntries(Map.entry("phase", _currentPhase.phase.name()),
				Map.entry("carsPassed", _currentPhase.carsPassed),
				Map.entry("carsOnRoad", _currentPhase.carsOnRoad),
				Map.entry("phaseTimer", _currentPhase.phaseTimer),
				Map.entry("len", _currentPhase.len)
				));}

	public JunctionController(Map <String, Integer> config)
	{
		_config = new HashMap<>(config);
		_currentPhase = new JunctionPhase();
		
		_roads = new Road[4];
		for (int i = 0; i < 4; i++)
			_roads[i] = new Road(_config.get("S"));

		_carArrivals = new int[4];
		for (int i = 1; i <=4; i++)
			_carArrivals[i - 1] = _config.get("A" + String.valueOf(i));
	}

	public void addCar (char roadChar)
			throws Exception
	{this.addCar (roadChar, 1);}

	public void addCar (char roadChar, int carNum)
			throws Exception
	{
		Road myRoad =
		(
			switch (roadChar) {
			case 'N'-> _roads[0];
			case 'W'-> _roads[1];
			case 'S'-> _roads[2];
			case 'E'-> _roads[3];
			default -> null;
		});
		if (myRoad == null)
			throw new Exception ("Invalid value: " + roadChar);

		for (int i = 0; i < carNum; i++)
			myRoad.addCar();
	}

	//FUNCTION TO RUN EVERY SECOND
	public void tick()
	{
		time ++;

		Map <String, Integer> res1;
		Map <String, Integer> res2;

		int roadOffset = (_currentPhase.phase == PhaseValue.NS_GREEN ? 0 : 1);
		res1 = _roads[0 + roadOffset].greenLight_tick(_currentPhase.len - _currentPhase.phaseTimer);
		res2 = _roads[2 + roadOffset].greenLight_tick(_currentPhase.len - _currentPhase.phaseTimer);

		// System.out.println("road1: " + res1);
		// System.out.println("road2: " + res2);

		//sets carsonroad, carspassed, phasetimer
		_currentPhase.update(res1, res2);

		//handle phase switching
		if (_currentPhase.phaseTimer >= _currentPhase.len)
		{
			System.out.println("--------------"+time+"---------------------");
			System.out.println("phase overview: " + _currentPhase);
			
			_currentPhase.switchPhase();

			System.out.println("New phase: " + _currentPhase);
			
			System.out.printf("Car Queues: \n\tNorth(%d) ; East(%d) ; South(%d) ; West:(%d)\n",
			_roads[0].getQueueLen(),
			_roads[1].getQueueLen(),
			_roads[2].getQueueLen(),
			_roads[3].getQueueLen());
			System.out.println("---------------------------------------");

		}

		//handle car arrivals
		for (int idx = 0; idx < _carArrivals.length ; idx ++ )
		{
			if (_carArrivals[idx] > 0 && time % _carArrivals[idx] == 0)
			{
				_roads[idx].addCar();
				String[] dirs = {"North", "East", "South", "West"};
				System.out.println(time + ": Car Arrived from " + dirs[idx]);
			}
		}
	}

	public class JunctionPhase {
		PhaseValue phase;
		int len;
		int phaseTimer;
		int carsPassed;
		int carsOnRoad;


		public JunctionPhase(){
			phase = PhaseValue.NS_GREEN;
			len = getPhaseLen();
			phaseTimer = carsPassed = carsOnRoad = 0;
		}

		private int getPhaseLen()
		{
			return (this.phase == PhaseValue.NS_GREEN ? _config.get("X1") : _config.get("X2"));
		}

		public void switchPhase()
		{
			this.phase = (phase == PhaseValue.NS_GREEN ? PhaseValue.EW_GREEN : PhaseValue.NS_GREEN);
			this.phaseTimer = this.carsPassed = this.carsOnRoad = 0;
			this.len = getPhaseLen();
			}

		public void update(Map <String, Integer> res1, Map <String, Integer> res2)
		{
			carsPassed += res1.get("carsPassed");
			carsPassed += res2.get("carsPassed");

			carsOnRoad = res1.get("carsOnRoad") + res2.get("carsOnRoad");
			this.phaseTimer++;

		}

		@Override public String toString() {
			return String.format("Phase: %s; len: %d;timer: %d; carsPassed: %d; carsOnRoad: %d", this.phase.name(),this.len, this.phaseTimer, this.carsPassed, this.carsOnRoad);
		}

	}
}
