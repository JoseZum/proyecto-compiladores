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
java Main test.txt
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
java Main test.txt

# 5. Limpiar si necesitas recompilar
clean
```

## Comandos Individuales

```bash
# Generar lexer
jflex lexer.flex

# Generar parser
java java_cup.Main parser.cup

# Compilar
javac *.java

# Ejecutar
java Main test.txt
java TestLexer test.txt
```

## Docker - Otros Comandos

```powershell
# Detener
docker compose down

# Reconstruir
docker compose up -d --build --force-recreate

# Ver logs
docker compose logs -f
```

# Estos son los que funcionan 
docker compose up -d --build
docker compose exec compilador bash
build
java TestLexer test.txt