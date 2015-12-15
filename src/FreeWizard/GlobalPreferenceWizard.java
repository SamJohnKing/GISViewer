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
	public GridLayout GridPattern=new GridLayout(12,4);
	public JButton GenerateScriptButton=new JButton("GenerateScript");
	public JButton GenerateRandomStyleButton=new JButton("GenerateRandomStyle");
	public JTextField PointRGBScript=new JTextField(10);
	public JTextField LineRGBScript=new JTextField(10);
	public JTextField PolygonRGBScript=new JTextField(10);
	public JTextField PointAlphaScript=new JTextField(10);
	public JTextField LineAlphaScript=new JTextField(10);
	public JTextField PolygonAlphaScript=new JTextField(10);
	public JTextField WordVisibleScript=new JTextField(10);
	public JTextField PointVisibleScript=new JTextField(10);
	public JTextField LineVisibleScript=new JTextField(10);
	public JTextField PolygonVisibleScript=new JTextField(10);
	public JTextField PointSizeScript=new JTextField(10);
	public JTextField LineWidthScript=new JTextField(10);
	public JTextField DashLineScript=new JTextField(10);
	public JTextField ArrowLineScript=new JTextField(10);
	public JTextField TitleScript=new JTextField(10);
	public JTextField InfoScript=new JTextField(10);
	public JLabel PointRGB_Script_Label=new JLabel("Script[PointRGB:0x?]");
	public JLabel LineRGB_Script_Label=new JLabel("Script[LineRGB:0x?]");
	public JLabel PolygonRGB_Script_Label=new JLabel("Script[PolygonRGB:0x?]");
	
	public class FacePic extends JPanel implements ActionListener,MouseListener,MouseMotionListener,ItemListener{
	public void GenerateStyle(){
		CommonString.setText("");
		bulletin.setText("");
		String buf="";
		if(PointRGBScript.getText()!=null)
			if(!(PointRGBScript.getText().isEmpty())){
				buf+="[PointRGB:0x"+PointRGBScript.getText()+"]";
				try{
					PointRGB_Script_Label.setForeground(new Color(Integer.parseInt(PointRGBScript.getText(), 16)));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Point_RGB_Err");
				}
			}
		if(LineRGBScript.getText()!=null)
			if(!(LineRGBScript.getText().isEmpty())){
				buf+="[LineRGB:0x"+LineRGBScript.getText()+"]";
				try{
					LineRGB_Script_Label.setForeground(new Color(Integer.parseInt(LineRGBScript.getText(), 16)));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Line_RGB_Err");
				}
			}
		if(PolygonRGBScript.getText()!=null)
			if(!(PolygonRGBScript.getText().isEmpty())){
				buf+="[PolygonRGB:0x"+PolygonRGBScript.getText()+"]";
				try{
					PolygonRGB_Script_Label.setForeground(new Color(Integer.parseInt(PolygonRGBScript.getText(), 16)));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Polygon_RGB_Err");
				}
			}
		if(PointAlphaScript.getText()!=null)
			if(!(PointAlphaScript.getText().isEmpty())){
				buf+="[PointAlpha:"+PointAlphaScript.getText()+"]";
				try{
					Color Origin=PointRGB_Script_Label.getForeground();
					if(!MapKernel.MapWizard.SingleItem.ShowAlphaFeature)
						PointRGB_Script_Label.setForeground(new Color(Origin.getRed(),Origin.getGreen(),Origin.getBlue()));
					else
					PointRGB_Script_Label.setForeground(new Color(Origin.getRed(),Origin.getGreen(),Origin.getBlue(),(int)(255*Float.parseFloat(PointAlphaScript.getText()))));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Poing_Alpha_Err");
				}
			}
		if(LineAlphaScript.getText()!=null)
			if(!(LineAlphaScript.getText().isEmpty())){
				buf+="[LineAlpha:"+LineAlphaScript.getText()+"]";
				try{
					Color Origin=LineRGB_Script_Label.getForeground();
					if(!MapKernel.MapWizard.SingleItem.ShowAlphaFeature)
						LineRGB_Script_Label.setForeground(new Color(Origin.getRed(),Origin.getGreen(),Origin.getBlue()));
					else
					LineRGB_Script_Label.setForeground(new Color(Origin.getRed(),Origin.getGreen(),Origin.getBlue(),(int)(255*Float.parseFloat(LineAlphaScript.getText()))));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Line_Alpha_Err");
				}
			}
		if(PolygonAlphaScript.getText()!=null)
			if(!(PolygonAlphaScript.getText().isEmpty())){
				buf+="[PolygonAlpha:"+PolygonAlphaScript.getText()+"]";
				try{
					Color Origin=PolygonRGB_Script_Label.getForeground();
					if(!MapKernel.MapWizard.SingleItem.ShowAlphaFeature)
						PolygonRGB_Script_Label.setForeground(new Color(Origin.getRed(),Origin.getGreen(),Origin.getBlue()));
					else
					PolygonRGB_Script_Label.setForeground(new Color(Origin.getRed(),Origin.getGreen(),Origin.getBlue(),(int)(255*Float.parseFloat(PolygonAlphaScript.getText()))));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Polygon_Alpha_Err");
				}
			}
		if(WordVisibleScript.getText()!=null)
			if(!(WordVisibleScript.getText().isEmpty())) buf+="[WordVisible:]";
		if(PointVisibleScript.getText()!=null)
			if(!(PointVisibleScript.getText().isEmpty())) buf+="[PointVisible:]";
		if(LineVisibleScript.getText()!=null)
			if(!(LineVisibleScript.getText().isEmpty())) buf+="[LineVisible:]";
		if(PolygonVisibleScript.getText()!=null)
			if(!(PolygonVisibleScript.getText().isEmpty())) buf+="[PolygonVisible:]";
		if(PointSizeScript.getText()!=null)
			if(!(PointSizeScript.getText().isEmpty())) buf+="[PointSize:"+PointSizeScript.getText()+"]";
		if(LineWidthScript.getText()!=null)
			if(!(LineWidthScript.getText().isEmpty())) buf+="[LineWidth:"+LineWidthScript.getText()+"]";
		if(DashLineScript.getText()!=null)
			if(!(DashLineScript.getText().isEmpty())) buf+="[DashLine:"+DashLineScript.getText()+"]";
		if(ArrowLineScript.getText()!=null)
			if(!(ArrowLineScript.getText().isEmpty())) buf+="[ArrowLine:"+ArrowLineScript.getText()+"]";
		if(TitleScript.getText()==null) buf+="[Title:]";
			else buf+="[Title:"+TitleScript.getText()+"]";
		if(InfoScript.getText()!=null)
			if(!(InfoScript.getText().isEmpty())) buf+="[Info:"+InfoScript.getText()+"]";
		CommonString.setText(buf);
		bulletin.setText(buf);
	}
	public void RenderRandomStyle(boolean full){
		java.util.Random RandomItem=new java.util.Random();
		int PointRGB=(int)((RandomItem.nextInt(256*256*256)+java.util.Calendar.getInstance().getTimeInMillis())%(256*256*256));
		float PointAlpha=(float)((RandomItem.nextInt(1000)+java.util.Calendar.getInstance().getTimeInMillis())%1000)/2000+0.49f;
		PointRGBScript.setText(Integer.toHexString(PointRGB));
		PointAlphaScript.setText(Float.toString(PointAlpha));
		
		int LineRGB=(int)((RandomItem.nextInt(256*256*256)+java.util.Calendar.getInstance().getTimeInMillis())%(256*256*256));
		float LineAlpha=(float)((RandomItem.nextInt(1000)+java.util.Calendar.getInstance().getTimeInMillis())%1000)/2000+0.49F;
		LineRGBScript.setText(Integer.toHexString(LineRGB));
		LineAlphaScript.setText(Float.toString(LineAlpha));
		
		int PolygonRGB=(int)((RandomItem.nextInt(256*256*256)+java.util.Calendar.getInstance().getTimeInMillis())%(256*256*256));
		float PolygonAlpha=(float)((RandomItem.nextInt(1000)+java.util.Calendar.getInstance().getTimeInMillis())%1000)/3000;
		PolygonRGBScript.setText(Integer.toHexString(PolygonRGB));
		PolygonAlphaScript.setText(Float.toString(PolygonAlpha));
		
		if(full){
			WordVisibleScript.setText("1");
			PointVisibleScript.setText("1");
			LineVisibleScript.setText("1");
			PolygonVisibleScript.setText("1");
			PointSizeScript.setText("6");
			LineWidthScript.setText("2");
			DashLineScript.setText("1");
			ArrowLineScript.setText("1");
		}
		
		GenerateStyle();
	}
	public FacePic(){
		setBounds(0,0,720,600);

		l0=new JLabel("Global Preference Configuration ");
		l0.setFont(new Font("serif",Font.BOLD+Font.ITALIC,28));
		add(l0);
		l1=new JLabel("[Three Click to Hide]");
		l1.setFont(new Font("serif",0,28));
		l1.setForeground(Color.red);
		add(l1);
		ExtendedUltility=new JPanel();
		ExtendedUltility.setVisible(false);
		ExtendedUltility.setPreferredSize(new Dimension(710,340));
		//Specific---------------------------------------------
		ValidizeCommonString=new JCheckBox("Validize Common String");
		ValidizeCommonString.setSelected(true);
		ValidizeCommonString.setOpaque(false);
		add(ValidizeCommonString);
		CommonString=new JTextField(40);
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
		AllowPreciseLinkRegion=new JCheckBox("Auto matching Points with the nearby [LinkRegion] polygon Precisely");
		AllowPreciseLinkRegion.setSelected(true);
		AllowPreciseLinkRegion.setOpaque(false);
		add(new JLabel("The Bulletin below Show Some Useful Information"));
		bulletin=new JTextArea(4,60);
		bulletin.setLineWrap(true);
		add(bulletin);
		add(AllowPreciseLinkRegion);
		add(VisualCommandLine);
		add(ExtendedUltility);
		ExtendedUltility.setOpaque(false);
		ExtendedUltility.setLayout(GridPattern);
		ExtendedUltility.add(PointRGB_Script_Label);
		ExtendedUltility.add(PointRGBScript);
		PointRGBScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[PointAlpha:?0.5]"));
		ExtendedUltility.add(PointAlphaScript);
		PointAlphaScript.addActionListener(this);
		
		ExtendedUltility.add(LineRGB_Script_Label);
		ExtendedUltility.add(LineRGBScript);
		LineRGBScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[LineAlpha:?0.5]"));
		ExtendedUltility.add(LineAlphaScript);
		LineAlphaScript.addActionListener(this);
		
		ExtendedUltility.add(PolygonRGB_Script_Label);
		ExtendedUltility.add(PolygonRGBScript);
		PolygonRGBScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[PolygonAlpha:?0.5]"));
		ExtendedUltility.add(PolygonAlphaScript);
		PolygonAlphaScript.addActionListener(this);
		
		ExtendedUltility.add(new JLabel("Script[WordVisible:]"));
		ExtendedUltility.add(WordVisibleScript);
		WordVisibleScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[PointVisible:]"));
		ExtendedUltility.add(PointVisibleScript);
		PointVisibleScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[LineVisible:]"));
		ExtendedUltility.add(LineVisibleScript);
		LineVisibleScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[PolygonVisible:]"));
		ExtendedUltility.add(PolygonVisibleScript);
		PolygonVisibleScript.addActionListener(this);
		
		ExtendedUltility.add(new JLabel("Script[PointSize:?]"));
		ExtendedUltility.add(PointSizeScript);
		PointSizeScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[LineWidth:?]"));
		ExtendedUltility.add(LineWidthScript);
		LineWidthScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[DashLine:]"));
		ExtendedUltility.add(DashLineScript);
		DashLineScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[ArrowLine:]"));
		ExtendedUltility.add(ArrowLineScript);
		ArrowLineScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[Title:?]"));
		ExtendedUltility.add(TitleScript);
		TitleScript.addActionListener(this);
		ExtendedUltility.add(new JLabel("Script[Info:?]"));
		ExtendedUltility.add(InfoScript);
		InfoScript.addActionListener(this);
		ExtendedUltility.add(GenerateScriptButton);
		ExtendedUltility.add(GenerateRandomStyleButton);
		GenerateRandomStyleButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				RenderRandomStyle(false);
			}
		});
		GenerateScriptButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				GenerateStyle();
			}
		});
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
		//-----------------------------------------------------
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void paintComponent(Graphics g){
		Toolkit kit=getToolkit();
		Image img=kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("Gear.jpg"));
		g.drawImage(img,0,0,this.getWidth(),this.getHeight(),this);
	}
	public void actionPerformed(ActionEvent e){
		GenerateStyle();
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
		setBounds(0,0,720,600);
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

