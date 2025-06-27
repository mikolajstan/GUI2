package pl.liftsim.view;

import pl.liftsim.model.ElevatorModel;

import javax.swing.*;
import java.awt.*;

public class ElevatorSimulatorView extends JFrame {
  private ControlPanel controlPanel;
  private FloorControlPanel floorControlPanel;
  private FloorViewPanel floorViewPanel;

  public ElevatorSimulatorView(ElevatorModel elevatorModel) {
    setupMainWindow();
    createComponents(elevatorModel);
    createLayout();
  }

  private void setupMainWindow() {
    setTitle("Elevator Simulator");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //    setSize(1200, Toolkit.getDefaultToolkit().getScreenSize().height);
    setSize(1100, 600);
    setLocationRelativeTo(null); // Center the window on the screen
  }

  private void createComponents(ElevatorModel elevatorModel) {
    controlPanel = new ControlPanel();
    floorControlPanel = new FloorControlPanel(elevatorModel);
    floorViewPanel = new FloorViewPanel(elevatorModel);
  }

  private void createLayout() {
    setLayout(new BorderLayout(10, 20));

    JPanel westWrapper = new JPanel();
    westWrapper.setLayout(new BoxLayout(westWrapper, BoxLayout.Y_AXIS));
    westWrapper.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Elevator Control Panel"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    westWrapper.setBackground(Color.LIGHT_GRAY);

    // Add vertical glue to center the panel vertically
    westWrapper.add(Box.createVerticalGlue());

    // Center the panel horizontally
    JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    centeringPanel.setOpaque(false);
    centeringPanel.add(floorControlPanel);
    westWrapper.add(centeringPanel);

    add(westWrapper, BorderLayout.WEST);

    add(floorViewPanel, BorderLayout.CENTER);

    //    add(createPlaceholderPanel("CENTER - Elevator Shaft", Color.WHITE), BorderLayout.CENTER);
    //    add(createPlaceholderPanel("EAST - Call Buttons", Color.LIGHT_GRAY), BorderLayout.EAST);

    add(controlPanel, BorderLayout.SOUTH);
  }

  public ControlPanel getControlPanel() {
    return controlPanel;
  }

  public FloorControlPanel getFloorControlPanel() {
    return floorControlPanel;
  }

  private JPanel createPlaceholderPanel(String text, Color color) {
    JPanel panel = new JPanel();
    panel.setBackground(color);
    panel.setBorder(BorderFactory.createTitledBorder(text));
    panel.setPreferredSize(new Dimension(200, 100)); // temp sizing
    return panel;
  }

  public FloorViewPanel getFloorViewPanel() {
    return floorViewPanel;
  }
}
