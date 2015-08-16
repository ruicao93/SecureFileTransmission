package com.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.net.ssl.KeyManagerFactory;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ssl.ClientConnector;
import com.ssl.Config;
import com.ssl.FileReceiver;
import com.ssl.FileSender;
import com.ssl.KeyManage;
import com.ssl.ServerConnector;

public class MainFrame {
	/**
	 * 主窗体
	 */
	private JFrame jFrame = new JFrame("安全公文传输工具");
	/**
	 * 顶部panel
	 */
	private JPanel topPanel = new JPanel();
	private JLabel chooseKeyStoreLabel = new JLabel("选择证书库:");
	private JTextField chooseKeyStoreTextField  = new JTextField(20);
	private JButton chooseKeyStoreButton = new JButton("选择文件");
	private JFileChooser chooseKeyStoreFileChooser = new JFileChooser(Config.getWorkPath());
	private JLabel chooseTrustKeyStoreLabel = new JLabel("选择受信任证书库:");
	private JTextField chooseTrustKeyStoreTextField  = new JTextField(20);
	private JButton chooseTrustKeyStoreButton = new JButton("选择文件");
	private JFileChooser chooseTrustKeyStoreFileChooser = new JFileChooser(Config.getWorkPath()); 
	/**
	 * 中部panel
	 */
	private JPanel middlePanel1 = new JPanel();
	private JPanel middlePanel2 = new JPanel();
	//发送方
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JLabel chooseFileLabel = new JLabel("选择要发送的文件");
	private JTextField chooseFileTextField = new JTextField(18);
	private JButton chooseFileButton = new JButton("选择文件");
	private JFileChooser chooseFileFileChooser = new JFileChooser();
	private JLabel ipAddressLabel = new JLabel("接收方IP地址：");
	private JTextField ipAddressTextField = new JTextField(10);
	private JLabel portLabel1 = new JLabel("接收方端口号：");
	private JTextField portTextField1 = new JTextField(5);
	private JButton sendButton = new JButton("点击发送");
	//接收方
	private JLabel chooseFilePathLabel = new JLabel("选择文件存放路径");
	private JTextField chooseFilePathTextField = new JTextField(18);
	private JButton chooseFilePathButton = new JButton("选择路径");
	private JFileChooser chooseFilePathFileChooser = new JFileChooser();
	private JLabel portLabel2 = new JLabel("接收文件端口号：");
	private JTextField portTextField2 = new JTextField(5);
	private JButton receiveButton = new JButton("点击接收");
	
	/**
	 * 底部panel
	 */
	private JPanel boottomPanel = new JPanel();
	private JTextArea textArea = new JTextArea(8,20);
	
	/**
	 * 全局变量
	 */
	private ServerConnector serverConnector = new ServerConnector();
	private ClientConnector clientConnector = new ClientConnector();
	private FileSender fileSender  = new FileSender();
	private FileReceiver fileReceiver = new FileReceiver();
	private KeyManage keyManage = new KeyManage();
	private String fileName;
	private String filePath;
	private String address = ipAddressTextField.getText().trim();
	private String portStr = portTextField1.getText().trim();
	private int port;
	private String keyStoreFile = "";
	private String keyTrustStoreFile = "";
	
	/**
	 * 窗口初始化函数
	 */
	public void init(){
		initTop();
		initMiddle();
		initBottom();
		//重定位输出流
		GUIPrintStream guiPrintStream = new GUIPrintStream(System.out, textArea);
		System.setOut(guiPrintStream);
	}
	/**
	 * 初始化Top Panel
	 */
	private void initTop(){
		//************************Top Panel配置***************************
		
		topPanel.setLayout(new GridLayout(2,1,5,0));//两行一列的布局
		//第一行
		Box topBox1 = Box.createHorizontalBox();
		chooseKeyStoreTextField.setMaximumSize(new Dimension(40, 25));
		topBox1.add(chooseKeyStoreLabel);
		topBox1.add(Box.createHorizontalStrut(20));
		topBox1.add(chooseKeyStoreTextField);
		topBox1.add(Box.createHorizontalStrut(20)); 
		topBox1.add(chooseKeyStoreButton);
		chooseKeyStoreTextField.setEditable(false);
		chooseKeyStoreFileChooser.addChoosableFileFilter(new JksFileFilter());
		chooseKeyStoreFileChooser.setAcceptAllFileFilterUsed(false);
		//点击“选择证书库”事件
		chooseKeyStoreButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				chooseKeyStoreFileChooser.setCurrentDirectory(new File("."+File.separator+"data"));
				int result = chooseKeyStoreFileChooser.showDialog(jFrame, "打开证书库");
				if(result == JFileChooser.APPROVE_OPTION){
					String name = chooseKeyStoreFileChooser.getSelectedFile().getPath();
					if(showKeyStorePassDialog(chooseKeyStoreFileChooser, name)){
						chooseKeyStoreTextField.setText(name);
					}
					//System.out.println(name);
				}
			}
		});
		topPanel.add(topBox1);
		//第二行
		Box topBox2 = Box.createHorizontalBox();
		topBox2.add(chooseTrustKeyStoreLabel);
		topBox2.add(Box.createHorizontalStrut(20)); 
		chooseTrustKeyStoreTextField.setMaximumSize(new Dimension(40, 25));
		topBox2.add(chooseTrustKeyStoreTextField);
		topBox2.add(Box.createHorizontalStrut(20)); 
		topBox2.add(chooseTrustKeyStoreButton);
		chooseTrustKeyStoreTextField.setEditable(false);
		chooseTrustKeyStoreFileChooser.addChoosableFileFilter(new JksFileFilter());
		chooseTrustKeyStoreFileChooser.setAcceptAllFileFilterUsed(false);
		//点击“选择证书库”事件
		chooseTrustKeyStoreButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				chooseTrustKeyStoreFileChooser.setCurrentDirectory(new File("."+File.separator+"data"));
				int result = chooseTrustKeyStoreFileChooser.showDialog(jFrame, "打开证书库");
				if(result == JFileChooser.APPROVE_OPTION){
					String name = chooseTrustKeyStoreFileChooser.getSelectedFile().getPath();
					if(showTrustKeyStorePassDialog(chooseTrustKeyStoreFileChooser, name)){
						chooseTrustKeyStoreTextField.setText(name);
					}
					//System.out.println(name);
				}
			}
		});
		topPanel.add(topBox2);
	}
	/**
	 * 初始化Middle Panel
	 */
	private void initMiddle(){
		//*********************发送文件Tab***********************
		middlePanel1.setLayout(new GridLayout(4, 1,20,15));
		//第一行，选择文件
		Box topBox1 = Box.createHorizontalBox();
		topBox1.add(Box.createHorizontalStrut(20)); 
		topBox1.add(chooseFileLabel);
		topBox1.add(Box.createHorizontalStrut(20)); 
		topBox1.add(chooseFileTextField);
		topBox1.add(Box.createHorizontalStrut(20)); 
		topBox1.add(chooseFileButton);
		topBox1.add(Box.createHorizontalStrut(20)); 
		chooseFileTextField.setEditable(false);
		//点击“选择文件”事件
		chooseFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//chooseFileFileChooser.setCurrentDirectory();
				int result = chooseFileFileChooser.showDialog(jFrame, "选择文件");
				if(result == JFileChooser.APPROVE_OPTION){
					String name = chooseFileFileChooser.getSelectedFile().getPath();
					chooseFileTextField.setText(name);
					//To do 这里应将文件名传出！！
					fileName = name;
					//System.out.println(name);
				}
			}
		});
		middlePanel1.add(topBox1);
		//第二行，设置接收方IP
		Box topBox2 = Box.createHorizontalBox();
		topBox2.add(Box.createHorizontalStrut(80)); 
		topBox2.add(ipAddressLabel);
		topBox2.add(Box.createHorizontalStrut(20)); 
		topBox2.add(ipAddressTextField);
		topBox2.add(Box.createHorizontalStrut(80)); 
		
		middlePanel1.add(topBox2);
		//第三行，设置接收方端口
		Box topBox3 = Box.createHorizontalBox();
		topBox3.add(Box.createHorizontalStrut(80)); 
		topBox3.add(portLabel1);
		topBox3.add(Box.createHorizontalStrut(20)); 
		topBox3.add(portTextField1);
		topBox3.add(Box.createHorizontalStrut(80)); 
		middlePanel1.add(topBox3);
		portTextField1.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				int keyChar = e.getKeyChar();                 
                if(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9){  
                      
                }else{  
                    e.consume(); //关键，屏蔽掉非法输入  
                }  
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		middlePanel1.add(topBox3);
		//第四行，添加发送按钮
		Box topBoxsend = Box.createHorizontalBox();
		topBoxsend.add(Box.createHorizontalStrut(140));
		topBoxsend.add(sendButton);
		topBoxsend.add(Box.createHorizontalStrut(100));
		middlePanel1.add(topBoxsend);
		
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendButtonAction();
			}
		});
		//向Tab添加标签
		tabbedPane.addTab("发送文件",middlePanel1);
		//*************************接收文件件Tab*******************************
		middlePanel2.setLayout(new GridLayout(3, 1,20,30));
		//第一行，选择文件存放路径
		Box topBox4 = Box.createHorizontalBox();
		topBox4.add(Box.createHorizontalStrut(20)); 
		topBox4.add(chooseFilePathLabel);
		topBox4.add(Box.createHorizontalStrut(20)); 
		topBox4.add(chooseFilePathTextField);
		topBox4.add(Box.createHorizontalStrut(20)); 
		topBox4.add(chooseFilePathButton);
		topBox4.add(Box.createHorizontalStrut(20)); 
		
		chooseFilePathTextField.setEditable(false);
		chooseFilePathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//点击“选择路径”事件
		chooseFilePathButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//chooseFileFileChooser.setCurrentDirectory();
				int result = chooseFilePathFileChooser.showDialog(jFrame, "选择文件存放目录");
				if(result == JFileChooser.APPROVE_OPTION){
					String name = chooseFilePathFileChooser.getSelectedFile().getPath();
					chooseFilePathTextField.setText(name);
					//To do 这里应将目录传出！！
					filePath = name;
					//System.out.println(name);
				}
			}
		});
		middlePanel2.add(topBox4);
		//第二行，设置接收方端口
		Box topBox5 = Box.createHorizontalBox();
		topBox5.add(Box.createHorizontalStrut(70)); 
		topBox5.add(portLabel2);
		topBox5.add(Box.createHorizontalStrut(20)); 
		topBox5.add(portTextField2);
		topBox5.add(Box.createHorizontalStrut(70)); 
		middlePanel2.add(topBox5);
		tabbedPane.addTab("接收文件",middlePanel2);
		portTextField2.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				int keyChar = e.getKeyChar();                 
                if(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9){  
                      
                }else{  
                    e.consume(); //关键，屏蔽掉非法输入  
                } 
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		middlePanel2.add(topBox5);
		
		//第三行，添加接收按钮
		Box topBoxreceive = Box.createHorizontalBox();
		topBoxreceive.add(Box.createHorizontalStrut(140));
		topBoxreceive.add(receiveButton);
		topBoxreceive.add(Box.createHorizontalStrut(100));
		middlePanel2.add(topBoxreceive);
		receiveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				receiveButtonAction();
			}
		});
		tabbedPane.addTab("接收文件",middlePanel2);
		
	}
	/**
	 * 初始化底部Panel
	 */
	private void initBottom(){
		
		boottomPanel.add(new JScrollPane(textArea));
		textArea.setEditable(false);
	}
	public void show(){
		
		//添加Top Panel
		((JPanel)jFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(15,60,20,60));
		  //水平间隔120，垂直间隔80
		jFrame.getContentPane().setLayout(new GridLayout(3,1,50,30));
		jFrame.add(topPanel);
		//添加Middle Panel
		jFrame.add(tabbedPane);
		//添加Bottom Panel
		jFrame.add(boottomPanel);
		//设置Jframe属性
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.pack();//自适应大小
		jFrame.setVisible(true);
		jFrame.setLocationRelativeTo(null);
		jFrame.setSize(500,600);
	}
	/**
	 * 弹出填写证书库密码的框
	 * @param parent
	 * @param filePath
	 * @return
	 */
	private boolean showKeyStorePassDialog(Component parent,String filePath){
		JPasswordField pf=new JPasswordField(15); 
		int result;
		result = JOptionPane.showOptionDialog(chooseKeyStoreFileChooser, pf, "请输入密码：", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if(result == JOptionPane.CANCEL_OPTION){
			return false;
		}
		while(result == JOptionPane.OK_OPTION){
			if(result == JOptionPane.CANCEL_OPTION){
				return false;
			}
			if(pf.getText().trim().isEmpty()){
				JOptionPane.showConfirmDialog(null, "密码不能为空！", "提示", JOptionPane.DEFAULT_OPTION);
				result = JOptionPane.showOptionDialog(chooseKeyStoreFileChooser, pf, "请输入密码：", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				continue;
			}
			//判断密码是否正确
			if(keyManage.setKeyStoreFile(filePath, pf.getText().trim())){
				keyStoreFile = filePath;
				return true;
			}else{
				keyStoreFile = "";
				JOptionPane.showConfirmDialog(null, "密码不正确！", "提示", JOptionPane.DEFAULT_OPTION);
				result = JOptionPane.showOptionDialog(chooseTrustKeyStoreFileChooser, pf, "请输入密码：", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				continue;
			}
		}
		if(result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION){
			return false;
		}
		System.out.println(pf.getPassword());
		return true;
	}
	/**
	 * 弹出填写证书库密码的框
	 * @param parent
	 * @param filePath
	 * @return
	 */
	private boolean showTrustKeyStorePassDialog(Component parent,String filePath){
		JPasswordField pf=new JPasswordField(15); 
		int result;
		result = JOptionPane.showOptionDialog(chooseTrustKeyStoreFileChooser, pf, "请输入密码：", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if(result == JOptionPane.CANCEL_OPTION){
			return false;
		}
		while(result == JOptionPane.OK_OPTION){
			if(result == JOptionPane.CANCEL_OPTION){
				return false;
			}
			if(pf.getText().trim().isEmpty()){
				JOptionPane.showConfirmDialog(null, "密码不能为空！", "提示", JOptionPane.DEFAULT_OPTION);
				result = JOptionPane.showOptionDialog(chooseTrustKeyStoreFileChooser, pf, "请输入密码：", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				continue;
			}
			//判断密码是否正确
			if(keyManage.setTrustKeyStoreFile(filePath, pf.getText().trim())){
				keyTrustStoreFile = filePath;
				return true;
			}else{
				keyTrustStoreFile = "";
				JOptionPane.showConfirmDialog(null, "密码不正确！", "提示", JOptionPane.DEFAULT_OPTION);
				result = JOptionPane.showOptionDialog(chooseTrustKeyStoreFileChooser, pf, "请输入密码：", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				continue;
			}
		}
		if(result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION){
			return false;
		}
		System.out.println(pf.getPassword());
		return true;
	}
	/**
	 * 发送按钮事件
	 */
	private void sendButtonAction(){
		address = ipAddressTextField.getText().trim();
		portStr = portTextField1.getText().trim();
		//检查数据合法性
		if(keyStoreFile.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "请先选择要使用的证书库！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		if(keyTrustStoreFile.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "请先选择要使用的证书信任库！", "提示", JOptionPane.DEFAULT_OPTION);
		}
		if(fileName.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "请先选择要发送的文件！", "提示", JOptionPane.DEFAULT_OPTION);
		}
		if(address.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "IP地址不能为空！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		if(portStr.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "port不能为空！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		port = Integer.valueOf(portStr);
		if(port > 65535 || port <0){
			JOptionPane.showConfirmDialog(jFrame, "端口号不在合法范围！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		//先将按钮置为不可用
		sendButton.setEnabled(false);
		//以下为新线程内进行，
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//1、重新初始化KeyManage，检查是否初始化完成
				if(!keyManage.isReady()){
					JOptionPane.showConfirmDialog(jFrame, keyManage.getLastError(), "提示", JOptionPane.DEFAULT_OPTION);
					sendButton.setEnabled(true);
					return;
				}
				//2、重置ClientConnector，传入KeyManage参数
				clientConnector.reset(keyManage);
				//3、连接接收端
				if(!clientConnector.connect(address,port)){
					JOptionPane.showConfirmDialog(jFrame, "连接服务端失败！", "提示", JOptionPane.DEFAULT_OPTION);
					sendButton.setEnabled(true);
					return;
				}
				//4、发送文件
				fileSender.setSocket(clientConnector.getSSLSocket());
				if(!fileSender.sendFile(fileName)){
					JOptionPane.showConfirmDialog(jFrame, "发送文件失败，原因：\n"+fileSender.getLastError(), "提示", JOptionPane.DEFAULT_OPTION);
					sendButton.setEnabled(true);
					return;
				}
				//发送成功
				clientConnector.closeConnection();
				JOptionPane.showConfirmDialog(jFrame, "文件发送成功！", "提示", JOptionPane.DEFAULT_OPTION);
				sendButton.setEnabled(true);
			}
		});
		thread.start();
	}
	/**
	 * 接收按钮事件
	 */
	private void receiveButtonAction(){
		portStr = portTextField2.getText().trim();
		//检查数据合法性
		if(keyStoreFile.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "请先选择要使用的证书库！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		if(keyTrustStoreFile.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "请先选择要使用的证书信任库！", "提示", JOptionPane.DEFAULT_OPTION);
		}
		if(filePath.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "请先选择要发送的文件！", "提示", JOptionPane.DEFAULT_OPTION);
		}
		if(portStr.isEmpty()){
			JOptionPane.showConfirmDialog(jFrame, "port不能为空！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		port = Integer.valueOf(portStr);
		if(port > 65535 || port <0){
			JOptionPane.showConfirmDialog(jFrame, "端口号不在合法范围！", "提示", JOptionPane.DEFAULT_OPTION);
			return;
		}
		//先将按钮置为不可用
		receiveButton.setEnabled(false);
		//以下为新线程内进行，
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//1、重新初始化KeyManage，检查是否初始化完成
				if(!keyManage.isReady()){
					JOptionPane.showConfirmDialog(jFrame, keyManage.getLastError(), "提示", JOptionPane.DEFAULT_OPTION);
					receiveButton.setEnabled(true);
					return;
				}
				//2、重置ClientConnector，传入KeyManage参数
				serverConnector.reset(keyManage);
				//3、开始监听
				if(!serverConnector.startListen(port)){
					JOptionPane.showConfirmDialog(jFrame, "等待发送端连接失败！", "提示", JOptionPane.DEFAULT_OPTION);
					receiveButton.setEnabled(true);
					return;
				}
				//4、接收
				fileReceiver.setSocket(serverConnector.getSSLSocket());
				if(!fileReceiver.receiveFile(filePath)){
					JOptionPane.showConfirmDialog(jFrame, "文件接收失败，原因：\n"+fileReceiver.getLastError(), "提示", JOptionPane.DEFAULT_OPTION);
					receiveButton.setEnabled(true);
					return;
				}
				//接收成功
				serverConnector.closeConnection();
				JOptionPane.showConfirmDialog(jFrame, "文件接收成功！", "提示", JOptionPane.DEFAULT_OPTION);
				receiveButton.setEnabled(true);;
			}
		});
		thread.start();
	}
	public static void main(String args[]){
		MainFrame mainFrame = new MainFrame();
		mainFrame.init();
		mainFrame.show();
	}
}
