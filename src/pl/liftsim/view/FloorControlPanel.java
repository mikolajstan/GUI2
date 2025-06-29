package pl.liftsim.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import pl.liftsim.model.ElevatorModel;

public class FloorControlPanel extends JPanel {
  private final List<JButton> floorButtons;
  private final int numFloors;
  private static final int BUTTON_SIZE = 30;
  private static final int BUTTON_SPACING = 10;

  public FloorControlPanel(ElevatorModel elevatorModel) {
    this.numFloors = elevatorModel.getFloors().size();
    this.floorButtons = new ArrayList<>();
    setupLayout();
    createComponents();
    addComponents();

    setPreferredSize(calculatePanelSize());
    setMinimumSize(calculatePanelSize());
    setMaximumSize(calculatePanelSize());
  }

  private void setupLayout() {
    setLayout(null);
    setBackground(Color.LIGHT_GRAY);
    setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
  }

  private Dimension calculatePanelSize() {
    int rows = (int) Math.ceil((double) numFloors / 3);
    int cols = Math.min(3, numFloors);

    int width = cols * BUTTON_SIZE + (cols - 1) * BUTTON_SPACING + 20 + 4; // 20 for padding, 4 for border
    int height = rows * BUTTON_SIZE + (rows - 1) * BUTTON_SPACING + 20 + 4; // 20 for padding, 4 for border

    return new Dimension(width, height);
  }

  private void createComponents() {
    for (int i = 0; i < numFloors; i++) {
      JButton floorButton = createRoundButton(String.valueOf(i + 1));
      floorButtons.add(floorButton);
    }
  }

  private JButton createRoundButton(String text) {
    JButton button = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
          g2d.setColor(Color.LIGHT_GRAY);
        } else {
          g2d.setColor(Color.WHITE);
        }
        g2d.fillOval(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(1, 1, getWidth() - 2, getHeight() - 2);

        g2d.setColor(Color.BLACK);
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - 2; // Adjust for vertical centering

        g2d.drawString(text, x, y);
        g2d.dispose();
      }

      @Override
      protected void paintBorder(Graphics g) {
        // No border painting needed, handled in paintComponent
      }

      @Override
      public boolean contains(int x, int y) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2;
        int distance = (int) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        return distance <= radius;
      }
    };

    button.setSize(BUTTON_SIZE, BUTTON_SIZE);
    button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
    button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
    button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));

    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setFocusPainted(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);

    return button;
  }

  private void addComponents() {
    int rows = (int) Math.ceil((double) numFloors / 3);

    for (int floorNum = 1; floorNum <= numFloors; floorNum++) {
      int positionFromBottom = floorNum - 1;
      int row = positionFromBottom / 3;
      int col = positionFromBottom % 3;

      // Calculate actual pixel position
      int x = 10 + col * (BUTTON_SIZE + BUTTON_SPACING); // 10 = border padding
      int y = 10 + (rows - 1 - row) * (BUTTON_SIZE + BUTTON_SPACING); // 10 = border padding, flip row

      JButton button = floorButtons.get(floorNum - 1);
      button.setBounds(x, y, BUTTON_SIZE, BUTTON_SIZE);
      add(button);
    }
  }

  public List<JButton> getFloorButtons() {
    return floorButtons;
  }

  public JButton getFloorButton(int floorIndex) {
    if (floorIndex >= 0 && floorIndex < floorButtons.size()) {
      return floorButtons.get(floorIndex);
    }
    return null;
  }
}
