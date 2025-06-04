package nroth.trafficSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
* [                    NORTH                 ] 0
* [                |    ( )    |             ] 1
* [                |           |             ] 2
* [                |     |     |             ] 3
* [                |           |             ] 4
* [       ---------|- - -|- - -|---------    ] 5
* [                                          ] 6
* [   WEST( ) - - -|- - -|- - -|- - - ( )EAST] 7
* [                                          ] 8
* [       ---------|- - -|- - -|---------    ] 9
* [                |           |             ] 10
* [                |     |     |             ] 11
* [                |           |             ] 12
* [                |    ( )    |             ] 13
* [                    SOUTH                 ] 14

	north_lane = junction[19, CARPOS]
	south_lane = junction[25, CARPOS]
	east_lane = junction[CARPOS, 6]
	west_lane = junction[CARPOS, 8]

	// how many units are across the junction
	// [E->W, N->S]
	// from point 0, it sould take DIS + 1 to cross the road
	laneSize_X = 13  // horizontal size, cars coming from W / E
	laneSize_Y = 5  // vertical size, cars coming from N / S

	// start position of the car BEFORE it is in the junction
	lanePoint0_N = [19, 4]
	lanePoint0_S = [25, 10]

	lanePoint0_E = [29,6]
	lanePoint0_W = [15, 8]


	//Car Queues positions
	//positions of the queue count title
	N = [22,1]
	S = [22,13]
	E = [36,7]
	W = [9,7]
*/
	// public class Constants{
	// 	public int[] getCarPosition (char dir, int carPos)
	// 			throws Exception{
	// 		int[] res = (switch (dir) {
	// 			case 'N' -> new int[]{19, carPos};
	// 			case 'W' -> new int[]{25, carPos};
	// 			case 'S' -> new int[]{carPos, 6};
	// 			case 'E' -> new int[]{carPos, 8};
	// 			default -> null;
	// 		});
	// 		if (res == null)
	// 			throw new Exception("Invalid value: " + dir);
	// 		return res;
	// 	}

	// 	public int getLaneSize (char dir, int carPos)
	// 	{
	// 		return
	// 			(dir == 'N' || dir == 'S'
	// 				? 5
	// 				: (dir == 'E' || dir == 'W' ? 13 : -1));
	// 	}

	// 	public int[] getStartPosition (char dir, int carPos)
public class Animator {
	private String[] _roadchars = {"N", "E", "S", "W"};
	public class calcCarPositions {
		public static int[] calcCarPos_N(Car c)
		{
			int[] lanePointZero = {19, 4};
			float laneSize = 5;
			int pos = lanePointZero[1];

			//x stays the same, y needs to be changed.
			if (c.posInJunction <= 0)
				pos += c.posInJunction;
			else if (c.posInJunction > 0 && c.posInJunction < 1)
				pos += 1 + (laneSize * c.posInJunction);
			else // if pos > 1
				pos += laneSize + (c.posInJunction);

			return new int[]{lanePointZero[0], pos};
		}
		public static int[] calcCarPos_W(Car c)
		{
			int[] lanePointZero = {29, 6};
			float laneSize = 13;
			int pos = lanePointZero[0];

			//y stays the same, x needs to be changed.
			if (c.posInJunction <= 0)
				pos += c.posInJunction;
			else if (c.posInJunction > 0 && c.posInJunction < 1)
				pos += 1 + (laneSize * c.posInJunction);
			else // if pos > 1
				pos += laneSize + (c.posInJunction);

			return new int[]{pos, lanePointZero[1]};
		}
		
	}
	private class JunctionMap{
		private char carChar = 'X';
		int[][] map_limits = {{11,33}, {2,12}};
		String[] mapStringArr =
			{
				"                    NORTH                  ",
				"                |   (  )    |              ",
				"                |           |              ",
				"                |     |     |              ",
				"                |           |              ",
				"       ---------|- - -|- - -|---------     ",
				"                                           ",
				"  WEST(  ) - - -|- - -|- - -|- - - (  )EAST",
				"                                           ",
				"       ---------|- - -|- - -|---------     ",
				"                |           |              ",
				"                |     |     |              ",
				"                |           |              ",
				"                |     |     |              ",
				"                |    (  )   |              ",
				"                    SOUTH                  "
			};
		
		public void addTitles (int[] carQueues){

			int[][] titlePos = {
				{21, 1}, //N
				{36, 7}, //E
				{22, 14}, //S
				{7, 7} //W
			};

			char[] chararr;
			for (int i = 0; i < carQueues.length ; i++)
			{
				if (carQueues[i] > 99)
					carQueues[i] = 99;

				chararr = String.format("%2s", String.valueOf(carQueues[i])).toCharArray();

				addChar(chararr[0], titlePos[i]);
				addChar(chararr[1], new int[] {titlePos[i][0] + 1, titlePos[i][1]});
			}

		}


		public void addChar(char c, int[] pos) {
			if (pos[0] < 0 || pos[0] >= mapStringArr[0].length()
			|| pos[1] < 0 || pos[1] >= mapStringArr.length )
				return;
			char[] row = this.mapStringArr[pos[1]].toCharArray();
			row[pos[0]] = c;
			this.mapStringArr[pos[1]] = new String(row);
		}
		public void addCar(int[] pos) {
			if (pos[0] < map_limits[0][0] || pos[0] > map_limits[0][1]
			|| pos[1] < map_limits[1][0] || pos[1] > map_limits[1][1] )
				return;
			addChar(carChar, pos);
		}


		public void print ()
		{
			System.out.printf("%c[2J%c[;H",(char) 27, (char) 27);

			for (String i : mapStringArr)
				System.out.println("|" + i + "|");
		}
		/**
		 *
		 * @param carsOnRoad {
		 * 	"N" -> List <Car>
		 * 	"W" -> List <Car>
		 * 	"S" -> List <Car>
		 * 	"E" -> List <Car>
		 * }
		 */
		// public void addAllCars(HashMap <String, List<Car>> carsOnRoad)

		public void addAllCars(Function<Car, int[]> func, LinkedList<Car> cars)
		{
			cars.forEach((Car c) -> {
				addCar(func.apply(c));
			});
		}


		public void addCarsToDirection(int dirIdx, LinkedList<Car> allCars)
		{
			System.out.println ("DIR: " + dirIdx);
			System.out.println ("CARS: " + allCars);
			Function<Car, int[]> func = null;
			switch (dirIdx) {
				case 0:  func = calcCarPositions::calcCarPos_N;
				default:
					break;
			
			}
			if (func != null)
				addAllCars(func, allCars);
		}

		/* 
		public void addAllCars(HashMap <String, List<Car>> carsOnRoad)
		{
			JunctionController.printDebug(carsOnRoad.toString());
			
			List<int[]> positions;
			List<Car> cars;
			int[] pos = {};
			for (String key : carsOnRoad.keySet())
			{
			// String key = "N";
				positions = new ArrayList<>();
				cars = carsOnRoad.get(key);
				for (Car c : cars)
				{
					if (key.equals("N"))
						pos = calcCarPos_N(c);
					if (key.equals("W"))
						pos = calcCarPos_W(c);
					
					positions.add(pos);
					// }
					// TODO: ADD LOGIC TO OTHER DIRECTIONS
				}
				addCar(positions);
			}


			// go over each direction, each has a different way to calculate car position
		}
 */
	}

	JunctionMap configureFrame(JunctionController jc)
	{
		JunctionMap newMap = new JunctionMap();
		//figure out what cars are on the road, and which are waiting in queue
		int[] carQueues = {0,0,0,0};
		LinkedList<Car> allCars;


		//go over each road.
		for (int i = 0; i < 4; i++)
		{
			carQueues[i] = jc.getRoads()[i].getQueueLen();
			allCars = (LinkedList<Car>) (jc.getRoads()[i].getWaitingCars());
			allCars.addAll((LinkedList<Car>) (jc.getRoads()[i].getRoadCars()));
			allCars.addAll((LinkedList<Car>) (jc.getRoads()[i].getPassedCars()));

			newMap.addCarsToDirection (i, allCars);
		}

		//actually build map
		newMap.addTitles(carQueues);
		
		// newMap.addCar(carsOnRoad);
		newMap.print();

		return newMap;
	}

}
