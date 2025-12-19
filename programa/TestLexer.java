import java.io.*;
import java_cup.runtime.*;

/**
 * Programa para probar el lexer de forma independiente
 * Imprime todos los tokens encontrados en el archivo de entrada
 */
public class TestLexer {
    public static void main(String[] args) {
        try {
            Lexer lexer;
            
            if (args.length > 0) {
                // Leer desde archivo
                System.out.println("=".repeat(50));
                System.out.println("Probando lexer con archivo: " + args[0]);
                System.out.println("=".repeat(50));
                FileReader fileReader = new FileReader(args[0]);
                lexer = new Lexer(fileReader);
            } else {
                // Leer desde stdin
                System.out.println("Ingrese el código (Ctrl+D para terminar):");
                lexer = new Lexer(new InputStreamReader(System.in));
            }
            
            Symbol token;
            int tokenCount = 0;
            
            System.out.printf("%-6s %-20s %-10s %-10s %s%n", 
                "NUM", "TOKEN", "LINEA", "COLUMNA", "LEXEMA");
            System.out.println("-".repeat(70));
            
            while (true) {
                token = lexer.next_token();
                
                // Si llegamos al EOF, terminamos
                if (token.sym == sym.EOF) {
                    break;
                }
                
                tokenCount++;
                String tokenName = getTokenName(token.sym);
                String lexeme = token.value != null ? token.value.toString() : "";
                
                System.out.printf("%-6d %-20s %-10d %-10d %s%n",
                    tokenCount,
                    tokenName,
                    token.left,
                    token.right,
                    lexeme);
            }
            
            System.out.println("-".repeat(70));
            System.out.println("✓ Total de tokens: " + tokenCount);
            System.out.println("✓ Análisis léxico completado exitosamente");
            
        } catch (FileNotFoundException e) {
            System.err.println("✗ Error: Archivo no encontrado - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("✗ Error de lectura: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Error durante el análisis léxico:");
            e.printStackTrace();
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
