package com.file;

import java.net.ServerSocket;
import java.net.Socket;

import com.ssl.Config;
import com.ssl.ServerConnector;

public class Server {
	public static void main(String[] args) {
//		try {  
//            Thread th = new Thread(new Runnable() {  
//                public void run() {  
//                	Socket socket=null;
//            		ServerConnector serverConnector = new ServerConnector(Config.getValuePath("ServerKeyStoreFile"), Config.getValue("ServerKeyStorePass"), 
//            				Config.getValuePath("ClientTrustKeyStoreFile"),Config.getValue("ServerTrustKeyStorePass"), 8088);
//            	    ExchangeMessage em = null;
//                        try {    
//                            if(serverConnector.startListen()){
//                            	em = new ExchangeMessage(serverConnector.getSSLSocket());
//                            	em.readMessage();
//                            	em.writeMessage("Hello");
//                            	String fileName = em.readMessage();
//                            	em.receiveFile(fileName);
//                            	System.out.println("发送成功");  
//                            }
//                        } catch (Exception e) {  
//                            System.out.println("服务器异常");  
//                            e.printStackTrace();  
//                        }  
//                    }  
//                }  
//            );  
//            th.run(); //启动线程运行  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }       
	}
}
