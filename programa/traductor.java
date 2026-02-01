import java.util.*;
import java.io.*;

/**
 * clase para traducir el codigo de tres direcciones al MIPS
 */
public class traductor {

    private String c3d; // contiene el codigo de tres direcciones
    private StringBuilder mips; // builder para guardar el codigo traducido
    private HashSet<String> variables; // hash para guardar las variables
    private Map<String, String> strings; // map para literales strings
    private int stringCount = 0; // contador para ver cuantos strings se han generado y darles etiquetas unicas
    // Mapa que almacena la cantidad de columnas de cada arreglo declarado (para
    // cálculos de índice)
    private Map<String, Integer> arrayCols; //
    private Map<String, Integer> arraySizes; // Para guardar el tamaño total en bytes

    // Buffer para acumular las instrucciones correspondientes a la función 'main'
    private StringBuilder mainMips;
    // Buffer para acumular las instrucciones de otras funciones definidas por el
    // usuario
    private StringBuilder funcMips;
    // Bandera para rastrear si estamos procesando código dentro de una función o en
    // el main
    private boolean inFunction = false;
    // Bandera que indica si hay un prólogo de función (guardar $ra, $fp) pendiente
    // de escribir
    private boolean pendingPrologue = false;

    // Constructor que recibe el código C3D e inicializa las estructuras
    public traductor(String c3d) {
        this.c3d = c3d;
        this.mips = new StringBuilder(); // Inicializa builder principal
        this.mainMips = new StringBuilder(); // Inicializa builder main
        this.funcMips = new StringBuilder(); // Inicializa builder funciones
        this.variables = new HashSet<>(); // Inicializa set de variables
        this.strings = new HashMap<>(); // Inicializa mapa de strings
        this.arrayCols = new HashMap<>(); // Inicializa metadatos de arrays
        this.arraySizes = new HashMap<>(); // Inicializa tamaños de arrays
    }

    // Método principal que ejecuta las fases de la traducción
    public String traducir() {
        mips.append("# --- CÓDIGO MIPS GENERADO ---\n\n"); // Cabecera
        preProcesar(); // Primera pasada: recolectar datos (vars, arrays, strings)
        generarDataSection(); // Generar sección .data con lo recolectado
        generarTextSection(); // Generar sección .text traduciendo instrucciones
        return mips.toString(); // Retornar todo el código ensamblado
    }

    /**
     * Identifica variables y strings antes de generar código.
     * Esta función analíza todo el C3D para saber qué memoria reservar.
     */
    private void preProcesar() {
        // Separar el C3D por líneas
        String[] lineas = c3d.split("\n");
        // Recorrer cada línea
        for (String linea : lineas) {
            linea = linea.trim(); // Limpiar espacios
            // Corrección: Normalizar comillas dobles si el generador C3D las duplicó
            linea = linea.replace("\"\"", "\"");

            // Ignorar líneas vacías o comentarios puros
            if (linea.isEmpty() || linea.startsWith("#"))
                continue;

            // Detección de strings para agregarlos a la sección .data
            if (linea.contains("\"")) {
                String content = extraerString(linea); // Sacar el texto entre comillas
                // Si es un string válido y no está mapedo, registrarlo
                if (content != null && !strings.containsKey(content)) {
                    strings.put(content, "str" + (++stringCount)); // "Texto" -> "strN"
                }
            }

            // Detección de declaraciones de arrays: array name, d1, d2
            if (linea.startsWith("array ")) {
                // Formato: array name, d1, d2;
                String[] parts = linea.substring(6).split(","); // Quitar "array " y separar
                String name = parts[0].trim(); // Nombre variable array
                int d1 = Integer.parseInt(parts[1].trim()); // Dimensión 1
                int d2 = Integer.parseInt(parts[2].trim()); // Dimensión 2
                arrayCols.put(name, d2); // Guardar cols para usar en 'index = i*cols + j'
                arraySizes.put(name, d1 * d2 * 4); // Calcular bytes totales (4 bytes por int)
                continue; // Saltar al siguiente ciclo, ya procesamos esta línea
            }

            // Detección de variables y temporales para declararlas como .word 0
            // Primero eliminamos los strings de la línea para evitar falsos positivos
            // dentro de comillas
            String sinStrings = linea.replaceAll("\".*?\"", " ");
            // Separamos por operadores y caracteres especiales para aislar identificadores
            String[] tokens = sinStrings.split("[\\s\\+\\-\\*/()=,\\[\\]]+");
            for (String token : tokens) {
                // Si parece indentificador (letras/núm), NO es palabra reservada y NO es un
                // array ya conocido
                if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !isReserved(token) && !arraySizes.containsKey(token)) {
                    variables.add(token); // Agregar al set de variables a declarar
                }
            }
        }
    }

    // Método auxiliar para extraer el contenido entre comillas de una línea
    private String extraerString(String linea) {
        int first = linea.indexOf("\""); // Indice primera comilla
        int last = linea.lastIndexOf("\""); // Indice ultima comilla
        if (first != -1 && last > first) {
            // Extraer subtring incluyendo comillas
            String str = linea.substring(first, last + 1);
            // Retornar contenido sin las comillas de los extremos
            return str.replaceAll("^\"+", "").replaceAll("\"+$", "");
        }
        return null; // Si no hay string válido
    }

    private int parseCharLiteral(String val) {
        // Val viene como 'A' o '\n'
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

    // Verifica si un token es una palabra reservada del C3D o instrucción, para no
    // tratarla como variable
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
     * Esto es útil si aparece una variable nueva durante la generación que se nos
     * pasó pre-procesar.
     */
    private void ensureVariableExists(String varName) {
        if (varName != null && !varName.isEmpty() &&
                varName.matches("[a-zA-Z_][a-zA-Z0-9_]*") && // Validar sintaxis
                !isReserved(varName) && // No es palabra clave
                !arraySizes.containsKey(varName)) { // No es array
            variables.add(varName); // Registrar para .data
        }
    }

    /**
     * SECCIÓN .DATA: Declaración de variables y constantes.
     * Genera el código MIPS para reservar memoria estática.
     */
    private void generarDataSection() {
        mips.append(".data\n");
        mips.append("    _input_buffer: .space 256\n"); // Buffer global para lecturas de string
        mips.append("    newline: .asciiz \"\\n\"\n"); // Constante para salto de línea

        mips.append("    # --- Strings Literales ---\n");
        // Iterar sobre mapa de strings literales encontrados
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            // Definir cada string: label: .asciiz "texto"
            mips.append("    ").append(entry.getValue()).append(": .asciiz \"")
                    .append(entry.getKey()).append("\"\n");
        }

        // Alinear memoria a palabra (4 bytes) antes de declarar arrays para evitar
        // errores de alineación
        mips.append("    .align 2\n");
        mips.append("    # --- Arrays ---\n");
        // Declarar espacio para arrays
        for (Map.Entry<String, Integer> entry : arraySizes.entrySet()) {
            // v_Nombre: .space Bytes
            mips.append("    v_").append(entry.getKey()).append(": .space ").append(entry.getValue()).append("\n");
        }

        // Alinear nuevamente antes de variables simples
        mips.append("    .align 2\n");
        mips.append("    # --- Variables y Temporales ---\n");
        // Declarar cada variable encontrada como una palabra (.word) inicializada en 0
        for (String var : variables) {
            mips.append("    v_").append(var).append(": .word 0\n");
        }
        mips.append("\n");
    }

    /**
     * SECCIÓN .TEXT: Instrucciones del programa y Macros.
     * Genera el código ejecutable.
     */
    private void generarTextSection() {
        mips.append(".text\n"); // Inicio sección código

        mips.append(".globl main\n\n"); // Declarar main global
        mips.append("main:\n"); // Etiqueta main
        // El flujo del programa caerá naturalmente hacia las instrucciones iniciales o
        // 'main:' (etiquetas de usuario)

        // Procesar cada línea del C3D y convertirla a MIPS
        String[] lineas = c3d.split("\n");
        for (String linea : lineas) {
            procesarLinea(linea.trim()); // Llamada al despachador de traducción
        }

        // --- Ensamblaje final ---
        // Primero colocar todo el código perteneciente al main
        mips.append(mainMips);

        // Generar salida del sistema (exit) al final del main para terminar limpiamente
        mips.append("\n    # Fin del programa (Exit)\n");
        mips.append("    li $v0, 10\n"); // Syscall 10 = exit
        mips.append("    syscall\n\n");

        // Después del exit, colocar el código de las funciones (subrutinas)
        mips.append(funcMips);

        // Finalmente, añadir rutinas de soporte ("runtime")
        generarRuntime();
    }

    // Genera funciones auxiliares en MIPS para IO y operaciones comunes
    private void generarRuntime() {
        mips.append("\n# --- RUTINAS DE SISTEMA ---\n");
        // Rutina para imprimir entero ($a0)
        mips.append("showInt:\n    li $v0, 1\n    syscall\n    jr $ra\n.end showInt\n\n");
        // Rutina para imprimir string ($a0 - dirección)
        mips.append("showString:\n    li $v0, 4\n    syscall\n    jr $ra\n.end showString\n\n");
        // Rutina para imprimir float ($f12)
        mips.append("showFloat:\n    li $v0, 2\n    syscall\n    jr $ra\n.end showFloat\n\n");
        // Rutina para imprimir carácter ($a0)
        mips.append("showChar:\n    li $v0, 11\n    syscall\n    jr $ra\n.end showChar\n\n");

        // Rutinas de lectura
        mips.append("readInt:\n    li $v0, 5\n    syscall\n    jr $ra\n.end readInt\n\n"); // Lee int a $v0
        mips.append("readFloat:\n    li $v0, 6\n    syscall\n    jr $ra\n.end readFloat\n\n"); // Lee float a $f0

        // Rutina compleja para leer Strings
        mips.append("readString:\n");
        mips.append("    li $v0, 8\n"); // syscall 8: read_string
        mips.append("    la $a0, _input_buffer\n"); // Buffer temporal estático
        mips.append("    li $a1, 255\n"); // Max longitud
        mips.append("    syscall\n");
        // Copiar string del buffer estático al heap para persistencia
        mips.append("    # Allocate heap memory for string\n");
        mips.append("    li $v0, 9\n"); // syscall 9: sbrk (malloc)
        mips.append("    li $a0, 256\n"); // tamaño a reservar
        mips.append("    syscall\n");
        mips.append("    move $t3, $v0\n"); // $t3 = puntero destino (Heap)
        mips.append("    la $t1, _input_buffer\n"); // $t1 = puntero origen (Buffer)
        // Bucle de copia byte a byte
        mips.append("_copy_loop:\n");
        mips.append("    lb $t2, ($t1)\n"); // Cargar byte
        mips.append("    sb $t2, ($t3)\n"); // Guardar byte
        mips.append("    beqz $t2, _copy_end\n"); // Si es null (fin de string), terminar
        mips.append("    addi $t1, $t1, 1\n"); // Avanzar origen
        mips.append("    addi $t3, $t3, 1\n"); // Avanzar destino
        mips.append("    j _copy_loop\n");
        mips.append("_copy_end:\n");
        mips.append("    # $v0 ya tiene la dirección de inicio retornada por sbrk\n");
        mips.append("    jr $ra\n");
        mips.append(".end readString\n\n");

        mips.append("readChar:\n");
        mips.append("    li $v0, 12\n");
        mips.append("    syscall\n");
        // Skip whitespace logic? The user might want to read ANY char.
        // But for "get" behavior usually we want to skip newlines from previous input.
        // A simple fix for interactive programs is to ignore CR/LF/Space
        mips.append("    # Optional: Skip whitespace (Space, Tab, Newline)\n");
        mips.append("    # ble $v0, 32, readChar\n    ble $v0, 32, readChar\n");
        mips.append("    jr $ra\n");
        mips.append(".end readChar\n\n");

        // Subrutina para calcular potencia entera
        mips.append("pow:\n");
        mips.append("    li $v0, 1\n"); // Resultado inicial = 1
        mips.append("_pow_loop:\n");
        mips.append("    blez $a1, _pow_end\n"); // Si exponente <= 0, fin
        mips.append("    mul $v0, $v0, $a0\n"); // Mult base
        mips.append("    sub $a1, $a1, 1\n"); // Decr exponente
        mips.append("    j _pow_loop\n");
        mips.append("_pow_end:\n    jr $ra\n.end pow\n\n");
    }

    /**
     * PROCESADOR DE LÍNEAS (El "Case" principal)
     * Decide qué metodo llamar según la instrucción C3D.
     */
    private void procesarLinea(String linea) {
        if (linea.isEmpty())
            return; // Saltar líneas vacías

        // Detectar directivas especiales de funciones en C3D para cambiar el buffer
        // destino
        if (linea.startsWith("# FUNC ")) {
            inFunction = true; // Estamos entrando a definición de función
        } else if (linea.startsWith("# MAIN ")) {
            inFunction = false; // Estamos entrando a código main
        }

        // Seleccionar en qué buffer concatenar (main o funciones)
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        // MANEJO DE COMENTARIOS Y DIRECTIVAS C3D
        if (linea.startsWith("# FUNC ")) {
            currentBuffer.append("\n    ").append(linea).append("\n"); // Preservar comentario
            pendingPrologue = true; // Marcar que necesitamos generar prólogo ($ra en pila) pronto
            return;
        }

        if (linea.startsWith("#")) {
            currentBuffer.append("\n    ").append(linea).append("\n"); // Copiar comentario
            // Si termina una función, salir del modo función
            if (linea.startsWith("# END FUNC")) {
                inFunction = false;
            }
            // Si termina el main, asegurar syscall de salida
            if (linea.startsWith("# END MAIN")) {
                currentBuffer.append("    li $v0, 10\n");
                currentBuffer.append("    syscall\n");
            }
            return;
        }

        // 1. MANEJO DE ETIQUETAS (L1:, func:, label:)
        if (linea.endsWith(":")) {
            if (!linea.equals("main:")) { // 'main:' ya se genera manualmente
                currentBuffer.append(linea).append("\n"); // Escribir etiqueta

                // Si estamos al inicio de una función (pendingPrologue), generar código de
                // entrada
                if (pendingPrologue && inFunction) {
                    currentBuffer.append("\n    # Reserva de Frame y guardado de $ra\n");
                    currentBuffer.append("    subu $sp, $sp, 4\n"); // Reservar espacio en pila
                    currentBuffer.append("    sw $ra, ($sp)\n"); // Guardar Return Address
                    pendingPrologue = false; // Reset bandera
                }
            }
            return;
        }

        // Tokenizar instrucción (separar comando de argumentos)
        String[] tokens = linea.split("\\s+");
        String cmd = tokens[0]; // Primera palabra es el comando/instrucción

        // 2. SWITCH PARA INSTRUCCIONES ESPECÍFICAS
        switch (cmd) {
            case "goto": // Salto Incondicional
                traducirGoto(linea.substring(5).trim());
                break;
            case "if": // Salto Condicional (si verdadero)
                traducirIf(linea);
                break;
            case "ifFalse": // Salto Condicional (si falso)
                traducirIfFalse(linea);
                break;
            case "print": // Alias para print_int
                traducirPrintInt(linea.substring(6).trim());
                break;
            case "print_int": // Imprimir entero
                traducirPrintInt(linea.substring(10).trim());
                break;
            case "print_float": // Imprimir decimal
                traducirPrintFloat(linea.substring(12).trim());
                break;
            case "print_char": // Imprimir caracter
                traducirPrintChar(linea.substring(11).trim());
                break;
            case "print_string": // Imprimir texto
                traducirPrintString(linea.substring(13).trim());
                break;
            case "read":
            case "read_int": // Leer entero
                traducirReadInt(linea.substring(cmd.length()).trim());
                break;
            case "read_float": // Leer float
                traducirReadFloat(linea.substring(11).trim());
                break;
            case "read_char": // Leer char
                traducirReadChar(linea.substring(10).trim());
                break;
            case "read_string": // Leer string
                traducirReadString(linea.substring(12).trim());
                break;
            case "param": // Empujar parámetro para llamada
                traducirParam(linea.substring(6).trim());
                break;
            case "call": // Llamar función
                traducirCall(linea);
                break;
            case "return": // Retornar de función
                String retVal = linea.length() > 6 ? linea.substring(7).trim() : null;
                traducirReturn(retVal);
                break;
            case "array": // Declaración array (ya manejada en preproceso, ignora aquí)
                break;
            case "get_stack": // Recuperar parámetro de la pila
                traducirGetStack(linea.substring(10).trim());
                break;
            case "save_local": // Guardar variable local en pila (para recursión)
                traducirSaveLocal(linea.substring(11).trim());
                break;
            case "restore_local": // Recuperar variable local de pila
                traducirRestoreLocal(linea.substring(14).trim());
                break;
            default:
                // 3. ASIGNACIONES (t1 = a + b, t1 = x, arr[...] = y)
                if (linea.contains("=")) {
                    traducirAsignacion(linea);
                } else {
                    // Si llega aquí, es algo que no entendemos
                    mips.append("    #ERROR. No se pudo traducir la linea ").append(linea).append("\n");
                }
                break;
        }
    }

    // --- MÉTODOS DE TRADUCCIÓN ---

    // Maneja operaciones matemáticas, lógicas y movimiento de datos
    private void traducirAsignacion(String linea) {
        // Seleccionar buffer correcto
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;

        // Caso especial: asignación desde llamada a función (t1 = call func)
        if (linea.contains("call ")) {
            traducirCall(linea); // Delegar a traducirCall
            return;
        }

        // Caso especial: Arreglos (lectura o escritura con [])
        if (linea.contains("[")) {
            traducirArreglo(linea); // Delegar a traducirArreglo
            return;
        }

        // Separar lado izquierdo (resultado) y derecho (expresión)
        String[] partes = linea.split("=", 2);
        String res = partes[0].trim(); // Variable destino
        String expr = partes[1].trim(); // Valor o expresión

        // Caso: Asignación de literal string (s = "Hola")
        if (expr.startsWith("\"") || expr.startsWith("'")) {
            currentBuffer.append("\n    # Asignación literal: ").append(res).append(" = ").append(expr).append("\n");
            cargarEnRegistro("$t0", expr); // Carga dirección del string
            currentBuffer.append("    sw $t0, v_").append(res).append("\n"); // Guarda en variable
            return;
        }

        String[] tokens = expr.split("\\s+");

        // Checar casteo: (float) val
        if (expr.startsWith("(float) ")) {
            String val = expr.substring(8).trim();
            traducirCastFloat(res, val); // Delegar a cast float
            return;
        }

        // Asignación simple: x = y ó x = 5
        if (tokens.length == 1) {
            String val = tokens[0];
            // Verificar si es literal float (tiene punto decimal)
            if (val.matches("-?\\d+\\.\\d+")) {
                currentBuffer.append("\n    # Asignación float literal: ").append(res).append(" = ").append(val)
                        .append("\n");
                currentBuffer.append("    li.s $f0, ").append(val).append("\n"); // Cargar inmediato float
                currentBuffer.append("    s.s $f0, v_").append(res).append("\n"); // Guardar
            } else {
                // Entero o Variable
                currentBuffer.append("\n    # Asignación simple: ").append(res).append(" = ").append(val).append("\n");
                cargarEnRegistro("$t0", val); // Cargar valor en temporal
                currentBuffer.append("    sw $t0, v_").append(res).append("\n"); // Guardar en destino
            }
        }
        // Operación binaria: t1 = op1 OP op2
        else if (tokens.length == 3) {
            String op = tokens[1]; // Operador
            currentBuffer.append("\n    # Operación: ").append(linea).append("\n");

            // Operaciones de punto flotante (sufrimadas en 'f', ej: +f, *f)
            if (op.endsWith("f")) {
                cargarEnRegistroFloat("$f0", tokens[0]); // Cargar primer operando
                cargarEnRegistroFloat("$f1", tokens[2]); // Cargar segundo operando

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
                currentBuffer.append("    s.s $f2, v_").append(res).append("\n"); // Guardar resultado float
                return;
            }

            // Operaciones enteras
            cargarEnRegistro("$t0", tokens[0]); // Op1
            cargarEnRegistro("$t1", tokens[2]); // Op2

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
                    break; // Modulo
                // Operadores relacionales (generan 1 o 0)
                case "==":
                    currentBuffer.append("    seq $t2, $t0, $t1\n");
                    break; // Set Equal chido
                case "!=":
                    currentBuffer.append("    sne $t2, $t0, $t1\n");
                    break; // Set Not Equal
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
                // Lógicos bitwise
                case "and":
                    currentBuffer.append("    and $t2, $t0, $t1\n");
                    break;
                case "or":
                    currentBuffer.append("    or $t2, $t0, $t1\n");
                    break;
                case "^": // Potencia
                    currentBuffer.append("    # Potencia usando subrutina\n");
                    currentBuffer.append("    move $a0, $t0\n"); // Base
                    currentBuffer.append("    move $a1, $t1\n"); // Esp
                    currentBuffer.append("    jal _pow\n"); // Llamar funcion interna pow
                    currentBuffer.append("    move $t2, $v0\n"); // Resultado
                    break;
            }
            currentBuffer.append("    sw $t2, v_").append(res).append("\n"); // Guardar resultado
        }
        // Operaciones unarias (not)
        else if (tokens.length == 2) {
            String op = tokens[0];
            String val = tokens[1];
            if (op.equals("not")) {
                currentBuffer.append("\n    # Operacion Not: ").append(linea).append("\n");
                cargarEnRegistro("$t0", val);
                // Si t0 es 0, seq escribe 1. Si t0 != 0, seq escribe 0. (Equivalente a NOT
                // lógico)
                currentBuffer.append("    seq $t0, $t0, $zero\n");
                currentBuffer.append("    sw $t0, v_").append(res).append("\n");
            }
        }
    }

    // Traduce: goto Label
    private void traducirGoto(String label) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Salto incondicional\n");
        currentBuffer.append("    j ").append(label).append("\n");
    }

    // Traduce: if cond goto Label
    private void traducirIf(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Salto condicional: ").append(linea).append("\n");
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1]; // Condición (variable)
        String label = tokens[3]; // Etiqueta destino

        cargarEnRegistro("$t0", cond);
        // Si $t0 no es zero (true), saltar
        currentBuffer.append("    bnez $t0, ").append(label).append("\n");
    }

    // Traduce: ifFalse cond goto Label
    private void traducirIfFalse(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Salto condicional inverso: ").append(linea).append("\n");
        String[] tokens = linea.split("\\s+");
        String cond = tokens[1];
        String label = tokens[3];

        cargarEnRegistro("$t0", cond);
        // Si $t0 es zero (false), saltar
        currentBuffer.append("    beqz $t0, ").append(label).append("\n");
    }

    // --- MÉTODOS PRINT ---

    private void traducirPrintInt(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir Int: ").append(val).append("\n");
        cargarEnRegistro("$a0", val); // Poner valor en argumento
        currentBuffer.append("    jal showInt\n"); // Llamar rutina
        printNewline(); // Salto de linea automatico
    }

    private void traducirPrintFloat(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir Float: ").append(val).append("\n");
        if (val.matches("-?\\d+\\.\\d+")) {
            currentBuffer.append("    li.s $f12, ").append(val).append("\n"); // Literal
        } else {
            currentBuffer.append("    l.s $f12, v_").append(val).append("\n"); // Variable
        }
        currentBuffer.append("    jal showFloat\n");
        printNewline();
    }

    private void traducirPrintString(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir String: ").append(val).append("\n");
        if (val.startsWith("\"")) {
            // Literal "Texto"
            String content = extraerString(val);
            String label = strings.get(content); // Buscar etiqueta .data asociada
            currentBuffer.append("    la $a0, ").append(label).append("\n"); // Cargar dirección
        } else {
            // Variable string (puntero)
            currentBuffer.append("    lw $a0, v_").append(val).append("\n");
        }
        currentBuffer.append("    jal showString\n");
        printNewline();
    }

    private void traducirPrintChar(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Imprimir Char: ").append(val).append("\n");
        if (val.startsWith("'")) {
            currentBuffer.append("    li $a0, ").append(val).append("\n"); // Literal char
        } else {
            currentBuffer.append("    lw $a0, v_").append(val).append("\n"); // Variable
        }
        currentBuffer.append("    jal showChar\n");
        printNewline();
    }

    // Imprime un \n
    private void printNewline() {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    la $a0, newline\n");
        currentBuffer.append("    jal showString\n");
    }

    // --- MÉTODOS READ ---

    // Lee entero de consola a variable
    private void traducirReadInt(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer Int: ").append(var).append("\n");
        currentBuffer.append("    jal readInt\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("\n");
    }

    // Lee float de consola a variable
    private void traducirReadFloat(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer Float: ").append(var).append("\n");
        currentBuffer.append("    jal readFloat\n");
        currentBuffer.append("    s.s $f0, v_").append(var).append("\n");
    }

    // Lee char de consola a variable
    private void traducirReadChar(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer Char: ").append(var).append("\n");
        currentBuffer.append("    jal readChar\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("\n");
    }

    // Lee string de consola a variable
    private void traducirReadString(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Leer String: ").append(var).append("\n");
        currentBuffer.append("    jal readString\n");
        currentBuffer.append("    sw $v0, v_").append(var).append("\n");
        ensureVariableExists(var); // Asegurar declaracion
    }

    // --- FUNCIONES Y PILA ---

    // Recupera un parámetro de la pila (offset positivo desde $sp)
    private void traducirGetStack(String args) {
        // args: variable, offset
        String[] parts = args.split(",");
        String var = parts[0].trim(); // Variable donde guardar lo recuperado
        String offset = parts[1].trim(); // Posición en pila

        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Cargar parametro dsd pila: ").append(var).append(" <- ").append(offset)
                .append("($sp)\n");
        currentBuffer.append("    lw $t0, ").append(offset).append("($sp)\n"); // Leer de pila
        currentBuffer.append("    sw $t0, v_").append(var).append("\n"); // Guardar en variable local
    }

    // Empuja un parámetro a la pila antes de llamar función
    private void traducirParam(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Parámetro de función: ").append(val).append("\n");
        cargarEnRegistro("$t0", val);
        currentBuffer.append("    subu $sp, $sp, 4\n"); // Crecer pila (hacia abajo)
        currentBuffer.append("    sw $t0, ($sp)\n"); // Guardar valor
    }

    // Guarda una variable local en la pila (util para recursión)
    private void traducirSaveLocal(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Guardar local en pila: ").append(var).append("\n");
        currentBuffer.append("    lw $t0, v_").append(var).append("\n");
        currentBuffer.append("    subu $sp, $sp, 4\n");
        currentBuffer.append("    sw $t0, ($sp)\n");
    }

    // Restaura una variable local desde la pila
    private void traducirRestoreLocal(String var) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Restaurar local de pila: ").append(var).append("\n");
        currentBuffer.append("    lw $t0, ($sp)\n"); // Leer tope pila
        currentBuffer.append("    addu $sp, $sp, 4\n"); // Liberar espacio (decrecer pila)
        currentBuffer.append("    sw $t0, v_").append(var).append("\n");
    }

    // Traduce la llamada a función
    private void traducirCall(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Llamada a función: ").append(linea).append("\n");
        // Formatos: "call func, n" O "t1 = call func, n"
        String func;
        if (linea.contains("=")) {
            String expr = linea.split("=")[1].trim();
            func = expr.split("\\s+")[1].replace(",", "");
        } else {
            func = linea.split("\\s+")[1].replace(",", "");
        }

        // Emitir instrucción Jump and Link
        currentBuffer.append("    jal ").append(func).append("\n");

        // Limpieza de parámetros de la pila (quitar n argumentos)
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
                /* ignorar error parseo */ }
        }

        // Si hay asignación de retorno (res = call...), mover $v0 a la variable
        if (linea.contains("=")) {
            String res = linea.split("=")[0].trim();
            currentBuffer.append("    sw $v0, v_").append(res).append("\n");
        }
    }

    // Traduce retorno de función
    private void traducirReturn(String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("\n    # Retorno de función\n");
        if (val != null) {
            cargarEnRegistro("$v0", val); // Poner valor de retorno en $v0 (convención MIPS)
        }
        currentBuffer.append("    # Restaurar $ra y Frame\n");
        currentBuffer.append("    lw $ra, ($sp)\n"); // Recuperar dirección de retorno guardada en prologo
        currentBuffer.append("    addu $sp, $sp, 4\n"); // Liberar espacio del RA
        currentBuffer.append("    jr $ra\n"); // Retornar al llamante
    }

    /**
     * Helper para cargar un valor (entero literal, boolean, char o variable) en un
     * registro.
     * Abstrae la lógica de 'li' vs 'lw'.
     */
    private void cargarEnRegistro(String reg, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        if (val.matches("-?\\d+")) { // Entero literal
            currentBuffer.append("    li ").append(reg).append(", ").append(val).append("\n");
        } else if (val.equals("true")) { // Boolean true
            currentBuffer.append("    li ").append(reg).append(", 1\n");
        } else if (val.equals("false")) { // Boolean false
            currentBuffer.append("    li ").append(reg).append(", 0\n");
        } else if (val.startsWith("'")) {
            // Char literal 'A' -> convertir a ASCII
            int ascii = parseCharLiteral(val);
            currentBuffer.append("    li ").append(reg).append(", ").append(ascii).append("\n");
        } else if (val.startsWith("\"")) {
            // String literal "Hola" -> Cargar dirección (Load Address)
            String content = extraerString(val);
            String label = strings.get(content);
            if (label != null) {
                currentBuffer.append("    la ").append(reg).append(", ").append(label).append("\n");
            } else {
                currentBuffer.append("    # Error: String literal not found in .data\n");
            }
        } else {
            // Variable -> Cargar valor de memoria (Load Word)
            currentBuffer.append("    lw ").append(reg).append(", v_").append(val).append("\n");
        }
    }

    // --- MANEJO DE ARREGLOS ---

    private void traducirArreglo(String linea) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        // Dos casos:
        // 1. Asignacion a arreglo: arr[i][j] = val
        // 2. Lectura de arreglo: t1 = arr[i][j]

        int b1 = linea.indexOf("[");
        if (b1 == -1)
            return;

        String preBracket = linea.substring(0, b1).trim();
        // Si antes del primer '[' no hay '=', es porque el array está a la izquierda
        // (arr[...] = pos)
        boolean esAsignacionAArreglo = !preBracket.contains("=");

        if (esAsignacionAArreglo) {
            // Caso: arr[i][j] = val
            String[] parts = linea.split("=");
            String val = parts[1].trim(); // Valor a guardar
            String leftSide = parts[0].trim(); // arr[i][j]

            procesarDireccionArreglo(leftSide, "$t3"); // Calcular dirección de memoria en $t3

            cargarEnRegistro("$t0", val); // Cargar valor en $t0
            currentBuffer.append("    sw $t0, ($t3)\n"); // Guardar valor en dirección calculada

        } else {
            // Caso: t1 = arr[i][j]
            String[] parts = linea.split("=");
            String dest = parts[0].trim(); // Variable destino
            String rightSide = parts[1].trim(); // arr[i][j]

            procesarDireccionArreglo(rightSide, "$t3"); // Calcular dirección de memoria en $t3

            currentBuffer.append("    lw $t0, ($t3)\n"); // Leer valor de esa dirección
            currentBuffer.append("    sw $t0, v_").append(dest).append("\n"); // Guardar en variable destino
        }
    }

    // Calcula la dirección del elemento arr[i][j] y la pone en regDest
    // Fórmula: DirBase + ((i * Columnas) + j) * 4
    private void procesarDireccionArreglo(String acceso, String regDest) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        // acceso tiene la forma: arr[i][j]
        int b1 = acceso.indexOf("[");
        String arrName = acceso.substring(0, b1).trim(); // Nombre array

        // Parseo simplificado de indices arr[i][j]
        String resto = acceso.substring(b1); // [i][j]
        // Convertir a lista: i, j
        String[] indices = resto.replace("][", ",").replace("[", "").replace("]", "").split(",");
        String i = indices[0].trim();
        String j = indices[1].trim();

        // Obtener numero de columnas para este array
        int cols = arrayCols.getOrDefault(arrName, 0);

        // Calcular offset plano = (i * cols + j)

        cargarEnRegistro("$t1", i); // Cargar fila (i) en $t1
        currentBuffer.append("    li $t2, ").append(cols).append("\n"); // Cargar num columnas en $t2
        currentBuffer.append("    mul $t1, $t1, $t2\n"); // $t1 = i * cols

        cargarEnRegistro("$t2", j); // Cargar columna (j) en $t2
        currentBuffer.append("    add $t1, $t1, $t2\n"); // $t1 = (i * cols) + j

        // Multiplicar por 4 (tamaño de entero en bytes)
        currentBuffer.append("    mul $t1, $t1, 4\n");

        // Cargar dirección base del array en el registro destino
        currentBuffer.append("    la ").append(regDest).append(", v_").append(arrName).append("\n");

        // Sumar el offset a la dirección base
        currentBuffer.append("    add ").append(regDest).append(", ").append(regDest).append(", $t1\n");
    }

    // Casteo explícito de int a float
    private void traducirCastFloat(String res, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        currentBuffer.append("    # Cast to Float: ").append(res).append(" = (float) ").append(val).append("\n");
        cargarEnRegistro("$t0", val); // Cargar entero
        currentBuffer.append("    mtc1 $t0, $f0\n"); // Mover de registro CPU a Coprocesador 1 (Float)
        currentBuffer.append("    cvt.s.w $f0, $f0\n"); // Instrucción de conversión: word to single
        currentBuffer.append("    s.s $f0, v_").append(res).append("\n"); // Guardar float resultante
    }

    // Helper para cargar float en registro FPU ($f0, $f12, etc.)
    private void cargarEnRegistroFloat(String reg, String val) {
        StringBuilder currentBuffer = inFunction ? funcMips : mainMips;
        if (val.matches("-?\\d+(\\.\\d+)?")) { // Literal float
            currentBuffer.append("    li.s ").append(reg).append(", ").append(val).append("\n");
        } else { // Variable float
            currentBuffer.append("    l.s ").append(reg).append(", v_").append(val).append("\n");
        }
    }
}
