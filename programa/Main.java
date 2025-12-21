import java.io.*;
import java_cup.runtime.*;

/**
 * Archivo main para ejecutar el analizador lexico
 * soporta entrada estandar o reciber archivos.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Bloque para verificar si se recibe un archivo
            if (args.length > 0) {
                // cuando se recibe el archivo
                // lee el archivo
                FileReader fileReader = new FileReader(args[0]);
                
                // Crear el analizador lexico
                Lexer lexer = new Lexer(fileReader);
                
                // Crear el analizador sintactico
                parser parser = new parser(lexer);
                
                System.out.println("Analizando archivo: " + args[0]);
                
                // Ejecutar el analisis sintactico
                Symbol result = parser.parse();
                
                System.out.println("Análisis completado exitosamente");
                
            } else {
                // cuando se recibe el archivo por nombre en tevlado
                System.out.println("Ingrese el código (Ctrl+D para terminar):");
                
                // Crear el analizador lexico 
                Lexer lexer = new Lexer(new InputStreamReader(System.in));
                
                // Crear el analizador sintactico
                parser parser = new parser(lexer);
                
                // corre el analisis
                Symbol result = parser.parse();
                
                System.out.println("Análisis completado exitosamente");
            }
            
        } catch (Exception e) {
            // Error en el analsiis
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }
}
