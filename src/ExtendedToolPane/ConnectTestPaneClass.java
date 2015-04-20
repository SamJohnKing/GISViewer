package ExtendedToolPane;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import MapKernel.MapControl;
public class ConnectTestPaneClass extends MapKernel.ToolPanel implements ExtendedToolPaneInterface,ActionListener{
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("BackGround34.jpg");
		g.drawImage(img,0,0,280,680,this);
	}
	MapKernel.MapControl MainHandle;
	JLabel PaneTitle;
	JRadioButton Origin,Terminal;
	JCheckBox TheShortestPath;
	JButton Search,Back,Forward;
	ButtonGroup Group;
	int Scan;
	double OriginLongitude=0,OriginLatitude=0,TerminalLongitude=0,TerminalLatitude=0;
	public ConnectTestPaneClass(){
		PaneTitle=new JLabel("ConnectTestPane");
		PaneTitle.setFont(new Font("华文新魏",Font.BOLD,30));
		add(PaneTitle);
		Origin=new JRadioButton("Choose Origin");
		Origin.setOpaque(false);
		Terminal=new JRadioButton("Choose Terminal");
		Terminal.setOpaque(false);
		Group=new ButtonGroup();
		Group.add(Origin);
		Group.add(Terminal);
		add(Origin);
		add(Terminal);
		TheShortestPath=new JCheckBox("Use SPFA to Search");
		TheShortestPath.setOpaque(false);
		add(TheShortestPath);
		Search=new JButton("Search For the path");
		Search.addActionListener(this);
		add(Search);
		Back=new JButton("Click to Back Path");
		Back.addActionListener(this);
		Forward=new JButton("Click to Forward Path");
		Forward.addActionListener(this);
		add(Back);
		add(Forward);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(!MainHandle.getKernel().CacheRoadNetworkDatabase.Loaded){
			JOptionPane.showMessageDialog(null,"请先生成路网","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		MapKernel.CacheRoadNetworkDatabaseClass NetDB=MainHandle.getKernel().CacheRoadNetworkDatabase;
		if(e.getSource()==Search){
		if(!TheShortestPath.isSelected()){
			NetDB.BFS(OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude);
			ShowPath(1);
			MainHandle.ChangeTitle("==========>Total Path:"+NetDB.AnsTail[0]);
			Scan=1;
		}else{
			NetDB.SPFA(OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude);
			ShowPath(1);
			MainHandle.ChangeTitle("==========>Total Path:"+NetDB.AnsTail[0]);
			Scan=1;
		}
		}else if(e.getSource()==Back){
			Scan--;
			ShowPath(Scan);
			MainHandle.ChangeTitle("Scan the "+Scan+" of "+NetDB.AnsTail[0]);
		}else if(e.getSource()==Forward){
			Scan++;
			ShowPath(Scan);
			MainHandle.ChangeTitle("Scan the "+Scan+" of "+NetDB.AnsTail[0]);
		}
	}
	double[] tx=new double[10000];
	double[] ty=new double[10000];
	int[] tref=new int[10000];
	int tcount;
	public void ShowPath(int k){
		double stx=OriginLongitude;
		double sty=OriginLatitude;
		double enx=TerminalLongitude;
		double eny=TerminalLatitude;
		MapKernel.CacheRoadNetworkDatabaseClass NetDB=MainHandle.getKernel().CacheRoadNetworkDatabase;
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		Database.PointDataSet PointDB=MainHandle.getPointDatabase();
		LineDB.DatabaseDelete("[info:Cache]");
		if(k>NetDB.AnsTail[0]){
			JOptionPane.showMessageDialog(null,"此路径为空","边界提示",JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(k<1){
			JOptionPane.showMessageDialog(null,"此路径为空","边界提示",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int ptr=NetDB.AnsTail[k];
		tcount=1;
		tx[0]=enx;
		ty[0]=eny;
		tref[0]=-1;
		while(ptr!=-1){
			tx[tcount]=NetDB.AllPointX[NetDB.Queue[ptr]];
			ty[tcount]=NetDB.AllPointY[NetDB.Queue[ptr]];
			tref[tcount]=NetDB.AllPointReference[NetDB.Queue[ptr]];
			tcount++;
			ptr=NetDB.Father[ptr];
		}
		tx[tcount]=stx;
		ty[tcount]=sty;
		tref[tcount]=-1;
		tcount++;
		LineDB.DatabaseDelete("[Info:Cache][Info:Path]");
		PointDB.DatabaseDelete("[Info:Cache][Info:Path]");
		LineDB.add(tx,ty,tcount,"[Info:Cache][Info:Path][Title:]");
		LineDB.LineVisible[LineDB.LineNum-1]=6153;// Pink Line
		double temp=0;
		for(int i=tcount-1;i>0;i--){
			temp+=MainHandle.AccurateMeterDistance(tx[i],ty[i],tx[i-1],ty[i-1]);
			if(tref[i]!=tref[i-1]){
				if(tref[i-1]!=-1){
					PointDB.add(tx[i-1],ty[i-1],"[Title:进入"+LineDB.getTitle(tref[i-1])+"][Info:Cache][Info:Path]");
					PointDB.PointVisible[PointDB.PointNum-1]=727;//Red Word and Red Point
				}
			}
		}
		MainHandle.ShowTextArea1("PathLength: "+(int)temp+" Meter",true);
		MainHandle.ScreenFlush();
	}
	@Override
	public void setLongitudeLatitude(double x, double y) {
		// TODO Auto-generated method stub
	}

	@Override
	public void emerge() {
		TheShortestPath.setSelected(false);
		Origin.setSelected(true);
		// TODO Auto-generated method stub
	}

	@Override
	public void convey(double x, double y) {
		// TODO Auto-generated method stub
		Database.PointDataSet PointDB=MainHandle.getKernel().PointDatabase;
		if(Origin.isSelected()){
			OriginLongitude=x;
			OriginLatitude=y;
			PointDB.DatabaseDelete("[Title:起点][Info:Cache]");
			PointDB.add(x,y,151,"[Title:起点][Info:Cache]");
		}else{
			TerminalLongitude=x;
			TerminalLatitude=y;
			PointDB.DatabaseDelete("[Title:终点][Info:Cache]");
			PointDB.add(x,y,151,"[Title:终点][Info:Cache]");
		}
		MainHandle.ScreenFlush();
	}

	@Override
	public void convey(double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHandle(MapControl MainHandle) {
		// TODO Auto-generated method stub
		this.MainHandle=MainHandle;
	}

	@Override
	public void setLongitude(double Longitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLatitude(double Latitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return "ConnectTestPane";
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
