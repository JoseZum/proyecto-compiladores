# Tests de Código de Tres Direcciones (C3D)

Este directorio contiene archivos de prueba para validar la generación de código de tres direcciones (C3D).

## Archivos de Prueba

1. **01_expresiones_aritmeticas.c** - Operaciones aritméticas (+, -, *, /, //, %, ^)
2. **02_expresiones_logicas.c** - Operaciones lógicas y relacionales (and, or, not, <, >, ==, etc.)
3. **03_decide_of.c** - Estructura decide of con múltiples casos y else
4. **04_for.c** - Estructura for con inicialización, condición e incremento
5. **05_loop.c** - Estructura loop con exit when
6. **06_funciones.c** - Declaración y llamada de funciones con parámetros y return
7. **07_arreglos.c** - Declaración, inicialización y acceso a arreglos 2D
8. **08_unarios.c** - Operadores unarios (++, --)
9. **09_reasignacion.c** - Reasignaciones múltiples y complejas
10. **10_break_return.c** - Sentencias break y return

## Cómo Ejecutar

Desde dentro del contenedor Docker:

```bash
cd /app/proyecto

# Ejecutar un test individual
./recompile.sh
java -cp ".:java-cup-11b-runtime.jar" Main test/testC3D/01_expresiones_aritmeticas.c

# Ejecutar todos los tests
for file in test/testC3D/*.c; do
    echo "========================================="
    echo "Testing: $file"
    echo "========================================="
    java -cp ".:java-cup-11b-runtime.jar" Main "$file"
    echo ""
done
```

## Salida Esperada

Cada test debe generar:
- **Árbol Sintáctico**: Representación visual de la estructura del programa
- **Tabla de Símbolos**: Variables, funciones y sus tipos declarados en cada scope
- **Código C3D**: Instrucciones de tres direcciones generadas

## Validación

El código C3D generado debe incluir:
- Temporales (t1, t2, t3, ...)
- Etiquetas (L1, L2, L3, ...)
- Instrucciones de asignación (t1 = a + b)
- Saltos condicionales (if t1 < t2 goto L1)
- Saltos incondicionales (goto L2)
- Llamadas a funciones (param x, call func, t1 = retval)
- Acceso a arreglos (t1 = arr[i][j])
