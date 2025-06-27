package pl.liftsim.model;

public class Passenger {
  private final int id;
  private final int fromFloor;
  private int destinationFloor;

  public Passenger(int id, int fromFloor) {
    this.id = id;
    this.fromFloor = fromFloor;
    this.destinationFloor = -1; // Will be set when entering elevator
  }

  public Passenger(int id, int fromFloor, int destinationFloor) {
    this.id = id;
    this.fromFloor = fromFloor;
    this.destinationFloor = destinationFloor;
  }

  public int getId() {
    return id;
  }

  public int getFromFloor() {
    return fromFloor;
  }

  public int getDestinationFloor() {
    return destinationFloor;
  }

  public void setDestinationFloor(int destinationFloor) {
    this.destinationFloor = destinationFloor;
  }

  @Override
  public String toString() {
    return "P" + id + (destinationFloor != -1 ? "→" + destinationFloor : "");
  }
}
