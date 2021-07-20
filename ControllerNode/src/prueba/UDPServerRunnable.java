package prueba;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class UDPServerRunnable extends Thread {

    DatagramSocket socket = null;
    DatagramSocket disco = null;
    DatagramPacket packet = null;
    int puerto;
    int discoNum;

    public UDPServerRunnable(int discoNum, int puerto, DatagramSocket disco) {

        this.disco = disco;
        this.puerto = puerto;
        this.discoNum = discoNum;

    }

    @Override
    public void run() {
        try {
            File directorio = new File("disk/raid5/disk" + this.discoNum);
            if (!directorio.exists()) {
                if (directorio.mkdirs()) {
                    System.out.println("Directorio creado");
                } else {
                    System.out.println("Error al crear directorio");
                }
            }

            while (true) {

                byte[] accion = new byte[1024];
                System.out.println("Disco " + this.discoNum + "esperando acciones");
                disco.receive(new DatagramPacket(accion, accion.length));// Save data to packet
                String accionString = data(accion).toString();

                switch (accionString) {
                    case "guardar":
                        GuardarArchivo(disco, puerto, discoNum);
                        break;
                    case "recuperar":
                        byte[] nombreBytes = new byte[1024];
                        disco.receive(new DatagramPacket(accion, accion.length));// Save data to packet
                        String nombre = data(nombreBytes).toString();
                        System.out.println("Disco " + this.discoNum + "recuperando archivo:" + nombre);
                        RecuperarArchivo(disco, puerto, discoNum, nombre);
                        break;

                    default:
                        break;
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated catch block
        finally {
            if (socket != null) {
                socket.close();
            }
        }

    }

    public static void GuardarArchivo(DatagramSocket disco, int puerto, int discoNum) throws IOException {
        System.out.println("Disco " + discoNum + " Guardando Archivo...");
        byte[] nombre = new byte[1024];
        disco.receive(new DatagramPacket(nombre, nombre.length));// Save data to packet
        String nombreString = data(nombre).toString();
        System.out.println("Archivo: " + nombreString);
        byte[] dataSize = new byte[1024];
        disco.receive(new DatagramPacket(dataSize, dataSize.length));// Save data to packet
        int size = Integer.parseInt(data(dataSize).toString());
        byte[] fileContent = new byte[size];
        disco.receive(new DatagramPacket(fileContent, fileContent.length));// Save data to packet
        Files.write(new File("disk/raid5/disk" + discoNum + "/" + nombreString).toPath(), fileContent);
        byte[] data2 = String.valueOf(puerto).getBytes();
        disco.send(new DatagramPacket(data2, data2.length, InetAddress.getByName("localhost"), 8888));
    }

    public static void RecuperarArchivo(DatagramSocket disco, int puerto, int discoNum, String nombre) throws UnknownHostException, IOException {
        String filesName[] = new File("disk/raid5/disk" + discoNum + "/").list();
        String archivo = "";
        for (int i = 0; i < filesName.length; i++) {
            if (filesName[i].matches(".*" + nombre + "-\\d+" + ".txt")) {
                archivo = filesName[i];
                break;
            }
        }
        File file = new File(archivo);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String dataSize = String.valueOf(fileContent.length);
        byte[] data3 = dataSize.getBytes();
        String srcPos = archivo.substring(archivo.indexOf("-") + 1,archivo.indexOf("."));

        disco.send(new DatagramPacket(data3, data3.length,InetAddress.getByName("localhost"), 8888));
        disco.send(new DatagramPacket(fileContent, fileContent.length,InetAddress.getByName("localhost"), 8888));
        disco.send(new DatagramPacket(srcPos.getBytes(), srcPos.getBytes().length, InetAddress.getByName("localhost"), 8888));
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
