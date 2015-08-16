package com.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * 测试服务端的程序
 * @author Administrator
 *
 */
public class ServerTest {
	
	public static void main(String args[]){
		ServerConnector serverConnector = new ServerConnector(Config.getValuePath("ServerKeyStoreFile"), Config.getValue("ServerKeyStorePass"), 
				Config.getValuePath("ClientTrustKeyStoreFile"),Config.getValue("ServerTrustKeyStorePass"), 8088);
		Socket socket = null;
		System.out.println("服务端已启动...");
//		if(!serverConnector.startListen()){
//			System.out.println("连接失败，原因：" + serverConnector.getLastError());
//		}
		
		socket = serverConnector.getSSLSocket();
//		System.out.println("已接收到来自客户端的请求...");
//		InputStreamReader reader = null;
//		try {
//			reader = new InputStreamReader(socket.getInputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		while(true){
//			StringBuffer buffer = new StringBuffer();
//			char tmp;
//			try {
//				while((tmp = (char)reader.read()) != '\n'){
//					buffer.append(tmp);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.print(buffer.toString());
//		}
		FileReceiver fileReceiver = new FileReceiver(socket);
		fileReceiver.receiveFile("D://test//dest");
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
