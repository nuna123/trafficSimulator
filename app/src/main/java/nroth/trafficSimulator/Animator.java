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


public class Animator {

	private final Terminal _terminal;
	private final TextGraphics textGraphics;


	//holder for the car position calculation functions

	// 0 = North → Y increases
	// 1 = East → X decreases
	// 2 = South → Y decreases
	// 3 = West → X increases



	//constants needed for displaying
	public class constants
	{
		final static int[][] lanePointZero = {
				{19, 4},	// North
				{29, 6},	// East
				{25, 10},	// South
				{15, 8}		// West
		};
		final static int laneSize_Y = 5; // for cars coming from N or S
		final static int laneSize_X = 13; // for cars coming from E or W

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
		// ANSI color codes for directions:
		final static String[] strColorArr = {
				"\u001B[31m", // North - Red
				"\u001B[32m", // East  - Green
				"\u001B[33m", // South - Yellow
				"\u001B[34m"  // West  - Blue
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
		public static int[] CarPositionCalculator(Car car, int directionIndex)
		{
			int[] startPoint = constants.lanePointZero[directionIndex].clone();

			boolean isVertical = (directionIndex % 2 == 0); // true for N/S, false for E/W
			int directionSign = (directionIndex % 3 == 0 ? 1 : -1); // 1 for N/W, -1 for S/E

			float laneLength = isVertical ? constants.laneSize_Y : constants.laneSize_X;
			int positionOffset = 0;

			if (car.posInJunction <= 0)
				positionOffset += car.posInJunction;
			else if (car.posInJunction > 0 && car.posInJunction < 1)
				positionOffset += 1 + (laneLength * car.posInJunction);
			else // if pos > 1
				positionOffset += laneLength + (car.posInJunction);

			// For S or E, position decreases as car advances
			positionOffset *= directionSign;

			// For N/S, Y changes; for E/W, X changes
			if (isVertical) 
				startPoint[1] = startPoint[1] + positionOffset;
			else 
				startPoint[0] = startPoint[0] + positionOffset;
			
			return startPoint;
		}

		public void addCarsToDirection(int dirIdx, LinkedList<Car> allCars)
		{
			allCars.forEach((Car c) -> {
				int[] pos;
				pos = CarPositionCalculator(c, dirIdx);
				addCar(pos);
				addToColorMap(pos, dirIdx);

			});

			
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
	throws IOException
	{
		JunctionMap newMap = new JunctionMap();
		//figure out what cars are on the road, and which are waiting in queue
		int[] carQueues = {0,0,0,0};
		LinkedList<Car> allCars;

		var jc_state = jc.getJunctionState();
		String stateString = (jc_state.get("currentPhase").equals("NS_GREEN") ? "North -> South (phase A)" : "East -> West (phase B)");
		String message =String.format("[%ds]\t%s\n\n", jc_state.get("elapsedTime"), stateString);


		//go over each road.
		for (int i = 0; i < 4; i++)
		{
			carQueues[i] = jc.getRoads()[i].getQueueLen();
			Iterator<Car> counter = jc.getRoads()[i].getWaitingCars().iterator();
			//Max of 5 cars are added to list. no point ading more bc they wont be printed
			allCars = new LinkedList<>();
			while (allCars.size() < 5 && counter.hasNext())
				allCars.add(counter.next());
			allCars.addAll((LinkedList<Car>) (jc.getRoads()[i].getRoadCars()));
			allCars.addAll((LinkedList<Car>) (jc.getRoads()[i].getPassedCars()));

			newMap.addCarsToDirection (i, allCars);
		}

		newMap.addTitles(carQueues);

		//may throw IOEXCEPTION
		printOnTerminal(newMap, message);
		
		return newMap;
	}

}
