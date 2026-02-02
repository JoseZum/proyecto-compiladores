import java.util.*;
import java.io.*;

public class traductor {

    private String c3d;
    private StringBuilder mips;
    private HashSet<String> variables;
    private Map<String, String> strings;
    private int stringCount = 0;

    // Columnas por arreglo
    private Map<String, Integer> arrayCols;
    private Map<String, Integer> arraySizes;

    // Buffer instrucciones main
    private StringBuilder mainMips;
    // Buffer instrucciones funciones
    private StringBuilder funcMips;
    // Bandera en funcion
    private boolean inFunction = false;
    // Bandera prologo pendiente
    private boolean pendingPrologue = false;

    // Constructor
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

    // Metodo principal de traduccion
    public String traducir() {
        mips.append("# --- CÓDIGO MIPS GENERADO ---\n\n");
        preProcesar();
        generarDataSection();
        generarTextSection();
        return mips.toString();
    }

    // Preprocesa vars y strings
    private void preProcesar() {
        String[] lineas = c3d.split("\n");
        for (String linea : lineas) {
            linea = linea.trim();
            // elimina comillas dobles
            linea = linea.replace("\"\"", "\"");

            if (linea.isEmpty() || linea.startsWith("#"))
                continue;

            // busca strings para data
            if (linea.contains("\"")) {
                String content = extraerString(linea);
                if (content != null && !strings.containsKey(content)) {
                    strings.put(content, "str" + (++stringCount));
                }
            }

            // Detección de declaraciones de arrays: array name, d1, d2
            if (linea.startsWith("array ")) {
                // Formato: array name, d1, d2;
                String[] parts = linea.substring(6).split(","); // separa partes del array
                String name = parts[0].trim();
                int d1 = Integer.parseInt(parts[1].trim());
                int d2 = Integer.parseInt(parts[2].trim());
                arrayCols.put(name, d2);
                arraySizes.put(name, d1 * d2 * 4);
                continue;
            }

            // Detección de variables y temporales para declararlas como .word 0
            String sinStrings = linea.replaceAll("\".*?\"", " ");
            String[] tokens = sinStrings.split("[\\s\\+\\-\\*/()=,\\[\\]]+");
            for (String token : tokens) {
                if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !isReserved(token) && !arraySizes.containsKey(token)) { // identificador
                    variables.add(token);
                }
            }
        }
    }

    private String extraerString(String linea) {
        int first = linea.indexOf("\""); // primera comilla
        int last = linea.lastIndexOf("\""); // ultima comilla
        if (first != -1 && last > first) {
            String str = linea.substring(first, last + 1);
            return str.replaceAll("^\"+", "").replaceAll("\"+$", "");
        }
        return null;
    }

    private int parseCharLiteral(String val) {
        String content = val.substring(1, val.length() - 1);
        if (content.startsWith("\\")) {
            if (content.length() > 1) {

                char esc = content.charAt(1);
                switch (esc) {
                    case 'n':
                        return 10;
                    case 't':
                        return 9;
                    case 'r':
                        return 13;
                    case '0':
                        return 0;
                    case '\\':
                        return 92;
                    case '\'':
                        return 39;
                    case '"':
                        return 34;
                }
            }
        }
        if (content.length() > 0)
            return (int) content.charAt(0);
        return 0;
    }

    // Verifica si es palabra reservada
    private boolean isReserved(String t) {
        return t.equals("goto") || t.equals("if") || t.equals("ifFalse") ||
                t.equals("call") || t.equals("param") || t.equals("return") ||
                t.equals("print") || t.equals("read") || t.equals("read_int") ||
                t.equals("read_float") || t.equals("read_char") || t.equals("read_string") ||
                t.equals("print_int") || t.equals("print_float") || t.equals("print_char") ||
                t.equals("print_string") || t.equals("array") || t.equals("to");
    }

    // Asegura que variable exista
    private void ensureVariableExists(String varName) {
        if (varName != null && !varName.isEmpty() &&
                varName.matches("[a-zA-Z_][a-zA-Z0-9_]*") &&
                !isReserved(varName) &&
                !arraySizes.containsKey(varName)) {
            variables.add(varName);
        }
    }

    // Genera seccion .data
    private void generarDataSection() {
        mips.append(".data\n");
        mips.append("    _input_buffer: .space 256  # buffer entr\n");
        mips.append("    newline: .asciiz \"\\n\"  # salto linea\n");

        mips.append("    # --- Strings Literales ---\n");
        // recorre el map de strings
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            // agrega cad strings al codigo mips
            mips.append("    ").append(entry.getValue()).append(": .asciiz \"")
                    .append(entry.getKey()).append("\"  # str lit\n");
        }

        mips.append("    .align 2\n");
        mips.append("    # --- Arrays ---\n");
        // asigna el espacio para arreglos
        for (Map.Entry<String, Integer> entry : arraySizes.entrySet()) {

            mips.append("    v_").append(entry.getKey()).append(": .space ").append(entry.getValue())
                    .append("  # space arr\n");
        }

        mips.append("    .align 2\n");
        mips.append("    # --- Variables y Temporales ---\n");
        // agrega cad variable al .data
        for (String var : variables) {
            mips.append("    v_").append(var).append(": .word 0  # var init 0\n");
        }
        mips.append("\n");
    }

    // agrega seccion .text
    private void generarTextSection() {
        mips.append(".text\n"); // segmento de codigo

        mips.append(".globl main\n\n");
        mips.append("main:\n");

        // agrega cada instruccion del C3D al .text
        String[] lineas = c3d.split("\n");
        for (String linea : lineas) {
            procesarLinea(linea.trim());
        }

        mips.append(mainMips);

        mips.append("\n    # Fin del programa (Exit)\n");
        mips.append("    li $v0, 10  # exit syscall\n");
        mips.append("    syscall  # adios\n\n");

        mips.append(funcMips);

        generarRuntime(); // agrega macros
    }

    // Genera rutinas runtime
    private void generarRuntime() {
        mips.append("\n# --- RUTINAS DE SISTEMA ---\n");
        // Syscall 1: showInt
        mips.append("showInt:\n    li $v0, 1  # sys print_int\n    syscall\n    jr $ra  # ret\n.end showInt\n\n");

        // Syscall 4: showString
        mips.append("showString:\n    li $v0, 4  # sys print_str\n    syscall\n    jr $ra  # ret\n.end showString\n\n");
        // Syscall 2: showFloat
        mips.append("showFloat:\n    li $v0, 2  # sys print_float\n    syscall\n    jr $ra  # ret\n.end showFloat\n\n");
        // Syscall 11: showChar
        mips.append("showChar:\n    li $v0, 11  # sys print_char\n    syscall\n    jr $ra  # ret\n.end showChar\n\n");

        // Syscall 5: read_int
        mips.append("readInt:\n    li $v0, 5  # sys read_int\n    syscall\n    jr $ra  # ret\n.end readInt\n\n");

        // Syscall 6: read_float
        mips.append("readFloat:\n    li $v0, 6  # sys read_float\n    syscall\n    jr $ra  # ret\n.end readFloat\n\n");

        // Syscall 8: readString
        mips.append("readString:\n");
        mips.append("    li $v0, 8  # sys read_str\n");
        mips.append("    la $a0, _input_buffer  # buffer est\n");
        mips.append("    li $a1, 255  # max len\n");
        mips.append("    syscall  # leer\n");
        // heap para persistencia
        mips.append("    li $v0, 9  # sys sbrk\n");
        mips.append("    li $a0, 256  # size\n");
        mips.append("    syscall  # alloc\n");
        mips.append("    move $t3, $v0  # ptr dest\n");
        mips.append("    la $t1, _input_buffer  # ptr orig\n");
        // Bucle de copia byte a byte
        mips.append("_copy_loop:\n");
        mips.append("    lb $t2, ($t1)  # load byte\n");
        mips.append("    sb $t2, ($t3)  # store byte\n");
        mips.append("    beqz $t2, _copy_end  # if null end\n");
        mips.append("    addi $t1, $t1, 1  # inc orig\n");
        mips.append("    addi $t3, $t3, 1  # inc dest\n");
        mips.append("    j _copy_loop  # loop\n");
        mips.append("_copy_end:\n");
        mips.append("    jr $ra  # ret\n");
        mips.append(".end readString\n\n");

        // Syscall 12: read_char
        mips.append("readChar:\n");
        mips.append("    li $v0, 12  # sys read_char\n");
        mips.append("    syscall  # leer\n");
        mips.append("    jr $ra  # ret\n");
        mips.append(".end readChar\n\n");

        // ptencia int
        mips.append("pow:\n");
        mips.append("    li $v0, 1  # res = 1\n");
        mips.append("_pow_loop:\n");
        mips.append("    blez $a1, _pow_end  # if exp<=0 end\n");
        mips.append("    mul $v0, $v0, $a0  # res = res*base\n");
        mips.append("    sub $a1, $a1, 1  # dec exp\n");
        mips.append("    j _pow_loop  # loop\n");
        mips.append("_pow_end:\n    jr $ra  # ret\n.end pow\n\n");
    }

    private void procesarLinea(String linea) {// procesa cada linea del c3d
        if (linea.isEmpty())
            return;

        // detecta si la linea es una funcion o main
        if (linea.startsWith("# FUNC ")) {
            inFunction = true;
        } else if (linea.startsWith("# MAIN ")) {
            inFunction = false;
        }

        // selecciona en qué buffer concatenar (main o funciones)
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        // manejo de comentarios y directivas c3d
        if (linea.startsWith("# FUNC ")) {
            currentBuffer.append("\n    ").append(linea).append("\n");
            pendingPrologue = true;
            return;
        }

        if (linea.startsWith("#")) {
            currentBuffer.append("\n    ").append(linea).append("\n"); // Copiar comentario
            // fin de funcion
            if (linea.startsWith("# END FUNC")) {
                inFunction = false;
            }
            // fin de main
            if (linea.startsWith("# END MAIN")) {
                currentBuffer.append("    li $v0, 10\n");
                currentBuffer.append("    syscall\n");
            }
            return;
        }

        // manejo de etiquetas
        if (linea.endsWith(":")) {
            if (!linea.equals("main:")) {
                currentBuffer.append(linea).append("\n"); // Escribir etiqueta

                // inicio de funcion
                if (pendingPrologue && inFunction) {
                    currentBuffer.append("\n    # Reserva de Frame y guardado de $ra\n");
                    currentBuffer.append("    subu $sp, $sp, 4  # push ra\n"); // guarda el espacio en pila
                    currentBuffer.append("    sw $ra, ($sp)  # save ra\n"); // guarda el valor de retorn
                    pendingPrologue = false;
                }
            }
            return;
        }

        // separar comados de argumetnos
        String[] tokens = linea.split("\\s+");
        String cmd = tokens[0];

        // switch para instrucciones
        switch (cmd) {
            case "goto": // salto incondicional
                traducirGoto(linea.substring(5).trim());
                break;
            case "if": // salto condicional (si verdadero)
                traducirIf(linea);
                break;
            case "ifFalse": // salto condicional (si falso)
                traducirIfFalse(linea);
                break;
            case "print": // alias para print_int
                traducirPrintInt(linea.substring(6).trim());
                break;
            case "print_int": // imprimir entero
                traducirPrintInt(linea.substring(10).trim());
                break;
            case "print_float": // imprimir decimal
                traducirPrintFloat(linea.substring(12).trim());
                break;
            case "print_char": // imprimir caracter
                traducirPrintChar(linea.substring(11).trim());
                break;
            case "print_string": // imprimir texto
                traducirPrintString(linea.substring(13).trim());
                break;
            case "read": // alias para read_int
                traducirReadInt(linea.substring(cmd.length()).trim());
                break;
            case "read_int": // Leer entero
                traducirReadInt(linea.substring(cmd.length()).trim());
                break;
            case "read_float": // Leer float
                traducirReadFloat(linea.substring(cmd.length()).trim());
                break;
            case "read_char": // Leer char
                traducirReadChar(linea.substring(cmd.length()).trim());
                break;
            case "read_string": // Leer string
                traducirReadString(linea.substring(cmd.length()).trim());
                break;
            case "param": // manda el parametro a la pila
                traducirParam(linea.substring(cmd.length()).trim());
                break;
            case "call": // llama a la funcion
                traducirCall(linea);
                break;
            case "return": // retorna de la funcion
                String retVal = linea.length() > 6 ? linea.substring(7).trim() : null;
                traducirReturn(retVal);
                break;
            case "array": // Declaración array
                break;
            case "get_stack": // Recuperar parámetro de la pila
                traducirGetStack(linea.substring(cmd.length()).trim());
                break;
            case "save_local": // Guardar variable local en pila (para recursión)
                traducirSaveLocal(linea.substring(cmd.length()).trim());
                break;
            case "restore_local": // Recuperar variable local de pila
                traducirRestoreLocal(linea.substring(cmd.length()).trim());
                break;
            default:
                // asignaciones
                if (linea.contains("=")) {
                    traducirAsignacion(linea);
                } else {
                    // Si llega aquí, es algo que no entendemos
                    mips.append("    #ERROR. No se pudo traducir la linea ").append(linea).append("\n");
                }
                break;
        }
    }

    // --- funciones para traducir ---

    // traducir asignaciones y operaciones
    private void traducirAsignacion(String linea) {
        // Seleccionar buffer correcto
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        // llamada a funcion
        if (linea.contains("call ")) {
            traducirCall(linea);
            return;
        }

        // arreglos
        if (linea.contains("[")) {
            traducirArreglo(linea);
            return;
        }

        // separar lado derecho e izquierdo
        String[] partes = linea.split("=", 2);
        String res = partes[0].trim();
        String expr = partes[1].trim(); // expresión

        // string literal
        if (expr.startsWith("\"") || expr.startsWith("'")) {
            currentBuffer.append("\n    # Asignación literal: ").append(res).append(" = ").append(expr).append("\n");
            cargarEnRegistro("$t0", expr);
            currentBuffer.append("    sw $t0, v_").append(res).append("\n");
            return;
        }

        String[] tokens = expr.split("\\s+");

        // revisa float
        if (expr.startsWith("(float) ")) {
            String val = expr.substring(8).trim();
            traducirCastFloat(res, val);
            return;
        }

        // Asignación simple
        if (tokens.length == 1) {
            String val = tokens[0];
            // revisa el punto flotante
            if (val.matches("-?\\d+\\.\\d+")) {
                currentBuffer.append("\n    # Asignación float literal: ").append(res).append(" = ").append(val)
                        .append("\n");
                currentBuffer.append("    li.s $f0, ").append(val).append("\n");
                currentBuffer.append("    s.s $f0, v_").append(res).append("\n");
            } else {
                // entero
                currentBuffer.append("\n    # Asignación simple: ").append(res).append(" = ").append(val).append("\n");
                cargarEnRegistro("$t0", val);
                currentBuffer.append("    sw $t0, v_").append(res).append("  # guardar var\n");
            }
        }
        // binarios
        else if (tokens.length == 3) {
            String op = tokens[1];
            currentBuffer.append("\n    # Operación: ").append(linea).append("\n");

            // Operaciones de float
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

            // Operaciones enteras
            cargarEnRegistro("$t0", tokens[0]);
            cargarEnRegistro("$t1", tokens[2]);

            switch (op) {
                case "+":
                    // add suma enteros con signo
                    currentBuffer.append("    add $t2, $t0, $t1  # suma\n");
                    break;
                case "-":
                    // sub resta enteros
                    currentBuffer.append("    sub $t2, $t0, $t1  # resta\n");
                    break;
                case "*":
                    // mul multiplicación entera
                    currentBuffer.append("    mul $t2, $t0, $t1  # mult\n");
                    break;
                case "/":
                case "//":
                    // div división entera con signo
                    currentBuffer.append("    div $t2, $t0, $t1  # div\n");
                    break;
                case "%":
                    // rem resto de división entera
                    currentBuffer.append("    rem $t2, $t0, $t1  # modulo\n");
                    break;
                // Operadores relacionales
                case "==":
                    // seq (Set EQual) $t2 = 1 si $t0 == $t1, sino 0
                    currentBuffer.append("    seq $t2, $t0, $t1  # eq\n");
                    break;
                case "!=":
                    // sne (Set Not Equal) $t2 = 1 si $t0 != $t1, sino 0
                    currentBuffer.append("    sne $t2, $t0, $t1  # neq\n");
                    break;
                case ">":
                    // sgt (Set Greater Than) $t2 = 1 si $t0 > $t1, sino 0
                    currentBuffer.append("    sgt $t2, $t0, $t1  # gt\n");
                    break;
                case "<":
                    // slt (Set Less Than) $t2 = 1 si $t0 < $t1, sino 0
                    currentBuffer.append("    slt $t2, $t0, $t1  # lt\n");
                    break;
                case ">=":
                    // sge (Set Greater Equal) $t2 = 1 si $t0 >= $t1, sino 0
                    currentBuffer.append("    sge $t2, $t0, $t1  # gte\n");
                    break;
                case "<=":
                    // sle (Set Less Equal) $t2 = 1 si $t0 <= $t1, sino 0
                    currentBuffer.append("    sle $t2, $t0, $t1  # lte\n");
                    break;
                // Lógicos bitwise (actúan bit a bit)
                case "and":
                    // and AND lógico bit a bit
                    currentBuffer.append("    and $t2, $t0, $t1  # and\n");
                    break;
                case "or":
                    // or OR lógico bit a bit
                    currentBuffer.append("    or $t2, $t0, $t1  # or\n");
                    break;
                case "^": // Potencia
                    currentBuffer.append("    # Potencia usando subrutina\n");
                    currentBuffer.append("    move $a0, $t0\n"); // copia registro $t0 a argumento $a0
                    currentBuffer.append("    move $a1, $t1\n"); // copia registro $t1 a argumento $a1
                    currentBuffer.append("    jal _pow\n"); // llama a pow
                    currentBuffer.append("    move $t2, $v0\n");
                    break;
                // --- RELACIONALES FLOAT ---
                case "==f":
                case "!=f":
                case "<f":
                case "<=f":
                case ">f":
                case ">=f":
                    // 1. Cargar operandos float
                    String fOp1 = tokens[0];
                    String fOp2 = tokens[2];
                    cargarEnRegistroFloat("$f0", fOp1);
                    cargarEnRegistroFloat("$f1", fOp2);

                    // cpmparaciones
                    switch (op) {
                        case "==f":
                            currentBuffer.append("    c.eq.s $f0, $f1\n"); // Check equal
                            break;
                        case "<f":
                            currentBuffer.append("    c.lt.s $f0, $f1\n"); // Check less than
                            break;
                        case "<=f":
                            currentBuffer.append("    c.le.s $f0, $f1\n"); // Check less equal
                            break;
                        case "!=f":
                            currentBuffer.append("    c.eq.s $f0, $f1\n"); // Check equal
                            break;
                        case ">f":
                            currentBuffer.append("    c.lt.s $f1, $f0\n"); // Check less than
                            break;
                        case ">=f":
                            currentBuffer.append("    c.le.s $f1, $f0\n"); // Check less equal
                            break;
                    }

                    String lblTrue = "L_F_TRUE_" + (++stringCount);
                    String lblEnd = "L_F_END_" + stringCount;

                    if (op.equals("!=f")) {
                        currentBuffer.append("    bc1f ").append(lblTrue).append("\n"); // falso son iguales
                    } else {
                        currentBuffer.append("    bc1t ").append(lblTrue).append("\n");
                    }

                    // Falso
                    currentBuffer.append("    li $t2, 0\n");
                    currentBuffer.append("    j ").append(lblEnd).append("\n");

                    // Verdadero
                    currentBuffer.append(lblTrue).append(":\n");
                    currentBuffer.append("    li $t2, 1\n");

                    currentBuffer.append(lblEnd).append(":\n");
                    break;
            }
            // sw guarda valor de registro $t2 en memoria
            currentBuffer.append("    sw $t2, v_").append(res).append("  # guardar resultado\n");
        }
        // unarias
        else if (tokens.length == 2) {
            String op = tokens[0];
            String val = tokens[1];
            if (op.equals("not")) {
                currentBuffer.append("\n    # Operacion Not: ").append(linea).append("\n");
                cargarEnRegistro("$t0", val);
                currentBuffer.append("    seq $t0, $t0, $zero  # not\n");
                currentBuffer.append("    sw $t0, v_").append(res).append("  # guardar res\n");
            }
        }
    }

    // goto
    private void traducirGoto(String label) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Salto incondicional\n");
        currentBuffer.append("    j ").append(label).append("  # goto\n");
    }

    // if
    private void traducirIf(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Salto condicional: ").append(linea).append("\n");
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        currentBuffer.append("    bnez $t0, ").append(label).append("  # if true goto\n");
    }

    // ifFalse
    private void traducirIfFalse(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Salto condicional inverso: ").append(linea).append("\n");
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        currentBuffer.append("    beqz $t0, ").append(label).append("  # if false goto\n");
    }

    // --- PRINT ---

    private void traducirPrintInt(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir Int: ").append(val).append("\n");
        cargarEnRegistro("$a0", val); // poner valor en argumento
        currentBuffer.append("    jal showInt  # print int\n"); // llamar rutina
        printNewline(); // salto de linea
    }

    private void traducirPrintFloat(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir Float: ").append(val).append("\n");
        if (val.matches("-?\\d+\\.\\d+")) {
            currentBuffer.append("    li.s $f12, ").append(val).append("  # carga literal float\n");
        } else {
            currentBuffer.append("    l.s $f12, v_").append(val).append("  # leer var float\n");
        }
        currentBuffer.append("    jal showFloat  # print float\n");
        printNewline();
    }

    private void traducirPrintString(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir String: ").append(val).append("\n");
        if (val.startsWith("\"")) {
            // Literal de texto
            String content = extraerString(val);
            String label = strings.get(content); // Buscar etiqueta .data
            currentBuffer.append("    la $a0, ").append(label).append("  # carga ptr str\n");
        } else {
            // Variable puntero string
            currentBuffer.append("    lw $a0, v_").append(val).append("  # leer ptr str\n");
        }
        currentBuffer.append("    jal showString  # print string\n");
        printNewline();
    }

    private void traducirPrintChar(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir Char: ").append(val).append("\n");
        if (val.startsWith("'")) {
            currentBuffer.append("    li $a0, ").append(val).append("  # carga char\n");
        } else {
            currentBuffer.append("    lw $a0, v_").append(val).append("  # leer var char\n");
        }
        currentBuffer.append("    jal showChar  # print char\n");
        printNewline();
    }

    // salto de linea
    private void printNewline() {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    la $a0, newline  # cargar newline\n");
        currentBuffer.append("    jal showString  # print newline\n");
    }

    // --- ectura---

    // Lee int
    private void traducirReadInt(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer Int: ").append(var).append("\n");
        currentBuffer.append("    jal readInt  # syscall read_int\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("  # guardar leido\n");
    }

    // Lee float
    private void traducirReadFloat(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer Float: ").append(var).append("\n");
        currentBuffer.append("    jal readFloat  # syscall read_float\n");
        currentBuffer.append("    s.s $f0, v_").append(var).append("  # guardar leido\n");
    }

    // Lee char
    private void traducirReadChar(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer Char: ").append(var).append("\n");
        currentBuffer.append("    jal readChar  # syscall read_char\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("  # guardar leido\n");
    }

    // Lee string
    private void traducirReadString(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer String: ").append(var).append("\n");
        currentBuffer.append("    jal readString  # syscall read_string\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("  # guardar ptr leido\n");
        ensureVariableExists(var);
    }

    // --- funciones y pila---

    // Recupera parametro de pila
    private void traducirGetStack(String linea) {

        String[] parts = linea.split(",");
        String var = parts[0].trim();
        String offset = parts[1].trim();

        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Cargar parametro dsd pila: ").append(var).append(" <- ").append(offset)
                .append("($sp)\n");
        currentBuffer.append("    lw $t0, ").append(offset).append("($sp)  # load param\n");
        currentBuffer.append("    sw $t0, v_").append(var).append("  # save local\n");
    }

    // Empuja parametro a pila
    private void traducirParam(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Parámetro de función: ").append(val).append("\n");
        cargarEnRegistro("$t0", val);
        currentBuffer.append("    subu $sp, $sp, 4  # push\n");
        currentBuffer.append("    sw $t0, ($sp)  # save param\n");
    }

    // Guarda local en pila
    private void traducirSaveLocal(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Guardar local en pila: ").append(var).append("\n");
        currentBuffer.append("    lw $t0, v_").append(var).append("\n");
        currentBuffer.append("    subu $sp, $sp, 4\n");
        currentBuffer.append("    sw $t0, ($sp)\n");
    }

    // Restaura local de pila
    private void traducirRestoreLocal(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Restaurar local de pila: ").append(var).append("\n");
        currentBuffer.append("    lw $t0, ($sp)  # pop val\n");
        currentBuffer.append("    addu $sp, $sp, 4  # pop op\n");
        currentBuffer.append("    sw $t0, v_").append(var).append("  # restore\n");
    }

    // Traduce llamada funcion
    private void traducirCall(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Llamada a función: ").append(linea).append("\n");
        String func;
        if (linea.contains("=")) {
            String expr = linea.split("=")[1].trim();
            func = expr.split("\\s+")[1].replace(",", "");
        } else {
            func = linea.split("\\s+")[1].replace(",", "");
        }

        currentBuffer.append("    jal ").append(func).append("  # call func\n");

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
            }
        }
        if (linea.contains("=")) {
            String res = linea.split("=")[0].trim();
            currentBuffer.append("    sw $v0, v_").append(res).append("\n");
        }
    }

    // Traduce retorno
    private void traducirReturn(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Retorno de función\n");
        if (val != null) {
            if (val.matches("-?\\d+\\.\\d+")) {
                cargarEnRegistroFloat("$f0", val);
            } else {
                cargarEnRegistro("$v0", val);
            }
        }
        if (inFunction) {
            currentBuffer.append("    # Restaurar $ra y Frame\n");
            currentBuffer.append("    lw $ra, ($sp)  # restore ra\n");
            currentBuffer.append("    addu $sp, $sp, 4  # pop ra\n");
            currentBuffer.append("    jr $ra  # ret\n");
        } else {
            currentBuffer.append("    # Fin de Main (Exit)\n");
            currentBuffer.append("    li $v0, 10  # exit syscall\n");
            currentBuffer.append("    syscall  # adios\n");
        }
    }

    // Helper carga registro
    private void cargarEnRegistro(String reg, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        if (val.matches("-?\\d+")) { // Entero literal
            currentBuffer.append("    li ").append(reg).append(", ").append(val).append("  # carga int\n");
        } else if (val.equals("true")) { // Boolean true
            currentBuffer.append("    li ").append(reg).append(", 1  # true\n");
        } else if (val.equals("false")) { // Boolean false
            currentBuffer.append("    li ").append(reg).append(", 0  # false\n");
        } else if (val.startsWith("'")) {
            // convierte a ascii
            int ascii = parseCharLiteral(val);
            currentBuffer.append("    li ").append(reg).append(", ").append(ascii).append("  # carga char\n");
        } else if (val.startsWith("\"")) {
            // carga direccion
            String content = extraerString(val);
            String label = strings.get(content);
            if (label != null) {
                currentBuffer.append("    la ").append(reg).append(", ").append(label).append("  # carga dir str\n");
            } else {
                currentBuffer.append("    # Error: String literal not found in .data\n");
            }
        } else {
            // carga valor de memoria
            currentBuffer.append("    lw ").append(reg).append(", v_").append(val).append("  # leer var\n");
        }
    }

    // --- ARREGLOS ---

    private void traducirArreglo(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        int b1 = linea.indexOf("[");
        if (b1 == -1)
            return;

        String preBracket = linea.substring(0, b1).trim();
        boolean esAsignacionAArreglo = !preBracket.contains("=");

        if (esAsignacionAArreglo) {
            String[] parts = linea.split("=");
            String val = parts[1].trim(); // guardar valor
            String leftSide = parts[0].trim(); // posicion

            procesarDireccionArreglo(leftSide, "$t3");

            cargarEnRegistro("$t0", val);
            currentBuffer.append("    sw $t0, ($t3)  # guardar en array\n");

        } else {

            String[] parts = linea.split("=");
            String dest = parts[0].trim(); // destino
            String rightSide = parts[1].trim();

            procesarDireccionArreglo(rightSide, "$t3");

            currentBuffer.append("    lw $t0, ($t3)  # leer del array\n");
            currentBuffer.append("    sw $t0, v_").append(dest).append("  # guardar var\n");
        }
    }

    // calcula direccion arr[i][j] en regDest
    private void procesarDireccionArreglo(String acceso, String regDest) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        int b1 = acceso.indexOf("[");
        String arrName = acceso.substring(0, b1).trim();

        String resto = acceso.substring(b1);

        String[] indices = resto.replace("][", ",").replace("[", "").replace("]", "").split(",");
        String i = indices[0].trim();
        String j = indices[1].trim();

        int cols = arrayCols.getOrDefault(arrName, 0); // coluimnas

        cargarEnRegistro("$t1", i);
        currentBuffer.append("    li $t2, ").append(cols).append("  # load cols\n");
        currentBuffer.append("    mul $t1, $t1, $t2  # i * cols\n");

        cargarEnRegistro("$t2", j);
        currentBuffer.append("    add $t1, $t1, $t2  # + j\n");

        // Multiplicar por 4
        currentBuffer.append("    mul $t1, $t1, 4  # offset byte\n");

        // Cargar dirección del array en el registro destino
        currentBuffer.append("    la ").append(regDest).append(", v_").append(arrName).append("  # base addr\n");

        // Sumar el offset a la dirección base para obtener la direción efectiva del
        currentBuffer.append("    add ").append(regDest).append(", ").append(regDest).append(", $t1  # dir efectiva\n");
    }

    // Casteo int -> float
    private void traducirCastFloat(String res, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Cast to Float: ").append(res).append(" = (float) ").append(val).append("\n");
        cargarEnRegistro("$t0", val);
        currentBuffer.append("    mtc1 $t0, $f0  # mov to fpu\n");
        currentBuffer.append("    cvt.s.w $f0, $f0  # int to float\n");
        currentBuffer.append("    s.s $f0, v_").append(res).append("  # guardar float\n");
    }

    // Carga float en FPU
    private void cargarEnRegistroFloat(String reg, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        if (val.matches("-?\\d+(\\.\\d+)?")) { // Literal float
            currentBuffer.append("    li.s ").append(reg).append(", ").append(val).append("  # load lit float\n");
        } else {
            currentBuffer.append("    l.s ").append(reg).append(", v_").append(val).append("  # load var float\n");
        }
    }
}
