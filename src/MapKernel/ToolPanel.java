package MapKernel;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

	abstract public class ToolPanel extends JPanel{
		//6个面板的共同父类，定义了通用的组件，比如经纬度显示栏和锁屏/解锁键,统一显示格式
		public JButton ScreenLockButton=new JButton("锁住屏幕"),ScreenUnLockButton=new JButton("解锁屏幕");
		public ToolPanel(){
			setBounds(0,0,280,680);
			FlowLayout layout=new FlowLayout();
			setLayout(layout);
			layout.setVgap(15);
		}
		abstract public void setLongitude(double Longitude);
		abstract public void setLatitude(double Latitude);
		abstract public String getString();
		public void ClockImpulse(){};
	}