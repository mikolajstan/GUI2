package pl.liftsim.controller;

import pl.liftsim.model.ElevatorModel;
import pl.liftsim.view.ElevatorSimulatorView;

public class ElevatorController {
    private final ElevatorModel model;
    private final ElevatorSimulatorView view;

    public ElevatorController(ElevatorModel model, ElevatorSimulatorView view) {
        this.model = model;
        this.view = view;
//        setupEventHandlers();
    }

//    private void setupEventHandlers() {
//        view.setOnFloorButtonPressed(floor -> model.requestElevator(floor));
//        view.setOnElevatorButtonPressed((floor, passenger) -> model.addPassengerToElevator(floor, passenger));
//        view.setOnResetButtonPressed(() -> model.resetSimulation());
//    }
}
