package ExtendedToolPane;
import MapKernel.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;
public class MapElementsEditorPaneClass extends ToolPanel implements ExtendedToolPaneInterface,ActionListener,ItemListener{
	MapControl MainHandle;
	double CursorLongitude,CursorLatitude;
	public String getString(){
		return "MapElementsEditorPane";
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public MapElementsEditorPaneClass(){
		JLabel Title=new JLabel("地图元素编辑面板");
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
		Image img=kit.getImage("BackGround34.jpg");
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
			confirm();
		}else if(e.getSource()==RollBack){
			rollback();
		}
	}
	//Specific Part--------------------------------------------
	JCheckBox ConfirmLink,ShowConsecutiveLink,ShowHeadTailLink,ShowPointHint;
	JCheckBox RemovePoint,RemoveLine,RemovePolygon;
	JButton CancelLastOne,CancelAll,Submit,RollBack;
	JRadioButton PointElement,LineElement,PolygonElement;
	public void SpecificProcess(){
		PointElement=new JRadioButton("选择添加地标点状地图元素");
		PointElement.setOpaque(false);
		PointElement.setSelected(true);
		LineElement=new JRadioButton("选择添加连通线状地图元素");
		LineElement.setOpaque(false);
		PolygonElement=new JRadioButton("选择添加多边形状地图元素");
		PolygonElement.setOpaque(false);
		ButtonGroup ElementSelect=new ButtonGroup();
		ElementSelect.add(PointElement);
		ElementSelect.add(LineElement);
		ElementSelect.add(PolygonElement);
		ConfirmLink=new JCheckBox("勾选则开始编辑，取消则不能编辑");
		ConfirmLink.setOpaque(false);
		ConfirmLink.addItemListener(this);
		add(ConfirmLink);
		ShowConsecutiveLink=new JCheckBox("勾选则开始点点连接，取消则不连接");
		ShowConsecutiveLink.setOpaque(false);;
		ShowConsecutiveLink.addItemListener(this);
		add(ShowConsecutiveLink);
		ShowHeadTailLink=new JCheckBox("勾选则开始首尾相连，取消则不相连");
		ShowHeadTailLink.setOpaque(false);
		ShowHeadTailLink.addItemListener(this);
		add(ShowHeadTailLink);
		ShowPointHint=new JCheckBox("勾选则显示添加顺序，取消则不显示");
		ShowPointHint.setOpaque(false);
		ShowPointHint.addItemListener(this);
		add(ShowPointHint);
		add(PointElement);
		add(LineElement);
		add(PolygonElement);
		RemovePoint=new JCheckBox("选中时删除地标点状数据");
		RemovePoint.setOpaque(false);
		add(RemovePoint);
		RemoveLine=new JCheckBox("选中时删除连接线状数据");
		RemoveLine.setOpaque(false);
		add(RemoveLine);
		RemovePolygon=new JCheckBox("选中时删除多边形状数据");
		RemovePolygon.setOpaque(false);
		add(RemovePolygon);
		CancelLastOne=new JButton("取消添加序列中最近点");
		CancelLastOne.addActionListener(this);
		add(CancelLastOne);
		CancelAll=new JButton("取消添加序列中全部点");
		CancelAll.addActionListener(this);
		add(CancelAll);
		Submit=new JButton("提交添加序列信息到数据库");
		Submit.addActionListener(this);
		add(Submit);
		RollBack=new JButton("从数据库撤销上一次的提交");
		RollBack.addActionListener(this);
		add(RollBack);
	}
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()==ConfirmLink){
			if(ConfirmLink.isSelected()){
				MainHandle.ChangeTitle("元素生成中");
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(true);
			}else{
				MainHandle.ChangeTitle("放弃了生成");
				MainHandle.PointEmpty();
				MainHandle.setPointVisible(false);
			}
		}else if(e.getSource()==ShowConsecutiveLink){
			if(ShowConsecutiveLink.isSelected()){
				MainHandle.ChangeTitle("显示连线");
				MainHandle.setPointConsecutiveLinkVisible(true);
			}else{
				MainHandle.ChangeTitle("隐去连线");
				MainHandle.setPointConsecutiveLinkVisible(false);
			}
		}else if(e.getSource()==ShowHeadTailLink){
			if(ShowHeadTailLink.isSelected()){
				MainHandle.ChangeTitle("首尾相连");
				MainHandle.setPointHeadTailLinkVisible(true);
			}else{
				MainHandle.ChangeTitle("首尾断开");
				MainHandle.setPointHeadTailLinkVisible(false);
			}
		}else if(e.getSource()==ShowPointHint){
			if(ShowPointHint.isSelected()){
				MainHandle.ChangeTitle("显示顺序");
				MainHandle.setPointHintVisible(true);
			}else{
				MainHandle.ChangeTitle("隐去顺序");
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
			MainHandle.PointPush(x,y,"E"+MainHandle.getPointCount());
		}else MainHandle.ChangeTitle("没有开始构建元素，点击无效");
	}
	public void convey(double x1,double y1,double x2,double y2){
		MainHandle.PointEmpty();
		MainHandle.PointPush(Math.min(x1,x2),Math.min(y1,y2));
		MainHandle.PointPush(Math.max(x1,x2),Math.min(y1,y2));
		MainHandle.PointPush(Math.max(x1,x2),Math.max(y1, y2));
		MainHandle.PointPush(Math.min(x1,x2),Math.max(y1, y2));
		MainHandle.PointPush(Math.min(x1,x2),Math.min(y1,y2));
		MainHandle.setPointConsecutiveLinkVisible(true);
		MainHandle.setPointVisible(true);
		MainHandle.setPointHeadTailLinkVisible(false);
		MainHandle.setPointHintVisible(false);
		//ACMSIGSPACIAL2014
		int n=JOptionPane.showConfirmDialog(null,"选择【是】删除，选择【否】撒点,【取消】则放弃","删除或者撒点",JOptionPane.YES_NO_CANCEL_OPTION);
		if(n==JOptionPane.YES_OPTION){
			if(RemovePoint.isSelected()&&(MainHandle.getPointDatabase().PointNum>0)){
				if(MainHandle.getPointDatabase().GetIndexPermission()!=null)
					MainHandle.getPointDatabase().GetIndexPermission().WriteBack();
				Database.PointDataSet PointDB=MainHandle.getPointDatabase();
				for(int ptr_i=0;ptr_i<PointDB.PointNum;ptr_i++){
					if((PointDB.AllPointX[ptr_i]-x1)*(PointDB.AllPointX[ptr_i]-x2)>=0) continue;
					if((PointDB.AllPointY[ptr_i]-y1)*(PointDB.AllPointY[ptr_i]-y2)>=0) continue;
					PointDB.DatabaseRemove(ptr_i);
				}
				PointDB.DatabaseResize();
				/*Use QuadTree To speed up
				Database.RTreeIndex PointRTree=new Database.RTreeIndex();
				PointRTree.IndexInit(MainHandle.getPointDatabase());
				PointRTree.Delete(x1, y1, x2, y2);
				PointRTree.WriteBack();
				MainHandle.getPointDatabase().SetIndexPermission(null);
				PointRTree=null;
				*/
			}
			if(RemoveLine.isSelected()&&(MainHandle.getLineDatabase().LineNum>0)){
				if(MainHandle.getLineDatabase().GetIndexPermission()!=null)
					MainHandle.getLineDatabase().GetIndexPermission().WriteBack();
				Database.RTreeIndex LineRTree=new Database.RTreeIndex();
				LineRTree.IndexInit(MainHandle.getLineDatabase());
				LineRTree.Delete(x1, y1, x2, y2);
				LineRTree.WriteBack();
				MainHandle.getLineDatabase().SetIndexPermission(null);
				LineRTree=null;
			}
			if(RemovePolygon.isSelected()&&(MainHandle.getPolygonDatabase().PolygonNum>0)){
				if(MainHandle.getPolygonDatabase().GetIndexPermission()!=null)
					MainHandle.getPolygonDatabase().GetIndexPermission().WriteBack();
				Database.RTreeIndex PolygonRTree=new Database.RTreeIndex();
				PolygonRTree.IndexInit(MainHandle.getPolygonDatabase());
				PolygonRTree.Delete(x1, y1, x2, y2);
				PolygonRTree.WriteBack();
				MainHandle.getPolygonDatabase().SetIndexPermission(null);
				PolygonRTree=null;
			}
		}else if(n==JOptionPane.NO_OPTION){
			String str=JOptionPane.showInputDialog(null,"输入随机点数量","随机点数量",JOptionPane.PLAIN_MESSAGE);
			int RandomCount=0;
			try{
				RandomCount=Integer.parseInt(str);
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,"输入格式有误");
				MainHandle.PointEmpty();
				MainHandle.setPointConsecutiveLinkVisible(false);
				MainHandle.setPointVisible(false);
				MainHandle.setPointHeadTailLinkVisible(false);
				MainHandle.setPointHintVisible(false);
				MainHandle.ScreenFlush();
				emerge();
				System.gc();
				return;
			}
			if(RandomCount<=0){
				JOptionPane.showMessageDialog(null,"数量输入有误");
				MainHandle.PointEmpty();
				MainHandle.setPointConsecutiveLinkVisible(false);
				MainHandle.setPointVisible(false);
				MainHandle.setPointHeadTailLinkVisible(false);
				MainHandle.setPointHintVisible(false);
				MainHandle.ScreenFlush();
				emerge();
				System.gc();
				return;
			}
			java.util.Calendar seed=java.util.Calendar.getInstance();
			java.util.Random rd=new java.util.Random(seed.getTimeInMillis()%10000);
			if(MainHandle.getLineDatabase().GetIndexPermission()!=null)
				MainHandle.getLineDatabase().GetIndexPermission().WriteBack();
			Database.RTreeIndex LineRTree=new Database.RTreeIndex();
			LineRTree.IndexInit(MainHandle.getLineDatabase());
			double RX,RY;
			double XStep=(MainHandle.getLongitudeEnd()-MainHandle.getLongitudeStart())/10000;
			double YStep=(MainHandle.getLatitudeEnd()-MainHandle.getLatitudeStart())/10000;
			ArrayList<Integer> result;
			int p=-1,q=-1;
			while(RandomCount!=0){
				RX=Math.min(x1,x2)+Math.abs(x1-x2)*rd.nextDouble();
				RY=Math.min(y1,y2)+Math.abs(y1-y2)*rd.nextDouble();
				result=LineRTree.Search(RX-XStep,RY-YStep,RX+XStep,RY+YStep);
				if(!result.isEmpty()){
					for(int i:result){
						p=MainHandle.getLineDatabase().LineHead[i];
						while(p!=-1){
							q=MainHandle.getLineDatabase().AllPointNext[p];
							if(q==-1) break;
							double PX=MainHandle.getLineDatabase().AllPointX[p];
							double PY=MainHandle.getLineDatabase().AllPointY[p];
							double QX=MainHandle.getLineDatabase().AllPointX[q];
							double QY=MainHandle.getLineDatabase().AllPointY[q];
							double a=Math.sqrt((PX-RX)*(PX-RX)+(PY-RY)*(PY-RY));
							double b=Math.sqrt((QX-RX)*(QX-RX)+(QY-RY)*(QY-RY));
							double c=Math.sqrt((PX-QX)*(PX-QX)+(PY-QY)*(PY-QY));
							if((RX-PX)*(RX-QX)<0)
								if((RY-PY)*(RY-QY)<0){
									if(Math.acos((a*a+b*b-c*c)/(2*a*b))<-Math.PI*0.95){
										break;
									}
								}
							p=q;
						}
						if(q!=-1) break;
					}
					if(q!=-1) continue;
				}
				MainHandle.getPointDatabase().add(RX,RY,"[Info:Random][Title:]");
				RandomCount--;
			}
			MainHandle.getLineDatabase().SetIndexPermission(null);
			LineRTree=null;
		}
		MainHandle.PointEmpty();
		MainHandle.setPointConsecutiveLinkVisible(false);
		MainHandle.setPointVisible(false);
		MainHandle.setPointHeadTailLinkVisible(false);
		MainHandle.setPointHintVisible(false);
		MainHandle.ScreenFlush();
		emerge();
		System.gc();
	}
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		if(PolygonElement.isSelected()){
			if(MainHandle.getPointCount()<3){
				JOptionPane.showMessageDialog(null,"少于三个点不予提交","拒绝提交",JOptionPane.WARNING_MESSAGE);
				return;
			}
			String str;
			if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
				str=MainHandle.getPreference().CommonString.getText();
			else str=JOptionPane.showInputDialog(null,"输入地理区域标签","确认提交",JOptionPane.PLAIN_MESSAGE);
			if(str!=null){
				MainHandle.ChangeTitle("成功提交了【"+str+"】");
				MainHandle.PolygonDatabaseAppend(str);
				MainHandle.PointEmpty();
			}else{
				MainHandle.ChangeTitle("放弃提交,仍然为您保留未提交数据");
			}
		}else if(LineElement.isSelected()){
			if(MainHandle.getPointCount()<2){
				JOptionPane.showMessageDialog(null,"少于两个点不予提交","拒绝提交",JOptionPane.WARNING_MESSAGE);
				return;
			}
			String str;
			if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
				str=MainHandle.getPreference().CommonString.getText();
			else str=JOptionPane.showInputDialog(null,"输入地理线路标签","确认提交",JOptionPane.PLAIN_MESSAGE);
			if(str!=null){
				MainHandle.ChangeTitle("成功提交了【"+str+"】");
				MainHandle.LineDatabaseAppend(str);
				MainHandle.PointEmpty();
			}else{
				MainHandle.ChangeTitle("放弃提交,仍然为您保留未提交数据");
			}
		}else{
			if(MainHandle.getPointCount()<1){
				JOptionPane.showMessageDialog(null,"不允许空提交","拒绝提交",JOptionPane.WARNING_MESSAGE);
				return;
			}
			String str;
			if(MainHandle.getPreference().ValidizeCommonString.isSelected()) 
				str=MainHandle.getPreference().CommonString.getText();
			else str=JOptionPane.showInputDialog(null,"输入地理线路标签","确认提交",JOptionPane.PLAIN_MESSAGE);
			if(str!=null){
				MainHandle.ChangeTitle("成功提交了【"+str+"】");
				MainHandle.PointDatabaseAppend(str);
				MainHandle.PointEmpty();
			}else{
				MainHandle.ChangeTitle("放弃提交,仍然为您保留未提交数据");
			}
		}
	}
	public void rollback(){
		if(PolygonElement.isSelected()){
			MainHandle.getPolygonDatabase().DatabaseDelete(MainHandle.getPolygonDatabase().PolygonNum-1);
			MainHandle.ScreenFlush();
		}else if(LineElement.isSelected()){
			MainHandle.getLineDatabase().DatabaseDelete(MainHandle.getLineDatabase().LineNum-1);
			MainHandle.ScreenFlush();
		}else{
			MainHandle.getPointDatabase().DatabaseDelete(MainHandle.getPointDatabase().PointNum-1);
			MainHandle.ScreenFlush();
		}
	}
	@Override
	public String GetSocketResult(String SocketQuery) {
		// TODO Auto-generated method stub
		return null;
	}
}
