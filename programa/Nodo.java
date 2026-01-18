
/*
 * Clase nodo para construir los arboles sintacticos
 */

import java.util.ArrayList;

public class Nodo {
    //lexema del nodo
    String lexema;

    //tipo de dato del nodo
    String tipo;

    //lista de hijos de este nodo en el arbol sintactico
    ArrayList<Nodo> hijos;

    // Constructor principal
    public Nodo(String lexema) {
        this.lexema = lexema;
        this.tipo = "";
        this.hijos = new ArrayList<>();
    }

    // Constructor alternativo que acepta cualquier objeto como lexema
    public Nodo(Object lexema) {
        this.lexema = lexema != null ? lexema.toString() : "";
        this.tipo = "";
        this.hijos = new ArrayList<>();
    }

    // Asigna el tipo de dato al nodo
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Obtiene el tipo de dato del nodo
    public String getTipo() {
        return this.tipo;
    }

    // pone un hijo a la lista de hijos del padre
    public void addHijo(Nodo hijo) {
        this.hijos.add(hijo);
    }

    // Imprime el arbol sintactico visual y con su jerarquia

    public void arbol() {
        System.out.println("-----------ÁRBOL SINTÁCTICO-----------\n");
        imprimirArbol("", true);
    }

    //metodo para imrpir el arbol, es recursivo
    private void imprimirArbol(String prefijo, boolean esUltimo) {
        
        String conector = esUltimo ? "└── " : "├── ";

        String etiqueta = this.lexema;
        if (this.tipo != null && !this.tipo.isEmpty()) {
            etiqueta += " : " + this.tipo;
        }
        System.out.println(prefijo + conector + etiqueta);

        String prefijoHijos = prefijo + (esUltimo ? "    " : "│   ");

        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimoHijo = (i == hijos.size() - 1);
            hijos.get(i).imprimirArbol(prefijoHijos, ultimoHijo);
        }
    }
}
