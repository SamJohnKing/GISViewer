package ExtendedToolPane;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JLabel;

import MapKernel.MapControl;

public class TaxiTrajectoryViewPaneClass extends MapKernel.ToolPanel implements ExtendedToolPaneInterface,ActionListener{
	MapControl MainHandle;
	JLabel PaneTitle;
	JTextField Interval,ViewNum;
	JButton TraceTaxi,StopTrace;
	public TaxiTrajectoryViewPaneClass(){
		PaneTitle=new JLabel("TaxiTrajectoryView");
		PaneTitle.setFont(new Font("华文新魏",Font.BOLD,25));
		add(PaneTitle);
		add(new JLabel("Interval Time Per impulse to Query"));
		Interval=new JTextField(20);
		add(Interval);
		add(new JLabel("Upper Number Limit of Taxi to Trace"));
		ViewNum=new JTextField(20);
		add(ViewNum);
		TraceTaxi=new JButton("Click to Trace the Taxi");
		TraceTaxi.addActionListener(this);
		StopTrace=new JButton("Click to Stop the Tracing Process");
		StopTrace.addActionListener(this);
		add(TraceTaxi);
		add(StopTrace);
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("BackGround34.jpg");
		g.drawImage(img,0,0,280,680,this);
	}
	@Override
	public void setLongitudeLatitude(double x, double y) {
		// TODO Auto-generated method stub
	}

	@Override
	public void emerge() {
		ImpulseInterval=-1;
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
		return "TaxiTrajectoryViewPane";
	}
	@Override
	public void ClockImpulse(){
		if(ImpulseInterval<1) return;
		Database.TaxiTrajectoryDatabaseClass TaxiDB=MainHandle.getKernel().TaxiTrajectoryDatabase;
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		if(MainHandle.getSecond()%ImpulseInterval!=0) return;
		TaxiDB.query(MainHandle.getSecond()-ImpulseInterval+1,MainHandle.getSecond());
		int st=TaxiDB.STPtr;
		int en=TaxiDB.ENPtr;
		System.out.println("Query["+(MainHandle.getSecond()-ImpulseInterval+1)+"->"+MainHandle.getSecond()+"]"
				+ "["+TaxiDB.AllTaxiSecond[TaxiDB.Iter(st)]+"->"+
					TaxiDB.AllTaxiSecond[TaxiDB.Iter(en)]+"]");
		String str;
		for(int i=st;i<en;i++){
			if(TaxiDB.AllTaxiID[TaxiDB.Iter(i)]>UpperLimit) continue;
			str="[Title:][Info:ClockDepend][Info:Cache][Info:TaxiTrajectory][Info:"+TaxiDB.AllTaxiID[TaxiDB.Iter(i)]+"]";
			ComparisionDelete(TaxiDB.AllTaxiLongitude[TaxiDB.Iter(i)],TaxiDB.AllTaxiLatitude[TaxiDB.Iter(i)],str);
			LineDB.DynamicAdd(TaxiDB.AllTaxiLongitude[TaxiDB.Iter(i)],TaxiDB.AllTaxiLatitude[TaxiDB.Iter(i)],str);
		}
		int hh=MainHandle.getKernel().ClockWizard.pic.getHour();
		int mm=MainHandle.getKernel().ClockWizard.pic.getMinute();
		int ss=MainHandle.getKernel().ClockWizard.pic.getSecond();
		MainHandle.ShowTextArea1(hh+":"+mm+":"+ss+" Condition:",true);
		MainHandle.ShowTextArea2("Retrieved Signal Count: "+(en-st),true);
		MainHandle.ScreenFlush();
	}
	public void ComparisionDelete(double newx,double newy,String Hint){
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		for(int i=0;i<LineDB.LineNum;i++){
			if(!LineDB.LineHint[i].equals(Hint)) continue;
			double lastx=LineDB.AllPointX[LineDB.LineTail[i]];
			double lasty=LineDB.AllPointY[LineDB.LineTail[i]];
			if(MainHandle.AccurateMeterDistance(lastx,lasty,newx,newy)>2000) LineDB.DatabaseDelete(i);
			break;
		}
	}
	int ImpulseInterval=-1;
	int UpperLimit;
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==TraceTaxi){
		try{
			ImpulseInterval=Integer.parseInt(Interval.getText());
			UpperLimit=Integer.parseInt(ViewNum.getText());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"格式错误","Notice the Format",JOptionPane.WARNING_MESSAGE);
		}
		}else if(arg0.getSource()==StopTrace){
			ImpulseInterval=-1;
		}
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
