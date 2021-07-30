package ExtendedToolPane;
import MapKernel.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;
public class PolygonAddPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	public String getString(){
		return "PolygonAddPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public PolygonAddPaneClass(){
		JLabel Title=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("区域新建工具栏"));
		Title.setFont(new Font("华文新魏",Font.BOLD,30));
		add(Title);
		add(ScreenLockButton);
		ScreenLockButton.addActionListener(this);
		add(ScreenUnLockButton);
		ScreenUnLockButton.addActionListener(this);
		SpecificProcess();
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("sky.jpg"));
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
			if(MainHandle.getPointCount()<3){
				JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("少于三个点不予提交"),
						MapKernel.MapWizard.LanguageDic.GetWords("拒绝提交"),JOptionPane.WARNING_MESSAGE);
				return;
			}
			String str;
			if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
				str=MainHandle.getPreference().CommonString.getText();
			else str=JOptionPane.showInputDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("输入地理区域标签"),
					MapKernel.MapWizard.LanguageDic.GetWords("确认提交"),JOptionPane.PLAIN_MESSAGE);
			if(str!=null){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("成功提交了")+"【"+str+"】");
				MainHandle.PolygonDatabaseAppend(str);
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
		ConfirmLink=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则开始连接多边形区域，取消则放弃"));
		ConfirmLink.setOpaque(false);
		ConfirmLink.addItemListener(this);
		add(ConfirmLink);
		ShowConsecutiveLink=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则开始显示多边形边界，取消则无边界"));
		ShowConsecutiveLink.setOpaque(false);;
		ShowConsecutiveLink.addItemListener(this);
		add(ShowConsecutiveLink);
		ShowHeadTailLink=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则开始自动闭合多边形，取消则不闭合"));
		ShowHeadTailLink.setOpaque(false);
		ShowHeadTailLink.addItemListener(this);
		add(ShowHeadTailLink);
		ShowPointHint=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则显示添加顺序，取消则不显示"));
		ShowPointHint.setOpaque(false);
		ShowPointHint.addItemListener(this);
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
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("多边形生成中"));
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("放弃了多边形生成"));
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(false);
			}
		}else if(e.getSource()==ShowConsecutiveLink){
			if(ShowConsecutiveLink.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("多边形显示边"));
				MainHandle.setPointConsecutiveLinkVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("多边形隐去边"));
				MainHandle.setPointConsecutiveLinkVisible(false);
			}
		}else if(e.getSource()==ShowHeadTailLink){
			if(ShowHeadTailLink.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("多边形自动闭合"));
				MainHandle.setPointHeadTailLinkVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("多边形不自动闭合"));
				MainHandle.setPointHeadTailLinkVisible(false);
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
		ShowHeadTailLink.setSelected(MainHandle.getPointHeadTailLinkVisible());
		ShowPointHint.setSelected(MainHandle.getPointHintVisible());
		//-----------------
	}
	public void convey(double x,double y){
		if(ConfirmLink.isSelected()){
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
			MainHandle.PointPush(x,y,"P"+MainHandle.getPointCount());
		}else MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("没有开始构建多边形，点击无效"));
	}
	public void convey(double x1,double y1,double x2,double y2){
		MainHandle.PointSelect(x1,y1,x2,y2);
		int n=JOptionPane.showConfirmDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("是则删除，否则区域截图生成"),
				MapKernel.MapWizard.LanguageDic.GetWords("确认删除"),JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION){
			MainHandle.PointSelectDelete();
			MainHandle.ResetPointHint("P");
		}else if(n==JOptionPane.NO_OPTION){
			MainHandle.getKernel().JPGOutput(x1,x2,y1,y2);
		}else{
			MainHandle.PointSelectCancel();
		}
	}
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		if(MainHandle.getPointCount()<3){
			JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("少于三个点不予提交"),
					MapKernel.MapWizard.LanguageDic.GetWords("拒绝提交"),JOptionPane.WARNING_MESSAGE);
			return;
		}
		String str;
		if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
			str=MainHandle.getPreference().CommonString.getText();
		else {
			MainHandle.getKernel().setVisible(true);
			str=JOptionPane.showInputDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("输入地理区域标签"),
					MapKernel.MapWizard.LanguageDic.GetWords("确认提交"),JOptionPane.PLAIN_MESSAGE);
		}
		if(str!=null){
			MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("成功提交了")+"【"+str+"】");
			MainHandle.PolygonDatabaseAppend(str);
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
