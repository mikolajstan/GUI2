package pl.liftsim.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
  private JButton startButton;

  public ControlPanel() {
    setupLayout();
    createComponents();
    addComponents();
  }

  private void setupLayout() {
    setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    setBackground(Color.LIGHT_GRAY);
    setBorder(BorderFactory.createTitledBorder("Control Panel"));
  }

  private void createComponents() {
    startButton = new JButton("START");

    startButton.setPreferredSize(new Dimension(100, 50));
    startButton.setFont(new Font("Arial", Font.BOLD, 14));
    startButton.setBackground(Color.GREEN);
    startButton.setForeground(Color.BLACK);
    startButton.setFocusPainted(false);
  }

  private void addComponents() {
    add(startButton);
  }

  public JButton getStartButton() {
    return startButton;
  }

  public void setStartButton(boolean enabled) {
    startButton.setEnabled(enabled);

    if (enabled) {
      startButton.setBackground(Color.GREEN);
      startButton.setText("START");
    } else {
      startButton.setBackground(Color.GRAY);
      startButton.setText("RUNNING...");
    }
  }
}
