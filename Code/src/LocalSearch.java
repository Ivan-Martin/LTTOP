import java.util.LinkedList;

public class LocalSearch {

    private Instance ins;

    public LocalSearch (Instance ins){
        this.ins = ins;
    }

    public void search (Solution sol) {
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
        //1st step: Check all possible movements type 1
        //Movement type 1 => Change the order of two points in the same vehicle route.
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
}
