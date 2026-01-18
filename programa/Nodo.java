/* package ParserLexer; */

import java.util.ArrayList;

public class Nodo {
    String lexema;
    String tipo;
    ArrayList<Nodo> hijos;

    public Nodo(String lexema) {
        this.lexema = lexema;
        this.tipo = "";
        this.hijos = new ArrayList<>();
    }

    // Constructor que acepta Object para facilitar el uso desde CUP
    public Nodo(Object lexema) {
        this.lexema = lexema != null ? lexema.toString() : "";
        this.tipo = "";
        this.hijos = new ArrayList<>();
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void addHijo(Nodo hijo) {
        this.hijos.add(hijo);
    }

    /**
     * Imprime el árbol sintáctico de forma visual
     */
    public void arbol() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              ÁRBOL SINTÁCTICO (AST)                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        imprimirArbol("", true);
    }

    /**
     * Método recursivo para imprimir el árbol con formato visual
     * @param prefijo El prefijo de indentación actual
     * @param esUltimo Si este nodo es el último hijo de su padre
     */
    private void imprimirArbol(String prefijo, boolean esUltimo) {
        // Determinar el conector
        String conector = esUltimo ? "└── " : "├── ";
        
        // Construir la etiqueta del nodo
        String etiqueta = this.lexema;
        if (this.tipo != null && !this.tipo.isEmpty()) {
            etiqueta += " : " + this.tipo;
        }
        
        // Imprimir este nodo
        System.out.println(prefijo + conector + etiqueta);
        
        // Preparar el prefijo para los hijos
        String prefijoHijos = prefijo + (esUltimo ? "    " : "│   ");
        
        // Imprimir los hijos
        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimoHijo = (i == hijos.size() - 1);
            hijos.get(i).imprimirArbol(prefijoHijos, ultimoHijo);
        }
    }
}
