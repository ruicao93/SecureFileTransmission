package com.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * 安全传输时的客户端类：
 * 负责建立安全的SSL连接
 * @author CR
 *
 */
public class ClientConnector {
	/**
	 * 服务端监听端口，默认为8088
	 */
	private int port = 8088;
	/**
	 * 服务器地址：默认为:127.0.0.1
	 */
	private String serverAddress = "127.0.0.1";
	/**
	 * 通信套接字
	 */
	private Socket socket = null;
	/**
	 * 本地证书库文件
	 */
	private String keyStoreFile;
	/**
	 * 信任证书库文件
	 */
	private String trustKeySoreFile;
	/**
	 * 本地证书库密码
	 */
	private String keyStorePass;
	/**
	 * 信任证书库密码
	 */
	private String trustKeyStorePass;
	/**
	 * 本地证书库
	 */
	private KeyStore keyStore;
	/**
	 * 信任证书库
	 */
	private KeyStore trustKeyStore;
	/**
	 * 最近一次发生的错误
	 */
	private String lastError;
	/**
	 * SSLContext
	 */
	private SSLContext ctx;
	private KeyManage keyManage;
	
	/**
	 * 构造函数
	 * @param keyStoreFile：本地证书库文件地址
	 * @param keyStorePass：本地证书库密码
	 * @param trustKeyStoreFile：受信任库文件地址
	 * @param trustKeyStorePass：受信任库密码
	 * @param port：通信端口
	 */
	public ClientConnector(String keyStoreFile,String keyStorePass,String trustKeyStoreFile,String trustKeyStorePass,
							int port,String serverAddress){
		this.keyStoreFile = keyStoreFile;
		this.keyStorePass = keyStorePass;
		this.trustKeySoreFile = trustKeyStoreFile;
		this.trustKeyStorePass = trustKeyStorePass;
		this.port = port;
		this.serverAddress = serverAddress;
	}
	public ClientConnector(){
		
	}
	public void reset(KeyManage keyManage){
		this.keyManage = keyManage;
		this.ctx = keyManage.getSSLContext();
		//重置socket
		if(this.socket != null && !this.socket.isClosed()){
			try {
				this.socket.close();
				socket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void reset(){
		//重置socket
		if(this.socket != null && !this.socket.isClosed()){
			try {
				this.socket.close();
				socket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 连接传输服务器端
	 */
	public boolean connect(String serverAddress, int port){
		reset();
		try {
			socket = (SSLSocket)ctx.getSocketFactory().createSocket(serverAddress,port);
			if (socket == null) {
				setLastError("套接字连接失败！");
				return false;  
		        } 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setLastError("套接字连接失败！\n"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 初始化SSL参数
	 */
	private boolean init(){
		try {
			ctx = SSLContext.getInstance("SSL");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			keyStore = KeyStore.getInstance("JKS");
			trustKeyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(keyStoreFile), keyStorePass.toCharArray());
			trustKeyStore.load(new FileInputStream(trustKeySoreFile),trustKeyStorePass.toCharArray());
			
			kmf.init(keyStore, keyStorePass.toCharArray());
			tmf.init(trustKeyStore);
			
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setLastError(e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	public void closeConnection(){
		reset();
	}
	/**
	 * 返回SSL套接字
	 */
	public Socket getSSLSocket(){
		return socket;
	}
	
	public void setLastError(String error){
		lastError = error;
	}
	public String getLastError(){
		return lastError;
	}
}
