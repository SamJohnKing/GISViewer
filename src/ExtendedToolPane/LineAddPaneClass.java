package ExtendedToolPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import MapKernel.MapControl;
import MapKernel.ToolPanel;

public class LineAddPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	public String getString(){
		return "LineAddPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public LineAddPaneClass(){
		JLabel Title=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("线路新建工具栏"));
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
	public void setLongitudeLatitude(double x,double y){
		//MainHandle.ShowTextArea1((float)x+"E/"+(float)y+"N",true);
	}
	public void setLongitude(double x){};
	public void setLatitude(double y){};
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==ScreenLockButton){
			MainHandle.ScreenLock(true);
			ScreenLockButton.setEnabled(false);
			ScreenUnLockButton.setEnabled(true);
		}else if(e.getSource()==ScreenUnLockButton){
			MainHandle.ScreenLock(false);
			ScreenLockButton.setEnabled(true);
			ScreenUnLockButton.setEnabled(false);
		//Specific Part---------------------------------------------
		}else if(e.getSource()==CancelLastOne){
			MainHandle.PointPop();
		}else if(e.getSource()==CancelAll){
			MainHandle.PointEmpty();
		}else if(e.getSource()==Submit){
			if(MainHandle.getPointCount()<2){
				JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("少于两个点不予提交"),
						MapKernel.MapWizard.LanguageDic.GetWords("拒绝提交"),JOptionPane.WARNING_MESSAGE);
				return;
			}
			String str;
			if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
				str=MainHandle.getPreference().CommonString.getText();
			else str=JOptionPane.showInputDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("输入地理线路标签"),
					MapKernel.MapWizard.LanguageDic.GetWords("确认提交"),JOptionPane.PLAIN_MESSAGE);
			if(str!=null){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("成功提交了")+"【"+str+"】");
				MainHandle.LineDatabaseAppend(str);
				MainHandle.PointEmpty();
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("放弃提交,仍然为您保留未提交数据"));
			}
		}
	}
	//Specific Part--------------------------------------------
	JCheckBox ConfirmLink,ShowConsecutiveLink,ShowHeadTailLink,ShowPointHint;
	JButton CancelLastOne,CancelAll,Submit;
	public void SpecificProcess(){
		ConfirmLink=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则开始创建线路，取消则放弃"));
		ConfirmLink.setOpaque(false);
		ConfirmLink.addItemListener(this);
		ConfirmLink.setForeground(Color.orange);
		add(ConfirmLink);
		ShowConsecutiveLink=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则开始显示连线，取消则无连线"));
		ShowConsecutiveLink.setOpaque(false);;
		ShowConsecutiveLink.addItemListener(this);
		ShowConsecutiveLink.setForeground(Color.orange);
		add(ShowConsecutiveLink);
		ShowPointHint=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则显示添加顺序，取消则不显示"));
		ShowPointHint.setOpaque(false);
		ShowPointHint.addItemListener(this);
		ShowPointHint.setForeground(Color.orange);
		add(ShowPointHint);
		CancelLastOne=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("取消最近"));
		CancelLastOne.addActionListener(this);
		add(CancelLastOne);
		CancelAll=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("取消全部"));
		CancelAll.addActionListener(this);
		add(CancelAll);
		Submit=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("提交信息"));
		Submit.addActionListener(this);
		add(Submit);
	}
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()==ConfirmLink){
			if(ConfirmLink.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("线路生成中"));
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("放弃了线路生成"));
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(false);
			}
		}else if(e.getSource()==ShowConsecutiveLink){
			if(ShowConsecutiveLink.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("连接点"));
				MainHandle.setPointConsecutiveLinkVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("不连接点"));
				MainHandle.setPointConsecutiveLinkVisible(false);
			}
		}else if(e.getSource()==ShowPointHint){
			if(ShowPointHint.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("显示轮廓点创建顺序"));
				MainHandle.setPointHintVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("不显示轮廓点创建顺序"));
				MainHandle.setPointHintVisible(false);
			}
		}
	}
	public void emerge(){
		//-----------------
		ConfirmLink.setSelected(MainHandle.getPointVisible());
		ShowConsecutiveLink.setSelected(MainHandle.getPointConsecutiveVisible());
		ShowPointHint.setSelected(MainHandle.getPointHintVisible());
		//-----------------
	}
	public void convey(double x,double y){
		if(ConfirmLink.isSelected()){
			MainHandle.ChangeTitle("FreeClick");
			//Matching The Point with LinkRegion Precisely
			if(MainHandle.getPreference().AllowPreciseLinkRegion.isSelected())
			for(int i=0;i<MainHandle.getPolygonDatabase().PolygonNum;i++){
				if(MainHandle.getPolygonDatabase().PolygonHint[i].indexOf("[Info:LinkRegion]")==-1) continue;
				if(MainHandle.getPolygonDatabase().CheckInsidePolygon(i, x, y)){
					ArrayList<Double> temp=MainHandle.getPolygonDatabase().PreciseMatchingAveragePoint(i);
					x=temp.get(0);
					y=temp.get(1);
					MainHandle.ChangeTitle("Precisely Matching With LinkRegion");
				}
			}
			//--------------------------------------------
			MainHandle.PointPush(x,y,"L"+MainHandle.getPointCount());
		}else MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("没有开始构建线路，点击无效"));
	}
	public void convey(double x1,double y1,double x2,double y2){
		MainHandle.PointSelect(x1,y1,x2,y2);
		int n=JOptionPane.showConfirmDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("是则删除，否则区域截图生成"),MapKernel.MapWizard.LanguageDic.GetWords("确认删除"),JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION){
			MainHandle.PointSelectDelete();
			MainHandle.ResetPointHint("L");
		}else if(n==JOptionPane.NO_OPTION){
			MainHandle.getKernel().JPGOutput(x1,x2,y1,y2);
		}else{
			MainHandle.PointSelectCancel();
		}
	}
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		if(MainHandle.getPointCount()<2){
			JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("少于两个点不予提交"),
					MapKernel.MapWizard.LanguageDic.GetWords("拒绝提交"),JOptionPane.WARNING_MESSAGE);
			return;
		}
		String str;
		if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
			str=MainHandle.getPreference().CommonString.getText();
		else str=JOptionPane.showInputDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("输入地理线路标签"),
				MapKernel.MapWizard.LanguageDic.GetWords("确认提交"),JOptionPane.PLAIN_MESSAGE);
		if(str!=null){
			MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("成功提交了")+"【"+str+"】");
			MainHandle.LineDatabaseAppend(str);
			MainHandle.PointEmpty();
		}else{
			MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("放弃提交,仍然为您保留未提交数据"));
		}
	}
	@Override
	public String GetSocketResult(String SocketQuery) {
		// TODO Auto-generated method stub
		return null;
	}
}

