package pl.liftsim.view;

import pl.liftsim.model.Passenger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Visual representation of the elevator car that moves within the shaft */
public class ElevatorCarView extends JPanel {
  private static final int CAR_WIDTH = 50;
  private static final int CAR_HEIGHT = 40;
  private static final Color CAR_COLOR = new Color(100, 150, 200);
  private static final Color CAR_BORDER_COLOR = Color.DARK_GRAY;

  private List<Passenger> passengers;
  private boolean doorOpen = false;

  public ElevatorCarView() {
    setPreferredSize(new Dimension(CAR_WIDTH, CAR_HEIGHT));
    setBackground(CAR_COLOR);
    setBorder(BorderFactory.createLineBorder(CAR_BORDER_COLOR, 2));
    setOpaque(true);
  }

  public void setPassengers(List<Passenger> passengers) {
    this.passengers = passengers;
    repaint();
  }

  public void setDoorOpen(boolean doorOpen) {
    this.doorOpen = doorOpen;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw elevator car
    g2d.setColor(CAR_COLOR);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // Draw door indicator
    if (doorOpen) {
      g2d.setColor(Color.GREEN);
      g2d.fillRect(2, getHeight() - 6, getWidth() - 4, 4);
    }

    // Draw passenger count
    if (passengers != null && !passengers.isEmpty()) {
      g2d.setColor(Color.WHITE);
      g2d.setFont(new Font("Arial", Font.BOLD, 10));
      String passengerText = passengers.size() + "👤";
      FontMetrics fm = g2d.getFontMetrics();
      int x = (getWidth() - fm.stringWidth(passengerText)) / 2;
      int y = getHeight() / 2 + 3;
      g2d.drawString(passengerText, x, y);
    }

    g2d.dispose();
  }
}
