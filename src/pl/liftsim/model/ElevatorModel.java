package pl.liftsim.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElevatorModel {
    public static final int MAX_FLOORS = 10;
    public static final int MAX_ELEVATOR_CAPACITY = 5;

    private final Elevator elevator;
    private final List<Floor> floors;
    private boolean simulationRunning;

    public ElevatorModel() {
        this.elevator = new Elevator();
        this.floors = new ArrayList<>();
        this.simulationRunning = false;

        for (int i = 0; i < MAX_FLOORS; i++) {
            floors.add(new Floor(i));
        }
    }

    public void setSimulationRunning(boolean running) {
        this.simulationRunning = running;
    }

    public void generateRandomPassengers() {
        Random random = new Random();

        for (Floor floor : floors) {
            floor.clearWaitingPassengers();
        }

        int passengerId = 1;
        for (Floor floor : floors) {
            int numPassengers = random.nextInt(MAX_ELEVATOR_CAPACITY + 1);
            for (int i = 0; i < numPassengers; i++) {
                Passenger passenger = new Passenger(passengerId++, floor.getFloorNumber());
                floor.addWWaitingPassenger(passenger);
            }
        }
    }

    public void reset() {
        elevator.reset();
        for (Floor floor : floors) {
            floor.clearWaitingPassengers();
        }
        simulationRunning = false;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }
}
