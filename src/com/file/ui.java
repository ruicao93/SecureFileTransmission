package com.file;

import java.applet.*;
import java.awt.*;

import javax.swing.*;

public class ui extends Applet{
    JFrame f = new JFrame("安全公文传输系统");
	JPanel p1 = new JPanel(); 
	JPanel p2 = new JPanel(); 
	JTextField srctext = new JTextField(20);
	JTextField destext = new JTextField(20);
	JTextField cer = new JTextField(20);
	
	JLabel srctext1 = new JLabel("keyStoreFile:",JLabel.CENTER);
	JLabel destext1 = new JLabel("trustKeySoreFile:",JLabel.CENTER);
	
	
	JButton b1 = new JButton("choose");
	JButton b2 = new JButton("choose");
	JButton b3 = new JButton("choose");
	JButton OK = new JButton("OK");
	
	String[] type = {"clear","RSA"};
	JComboBox type1 = new JComboBox();
	
	public void init(){
		
		f.setSize(700, 300);
		p1.setSize(700, 200);
		p2.setSize(700,50);
		
		
		f.add(p1);
		f.add(p2);
		f.setLayout(null);
		f.setVisible(true);//设置f的可见性为TRUE
		f.setResizable(false);//设置f的窗体不能被用户拖拉
		p1.setLayout(new GridLayout(2,3));
		
		p2.setLocation(0,200);
		p2.add(OK);
		
		
		p1.add(srctext1);
		p1.add(srctext);
		p1.add(b1);
		p1.add(destext1);
		p1.add(destext);
		p1.add(b2);
		p1.add(cer);
		p1.add(b3);
		
		
		for(int i=0;i<2;i++)
			type1.addItem(type[i]);
		p1.add(type1);
		
		
	}

}
