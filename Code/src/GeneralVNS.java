import java.util.Random;

public class GeneralVNS {

    private Instance ins;
    private Random rand;

    public GeneralVNS (Instance ins){
        this.ins = ins;
        this.rand = new Random();
    }

    public Solution VNS (Solution sol, LocalSearch ls) {
        int k = 0;
        while (k < 3){
            Solution copy = new Solution(sol);
            switch (k){
                case 0 -> ls.search(copy, LocalSearch.SearchMode.INSERTION);
                case 1 -> ls.search(copy, LocalSearch.SearchMode.INTERCHANGE);
                case 2 -> ls.search(copy, LocalSearch.SearchMode.TWOOPT);
            }
            k = checkNeighbourhood(sol, copy, k); //sol = S, copy = S''
        }
        return sol;
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
}
