import java.io.*;
import java_cup.runtime.*;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                
                FileReader fileReader = new FileReader(args[0]);
                Lexer lexer = new Lexer(fileReader);
                parser parser = new parser(lexer);
                
                System.out.println("Analizando archivo: " + args[0]);
                Symbol result = parser.parse();
                System.out.println("Análisis completado exitosamente");
                
            } else {
                
                System.out.println("Ingrese el código (Ctrl+D para terminar):");
                Lexer lexer = new Lexer(new InputStreamReader(System.in));
                parser parser = new parser(lexer);
                
                Symbol result = parser.parse();
                System.out.println("Análisis completado exitosamente");
            }
            
        } catch (Exception e) {
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }
}
