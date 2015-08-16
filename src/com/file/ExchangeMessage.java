package com.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ExchangeMessage {
	private PrintWriter printWriter;
	private InputStreamReader inputStreamReader;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private Socket socket;
	public  ExchangeMessage(Socket socket) {
		this.socket = socket;
		try {
			inputStreamReader = new InputStreamReader(socket.getInputStream());
			printWriter = new PrintWriter(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void writeMessage(String str) {
		printWriter.write(str);
		printWriter.write('\n');
		printWriter.flush();
	}
	public String readMessage(){
		char tmp;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			while((tmp = (char)inputStreamReader.read()) != '\n'){
				stringBuffer.append(tmp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
	public void sendFile(String filePath) throws IOException {
		int length = 0;  
	    double sumL = 0 ;  
	    byte[] sendBytes = null;  
	    boolean bool = false;  
	    try {  
	        File file = new File(filePath); //要传输的文件路径  
	        long l = file.length();    
	        fileInputStream = new FileInputStream(file);        
	        sendBytes = new byte[1024];    
	        while ((length = fileInputStream.read(sendBytes, 0, sendBytes.length)) > 0) {  
	            sumL += length;    
	            System.out.println("已传输："+((sumL/l)*100)+"%");  
	            dataOutputStream.write(sendBytes, 0, length);  
	            dataOutputStream.flush();  
	        }   
	        //虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较  
	        if(sumL==l){  
	            bool = true;  
	        }  
	    }catch (Exception e) {  
	        System.out.println("客户端文件传输异常");  
	        bool = false;  
	        e.printStackTrace();    
	    }
	    System.out.println(bool?"成功":"失败");  
	}
	public void receiveFile(String fileName) throws IOException {  
	    byte[] inputByte = null;  
	    int length = 0;  
	    String filePath = "D:/temp/"+fileName;  
	    try {  
	        try {  
	            File f = new File("D:/temp");  
	            if(!f.exists()){  
	                f.mkdir();    
	            }  
	            /*   
	             * 文件存储位置   
	             */  
	            fileOutputStream = new FileOutputStream(new File(filePath));      
	            inputByte = new byte[1024];     
	            System.out.println("开始接收数据...");    
	            while ((length = dataInputStream.read(inputByte, 0, inputByte.length)) > 0) {  
	            	fileOutputStream.write(inputByte, 0, length);  
	            	fileOutputStream.flush();      
	            }  
	            System.out.println("完成接收："+filePath);  
	        } finally {  
	            if (fileOutputStream != null)  
	            	fileOutputStream.close();  
	            if (dataInputStream != null)  
	            	dataInputStream.close();   
	        }  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
}
}
