/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sasearch;

/**
 *
 * @author Heller
 */

import Huffman.Huffman;
import java.io.*;
import java.net.*;
import java.nio.file.Files;


public class SaSEARCH {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

      DatagramSocket sock = null;
        int port = 7777;
        String s;
        Huffman huffman=new Huffman();

        //BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

        try {
            sock = new DatagramSocket();
            InetAddress host = InetAddress.getByName("localhost");

            while (true) {
                //take input and send the packet
                
                Huffman.zipFile("texto.txt", "comprimido.txt");
                
                byte[] accion = new String("recibir").getBytes();
                sock.send(new DatagramPacket(accion, accion.length, host, port));
                
                byte[] b = Files.readAllBytes(new File("comprimido.txt").toPath());
                DatagramPacket dp = new DatagramPacket(b, b.length, host, port);
                sock.send(dp);

                //now receive reply
                //buffer to receive incoming data
                byte[] buffer = new byte[65536];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                sock.receive(reply);

                byte[] data = reply.getData();
                s = new String(data, 0, reply.getLength());

                //echo the details of incoming data - client ip : client port - client message
                echo(reply.getAddress().getHostAddress() + " :  " + s);
            }
        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
    }

    //simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }    
}
