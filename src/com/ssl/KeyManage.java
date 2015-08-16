package com.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * 安全通信配置文件
 */
public class KeyManage {
	/**
	 * 本地证书库文件
	 */
	private String keyStoreFile;
	/**
	 * 本地证书库密码
	 */
	private String keyStorePass;
	/**
	 * 信任证书库文件
	 */
	private String trustKeyStoreFile;
	/**
	 * 信任证书库密码
	 */
	private String trustKeyStorePass;
	/**
	 * 别名
	 */
	private String keyAlias;
	/**
	 * 别名密码
	 */
	private String keyAliasPass;
	/**
	 * 本地证书库
	 */
	private KeyStore keyStore;
	/**
	 * 信任证书库
	 */
	private KeyStore trustKeyStore;
	private KeyManagerFactory kmf;
	private TrustManagerFactory tmf;
	/**
	 * SSLContext
	 */
	private SSLContext ctx;
	/**
	 * 最近一次发生错误
	 */
	private String lastError;
	/**
	 * 初始化本地证书库
	 */
	private boolean setKey;
	/**
	 * 初始化信任证书库
	 */
	private boolean setTrust;
	
	public KeyManage(){
		try {
			ctx = SSLContext.getInstance("SSL");
			kmf = KeyManagerFactory.getInstance("SunX509");
			tmf = TrustManagerFactory.getInstance("SunX509");
			keyStore = KeyStore.getInstance("JKS");
			trustKeyStore = KeyStore.getInstance("JKS");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static PublicKey getPublicKey(String keyStoreFile,String keyStorePass, String keyAlias) {
		// 读取密钥是所要用到的工具类
		KeyStore ks;
		// 公钥类所对应的类
		PublicKey publicKey = null;
		try {
			// 得到实例对象
			ks = KeyStore.getInstance("JKS");
			FileInputStream fin;
			try {
				// 读取JKS文件
				fin = new FileInputStream(keyStoreFile);
				try {
					// 读取公钥
					ks.load(fin, keyStorePass.toCharArray());
					java.security.cert.Certificate cert = ks.getCertificate(keyAlias);
					publicKey = cert.getPublicKey();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
			}
		return publicKey;
	}

	/**
	* 得到私钥
	* 
	* @param keyStoreFile
	*            私钥文件
	* @param storeFilePass
	*            私钥文件的密码
	* @param keyAlias
	*            别名
	* @param keyAliasPass
	*            密码
	* @return
	*/
	public static PrivateKey getPrivateKey(String keyStoreFile,String storeFilePass, String keyAlias, String keyAliasPass) {
		KeyStore ks;
		PrivateKey prikey = null;
		try {
			ks = KeyStore.getInstance("JKS");
			FileInputStream fin;
			try {
			    fin = new FileInputStream(keyStoreFile);
			    try {
			    	try {
			    		ks.load(fin, storeFilePass.toCharArray());
			    		// 先打开文件
			    		prikey = (PrivateKey) ks.getKey(keyAlias, keyAliasPass.toCharArray());
			    		// 通过别名和密码得到私钥
			    		} catch (UnrecoverableKeyException e) {
			    				e.printStackTrace();
			    		} catch (CertificateException e) {
			    				e.printStackTrace();
			    		} catch (IOException e) {
			    				e.printStackTrace();
			    		}
			    	} catch (NoSuchAlgorithmException e) {
			    				e.printStackTrace();
			    		}
			   	} catch (FileNotFoundException e) {
			   					e.printStackTrace();
			   			}
			 } catch (KeyStoreException e) {
				 e.printStackTrace();
			  	 }
			  return prikey;
	}
	public boolean isReady(){
		if(!setKey){
			setLastError("设置本地证书库文件失败");
			return false;
		}
		if(!setTrust){
			setLastError("设置信任证书库文件失败");
			return false;
		}
		try {
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	public boolean setKeyStoreFile(String keyStoreFile,String keyStorePass) {
		this.keyStoreFile = keyStoreFile;
		this.keyStorePass = keyStorePass;
			try {
				keyStore.load(new FileInputStream(keyStoreFile), keyStorePass.toCharArray());
				kmf.init(keyStore, keyStorePass.toCharArray());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setKey = false;
				return false;
			}
		setKey = true;
		return true;
	}
	public boolean setTrustKeyStoreFile(String trustKeyStoreFile,String trustKeyStorePass) {
		this.trustKeyStoreFile = trustKeyStoreFile;
		this.trustKeyStorePass = trustKeyStorePass;
		try {
			trustKeyStore.load(new FileInputStream(trustKeyStoreFile),trustKeyStorePass.toCharArray());
			tmf.init(trustKeyStore);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setTrust = false;
			return false;
		}
		setTrust = true;
		return true;
	}
	public String sign(byte[] data) {
		Signature signature = new Signature("MD5withRSA") {
			
			@Override
			protected boolean engineVerify(byte[] arg0) throws SignatureException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected void engineUpdate(byte[] arg0, int arg1, int arg2)
					throws SignatureException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void engineUpdate(byte arg0) throws SignatureException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected byte[] engineSign() throws SignatureException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected void engineSetParameter(String arg0, Object arg1)
					throws InvalidParameterException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void engineInitVerify(PublicKey arg0) throws InvalidKeyException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void engineInitSign(PrivateKey arg0) throws InvalidKeyException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected Object engineGetParameter(String arg0)
					throws InvalidParameterException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		try {
			signature.initSign(getPrivateKey(trustKeyStoreFile, keyStorePass, keyAlias, keyAliasPass));
			signature.update(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data.toString();
	}
	public String verify(byte[] data) {
		 Signature signature = new Signature("MD5withRSA") {
			
			@Override
			protected boolean engineVerify(byte[] arg0) throws SignatureException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected void engineUpdate(byte[] arg0, int arg1, int arg2)
					throws SignatureException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void engineUpdate(byte arg0) throws SignatureException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected byte[] engineSign() throws SignatureException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected void engineSetParameter(String arg0, Object arg1)
					throws InvalidParameterException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void engineInitVerify(PublicKey arg0) throws InvalidKeyException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void engineInitSign(PrivateKey arg0) throws InvalidKeyException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected Object engineGetParameter(String arg0)
					throws InvalidParameterException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		try {
			signature.initVerify(getPublicKey(keyStoreFile, keyStorePass, keyAlias));
			signature.update(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data.toString();
	}
	public void setLastError(String error){
		lastError = error;
	}
	public String getLastError(){
		return lastError;
	}
	public SSLContext getSSLContext() {
		return ctx;
	}
}
