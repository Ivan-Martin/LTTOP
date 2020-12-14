import java.util.HashSet;
import java.util.LinkedList;

public class LocalSearch {

    private Instance ins;

    public enum SearchMode {
        INSERTION, INTERCHANGE, TWOOPT
    }

    public LocalSearch (Instance ins){
        this.ins = ins;
    }

    public void search (Solution sol, SearchMode mode) {
        checkFactibility(sol);
        switch (mode){
            case INSERTION:
                insertionLocalSearch(sol);
                break;
            case INTERCHANGE:
                interchangeLocalSearch(sol);
                break;
            case TWOOPT:
                twoOptLocalSearch(sol);
                break;
        }
    }

    private void insertionLocalSearch(Solution sol) {
        //Optimizes order of points in a path.
        boolean flag = true;
        while (flag){

            flag = false; //Continues searching until completes a cycle without changes.
            //First improvement approach.
            int i = 0;
            while (i < ins.getPaths()){
                flag = checkVehiclePath(i, sol);
                //Checks every vehicle separately.
                //Only continues with next vehicle when previous one cannot be lowered.
                if (!flag){
                    i++;
                }
            }

        }
    }

    private void interchangeLocalSearch(Solution sol) {
        //Optimizes point assignments to a vehicle
        boolean flag = true;
        while (flag){
            flag = false; //Continues searching until completes a cycle without changes.
            //First improvement approach.
            int i = 0;
            while (i < ins.getPaths()){
                flag = checkVehiclePoints(i, sol);
                //Checks every vehicle separately.
                //Only continues with next vehicle when previous one cannot be lowered.
                if (!flag){
                    i++;
                }
            }

        }
    }

    private boolean checkVehiclePoints (int vehicleId, Solution sol){
        LinkedList <Integer> path = sol.getVehiclePath(vehicleId);
        //For a given vehicle path, checks all points of that path in all positions of all other vehicles.
        boolean flag = false;
        for (int h = 0; h < path.size(); h++){
            int pointId = path.get(h);
            double timeGain = calculateTimeRemoval(path, h);
            for (int i = 0; i < ins.getPaths(); i++){
                if (i != vehicleId){
                    LinkedList <Integer> path2 = sol.getVehiclePath(i);
                    for (int j = 0; j <= path2.size(); j++){
                        double timeLoss = calculateTimeAddition(path2, j, pointId);
                        if (timeLoss + timeGain < 0 && sol.evaluateVehicle(i) + timeLoss < ins.getMaxTime(i)){
                            //Three edge removals + three edges additions. If total sum equals less time; better solution.
                            path2.add(j, pointId);
                            path.removeFirstOccurrence(pointId);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) break;
                }
            }
            if (flag) break;
        }
        return flag;
    }

    private boolean checkVehiclePath (int vehicleId, Solution sol){
        //1st step: Check all possible movements insertion 1
        //Insertion 1 => Change the order of one point in the same vehicle route.
        boolean foundBest = false;
        LinkedList<Integer> originalPath = sol.getVehiclePath(vehicleId);
        LinkedList<Integer> path = new LinkedList <> (originalPath);
        double originalCost = sol.evaluateVehicle(vehicleId);
        for (int point : originalPath){
            for (int j = 0; j < path.size(); j++){
                path.removeFirstOccurrence(point);
                path.add(j, point);
                sol.setVehiclePath(vehicleId, path);
                if (originalCost > sol.evaluateVehicle(vehicleId)){
                    foundBest = true;
                    break;
                }
            }
            if (foundBest) break;
        }
        return foundBest;
    }

    private void checkFactibility (Solution sol){
        /* Ejection chain approach:
        * Worst point is removed and allocated to another path, possibly removing other points
        * Process continues removing and adding points until a factible solution is found.
         */
        HashSet <Integer> [] exploredPossibilities = new HashSet [ins.getPoints()];
        for (int i = 0; i < ins.getPoints(); i++) exploredPossibilities[i] = new HashSet<>();
        LinkedList <Integer> remainingPoints = new LinkedList<>();
        //HashSet <Integer> movedPoints = new HashSet<>();

        removePoints(sol, remainingPoints, exploredPossibilities);
        while (!remainingPoints.isEmpty()){

            addPoints(sol, remainingPoints, exploredPossibilities);
            removePoints(sol, remainingPoints, exploredPossibilities);
        }


    }

    private void addPoints(Solution sol, LinkedList<Integer> remainingPoints, HashSet <Integer> [] exploredPossibilities) {
        while (!remainingPoints.isEmpty()){
            int point = remainingPoints.pop();
            int bestPosition = -1;
            int bestVehicle = -1;
            double minCost = Double.POSITIVE_INFINITY;
            for (int i = 0; i < ins.getPaths(); i++){
                if (exploredPossibilities[point].contains(i)) continue;
                LinkedList <Integer> path = sol.getVehiclePath(i);
                double actualValue = sol.evaluateVehicle(i);
                for (int j = 0; j <= path.size(); j++){
                    path.add(j, point);
                    double newValue = sol.evaluateVehicle(i);
                    if ((actualValue - newValue) < minCost){
                        bestPosition = j;
                        bestVehicle = i;
                    }
                    path.removeFirstOccurrence(point);
                }
            }

            sol.getVehiclePath(bestVehicle).add(bestPosition, point);
            exploredPossibilities[point].add(bestVehicle);
        }
    }

    private void removePoints(Solution sol, LinkedList <Integer> remainingPoints, HashSet <Integer> [] exploredPossibilities) {

        for (int i = 0; i < ins.getPaths(); i++){
            double totalCost = sol.evaluateVehicle(i);
            while (totalCost > ins.getMaxTime(i)){
                //Remove packages until cost meets time restrictions
                LinkedList <Integer> path = sol.getVehiclePath(i);
                double maxTime = 0.0;
                int pointRemoval = -1;
                for (int j = 0; j < path.size(); j++){
                    double time = calculateTimeRemoval(path, j) * -1;
                    if (time > maxTime && exploredPossibilities[path.get(j)].size() != ins.getPaths()){
                        //To remove a package, its explored possibilities should not be the total of available vehicles.
                        pointRemoval = path.get(j);
                        maxTime = time;
                    }
                }
                //if (pointRemoval == -1) continue;
                //movedPoints.add(path.get(pointRemoval));

                remainingPoints.add(pointRemoval);
                path.removeFirstOccurrence(pointRemoval);
                totalCost = sol.evaluateVehicle(i);
            }
        }
    }
    /*
    @param  point   location in the list of the point to be removed
     */
    private double calculateTimeRemoval(LinkedList<Integer> path, int pointLocation) {
        //Calculates time resulting of the removal of a point as such:
        //Two edge removals: previous <-> point <-> following
        //One edge addition: previous <-> following
        if (path.size() == 1) {
            return - ins.distanceBetweenPoints(0, path.get(0))*2;
        }
        if (pointLocation == 0){
            return - ins.distanceBetweenPoints(path.get(0), 0)
                    - ins.distanceBetweenPoints(path.get(0), path.get(1))
                        + ins.distanceBetweenPoints(0, path.get(1));
        } else if (pointLocation == path.size()-1){
            return - ins.distanceBetweenPoints(path.getLast(), 0)
                    - ins.distanceBetweenPoints(path.get(pointLocation-1), path.getLast())
                        + ins.distanceBetweenPoints(0, path.get(pointLocation-1));
        } else {
            return - ins.distanceBetweenPoints(path.get(pointLocation-1), path.get(pointLocation))
                    - ins.distanceBetweenPoints(path.get(pointLocation), path.get(pointLocation+1))
                        + ins.distanceBetweenPoints(path.get(pointLocation-1), path.get(pointLocation+1));
        }
    }

    private double calculateTimeAddition(LinkedList<Integer> path, int location, int pointId) {
        //Calculates time resulting of the addition of a point as such:
        //Two edge additions: previousLocation <-> point <-> location
        //One edge removal: previousLocation <-> location
        if (path.size() == 0) {
            return 2* ins.distanceBetweenPoints(0, pointId);
        }
        if (location == 0){
            //Origin <-> point <-> path.get(0); Origin <-> path.get(0)
            return ins.distanceBetweenPoints(0, pointId)
                    + ins.distanceBetweenPoints(pointId, path.get(0))
                    - ins.distanceBetweenPoints(0, path.get(0));
        } else if (location == path.size()){
            //path.getLast() <-> point <-> Origin; path.getLast() <-> Origin
            return ins.distanceBetweenPoints(path.getLast(), pointId)
                    + ins.distanceBetweenPoints(pointId, 0)
                    - ins.distanceBetweenPoints(path.getLast(), 0);
        } else {
            //path.get(location - 1) <-> point <-> path.get(location); path.get(location - 1) <-> path.get(location);
            return ins.distanceBetweenPoints(path.get(location - 1), pointId)
                    + ins.distanceBetweenPoints(pointId, path.get(location))
                    - ins.distanceBetweenPoints(path.get(location - 1), path.get(location));
        }
    }

    private void twoOptLocalSearch (Solution sol){
        //Optimizes point assignments to a vehicle
        boolean flag = true;
        while (flag){
            flag = false; //Continues searching until completes a cycle without changes.
            //First improvement approach.
            int i = 0;
            while (i < ins.getPaths()){
                flag = twoOptVehicle(sol, i);
                //Checks every vehicle separately.
                //Only continues with next vehicle when previous one cannot be lowered.
                if (!flag){
                    i++;
                }
            }

        }
    }

    private boolean twoOptVehicle(Solution sol, int vehicleId) {
        LinkedList <Integer> path = sol.getVehiclePath(vehicleId);
        double original_value = sol.evaluateVehicle(vehicleId);
        boolean flag = false;
        for (int i = 1; i <= path.size()-2; i++){
            for (int j = i+1; j <= path.size()-1; j++){
                LinkedList <Integer> newPath = twoOptSwap(path, i, j);
                sol.setVehiclePath(vehicleId, newPath);
                double newValue = sol.evaluateVehicle(vehicleId);
                if (newValue < original_value) {
                    flag = true;
                    break;
                }
            }
            if (flag) break;
            else sol.setVehiclePath(vehicleId, path);
        }
        return flag;
    }

    private LinkedList <Integer> twoOptSwap (LinkedList <Integer> path, int start, int end){
        LinkedList <Integer> newPath = new LinkedList<>();
        for (int i = 0; i < start; i++) newPath.add(path.get(i));
        for (int i = start; i <= end; i++) newPath.add(path.get(end-i+start));
        for (int i = end; i < path.size(); i++) newPath.add(path.get(i));
        return newPath;
    }
}
