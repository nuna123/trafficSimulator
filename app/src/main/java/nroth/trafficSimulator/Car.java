package nroth.trafficSimulator;

public class Car {
	public int length;
	public float posInJunction;
	public int S;
	public int id;

	public Car (int i, int len, float p, int s)
	{
		this.id = i;
		this.length = len;
		this.posInJunction = p;
		this.S = s * len;
	}

	@Override public String toString() {
		return "Car" + id +": posInJunction= " + posInJunction;
	}
}
