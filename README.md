# Traffic Simulator

Traffic Intersection Simulator - Green:Code Internship Assignment

---

## Overview

This project simulates a four-way intersection with two traffic light phases:

- **Phase A:** North ↔ South Movement
- **Phase B:** East ↔ West Movement

**Key Features:**
- Vehicles arrive from each direction at user-defined intervals.
- Cars may cross only during their designated green light phase.
- No pedestrian crosswalks.
- All cars travel straight across the intersection (no turns).
- A car will not start crossing if there isn’t enough time to cross safely.

---

## Usage

The project is written in Java and uses Gradle.

To build and run:

```bash
./gradlew build
./gradlew run
```

---

## Configuration

The configuration file is located at:
`app/src/main/resources/config.properties`

> The path is hardcoded as `String CONFIG` in `App.java` and may be changed.

| Variable | Description                                      |
|----------|--------------------------------------------------|
| `X1`     | Duration of Phase A (North-South green light)    |
| `X2`     | Duration of Phase B (East-West green light)      |
| `S`      | Time taken by a car to cross the intersection    |
| `A1`     | Arrival interval of cars from **North** (Road 1) |
| `A2`     | Arrival interval of cars from **East** (Road 2)  |
| `A3`     | Arrival interval of cars from **South** (Road 3) |
| `A4`     | Arrival interval of cars from **West** (Road 4)  |

---

## Car Passage Timing

Assuming `S = 5`:

- The first car (`x1`) will take `S` seconds to pass.
- The nth car (`xn`) will take `S + (n - 1)` seconds to pass.

| Seconds |   Road    |
|---------|-----------|
|   0     | -xx[----] |
|   1     | --x[x---] |
|   2     | ---[xx--] |
|   3     | ---[-xx-] |# Traffic Simulator

Traffic Intersection Simulator - Green:Code Internship Assignment

---

## Overview

This project simulates a four-way intersection with two traffic light phases:

- **Phase A:** North ↔ South Movement
- **Phase B:** East ↔ West Movement

**Key Features:**
- Vehicles arrive from each direction at user-defined intervals.
- Cars may cross only during their designated green light phase.
- No pedestrian crosswalks.
- All cars travel straight across the intersection (no turns).
- A car will not start crossing if there isn’t enough time to cross safely.

---

## Usage

The project is written in Java and uses Gradle.

To build and run:

```bash
./gradlew build
./gradlew run
```

---

## Configuration

The configuration file is located at:
`app/src/main/resources/config.properties`

> The path is hardcoded as `String CONFIG` in `App.java` and may be changed.

| Variable | Description                                      |
|----------|--------------------------------------------------|
| `X1`     | Duration of Phase A (North-South green light)    |
| `X2`     | Duration of Phase B (East-West green light)      |
| `S`      | Time taken by a car to cross the intersection    |
| `A1`     | Arrival interval of cars from **North** (Road 1) |
| `A2`     | Arrival interval of cars from **East** (Road 2)  |
| `A3`     | Arrival interval of cars from **South** (Road 3) |
| `A4`     | Arrival interval of cars from **West** (Road 4)  |

---

## Car Passage Timing

Assuming `S = 5`:

- The first car (`x1`) will take `S` seconds to pass.
- The nth car (`xn`) will take `S + (n - 1)` seconds to pass.

| Seconds |   Road    |
|---------|-----------|
|   0     | -xx[----] |
|   1     | --x[x---] |
|   2     | ---[xx--] |
|   3     | ---[-xx-] |
|   4     | ---[--xx] |
|   5     | ---[---x]x|
|   6     | ---[----]xx|

- `[ ]` indicates the active intersection.
- `-` represents empty road units.
- `x` represents cars.

---# Traffic Simulator

Traffic Intersection Simulator - Green:Code Internship Assignment

---

## Overview

This project simulates a four-way intersection with two traffic light phases:

- **Phase A:** North ↔ South Movement
- **Phase B:** East ↔ West Movement

**Key Features:**
- Vehicles arrive from each direction at user-defined intervals.
- Cars may cross only during their designated green light phase.
- No pedestrian crosswalks.
- All cars travel straight across the intersection (no turns).
- A car will not start crossing if there isn’t enough time to cross safely.

---

## Usage

The project is written in Java and uses Gradle.

To build and run:

```bash
./gradlew build
./gradlew run
```

---

## Configuration

The configuration file is located at:
`app/src/main/resources/config.properties`

> The path is hardcoded as `String CONFIG` in `App.java` and may be changed.

| Variable | Description                                      |
|----------|--------------------------------------------------|
| `X1`     | Duration of Phase A (North-South green light)    |
| `X2`     | Duration of Phase B (East-West green light)      |
| `S`      | Time taken by a car to cross the intersection    |
| `A1`     | Arrival interval of cars from **North** (Road 1) |
| `A2`     | Arrival interval of cars from **East** (Road 2)  |
| `A3`     | Arrival interval of cars from **South** (Road 3) |
| `A4`     | Arrival interval of cars from **West** (Road 4)  |

---

## Car Passage Timing

Assuming `S = 5`:

- The first car (`x1`) will take `S` seconds to pass.
- The nth car (`xn`) will take `S + (n - 1)` seconds to pass.

| Seconds |   Road    |
|---------|-----------|
|   0     | -xx[----] |
|   1     | --x[x---] |
|   2     | ---[xx--] |
|   3     | ---[-xx-] |
|   4     | ---[--xx] |
|   5     | ---[---x]x|
|   6     | ---[----]xx|

- `[ ]` indicates the active intersection.
- `-` represents empty road units.
- `x` represents cars.

---
|   4     | ---[--xx] |
|   5     | ---[---x]x|
|   6     | ---[----]xx|

- `[ ]` indicates the active intersection.
- `-` represents empty road units.
- `x` represents cars.

---