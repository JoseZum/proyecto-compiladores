
import java.io.*;
import java_cup.runtime.*;

/**
 * compilador - analisis lexico y sintactico
 *
 * programa main para ejectar las primeras dos fases del compilador, el analisis lexico y el
 * sintactico
 */
public class Main {

    /**
     * principal para coordinar la compilacion del archivo
     * @param args argumento de linea, se espera el nombre del archivo fuente
     */
    public static void main(String[] args) {
        // valida args
        if (args.length == 0) {
            System.out.println("Uso: java Main <archivo_fuente>");
            System.out.println("Ejemplo: java Main test/test_completo.txt");
            return;
        }

        String archivo = args[0];

        try {
            System.out.println("-".repeat(60));
            System.out.println("COMPILADOR Análisis Léxico y Sintáctico");
            System.out.println("-".repeat(60));
            System.out.println("Archivo fuente: " + archivo);
            System.out.println();

            //                      ANALISIS LEXICO 
            System.out.println("----ANAELISIS LEXICO----");
            System.out.println("-".repeat(60));
            //ejecuta el léxico y muestra tokens ademas de crear el archivo con los tokens
            ejecutarAnalisisLexico(archivo);
            System.out.println();

            //                      ANaLISIS SINTACTICO 
            System.out.println("----ANALISIS SINTACTICO----");
            System.out.println("-".repeat(60));
            // Ejecuta el sintactico, imprime el arbol sintactico y tablas de simbolos
            ejecutarAnalisisSintactico(archivo);
            System.out.println();

            System.out.println("-".repeat(60));
            System.out.println("COMPILACION COMPLETA");
            System.out.println("-".repeat(60));

        } catch (FileNotFoundException e) {
            // error de que el funete no exista
            System.err.println("Error: Archivo no encontrado - " + archivo);
        } catch (Exception e) {
            // otro error en el analiss
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }

    
    private static void ejecutarAnalisisLexico(String archivo) throws Exception {
        // funcion para ejecutar el analisis lexico
        PrintWriter fileWriter = new PrintWriter(new FileWriter("tokens_output.txt"));
        FileReader fileReader = new FileReader(archivo);
        Lexer lexer = new Lexer(fileReader);

        // encabezado
        String header = String.format("%-6s %-20s %-10s %-10s %s",
            "NUM", "TOKEN", "LINEA", "COLUMNA", "LEXEMA");
        String separator = "-".repeat(70);

        System.out.println(header);
        System.out.println(separator);
        fileWriter.println("Analisis lexico de: " + archivo);
        fileWriter.println("-".repeat(50));
        fileWriter.println(header);
        fileWriter.println(separator);

        Symbol token;
        int tokenCount = 0; //cienta la cantidad de tokens que hay

        // ciclo para obtener todos los tokens
        while (true) {
            token = lexer.next_token();
            if (token.sym == sym.EOF) break;

            tokenCount++;
            String tokenName = getTokenName(token.sym);//nomrbe del token
            String lexeme = token.value != null ? token.value.toString() : "";//lexema

            String tokenLine = String.format("%-6d %-20s %-10d %-10d %s",
                tokenCount, tokenName, token.left, token.right, lexeme);

            System.out.println(tokenLine);
            fileWriter.println(tokenLine);
        }

        // imprime el resultado
        System.out.println(separator);
        System.out.println("Total de tokens: " + tokenCount);
        System.out.println("Tokens guardados en: tokens_output.txt");

        fileWriter.println(separator);
        fileWriter.println("Total de tokens: " + tokenCount);
        fileWriter.close();
        fileReader.close();
    }


    private static void ejecutarAnalisisSintactico(String archivo) throws Exception {
        //funcion para ejecutar el analisis sintactico
        FileReader fileReader = new FileReader(archivo);
        Lexer lexer = new Lexer(fileReader);
        parser parser = new parser(lexer);

        
        Symbol result = parser.parse();//ejecuta el metodo parse para iniciar el analisis sintactico

        fileReader.close();
    }

   
    private static String getTokenName(int symCode) {
        //funcion para obtener el nombre del token a partir de su codigo
        try {
            java.lang.reflect.Field[] fields = sym.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getType() == int.class) {
                    int value = field.getInt(null);
                    if (value == symCode) {
                        return field.getName();
                    }
                }
            }
        } catch (Exception e) {
            //fallo y retorna el error
        }
        return "SYM_" + symCode;
    }
}
