package nroth.trafficSimulator;

import java.util.*;

public class Road {
	private int _S;
	// private record Car(float length, float posInJunction, int S) {};


	private Queue<Car> carsQueue= new LinkedList<>();
	int carId = 1;

	public Road (int S){_S = S;}

	public void addCar(int carLength){
		carsQueue.add(new Car(carId++, carLength, getQueueLen() * -1, _S));
	}
	public void addCar(){ addCar(1);}
	public void removeCar(){ if (!carsQueue.isEmpty()) {carsQueue.remove();}}
	public int getQueueLen() {return carsQueue.size();}


	public Map<String, Integer> greenLight_tick(int secInLight)
	{
		int carsOnRoad = 0;
		int carsPassed = 0;

		//go over car in passage, advance by 1/S
		//count cars on passage, count cars that passed
		//add car to passage if theres time

		Iterator<Car> carIterator = carsQueue.iterator();
		Car currCar;
		int roadCounter;

		while (carIterator.hasNext())
		{
			currCar = carIterator.next();

			// System.out.println(currCar);

			if ((currCar.posInJunction > 0 && currCar.posInJunction < 1) || (
				currCar.posInJunction == 0 && currCar.S <= secInLight
			))
			{
				currCar.posInJunction = currCar.posInJunction + (1.0f / currCar.S);
				
			}
			
			if (currCar.posInJunction >= 1)
				carsPassed += 1;
			else if (currCar.posInJunction > 0)
				carsOnRoad += 1;
			else if (currCar.posInJunction <= 0)
			{
				roadCounter = 0;

				do {
					currCar.posInJunction = roadCounter;
					roadCounter--;
				}
				while (carIterator.hasNext() && (currCar = carIterator.next()) != null );
			}
		}
		
		//cleanup cars that passed from the road
		for (int i = 0; i < carsPassed; i++) {carsQueue.poll();}

		Map<String, Integer> ret = new HashMap<>();
		ret.put("carsOnRoad", carsOnRoad);
		ret.put("carsPassed", carsPassed);

		return ret;
	}


	public void print_road ()
	{
		System.out.println("-------------");
		Iterator <Car> it = carsQueue.iterator();
		Car currCar;
		int idx = 0;
		while (it.hasNext())
		{
			currCar = it.next();
			System.out.println(idx + ": " + currCar);
			idx++;
		}
	}
/* 
	public Map<String, Int> greenLight_tick(int secInLight)
	{
		int carsOnRoad = 0;
		int carsPassed = 0;

		//go over car in passage, advance by 1/S
		//count cars on passage, count cars that passed
		//add car to passage if theres time
		ListIterator<Car> carIterator = carsQueue.iterator();
		Car currCar;
		int currCarIdx;

		while (carIterator.hasNext())
		{
			currCarIdx = carIterator.nextIndex();
			currCar = carIterator.next();

			System.out.println(currCar);

			if (currCar.posInJunction >= 1) //car has passed the road
			{
				carsPassed += 1;
				currCar.posInJunction = 2; //set to know to delete later
			}

			if (currCar.posInJunction < 1 && currCar.posInJunction > 0) //car is on the road
			{
				currCar.posInJunction += (1 / currCar.S);
				if (currCar.posInJunction >= 1) //car has passed the road
				{
					carsPassed += 1;
					currCar.posInJunction = 2; //set to know to delete later
				}
				else
				{
					carsOnRoad += 1;
				}
			}
			if (currCar.posInJunction == 0 && currCar.S <= secInLight)  //car is first in queue
			{
				currCar.posInJunction += (1 / currCar.S);
				carsOnRoad += 1;
			}
		}


		Map<String, Int> ret = new HashMap<>();
		ret.put("carsOnRoad", carsOnRoad);
		ret.put("carsPassed", carsPassed);

		return ret;
	}

 */

	/* 
	public int calcCarPassage(int phaseTime)
	{
		int car_counter = 0;
		Iterator<Car> carIterator = carsQueue.iterator();
		Car currCar;

		while (phaseTime > 0 && carIterator.hasNext())
		{
			currCar = carIterator.next();
			// System.out.println(currCar.length());
			if(phaseTime - (currCar.length() * _S) >= 0)
			{
				car_counter += 1;
				phaseTime -= (currCar.length() * _S);
			}
			else
			{
				return car_counter;
			}
		}
		return car_counter;
	}

	public int greenLight (int time)
	{
		int carPassing = calcCarPassage(time);
		for (int i = 0; i< carPassing; i++)
		{carsQueue.poll();}

		return carPassing;
	}

 */
}
