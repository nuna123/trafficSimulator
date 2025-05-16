# Traffic Simulator
Traffic Intersection Simulator - Green:Code Internship Assignment


# ABOUT
This project will simulate a four-way cross road. Each cycle is made of two phases - 
 - PHASE A: North ↔ South Movement
 - PHASE B: East ↔ West Movement

Each road will have cars approaching it in set intervals.

## CONFIG VARIABLES: 

| Variable | Description                                      | Unit        |
| -------- | ------------------------------------------------ | ----------- |
| `X1`     | Duration of Phase A (North-South green light)    | seconds     |
| `X2`     | Duration of Phase B (East-West green light)      | seconds     |
| `S`      | Time taken by a car to cross the intersection    | seconds     |
| `A1`     | Arrival interval of cars from **North** (Road 1) | seconds/car |
| `A2`     | Arrival interval of cars from **East** (Road 2)  | seconds/car |
| `A3`     | Arrival interval of cars from **South** (Road 3) | seconds/car |
| `A4`     | Arrival interval of cars from **West** (Road 4)  | seconds/car |

## ASSUMPTIONS

 - There are no pedestrian crosswalks
 - All cars are only going to the road across them, without turning.
 - A car will not start crossing the road if it doesnt have enough time to cross safely to the other side.
