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

    public double evaluateCompleteSolution () {
        if (this.totalCost > 0){
            return this.totalCost;
            //Stores the previous evaluation made.
            //If there weren't any changes made, returns the previous evaluation.
        }
        double totalCost = 0.0;
        for (int i = 0; i < paths.length; i++){
            double vehicleCost = 0.0;
            int previous = 0;
            for (Integer point : paths[i]){
                vehicleCost += ins.distanceBetweenPoints(previous, point);
                previous = point;
            }
            vehicleCost += ins.distanceBetweenPoints(previous, 0);
            costPerVehicle[i] = vehicleCost;
            totalCost += vehicleCost;
        }
        this.totalCost = totalCost;
        return totalCost;
    }

    public void addPoint (int point, int vehicle){
        paths[vehicle].add(point);
        totalCost = 0.0;
        //Sets totalCost to 0.0 indicating that it must be evaluated again
        //As changes were made to the solution
    }
}
