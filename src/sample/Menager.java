package sample;

import java.io.File;
import java.io.FilenameFilter;

public class Menager {


    public static File fileExpert(File filename){

        String name1 = filename.getName().split("\\.")[0];
        File f = new File(".\\EyesVesselsExpert");
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(name1);
            }
        });

        return matchingFiles[0];
    }

    public static File fileResult(File filename){

        String name1 = filename.getName().split("\\.")[0];
        File f = new File(".\\Results");
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(name1);
            }
        });

        return matchingFiles[0];
    }


    public static File fileLearn(File filename){

        String name1 = "LEARN" +filename.getName().split("\\.")[0];
        File f = new File(".\\Results");
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(name1);
            }
        });

        return matchingFiles[0];
    }
}