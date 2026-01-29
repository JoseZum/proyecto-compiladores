#!/bin/bash

# Script para ejecutar todos los tests de C3D
# Uso: ./run_all_tests.sh

echo "========================================"
echo "EJECUTANDO TESTS DE CÓDIGO C3D"
echo "========================================"
echo ""

# Recompilar primero
echo "Recompilando proyecto..."
cd /app/proyecto
./recompile.sh
echo ""

# Contador de tests
total=0
passed=0
failed=0

# Ejecutar cada test
for file in test/testC3D/*.c; do
    ((total++))
    echo "========================================="
    echo "Test #$total: $(basename $file)"
    echo "========================================="
    
    if java -cp ".:java-cup-11b-runtime.jar" Main "$file" 2>&1 | grep -q "CODIGO DE TRES DIRECCIONES"; then
        echo "✓ PASSED: Código C3D generado correctamente"
        ((passed++))
    else
        echo "✗ FAILED: No se generó código C3D"
        ((failed++))
    fi
    
    echo ""
    echo "Presiona Enter para continuar al siguiente test..."
    read
done

# Resumen
echo "========================================="
echo "RESUMEN DE TESTS"
echo "========================================="
echo "Total: $total"
echo "Pasados: $passed"
echo "Fallados: $failed"
echo "========================================="
