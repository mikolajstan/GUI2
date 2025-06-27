package pl.liftsim;

import pl.liftsim.model.ElevatorModel;
import pl.liftsim.view.ElevatorSimulatorView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ElevatorModel elevatorModel = new ElevatorModel();
        SwingUtilities.invokeLater(() -> {
            ElevatorSimulatorView view = new ElevatorSimulatorView(elevatorModel);
            view.setVisible(true);

            // Test button state change
            view.getControlPanel().getStartButton().addActionListener(e -> {
                view.getControlPanel().setStartButton(false);

                // re-enable after a dela
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        SwingUtilities.invokeLater(() ->
                                view.getControlPanel().setStartButton(true));
                    } catch (InterruptedException ex) {
                    }
                }).start();
            });
        });
    }
}