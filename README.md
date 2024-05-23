# Planet Simulation

This is a simple planetary simulation written in Java using Swing for the GUI. The simulation models the motion of planets around the sun using basic gravitational physics and includes the Sun, Mercury, Venus, Earth, and Mars.

## Prerequisites

- Java Development Kit (JDK) installed. You can download it from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or install it via a package manager (e.g., `apt`, `brew`).

## Directory Structure

``` 
PlanetSimulation/
├── .gitignore
├── README.md
└── src/
    └── PlanetSimulation.java 
``` 

## How to Run

1. **Compile the Java file:**

    Navigate to the root directory of the project and run the following command to compile the source file:

    ```sh
    javac src/PlanetSimulation.java
    ```

2. **Run the compiled Java program:**

    After compiling, run the program using the following command:

    ```sh
    java -cp src PlanetSimulation
    ```

## Code Overview

The main class `PlanetSimulation` extends `JPanel` and sets up the simulation. The `Planet` inner class models the properties and behaviors of a planet, including drawing and updating its position based on gravitational forces.

### Key Components

- **PlanetSimulation Class**: Initializes the simulation and manages the rendering of planets.
- **Planet Class**: Represents a planet, including its position, velocity, mass, and drawing method.
- **Gravity Calculation**: Uses Newton's law of universal gravitation to compute the forces between planets and update their velocities and positions accordingly.

### Main Simulation Loop

The main loop of the simulation runs in a separate thread, updating the positions of the planets and repainting the frame at each timestep.

```java
Thread simulationThread = new Thread(() -> {
    while (true) {
        try {
            Thread.sleep((long) (TIMESTEP / 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Planet planet : simulation.planets) {
            planet.updatePosition(simulation.planets);
        }
        frame.repaint();
    }
});
simulationThread.start();
```
