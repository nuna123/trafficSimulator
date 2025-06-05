package nroth.trafficSimulator;

import java.util.LinkedList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

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

public class Animator {

	private final Terminal _terminal;
	private final TextGraphics textGraphics;


	//holder for the car position calculation functions
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
		public static int[] calcCarPos_S(Car c)
		{
			int[] lanePointZero = {25, 10};
			float laneSize = 5;
			int pos = lanePointZero[1];

			//x stays the same, y needs to be changed.
			if (c.posInJunction <= 0)
				pos -= c.posInJunction;
			else if (c.posInJunction > 0 && c.posInJunction < 1)
				pos -= 1 + (laneSize * c.posInJunction);
			else // if pos > 1
				pos -= laneSize + (c.posInJunction);

			return new int[]{lanePointZero[0], pos};
		}
		public static int[] calcCarPos_W(Car c)
		{
			int[] lanePointZero = {15, 8};
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
		public static int[] calcCarPos_E(Car c)
		{
			int[] lanePointZero = {29, 6};
			float laneSize = 13;
			int pos = lanePointZero[0];

			//y stays the same, x needs to be changed.
			if (c.posInJunction <= 0)
				pos -= c.posInJunction;
			else if (c.posInJunction > 0 && c.posInJunction < 1)
				pos -= 1 + (laneSize * c.posInJunction);
			else // if pos > 1
				pos -= laneSize + (c.posInJunction);

			return new int[]{pos, lanePointZero[1]};
		}
	}

	//constants needed for displaying
	public class constants
	{
		final static char carChar = 'X';
		final static int[][] map_limits = {{11,33}, {2,12}};
		final static int[][] titlePos = {
				{21, 1}, //N
				{36, 7}, //E
				{22, 14}, //S
				{7, 7} //W
			};

		final static TextColor.ANSI ANSIdefaultColor = TextColor.ANSI.WHITE;
		final static TextColor.ANSI[] ANSIcolorArr = {
			TextColor.ANSI.RED,
			TextColor.ANSI.GREEN,
			TextColor.ANSI.BLUE,
			TextColor.ANSI.YELLOW};


		final static String strResetColor = "\u001B[0m";
		final static String[] strColorArr = {
				"\u001B[31m", // North 
				"\u001B[32m", //East
				"\u001B[33m", //South
				"\u001B[34m" //West
			};


		final static String[] mapStringArr =
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
	}
	
	//actual map handling
	private class JunctionMap{

		String[] mapStringArr;
		String[] colorMap;

		public JunctionMap()
		{
			mapStringArr = Arrays.stream(constants.mapStringArr).toArray(String[]::new);
			colorMap =  new String[mapStringArr.length + 1];

			for (int i= 0; i < colorMap.length; i++)
				colorMap[i] = new String(new char[45]).replace('\0', ' ');
		}
		
		public void addTitles (int[] carQueues){
			char[] chararr;
			int[] titlePos;

			for (int i = 0; i < carQueues.length ; i++)
			{
				titlePos = constants.titlePos[i];

				//max 2 digits
				if (carQueues[i] > 99)
					carQueues[i] = 99;

				chararr = String.format("%2s", String.valueOf(carQueues[i])).toCharArray();

				addChar(chararr[0], titlePos);
				addChar(chararr[1], new int[] {titlePos[0] + 1, titlePos[1]});
			}
		}

		public void addChar(char c, int[] pos) {
			//check that character is within bounds of map
			if (pos[0] < 0 || pos[0] >= mapStringArr[0].length()
			|| pos[1] < 0 || pos[1] >= mapStringArr.length )
				return;
			char[] row = this.mapStringArr[pos[1]].toCharArray();
			row[pos[0]] = c;
			this.mapStringArr[pos[1]] = new String(row);
		}

		public void addCar(int[] pos) {
			//check that car is within defined map limits
			if (pos[0] < constants.map_limits[0][0] || pos[0] > constants.map_limits[0][1]
			|| pos[1] < constants.map_limits[1][0] || pos[1] > constants.map_limits[1][1] )
				return;
			addChar(constants.carChar, pos);
		}

		public void addToColorMap(int[] pos, int dirIdx)
		{
			//check that car is within defined map array
			if (pos[0] < 0 || pos[0] >= mapStringArr[0].length()
			|| pos[1] < 0 || pos[1] >= mapStringArr.length )
				return;
			String row = (colorMap[pos[1]]);
			char[] row_c =row.toCharArray();
			row_c[pos[0]] = (char) (dirIdx + (int)'0');

			colorMap[pos[1]] = new String(row_c);
		}

		public void addAllCars(Function<Car, int[]> func, int dirIdx, LinkedList<Car> cars)
		{
			cars.forEach((Car c) -> {
				int[] pos;
				pos = func.apply(c);
				addCar(pos);
				addToColorMap(pos, dirIdx);

			});
		}

		public void addCarsToDirection(int dirIdx, LinkedList<Car> allCars)
		{
			Function<Car, int[]> func = null;

			switch (dirIdx) {
				case 0:  func = calcCarPositions::calcCarPos_N; break;
				case 1:  func = calcCarPositions::calcCarPos_E; break;
				case 2:  func = calcCarPositions::calcCarPos_S; break;
				case 3:  func = calcCarPositions::calcCarPos_W; break;
				default:
					break;
			
			}
			if (func != null)
				addAllCars(func, dirIdx, allCars);
		}

		public void printWithColor ()
		{
			System.out.printf("%c[2J%c[;H",(char) 27, (char) 27);
			char[] colorRow;
			char[] mapRow;

			char colorChar;
			for (int y = 0; y <  mapStringArr.length ; y++)
			{
				if (colorMap[y] == null)
					System.out.println(mapStringArr[y]);
				else 
				{
					for (int x = 0; x < mapStringArr[y].length() ; x++)
					{
						colorRow = colorMap[y].toCharArray();
						mapRow = mapStringArr[y].toCharArray();

						colorChar = colorRow[x];
						if (colorChar != ' ')
						{
							colorChar -= '0';
							System.out.printf("%s%c%s",
								constants.strColorArr[colorChar],
								mapRow[x],
								constants.strResetColor);
						}
						else
							System.out.print(mapRow[x]);
					}
					System.out.print("\n");
				}
				
			}
		}

	}
	
	public void shutdown ()
	{
		try {
			this._terminal.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}

	public Animator ()
	throws IOException
	{
		_terminal =  (new DefaultTerminalFactory()).createTerminal();
		textGraphics =  _terminal.newTextGraphics();
	}

	public void printOnTerminal (JunctionMap jm, String messageString)
				throws IOException
	{
		// https://github.com/mabe02/lanterna/blob/master/docs/tutorial/Tutorial02.md
		
		_terminal.clearScreen();

		char[] colorRow;
		char[] mapRow;
		

		char colorChar;

		for (int y = 0; y <  jm.mapStringArr.length ; y++)
		{
			if (jm.colorMap[y] == null || jm.colorMap[y].isBlank())
				textGraphics.putString(0, y, jm.mapStringArr[y]);
			else 
			{
				mapRow = jm.mapStringArr[y].toCharArray();
				colorRow = jm.colorMap[y].toCharArray();

				for (int x = 0; x < jm.mapStringArr[y].length() ; x++)
				{

					colorChar = colorRow[x];
					if (colorChar != ' ')
					{
						int colorIdx = colorChar - '0';
						if (colorIdx >= 0 && colorIdx < constants.ANSIcolorArr.length)
							textGraphics.setForegroundColor(constants.ANSIcolorArr[colorIdx]);
						textGraphics.putString(x, y, String.valueOf(mapRow[x]));
						textGraphics.setForegroundColor(constants.ANSIdefaultColor);
					}
					else
						textGraphics.putString(x, y, String.valueOf(mapRow[x]));
				}
			}
		}
		textGraphics.putString(0, jm.mapStringArr.length + 1, messageString);
		
		_terminal.flush();
	}



	JunctionMap configureFrame(JunctionController jc)
	{
		JunctionMap newMap = new JunctionMap();
		//figure out what cars are on the road, and which are waiting in queue
		int[] carQueues = {0,0,0,0};
		LinkedList<Car> allCars;

		var jc_state = jc.getJunctionState();
		String stateString = (jc_state.get("currentPhase").equals("NS_GREEN") ? "North -> South (phase A)" : "East -> West (phase B)");
		String message =String.format("[%ds]\t%s\n", jc_state.get("elapsedTime"), stateString);


		//go over each road.
		for (int i = 0; i < 4; i++)
		{
			carQueues[i] = jc.getRoads()[i].getQueueLen();
			Iterator<Car> counter = jc.getRoads()[i].getWaitingCars().iterator();
			//Max of 5 cars are added to list. no point ading more bc they wont be printed
			allCars = new LinkedList<>();
			while (allCars.size() < 5 && counter.hasNext())
				allCars.add(counter.next());



			allCars = (LinkedList<Car>) (jc.getRoads()[i].getWaitingCars());
			// allCars = (LinkedList<Car>) (jc.getRoads()[i].getWaitingCars());
			allCars.addAll((LinkedList<Car>) (jc.getRoads()[i].getRoadCars()));
			allCars.addAll((LinkedList<Car>) (jc.getRoads()[i].getPassedCars()));

			newMap.addCarsToDirection (i, allCars);
		}

		//actually build map
		newMap.addTitles(carQueues);
		
		// newMap.printWithColor(); // printer for user console
		// System.out.print(message);

		try{
			printOnTerminal(newMap, message);
		} catch (IOException e)
		{
			System.out.println(e);
			System.exit(1);
		}
		return newMap;
	}

}
