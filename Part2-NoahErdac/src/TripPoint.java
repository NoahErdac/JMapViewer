
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Noah Erdachew
 */

public class TripPoint {
    /**   The latitude value     */
	private double lat;
	/**   The longitude value     */
	private double lon;
	/**   The time value     */
	private int time;
	/**   ArrayList composed of trip objects     */
	private static ArrayList<TripPoint> trip;
	

	/**
	 * Constructs a tripPoint object
	 * 
	 * @param time the time value 
	 * @param lat the latitude value
	 * @param lon the longitude value
	 * 
	 */
	public TripPoint(int time, double lat, double lon) {
		this.time = time;
		this.lat = lat;
		this.lon = lon;
		
	}
	/**
	 *  Returns the time of the TripPoint object
	 *
	 * @return the time of the TripPoint object
	 */
	public int getTime() {
		return this.time;
	}
	/**
	 * Returns the latitude value of the TripPoint object
	 * 
	 * @return the latitude value of the TripPoint object
	 */
	public double getLat() {
		return this.lat;
	}
	/**
	 * Returns the longitude value of the TripPoint object
	 * 
	 * @return the longitude value of the TripPoint object
	 */
	
	public double getLon() {
		return this.lon;
	}
	
	/**
	 * Returns a copy of an ArrayList of a TripPoint object
	 * 
	 * @return a copy of an ArrayList of a TripPoint object
	 */
	public static ArrayList<TripPoint> getTrip(){
		return new ArrayList<>(trip);
	}
	
	/**
	 * This method reads in a csv file to the trip ArrayList by initializing the values in the TripPoint object.
	 * It then fills the ArratList with the initialized TripPoint objects
	 * @param filename the csv file that contains the time, longitude and latitude value
	 */
	public static void readFile(String filename) {
		    ArrayList<TripPoint> trip = new ArrayList<>();
	        String line = "";
	        String csvSplitBy = ",";
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	        	br.readLine();
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(csvSplitBy);
	                TripPoint tripPoint = new TripPoint(Integer.parseInt(data[0]), Double.parseDouble(data[1]), 
	                Double.parseDouble(data[2]));
	               trip.add(tripPoint);
	                
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        catch (NullPointerException e) {
	            e.printStackTrace();
	        }
				TripPoint.trip = trip;
			}
	
	/**
	 * This method calculates the total time of the trip in hours
	 * 
	 * @return total time of trip in hours
	 */
	public static double totalTime() {
		 double totalMinutes = 0;
	        int size = trip.size();
	        for (int i = 1; i < size; i++) {
	            int time1 = trip.get(i - 1).getTime();
	            int time2 = trip.get(i).getTime();
	            int timeDiff = time2 - time1;
	            if (timeDiff >= 0) {
	                totalMinutes += timeDiff;
	            } else {
	                // Account for the case when the time value wraps around to 0
	                totalMinutes += (1440 + timeDiff);
	            }
	        }
	        double totalHours = totalMinutes / 60.0;
	        return totalHours;
	    }
	
	/**
	 * This method computes and returns the Haversine distance between two points in kilometers.
	 * 
	 * @param a the first point
	 * @param b the second Point
	 * @return the Haversine distance between point a and b
	 */
	public static double haversineDistance(TripPoint a, TripPoint b) {
	    final int R = 6371; // Radius of the earth in kilometers
	    double lat1 = Math.toRadians(a.lat);
	    double lon1 = Math.toRadians(a.lon);
	    double lat2 = Math.toRadians(b.lat);
	    double lon2 = Math.toRadians(b.lon);
	    double dLat = lat2 - lat1;
	    double dLon = lon2 - lon1;
	    double sinLat = Math.sin(dLat / 2);
	    double sinLon = Math.sin(dLon / 2);
	    double a1 = sinLat * sinLat + Math.cos(lat1) * Math.cos(lat2) * sinLon * sinLon;
	    double c = 2 * Math.atan2(Math.sqrt(a1), Math.sqrt(1 - a1));
	    double distance = R * c; // Distance in kilometers
	    return distance;
	}
	
	/**
	 * This method calculates and returns the total distance of the trip in kilometers
	 * @return total distance between every point in the trip ArrayList.
	 */
	public static double totalDistance() {
	    ArrayList<TripPoint> trip = getTrip();
	    double totalDistance = 0;
	    for (int i = 0; i < trip.size() - 1; i++) {
	        TripPoint a = trip.get(i);
	        TripPoint b = trip.get(i+1);
	        double distance = haversineDistance(a, b);
	        totalDistance += distance;
	    }
	    return totalDistance;
	}
	
	/**
	 * This method computes and returns the average speed between two points in kilometers per hour
	 * This method works for regardless of which order the points are given
	 * 
	 * @param a the first point
	 * @param b the second point
	 * @return the speed between point a and b
	 */
	public static double avgSpeed(TripPoint a, TripPoint b) {
	    double distance = haversineDistance(a, b);
	    int timeDiff = Math.abs(a.getTime() - b.getTime());
	    if (timeDiff > 720) { // account for time wraparound
	        timeDiff = 1440 - timeDiff;
	    }
	    double timeHours = (double) timeDiff / 60;
	    double avgSpeed = distance / timeHours;
	    return avgSpeed;
	



	}
}
	
		
		
		
		
	
				
		

	
	
	
	
	
	


