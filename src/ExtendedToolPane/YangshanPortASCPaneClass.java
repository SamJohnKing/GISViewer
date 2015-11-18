package ExtendedToolPane;

import MapKernel.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
class SlotEntity{
	double x,y;
	double width,height;
	int layer;
	String ColorCode="0xFFFFFF";
}
public class YangshanPortASCPaneClass extends ToolPanel implements ExtendedToolPaneInterface {
	MapControl MainHandle;
	double CursrLongitude,CursorLatitude;
	JTextField LongitudeText,LatitudeText;
	JTextField SlotRenderRowField,SlotRenderColField;
	JTextField SampleRateField,FlushRateField;
	JButton SlotGenerateButton,SlotClearButton;
	JButton ASCPlayButton,ASCStopButton;
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
	
	public void ClockImpulse(){
		if(!SlotPlayIndicator) return;
		if(MainHandle.getSecond()%FlushRate!=0) return;
		MainHandle.getPolygonDatabase().DatabaseDelete("[Info:YangshanPort]");
		SlotEntity Slot_ptr;
		for(int row_i=0;row_i<SlotRow;row_i++){
			for(int col_i=0;col_i<SlotCol;col_i++){
				Slot_ptr=SlotMatrix[row_i][col_i];
				MainHandle.getPolygonDatabase().add(new double[]{Slot_ptr.x,Slot_ptr.x+2.8,Slot_ptr.x+2.8,Slot_ptr.x},
				new double[]{Slot_ptr.y+MainHandle.getSecond()%10,Slot_ptr.y+MainHandle.getSecond()%10,MainHandle.getSecond()%10+Slot_ptr.y+6.4,MainHandle.getSecond()%10+Slot_ptr.y+6.4}, 4, 
				"[Title:][Info:YangshanPort][PointRGB:0x6ac96f][PointAlpha:0.4][LineRGB:0x24fc4f][LineAlpha:0.5][PolygonRGB:0x123456][PolygonAlpha:0.5][PolygonVisible:][LineVisible:][PointVisible:][LineWidth:0][DashLine:][PointSize:10]");
			}
		}
		MainHandle.ShowTextArea1(MapWizard.SingleItem.ClockWizard.pic.getTimeString(), true);
		MainHandle.ScreenFlush();
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
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("SlotRow")));
		add((SlotRenderRowField=new JTextField(5)));
		add(new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("SlotCol")));
		add((SlotRenderColField=new JTextField(5)));
		add((SlotGenerateButton=new JButton("SlotGenerate")));
		add((SlotClearButton=new JButton("SlotRemoveAll")));
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
		add(new JLabel("SampleRate"));
		add((SampleRateField=new JTextField(5)));
		add(new JLabel("FlushRate"));
		add((FlushRateField=new JTextField(5)));
		add((ASCPlayButton=new JButton("ASCPlay")));
		add((ASCStopButton=new JButton("ASCStop")));
		ASCPlayButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try{
					SampleRate=Double.parseDouble(SampleRateField.getText());
					FlushRate=Double.parseDouble(FlushRateField.getText());
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
			}
		});
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("BackGround34.jpg");
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
