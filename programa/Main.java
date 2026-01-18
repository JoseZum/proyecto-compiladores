import java.io.*;
import java_cup.runtime.*;

/**
 * Compilador Navidad - Análisis Léxico y Sintáctico
 * 
 * Este programa ejecuta:
 * 1. Análisis léxico: genera archivo tokens_output.txt con todos los tokens
 * 2. Análisis sintáctico: valida gramática, construye AST, tablas de símbolos
 * 
 * Uso: java Main <archivo.txt>
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java Main <archivo_fuente>");
            System.out.println("Ejemplo: java Main test_completo.txt");
            return;
        }

        String archivo = args[0];
        
        try {
            System.out.println("=".repeat(60));
            System.out.println("COMPILADOR NAVIDAD - Análisis Léxico y Sintáctico");
            System.out.println("=".repeat(60));
            System.out.println("Archivo fuente: " + archivo);
            System.out.println();

            // ==================== FASE 1: ANÁLISIS LÉXICO ====================
            System.out.println(">>> FASE 1: ANÁLISIS LÉXICO");
            System.out.println("-".repeat(60));
            
            ejecutarAnalisisLexico(archivo);
            
            System.out.println();

            // ==================== FASE 2: ANÁLISIS SINTÁCTICO ====================
            System.out.println(">>> FASE 2: ANÁLISIS SINTÁCTICO");
            System.out.println("-".repeat(60));
            
            ejecutarAnalisisSintactico(archivo);
            
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.println("COMPILACIÓN COMPLETADA EXITOSAMENTE");
            System.out.println("=".repeat(60));
            
        } catch (FileNotFoundException e) {
            System.err.println("Error: Archivo no encontrado - " + archivo);
        } catch (Exception e) {
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }

    /**
     * Fase 1: Análisis Léxico
     * Lee el archivo fuente y genera tokens_output.txt con todos los tokens
     */
    private static void ejecutarAnalisisLexico(String archivo) throws Exception {
        PrintWriter fileWriter = new PrintWriter(new FileWriter("tokens_output.txt"));
        FileReader fileReader = new FileReader(archivo);
        Lexer lexer = new Lexer(fileReader);

        // Encabezado
        String header = String.format("%-6s %-20s %-10s %-10s %s",
            "NUM", "TOKEN", "LINEA", "COLUMNA", "LEXEMA");
        String separator = "-".repeat(70);

        System.out.println(header);
        System.out.println(separator);
        fileWriter.println("Análisis léxico de: " + archivo);
        fileWriter.println("=".repeat(50));
        fileWriter.println(header);
        fileWriter.println(separator);

        Symbol token;
        int tokenCount = 0;

        while (true) {
            token = lexer.next_token();
            if (token.sym == sym.EOF) break;

            tokenCount++;
            String tokenName = getTokenName(token.sym);
            String lexeme = token.value != null ? token.value.toString() : "";

            String tokenLine = String.format("%-6d %-20s %-10d %-10d %s",
                tokenCount, tokenName, token.left, token.right, lexeme);

            System.out.println(tokenLine);
            fileWriter.println(tokenLine);
        }

        System.out.println(separator);
        System.out.println("Total de tokens: " + tokenCount);
        System.out.println("Tokens guardados en: tokens_output.txt");

        fileWriter.println(separator);
        fileWriter.println("Total de tokens: " + tokenCount);
        fileWriter.close();
        fileReader.close();
    }

    /**
     * Fase 2: Análisis Sintáctico
     * Valida gramática, construye AST y genera tablas de símbolos
     */
    private static void ejecutarAnalisisSintactico(String archivo) throws Exception {
        FileReader fileReader = new FileReader(archivo);
        Lexer lexer = new Lexer(fileReader);
        parser parser = new parser(lexer);
        
        Symbol result = parser.parse();
        
        fileReader.close();
    }

    /**
     * Convierte el código del símbolo a su nombre legible
     */
    private static String getTokenName(int symCode) {
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
            // Si falla, retornar el código
        }
        return "SYM_" + symCode;
    }
}
