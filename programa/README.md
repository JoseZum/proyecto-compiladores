# Compiladores - Guía Rápida

## Iniciar Docker

```powershell
# En Windows PowerShell (desde /programa)
docker compose up -d --build
docker compose exec compilador bash
```

## Probar Lexer

```bash
jflex lexer.flex
javac Lexer.java TestLexer.java
java TestLexer test.txt
```

## Build Completo (Lexer + Parser)

```bash
# Genera lexer, parser y compila todo
build

# Ejecutar con archivo de prueba
java TestLexer test.txt 
```

## Limpiar

```bash
clean
```

## Flujo Completo desde Cero

```powershell
# 1. Levantar Docker (PowerShell)
docker compose up -d --build
docker compose exec compilador bash

# 2. Dentro del contenedor - Probar solo lexer
jflex lexer.flex
javac Lexer.java TestLexer.java
java TestLexer test.txt

# 3. Build completo (lexer + parser)
build

# 4. Ejecutar
java TestLexer test.txt 

# 5. Limpiar si necesitas recompilar
clean
```
