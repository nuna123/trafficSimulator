# ANIMATION

## grid:

[                    NORTH                 ] 0
[                |    ( )    |             ] 1
[                |           |             ] 2
[                |     |     |             ] 3
[                |           |             ] 4
[       ---------|- - -|- - -|---------    ] 5
[                                          ] 6
[   WEST( ) - -  |- - -|- - -|- - - ( )EAST] 7
[                                          ] 8
[       ---------|- - -|- - -|---------    ] 9
[                |           |             ] 10
[                |     |     |             ] 11
[                |           |             ] 12
[                |    ( )    |             ] 13
[                    SOUTH                 ] 14

# constants:

	cars position in lane:
		north_lane = junction[19, CARPOS]
		south_lane = junction[25, CARPOS]
		east_lane = junction[CARPOS, 6]
		west_lane = junction[CARPOS, 8]

	// how many units are across the junction
	// from carPosition 0, it sould take DIS to cross the road
	
	laneSize_X = 13  // horizontal size, cars coming from W / E
	laneSize_Y = 5  // vertical size, cars coming from N / S


	// start position of the car BEFORE it is in the junction
	int[][] lanePointZero = {
		{19, 4}, //N
		{29,6}, //E
		{25, 10}, //S
		{15, 8}, //W
	};


	//Car Queues positions
	//positions of the queue count title
	N = [22,1]
	S = [22,13]
	E = [36,7]
	W = [9,7]


##
	TODO:
	[x] - cars passing the junction should also be printed. the structure of Road needs to keep and advance passing cars.
	[ ] - Car directions:
		[x] - North
		[ ] - South
		[ ] - East
		[x] - West
	[ ] - current phase title
	[ ] - timer title
	[ ] - total passed cars? per road counter?
	[ ] - Colors??
	



