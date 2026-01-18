# Compilador Navidad - Análisis Léxico y Sintáctico

Compilador para el lenguaje **Navidad** desarrollado con JFlex y CUP.

## 📋 Requisitos

- Docker y Docker Compose

## 🚀 Comandos

### 1. Crear y levantar el contenedor Docker

```bash
cd programa
docker compose up -d --build
```

### 2. Generar Lexer + Parser + Compilar

```bash
docker exec compiladores_pp1 bash -c 'cd /app/proyecto && jflex lexer.flex && java java_cup.Main -parser parser -symbols sym -expect 1 parser.cup && javac *.java'
```

### 3. Ejecutar el compilador

```bash
docker exec compiladores_pp1 bash -c 'cd /app/proyecto && java Main test_completo.txt'
```

## ⚡ Todo-en-Uno (desde cero)

```bash
docker exec compiladores_pp1 bash -c 'cd /app/proyecto && jflex lexer.flex && java java_cup.Main -parser parser -symbols sym -expect 1 parser.cup && javac *.java && java Main test_completo.txt'
```

## 📤 Salida del Compilador

El programa `Main` ejecuta dos fases:

1. **Fase 1 - Análisis Léxico:** Muestra todos los tokens y genera `tokens_output.txt`
2. **Fase 2 - Análisis Sintáctico:** Valida gramática, construye AST y muestra tablas de símbolos

## 🧹 Limpiar archivos generados

```bash
docker exec compiladores_pp1 bash -c 'cd /app/proyecto && rm -f *.class Lexer.java parser.java sym.java tokens_output.txt'
```

## 🔄 Detener/Reiniciar contenedor

```bash
# Detener
docker compose down

# Reiniciar
docker compose up -d
```

## 📁 Estructura del Proyecto

| Archivo | Descripción |
|---------|-------------|
| `lexer.flex` | Especificación JFlex para análisis léxico |
| `parser.cup` | Gramática CUP con acciones semánticas |
| `Nodo.java` | Clase para nodos del AST |
| `Main.java` | Punto de entrada (ejecuta léxico + sintáctico) |
| `test_completo.txt` | Archivo de prueba con todas las estructuras |
