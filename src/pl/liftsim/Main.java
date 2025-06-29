package pl.liftsim;

import pl.liftsim.controller.ElevatorController;
import pl.liftsim.model.ElevatorModel;
import pl.liftsim.view.ElevatorSimulatorView;

import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    ElevatorModel elevatorModel = new ElevatorModel();
    SwingUtilities.invokeLater(
        () -> {
          ElevatorSimulatorView view = new ElevatorSimulatorView(elevatorModel);
          // Initialize elevator at floor 1
          view.getFloorViewPanel().updateElevatorPosition(1);

          view.setVisible(true);
        });
  }
}
