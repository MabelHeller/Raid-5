package prueba;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;
import static prueba.UDPService.data;

public class UDPClient {

    public static void main(String[] args) {
        boolean isNot = true;
        int[] puertosDiscos = {8877,8878,8879,8880};
        try {
            
            DatagramSocket socketS = new DatagramSocket(8888);
            InetAddress adress = InetAddress.getByName("localhost");
            for(int i=0 ;i< puertosDiscos.length;i++){
               byte[] data2 = String.valueOf(puertosDiscos[i]).getBytes();
               System.out.println("Client Port:" + puertosDiscos[i]);
               socketS.send(new DatagramPacket(data2, data2.length, adress, 7777));
               byte[] receive = new byte[65535];
               socketS.receive(new DatagramPacket(receive, receive.length));
               int puerto = Integer.parseInt(data(receive).toString());
               System.out.println("Confirmación de :"+ puerto);
            }
            String srcFilePath = "descomprimido.txt";
            long srcFileLength = new File(srcFilePath).length();
            long dflel = srcFileLength / 4;
            long dfll = dflel + srcFileLength % 4;
        
            for(int i=0 ;i< puertosDiscos.length;i++){
                String name = dividirArchivo(i,dfll,dflel, 4, "descomprimido.txt");
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
                System.out.println("Confirmación de dos :"+ puerto);
            }
            
            //DatagramSocket socketC = new DatagramSocket(8877);
            System.out.println("Username: 8877");
            System.out.println("Please enter the other party's IP, the other party's port, and the content sent, separated by spaces (such as: 189.163.122.122 8801 Hello). Press enter to send.");
//            while (isNot) {
//                String mensaje = "Hola soy un mensaje para el servidor";
//                byte[] fasong = mensaje.getBytes();
//                InetAddress address = InetAddress.getByName("localhost");
//                int port2 = 8877;
//                byte[] data2 = "Hello, I am the server, the connection is successful".getBytes();
//                System.out.println("Client Port:" + port2);
//                socketC.send(new DatagramPacket(data2, data2.length, address, 8888));
//                if (str.equals("t")) {
//                    isNot = false;
//                } else {
//                    byte[] data = str.getBytes();
//                    //Create data package
//                    DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//                    //Create DatagramSocket
//                    socket.send(packet);
//                }
//            }
        }catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated catch block
         catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static String dividirArchivo(int loc, long dfll,long dflel, int dataDisk, String srcFilePath) {
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
        return desFile.getName();
	}
}
