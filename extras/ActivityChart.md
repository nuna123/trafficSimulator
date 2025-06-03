```

/start {JunctionController.start()}
	start activities: 
		initialize scheduler;
		addShutdownHook
|
/runnable {JunctionController.tick()}
	- add to elapsedTime
	- greenlight_tick() on correct roads
	|	- Loop over all cars: {CARLOOP}
	|	|	- increase position:
	|	|		- IF car.position is between 0 and 1 (is on road)
	|	|				OR car.position == 0 (first in queue) and theres
	|	|					enough time to cross
	|	|			=> advance by 1/car.crossingTime
	|	|	
	|	|	- evaluate car positions, position on road and extract info
	|	|		IF Car.position >= 1	//car passed the road
	|	|			=> carsPassed ++
	|	|		ELSE IF car.position > 0	// car is currently on road
	|	|			=> carsOnRoad ++
	|	|		ELSE IF car.position <= 0	// car is in qqueue, waiting to cross
	|	|			=> LOOP
	|	|				=> update car.position to represent its location in loop
	|	|				=> BREAK from {CARLOOP}
	|	|
	|	- clean up cars that passed the junction
	|	|	LOOP 
	|	|		IF car.position >= 1
	|	|			DELETE car
		| RETURN carsPassed, carsOnRoad

	- update _currentPhase (using carsPassed, carsOnRoad)
	- IF phase switching needed => switch


```