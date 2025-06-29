package pl.liftsim.model;

import java.util.ArrayList;
import java.util.List;

public class Floor {
  private final int floorNumber;
  private final List<Passenger> waitingPassengers;

  public Floor(int floorNumber) {
    this.floorNumber = floorNumber;
    this.waitingPassengers = new ArrayList<>();
  }

  public int getFloorNumber() {
    return floorNumber;
  }

  public List<Passenger> getWaitingPassengers() {
    return waitingPassengers;
  }

  public void addWaitingPassenger(Passenger passenger) {
    waitingPassengers.add(passenger);
  }

  public void clearWaitingPassengers() {
    waitingPassengers.clear();
  }
}
