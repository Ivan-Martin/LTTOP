import java.io.File;

public class Main {

    public static void main (String [] args){
        //String path = "OriginalInstances/Instances_G";
        String path = "NewInstances";
        File directory = new File(path);
        long time = System.currentTimeMillis();
        for (File fi : directory.listFiles()) {
            for (File f : fi.listFiles()){
                Instance i = new Instance(f.getPath());
                execute (i);
            }
            System.out.println();
        }
        System.out.println(System.currentTimeMillis() - time);
    }

    public static void execute (Instance ins){
        Constructive cons = new Constructive(ins);
        Solution sol = cons.construct();
        //System.out.println("Constructed solution: " + sol.evaluateCompleteSolution());
        LocalSearch ls = new LocalSearch(ins);
        ls.search(sol, LocalSearch.SearchMode.INSERTION);
        ParallelVNS vns = new ParallelVNS(ins, 8);
        //System.out.println("Searched solution: " + sol.evaluateCompleteSolution());
        Solution best = vns.VNS(sol, ls, 5);
        System.out.println(best.evaluateCompleteSolution());
        //System.out.println(best.toString());

    }

}
