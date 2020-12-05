import java.io.*;

public class Instance {

    private int points;
    private int [][] pointLocations;
    private int paths;
    private int [] maxTime;
    private int [] scores;

    public Instance (String filepath) {
        //Try reading and parsing the file to instance details.
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            points = Integer.parseInt(br.readLine().split(" ")[0]);
            paths = Integer.parseInt(br.readLine().split(" ")[0]);
            maxTime = new int[points];
            for (int i = 0; i < paths; i++){
                maxTime[i] = Integer.parseInt(br.readLine());
            }
            pointLocations = new int [points][2];
            scores = new int [points];
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

    public int getPoints() {
        return points;
    }

    public int getPaths() {
        return paths;
    }

    public double getMaxTime (int vehicleId){
        return maxTime[vehicleId];
    }

    public int [] getPointLocation (int id) {
        return pointLocations[id];
    }

    public double distanceBetweenPoints (int pointA, int pointB) {
        double x1 = pointLocations[pointA][0];
        double y1 = pointLocations[pointB][0];
        double x2 = pointLocations[pointA][1];
        double y2 = pointLocations[pointB][1];
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

}
