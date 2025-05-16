# Traffic Simulator
Traffic Intersection Simulator - Green:Code Internship Assignment

## TODO
- [ ] Define Vehicle class
- [ ] Implement TrafficLight switching
- [ ] Add Config validation for unreasonable values:
	[x]- S cant be larger than X1/2!
- [ ] Write unit tests for Intersection

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

