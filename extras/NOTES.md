# trafficSimulator
Traffic Intersection Simulator - Green:Code Internship Assignment


## CONFIG VARIABLES: 
- X1		Phase A length in sec
- X2		Phase B length in sec
- S			How long for a car to cross the road, in sec
- A[1-4]	How often does a car approach each road, from North(1) clockwise to West(4)


ALL VALUES ARE KEPT IN CONFIG.PROPERTIES


CLASSES:
	ConfigReader
		reads the config file, makes sure all values are valid and present. 
		reads into a Map <String, Float>

		