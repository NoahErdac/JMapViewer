import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

import java.awt.*;
import java.util.ArrayList;

public class CustomJMapViewer extends JMapViewer {
    private ArrayList<ICoordinate> trail;
    private static final int TRAIL_WIDTH = 2;
    private static final Color TRAIL_COLOR = Color.RED;

    public CustomJMapViewer() {
        super();
        trail = new ArrayList<>();
    }

    public void setTrail(ArrayList<ICoordinate> trail) {
        this.trail = trail;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(TRAIL_COLOR);
        g2d.setStroke(new BasicStroke(TRAIL_WIDTH));

        for (int i = 1; i < trail.size(); i++) {
            Point p1 = getMapPosition(trail.get(i - 1), false);
            Point p2 = getMapPosition(trail.get(i), false);

            if (p1 != null && p2 != null) {
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
}
