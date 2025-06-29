package pl.liftsim.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import pl.liftsim.model.ElevatorModel;

/**
 * Central panel showing all floors in a scrollable view Each floor has elevator
 * shaft, call button,
 * and passenger area
 */
public class FloorViewPanel extends JPanel {
  private final int numFloors;
  private final List<JPanel> floorPanels;
  private final List<JPanel> elevatorShafts;
  private final List<JButton> callButtons; // Single call button per floor
  private final List<JPanel> passengerAreas;
  private JScrollPane scrollPane;

  private ElevatorCarView elevatorCar;

  private static final int FLOOR_HEIGHT = 160;
  private static final int SEPARATOR_HEIGHT = 5;
  private static final int ELEVATOR_SHAFT_WIDTH = 60;
  private static final int CALL_BUTTON_WIDTH = 50;
  private static final int CALL_BUTTON_HEIGHT = 20;
  private static final int SHAFT_WALL_WIDTH = 3;
  private static final Color FLOOR_COLOR = new Color(245, 245, 245);
  private static final Color SEPARATOR_COLOR = Color.BLACK;
  private static final Color SHAFT_COLOR = new Color(220, 220, 220);
  private static final Color SHAFT_BORDER_COLOR = Color.DARK_GRAY;
  private static final Color CALL_BUTTON_COLOR = Color.BLACK;
  private static final Color PASSENGER_AREA_COLOR = new Color(255, 255, 240);

  public FloorViewPanel(ElevatorModel elevatorModel) {
    this.numFloors = elevatorModel.getFloors().size();
    this.floorPanels = new ArrayList<>();
    this.elevatorShafts = new ArrayList<>();
    this.callButtons = new ArrayList<>();
    this.passengerAreas = new ArrayList<>();
    this.elevatorCar = new ElevatorCarView();

    setupLayout();
    createFloorPanels();
    setupScrollPane();
    scrollToBottom();
  }

  private void setupLayout() {
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createTitledBorder("Building View"));
  }

  private void createFloorPanels() {
    JPanel floorsContainer = new JPanel();
    floorsContainer.setLayout(new BoxLayout(floorsContainer, BoxLayout.Y_AXIS));
    floorsContainer.setBackground(Color.WHITE);

    // Initialize call button list with nulls first
    for (int i = 0; i < numFloors; i++) {
      callButtons.add(null);
    }

    // Create floors from top to bottom (highest floor number first)
    for (int floor = numFloors; floor >= 1; floor--) {
      JPanel floorPanel = createSingleFloor(floor);
      floorPanels.add(0, floorPanel);

      floorsContainer.add(floorPanel);

      if (floor > 1) {
        JPanel separator = createSeparator();
        floorsContainer.add(separator);
      }
    }

    scrollPane = new JScrollPane(floorsContainer);
    add(scrollPane, BorderLayout.CENTER);
  }

  private JPanel createSingleFloor(int floorNumber) {
    JPanel floor = new JPanel(new BorderLayout());
    floor.setPreferredSize(new Dimension(0, FLOOR_HEIGHT));
    floor.setMinimumSize(new Dimension(300, FLOOR_HEIGHT));
    floor.setBackground(FLOOR_COLOR);
    floor.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    // Floor number label
    JLabel floorLabel = new JLabel(String.valueOf(floorNumber), SwingConstants.CENTER);
    floorLabel.setFont(new Font("Arial", Font.BOLD, 12));
    floorLabel.setPreferredSize(new Dimension(35, 25));
    floorLabel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    floorLabel.setOpaque(true);
    floorLabel.setBackground(Color.WHITE);

    JPanel labelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    labelWrapper.setBackground(FLOOR_COLOR);
    labelWrapper.add(floorLabel);

    floor.add(labelWrapper, BorderLayout.WEST);

    // Create the main content area
    JPanel contentArea = createFloorContentArea(floorNumber);
    floor.add(contentArea, BorderLayout.CENTER);

    return floor;
  }

  private JPanel createFloorContentArea(int floorNumber) {
    JPanel contentArea = new JPanel(new BorderLayout());
    contentArea.setBackground(FLOOR_COLOR);
    contentArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    // Elevator shaft on the left
    JPanel elevatorShaft = createElevatorShaft(floorNumber);
    elevatorShafts.add(elevatorShaft);
    contentArea.add(elevatorShaft, BorderLayout.WEST);

    // Call button and passenger area on the right
    JPanel rightPanel = createRightPanel(floorNumber);
    contentArea.add(rightPanel, BorderLayout.CENTER);

    return contentArea;
  }

  private JPanel createElevatorShaft(int floorNumber) {
    JPanel shaft = new JPanel(new BorderLayout());
    shaft.setPreferredSize(new Dimension(ELEVATOR_SHAFT_WIDTH, FLOOR_HEIGHT - 30));
    shaft.setMinimumSize(new Dimension(ELEVATOR_SHAFT_WIDTH, FLOOR_HEIGHT - 30));
    shaft.setMaximumSize(new Dimension(ELEVATOR_SHAFT_WIDTH, FLOOR_HEIGHT - 30));
    shaft.setBackground(SHAFT_COLOR);

    // Add borders for continuous shaft walls
    shaft.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(
                0, SHAFT_WALL_WIDTH, 0, SHAFT_WALL_WIDTH, SHAFT_BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)));

    // Placeholder for elevator car (will be added later)
    JLabel shaftLabel = new JLabel("", SwingConstants.CENTER);
    shaft.add(shaftLabel, BorderLayout.CENTER);

    return shaft;
  }

  private JPanel createRightPanel(int floorNumber) {
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setBackground(FLOOR_COLOR);
    rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    // Call button panel on the left side of right panel
    JPanel callButtonPanel = createCallButtonPanel(floorNumber);
    rightPanel.add(callButtonPanel, BorderLayout.WEST);

    // Passenger area on the right side
    JPanel passengerArea = createPassengerArea(floorNumber);
    passengerAreas.add(passengerArea);
    rightPanel.add(passengerArea, BorderLayout.CENTER);

    return rightPanel;
  }

  private JPanel createCallButtonPanel(int floorNumber) {
    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.setBackground(FLOOR_COLOR);
    buttonPanel.setPreferredSize(new Dimension(80, FLOOR_HEIGHT - 30));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

    // Single call button for every floor
    JButton callButton = createCallButton(floorNumber);
    callButtons.set(floorNumber - 1, callButton);

    // Center the button vertically in the panel
    JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
    centerWrapper.setBackground(FLOOR_COLOR);
    centerWrapper.add(callButton);

    buttonPanel.add(centerWrapper, BorderLayout.SOUTH);

    return buttonPanel;
  }

  private JButton createCallButton(int floorNumber) {
    JButton button = new JButton();
    button.setPreferredSize(new Dimension(CALL_BUTTON_WIDTH, CALL_BUTTON_HEIGHT));
    button.setMinimumSize(new Dimension(CALL_BUTTON_WIDTH, CALL_BUTTON_HEIGHT));
    button.setMaximumSize(new Dimension(CALL_BUTTON_WIDTH, CALL_BUTTON_HEIGHT));
    button.setBackground(CALL_BUTTON_COLOR);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createRaisedBevelBorder());
    button.setOpaque(true);

    // Store floor info for later use by controller
    button.putClientProperty("floor", floorNumber);

    return button;
  }

  private JPanel createPassengerArea(int floorNumber) {
    JPanel passengerArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
    passengerArea.setBackground(PASSENGER_AREA_COLOR);
    passengerArea.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Passengers"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    passengerArea.setPreferredSize(new Dimension(250, FLOOR_HEIGHT - 30));

    // Initially empty - passengers will be added by controller
    JLabel emptyLabel = new JLabel("No passengers", SwingConstants.CENTER);
    emptyLabel.setFont(new Font("Arial", Font.ITALIC, 11));
    emptyLabel.setForeground(Color.GRAY);
    passengerArea.add(emptyLabel);

    return passengerArea;
  }

  public void updateElevatorPosition(int currentFloor) {
    // Remove elevator from all shafts first
    for (JPanel shaft : elevatorShafts) {
      shaft.removeAll();
    }

    // Add elevator to current floor shaft (floors are 1-indexed, list is 0-indexed)
    if (currentFloor >= 1 && currentFloor <= numFloors) {
      JPanel currentShaft = elevatorShafts.get(numFloors - currentFloor); // Reverse order
      currentShaft.setLayout(new BorderLayout());
      currentShaft.add(elevatorCar, BorderLayout.CENTER);
    }

    // Refresh all shafts
    for (JPanel shaft : elevatorShafts) {
      shaft.revalidate();
      shaft.repaint();
    }
  }

  public ElevatorCarView getElevatorCar() {
    return elevatorCar;
  }

  private JPanel createSeparator() {
    JPanel separator = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(SEPARATOR_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    };

    separator.setPreferredSize(new Dimension(0, SEPARATOR_HEIGHT));
    separator.setMinimumSize(new Dimension(0, SEPARATOR_HEIGHT));
    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, SEPARATOR_HEIGHT));
    separator.setOpaque(true);

    return separator;
  }

  private void setupScrollPane() {
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
  }

  private void scrollToBottom() {
    SwingUtilities.invokeLater(
        () -> {
          JScrollBar vertical = scrollPane.getVerticalScrollBar();
          vertical.setValue(vertical.getMaximum());
        });
  }

  // Getter methods for controller access
  public List<JPanel> getFloorPanels() {
    return floorPanels;
  }

  public List<JPanel> getElevatorShafts() {
    return elevatorShafts;
  }

  public List<JButton> getCallButtons() {
    return callButtons;
  }

  public List<JPanel> getPassengerAreas() {
    return passengerAreas;
  }

  public JButton getCallButton(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      return callButtons.get(floorNumber - 1);
    }
    return null;
  }

  public JPanel getPassengerArea(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      return passengerAreas.get(floorNumber - 1);
    }
    return null;
  }

  public void scrollToFloor(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      JPanel targetFloor = floorPanels.get(floorNumber - 1);
      if (targetFloor != null) {
        targetFloor.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
      }
    }
  }

  // Method to add passenger visual representation
  public void addPassengerToFloor(int floorNumber, String passengerInfo) {
    JPanel passengerArea = getPassengerArea(floorNumber);
    if (passengerArea != null) {
      // Remove "No passengers" label if it exists
      Component[] components = passengerArea.getComponents();
      for (Component comp : components) {
        if (comp instanceof JLabel && ((JLabel) comp).getText().equals("No passengers")) {
          passengerArea.remove(comp);
          break;
        }
      }

      // Add passenger representation
      JLabel passengerLabel = new JLabel("👤");
      passengerLabel.setToolTipText(passengerInfo);
      passengerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
      passengerArea.add(passengerLabel);

      passengerArea.revalidate();
      passengerArea.repaint();
    }
  }

  // Method to remove passenger from floor
  public void removePassengerFromFloor(int floorNumber, int passengerIndex) {
    JPanel passengerArea = getPassengerArea(floorNumber);
    if (passengerArea != null && passengerIndex < passengerArea.getComponentCount()) {
      passengerArea.remove(passengerIndex);

      // Add "No passengers" label back if area is empty
      if (passengerArea.getComponentCount() == 0) {
        JLabel emptyLabel = new JLabel("No passengers", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        emptyLabel.setForeground(Color.GRAY);
        passengerArea.add(emptyLabel);
      }

      passengerArea.revalidate();
      passengerArea.repaint();
    }
  }
}
