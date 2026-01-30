#!/bin/bash
echo "Limpiando archivos compilados..."
rm -f *.class

echo "Regenerando Lexer desde lexer.flex..."
java -jar jflex-1.9.1/lib/jflex-full-1.9.1.jar lexer.flex

echo "Regenerando parser desde parser.cup..."
java -jar java-cup-11b.jar -expect 0 -parser parser -symbols sym parser.cup

echo "Compilando archivos Java..."
javac -cp ".:java-cup-11b-runtime.jar" *.java

echo "Compilación completa!"
echo ""
echo "Uso: java -cp \".:java-cup-11b-runtime.jar\" Main <archivo_fuente>"
