import java_cup.runtime.*;
import java.io.*;

class Main {

    //Main function used to run our parser.
    public static void main(String[] argv) throws Exception{
        System.out.println("public class Main { \n");
        Parser p = new Parser(new Scanner(new InputStreamReader(System.in)));
        p.parse();
        System.out.println("} \n");
    }
}
