import java.io.File;

public class Main {

    public static void main (String [] args){
        String path = "Instances_A";
        File directory = new File(path);
        for (File f : directory.listFiles()){
            Instance i = new Instance(f.getPath());
            execute (i);
        }
    }

    public static void execute (Instance ins){
        Constructive cons = new Constructive(ins);
        Solution sol = cons.construct();
        System.out.println(sol.evaluateCompleteSolution());
    }

}
