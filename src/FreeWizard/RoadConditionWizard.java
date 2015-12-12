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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import FreeWizard.PolygonDatabaseWizard.FacePic;
import MapKernel.MapControl;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class RoadConditionWizard extends JFrame implements FreeWizardInterface{
	MapControl MainHandle;
	public RoadConditionWizard(){
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
	String KeyWordStr="";
	int RoadNum=0;
	int[] StatisticalValue=new int[10000];
	int[] RetrievedValue=new int[10000];
	int[] RoadReference=new int[10000];
	String[][] Result=new String[10000][5];
	String[] Title={"Sequence","Road Index","Information","Statistical Count","Retrieved Count"};
	JTable ResultTable;
	TableModel Content;
	JScrollPane Handle;
	public void DataClear(){
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		int i;
		for(i=0;i<LineDB.LineNum;i++){
			if(LineDB.LineHint[i].indexOf("[Info:Road]")==-1) break;
			StatisticalValue[i]=0;
			RetrievedValue[i]=0;
		}
		RoadNum=i;
	}
	int getValue(int k){
		if(Pic.StatisticalInfoSort.isSelected()){
			return -StatisticalValue[k];
		}else return -RetrievedValue[k];
	}
	public void sort(int l,int r){
		if(l>=r) return;
		int mid=RoadReference[(l+r)/2];
		int ll=l,rr=r,temp;
		while(ll<=rr){
			while(getValue(RoadReference[ll])<getValue(mid)) ll++;
			while(getValue(RoadReference[rr])>getValue(mid)) rr--;
			if(ll<=rr){
			temp=RoadReference[ll];
			RoadReference[ll]=RoadReference[rr];
			RoadReference[rr]=temp;
			ll++;
			rr--;
			}
		}
		sort(l,rr);
		sort(ll,r);
	}
	public void TransSignal(int RoadIndex){
		RetrievedValue[RoadIndex]++;
	}
	public void ShowChart(){
		for(int i=0;i<RoadNum;i++) RoadReference[i]=i;
		sort(0,RoadNum-1);
		Database.LineDataSet LineDB=MainHandle.getLineDatabase();
		DefaultTableModel tableModel = (DefaultTableModel) ResultTable.getModel();
		while(tableModel.getRowCount()!=0) tableModel.removeRow(0);
		for(int i=0;i<RoadNum;i++){
			if(LineDB.LineHint[RoadReference[i]].indexOf(KeyWordStr)==-1) continue;
			String[] Arr=new String[5];
			Arr[0]=Integer.toString(i+1);
			Arr[1]=Integer.toString(RoadReference[i]);
			Arr[2]=LineDB.LineHint[RoadReference[i]];
			Arr[3]=Integer.toString(StatisticalValue[RoadReference[i]]);
			Arr[4]=Integer.toString(RetrievedValue[RoadReference[i]]);
			tableModel.addRow(Arr);
		}
	}
	public void UpdateChart(){
		for(int i=0;i<RoadNum;i++){
			StatisticalValue[i]+=RetrievedValue[i];
		}
	}
		FacePic Pic;
		class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener{
		JButton Hide,Reset;
		JLabel l0,l1;
		JTextField KeyWord;
		JCheckBox ContemporaryInfoSort,StatisticalInfoSort;
		ButtonGroup Group;
		boolean[] Hit=new boolean[10000];
		public FacePic(){
			ResultTable=new JTable();
			setBounds(0,0,600,435);
			Hide=new JButton("Back");
			Hide.addActionListener(this);
			l1=new JLabel("KeyWord");
			KeyWord=new JTextField(10);
			Reset=new JButton("Reset");
			Reset.addActionListener(this);
			l1.setForeground(Color.black);
			l0=new JLabel("Road Crowdedness Condition Statistical Analysis");
			l0.setForeground(Color.black);
			l0.setFont(new Font("华文新魏",Font.BOLD,25));
			KeyWord.addActionListener(this);
			
			ButtonGroup Group=new ButtonGroup();
			ContemporaryInfoSort=new JCheckBox("Retrieved Sort");
			StatisticalInfoSort=new JCheckBox("Statistical Sort");
			ContemporaryInfoSort.setForeground(Color.black);
			ContemporaryInfoSort.setOpaque(false);
			StatisticalInfoSort.setForeground(Color.black);
			StatisticalInfoSort.setOpaque(false);
			Group.add(ContemporaryInfoSort);
			Group.add(StatisticalInfoSort);
			StatisticalInfoSort.setSelected(true);
			
			add(l0);
			add(l1);
			add(KeyWord);
			add(ContemporaryInfoSort);
			add(StatisticalInfoSort);
			add(Reset);
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
			Handle.setPreferredSize(new Dimension(580,350));
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		public void paintComponent(Graphics g){
			Toolkit kit=getToolkit();
			Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("BackGround17.jpg"));
			g.drawImage(img,0,0,600,435,this);
		}
		public void actionPerformed(ActionEvent e){
			if(!MainHandle.IsLineLoaded()){
				submerge();
				return;
			}
			if(e.getSource()==Hide){
				submerge();
			}else if(e.getSource()==Reset){
				DataClear();
				ShowChart();
			}else if(e.getSource()==KeyWord){
				KeyWordStr=KeyWord.getText();
				ShowChart();
			}
		}
		public void mouseDragged(MouseEvent e) {
			Move(e.getX()-PressedX,e.getY()-PressedY);
		}
		public void mouseMoved(MouseEvent e) {
		}
		public void mouseClicked(MouseEvent e) {
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
	@Override
	public void emerge() {
		// TODO Auto-generated method stub
		/*
		if(!(MainHandle.getNowPanel() instanceof ExtendedToolPane.AutoDrivePaneClass)) return;
		ExtendedToolPane.AutoDrivePaneClass AutoDrivePane=(ExtendedToolPane.AutoDrivePaneClass)MainHandle.getNowPanel();
		if(!AutoDrivePane.IsDrive) return;
		*/
		Pic.KeyWord.setText(KeyWordStr);
		setVisible(true);
	}
	@Override
	public void submerge() {
		// TODO Auto-generated method stub
		setVisible(false);
	}
	@Override
	public void setHandle(MapControl MainHandle) {
		// TODO Auto-generated method stub
		this.MainHandle=MainHandle;
	}
	@Override
	public boolean IsEmerge() {
		// TODO Auto-generated method stub
		return this.isVisible();
	}
	
}
