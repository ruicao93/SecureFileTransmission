package com.ssl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author CaoRui
 *
 */
public class FileSender {
	/**
	 * 用于通信的socket
	 */
	private Socket socket;
	/**
	 * 通信中的输入流
	 */
	private InputStream inputStream = null;
	/**
	 * 通信中的输出流
	 */
	private OutputStream outputStream = null;
	/**
	 * 通信中的字符输入流
	 */
	private InputStreamReader reader = null;
	/**
	 * 通信中的字符输出流
	 */
	private PrintWriter printWriter = null;
	/**
	 * 构造函数
	 * @param socket
	 */
	/**
	 * 最后一次错误
	 * @param socket
	 */
	private String lastError;
	private static String HELLO = "hello";
	private static String SUCCESS_CODE = "250";
	public FileSender(Socket socket){
		this.socket = socket;
		//初始化输入流
		if(socket != null){
			try {
				inputStream  = this.socket.getInputStream();
				outputStream = this.socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public FileSender(){
		
	}
	public void setSocket(Socket socket){
		if(this.socket != null && !this.socket.isClosed()){
			try {
				this.socket.close();
				socket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.socket = socket;
		if(socket != null){
			try {
				inputStream  = this.socket.getInputStream();
				outputStream = this.socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		printWriter = null;
		reader = null;
	}
	/**
	 * 发送指定文件
	 * @param filePath 带路径的文件名
	 * @return
	 */
	public boolean sendFile(String filePath){
		File file = new File(filePath);
		if(!file.exists()){
			setLastError("文件不存在！");
			return false;
		}
		//1。向接收端说hello 
		if(!sayHello()){
			setLastError("SayHello Error\n" + getLastError());
			return false;
		}
		//2.向接收端发送文件名
		if(!sendFileName(file.getName())){
			setLastError("发送文件名出错");
			return false;
		}
		//3.向接收端发送文件长度
		if(!sendFileLength(file.length())){
			setLastError("发送文件长度出错");
			return false;
		}
		//4.向接收端发送文件二进制数据
		if(!sendFileBody(file, file.length())){
			setLastError("发送文件体出错！");
			return false;
		}
        // 5.向接收端发送文件摘要
        if (!sendFileDigest(file)) {
            setLastError("发送文件摘要失败或文件被修改！");
            return false;
        }
		
		return true;
	}
	/**
	 * 向接收端sayHello
	 * @return
	 */
	private boolean sayHello(){
		writeMessage(HELLO);
		String result = readMessage();
		if(isResultSuccess(result)){
			return true;
		}
		return false;
	}
	/**
	 * 向接收端发送文件名
	 * @param fileName
	 * @return
	 */
	private boolean sendFileName(String fileName){
		writeMessage("filename:"+fileName);
		return isResultSuccess(readMessage());
	}
	/**
	 * 向接收端发送文件长度
	 * @param fileLength
	 * @return
	 */
	private boolean sendFileLength(long fileLength){
		writeMessage("filelength:" + String.valueOf(fileLength));
		return isResultSuccess(readMessage());
	}
	/**
	 * 发送文件体
	 * @param filePath
	 * @return
	 */
	private boolean sendFileBody(File file,long length){
		try {
			if(length == 0){
				length = file.length();
			}
			FileInputStream fileInputStream = new FileInputStream(file);
			writeBinaryData(fileInputStream, length);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

    /**
     * 发送文件摘要
     * 
     * @param file
     * @return
     * @throws IOException
     */
    private boolean sendFileDigest(File file) {
        try {
            writeMessage(fileDigest(file, "MD5"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isResultSuccess(readMessage());
    }
	/**
	 * 从输入流读取一条消息
	 * @return
	 */
	private String readMessage(){
		String result = "";
		if(reader == null){
			reader = new InputStreamReader(inputStream);
		}
		StringBuffer buffer = new StringBuffer();
		char tmp;
		try {
			while((tmp = (char)reader.read()) > 0 && tmp!='\n'){
				buffer.append(tmp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(e.getMessage().contains("No trusted certificate found")){
				setLastError("对方证书不受信任！");
			}
		}
		result = buffer.toString();
		System.out.println("返回：" +result);
		return result;
	}
	/**
	 * 输出一条消息
	 * @param message
	 */
	private void writeMessage(String message){
		if(printWriter == null){
			printWriter = new PrintWriter(outputStream);
		}
		printWriter.write(message);
		printWriter.write('\n');
		printWriter.flush();
		System.out.println("发送：" + message);
	}
	/**
	 * 传输二进制数据
	 * @param sourceInputStream 数据源
	 * @param length 要传输的数据长度
	 */
	private void writeBinaryData(InputStream sourceInputStream,long length){
		byte[] buffer = new byte[1024];
		long count = 0;
		int tmp;
		try {
			while(((tmp = sourceInputStream.read(buffer)) > 0) && count < length){
				if(count + tmp >length){
					tmp = (int)(length - count);
				}
				outputStream.write(buffer, 0, tmp);
				count += tmp;
			}
			System.out.println("文件发送完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 接收指定长度的二进制数据
	 * @param destOutputStream
	 * @param length
	 */
	private void readBinaryData(OutputStream destOutputStream,int length){
		byte[] buffer = new byte[1024];
		int count = 0;
		int tmp;
		try {
			while(count < length &&(tmp = inputStream.read(buffer)) > 0){
				if(count + tmp >length){
					tmp = length -count;
				}
				destOutputStream.write(buffer, 0, tmp);
				count += tmp;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * 获取文件摘要
     * @param file
     * @param md
     * @return
     * @throws IOException
     */
    private String fileDigest(File file, String md) throws IOException {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(md);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        int ch;
        while ((ch = in.read()) != -1) {
            // 用指定字节更新摘要
            digest.update((byte) ch);
        }
        in.close();
        // 完成散列摘要计算
        byte[] hash = digest.digest();
        return byteToString(hash);
    }

    /**
     * 字符数组转换为字符串
     * 
     * @param in
     * @return
     * @throws Exception
     */
    private String byteToString(byte[] scoure) {
        StringBuilder builder = new StringBuilder();
        for (byte c : scoure) {
            builder.append((int) c);
        }
        return builder.toString();
    }

    /**
     * 判断返回结果是否为成功
     * 
     * @param result
     * @return
     */
	private boolean isResultSuccess(String result){
		return result.contains(SUCCESS_CODE);
	}
	
	public void setLastError(String error){
		this.lastError = error;
	}
	public String getLastError(){
		return lastError;
	}
}
