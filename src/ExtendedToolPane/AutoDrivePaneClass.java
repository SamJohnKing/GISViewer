package ExtendedToolPane;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import Database.RTreeIndex;
import Database.TimeStampPointStructure;
import MapKernel.MapControl;

public class AutoDrivePaneClass extends MapKernel.ToolPanel implements ExtendedToolPaneInterface,ActionListener{
	class Photo extends JPanel{//专门用来显示图片
		String PhotoName=null;
		public Photo(){
			setBounds(0,0,240,200);
			setPreferredSize(new Dimension(240,200));
		}
		public void paint(Graphics g){
			Toolkit kit=getToolkit();
			Image img=kit.getImage("DefaultPhoto.jpg");
			if((MainHandle.getKernel().ImageDir!=null)&&(PhotoName!=null)){
				System.out.println(MainHandle.getKernel().ImageDir.toString()+"/"+PhotoName);
				File f=new File(MainHandle.getKernel().ImageDir,PhotoName);
				if(f.exists()){
					img=kit.getImage(MainHandle.getKernel().ImageDir.toString()+"/"+PhotoName);
				}
			}
			g.drawImage(img,0,0,240,200,this);
		}
	}
	MapKernel.MapControl MainHandle;
	JLabel PaneTitle;
	JCheckBox ClickToChoose,SelectOrigin,SelectTerminal,ShowMoreInfo;
	JButton StartAutoDrive,StopAutoDrive,StartServer,StopServer,StartClient,StopClient;
	JTextField IPField=new JTextField(9);
	JTextField PortField=new JTextField(3);
	ButtonGroup Group;
	JTextField AutoVelocity;
	Photo LandMarkPhoto;
	JTextArea Script;
	JScrollPane ScrollPane;
	String[][] Result=new String[10000][3];
	String[] Title={"Sequence","Identifier","Record"};
	JTable ResultTable;
	TableModel Content;
	JScrollPane Handle;
	int[] Hit=new int[10000];
	HashMap<String,ArrayList<Database.TimeStampPointStructure>> TrajectoryDB=new HashMap<String,ArrayList<Database.TimeStampPointStructure>>();
	HashMap<String,Boolean> RecordList=new HashMap<String,Boolean>();
	//-----------------------------------------------------------------
	JTextField IdentifierFilter1,IdentifierFilter2;
	JTextField LandmarkFilter,DistanceFilter;
	JCheckBox ClientShadowing,CrossHintSwitch;
	JButton ShadowingSwitch,ClearRecord;
	JButton RecordSwitch,RecordAllSwitch;
	private Database.RTreeIndex PointRTree=null;
	//-----------------------------------------------------------------
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("BackGround34.jpg");
		g.drawImage(img,0,0,280,680,this);
	}
	JPanel ServerPane,ClientPane;
	public AutoDrivePaneClass(){
		PaneTitle=new JLabel("LBS AutoDriver Panel");
		PaneTitle.setFont(new Font("华文新魏",Font.BOLD,25));
		add(PaneTitle);
		add(new JLabel("IPADDRESS:"));
		add(IPField);
		add(new JLabel("PORT:"));
		add(PortField);
		StartServer=new JButton("Start Server");
		StartServer.addActionListener(this);
		add(StartServer);
		StopServer=new JButton("Stop Server");
		StopServer.addActionListener(this);
		add(StopServer);
		StartClient=new JButton("Start Client");
		StartClient.addActionListener(this);
		add(StartClient);
		StopClient=new JButton("Stop Client");
		StopClient.addActionListener(this);
		add(StopClient);
		ClientPane=new JPanel();
		ClientPane.setPreferredSize(new Dimension(280,500));
		ClientPane.setOpaque(false);
		ClientPane.setVisible(false);
		add(ClientPane);
		ServerPane=new JPanel();
		ServerPane.setPreferredSize(new Dimension(280,500));
		ServerPane.setOpaque(false);
		ServerPane.setVisible(false);
		add(ServerPane);
		//--------------------------------------------------
		ClickToChoose=new JCheckBox("Click to Select");
		ClientPane.add(ClickToChoose);
		SelectOrigin=new JCheckBox("Origion");
		SelectTerminal=new JCheckBox("Terminal");
		Group=new ButtonGroup();
		Group.add(SelectOrigin);
		Group.add(SelectTerminal);
		ClientPane.add(SelectOrigin);
		ClientPane.add(SelectTerminal);
		ClientPane.add(new JLabel("Auto Km/h"));
		AutoVelocity=new JTextField(4);
		AutoVelocity.addActionListener(this);
		ClientPane.add(AutoVelocity);
		ShowMoreInfo=new JCheckBox("Hotspot Recommend");
		ClientPane.add(ShowMoreInfo);
		ClientShadowing=new JCheckBox("Client Shadowing");
		ClientShadowing.setSelected(true);
		ClientShadowing.setOpaque(false);
		ClientPane.add(ClientShadowing);
		CrossHintSwitch=new JCheckBox("Cross Hint Switch");
		CrossHintSwitch.setSelected(false);
		CrossHintSwitch.setOpaque(false);
		ClientPane.add(CrossHintSwitch);
		//---------------------------------------------------
		StartAutoDrive=new JButton("Start Auto Drive");
		StopAutoDrive=new JButton("Stop Auto Drive");
		StartAutoDrive.addActionListener(this);
		StopAutoDrive.addActionListener(this);
		ClientPane.add(StartAutoDrive);
		ClientPane.add(StopAutoDrive);
		ClickToChoose.setOpaque(false);
		SelectOrigin.setOpaque(false);
		SelectTerminal.setOpaque(false);
		ShowMoreInfo.setOpaque(false);
		LandMarkPhoto=new Photo();
		ClientPane.add(LandMarkPhoto);
		Script=new JTextArea(9,22);
		Script.setLineWrap(true);
		ScrollPane=new JScrollPane(Script,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ClientPane.add(ScrollPane);
		LandMarkPhoto.setVisible(false);
		ScrollPane.setVisible(false);
		//------------------------------------------------------------
		IdentifierFilter1=new JTextField(12);
		ServerPane.add(new JLabel("IDENTIFIERFILTER1"));
		ServerPane.add(IdentifierFilter1);
		IdentifierFilter1.addActionListener(this);
		IdentifierFilter2=new JTextField(12);
		ServerPane.add(new JLabel("IDENTIFIERFILTER2"));
		ServerPane.add(IdentifierFilter2);
		IdentifierFilter2.addActionListener(this);
		LandmarkFilter=new JTextField(7);
		ServerPane.add(new JLabel("ToLandmark"));
		ServerPane.add(LandmarkFilter);
		LandmarkFilter.addActionListener(this);
		ServerPane.add(new JLabel("in"));
		DistanceFilter=new JTextField(3);
		ServerPane.add(DistanceFilter);
		DistanceFilter.addActionListener(this);
		ServerPane.add(new JLabel("Meters"));
		DefaultTableModel model = new DefaultTableModel(Result,Title) {
			  public boolean isCellEditable(int row, int column) {
				  return false;
			  }
			};
		ResultTable=new JTable(model);
		Content=ResultTable.getModel();
		Handle=new JScrollPane(ResultTable);
		Handle.setPreferredSize(new Dimension(270,320));
		ServerPane.add(Handle);
		ShadowingSwitch=new JButton("Shadowing Switch");
		ShadowingSwitch.addActionListener(this);
		ServerPane.add(ShadowingSwitch);
		RecordSwitch=new JButton("Record Switch");
		RecordSwitch.addActionListener(this);
		ServerPane.add(RecordSwitch);
		ClearRecord=new JButton("Clear Record");
		ClearRecord.addActionListener(this);
		ServerPane.add(ClearRecord);
		RecordAllSwitch=new JButton("Global Record Switch");
		RecordAllSwitch.addActionListener(this);
		ServerPane.add(RecordAllSwitch);
	}
	@Override
	public void setLongitudeLatitude(double x, double y) {
		// TODO Auto-generated method stub
	}
	@Override
	public void emerge() {
		SelectOrigin.setSelected(true);
		SelectTerminal.setSelected(false);
		ClickToChoose.setSelected(false);
		ShowMoreInfo.setSelected(false);
		OriginValid=false;
		TerminalValid=false;
		IsDrive=false;
		AutoSpeed=-1;
		DetectMaxSpeed=-1;
		DetectMinSpeed=-1;
		CohesionLimit=-1;
		ServerPane.setVisible(false);
		ClientPane.setVisible(false);
		MainHandle.getKernel().CacheRoadNetworkDatabase.Init();
	}
	double OriginX,OriginY,TerminalX,TerminalY,NextX,NextY;
	boolean OriginValid,TerminalValid;
	@Override
	public void convey(double x, double y) {
		if(!ClickToChoose.isSelected()){
			MainHandle.ChangeTitle("Please Click the Permission");
			return;
		}
		if(SelectOrigin.isSelected()){
			MainHandle.getPointDatabase().DatabaseDelete("[Info:Cache][Info:Origin]");
			MainHandle.getPointDatabase().add(x,y,"[Info:Cache][Info:Origin][Title:Origin]");
			MainHandle.ChangeTitle("Origin:"+x+"E/"+y+"N");
			OriginX=x;
			OriginY=y;
			OriginValid=true;
		}else{
			MainHandle.getPointDatabase().DatabaseDelete("[Info:Cache][Info:Terminal]");
			MainHandle.getPointDatabase().add(x,y,"[Info:Cache][Info:Terminal][Title:Terminal]");
			MainHandle.ChangeTitle("Terminal:"+x+"E/"+y+"N");
			TerminalX=x;
			TerminalY=y;
			TerminalValid=true;
		}
		if(OriginValid && TerminalValid){
			MainHandle.getLineDatabase().DatabaseDelete("[Info:Cache][Info:OriginTerminal]");
			MainHandle.getLineDatabase().DynamicAdd(OriginX,OriginY,"[Info:Cache][Info:OriginTerminal][Title:]");
			MainHandle.getLineDatabase().DynamicAdd(TerminalX,TerminalY,"[Info:Cache][Info:OriginTerminal][Title:]");
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
		return "AutoDrivePane";
	}
	public boolean IsDrive;
	double AutoSpeed,DetectMaxSpeed,DetectMinSpeed,CohesionLimit,TextValue;
	int MinVolume;
	private InternetHandle.ClientHandle ClientSocket=null;
	private InternetHandle.ServerHandle ServerSocket=null;
	private String IPAddress=null;
	private int port=-1;
	public boolean CheckAutoVelocityValid(){
		try{
			TextValue=Double.parseDouble(AutoVelocity.getText());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"The Velocity Value is not a Positive Real Number","Format Error",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(TextValue<=0){
			JOptionPane.showMessageDialog(null,"The Velocity Value is not a Positive Real Number","Format Error",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(TextValue>1000){
			JOptionPane.showMessageDialog(null,"The Velocity Value is not a Proper Positive Real Number","Impossible",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		AutoSpeed=TextValue;
		return true;
	}
	private String ClientIdentifier;
	private String ShadowingIdentifier=null;
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==StartAutoDrive){
			if(!CheckAutoVelocityValid()) return;
			if(!OriginValid){
				JOptionPane.showMessageDialog(null,"Origin is not set","Origin Invalid",JOptionPane.WARNING_MESSAGE);
				return;
			}
			if(!TerminalValid){
				JOptionPane.showMessageDialog(null,"Terminal is not set","Terminal Invalid",JOptionPane.WARNING_MESSAGE);
				return;
			}
			IsDrive=true;
			NextX=OriginX;
			NextY=OriginY;
		}else if(arg0.getSource()==StopAutoDrive){
			IsDrive=false;
			MainHandle.getPointDatabase().AttributeDelete("Cache","Origin",null,null,null);
			MainHandle.getPointDatabase().AttributeDelete("Cache","Terminal",null,null,null);
			MainHandle.getLineDatabase().AttributeDelete("Cache","OriginTerminal",null,null,null);
			MainHandle.getLineDatabase().AttributeDelete("Cache","Path",null,null,null);
			MainHandle.getPointDatabase().AttributeDelete("Cache","Path",null,null,null);
			MainHandle.getPointDatabase().AttributeDelete("Cache","ClockDepend","SlowTaxi",null,null);
			MainHandle.getLineDatabase().AttributeDelete("Cache","ClockDepend","SlowTaxi",null,null);
			OriginValid=false;
			TerminalValid=false;
			MainHandle.ScreenFlush();
			MainHandle.PointEmpty();
			MainHandle.setPointVisible(false);
		}else if(arg0.getSource()==AutoVelocity){
			CheckAutoVelocityValid();
		}else if(arg0.getSource()==StartServer){
			ServerPane.setVisible(false);
			ClientPane.setVisible(false);
			try{
				MapKernel.CacheRoadNetworkDatabaseClass NetDB=MainHandle.getKernel().CacheRoadNetworkDatabase;
				if(!NetDB.Loaded) NetDB.Init();
				if(MainHandle.IsPointLoaded()){
					if(PointRTree==null){
						PointRTree=new Database.RTreeIndex();
						PointRTree.IndexInit(MainHandle.getPointDatabase());
					}
				}
				if(ServerSocket==null){
					ServerSocket=new InternetHandle.ServerHandle();
					ServerSocket.setHandle(MainHandle);
					ServerSocket.setProcessor(this);
				}
				port=Integer.parseInt(PortField.getText());
				ServerSocket.StartSocket(port);
			}catch(Exception ex){
				MainHandle.SolveException(ex);
				return;
			}
			ServerPane.setVisible(true);
		}else if(arg0.getSource()==StartClient){
			ServerPane.setVisible(false);
			ClientPane.setVisible(false);
			try{
				ClientIdentifier=JOptionPane.showInputDialog(null,"Input Your Identifier");
				if((ClientIdentifier==null)||(ClientIdentifier.equals(""))){
					JOptionPane.showMessageDialog(null,"Invalid Identifier");
					return;
				}
				MapKernel.CacheRoadNetworkDatabaseClass NetDB=MainHandle.getKernel().CacheRoadNetworkDatabase;
				if(!NetDB.Loaded) NetDB.Init();
				if(ClientSocket==null) ClientSocket=new InternetHandle.ClientHandle();
				IPAddress=IPField.getText();
				port=Integer.parseInt(PortField.getText());
				if(ClientSocket.SendMsg(IPAddress, port,"Ping::").indexOf("Fail::")==0) throw new Exception("Client MSG ERR!!!");
			}catch(Exception ex){
				MainHandle.SolveException(ex);
				return;
			}
			ClientPane.setVisible(true);
		}else if(arg0.getSource()==StopServer){
			ServerPane.setVisible(false);			
			try{
				if(ServerSocket==null) return;
				ServerSocket.EndSocket();
				MainHandle.getPointDatabase().AttributeDelete("Cache","Trajectory",null,null,null);
				MainHandle.getLineDatabase().AttributeDelete("Cache","Trajectory",null,null,null);
				RecordList.clear();
				TrajectoryDB.clear();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(arg0.getSource()==StopClient){
			ClientPane.setVisible(false);
			IsDrive=false;
			MainHandle.getPointDatabase().AttributeDelete("Cache","Origin",null,null,null);
			MainHandle.getPointDatabase().AttributeDelete("Cache","Terminal",null,null,null);
			MainHandle.getLineDatabase().AttributeDelete("Cache","OriginTerminal",null,null,null);
			MainHandle.getLineDatabase().AttributeDelete("Cache","Path",null,null,null);
			MainHandle.getPointDatabase().AttributeDelete("Cache","Path",null,null,null);
			MainHandle.getPointDatabase().AttributeDelete("Cache","ClockDepend","SlowTaxi",null,null);
			MainHandle.getLineDatabase().AttributeDelete("Cache","ClockDepend","SlowTaxi",null,null);
			OriginValid=false;
			TerminalValid=false;
			MainHandle.ScreenFlush();
			MainHandle.PointEmpty();
			MainHandle.setPointVisible(false);
		}else if(arg0.getSource()==ShadowingSwitch){
			int selectRows=ResultTable.getSelectedRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			if(selectRows==1){
			  int selectedRowIndex = ResultTable.getSelectedRow();
			  ShadowingIdentifier=(String)tableModel.getValueAt(selectedRowIndex,1);
			}else{
				ShadowingIdentifier=null;
				JOptionPane.showMessageDialog(null,"Give Up Shadowing","Give Up Shadowing",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}else if(arg0.getSource()==ClearRecord){
			RecordList.clear();
			TrajectoryDB.clear();
		}else if(arg0.getSource()==RecordSwitch){
			int selectRows=ResultTable.getSelectedRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			if(selectRows==1){
			  int selectedRowIndex = ResultTable.getSelectedRow();
			  String key=(String)(String)tableModel.getValueAt(selectedRowIndex,1);
			  String value=(String)tableModel.getValueAt(selectedRowIndex,2);
			  boolean BoolV=Boolean.parseBoolean(value);
			  BoolV=!BoolV;
			  RecordList.put(key,BoolV);
			  ServerQuery();
			}else{
				JOptionPane.showMessageDialog(null,"Please Choose One Row","Please Choose One Row",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}else if(arg0.getSource()==RecordAllSwitch){
			java.util.Iterator iter=RecordList.entrySet().iterator();
			String key;
			Boolean val;
			while(iter.hasNext()){
				java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
				val=(Boolean)entry.getValue();
				entry.setValue(!val);
			}
			ServerQuery();
		}else if(arg0.getSource()==IdentifierFilter1){
			ServerQuery();
		}else if(arg0.getSource()==IdentifierFilter2){
			ServerQuery();
		}else if(arg0.getSource()==LandmarkFilter){
			ServerQuery();
		}else if(arg0.getSource()==DistanceFilter){
			try{
				Double.parseDouble(DistanceFilter.getText());
			}catch(Exception ex){
				DistanceFilter.setText("");
				return;
			}
			ServerQuery();
		}
	}
	public void ServerQuery(){
		if(!ServerPane.isVisible()) return;
		//-------------------------------------------
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		Database.PointDataSet PointDB=MainHandle.getPointDatabase();
		LineDB.AttributeDelete("Cache","Trajectory",null,null,null);
		PointDB.AttributeDelete("Cache","Trajectory",null,null,null);
		//-------------------------------------------
		DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
		while(tableModel.getRowCount()!=0) tableModel.removeRow(0);
		TableColumn Column = ResultTable.getColumnModel().getColumn(0);
		Column.setPreferredWidth(3);
		//-------------------------------------------
		String key;
		String[] temp;
		String buf;
		int Counter=0;
		ArrayList<Integer> res;
		double DistanceUpperLimit=-1;
		boolean hit;
		try{
		if(DistanceFilter.getText()!=null)
		if(!DistanceFilter.getText().equals(""))
			DistanceUpperLimit=Double.parseDouble(DistanceFilter.getText());
		}catch(Exception ex){DistanceUpperLimit=-1;}
		ArrayList<Database.TimeStampPointStructure> val;
		Iterator iter = TrajectoryDB.entrySet().iterator();
		while(iter.hasNext()){
		java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
		key =(String)entry.getKey();
		buf=IdentifierFilter1.getText();
		if((buf!=null)&&(!buf.equals(""))){
			if(key.indexOf(buf)==-1) continue;
		}
		buf=IdentifierFilter2.getText();
		if((buf!=null)&&(!buf.equals(""))){
			if(key.indexOf(buf)==-1) continue;
		}
		val =(ArrayList<Database.TimeStampPointStructure>)entry.getValue();
		double lastx=val.get(val.size()-1).x;
		double lasty=val.get(val.size()-1).y;
		buf=LandmarkFilter.getText();
		if(DistanceUpperLimit>0)
		if((buf!=null)&&(!buf.equals(""))){
			if(PointRTree!=null){
				res=PointRTree.Search(lastx-MainHandle.MetertoLongitude(DistanceUpperLimit),
						lasty-MainHandle.MetertoLatitude(DistanceUpperLimit),
						lastx+MainHandle.MetertoLongitude(DistanceUpperLimit),
						lasty+MainHandle.MetertoLatitude(DistanceUpperLimit));
				if(res.isEmpty()) continue;
				hit=false;
				for(Integer item:res){
					if(PointDB.PointHint[item].indexOf(buf)!=-1){
						hit=true;
						break;
					}
				}
				if(!hit) continue;
			}
		}
		if(ShadowingIdentifier!=null){
			if(key.equals(ShadowingIdentifier)){
				MainHandle.getKernel().Screen.MoveMiddle(lastx,lasty);
			}
		}
		val =(ArrayList<Database.TimeStampPointStructure>)entry.getValue();
		temp=new String[3];
		temp[0]=Integer.toString(Counter);
		Counter++;
		temp[1]=key;
		boolean RecordTrajectory=(Boolean)RecordList.get(key);
		temp[2]=Boolean.toString(RecordTrajectory);
		tableModel.addRow(temp);
		PointDB.add(val,"[Info:Cache][Info:Trajectory][Title:"+key+"]");
		if(RecordTrajectory) LineDB.add(val,"[Info:Cache][Info:Trajectory][Title:"+key+"]");
		}
		//----------------------------------------------
		MainHandle.ScreenFlush();
		ResultTable.validate();
		Handle.setVisible(true);
		validate();
	}
	final static int ImpulseInterval=5;
	public void ClockImpulse(){
		if(ShowMoreInfo.isSelected()){
			LandMarkPhoto.setVisible(true);
			LandMarkPhoto.setVisible(false);
			LandMarkPhoto.setVisible(true);
			ScrollPane.setVisible(true);
			ClientPane.validate();
			ServerPane.validate();
			validate();
		}else{
			LandMarkPhoto.setVisible(false);
			ScrollPane.setVisible(false);
			ClientPane.validate();
			ServerPane.validate();
			validate();
		}
		if(MainHandle.getSecond()%ImpulseInterval!=0) return;
		try{
		MainHandle.ScreenFlush();
		if(IsDrive)
		{
			MapKernel.CacheRoadNetworkDatabaseClass NetDB=MainHandle.getKernel().CacheRoadNetworkDatabase;
			if(!NetDB.Loaded) NetDB.Init();
			Database.PointDataSet PointDB=MainHandle.getPointDatabase();
			Database.LineDataSet LineDB=MainHandle.getLineDatabase();
			Database.PolygonDataSet PolyDB=MainHandle.getPolygonDatabase();
			PointDB.AttributeDelete("Cache","ClockDepend","SlowTaxi",null,null);
			LineDB.AttributeDelete("Cache","ClockDepend","SlowTaxi",null,null);
			PolyDB.AttributeDelete("Cache","ClockDepend","Crowdedness",null,null);
			LineDB.AttributeDelete("Cache","ClockDepend","Crowdedness",null,null);
			//Drive to Terminal------------------------------------
			double DistanceSpan=AutoSpeed*ImpulseInterval*1000/3600.0;
			OriginX=NextX;
			OriginY=NextY;
			if(ClientShadowing.isSelected())
				MainHandle.getKernel().Screen.MoveMiddle(OriginX,OriginY);
			PointDB.DatabaseDelete("[Info:Cache][Info:Origin]");
			PointDB.add(OriginX,OriginY,"[Info:Cache][Info:Origin][Title:Origin]");
			LineDB.DatabaseDelete("[Info:Cache][Info:OriginTerminal]");
			LineDB.DynamicAdd(OriginX,OriginY,"[Info:Cache][Info:OriginTerminal][Title:]");
			LineDB.DynamicAdd(TerminalX,TerminalY,"[Info:Cache][Info:OriginTerminal][Title:]");
			//LandMarkHint---------------------------------------------------------------------------
			MapKernel.GPSSet LandMarkDB=MainHandle.getKernel().GPSPoints;
			double mindis=1e100;
			int best=-1;
			for(int i=0;i<LandMarkDB.LandMarkNum;i++){
				if(MainHandle.AccurateDistance(LandMarkDB.LandMarkLongitude[i],LandMarkDB.LandMarkLatitude[i],OriginX,OriginY)<mindis){
					mindis=MainHandle.AccurateDistance(LandMarkDB.LandMarkLongitude[i],LandMarkDB.LandMarkLatitude[i],OriginX,OriginY);
					best=i;
				}
			}
			if(best>=0){
			int mindist=(int)MainHandle.AccurateMeterDistance(LandMarkDB.LandMarkLongitude[best],LandMarkDB.LandMarkLatitude[best],OriginX,OriginY);
			MainHandle.ShowTextArea2("To["+LandMarkDB.LandMarkName[best]+"]:"+mindist+"M",true);
			LandMarkPhoto.PhotoName=LandMarkDB.LandMarkName[best]+".jpg";
			LandMarkPhoto.repaint();
			Script.setText(LandMarkDB.LandMarkScript[best]);
			}
			MainHandle.PointEmpty();
			MainHandle.PointPush(OriginX,OriginY);
			if(best>=0)
				MainHandle.PointPush(LandMarkDB.LandMarkLongitude[best],LandMarkDB.LandMarkLatitude[best]);
			MainHandle.setPointVisible(true);
			MainHandle.setPointConsecutiveLinkVisible(true);
			MainHandle.ScreenFlush();
			//Check Terminal----------------------------------------------------------------------
			if(MainHandle.AccurateMeterDistance(OriginX,OriginY,TerminalX,TerminalY)<50){
				IsDrive=false;
				JOptionPane.showMessageDialog(null,"The Terminal Target Arrived","Congratulations!!!",JOptionPane.WARNING_MESSAGE);
				return;
			}
			NetDB.DynamicSPFA(OriginX, OriginY, TerminalX, TerminalY);
			ShowPath(DistanceSpan);
			MainHandle.ScreenFlush();
		}
		if(ServerPane.isVisible()){
			ServerQuery();
		}
		}catch(Exception ex){
			MainHandle.SolveException(ex);
			IsDrive=false;
		}
	}
	double[] tx=new double[10000];
	double[] ty=new double[10000];
	int[] tref=new int[10000];
	int tcount;
	public void ShowPath(double DistanceSpan) throws Exception{
		double stx=OriginX;
		double sty=OriginY;
		double enx=TerminalX;
		double eny=TerminalY;
		MapKernel.CacheRoadNetworkDatabaseClass NetDB=MainHandle.getKernel().CacheRoadNetworkDatabase;
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		Database.PointDataSet PointDB=MainHandle.getPointDatabase();
		LineDB.DatabaseDelete("[info:Cache]");
		if(NetDB.AnsTail[0]==0){
			JOptionPane.showMessageDialog(null,"The Condition of Road NetWork is bad,Wait Here","Wait Here",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int ptr=NetDB.AnsTail[1];
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
			if(CrossHintSwitch.isSelected())
			if(tref[i]!=tref[i-1]){
				if(tref[i-1]!=-1){
					PointDB.add(tx[i-1],ty[i-1],"[Title:进入"+LineDB.getTitle(tref[i-1])+"][Info:Cache][Info:Path]");
					PointDB.PointVisible[PointDB.PointNum-1]=727;//Red Word and Red Point
				}
			}
		}
		String str=ClientSocket.SendMsg(IPAddress,port,ClientIdentifier+"::"+stx+"/"+sty+"/"+enx+"/"+eny);
		MainHandle.ShowTextArea1(str.substring(str.indexOf("::")+2)+"/PathLen "+(int)temp+" M",true);
		MainHandle.ScreenFlush();
		temp=0;
		int i;
		for(i=tcount-1;i>0;i--){
			temp+=MainHandle.AccurateMeterDistance(tx[i],ty[i],tx[i-1],ty[i-1]);
			if(temp>DistanceSpan) break;
		}
		if(i==0){
			NextX=TerminalX;
			NextY=TerminalY;
		}else{
			double ratio=1-((temp-DistanceSpan)/MainHandle.AccurateMeterDistance(tx[i],ty[i],tx[i-1],ty[i-1]));
			NextX=tx[i]+ratio*(tx[i-1]-tx[i]);
			NextY=ty[i]+ratio*(ty[i-1]-ty[i]);
		}
	}
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
	}
	@Override
	public String GetSocketResult(String SocketQuery){
		// TODO Auto-generated method stub
		//-------------------------------------------------------------
		try{
		if(SocketQuery.equals("Ping::")) return "Server::Ping";
		int p1=SocketQuery.indexOf("::");
		String str=SocketQuery.substring(0,p1);
		p1+=2;
		int p2=SocketQuery.indexOf('/',p1);
		System.out.println(SocketQuery.substring(p1,p2));
		double x=Double.parseDouble(SocketQuery.substring(p1,p2));
		p2++;
		p1=SocketQuery.indexOf('/',p2);
		System.out.println(SocketQuery.substring(p2,p1));
		double y=Double.parseDouble(SocketQuery.substring(p2,p1));
		int hh=MainHandle.getKernel().ClockWizard.pic.getHour();
		int mm=MainHandle.getKernel().ClockWizard.pic.getMinute();
		int ss=MainHandle.getKernel().ClockWizard.pic.getSecond();
		int TCounter=hh*3600+mm*60+ss;
		System.out.println(TCounter);
		if(RecordList.containsKey(str)){
			ArrayList<Database.TimeStampPointStructure> arr;
			boolean record=(Boolean)RecordList.get(str);
			System.out.println("Check The Record Boolean File");
			arr=(ArrayList<Database.TimeStampPointStructure>)TrajectoryDB.get(str);
			System.out.println("Get The TimeStampPointStructure ArrayList");
			if(record){
				Database.TimeStampPointStructure p=new Database.TimeStampPointStructure(x, y, TCounter);
				System.out.println("Create a New Node");
				arr.add(p);
				System.out.println("Finish The Append Operation");
			}else{
				while(arr.size()>1) arr.remove(0);
				System.out.println("Clear the extra element");
				Database.TimeStampPointStructure p=arr.get(0);
				System.out.println("Get the Point");
				p.x=x;
				p.y=y;
				p.t=TCounter;
				System.out.println("Finish The Update Operation");
			}
		}else{
			Database.TimeStampPointStructure p=new Database.TimeStampPointStructure(x, y, TCounter);
			RecordList.put(str,false);
			ArrayList<Database.TimeStampPointStructure> arr=new ArrayList<Database.TimeStampPointStructure>();
			arr.add(p);
			TrajectoryDB.put(str,arr);
			System.out.println("Insert New One:"+str);
		}
		//-------------------------------------------------------------
		return "Server::"+hh+":"+mm+":"+ss;
		}catch(Exception ex){
			return "Fail::";
		}
	}
}
