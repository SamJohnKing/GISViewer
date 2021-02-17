package FreeWizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;

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

import MapKernel.MapControl;
public class PolygonDatabaseWizard extends JFrame  implements FreeWizardInterface{
	
		FacePic Pic;
		class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener{
		JButton Hide,Query,DeleteRow,Delete,UpdateRow,Update,MoreInfo,Locate;
		JLabel l0,l1,l2;
		JTextField KeyWordA,KeyWordB;
		String[][] Result=new String[Math.min(100000,Database.PolygonDataSet.PolygonMaxNum)][3];
		String[] Title={MapKernel.MapWizard.LanguageDic.GetWords("序号"),
				MapKernel.MapWizard.LanguageDic.GetWords("显示"),
				MapKernel.MapWizard.LanguageDic.GetWords("备注")};
		JTable ResultTable;
		TableModel Content;
		JScrollPane Handle;
		int[] Hit=new int[Math.min(100000,Database.PolygonDataSet.PolygonMaxNum)];
		public FacePic(){
			ResultTable=new JTable();
			setBounds(0,0,600,435);
			Hide=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("返回"));
			Hide.addActionListener(this);
			l1=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("关键字A"));
			KeyWordA=new JTextField(10);
			l2=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("关键字B"));
			KeyWordB=new JTextField(10);
			Query=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("查询"));
			l1.setForeground(Color.orange);
			l2.setForeground(Color.orange);
			l0=new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("【二维多边形地理区域数据库检索视窗】"));
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
			DeleteRow=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("删除所在行"));
			Delete=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("删除全部行"));
			UpdateRow=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("写回所在行"));
			Update=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("写回全部行"));
			MoreInfo=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("配置"));
			Locate=new JButton(MapKernel.MapWizard.LanguageDic.GetWords("定位"));
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
			Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("Metal.jpg"));
			g.drawImage(img,0,0,this.getWidth(),this.getHeight(),this);
		}
		public void ProcessQuery(){
			Database.PolygonDataSet TempDatabase=MainHandle.getPolygonDatabase();
			int HitNum=0;
			for(int i=0;i<TempDatabase.PolygonNum;i++){
				if(KeyWordA.getText()!=""){
					if(TempDatabase.PolygonHint[i].indexOf(KeyWordA.getText())==-1)
						continue;
				}
				if(KeyWordB.getText()!=""){
					if(TempDatabase.PolygonHint[i].indexOf(KeyWordB.getText())==-1)
						continue;
				}
				if(HitNum==Math.min(100000,Database.PolygonDataSet.PolygonMaxNum)){
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("结果过多只返回前一部分"));
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
				JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("对不起没要您要的结果"),
						MapKernel.MapWizard.LanguageDic.GetWords("未命中"),JOptionPane.WARNING_MESSAGE);
				return;
			}
			for(int i=0;i<HitNum;i++){
				String[] temp=new String[3];
				temp[0]=Integer.toString(Hit[i]);
				temp[1]=Integer.toString(TempDatabase.PolygonVisible[Hit[i]]);
				temp[2]=TempDatabase.PolygonHint[Hit[i]];
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
			if(!MainHandle.IsPolygonLoaded()){
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
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
							MapKernel.MapWizard.LanguageDic.GetWords("更改内容时不可编辑单元格"),JOptionPane.WARNING_MESSAGE);
					return;
				}
				int selectRows=ResultTable.getSelectedRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
				if(selectRows==1){
				  int selectedRowIndex = ResultTable.getSelectedRow();
				  String str=(String)tableModel.getValueAt(selectedRowIndex,0);
				  MainHandle.getPolygonDatabase().DatabaseDelete(Integer.parseInt(str));
				  ProcessQuery();
				}else{
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("请您选中一行"),
							MapKernel.MapWizard.LanguageDic.GetWords("选中行异常"),JOptionPane.WARNING_MESSAGE);
					return;
				}
			}else if(e.getSource()==Delete){
				if(ResultTable.getCellEditor()!=null){
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
							MapKernel.MapWizard.LanguageDic.GetWords("更改内容时不可编辑单元格"),JOptionPane.WARNING_MESSAGE);
					return;
				}
				int RowCount=ResultTable.getRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
				for(int i=RowCount-1;i>=0;i--){
					String str=(String)tableModel.getValueAt(i,0);
					MainHandle.getPolygonDatabase().DatabaseRemove(Integer.parseInt(str));
				}
				MainHandle.getPolygonDatabase().DatabaseResize();
				ProcessQuery();
			}else if(e.getSource()==UpdateRow){
				if(ResultTable.getCellEditor()!=null){
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
							MapKernel.MapWizard.LanguageDic.GetWords("更改内容时不可编辑单元格"),JOptionPane.WARNING_MESSAGE);
					return;
				}
				int selectRows=ResultTable.getSelectedRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
				if(selectRows==1){
				  int selectedRowIndex = ResultTable.getSelectedRow();
				  try{
				  String str=(String)tableModel.getValueAt(selectedRowIndex,0);
				  MainHandle.getPolygonDatabase().update(str,
						  					 (String)tableModel.getValueAt(selectedRowIndex,1),
						  					(String)tableModel.getValueAt(selectedRowIndex,2));
				  }catch(Exception ex){
					  JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("数据库检查到异常格式"),
							  MapKernel.MapWizard.LanguageDic.GetWords("数据格式异常"),JOptionPane.WARNING_MESSAGE);
				  }
				  ProcessQuery();
				}else{
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("请您选中一行"),
							MapKernel.MapWizard.LanguageDic.GetWords("选中行异常"),JOptionPane.WARNING_MESSAGE);
					return;
				}
			}else if(e.getSource()==Update){
				if(ResultTable.getCellEditor()!=null){
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
							MapKernel.MapWizard.LanguageDic.GetWords("更改内容时不可编辑单元格"),JOptionPane.WARNING_MESSAGE);
					return;
				}
				int RowCount=ResultTable.getRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
				for(int i=0;i<RowCount;i++){
				  String str=(String)tableModel.getValueAt(i,0);
				  try{
					  MainHandle.getPolygonDatabase().update(str,
						  					 (String)tableModel.getValueAt(i,1),
						  					(String)tableModel.getValueAt(i,2));
				  }catch(Exception ex){
					  JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("数据库检查到异常格式"),
							  MapKernel.MapWizard.LanguageDic.GetWords("数据格式异常"),JOptionPane.WARNING_MESSAGE);
				  }
				}
				ProcessQuery();
			}else if(e.getSource()==MoreInfo){
				int selectRows=ResultTable.getSelectedRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
				if(selectRows==1){
					int selectedRowIndex = ResultTable.getSelectedRow();
					MainHandle.getKernel().PolygonPreferenceView.Convey(Integer.parseInt((String)tableModel.getValueAt(selectedRowIndex,0)));
					MainHandle.getKernel().PolygonPreferenceView.emerge();
				}else{
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("请您选中一行"),
							MapKernel.MapWizard.LanguageDic.GetWords("选中行异常"),JOptionPane.WARNING_MESSAGE);
					return;
				}
			}else if(e.getSource()==Locate){
				int selectRows=ResultTable.getSelectedRowCount();
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
				if(selectRows==1){
					int selectedRowIndex = ResultTable.getSelectedRow();
					int k=Integer.parseInt((String)tableModel.getValueAt(selectedRowIndex,0));
					double midx=MainHandle.getPolygonDatabase().AllPointX[MainHandle.getPolygonDatabase().PolygonHead[k]]
							+MainHandle.getPolygonDatabase().dx[k];
					double midy=MainHandle.getPolygonDatabase().AllPointY[MainHandle.getPolygonDatabase().PolygonHead[k]]
							+MainHandle.getPolygonDatabase().dy[k];
					MainHandle.getKernel().Screen.MoveMiddle(midx,midy);
					MainHandle.ScreenFlush();
				}else{
					JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("请您选中一行"),
							MapKernel.MapWizard.LanguageDic.GetWords("选中行异常"),JOptionPane.WARNING_MESSAGE);
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
				if(MainHandle.getPolygonDatabase().PolygonHint[0].indexOf("[Info:No:")==-1){
					for(int i=0;i<MainHandle.getPolygonDatabase().PolygonNum;i++){
						MainHandle.getPolygonDatabase().PolygonHint[i]+="[Info:No:"+i+"]";
					}
				}else{
					int l,r;
					String s1,s2;
					for(int i=0;i<MainHandle.getPolygonDatabase().PolygonNum;i++){
						l=MainHandle.getPolygonDatabase().PolygonHint[i].indexOf("[Info:No:");
						if(l==-1) continue;
						r=MainHandle.getPolygonDatabase().PolygonHint[i].indexOf(']',l);
						if(r==-1) continue;
						s1=MainHandle.getPolygonDatabase().PolygonHint[i].substring(0,l);
						s2=MainHandle.getPolygonDatabase().PolygonHint[i].substring(r+1);
						MainHandle.getPolygonDatabase().PolygonHint[i]=s1+s2;
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
		public PolygonDatabaseWizard(){
			setVisible(false);
			//String os = System.getProperty("os.name");
			//if(os.toLowerCase().startsWith("win")) {
			//	setUndecorated(true);
			//} else {
				this.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						submerge();
					}
				});
			//}
			setBounds(0,0,610,470);
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
