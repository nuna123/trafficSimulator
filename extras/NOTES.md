# Traffic Simulator
Traffic Intersection Simulator - Green:Code Internship Assignment


## TODO
- [x] Make car move across the road in real time!
- [x] Define Vehicle class NOTE: done as record in Road class
	- [ ] OPTIONAL:  trucks? to be *2 size, (S), of regular cars

- [x] Road class
		will contain a queue of the cars
			queue can be added and removed from in FIFO style
		when prompted, will assert how many cars shall pass given the Phase - green light - time

- [x] JunctionController class
	- [ ] Arr[4] of Roads
	- [x] light switching logic -
				Limit by number of cars??? time???? both????
				wait for car to arrive-> turn on phase A/B -> switch to other phase if needed
				if no cars, no need to turn any lights on.
	- BONUS
		-[ ] tcp connection on a separate thread to accept input.

- [x] Add Config validation for unreasonable values:
	[x]- S cant be larger than X1/X2!
	[x]- A[1-4] can be only positive int or -1
	[x]- other values than A[1-4] can be only positive int, largr than 0

- [x] Write unit tests for Intersection

- [x] Add diagrams - UML



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

	Car {
		length = 1;
		S [time to cross junction]
		Position in junction  [-QUEUELEN	= not first in road.
								0 			= first in road
								>0&<1		= on road
									+1/S	every second
								>=1			= car has passed the road]
	}

	Road
		contain a queue of the cars
		queue can be added and removed from in FIFO style
		tasks for each second - 
			- advance cars on road
			- calculate if car in queue position 0 should start crossing, based on time left in current phase
			- if car in queue 0 passes, advance cars waiting in queue 

	JunctionController
		contain a list of 4 roads
		controlls phase switching, timing, car arrivals, logging


# GRADLE
## what is it?
A build automation tool that simplifies the process of building and deploying software.

## DSL (Domain Specific Language)
    The language used to configure build scripts in gradle. Gradle uses either Groovy or Kotlin
   *Groovy* :
   - Original Gradle DSL.
   - Syntax more dynamic, flexible
   - easier for beginners
   - because it has longer history, more online examples/tutorials
   *Kotlin* :
   - Introduced to Gradle to address limitations of Groovy.
   - Syntax is more Strict
   - Type-safe (ensures the datatype is correct at compile time)
   - even though its newer than Groovy (introduced 2016) has sa ton of online guides


## The Gradle Wrapper
    The Wrapper is a script that invokes a declared version of Gradle, downloading it beforehand if necessary. Instead of running gradle build using the installed Gradle, you use the Gradle Wrapper by calling ./gradlew build.
    - allows to run a gradle project without having gradle installed on your system.
    - makes sure the same version of gradle is used for builds by different devs
[./gradlew build] - will invoke the wrapper, download and cache the Gradle binaries if they are not installed on the host. will create /app/build folder

##tasks
    work unit that can be done by Gradle as part of the build - like compilation, copying files around, etc
    view using [./gradlew tasks]


##files:
### /gradle
    Contains the JAR file and configuration of the Gradle Wrapper.
### /gradlew  /gradlew.bat
    scripts for executing builds using the Gradle Wrapper
### /settings.gradle.kts
	The project’s settings file where the list of subprojects is defined.
    the entrypoint to the project.
### app
    The source code and build configuration for my app.
###     app/build.gradle.kts
	configuration file to define which dependencies and plugins the app subproject is using
