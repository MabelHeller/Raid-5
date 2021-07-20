package prueba;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

                byte[] data = new byte[1024];

                System.out.println("I am the server, waiting for the client to connect");
                disco.receive(new DatagramPacket(data, data.length));// Save data to packet
                
                String name = data(data).toString();
                System.out.println("Archivo: " + name);
                byte[] dataSize = new byte[1024];
                disco.receive(new DatagramPacket(dataSize, dataSize.length));// Save data to packet
                int size = Integer.parseInt(data(dataSize).toString());
                byte[] fileContent = new byte[size];
                disco.receive(new DatagramPacket(fileContent, fileContent.length));// Save data to packet

                Files.write(new File("disk/raid5/disk" + this.discoNum + "/" + name).toPath(), fileContent);
                byte[] data2 = String.valueOf(puerto).getBytes();
                disco.send(new DatagramPacket(data2, data2.length, InetAddress.getByName("localhost"), 8888));
                /*
				 * Respond to the client
                 */
                // The information to be sent
                // Define client address, port
                // byte[] fasong = strings[2].getBytes();
                // InetAddress address = InetAddress.getByName("localhost");
                // int port2 = Integer.parseInt(strings[1]);
                // byte[] data2 = "Hello, I am the server, the connection is
                // successful".getBytes();
                // System.out.println("Client Port:" + port2);
                // DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address,
                // 8888);
                // socket.send(packet2);
            }
        }catch (IOException e) {
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
