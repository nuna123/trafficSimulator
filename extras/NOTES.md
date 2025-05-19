# Traffic Simulator
Traffic Intersection Simulator - Green:Code Internship Assignment

NOTE : test road green light timing better, add documentation.

## TODO
- [x] Define Vehicle class NOTE: done as record in Road class
	- [ ] OPTIONAL:  trucks? to be *2 size, (S), of regular cars

- [x] Road class
		will contain a queue of the cars
			queue can be added and removed from in FIFO style
		when prompted, will assert how many cars shall pass given the Phase - green light - time

- [ ] JunctionController class
	- [ ] Arr[4] of Roads
	- [ ] light switching logic -
				Limited by number of cars??? time???? both????
				wait for car to arrive-> turn on phase A/B -> switch to other phase if needed
				if no cars, no need to turn any lights on.
	- BONUS
		-[ ] tcp connection on a separate thread to accept input.

- [ ] Add Config validation for unreasonable values:
	[x]- S cant be larger than X1/X2!
- [ ] Write unit tests for Intersection
- [ ] Add diagrams - UML and Sequence/Activity diagram


## CONFIG VARIABLES:
- X1		Phase A length in sec
- X2		Phase B length in sec
- S			How long for a car to cross the road, in sec
- A[1-4]	How often does a car approach each road, from North(1) clockwise to West(4)


ALL VALUES ARE KEPT IN CONFIG.PROPERTIES


## CLASSES:
	ConfigReader
		reads the config file, makes sure all values are valid and present.
		reads into a Map <String, Float>

	## TODO
	Road
		will contain a queue of the cars
		queue can be added and removed from in FIFO style
		when prompted, will assert how many cars shall pass given the Phase, green light, time


# UM?
- if a car is standing behind another, how long would it take it to reach the juncture? is it calculated as 1timeunit / car or should the distance of the car from the cross be calculated?

