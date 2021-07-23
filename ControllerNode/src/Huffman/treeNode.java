
package Huffman;

class treeNode implements Comparable<treeNode>{
    Byte data;//Datos almacenados
    int value;// Peso del nodo
    treeNode left;// Nodo hijo izquierdo
    treeNode right;// Nodo hijo derecho

    public treeNode(Byte data, int value) {
        this.data = data;
        this.value = value;
    }

    @Override
    public String toString() {
        return "treeNode{" +
                "data=" + data +
                ", value=" + value +
                '}';
    }

    @Override
    public int compareTo(treeNode o) {
        // Indica ordenar de pequeño a grande
        //return this.value-o.value;
        // Si significa ordenar de mayor a menor
        return -(this.value-o.value);
    }

    // Recorrido de reserva
    public void preOreder(){
        System.out.println(this);
        if(this.left!=null){
            this.left.preOreder();
        }
        if(this.right!=null){
            this.right.preOreder();
        }

    }
}