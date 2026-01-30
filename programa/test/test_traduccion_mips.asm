# --- CÓDIGO MIPS GENERADO ---

.data
    newline: .asciiz "\n"
    # --- Strings Literales ---
    str4: .asciiz "Mult (50):"
    str8: .asciiz "x no es mayor que y"
    str9: .asciiz "Llamada Funcion Suma (15):"
    str1: .asciiz "--- INICIO TEST MIPS ---"
    str12: .asciiz "--- FIN TEST MIPS ---"
    str5: .asciiz "Div (2):"
    str7: .asciiz "x es mayor que y"
    str10: .asciiz "Llamada Funcion Potencia (8):"
    str2: .asciiz "Suma (15):"
    str6: .asciiz "Prueba IF (expect: x mayor que y):"
    str11: .asciiz "Prueba Ciclo (1 al 3):"
    str3: .asciiz "Resta (5):"
    # --- Variables y Temporales ---
    Ciclo: .word 0
    t10: .word 0
    t11: .word 0
    INICIO: .word 0
    exp: .word 0
    resDiv: .word 0
    IF: .word 0
    res: .word 0
    mayor: .word 0
    resMult: .word 0
    callPow: .word 0
    al: .word 0
    es: .word 0
    Suma: .word 0
    potencia: .word 0
    TEST: .word 0
    sumar: .word 0
    MIPS: .word 0
    Potencia: .word 0
    t1: .word 0
    t2: .word 0
    t3: .word 0
    t4: .word 0
    que: .word 0
    t5: .word 0
    no: .word 0
    callSuma: .word 0
    t6: .word 0
    t7: .word 0
    t8: .word 0
    t9: .word 0
    resResta: .word 0
    Resta: .word 0
    a: .word 0
    b: .word 0
    L1: .word 0
    L2: .word 0
    L3: .word 0
    i: .word 0
    FIN: .word 0
    resSuma: .word 0
    Div: .word 0
    Prueba: .word 0
    Funcion: .word 0
    Mult: .word 0
    x: .word 0
    y: .word 0
    Llamada: .word 0
    base: .word 0

.text
# --- MACROS DE ENTRADA/SALIDA Y POTENCIA ---

.macro getInt(%var)
    li $v0, 5
    syscall
    sw $v0, %var
.end_macro

.macro getFloat(%var)
    li $v0, 6
    syscall
    s.s $f0, %var
.end_macro

.macro getString(%var, %len)
    la $a0, %var
    li $a1, %len
    li $v0, 8
    syscall
.end_macro

.macro getChar(%var)
    li $v0, 12
    syscall
    sb $v0, %var
.end_macro

.macro showInt(%val)
    move $a0, %val
    li $v0, 1
    syscall
.end_macro

.macro showFloat(%val)
    l.s $f12, %val
    li $v0, 2
    syscall
.end_macro

.macro showString(%label)
    la $a0, %label
    li $v0, 4
    syscall
.end_macro

.macro showChar(%val)
    move $a0, %val
    li $v0, 11
    syscall
.end_macro

.macro pow(%base, %exp, %res)
    li %res, 1
    move $t8, %base
    move $t9, %exp
pow_loop:
    blez $t9, pow_end
    mul %res, %res, $t8
    addi $t9, $t9, -1
    j pow_loop
pow_end:
.end_macro

.globl main

main:
    # FUNC sumar
    # Reserva de Frame y guardado de $ra
    subu $sp, $sp, 4
    sw $ra, ($sp)
sumar:
    # Operación: t1 = a + b
    lw $t0, a
    lw $t1, b
    add $t2, $t0, $t1
    sw $t2, t1
    # Retorno de función
    lw $v0, t1
    # Restaurar $ra y Frame
    lw $ra, ($sp)
    addu $sp, $sp, 4
    jr $ra
    # END FUNC sumar
    # FUNC potencia
    # Reserva de Frame y guardado de $ra
    subu $sp, $sp, 4
    sw $ra, ($sp)
potencia:
    # Operación: t2 = base ^ exp
    lw $t0, base
    lw $t1, exp
    # Potencia usando macro
    pow($t0, $t1, $t2)
    sw $t2, t2
    # Asignación simple: res = t2
    lw $t0, t2
    sw $t0, res
    # Retorno de función
    lw $v0, res
    # Restaurar $ra y Frame
    lw $ra, ($sp)
    addu $sp, $sp, 4
    jr $ra
    # END FUNC potencia
    # MAIN navidad
    # Imprimir usando macro: ""--- INICIO TEST MIPS ---""
    showString(str1)
    showString(newline)
    # Asignación simple: x = 10
    li $t0, 10
    sw $t0, x
    # Asignación simple: y = 5
    li $t0, 5
    sw $t0, y
    # Operación: t3 = x + y
    lw $t0, x
    lw $t1, y
    add $t2, $t0, $t1
    sw $t2, t3
    # Asignación simple: resSuma = t3
    lw $t0, t3
    sw $t0, resSuma
    # Operación: t4 = x - y
    lw $t0, x
    lw $t1, y
    sub $t2, $t0, $t1
    sw $t2, t4
    # Asignación simple: resResta = t4
    lw $t0, t4
    sw $t0, resResta
    # Operación: t5 = x * y
    lw $t0, x
    lw $t1, y
    mul $t2, $t0, $t1
    sw $t2, t5
    # Asignación simple: resMult = t5
    lw $t0, t5
    sw $t0, resMult
    # Operación: t6 = x / y
    lw $t0, x
    lw $t1, y
    div $t2, $t0, $t1
    sw $t2, t6
    # Asignación simple: resDiv = t6
    lw $t0, t6
    sw $t0, resDiv
    # Imprimir usando macro: ""Suma (15):""
    showString(str2)
    showString(newline)
    # Imprimir usando macro: resSuma
    lw $t0, resSuma
    showInt($t0)
    showString(newline)
    # Imprimir usando macro: ""Resta (5):""
    showString(str3)
    showString(newline)
    # Imprimir usando macro: resResta
    lw $t0, resResta
    showInt($t0)
    showString(newline)
    # Imprimir usando macro: ""Mult (50):""
    showString(str4)
    showString(newline)
    # Imprimir usando macro: resMult
    lw $t0, resMult
    showInt($t0)
    showString(newline)
    # Imprimir usando macro: ""Div (2):""
    showString(str5)
    showString(newline)
    # Imprimir usando macro: resDiv
    lw $t0, resDiv
    showInt($t0)
    showString(newline)
    # Imprimir usando macro: ""Prueba IF (expect: x mayor que y):""
    showString(str6)
    showString(newline)
    # Operación: t7 = x > y
    lw $t0, x
    lw $t1, y
    sgt $t2, $t0, $t1
    sw $t2, t7
    # Salto condicional inverso: ifFalse t7 goto L1
    lw $t0, t7
    beqz $t0, L1
    # Imprimir usando macro: ""x es mayor que y""
    showString(str7)
    showString(newline)
L1:
    # ELSE
    # Imprimir usando macro: ""x no es mayor que y""
    showString(str8)
    showString(newline)
    # DECIDE fin
    # Parámetro de función: x
    lw $t0, x
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Parámetro de función: y
    lw $t0, y
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Llamada a función: t8 = call sumar, 2
    jal sumar
    addu $sp, $sp, 8 # Limpiar 2 params
    sw $v0, t8
    # Asignación simple: callSuma = t8
    lw $t0, t8
    sw $t0, callSuma
    # Imprimir usando macro: ""Llamada Funcion Suma (15):""
    showString(str9)
    showString(newline)
    # Imprimir usando macro: callSuma
    lw $t0, callSuma
    showInt($t0)
    showString(newline)
    # Parámetro de función: 2
    li $t0, 2
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Parámetro de función: 3
    li $t0, 3
    subu $sp, $sp, 4
    sw $t0, ($sp)
    # Llamada a función: t9 = call potencia, 2
    jal potencia
    addu $sp, $sp, 8 # Limpiar 2 params
    sw $v0, t9
    # Asignación simple: callPow = t9
    lw $t0, t9
    sw $t0, callPow
    # Imprimir usando macro: ""Llamada Funcion Potencia (8):""
    showString(str10)
    showString(newline)
    # Imprimir usando macro: callPow
    lw $t0, callPow
    showInt($t0)
    showString(newline)
    # Imprimir usando macro: ""Prueba Ciclo (1 al 3):""
    showString(str11)
    showString(newline)
    # Asignación simple: i = 1
    li $t0, 1
    sw $t0, i
    # LOOP inicio
L2:
    # Imprimir usando macro: i
    lw $t0, i
    showInt($t0)
    showString(newline)
    # Operación: t10 = i + 1
    lw $t0, i
    li $t1, 1
    add $t2, $t0, $t1
    sw $t2, t10
    # Asignación simple: i = t10
    lw $t0, t10
    sw $t0, i
    # Operación: t11 = i > 3
    lw $t0, i
    li $t1, 3
    sgt $t2, $t0, $t1
    sw $t2, t11
    # Salto condicional: if t11 goto L3
    lw $t0, t11
    bnez $t0, L3
    # Salto incondicional
    j L2
L3:
    # LOOP fin
    # Imprimir usando macro: ""--- FIN TEST MIPS ---""
    showString(str12)
    showString(newline)
    # END MAIN

    # Fin del programa (Exit)
    li $v0, 10
    syscall

