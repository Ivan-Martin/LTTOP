import java.util.LinkedList;
import java.util.Random;

public class BasicVNS {

    private Instance ins;
    private Random rand;

    public BasicVNS (Instance ins){
        this.ins = ins;
        this.rand = new Random();
    }

    public Solution VNS (Solution sol, LocalSearch ls, int kMax) {
        int k = 1;
        while (k < kMax){
            Solution copy = new Solution(sol);
            shake(copy, k); //copy = S'
            ls.search(copy, LocalSearch.SearchMode.INSERTION); //copy = S''
            k = checkNeighbourhood(sol, copy, k); //sol = S, copy = S''
            if (k == 1) {
                //Only happens if k returns to 1 by finding a better solution.
                sol = copy;
            }
        }
        return sol;
    }

    private void shake (Solution sol, int k){
        /*
          Selects a random point from a Vehicle, removes it from its path
          And adds that point to another vehicle's path.
          Complexity = O(1).
         */
        int totalVehicles = ins.getPaths();
        for (int i = 0; i < k; i++){
            int randomVehicleId = rand.nextInt(totalVehicles);
            LinkedList<Integer> randomPath = sol.getVehiclePath(randomVehicleId);
            if (randomPath.size() == 0){
                i--;
                continue;
            }
            int randomPoint = randomPath.remove(rand.nextInt(randomPath.size()));
            //Remove randomly a point assigned to a randomly selected vehicle's path.

            int randomVehicleId2 = rand.nextInt(totalVehicles-1);
            //Select another random vehicle.
            if (randomVehicleId2 == randomVehicleId){
                //If randomly selected same vehicle, set the 2nd random vehicle to the last one
                //(Last vehicle couldn't possibly be selected as random was launched [0, (total-1)]
                //This ensures the two random vehicles are not the same at O(1) complexity.
                randomVehicleId2 = totalVehicles-1;
            }
            sol.getVehiclePath(randomVehicleId2).add(rand.nextInt(sol.getVehiclePath(randomVehicleId2).size()), randomPoint);
            //Add to the second vehicle selected, the point removed from 1st vehicle route, in a random Position.
        }
    }

    private int checkNeighbourhood(Solution sol, Solution copy, int k) {
        double solValue = sol.evaluateCompleteSolution();
        double copyValue = copy.evaluateCompleteSolution();
        if (compareDoubles(solValue, copyValue)){
            //Values are equal, we've arrived at same solution. Increase k.
            return k+1;
        } else if (solValue < copyValue){
            //Original solution was better than new solution found. Increase k.
            return k+1;
        } else {
            //New solution is better than original one. Return k to 1.
            return 1;
        }
    }

    private boolean compareDoubles (double d1, double d2){
        //Method to check if two doubles are equal or not.
        double subtraction = Math.abs(d1-d2);
        return (subtraction <= 0.000000000001);
    }

}
