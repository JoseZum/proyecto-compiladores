import java.util.*;
import java.io.*;

/**
 * Traductor de Código de Tres Direcciones (C3D) a MIPS.
 * Esta versión contiene la estructura completa (esqueleto) para todas
 * las instrucciones posibles de la gramática.
 */
public class traductor {
    private String c3d;
    private StringBuilder mips;
    private HashSet<String> variables;
    private Map<String, String> strings;
    private int stringCount = 0;

    public traductor(String c3d) {
        this.c3d = c3d;
        this.mips = new StringBuilder();
        this.variables = new HashSet<>();
        this.strings = new HashMap<>();
    }

    public String traducir() {
        mips.append("# --- CÓDIGO MIPS GENERADO ---\n\n");
        preProcesar();
        generarDataSection();
        generarTextSection();
        return mips.toString();
    }

    /**
     * Identifica variables y strings antes de generar código.
     */
    private void preProcesar() {
        String[] lineas = c3d.split("\n");
        for (String linea : lineas) {
            linea = linea.trim();
            // Normalizar comillas dobles si el C3D las generó duplicadas
            linea = linea.replace("\"\"", "\"");

            if (linea.isEmpty() || linea.startsWith("#"))
                continue;

            // Detección de strings para .data
            if (linea.contains("\"")) {
                String content = extraerString(linea);
                if (content != null && !strings.containsKey(content)) {
                    strings.put(content, "str" + (++stringCount));
                }
            }

            // Detección de variables y temporales
            String sinStrings = linea.replaceAll("\".*?\"", " ");
            String[] tokens = sinStrings.split("[\\s\\+\\-\\*/()=,]+");
            for (String token : tokens) {
                if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !isReserved(token)) {
                    variables.add(token);
                }
            }
        }
    }

    private String extraerString(String linea) {
        int first = linea.indexOf("\"");
        int last = linea.lastIndexOf("\"");
        if (first != -1 && last > first) {
            String str = linea.substring(first, last + 1);
            return str.replaceAll("^\"+", "").replaceAll("\"+$", "");
        }
        return null;
    }

    private boolean isReserved(String t) {
        return t.equals("goto") || t.equals("if") || t.equals("ifFalse") ||
                t.equals("call") || t.equals("param") || t.equals("return") ||
                t.equals("print") || t.equals("read");
    }

    /**
     * SECCIÓN .DATA: Declaración de variables y constantes.
     */
    private void generarDataSection() {
        mips.append(".data\n");
        mips.append("    newline: .asciiz \"\\n\"\n");

        mips.append("    # --- Strings Literales ---\n");
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            mips.append("    ").append(entry.getValue()).append(": .asciiz \"")
                    .append(entry.getKey()).append("\"\n");
        }

        mips.append("    # --- Variables y Temporales ---\n");
        for (String var : variables) {
            mips.append("    v_").append(var).append(": .word 0\n");
        }
        mips.append("\n");
    }

    /**
     * SECCIÓN .TEXT: Instrucciones del programa y Macros.
     */
    private void generarTextSection() {
        mips.append(".text\n");

        mips.append(".globl main\n\n");
        mips.append("main:\n");

        String[] lineas = c3d.split("\n");
        for (String linea : lineas) {
            procesarLinea(linea.trim());
        }

        mips.append("\n    # Fin del programa (Exit)\n");
        mips.append("    li $v0, 10\n");
        mips.append("    syscall\n");
        generarRuntime();
    }

    private void generarRuntime() {
        mips.append("\n# --- RUTINAS DE SISTEMA ---\n");
        mips.append("showInt:\n    li $v0, 1\n    syscall\n    jr $ra\n.end showInt\n\n");
        mips.append("showString:\n    li $v0, 4\n    syscall\n    jr $ra\n.end showString\n\n");
        mips.append("showFloat:\n    li $v0, 2\n    syscall\n    jr $ra\n.end showFloat\n\n");
        mips.append("showChar:\n    li $v0, 11\n    syscall\n    jr $ra\n.end showChar\n\n");

        mips.append("readInt:\n    li $v0, 5\n    syscall\n    jr $ra\n.end readInt\n\n");
        mips.append("readFloat:\n    li $v0, 6\n    syscall\n    jr $ra\n.end readFloat\n\n");
        mips.append("readString:\n    li $v0, 8\n    syscall\n    jr $ra\n.end readString\n\n");
        mips.append("readChar:\n    li $v0, 12\n    syscall\n    jr $ra\n.end readChar\n\n");

        mips.append("pow:\n");
        mips.append("    li $v0, 1\n");
        mips.append("_pow_loop:\n");
        mips.append("    blez $a1, _pow_end\n");
        mips.append("    mul $v0, $v0, $a0\n");
        mips.append("    sub $a1, $a1, 1\n");
        mips.append("    j _pow_loop\n");
        mips.append("_pow_end:\n    jr $ra\n.end pow\n\n");
    }

    /**
     * PROCESADOR DE LÍNEAS (El "Case" principal)
     */
    private void procesarLinea(String linea) {
        if (linea.isEmpty())
            return;

        // MANEJO DE FUNCIONES (Basado en comentarios del C3D)
        if (linea.startsWith("# FUNC ")) {
            mips.append("    ").append(linea).append("\n");
            mips.append("    # Reserva de Frame y guardado de $ra\n");
            mips.append("    subu $sp, $sp, 4\n");
            mips.append("    sw $ra, ($sp)\n");
            return;
        }

        if (linea.startsWith("#")) {
            mips.append("    ").append(linea).append("\n");
            return;
        }

        // 1. ETIQUETAS (L1:, func:)
        if (linea.endsWith(":")) {
            if (!linea.equals("navidad:") && !linea.equals("main:")) {
                mips.append(linea).append("\n");
            }
            return;
        }

        // 2. ASIGNACIONES (t1 = a + b o t1 = call func)
        if (linea.contains("=")) {
            traducirAsignacion(linea);
            return;
        }

        String[] tokens = linea.split("\\s+");
        String cmd = tokens[0];

        // 3. EL "CASE" PARA EL RESTO DE INSTRUCCIONES
        switch (cmd) {
            case "goto":
                traducirGoto(linea.substring(5).trim());
                break;
            case "if":
                traducirIf(linea);
                break;
            case "ifFalse":
                traducirIfFalse(linea);
                break;
            case "print":
                traducirPrintInt(linea.substring(6).trim());
                break;
            case "print_int":
                traducirPrintInt(linea.substring(10).trim());
                break;
            case "print_float":
                traducirPrintFloat(linea.substring(12).trim());
                break;
            case "print_char":
                traducirPrintChar(linea.substring(11).trim());
                break;
            case "print_string":
                traducirPrintString(linea.substring(13).trim());
                break;
            case "read":
            case "read_int":
                traducirReadInt(linea.substring(cmd.length()).trim());
                break;
            case "read_float":
                traducirReadFloat(linea.substring(11).trim());
                break;
            case "read_char":
                traducirReadChar(linea.substring(10).trim());
                break;
            case "read_string":
                traducirReadString(linea.substring(12).trim());
                break;
            case "param":
                traducirParam(linea.substring(6).trim());
                break;
            case "call":
                traducirCall(linea);
                break;
            case "return":
                String retVal = linea.length() > 6 ? linea.substring(7).trim() : null;
                traducirReturn(retVal);
                break;
            default:
                mips.append("    #ERROR. No se pudo traducir la linea ").append(linea).append("\n");
                break;
        }
    }

    // --- MÉTODOS DE TRADUCCIÓN (SKELETON) ---

    private void traducirAsignacion(String linea) {
        // Si es una asignación producto de una llamada: t1 = call func
        if (linea.contains("call ")) {
            traducirCall(linea);
            return;
        }

        String[] partes = linea.split("=");
        String res = partes[0].trim();
        String expr = partes[1].trim();
        String[] tokens = expr.split("\\s+");

        if (tokens.length == 1) {
            mips.append("    # Asignación simple: ").append(res).append(" = ").append(tokens[0]).append("\n");
            cargarEnRegistro("$t0", tokens[0]);
            mips.append("    sw $t0, v_").append(res).append("\n");
        } else if (tokens.length == 3) {
            String op = tokens[1];
            mips.append("    # Operación: ").append(linea).append("\n");
            cargarEnRegistro("$t0", tokens[0]);
            cargarEnRegistro("$t1", tokens[2]);

            // Case para cada operador
            switch (op) {
                case "+":
                    mips.append("    add $t2, $t0, $t1\n");
                    break;
                case "-":
                    mips.append("    sub $t2, $t0, $t1\n");
                    break;
                case "*":
                    mips.append("    mul $t2, $t0, $t1\n");
                    break;
                case "/":
                case "//":
                    mips.append("    div $t2, $t0, $t1\n");
                    break;
                case "%":
                    mips.append("    rem $t2, $t0, $t1\n");
                    break;
                case "==":
                    mips.append("    seq $t2, $t0, $t1\n");
                    break;
                case "!=":
                    mips.append("    sne $t2, $t0, $t1\n");
                    break;
                case ">":
                    mips.append("    sgt $t2, $t0, $t1\n");
                    break;
                case "<":
                    mips.append("    slt $t2, $t0, $t1\n");
                    break;
                case ">=":
                    mips.append("    sge $t2, $t0, $t1\n");
                    break;
                case "<=":
                    mips.append("    sle $t2, $t0, $t1\n");
                    break;
                case "AND":
                    mips.append("    and $t2, $t0, $t1\n");
                    break;
                case "OR":
                    mips.append("    or $t2, $t0, $t1\n");
                    break;
                case "^":
                    mips.append("    # Potencia usando subrutina\n");
                    mips.append("    move $a0, $t0\n");
                    mips.append("    move $a1, $t1\n");
                    mips.append("    jal _pow\n");
                    mips.append("    move $t2, $v0\n");
                    break;
            }
            mips.append("    sw $t2, v_").append(res).append("\n");
        }
    }

    private void traducirGoto(String label) {
        mips.append("    # Salto incondicional\n");
        mips.append("    j ").append(label).append("\n");
    }

    private void traducirIf(String linea) {
        mips.append("    # Salto condicional: ").append(linea).append("\n");
        // Formato: if cond goto Label
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        mips.append("    bnez $t0, ").append(label).append("\n");
    }

    private void traducirIfFalse(String linea) {
        mips.append("    # Salto condicional inverso: ").append(linea).append("\n");
        // Formato: ifFalse cond goto Label
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        mips.append("    beqz $t0, ").append(label).append("\n");
    }

    // --- MÉTODOS PRINT ---

    private void traducirPrintInt(String val) {
        mips.append("    # Imprimir Int: ").append(val).append("\n");
        cargarEnRegistro("$a0", val);
        mips.append("    jal showInt\n");
        printNewline();
    }

    private void traducirPrintFloat(String val) {
        mips.append("    # Imprimir Float: ").append(val).append("\n");
        if (val.matches("-?\\d+\\.\\d+")) {
            mips.append("    li.s $f12, ").append(val).append("\n");
        } else {
            // Cargar variable float
            mips.append("    l.s $f12, v_").append(val).append("\n");
        }
        mips.append("    jal showFloat\n");
        printNewline();
    }

    private void traducirPrintString(String val) {
        mips.append("    # Imprimir String: ").append(val).append("\n");
        if (val.startsWith("\"")) {
            String content = extraerString(val);
            String label = strings.get(content);
            mips.append("    la $a0, ").append(label).append("\n");
        } else {
            // Si fuera variable string (puntero)
            mips.append("    lw $a0, v_").append(val).append("\n");
        }
        mips.append("    jal showString\n");
        printNewline();
    }

    private void traducirPrintChar(String val) {
        mips.append("    # Imprimir Char: ").append(val).append("\n");
        if (val.startsWith("'")) {
            mips.append("    li $a0, ").append(val).append("\n");
        } else {
            mips.append("    lw $a0, v_").append(val).append("\n");
        }
        mips.append("    jal showChar\n");
        printNewline();
    }

    private void printNewline() {
        mips.append("    la $a0, newline\n");
        mips.append("    jal showString\n");
    }

    // --- MÉTODOS READ ---

    private void traducirReadInt(String var) {
        mips.append("    # Leer Int: ").append(var).append("\n");
        mips.append("    jal readInt\n");
        mips.append("    sw $v0, v_").append(var).append("\n");
    }

    private void traducirReadFloat(String var) {
        mips.append("    # Leer Float: ").append(var).append("\n");
        mips.append("    jal readFloat\n");
        mips.append("    s.s $f0, v_").append(var).append("\n");
    }

    private void traducirReadChar(String var) {
        mips.append("    # Leer Char: ").append(var).append("\n");
        mips.append("    jal readChar\n");
        mips.append("    sw $v0, v_").append(var).append("\n");
    }

    private void traducirReadString(String var) {
        // Para strings es más complejo (buffer), pero dejaremos el placeholder
        mips.append("    # Leer String (Not fully supported): ").append(var).append("\n");
        mips.append("    jal readString\n");
    }

    private void traducirParam(String val) {
        mips.append("    # Parámetro de función: ").append(val).append("\n");
        cargarEnRegistro("$t0", val);
        mips.append("    subu $sp, $sp, 4\n");
        mips.append("    sw $t0, ($sp)\n");
    }

    private void traducirCall(String linea) {
        mips.append("    # Llamada a función: ").append(linea).append("\n");
        // Formato: [t1 =] call func, n
        String func;
        if (linea.contains("=")) {
            String expr = linea.split("=")[1].trim();
            func = expr.split("\\s+")[1].replace(",", "");
        } else {
            func = linea.split("\\s+")[1].replace(",", "");
        }

        mips.append("    jal ").append(func).append("\n");

        // Limpieza de parámetros (suponiendo n parámetros del C3D, n se extrae si está
        // disponible)
        if (linea.contains(",")) {
            String[] callParts = linea.split(",");
            try {
                int nParams = Integer.parseInt(callParts[1].trim());
                if (nParams > 0) {
                    mips.append("    addu $sp, $sp, ").append(nParams * 4).append(" # Limpiar ").append(nParams)
                            .append(" params\n");
                }
            } catch (Exception e) {
                /* ignore */ }
        }

        // Si hay retorno, mover $v0 al temporal
        if (linea.contains("=")) {
            String res = linea.split("=")[0].trim();
            mips.append("    sw $v0, v_").append(res).append("\n");
        }
    }

    private void traducirReturn(String val) {
        mips.append("    # Retorno de función\n");
        if (val != null) {
            cargarEnRegistro("$v0", val); // Poner valor de retorno en $v0
        }
        mips.append("    # Restaurar $ra y Frame\n");
        mips.append("    lw $ra, ($sp)\n");
        mips.append("    addu $sp, $sp, 4\n");
        mips.append("    jr $ra\n");
    }

    /**
     * Helper para cargar un valor (entero o variable) en un registro.
     */
    private void cargarEnRegistro(String reg, String val) {
        if (val.matches("-?\\d+")) {
            mips.append("    li ").append(reg).append(", ").append(val).append("\n");
        } else {
            mips.append("    lw ").append(reg).append(", v_").append(val).append("\n");
        }
    }
}
