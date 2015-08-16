package com.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class JksFileFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
		// TODO Auto-generated method stub
		String fileName = f.getName();
		if(fileName.toLowerCase().endsWith(".jks")){
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		
		return "证书库文件(*.jks)";
	}

}
