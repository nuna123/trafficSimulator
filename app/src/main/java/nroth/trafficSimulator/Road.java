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
	private final Queue<Car> _waitingCarsQueue= new LinkedList<>();
	private final Queue<Car> _carsOnRoad= new LinkedList<>();
	private final Queue<Car> _passedCarsQueue= new LinkedList<>();


	public Road (int S, String roadname){_S = S;_roadName = roadname;}

	// car length is not really used.
	public Car addCar(int carLength){
		Car newCar = new Car(carLength, getQueueLen() * -1, _S);
		_waitingCarsQueue.add(newCar);
		JunctionController.printDebug(_roadName + ": Car [" +newCar.plate + "] Arrived");
		return newCar;
	}
	public Car addCar(){ return addCar(1);}
	public int getQueueLen() {return _waitingCarsQueue.size();}
	public Queue<Car> getRoadCars() {return new LinkedList<>(_carsOnRoad);}
	public Queue<Car> getWaitingCars() {return new LinkedList<>(_waitingCarsQueue);}
	public Queue<Car> getPassedCars() {return new LinkedList<>(_passedCarsQueue);}

	public void advancePassedCars()
	{
		Iterator<Car> carIterator = _passedCarsQueue.iterator();
		Car currCar;

		LinkedList<Car> carsToRemove = new LinkedList<>();

		//advance passed cars
		while (carIterator.hasNext())
		{
			currCar = carIterator.next();
			if (currCar.posInJunction >= 3)
				carsToRemove.add(currCar);
			else
				currCar.posInJunction ++;
		}
		_passedCarsQueue.removeAll(carsToRemove);
	}
	public void updateWaitingCars()
	{
		Iterator<Car> carIterator = _waitingCarsQueue.iterator();
		Car currCar;

		int counter = 0;
		while (carIterator.hasNext())
		{
			currCar = carIterator.next();
			if (currCar.posInJunction == counter)
				break;
			currCar.posInJunction = counter;
			counter --;
		}
	}

	public int advanceCarsOnRoad()
	{
		int passedCars = 0;
		Iterator<Car> carIterator = _carsOnRoad.iterator();
		Car currCar;

		//advance passed cars
		while (carIterator.hasNext())
		{
			currCar = carIterator.next();
			currCar.posInJunction = currCar.posInJunction + (1.0f / currCar.crossingTime);
			// currCar.posInJunction = currCar.posInJunction + 1 / currCar.crossingTime;

			if (currCar.posInJunction >= 1)
			{
				currCar.posInJunction = 0; // make sure its not some float, 0 so it will be advanced by advancePassedCars 
				_passedCarsQueue.add(currCar);
				passedCars ++;
			}
		}
		_carsOnRoad.removeAll(_passedCarsQueue);
		return passedCars;
	}

	/**
	 * Runs every second while road has green light.
	 * goes over all cars, advances if theyre on the road,
	 * calculates if have time to pass if car is at position 0
	 * @param secInLight seconds left in this road phase
	 * @return	mapped values of current carsOnRoad and carsPassed
	 */
	public Map<String, Integer> greenLight_tick(int secInLight)
	{
		if (_waitingCarsQueue.peek() != null &&
			_waitingCarsQueue.peek().crossingTime <= secInLight)
			{
				_waitingCarsQueue.peek().posInJunction = 0;
				_carsOnRoad.add(_waitingCarsQueue.poll());
			}
		int passedCars = advanceCarsOnRoad();
		// advance cars that already passed the junction
		advancePassedCars();
		updateWaitingCars();
		//organize and return values
		Map<String, Integer> ret = new HashMap<>();
		ret.put("carsOnRoad", _carsOnRoad.size());
		ret.put("carsPassed", passedCars);

		return ret;
	}


	/**
	 * prints cars in road queue
	 */

	@Override
	public String toString() {
		Iterator <Car> it = _waitingCarsQueue.iterator();
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
