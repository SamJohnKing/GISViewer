package ExtendedToolPane;

import MapKernel.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
class SlotEntity{
	double x,y;
	double width,height;
	int layer;
}
class ASC_Command{
	boolean CarryContainer=false;
	int Dest_YBY=-1;
	int Dest_Col=-1;
	Calendar ST_time=null;
	Calendar EN_time=null;
}
public class YangshanPortASCPaneClass extends ToolPanel implements ExtendedToolPaneInterface {
	MapControl MainHandle;
	double CursrLongitude,CursorLatitude;
	JTextField LongitudeText,LatitudeText;
	JTextField SampleRateField,FlushRateField;
	JButton ASCPlayButton,ASCStopButton;
	JButton Yard_Layout_Button,Container_Button;
	JButton Sea_Move_Button,Land_Move_Button;
	JTextField Yard_Layout_Field,Container_Field;
	JTextField Sea_Move_Field,Land_Move_Field;
	JTextField Sea_ASC_Loc_Setting,Land_ASC_Loc_Setting;
	int Sea_ASC_Loc=0;
	int Land_ASC_Loc=150;
	Vector<ASC_Command> Sea_Task=new Vector<ASC_Command>();
	Vector<ASC_Command> Land_Task=new Vector<ASC_Command>();
	int Sea_Task_Ptr=0,Land_Task_Ptr=0;
	Calendar ASC_Time=null;
	int SlotRow,SlotCol;
	double SampleRate,FlushRate;
	boolean SlotPlayIndicator=false;
	SlotEntity[][] SlotMatrix=null;
	//---------------------------------------------------
	public void CheckLockButtons(){
		if (MapWizard.SingleItem.Screen.lock) {
			ScreenLockButton.setEnabled(false);
			ScreenUnLockButton.setEnabled(true);
		} else {
			ScreenLockButton.setEnabled(true);
			ScreenUnLockButton.setEnabled(false);
		}
	}
	public int Normalized_YBY(int k){
		if(k>SlotRow*2-1) return SlotRow*2;
		if(k<1) return 0;
		return k;
	}
	public float DeltaSecond(Calendar c1,Calendar c2){
		long delta=c1.getTimeInMillis()-c2.getTimeInMillis();
		return delta/1000f;
	}
	public void Container_Lift(int YBY,int Col){
		YBY=Normalized_YBY(YBY);
		if(YBY==0) return;
		if(YBY==SlotRow*2) return;
		if(YBY%2==0){
			SlotMatrix[YBY/2][Col-1].layer--;
			SlotMatrix[YBY/2-1][Col-1].layer--;
		}else{
			SlotMatrix[YBY/2][Col-1].layer--;
		}
	}
	public void Container_Down(int YBY,int Col){
		YBY=Normalized_YBY(YBY);
		if(YBY==0) return;
		if(YBY==SlotRow*2) return;
		if(YBY%2==0){
			SlotMatrix[YBY/2][Col-1].layer++;
			SlotMatrix[YBY/2-1][Col-1].layer++;
		}else{
			SlotMatrix[YBY/2][Col-1].layer++;
		}
	}
	public void ClockImpulse(){
		try{
		if(!SlotPlayIndicator) return;
		if(MainHandle.getMilliSecond()/50%((int)FlushRate/50)!=0) return;
		
		MainHandle.getPolygonDatabase().DatabaseDelete("[Info:YangshanPort]");
		MainHandle.getPointDatabase().DatabaseDelete("[Info:YangshanPort]");
		MainHandle.getLineDatabase().DatabaseDelete("[Info:YangshanPort]");
		SlotEntity Slot_ptr;
		String Info_String;
		
		Calendar FutureTime=Calendar.getInstance();
		FutureTime.setTime(ASC_Time.getTime());
		FutureTime.add(Calendar.SECOND, (int)SampleRate);
		int Next_Land_Task_Ptr=Land_Task_Ptr;
		int Next_Sea_Task_Ptr=Sea_Task_Ptr;
		int Next_Sea_ASC_Loc=Sea_ASC_Loc;
		int Next_Land_ASC_Loc=Land_ASC_Loc;
		
		
		while(Next_Sea_Task_Ptr<Sea_Task.size()){
			if(Sea_Task.elementAt(Next_Sea_Task_Ptr).EN_time.before(FutureTime)) Next_Sea_Task_Ptr++;
			else break;
		}
		if(Next_Sea_Task_Ptr-Sea_Task_Ptr>1) throw new Exception("Too Large SampleRate");
		
		while(Next_Land_Task_Ptr<Land_Task.size()){
			if(Land_Task.elementAt(Next_Land_Task_Ptr).EN_time.before(FutureTime)) Next_Land_Task_Ptr++;
			else break;
		}
		if(Next_Land_Task_Ptr-Land_Task_Ptr>1) throw new Exception("Too Large SampleRate");
		
		if(Next_Sea_Task_Ptr>=Sea_Task.size()){
			Next_Sea_ASC_Loc=Sea_Task.lastElement().Dest_YBY;
		}else if(Next_Sea_Task_Ptr==Sea_Task_Ptr){
			if(Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time.after(FutureTime)) Next_Sea_ASC_Loc=Sea_ASC_Loc;
			else if(Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time.after(ASC_Time)){
				Next_Sea_ASC_Loc=Math.round(Sea_ASC_Loc+(Normalized_YBY(Sea_Task.elementAt(Next_Sea_Task_Ptr).Dest_YBY)-Sea_ASC_Loc)/
						DeltaSecond(Sea_Task.elementAt(Next_Sea_Task_Ptr).EN_time,Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time)*
						DeltaSecond(FutureTime,Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time));
			}else{
				Next_Sea_ASC_Loc=Math.round(Sea_ASC_Loc+(Normalized_YBY(Sea_Task.elementAt(Next_Sea_Task_Ptr).Dest_YBY)-Sea_ASC_Loc)/
						DeltaSecond(Sea_Task.elementAt(Next_Sea_Task_Ptr).EN_time,ASC_Time)*(int)SampleRate);
			}
		}else{
			if(FutureTime.after(Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time)){
				Next_Sea_ASC_Loc=Math.round(Normalized_YBY(Sea_Task.elementAt(Sea_Task_Ptr).Dest_YBY)+
						(Normalized_YBY(Sea_Task.elementAt(Next_Sea_Task_Ptr).Dest_YBY-
						Normalized_YBY(Sea_Task.elementAt(Sea_Task_Ptr).Dest_YBY)))/
						DeltaSecond(Sea_Task.elementAt(Next_Sea_Task_Ptr).EN_time,Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time)*
						DeltaSecond(FutureTime,Sea_Task.elementAt(Next_Sea_Task_Ptr).ST_time));
			}else{
				Next_Sea_ASC_Loc=Normalized_YBY(Sea_Task.elementAt(Sea_Task_Ptr).Dest_YBY);
			}
		}
		
		if(Next_Sea_Task_Ptr!=Sea_Task_Ptr){
			if(Sea_Task_Ptr<Sea_Task.size())
			if(Sea_Task.elementAt(Sea_Task_Ptr).CarryContainer){
				Container_Down(Sea_Task.elementAt(Sea_Task_Ptr).Dest_YBY,Sea_Task.elementAt(Sea_Task_Ptr).Dest_Col);
			}
			if(Next_Sea_Task_Ptr<Sea_Task.size())
			if(Sea_Task.elementAt(Next_Sea_Task_Ptr).CarryContainer){
				Container_Lift(Sea_Task.elementAt(Sea_Task_Ptr).Dest_YBY,Sea_Task.elementAt(Sea_Task_Ptr).Dest_Col);
			}
		}
		
		if(Next_Land_Task_Ptr>=Land_Task.size()){
			Next_Land_ASC_Loc=Land_Task.lastElement().Dest_YBY;
		}else if(Next_Land_Task_Ptr==Land_Task_Ptr){
			if(Land_Task.elementAt(Next_Land_Task_Ptr).ST_time.after(FutureTime)) Next_Land_ASC_Loc=Land_ASC_Loc;
			else if(Land_Task.elementAt(Next_Land_Task_Ptr).ST_time.after(ASC_Time)){
				Next_Land_ASC_Loc=Math.round(Land_ASC_Loc+(Normalized_YBY(Land_Task.elementAt(Next_Land_Task_Ptr).Dest_YBY)-Land_ASC_Loc)/
						DeltaSecond(Land_Task.elementAt(Next_Land_Task_Ptr).EN_time,Land_Task.elementAt(Next_Land_Task_Ptr).ST_time)*
						DeltaSecond(FutureTime,Land_Task.elementAt(Next_Land_Task_Ptr).ST_time));
			}else{
				Next_Land_ASC_Loc=Math.round(Land_ASC_Loc+(Normalized_YBY(Land_Task.elementAt(Next_Land_Task_Ptr).Dest_YBY)-Land_ASC_Loc)/
						DeltaSecond(Land_Task.elementAt(Next_Land_Task_Ptr).EN_time,ASC_Time)*(int)SampleRate);
			}
		}else{
			if(FutureTime.after(Land_Task.elementAt(Next_Land_Task_Ptr).ST_time)){
				Next_Land_ASC_Loc=Math.round(Normalized_YBY(Land_Task.elementAt(Land_Task_Ptr).Dest_YBY)+
						(Normalized_YBY(Land_Task.elementAt(Next_Land_Task_Ptr).Dest_YBY-
						Normalized_YBY(Land_Task.elementAt(Land_Task_Ptr).Dest_YBY)))/
						DeltaSecond(Land_Task.elementAt(Next_Land_Task_Ptr).EN_time,Land_Task.elementAt(Next_Land_Task_Ptr).ST_time)*
						DeltaSecond(FutureTime,Land_Task.elementAt(Next_Land_Task_Ptr).ST_time));
			}else{
				Next_Land_ASC_Loc=Normalized_YBY(Land_Task.elementAt(Land_Task_Ptr).Dest_YBY);
			}
		}
		
		if(Next_Land_Task_Ptr!=Land_Task_Ptr){
			if(Land_Task_Ptr<Land_Task.size())
			if(Land_Task.elementAt(Land_Task_Ptr).CarryContainer){
				Container_Down(Land_Task.elementAt(Land_Task_Ptr).Dest_YBY,Land_Task.elementAt(Land_Task_Ptr).Dest_Col);
			}
			if(Next_Land_Task_Ptr<Land_Task.size())
			if(Land_Task.elementAt(Next_Land_Task_Ptr).CarryContainer){
				Container_Lift(Land_Task.elementAt(Land_Task_Ptr).Dest_YBY,Land_Task.elementAt(Land_Task_Ptr).Dest_Col);
			}
		}
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Next_Land_ASC_Loc=Normalized_YBY(Next_Land_ASC_Loc);
		Next_Sea_ASC_Loc=Normalized_YBY(Next_Sea_ASC_Loc);
		if(Math.max(Sea_ASC_Loc, Next_Sea_ASC_Loc)>=Math.min(Land_ASC_Loc, Next_Land_ASC_Loc)){
			throw new Exception("ASC Crash at ["+df.format(ASC_Time.getTime())+","+df.format(FutureTime.getTime())+"]");
		}
			
		ASC_Time=FutureTime;
		Sea_Task_Ptr=Next_Sea_Task_Ptr;
		Land_Task_Ptr=Next_Land_Task_Ptr;
		
		Land_ASC_Loc=Next_Land_ASC_Loc;
		Sea_ASC_Loc=Next_Sea_ASC_Loc;
		
		String Sea_ASC_Str="[Title:Sea][Info:YangshanPort][LineRGB:0x00FFFF][DashLine:][LineVisible:][WordVisible:]";
		String Land_ASC_Str="[Title:Land][Info:YangshanPort][LineRGB:0xFF00FF][DashLine:][LineVisible:][WordVisible:]";
		
		if(Sea_Task_Ptr<Sea_Task.size()){
			if(ASC_Time.after(Sea_Task.elementAt(Sea_Task_Ptr).ST_time))
				if(Sea_Task.elementAt(Sea_Task_Ptr).CarryContainer)
					Sea_ASC_Str="[Title:+Sea][Info:YangshanPort][LineRGB:0x00FFFF][DashLine:][LineVisible:][WordVisible:]";
		}
		
		if(Land_Task_Ptr<Land_Task.size()){
			if(ASC_Time.after(Land_Task.elementAt(Land_Task_Ptr).ST_time))
				if(Land_Task.elementAt(Land_Task_Ptr).CarryContainer)
					Land_ASC_Str="[Title:+Land][Info:YangshanPort][LineRGB:0xFF00FF][DashLine:][LineVisible:][WordVisible:]";
		}
		
		MainHandle.getLineDatabase().add(new double[]{-50,50}, new double[]{Sea_ASC_Loc*6.4/2,Sea_ASC_Loc*6.4/2}, 2,Sea_ASC_Str);
		MainHandle.getLineDatabase().add(new double[]{-50,50}, new double[]{Land_ASC_Loc*6.4/2,Land_ASC_Loc*6.4/2}, 2,Land_ASC_Str);
		
		MainHandle.getPointDatabase().add(100, 10, 23,"[Title:Layer0][Info:YangshanPort][PointRGB:0xF0F0F0][PointSize:10][PointVisible:][WordVisible:]");
		MainHandle.getPointDatabase().add(100, 30, 23,"[Title:Layer1][Info:YangshanPort][PointRGB:0x99D9EB][PointSize:10][PointVisible:][WordVisible:]");
		MainHandle.getPointDatabase().add(100, 50, 23,"[Title:Layer2][Info:YangshanPort][PointRGB:0xB5E61C][PointSize:10][PointVisible:][WordVisible:]");
		MainHandle.getPointDatabase().add(100, 70, 23,"[Title:Layer3][Info:YangshanPort][PointRGB:0xFAFA00][PointSize:10][PointVisible:][WordVisible:]");
		MainHandle.getPointDatabase().add(100, 90, 23,"[Title:Layer4][Info:YangshanPort][PointRGB:0xEB960A][PointSize:10][PointVisible:][WordVisible:]");
		MainHandle.getPointDatabase().add(100, 110, 23,"[Title:Layer5][Info:YangshanPort][PointRGB:0xED1C24][PointSize:10][PointVisible:][WordVisible:]");
		MainHandle.getPointDatabase().add(100, 130, 23,"[Title:Layer6][Info:YangshanPort][PointRGB:0x30245C][PointSize:10][PointVisible:][WordVisible:]");
		for(int row_i=0;row_i<SlotRow;row_i++){
			for(int col_i=0;col_i<SlotCol;col_i++){
				Slot_ptr=SlotMatrix[row_i][col_i];
				Info_String="[Title:][Info:YangshanPort][LineAlpha:0.5][PolygonRGB:0x123456][PolygonAlpha:0.5][LineVisible:]";
				if(Slot_ptr.layer==0) Info_String+="[LineRGB:0xF0F0F0]";
				else if(Slot_ptr.layer==1) Info_String+="[LineRGB:0x99D9EB]";
				else if(Slot_ptr.layer==2) Info_String+="[LineRGB:0xB5E61C]";
				else if(Slot_ptr.layer==3) Info_String+="[LineRGB:0xFAFA00]";
				else if(Slot_ptr.layer==4) Info_String+="[LineRGB:0xEB960A]";
				else if(Slot_ptr.layer==5) Info_String+="[LineRGB:0xED1C24]";
				else Info_String+="[LineRGB:0x30245C]";
				MainHandle.getPolygonDatabase().add(new double[]{Slot_ptr.x,Slot_ptr.x+1,Slot_ptr.x+1,Slot_ptr.x},
				new double[]{Slot_ptr.y,Slot_ptr.y,Slot_ptr.y+5,Slot_ptr.y+5}, 4, Info_String);
			}
		}  
		MainHandle.ShowTextArea1(df.format(ASC_Time.getTime()),false);
		MainHandle.ScreenFlush();
		}catch(Exception ex){
			SlotPlayIndicator=false;
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}
	
	public YangshanPortASCPaneClass(){
		JLabel Title=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("YangShanPortASC"));
		Title.setFont(new Font("华文新魏",Font.BOLD,30));
		add(Title);
		ScreenLockButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				MapWizard.SingleItem.Screen.setLock(true);
				CheckLockButtons();
			}
		});
		ScreenUnLockButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MapWizard.SingleItem.Screen.setLock(false);
				CheckLockButtons();
			}
		});
		LongitudeText = new JTextField(15);
		LatitudeText = new JTextField(15);
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("鼠标指向经度")));
		add(LongitudeText);
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("鼠标指向纬度")));
		add(LatitudeText);
		//-------------------------------------------------------
		/*
		SlotGenerateButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					SlotRow=Integer.parseInt(SlotRenderRowField.getText());
					SlotCol=Integer.parseInt(SlotRenderColField.getText());
					SlotMatrix=new SlotEntity[SlotRow][SlotCol];
					SlotEntity Slot_ptr;
					for(int row_i=0;row_i<SlotRow;row_i++){
						for(int col_i=0;col_i<SlotCol;col_i++){
							Slot_ptr=SlotMatrix[row_i][col_i]=new SlotEntity();
							Slot_ptr.height=6;
							Slot_ptr.width=2.5;
							Slot_ptr.x=col_i*2.8;
							Slot_ptr.y=row_i*6.4;
							Slot_ptr.layer=0;
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "ERR");
				}
			}
		});
		SlotClearButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(SlotPlayIndicator) return;
				SlotMatrix=null;
			}
		});
		*/
		//---------------------------------------------------------
		add(new JLabel("SampleRate(>=1s)"));
		add((SampleRateField=new JTextField(6)));
		add(new JLabel("FlushRate(>=50ms)"));
		add((FlushRateField=new JTextField(6)));
		add(Yard_Layout_Button=new JButton("Yard_Layout"));
		add(Yard_Layout_Field=new JTextField(12));
		Yard_Layout_Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(null);
				if(state==JFileChooser.APPROVE_OPTION){
					Yard_Layout_Field.setText(FileDialog.getSelectedFile().getAbsolutePath());
				}
			}
		});
		add(Container_Button=new JButton("Container"));
		add(Container_Field=new JTextField(12));
		Container_Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(null);
				if(state==JFileChooser.APPROVE_OPTION){
					Container_Field.setText(FileDialog.getSelectedFile().getAbsolutePath());
				}
			}
		});
		add(Sea_Move_Button=new JButton("Sea_Move_ASC"));
		add(Sea_Move_Field=new JTextField(12));
		Sea_Move_Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(null);
				if(state==JFileChooser.APPROVE_OPTION){
					Sea_Move_Field.setText(FileDialog.getSelectedFile().getAbsolutePath());
				}
			}
		});
		add(Land_Move_Button=new JButton("Land_Move_ASC"));
		add(Land_Move_Field=new JTextField(12));
		Land_Move_Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser FileDialog=new JFileChooser();
				int state=FileDialog.showOpenDialog(null);
				if(state==JFileChooser.APPROVE_OPTION){
					Land_Move_Field.setText(FileDialog.getSelectedFile().getAbsolutePath());
				}
			}
		});
		add(new JLabel("Sea_ASC_Loc"));
		Sea_ASC_Loc_Setting=new JTextField(10);
		add(Sea_ASC_Loc_Setting);
		Sea_ASC_Loc_Setting.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					Sea_ASC_Loc=Integer.parseInt(Sea_ASC_Loc_Setting.getText());
					MainHandle.ChangeTitle("SEA_ASC->Bay"+Sea_ASC_Loc);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "ERR");
				}
			}
		});
		add(new JLabel("Land_ASC_Loc"));
		Land_ASC_Loc_Setting=new JTextField(10);
		add(Land_ASC_Loc_Setting);
		Land_ASC_Loc_Setting.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					Land_ASC_Loc=Integer.parseInt(Land_ASC_Loc_Setting.getText());
					MainHandle.ChangeTitle("LAND_ASC->Bay"+Land_ASC_Loc);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "ERR");
				}
			}
		});
		//----------------------------------------------------------
		add((ASCPlayButton=new JButton("ASCPlay")));
		add((ASCStopButton=new JButton("ASCStop")));
		ASCPlayButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try{
					SlotMatrix=null;
					
					File f=new File(Yard_Layout_Field.getText());
					BufferedReader fin=new BufferedReader(new FileReader(f)); 
					String str;
					String[] attribute;
					String[] value;
					str=fin.readLine();
					attribute=str.split(",");
					str=fin.readLine();
					value=str.split(",");
					for(int i=0;i<attribute.length;i++){
						if(attribute[i].equals("\"YAA_EDBAYNO\"")){
							SlotRow=(Integer.parseInt(value[i].substring(1,value[i].length()-1))+1)/2;
						}else if(attribute[i].equals("\"YAA_ROWS\"")){
							SlotCol=Integer.parseInt(value[i].substring(1,value[i].length()-1));
						}
					}
					fin.close();
					SlotMatrix=new SlotEntity[SlotRow][SlotCol];
					for(int Row_i=0;Row_i<SlotRow;Row_i++){
						for(int Col_i=0;Col_i<SlotCol;Col_i++){
							SlotMatrix[Row_i][Col_i]=new SlotEntity();
							SlotMatrix[Row_i][Col_i].height=6;
							SlotMatrix[Row_i][Col_i].width=2.5;
							SlotMatrix[Row_i][Col_i].x=Col_i*2.8;
							SlotMatrix[Row_i][Col_i].y=Row_i*6.4;
							SlotMatrix[Row_i][Col_i].layer=0;
						}
					}
					
					int BayID=0;
					int col_ID=0;
					int layer=0;
					int container_length=0;
					f=new File(Container_Field.getText());
					fin=new BufferedReader(new FileReader(f));
					str=fin.readLine();
					attribute=str.split(",");
					while((str=fin.readLine())!=null){
						if(str.trim().isEmpty()) continue;
						value=str.split(",");
						for(int i=0;i<attribute.length;i++){
							if(attribute[i].equals("IYC_CSZ_CSIZECD")){
								container_length=Integer.parseInt(value[i]);
							}else if(attribute[i].equals("IYC_YLOCATION")){
								BayID=Integer.parseInt(value[i].substring(2,3),16)*10+Integer.parseInt(value[i].substring(3,4));
								col_ID=Integer.parseInt(value[i].substring(4,5));
								layer=Integer.parseInt(value[i].substring(5,6));
							}
						}
						if(container_length>20){
							SlotMatrix[BayID/2-1][col_ID-1].layer++;
							SlotMatrix[BayID/2][col_ID-1].layer++;
						}else{
							SlotMatrix[BayID/2][col_ID-1].layer++;
						}
					}
					fin.close();
					
					SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					ASC_Time=null;
					Sea_Task_Ptr=0;
					Land_Task_Ptr=0;
					
					Sea_Task.clear();
					f=new File(Sea_Move_Field.getText());
					fin=new BufferedReader(new FileReader(f));
					str=fin.readLine();
					attribute=str.split(",");
					while((str=fin.readLine())!=null){
						if(str.trim().isEmpty()) continue;
						value=str.split(",");
						ASC_Command TaskPtr=new ASC_Command();
						for(int i=0;i<attribute.length;i++){
							if(attribute[i].equals("State")){
								TaskPtr.CarryContainer=value[i].equals("0")?false:true;
							}else if(attribute[i].equals("Dest")){
								TaskPtr.Dest_YBY=Integer.parseInt(value[i].substring(2,3),16)*10+Integer.parseInt(value[i].substring(3,4));
								TaskPtr.Dest_Col=Integer.parseInt(value[i].substring(4,5));
							}else if(attribute[i].equals("St-time")){
								TaskPtr.ST_time=Calendar.getInstance();
								TaskPtr.ST_time.setTime(sFormat.parse(value[i]));
							}else if(attribute[i].equals("En-time")){
								TaskPtr.EN_time=Calendar.getInstance();
								TaskPtr.EN_time.setTime(sFormat.parse(value[i]));
							}
						}
						Sea_Task.add(TaskPtr);
					}
					fin.close();
					
					Land_Task.clear();
					f=new File(Land_Move_Field.getText());
					fin=new BufferedReader(new FileReader(f));
					str=fin.readLine();
					attribute=str.split(",");
					while((str=fin.readLine())!=null){
						if(str.trim().isEmpty()) continue;
						value=str.split(",");
						ASC_Command TaskPtr=new ASC_Command();
						for(int i=0;i<attribute.length;i++){
							if(attribute[i].equals("State")){
								TaskPtr.CarryContainer=value[i].equals("0")?false:true;
							}else if(attribute[i].equals("Dest")){
								TaskPtr.Dest_YBY=Integer.parseInt(value[i].substring(2,3),16)*10+Integer.parseInt(value[i].substring(3,4));
								TaskPtr.Dest_Col=Integer.parseInt(value[i].substring(4,5));
							}else if(attribute[i].equals("St-time")){
								TaskPtr.ST_time=Calendar.getInstance();
								TaskPtr.ST_time.setTime(sFormat.parse(value[i]));
							}else if(attribute[i].equals("En-time")){
								TaskPtr.EN_time=Calendar.getInstance();
								TaskPtr.EN_time.setTime(sFormat.parse(value[i]));
							}
						}
						Land_Task.add(TaskPtr);
					}
					fin.close();
					
					if(Sea_Task.size()+Land_Task.size()==0)
						throw new Exception("None Task");
					
					if(Sea_Task.size()>0)
					ASC_Time=Sea_Task.firstElement().ST_time;
					
					if(Land_Task.size()>0){
						if(ASC_Time==null){
							ASC_Time=Land_Task.firstElement().ST_time;
						}else if(ASC_Time.after(Land_Task.firstElement().ST_time)){
							ASC_Time=Land_Task.firstElement().ST_time;
						}
					}
					
					SampleRate=Double.parseDouble(SampleRateField.getText());
					FlushRate=Double.parseDouble(FlushRateField.getText());
					
					ASC_Command TaskPtr=new ASC_Command();
					TaskPtr.CarryContainer=false;
					TaskPtr.Dest_Col=1;
					TaskPtr.Dest_YBY=Sea_ASC_Loc=Integer.parseInt(Sea_ASC_Loc_Setting.getText());
					TaskPtr.EN_time=ASC_Time;
					TaskPtr.ST_time=Calendar.getInstance();
					TaskPtr.ST_time.setTime(TaskPtr.EN_time.getTime());
					TaskPtr.ST_time.add(Calendar.SECOND, (int)-SampleRate*2);
					Sea_Task.insertElementAt(TaskPtr, 0);

					TaskPtr=new ASC_Command();
					TaskPtr.CarryContainer=false;
					TaskPtr.Dest_Col=1;
					TaskPtr.Dest_YBY=Land_ASC_Loc=Integer.parseInt(Land_ASC_Loc_Setting.getText());
					TaskPtr.EN_time=ASC_Time;
					TaskPtr.ST_time=Calendar.getInstance();
					TaskPtr.ST_time.setTime(TaskPtr.EN_time.getTime());
					TaskPtr.ST_time.add(Calendar.SECOND, (int)-SampleRate*2);
					Land_Task.insertElementAt(TaskPtr, 0);
					
					ASC_Time=TaskPtr.ST_time;
					SlotPlayIndicator=true;
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "ERR");
				}
			}
		});
		ASCStopButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SlotPlayIndicator=false;
				SlotMatrix=null;
				MainHandle.getPolygonDatabase().DatabaseDelete("[Info:YangshanPort]");
				MainHandle.getPointDatabase().DatabaseDelete("[Info:YangshanPort]");
				MainHandle.getLineDatabase().DatabaseDelete("[Info:YangshanPort]");
			}
		});
		//----------------------------------------------------------
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("BackGround34.jpg"));
		g.drawImage(img,0,0,280,680,this);
	}
	@Override
	public void setLongitudeLatitude(double x, double y) {
		// TODO Auto-generated method stub
		LongitudeText.setText(java.lang.Double.toString(x));
		LatitudeText.setText(java.lang.Double.toString(y));
	}
	
	@Override
	public void emerge() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void convey(double x, double y) {
		// TODO Auto-generated method stub
		
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
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String GetSocketResult(String SocketQuery) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLongitude(double Longitude) {
		// TODO Auto-generated method stub
		LongitudeText.setText(java.lang.Double.toString(Longitude));
	}

	@Override
	public void setLatitude(double Latitude) {
		// TODO Auto-generated method stub
		LatitudeText.setText(java.lang.Double.toString(Latitude));
	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return "YangshanPortASCPane";
	}

}
