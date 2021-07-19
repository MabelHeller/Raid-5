/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author Heller
 */

import Huffman.Huffman;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;

public class Servidor
{
    public static void main(String[] args) throws IOException
    {
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket ds = new DatagramSocket(7777);
        byte[] receive = new byte[65535];
        Huffman huffman=new Huffman();                
                
        DatagramPacket DpReceive = null;
        while (true)
        {
 
            // Step 2 : create a DatgramPacket to receive the data.
            DpReceive = new DatagramPacket(receive, receive.length);

            File fileTemp=new File("temporal.txt");           

            // Step 3 : revieve the data in byte buffer.
            ds.receive(DpReceive);
            Files.write(fileTemp.toPath(), receive);
 
            Huffman.unZipFile("temporal.txt", "descomprimido.txt");
            
            System.out.println("Client:" + data(receive));
            //String s = new String(receive, StandardCharsets.UTF_8);
            //String s="01-11-10-00";
            //System.out.println("Decrypted: " + huffmanTree.decrypt(s));
            //System.out.println("Decrypted: " + huffmanTree.decrypt(data(receive).toString()));
            

            // Exit the server if the client sends "bye"
            if (data(receive).toString().equals("bye"))
            {
                System.out.println("Client sent bye.....EXITING");
                break;
            }
 
            // Clear the buffer after every message.
            receive = new byte[65535];
        }
    }
 
    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
