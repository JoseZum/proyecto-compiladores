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
    private Map<String, Integer> arrayCols; // Para guardar la dimension 2 (columnas)
    private Map<String, Integer> arraySizes; // Para guardar el tamaño total en bytes

    private StringBuilder mainMips;
    private StringBuilder funcMips;
    private boolean inFunction = false;
    private boolean pendingPrologue = false;

    public traductor(String c3d) {
        this.c3d = c3d;
        this.mips = new StringBuilder();
        this.mainMips = new StringBuilder();
        this.funcMips = new StringBuilder();
        this.variables = new HashSet<>();
        this.strings = new HashMap<>();
        this.arrayCols = new HashMap<>();
        this.arraySizes = new HashMap<>();
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

            // Detección de arrays: array name, d1, d2
            if (linea.startsWith("array ")) {
                // array name, d1, d2;
                String[] parts = linea.substring(6).split(",");
                String name = parts[0].trim();
                int d1 = Integer.parseInt(parts[1].trim());
                int d2 = Integer.parseInt(parts[2].trim());
                arrayCols.put(name, d2);
                arraySizes.put(name, d1 * d2 * 4); // 4 bytes por int
                continue; // No procesar como variable normal, pero continuar con otras líneas
            }

            // Detección de variables y temporales
            String sinStrings = linea.replaceAll("\".*?\"", " ");
            // Split on operators, brackets, parentheses, etc.
            String[] tokens = sinStrings.split("[\\s\\+\\-\\*/()=,\\[\\]]+");
            for (String token : tokens) {
                if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !isReserved(token) && !arraySizes.containsKey(token)) {
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
                t.equals("print") || t.equals("read") || t.equals("read_int") ||
                t.equals("read_float") || t.equals("read_char") || t.equals("read_string") ||
                t.equals("print_int") || t.equals("print_float") || t.equals("print_char") ||
                t.equals("print_string") || t.equals("array") || t.equals("to");
    }

    /**
     * Asegura que una variable esté registrada en el conjunto de variables.
     * Esto es necesario para variables que se generan dinámicamente durante la
     * traducción.
     */
    private void ensureVariableExists(String varName) {
        if (varName != null && !varName.isEmpty() &&
                varName.matches("[a-zA-Z_][a-zA-Z0-9_]*") &&
                !isReserved(varName) &&
                !arraySizes.containsKey(varName)) {
            variables.add(varName);
        }
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

        // Alinear a 4 bytes antes de arrays
        mips.append("    .align 2\n");
        mips.append("    # --- Arrays ---\n");
        for (Map.Entry<String, Integer> entry : arraySizes.entrySet()) {
            mips.append("    v_").append(entry.getKey()).append(": .space ").append(entry.getValue()).append("\n");
        }

        // Alinear a 4 bytes antes de variables
        mips.append("    .align 2\n");
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
        // Removed explicit jump to navidad to allow global initialization instructions
        // to run first
        // Flow will fall through to navidad: label naturally

        String[] lineas = c3d.split("\n");
        for (String linea : lineas) {
            procesarLinea(linea.trim());
        }

        // Agregar primero el código del main (navidad)
        mips.append(mainMips);

        mips.append("\n    # Fin del programa (Exit)\n");
        mips.append("    li $v0, 10\n");
        mips.append("    syscall\n\n");

        // Luego agregar las funciones
        mips.append(funcMips);

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

        // Detectar cambio de contexto (Main vs Función)
        if (linea.startsWith("# FUNC ")) {
            inFunction = true;
        } else if (linea.startsWith("# MAIN ")) {
            inFunction = false;
        }

        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        // MANEJO DE FUNCIONES (Basado en comentarios del C3D)
        if (linea.startsWith("# FUNC ")) {
            currentBuffer.append("    ").append(linea).append("\n");
            pendingPrologue = true; // Fix: Postpone prologue until after label
            return;
        }

        if (linea.startsWith("#")) {
            currentBuffer.append("    ").append(linea).append("\n");
            // Si termina una funcion, salir del modo funcion
            if (linea.startsWith("# END FUNC")) {
                inFunction = false;
            }
            // Fix: Add exit system call at end of main to prevent fallthrough
            if (linea.startsWith("# END MAIN")) {
                currentBuffer.append("    li $v0, 10\n");
                currentBuffer.append("    syscall\n");
            }
            return;
        }

        // 1. ETIQUETAS (L1:, func:)
        if (linea.endsWith(":")) {
            if (!linea.equals("main:")) {
                currentBuffer.append(linea).append("\n");

                // Fix: Emit prologue AFTER the label
                if (pendingPrologue && inFunction) {
                    currentBuffer.append("    # Reserva de Frame y guardado de $ra\n");
                    currentBuffer.append("    subu $sp, $sp, 4\n");
                    currentBuffer.append("    sw $ra, ($sp)\n");
                    pendingPrologue = false;
                }
            }
            return;
        }

        String[] tokens = linea.split("\\s+");
        String cmd = tokens[0];

        // 2. EL "CASE" PARA INSTRUCCIONES (check print/read BEFORE assignments)
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
            case "array":
                // No hace nada en runtime
                break;
            case "get_stack":
                traducirGetStack(linea.substring(10).trim());
                break;
            case "save_local":
                traducirSaveLocal(linea.substring(11).trim());
                break;
            case "restore_local":
                traducirRestoreLocal(linea.substring(14).trim());
                break;
            default:
                // 3. ASIGNACIONES (t1 = a + b o t1 = call func)
                if (linea.contains("=")) {
                    traducirAsignacion(linea);
                } else {
                    mips.append("    #ERROR. No se pudo traducir la linea ").append(linea).append("\n");
                }
                break;
        }
    }

    // --- MÉTODOS DE TRADUCCIÓN (SKELETON) ---

    private void traducirAsignacion(String linea) {
        // Asignación simple o compleja
        // Usar los buffers correctos
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        // Si es una asignación producto de una llamada: t1 = call func
        if (linea.contains("call ")) {
            traducirCall(linea);
            return;
        }

        // --- Chequeo de arreglos en asignacion (arr[i][j] = val) o lectura (t =
        // arr[i][j])
        if (linea.contains("[")) {
            traducirArreglo(linea);
            return;
        }

        String[] partes = linea.split("=", 2);
        String res = partes[0].trim();
        String expr = partes[1].trim();

        // Handle String literal assignment (e.g. s = "Hola Mundo")
        if (expr.startsWith("\"") || expr.startsWith("'")) {
            currentBuffer.append("    # Asignación literal: ").append(res).append(" = ").append(expr).append("\n");
            cargarEnRegistro("$t0", expr);
            currentBuffer.append("    sw $t0, v_").append(res).append("\n");
            return;
        }

        String[] tokens = expr.split("\\s+");

        if (tokens.length == 1) {
            // Check for cast: t1 = (float) t2
            if (tokens[0].equals("(float)")) {
                // Format: res = (float) val
                // tokens: ["(float)", "val"] (actually expr split by space might behave
                // differently)
            }
            // Better parsing for cast in top level
        }

        // Re-parsing to handle (float) logic cleanly
        if (expr.startsWith("(float) ")) {
            String val = expr.substring(8).trim();
            traducirCastFloat(res, val);
            return;
        }

        if (tokens.length == 1) {
            String val = tokens[0];
            // Fix: Check if value is a float literal to use correct instructions
            // Pattern handles standard floats like 1.5, -0.5, 2.0
            if (val.matches("-?\\d+\\.\\d+")) {
                currentBuffer.append("    # Asignación float literal: ").append(res).append(" = ").append(val)
                        .append("\n");
                currentBuffer.append("    li.s $f0, ").append(val).append("\n");
                currentBuffer.append("    s.s $f0, v_").append(res).append("\n");
            } else {
                currentBuffer.append("    # Asignación simple: ").append(res).append(" = ").append(val).append("\n");
                cargarEnRegistro("$t0", val);
                currentBuffer.append("    sw $t0, v_").append(res).append("\n");
            }
        } else if (tokens.length == 3) {
            String op = tokens[1];
            currentBuffer.append("    # Operación: ").append(linea).append("\n");

            // Check if float operation
            if (op.endsWith("f")) {
                cargarEnRegistroFloat("$f0", tokens[0]);
                cargarEnRegistroFloat("$f1", tokens[2]);

                switch (op) {
                    case "+f":
                        currentBuffer.append("    add.s $f2, $f0, $f1\n");
                        break;
                    case "-f":
                        currentBuffer.append("    sub.s $f2, $f0, $f1\n");
                        break;
                    case "*f":
                        currentBuffer.append("    mul.s $f2, $f0, $f1\n");
                        break;
                    case "/f":
                        currentBuffer.append("    div.s $f2, $f0, $f1\n");
                        break;
                }
                currentBuffer.append("    s.s $f2, v_").append(res).append("\n");
                return;
            }

            cargarEnRegistro("$t0", tokens[0]);
            cargarEnRegistro("$t1", tokens[2]);

            // Case para cada operador
            switch (op) {
                case "+":
                    currentBuffer.append("    add $t2, $t0, $t1\n");
                    break;
                case "-":
                    currentBuffer.append("    sub $t2, $t0, $t1\n");
                    break;
                case "*":
                    currentBuffer.append("    mul $t2, $t0, $t1\n");
                    break;
                case "/":
                case "//":
                    currentBuffer.append("    div $t2, $t0, $t1\n");
                    break;
                case "%":
                    currentBuffer.append("    rem $t2, $t0, $t1\n");
                    break;
                case "==":
                    currentBuffer.append("    seq $t2, $t0, $t1\n");
                    break;
                case "!=":
                    currentBuffer.append("    sne $t2, $t0, $t1\n");
                    break;
                case ">":
                    currentBuffer.append("    sgt $t2, $t0, $t1\n");
                    break;
                case "<":
                    currentBuffer.append("    slt $t2, $t0, $t1\n");
                    break;
                case ">=":
                    currentBuffer.append("    sge $t2, $t0, $t1\n");
                    break;
                case "<=":
                    currentBuffer.append("    sle $t2, $t0, $t1\n");
                    break;
                case "and":
                    currentBuffer.append("    and $t2, $t0, $t1\n");
                    break;
                case "or":
                    currentBuffer.append("    or $t2, $t0, $t1\n");
                    break;
                case "^":
                    currentBuffer.append("    # Potencia usando subrutina\n");
                    currentBuffer.append("    move $a0, $t0\n");
                    currentBuffer.append("    move $a1, $t1\n");
                    currentBuffer.append("    jal _pow\n");
                    currentBuffer.append("    move $t2, $v0\n");
                    break;
            }
            currentBuffer.append("    sw $t2, v_").append(res).append("\n");
        } else if (tokens.length == 2) {
            // Operadores unarios (ej: not)
            String op = tokens[0];
            String val = tokens[1];
            if (op.equals("not")) {
                currentBuffer.append("    # Operacion Not: ").append(linea).append("\n");
                cargarEnRegistro("$t0", val);
                currentBuffer.append("    seq $t0, $t0, $zero\n");
                currentBuffer.append("    sw $t0, v_").append(res).append("\n");
            }
        }
    }

    private void traducirGoto(String label) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Salto incondicional\n");
        currentBuffer.append("    j ").append(label).append("\n");
    }

    private void traducirIf(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Salto condicional: ").append(linea).append("\n");
        // Formato: if cond goto Label
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        currentBuffer.append("    bnez $t0, ").append(label).append("\n");
    }

    private void traducirIfFalse(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Salto condicional inverso: ").append(linea).append("\n");
        // Formato: ifFalse cond goto Label
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        currentBuffer.append("    beqz $t0, ").append(label).append("\n");
    }

    // --- MÉTODOS PRINT ---

    private void traducirPrintInt(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Imprimir Int: ").append(val).append("\n");
        cargarEnRegistro("$a0", val);
        currentBuffer.append("    jal showInt\n");
        printNewline();
    }

    private void traducirPrintFloat(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Imprimir Float: ").append(val).append("\n");
        if (val.matches("-?\\d+\\.\\d+")) {
            currentBuffer.append("    li.s $f12, ").append(val).append("\n");
        } else {
            // Cargar variable float
            currentBuffer.append("    l.s $f12, v_").append(val).append("\n");
        }
        currentBuffer.append("    jal showFloat\n");
        printNewline();
    }

    private void traducirPrintString(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Imprimir String: ").append(val).append("\n");
        if (val.startsWith("\"")) {
            String content = extraerString(val);
            String label = strings.get(content);
            currentBuffer.append("    la $a0, ").append(label).append("\n");
        } else {
            // Si fuera variable string (puntero)
            currentBuffer.append("    lw $a0, v_").append(val).append("\n");
        }
        currentBuffer.append("    jal showString\n");
        printNewline();
    }

    private void traducirPrintChar(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Imprimir Char: ").append(val).append("\n");
        if (val.startsWith("'")) {
            currentBuffer.append("    li $a0, ").append(val).append("\n");
        } else {
            currentBuffer.append("    lw $a0, v_").append(val).append("\n");
        }
        currentBuffer.append("    jal showChar\n");
        printNewline();
    }

    private void printNewline() {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    la $a0, newline\n");
        currentBuffer.append("    jal showString\n");
    }

    // --- MÉTODOS READ ---

    private void traducirReadInt(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Leer Int: ").append(var).append("\n");
        currentBuffer.append("    jal readInt\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("\n");
    }

    private void traducirReadFloat(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Leer Float: ").append(var).append("\n");
        currentBuffer.append("    jal readFloat\n");
        currentBuffer.append("    s.s $f0, v_").append(var).append("\n");
    }

    private void traducirReadChar(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Leer Char: ").append(var).append("\n");
        currentBuffer.append("    jal readChar\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("\n");
    }

    private void traducirReadString(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        // Para strings es más complejo (buffer), pero dejaremos el placeholder
        currentBuffer.append("    # Leer String (Not fully supported): ").append(var).append("\n");
        currentBuffer.append("    jal readString\n");
    }

    private void traducirGetStack(String args) {
        // args: variable, offset
        String[] parts = args.split(",");
        String var = parts[0].trim();
        String offset = parts[1].trim();

        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Cargar parametro dsd pila: ").append(var).append(" <- ").append(offset)
                .append("($sp)\n");
        currentBuffer.append("    lw $t0, ").append(offset).append("($sp)\n");
        currentBuffer.append("    sw $t0, v_").append(var).append("\n");
    }

    private void traducirParam(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Parámetro de función: ").append(val).append("\n");
        cargarEnRegistro("$t0", val);
        currentBuffer.append("    subu $sp, $sp, 4\n");
        currentBuffer.append("    sw $t0, ($sp)\n");
    }

    private void traducirSaveLocal(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Guardar local en pila: ").append(var).append("\n");
        currentBuffer.append("    lw $t0, v_").append(var).append("\n");
        currentBuffer.append("    subu $sp, $sp, 4\n");
        currentBuffer.append("    sw $t0, ($sp)\n");
    }

    private void traducirRestoreLocal(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Restaurar local de pila: ").append(var).append("\n");
        currentBuffer.append("    lw $t0, ($sp)\n");
        currentBuffer.append("    addu $sp, $sp, 4\n");
        currentBuffer.append("    sw $t0, v_").append(var).append("\n");
    }

    private void traducirCall(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Llamada a función: ").append(linea).append("\n");
        // Formato: [t1 =] call func, n
        String func;
        if (linea.contains("=")) {
            String expr = linea.split("=")[1].trim();
            func = expr.split("\\s+")[1].replace(",", "");
        } else {
            func = linea.split("\\s+")[1].replace(",", "");
        }

        currentBuffer.append("    jal ").append(func).append("\n");

        // Limpieza de parámetros (suponiendo n parámetros del C3D)
        if (linea.contains(",")) {
            String[] callParts = linea.split(",");
            try {
                int nParams = Integer.parseInt(callParts[1].trim());
                if (nParams > 0) {
                    currentBuffer.append("    addu $sp, $sp, ").append(nParams * 4).append(" # Limpiar ")
                            .append(nParams)
                            .append(" params\n");
                }
            } catch (Exception e) {
                /* ignore */ }
        }

        // Si hay retorno, mover $v0 al temporal
        if (linea.contains("=")) {
            String res = linea.split("=")[0].trim();
            currentBuffer.append("    sw $v0, v_").append(res).append("\n");
        }
    }

    private void traducirReturn(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Retorno de función\n");
        if (val != null) {
            cargarEnRegistro("$v0", val); // Poner valor de retorno en $v0
        }
        currentBuffer.append("    # Restaurar $ra y Frame\n");
        currentBuffer.append("    lw $ra, ($sp)\n");
        currentBuffer.append("    addu $sp, $sp, 4\n");
        currentBuffer.append("    jr $ra\n");
    }

    /**
     * Helper para cargar un valor (entero o variable) en un registro.
     */
    private void cargarEnRegistro(String reg, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        if (val.matches("-?\\d+")) {
            currentBuffer.append("    li ").append(reg).append(", ").append(val).append("\n");
        } else if (val.equals("true")) {
            currentBuffer.append("    li ").append(reg).append(", 1\n");
        } else if (val.equals("false")) {
            currentBuffer.append("    li ").append(reg).append(", 0\n");
        } else if (val.startsWith("'")) {
            // Char literal 'A'
            char c = val.charAt(1);
            currentBuffer.append("    li ").append(reg).append(", ").append((int) c).append("\n");
        } else if (val.startsWith("\"")) {
            // String literal "Hola" -> Load address
            String content = extraerString(val);
            String label = strings.get(content);
            if (label != null) {
                currentBuffer.append("    la ").append(reg).append(", ").append(label).append("\n");
            } else {
                // Fallback if string not found (shouldn't happen if preprocessed)
                currentBuffer.append("    # Error: String literal not found in .data\n");
            }
        } else {
            currentBuffer.append("    lw ").append(reg).append(", v_").append(val).append("\n");
        }
    }

    // --- MANEJO DE ARREGLOS ---

    private void traducirArreglo(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        // Dos casos:
        // 1. Asignacion a arreglo: arr[i][j] = val
        // 2. Lectura de arreglo: t1 = arr[i][j]

        // Parsear: arrId[i][j]
        // Buscar el primer '['
        int b1 = linea.indexOf("[");
        if (b1 == -1)
            return;

        // Si empieza con una variable y luego =, es lectura (t1 = ...)
        // Si empieza con el ID del arreglo (antes del [), es asignacion

        String preBracket = linea.substring(0, b1).trim();
        boolean esAsignacionAArreglo = !preBracket.contains("=");

        if (esAsignacionAArreglo) {
            // Caso: arr[i][j] = val
            String[] parts = linea.split("=");
            String val = parts[1].trim();
            String leftSide = parts[0].trim();

            procesarDireccionArreglo(leftSide, "$t3"); // $t3 tendrá la dirección

            cargarEnRegistro("$t0", val);
            currentBuffer.append("    sw $t0, ($t3)\n");

        } else {
            // Caso: t1 = arr[i][j]
            String[] parts = linea.split("=");
            String dest = parts[0].trim();
            String rightSide = parts[1].trim();

            procesarDireccionArreglo(rightSide, "$t3"); // $t3 tendrá la dirección

            currentBuffer.append("    lw $t0, ($t3)\n");
            currentBuffer.append("    sw $t0, v_").append(dest).append("\n");
        }
    }

    // Calcula la dirección del elemento arr[i][j] y la pone en regDest
    private void procesarDireccionArreglo(String acceso, String regDest) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        // acceso tiene la forma: arr[i][j]
        // extraer arr, i, j
        int b1 = acceso.indexOf("[");
        String arrName = acceso.substring(0, b1).trim();

        // Obtener indices. Parseo simplificado asumiendo formato fijo arr[i][j]
        String resto = acceso.substring(b1); // [i][j]
        String[] indices = resto.replace("][", ",").replace("[", "").replace("]", "").split(",");
        String i = indices[0].trim();
        String j = indices[1].trim();

        int cols = arrayCols.getOrDefault(arrName, 0);

        // Calcular offset = (i * cols + j) * 4

        // Cargar i en $t1
        cargarEnRegistro("$t1", i);
        // Cargar col en $t2
        currentBuffer.append("    li $t2, ").append(cols).append("\n");
        // $t1 = i * cols
        currentBuffer.append("    mul $t1, $t1, $t2\n");

        // Cargar j en $t2
        cargarEnRegistro("$t2", j);
        // $t1 = (i*cols) + j
        currentBuffer.append("    add $t1, $t1, $t2\n");

        // $t1 = $t1 * 4 (offset en bytes)
        currentBuffer.append("    mul $t1, $t1, 4\n");

        // Cargar direccion base de arr en regDest
        currentBuffer.append("    la ").append(regDest).append(", v_").append(arrName).append("\n");

        // Sumar offset
        currentBuffer.append("    add ").append(regDest).append(", ").append(regDest).append(", $t1\n");
    }

    private void traducirCastFloat(String res, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Cast to Float: ").append(res).append(" = (float) ").append(val).append("\n");
        cargarEnRegistro("$t0", val); // Cargar entero
        currentBuffer.append("    mtc1 $t0, $f0\n"); // Mover a coprocesador
        currentBuffer.append("    cvt.s.w $f0, $f0\n"); // Convertir int a float
        currentBuffer.append("    s.s $f0, v_").append(res).append("\n"); // Guardar
    }

    private void cargarEnRegistroFloat(String reg, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        if (val.matches("-?\\d+(\\.\\d+)?")) { // Literal float
            currentBuffer.append("    li.s ").append(reg).append(", ").append(val).append("\n");
        } else {
            currentBuffer.append("    l.s ").append(reg).append(", v_").append(val).append("\n");
        }
    }
}
