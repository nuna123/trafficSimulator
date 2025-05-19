import java.util.*;



public class JunctionController {
	private Road[] _roads;
	private Map<String, Integer> _config = null;

	private enum Phase { NS_GREEN, EW_GREEN }
	private Phase _currentPhase = Phase.NS_GREEN;


	private int phaseTimer = 0;
	private	int time = 0;
	private int currPhaseLen;

	private int[] _carArrivals;

	public Road[] getRoads (){return _roads;}
	public Map<String, Integer> getConfig (){return (_config == null ? null : Collections.unmodifiableMap(_config));}

	public JunctionController(Map <String, Integer> config)
	{
		_config = new HashMap<>(config);
		currPhaseLen = (_currentPhase == Phase.NS_GREEN ? _config.get ("X1") : _config.get ("X2"));

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


	//only triggered when end of phase reached
	private void switchPhase()
	{
		switch (_currentPhase)
		{
			case NS_GREEN -> {
				_roads[0].greenLight (currPhaseLen);
				_roads[2].greenLight (currPhaseLen);

				_currentPhase = Phase.EW_GREEN;
				currPhaseLen =  _config.get("X1");

			}
			case EW_GREEN -> {
				_roads[1].greenLight (currPhaseLen);
				_roads[3].greenLight (currPhaseLen);

				_currentPhase = Phase.NS_GREEN;
				currPhaseLen =  _config.get("X2");
			}
		}
	}

	//FUNCTION TO RUN EVERY SECOND
	public void tick()
	{

		// System.out.println("Tick. Phase: " + _currentPhase);

		//switch phases
		phaseTimer ++;
		time ++;

		//handle phase switching
		if (phaseTimer >= currPhaseLen)
		{
			switchPhase();
			phaseTimer = 0;

			System.out.println("--------------"+time+"---------------------");
			System.out.printf("Phase switched! new phase: %s\n", (_currentPhase == Phase.NS_GREEN ? "North -> South" : "West -> East"));
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

			if (time % _carArrivals[idx] == 0)
			{
				_roads[idx].addCar();
				String[] dirs = {"North", "East", "South", "West"};
				System.out.println(time + ": Car Arrived from " + dirs[idx]);
			}
		}

	}
}
