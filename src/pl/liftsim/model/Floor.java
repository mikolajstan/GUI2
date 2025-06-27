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

    public void addWWaitingPassenger(Passenger passenger) {
        waitingPassengers.add(passenger);
    }

    public List<Passenger> getWaitingPassengers() {
        return new ArrayList<>(waitingPassengers);
    }

    public void clearWaitingPassengers() {
        waitingPassengers.clear();
    }
}
