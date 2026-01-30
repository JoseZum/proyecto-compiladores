# --- MACROS DE ENTRADA/SALIDA Y POTENCIA ---

# Macro: Leer Entero (getInt)
.macro getInt(%var)
    li $v0, 5
    syscall
    sw $v0, %var
.end_macro

# Macro: Leer Flotante (getFloat)
.macro getFloat(%var)
    li $v0, 6
    syscall
    s.s $f0, %var
.end_macro

# Macro: Leer Cadena (getString)
.macro getString(%var, %len)
    la $a0, %var
    li $a1, %len
    li $v0, 8
    syscall
.end_macro

# Macro: Leer Carácter (getChar)
.macro getChar(%var)
    li $v0, 12
    syscall
    sb $v0, %var
.end_macro

# Macro: Mostrar Entero (showInt)
.macro showInt(%val)
    move $a0, %val
    li $v0, 1
    syscall
.end_macro

# Macro: Mostrar Flotante (showFloat)
.macro showFloat(%val)
    l.s $f12, %val
    li $v0, 2
    syscall
.end_macro

# Macro: Mostrar Cadena (showString)
.macro showString(%label)
    la $a0, %label
    li $v0, 4
    syscall
.end_macro

# Macro: Mostrar Carácter (showChar)
.macro showChar(%val)
    move $a0, %val
    li $v0, 11
    syscall
.end_macro

# Macro: Potencia (pow)
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
