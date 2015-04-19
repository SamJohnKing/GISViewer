package FreeWizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import FreeWizard.PointDatabaseWizard.FacePic;
import MapKernel.MapControl;

public class PointDatabaseWizard extends JFrame  implements FreeWizardInterface{
	
	FacePic Pic;
	class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener{
	JButton Hide,Query,DeleteRow,Delete,UpdateRow,Update,MoreInfo,Locate;
	JLabel l0,l1,l2;
	JTextField KeyWordA,KeyWordB;
	String[][] Result=new String[Math.min(100000,Database.PointDataSet.PointMaxNum)][3];
	String[] Title={"序号","显示","备注"};
	JTable ResultTable;
	TableModel Content;
	JScrollPane Handle;
	int[] Hit=new int[Math.min(100000,Database.PointDataSet.PointMaxNum)];
	public FacePic(){
		ResultTable=new JTable();
		setBounds(0,0,600,435);
		Hide=new JButton("返回");
		Hide.addActionListener(this);
		l1=new JLabel("关键字A");
		KeyWordA=new JTextField(10);
		l2=new JLabel("关键字B");
		KeyWordB=new JTextField(10);
		Query=new JButton("查询");
		l1.setForeground(Color.orange);
		l2.setForeground(Color.orange);
		l0=new JLabel("【经纬度平面空间兴趣点数据库检索】");
		l0.setForeground(Color.orange);
		l0.setFont(new Font("华文新魏",Font.BOLD,30));
		
		KeyWordA.addActionListener(this);
		KeyWordB.addActionListener(this);
		
		add(l0);
		add(l1);
		add(KeyWordA);
		add(l2);
		add(KeyWordB);
		add(Query);
		add(Hide);
		DefaultTableModel model = new DefaultTableModel(Result,Title) {
			  public boolean isCellEditable(int row, int column) {
				  if(column==0) return false;
				  else return true;
			  }
			};
		ResultTable=new JTable(model);
		Content=ResultTable.getModel();
		Handle=new JScrollPane(ResultTable);
		add(Handle);
		Handle.setVisible(false);
		Handle.setPreferredSize(new Dimension(550,320));
		addMouseListener(this);
		addMouseMotionListener(this);
		Query.addActionListener(this);
		DeleteRow=new JButton("删除所在行");
		Delete=new JButton("删除全部行");
		UpdateRow=new JButton("写回所在行");
		Update=new JButton("写回全部行");
		MoreInfo=new JButton("配置");
		Locate=new JButton("定位");
		DeleteRow.setVisible(false);
		Delete.setVisible(false);
		UpdateRow.setVisible(false);
		Update.setVisible(false);
		MoreInfo.setVisible(false);
		Locate.setVisible(false);
		add(DeleteRow);
		add(Delete);
		add(UpdateRow);
		add(Update);
		add(MoreInfo);
		add(Locate);
		DeleteRow.addActionListener(this);
		Delete.addActionListener(this);
		UpdateRow.addActionListener(this);
		Update.addActionListener(this);
		MoreInfo.addActionListener(this);
		Locate.addActionListener(this);
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("Metal.jpg");
		g.drawImage(img,0,0,600,435,this);
	}
	public void ProcessQuery(){
		Database.PointDataSet TempDatabase=MainHandle.getPointDatabase();
		int HitNum=0;
		for(int i=0;i<TempDatabase.PointNum;i++){
			if(KeyWordA.getText()!=""){
				if(TempDatabase.PointHint[i].indexOf(KeyWordA.getText())==-1)
					continue;
			}
			if(KeyWordB.getText()!=""){
				if(TempDatabase.PointHint[i].indexOf(KeyWordB.getText())==-1)
					continue;
			}
			if(HitNum==Math.min(100000,Database.PointDataSet.PointMaxNum)){
				JOptionPane.showMessageDialog(null,"结果过多只返回前一部分");
				break;
			}
			Hit[HitNum]=i;
			HitNum++;
		}
		DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
		while(tableModel.getRowCount()!=0) tableModel.removeRow(0);
		TableColumn Column = ResultTable.getColumnModel().getColumn(0);
		Column.setPreferredWidth(3);
		if(HitNum==0){
			JOptionPane.showMessageDialog(null,"对不起没要您要的结果","未命中",JOptionPane.WARNING_MESSAGE);
			return;
		}
		for(int i=0;i<HitNum;i++){
			String[] temp=new String[3];
			temp[0]=Integer.toString(Hit[i]);
			temp[1]=Integer.toString(TempDatabase.PointVisible[Hit[i]]);
			temp[2]=TempDatabase.PointHint[Hit[i]];
			tableModel.addRow(temp);
		}
		ResultTable.validate();
		Handle.setVisible(true);
		DeleteRow.setVisible(true);
		Delete.setVisible(true);
		UpdateRow.setVisible(true);
		Update.setVisible(true);
		MoreInfo.setVisible(true);
		Locate.setVisible(true);
		validate();
	}
	public void actionPerformed(ActionEvent e){
		if(!MainHandle.IsPointLoaded()){
			submerge();
			return;
		}
		if(e.getSource()==Hide){
			submerge();
		}else if(e.getSource()==Query){
			if(ResultTable.getCellEditor()!=null){
				ResultTable.setCellEditor(null);
			}
			ProcessQuery();
		}else if(e.getSource()==DeleteRow){
			if(ResultTable.getCellEditor()!=null){
				JOptionPane.showMessageDialog(null,"您正在编辑单元格,为了数据安全请提前确认","更改内容时不可编辑单元格",JOptionPane.WARNING_MESSAGE);
				return;
			}
			int selectRows=ResultTable.getSelectedRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			if(selectRows==1){
			  int selectedRowIndex = ResultTable.getSelectedRow();
			  String str=(String)tableModel.getValueAt(selectedRowIndex,0);
			  MainHandle.getPointDatabase().DatabaseDelete(Integer.parseInt(str));
			  ProcessQuery();
			}else{
				JOptionPane.showMessageDialog(null,"请您选中一行","选中行异常",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}else if(e.getSource()==Delete){
			if(ResultTable.getCellEditor()!=null){
				JOptionPane.showMessageDialog(null,"您正在编辑单元格,为了数据安全请提前确认","更改内容时不可编辑单元格",JOptionPane.WARNING_MESSAGE);
				return;
			}
			int RowCount=ResultTable.getRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			for(int i=RowCount-1;i>=0;i--){
				String str=(String)tableModel.getValueAt(i,0);
				MainHandle.getPointDatabase().DatabaseRemove(Integer.parseInt(str));
			}
			MainHandle.getPointDatabase().DatabaseResize();
			ProcessQuery();
		}else if(e.getSource()==UpdateRow){
			if(ResultTable.getCellEditor()!=null){
				JOptionPane.showMessageDialog(null,"您正在编辑单元格,为了数据安全请提前确认","更改内容时不可编辑单元格",JOptionPane.WARNING_MESSAGE);
				return;
			}
			int selectRows=ResultTable.getSelectedRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			if(selectRows==1){
			  int selectedRowIndex = ResultTable.getSelectedRow();
			  try{
			  String str=(String)tableModel.getValueAt(selectedRowIndex,0);
			  MainHandle.getPointDatabase().update(str,
					  					 (String)tableModel.getValueAt(selectedRowIndex,1),
					  					(String)tableModel.getValueAt(selectedRowIndex,2));
			  }catch(Exception ex){
				  JOptionPane.showMessageDialog(null,"数据库检查到异常格式","数据格式异常",JOptionPane.WARNING_MESSAGE);
			  }
			  ProcessQuery();
			}else{
				JOptionPane.showMessageDialog(null,"请您选中一行","选中行异常",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}else if(e.getSource()==Update){
			if(ResultTable.getCellEditor()!=null){
				JOptionPane.showMessageDialog(null,"您正在编辑单元格,为了数据安全请提前确认","更改内容时不可编辑单元格",JOptionPane.WARNING_MESSAGE);
				return;
			}
			int RowCount=ResultTable.getRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			for(int i=0;i<RowCount;i++){
			  String str=(String)tableModel.getValueAt(i,0);
			  try{
				  MainHandle.getPointDatabase().update(str,
					  					 (String)tableModel.getValueAt(i,1),
					  					(String)tableModel.getValueAt(i,2));
			  }catch(Exception ex){
				  JOptionPane.showMessageDialog(null,"数据库检查到异常格式","数据格式异常",JOptionPane.WARNING_MESSAGE);
			  }
			}
			ProcessQuery();
		}else if(e.getSource()==MoreInfo){
			int selectRows=ResultTable.getSelectedRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			if(selectRows==1){
				int selectedRowIndex = ResultTable.getSelectedRow();
				MainHandle.getKernel().PointPreferenceView.Convey(Integer.parseInt((String)tableModel.getValueAt(selectedRowIndex,0)));
				MainHandle.getKernel().PointPreferenceView.emerge();
			}else{
				JOptionPane.showMessageDialog(null,"请您选中一行","选中行异常",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}else if(e.getSource()==Locate){
			int selectRows=ResultTable.getSelectedRowCount();
			DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
			if(selectRows==1){
				int selectedRowIndex = ResultTable.getSelectedRow();
				int k=Integer.parseInt((String)tableModel.getValueAt(selectedRowIndex,0));
				double midx=MainHandle.getPointDatabase().AllPointX[k];
				double midy=MainHandle.getPointDatabase().AllPointY[k];
				MainHandle.getKernel().Screen.MoveMiddle(midx,midy);
				MainHandle.ScreenFlush();
			}else{
				JOptionPane.showMessageDialog(null,"请您选中一行","选中行异常",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}else if((e.getSource()==KeyWordA)||(e.getSource()==KeyWordB)){
			if(ResultTable.getCellEditor()!=null){
				ResultTable.setCellEditor(null);
			}
			ProcessQuery();
		}
		MainHandle.ScreenFlush();
	}
	public void mouseDragged(MouseEvent e) {
		Move(e.getX()-PressedX,e.getY()-PressedY);
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()>=2){
			if(MainHandle.getPointDatabase().PointHint[0].indexOf("[Info:No:")==-1){
				for(int i=0;i<MainHandle.getPointDatabase().PointNum;i++){
					MainHandle.getPointDatabase().PointHint[i]+="[Info:No:"+i+"]";
				}
			}else{
				int l,r;
				String s1,s2;
				for(int i=0;i<MainHandle.getPointDatabase().PointNum;i++){
					l=MainHandle.getPointDatabase().PointHint[i].indexOf("[Info:No:");
					if(l==-1) continue;
					r=MainHandle.getPointDatabase().PointHint[i].indexOf(']',l);
					if(r==-1) continue;
					s1=MainHandle.getPointDatabase().PointHint[i].substring(0,l);
					s2=MainHandle.getPointDatabase().PointHint[i].substring(r+1);
					MainHandle.getPointDatabase().PointHint[i]=s1+s2;
				}
			}
			ProcessQuery();
			System.gc();
		}
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
	}
	public PointDatabaseWizard(){
		setVisible(false);
		setUndecorated(true);
		setBounds(0,0,600,435);
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
	public void emerge() {
		// TODO Auto-generated method stub
		MainHandle.ForbidOperate();
		this.setVisible(true);
		Pic.ProcessQuery();
	}
	@Override
	public void submerge() {
		// TODO Auto-generated method stub
		MainHandle.AllowOperate();
		this.setVisible(false);
	}
	MapControl MainHandle;
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public boolean IsEmerge(){
		return this.isVisible();
	}
}

