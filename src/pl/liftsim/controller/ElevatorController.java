package pl.liftsim.controller;

import pl.liftsim.model.Passenger;
import pl.liftsim.view.ElevatorSimulatorView;
import pl.liftsim.model.ElevatorModel;
import pl.liftsim.model.Floor;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ElevatorController {
  private final ElevatorModel model;
  private final ElevatorSimulatorView view;
  private Timer simulationTimer;
  private boolean simulationRunning = false;
  // Elevator state
  private ElevatorDirection currentDirection = ElevatorDirection.UP;
  private Set<Integer> callRequests = new HashSet<>(); // Call button requests
  private Set<Integer> destinationRequests = new HashSet<>(); // Floor panel destination requests
  private boolean isMoving = false;
  private boolean doorsOpen = false;

  // Timing constants (in milliseconds)
  private static final int SIMULATION_TICK_INTERVAL = 3000; // Check for new actions every 3 seconds
  private static final int FLOOR_TO_FLOOR_TIME = 3000; // 3 seconds to travel ONE floor
  private static final int DOOR_OPERATION_TIME = 1500; // 1.5 seconds for each door operation step
  private static final int PASSENGER_EXIT_TIME = 2000; // 2 seconds for passengers to exit
  private static final int PASSENGER_ENTRY_TIME = 2000; // 2 seconds for passengers to enter

  private enum ElevatorDirection {
    UP,
    DOWN,
    IDLE
  }

  public ElevatorController(ElevatorModel model, ElevatorSimulatorView view) {
    this.model = model;
    this.view = view;
    setupEventHandlers();
    setupSimulationTimer();
  }

  private void setupEventHandlers() {
    // Start/Stop button
    view.getControlPanel()
        .getStartButton()
        .addActionListener(
            e -> {
              if (!simulationRunning) {
                startSimulation();
              } else {
                stopSimulation();
              }
            });

    // Reset button
    // view.getControlPanel().getResetButton().addActionListener(e ->
    // resetSimulation());

    // Floor control panel buttons - these add destination requests ONLY
    for (int floor = 1; floor <= model.getFloors().size(); floor++) {
      JButton floorButton = view.getFloorControlPanel().getFloorButton(floor);
      if (floorButton != null) {
        final int targetFloor = floor;
        floorButton.addActionListener(
            e -> {
              addDestinationRequest(targetFloor);
              System.out.println(
                  "🎯 Floor " + targetFloor + " button pressed - Added to destinations");
            });
      }
    }

    // Call buttons from floors - these add pickup requests ONLY
    for (int floor = 1; floor <= model.getFloors().size(); floor++) {
      JButton callButton = view.getFloorViewPanel().getCallButton(floor);
      if (callButton != null) {
        final int fromFloor = floor;
        callButton.addActionListener(
            e -> {
              addCallRequest(fromFloor);
              System.out.println(
                  "📞 Call button pressed on floor " + fromFloor + " - Elevator called");
            });
      }
    }
  }

  private void setupSimulationTimer() {
    simulationTimer = new Timer(
        SIMULATION_TICK_INTERVAL,
        e -> {
          if (simulationRunning && !isMoving && !doorsOpen) {
            processElevatorMovement();
          }
        });
  }

  private void startSimulation() {
    simulationRunning = true;
    model.setSimulationRunning(true);
    view.getControlPanel().setStartButton(false);
    simulationTimer.start();

    model.generateRandomPassengers();

    for (int floor = 1; floor <= model.getFloors().size(); floor++) {
      updateFloorPassengerDisplay(floor);
    }

    System.out.println("🟢 Simulation started - Elevator is IDLE until called");
    System.out.println("📋 Use call buttons (black buttons) to call elevator to floors");
    System.out.println("📋 Use floor panel buttons (left side) to select destinations");
  }

  private void stopSimulation() {
    simulationRunning = false;
    model.setSimulationRunning(false);
    view.getControlPanel().setStartButton(true);
    simulationTimer.stop();
    isMoving = false;
    doorsOpen = false;

    System.out.println("🔴 Simulation stopped");
  }

  private void resetSimulation() {
    stopSimulation();
    model.reset();
    callRequests.clear();
    destinationRequests.clear();
    currentDirection = ElevatorDirection.UP;
    isMoving = false;
    doorsOpen = false;

    // Clear all passenger areas
    for (int floor = 1; floor <= model.getFloors().size(); floor++) {
      clearPassengersFromFloor(floor);
    }

    // Reset elevator position to floor 1
    model.getElevator().setCurrentFloor(1);
    view.getFloorViewPanel().updateElevatorPosition(1);
    view.getFloorViewPanel().getElevatorCar().setPassengers(model.getElevator().getPassengers());
    view.getFloorViewPanel().getElevatorCar().setDoorOpen(false);

    System.out.println("🔄 Simulation reset - Elevator at floor 1, all requests cleared");
  }

  private void addDestinationRequest(int floor) {
    destinationRequests.add(floor);
    System.out.println("📍 Destination request added: Floor " + floor);
    printRequestStatus();
  }

  private void addCallRequest(int floor) {
    callRequests.add(floor);
    System.out.println("📞 Call request added: Floor " + floor);
    printRequestStatus();
  }

  private void printRequestStatus() {
    System.out.println(
        "📋 Current requests - Calls: " + callRequests + ", Destinations: " + destinationRequests);
  }

  private void processElevatorMovement() {
    // Check if there are ANY requests at all
    if (callRequests.isEmpty() && destinationRequests.isEmpty()) {
      if (currentDirection != ElevatorDirection.IDLE) {
        currentDirection = ElevatorDirection.IDLE;
        System.out.println(
            "💤 Elevator is IDLE - No call buttons pressed, no destinations selected");
      }
      return;
    }

    // Combine all requests for movement logic
    Set<Integer> allRequests = new HashSet<>();
    allRequests.addAll(callRequests);
    allRequests.addAll(destinationRequests);

    int currentFloor = model.getElevator().getCurrentFloor();

    // If elevator was idle and now has requests, determine initial direction
    if (currentDirection == ElevatorDirection.IDLE) {
      determineInitialDirection(allRequests, currentFloor);
    }

    // Determine if we should stop at current floor
    if (allRequests.contains(currentFloor)) {
      stopAtCurrentFloor();
      return;
    }

    // Determine next floor to move to
    int nextFloor = getNextFloor(allRequests);
    if (nextFloor != -1) {
      moveToFloorStepByStep(nextFloor);
    } else {
      // No more requests in current direction, switch direction
      switchDirection();
      nextFloor = getNextFloor(allRequests);
      if (nextFloor != -1) {
        moveToFloorStepByStep(nextFloor);
      } else {
        // No more requests anywhere
        currentDirection = ElevatorDirection.IDLE;
        System.out.println("💤 All requests completed - Elevator is IDLE");
      }
    }
  }

  private void determineInitialDirection(Set<Integer> allRequests, int currentFloor) {
    // Find if there are requests above or below current floor
    boolean hasRequestsAbove = allRequests.stream().anyMatch(floor -> floor > currentFloor);
    boolean hasRequestsBelow = allRequests.stream().anyMatch(floor -> floor < currentFloor);

    if (hasRequestsAbove && !hasRequestsBelow) {
      currentDirection = ElevatorDirection.UP;
      System.out.println("⬆️ Starting to move UP (requests above current floor)");
    } else if (hasRequestsBelow && !hasRequestsAbove) {
      currentDirection = ElevatorDirection.DOWN;
      System.out.println("⬇️ Starting to move DOWN (requests below current floor)");
    } else if (hasRequestsAbove && hasRequestsBelow) {
      // Requests both above and below - choose closest
      int closestAbove = allRequests.stream()
          .filter(f -> f > currentFloor)
          .min(Integer::compareTo)
          .orElse(Integer.MAX_VALUE);
      int closestBelow = allRequests.stream()
          .filter(f -> f < currentFloor)
          .max(Integer::compareTo)
          .orElse(Integer.MIN_VALUE);

      if (Math.abs(closestAbove - currentFloor) <= Math.abs(currentFloor - closestBelow)) {
        currentDirection = ElevatorDirection.UP;
        System.out.println("⬆️ Starting to move UP (closest request is above)");
      } else {
        currentDirection = ElevatorDirection.DOWN;
        System.out.println("⬇️ Starting to move DOWN (closest request is below)");
      }
    }
  }

  private int getNextFloor(Set<Integer> allRequests) {
    int currentFloor = model.getElevator().getCurrentFloor();

    if (currentDirection == ElevatorDirection.UP) {
      // Find lowest floor above current floor
      return allRequests.stream()
          .filter(floor -> floor > currentFloor)
          .min(Integer::compareTo)
          .orElse(-1);
    } else if (currentDirection == ElevatorDirection.DOWN) {
      // Find highest floor below current floor
      return allRequests.stream()
          .filter(floor -> floor < currentFloor)
          .max(Integer::compareTo)
          .orElse(-1);
    }

    return -1;
  }

  private void switchDirection() {
    if (currentDirection == ElevatorDirection.UP) {
      currentDirection = ElevatorDirection.DOWN;
      System.out.println("🔄 Switched direction to DOWN");
    } else if (currentDirection == ElevatorDirection.DOWN) {
      currentDirection = ElevatorDirection.UP;
      System.out.println("🔄 Switched direction to UP");
    }
  }

  /** Move elevator step-by-step through each floor to reach target */
  private void moveToFloorStepByStep(int targetFloor) {
    isMoving = true;
    int currentFloor = model.getElevator().getCurrentFloor();

    if (currentFloor == targetFloor) {
      isMoving = false;
      return;
    }

    System.out.println(
        "🚀 Moving from floor "
            + currentFloor
            + " to floor "
            + targetFloor
            + " ("
            + Math.abs(targetFloor - currentFloor)
            + " floors, "
            + (Math.abs(targetFloor - currentFloor) * FLOOR_TO_FLOOR_TIME / 1000.0)
            + "s total)");

    // Determine direction of movement
    int direction = (targetFloor > currentFloor) ? 1 : -1;

    // Start the step-by-step movement
    moveOneFloorStep(currentFloor, targetFloor, direction);
  }

  /** Move one floor at a time with visual updates */
  private void moveOneFloorStep(int currentFloor, int targetFloor, int direction) {
    int nextFloor = currentFloor + direction;

    System.out.println(
        "  🏢 Moving from floor " + currentFloor + " to floor " + nextFloor + " (3 seconds)");

    Timer stepTimer = new Timer(
        FLOOR_TO_FLOOR_TIME,
        e -> {
          // Update elevator position
          model.getElevator().setCurrentFloor(nextFloor);

          SwingUtilities.invokeLater(
              () -> {
                view.getFloorViewPanel().updateElevatorPosition(nextFloor);
              });

          System.out.println("  ✅ Passed floor " + nextFloor);

          // Check if we've reached the target floor
          if (nextFloor == targetFloor) {
            // Reached destination
            isMoving = false;
            System.out.println("🎯 Arrived at target floor " + targetFloor);
            ((Timer) e.getSource()).stop();
          } else {
            // Continue to next floor
            ((Timer) e.getSource()).stop();
            moveOneFloorStep(nextFloor, targetFloor, direction);
          }
        });
    stepTimer.setRepeats(false);
    stepTimer.start();
  }

  private void stopAtCurrentFloor() {
    int currentFloor = model.getElevator().getCurrentFloor();

    // Remove this floor from both request types
    boolean wasCallRequest = callRequests.remove(currentFloor);
    boolean wasDestinationRequest = destinationRequests.remove(currentFloor);

    doorsOpen = true;

    String requestType = "";
    if (wasCallRequest && wasDestinationRequest) {
      requestType = " (Call + Destination)";
    } else if (wasCallRequest) {
      requestType = " (Call Request)";
    } else if (wasDestinationRequest) {
      requestType = " (Destination Request)";
    }

    System.out.println("🛑 Stopping at floor " + currentFloor + requestType);

    SwingUtilities.invokeLater(
        () -> {
          // Step 1: Open doors
          view.getFloorViewPanel().getElevatorCar().setDoorOpen(true);
          System.out.println("🚪 Opening doors...");

          Timer doorSequenceTimer = new Timer(
              DOOR_OPERATION_TIME,
              new ActionListener() {
                private int step = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                  if (step == 0) {
                    // Step 2: Passengers exit (after door opening delay)
                    System.out.println(
                        "👥 Passengers exiting... (takes "
                            + (PASSENGER_EXIT_TIME / 1000.0)
                            + "s)");
                    processPassengerExit(currentFloor);
                    ((Timer) e.getSource()).setDelay(PASSENGER_EXIT_TIME);
                    step++;
                  } else if (step == 1) {
                    // Step 3: Passengers enter (after exit is complete)
                    System.out.println(
                        "👥 Passengers entering... (takes "
                            + (PASSENGER_ENTRY_TIME / 1000.0)
                            + "s)");
                    processPassengerEntry(currentFloor);
                    ((Timer) e.getSource()).setDelay(PASSENGER_ENTRY_TIME);
                    step++;
                  } else if (step == 2) {
                    // Step 4: Close doors (after entry is complete)
                    System.out.println("🚪 Closing doors...");
                    ((Timer) e.getSource()).setDelay(DOOR_OPERATION_TIME);
                    step++;
                  } else {
                    // Step 5: Doors closed, ready to move
                    view.getFloorViewPanel().getElevatorCar().setDoorOpen(false);
                    doorsOpen = false;
                    ((Timer) e.getSource()).stop();
                    System.out.println(
                        "✅ Doors closed at floor " + currentFloor + " - Ready to move");
                    printRequestStatus();
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                  }
                }
              });
          doorSequenceTimer.start();
        });
  }

  private void processPassengerExit(int currentFloor) {
    List<Passenger> exitingPassengers = new ArrayList<>();

    // Find passengers who want to exit at this floor
    for (Passenger passenger : model.getElevator().getPassengers()) {
      if (passenger.getDestinationFloor() == currentFloor) {
        exitingPassengers.add(passenger);
      }
    }

    // Remove exiting passengers from elevator
    for (Passenger passenger : exitingPassengers) {
      model.getElevator().getPassengers().remove(passenger);
      System.out.println(
          "  ↪️ Passenger "
              + passenger.getId()
              + " exited at floor "
              + currentFloor
              + " (JOURNEY COMPLETED ✨)");
    }

    // Update elevator display
    view.getFloorViewPanel().getElevatorCar().setPassengers(model.getElevator().getPassengers());

    if (exitingPassengers.isEmpty()) {
      System.out.println("  ↪️ No passengers to exit");
    } else {
      System.out.println(
          "  ✅ " + exitingPassengers.size() + " passenger(s) exited at floor " + currentFloor);
    }
  }

  private void processPassengerEntry(int currentFloor) {
    Floor floor = model.getFloors().get(currentFloor - 1);
    List<Passenger> enteringPassengers = new ArrayList<>();

    // Find passengers who want to enter and are going in the current direction
    for (Passenger passenger : floor.getWaitingPassengers()) {
      if (model.getElevator().getPassengers().size()
          + enteringPassengers.size() < ElevatorModel.MAX_ELEVATOR_CAPACITY) {
        enteringPassengers.add(passenger);
      } else {
        System.out.println("  ⚠️ Elevator full - Passenger " + passenger.getId() + " must wait");
        break; // Elevator is full
      }
    }

    // Move passengers from floor to elevator
    for (Passenger passenger : enteringPassengers) {
      floor.getWaitingPassengers().remove(passenger);
      model.getElevator().addPassenger(passenger);
      // Automatically add their destination when they enter
      addDestinationRequest(passenger.getDestinationFloor());
      System.out.println(
          "  ↩️ Passenger "
              + passenger.getId()
              + " entered elevator, going to floor "
              + passenger.getDestinationFloor());
    }

    // Update displays
    view.getFloorViewPanel().getElevatorCar().setPassengers(model.getElevator().getPassengers());
    updateFloorPassengerDisplay(currentFloor);

    if (enteringPassengers.isEmpty() && !floor.getWaitingPassengers().isEmpty()) {
      System.out.println("  ↩️ No passengers could enter (wrong direction or elevator full)");
    } else if (enteringPassengers.isEmpty()) {
      System.out.println("  ↩️ No passengers waiting to enter");
    } else {
      System.out.println(
          "  ✅ " + enteringPassengers.size() + " passenger(s) entered at floor " + currentFloor);
    }
  }

  private boolean canPassengerEnter(Passenger passenger, int currentFloor) {
    int destination = passenger.getDestinationFloor();

    if (currentDirection == ElevatorDirection.UP) {
      return destination > currentFloor;
    } else if (currentDirection == ElevatorDirection.DOWN) {
      return destination < currentFloor;
    }

    return true; // If idle, allow any passenger
  }

  private void updateFloorPassengerDisplay(int floorNumber) {
    clearPassengersFromFloor(floorNumber);

    Floor floor = model.getFloors().get(floorNumber - 1);
    for (Passenger passenger : floor.getWaitingPassengers()) {
      view.getFloorViewPanel()
          .addPassengerToFloor(
              floorNumber, "P" + passenger.getId() + "→" + passenger.getDestinationFloor());
    }

    SwingUtilities.invokeLater(
        () -> {
          view.getFloorViewPanel().revalidate();
          view.getFloorViewPanel().repaint();
        });
  }

  private void clearPassengersFromFloor(int floorNumber) {
    JPanel passengerArea = view.getFloorViewPanel().getPassengerArea(floorNumber);
    if (passengerArea != null) {
      // Clear the passenger area for the specified floor
      passengerArea.removeAll();

      Floor floor = model.getFloors().get(floorNumber - 1);
      if (floor.getWaitingPassengers().isEmpty()) {
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
