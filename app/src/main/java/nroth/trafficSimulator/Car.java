package nroth.trafficSimulator;

public class Car {
	public int length;
	public float posInJunction;
	public int S;
	public int id;

	public Car (int i, int len, float p, int s)
	{
		id = i;
		length = len;
		posInJunction = p;
		S = s;
	}

	@Override public String toString() {
		return "Car" + id +": posInJunction= " + posInJunction;
	}
}
