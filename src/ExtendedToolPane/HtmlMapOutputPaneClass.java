package ExtendedToolPane;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import MapKernel.FileAccept;
import MapKernel.MapControl;
import MapKernel.ToolPanel;

public  class HtmlMapOutputPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	public String getString(){
		return "HtmlMapOutputPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public HtmlMapOutputPaneClass(){
		JLabel Title=new JLabel("HtmlMapOutput");
		Title.setFont(new Font("华文新魏",Font.BOLD,30));
		Title.setForeground(Color.orange);
		add(Title);
		//add(ScreenLockButton);
		ScreenLockButton.addActionListener(this);
		//add(ScreenUnLockButton);
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
		}else if(e.getSource()==Point_AllTagOutput){
			if(Point_AllTagOutput.isSelected())
				Point_SpecificTagList.setEditable(false);
		}else if(e.getSource()==Point_SpecificTagOutput){
			if(Point_SpecificTagOutput.isSelected()){
				Point_SpecificTagList.setEditable(true);
			}
		}else if(e.getSource()==Point_AllVisibleOutput){
			if(Point_AllVisibleOutput.isSelected()){
				Point_SpecificTagList.setEditable(false);
			}
		}else if(e.getSource()==Point_NoneOutput){
			if(Point_NoneOutput.isSelected()){
				Point_SpecificTagList.setEditable(false);
			}
		}else if(e.getSource()==Line_AllTagOutput){
			if(Line_AllTagOutput.isSelected())
				Line_SpecificTagList.setEditable(false);
		}else if(e.getSource()==Line_SpecificTagOutput){
			if(Line_SpecificTagOutput.isSelected()){
				Line_SpecificTagList.setEditable(true);
			}
		}else if(e.getSource()==Line_AllVisibleOutput){
			if(Line_AllVisibleOutput.isSelected()){
				Line_SpecificTagList.setEditable(false);
			}
		}else if(e.getSource()==Line_NoneOutput){
			if(Line_NoneOutput.isSelected()){
				Line_SpecificTagList.setEditable(false);
			}
		}else if(e.getSource()==Polygon_AllTagOutput){
			if(Polygon_AllTagOutput.isSelected())
				Polygon_SpecificTagList.setEditable(false);
		}else if(e.getSource()==Polygon_SpecificTagOutput){
			if(Polygon_SpecificTagOutput.isSelected()){
				Polygon_SpecificTagList.setEditable(true);
			}
		}else if(e.getSource()==Polygon_AllVisibleOutput){
			if(Polygon_AllVisibleOutput.isSelected()){
				Polygon_SpecificTagList.setEditable(false);
			}
		}else if(e.getSource()==Polygon_NoneOutput){
			if(Polygon_NoneOutput.isSelected()){
				Polygon_SpecificTagList.setEditable(false);
			}
		}else if(e.getSource()==OpenSourceFile){
			JFileChooser OpenWizard=new JFileChooser();
			int state=OpenWizard.showOpenDialog(null);
			if(state==JFileChooser.APPROVE_OPTION){
				OpenSourceFilePath.setText(OpenWizard.getSelectedFile().getAbsolutePath());
			}
		}else if(e.getSource()==InstantView){
			Process proc = Runtime.getRuntime().exec("C:\\\\Program Files\\Internet Explorer\\iexplore "+GenerateHTML("MapTemp.html"));
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();
		}else if(e.getSource()==OutputToFile){
			if((OutputDirectoryPath.getText()==null)||(OutputDirectoryPath.getText().isEmpty()))
			{
				JOptionPane.showMessageDialog(null,"Please Set The Directory");
				return;
			}
			String FullFileName=OutputDirectoryPath.getText()+"\\";
			if((OutputFileName.getText()==null)||(OutputFileName.getText().isEmpty())){
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				FullFileName+="["+df.format(new Date())+"].html";
			}else FullFileName+=OutputFileName.getText();
			GenerateHTML(FullFileName);
		}
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
	}
	//Specific Part--------------------------------------------
	double GetLatLngDelta(String str){
		try{
			if(str==null) return 0;
			if(str.isEmpty()) return 0;
			return Double.parseDouble(str);
		}catch(Exception ex){
			return 0;
		}
	}
	String GenerateHTML(String FileName){
		File OutFile=new File(FileName);
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(OpenSourceFilePath.getText()),"UTF-8"));
			BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutFile,false),"UTF-8"));
			String buf;
			boolean Point_check=false;
			boolean Line_check=false;
			boolean Polygon_check=false;
			String[] Point_tag_list=Point_SpecificTagList.getText().split(",");
			String[] Line_tag_list=Line_SpecificTagList.getText().split(",");
			String[] Polygon_tag_list=Polygon_SpecificTagList.getText().split(",");
			while((buf=in.readLine())!=null){
				out.write(buf);
				out.newLine();
				if(buf.equals("//Insert Point")){
					if(Point_NoneOutput.isSelected()) continue;
					//----------------------------------------------
					Database.PointDataSet PointDB=MainHandle.getPointDatabase();
					for(int i=0;i<PointDB.PointNum;i++){
						Point_check=false;
						if(Point_AllTagOutput.isSelected()) Point_check=true;
						//-------------------------------------------
						if(Point_SpecificTagOutput.isSelected()){
							for(String tag: Point_tag_list){
								if(PointDB.PointHint[i].indexOf(tag)!=-1){
									Point_check=true;
									break;
								}
							}
						}
						//--------------------------------------------
						if(Point_AllVisibleOutput.isSelected()){
							if((PointDB.PointVisible[i]&1)==1) Point_check=true;
						}
						if(Point_check){
							out.write("L.circle(["+(PointDB.AllPointY[i]+GetLatLngDelta(Point_LatitudeDelta.getText()))+","+(PointDB.AllPointX[i]+GetLatLngDelta(Point_LongitudeDelta.getText()))+"],50,{color:'green',fillColor: 'green',fillOpacity: 0.5}).addTo(map);");
							out.newLine();
						}
					}
				}else if(buf.equals("//Insert Polyline")){
					if(Line_NoneOutput.isSelected()) continue;
					//----------------------------------------------
					Database.LineDataSet LineDB=MainHandle.getLineDatabase();
					for(int i=0;i<LineDB.LineNum;i++){
						Line_check=false;
						if(Line_AllTagOutput.isSelected()) Line_check=true;
						//-------------------------------------------
						if(Line_SpecificTagOutput.isSelected()){
							for(String tag: Line_tag_list){
								if(LineDB.LineHint[i].indexOf(tag)!=-1){
									Line_check=true;
									break;
								}
							}
						}
						//--------------------------------------------
						if(Line_AllVisibleOutput.isSelected()){
							if((LineDB.LineVisible[i]&1)==1) Line_check=true;
						}
						if(Line_check){
							out.write("L.polyline([");
							out.newLine();
							int ptr=LineDB.LineHead[i];
							while(ptr!=-1){
								out.write("["+(LineDB.AllPointY[ptr]+GetLatLngDelta(Line_LatitudeDelta.getText()))+","+(LineDB.AllPointX[ptr]+GetLatLngDelta(Line_LongitudeDelta.getText()))+"]");
								ptr=LineDB.AllPointNext[ptr];
								if(ptr!=-1) out.write(",\n"); else out.newLine();
							}
							out.write("], {color: 'orange'}).addTo(map);");
							out.newLine();
						}
					}
				}else if(buf.equals("//Insert Polygon")){
					if(Polygon_NoneOutput.isSelected()) continue;
					//----------------------------------------------
					Database.PolygonDataSet PolygonDB=MainHandle.getPolygonDatabase();
					for(int i=0;i<PolygonDB.PolygonNum;i++){
						Polygon_check=false;
						if(Polygon_AllTagOutput.isSelected()) Polygon_check=true;
						//-------------------------------------------
						if(Polygon_SpecificTagOutput.isSelected()){
							for(String tag: Polygon_tag_list){
								if(PolygonDB.PolygonHint[i].indexOf(tag)!=-1){
									Polygon_check=true;
									break;
								}
							}
						}
						//--------------------------------------------
						if(Polygon_AllVisibleOutput.isSelected()){
							if((PolygonDB.PolygonVisible[i]&1)==1) Polygon_check=true;
						}
						if(Polygon_check){
							out.write("L.polygon([");
							out.newLine();
							int ptr=PolygonDB.PolygonHead[i];
							while(ptr!=-1){
								out.write("["+(PolygonDB.AllPointY[ptr]+GetLatLngDelta(Polygon_LatitudeDelta.getText()))+","+(PolygonDB.AllPointX[ptr]+GetLatLngDelta(Polygon_LongitudeDelta.getText()))+"]");
								ptr=PolygonDB.AllPointNext[ptr];
								if(ptr!=-1) out.write(",\n"); else out.newLine();
							}
							out.write("], {color: 'red'}).addTo(map);");
							out.newLine();
						}
					}
				}
			}
			in.close();
			out.close();
			return OutFile.getAbsolutePath();
		}catch(Exception ex){
			System.out.println(ex.toString()+"\n\n");
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Setting Error");
			return OutFile.getAbsolutePath();
		}
	}
	JButton 		OpenSourceFile=new JButton("打开源文件");
	JTextField 		OpenSourceFilePath=new JTextField(12);
	JRadioButton 	Point_AllTagOutput=new JRadioButton("[PointDB]::全部导出");
	JRadioButton 	Point_SpecificTagOutput=new JRadioButton("按照特定标签导出");
	JTextField		Point_SpecificTagList=new JTextField(16);
	JRadioButton 	Point_AllVisibleOutput=new JRadioButton("全部可视元素导出");
	JRadioButton 	Point_NoneOutput=new JRadioButton("不导出");
	JRadioButton 	Line_AllTagOutput=new JRadioButton("[LineDB]::全部导出");
	JRadioButton 	Line_SpecificTagOutput=new JRadioButton("按照特定标签导出");
	JTextField 		Line_SpecificTagList=new JTextField(16);
	JRadioButton 	Line_AllVisibleOutput=new JRadioButton("全部可视元素导出");
	JRadioButton 	Line_NoneOutput=new JRadioButton("不导出");
	JRadioButton 	Polygon_AllTagOutput=new JRadioButton("[PolygonDB]::全部导出");
	JRadioButton 	Polygon_SpecificTagOutput=new JRadioButton("按照特定标签导出");
	JTextField		Polygon_SpecificTagList=new JTextField(16);
	JRadioButton 	Polygon_AllVisibleOutput=new JRadioButton("全部可视元素导出");
	JRadioButton 	Polygon_NoneOutput=new JRadioButton("不导出");
	JButton 		InstantView=new JButton("立即打开HTML");
	JButton			OutputToFile=new JButton("导出HTML文件");
	JTextField		OutputDirectoryPath=new JTextField(15);
	JTextField		OutputFileName=new JTextField(15);
	JTextField		Point_LongitudeDelta=new JTextField(8);
	JTextField		Point_LatitudeDelta=new JTextField(8);
	JTextField		Line_LongitudeDelta=new JTextField(8);
	JTextField		Line_LatitudeDelta=new JTextField(8);
	JTextField		Polygon_LongitudeDelta=new JTextField(8);
	JTextField		Polygon_LatitudeDelta=new JTextField(8);
	public void SpecificProcess(){
		add(OpenSourceFile);
		OpenSourceFile.addActionListener(this);
		add(OpenSourceFilePath);
		OpenSourceFilePath.setText("Template.html");
		//----------------------------------------------------------------
		JLabel Point_Tag_Label=new JLabel("PointTagList");
		Point_Tag_Label.setForeground(Color.orange);
		add(Point_Tag_Label);
		add(Point_SpecificTagList);
		JLabel Point_Latitude_Delta_Label=new JLabel("Lat+");
		Point_Latitude_Delta_Label.setForeground(Color.orange);
		add(Point_Latitude_Delta_Label);
		add(Point_LatitudeDelta);
		JLabel Point_Longitude_Delta_Label=new JLabel("Lng+");
		Point_Longitude_Delta_Label.setForeground(Color.orange);
		add(Point_Longitude_Delta_Label);
		add(Point_LongitudeDelta);
		add(Point_AllTagOutput);
		Point_AllTagOutput.setOpaque(false);
		Point_AllTagOutput.setForeground(Color.orange);
		add(Point_NoneOutput);
		Point_NoneOutput.setOpaque(false);
		Point_NoneOutput.setForeground(Color.orange);
		add(Point_SpecificTagOutput);
		Point_SpecificTagOutput.setOpaque(false);
		Point_SpecificTagOutput.setForeground(Color.orange);
		add(Point_AllVisibleOutput);
		Point_AllVisibleOutput.setOpaque(false);
		Point_AllVisibleOutput.setForeground(Color.orange);
		ButtonGroup Group_Point=new ButtonGroup();
		Point_SpecificTagList.setEditable(false);
		Group_Point.add(Point_AllTagOutput);
		Group_Point.add(Point_SpecificTagOutput);
		Group_Point.add(Point_AllVisibleOutput);
		Group_Point.add(Point_NoneOutput);
		Point_NoneOutput.setSelected(true);
		Point_AllTagOutput.addActionListener(this);
		Point_SpecificTagOutput.addActionListener(this);
		Point_AllVisibleOutput.addActionListener(this);
		Point_NoneOutput.addActionListener(this);
		//----------------------------------------------------------------
		JLabel Line_Tag_Label=new JLabel("LineTagList");
		Line_Tag_Label.setForeground(Color.orange);
		add(Line_Tag_Label);
		add(Line_SpecificTagList);
		JLabel Line_Latitude_Delta_Label=new JLabel("Lat+");
		Line_Latitude_Delta_Label.setForeground(Color.orange);
		add(Line_Latitude_Delta_Label);
		add(Line_LatitudeDelta);
		JLabel Line_Longitude_Delta_Label=new JLabel("Lng+");
		Line_Longitude_Delta_Label.setForeground(Color.orange);
		add(Line_Longitude_Delta_Label);
		add(Line_LongitudeDelta);
		add(Line_AllTagOutput);
		Line_AllTagOutput.setOpaque(false);
		Line_AllTagOutput.setForeground(Color.orange);
		add(Line_NoneOutput);
		Line_NoneOutput.setOpaque(false);
		Line_NoneOutput.setForeground(Color.orange);
		add(Line_SpecificTagOutput);
		Line_SpecificTagOutput.setOpaque(false);
		Line_SpecificTagOutput.setForeground(Color.orange);
		add(Line_AllVisibleOutput);
		Line_AllVisibleOutput.setOpaque(false);
		Line_AllVisibleOutput.setForeground(Color.orange);
		ButtonGroup Group_Line=new ButtonGroup();
		Line_SpecificTagList.setEditable(false);
		Group_Line.add(Line_AllTagOutput);
		Group_Line.add(Line_SpecificTagOutput);
		Group_Line.add(Line_AllVisibleOutput);
		Group_Line.add(Line_NoneOutput);
		Line_NoneOutput.setSelected(true);
		Line_AllTagOutput.addActionListener(this);
		Line_SpecificTagOutput.addActionListener(this);
		Line_AllVisibleOutput.addActionListener(this);
		Line_NoneOutput.addActionListener(this);
		//-------------------------------------------------------------------
		JLabel Polygon_Tag_Label=new JLabel("PolygonTagList");
		Polygon_Tag_Label.setForeground(Color.orange);
		add(Polygon_Tag_Label);
		add(Polygon_SpecificTagList);
		JLabel Polygon_Latitude_Delta_Label=new JLabel("Lat+");
		Polygon_Latitude_Delta_Label.setForeground(Color.orange);
		add(Polygon_Latitude_Delta_Label);
		add(Polygon_LatitudeDelta);
		JLabel Polygon_Longitude_Delta_Label=new JLabel("Lng+");
		Polygon_Longitude_Delta_Label.setForeground(Color.orange);
		add(Polygon_Longitude_Delta_Label);
		add(Polygon_LongitudeDelta);
		add(Polygon_AllTagOutput);
		Polygon_AllTagOutput.setOpaque(false);
		Polygon_AllTagOutput.setForeground(Color.orange);
		add(Polygon_NoneOutput);
		Polygon_NoneOutput.setOpaque(false);
		Polygon_NoneOutput.setForeground(Color.orange);
		add(Polygon_SpecificTagOutput);
		Polygon_SpecificTagOutput.setOpaque(false);
		Polygon_SpecificTagOutput.setForeground(Color.orange);
		add(Polygon_AllVisibleOutput);
		Polygon_AllVisibleOutput.setOpaque(false);
		Polygon_AllVisibleOutput.setForeground(Color.orange);
		ButtonGroup Group_Polygon=new ButtonGroup();
		Polygon_SpecificTagList.setEditable(false);
		Group_Polygon.add(Polygon_AllTagOutput);
		Group_Polygon.add(Polygon_SpecificTagOutput);
		Group_Polygon.add(Polygon_AllVisibleOutput);
		Group_Polygon.add(Polygon_NoneOutput);
		Polygon_NoneOutput.setSelected(true);
		Polygon_AllTagOutput.addActionListener(this);
		Polygon_SpecificTagOutput.addActionListener(this);
		Polygon_AllVisibleOutput.addActionListener(this);
		Polygon_NoneOutput.addActionListener(this);
		//-------------------------------------------------------------------
		add(InstantView);
		add(OutputToFile);
		InstantView.addActionListener(this);
		OutputToFile.addActionListener(this);
		JLabel Directory_Label=new JLabel("Directory");
		Directory_Label.setForeground(Color.orange);
		add(Directory_Label);
		add(OutputDirectoryPath);
		JLabel FileName_Label=new JLabel("FileName");
		FileName_Label.setForeground(Color.orange);
		add(FileName_Label);
		add(FileName_Label);
		add(OutputFileName);
	}
	public void itemStateChanged(ItemEvent e) {
	}
	public void emerge(){
		//-----------------		
		//-----------------
	}
	public void convey(double x,double y){
		JOptionPane.showMessageDialog(null,"ConveyPoint");
	}
	public void convey(double x1,double y1,double x2,double y2){
		JOptionPane.showMessageDialog(null,"ConveyRectangle");
	}
	@Override
	public void confirm() {
		JOptionPane.showMessageDialog(null, "ConfirmFunction");
		// TODO Auto-generated method stub
	}
	private int SocketTransactionCounter=0;
	private double[] SocketX=new double[100000];
	private double[] SocketY=new double[100000];
	private int SocketXYCounter;
	public String GetSocketResult(String SocketQuery) {
		return null;
	}
}
