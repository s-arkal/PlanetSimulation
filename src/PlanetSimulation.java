package src;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class PlanetSimulation extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final Color WHITE = Color.WHITE;
    private static final Color YELLOW = Color.YELLOW;
    private static final Color BLUE = new Color(100, 149, 237);
    private static final Color RED = new Color(188, 39, 50);
    private static final Color DARK_GREY = new Color(80, 78, 81);
    private static final Font FONT = new Font("Consolas", Font.PLAIN, 16);

    private static final double AU = 149.6e6 * 1000;
    private static final double G = 6.67428e-11;
    private static final double SCALE = 250 / AU;
    private static final double TIMESTEP = 3600 * 24;

    private ArrayList<Planet> planets;

    public PlanetSimulation() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        planets = new ArrayList<>();
        Planet sun = new Planet(0, 0, 30, YELLOW, 1.98892e30, true, "Sun");
        Planet earth = new Planet(-1 * AU, 0, 16, BLUE, 5.9742e24, false, "Earth");
        earth.yVel = 29.783 * 1000;
        Planet mars = new Planet(-1.524 * AU, 0, 12, RED, 6.39e23, false, "Mars");
        mars.yVel = 24.077 * 1000;
        Planet mercury = new Planet(0.387 * AU, 0, 8, DARK_GREY, 3.30e23, false, "Mercury");
        mercury.yVel = -47.4 * 1000;
        Planet venus = new Planet(0.723 * AU, 0, 14, WHITE, 4.8685e24, false, "Venus");
        venus.yVel = -35.02 * 1000;

        planets.add(sun);
        planets.add(earth);
        planets.add(mars);
        planets.add(mercury);
        planets.add(venus);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (Planet planet : planets) {
            planet.draw(g2d);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlanetSimulation simulation = new PlanetSimulation();
            JFrame frame = new JFrame("Planet Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(simulation);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Thread simulationThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep((long) (TIMESTEP / 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (Planet planet : simulation.planets) {
                        planet.updatePosition(simulation.planets);
                    }
                    frame.repaint();
                }
            });
            simulationThread.start();
        });
    }

    private static class Planet {
        double x, y, radius, mass;
        Color color;
        boolean isSun;
        double distanceToSun;
        double xVel, yVel;
        ArrayList<Point> orbit;
        String name;

        Planet(double x, double y, double radius, Color color, double mass, boolean isSun, String name) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.mass = mass;
            this.isSun = isSun;
            this.name = name;
            this.orbit = new ArrayList<>();
        }

        void draw(Graphics2D g2d) {
            int x = (int) (this.x * SCALE + WIDTH / 2);
            int y = (int) (this.y * SCALE + HEIGHT / 2);

            if (orbit.size() > 2) {
                int[] xPoints = new int[orbit.size()];
                int[] yPoints = new int[orbit.size()];
                for (int i = 0; i < orbit.size(); i++) {
                    Point point = orbit.get(i);
                    xPoints[i] = (int) (point.x * SCALE + WIDTH / 2);
                    yPoints[i] = (int) (point.y * SCALE + HEIGHT / 2);
                }
                g2d.setColor(color);
                g2d.drawPolyline(xPoints, yPoints, orbit.size());
            }

            g2d.setColor(color);
            g2d.fillOval(x - (int) radius, y - (int) radius, (int) radius * 2, (int) radius * 2);

            if (!isSun) {
                String distanceText = String.format("%.1fkm", distanceToSun / 1000);
                g2d.setColor(WHITE);
                g2d.setFont(FONT);
                FontMetrics metrics = g2d.getFontMetrics(FONT);
                int textWidth = metrics.stringWidth(distanceText);
                int textHeight = metrics.getHeight();
                g2d.drawString(distanceText, x - textWidth / 2, y - (int) radius - textHeight / 2 - 5);

                int nameWidth = metrics.stringWidth(name);
                g2d.drawString(name, x - nameWidth / 2, y - (int) radius - textHeight / 2 - 25);
            }
        }

        void attraction(Planet other) {
            double otherX = other.x;
            double otherY = other.y;
            double distanceX = otherX - x;
            double distanceY = otherY - y;
            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            if (other.isSun) {
                distanceToSun = distance;
            }

            double force = G * mass * other.mass / (distance * distance);
            double theta = Math.atan2(distanceY, distanceX);
            double forceX = Math.cos(theta) * force;
            double forceY = Math.sin(theta) * force;

            xVel += forceX / mass * TIMESTEP;
            yVel += forceY / mass * TIMESTEP;
        }

        void updatePosition(ArrayList<Planet> planets) {
            double totalFX = 0;
            double totalFY = 0;

            for (Planet planet : planets) {
                if (planet != this) {
                    planet.attraction(this);
                    totalFX += planet.xVel;
                    totalFY += planet.yVel;
                }
            }

            x += xVel * TIMESTEP;
            y += yVel * TIMESTEP;
            orbit.add(new Point((int) x, (int) y));
        }
    }
}
