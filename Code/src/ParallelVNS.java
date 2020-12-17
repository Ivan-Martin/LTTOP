import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelVNS {

    private Instance ins;
    private Random rand;
    private ExecutorService exec;
    private int threadNumber;
    private CountDownLatch latch;
    private Solution [] threadSolutions;

    public ParallelVNS (Instance ins, int threadNumber){
        this.ins = ins;
        rand = new Random(24);
        exec = Executors.newFixedThreadPool(threadNumber);
        this.threadNumber = threadNumber;
        latch = new CountDownLatch(threadNumber);
        threadSolutions = new Solution[threadNumber];
    }

    public Solution VNS (Solution initial, LocalSearch ls, int kMax){
        int k = 0;
        Solution s1;
        while (k < kMax){
            s1 = parallelVNS(initial, ls, k);
            k = checkNeighbourhood(initial, s1, k);
            if (k == 0) initial = s1;
        }
        exec.shutdown();
        return initial;
    }

    public Solution parallelVNS (Solution s0, LocalSearch ls, int k){
        for (int i = 0; i < threadNumber; i++){
            final int id = i;
            exec.submit(() -> threadVNS(s0, ls, k, id));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Solution best = null;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < threadNumber; i++){
            double evaluation = threadSolutions[i].evaluateCompleteSolution();
            if (evaluation < min){
                min = evaluation;
                best = threadSolutions[i];
            }
        }
        return best;
    }

    public Solution threadVNS (Solution s0, LocalSearch ls, int k, int threadId) {
        Solution s2 = new Solution (s0);
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
            l = checkNeighbourhood(s0, s2, l);
            if (l == 0) s0 = s2;
        }
        threadSolutions[threadId] = s0;
        latch.countDown();

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
