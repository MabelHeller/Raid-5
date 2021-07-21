package prueba;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

import static prueba.UDPService.data;

public class UDPClient {

    static int[] puertosDiscos;

    public static void main(String[] args) {

        try {
            boolean flag = true;
            DatagramSocket socketS = new DatagramSocket(8888);
            InetAddress adress = InetAddress.getByName("localhost");
            while (flag) {
                byte[] accion = new byte[1024];
                System.out.println("Esperando acciones...");
                socketS.receive(new DatagramPacket(accion, accion.length));// Save data to packet
                String accionString = data(accion).toString();

                switch (accionString) {
                    case "guardar":
                        byte[] libroGuardar = new byte[1024];
                        socketS.receive(new DatagramPacket(libroGuardar, libroGuardar.length));// Save data to packet
                        String libroGuardarNombre = data(libroGuardar).toString();
                        System.out.println("Guardando libro: "+ libroGuardarNombre);
                        guardarLibroEnDiscos(socketS, adress, libroGuardarNombre);
                        break;
                    case "recuperar":
                        byte[] libroRecuperar = new byte[1024];
                        socketS.receive(new DatagramPacket(libroRecuperar, libroRecuperar.length));// Save data to packet
                        String libroRecuperarNombre = data(libroRecuperar).toString();
                        System.out.println("Recuperando libro: "+ libroRecuperarNombre);
                        recuperarArchivosDeDiscos(socketS, adress, libroRecuperarNombre);
                        break;

                    default:
                        break;
                }

                iniciarDiscos(socketS, adress);

                
                deleteDirectoryStream(new File("temps").toPath());
                //
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // TODO Auto-generated catch block
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void recuperarArchivosDeDiscos(DatagramSocket socketS, InetAddress adress, String name) throws IOException {
        File temporal = new File("temporalFinal.txt");
        temporal.createNewFile();
        for (int i = 0; i < puertosDiscos.length; i++) {
            // String name = dividirArchivo(i,dfll,dflel, 4, "descomprimido.txt");
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
        long srcFileLength = new File(srcFilePath).length();

        long dflel = srcFileLength / puertosDiscos.length;
        long dfll = dflel + srcFileLength % puertosDiscos.length;
        System.err.println("Length: " + srcFileLength + " PuertosList:" + puertosDiscos.length);
        for (int i = 0; i < puertosDiscos.length; i++) {
            byte[] accion = "guardar".getBytes();
            socketS.send(new DatagramPacket(accion, accion.length, adress, puertosDiscos[i]));
            String name = dividirArchivo(i, dfll, dflel, puertosDiscos.length, srcFilePath);
            byte[] data2 = name.getBytes();
            socketS.send(new DatagramPacket(data2, data2.length, adress, puertosDiscos[i]));
            File file = new File(name);
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
            System.out.println("Client Port:" + puertosDiscos[i]);
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
            rafDes.seek(srcPos);// 设置文件指针位置
            byte[] buffer = new byte[1024];
            int len;
            while ((len = rafSrc.read(buffer)) != -1) {
                rafDes.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                rafDes.close();// 关闭流
                rafSrc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String dividirArchivo(int loc, long dfll, long dflel, int dataDisk, String srcFilePath) throws IOException {
        // long dfll;

        long fileLength;// 分割后的文件长度
        long startPos = loc * dflel;
        if (loc != dataDisk - 1) {
            fileLength = dflel;
        } else {
            fileLength = dfll;
        }

        File desFile = new File("temps/temp" + "-" + startPos + ".txt");// 目标文件路径
        if (desFile.exists()) {// 存在则删除
            desFile.delete();
        }
        RandomAccessFile rafSrc = null;
        RandomAccessFile rafDes = null;
        System.err.println("Loc: " + loc + " Dflel:" + dflel + " SrcPos:" + startPos);
        try {
            desFile.createNewFile();// 创建文件
            rafSrc = new RandomAccessFile(srcFilePath, "r");// 随机读方式打开
            rafDes = new RandomAccessFile(desFile, "rw");// 随机写方式打开
            rafSrc.seek(loc * dflel);// 设置读文件指针位置
            int bufferLen = 1024;
            byte[] buffer = new byte[bufferLen];

            if (fileLength <= bufferLen) {// 一次读出来的字节数大于该文件条带的大小
                rafSrc.read(buffer);
                rafDes.write(buffer, 0, (int) fileLength);
            } else {
                while (true) {
                    rafDes.write(buffer, 0, rafSrc.read(buffer));
                    fileLength -= bufferLen;
                    if (fileLength >= bufferLen) {// 剩余未读长度大于缓冲区长度
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
                rafDes.close();// 关闭流
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
}
