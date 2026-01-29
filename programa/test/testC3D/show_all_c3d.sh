#!/bin/bash
# Script simple para ver el código C3D de todos los tests

cd /app/proyecto

echo "======================================================================"
echo "           CÓDIGO DE TRES DIRECCIONES - TODOS LOS TESTS"
echo "======================================================================"

for file in test/testC3D/0*.c; do
    echo ""
    echo "======================================================================"
    echo "Archivo: $(basename $file)"
    echo "======================================================================"
    java -cp ".:java-cup-11b-runtime.jar" Main "$file" 2>&1 | \
        sed -n '/CÓDIGO DE TRES DIRECCIONES/,/--------------------/p'
done

echo ""
echo "======================================================================"
echo "                        FIN DE TESTS"
echo "======================================================================"
