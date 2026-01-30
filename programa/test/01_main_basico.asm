# --- CÓDIGO MIPS GENERADO ---

.data
    newline: .asciiz "\n"
    # --- Strings Literales ---
    str4: .asciiz "Introduce un Flotante:"
    str1: .asciiz "--- TEST IO TYPES ---"
    str2: .asciiz "Introduce un Entero:"
    str3: .asciiz "Leido:"
    str5: .asciiz "Introduce un Caracter:"
    # --- Variables y Temporales ---
    v_a: .word 0
    v_read_float: .word 0
    v_c: .word 0
    v_print_int: .word 0
    v_read_char: .word 0
    v_f: .word 0
    v_print_string: .word 0
    v_print_char: .word 0
    v_read_int: .word 0
    v_print_float: .word 0

.text
.globl main

main:
    # MAIN navidad
    # Imprimir String: ""--- TEST IO TYPES ---""
    la $a0, str1
    jal showString
    la $a0, newline
    jal showString
    # Imprimir String: ""Introduce un Entero:""
    la $a0, str2
    jal showString
    la $a0, newline
    jal showString
    # Leer Int: a
    jal readInt
    sw $v0, v_a
    # Imprimir String: ""Leido:""
    la $a0, str3
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: a
    lw $a0, v_a
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: ""Introduce un Flotante:""
    la $a0, str4
    jal showString
    la $a0, newline
    jal showString
    # Leer Float: f
    jal readFloat
    s.s $f0, v_f
    # Imprimir String: ""Leido:""
    la $a0, str3
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: f
    l.s $f12, v_f
    jal showFloat
    la $a0, newline
    jal showString
    # Imprimir String: ""Introduce un Caracter:""
    la $a0, str5
    jal showString
    la $a0, newline
    jal showString
    # Leer Char: c
    jal readChar
    sw $v0, v_c
    # Imprimir String: ""Leido:""
    la $a0, str3
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Char: c
    lw $a0, v_c
    jal showChar
    la $a0, newline
    jal showString
    # END MAIN

    # Fin del programa (Exit)
    li $v0, 10
    syscall

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
    syscall
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


