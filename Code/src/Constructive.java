import java.util.LinkedList;

public class Constructive {

    private Instance ins;

    public Constructive (Instance ins){
        this.ins = ins;
    }

    public Solution construct () {
        Solution sol = new Solution (ins);
        int [] vehiclePositions = new int [ins.getPaths()];
        //VehiclePositions stores the actual position of each vehicle.

        double [] vehicleTimes = new double [ins.getPaths()];
        //vehicleTimes stores the actual utilized time per vehicle.

        LinkedList <Integer> remainingPoints = new LinkedList<Integer>();
        //RemainingPoints stores the points still to locate in a Vehicle.

        for (int i = 0; i < ins.getPoints(); i++) remainingPoints.add(i);

        while (!remainingPoints.isEmpty()){
            int bestPoint = -1;
            int bestVehicle = -1;
            double minDistance = Double.POSITIVE_INFINITY;
            for (int point : remainingPoints){
                for (int i = 0; i < ins.getPaths(); i++){
                    double cost = ins.distanceBetweenPoints(point, vehiclePositions[i]);
                    if (minDistance > cost && vehicleTimes[i] + cost < ins.getMaxTime(i)){
                        minDistance = cost;
                        bestPoint = point;
                        bestVehicle = i;
                    }
                }
            }
            //Checks for each remaining point, for each vehicle, which is the minimal
            //time possible for any given possibility.
            //Assigns best point to the best vehicle, ignoring all possibilities
            //that exceed the max Time allocated for that vehicle.
            remainingPoints.removeFirstOccurrence(bestPoint);
            vehiclePositions[bestVehicle] = bestPoint;
            vehicleTimes[bestVehicle] += minDistance;
            sol.addPoint(bestPoint, bestVehicle);
        }

        return sol;

    }

}
