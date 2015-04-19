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

public  class GISCompletionPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	JButton TrajectoryToPointsTransformer,LoadClear,AllLoadClear,TrajectoryToLinesTransformer;
	JButton ThrowPointsData,RetriveData,PointsAlgorithmExe1,PointsAlgorithmExe2,PointsAlgorithmExe3;
	JTextField ThrowPointsDataField,RetriveDataField,ExePointsField1,ExePointsField2,ExePointsField3;
	JButton ThrowLinesData,LinesAlgorithmExe1,LinesAlgorithmExe2,LinesAlgorithmExe3;
	JTextField ThrowLinesDataField,ExeLinesField1,ExeLinesField2,ExeLinesField3;
	JCheckBox SelectRoadID,ConveyRegionToAlgorithm;
	public String getString(){
		return "GISCompletionPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public GISCompletionPaneClass(){
		JLabel Title=new JLabel("电子地图补全面板");
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
		Image img=kit.getImage("BackGround21.jpg");
		g.drawImage(img,0,0,280,680,this);
	}
	public void setLongitudeLatitude(double x,double y){}
	public void setLongitude(double x){};
	public void setLatitude(double y){};
	private void ChangePointTitle(int id,int index){
		String str=MainHandle.getPointDatabase().PointHint[id];
		if(str.indexOf("[Info:TemporaryVisual]")==-1) str+="[Info:TemporaryVisual]";
		int st,en;
		if((st=str.indexOf("[Title:"))!=-1){
			en=str.indexOf("]",st);
			str=str.replace(str.substring(st,en+1),"[Title:"+index+"]");
		}else str="[Title:"+index+"]"+str;
		MainHandle.getPointDatabase().PointHint[id]=str;
	}
	private void RemoveTemporaryVisual(int id){
		String str=MainHandle.getPointDatabase().PointHint[id];
		if(str.indexOf("[Info:TemporaryVisual]")!=-1)
		{
			str=str.replace("[Info:TemporaryVisual]","");
			int st,en;
			if((st=str.indexOf("[Title:"))!=-1){
				en=str.indexOf("]",st);
				str=str.replace(str.substring(st,en+1),"[Title:]");
			}else str="[Title:]"+str;
			MainHandle.getPointDatabase().PointHint[id]=str;
		}
	}
	private void PointsDataRelease(){
		try{
		if((ThrowPointsDataField.getText()==null)||(ThrowPointsDataField.getText().equals(""))){
			ThrowPointsDataField.setText(JOptionPane.showInputDialog("Please complete the Output File Name!!!"));
		}
		if(ThrowPointsDataField.getText()==null) return;
		File f=new File(ThrowPointsDataField.getText()+MainHandle.GetInternationalTimeSignature()+".txt");
		BufferedWriter FOut=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,false),"UTF-8"));
		int UpperLimit=MainHandle.getPointDatabase().PointNum;
		for(int i=0;i<UpperLimit;i++){
			if((MainHandle.getPointDatabase().PointVisible[i]&1)==1){
				FOut.write(MainHandle.getPointDatabase().AllPointX[i]+" "
						+MainHandle.getPointDatabase().AllPointY[i]+"\n");
			}
		}
		FOut.flush();
		FOut.close();
		JOptionPane.showMessageDialog(null,"OutputFinished!");
		}catch(Exception ex){MainHandle.SolveException(ex);}
	}
	private void LinesDataRelease(){
		try{
		ThrowLinesDataField.setText(JOptionPane.showInputDialog("Please complete the Output File Name!!!")+MainHandle.GetInternationalTimeSignature()+".txt");
		if(ThrowLinesDataField.getText()==null) return;
		File f=new File(ThrowLinesDataField.getText());
		BufferedWriter FOut=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,false),"UTF-8"));
		for(ArrayList<Database.TimeStampPointStructure>i:MissingTrajectoryList){
		for(Database.TimeStampPointStructure j:i){
			FOut.write(j.t+" "+j.y+" "+j.x+" -1\n");
		}
		FOut.write("-1\n");
		}
		FOut.flush();
		FOut.close();
		MissingTrajectoryList.clear();
		JOptionPane.showMessageDialog(null,"OutputFinished!");
		}catch(Exception ex){MainHandle.SolveException(ex);}
	}
	private int RetriveCounter=0;
	private boolean Reversed=true;
	private void DataRetrive(){
		BufferedReader FIN;
		try{
			RetriveCounter++;
			//Reversed=!Reversed;
			File f=new File(RetriveDataField.getText());
			FIN=new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
			String buf;
			double[] xlist=new double[10000];
			double[] ylist=new double[10000];
			int xycount=0;
			int st,splitpos,en;
			
			while((buf=FIN.readLine())!=null){
			if((buf.equals("-1"))||(((splitpos=buf.indexOf(' ',buf.indexOf(' ')+1))!=-1)&&(buf.indexOf(' ',splitpos+1)!=-1))){
				if(buf.equals("-1")){
					if(ylist[0]>xlist[0]) MainHandle.getLineDatabase().add(ylist,xlist,xycount,"[Title:][Info:RoadUpdate][Info:"+RetriveCounter+"times]");
					else MainHandle.getLineDatabase().add(xlist,ylist,xycount,"[Title:][Info:RoadUpdate][Info:"+RetriveCounter+"times]");
					MainHandle.getLineDatabase().LineVisible[MainHandle.getLineDatabase().LineNum]=2061;
					xycount=0;
					if(MainHandle.getLineDatabase().LineNum+10>100000){
						FIN.close();
						MainHandle.ScreenFlush();
						JOptionPane.showMessageDialog(null,"WARNING::LineDB OverFlowed!!!");
						return;
					}
				}else{
						st=buf.indexOf(' ');
						splitpos=buf.indexOf(' ',st+1);
						en=buf.indexOf(' ',splitpos+1);
						xlist[xycount]=Double.parseDouble(buf.substring(st+1,splitpos));
						ylist[xycount]=Double.parseDouble(buf.substring(splitpos+1,en));
						xycount++;
				}
			}else{
				if(buf.equals("#")){
					if(ylist[0]>xlist[0]) MainHandle.getLineDatabase().add(ylist,xlist,xycount,"[Title:][Info:RoadUpdate][Info:"+RetriveCounter+"times]");
					else MainHandle.getLineDatabase().add(xlist,ylist,xycount,"[Title:][Info:RoadUpdate][Info:"+RetriveCounter+"times]");
					MainHandle.getLineDatabase().LineVisible[MainHandle.getLineDatabase().LineNum]=2061;
					xycount=0;
					/*
					if(MainHandle.getLineDatabase().LineNum+10>100000){
						FIN.close();
						MainHandle.ScreenFlush();
						JOptionPane.showMessageDialog(null,"WARNING::LineDB OverFlowed!!!");
						return;
					}
					*/
				}else{
					splitpos=buf.indexOf(' ');
					xlist[xycount]=Double.parseDouble(buf.substring(0,splitpos));
					ylist[xycount]=Double.parseDouble(buf.substring(splitpos+1));
					xycount++;
				}
			}
			}
			FIN.close();
			MainHandle.ScreenFlush();
			JOptionPane.showMessageDialog(null,"InputFinished!");
		}catch(Exception ex){MainHandle.SolveException(ex);}
	}
	private ArrayList<ArrayList<Database.TimeStampPointStructure>> MissingTrajectoryList=null;
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==ScreenLockButton){
			MainHandle.ScreenLock(true);
			ScreenLockButton.setEnabled(false);
			ScreenUnLockButton.setEnabled(true);
		}else if(e.getSource()==ScreenUnLockButton){
			MainHandle.ScreenLock(false);
			ScreenLockButton.setEnabled(true);
			ScreenUnLockButton.setEnabled(false);
		}else if(e.getSource()==TrajectoryToPointsTransformer){
			try{
				int TrajectoryCount=0;
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(this);
				if(state==JFileChooser.APPROVE_OPTION){
					Algorithm.ClassicalMapMatchingAlgorithmClass MapMatching=new Algorithm.ClassicalMapMatchingAlgorithmClass();
					MapMatching.setHandle(MainHandle);
					MapKernel.FileAccept File_Accept = new FileAccept();
					File_Accept.setExtendName("txt");
					File[] File_list = FileDialog.getCurrentDirectory().listFiles(File_Accept);
					int pos1,pos2,pos3;
					double Latitude,Longitude;
					ArrayList<Database.PointStructure> arr=new ArrayList<Database.PointStructure>();
					for(File fin:File_list){
					//Single File------------------------------------
					System.out.println("Start Processcing File::"+fin.getName());
					BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(fin),"UTF-8"));
					String buf="";
						while((buf=in.readLine())!=null){
							if(buf.equals("-1")){
								MapMatching.setInput(arr);
								arr.clear();
								TrajectoryCount++;
								if(TrajectoryCount%1000==0)
								{
									java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									System.out.println("Finishing Entire "+TrajectoryCount+" Trajectories at "+df.format(new Date()));
								}
								continue;
							}
							pos1=buf.indexOf(' ');
							pos2=buf.indexOf(' ',pos1+1);
							pos3=buf.indexOf(' ',pos2+1);
							Latitude=Double.parseDouble(buf.substring(pos1+1,pos2));
							Longitude=Double.parseDouble(buf.substring(pos2+1,pos3));
							arr.add(new Database.PointStructure(Longitude,Latitude));
						}
					in.close();
					//------------------------------------------------
					}
					MapMatching=null;
					MainHandle.ScreenFlush();
					JOptionPane.showMessageDialog(null,"MapMatchingFinished!");
				}
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(e.getSource()==TrajectoryToLinesTransformer){
			/**
			if(SelectRoadID.isSelected())
			try{
				Database.PolygonDataSet PolygonDB=MainHandle.getPolygonDatabase();
				if(PolygonDB.PolygonHint[PolygonDB.PolygonNum-1].indexOf("[Info:ExperimentRegion]")==-1){
					JOptionPane.showMessageDialog(null,"Please Select The Experiment Region!!!");
					return;
				}
				PolygonDB.GenerateMBR();
				double x1=PolygonDB.GetMBRX1(PolygonDB.PolygonNum-1);
				double y1=PolygonDB.GetMBRY1(PolygonDB.PolygonNum-1);
				double x2=PolygonDB.GetMBRX2(PolygonDB.PolygonNum-1);
				double y2=PolygonDB.GetMBRY2(PolygonDB.PolygonNum-1);
				int TrajectoryCount=0;
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(this);
				if(state==JFileChooser.APPROVE_OPTION){
					MapKernel.FileAccept File_Accept = new FileAccept();
					File_Accept.setExtendName("txt");
					File[] File_list = FileDialog.getCurrentDirectory().listFiles(File_Accept);
					Algorithm.PointListRectangleCapture TrajectoryCapture=new Algorithm.PointListRectangleCapture();
					TrajectoryCapture.setHandle(MainHandle);
					TrajectoryCapture.SetParameter(x1, y1, x2, y2);
					System.gc();
					int pos1,pos2,pos3;
					double Latitude,Longitude;
					int SecondCounter;
					ArrayList<Database.TimeStampPointStructure> arr=new ArrayList<Database.TimeStampPointStructure>();
					for(File fin:File_list){
					//Single File------------------------------------
					System.out.println("Start Processcing File::"+fin.getName());
					FileReader _in=new FileReader(fin);
					BufferedReader in=new BufferedReader(_in);
					String buf="";
						while((buf=in.readLine())!=null){
							if(buf.equals("-1")){
								TrajectoryCapture.setInput(arr);
								arr.clear();
								TrajectoryCount++;
								if(TrajectoryCount%1000==0)
								{
									java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									System.out.println("Finishing Entire "+TrajectoryCount+" Trajectories at "+df.format(new Date()));
								}
								continue;
							}
							pos1=buf.indexOf(' ');
							pos2=buf.indexOf(' ',pos1+1);
							pos3=buf.indexOf(' ',pos2+1);
							SecondCounter=Integer.parseInt(buf.substring(0,pos1));
							Latitude=Double.parseDouble(buf.substring(pos1+1,pos2));
							Longitude=Double.parseDouble(buf.substring(pos2+1,pos3));
							arr.add(new Database.TimeStampPointStructure(Longitude,Latitude,SecondCounter));
						}
					_in.close();
					in.close();
					//------------------------------------------------
					}
					MissingTrajectoryList=(ArrayList<ArrayList<Database.TimeStampPointStructure>>)TrajectoryCapture.getOutput();
					MainHandle.ScreenFlush();
					LinesDataRelease();
					TrajectoryCapture.Fresh();
					System.gc();
					JOptionPane.showMessageDialog(null,"TrajectoryCaptureFinished!");
				}
				return;
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
			*/
			try{
				String JointPointNumString=JOptionPane.showInputDialog(null,"Input Joint Point Number");
				if((JointPointNumString==null)||(JointPointNumString.equals(""))) return;
				int JointPointNum=Integer.parseInt(JointPointNumString);
				int TrajectoryCount=0;
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(this);
				if(state==JFileChooser.APPROVE_OPTION){
					Algorithm.TrajectoryMapMatchingAlgorithmClass MapMatching=new Algorithm.TrajectoryMapMatchingAlgorithmClass();
					MapMatching.setHandle(MainHandle);
					MapMatching.setExtraInput(SelectedPolygonList);
					if(SelectRoadID.isSelected()) MapMatching.setExtraInput(-1);
					else MapMatching.setExtraInput(50);
					MapMatching.Fresh();
					System.gc();
					MapKernel.FileAccept File_Accept = new FileAccept();
					File_Accept.setExtendName("txt");
					File[] File_list = FileDialog.getCurrentDirectory().listFiles(File_Accept);
					int pos1,pos2,pos3;
					double Latitude,Longitude;
					int SecondCounter;
					ArrayList<Database.TimeStampPointStructure> arr=new ArrayList<Database.TimeStampPointStructure>();
					for(File fin:File_list){
					//Single File------------------------------------
					System.out.println("Start Processcing File::"+fin.getName());
					BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(fin),"UTF-8"));
					String buf="";
						while((buf=in.readLine())!=null){
							if(buf.equals("-1")){
								MapMatching.setInput(arr,JointPointNum);
								arr.clear();
								TrajectoryCount++;
								if(TrajectoryCount%1000==0)
								{
									java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									System.out.println("Finishing Entire "+TrajectoryCount+" Trajectories at "+df.format(new Date()));
								}
								continue;
							}
							pos1=buf.indexOf(' ');
							pos2=buf.indexOf(' ',pos1+1);
							pos3=buf.indexOf(' ',pos2+1);
							SecondCounter=Integer.parseInt(buf.substring(0,pos1));
							Latitude=Double.parseDouble(buf.substring(pos1+1,pos2));
							Longitude=Double.parseDouble(buf.substring(pos2+1,pos3));
							arr.add(new Database.TimeStampPointStructure(Longitude,Latitude,SecondCounter));
						}
					in.close();
					//------------------------------------------------
					}
					MissingTrajectoryList=(ArrayList<ArrayList<Database.TimeStampPointStructure>>)MapMatching.getOutput();
					LinesDataRelease();
					MapMatching.Fresh();
					MainHandle.ScreenFlush();
					System.gc();
					JOptionPane.showMessageDialog(null,"MapMatchingFinished!");
				}
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(e.getSource()==ThrowPointsData){
		try{
				PointsDataRelease();
		}catch(Exception ex){MainHandle.SolveException(ex);}
		}else if(e.getSource()==ThrowLinesData){
		try{
				LinesDataRelease();
		}catch(Exception ex){MainHandle.SolveException(ex);}
		}else if(e.getSource()==RetriveData){
		try{
				DataRetrive();
		}catch(Exception ex){MainHandle.SolveException(ex);}
		}else if(e.getSource()==PointsAlgorithmExe1){
			try{
				Process proc = Runtime.getRuntime().exec(ExePointsField1.getText());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
				errorGobbler.start();
				outputGobbler.start();
				//proc.waitFor();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
			/*
			//Core Algorithm---------------------------
			//Loading The Point------------------------
			int UpperLimit=MainHandle.getPointDatabase().PointNum;
			ArrayList<Database.PointStructure> ProcessingPointSet=new ArrayList<Database.PointStructure>();
			for(int i=0;i<UpperLimit;i++){
				if(((MainHandle.getPointDatabase().PointVisible[i])&1)==1){
					ProcessingPointSet.add(new Database.PointStructure(MainHandle.getPointDatabase().AllPointX[i],
							MainHandle.getPointDatabase().AllPointY[i]));
				}
			}
			Database.PointDataRecordSet PointRecord=new Database.PointDataRecordSet();
			PointRecord.DatabaseInit(ProcessingPointSet);
			//-----------------------------------------
			*/
		}else if(e.getSource()==PointsAlgorithmExe2){
			try{
				Process proc = Runtime.getRuntime().exec(ExePointsField2.getText());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
				errorGobbler.start();
				outputGobbler.start();
				//proc.waitFor();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(e.getSource()==PointsAlgorithmExe3){
			try{
				Process proc = Runtime.getRuntime().exec(ExePointsField3.getText());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
				errorGobbler.start();
				outputGobbler.start();
				//proc.waitFor();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(e.getSource()==LinesAlgorithmExe1){
			try{
				Process proc = Runtime.getRuntime().exec(ExeLinesField1.getText());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
				errorGobbler.start();
				outputGobbler.start();
				//proc.waitFor();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(e.getSource()==LinesAlgorithmExe2){
			try{
				Process proc = Runtime.getRuntime().exec(ExeLinesField2.getText());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
				errorGobbler.start();
				outputGobbler.start();
				//proc.waitFor();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}else if(e.getSource()==LinesAlgorithmExe3){
			try{
				Process proc = Runtime.getRuntime().exec(ExeLinesField3.getText());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
				errorGobbler.start();
				outputGobbler.start();
				//proc.waitFor();
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
		}
		else if(e.getSource()==LoadClear){
			if(RetriveCounter==0) return;
			Database.LineDataSet DB=MainHandle.getLineDatabase();
			int UpperLimit=DB.LineNum;
			for(int i=0;i<UpperLimit;i++){
				if(DB.LineHint[i].indexOf("[Info:RoadUpdate]")!=-1)
				if(DB.LineHint[i].indexOf("[Info:"+RetriveCounter+"times]")!=-1)
					DB.DatabaseRemove(i);
			}
			DB.DatabaseResize();
			RetriveCounter--;
			MainHandle.ScreenFlush();
		}else if(e.getSource()==AllLoadClear){
			if(RetriveCounter==0) return;
			Database.LineDataSet DB=MainHandle.getLineDatabase();
			int UpperLimit=DB.LineNum;
			for(int i=0;i<UpperLimit;i++){
				if(DB.LineHint[i].indexOf("[Info:RoadUpdate]")!=-1) DB.DatabaseRemove(i);
			}
			DB.DatabaseResize();
			RetriveCounter=0;
			MainHandle.ScreenFlush();
		}
	}
	//Specific Part--------------------------------------------
	public void SpecificProcess(){
		RetriveDataField=new JTextField(16);
		add(RetriveDataField);
		RetriveData=new JButton("Retrive");
		add(RetriveData);
		RetriveData.addActionListener(this);
		LoadClear=new JButton("ClearLastResults");
		add(LoadClear);
		LoadClear.addActionListener(this);
		AllLoadClear=new JButton("ClearAllResults");
		add(AllLoadClear);
		AllLoadClear.addActionListener(this);
		//----------------------------------------------------------------------------------
		SelectRoadID=new JCheckBox("Release Road ID & Trajectories");
		SelectRoadID.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				MainHandle.getPolygonDatabase().AttributeDelete("ExperimentRegion",null,null,null,null);
				MainHandle.ScreenFlush();
			}
		});
		add(SelectRoadID);
		SelectRoadID.setSelected(false);
		SelectRoadID.setOpaque(false);
		SelectRoadID.setFont(new Font("华文新魏",Font.BOLD,16));
		SelectRoadID.setForeground(Color.orange);
		ConveyRegionToAlgorithm=new JCheckBox("Convey Region to Algorithm");
		add(ConveyRegionToAlgorithm);
		ConveyRegionToAlgorithm.setSelected(false);
		ConveyRegionToAlgorithm.setOpaque(false);
		ConveyRegionToAlgorithm.setFont(new Font("华文新魏",Font.BOLD,16));
		ConveyRegionToAlgorithm.setForeground(Color.orange);
		//-------------------------------------------------------------------------------
		TrajectoryToPointsTransformer=new JButton("Trajectory to Points Tranformer");
		add(TrajectoryToPointsTransformer);
		TrajectoryToPointsTransformer.addActionListener(this);
		
		ThrowPointsData=new JButton("Release");
		add(ThrowPointsData);
		ThrowPointsData.addActionListener(this);
		ThrowPointsDataField=new JTextField(16);
		add(ThrowPointsDataField);
		
		PointsAlgorithmExe1=new JButton("Execute1");
		add(PointsAlgorithmExe1);
		PointsAlgorithmExe1.addActionListener(this);
		ExePointsField1=new JTextField(16);
		add(ExePointsField1);
		
		PointsAlgorithmExe2=new JButton("Execute2");
		add(PointsAlgorithmExe2);
		PointsAlgorithmExe2.addActionListener(this);
		ExePointsField2=new JTextField(16);
		add(ExePointsField2);
		
		PointsAlgorithmExe3=new JButton("Execute3");
		add(PointsAlgorithmExe3);
		PointsAlgorithmExe3.addActionListener(this);
		ExePointsField3=new JTextField(16);
		add(ExePointsField3);
		//--------------------------------------------------------------------------------
		TrajectoryToLinesTransformer=new JButton("Trajectory To Lines Transformer");
		add(TrajectoryToLinesTransformer);
		TrajectoryToLinesTransformer.addActionListener(this);
		
		ThrowLinesData=new JButton("Release");
		//add(ThrowLinesData);
		ThrowLinesData.addActionListener(this);
		ThrowLinesDataField=new JTextField(16);
		//add(ThrowLinesDataField);
		
		LinesAlgorithmExe1=new JButton("Execute1");
		add(LinesAlgorithmExe1);
		LinesAlgorithmExe1.addActionListener(this);
		ExeLinesField1=new JTextField(16);
		add(ExeLinesField1);
		
		LinesAlgorithmExe2=new JButton("Execute2");
		add(LinesAlgorithmExe2);
		LinesAlgorithmExe2.addActionListener(this);
		ExeLinesField2=new JTextField(16);
		add(ExeLinesField2);
		
		LinesAlgorithmExe3=new JButton("Execute3");
		add(LinesAlgorithmExe3);
		LinesAlgorithmExe3.addActionListener(this);
		ExeLinesField3=new JTextField(16);
		add(ExeLinesField3);
	}
	public void itemStateChanged(ItemEvent e) {
	}
	public void emerge(){
		//-----------------
		//-----------------
	}
	public void convey(double x,double y){
		//JOptionPane.showMessageDialog(null,"ConveyPoint");
	}
	public void ReleaseRoadID(double x1,double y1,double x2,double y2){
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		ArrayList<Integer> IndexList=new ArrayList<Integer>();
		int p=-1;
		double Xmin=Math.min(x1,x2);
		double Xmax=Math.max(x1,x2);
		double Ymin=Math.min(y1,y2);
		double Ymax=Math.max(y1,y2);
		for(int i=0;i<LineDB.LineNum;i++){
			if(LineDB.LineHint[i].indexOf("[Info:Road]")==-1) continue;
			p=LineDB.LineHead[i];
			while(p!=-1){
				if(LineDB.AllPointX[p]<Xmin) {p=LineDB.AllPointNext[p];continue;}
				if(LineDB.AllPointX[p]>Xmax) {p=LineDB.AllPointNext[p];continue;}
				if(LineDB.AllPointY[p]<Ymin) {p=LineDB.AllPointNext[p];continue;}
				if(LineDB.AllPointY[p]>Ymax) {p=LineDB.AllPointNext[p];continue;}
				break;
			}
			if(p!=-1) IndexList.add(i);
		}
		try{
			String FileName=JOptionPane.showInputDialog("Please complete the Delete Road Index File Name!!!");
			if(FileName==null) return;
			File f=new File(FileName+MainHandle.GetInternationalTimeSignature()+".txt");
			BufferedWriter FOut=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,false),"UTF-8"));
			for(Integer index:IndexList){
				FOut.write(index+"\n");
			}
			FOut.flush();
			FOut.close();
			JOptionPane.showMessageDialog(null,"RecordDeleteRoadFinished!");
		}catch(Exception ex){MainHandle.SolveException(ex);}
	}
	public void ReleaseRoadID(ArrayList<Integer> PolygonList){
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		Database.PolygonDataSet PolygonDB=MainHandle.getPolygonDatabase();
		ArrayList<Integer> IndexList=new ArrayList<Integer>();
		PolygonDB.GenerateMBR();
		int p=-1;
		for(int i=0;i<LineDB.LineNum;i++){
			if(LineDB.LineHint[i].indexOf("[Info:Road]")==-1) continue;
			p=LineDB.LineHead[i];
			boolean Hit=false;
			while(p!=-1){
				for(int PolyID:PolygonList){
				if(!PolygonDB.CheckInsideMBR(PolyID,LineDB.AllPointX[p],LineDB.AllPointY[p])) continue;
				if(!PolygonDB.CheckInsidePolygon(PolyID,LineDB.AllPointX[p],LineDB.AllPointY[p])) continue;
				Hit=true;
				break;
				}
				if(Hit){
					IndexList.add(i);
					break;
				}
				p=LineDB.AllPointNext[p];
			}
		}
		try{
			String FileName=JOptionPane.showInputDialog("Please complete the Delete Road Index File Name!!!");
			if(FileName==null) return;
			File f=new File(FileName+MainHandle.GetInternationalTimeSignature()+".txt");
			BufferedWriter FOut=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,false),"UTF-8"));
			for(Integer index:IndexList){
				FOut.write(index+"\n");
			}
			FOut.flush();
			FOut.close();
			JOptionPane.showMessageDialog(null,"RecordDeleteRoadFinished!");
		}catch(Exception ex){MainHandle.SolveException(ex);}
	}
	ArrayList<Integer> SelectedPolygonList=new ArrayList<Integer>();
	public void convey(double x1,double y1,double x2,double y2){
		try{
		if(ConveyRegionToAlgorithm.isSelected())
		{	
			int n=JOptionPane.showConfirmDialog(null,"Do you want to Convey Region to Algorithm?","Hint", JOptionPane.YES_NO_OPTION);
			if(n==JOptionPane.NO_OPTION) return;
			double Xmin=Math.min(x1, x2);
			double Xmax=Math.max(x1, x2);
			double Ymin=Math.min(y1, y2);
			double Ymax=Math.max(y1, y2);
			MainHandle.GetClientPane().SocketCommandLineField.setText("RegionComputation::"+Xmin+" "+Xmax+" "+Ymin+" "+Ymax);
			MainHandle.GetClientPane().ClientHandle.SendMsg
			(MainHandle.GetClientPane().SocketIPField.getText(),
			Integer.parseInt(MainHandle.GetClientPane().SocketNumField.getText()),
			MainHandle.GetClientPane().SocketCommandLineField.getText());
			return;
		}
		if(SelectRoadID.isSelected()){
			/**
			if(MainHandle.getPolygonDatabase().PolygonHint[MainHandle.getPolygonDatabase().PolygonNum-1].indexOf("[Info:ExperimentRegion]")!=-1){
				MainHandle.getPolygonDatabase().DatabaseDelete(MainHandle.getPolygonDatabase().PolygonNum-1);
			}
			MainHandle.getPolygonDatabase().Clear(MainHandle.getPolygonDatabase().PolygonNum);
			MainHandle.getPolygonDatabase().append(MainHandle.getPolygonDatabase().PolygonNum,Math.min(x1,x2),Math.min(y1,y2));
			MainHandle.getPolygonDatabase().append(MainHandle.getPolygonDatabase().PolygonNum,Math.max(x1,x2),Math.min(y1,y2));
			MainHandle.getPolygonDatabase().append(MainHandle.getPolygonDatabase().PolygonNum,Math.max(x1,x2),Math.max(y1,y2));
			MainHandle.getPolygonDatabase().append(MainHandle.getPolygonDatabase().PolygonNum,Math.min(x1,x2),Math.max(y1,y2));
			MainHandle.getPolygonDatabase().append(MainHandle.getPolygonDatabase().PolygonNum,Math.min(x1,x2),Math.min(y1,y2));
			MainHandle.getPolygonDatabase().PolygonHint[MainHandle.getPolygonDatabase().PolygonNum]="[Info:ExperimentRegion][Title:]";
			MainHandle.getPolygonDatabase().PolygonVisible[MainHandle.getPolygonDatabase().PolygonNum]=15;
			MainHandle.getPolygonDatabase().PolygonNum++;
			MainHandle.ScreenFlush();
			ReleaseRoadID(x1,y1,x2,y2); 
			*/
			Database.RTreeIndex RTree=new Database.RTreeIndex();
			RTree.IndexInit(MainHandle.getPolygonDatabase());
			SelectedPolygonList.clear();
			SelectedPolygonList=RTree.Search(x1, y1, x2, y2);
			ReleaseRoadID(SelectedPolygonList);
			/**
			try{
				int TrajectoryCount=0;
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(this);
				if(state==JFileChooser.APPROVE_OPTION){
					MapKernel.FileAccept File_Accept = new FileAccept();
					File_Accept.setExtendName("txt");
					File[] File_list = FileDialog.getCurrentDirectory().listFiles(File_Accept);
					Algorithm.PointListRectangleCapture TrajectoryCapture=new Algorithm.PointListRectangleCapture();
					TrajectoryCapture.setHandle(MainHandle);
					TrajectoryCapture.SetParameter(x1, y1, x2, y2);
					System.gc();
					int pos1,pos2,pos3;
					double Latitude,Longitude;
					int SecondCounter;
					ArrayList<Database.TimeStampPointStructure> arr=new ArrayList<Database.TimeStampPointStructure>();
					for(File fin:File_list){
					//Single File------------------------------------
					System.out.println("Start Processcing File::"+fin.getName());
					FileReader _in=new FileReader(fin);
					BufferedReader in=new BufferedReader(_in);
					String buf="";
						while((buf=in.readLine())!=null){
							if(buf.equals("-1")){
								TrajectoryCapture.setInput(arr);
								arr.clear();
								TrajectoryCount++;
								if(TrajectoryCount%1000==0)
								{
									java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									System.out.println("Finishing Entire "+TrajectoryCount+" Trajectories at "+df.format(new Date()));
								}
								continue;
							}
							pos1=buf.indexOf(' ');
							pos2=buf.indexOf(' ',pos1+1);
							pos3=buf.indexOf(' ',pos2+1);
							SecondCounter=Integer.parseInt(buf.substring(0,pos1));
							Latitude=Double.parseDouble(buf.substring(pos1+1,pos2));
							Longitude=Double.parseDouble(buf.substring(pos2+1,pos3));
							arr.add(new Database.TimeStampPointStructure(Longitude,Latitude,SecondCounter));
						}
					_in.close();
					in.close();
					//------------------------------------------------
					}
					MissingTrajectoryList=(ArrayList<ArrayList<Database.TimeStampPointStructure>>)TrajectoryCapture.getOutput();
					MainHandle.ScreenFlush();
					LinesDataRelease();
					TrajectoryCapture.Fresh();
					System.gc();
					JOptionPane.showMessageDialog(null,"TrajectoryCaptureFinished!");
				}
			}catch(Exception ex){
				MainHandle.SolveException(ex);
			}
			*/
			return;
		}
		MainHandle.PointEmpty();
		MainHandle.PointPush(Math.min(x1,x2),Math.min(y1,y2));
		MainHandle.PointPush(Math.max(x1,x2),Math.min(y1,y2));
		MainHandle.PointPush(Math.max(x1,x2),Math.max(y1,y2));
		MainHandle.PointPush(Math.min(x1,x2),Math.max(y1,y2));
		MainHandle.PointPush(Math.min(x1,x2),Math.min(y1,y2));
		MainHandle.setPointConsecutiveLinkVisible(true);
		MainHandle.setPointVisible(true);
		MainHandle.setPointHeadTailLinkVisible(false);
		MainHandle.setPointHintVisible(false);
		MainHandle.ScreenFlush();
		int reply=JOptionPane.showConfirmDialog(null,"Yes To Choose Points;No To Choose Polygons");
		MainHandle.PointEmpty();
		MainHandle.setPointConsecutiveLinkVisible(false);
		MainHandle.setPointVisible(false);
		MainHandle.setPointHeadTailLinkVisible(false);
		MainHandle.setPointHintVisible(false);
		MainHandle.ScreenFlush();
		if(reply==JOptionPane.CANCEL_OPTION) return;
		int UpperLimit=MainHandle.getPointDatabase().PointNum;
		for(int i=0;i<UpperLimit;i++){
			MainHandle.getPointDatabase().PointVisible[i]|=1;
			RemoveTemporaryVisual(i);
		}
		MainHandle.ScreenFlush();
		Database.RTreeIndex RTree=new Database.RTreeIndex();
		RTree.IndexInit(MainHandle.getPolygonDatabase());
		SelectedPolygonList.clear();
		SelectedPolygonList=RTree.Search(x1, y1, x2, y2);
		if(reply==JOptionPane.NO_OPTION) return;
		//-----------------------------------------------------------
		ArrayList<Integer> res=SelectedPolygonList;
		SelectedPolygonList=new ArrayList<Integer>();
		System.gc();
		//-----------------------------------------------------------
		MainHandle.getPolygonDatabase().GenerateMBR();
		boolean hit=false;
		for(int pointid=0;pointid<UpperLimit;pointid++){
			if(MainHandle.getPointDatabase().AllPointX[pointid]<Math.min(x1,x2)) continue;
			if(MainHandle.getPointDatabase().AllPointX[pointid]>Math.max(x1,x2)) continue;
			if(MainHandle.getPointDatabase().AllPointY[pointid]<Math.min(y1,y2)) continue;
			if(MainHandle.getPointDatabase().AllPointY[pointid]>Math.max(y1,y2)) continue;
			hit=false;
			for(int polygonid:res){
				//if(MainHandle.getPolygonDatabase().PolygonHint[polygonid].indexOf("[Info:SpiderRegion]")==-1) continue;
				if(MainHandle.getPolygonDatabase().CheckInsideMBR(polygonid,MainHandle.getPointDatabase().AllPointX[pointid],MainHandle.getPointDatabase().AllPointY[pointid]))
				if(MainHandle.getPolygonDatabase().CheckInsidePolygon(polygonid,MainHandle.getPointDatabase().AllPointX[pointid],MainHandle.getPointDatabase().AllPointY[pointid])){
					hit=true;
					break;
				}
			}
			if(hit){
				MainHandle.getPointDatabase().PointVisible[pointid]/=2;
				MainHandle.getPointDatabase().PointVisible[pointid]*=2;
			}
		}
		for(int i=0;i<UpperLimit;i++){
			MainHandle.getPointDatabase().PointVisible[i]^=1;
		}
		int count=0;
		for(int i=0;i<UpperLimit;i++){
			if((MainHandle.getPointDatabase().PointVisible[i]&1)==1){
				ChangePointTitle(i,count);
				count++;
			}
		}
		MainHandle.ScreenFlush();
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
		//-----------------------------------------------------------
			
		//-----------------------------------------------------------
		/*Code For Testing the DBIndex
		double[] xx=new double[4];
		double[] yy=new double[4];
		xx[0]=x1;
		yy[0]=y1;
		xx[1]=x1;
		yy[1]=y2;
		xx[2]=x2;
		yy[2]=y2;
		xx[3]=x2;
		yy[3]=y1;
		MainHandle.getPolygonDatabase().add(xx,yy,4,"[Title:][Info:TestRTree]");
		 Database.RTreeIndex RTree=new Database.RTreeIndex();
		 RTree.IndexInit(MainHandle.getLineDatabase());
		 ArrayList<Integer> res=RTree.Search(x1, y1, x2, y2);
		 while(!res.isEmpty()){
			 MainHandle.getLineDatabase().LineVisible[res.get(0)]=1421;
			 res.remove(0);
		 }
		 MainHandle.ScreenFlush();
		 */
		 //-------------------------------------------------------------
	}
	@Override
	public void confirm() {
		JOptionPane.showMessageDialog(null, "ConfirmFunction");
		// TODO Auto-generated method stub
	}
	@Override
	public String GetSocketResult(String SocketQuery) {
		// TODO Auto-generated method stub
		return null;
	}
}
class StreamGobbler extends Thread {
	InputStream is;
	String type;
	public StreamGobbler(InputStream is, String type){
		this.is = is;
		this.type = type;
	}
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (type.equals("Error")) {
					System.out.println("Error	:" + line);
				} else {
					System.out.println("Debug:" + line);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
   
