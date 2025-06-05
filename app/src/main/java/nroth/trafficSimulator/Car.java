package nroth.trafficSimulator;

import java.util.Random;


 /**
 * CAR
 * Simple class used to populate queue in {@link Road}.
 *
 * The field 'posInJunction' indicates the car's current position relative to the junction:
 *	posInJunction <= 0	: Car is in the queue.
 *	posInJunction == 0	: Car is at the front of the queue and is the next to enter the junction.
 *	0 < posInJunction < 1 : Car is currently crossing the junction. Moves forward by [+1/crossingTime] units per second.
 *	posInJunction >= 1	: Car has passed through the junction.
 */

public class Car {
	private static final int MIN_ID = 1111;
	private static final int MAX_ID = 9999;
	private final static Random RANDOM = new Random();

	public final int length;
	public final int crossingTime;
	public final String plate;

	public float posInJunction;



	public Car (int len, float position, int secToCross)
	{
		this.length = len;
		this.posInJunction = position;
		this.crossingTime = secToCross * this.length; // to update s, incase different car lengths is implemented
		this.plate = generatePlate();
	}

	/**
	 * random plate generator in format 1AA[1111-9999]
	 * @return random plate
	 */
	private String generatePlate()
	{
		int r = RANDOM.nextInt(MAX_ID - MIN_ID + 1) + MIN_ID;

		return ("1AA" + String.valueOf(r));
	}

	@Override public String toString() {
		return "Car [" + plate +"]: posInJunction= " + posInJunction;
	}
}
