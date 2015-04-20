package FreeWizard;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FreeWizard.PolygonDatabaseWizard.FacePic;
import MapKernel.MapControl;

public class PolygonPreferenceWizard extends JFrame implements FreeWizardInterface{
	MapControl MainHandle;
	FacePic Pic;
	JButton Hide,Confirm,Apply,ReLocation;
	JLabel l0;
	JTextField KeyWord,Visible,ReTitle,AppendInfo,AppendSpur;
	JRadioButton WordWhite,WordRed,WordYellow,WordBlue,WordGreen,WordCyan,WordPink,WordOrange;
	JRadioButton PointWhite,PointRed,PointYellow,PointBlue,PointGreen,PointCyan,PointPink,PointOrange;
	JRadioButton LineWhite,LineRed,LineYellow,LineBlue,LineGreen,LineCyan,LinePink,LineOrange;
	ButtonGroup WordGroup,PointGroup,LineGroup;
	JCheckBox IsVisible,IsWordVisible,IsPointVisible,IsLineVisible,IsUniversal,InfoWriteOver,SpurWriteOver,AutoApply;
	class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener,ItemListener{
	public FacePic(){
		setBounds(0,0,520,330);
		Hide=new JButton("撤销更改并返回");
		Hide.addActionListener(this);
		Confirm=new JButton("生效并返回");
		Confirm.addActionListener(this);
		KeyWord=new JTextField(32);
		Visible=new JTextField(4);
		l0=new JLabel("【多边形地理区域配置视窗】");
		l0.setFont(new Font("华文新魏",Font.BOLD,36));
		add(l0);
		add(new JLabel("关键字"));
		add(KeyWord);
		add(new JLabel("特征值"));
		add(Visible);
		//Specific---------------------------------------------
		AutoApply=new JCheckBox("自动显示变化");
		AutoApply.setOpaque(false);
		add(AutoApply);
		IsUniversal=new JCheckBox("是否应用于其他检索项");
		IsUniversal.setOpaque(false);
		add(IsUniversal);
		
		add(new JLabel("显示名"));
		
		ReTitle=new JTextField(18);
		add(ReTitle);
		
		IsVisible=new JCheckBox("允许在地图上显示");
		IsVisible.setOpaque(false);
		IsWordVisible=new JCheckBox("在地图上显示字");
		IsWordVisible.setOpaque(false);
		IsPointVisible=new JCheckBox("在地图上显示点");
		IsPointVisible.setOpaque(false);
		IsLineVisible=new JCheckBox("在地图上显示线");
		IsLineVisible.setOpaque(false);
		add(IsVisible);
		add(IsWordVisible);
		add(IsPointVisible);
		add(IsLineVisible);
				
		WordGroup=new ButtonGroup();
		PointGroup=new ButtonGroup();
		LineGroup=new ButtonGroup();
		WordWhite=new JRadioButton("White");
		WordWhite.setOpaque(false);
		WordRed=new JRadioButton("Red");
		WordRed.setOpaque(false);
		WordYellow=new JRadioButton("Yellow");
		WordYellow.setOpaque(false);
		WordBlue=new JRadioButton("Blue");
		WordBlue.setOpaque(false);
		WordGreen=new JRadioButton("Green");
		WordGreen.setOpaque(false);
		WordCyan=new JRadioButton("Cyan");
		WordCyan.setOpaque(false);
		WordPink=new JRadioButton("Pink");
		WordPink.setOpaque(false);
		WordOrange=new JRadioButton("Orange");
		WordOrange.setOpaque(false);
		WordGroup.add(WordWhite);
		WordGroup.add(WordRed);
		WordGroup.add(WordYellow);
		WordGroup.add(WordBlue);
		WordGroup.add(WordGreen);
		WordGroup.add(WordCyan);
		WordGroup.add(WordPink);
		WordGroup.add(WordOrange);
		PointWhite=new JRadioButton("White");
		PointWhite.setOpaque(false);
		PointRed=new JRadioButton("Red");
		PointRed.setOpaque(false);
		PointYellow=new JRadioButton("Yellow");
		PointYellow.setOpaque(false);
		PointBlue=new JRadioButton("Blue");
		PointBlue.setOpaque(false);
		PointGreen=new JRadioButton("Green");
		PointGreen.setOpaque(false);
		PointCyan=new JRadioButton("Cyan");
		PointCyan.setOpaque(false);
		PointPink=new JRadioButton("Pink");
		PointPink.setOpaque(false);
		PointOrange=new JRadioButton("Orange");
		PointOrange.setOpaque(false);
		PointGroup.add(PointWhite);
		PointGroup.add(PointRed);
		PointGroup.add(PointYellow);
		PointGroup.add(PointBlue);
		PointGroup.add(PointGreen);
		PointGroup.add(PointCyan);
		PointGroup.add(PointPink);
		PointGroup.add(PointOrange);
		LineWhite=new JRadioButton("White");
		LineWhite.setOpaque(false);
		LineRed=new JRadioButton("Red");
		LineRed.setOpaque(false);
		LineYellow=new JRadioButton("Yellow");
		LineYellow.setOpaque(false);
		LineBlue=new JRadioButton("Blue");
		LineBlue.setOpaque(false);
		LineGreen=new JRadioButton("Green");
		LineGreen.setOpaque(false);
		LineCyan=new JRadioButton("Cyan");
		LineCyan.setOpaque(false);
		LinePink=new JRadioButton("Pink");
		LinePink.setOpaque(false);
		LineOrange=new JRadioButton("Orange");
		LineOrange.setOpaque(false);
		LineGroup.add(LineWhite);
		LineGroup.add(LineRed);
		LineGroup.add(LineYellow);
		LineGroup.add(LineBlue);
		LineGroup.add(LineGreen);
		LineGroup.add(LineCyan);
		LineGroup.add(LinePink);
		LineGroup.add(LineOrange);
		add(new JLabel("字色"));
		add(WordWhite);
		add(WordRed);
		add(WordYellow);
		add(WordBlue);
		add(WordGreen);
		add(WordCyan);
		add(WordPink);
		add(WordOrange);
		add(new JLabel("点色"));
		add(PointWhite);
		add(PointRed);
		add(PointYellow);
		add(PointBlue);
		add(PointGreen);
		add(PointCyan);
		add(PointPink);
		add(PointOrange);
		add(new JLabel("线色"));
		add(LineWhite);
		add(LineRed);
		add(LineYellow);
		add(LineBlue);
		add(LineGreen);
		add(LineCyan);
		add(LinePink);
		add(LineOrange);
		
		add(new JLabel("追加信息"));
		AppendInfo=new JTextField(28);
		add(AppendInfo);
		InfoWriteOver=new JCheckBox("覆写信息而不追加");
		InfoWriteOver.setOpaque(false);
		add(InfoWriteOver);
		add(new JLabel("追加触发"));
		AppendSpur=new JTextField(28);
		add(AppendSpur);
		SpurWriteOver=new JCheckBox("覆写触发而不追加");
		SpurWriteOver.setOpaque(false);
		add(SpurWriteOver);
		
		IsVisible.addItemListener(this);
		IsWordVisible.addItemListener(this);
		IsPointVisible.addItemListener(this);
		IsLineVisible.addItemListener(this);
		
		WordWhite.addItemListener(this);
		WordRed.addItemListener(this);
		WordYellow.addItemListener(this);
		WordBlue.addItemListener(this);
		WordGreen.addItemListener(this);
		WordCyan.addItemListener(this);
		WordPink.addItemListener(this);
		WordOrange.addItemListener(this);
		
		PointWhite.addItemListener(this);
		PointRed.addItemListener(this);
		PointYellow.addItemListener(this);
		PointBlue.addItemListener(this);
		PointGreen.addItemListener(this);
		PointCyan.addItemListener(this);
		PointPink.addItemListener(this);
		PointOrange.addItemListener(this);	
		
		LineWhite.addItemListener(this);
		LineRed.addItemListener(this);
		LineYellow.addItemListener(this);
		LineBlue.addItemListener(this);
		LineGreen.addItemListener(this);
		LineCyan.addItemListener(this);
		LinePink.addItemListener(this);
		LineOrange.addItemListener(this);
		
		Visible.setEditable(false);
		KeyWord.setEditable(false);
		//---------------------------------------------
		Apply=new JButton("立即生效不返回");
		Apply.addActionListener(this);
		
		ReLocation=new JButton("设置文字位置");
		ReLocation.addActionListener(this);
		
		add(ReLocation);
		add(Hide);
		add(Apply);
		add(Confirm);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("Gear.jpg");
		g.drawImage(img,0,0,520,330,this);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==Hide){
			submerge();
		}else if(e.getSource()==Confirm){
			if(IsUniversal.isSelected()){
				FreeWizard.PolygonDatabaseWizard TempView=MainHandle.getKernel().PolygonDatabaseView;
				int RowCount=TempView.Pic.ResultTable.getRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) TempView.Pic.ResultTable.getModel();
				for(int i=0;i<RowCount;i++){
				  String str=(String)tableModel.getValueAt(i,0);
				  try{
					  ProcessRow(Integer.parseInt(str));
				  }catch(Exception ex){
					  JOptionPane.showMessageDialog(null,"数据库检查到异常格式","数据格式异常",JOptionPane.WARNING_MESSAGE);
				  }
				}
			}else{
				ProcessRow(Index);
			}
			submerge();
			MainHandle.ScreenFlush();
		}else if(e.getSource()==Apply){
			if(IsUniversal.isSelected()){
				FreeWizard.PolygonDatabaseWizard TempView=MainHandle.getKernel().PolygonDatabaseView;
				int RowCount=TempView.Pic.ResultTable.getRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) TempView.Pic.ResultTable.getModel();
				for(int i=0;i<RowCount;i++){
				  String str=(String)tableModel.getValueAt(i,0);
				  try{
					  ProcessRow(Integer.parseInt(str));
				  }catch(Exception ex){
					  JOptionPane.showMessageDialog(null,"数据库检查到异常格式","数据格式异常",JOptionPane.WARNING_MESSAGE);
				  }
				}
			}else{
				ProcessRow(Index);
			}
			MainHandle.ScreenFlush();
		}else if(e.getSource()==ReLocation){
			MainHandle.getKernel().PolygonHintReLocationView.convey(Index);
			MainHandle.getKernel().PolygonHintReLocationView.emerge();
		}
	}
	public void mouseDragged(MouseEvent e) {
		Move(e.getX()-PressedX,e.getY()-PressedY);
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()>2) submerge();
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	int PressedX,PressedY;
	public void mousePressed(MouseEvent e) {
		PressedX=e.getX();
		PressedY=e.getY();
	}
	public void mouseReleased(MouseEvent e) {
		Move(e.getX()-PressedX,e.getY()-PressedY);
	}
	public int setbit(int a,int b,int c){
		int tmp=a;
		a=a>>c;
		a=(a>>3)<<3;
		a=a | b;
		a=(a<<c)+tmp%(1<<c);
		return a;
	}
	public int setsinglebit(int a,int b,boolean f){
		int c;
		c=f?1:0;
		int d=a;
		a=(a>>b);
		a=(a>>1)<<1;
		a=a | c;
		a=(a<<b)+d%(1<<b);
		return a;
	}
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if(!IsEmerge()) return;
		int value=Integer.parseInt(Visible.getText());
		
		if(e.getSource()==IsVisible) value=setsinglebit(value,0,IsVisible.isSelected());
		if(e.getSource()==IsWordVisible) value=setsinglebit(value,1,IsWordVisible.isSelected());
		if(e.getSource()==IsPointVisible) value=setsinglebit(value,2,IsPointVisible.isSelected());
		if(e.getSource()==IsLineVisible) value=setsinglebit(value,3,IsLineVisible.isSelected());


		if(e.getSource() instanceof JRadioButton)
		if(((JRadioButton)e.getSource()).isSelected()==true)
		{		
		if(e.getSource()==WordWhite) 	value=setbit(value,0,4);
		if(e.getSource()==WordRed)		value=setbit(value,1,4);
		if(e.getSource()==WordYellow)	value=setbit(value,2,4);
		if(e.getSource()==WordBlue)		value=setbit(value,3,4);
		if(e.getSource()==WordGreen)	value=setbit(value,4,4);
		if(e.getSource()==WordCyan)		value=setbit(value,5,4);
		if(e.getSource()==WordPink)		value=setbit(value,6,4);
		if(e.getSource()==WordOrange)	value=setbit(value,7,4);
		
		if(e.getSource()==PointWhite)	value=setbit(value,0,7);
		if(e.getSource()==PointRed)		value=setbit(value,1,7);
		if(e.getSource()==PointYellow)	value=setbit(value,2,7);
		if(e.getSource()==PointBlue)	value=setbit(value,3,7);
		if(e.getSource()==PointGreen)	value=setbit(value,4,7);
		if(e.getSource()==PointCyan)	value=setbit(value,5,7);
		if(e.getSource()==PointPink)	value=setbit(value,6,7);
		if(e.getSource()==PointOrange)	value=setbit(value,7,7);
		
		if(e.getSource()==LineWhite)	value=setbit(value,0,10);
		if(e.getSource()==LineRed)		value=setbit(value,1,10);
		if(e.getSource()==LineYellow)	value=setbit(value,2,10);
		if(e.getSource()==LineBlue)		value=setbit(value,3,10);
		if(e.getSource()==LineGreen)	value=setbit(value,4,10);
		if(e.getSource()==LineCyan)		value=setbit(value,5,10);
		if(e.getSource()==LinePink)		value=setbit(value,6,10);
		if(e.getSource()==LineOrange)	value=setbit(value,7,10);
		}
		Visible.setText(Integer.toString(value));
		
		if(AutoApply.isSelected()){
		if(IsUniversal.isSelected()){
			FreeWizard.PolygonDatabaseWizard TempView=MainHandle.getKernel().PolygonDatabaseView;
			int RowCount=TempView.Pic.ResultTable.getRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) TempView.Pic.ResultTable.getModel();
			for(int i=0;i<RowCount;i++){
			  String str=(String)tableModel.getValueAt(i,0);
			  try{
				  ProcessRow(Integer.parseInt(str));
			  }catch(Exception ex){
				  JOptionPane.showMessageDialog(null,"数据库检查到异常格式","数据格式异常",JOptionPane.WARNING_MESSAGE);
			  }
			}
		}else{
			ProcessRow(Index);
		}
		MainHandle.ScreenFlush();
		}
	}
	}
	//Above Pic-------------------------------------------------------

	@Override
	public void emerge() {
		// TODO Auto-generated method stub
		MainHandle.getKernel().PolygonDatabaseView.submerge();
		MainHandle.ForbidOperate();
		this.setVisible(true);
	}
	public void Convey(int Index){
		this.Index=Index;
		KeyWord.setText(MainHandle.getPolygonDatabase().PolygonHint[Index]);
		Visible.setText(Integer.toString(MainHandle.getPolygonDatabase().PolygonVisible[Index]));
		int state=MainHandle.getPolygonDatabase().PolygonVisible[Index];
		int temp;
		IsUniversal.setSelected(false);
		InfoWriteOver.setSelected(false);
		SpurWriteOver.setSelected(false);
		
		if((state & 1)==1) IsVisible.setSelected(true);
		else IsVisible.setSelected(false);
		
		state=state >> 1;
		if((state & 1)==1) IsWordVisible.setSelected(true);
		else IsWordVisible.setSelected(false);
		
		state=state >> 1;
		if((state & 1)==1) IsPointVisible.setSelected(true);
		else IsPointVisible.setSelected(false);
		
		state=state >> 1;
		if((state & 1)==1) IsLineVisible.setSelected(true);
		else IsLineVisible.setSelected(false);
		
		state=state >> 1;
		temp=state & 7;
		if(temp==0) WordWhite.setSelected(true);
		else if(temp==1) WordRed.setSelected(true);
		else if(temp==2) WordYellow.setSelected(true);
		else if(temp==3) WordBlue.setSelected(true);
		else if(temp==4) WordGreen.setSelected(true);
		else if(temp==5) WordCyan.setSelected(true);
		else if(temp==6) WordPink.setSelected(true);
		else WordOrange.setSelected(true);
		
		state=state >> 3;
		temp=state & 7;
		if(temp==0) PointWhite.setSelected(true);
		else if(temp==1) PointRed.setSelected(true);
		else if(temp==2) PointYellow.setSelected(true);
		else if(temp==3) PointBlue.setSelected(true);
		else if(temp==4) PointGreen.setSelected(true);
		else if(temp==5) PointCyan.setSelected(true);
		else if(temp==6) PointPink.setSelected(true);
		else PointOrange.setSelected(true);
		
		state=state >> 3;
		temp=state & 7;
		if(temp==0) LineWhite.setSelected(true);
		else if(temp==1) LineRed.setSelected(true);
		else if(temp==2) LineYellow.setSelected(true);
		else if(temp==3) LineBlue.setSelected(true);
		else if(temp==4) LineGreen.setSelected(true);
		else if(temp==5) LineCyan.setSelected(true);
		else if(temp==6) LinePink.setSelected(true);
		else LineOrange.setSelected(true);
		
		ReTitle.setText("");
		AppendInfo.setText("");
		AppendSpur.setText("");
	}
	public void ProcessRow(int k){
		int VisibleValue=Integer.parseInt(Visible.getText());
		StringBuffer Hint=new StringBuffer(MainHandle.getPolygonDatabase().PolygonHint[k]);
		
		if(AppendInfo.getText()!=null)
		if(!AppendInfo.getText().equals(""))
		if(InfoWriteOver.isSelected()){
			int i=0,j;
			while((i=Hint.indexOf("[Info:",i))!=-1){
				j=Hint.indexOf("]",i);
				if(j==-1) break;
				Hint.delete(i,j+1);
			}
		}
		//-------------------------------
		if(AppendSpur.getText()!=null)
		if(!AppendSpur.getText().equals(""))
		if(SpurWriteOver.isSelected()){
			int i=0,j;
			while((i=Hint.indexOf("[Spur:",i))!=-1){
				j=Hint.indexOf("]",i);
				if(j==-1) break;
				Hint.delete(i,j+1);
			}
		}
		//-------------------------------
		if(ReTitle.getText()!=null)
		if(!ReTitle.getText().equals("")){
			int i=0,j;
			while((i=Hint.indexOf("[Title:",i))!=-1){
				j=Hint.indexOf("]",i);
				if(j==-1) break;
				Hint.delete(i,j+1);
			}
		}
		//-------------------------------
		String str=ReTitle.getText();
		if(str.indexOf(']')!=-1){
			JOptionPane.showMessageDialog(null,str+"~~~The ] can not exist!!!","Error And Give Up Updating",JOptionPane.WARNING_MESSAGE);
			return;
		}
		if((str!=null)&&(!str.equals(""))){
			Hint.insert(0,"[Title:"+str+"]");
		}
		//---------------------------------
		str=AppendInfo.getText();
		if(str.indexOf(']')!=-1){
			JOptionPane.showMessageDialog(null,str+"~~~The ] can not exist!!!","Error And Give Up Updating",JOptionPane.WARNING_MESSAGE);
			return;
		}
		if((str!=null)&&(!str.equals(""))){
			Hint.insert(0,"[Info:"+str+"]");
		}
		//------------------------------------
		str=AppendSpur.getText();
		if(str.indexOf(']')!=-1){
			JOptionPane.showMessageDialog(null,str+"~~~The ] can not exist!!!","Error And Give Up Updating",JOptionPane.WARNING_MESSAGE);
			return;
		}
		if((str!=null)&&(!str.equals(""))){
			Hint.insert(0,"[Spur:"+str+"]");
		}
		MainHandle.getPolygonDatabase().update(k,VisibleValue,new String(Hint));
	}
	int Index;
	@Override
	public void submerge() {
		// TODO Auto-generated method stub
		MainHandle.getKernel().PolygonDatabaseView.emerge();
		MainHandle.getKernel().PolygonDatabaseView.Pic.ProcessQuery();
		this.setVisible(false);
	}

	@Override
	public void setHandle(MapControl MainHandle) {
		// TODO Auto-generated method stub
		this.MainHandle=MainHandle;
	}
	public PolygonPreferenceWizard(){
		setBounds(0,0,520,330);
		setVisible(false);
		setUndecorated(true);
		setLocationRelativeTo(null);
		Pic=new FacePic();
		add(Pic,BorderLayout.CENTER);
	}
	public void Move(int dx,int dy){
		int x=this.getLocation().x;
		int y=this.getLocation().y;
		this.setLocation(x+dx,y+dy);
	}
	@Override
	public boolean IsEmerge(){
		return this.isVisible();
	}
}
