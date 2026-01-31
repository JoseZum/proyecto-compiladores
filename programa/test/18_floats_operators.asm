# --- CÓDIGO MIPS GENERADO ---

.data
    newline: .asciiz "\n"
    # --- Strings Literales ---
    str1: .asciiz "10.5 + 2.5 = "
    str3: .asciiz "10.5 * 2.5 = "
    str8: .asciiz "(10.5 + 2.5) * 2.0 = "
    str5: .asciiz "10.5 + 5 = "
    str6: .asciiz "5 * 2.5 = "
    str7: .asciiz "-1.5 + 2.0 = "
    str2: .asciiz "10.5 - 2.5 = "
    str4: .asciiz "10.5 / 2.5 = "
    .align 2
    # --- Arrays ---
    .align 2
    # --- Variables y Temporales ---
    v_t4: .word 0
    v_t5: .word 0
    v_t6: .word 0
    v_t7: .word 0
    v_t8: .word 0
    v_t9: .word 0
    v_f: .word 0
    v_res_: .word 0
    v_float: .word 0
    v_t10: .word 0
    v_t12: .word 0
    v_t11: .word 0
    v_i_: .word 0
    v_b_: .word 0
    v_t1: .word 0
    v_a_: .word 0
    v_t2: .word 0
    v_t3: .word 0

.text
.globl main

main:
    # Salto al inicio del bloque principal (navidad)
    j navidad

    # MAIN navidad
navidad:
    # Asignación float literal: a_ = 10.5
    li.s $f0, 10.5
    s.s $f0, v_a_
    # Asignación float literal: b_ = 2.5
    li.s $f0, 2.5
    s.s $f0, v_b_
    # Asignación simple: i_ = 5
    li $t0, 5
    sw $t0, v_i_
    # Operación: t1 = a_ +f b_
    l.s $f0, v_a_
    l.s $f1, v_b_
    add.s $f2, $f0, $f1
    s.s $f2, v_t1
    # Asignación simple: res_ = t1
    lw $t0, v_t1
    sw $t0, v_res_
    # Imprimir String: ""10.5 + 2.5 = ""
    la $a0, str1
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Operación: t2 = a_ -f b_
    l.s $f0, v_a_
    l.s $f1, v_b_
    sub.s $f2, $f0, $f1
    s.s $f2, v_t2
    # Asignación simple: res_ = t2
    lw $t0, v_t2
    sw $t0, v_res_
    # Imprimir String: ""10.5 - 2.5 = ""
    la $a0, str2
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Operación: t3 = a_ *f b_
    l.s $f0, v_a_
    l.s $f1, v_b_
    mul.s $f2, $f0, $f1
    s.s $f2, v_t3
    # Asignación simple: res_ = t3
    lw $t0, v_t3
    sw $t0, v_res_
    # Imprimir String: ""10.5 * 2.5 = ""
    la $a0, str3
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Operación: t4 = a_ /f b_
    l.s $f0, v_a_
    l.s $f1, v_b_
    div.s $f2, $f0, $f1
    s.s $f2, v_t4
    # Asignación simple: res_ = t4
    lw $t0, v_t4
    sw $t0, v_res_
    # Imprimir String: ""10.5 / 2.5 = ""
    la $a0, str4
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Cast to Float: t5 = (float) i_
    lw $t0, v_i_
    mtc1 $t0, $f0
    cvt.s.w $f0, $f0
    s.s $f0, v_t5
    # Operación: t6 = a_ +f t5
    l.s $f0, v_a_
    l.s $f1, v_t5
    add.s $f2, $f0, $f1
    s.s $f2, v_t6
    # Asignación simple: res_ = t6
    lw $t0, v_t6
    sw $t0, v_res_
    # Imprimir String: ""10.5 + 5 = ""
    la $a0, str5
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Cast to Float: t7 = (float) i_
    lw $t0, v_i_
    mtc1 $t0, $f0
    cvt.s.w $f0, $f0
    s.s $f0, v_t7
    # Operación: t8 = t7 *f b_
    l.s $f0, v_t7
    l.s $f1, v_b_
    mul.s $f2, $f0, $f1
    s.s $f2, v_t8
    # Asignación simple: res_ = t8
    lw $t0, v_t8
    sw $t0, v_res_
    # Imprimir String: ""5 * 2.5 = ""
    la $a0, str6
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Asignación float literal: t9 = -1.5
    li.s $f0, -1.5
    s.s $f0, v_t9
    # Operación: t10 = t9 +f 2.0
    l.s $f0, v_t9
    li.s $f1, 2.0
    add.s $f2, $f0, $f1
    s.s $f2, v_t10
    # Asignación simple: res_ = t10
    lw $t0, v_t10
    sw $t0, v_res_
    # Imprimir String: ""-1.5 + 2.0 = ""
    la $a0, str7
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
    la $a0, newline
    jal showString
    # Operación: t11 = b_ *f 2.0
    l.s $f0, v_b_
    li.s $f1, 2.0
    mul.s $f2, $f0, $f1
    s.s $f2, v_t11
    # Operación: t12 = a_ +f t11
    l.s $f0, v_a_
    l.s $f1, v_t11
    add.s $f2, $f0, $f1
    s.s $f2, v_t12
    # Asignación simple: res_ = t12
    lw $t0, v_t12
    sw $t0, v_res_
    # Imprimir String: ""(10.5 + 2.5) * 2.0 = ""
    la $a0, str8
    jal showString
    la $a0, newline
    jal showString
    # Imprimir Float: res_
    l.s $f12, v_res_
    jal showFloat
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


