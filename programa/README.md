# Compilador Navidad - Análisis Léxico y Sintáctico

Compilador desarrollado con JFlex y CUP.

## Requisitos

- Docker y Docker Compose

## Comandos

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
docker exec compiladores_pp1 bash -c 'cd /app/proyecto && java Main test/base_mal.c'
```

## Todo

```bash
docker exec compiladores_pp1 bash -c 'cd /app/proyecto && jflex lexer.flex && java java_cup.Main -parser parser -symbols sym -expect 1 parser.cup && javac *.java && java Main test/01_main_basico.txt'
```

## Borrar innecesarios

rm -f Lexer.class Lexer.java Main.class Nodo.class TestLexer.class parser$CUP$parser$actions.class parser$SymInfo.class parser.class parser.java sym.class sym.java test_completo.txt tokens_output.txt 

## Correr

cd programa
docker compose up -d --build

docker exec -it compiladores_pp1 bash

cd /app/proyecto
./recompile.sh

java -cp ".:java-cup-11b-runtime.jar" Main test/01_main_basico.txt
java Main "test/ejemplo código full.sintactico.base 7.c"