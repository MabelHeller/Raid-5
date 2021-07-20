package prueba;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPService {

    public static void main(String[] args) {
        /*
         * Receive client information
         */
        try {
            DatagramSocket socket = new DatagramSocket(7777);
            InetAddress adress = InetAddress.getByName("localhost");
            for (int i = 0; i < 4; i++) {

                System.err.println("Holis");

                // DatagramSocket socketD1 = new DatagramSocket(puerto);
                byte[] receive = new byte[65535];
                socket.receive(new DatagramPacket(receive, receive.length));
                int puerto = Integer.parseInt(data(receive).toString());
                System.err.println("Solicitud de creacion de " + puerto);
                UDPServerRunnable udpThread = new UDPServerRunnable(i,puerto, new DatagramSocket(puerto));
                udpThread.start();
                byte[] data2 = String.valueOf(puerto).getBytes();
                socket.send(new DatagramPacket(data2, data2.length, adress, 8888));
                System.out.println("Creación Exitosa de: " + puerto);
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(UDPService.class.getName()).log(Level.SEVERE, null, ex);
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
