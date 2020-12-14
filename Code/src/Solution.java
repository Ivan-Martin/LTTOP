import java.util.Arrays;
import java.util.LinkedList;

public class Solution {

    private LinkedList<Integer> paths [];
    private Instance ins;
    private double totalCost;
    private double [] costPerVehicle;

    public Solution (Instance ins){
        this.ins = ins;
        paths = new LinkedList[ins.getPaths()];
        for (int i = 0; i < paths.length; i++){
            paths[i] = new LinkedList <Integer> ();
        }
        totalCost = 0;
        costPerVehicle = new double [ins.getPaths()];
    }

    public Solution (Solution s){
        //Copy constructor
        this.ins = s.ins;
        this.totalCost = s.totalCost;
        this.costPerVehicle = Arrays.copyOf(s.costPerVehicle, s.costPerVehicle.length);
        this.paths = new LinkedList[s.paths.length];
        for (int i = 0; i < s.paths.length; i++){
            this.paths[i] = new LinkedList<>(s.paths[i]);
        }
    }

    public double evaluateCompleteSolution () {
        double totalCost = 0.0;
        for (int i = 0; i < paths.length; i++){
            totalCost += evaluateVehicle(i);
        }
        this.totalCost = totalCost;
        return totalCost;
    }

    public double evaluateVehicle (int vehicleId){
        if (paths[vehicleId].size() == 0) return 0.0;
        double vehicleCost = 0.0;
        int previous = 0;
        for (Integer point : paths[vehicleId]){
            vehicleCost += ins.distanceBetweenPoints(previous, point);
            previous = point;
        }
        vehicleCost += ins.distanceBetweenPoints(previous, 0);
        costPerVehicle[vehicleId] = vehicleCost;
        return vehicleCost;
    }

    public LinkedList <Integer> getVehiclePath (int vehicleId){
        return paths[vehicleId];
    }

    public void setVehiclePath (int vehicleId, LinkedList <Integer> path){
        paths[vehicleId] = path;
        totalCost = 0.0;
    }

    public void addPoint (int point, int vehicle){
        paths[vehicle].add(point);
        totalCost = 0.0;
        //Sets totalCost to 0.0 indicating that it must be evaluated again
        //As changes were made to the solution
    }

    public String toString () {
        String result = "[";
        /*int i = 0;
        for (LinkedList<Integer> p : paths){
            result += "V" + i + ": " + p.toString() + ", ";
            i++;
        }
        result += "]" +  System.lineSeparator();*/
        for (int j = 0; j < ins.getPaths(); j++){
            result += "Vev" + j + ": " + evaluateVehicle(j) + ", ";
        }
        return result;
    }
}
