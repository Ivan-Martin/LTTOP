import java.io.File;

public class Main {

    public static void main (String [] args){
        String path = "Instances_G";
        File directory = new File(path);
        for (File f : directory.listFiles()){
            Instance i = new Instance(f.getPath());
            execute (i);
        }
    }

    public static void execute (Instance ins){
        Constructive cons = new Constructive(ins);
        Solution sol = cons.construct();
        System.out.println("Constructed solution: " + sol.evaluateCompleteSolution());
        LocalSearch ls = new LocalSearch(ins);
        ls.search(sol);
        BasicVNS vns = new BasicVNS(ins);
        System.out.println("Searched solution: " + sol.evaluateCompleteSolution());
        Solution best = vns.VNS(sol, ls, 5);
        System.out.println("BVNS solution: " + best.evaluateCompleteSolution());
    }

}
