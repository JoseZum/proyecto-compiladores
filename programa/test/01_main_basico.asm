# --- CÓDIGO MIPS GENERADO ---

.data
    newline: .asciiz "\n"
    # --- Strings Literales ---
    str5: .asciiz "Matriz[0][0] * 2 = "
    str4: .asciiz "10 + 50 = "
    str2: .asciiz "Matriz[0][1] = "
    str3: .asciiz "Matriz[1][2] = "
    str1: .asciiz "Matriz[0][0] = "
    str6: .asciiz "Matriz[i][j] donde i=1, j=2: "
    str7: .asciiz "Nuevo valor de Matriz[1][2] = "
    .align 2
    # --- Arrays ---
    v_matriz: .space 24
    .align 2
    # --- Variables y Temporales ---
    v_t4: .word 0
    v_suma: .word 0
    v_t5: .word 0
    v_t6: .word 0
    v_t7: .word 0
    v_t8: .word 0
    v_t9: .word 0
    v_i: .word 0
    v_j: .word 0
    v_t10: .word 0
    v_t12: .word 0
    v_t11: .word 0
    v_t14: .word 0
    v_t13: .word 0
    v_t16: .word 0
    v_t15: .word 0
    v_t18: .word 0
    v_t17: .word 0
    v_t1: .word 0
    v_t19: .word 0
    v_t2: .word 0
    v_t3: .word 0

.text
.globl main

main:
    # Salto al inicio del bloque principal (navidad)
    j navidad

    # MAIN navidad
navidad:
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t1
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 10
    sw $t0, ($t3)
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t2
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 20
    sw $t0, ($t3)
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 2
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t3
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 2
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 30
    sw $t0, ($t3)
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t4
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 40
    sw $t0, ($t3)
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t5
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 50
    sw $t0, ($t3)
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 2
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t6
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 2
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 60
    sw $t0, ($t3)
    # Imprimir String: ""Matriz[0][0] = ""
    la $a0, str1
    jal showString
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t7
    # Imprimir Int: t7
    lw $a0, v_t7
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: ""Matriz[0][1] = ""
    la $a0, str2
    jal showString
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t8
    # Imprimir Int: t8
    lw $a0, v_t8
    jal showInt
    la $a0, newline
    jal showString
    # Imprimir String: ""Matriz[1][2] = ""
    la $a0, str3
    jal showString
    la $a0, newline
    jal showString
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 2
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t9
    # Imprimir Int: t9
    lw $a0, v_t9
    jal showInt
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t10
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 1
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t11
    # Operación: t12 = t10 + t11
    lw $t0, v_t10
    lw $t1, v_t11
    add $t2, $t0, $t1
    sw $t2, v_t12
    # Asignación simple: suma = t12
    lw $t0, v_t12
    sw $t0, v_suma
    # Imprimir String: ""10 + 50 = ""
    la $a0, str4
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Int: suma
    lw $a0, v_suma
    jal showInt
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t13
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t14
    # Operación: t15 = t14 * 2
    lw $t0, v_t14
    li $t1, 2
    mul $t2, $t0, $t1
    sw $t2, v_t15
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, v_t15
    sw $t0, ($t3)
    # Imprimir String: ""Matriz[0][0] * 2 = ""
    la $a0, str5
    jal showString
    la $a0, newline
    jal showString
    li $t1, 0
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 0
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t16
    # Imprimir Int: t16
    lw $a0, v_t16
    jal showInt
    la $a0, newline
    jal showString
    # Asignación simple: i = 1
    li $t0, 1
    sw $t0, v_i
    # Asignación simple: j = 2
    li $t0, 2
    sw $t0, v_j
    # Imprimir String: ""Matriz[i][j] donde i=1, j=2: ""
    la $a0, str6
    jal showString
    la $a0, newline
    jal showString
    lw $t1, v_i
    li $t2, 3
    mul $t1, $t1, $t2
    lw $t2, v_j
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t17
    # Imprimir Int: t17
    lw $a0, v_t17
    jal showInt
    la $a0, newline
    jal showString
    lw $t1, v_i
    li $t2, 3
    mul $t1, $t1, $t2
    lw $t2, v_j
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t18
    lw $t1, v_i
    li $t2, 3
    mul $t1, $t1, $t2
    lw $t2, v_j
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    li $t0, 99
    sw $t0, ($t3)
    # Imprimir String: ""Nuevo valor de Matriz[1][2] = ""
    la $a0, str7
    jal showString
    la $a0, newline
    jal showString
    li $t1, 1
    li $t2, 3
    mul $t1, $t1, $t2
    li $t2, 2
    add $t1, $t1, $t2
    mul $t1, $t1, 4
    la $t3, v_matriz
    add $t3, $t3, $t1
    lw $t0, ($t3)
    sw $t0, v_t19
    # Imprimir Int: t19
    lw $a0, v_t19
    jal showInt
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


