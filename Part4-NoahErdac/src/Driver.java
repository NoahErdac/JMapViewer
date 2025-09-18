import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.Layer;

import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Driver {
    private static TripPoint tripPoint;
    private static CustomJMapViewer mapViewer;
    private static JComboBox<Integer> animationTimeComboBox;
    private static JCheckBox includeStopsCheckBox;
    private static Timer timer;
    private static JButton playButton;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void createAndShowGUI() throws FileNotFoundException, IOException {
        // Read file and call stop detection
     
        	TripPoint.readFile("triplog.csv");
        
        TripPoint.h2StopDetection();

        // Set up frame, include your name in the title
        JFrame frame = new JFrame("Trip Animation - Noah");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        frame.setLayout(new BorderLayout());

        // Set up Panel for input selections
        JPanel controlPanel = new JPanel();

        // Play Button
        playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    animateTrip();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // CheckBox to enable/disable stops
        includeStopsCheckBox = new JCheckBox("Include Stops");
        includeStopsCheckBox.setSelected(false);

        // ComboBox to pick animation time
        Integer[] animationTimes = {15, 30, 60, 90};
        animationTimeComboBox = new JComboBox<>(animationTimes);
        animationTimeComboBox.setSelectedIndex(0);

        // Add all to top panel
        controlPanel.add(playButton);
        controlPanel.add(includeStopsCheckBox);
        controlPanel.add(animationTimeComboBox);

        // Set up mapViewer
        mapViewer = new CustomJMapViewer();
        mapViewer.setTileSource(new OsmTileSource.TransportMap());
        mapViewer.setZoom(14);

        // Set the map center and zoom level
        ArrayList<TripPoint> trip = TripPoint.getTrip();
        if (!trip.isEmpty()) {
            TripPoint tp = trip.get(0);
            Coordinate coord = new Coordinate(tp.getLat(), tp.getLon());
            if (coord != null) {
                mapViewer.setDisplayPosition(coord, mapViewer.getZoom());
            }
        }

        

        // Add components to frame
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(mapViewer, BorderLayout.CENTER);

        // Show the frame
        frame.setVisible(true);
    }

    private static void animateTrip() throws FileNotFoundException, IOException {
        ArrayList<TripPoint> trip = includeStopsCheckBox.isSelected() ? TripPoint.getTrip() : TripPoint.getMovingTrip();
        int animationTime = (int) animationTimeComboBox.getSelectedItem() * 1000;
        int delay = animationTime / trip.size();

        // Clear previous markers and polylines
        mapViewer.removeAllMapMarkers();

        Image raccoonImage = ImageIO.read(new File("raccoon.png"));

        // If a timer exists, stop it and remove the ActionListener
        if (timer != null) {
            timer.stop();
            for (ActionListener al : timer.getActionListeners()) {
                timer.removeActionListener(al);
            }
        }

        timer = new Timer(delay, null);
        ArrayList<ICoordinate> trail = new ArrayList<>();

        timer.addActionListener(new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < trip.size()) {
                    TripPoint tp = trip.get(index);
                    Coordinate coord = new Coordinate(tp.getLat(), tp.getLon());

                    // Update the trail with the current coordinate
                    trail.add(coord);
                    mapViewer.setTrail(trail);

                    // Draw the raccoon marker
                    MapMarker marker = new IconMarker(new Coordinate(tp.getLat(), tp.getLon()), raccoonImage);
                    mapViewer.removeAllMapMarkers();
                    mapViewer.addMapMarker(marker);

                    // Center map if raccoon is outside the visible area
                    Point currentMarkerPoint = mapViewer.getMapPosition(coord, false);
                    if (currentMarkerPoint != null && !mapViewer.getBounds().contains(currentMarkerPoint)) {
                        mapViewer.setDisplayPosition(new Coordinate(34.82,-107.99),6);
                    }

                    index++;
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }


 


}