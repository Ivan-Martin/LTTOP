import java.util.LinkedList;
import java.util.Random;

public class GeneralVNS {

    private Instance ins;
    private Random rand;

    public GeneralVNS (Instance ins){
        this.ins = ins;
        this.rand = new Random();
    }

    public Solution VNS (Solution s0, LocalSearch ls, int kMax) {
        int k = 0;
        Solution s1;
        while (k < kMax){
            s1 = new Solution (s0);
            Solution s2 = new Solution (s1);
            shake(s2, k);
            int l = 0;
            while (l < 3){
                switch (l){
                    case 0:
                        ls.search(s2, LocalSearch.SearchMode.INSERTION);
                        break;
                    case 1:
                        ls.search(s2, LocalSearch.SearchMode.TWOOPT);
                        break;
                    case 2:
                        ls.search(s2, LocalSearch.SearchMode.INTERCHANGE);
                        break;
                }
                l = checkNeighbourhood(s1, s2, l);
                if (l == 0) s1 = s2;
                //System.out.println("l = " + l);
            }
            k = checkNeighbourhood(s0, s1, k);
            if (k == 0) s0 = s1;
            //System.out.println("k = " + k);

        }

        return s0;
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
            return 0;
        }
    }

    private boolean compareDoubles (double d1, double d2){
        //Method to check if two doubles are equal or not.
        double subtraction = Math.abs(d1-d2);
        return (subtraction <= 0.000000000001);
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
            if (sol.getVehiclePath(randomVehicleId2).size() == 0){
                sol.getVehiclePath(randomVehicleId2).add(randomPoint);
            } else {
                sol.getVehiclePath(randomVehicleId2).add(rand.nextInt(sol.getVehiclePath(randomVehicleId2).size()), randomPoint);
                //Add to the second vehicle selected, the point removed from 1st vehicle route, in a random Position.
            }

        }
    }
}
