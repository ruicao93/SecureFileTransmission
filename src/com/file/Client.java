package com.file;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import com.ssl.ClientConnector;
import com.ssl.Config;

public class Client {
	public static void main(String[] args) throws IOException {
//		ClientConnector clientConnector = new ClientConnector(Config.getValuePath("ClientKeyStoreFile"), Config.getValue("ClientKeyStorePass"), Config.getValuePath("ClientTrustKeyStoreFile"), Config.getValue("ClientTrustKeyStorePass"), 8088, "127.0.0.1");
//		Socket socket = null;
//		if(clientConnector.connect()){
//			socket = clientConnector.getSSLSocket();
//			ExchangeMessage em = new ExchangeMessage(clientConnector.getSSLSocket());
//			//发送hello
//			em.writeMessage("Hello");
//			//如果得到服务器的响应,发送文件名,发送文件
//			if(em.readMessage().equals("Hello")){
//				String fileName = getFileName();
//				em.writeMessage(fileName);
//				em.sendFile(getFilePath());
//			}
//		}
		
	}
	public static String getFilePath() {
		Scanner input = new Scanner(System.in);
		System.out.println("请输入文件路径");
		String filePath = input.nextLine();
		return filePath;
	}
	public static String getFileName() {
		Scanner input = new Scanner(System.in);
		System.out.println("请输入文件名");
		String fileName = input.nextLine();
		return fileName;
	}
}
