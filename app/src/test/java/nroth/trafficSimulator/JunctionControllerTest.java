package nroth.trafficSimulator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nroth.trafficSimulator.JunctionController.JunctionState;
import nroth.trafficSimulator.ConfigReader.Config;

class JunctionControllerTest {

	private JunctionController controller;
	private Config config;

	@BeforeEach
	public void setUp() {


		config = new Config(
			7,
			8,
			10,
			2,
			3,
			4, 
			5);

		controller = new JunctionController(config);
	}

	@Test
	void testJunctionPhaseInitialValues() {
		//default is phase1
		JunctionController.JunctionPhase phase = controller.new JunctionPhase();
		assertEquals("NS_GREEN", phase.phase.name());
		assertEquals(config.X1(), phase.len);
		assertEquals(0, phase.phaseTimer);
		assertEquals(0, phase.carsPassed);
		assertEquals(0, phase.carsOnRoad);
	}

	@Test
	void testJunctionPhaseSwitch() {
		JunctionController.JunctionPhase phase = controller.new JunctionPhase();
		phase.phaseTimer = 5;
		phase.carsPassed = 3;
		phase.carsOnRoad = 2;

		phase.switchPhase();

		assertEquals("EW_GREEN", phase.phase.name());
		assertEquals(0, phase.phaseTimer);
		assertEquals(0, phase.carsPassed);
		assertEquals(0, phase.carsOnRoad);
		// After switching, len should update to X2
		assertEquals(config.X2(), phase.len);
	}

	@Test
	void testJunctionPhaseUpdate() {
		JunctionController.JunctionPhase phase = controller.new JunctionPhase();


		Map<String, Integer> res1 = new HashMap<>();
		res1.put("carsPassed", 7);
		res1.put("carsOnRoad", 5);

		Map<String, Integer> res2 = new HashMap<>();
		res2.put("carsPassed", 7);
		res2.put("carsOnRoad", 4);

		phase.update(res1, res2);

		assertEquals(14 , phase.carsPassed);
		assertEquals(9 , phase.carsOnRoad);

		res1 = new HashMap<>();
		res1.put("carsPassed", 3);
		res1.put("carsOnRoad", 5);

		res2 = new HashMap<>();
		res2.put("carsPassed", 3);
		res2.put("carsOnRoad", 4);

		phase.update(res1, res2);

		assertEquals(20 , phase.carsPassed); // should be added to old value
		assertEquals(9, phase.carsOnRoad); // should replace old value
		assertEquals(2, phase.phaseTimer); //timer should increase with each update call
	}

	@Test
	void testGetRoadsAndConfig() {
		Road[] roads = controller.getRoads();
		assertNotNull(roads);
		assertEquals(4, roads.length);
		Config cfg = controller.getConfig();
		assertNotNull(cfg);
		assertEquals(config, cfg);
	}

	@Test
	void testAddCarValid() throws Exception {
		controller.addCar('N');
		controller.addCar('E', 2);
		controller.addCar('S', 5);
		controller.addCar('W', 12);

		Road[] roads = controller.getRoads();
		assertEquals(1, roads[0].getQueueLen());
		assertEquals(2, roads[1].getQueueLen());
		assertEquals(5, roads[2].getQueueLen());
		assertEquals(12, roads[3].getQueueLen());
	}

	@Test
	void testAddCarInvalid() {
		Exception exception = assertThrows(Exception.class, () -> controller.addCar('Z'));
		assertTrue(exception.getMessage().contains("Invalid value"));
	}

	@Test
	void testTickCarArrivals() throws Exception {
		// Roads should all already be clear
		var roads = controller.getJunctionState().roadQueues();
		assertEquals(0, roads.get("Total"));

		// Simulate ticks and check arrivals
		for (int t = 1; t <= 12; t++) {
			controller.tick();
		}
		
		roads = controller.getJunctionState().roadQueues();
		// After 12 ticks, check that cars have arrived according to config
		// A1=2, A2=3, A3=4, A4=5
		assertEquals(6, roads.get("North")); // 12/2 = 6
		assertEquals(4, roads.get("East")); // 12/3 = 4
		assertEquals(3, roads.get("South")); // 12/4 = 3
		assertEquals(2, roads.get("West")); // 12/5 = 2
	}

	@Test
	void testTickPhaseSwitching() {
		// Simulate enough ticks to force a phase switch
		String initialPhase = (String) controller.getPhase().phase.name();
		int phaseLen = (Integer) controller.getPhase().len;


		for (int i = 0; i < phaseLen; i++) {
			controller.tick();
		}
		// After phaseLen ticks, phase should have switched
		String newPhase = (String) controller.getPhase().phase.name();

		assertNotEquals(initialPhase, newPhase);
	}

	@Test
	void testMultipleTickPhaseSwitching() {
		// Simulate enough ticks to force a phase switch
		String initialPhase = (String) controller.getPhase().phase.name();
		int phaseLen = (Integer) controller.getPhase().len;
		for (int i = 0; i < phaseLen; i++) {
			controller.tick();
		}
		//again!
		phaseLen = (Integer) controller.getPhase().len;
		for (int i = 0; i < phaseLen; i++) {
			controller.tick();
		}

		// After phaseLen ticks, phase should have switched
		String newPhase = (String) controller.getPhase().phase.name();

		assertEquals(initialPhase, newPhase);
	}

	@Test
	void testAllCarsPassedOnePhase() {
		int phaseLen = controller.getPhase().len;


		// Simulate enough ticks to force a phase switch
		for (int i = 0; i < phaseLen; i++) {
			controller.tick();
		}

		// After phaseLen ticks, no cars should be on road
		int carsOnRoad = controller.getPhase().carsOnRoad;

		assertEquals(0, carsOnRoad);
	}

	@Test
	void testAllCarsPassedMultiplePhases() {
		int phaseLen = controller.getPhase().len;

		// Simulate enough ticks to force a phase switch
		for (int i = 0; i < phaseLen; i++) {
			controller.tick();
		}

		//and switch again!
		phaseLen = controller.getPhase().len;

		// Simulate enough ticks to force a phase switch
		for (int i = 0; i < phaseLen; i++) {
			controller.tick();
		}

		// After phaseLen ticks, no cars should be on road
		int carsOnRoad = controller.getPhase().carsOnRoad;

		assertEquals(0, carsOnRoad);
	}

	@Test
	void testTimeLimitFollowed()
	{
		int timeLimit = 5;

		controller.start(timeLimit);

		// After simulation, elapsed time should be at least the time limit, but not much more
		var state = controller.getJunctionState();

		int elapsed = state.elapsedTime();
		assertEquals(timeLimit, elapsed);
		// assertTrue(elapsed == timeLimit, "Simulation should run for the specified time limit");
	}
}