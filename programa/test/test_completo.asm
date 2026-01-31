# --- CÓDIGO MIPS GENERADO ---

.data
    _input_buffer: .space 256
    newline: .asciiz "\n"
    # --- Strings Literales ---
    str21: .asciiz "--- TESTING SCOPE ---"
    str25: .asciiz "Placeholder"
    str14: .asciiz "For Loop 0 to 2:"
    str5: .asciiz "Global Bool (true/1):"
    str17: .asciiz "Factorial 5 = 120:"
    str16: .asciiz "Suma 10+20 = 30:"
    str18: .asciiz "--- TESTING ARRAYS ---"
    str28: .asciiz "--- ALL TESTS COMPLETED ---"
    str12: .asciiz "--- TESTING LOOPS ---"
    str13: .asciiz "Loop 0 to 4:"
    str19: .asciiz "Arreglo[0][0] (100):"
    str6: .asciiz "--- TESTING ARITHMETIC ---"
    str20: .asciiz "Arreglo[1][1] (400):"
    str15: .asciiz "--- TESTING FUNCTIONS ---"
    str2: .asciiz "--- TESTING GLOBALS ---"
    str22: .asciiz "Inner val (10):"
    str9: .asciiz "X es mayor que 15 (Correcto)"
    str26: .asciiz "Ingrese un nombre:"
    str3: .asciiz "Global X (10):"
    str11: .asciiz "Else (Incorrecto)"
    str4: .asciiz "Global F (3.14):"
    str1: .asciiz "Hola Mundo"
    str27: .asciiz "Hola:"
    str23: .asciiz "Outer val (5):"
    str7: .asciiz "10 + 5 * 2 = 20:"
    str10: .asciiz "X es 20 (No debe salir)"
    str24: .asciiz "--- TESTING READ STRING ---"
    str8: .asciiz "--- TESTING CONDITIONALS (DECIDE) ---"
    .align 2
    # --- Arrays ---
    v_arreglo: .space 24
    .align 2
    # --- Variables y Temporales ---
    v_get_stack: .word 0
    v_restore_local: .word 0
    v_t10: .word 0
    v_t12: .word 0
    v_t11: .word 0
    v_t14: .word 0
    v_t13: .word 0
    v_t16: .word 0
    v_t15: .word 0
    v_t18: .word 0
    v_t17: .word 0
    v_t19: .word 0
    v_val: .word 0
    v_t21: .word 0
    v_t20: .word 0
    v_true: .word 0
    v_t1: .word 0
    v_t2: .word 0
    v_t3: .word 0
    v_t4: .word 0
    v_suma: .word 0
    v_t5: .word 0
    v_t6: .word 0
    v_t7: .word 0
    v_t8: .word 0
    v_t9: .word 0
    v_factorial: .word 0
    v_nombre: .word 0
    v_global_flag: .word 0
    v_global_s: .word 0
    v_a: .word 0
    v_b: .word 0
    v_L1: .word 0
    v_L2: .word 0
    v_f: .word 0
    v_save_local: .word 0
    v_L3: .word 0
    v_L4: .word 0
    v_i: .word 0
    v_L5: .word 0
    v_global_x: .word 0
    v_j: .word 0
    v_L6: .word 0
    v_L10: .word 0
    v_L7: .word 0
    v_L8: .word 0
    v_L9: .word 0
    v_n: .word 0
    v_global_c: .word 0
    v_s: .word 0
    v_x: .word 0
    v_y: .word 0
    v_global_f: .word 0

.text
.globl main

main:
    # Asignación simple: global_x = 10
    li $t0, 10
    sw $t0, v_global_x
    # Asignación float literal: global_f = 3.14
    li.s $f0, 3.14
    s.s $f0, v_global_f
    # Asignación simple: global_flag = true
    li $t0, 1
    sw $t0, v_global_flag
    # Asignación literal: global_c = 'A'
    li $t0, 65
    sw $t0, v_global_c
    # Asignación literal: global_s = "Hola Mundo"
    la $t0, str1
    sw $t0, v_global_s
    # MAIN navidad
navidad:
    # Asignación simple: x = 0
    li $t0, 0
    sw $t0, v_x
    # Asignación simple: y = 20
    li $t0, 20
    sw $t0, v_y
    # Imprimir String: "--- TESTING GLOBALS ---"
    la $a0, str2
    jal showString
    la $a0, newline
    jal showString
    # Imprimir String: "Global X (10):"
    la $a0, str3
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: global_x
    lw $a0, v_global_x
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: "Global F (3.14):"
    la $a0, str4
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: global_f
    l.s $f12, v_global_f
    jal showFloat
    la $a0, newline
    jal showString
    # Imprimir String: "Global Bool (true/1):"
    la $a0, str5
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: global_flag
    lw $a0, v_global_flag
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: global_s
    lw $a0, v_global_s
    jal showString
    la $a0, newline
    jal showString
    # Imprimir String: "--- TESTING ARITHMETIC ---"
    la $a0, str6
    jal showString
    la $a0, newline
    jal showString
    # Operación: t6 = 5 * 2
    li $t0, 5
    li $t1, 2
    mul $t2, $t0, $t1
    sw $t2, v_t6
    # Operación: t7 = 10 + t6
    li $t0, 10
    lw $t1, v_t6
    add $t2, $t0, $t1
    sw $t2, v_t7
    # Asignación simple: x = t7
    lw $t0, v_t7
    sw $t0, v_x
    # Imprimir String: "10 + 5 * 2 = 20:"
    la $a0, str7
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: x
    lw $a0, v_x
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: "--- TESTING CONDITIONALS (DECIDE) ---"
    la $a0, str8
    jal showString
    la $a0, newline
    jal showString
    # Operación: t8 = x > 15
    lw $t0, v_x
    li $t1, 15
    sgt $t2, $t0, $t1
    sw $t2, v_t8
    # Salto condicional inverso: ifFalse t8 goto L2
    lw $t0, v_t8
    beqz $t0, L2
    # Imprimir String: "X es mayor que 15 (Correcto)"
    la $a0, str9
    jal showString
    la $a0, newline
    jal showString
L2:
    # Operación: t9 = x == 20
    lw $t0, v_x
    li $t1, 20
    seq $t2, $t0, $t1
    sw $t2, v_t9
    # Salto condicional inverso: ifFalse t9 goto L3
    lw $t0, v_t9
    beqz $t0, L3
    # Imprimir String: "X es 20 (No debe salir)"
    la $a0, str10
    jal showString
    la $a0, newline
    jal showString
L3:
    # ELSE
    # Imprimir String: "Else (Incorrecto)"
    la $a0, str11
    jal showString
    la $a0, newline
    jal showString
    # DECIDE fin
    # Imprimir String: "--- TESTING LOOPS ---"
    la $a0, str12
    jal showString
    la $a0, newline
    jal showString
    # Imprimir String: "Loop 0 to 4:"
    la $a0, str13
    jal showString
    la $a0, newline
    jal showString
    # Asignación simple: i = 0
    li $t0, 0
    sw $t0, v_i
    # LOOP inicio
L4:
    # Imprimir Int: i
    lw $a0, v_i
    jal showInt
    la $a0, newline
    jal showString
    # Operación: t10 = i + 1
    lw $t0, v_i
    li $t1, 1
    add $t2, $t0, $t1
    sw $t2, v_t10
    # Asignación simple: i = t10
    lw $t0, v_t10
    sw $t0, v_i
    # Operación: t11 = i == 5
    lw $t0, v_i
    li $t1, 5
    seq $t2, $t0, $t1
    sw $t2, v_t11
    # Salto condicional: if t11 goto L5
    lw $t0, v_t11
    bnez $t0, L5
    # Salto incondicional
    j L4
L5:
    # LOOP fin
    # Imprimir String: "For Loop 0 to 2:"
    la $a0, str14
    jal showString
    la $a0, newline
    jal showString
    # FOR inicio
    # Asignación simple: j = 0
    li $t0, 0
    sw $t0, v_j
L6:
    # Operación: t12 = j < 3
    lw $t0, v_j
    li $t1, 3
    slt $t2, $t0, $t1
    sw $t2, v_t12
    # Salto condicional inverso: ifFalse t12 goto L7
    lw $t0, v_t12
    beqz $t0, L7
    # Salto incondicional
    j L9
L8:
    # Operación: j = j + 1
    lw $t0, v_j
    li $t1, 1
    add $t2, $t0, $t1
    sw $t2, v_j
    # Salto incondicional
    j L6
L9:
    # Imprimir Int: j
    lw $a0, v_j
    jal showInt
    la $a0, newline
    jal showString
    # Salto incondicional
    j L8
L7:
    # FOR fin
    # Imprimir String: "--- TESTING FUNCTIONS ---"
    la $a0, str15
    jal showString
    la $a0, newline
    jal showString
    # Guardar local en pila: x
    lw $t0, v_x
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Guardar local en pila: y
    lw $t0, v_y
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Guardar local en pila: i
    lw $t0, v_i
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Parámetro de función: 10
    li $t0, 10
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Parámetro de función: 20
    li $t0, 20
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Llamada a función: t13 = call suma, 2
    jal suma
    addu $sp, $sp, 8 # Limpiar 2 params
    sw $v0, v_t13
    # Restaurar local de pila: i
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_i
    # Restaurar local de pila: y
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_y
    # Restaurar local de pila: x
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_x
    # Asignación simple: s = t13
    lw $t0, v_t13
    sw $t0, v_s
    # Imprimir String: "Suma 10+20 = 30:"
    la $a0, str16
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: s
    lw $a0, v_s
    jal showInt
    la $a0, newline
    jal showString
    # Guardar local en pila: s
    lw $t0, v_s
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Guardar local en pila: x
    lw $t0, v_x
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Guardar local en pila: y
    lw $t0, v_y
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Guardar local en pila: i
    lw $t0, v_i
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Parámetro de función: 5
    li $t0, 5
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Llamada a función: t14 = call factorial, 1
    jal factorial
    addu $sp, $sp, 4 # Limpiar 1 params
    sw $v0, v_t14
    # Restaurar local de pila: i
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_i
    # Restaurar local de pila: y
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_y
    # Restaurar local de pila: x
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_x
    # Restaurar local de pila: s
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_s
    # Asignación simple: f = t14
    lw $t0, v_t14
    sw $t0, v_f
    # Imprimir String: "Factorial 5 = 120:"
    la $a0, str17
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: f
    lw $a0, v_f
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: "--- TESTING ARRAYS ---"
    la $a0, str18
    jal showString
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t15
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    li $t0, 100
    sw $t0, ($t3)
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t16
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    li $t0, 200
    sw $t0, ($t3)
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t17
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    li $t0, 300
    sw $t0, ($t3)
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t18
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    li $t0, 400
    sw $t0, ($t3)
    # Imprimir String: "Arreglo[0][0] (100):"
    la $a0, str19
    jal showString
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t19
    # Imprimir Int: t19
    lw $a0, v_t19
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: "Arreglo[1][1] (400):"
    la $a0, str20
    jal showString
    la $a0, newline
    jal showString
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_arreglo
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t20
    # Imprimir Int: t20
    lw $a0, v_t20
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: "--- TESTING SCOPE ---"
    la $a0, str21
    jal showString
    la $a0, newline
    jal showString
    # Asignación simple: val = 5
    li $t0, 5
    sw $t0, v_val
    # Operación: t21 = val > 0
    lw $t0, v_val
    li $t1, 0
    sgt $t2, $t0, $t1
    sw $t2, v_t21
    # Salto condicional inverso: ifFalse t21 goto L10
    lw $t0, v_t21
    beqz $t0, L10
    # Asignación simple: val = 10
    li $t0, 10
    sw $t0, v_val
    # Imprimir String: "Inner val (10):"
    la $a0, str22
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: val
    lw $a0, v_val
    jal showInt
    la $a0, newline
    jal showString
L10:
    # DECIDE fin
    # Imprimir String: "Outer val (5):"
    la $a0, str23
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: val
    lw $a0, v_val
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: "--- TESTING READ STRING ---"
    la $a0, str24
    jal showString
    la $a0, newline
    jal showString
    # Asignación literal: nombre = "Placeholder"
    la $t0, str25
    sw $t0, v_nombre
    # Imprimir String: "Ingrese un nombre:"
    la $a0, str26
    jal showString
    la $a0, newline
    jal showString
    # Leer String: nombre
    jal readString
    sw $v0, v_nombre
    # Imprimir String: "Hola:"
    la $a0, str27
    jal showString
    la $a0, newline
    jal showString
    # Imprimir String: nombre
    lw $a0, v_nombre
    jal showString
    la $a0, newline
    jal showString
    # Imprimir String: "--- ALL TESTS COMPLETED ---"
    la $a0, str28
    jal showString
    la $a0, newline
    jal showString
    # END MAIN
    li $v0, 10
    syscall

    # Fin del programa (Exit)
    li $v0, 10
    syscall

    # FUNC suma
suma:
    # Reserva de Frame y guardado de $ra
    subu $sp, $sp, 4
    sw $ra, ($sp)
    # Cargar parametro dsd pila: a <- 8($sp)
    lw $t0, 8($sp)
    sw $t0, v_a
    # Cargar parametro dsd pila: b <- 4($sp)
    lw $t0, 4($sp)
    sw $t0, v_b
    # Operación: t1 = a + b
    lw $t0, v_a
    lw $t1, v_b
    add $t2, $t0, $t1
    sw $t2, v_t1
    # Retorno de función
    lw $v0, v_t1
    # Restaurar $ra y Frame
    lw $ra, ($sp)
    addu $sp, $sp, 4
    jr $ra
    # END FUNC suma
    # FUNC factorial
factorial:
    # Reserva de Frame y guardado de $ra
    subu $sp, $sp, 4
    sw $ra, ($sp)
    # Cargar parametro dsd pila: n <- 4($sp)
    lw $t0, 4($sp)
    sw $t0, v_n
    # Operación: t2 = n == 0
    lw $t0, v_n
    li $t1, 0
    seq $t2, $t0, $t1
    sw $t2, v_t2
    # Salto condicional inverso: ifFalse t2 goto L1
    lw $t0, v_t2
    beqz $t0, L1
    # Retorno de función
    li $v0, 1
    # Restaurar $ra y Frame
    lw $ra, ($sp)
    addu $sp, $sp, 4
    jr $ra
L1:
    # ELSE
    # Operación: t3 = n - 1
    lw $t0, v_n
    li $t1, 1
    sub $t2, $t0, $t1
    sw $t2, v_t3
    # Guardar local en pila: n
    lw $t0, v_n
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Parámetro de función: t3
    lw $t0, v_t3
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Llamada a función: t4 = call factorial, 1
    jal factorial
    addu $sp, $sp, 4 # Limpiar 1 params
    sw $v0, v_t4
    # Restaurar local de pila: n
    lw $t0, ($sp)
    addu $sp, $sp, 4
    sw $t0, v_n
    # Operación: t5 = n * t4
    lw $t0, v_n
    lw $t1, v_t4
    mul $t2, $t0, $t1
    sw $t2, v_t5
    # Retorno de función
    lw $v0, v_t5
    # Restaurar $ra y Frame
    lw $ra, ($sp)
    addu $sp, $sp, 4
    jr $ra
    # DECIDE fin
    # END FUNC factorial

# --- RUTINAS DE SISTEMA ---
showInt:
    li $v0, 1
    syscall
    jr $ra
.end showInt

showString:
    li $v0, 4
    syscall
    jr $ra
.end showString

showFloat:
    li $v0, 2
    syscall
    jr $ra
.end showFloat

showChar:
    li $v0, 11
    syscall
    jr $ra
.end showChar

readInt:
    li $v0, 5
    syscall
    jr $ra
.end readInt

readFloat:
    li $v0, 6
    syscall
    jr $ra
.end readFloat

readString:
    li $v0, 8
    la $a0, _input_buffer
    li $a1, 255
    syscall
    # Allocate heap memory for string
    li $v0, 9
    li $a0, 256
    syscall
    move $t3, $v0
    la $t1, _input_buffer
_copy_loop:
    lb $t2, ($t1)
    sb $t2, ($t3)
    beqz $t2, _copy_end
    addi $t1, $t1, 1
    addi $t3, $t3, 1
    j _copy_loop
_copy_end:
    # $v0 already has the start address from sbrk
    jr $ra
.end readString

readChar:
    li $v0, 12
    syscall
    jr $ra
.end readChar

pow:
    li $v0, 1
_pow_loop:
    blez $a1, _pow_end
    mul $v0, $v0, $a0
    sub $a1, $a1, 1
    j _pow_loop
_pow_end:
    jr $ra
.end pow


