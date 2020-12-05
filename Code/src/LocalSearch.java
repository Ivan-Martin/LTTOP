import java.util.LinkedList;

public class LocalSearch {

    private Instance ins;

    public LocalSearch (Instance ins){
        this.ins = ins;
    }

    public Solution search (Solution sol) {
        boolean flag = true;
        while (flag){
            flag = false; //Continues searching until completes a cycle without changes.
            //First improvement approach.
            //1st step: Check all possible movements type 1
            //Movement type 1 => Change the order of two points in the same vehicle route.
            int i = 0;
            while (i < ins.getPaths()){
                flag = false;
                //Checks every vehicle separately.
                //Only continues with next vehicle when previous one cannot be lowered.
                LinkedList<Integer> originalPath = sol.getVehiclePath(i);
                LinkedList<Integer> path = new LinkedList <> (originalPath);
                double originalCost = sol.evaluateVehicle(i);
                for (int point : originalPath){
                    for (int j = 0; j < path.size(); j++){
                        path.removeFirstOccurrence(point);
                        path.add(j, point);
                        sol.setVehiclePath(i, path);
                        if (originalCost > sol.evaluateVehicle(i)){
                            flag = true;
                            break;
                        }
                    }
                    if (flag) break;
                }
                if (!flag){
                    i++;
                }
            }
        }
        return sol;
    }
}
