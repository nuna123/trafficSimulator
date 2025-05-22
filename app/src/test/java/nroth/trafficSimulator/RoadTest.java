package nroth.trafficSimulator;

import org.junit.jupiter.api.BeforeEach;


class RoadTest {

	private Road controller;

	@BeforeEach
	public void setUp() {
		int S = 3; // how longit takes car to cross the road
		controller = new Road(S);
	}


}
