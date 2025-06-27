package pl.liftsim.model;

public class Passenger {
    private final int id;
    private final int originFloor;

    public Passenger(int id, int originFloor) {
        this.id = id;
        this.originFloor = originFloor;
    }

    public int getId() {
        return id;
    }

    public int getOriginFloor() {
        return originFloor;
    }
}
