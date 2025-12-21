import java.io.*;
import java_cup.runtime.*;

/**
 * Programa para probar el lexer de forma independiente
 * Imprime todos los tokens encontrados en el archivo de entrada
 * y los guarda en tokens_output.txt
 */
public class TestLexer {
    public static void main(String[] args) {
        PrintWriter fileWriter = null;
        try {
            Lexer lexer;

            // Crear archivo de salida para tokens
            fileWriter = new PrintWriter(new FileWriter("tokens_output.txt"));

            if (args.length > 0) {
                // Leer desde archivo
                System.out.println("=".repeat(50));
                System.out.println("Probando lexer con archivo: " + args[0]);
                System.out.println("=".repeat(50));
                fileWriter.println("=".repeat(50));
                fileWriter.println("Análisis léxico de archivo: " + args[0]);
                fileWriter.println("=".repeat(50));

                FileReader fileReader = new FileReader(args[0]);
                lexer = new Lexer(fileReader);
            } else {
                // Leer desde stdin
                System.out.println("Ingrese el código (Ctrl+D para terminar):");
                fileWriter.println("Análisis léxico desde entrada estándar");
                fileWriter.println("=".repeat(50));

                lexer = new Lexer(new InputStreamReader(System.in));
            }

            Symbol token;
            int tokenCount = 0;

            // Imprimir encabezado en consola y archivo
            String header = String.format("%-6s %-20s %-10s %-10s %s",
                "NUM", "TOKEN", "LINEA", "COLUMNA", "LEXEMA");
            String separator = "-".repeat(70);

            System.out.println(header);
            System.out.println(separator);
            fileWriter.println(header);
            fileWriter.println(separator);

            while (true) {
                token = lexer.next_token();

                // Si llegamos al EOF, terminamos
                if (token.sym == sym.EOF) {
                    break;
                }

                tokenCount++;
                String tokenName = getTokenName(token.sym);
                String lexeme = token.value != null ? token.value.toString() : "";

                String tokenLine = String.format("%-6d %-20s %-10d %-10d %s",
                    tokenCount,
                    tokenName,
                    token.left,
                    token.right,
                    lexeme);

                // Escribir en consola y archivo
                System.out.println(tokenLine);
                fileWriter.println(tokenLine);
            }

            // Imprimir resumen en consola y archivo
            System.out.println(separator);
            System.out.println("✓ Total de tokens: " + tokenCount);
            System.out.println("✓ Análisis léxico completado exitosamente");
            System.out.println("✓ Tokens guardados en: tokens_output.txt");

            fileWriter.println(separator);
            fileWriter.println("Total de tokens: " + tokenCount);
            fileWriter.println("Análisis completado exitosamente");

        } catch (FileNotFoundException e) {
            System.err.println("✗ Error: Archivo no encontrado - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("✗ Error de lectura/escritura: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Error durante el análisis léxico:");
            e.printStackTrace();
        } finally {
            // Cerrar el archivo de salida
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
    
    /**
     * Convierte el código del símbolo a su nombre legible
     */
    private static String getTokenName(int symCode) {
        try {
            // Usar reflexión para obtener el nombre del token desde la clase sym
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
