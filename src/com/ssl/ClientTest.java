package com.ssl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.sound.midi.Receiver;

public class ClientTest {
	public static void main(String args[]){
		ClientConnector clientConnector = new ClientConnector(Config.getValuePath("ServerKeyStoreFile"), Config.getValue("ServerKeyStorePass"), 
				Config.getValuePath("ClientTrustKeyStoreFile"),Config.getValue("ServerTrustKeyStorePass"), 8088,"127.0.01");
		Socket socket = null;
		System.out.println("客户端已启动...");
//		if(!clientConnector.connect()){
//			System.out.println("连接失败，原因：" + clientConnector.getLastError());
//		}
		
	    socket = clientConnector.getSSLSocket();
//	    System.out.println("成功连接至服务端...");
//		String msg;
//		Scanner  reader = new Scanner(System.in);
//		PrintWriter writer = null;
//		try {
//			writer = new PrintWriter(socket.getOutputStream());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		while(reader.hasNextLine()){
//			msg = reader.nextLine();
//			if(msg.equals("quit")){
//				try {
//					socket.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				break;
//			}
//			try {
//				writer.println(msg);
//				writer.flush();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	    FileSender fileSender = new FileSender(socket);
		fileSender.sendFile("D://test//src//test.txt");
		
	}
}
