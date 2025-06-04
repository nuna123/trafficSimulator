package nroth.trafficSimulator;

import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class RoadTest {

	private Road controller;

	@BeforeEach
	public void setUp() {
		int S = 1; // how long it takes car to cross the road
		controller = new Road(S, "testRoad");
	}

	@Test
	public void carAdding()
	{
		int roadLen = controller.getQueueLen();
		assertEquals(0, roadLen); //there are no cars in the queue

		Car addedCar = controller.addCar();
		roadLen = controller.getQueueLen();

		assertEquals(1, roadLen); //only 1 car in queue
		assertEquals(controller.getWaitingCars().peek(), addedCar); //car added is the new car
	}

	@Test
	public void queueIsFifo()
	{
		Car addedCar = controller.addCar();
		for (int i = 0;i ++ < 5;){controller.addCar();}

		assertEquals(addedCar, controller.getWaitingCars().peek()); //first car in queue is the car that was added first
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
		controller = new Road(3, "road!");

		int car_queue_len = 5;
		int timer = 5;
		for (int i = 0;i < car_queue_len; i++){controller.addCar();} //5 cars on the road

		System.out.println(controller.getWaitingCars());
		System.out.println(controller.getRoadCars());
		assertEquals(car_queue_len, controller.getQueueLen());
		
		controller.greenLight_tick(timer--);
		System.out.println(controller.getRoadCars());
		//car should not have passed, but should be on road
		assertEquals(car_queue_len - 1, controller.getQueueLen());
		assertEquals(1, controller.getRoadCars().size());
		
		//in 4 more ticks, 3 cars should fully pass
		while (timer > 0){
			controller.greenLight_tick(timer);
			System.out.println(controller.getRoadCars());
			timer--;
		}

		assertEquals(car_queue_len - 3, controller.getQueueLen());
		assertEquals(0, controller.getRoadCars().size());

/* x = car; [---] = junction
	TS
	0	xxx[---]	{T = 5}
	1	-xx[x--]	{T = 4}=> assertEquals(car_queue_len - 1, controller.getQueueLen())
	2	--x[xx-]	{T = 3}
	3	--x[-xx]	{T = 2}
	4	--x[--x]x	{T = 1}
	5	--x[---]xx	{T = 0}
 */
	}

	@Test
	public void testGetQueueReturnsCopy() {
		controller.addCar();
		Queue<Car> queue1 = controller.getWaitingCars();
		Queue<Car> queue2 = controller.getWaitingCars();
		assertEquals(queue1, queue2);
		queue1.poll();
		// Original queue should not be affected
		assertEquals(1, controller.getQueueLen());
	}

	@Test
	public void testGreenLightTickReturnsCorrectMap() {
		controller.addCar();
		Map<String, Integer> result = controller.greenLight_tick(1);
		// After one tick, one car should have passed (S=1)
		assertEquals(0, (int) result.get("carsOnRoad"));
		assertEquals(1, (int) result.get("carsPassed"));
	}

	@Test
	public void testGreenLightTickUpdatesQueue() {
		controller.addCar();
		controller.greenLight_tick(1);

		// After one tick, one car should have passed (S=1)
		assertEquals(0, (int) controller.getWaitingCars().size());
		assertEquals(0, (int) controller.getRoadCars().size());
		assertEquals(1, (int) controller.getPassedCars().size());
	}
}