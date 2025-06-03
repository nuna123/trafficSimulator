
# Traffic Intersection Simulator

Java application that simulates a four‑way traffic‑light junction. Cars arrive at user‑defined intervals, traffic phases switch automatically, and information is logged in real time.
The application cycles between 2 phases: 
	NS_GREEN (Phase A): North ↔ South car movement
	EW_GREEN (Phase B): East ↔ West car movement


---
## Installation

### Prerequisites

* **JDK 17** or later (the code uses modern Java features such as enhanced `switch`).

* **Gradle 8** (optional). Can be compiled with JDK alone.

### Clone & Build

```bash
# Clone the repository
$ git clone https://github.com/nuna123/trafficSimulator.git
$ cd trafficSimulator

# Build with Gradle
$ ./gradlew build
```

---

## Usage

Run the simulator with the default configuration:

```bash
$ ./gradlew run
```

Example console output:

```
[0s]	 MAIN: CONFIG:{A1=-1, A2=2, A3=3, S=4, A4=3, X1=5, X2=6}
[0s]	 From JunctionController: starting simulation...
[2s]	 West: Car [1AA2144] Arrived
[3s]	 South: Car [1AA4248] Arrived
[3s]	 East: Car [1AA1186] Arrived
[4s]	 West: Car [1AA3198] Arrived
[5s]		  --------Phase switch!-----------------
		last phase overview: 
				Phase: NS_GREEN; len: 5; timer: 5; carsPassed: 0; carsOnRoad: 0
		New phase: EW_GREEN
		Car Queues:	  N [0] ; E [2] ; S [1] ; W :[1]
```

Press **Ctrl+C** to terminate and print a final summary table.

---

## Configuration

The simulator reads `config.properties` from the classpath. Default config in `/src/main/resources/`

All values are positive integers:
| Variable | Description												  |
|----------|--------------------------------------------------|
| `X1`	  | Duration of Phase A (North-South green light)	 |
| `X2`	  | Duration of Phase B (East-West green light)		|
| `S`		| Time taken by a car to cross the intersection	 |
| `A1`	  | Arrival interval of cars from **North** (Road 1) |
| `A2`	  | Arrival interval of cars from **East** (Road 2)  |
| `A3`	  | Arrival interval of cars from **South** (Road 3) |
| `A4`	  | Arrival interval of cars from **West** (Road 4)  |

*All values must be present in order for the program to run*
Note: `A[1-4]` may be set to -1 to disable road arrivals
**Rule checks** performed by `ConfigReader`:
* All required keys (`X1`, `X2`, `S`, `A1‑A4`) must be present.
* `S` may not exceed `X1` or `X2`.
* `A1‑A4` must be either a positive integer or `-1`.

---
## Code Overview

### Assumptions
- Cars may cross only during their designated green light phase.
- There are n pedestrian crosswalks.
- All cars travel straight across the intersection (no turns).
- A car will not start crossing if there isn’t enough time to cross safely.
- All cars take exactly `S` seconds to cross the intersection.


### Project Structure
```
.
├── app/                                   # Main application module
│   ├── bin/                               # Compiled binaries
│   ├── build.gradle                       # Gradle build script for the app module
│   ├── design/                            # Class diagrams and code
│   └── src/
│       ├── main/
│       │   ├── java/nroth/trafficSimulator/
│       │   │   ├── App.java               # Application entry point
│       │   │   ├── Car.java               # Class for a car in the simulation
│       │   │   ├── ConfigReader.java      # Loads configuration settings
│       │   │   ├── JunctionController.java# Manages logic for junctions/intersections
│       │   │   └── Road.java              # Class for a road in the simulation
│       │   └── resources/
│       │       ├── config.properties      # Simulation config
│       │       └── logback.xml            # Logback logging configuration
│       └── test/                          # Testing files and their resources
├── build/                                 # Gradle build output (auto-generated)
├── extras/                                # Extra materials and notes
├── gradle/                                # Gradle wrapper configuration files
├── gradle.properties                      # Global Gradle settings
├── gradlew                                # Gradle wrapper shell script (Linux)
├── gradlew.bat                            # Gradle wrapper batch script (Windows)
├── README.md                              # Project documentation
└── settings.gradle                        # Gradle project settings and modules


```

### Key Classes

**App** 
	Application entry point.
	Loads config, initializes  `JunctionController`,  and calls `start()` to begin the tick loop

**ConfigReader**
	Reads `config.properties`, validates mandatory keys and value ranges, and exposes an immutable `Map<String,Integer>`. 

**JunctionController**
	Coordinates the entire simulation: maintains the current `JunctionPhase`, schedules a 1‑second tick with `ScheduledExecutorService`, handles car arrivals, and logs events. Contains nested `JunctionPhase` (state machine for light cycles). 

**Road**
	Manages a queue of `Car` objects for one compass direction, advancing positions when the light is green and removing cars that have cleared the junction. 

**Car**
	Simple class representing a vehicle. Holds its plate (randomly generated), length, crossing time, and position relative to the junction. 

---

## Features

* **Real‑time tick loop** (1s) with Java's `ScheduledExecutorService`.

* **Dynamic phase switching:** the light only changes if vehicles are waiting on the perpendicular axis, reducing empty‑green time.

* **Per‑road arrival rates:** individual roads can be disabled with `-1`.

* **SLF4J logging** at INFO and DEBUG levels.

* **Graceful shutdown hook** prints a full junction summary on exit.
---

## Code Logic

Assuming `S = 5`:
The first car (`x1`) will take `S` seconds to pass.
The nth car (`xn`) will take `S + (n - 1)` seconds to pass.
```
0	-xx[----]
1	--x[x---]
2	---[xx--]
3	---[-xx-]
4	---[--xx]
5	---[---x]x
6	---[----]xx

- `[ ]` indicates the active intersection.
- `-` represents empty road units.
- `x` represents cars.
```
---
## Code Examples

#### Running a 2‑minute simulation and injecting traffic manually

```java

Map<String,Integer> config = App.getConfig();

JunctionController jc = new JunctionController(config);

// Add queued traffic before the lights turn green

jc.addCar('N', 3); // 3 cars from North

jc.addCar('W'); // 1 car from West

jc.start(120); // run for 120 seconds

```

---

## Tests

Unit tests are implemented with JUnit5. run them with:

```bash
./gradlew test
```
---

## Tests

Unit tests are implemented with JUnit5. run them with:

```bash
./gradlew test
```
---

## Possible future features

 - **Animation**
 	This would help visualize the car movement across the junction.
 - **Variable vhicle length**
	Introduce trucks that are different length than cars and therefore take longer to cross can be implemented
 - **Randomized parameters**
	To make the simulation more realistic, slight randomness values can be implemented. (eg. car speed, arrival rate)
 -  **Car autonomy**
	A realistic road would have movements controlled by the car, which watches for the green light.
 - **Modified light phase lengths**
	Let green light phases times adapt according to the car queues or wait times.
 - **Grid position system**
	A grid-based system with car position and dimentions to detect collisions.

