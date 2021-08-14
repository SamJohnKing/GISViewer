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
import java.util.Vector;

import javax.swing.*;

import LWJGLPackage.OriginalOpenGLWizard;
import MapKernel.MapControl;
import MapKernel.MapWizard;
import MapKernel.ToolPanel;
import SecondaryScreen.SwtHtmlBrowser;

public class PointAddPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	public String getString(){
		return "PointAddPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public PointAddPaneClass(){
		JLabel Title=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("兴趣点批量插入面板"));
		Title.setFont(new Font("华文新魏",Font.BOLD,28));
		Title.setForeground(Color.red);
		add(Title);
		add(ScreenLockButton);
		ScreenLockButton.addActionListener(this);
		add(ScreenUnLockButton);
		ScreenUnLockButton.addActionListener(this);
		SpecificProcess();
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("BackGround18.jpg"));
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
			if(MainHandle.getPointCount()<1){
				JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("不允许空提交"),
						MapKernel.MapWizard.LanguageDic.GetWords("拒绝提交"),JOptionPane.WARNING_MESSAGE);
				return;
			}
			String str;
			if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
				str=MainHandle.getPreference().CommonString.getText();
			else str=JOptionPane.showInputDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("输入地理兴趣点标签"),
					MapKernel.MapWizard.LanguageDic.GetWords("确认提交"),JOptionPane.PLAIN_MESSAGE);
			if(str!=null){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("成功提交了")+"【"+str+"】");
				MainHandle.PointDatabaseAppend(str);
				MainHandle.PointEmpty();
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("放弃提交,仍然为您保留未提交数据"));
			}
		}
	}
	//Specific Part--------------------------------------------
	JCheckBox ConfirmLink,ShowPointHint,QueryAllow;
	JButton CancelLastOne,CancelAll,Submit;
	public void SpecificProcess(){
		ConfirmLink=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则开始创建兴趣点，取消则放弃"));
		ConfirmLink.setOpaque(false);
		ConfirmLink.addItemListener(this);
		ConfirmLink.setForeground(Color.red);
		add(ConfirmLink);
		ShowPointHint=new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("勾选则显示添加顺序，取消则不显示"));
		ShowPointHint.setOpaque(false);
		ShowPointHint.addItemListener(this);
		ShowPointHint.setForeground(Color.red);
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
		QueryAllow = new JCheckBox(MapWizard.LanguageDic.GetWords("允许左键点击询问周围数据元素"));
		QueryAllow.setOpaque(false);
		QueryAllow.setForeground(Color.red);
		QueryAllow.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(QueryAllow.isSelected()) ConfirmLink.setSelected(false);
			}
		});
		add(QueryAllow);
	}
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()==ConfirmLink){
			if(ConfirmLink.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("兴趣点批量插入中"));
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(true);
				QueryAllow.setSelected(false);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("放弃了兴趣点批量插入"));
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(false);
			}
		}else if(e.getSource()==ShowPointHint){
			if(ShowPointHint.isSelected()){
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("显示创建顺序"));
				MainHandle.setPointHintVisible(true);
			}else{
				MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("不显示创建顺序"));
				MainHandle.setPointHintVisible(false);
			}
		}
	}
	public void emerge(){
		//-----------------
		ConfirmLink.setSelected(MainHandle.getPointVisible());
		ShowPointHint.setSelected(MainHandle.getPointHintVisible());
		//-----------------
	}
	public void convey(double x,double y){
		if(ConfirmLink.isSelected()){
			MainHandle.PointPush(x,y,"T"+MainHandle.getPointCount());
		} else if(QueryAllow.isSelected()){
			double xscale = MainHandle.getKernel().Screen.LongitudeScale;
			xscale = OriginalOpenGLWizard.SingleItem != null ? Math.min(xscale, OriginalOpenGLWizard.SingleItem.LongitudeScale) : xscale;
			xscale = SwtHtmlBrowser.SingleItemThread != null ? Math.min(xscale, SwtHtmlBrowser.GetLongitudeEnd() - SwtHtmlBrowser.GetLongitudeStart()) : xscale;
			double yscale = MainHandle.getKernel().Screen.LatitudeScale;
			yscale = OriginalOpenGLWizard.SingleItem != null ? Math.min(yscale, OriginalOpenGLWizard.SingleItem.LatitudeScale) : yscale;
			yscale = SwtHtmlBrowser.SingleItemThread != null ? Math.min(yscale, SwtHtmlBrowser.GetLatitudeEnd() - SwtHtmlBrowser.GetLatitudeStart()) : yscale;
			xscale = xscale/100;
			yscale = yscale/100;
			Vector res;
			System.out.println("QueryRegion:\t ( " + (x - xscale) + " , " + (y - yscale) + " , " + (x + xscale) + " , " + (y + yscale) + " )");
			System.out.println(res = MainHandle.getKernel().PointDatabase.KeyValueQuery(x - xscale, y - yscale, x + xscale, y + yscale, null, null, null, null, null));
			MainHandle.getKernel().setVisible(true);
			String resstr = res.toString();
			JFrame jf = new JFrame("QueryRegion:\t ( " + (x - xscale) + " , " + (y - yscale) + " , " + (x + xscale) + " , " + (y + yscale) + " )");
			java.awt.Container contentPane = jf.getContentPane();
			contentPane.setLayout(new java.awt.BorderLayout());
			JTextArea jta = new JTextArea(resstr);
			jta.setLineWrap(true);// 激活自动换行功能
			jta.setWrapStyleWord(true);// 激活断行不断字功能
			JScrollPane jscrollPane = new JScrollPane(jta);
			contentPane.add(jscrollPane, java.awt.BorderLayout.CENTER);
			jf.setSize(800, 600);
			jf.setLocationRelativeTo(null);
			jf.setVisible(true);
		} else MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("没有开始插入兴趣点，点击无效"));
	}
	public void convey(double x1,double y1,double x2,double y2){
		MainHandle.PointSelect(x1,y1,x2,y2);
		int n=JOptionPane.showConfirmDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("是则编辑点删除，否则区域截图生成"),
				MapKernel.MapWizard.LanguageDic.GetWords("确认删除"),JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION){
			MainHandle.PointSelectDelete();
			MainHandle.ResetPointHint("T");
		}else if(n==JOptionPane.NO_OPTION){
			MainHandle.getKernel().JPGOutput(x1,x2,y1,y2);
		}else{
			MainHandle.PointSelectCancel();
		}
	}
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		if(MainHandle.getPointCount()<1){
			MainHandle.getKernel().setVisible(true);
			JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("不允许空提交"),
					MapKernel.MapWizard.LanguageDic.GetWords("拒绝提交"),JOptionPane.WARNING_MESSAGE);
			return;
		}
		String str;
		if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
			str=MainHandle.getPreference().CommonString.getText();
		else {
			MainHandle.getKernel().setVisible(true);
			str=JOptionPane.showInputDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("输入地理兴趣点标签"),
					MapKernel.MapWizard.LanguageDic.GetWords("确认提交"),JOptionPane.PLAIN_MESSAGE);
		}
		if(str!=null){
			MainHandle.ChangeTitle(MapKernel.MapWizard.LanguageDic.GetWords("成功提交了")+"【"+str+"】");
			MainHandle.PointDatabaseAppend(str);
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


