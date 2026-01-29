#!/bin/bash
# Script rápido para ejecutar varios tests C3D

cd /app/proyecto

echo "============================================"
echo "TESTS DE CÓDIGO DE TRES DIRECCIONES (C3D)"
echo "============================================"
echo ""

tests=(
    "01_expresiones_aritmeticas.c:Expresiones Aritméticas"
    "02_expresiones_logicas.c:Expresiones Lógicas"
    "04_for.c:Estructura FOR"
    "05_loop.c:Estructura LOOP"
    "06_funciones.c:Funciones"
    "08_unarios.c:Operadores Unarios"
    "09_reasignacion.c:Reasignaciones"
)

for test_info in "${tests[@]}"; do
    IFS=':' read -r file desc <<< "$test_info"
    echo "========================================="
    echo "TEST: $desc"
    echo "Archivo: $file"
    echo "========================================="
    java -cp ".:java-cup-11b-runtime.jar" Main "test/testC3D/$file" 2>&1 | grep -A 100 "CÓDIGO DE TRES DIRECCIONES"
    echo ""
    echo "Presiona Enter para continuar..."
    read
done

echo "============================================"
echo "TESTS COMPLETADOS"
echo "============================================"
