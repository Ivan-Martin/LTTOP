import java.io.*;
import java.util.Arrays;

public class Instance {

    private int points;
    private double [][] pointLocations;
    private int paths;
    private double [] maxTime;
    private int [] scores;

    public Instance (String filepath) {
        //Try reading and parsing the file to instance details.
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            points = Integer.parseInt(br.readLine().split(" ")[1]);
            paths = Integer.parseInt(br.readLine().split(" ")[1]);
            maxTime = new double[paths];
            String [] read = br.readLine().split(" ");
            for (int i = 0; i < paths; i++){
                //maxTime[i] = (Double.parseDouble(read[1])*250) + ((Math.random()-0.5)*200.0);
                maxTime[i] = 1000 + ((Math.random()-0.5)*200.0);
            }
            System.out.println(Arrays.toString(maxTime));
            pointLocations = new double [points][2];
            scores = new int [points];
            for (int i = 0; i < points; i++){
                String [] splitted = br.readLine().split("\t");
                pointLocations[i][0] = Double.parseDouble(splitted[0]);
                pointLocations[i][1] = Double.parseDouble(splitted[1]);
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

    public double [] getPointLocation (int id) {
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
