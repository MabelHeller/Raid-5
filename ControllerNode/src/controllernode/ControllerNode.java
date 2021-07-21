package controllernode;
import Huffman.Huffman;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ControllerNode {

    static int[] puertosDiscos;
    

    public static void main(String[] args) {

        try {
            boolean flag = true;
            DatagramSocket socketS = new DatagramSocket(8888);
            InetAddress adress = InetAddress.getByName("localhost");
            iniciarDiscos(socketS, adress);
            while (flag) {
                byte[] accion = new byte[1024];
                
                socketS.receive(new DatagramPacket(accion, accion.length));
                String accionString = data(accion).toString();                
                switch (accionString) {
                    case "guardar":
                        byte[] libroGuardar = new byte[1024];
                        socketS.receive(new DatagramPacket(libroGuardar, libroGuardar.length));
                        String libroGuardarNombre = data(libroGuardar).toString();
                        
                        byte[] receive = new byte[65535];
                        File fileTemp = new File("temporal.txt");                             
                        socketS.receive(new DatagramPacket(receive, receive.length));
                        Files.write(fileTemp.toPath(), receive);
                        Huffman.unZipFile("temporal.txt", "descomprimido.txt");                                                                     
                        guardarLibroEnDiscos(socketS, adress, libroGuardarNombre);
                        break;
                    case "recuperar":
                        byte[] libroRecuperar = new byte[1024];
                        socketS.receive(new DatagramPacket(libroRecuperar, libroRecuperar.length));
                        String libroRecuperarNombre = data(libroRecuperar).toString();
                        recuperarArchivosDeDiscos(socketS, adress, libroRecuperarNombre);
                        Huffman.zipFile("temporalFinal.txt", "temps/libroTemporalComprimido.txt");
                        byte[] libroFinal = Files.readAllBytes(new File("temps/libroTemporalComprimido.txt").toPath());
                        socketS.send(new DatagramPacket(libroFinal, libroFinal.length, adress, 8866));
                        break;

                    default:
                        break;
                }

                deleteDirectoryStream(new File("temps").toPath());
                
            }
        } catch (SocketException e) {
            
            e.printStackTrace();
        } 
        catch (IOException e) {
            
            e.printStackTrace();
        }

    }

    public static void recuperarArchivosDeDiscos(DatagramSocket socketS, InetAddress adress, String name) throws IOException {
        File temporal = new File("temporalFinal.txt");
        temporal.createNewFile();
        for (int i = 0; i < puertosDiscos.length; i++) {
            byte[] accion = "recuperar".getBytes();
            socketS.send(new DatagramPacket(accion, accion.length, adress, puertosDiscos[i]));
            byte[] data2 = name.getBytes();
            socketS.send(new DatagramPacket(data2, data2.length, adress, puertosDiscos[i]));

            byte[] fileSize = new byte[1024];
            socketS.receive(new DatagramPacket(fileSize, fileSize.length));
            int tamaño = Integer.parseInt(data(fileSize).toString());
            byte[] fileContent = new byte[tamaño];
            socketS.receive(new DatagramPacket(fileContent, fileContent.length));
            Files.write(new File("tempFile.txt").toPath(), fileContent);
            byte[] srcPos = new byte[1024];
            socketS.receive(new DatagramPacket(srcPos, srcPos.length));
            int pos = Integer.parseInt(data(srcPos).toString());
            armarArchivo(i, pos);
        }
    }

    
    public static void guardarLibroEnDiscos(DatagramSocket socketS, InetAddress adress, String srcFilePath) throws IOException {
        
        File archivo=new File("descomprimido.txt");
        long srcFileLength = archivo.length();
        
        long dflel = srcFileLength / puertosDiscos.length;
        long dfll = dflel + srcFileLength % puertosDiscos.length;
        for (int i = 0; i < puertosDiscos.length; i++) {
            byte[] accion = "guardar".getBytes();
            socketS.send(new DatagramPacket(accion, accion.length, adress, puertosDiscos[i]));
            String name = dividirArchivo(i, dfll, dflel, puertosDiscos.length, srcFilePath);
            byte[] data2 = name.getBytes();
            socketS.send(new DatagramPacket(data2, data2.length, adress, puertosDiscos[i]));
            File file = new File("temps/"+name);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String dataSize = String.valueOf(fileContent.length);
            byte[] data3 = dataSize.getBytes();
            socketS.send(new DatagramPacket(data3, data3.length, adress, puertosDiscos[i]));
            socketS.send(new DatagramPacket(fileContent, fileContent.length, adress, puertosDiscos[i]));

            byte[] receive = new byte[65535];
            socketS.receive(new DatagramPacket(receive, receive.length));
            int puerto = Integer.parseInt(data(receive).toString());
            System.out.println("Confirmación de dos :" + puerto);
        }
    }

    public static void iniciarDiscos(DatagramSocket socketS, InetAddress adress) throws IOException {
        int numDisk = 4;
        puertosDiscos = new int[4];
        int puertoBase = 8877;
        for (int i = 0; i < numDisk; i++) {
            puertosDiscos[i] = puertoBase;
            byte[] data2 = String.valueOf(puertosDiscos[i]).getBytes();
            socketS.send(new DatagramPacket(data2, data2.length, adress, 7777));
            byte[] receive = new byte[65535];
            socketS.receive(new DatagramPacket(receive, receive.length));
            int puerto = Integer.parseInt(data(receive).toString());
            System.out.println("Confirmación de :" + puerto);
            puertoBase++;
        }
    }

    public static void armarArchivo(int loc, int srcPos) {

        RandomAccessFile rafSrc = null;
        RandomAccessFile rafDes = null;

        try {
            rafSrc = new RandomAccessFile("tempFile.txt", "r");
            rafDes = new RandomAccessFile("temporalFinal.txt", "rw");
            rafDes.seek(srcPos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = rafSrc.read(buffer)) != -1) {
                rafDes.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                rafDes.close();
                rafSrc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String dividirArchivo(int loc, long dfll, long dflel, int dataDisk, String srcFilePath) throws IOException {
        long fileLength;
        long startPos = loc * dflel;
        if (loc != dataDisk - 1) {
            fileLength = dflel;
        } else {
            fileLength = dfll;
        }

        File desFile = new File("temps/"+srcFilePath + "-" + startPos + ".txt");
        if (desFile.exists()) {
            desFile.delete();
        }
        RandomAccessFile rafSrc = null;
        RandomAccessFile rafDes = null;
        try {
            desFile.createNewFile();
            rafSrc = new RandomAccessFile("descomprimido.txt", "r");
            rafDes = new RandomAccessFile(desFile, "rw");
            rafSrc.seek(loc * dflel);
            int bufferLen = 1024;
            byte[] buffer = new byte[bufferLen];

            if (fileLength <= bufferLen) {
                rafSrc.read(buffer);
                rafDes.write(buffer, 0, (int) fileLength);
            } else {
                while (true) {
                    rafDes.write(buffer, 0, rafSrc.read(buffer));
                    fileLength -= bufferLen;
                    if (fileLength >= bufferLen) {
                        continue;
                    } else {
                        rafSrc.read(buffer);
                        rafDes.write(buffer, 0, (int) fileLength);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                rafDes.close();
                rafSrc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return desFile.getName();
    }

    public static void deleteDirectoryStream(Path path) throws IOException {
        for (File file : new File("temps").listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
    
    public static StringBuilder data(byte[] a) {
        if (a == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}

   
