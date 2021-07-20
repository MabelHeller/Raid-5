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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Servidor
{
    public static void main(String[] args) throws IOException
    {
        String srcFilePath = "descomprimido.txt";
        long srcFileLength = new File(srcFilePath).length();
        long dflel = srcFileLength / 4;
        long dfll = dflel + srcFileLength % 4;
        for(int i=0; i<4; i++){
            dividirArchivo(i,dfll,dflel, 4, "descomprimido.txt");
        }
        
        // System.out.println(data(parte));
        // Step 1 : Create a socket to listen at port 1234
//        DatagramSocket ds = new DatagramSocket(7777);
//                   
//        byte[] receive = new byte[65535];            
//        boolean flag = true;
//        System.out.println("Server Start...");
//        while (flag)
//        {
//            System.out.println("Waiting for actions...");
//            // Step 2 : create a DatgramPacket to receive the data.
//            // DpReceive = new DatagramPacket(receive, receive.length);
//            ds.receive(new DatagramPacket(receive, receive.length));
//            switch (data(receive).toString()) {
//                case "recibir":
//                    System.out.println("Accion Recibir");
//                    receive = new byte[65535];
//                    File fileTemp=new File("temporal.txt");           
//                    // Step 3 : revieve the data in byte buffer.
//                    ds.receive(new DatagramPacket(receive, receive.length));
//                    Files.write(fileTemp.toPath(), receive);
//                    Huffman.unZipFile("temporal.txt", "descomprimido.txt");  
//                break;
//                case "salir":
//                    System.out.println("Client sent bye.....EXITING");
//                    ds.close();
//                    flag = false;
//                break;
//                default:
//                    break;
//            }
// 
            // Clear the buffer after every message.
 
//        }
    }

    
    
	public static void dividirArchivo(int loc, long dfll,long dflel, int dataDisk, String srcFilePath) {
		// long dfll;
		long fileLength;// 分割后的文件长度
		long startPos = loc * dflel;
		if (loc != dataDisk - 1) {
			fileLength = dflel;
		} else {
			fileLength = dfll;
		}
		File desFile = new File("temp" + "-" + startPos + ".txt");// 目标文件路径
		if (desFile.exists()) {// 存在则删除
			desFile.delete();
		}
		RandomAccessFile rafSrc = null;
		RandomAccessFile rafDes = null;

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
