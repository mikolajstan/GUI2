package pl.liftsim.view;

import pl.liftsim.model.ElevatorModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FloorViewPanel extends JPanel {
  private final int numFloors;
  private final List<JPanel> floorPanels;
  private final List<JPanel> elevatorShafts;
  private final List<JButton> callButtons;
  private final List<JPanel> passengerAreas;
  private JScrollPane scrollPane;
  private JPanel floorsContainer;
  private ElevatorCarView elevatorCar;

  private static final int FLOOR_HEIGHT = 80;
  private static final int SEPARATOR_HEIGHT = 3;
  private static final int ELEVATOR_SHAFT_WIDTH = 60;
  private static final int SHAFT_WALL_WIDTH = 2;
  private static final Color FLOOR_COLOR = new Color(245, 245, 245);
  private static final Color SEPARATOR_COLOR = Color.LIGHT_GRAY;
  private static final Color SHAFT_COLOR = new Color(220, 220, 220);
  private static final Color SHAFT_BORDER_COLOR = Color.DARK_GRAY;

  public FloorViewPanel(ElevatorModel elevatorModel) {
    this.numFloors = elevatorModel.getFloors().size();
    this.floorPanels = new ArrayList<>();
    this.elevatorShafts = new ArrayList<>();
    this.callButtons = new ArrayList<>();
    this.passengerAreas = new ArrayList<>();
    this.elevatorCar = new ElevatorCarView(); // Add this line

    setupLayout();
    createFloorPanels();
    setupScrollPane();
    scrollToBottom();
  }

  private void setupLayout() {
    setLayout(new BorderLayout());
    setBackground(Color.LIGHT_GRAY);
    setBorder(BorderFactory.createTitledBorder("Building View"));
  }

  private void createFloorPanels() {
    floorsContainer = new JPanel();
    floorsContainer.setLayout(new BoxLayout(floorsContainer, BoxLayout.Y_AXIS));
    floorsContainer.setBackground(Color.WHITE);

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

  private void paintContinuousShaftWalls(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setColor(SHAFT_BORDER_COLOR);
    g2d.setStroke(new BasicStroke(SHAFT_WALL_WIDTH));

    // Calculate shaft position based on the first floor's shaft
    if (!floorPanels.isEmpty() && !elevatorShafts.isEmpty()) {
      // Get the first floor panel to calculate shaft position
      JPanel firstFloor = floorPanels.getFirst();
      JPanel firstShaft = elevatorShafts.getFirst();

      // Calculate the x-position of shaft walls relative to the container
      Point shaftLocation = calculateShaftPosition(firstFloor, firstShaft);

      if (shaftLocation != null) {
        int leftWallX = shaftLocation.x;
        int rightWallX = shaftLocation.x + ELEVATOR_SHAFT_WIDTH - SHAFT_WALL_WIDTH;

        // Draw continuous vertical lines from top to bottom
        g2d.drawLine(leftWallX, 0, leftWallX, getHeight());
        g2d.drawLine(rightWallX, 0, rightWallX, getHeight());
      }
    }

    g2d.dispose();
  }

  private Point calculateShaftPosition(JPanel floorPanel, JPanel shaftPanel) {
    try {
      // Calculate relative position of shaft within the container
      int floorX = 0;
      int floorY = 0;

      // Find floor position in container
      for (Component comp : floorsContainer.getComponents()) {
        if (comp == floorPanel) {
          break;
        }
        if (comp instanceof JPanel) {
          floorY += comp.getHeight();
        }
      }

      // Add floor's internal positioning
      floorX += 20; // Floor left padding
      floorX += 30 + 15; // Label wrapper width + spacing
      floorX += 15; // Content area left padding
      floorX += SHAFT_WALL_WIDTH; // Account for shaft wall width

      return new Point(floorX, floorY);
    } catch (Exception e) {
      return null;
    }
  }

  private JPanel createSingleFloor(int floorNumber) {
    JPanel floor = new JPanel(new BorderLayout());
    floor.setPreferredSize(new Dimension(0, FLOOR_HEIGHT));
    floor.setMinimumSize(new Dimension(200, FLOOR_HEIGHT));
    floor.setBackground(FLOOR_COLOR);
    floor.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    JLabel floorLabel = new JLabel(String.valueOf(floorNumber), SwingConstants.CENTER);
    floorLabel.setFont(new Font("Arial", Font.BOLD, 10));
    floorLabel.setPreferredSize(new Dimension(30, 20));
    floorLabel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)));
    floorLabel.setOpaque(true);
    floorLabel.setBackground(Color.WHITE);

    JPanel labelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    labelWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    labelWrapper.setBackground(FLOOR_COLOR);
    labelWrapper.add(floorLabel);

    floor.add(labelWrapper, BorderLayout.WEST);

    JPanel contentArea = createFloorContentArea(floorNumber);
    contentArea.setBackground(FLOOR_COLOR);
    contentArea.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 1, 5, 5, false));

    floor.add(contentArea, BorderLayout.CENTER);

    return floor;
  }

  private JPanel createFloorContentArea(int floorNumber) {
    JPanel contentArea = new JPanel(new BorderLayout());
    contentArea.setBackground(FLOOR_COLOR);
    contentArea.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

    // Create elevator shaft area on the left side of content
    JPanel elevatorShaft = createElevatorShaft(floorNumber);
    elevatorShafts.add(elevatorShaft); // Keep track of shafts
    contentArea.add(elevatorShaft, BorderLayout.WEST);

    // Create area for call buttons and passengers (future implementation)
    JPanel rightArea = createRightArea(floorNumber);
    contentArea.add(rightArea, BorderLayout.CENTER);

    return contentArea;
  }

  private JPanel createElevatorShaft(int floorNumber) {
    JPanel shaft = new JPanel(new BorderLayout());
    shaft.setPreferredSize(new Dimension(ELEVATOR_SHAFT_WIDTH, FLOOR_HEIGHT - 20));
    shaft.setMinimumSize(new Dimension(ELEVATOR_SHAFT_WIDTH, FLOOR_HEIGHT - 20));
    shaft.setMaximumSize(new Dimension(ELEVATOR_SHAFT_WIDTH, FLOOR_HEIGHT - 20));
    shaft.setBackground(SHAFT_COLOR);

    shaft.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 2, SHAFT_BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    JLabel shaftLabel = new JLabel("", SwingConstants.CENTER);
    shaftLabel.setFont(new Font("Arial", Font.PLAIN, 8));
    shaftLabel.setForeground(Color.GRAY);
    shaft.add(shaftLabel, BorderLayout.CENTER);

    return shaft;
  }

  private JPanel createRightArea(int floorNumber) {
    JPanel rightArea = new JPanel(new BorderLayout());
    rightArea.setBackground(FLOOR_COLOR);
    rightArea.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 1, 5, 5, false),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

    JLabel placeholderLabel =
        new JLabel("Call buttons & passengers area - Floor " + floorNumber, SwingConstants.CENTER);
    placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 9));
    placeholderLabel.setForeground(Color.GRAY);
    rightArea.add(placeholderLabel, BorderLayout.CENTER);

    return rightArea;
  }

  private JPanel createSeparator() {
    JPanel separator =
        new JPanel() {
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

  public List<JPanel> getFloorPanels() {
    return floorPanels;
  }

  public JPanel getFloorPanel(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      return floorPanels.get(floorNumber - 1); // Convert to 0-based index
    }
    return null;
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

  public List<JButton> getCallButtons() {
    return callButtons;
  }

  public JButton getCallButton(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      return callButtons.get(floorNumber - 1);
    }
    return null;
  }

  public List<JPanel> getElevatorShafts() {
    return elevatorShafts;
  }

  public JPanel getElevatorShaft(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      return elevatorShafts.get(floorNumber - 1);
    }
    return null;
  }

  public void scrollToFloor(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      JPanel targetFloor = getFloorPanel(floorNumber);
      if (targetFloor != null) {
        targetFloor.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
      }
    }
  }

  public JPanel getPassengerArea(int floorNumber) {
    if (floorNumber >= 1 && floorNumber <= numFloors) {
      return passengerAreas.get(floorNumber - 1);
    }
    return null;
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
