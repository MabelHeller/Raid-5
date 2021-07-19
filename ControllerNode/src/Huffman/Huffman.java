/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Huffman;

import java.io.*;
import java.util.*;

/**
 *
 * @author Heller
 */
public class Huffman {
  static Map<Byte,String>huffmanCodes=new HashMap<>();// Como a-> 1001
/*
   * Descripción de la función: encapsule el proceso de codificación de Huffman para llamar fácilmente
   * @param: contentbytes La matriz de bytes correspondiente al contenido a transmitir
 * @return byte [] La cadena larga binaria completa de Huffman se convierte a forma de byte para su transmisión
        */
    public static byte[] huffmanZip(byte[] contentbytes){
        List<treeNode> contentlist=getList(contentbytes);// Convierta la matriz de bytes en una Lista compuesta por Nodo
        treeNode root=createhuffmantree(contentlist);
        root.preOreder();
        huffmanCodes=getCodes(root);
        System.out.println("El código Huffman correspondiente a cada personaje:"+huffmanCodes);
        byte[] zipCodes=zip(contentbytes,huffmanCodes);
        System.out.println("La cadena completa de Huffman se convierte en forma de bytes para su transmisión:");
        System.out.println(Arrays.toString(zipCodes));
        return zipCodes;
    }

    /**
           * Método de escritura, comprime un archivo
           * @param srcFile La ruta completa del archivo que desea comprimir
           * @param dstFile ¿Dónde colocamos el archivo comprimido después de la compresión?
     */
    public static void zipFile(String srcFile, String dstFile) {
        // Crear flujo de entrada de archivo
        FileInputStream is=null;
        // Crear flujo de salida de archivo
        OutputStream os=null;
        ObjectOutputStream oos=null;
        try {
            is=new FileInputStream(srcFile);
            byte[] b=new byte[is.available()];// Crea una matriz de bytes [] con el mismo tamaño que el archivo
            is.read(b);// Leer el contenido del archivo y copiarlo en b
            byte[] huffmanBytes=huffmanZip(b);// Obtenga la cadena larga binaria completa de Huffman y conviértala en forma de byte para su transmisión
            os=new FileOutputStream(dstFile);// Crear flujo de salida de archivo, almacenar archivos comprimidos
            oos=new ObjectOutputStream(os);// Cree un ObjectOutputStream asociado con el flujo de salida del archivo
            oos.writeObject(huffmanBytes);// Escribe en forma de flujo de objetos
            oos.writeObject(huffmanCodes);// Tenga cuidado aquí, debe pasar la tabla de mapeo de codificación de Huffman, de lo contrario no se puede descomprimir

        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            try {
                oos.close();// El orden de cierre de la transmisión debe ser opuesto al orden de creación de la transmisión
                os.close();
                is.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
        }
    }
    }
    /**
           * Escribe un método para completar la descompresión de archivos comprimidos
           * @param zipFile El archivo a descomprimir
           * @param dstFile a qué ruta extraer el archivo
     */
    public static void unZipFile(String zipFile, String dstFile) {
        InputStream is=null;
        ObjectInputStream ois=null;
        OutputStream os=null;
        try{
            is=new FileInputStream(zipFile);
            ois=new ObjectInputStream(is);
            byte[] huffmanBytes=(byte[]) ois.readObject();
            Map<Byte,String>huffmanMap=(Map<Byte,String>)ois.readObject();
            byte[] finalBytes=decode(huffmanMap,huffmanBytes);//descodificación
            os=new FileOutputStream(dstFile);
            os.write(finalBytes);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            try {
                os.close();
                ois.close();
                is.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static List<treeNode> getList(byte[] contentbytes){
        ArrayList<treeNode> nodes=new ArrayList<treeNode>();
        // Recorre bytes y cuenta el número de ocurrencias de cada byte
        Map<Byte,Integer>counts=new HashMap<>();
        for(byte item:contentbytes){
            Integer count=counts.get(item);// Encuentra el valor correspondiente al elemento en el Mapa
            if(count==null){// Indica que este recuento aún no existe
                counts.put(item,1);
            }else {
                counts.put(item,count+1);
            }
        }
        // Convierta cada par de valores clave en treeNode y almacénelos en la colección de nodos
        // Recorre el mapa
        for(Map.Entry<Byte,Integer>entry:counts.entrySet()){
            nodes.add(new treeNode(entry.getKey(),entry.getValue()));
        }
        return nodes;

    }

    // Construye el árbol de Huffman
    public static treeNode createhuffmantree(List<treeNode> nodes){
        while(nodes.size()>1){
            Collections.sort(nodes);
            treeNode leftNode=nodes.get(0);
            treeNode rightNode=nodes.get(1);
            treeNode parentNode=new treeNode(null,leftNode.value+rightNode.value);// Los nodos que no son hojas solo tienen pesos, sin datos
            parentNode.left=leftNode;
            parentNode.right=rightNode;
            nodes.remove(0);
            nodes.remove(0);
            nodes.add(parentNode);
        }
        return nodes.get(0);
    }

    static Map<Byte,String>huffmancodes=new HashMap<>();// Como a-> 1001
    static StringBuilder stringBuilder=new StringBuilder();// Se usa para empalmar cadenas

/*
   * Descripción de la función: convierta el árbol Huffman en código Huffman y guárdelo en el mapa
   * @param: nodo: nodo entrante
   * @param: curcode: la ruta del nodo actual
 * @param: stringBuilder
*  @return void
        */
    public static void getCodes(treeNode node,String curcode,StringBuilder stringBuilder){
        StringBuilder stringBuilder2=new StringBuilder(stringBuilder);
        stringBuilder2.append(curcode);
        if(node!=null){// No procesar si el nodo está vacío
            // Determinar si es un nodo hoja
            if(node.data==null){// La descripción es un nodo no hoja
                getCodes(node.left,"0",stringBuilder2);
                getCodes(node.right,"1",stringBuilder2);
            }else{// Es un nodo hoja
                huffmancodes.put(node.data,stringBuilder2.toString());
            }

        }
    }
    /*
           * Descripción de la función: sobrecargue la función getcdes (haga que el formulario de referencia sea más conciso)
         * @param: pasa de nodo en el nodo raíz
         * @return Map <Byte, String> Mapa de codificación
            */
    public static Map<Byte,String> getCodes(treeNode node){
        if(node==null){
            return null;
        }
        getCodes(node.left,"0",stringBuilder);
        getCodes(node,"1",stringBuilder);
        return huffmancodes;
    }
    /*
           * Descripción de la función: la matriz de bytes [] correspondiente a la cadena se genera de acuerdo con el mapa de código de Huffman, y una cadena larga de códigos binarios de Huffman se genera y se convierte en byte []
           * @param: bytes son contentbytes
           * @param: tabla de mapeo huffmanCodes correspondiente al byte de cada carácter
           * @return byte [] El código de Huffman binario empalmado se convierte en byte []
            */
    public static byte[] zip(byte[] bytes,Map<Byte,String>huffmanCodes){
        // Obtenga el código binario completo de Huffman
        StringBuilder stringBuilder=new StringBuilder();
        for(byte b:bytes){
            stringBuilder.append(huffmanCodes.get(b));
        }
        System.out.println("Cadena binaria codificada"+stringBuilder.toString());

        // Calcula el número de bits convertidos de codificación binaria Huffman a byte []
        int len=0;
        if(stringBuilder.length()%8==0){
            len=stringBuilder.length()/8;
        }else{
            len=stringBuilder.length()/8+1;// Puede no ser divisible
        }

        // Crear matriz de bytes [] comprimidos
        int index=0;
        byte[] huffmanCodesByte=new byte[len];
        for (int i = 0; i <stringBuilder.length(); i=i+8) {
            String curString;
            if(stringBuilder.length()<i+8){
                curString=stringBuilder.substring(i);
            }else{
                curString=stringBuilder.substring(i,i+8);// índice de coordenadas de subcadena antes y después no incluido
            }
            huffmanCodesByte[index]=(byte)Integer.parseInt(curString,2);// Por defecto, curString está en formato binario, conviértalo a decimal Int, y luego convierta el tipo a byte
            index++;
        }
        return huffmanCodesByte;
    }
/*
   * Descripción de la función: convierte un byte en una cadena binaria
   * @param: si la bandera debe complementarse con bits altos, si es verdadera, debe complementarse, y false no. Si es el último byte, no es necesario llenar el bit alto
 * @param: b
 * @return java.lang.String es la cadena binaria correspondiente ab (tenga en cuenta que se devuelve en complemento)
        */
    public static String byteToBitString(boolean flag,byte b){
        int temp=b;// convertir b a int
        if (flag) {
            temp |=256;
        }
        String str=Integer.toBinaryString(temp);//Integer.toBinaryString devuelve el complemento a dos de temp. !
        if(flag){
            return str.substring(str.length()-8);
        }else{
            return str;
        }
    }
/*
   * Función descriptiva:
   * @param: huffmanCodes Tabla de mapeo de código de Huffman
   * @param: huffmanCodesByte matriz de bytes obtenida por codificación de Huffman
 * @return byte [] la matriz correspondiente a la cadena original
        */
    public static byte[] decode(Map<Byte,String>huffmanCodes,byte[] huffmanCodesByte){
        // 1, primero obtenga la cadena binaria correspondiente a huffmanCodesByte
        StringBuilder stringBuilder1=new StringBuilder();
        for (int i = 0; i <huffmanCodesByte.length; i++) {
            byte b=huffmanCodesByte[i];
            boolean flag=(i==huffmanCodesByte.length-1);
            stringBuilder1.append(byteToBitString(!flag,b));
        }
        System.out.println("Cadena binaria después de la decodificación"+stringBuilder1.toString());

        // Decodifica la cadena de acuerdo con la tabla de mapeo de Huffman
        // 1. Reemplace la tabla de asignación de codificación de Huffman, porque se requiere una consulta inversa
        Map<String,Byte>map=new HashMap<String,Byte>();// la clave es una cadena binaria, el valor es el byte correspondiente al carácter decodificado
        for(Map.Entry<Byte,String>entry:huffmanCodes.entrySet()){
            map.put(entry.getValue(),entry.getKey());
        }
        // Crea una colección para almacenar bytes
        List<Byte> list=new ArrayList<>();
        for (int i = 0; i <stringBuilder1.length() ; ) {
            int count=0;// Contador interno, debido a que el número de bits de Huffman correspondientes a cada carácter escaneado es incierto, el contador se borra cada vez que la búsqueda es exitosa
            boolean flag=true;
            while(flag){
                String search=stringBuilder1.substring(i,i+count);
                Byte b=map.get(search);
                if(b==null){
                    count++;
                }else{
                    flag=false;
                    list.add(b);
                }
            }
            i=i+count;
        }
        // Almacena los datos de la lista en bytes []
        byte[] listToByte=new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            listToByte[i]=list.get(i);
        }
        return listToByte;
    }

}
  