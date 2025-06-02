package nroth.trafficSimulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * ROAD
 * manages individual roads. 
 * contains a linked list of {@link Car} to be added and removed from, and individually advanced.
 * greenlight_tick() -
 * 		goes over all cars, advances each depending on position
 * 		removes cars that safely crossed the road
 */
public class Road {
	//how long it takes car to cross the junction
	private final int _S;
	private final String _roadName;
	private final Queue<Car> _carsQueue= new LinkedList<>();


	public Road (int S, String roadname){_S = S;_roadName = roadname;}

	// car length is not really used.
	public Car addCar(int carLength){
		Car newCar = new Car(carLength, getQueueLen() * -1, _S);
		_carsQueue.add(newCar);
		JunctionController.log(_roadName + ": Car [" +newCar.plate + "] Arrived");
		return newCar;
	}
	public Car addCar(){ return addCar(1);}
	public void removeCar(){_carsQueue.poll();}
	public int getQueueLen() {return _carsQueue.size();}
	public Queue<Car> getQueue() {return new LinkedList<>(_carsQueue);}

	/**
	 * Runs every second while road has green light.
	 * goes over all cars, advances if theyre on the road,
	 * calculates if have time to pass if car is at position 0
	 * @param secInLight seconds left in this road phase
	 * @return	mapped values of current carsOnRoad and carsPassed
	 */
	public Map<String, Integer> greenLight_tick(int secInLight)
	{
		int carsOnRoad = 0;
		int carsPassed = 0;

		//go over car in passage, advance by 1/S
		//count cars on passage, count cars that passed
		//add car to passage if theres time

		Iterator<Car> carIterator = _carsQueue.iterator();
		Car currCar;
		int roadCounter;

		while (carIterator.hasNext())
		{
			currCar = carIterator.next();
			//if car is on the road OR is in position 0 and has enoughtime left to safely cross
			if ((currCar.posInJunction > 0 && currCar.posInJunction < 1) || (
				currCar.posInJunction == 0 && currCar.crossingTime <= secInLight))
				currCar.posInJunction = currCar.posInJunction + (1.0f / currCar.crossingTime);

			if (currCar.posInJunction >= 1) //if the car passed the junction
			{
				carsPassed += 1;
				JunctionController.log(_roadName + ": car " + currCar.plate + " has passed!");
			}
			else if (currCar.posInJunction > 0) // if car is currently on road
				carsOnRoad += 1;
			else if (currCar.posInJunction <= 0) // car is still waiting to cross
			{
				roadCounter = 0;
				do {
					currCar.posInJunction = roadCounter;
					roadCounter--;
				}
				while (carIterator.hasNext() && (currCar = carIterator.next()) != null);
			}
		}

		//cleanup cars that passed from the road
		for (int i = 0; i < carsPassed; i++) {_carsQueue.poll();}

		//organize and return values
		Map<String, Integer> ret = new HashMap<>();
		ret.put("carsOnRoad", carsOnRoad);
		ret.put("carsPassed", carsPassed);

		return ret;
	}


	/**
	 * prints cars in road queue
	 */

	@Override
	public String toString() {
		Iterator <Car> it = _carsQueue.iterator();
		Car currCar;
		int idx = 0;
		String out = "";
		out += (_roadName + ": ");
		if (!it.hasNext())
			out += "[No Cars]";
		while (it.hasNext())
		{
			currCar = it.next();
			out += (idx + ": " + currCar);
			idx++;
		}
		return out;
	}

}
