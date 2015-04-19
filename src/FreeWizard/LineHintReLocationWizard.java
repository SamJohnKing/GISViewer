package FreeWizard;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import FreeWizard.LineHintReLocationWizard.FacePic;
import FreeWizard.LineHintReLocationWizard.MovePane;
import MapKernel.MapControl;

public class LineHintReLocationWizard extends JFrame implements FreeWizardInterface{
	JButton Hide;
	FacePic Pic;
	MovePane MoveTool;
	JLabel l0;
	ButtonGroup Group;
	JRadioButton WordVertical,WordHorizontal;
	MapKernel.MapControl MainHandle;
	JButton Reset;
	int Index;
	double ddx,ddy,dx0,dy0;
	class MovePane extends Canvas implements MouseListener,MouseMotionListener{
		public void paint(Graphics g){
			Toolkit kit=getToolkit();
			Image img=kit.getImage("BackGround30.jpg");
			g.drawImage(img,0,0,this);
		}
		public MovePane(){
			setBounds(0,0,275,275);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		public void mouseDragged(MouseEvent e) {
			ddx=((double)(e.getX()-PressedX))/
			((double)(MainHandle.getKernel().Screen.ScreenWidth))*
			(MainHandle.getKernel().Screen.LongitudeScale);
			
			MainHandle.getLineDatabase().dx[Index]=dx0+ddx;

			ddy=((double)(PressedY-e.getY()))/
			((double)(MainHandle.getKernel().Screen.ScreenHeight))*
			(MainHandle.getKernel().Screen.LatitudeScale);
			
			MainHandle.getLineDatabase().dy[Index]=dy0+ddy;

			MainHandle.ScreenFlush();
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
			ddx=((double)(e.getX()-PressedX))/
			((double)(MainHandle.getKernel().Screen.ScreenWidth))*
			(MainHandle.getKernel().Screen.LongitudeScale);
			
			MainHandle.getLineDatabase().dx[Index]=dx0+ddx;

			ddy=((double)(PressedY-e.getY()))/
			((double)(MainHandle.getKernel().Screen.ScreenHeight))*
			(MainHandle.getKernel().Screen.LatitudeScale);
			
			MainHandle.getLineDatabase().dy[Index]=dy0+ddy;

			dx0=MainHandle.getLineDatabase().dx[Index];
			dy0=MainHandle.getLineDatabase().dy[Index];
			MainHandle.ScreenFlush();
		}
	}
	class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener,ItemListener{
	public FacePic(){
		l0=new JLabel("【标签重定位】");
		l0.setFont(new Font("华文新魏",Font.BOLD,36));
		add(l0);
		setBounds(0,0,330,360);
		Hide=new JButton("返回");
		Hide.addActionListener(this);
		Reset=new JButton("重置");
		Reset.addActionListener(this);
		WordVertical=new JRadioButton("竖排");
		WordHorizontal=new JRadioButton("横排");
		Group=new ButtonGroup();
		Group.add(WordVertical);
		Group.add(WordHorizontal);
		WordVertical.setOpaque(false);
		WordHorizontal.setOpaque(false);
		WordVertical.addItemListener(this);
		WordHorizontal.addItemListener(this);
		MoveTool=new MovePane();
		add(MoveTool);
		add(WordVertical);
		add(WordHorizontal);
		add(Reset);
		add(Hide);		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("Gear.jpg");
		g.drawImage(img,0,0,330,360,this);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==Hide){
			submerge();
		}else if(e.getSource()==Reset){
			MainHandle.getLineDatabase().dx[Index]=0;
			MainHandle.getLineDatabase().dy[Index]=0;
			dx0=0;
			dy0=0;
			MainHandle.ScreenFlush();
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
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if(!IsEmerge()) return;
		if(e.getSource()==WordVertical)
			if(WordVertical.isSelected()){
				MainHandle.getLineDatabase().isVertical[Index]=true;
			}
		if(e.getSource()==WordHorizontal){
			if(WordHorizontal.isSelected()){
				MainHandle.getLineDatabase().isVertical[Index]=false;
			}
		}
		MainHandle.ScreenFlush();
	}
	}
	//Above Pic---------------------------------------------------------------------------------
	public void convey(int Index){
		this.Index=Index;
		dx0=MainHandle.getLineDatabase().dx[Index];
		dy0=MainHandle.getLineDatabase().dy[Index];
		WordVertical.setSelected(MainHandle.getLineDatabase().isVertical[Index]);
		WordHorizontal.setSelected(!MainHandle.getLineDatabase().isVertical[Index]);
	}
	public LineHintReLocationWizard(){
		setBounds(0,0,330,360);
		setVisible(false);
		setUndecorated(true);
		setLocationRelativeTo(null);
		Pic=new FacePic();
		add(Pic,BorderLayout.CENTER);
	}
	@Override
	public void emerge() {
		// TODO Auto-generated method stub
		MainHandle.getKernel().LinePreferenceView.submerge();
		MainHandle.getKernel().LineDatabaseView.submerge();
		MainHandle.ForbidOperate();
		this.setVisible(true);
	}

	@Override
	public void submerge() {
		// TODO Auto-generated method stub
		MainHandle.getKernel().LinePreferenceView.emerge();
		this.setVisible(false);
	}

	@Override
	public void setHandle(MapControl MainHandle) {
		// TODO Auto-generated method stub
		this.MainHandle=MainHandle;
	}
	public void Move(int dx,int dy){
		int x=this.getLocation().x;
		int y=this.getLocation().y;
		this.setLocation(x+dx,y+dy);
	}
	public boolean IsEmerge(){
		return this.isVisible();
	}
}

