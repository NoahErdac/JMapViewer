import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;



public class TripPoint {

	private double lat;	// latitude
	private double lon;	// longitude
	private int time;	// time in minutes
	public static double HEURISTIC_1_THRESHOLD = 0.6; // km
	public static double HEURISTIC_2_RADIUS = 0.5; // km
	private static ArrayList<TripPoint> trip;	// ArrayList of every point in a trip
	private static ArrayList<TripPoint> movingTrip;

	// default constructor
	public TripPoint() {
		time = 0;
		lat = 0.0;
		lon = 0.0;
	}

	// constructor given time, latitude, and longitude
	public TripPoint(int time, double lat, double lon) {
		this.time = time;
		this.lat = lat;
		this.lon = lon;
	}

	// returns time
	public int getTime() {
		return time;
	}

	// returns latitude
	public double getLat() {
		return lat;
	}

	// returns longitude
	public double getLon() {
		return lon;
	}

	// returns a copy of trip ArrayList
	public static ArrayList<TripPoint> getTrip() {
		return new ArrayList<>(trip);
	}

	// uses the haversine formula for great sphere distance between two points
	public static double haversineDistance(TripPoint first, TripPoint second) {
		// distance between latitudes and longitudes
		double lat1 = first.getLat();
		double lat2 = second.getLat();
		double lon1 = first.getLon();
		double lon2 = second.getLon();

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		// convert to radians
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		// apply formulae
		double a = Math.pow(Math.sin(dLat / 2), 2) +
				Math.pow(Math.sin(dLon / 2), 2) *
				Math.cos(lat1) *
				Math.cos(lat2);
		double rad = 6371;
		double c = 2 * Math.asin(Math.sqrt(a));
		return rad * c;
	}

	// finds the average speed between two TripPoints in km/hr
	public static double avgSpeed(TripPoint a, TripPoint b) {

		int timeInMin = Math.abs(a.getTime() - b.getTime());

		double dis = haversineDistance(a, b);

		double kmpmin = dis / timeInMin;

		return kmpmin*60;
	}

	// returns the total time of trip in hours
	public static double totalTime() {
		int minutes = trip.get(trip.size()-1).getTime();
		double hours = minutes / 60.0;
		return hours;
	}

	// finds the total distance traveled over the trip
	public static double totalDistance() throws FileNotFoundException, IOException {

		double distance = 0.0;

		if (trip.isEmpty()) {
			readFile("triplog.csv");
		}

		for (int i = 1; i < trip.size(); ++i) {
			distance += haversineDistance(trip.get(i-1), trip.get(i));
		}

		return distance;
	}

	public String toString() {

		return null;
	}

	public static void readFile(String filename) throws FileNotFoundException, IOException {

		// construct a file object for the file with the given name.
		File file = new File(filename);

		// construct a scanner to read the file.
		Scanner fileScanner = new Scanner(file);

		// initiliaze trip
		trip = new ArrayList<TripPoint>();

		// create the Array that will store each lines data so we can grab the time, lat, and lon
		String[] fileData = null;

		// grab the next line
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();

			// split each line along the commas
			fileData = line.split(",");

			// only write relevant lines
			if (!line.contains("Time")) {
				// fileData[0] corresponds to time, fileData[1] to lat, fileData[2] to lon
				trip.add(new TripPoint(Integer.parseInt(fileData[0]), Double.parseDouble(fileData[1]), Double.parseDouble(fileData[2])));
			}
		}

		// close scanner
		fileScanner.close();
	}
	/**
	 * Heuristic 1 stop detection method. This method calculates the number of stops using a threshold
	 * distance between consecutive points. If the distance is less than the threshold, the point is
	 * considered a stop.
	 *
	 * @return The number of stops detected using heuristic 1.
	 * @throws FileNotFoundException If the input file is not found.
	 * @throws IOException If there is an error reading the input file.
	 */

	public static int h1StopDetection() throws FileNotFoundException, IOException {
		// Read in file
		readFile("triplog.csv");
		// Number of stops
		int numStops = 0;
		// New ArrayList that consists of no stops
		movingTrip = new ArrayList<>();
		// First TripPoint declaration
		TripPoint prev = null;
		// Second TripPoint declaration and for each loop starter
		for (TripPoint point : trip) {
			if (prev == null) {
				movingTrip.add(point);
			} else {
				// Compute distance between the consecutive TripPoints
				double distance = haversineDistance(prev, point);
				//If the distance is less than the threshold, the point considered a stop.
				if (distance > HEURISTIC_1_THRESHOLD) {
					movingTrip.add(point);
				} else {
					numStops++;
				}
			} 
			// Continue to the next points
			prev = point;
		}
		return numStops;
	}
	/**
	 * Heuristic 2 stop detection method. This method checks for groups of three or more points that are all within 0.5km
	 * of each other. Also called "stop radius". If there are three or more points within the stop radius of each other, 
	 * they are considered to be part of the same stop zone. 
	 * 
	 * @return the number of stops in the trip
	 * @throws FileNotFoundException if the csv file is not found
	 * @throws IOException if an I/O error occurs while reading the csv file
	 */
	public static int h2StopDetection() throws FileNotFoundException, IOException {
		// Read trip data from csv file
		readFile("triplog.csv");
		// Initialize variables
		int numStops = 0;
		movingTrip = new ArrayList<>();
		// Create ArrayList to keep track of whether each point is a stop or not
		ArrayList<Boolean> isStop = new ArrayList<>(Collections.nCopies(trip.size(), false));
		// Iterate through each point in the trip
		for (int i = 0; i < trip.size(); i++) {
			TripPoint current = trip.get(i);
			ArrayList<Integer> nearbyPoints = new ArrayList<>();
			// Find nearby points within a certain radius using haversine distance
			for (int j = i+1; j < trip.size()-1; j++) {
				if (j == i) continue;
				TripPoint other = trip.get(j);
				if (haversineDistance(current, other) <= HEURISTIC_2_RADIUS) {
					nearbyPoints.add(j);
				}
			}
			// If there are at least two nearby points, mark them as stops
			if (nearbyPoints.size() >= 2) {
				isStop.set(i, true);
				for (Integer index : nearbyPoints) {
					isStop.set(index, true);
				}
			}
		}
		// Count the number of stops and create a list of moving trip points
		for (int i = 0; i < trip.size(); i++) {
			if (isStop.get(i)) {
				numStops++;
			} else {
				movingTrip.add(trip.get(i));
			}
		}

		return numStops;
	}
	/**
	 * This method Calculates the total time spent in motion during a trip it uses an ArrayList of TripPoints 
	 * to determine the total trip time and the time spent stopped.
	 * The moving time is then calculated by subtracting the stop time from the total trip time.
	 * 
	 * @return the moving time in hours
	 */
	public static double movingTime() {
		// Check if the ArrayList of moving trip points is empty or has less than two points
		if (movingTrip == null || movingTrip.size() < 2) {
			return 0;
		}
		// Initialize variables
		double totalTimeInMinutes = trip.get(trip.size() - 1).getTime();
		double stopTimeInMinutes = 0;
		// Iterate through each point in the trip to calculate stop time
		for (int i = 1; i < trip.size(); i++) {
			if (!movingTrip.contains(trip.get(i))) {
				stopTimeInMinutes += Math.abs(trip.get(i).getTime() - trip.get(i - 1).getTime());
			}
		}
		// Calculate the moving time and convert from minutes to hours
		double movingTimeInMinutes = totalTimeInMinutes - stopTimeInMinutes;

		return movingTimeInMinutes / 60.0; 
	}

	/**
	 * This method returns a copy of the ArrayList movingTrip
	 * @return copy of movingTrip ArrayList
	 */
	public static ArrayList<TripPoint> getMovingTrip() {
		return new ArrayList<>(movingTrip);
	}
	/** 
	 * This method calculates the average moving speed during a trip. It uses an ArrayList of TripPoints containing only 
	 * the points where the vehicle was in motion.
	 * The distance between each pair of adjacent points in the moving trip is calculated using the haversine distance formula.
	 * The average moving speed is then calculated by dividing the total moving distance by the moving time.
	 * 
	 * @return the average moving speed in kilometers per hour
	 * @throws FileNotFoundException if the csv file is not found
	 * @throws IOException if an I/O error occurs while reading the csv file
	 */
	public static double avgMovingSpeed() throws FileNotFoundException, IOException {
		// Check if the ArrayList of moving trip points is empty or has less than two points
		if (movingTrip == null || movingTrip.size() < 2) {
			return 0;
		}
		// Calculate the total moving distance
		double totalMovingDistance = 0.0;
		for (int i = 1; i < movingTrip.size(); ++i) {
			totalMovingDistance += haversineDistance(movingTrip.get(i - 1), movingTrip.get(i));
		}
		// Calculate the moving time in hours
		double movingTimeInHours = movingTime();
		// Check if the moving time is zero
		if (movingTimeInHours == 0) {
			return 0;
		}
		// Calculate and return the average moving speed in kilometers per hour
		return totalMovingDistance / movingTimeInHours;
	}
	/**
	 * Calculates the total time spent stopped during a trip. It uses an ArrayList of TripPoints to determine the 
	 * total trip time and the time spent in motion.
	 * The stopped time is then calculated by subtracting the moving time from the total trip time.
	 * 
	 * @return the stopped time in hours
	 * @throws FileNotFoundException if the csv file is not found
	 * @throws IOException if an I/O error occurs while reading the csv file
	 */
	public static double stoppedTime() throws FileNotFoundException, IOException {
		// Check if the ArrayList of trip points is empty or has less than two points
		if (trip == null || trip.size() < 2) {
			return 0;
		}

		// Calculate the total trip time and the moving time
		double totalTimeInHours = totalTime();
		double movingTimeInHours = movingTime();
		
		// Calculate the stopped time and return it in hours
		return totalTimeInHours - movingTimeInHours;
	}

}
