package ExtendedToolPane;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import MapKernel.FileAccept;
import MapKernel.MapControl;
import MapKernel.ToolPanel;

public  class ClientSocketPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	public String getString(){
		return "ClientSocketPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
		ClientHandle.setHandle(MainHandle);
	}
	public ClientSocketPaneClass(){
		JLabel Title=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("ClientSocketPane"));
		Title.setFont(new Font("华文新魏",Font.BOLD,30));
		Title.setForeground(Color.orange);
		add(Title);
		add(ScreenLockButton);
		ScreenLockButton.addActionListener(this);
		add(ScreenUnLockButton);
		ScreenUnLockButton.addActionListener(this);
		SpecificProcess();
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("BackGround21.jpg"));
		g.drawImage(img,0,0,280,680,this);
	}
	public void setLongitudeLatitude(double x,double y){}
	public void setLongitude(double x){};
	public void setLatitude(double y){};
	public void actionPerformed(ActionEvent e){
		try{
		if(e.getSource()==ScreenLockButton){
			MainHandle.ScreenLock(true);
			ScreenLockButton.setEnabled(false);
			ScreenUnLockButton.setEnabled(true);
		}else if(e.getSource()==ScreenUnLockButton){
			MainHandle.ScreenLock(false);
			ScreenLockButton.setEnabled(true);
			ScreenUnLockButton.setEnabled(false);
		}else if(e.getSource()==StartButton){
			try{
				// TODO Auto-generated method stub
				if(SocketCommandLineField.getText()!=null)
				if(!SocketCommandLineField.getText().equals(""))
				{
					ClientHandle.SendMsg(SocketIPField.getText(),Integer.parseInt(SocketNumField.getText()),SocketCommandLineField.getText());
					return;
				}
				ClientHandle.SendMsg(SocketIPField.getText(),Integer.parseInt(SocketNumField.getText()),"Ping::");
			}catch(Exception ex){
					MainHandle.SolveException(ex);
				}
		}
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
	}
	//Specific Part--------------------------------------------
	InternetHandle.ClientHandle ClientHandle;
	JButton StartButton;
	JTextField SocketIPField,SocketNumField,SocketCommandLineField;
	public void SpecificProcess(){
		ClientHandle=new InternetHandle.ClientHandle();
		JLabel IP=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("ClientSocketIP"));
		IP.setFont(new Font("华文新魏",Font.BOLD,16));
		IP.setForeground(Color.orange);
		add(IP);
		SocketIPField=new JTextField(10);
		add(SocketIPField);
		JLabel port=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("ClientSocketPort"));
		port.setFont(new Font("华文新魏",Font.BOLD,16));
		port.setForeground(Color.orange);
		add(port);
		SocketNumField=new JTextField(10);
		add(SocketNumField);
		JLabel CommandLabel=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("Command Line Through Socket"));
		CommandLabel.setFont(new Font("华文新魏",Font.BOLD,16));
		CommandLabel.setForeground(Color.orange);
		add(CommandLabel);
		SocketCommandLineField=new JTextField(18);
		add(SocketCommandLineField);
		StartButton=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("Exe"));
		add(StartButton);
		StartButton.addActionListener(this);
	}
	public void itemStateChanged(ItemEvent e) {
	}
	public void emerge(){
		//-----------------
		//-----------------
	}
	public void convey(double x,double y){
		JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("ConveyPoint"));
	}
	public void convey(double x1,double y1,double x2,double y2){
		JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("ConveyRegion"));
	}
	@Override
	public void confirm() {
		JOptionPane.showMessageDialog(null, MapKernel.MapWizard.LanguageDic.GetWords("ConfirmFunction"));
		// TODO Auto-generated method stub
	}
	@Override
	public String GetSocketResult(String SocketQuery) {
		// TODO Auto-generated method stub
		return null;
	}
}
