import java.util.*;

public class Road {
	private int _S;
	private record Car(int length) {};
	private Queue<Car> carsQueue= new LinkedList<>();

	public Road (int S){_S = S;}

	public void addCar(int carLength){ carsQueue.add(new Car(carLength)); }
	public void addCar(){ carsQueue.add(new Car(1)); }
	public void removeCar(){ if (!carsQueue.isEmpty()) {carsQueue.remove();}}
	public int getQueueLen() {return carsQueue.size();}

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


}
