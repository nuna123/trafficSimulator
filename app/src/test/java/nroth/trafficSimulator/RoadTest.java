package nroth.trafficSimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class RoadTest {

	private Road controller;

	@BeforeEach
	public void setUp() {
		int S = 1; // how long it takes car to cross the road
		controller = new Road(S);
	}

	@Test
	public void carAdding()
	{
		int roadLen = controller.getQueueLen();
		assertEquals(0, roadLen); //there are no cars in the queue

		Car addedCar = controller.addCar();
		roadLen = controller.getQueueLen();

		assertEquals(1, roadLen); //only 1 car in queue
		assertEquals(controller.getQueue().peek(), addedCar); //car added is the new car
	}

	@Test
	public void queueIsFifo()
	{
		Car addedCar = controller.addCar();
		for (int i = 0;i ++ < 5;){controller.addCar();}

		assertEquals(addedCar, controller.getQueue().peek()); //first car in queue is the car that was added first
	}

	@Test
	public void carPasses_s1() //note: S = 1, meaning a car will fully pass in one tick
	{
		int car_queue_len = 5;
		for (int i = 0;i < car_queue_len; i++){controller.addCar();} //5 cars on the road

		controller.greenLight_tick(5); //theres more than enough time
		car_queue_len--;
		//1 car shouldve passed
		assertEquals(car_queue_len, controller.getQueueLen());

		for (int i = 0;i < car_queue_len; i ++){controller.greenLight_tick(5);} //theres more than enough time
		assertEquals(0, controller.getQueueLen());
	}
	@Test
	public void carPasses_s3() //note: S = 3, meaning a car will fully pass in 3 ticks
	{
		controller = new Road(3);

		int car_queue_len = 5;
		int timer = 5;
		for (int i = 0;i < car_queue_len; i++){controller.addCar();} //5 cars on the road


		controller.greenLight_tick(timer--);
		//car should not have passed
		assertEquals(car_queue_len, controller.getQueueLen());
		System.out.print("\n");
		controller.printRoad();

		//in 4 more ticks, 3 cars should fully pass
		while (timer > 0){controller.greenLight_tick(timer);  System.out.println("\ntimer: " + timer--);controller.printRoad();}

		assertEquals(car_queue_len - 3, controller.getQueueLen());

/* x = car; [---] = junction
	TS
	0	-xx[---]

	1	--x[x--]
	2	---[xx-]
	3	---[-xx]
	4	---[--x]x
	5	---[---]xx
 */
	}
}
