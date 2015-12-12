package ExtendedToolPane;

import MapKernel.MapControl;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class AutoCrossLinkPaneClass extends MapKernel.ToolPanel implements ExtendedToolPaneInterface,ActionListener {
	MapKernel.MapControl MainHandle;
	JLabel PaneTitle;
	JTextField CrossCrossLimit,CrossRoadLimit,CrossAngleLimit;
	JButton Confirm;
	public AutoCrossLinkPaneClass(){
		PaneTitle=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("AutoCrossLinkPane"));
		PaneTitle.setFont(new Font("华文新魏",Font.BOLD,25));
		add(PaneTitle);
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("Cross-Cross Distance Upper Limit/m")));
		CrossCrossLimit=new JTextField(20);
		add(CrossCrossLimit);
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("Cross-Skeleton Distance Upper Limit/m")));
		CrossRoadLimit=new JTextField(20);
		add(CrossRoadLimit);
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("Cross-Skeleton Angle Upper Limit/360")));
		CrossAngleLimit=new JTextField(20);
		add(CrossAngleLimit);
		Confirm=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("Confirm to Operate"));
		Confirm.addActionListener(this);
		add(Confirm);
	}
	@Override
	public void setLongitudeLatitude(double x, double y) {
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("BackGround34.jpg"));
		g.drawImage(img,0,0,280,680,this);
	}
	@Override
	public void emerge() {
	}
	@Override
	public void convey(double x, double y) {
	}
	@Override
	public void convey(double x1, double y1, double x2, double y2) {
	}

	@Override
	public void setHandle(MapControl MainHandle) {
		this.MainHandle=MainHandle;
	}

	@Override
	public void setLongitude(double Longitude) {
	}

	@Override
	public void setLatitude(double Latitude) {
	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return "AutoCrossLinkPane";
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		double l1,l2,l3;
		try{
			l1=Double.parseDouble(this.CrossCrossLimit.getText());
			l2=Double.parseDouble(this.CrossRoadLimit.getText());
			l3=Double.parseDouble(this.CrossAngleLimit.getText());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("格式错误"),
					MapKernel.MapWizard.LanguageDic.GetWords("格式错误"),JOptionPane.WARNING_MESSAGE);
			return;
		}
		MainHandle.getKernel().CacheRoadNetworkDatabase.Init();
		MainHandle.ScreenFlush();
	}
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String GetSocketResult(String SocketQuery) {
		// TODO Auto-generated method stub
		return null;
	}
}
