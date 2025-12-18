# Compiladores - Proyecto 1
## Entorno de Desarrollo con Docker

Este proyecto incluye un entorno Docker completo para desarrollar compiladores con JFlex y CUP.

## 🚀 Inicio Rápido

### 1. Construir y levantar el contenedor
```bash
docker-compose up -d --build
```

### 2. Acceder al contenedor
```bash
docker-compose exec compilador bash
```

### 3. Ver comandos disponibles
Una vez dentro del contenedor, el sistema mostrará automáticamente los comandos disponibles. También puedes ejecutar:
```bash
help
```

## 🛠️ Comandos Útiles

### Dentro del Contenedor

#### Generar Lexer
```bash
jflex lexer.flex
```

#### Generar Parser
```bash
java java_cup.Main parser.cup
```

#### Compilar todo automáticamente
```bash
build
```
Este comando:
- Genera el lexer desde `lexer.flex`
- Genera el parser desde `parser.cup`
- Compila todos los archivos `.java`

#### Limpiar archivos generados
```bash
clean
```

#### Compilar Java manualmente
```bash
javac *.java
```

#### Ejecutar el compilador
```bash
java Main archivo_entrada.txt
```

### Fuera del Contenedor

#### Detener el contenedor
```bash
docker-compose down
```

#### Ver logs del contenedor
```bash
docker-compose logs -f
```

#### Reconstruir el contenedor
```bash
docker-compose up -d --build --force-recreate
```

#### Ejecutar comando sin entrar al contenedor
```bash
docker-compose exec compilador build
```

## 📁 Estructura del Proyecto

```
programa/
├── Dockerfile              # Configuración de la imagen Docker
├── docker-compose.yml      # Orquestación del contenedor
├── README.md              # Este archivo
├── lexer.flex             # Especificación del lexer (por crear)
├── parser.cup             # Especificación del parser (por crear)
└── Main.java              # Clase principal (por crear)
```

## 🔧 Herramientas Incluidas

- **Java 17 JDK** - Entorno de ejecución
- **JFlex 1.9.1** - Generador de analizadores léxicos
- **CUP 11b** - Generador de analizadores sintácticos
- **vim, nano** - Editores de texto
- **tree** - Visualizador de estructura de directorios

## 📝 Flujo de Trabajo Típico

1. **Crear/editar** `lexer.flex` con las reglas léxicas
2. **Crear/editar** `parser.cup` con la gramática
3. **Ejecutar** `build` para generar y compilar todo
4. **Probar** con `java Main archivo_test.txt`
5. Si hay errores, **limpiar** con `clean` y repetir

## 💡 Tips

- Los archivos en `programa/` están montados en `/app/proyecto` dentro del contenedor
- Los cambios se reflejan inmediatamente (no necesitas reconstruir)
- Usa `clean` antes de `build` si tienes problemas
- El CLASSPATH ya incluye las librerías de CUP

## 🐛 Solución de Problemas

### El contenedor no inicia
```bash
docker-compose down
docker-compose up -d --build --force-recreate
```

### Permisos de archivos generados
Los archivos generados dentro del contenedor pueden tener permisos de root. Si necesitas modificarlos:
```bash
sudo chown -R $USER:$USER .
```

### Ver errores de compilación
```bash
docker-compose logs compilador
```

## 📚 Referencias

- [JFlex Manual](https://jflex.de/manual.html)
- [CUP Manual](http://www2.cs.tum.edu/projects/cup/manual.html)
- [Docker Compose Docs](https://docs.docker.com/compose/)
