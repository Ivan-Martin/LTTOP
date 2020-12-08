import java.util.LinkedList;

public class LocalSearch {

    private Instance ins;

    public LocalSearch (Instance ins){
        this.ins = ins;
    }

    public void search (Solution sol) {
        for (int i = 0; i < ins.getPaths(); i++) checkVehiclePath(i, sol);
        checkFactibility(sol);
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
        LinkedList <Integer> remainingPoints = new LinkedList<>();
        removePoints(sol, remainingPoints);
        while (!remainingPoints.isEmpty()){
            addPoints(sol, remainingPoints);
            removePoints(sol, remainingPoints);
        }

    }

    private void addPoints(Solution sol, LinkedList<Integer> remainingPoints) {
        while (!remainingPoints.isEmpty()){
            int point = remainingPoints.pop();
            int bestPosition = -1;
            int bestVehicle = -1;
            double minCost = Double.POSITIVE_INFINITY;
            for (int i = 0; i < ins.getPaths(); i++){
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
        }
    }

    private void removePoints(Solution sol, LinkedList <Integer> remainingPoints) {
        for (int i = 0; i < ins.getPaths(); i++){
            double totalCost = sol.evaluateVehicle(i);
            while (totalCost > ins.getMaxTime(i)){
                //Remove packages until cost meets time restrictions
                LinkedList <Integer> path = sol.getVehiclePath(i);
                double maxTime = 0.0;
                int pointRemoval = -1;
                for (int j = 0; j < path.size(); j++){
                    double time = calculateTime(path, j);
                    if (time > maxTime){
                        pointRemoval = j;
                        maxTime = time;
                    }
                }
                remainingPoints.add(pointRemoval);
                path.removeFirstOccurrence(pointRemoval);
                totalCost = sol.evaluateVehicle(i);
            }
        }
    }

    private double calculateTime(LinkedList<Integer> path, int point) {
        //Calculates time gain by removing that point as such:
        //Two edge removals: previous <-> point <-> following
        //One edge addition: previous <-> following
        if (point == 0){
            return ins.distanceBetweenPoints(path.get(0), 0)
                    + ins.distanceBetweenPoints(path.get(0), path.get(1))
                        - ins.distanceBetweenPoints(0, path.get(1));
        } else if (point == path.size()-1){
            return ins.distanceBetweenPoints(path.getLast(), 0)
                    + ins.distanceBetweenPoints(path.get(point-1), path.getLast())
                        - ins.distanceBetweenPoints(0, path.get(point-1));
        } else {
            return ins.distanceBetweenPoints(path.get(point-1), path.get(point))
                    + ins.distanceBetweenPoints(path.get(point), path.get(point+1))
                        - ins.distanceBetweenPoints(path.get(point-1), path.get(point+1));
        }
    }
}
