public class BasicVNS {

    private Instance ins;

    public BasicVNS (Instance ins){
        this.ins = ins;
    }

    public void VNS (Solution sol, LocalSearch ls, int kMax) {
        int k = 1;
        while (k < kMax){
            Solution copy = new Solution(sol);
            shake(copy);
            ls.search(sol);
            k = checkNeighbourhood(sol, copy, k);
        }
    }

    private void shake (Solution sol){

    }

    private int checkNeighbourhood(Solution sol, Solution copy, int k) {
        double solValue = sol.evaluateCompleteSolution();
        double copyValue = sol.evaluateCompleteSolution();
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
