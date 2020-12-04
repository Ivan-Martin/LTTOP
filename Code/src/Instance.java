import java.io.*;

public class Instance {

    int points;
    int [][] pointLocations;
    int paths;
    int [] maxTime;
    int [] scores;

    public Instance (String filepath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            points = Integer.parseInt(br.readLine().split(" ")[0]);
            paths = Integer.parseInt(br.readLine().split(" ")[0]);
            for (int i = 0; i < paths; i++){
                maxTime[i] = Integer.parseInt(br.readLine());
            }
            for (int i = 0; i < points; i++){
                String [] splitted = br.readLine().split(" ");
                pointLocations[i][0] = Integer.parseInt(splitted[0]);
                pointLocations[i][1] = Integer.parseInt(splitted[1]);
                scores[i] = Integer.parseInt(splitted[2]);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("File does not follow standard format!");
            e.printStackTrace();
        }

    }

    public double distance_between_points (int pointA, int pointB) {
        double x1 = pointLocations[pointA][0];
        double y1 = pointLocations[pointB][0];
        double x2 = pointLocations[pointA][1];
        double y2 = pointLocations[pointB][1];
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

}
