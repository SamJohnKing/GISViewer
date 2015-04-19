package FreeWizard;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FreeWizard.PolygonDatabaseWizard.FacePic;
import MapKernel.MapControl;

public class GlobalPreferenceWizard extends JFrame implements FreeWizardInterface{
	MapControl MainHandle;
	FacePic Pic;
	JLabel l0,l1;
	public JCheckBox AllowPreciseLinkRegion,ValidizeCommonString,VisualCommandLine;
	public JTextField CommonString;
	public JTextArea bulletin;
	public JPanel ExtendedUltility;
	class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener,ItemListener{
	public FacePic(){
		setBounds(0,0,540,560);

		l0=new JLabel("Global Preference Configuration ");
		l0.setFont(new Font("serif",Font.BOLD+Font.ITALIC,26));
		add(l0);
		l1=new JLabel("Three Click to Hide the Preference Wizard");
		l1.setFont(new Font("serif",0,20));
		add(l1);
		ExtendedUltility=new JPanel();
		ExtendedUltility.setVisible(false);
		ExtendedUltility.setPreferredSize(new Dimension(520,400));
		//Specific---------------------------------------------
		AllowPreciseLinkRegion=new JCheckBox("Auto matching Points with the nearby [LinkRegion] polygon Precisely");
		AllowPreciseLinkRegion.setSelected(true);
		AllowPreciseLinkRegion.setOpaque(false);
		add(AllowPreciseLinkRegion);
		ValidizeCommonString=new JCheckBox("Validize Common String");
		ValidizeCommonString.setSelected(true);
		ValidizeCommonString.setOpaque(false);
		add(ValidizeCommonString);
		CommonString=new JTextField(30);
		add(CommonString);
		VisualCommandLine=new JCheckBox("Select The CheckBox to Show the Extended Ultility Command List");
		VisualCommandLine.setOpaque(false);
		VisualCommandLine.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				ExtendedUltility.setVisible(!ExtendedUltility.isVisible());
				validate();
			}
		});
		add(new JLabel("The Bulletin below Show Some Useful Information"));
		bulletin=new JTextArea(6,48);
		add(bulletin);
		add(VisualCommandLine);
		add(ExtendedUltility);
		ExtendedUltility.setOpaque(false);
		//Invalid Button---------------------------------------
		JButton PointInputTransform=new JButton("PointInputTransform");
		PointInputTransform.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.PointInputTransform();
			}
		});
		//ExtendedUltility.add(PointInputTransform);
		//------------------------------------------------------
		JButton LineInputTransform=new JButton("LineInputTransform");
		LineInputTransform.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.LineInputTransform();
			}
		});
		//ExtendedUltility.add(LineInputTransform);
		//-------------------------------------------------------
		JButton PointOutputTransform=new JButton("PointOutputTransform");
		PointOutputTransform.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.PointOutputTransform();
			}
		});
		ExtendedUltility.add(PointOutputTransform);
		//-------------------------------------------------------
		JButton LineOutputTransform=new JButton("LineOutputTransform");
		LineOutputTransform.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.LineOutputTransform();
			}
		});
		ExtendedUltility.add(LineOutputTransform);
		//---------------------------------------------------------
		JButton PolygonOutputTransform=new JButton("PolygonOutputTransform");
		PolygonOutputTransform.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.PolygonOutputTransform();
			}
		});
		ExtendedUltility.add(PolygonOutputTransform);
		//-------------------------------------------------------
		JButton PendingTrap=new JButton("PendingTrap");
		PendingTrap.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.PendingTrap();
			}
		});
		ExtendedUltility.add(PendingTrap);
		//-------------------------------------------------------
		JButton CheckDelete=new JButton("CheckDelete");
		CheckDelete.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.CheckDelete();
			}
		});
		ExtendedUltility.add(CheckDelete);
		//-----------------------------------------------------
		JButton MultipleData=new JButton("MultipleData");
		MultipleData.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int row=-1,col=-1;
				Exception fail=new Exception("Number Failure!");
				try{
					row=Integer.parseInt(JOptionPane.showInputDialog(null,"Please Input the Row Number"));
					col=Integer.parseInt(JOptionPane.showInputDialog(null,"Please Input the Col Number"));
					if(row<=0) throw fail;
					if(col<=0) throw fail;
					if(JOptionPane.showConfirmDialog(null,"Confirm to Operate?")==JOptionPane.OK_OPTION){
						MainHandle.MultipleData(row, col);
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,ex.toString());
				}
			}
		});
		ExtendedUltility.add(MultipleData);
		//-----------------------------------------------------
		JButton PointInput=new JButton("PointInput");
		PointInput.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.PointInput();
			}
		});
		ExtendedUltility.add(PointInput);
		//-----------------------------------------------------
		JButton LineInput=new JButton("LineInput");
		LineInput.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.LineInput();
			}
		});
		ExtendedUltility.add(LineInput);
		//-----------------------------------------------------
		JButton PolygonInput=new JButton("PolygonInput");
		PolygonInput.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.PolygonInput();
			}
		});
		ExtendedUltility.add(PolygonInput);
		//-----------------------------------------------------
		JButton Resize=new JButton("Resize");
		Resize.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.Resize();
			}
		});
		ExtendedUltility.add(Resize);
		//-----------------------------------------------------
		JButton ImageDirChange=new JButton("ImageDirChange");
		ImageDirChange.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MainHandle.ImageDirChange();
			}
		});
		ExtendedUltility.add(ImageDirChange);
		//-----------------------------------------------------
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage("Gear.jpg");
		g.drawImage(img,0,0,540,560,this);
	}
	public void actionPerformed(ActionEvent e){
	}
	public void mouseDragged(MouseEvent e) {
		Move(e.getX()-PressedX,e.getY()-PressedY);
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==3) submerge();
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
	}
	}
	//Above Pic-------------------------------------------------------

	@Override
	public void emerge() {
		// TODO Auto-generated method stub
		String str="PointDB Total: "+MainHandle.getPointDatabase().PointNum;
		str+="\nLineDB Total: <Line> "+MainHandle.getLineDatabase().LineNum;
		str+=" <Point> "+MainHandle.getLineDatabase().PointUse;
		str+="\nPolyDB Total: <Poly> "+MainHandle.getPolygonDatabase().PolygonNum;
		str+=" <Point> "+MainHandle.getPolygonDatabase().PointUse;
		if(JOptionPane.showConfirmDialog(null,str)==JOptionPane.YES_OPTION) this.setVisible(true);
	}

	int Index;
	@Override
	public void submerge() {
		// TODO Auto-generated method stub
		this.setVisible(false);
	}

	@Override
	public void setHandle(MapControl MainHandle) {
		// TODO Auto-generated method stub
		this.MainHandle=MainHandle;
	}
	public GlobalPreferenceWizard(){
		setBounds(0,0,540,560);
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

