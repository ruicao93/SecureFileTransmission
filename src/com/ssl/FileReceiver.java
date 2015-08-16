package com.ssl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileReceiver {
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
	 * 接收文件名
	 */
	private String fileName;
	/**
	 * 接收文件路径
	 */
	private String filePath;
	/**
	 * 接收文件大小
	 */
	private long fileLength;
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
	private static String ERROR_CODE = "401";
	public FileReceiver(Socket socket){
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
	public FileReceiver(){
		
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
	 * @param filePath 将要存储的文件路径
	 * @return
	 */
	public boolean receiveFile(String filePath){
//		File file = new File(filePath);
//		if(!file.exists()){
//			setLastError("文件不存在！");
//			return false;
//		}
		setFilePath(filePath);
		//1。等待发送方sayhello 
		if(!receiveHello()){
			return false;
		}
		//2.等待发送方发送文件名
		if(!receiveFileName()){
			return false;
		}
		//3.等待发送方发送文件长度
		if(!receiveFileLength()){
			return false;
		}
		//4.接收文件二进制数据
		if(!receiveFileBody()){
			return false;
		}
        // 5.检查文件是否被修改
        File file = new File(filePath+File.separator+fileName);
        if (!fileCurrect(file)) {
            return false;
        }
        if(!socket.isClosed()){
        	try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

		return true;
	}
	/**
	 * 等待发送方sayHello
	 * @return
	 */
	private boolean receiveHello(){
		String result = readMessage();
		if(result.contains(HELLO)){
			writeMessage(SUCCESS_CODE);
			return true;
		}
		writeMessage(ERROR_CODE+":没有接收到hello信息！");
		return false;
	}
	/**
	 * 接收文件名
	 * @param fileName
	 * @return
	 */
	private boolean receiveFileName(){
		String fileName = readMessage();
		fileName = fileName.substring(fileName.lastIndexOf(":")+1);
		if(fileName == null){
			writeMessage(ERROR_CODE+":没有接收到文件名！");
			return false;
		}
		setFileName(fileName);
		writeMessage(SUCCESS_CODE);
		return true;
	}
	/**
	 * 接收文件长度
	 * @param fileLength
	 * @return
	 */
	private boolean receiveFileLength(){
		String result = readMessage();
		result = result.substring(result.lastIndexOf(":")+1);
		long length = Long.valueOf(result);
		setFileLength(length);
		//writeMessage(SUCCESS_CODE);
		return true;
	}
	/**
	 * 接收文件体
	 * @param filePath
	 * @return
	 */
	private boolean receiveFileBody(){
		try {
			if(getFileLength() < 0){
				setLastError("文件大小错误！");
				writeMessage(ERROR_CODE+"文件大小错误！");
				return false;
			}
			File file = new File(getFilePath() + File.separator + getFileName());
			if(!file.exists()){
				if(!file.createNewFile()){
					setLastError("创建文件失败！");
					writeMessage(ERROR_CODE+"文件创建失败！");
					return false;
				}
			}
			writeMessage(SUCCESS_CODE);//通知发送方开始发送文件
			readBinaryData(new FileOutputStream(file), getFileLength());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
    /**
     * 获取发送过来的文件摘要
     * 
     * @return
     */
    private String receiveFlieDigest() {
        String digest = readMessage();
        return digest;
    }
    
    /**
     * 比较文件摘要
     * @param file
     * @return
     */
    private boolean fileCurrect(File file) {
        try {
        	System.out.println("正在验证文件");
            if (receiveFlieDigest().equals(fileDigest(file, "MD5"))) {
            	writeMessage(SUCCESS_CODE);
                return true;
            } else {
            	deleteFile(file);
            	writeMessage(ERROR_CODE+"文件被修改！");
                return false;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    private boolean deleteFile(File file) {
		if(file.exists()){
			file.delete();
			return true;
		}
		return false;
	}
    /**
     * 从输入流读取一条消息
     * 
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
			if(e.getMessage().contains("certificate_unknown")){
				setLastError("对方证书不受信任！");
			}
			e.printStackTrace();
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
     * 获取文件摘要
     * 
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
	 * 接收指定长度的二进制数据
	 * @param destOutputStream
	 * @param length
	 */
	private void readBinaryData(OutputStream destOutputStream,long length){
		byte[] buffer = new byte[1024];
		long count = 0;
		int tmp;
		try {
			while(count < length &&(tmp = inputStream.read(buffer)) > 0) {
				if(count + tmp >length){
					tmp = (int)(length -count);
				}
				destOutputStream.write(buffer, 0, tmp);
				count += tmp;
			}
			destOutputStream.close();
			System.out.println("文件接收完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 判断返回结果是否为成功
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
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	public String getFilePath(){
		return filePath;
	}
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public String getFileName(){
		return fileName;
	}
	public void setFileLength(long fileLength){
		this.fileLength = fileLength;
	}
	public long getFileLength(){
		return fileLength;
	}
}
