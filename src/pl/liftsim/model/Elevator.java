package pl.liftsim.model;

import java.util.ArrayList;
import java.util.List;

public class Elevator {
  private int currentFloor;
  private final List<Passenger> passengers;

  public Elevator() {
    this.currentFloor = 1; // Start at floor 1, not 0
    this.passengers = new ArrayList<>();
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public void setCurrentFloor(int floor) {
    this.currentFloor = floor;
  }

  public List<Passenger> getPassengers() {
    return passengers; // Return the actual list, not a copy
  }

  public boolean addPassenger(Passenger passenger) {
    if (passengers.size() < ElevatorModel.MAX_ELEVATOR_CAPACITY) {
      return passengers.add(passenger);
    }
    return false;
  }

  public void reset() {
    this.currentFloor = 1; // Reset to floor 1
    this.passengers.clear();
  }
}
