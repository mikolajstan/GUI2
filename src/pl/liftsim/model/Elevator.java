package pl.liftsim.model;


import java.util.ArrayList;
import java.util.List;

//@TODO: Implement exception handling for elevator capacity and passenger limits
public class Elevator {
    private int currentFloor;
    private final List<Passenger> passengers;

    public Elevator() {
        this.currentFloor = 0;
        this.passengers = new ArrayList<>();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    public boolean addPassenger(Passenger passenger) {
        if (passengers.size() < ElevatorModel.MAX_ELEVATOR_CAPACITY) {
            return passengers.add(passenger);
        }
        return false;
    }

    public void reset() {
        this.currentFloor = 0;
        this.passengers.clear();
    }
}
