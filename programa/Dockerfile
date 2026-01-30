FROM eclipse-temurin:17-jdk

WORKDIR /app

# Instalar las herramientas
RUN apt-get update && apt-get install -y \
    vim \
    nano \
    tree \
    && rm -rf /var/lib/apt/lists/*

# Copiar los archivos de JFlez
COPY jflex-1.9.1.tar.gz /tmp/
RUN tar xzf /tmp/jflex-1.9.1.tar.gz -C /app \
    && rm /tmp/jflex-1.9.1.tar.gz

ENV PATH="/app/jflex-1.9.1/bin:${PATH}"

# Copiar los archivos de Cup
COPY java-cup-bin-11b-20160615.tar.gz /tmp/
RUN tar xzf /tmp/java-cup-bin-11b-20160615.tar.gz -C /app \
    && rm /tmp/java-cup-bin-11b-20160615.tar.gz

ENV CLASSPATH="/app/java-cup-11b.jar:/app/java-cup-11b-runtime.jar:."

# Crear los archivos
RUN echo '#!/bin/bash\n\
echo "========================================"\n\
echo "Compiladores - Proyecto 1"\n\
echo "========================================"\n\
echo ""\n\
echo "Available commands:"\n\
echo "  jflex <file.flex>     - Generate lexer"\n\
echo "  java java_cup.Main <file.cup> - Generate parser"\n\
echo "  javac <files>         - Compile Java files"\n\
echo "  java <class>          - Run Java program"\n\
echo ""\n\
echo "Example workflow:"\n\
echo "  1. jflex lexer.flex"\n\
echo "  2. java java_cup.Main parser.cup"\n\
echo "  3. javac *.java"\n\
echo "  4. java Main input.txt"\n\
echo ""\n\
echo "Current directory: $(pwd)"\n\
echo "========================================"\n\
' > /usr/local/bin/help && chmod +x /usr/local/bin/help

# Crear el archivo de construccion
RUN echo '#!/bin/bash\n\
set -e\n\
echo "Building lexer..."\n\
if [ -f "lexer.flex" ]; then\n\
    jflex lexer.flex\n\
    echo "✓ Lexer generated"\n\
else\n\
    echo "⚠ lexer.flex not found"\n\
fi\n\
echo ""\n\
echo "Building parser..."\n\
if [ -f "parser.cup" ]; then\n\
    java java_cup.Main -expect 0 parser.cup\n\
    echo "✓ Parser generated"\n\
else\n\
    echo "⚠ parser.cup not found"\n\
fi\n\
echo ""\n\
echo "Compiling Java files..."\n\
if ls *.java 1> /dev/null 2>&1; then\n\
    javac *.java\n\
    echo "✓ Java files compiled"\n\
else\n\
    echo "⚠ No Java files found"\n\
fi\n\
echo ""\n\
echo "Build complete!"\n\
' > /usr/local/bin/build && chmod +x /usr/local/bin/build

# Crear el archivo clean
RUN echo '#!/bin/bash\n\
echo "Cleaning generated files..."\n\
rm -f *.class\n\
rm -f Lexer.java\n\
rm -f parser.java\n\
rm -f sym.java\n\
echo "✓ Clean complete"\n\
' > /usr/local/bin/clean && chmod +x /usr/local/bin/clean

# ayuda
CMD ["bash", "-c", "help && exec bash"]
