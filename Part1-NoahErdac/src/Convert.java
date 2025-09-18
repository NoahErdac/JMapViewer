import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Convert {
    public static void convertFile(String gpxFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(gpxFile));
            String line;
            List<String> data = new ArrayList<>();
            int time = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<trkpt lat=")) {
                	line = line.replace("?", "");
                    int startIndex = line.indexOf("lat=") + 5;
                    int endIndex = line.indexOf("lon=") - 2;
                    String latitude = line.substring(startIndex, endIndex);
                    latitude = latitude.replace("\"", "");
                    startIndex = line.indexOf("lon=") + 5;
                    endIndex = line.indexOf(">") - 2;
                    String longitude = line.substring(startIndex, endIndex);
                    longitude = longitude.replaceAll("\\s+", "");
                    data.add(time + "," + latitude + "," + longitude);
                    time += 5;
                }
            }
            reader.close();
            
            String outputFile = gpxFile.substring(0, gpxFile.lastIndexOf(".")) + ".csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write("Time,Latitude,Longitude\n");
            for (String record : data) {
                writer.write(record + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
