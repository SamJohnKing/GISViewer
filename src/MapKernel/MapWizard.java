package MapKernel;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.geom.Ellipse2D.Double;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

import Database.LineDataSet;
import Database.PointDataSet;
import Database.PointStructure;
import ExtendedToolPane.ExtendedToolPaneInterface;
import ExtendedToolPane.MapElementsEditorPaneClass;
import ExtendedToolPane.PolygonAddPaneClass;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Handler;

import ExtendedToolPane.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;

import com.sun.image.codec.jpeg.*;

public class MapWizard extends JFrame implements ActionListener {
	// The Elements:------------------------
	public static MapWizard SingleItem=null;
	CardLayout ToolCard;
	JMenuBar menubar;
	JMenu FileMenu, EditMenu, MapControlMenu, MapDataMenu, HelpMenu;
	JPanel Tool;
	public ScreenCanvas Screen;
	public int VisualObjectMaxNum = 25000;
	boolean MyTimerEnable = true;
	File DIR = null;
	public File ImageDir = null;
	File TrafficFile = null;
	File TaxiDir = null;
	File LandMarkFile = null;
	JMenuItem OpenItem, ExitItem, BasicInfoItem, SaveItem, TwoPointItem,
			ClearAllStaticPoint, ClearLastPoint, ClearDirection;
	JMenuItem CalibrateItem, ShowClockItem, ShowTaxiSearchItem,
			RouteSearchItem, WashScreenItem, ShowCenterItem, VeilCenterItem;
	JMenuItem LandMarkEditItem, AboutFrameItem, LandMarkOnScreenItem,
			LandMarkVeilItem, LandMarkNameOnScreenItem, LandMarkNameVeilItem;
	JMenuItem LandMarkQueryItem, MyTimerOn, MyTimerOff, ClearMemory;
	JFileChooser FileDialog;
	LandMarkQueryFrameClass LandMarkQueryFrame;
	LandMarkSpotFrameClass LandMarkSpotFrame;
	double LongitudeStart = 1e100, LongitudeEnd = -1e100,
			LatitudeStart = 1e100, LatitudeEnd = -1e100;
	JFrame SubFrameClock;
	public GPSSet GPSPoints;
	int HighestRate;
	double RoadDeltaLongitude, RoadDeltaLatitude, GPSDeltaLongitude,
			GPSDeltaLatitude;
	NULLPaneClass NULL;
	BasicInfoPaneClass BasicInfoPane;
	TwoPointPaneClass TwoPointPane;
	CalibratePaneClass CalibratePane;
	TaxiSearchPaneClass TaxiSearchPane;
	RouteSearchPaneClass RouteSearchPane;
	LandMarkEditPaneClass LandMarkEditPane;
	ConcentrateTaxiWizardClass ConcentrateTaxiWizard;
	About AboutFrame;
	double SelectedX1, SelectEdX2, SelectedY1, SelectedY2;
	public ToolPanel NowPanel;
	Timer myTimer = new Timer(1000, this);
	public ClockWizardClass ClockWizard;
	public MapHandle Handle;

	// -----------------------------------------------------------------------
	public void CleanUp() {// 打开文件时清空所有数据结构
		DIR = null;
		ImageDir = null;
		TrafficFile = null;
		TaxiDir = null;
		LandMarkFile = null;
		PolygonDatabaseFile = null;
		LineDatabaseFile = null;
		TaxiTrajectoryDatabaseFile = null;
		ClearStateAfterSwitchPane();
	}

	// -----------------------------------------------------------------------
	void Idle(int t) {// 等待进程，类似sleep
		try {
			Thread.sleep(t);
		} catch (Exception e) {
			System.exit(0);
		}
	}

	// ---------------------------------------------------------------------------
	public class ConcentrateTaxiWizardClass extends JFrame {
		int XPoint = 0, YPoint = 0, XWidth = 315, YHeight = 160;
		FacePic BackGroundPic;
		JButton ShowTaxiCode, VeilTaxiCode, StartTrace, GiveUpTrace,
				ShowLandMark, Back;
		JLabel l0;
		JTextField Code;
		boolean IsShowLandMark = false;

		class FacePic extends JPanel implements ActionListener, MouseListener,
				MouseMotionListener {
			public FacePic() {
				setBounds(XPoint, YPoint, XWidth, YHeight);
				addMouseListener(this);
				addMouseMotionListener(this);
				// --------------------------------------------
				l0 = new JLabel(LanguageDic.GetWords("出租车自由追踪"));
				l0.setFont(new Font("华文新魏", Font.BOLD, 25));
				l0.setForeground(Color.black);
				add(l0);
				ShowTaxiCode = new JButton(LanguageDic.GetWords("显示出租车标识"));
				ShowTaxiCode.addActionListener(this);
				VeilTaxiCode = new JButton(LanguageDic.GetWords("隐藏出租车标识"));
				VeilTaxiCode.addActionListener(this);
				add(ShowTaxiCode);
				add(VeilTaxiCode);
				JLabel l1 = new JLabel(LanguageDic.GetWords("出租车标识"));
				l1.setForeground(Color.red);
				add(l1);
				Code = new JTextField(20);
				Code.addActionListener(this);
				add(Code);
				ShowTaxiCode.setEnabled(true);
				VeilTaxiCode.setEnabled(false);
				StartTrace = new JButton(LanguageDic.GetWords("开始追踪"));
				StartTrace.addActionListener(this);
				GiveUpTrace = new JButton(LanguageDic.GetWords("放弃追踪"));
				GiveUpTrace.addActionListener(this);
				Back = new JButton(LanguageDic.GetWords("返回"));
				Back.addActionListener(this);
				add(StartTrace);
				add(GiveUpTrace);
				add(Back);
				ShowLandMark = new JButton(LanguageDic.GetWords("动态显示/隐藏周遭信息"));
				ShowLandMark.addActionListener(this);
				add(ShowLandMark);
				// --------------------------------------------
			}

			public void paintComponent(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("Metal.jpg");
				g.drawImage(img, 0, 0, XWidth, YHeight, this);
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == ShowTaxiCode) {
					ShowTaxiCode.setEnabled(false);
					VeilTaxiCode.setEnabled(true);
				} else if (e.getSource() == VeilTaxiCode) {
					ShowTaxiCode.setEnabled(true);
					VeilTaxiCode.setEnabled(false);
				} else if ((e.getSource() == Code)
						|| (e.getSource() == StartTrace)) {
					int kkk = -1;
					for (int i = 0; i < GPSPoints.TaxiNum; i++) {
						if (GPSPoints.TaxiID[i].equals(Code.getText())) {
							kkk = i;
							break;
						}
					}
					if (kkk < 0) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("输入的数据未命中"),
								LanguageDic.GetWords("请检查后重新输入"),
								JOptionPane.WARNING_MESSAGE);
					} else {
						TaxiSearchPane.Trace = kkk + 1;
						TaxiSearchPane.TraceTheNearest.setEnabled(true);
						TaxiSearchPane.StopTrace.setEnabled(false);
						StartTrace.setEnabled(false);
						GiveUpTrace.setEnabled(true);
					}
				} else if (e.getSource() == GiveUpTrace) {
					TaxiSearchPane.Trace = -1;
					TaxiSearchPane.TraceTheNearest.setEnabled(true);
					TaxiSearchPane.StopTrace.setEnabled(false);
					StartTrace.setEnabled(true);
					GiveUpTrace.setEnabled(false);
				} else if (e.getSource() == ShowLandMark) {
					if (IsShowLandMark) {
						LandMarkSpotFrame.Hide();
						Screen.ShowScreenHint = false;
						Screen.IsShowDirection = false;
					} else {
						Screen.ShowScreenHint = true;
						Screen.IsShowDirection = true;
					}
					IsShowLandMark = !IsShowLandMark;
				} else if (e.getSource() == Back) {
					Hide();
				}
			}

			public void mouseDragged(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			int PressedX, PressedY;

			public void mousePressed(MouseEvent e) {
				PressedX = e.getX();
				PressedY = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}
		}

		// -------------------------------------------------------
		public ConcentrateTaxiWizardClass() {
			setVisible(false);
			setUndecorated(true);
			setBounds(XPoint, YPoint, XWidth, YHeight);
			setLocationRelativeTo(null);
			BackGroundPic = new FacePic();
			add(BackGroundPic);
		}

		public void Hide() {// 隐藏窗口
			setVisible(false);
		}

		public void emerge() {// 显示窗口
			setVisible(true);
			if (TaxiSearchPane.Trace > 0) {
				StartTrace.setEnabled(false);
				GiveUpTrace.setEnabled(true);
			} else {
				StartTrace.setEnabled(true);
				GiveUpTrace.setEnabled(false);
			}
		}

		public void Move(int dx, int dy) {
			int x = this.getLocation().x;
			int y = this.getLocation().y;
			this.setLocation(x + dx, y + dy);
		}

		public void WizardResize(int width, int height) {
			this.setBounds(0, 0, width, height);
			XWidth = width;
			YHeight = height;
			setLocationRelativeTo(null);
		}
	}

	// -----------------------------------------------------------------------
	public class LandMarkSpotFrameClass extends JFrame {// 显示地标点的快速定位窗口
		FacePic Pic;

		class Photo extends Canvas {// 专门用来显示图片
			String PhotoName = null;

			public Photo() {
				setBounds(0, 0, 300, 250);
			}

			public void paint(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("DefaultPhoto.jpg");

				if (ImageDir != null) {
					File f = new File(ImageDir, PhotoName);
					if (f.exists()) {
						img = kit.getImage(ImageDir.toString() + "/"
								+ PhotoName);
					}
				}

				g.drawImage(img, 0, 0, 300, 250, this);
			}
		}

		JLabel l0;

		class FacePic extends JPanel implements ActionListener, MouseListener,
				MouseMotionListener {
			// 继承JPanel装载各种控制组件
			Photo PhotoPic;
			JButton Hide, Confirm, WriteBack;
			JTextField Name, Type;
			JTextArea Script;
			JScrollPane Handle;

			public FacePic() {
				setBounds(0, 0, 315, 525);
				l0 = new JLabel(LanguageDic.GetWords("快速定位工具窗"));
				l0.setFont(new Font("华文新魏", Font.BOLD, 20));
				add(l0);
				PhotoPic = new Photo();
				add(PhotoPic);
				Hide = new JButton(LanguageDic.GetWords("返回"));
				Hide.addActionListener(this);
				addMouseListener(this);
				addMouseMotionListener(this);
				Confirm = new JButton(LanguageDic.GetWords("定位"));
				Confirm.addActionListener(this);
				WriteBack = new JButton(LanguageDic.GetWords("写回缓存"));
				WriteBack.addActionListener(this);
				Name = new JTextField(27);
				Script = new JTextArea(8, 26);
				Type = new JTextField(27);
				add(Name);
				add(Type);
				Script.setLineWrap(true);
				add(new JScrollPane(Script,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
				add(WriteBack);
				add(Hide);
				add(Confirm);
			}

			public void paintComponent(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("BackGround16.jpg");
				g.drawImage(img, 0, 0, 315, 525, this);
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == Hide) {
					Hide();
				} else if (e.getSource() == Confirm) {
					Screen.MoveMiddle(GPSPoints.LandMarkLongitude[Emerge_ID],
							GPSPoints.LandMarkLatitude[Emerge_ID]);
					Screen.repaint();
					if (NowPanel == RouteSearchPane) {
						int n = JOptionPane.showConfirmDialog(null, LanguageDic
								.GetWords("您现在正处于路径规划阶段，选为起点按是,选为终点按否，放弃按取消"),
								LanguageDic.GetWords("起点终点选取"),
								JOptionPane.YES_NO_CANCEL_OPTION);
						if (n == JOptionPane.YES_OPTION) {
							RouteSearchPane.SourceLongitude
									.setText(java.lang.Double
											.toString(GPSPoints.LandMarkLongitude[Emerge_ID]));
							RouteSearchPane.SourceLatitude
									.setText(java.lang.Double
											.toString(GPSPoints.LandMarkLatitude[Emerge_ID]));
							JOptionPane.showMessageDialog(null, "【"
									+ GPSPoints.LandMarkName[Emerge_ID] + "】"
									+ LanguageDic.GetWords("成功设置为起点"),
									LanguageDic.GetWords("起点设置成功"),
									JOptionPane.INFORMATION_MESSAGE);
						} else if (n == JOptionPane.NO_OPTION) {
							RouteSearchPane.TerminalLongitude
									.setText(java.lang.Double
											.toString(GPSPoints.LandMarkLongitude[Emerge_ID]));
							RouteSearchPane.TerminalLatitude
									.setText(java.lang.Double
											.toString(GPSPoints.LandMarkLatitude[Emerge_ID]));
							JOptionPane.showMessageDialog(null, "【"
									+ GPSPoints.LandMarkName[Emerge_ID] + "】"
									+ LanguageDic.GetWords("成功设置为终点"),
									LanguageDic.GetWords("终点设置成功"),
									JOptionPane.INFORMATION_MESSAGE);
						}
					} else if (NowPanel == TaxiSearchPane) {
						int n = JOptionPane.showConfirmDialog(null, LanguageDic
								.GetWords("您正在应用出租车搜寻功能，选为搜寻源点则按是,否则放弃"),
								LanguageDic.GetWords("出租车搜寻源点设置"),
								JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.YES_OPTION) {
							TaxiSearchPane.CenterLongitude
									.setText(java.lang.Double
											.toString(GPSPoints.LandMarkLongitude[Emerge_ID]));
							TaxiSearchPane.CenterLatitude
									.setText(java.lang.Double
											.toString(GPSPoints.LandMarkLatitude[Emerge_ID]));
							JOptionPane.showMessageDialog(null, "【"
									+ GPSPoints.LandMarkName[Emerge_ID] + "】"
									+ LanguageDic.GetWords("成功设置为源点"),
									LanguageDic.GetWords("源点设置成功"),
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
					Hide();
				} else if (e.getSource() == WriteBack) {
					GPSPoints.LandMarkName[Emerge_ID] = Name.getText().trim();
					GPSPoints.LandMarkType[Emerge_ID] = Type.getText().trim();
					GPSPoints.LandMarkScript[Emerge_ID] = Script.getText()
							.trim();
					Hide();
				}
			}

			public void mouseDragged(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			int PressedX, PressedY;

			public void mousePressed(MouseEvent e) {
				PressedX = e.getX();
				PressedY = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}
		}

		// -------------------------------------------------------
		int Emerge_ID = -1;

		public LandMarkSpotFrameClass() {
			setVisible(false);
			setUndecorated(true);
			setBounds(0, 0, 315, 525);
			setLocationRelativeTo(null);
			Pic = new FacePic();
			add(Pic, BorderLayout.CENTER);
		}

		public void Hide() {// 隐藏窗口
			setVisible(false);
		}

		public void emerge(int id) {// 显示窗口，并设定当前需要显示的地标ID
			Pic.Name.setText(GPSPoints.LandMarkName[id]);
			Pic.Script.setText(GPSPoints.LandMarkScript[id]);
			Pic.Type.setText(GPSPoints.LandMarkType[id]);
			Pic.PhotoPic.PhotoName = GPSPoints.LandMarkName[id] + ".jpg";
			l0.setText("[" + GPSPoints.LandMarkName[id] + "]");
			Emerge_ID = id;
			setVisible(true);
			validate();
		}

		public void Move(int dx, int dy) {
			int x = this.getLocation().x;
			int y = this.getLocation().y;
			this.setLocation(x + dx, y + dy);
		}
	}

	// ---------------------------------------------------------------------------------
	public class LandMarkQueryFrameClass extends JFrame {// 用于显示数据库的显示窗口
		FacePic Pic;

		class FacePic extends JPanel implements ActionListener, MouseListener,
				MouseMotionListener {
			JButton Hide, Query, DeleteRow, Delete, UpdateRow, Update,
					MoreInfo, Transit, TransitAll;
			JLabel l0, l1, l2;
			JTextField Name, Type;
			String[][] Result = new String[10000][5];
			String[] Title = { LanguageDic.GetWords("序号"),
					LanguageDic.GetWords("经度"), LanguageDic.GetWords("纬度"),
					LanguageDic.GetWords("名称"), LanguageDic.GetWords("类型") };
			JTable ResultTable;
			TableModel Content;
			JScrollPane Handle;
			boolean[] Hit = new boolean[10000];

			public FacePic() {
				ResultTable = new JTable();
				setBounds(0, 0, 600, 435);
				Hide = new JButton(LanguageDic.GetWords("返回"));
				Hide.addActionListener(this);
				l1 = new JLabel(LanguageDic.GetWords("名称关键字"));
				Name = new JTextField(10);
				l2 = new JLabel(LanguageDic.GetWords("类型关键字"));
				Type = new JTextField(10);
				Query = new JButton(LanguageDic.GetWords("查询"));
				l1.setForeground(Color.orange);
				l2.setForeground(Color.orange);

				l0 = new JLabel(LanguageDic.GetWords("【在下面的输入框内输入地标关键字】"));
				l0.setForeground(Color.orange);
				l0.setFont(new Font("华文新魏", Font.BOLD, 30));

				Name.addActionListener(this);
				Type.addActionListener(this);

				add(l0);
				add(l1);
				add(Name);
				add(l2);
				add(Type);
				add(Query);
				add(Hide);
				DefaultTableModel model = new DefaultTableModel(Result, Title) {
					public boolean isCellEditable(int row, int column) {
						if (column == 0)
							return false;
						else
							return true;
					}
				};
				ResultTable = new JTable(model);
				Content = ResultTable.getModel();
				Handle = new JScrollPane(ResultTable);
				add(Handle);
				Handle.setVisible(false);
				Handle.setPreferredSize(new Dimension(550, 320));
				addMouseListener(this);
				addMouseMotionListener(this);
				Query.addActionListener(this);
				DeleteRow = new JButton(LanguageDic.GetWords("删除选中"));
				Delete = new JButton(LanguageDic.GetWords("删除全部"));
				UpdateRow = new JButton(LanguageDic.GetWords("写回选中"));
				Update = new JButton(LanguageDic.GetWords("写回全部"));
				MoreInfo = new JButton(LanguageDic.GetWords("详细"));
				Transit = new JButton(LanguageDic.GetWords("导出"));
				TransitAll = new JButton(LanguageDic.GetWords("全部导出"));
				DeleteRow.setVisible(false);
				Delete.setVisible(false);
				UpdateRow.setVisible(false);
				Update.setVisible(false);
				MoreInfo.setVisible(false);
				Transit.setVisible(false);
				TransitAll.setVisible(false);
				add(DeleteRow);
				add(Delete);
				add(UpdateRow);
				add(Update);
				add(MoreInfo);
				add(Transit);
				add(TransitAll);
				DeleteRow.addActionListener(this);
				Delete.addActionListener(this);
				UpdateRow.addActionListener(this);
				Update.addActionListener(this);
				MoreInfo.addActionListener(this);
				Transit.addActionListener(this);
				TransitAll.addActionListener(this);
			}

			public void paintComponent(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("BackGround15.jpg");
				g.drawImage(img, 0, 0, 600, 435, this);
			}

			public void reQuery() {
				int HitNum = 0;
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					Hit[i] = true;
					if (Name.getText() != "") {
						if (GPSPoints.LandMarkName[i].indexOf(Name.getText()) == -1)
							Hit[i] = false;
					}
					if (Type.getText() != "") {
						if (GPSPoints.LandMarkType[i].indexOf(Type.getText()) == -1)
							Hit[i] = false;
					}
					if (Hit[i])
						HitNum++;
				}
				DefaultTableModel tableModel = (DefaultTableModel) ResultTable
						.getModel();
				while (tableModel.getRowCount() != 0)
					tableModel.removeRow(0);
				TableColumn Column = ResultTable.getColumnModel().getColumn(0);
				Column.setPreferredWidth(3);
				if (HitNum == 0) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("已经没有匹配的结果了"),
							LanguageDic.GetWords("未命中"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					if (!Hit[i])
						continue;
					String[] temp = new String[5];
					temp[0] = Integer.toString(i);
					temp[1] = java.lang.Double
							.toString(GPSPoints.LandMarkLongitude[i]);
					temp[2] = java.lang.Double
							.toString(GPSPoints.LandMarkLatitude[i]);
					temp[3] = GPSPoints.LandMarkName[i];
					temp[4] = GPSPoints.LandMarkType[i];
					tableModel.addRow(temp);
				}
				ResultTable.validate();
			}

			public void actionPerformed(ActionEvent e) {
				if (LandMarkFile == null) {
					Hide();
					return;
				}
				if (e.getSource() == Hide) {
					Hide();
				} else if (e.getSource() == Query) {
					if (ResultTable.getCellEditor() != null) {
						ResultTable.setCellEditor(null);
					}
					int HitNum = 0;
					for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
						Hit[i] = true;
						if (Name.getText() != "") {
							if (GPSPoints.LandMarkName[i].indexOf(Name
									.getText()) == -1)
								Hit[i] = false;
						}
						if (Type.getText() != "") {
							if (GPSPoints.LandMarkType[i].indexOf(Type
									.getText()) == -1)
								Hit[i] = false;
						}
						if (Hit[i])
							HitNum++;
					}
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					while (tableModel.getRowCount() != 0)
						tableModel.removeRow(0);
					TableColumn Column = ResultTable.getColumnModel()
							.getColumn(0);
					Column.setPreferredWidth(3);
					if (HitNum == 0) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("对不起没要您要的结果"),
								LanguageDic.GetWords("未命中"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
						if (!Hit[i])
							continue;
						String[] temp = new String[5];
						temp[0] = Integer.toString(i);
						temp[1] = java.lang.Double
								.toString(GPSPoints.LandMarkLongitude[i]);
						temp[2] = java.lang.Double
								.toString(GPSPoints.LandMarkLatitude[i]);
						temp[3] = GPSPoints.LandMarkName[i];
						temp[4] = GPSPoints.LandMarkType[i];
						tableModel.addRow(temp);
					}
					ResultTable.validate();
					Handle.setVisible(true);
					DeleteRow.setVisible(true);
					Delete.setVisible(true);
					UpdateRow.setVisible(true);
					Update.setVisible(true);
					MoreInfo.setVisible(true);
					Transit.setVisible(true);
					TransitAll.setVisible(true);
					validate();
				} else if (e.getSource() == DeleteRow) {
					if (ResultTable.getCellEditor() != null) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
								LanguageDic.GetWords("更改内容时不可编辑单元格"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					int selectRows = ResultTable.getSelectedRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					if (selectRows == 1) {
						int selectedRowIndex = ResultTable.getSelectedRow();
						String str = (String) tableModel.getValueAt(
								selectedRowIndex, 0);
						GPSPoints.LandMarkDeleteRow(Integer.parseInt(str));
						reQuery();
					} else {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("请您选中一行"),
								LanguageDic.GetWords("选中行异常"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else if (e.getSource() == Delete) {
					if (ResultTable.getCellEditor() != null) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
								LanguageDic.GetWords("更改内容时不可编辑单元格"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					int RowCount = ResultTable.getRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					for (int i = RowCount - 1; i >= 0; i--) {
						String str = (String) tableModel.getValueAt(i, 0);
						GPSPoints.LandMarkDeleteRow(Integer.parseInt(str));
					}
					reQuery();
				} else if (e.getSource() == UpdateRow) {
					if (ResultTable.getCellEditor() != null) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
								LanguageDic.GetWords("更改内容时不可编辑单元格"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					int selectRows = ResultTable.getSelectedRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					if (selectRows == 1) {
						int selectedRowIndex = ResultTable.getSelectedRow();
						try {
							String str = (String) tableModel.getValueAt(
									selectedRowIndex, 0);
							GPSPoints.LandMarkUpdateRow(Integer.parseInt(str),
									(String) tableModel.getValueAt(
											selectedRowIndex, 1),
									(String) tableModel.getValueAt(
											selectedRowIndex, 2),
									(String) tableModel.getValueAt(
											selectedRowIndex, 3),
									(String) tableModel.getValueAt(
											selectedRowIndex, 4));
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null,
									LanguageDic.GetWords("数据库检查到异常格式"),
									LanguageDic.GetWords("数据格式异常"),
									JOptionPane.WARNING_MESSAGE);
						}
						reQuery();
					} else {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("请您选中一行"),
								LanguageDic.GetWords("选中行异常"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else if (e.getSource() == Update) {
					if (ResultTable.getCellEditor() != null) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
								LanguageDic.GetWords("更改内容时不可编辑单元格"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					int RowCount = ResultTable.getRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					for (int i = 0; i < RowCount; i++) {
						String str = (String) tableModel.getValueAt(i, 0);
						try {
							GPSPoints.LandMarkUpdateRow(Integer.parseInt(str),
									(String) tableModel.getValueAt(i, 1),
									(String) tableModel.getValueAt(i, 2),
									(String) tableModel.getValueAt(i, 3),
									(String) tableModel.getValueAt(i, 4));
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null,
									LanguageDic.GetWords("数据库检查到异常格式"),
									LanguageDic.GetWords("数据格式异常"),
									JOptionPane.WARNING_MESSAGE);
						}
					}
					reQuery();
				} else if (e.getSource() == MoreInfo) {
					int selectRows = ResultTable.getSelectedRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					if (selectRows == 1) {
						int selectedRowIndex = ResultTable.getSelectedRow();
						LandMarkSpotFrame.emerge(Integer
								.parseInt((String) tableModel.getValueAt(
										selectedRowIndex, 0)));
					} else {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("请您选中一行"),
								LanguageDic.GetWords("选中行异常"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else if ((e.getSource() == Name) || (e.getSource() == Type)) {
					if (ResultTable.getCellEditor() != null) {
						ResultTable.setCellEditor(null);
					}
					reQuery();
				} else if (e.getSource() == Transit) {
					int selectRows = ResultTable.getSelectedRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					if (selectRows == 1) {
						int selectedRowIndex = ResultTable.getSelectedRow();
						int index = Integer.parseInt((String) tableModel
								.getValueAt(selectedRowIndex, 0));
						String str = "[Title:" + GPSPoints.LandMarkName[index]
								+ "][Info:" + GPSPoints.LandMarkType[index]
								+ "]";
						PointDatabase.add(GPSPoints.LandMarkLongitude[index],
								GPSPoints.LandMarkLatitude[index], str);
					} else {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("请您选中一行"),
								LanguageDic.GetWords("选中行异常"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else if (e.getSource() == TransitAll) {
					if (ResultTable.getCellEditor() != null) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("您正在编辑单元格,为了数据安全请提前确认"),
								LanguageDic.GetWords("更改内容时不可编辑单元格"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					int RowCount = ResultTable.getRowCount();
					DefaultTableModel tableModel = (DefaultTableModel) ResultTable
							.getModel();
					for (int i = 0; i < RowCount; i++) {
						String str = (String) tableModel.getValueAt(i, 0);
						try {
							String sss = "[Title:"
									+ GPSPoints.LandMarkName[Integer
											.parseInt(str)]
									+ "][Info:"
									+ GPSPoints.LandMarkType[Integer
											.parseInt(str)] + "]";
							PointDatabase.add(
									GPSPoints.LandMarkLongitude[Integer
											.parseInt(str)],
									GPSPoints.LandMarkLatitude[Integer
											.parseInt(str)], sss);

						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null,
									LanguageDic.GetWords("数据库检查到异常格式"),
									LanguageDic.GetWords("数据格式异常"),
									JOptionPane.WARNING_MESSAGE);
						}
					}
					reQuery();
				}
			}

			public void mouseDragged(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			int PressedX, PressedY;

			public void mousePressed(MouseEvent e) {
				PressedX = e.getX();
				PressedY = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}
		}

		public LandMarkQueryFrameClass() {
			setVisible(false);
			setUndecorated(true);
			setBounds(0, 0, 600, 435);
			setLocationRelativeTo(null);
			Pic = new FacePic();
			add(Pic, BorderLayout.CENTER);
		}

		public void Hide() {
			setVisible(false);
		}

		public void Move(int dx, int dy) {
			int x = this.getLocation().x;
			int y = this.getLocation().y;
			this.setLocation(x + dx, y + dy);
		}
	}

	public class Face extends JFrame {// 开启时显示程序的徽标界面
		class FacePic extends JPanel {
			public FacePic() {
				setBounds(0, 0, 560, 400);
			}

			public void paintComponent(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("tower.jpg");
				g.drawImage(img, 0, 0, 560, 400, this);
			}
		}

		public Face() {
			setBounds(300, 200, 560, 400);
			setLocationRelativeTo(null);
			setUndecorated(true);
			setVisible(true);
			add(new FacePic(), BorderLayout.CENTER);
		}
	}

	public class About extends JFrame {// 显示版权信息
		class FacePic extends JPanel {
			public FacePic() {
				setLayout(null);
				setBounds(0, 0, 542, 390);
				setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				setTitle(LanguageDic.GetWords("关于软件"));
			}

			public void paintComponent(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("About.jpg");
				g.drawImage(img, 0, 0, this);
				// Image fdu=kit.getImage("Fudan.jpg");
				// g.drawImage(fdu,395,250,90,90,this);
			}
		}

		public About() {
			setVisible(false);
			setBounds(300, 200, 542, 390);
			setResizable(false);
			add(new FacePic(), BorderLayout.CENTER);
		}
	}

	public void ClearStateAfterSwitchPane() {// 当工具栏切换时进行清空屏幕的善后工作，并且为新面板初始化
		Screen.ExtendedComponentReset();
		Screen.ShowLargeRegion = false;
		Screen.IsShowDirection = false;
		Screen.XYCount = 0;
		Screen.SelectedPointList[0] = 0;
		Screen.ShowCenter = false;
		Screen.ShowScreenHint = false;
		Screen.LineCount = 0;
		Screen.LandMarkSelectedNum = 0;
		for (int i = 0; i < GPSPoints.LandMarkNum; i++)
			Screen.IsLandMarkSelected[i] = false;
		if (Screen.lock) {
			NowPanel.ScreenLockButton.setEnabled(false);
			NowPanel.ScreenUnLockButton.setEnabled(true);
		} else {
			NowPanel.ScreenLockButton.setEnabled(true);
			NowPanel.ScreenUnLockButton.setEnabled(false);
		}
		ConcentrateTaxiWizard.IsShowLandMark = false;
		LandMarkSpotFrame.Hide();
		ConcentrateTaxiWizard.Hide();
		TaxiSearchPane.Trace = -1;
		Screen.repaint();
		this.setTitle("CityGeoInfo");
	}

	// ----------------------------------------------------
	class NULLPaneClass extends ToolPanel {// 空面板，作为默认的显示面板
		public void setLongitude(double Longitude) {
		}

		public void setLatitude(double Latitude) {
		}

		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("BackGround1.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		public String getString() {
			return "NULL";
		}

		public NULLPaneClass() {
		}
	}

	// ------------------------------------------------------
	class CalibratePaneClass extends ToolPanel implements ActionListener {
		// 实现GPS误差统计的功能，主要面向测绘和校准功能
		public String getString() {
			return "CalibratePane";
		}

		public void setLongitude(double Longitude) {
			LongitudeText.setText(java.lang.Double.toString(Longitude));
		}

		public void setLatitude(double Latitude) {
			LatitudeText.setText(java.lang.Double.toString(Latitude));
		}

		JTextField LongitudeText, LatitudeText, X1Text, Y1Text, X2Text, Y2Text;
		JTextField AveDeltaLongitude, AveDeltaLatitude, AveDeltaNorth,
				AveDeltaEast;
		JRadioButton SetPoint1, SetPoint2, GoalPoint1, GoalPoint2;
		JButton ForbidAddPoint, PermitAddPoint;
		JButton RemoveSelectedPoints, RemoveInvolvedPoints, ShowDirection,
				ShowResult;
		boolean CanAdd = true;

		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("BackGround2.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		public CalibratePaneClass() {
			JLabel Title = new JLabel(LanguageDic.GetWords("GPS偏差校准工具栏"));
			Title.setFont(new Font("华文新魏", Font.BOLD, 25));
			add(Title);
			LongitudeText = new JTextField(15);
			LatitudeText = new JTextField(15);
			ScreenLockButton = new JButton(LanguageDic.GetWords("锁住屏幕"));
			ScreenLockButton.addActionListener(this);
			ScreenUnLockButton = new JButton(LanguageDic.GetWords("解锁屏幕"));
			ScreenUnLockButton.addActionListener(this);
			X1Text = new JTextField(15);
			Y1Text = new JTextField(15);
			X2Text = new JTextField(15);
			Y2Text = new JTextField(15);

			add(new JLabel(LanguageDic.GetWords("鼠标指向经度")));
			add(LongitudeText);
			add(new JLabel(LanguageDic.GetWords("鼠标指向纬度")));
			add(LatitudeText);
			add(ScreenLockButton);
			add(new JLabel("    "));
			add(ScreenUnLockButton);

			ForbidAddPoint = new JButton(LanguageDic.GetWords("禁止插入新点"));
			ForbidAddPoint.addActionListener(this);
			add(ForbidAddPoint);

			PermitAddPoint = new JButton(LanguageDic.GetWords("允许插入新点"));
			PermitAddPoint.addActionListener(this);
			add(PermitAddPoint);

			RemoveSelectedPoints = new JButton(LanguageDic.GetWords("删除被选中的点"));
			RemoveSelectedPoints.addActionListener(this);
			add(RemoveSelectedPoints);

			RemoveInvolvedPoints = new JButton(LanguageDic.GetWords("删除被影响的点"));
			RemoveInvolvedPoints.addActionListener(this);
			add(RemoveInvolvedPoints);

			ShowDirection = new JButton(LanguageDic.GetWords("显示偏差向量"));
			ShowDirection.addActionListener(this);
			add(ShowDirection);

			ShowResult = new JButton(LanguageDic.GetWords("显示统计结果"));
			ShowResult.addActionListener(this);
			add(ShowResult);

			JLabel Tip1 = new JLabel(LanguageDic.GetWords("[温馨提示]拖拽选中"));
			JLabel Tip2 = new JLabel(LanguageDic.GetWords("需要锁屏和禁止加点"));
			Tip1.setForeground(Color.red);
			Tip2.setForeground(Color.red);
			Tip1.setFont(new Font("华文新魏", Font.BOLD, 25));
			Tip2.setFont(new Font("华文新魏", Font.BOLD, 25));
			add(Tip1);
			add(Tip2);

			add(new JLabel(LanguageDic.GetWords("平均GPS经度差  ")));
			AveDeltaLongitude = new JTextField(15);
			add(AveDeltaLongitude);

			add(new JLabel(LanguageDic.GetWords("平均GPS纬度差  ")));
			AveDeltaLatitude = new JTextField(15);
			add(AveDeltaLatitude);

			add(new JLabel(LanguageDic.GetWords("球面投影北偏/米")));
			AveDeltaNorth = new JTextField(15);
			add(AveDeltaNorth);

			add(new JLabel(LanguageDic.GetWords("球面投影东偏/米")));
			AveDeltaEast = new JTextField(15);
			add(AveDeltaEast);

			if (Screen.lock) {
				ScreenLockButton.setEnabled(false);
				ScreenUnLockButton.setEnabled(true);
			} else {
				ScreenLockButton.setEnabled(true);
				ScreenUnLockButton.setEnabled(false);
			}

			if (CanAdd) {
				ForbidAddPoint.setEnabled(true);
				PermitAddPoint.setEnabled(false);
			} else {
				ForbidAddPoint.setEnabled(false);
				PermitAddPoint.setEnabled(true);
			}

		}

		double x1, y1, x2, y2;

		public void setClickLongitude(double Longitude) {
			if (SetPoint1.isSelected()) {
				x1 = Longitude;
				X1Text.setText(java.lang.Double.toString(Longitude));
			} else if (SetPoint2.isSelected()) {
				x2 = Longitude;
				X2Text.setText(java.lang.Double.toString(Longitude));
			}
		}

		public void setClickLatitude(double Latitude) {
			if (SetPoint1.isSelected()) {
				y1 = Latitude;
				Y1Text.setText(java.lang.Double.toString(Latitude));
			} else if (SetPoint2.isSelected()) {
				y2 = Latitude;
				Y2Text.setText(java.lang.Double.toString(Latitude));
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ScreenLockButton) {
				Screen.setLock(true);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ScreenUnLockButton) {
				Screen.setLock(false);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == PermitAddPoint) {
				CanAdd = true;
				if (CanAdd) {
					ForbidAddPoint.setEnabled(true);
					PermitAddPoint.setEnabled(false);
				} else {
					ForbidAddPoint.setEnabled(false);
					PermitAddPoint.setEnabled(true);
				}
			} else if (e.getSource() == ForbidAddPoint) {
				CanAdd = false;
				if (CanAdd) {
					ForbidAddPoint.setEnabled(true);
					PermitAddPoint.setEnabled(false);
				} else {
					ForbidAddPoint.setEnabled(false);
					PermitAddPoint.setEnabled(true);
				}
			} else if (e.getSource() == RemoveSelectedPoints) {
				int temp, j;
				temp = 0;
				j = 1;
				for (int i = 0; i < Screen.XYCount; i++) {
					if (j <= Screen.SelectedPointList[0]) {
						if (Screen.SelectedPointList[j] > i) {
							Screen.Xlist[temp] = Screen.Xlist[i];
							Screen.Ylist[temp] = Screen.Ylist[i];
							temp++;
						} else if (Screen.SelectedPointList[j] == i) {
							j++;
						} else if (Screen.SelectedPointList[j] < i) {
							j++;
						}
					} else {
						Screen.Xlist[temp] = Screen.Xlist[i];
						Screen.Ylist[temp] = Screen.Ylist[i];
						temp++;
					}
				}
				Screen.XYCount = temp;
				Screen.SelectedPointList[0] = 0;
				Screen.repaint();
			} else if (e.getSource() == RemoveInvolvedPoints) {
				if (Screen.SelectedPointList[0] > 0) {
					Screen.XYCount = Screen.SelectedPointList[1];
				}
				Screen.SelectedPointList[0] = 0;
				Screen.repaint();
			} else if (e.getSource() == ShowDirection) {
				Screen.IsShowDirection = true;
				Screen.repaint();
			} else if (e.getSource() == ShowResult) {
				double x1, x2, y1, y2;
				double dx, dy, disx, disy;
				double totdx = 0, totdy = 0, totdisx = 0, totdisy = 0;
				for (int i = 0; i < Screen.XYCount; i++) {
					if (i % 2 == 1) {
						x1 = Screen.Xlist[i - 1];
						y1 = Screen.Ylist[i - 1];
						x2 = Screen.Xlist[i];
						y2 = Screen.Ylist[i];
						dx = x2 - x1;
						dy = y2 - y1;
						disx = (x2 - x1) / 180 * Math.PI * 6371 * 1000
								* Math.cos((y1 + y2) / 360 * Math.PI);
						disy = (y2 - y1) / 180 * Math.PI * 6371 * 1000;
						totdx += dx;
						totdy += dy;
						totdisx += disx;
						totdisy += disy;
					}
				}
				totdx /= (Screen.XYCount / 2);
				totdy /= (Screen.XYCount / 2);
				totdisx /= (Screen.XYCount / 2);
				totdisy /= (Screen.XYCount / 2);
				AveDeltaLongitude.setText(java.lang.Double.toString(totdx));
				AveDeltaLatitude.setText(java.lang.Double.toString(totdy));
				AveDeltaEast.setText(java.lang.Double.toString(totdisx));
				AveDeltaNorth.setText(java.lang.Double.toString(totdisy));
			}
		}
	}

	// ------------------------------------------------------
	class BasicInfoPaneClass extends ToolPanel implements ActionListener {
		// 实现基本信息工具栏的功能，主要是负责只读地查询数据库，并且幻灯片式地放映相关的地标信息
		public String getString() {
			return "BasicInfoPane";
		}

		class Slide extends Canvas {
			String PhotoName = null;

			public Slide() {
				setBounds(0, 0, 240, 200);
			}

			public void paint(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("DefaultPhoto.jpg");
				if ((ImageDir != null) && (PhotoName != null)) {
					System.out.println(ImageDir.toString() + "/" + PhotoName);
					File f = new File(ImageDir, PhotoName);
					if (f.exists()) {
						img = kit.getImage(ImageDir.toString() + "/"
								+ PhotoName);
					}
				}
				g.drawImage(img, 0, 0, 240, 200, this);
			}
		}

		public void setLongitude(double Longitude) {
			LongitudeText.setText(java.lang.Double.toString(Longitude));
		}

		public void setLatitude(double Latitude) {
			LatitudeText.setText(java.lang.Double.toString(Latitude));
		}

		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("BackGround5.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		JTextField LongitudeText, LatitudeText, LandMarkName, LandMarkType;
		JTextArea LandMarkScript;
		Slide LandMarkSlide;
		JButton pred, succ;
		int SlideID = -1;

		public BasicInfoPaneClass() {
			JLabel Title = new JLabel(LanguageDic.GetWords("地理信息工具栏"));
			Title.setFont(new Font("华文新魏", Font.BOLD, 30));
			add(Title);
			LongitudeText = new JTextField(15);
			LatitudeText = new JTextField(15);
			ScreenLockButton = new JButton(LanguageDic.GetWords("锁住屏幕"));
			ScreenLockButton.addActionListener(this);
			ScreenUnLockButton = new JButton(LanguageDic.GetWords("解锁屏幕"));
			ScreenUnLockButton.addActionListener(this);

			add(new JLabel(LanguageDic.GetWords("鼠标指向经度")));
			add(LongitudeText);
			add(new JLabel(LanguageDic.GetWords("鼠标指向纬度")));
			add(LatitudeText);
			add(ScreenLockButton);
			add(ScreenUnLockButton);

			LandMarkSlide = new Slide();
			LandMarkName = new JTextField(22);
			LandMarkType = new JTextField(22);
			LandMarkScript = new JTextArea(8, 22);
			pred = new JButton(LanguageDic.GetWords("上一个"));
			succ = new JButton(LanguageDic.GetWords("下一个"));
			pred.addActionListener(this);
			succ.addActionListener(this);

			add(LandMarkSlide);
			add(LandMarkName);
			add(LandMarkType);
			LandMarkScript.setLineWrap(true);
			add(new JScrollPane(LandMarkScript,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
			add(pred);
			add(succ);

			if (Screen.lock) {
				ScreenLockButton.setEnabled(false);
				ScreenUnLockButton.setEnabled(true);
			} else {
				ScreenLockButton.setEnabled(true);
				ScreenUnLockButton.setEnabled(false);
			}
		}

		public void FullFillSlide() {
			if (SlideID == -1)
				return;
			LandMarkSlide.PhotoName = GPSPoints.LandMarkName[SlideID] + ".jpg";
			LandMarkName.setText(GPSPoints.LandMarkName[SlideID]);
			LandMarkType.setText(GPSPoints.LandMarkType[SlideID]);
			LandMarkScript.setText(GPSPoints.LandMarkScript[SlideID]);
			LandMarkSlide.repaint();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ScreenLockButton) {
				Screen.setLock(true);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ScreenUnLockButton) {
				Screen.setLock(false);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == succ) {
				if (Screen.LandMarkSelectedNum == 0)
					return;
				if (SlideID == -1)
					return;
				int temp = -1;
				for (int i = SlideID + 1; i < GPSPoints.LandMarkNum; i++) {
					if (Screen.IsLandMarkSelected[i]) {
						temp = i;
						break;
					}
				}
				if (temp == -1) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("已经到尾元素"),
							LanguageDic.GetWords("遭遇尾元素"),
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					SlideID = temp;
					FullFillSlide();
				}
			} else if (e.getSource() == pred) {
				if (Screen.LandMarkSelectedNum == 0)
					return;
				if (SlideID == -1)
					return;
				int temp = -1;
				for (int i = SlideID - 1; i >= 0; i--) {
					if (Screen.IsLandMarkSelected[i]) {
						temp = i;
						break;
					}
				}
				if (temp == -1) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("已经到首元素"),
							LanguageDic.GetWords("遭遇首元素"),
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					SlideID = temp;
					FullFillSlide();
				}
			}
		}
	}

	// --------------------------------------------------
	class TaxiSearchPaneClass extends ToolPanel implements ActionListener {
		public String getString() {
			return "TaxiSearchPane";
		}

		// 负责实现出租车查询工具栏的有关功能，为用户追踪多辆出租车
		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("sky.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		public void setLongitude(double Longitude) {
			LongitudeText.setText(java.lang.Double.toString(Longitude));
		}

		public void setLatitude(double Latitude) {
			LatitudeText.setText(java.lang.Double.toString(Latitude));
		}

		boolean[] IsShowTaxi = new boolean[100000];
		JTextField LongitudeText, LatitudeText, CenterLongitude,
				CenterLatitude, RadiusDis, TaxiCount, TheNearestDis;
		JButton ShowSelectedTaxi, NotShowSelectedTaxi, TraceTheNearest,
				StopTrace, DataBaseReference, TraceFree;
		double centerlongitude, centerlatitude, centerradius;
		boolean CanShowTaxi = false;
		int Trace = -1;

		public void FreshIsShowTaxi() {
			for (int i = 0; i < GPSPoints.TaxiNum; i++) {
				IsShowTaxi[i] = false;
			}
		}

		public TaxiSearchPaneClass() {
			ConcentrateTaxiWizard = new ConcentrateTaxiWizardClass();
			JLabel Title = new JLabel(LanguageDic.GetWords("出租车定位工具栏"));
			Title.setFont(new Font("华文新魏", Font.BOLD, 30));
			add(Title);
			LongitudeText = new JTextField(15);
			LatitudeText = new JTextField(15);
			ScreenLockButton = new JButton(LanguageDic.GetWords("锁住屏幕"));
			ScreenLockButton.addActionListener(this);
			ScreenUnLockButton = new JButton(LanguageDic.GetWords("解锁屏幕"));
			ScreenUnLockButton.addActionListener(this);

			add(new JLabel(LanguageDic.GetWords("鼠标指向经度")));
			add(LongitudeText);
			add(new JLabel(LanguageDic.GetWords("鼠标指向纬度")));
			add(LatitudeText);
			add(ScreenLockButton);
			add(new JLabel("      "));
			add(ScreenUnLockButton);

			if (Screen.lock) {
				ScreenLockButton.setEnabled(false);
				ScreenUnLockButton.setEnabled(true);
			} else {
				ScreenLockButton.setEnabled(true);
				ScreenUnLockButton.setEnabled(false);
			}

			CenterLongitude = new JTextField(15);
			CenterLatitude = new JTextField(15);
			RadiusDis = new JTextField(15);

			ShowSelectedTaxi = new JButton(LanguageDic.GetWords("在地图上显示出租车"));
			NotShowSelectedTaxi = new JButton(LanguageDic.GetWords("在地图上清除出租车"));
			ShowSelectedTaxi.addActionListener(this);
			NotShowSelectedTaxi.addActionListener(this);
			DataBaseReference = new JButton(
					LanguageDic.GetWords("利用数据库进行经纬度坐标选择"));
			DataBaseReference.addActionListener(this);

			add(new JLabel(LanguageDic.GetWords("所在点经度     ")));
			add(CenterLongitude);
			add(new JLabel(LanguageDic.GetWords("所在点纬度     ")));
			add(CenterLatitude);
			add(new JLabel(LanguageDic.GetWords("查询的半径/米")));
			add(RadiusDis);
			add(ShowSelectedTaxi);
			add(NotShowSelectedTaxi);
			add(DataBaseReference);

			TaxiCount = new JTextField(5);
			add(new JLabel(LanguageDic.GetWords("当前区域内的出租车数量    ")));
			add(TaxiCount);

			TheNearestDis = new JTextField(12);
			add(new JLabel(LanguageDic.GetWords("最近出租车/米")));
			add(TheNearestDis);

			TraceTheNearest = new JButton(
					LanguageDic.GetWords("跟踪此刻离你最近的一辆出租车"));
			StopTrace = new JButton(LanguageDic.GetWords("放弃跟踪最近的一辆出租车"));
			TraceFree = new JButton(LanguageDic.GetWords("打开/关闭出租车自由追踪面板"));

			add(TraceTheNearest);
			add(StopTrace);
			add(TraceFree);

			TraceTheNearest.setEnabled(Trace >= 0 ? false : true);
			StopTrace.setEnabled(Trace >= 0 ? true : false);

			TraceTheNearest.addActionListener(this);
			StopTrace.addActionListener(this);

			TraceFree.addActionListener(this);

			RadiusDis.setText("5000");
			FreshIsShowTaxi();
		}

		public void GetGPSInfo() {
			int tempsecond = ClockWizard.pic.getSecond();
			int tempMinute = ClockWizard.pic.getMinute();
			int tempHour = ClockWizard.pic.getHour();
			GPSPoints.TaxiHeapPop(tempHour * 3600 + tempMinute * 60
					+ tempsecond);
			for (int i = 0; i < GPSPoints.TaxiNum; i++)
				IsShowTaxi[i] = false;
			double tempdis, mindis = 1e100;
			int best = -1;
			for (int i = 1; i <= GPSPoints.HeapResult[0]; i++) {
				int k = GPSPoints.HeapResult[i];
				double xx = GPSPoints.TaxiLongitudeList[GPSPoints.TaxiPtr[k] - 1];
				double yy = GPSPoints.TaxiLatitudeList[GPSPoints.TaxiPtr[k] - 1];
				if (GPSPoints.Distance(centerlongitude, centerlatitude, xx, yy) < centerradius) {
					IsShowTaxi[k] = true;
				}
			}
			Screen.XYCount = 0;
			for (int i = 0; i < GPSPoints.TaxiNum; i++) {
				if (!IsShowTaxi[i])
					continue;
				double xx = GPSPoints.TaxiLongitudeList[GPSPoints.TaxiPtr[i] - 1];
				double yy = GPSPoints.TaxiLatitudeList[GPSPoints.TaxiPtr[i] - 1];
				if ((tempdis = GPSPoints.Distance(centerlongitude,
						centerlatitude, xx, yy)) < mindis) {
					mindis = tempdis;
					best = i;
				}
				Screen.Xlist[Screen.XYCount] = xx;
				Screen.Ylist[Screen.XYCount] = yy;
				Screen.TempString[Screen.XYCount] = GPSPoints.TaxiID[i];
				Screen.XYCount++;
				if (Trace - 1 == i) {
					Screen.MoveMiddle(xx, yy);
				}
			}
			TaxiCount.setText(Integer.toString(Screen.XYCount));
			if ((Trace == 0) && (best != -1)) {
				double xx = GPSPoints.TaxiLongitudeList[GPSPoints.TaxiPtr[best] - 1];
				double yy = GPSPoints.TaxiLatitudeList[GPSPoints.TaxiPtr[best] - 1];
				TheNearestDis.setText(Integer.toString((int) mindis));
				Screen.MoveMiddle(xx, yy);
			}

			TraceTheNearest.setEnabled(Trace >= 0 ? false : true);
			StopTrace.setEnabled(Trace >= 0 ? true : false);

			if (ConcentrateTaxiWizard.IsShowLandMark) {
				mindis = 1e100;
				best = -1;
				double xx = Screen.ScreenLongitude + Screen.LongitudeScale / 2;
				double yy = Screen.ScreenLatitude - Screen.LatitudeScale / 2;
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					tempdis = GPSPoints.Distance(
							GPSPoints.LandMarkLongitude[i],
							GPSPoints.LandMarkLatitude[i], xx, yy);
					if (tempdis < mindis) {
						mindis = tempdis;
						best = i;
					}
				}
				if (best >= 0)
					Screen.ScreenHint = Integer.toString(ClockWizard.pic
							.getHour())
							+ LanguageDic.GetWords("时")
							+ Integer.toString(ClockWizard.pic.getMinute())
							+ LanguageDic.GetWords("分")
							+ Integer.toString(ClockWizard.pic.getSecond())
							+ LanguageDic.GetWords("秒状况：")
							+ LanguageDic.GetWords("距离")
							+ "["
							+ GPSPoints.LandMarkName[best]
							+ "]"
							+ LanguageDic.GetWords("有 ")
							+ Integer.toString((int) mindis)
							+ LanguageDic.GetWords(" 米");
				else
					Screen.ScreenHint = LanguageDic.GetWords(LanguageDic
							.GetWords("未收到返回信号"));
				Screen.showDirection(xx, yy, GPSPoints.LandMarkLongitude[best],
						GPSPoints.LandMarkLatitude[best]);
				LandMarkSpotFrame.Hide();
				LandMarkSpotFrame.emerge(best);
			}
			Screen.repaint();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ScreenLockButton) {
				Screen.setLock(true);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ScreenUnLockButton) {
				Screen.setLock(false);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ShowSelectedTaxi) {
				try {
					centerlongitude = java.lang.Double
							.parseDouble(CenterLongitude.getText());
					centerlatitude = java.lang.Double
							.parseDouble(CenterLatitude.getText());
					centerradius = java.lang.Double.parseDouble(RadiusDis
							.getText());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("输入的数据有误"),
							LanguageDic.GetWords("数据异常"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				Screen.XYCount = 0;
				FreshIsShowTaxi();
				double w = 2 * centerradius / 6371 / 1000
						/ Math.cos(centerlatitude / 180 * Math.PI) / Math.PI
						* 180;
				double h = 2 * centerradius / 6371 / 1000 / Math.PI * 180;
				Screen.setShowLargeRegion(centerlongitude, centerlatitude, w, h);
				Screen.ShowLargeRegion = true;
				CanShowTaxi = true;
				if (CanShowTaxi == true) {
					ShowSelectedTaxi.setEnabled(false);
					NotShowSelectedTaxi.setEnabled(true);
				} else {
					ShowSelectedTaxi.setEnabled(true);
					NotShowSelectedTaxi.setEnabled(false);
				}
			} else if (e.getSource() == NotShowSelectedTaxi) {
				CanShowTaxi = false;
				if (CanShowTaxi == true) {
					ShowSelectedTaxi.setEnabled(false);
					NotShowSelectedTaxi.setEnabled(true);
				} else {
					ShowSelectedTaxi.setEnabled(true);
					NotShowSelectedTaxi.setEnabled(false);
				}
				ClearStateAfterSwitchPane();
				Screen.repaint();
			} else if (e.getSource() == TraceTheNearest) {
				Trace = 0;
				TraceTheNearest.setEnabled(Trace >= 0 ? false : true);
				StopTrace.setEnabled(Trace >= 0 ? true : false);
				TheNearestDis.setEnabled(true);
				ConcentrateTaxiWizard.Hide();
			} else if (e.getSource() == StopTrace) {
				Trace = -1;
				TraceTheNearest.setEnabled(Trace >= 0 ? false : true);
				StopTrace.setEnabled(Trace >= 0 ? true : false);
				TheNearestDis.setEnabled(false);
				ConcentrateTaxiWizard.Hide();
			} else if (e.getSource() == DataBaseReference) {
				LandMarkQueryFrame.Pic.Handle.setVisible(false);
				LandMarkQueryFrame.Pic.DeleteRow.setVisible(false);
				LandMarkQueryFrame.Pic.Delete.setVisible(false);
				LandMarkQueryFrame.Pic.UpdateRow.setVisible(false);
				LandMarkQueryFrame.Pic.Update.setVisible(false);
				LandMarkQueryFrame.Pic.MoreInfo.setVisible(false);
				LandMarkQueryFrame.setVisible(true);
			} else if (e.getSource() == TraceFree) {
				if (ShowSelectedTaxi.isEnabled())
					return;
				if (ConcentrateTaxiWizard.isVisible())
					ConcentrateTaxiWizard.Hide();
				else
					ConcentrateTaxiWizard.emerge();
			}
		}
	}

	// ------------------------------------------------
	class TwoPointPaneClass extends ToolPanel implements ActionListener {
		public String getString() {
			return "TwoPointPane";
		}

		// 实现两点定位工具栏的功能，测量地图上两点的球面距离
		public void setLongitude(double Longitude) {
			LongitudeText.setText(java.lang.Double.toString(Longitude));
		}

		public void setLatitude(double Latitude) {
			LatitudeText.setText(java.lang.Double.toString(Latitude));
		}

		JTextField LongitudeText, LatitudeText, X1Text, Y1Text, X2Text, Y2Text;
		JTextField DeltaLongitude, DeltaLatitude, DeltaNorth, DeltaEast,
				DeltaDis;
		JRadioButton SetPoint1, SetPoint2, GoalPoint1, GoalPoint2;
		JButton ComputeButton, DecideButton, MoveScreenButton;
		ButtonGroup group, grounp1;
		boolean CanChange = true;

		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("BackGround8.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		public TwoPointPaneClass() {
			JLabel Title = new JLabel(LanguageDic.GetWords("两点定位工具栏"));
			Title.setFont(new Font("华文新魏", Font.BOLD, 30));
			add(Title);
			LongitudeText = new JTextField(15);
			LatitudeText = new JTextField(15);
			ScreenLockButton = new JButton(LanguageDic.GetWords("锁住屏幕"));
			ScreenLockButton.addActionListener(this);
			ScreenUnLockButton = new JButton(LanguageDic.GetWords("解锁屏幕"));
			ScreenUnLockButton.addActionListener(this);
			X1Text = new JTextField(15);
			Y1Text = new JTextField(15);
			X2Text = new JTextField(15);
			Y2Text = new JTextField(15);

			add(new JLabel(LanguageDic.GetWords("鼠标指向经度")));
			add(LongitudeText);
			add(new JLabel(LanguageDic.GetWords("鼠标指向纬度")));
			add(LatitudeText);
			add(ScreenLockButton);
			add(new JLabel("    "));
			add(ScreenUnLockButton);
			add(new JLabel(LanguageDic.GetWords("第一个点的经度")));
			add(X1Text);
			add(new JLabel(LanguageDic.GetWords("第一个点的纬度")));
			add(Y1Text);
			add(new JLabel(LanguageDic.GetWords("第二个点的经度")));
			add(X2Text);
			add(new JLabel(LanguageDic.GetWords("第二个点的纬度")));
			add(Y2Text);

			group = new ButtonGroup();
			SetPoint1 = new JRadioButton(LanguageDic.GetWords("当前修改第一个点"));
			SetPoint1.setOpaque(false);
			SetPoint2 = new JRadioButton(LanguageDic.GetWords("当前修改第二个点"));
			SetPoint2.setOpaque(false);
			group.add(SetPoint1);
			group.add(SetPoint2);
			add(SetPoint1);
			add(SetPoint2);
			SetPoint1.setSelected(true);

			group = new ButtonGroup();
			GoalPoint1 = new JRadioButton(LanguageDic.GetWords("以第一点为终点"));
			GoalPoint1.setOpaque(false);
			GoalPoint2 = new JRadioButton(LanguageDic.GetWords("以第二点为终点"));
			GoalPoint2.setOpaque(false);
			group.add(GoalPoint1);
			group.add(GoalPoint2);
			add(GoalPoint1);
			add(GoalPoint2);
			GoalPoint2.setSelected(true);

			add(new JLabel(LanguageDic.GetWords("终点-起点经度差")));
			DeltaLongitude = new JTextField(15);
			add(DeltaLongitude);

			add(new JLabel(LanguageDic.GetWords("终点-起点纬度差")));
			DeltaLatitude = new JTextField(15);
			add(DeltaLatitude);

			add(new JLabel(LanguageDic.GetWords("终点在起点北/米")));
			DeltaNorth = new JTextField(15);
			add(DeltaNorth);

			add(new JLabel(LanguageDic.GetWords("终点在起点东/米")));
			DeltaEast = new JTextField(15);
			add(DeltaEast);

			add(new JLabel(LanguageDic.GetWords("两点间的距离/米")));
			DeltaDis = new JTextField(15);
			add(DeltaDis);

			DecideButton = new JButton(LanguageDic.GetWords("锁定/解锁起点终点"));
			DecideButton.addActionListener(this);
			add(DecideButton);

			ComputeButton = new JButton(LanguageDic.GetWords("计算位置参数"));
			ComputeButton.addActionListener(this);
			add(ComputeButton);

			MoveScreenButton = new JButton(
					LanguageDic.GetWords("移动视角到选择的两点区域内"));
			MoveScreenButton.addActionListener(this);
			add(MoveScreenButton);

			if (Screen.lock) {
				ScreenLockButton.setEnabled(false);
				ScreenUnLockButton.setEnabled(true);
			} else {
				ScreenLockButton.setEnabled(true);
				ScreenUnLockButton.setEnabled(false);
			}

		}

		double x1, y1, x2, y2;

		public void setClickLongitude(double Longitude) {
			if (SetPoint1.isSelected()) {
				x1 = Longitude;
				X1Text.setText(java.lang.Double.toString(Longitude));
			} else if (SetPoint2.isSelected()) {
				x2 = Longitude;
				X2Text.setText(java.lang.Double.toString(Longitude));
			}
		}

		public void setClickLatitude(double Latitude) {
			if (SetPoint1.isSelected()) {
				y1 = Latitude;
				Y1Text.setText(java.lang.Double.toString(Latitude));
			} else if (SetPoint2.isSelected()) {
				y2 = Latitude;
				Y2Text.setText(java.lang.Double.toString(Latitude));
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ScreenLockButton) {
				Screen.setLock(true);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ScreenUnLockButton) {
				Screen.setLock(false);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ComputeButton) {
				try {
					double tempx1 = java.lang.Double.parseDouble(X1Text
							.getText());
					double tempy1 = java.lang.Double.parseDouble(Y1Text
							.getText());
					double tempx2 = java.lang.Double.parseDouble(X2Text
							.getText());
					double tempy2 = java.lang.Double.parseDouble(Y2Text
							.getText());
					if (GoalPoint1.isSelected()) {
						tempx1 = java.lang.Double.parseDouble(X2Text.getText());
						tempy1 = java.lang.Double.parseDouble(Y2Text.getText());
						tempx2 = java.lang.Double.parseDouble(X1Text.getText());
						tempy2 = java.lang.Double.parseDouble(Y1Text.getText());
					}
					Screen.showDirection(tempx1, tempy1, tempx2, tempy2);
					Screen.XYCount = 2;
					Screen.Xlist[0] = tempx1;
					Screen.Ylist[0] = tempy1;
					Screen.Xlist[1] = tempx2;
					Screen.Ylist[1] = tempy2;
					double delta_longitude = tempx2 - tempx1;
					double delta_latitude = tempy2 - tempy1;
					double delta_north = delta_latitude / 180 * Math.PI * 6371
							* 1000;
					double delta_east = delta_longitude / 180 * Math.PI * 6371
							* 1000
							* Math.cos((tempy1 + tempy2) / 360 * Math.PI);
					double delta_dis = Math.sqrt(delta_north * delta_north
							+ delta_east * delta_east);

					DeltaLongitude.setText(java.lang.Double
							.toString(delta_longitude));
					DeltaLatitude.setText(java.lang.Double
							.toString(delta_latitude));
					DeltaNorth.setText(java.lang.Double.toString(delta_north));
					DeltaEast.setText(java.lang.Double.toString(delta_east));
					DeltaDis.setText(java.lang.Double.toString(delta_dis));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("您的经纬度输入不正确"),
							LanguageDic.GetWords("数据安全提示"),
							JOptionPane.WARNING_MESSAGE);
				}
			} else if (e.getSource() == DecideButton) {
				CanChange = CanChange ? false : true;
				if (CanChange) {
					X1Text.setEnabled(true);
					X2Text.setEnabled(true);
					Y1Text.setEnabled(true);
					Y2Text.setEnabled(true);
				} else {
					X1Text.setEnabled(false);
					X2Text.setEnabled(false);
					Y1Text.setEnabled(false);
					Y2Text.setEnabled(false);
				}
			} else if (e.getSource() == MoveScreenButton) {
				if (X1Text.isEnabled()) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("您没有锁定两点，请按锁定按钮！！！"),
							LanguageDic.GetWords("数据安全提示"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					X1Text.setEnabled(true);
					X2Text.setEnabled(true);
					Y1Text.setEnabled(true);
					Y2Text.setEnabled(true);
					double tempx1 = java.lang.Double.parseDouble(X1Text
							.getText());
					double tempy1 = java.lang.Double.parseDouble(Y1Text
							.getText());
					double tempx2 = java.lang.Double.parseDouble(X2Text
							.getText());
					double tempy2 = java.lang.Double.parseDouble(Y2Text
							.getText());
					double midx = (tempx1 + tempx2) / 2;
					double midy = (tempy1 + tempy2) / 2;
					Screen.MoveMiddle(midx, midy);
					Screen.repaint();
					Idle(2000);
					Screen.MiddleReSize(Math.abs(tempx2 - tempx1),
							Math.abs(tempy2 - tempy1));
					Screen.repaint();
					CanChange = true;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("您的经纬度输入不正确"),
							LanguageDic.GetWords("数据安全提示"),
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

	// --------------------------------------
	class RouteSearchPaneClass extends ToolPanel implements ActionListener,
			ItemListener {
		public String getString() {
			return "RouteSearchPane";
		}

		// 为用户提供路径规划的功能，需要读取和处理底层的路网数据
		public void setLongitude(double Longitude) {
			LongitudeText.setText(java.lang.Double.toString(Longitude));
		}

		public void setLatitude(double Latitude) {
			LatitudeText.setText(java.lang.Double.toString(Latitude));
		}

		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("BackGround10.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		JTextField LongitudeText, LatitudeText, SourceLongitude,
				SourceLatitude, TerminalLongitude, TerminalLatitude, PathNum,
				PathLength;
		JRadioButton setSource, setTerminal;
		JButton ShowPath, VeilPath, LastPath, NextPath, DataBaseReference;
		JCheckBox TheShortestPath;
		boolean PathOnScreen = false, FocusSource = true;

		public RouteSearchPaneClass() {
			JLabel Title = new JLabel(LanguageDic.GetWords("路径规划工具栏"));
			Title.setFont(new Font("华文新魏", Font.BOLD, 30));
			Title.setForeground(Color.orange);
			add(Title);
			LongitudeText = new JTextField(15);
			LatitudeText = new JTextField(15);
			ScreenLockButton = new JButton(LanguageDic.GetWords("锁住屏幕"));
			ScreenLockButton.addActionListener(this);
			ScreenUnLockButton = new JButton(LanguageDic.GetWords("解锁屏幕"));
			ScreenUnLockButton.addActionListener(this);

			JLabel Label1 = new JLabel(LanguageDic.GetWords("鼠标指向经度"));
			Label1.setForeground(Color.orange);
			add(Label1);
			add(LongitudeText);
			JLabel Label2 = new JLabel(LanguageDic.GetWords("鼠标指向纬度"));
			Label2.setForeground(Color.orange);
			add(Label2);
			add(LatitudeText);
			add(ScreenLockButton);
			add(new JLabel("      "));
			add(ScreenUnLockButton);

			JLabel Label3 = new JLabel(LanguageDic.GetWords("路径起点经度"));
			JLabel Label4 = new JLabel(LanguageDic.GetWords("路径起点纬度"));
			JLabel Label5 = new JLabel(LanguageDic.GetWords("路径终点经度"));
			JLabel Label6 = new JLabel(LanguageDic.GetWords("路径终点纬度"));
			Label3.setForeground(Color.white);
			Label4.setForeground(Color.white);
			Label5.setForeground(Color.white);
			Label6.setForeground(Color.white);

			SourceLongitude = new JTextField(15);
			SourceLatitude = new JTextField(15);
			TerminalLongitude = new JTextField(15);
			TerminalLatitude = new JTextField(15);

			add(Label3);
			add(SourceLongitude);
			add(Label4);
			add(SourceLatitude);
			add(Label5);
			add(TerminalLongitude);
			add(Label6);
			add(TerminalLatitude);

			DataBaseReference = new JButton(
					LanguageDic.GetWords("利用数据库进行经纬度坐标选择"));
			DataBaseReference.addActionListener(this);
			add(DataBaseReference);

			setSource = new JRadioButton(LanguageDic.GetWords("当前设置路径起点"));
			setTerminal = new JRadioButton(LanguageDic.GetWords("当前设置路径终点"));
			ButtonGroup group = new ButtonGroup();
			group.add(setSource);
			group.add(setTerminal);
			add(setSource);
			add(setTerminal);
			setSource.setSelected(true);

			setSource.addItemListener(this);
			setTerminal.addItemListener(this);

			setSource.setOpaque(false);
			setTerminal.setOpaque(false);

			TheShortestPath = new JCheckBox(LanguageDic.GetWords("最短路"));
			TheShortestPath.setOpaque(false);
			ShowPath = new JButton(LanguageDic.GetWords("显示路径"));
			VeilPath = new JButton(LanguageDic.GetWords("隐藏路径"));

			add(ShowPath);
			add(TheShortestPath);
			add(VeilPath);

			TheShortestPath.setSelected(true);
			ShowPath.addActionListener(this);
			VeilPath.addActionListener(this);

			PathLength = new JTextField(15);
			JLabel l0 = new JLabel(LanguageDic.GetWords("当前总里程/米"));
			l0.setForeground(Color.white);
			add(l0);
			add(PathLength);

			PathNum = new JTextField(3);
			LastPath = new JButton(LanguageDic.GetWords("上一条"));
			NextPath = new JButton(LanguageDic.GetWords("下一条"));

			add(LastPath);
			add(PathNum);
			add(NextPath);

			LastPath.addActionListener(this);
			NextPath.addActionListener(this);

			if (Screen.lock) {
				ScreenLockButton.setEnabled(false);
				ScreenUnLockButton.setEnabled(true);
			} else {
				ScreenLockButton.setEnabled(true);
				ScreenUnLockButton.setEnabled(false);
			}

			if (PathOnScreen) {
				ShowPath.setEnabled(false);
				VeilPath.setEnabled(true);
			} else {
				ShowPath.setEnabled(true);
				VeilPath.setEnabled(false);
			}

			if (FocusSource) {
				SourceLongitude.setEnabled(true);
				SourceLatitude.setEnabled(true);
				TerminalLongitude.setEnabled(false);
				TerminalLatitude.setEnabled(false);
				setSource.setSelected(true);
			} else {
				SourceLongitude.setEnabled(false);
				SourceLatitude.setEnabled(false);
				TerminalLongitude.setEnabled(true);
				TerminalLatitude.setEnabled(true);
				setTerminal.setSelected(true);
			}
		}

		void DrawRouteOnScreen(int k) {
			double x1, y1, x2, y2;
			try {
				x1 = java.lang.Double.parseDouble(SourceLongitude.getText());
				y1 = java.lang.Double.parseDouble(SourceLatitude.getText());
				x2 = java.lang.Double.parseDouble(TerminalLongitude.getText());
				y2 = java.lang.Double.parseDouble(TerminalLatitude.getText());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("起点终点格式设置有误"),
						LanguageDic.GetWords("GPS数据异常"),
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.LineXlist[0] = x2;
			Screen.LineYlist[0] = y2;
			Screen.LineXlist[1] = GPSPoints.Longitude[GPSPoints.LastAns[k]];
			Screen.LineYlist[1] = GPSPoints.Latitude[GPSPoints.LastAns[k]];
			Screen.LineCount = 2;
			int ptr = GPSPoints.AnsCount[k];
			while (ptr != -1) {
				Screen.LineXlist[Screen.LineCount] = GPSPoints.Longitude[GPSPoints.queue[ptr]];
				Screen.LineYlist[Screen.LineCount] = GPSPoints.Latitude[GPSPoints.queue[ptr]];
				Screen.LineCount++;
				ptr = GPSPoints.father[ptr];
			}
			Screen.LineXlist[Screen.LineCount] = x1;
			Screen.LineYlist[Screen.LineCount] = y1;
			Screen.LineCount++;
			double len = 0;
			for (int i = 1; i < Screen.LineCount; i++)
				len += GPSPoints.Distance(Screen.LineXlist[i - 1],
						Screen.LineYlist[i - 1], Screen.LineXlist[i],
						Screen.LineYlist[i]);

			PathLength.setText(Integer.toString((int) len));
		}

		void ClearRouteOnScreen() {
			Screen.LineCount = 0;
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ScreenLockButton) {
				Screen.setLock(true);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ScreenUnLockButton) {
				Screen.setLock(false);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ShowPath) {
				double x1, y1, x2, y2;
				try {
					x1 = java.lang.Double
							.parseDouble(SourceLongitude.getText());
					y1 = java.lang.Double.parseDouble(SourceLatitude.getText());
					x2 = java.lang.Double.parseDouble(TerminalLongitude
							.getText());
					y2 = java.lang.Double.parseDouble(TerminalLatitude
							.getText());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("起点终点格式设置有误"),
							LanguageDic.GetWords("GPS数据异常"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				Screen.XYCount = 2;
				Screen.Xlist[0] = x1;
				Screen.Ylist[0] = y1;
				Screen.Xlist[1] = x2;
				Screen.Ylist[1] = y2;
				Screen.IsShowDirection = true;
				Screen.MoveMiddle((x1 + x2) / 2, (y1 + y2) / 2);
				Screen.repaint();
				int n = JOptionPane.showConfirmDialog(null,
						LanguageDic.GetWords("起点终点已经在地图上标识，请确认"),
						LanguageDic.GetWords("即将开始规划路径"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					if (GPSPoints.Distance(x1, y1, x2, y2) < 500) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("两点之间距离小于500米，建议直接步行"),
								LanguageDic.GetWords("建议步行"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					// -------------
					if (TheShortestPath.isSelected())
						GPSPoints.SPFA(x1, y1, x2, y2);
					else
						GPSPoints.SearchRoute(x1, y1, x2, y2);
					if (GPSPoints.AnsCount[0] == 0) {
						JOptionPane.showMessageDialog(null,
								LanguageDic.GetWords("由于路网信息骨架不完整，查询未能命中"),
								LanguageDic.GetWords("查询未能命中"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					// -------------
					DrawRouteOnScreen(1);
					PathNum.setText("1");
					// -------------
					PathOnScreen = true;
					if (PathOnScreen) {
						ShowPath.setEnabled(false);
						VeilPath.setEnabled(true);
					} else {
						ShowPath.setEnabled(true);
						VeilPath.setEnabled(false);
					}
					Screen.repaint();
				}
			} else if (e.getSource() == VeilPath) {
				Screen.IsShowDirection = false;
				Screen.XYCount = 0;
				PathOnScreen = false;
				if (PathOnScreen) {
					ShowPath.setEnabled(false);
					VeilPath.setEnabled(true);
				} else {
					ShowPath.setEnabled(true);
					VeilPath.setEnabled(false);
				}
				Screen.repaint();
			} else if (e.getSource() == LastPath) {
				if (!PathOnScreen)
					return;
				int k;
				try {
					k = Integer.parseInt(PathNum.getText());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("请不要擅自改动"),
							LanguageDic.GetWords("请不要擅自改动"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				k = (k == 1 ? 1 : k - 1);
				PathNum.setText(Integer.toString(k));
				ClearRouteOnScreen();
				DrawRouteOnScreen(k);
				Screen.repaint();
			} else if (e.getSource() == NextPath) {
				if (!PathOnScreen)
					return;
				int k;
				try {
					k = Integer.parseInt(PathNum.getText());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("请不要擅自改动"),
							LanguageDic.GetWords("请不要擅自改动"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				k = (k == GPSPoints.AnsCount[0] ? GPSPoints.AnsCount[0] : k + 1);
				PathNum.setText(Integer.toString(k));
				ClearRouteOnScreen();
				DrawRouteOnScreen(k);
				Screen.repaint();
			} else if (e.getSource() == DataBaseReference) {
				LandMarkQueryFrame.Pic.Handle.setVisible(false);
				LandMarkQueryFrame.Pic.DeleteRow.setVisible(false);
				LandMarkQueryFrame.Pic.Delete.setVisible(false);
				LandMarkQueryFrame.Pic.UpdateRow.setVisible(false);
				LandMarkQueryFrame.Pic.Update.setVisible(false);
				LandMarkQueryFrame.Pic.MoreInfo.setVisible(false);
				LandMarkQueryFrame.setVisible(true);
			}
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == setSource) {
				FocusSource = true;
				SourceLongitude.setEnabled(true);
				SourceLatitude.setEnabled(true);
				TerminalLongitude.setEnabled(false);
				TerminalLatitude.setEnabled(false);
			} else {
				FocusSource = false;
				SourceLongitude.setEnabled(false);
				SourceLatitude.setEnabled(false);
				TerminalLongitude.setEnabled(true);
				TerminalLatitude.setEnabled(true);
			}
		}
	}

	// --------------------------------------
	class LandMarkEditPaneClass extends ToolPanel implements ActionListener {
		public String getString() {
			return "LandMarkEditPane";
		}

		// 专门负责地标编辑的功能，实现用户自定义地标写入数据库
		public void setLongitude(double Longitude) {
			LongitudeText.setText(java.lang.Double.toString(Longitude));
		}

		public void setLatitude(double Latitude) {
			LatitudeText.setText(java.lang.Double.toString(Latitude));
		}

		public void paintComponent(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("BackGround3.jpg");
			g.drawImage(img, 0, 0, 280, 680, this);
		}

		JTextField LongitudeText, LatitudeText, LandMarkLongitude,
				LandMarkLatitude;
		JTextField LandMarkName, LandMarkType;
		JTextArea LandMarkScript;
		JButton LandMarkAdd, DeleteSelectedLandMark;

		public LandMarkEditPaneClass() {
			JLabel Title = new JLabel(LanguageDic.GetWords("地标编辑工具栏"));
			Title.setFont(new Font("华文新魏", Font.BOLD, 30));
			add(Title);
			LongitudeText = new JTextField(15);
			LatitudeText = new JTextField(15);
			ScreenLockButton = new JButton(LanguageDic.GetWords("锁住屏幕"));
			ScreenLockButton.addActionListener(this);
			ScreenUnLockButton = new JButton(LanguageDic.GetWords("解锁屏幕"));
			ScreenUnLockButton.addActionListener(this);

			add(new JLabel(LanguageDic.GetWords("鼠标指向经度")));
			add(LongitudeText);
			add(new JLabel(LanguageDic.GetWords("鼠标指向纬度")));
			add(LatitudeText);
			add(ScreenLockButton);
			add(new JLabel("      "));
			add(ScreenUnLockButton);

			if (Screen.lock) {
				ScreenLockButton.setEnabled(false);
				ScreenUnLockButton.setEnabled(true);
			} else {
				ScreenLockButton.setEnabled(true);
				ScreenUnLockButton.setEnabled(false);
			}

			LandMarkLongitude = new JTextField(15);
			LandMarkLatitude = new JTextField(15);

			add(new JLabel(LanguageDic.GetWords("地标点经度位置")));
			add(LandMarkLongitude);
			add(new JLabel(LanguageDic.GetWords("地标点纬度位置")));
			add(LandMarkLatitude);

			LandMarkName = new JTextField(15);
			LandMarkType = new JTextField(15);
			LandMarkScript = new JTextArea(12, 22);

			add(new JLabel(LanguageDic.GetWords("地标点指代名称")));
			add(LandMarkName);
			add(new JLabel(LanguageDic.GetWords("地标点建筑类型")));
			add(LandMarkType);

			add(new JLabel(LanguageDic.GetWords("请输入地标点的相关简介")));
			LandMarkScript.setLineWrap(true);
			add(new JScrollPane(LandMarkScript,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

			LandMarkAdd = new JButton(LanguageDic.GetWords("确认加入此地标"));
			LandMarkAdd.addActionListener(this);
			add(LandMarkAdd);

			DeleteSelectedLandMark = new JButton(
					LanguageDic.GetWords("删除选中的地标"));
			DeleteSelectedLandMark.addActionListener(this);
			add(DeleteSelectedLandMark);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ScreenLockButton) {
				Screen.setLock(true);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == ScreenUnLockButton) {
				Screen.setLock(false);
				if (Screen.lock) {
					ScreenLockButton.setEnabled(false);
					ScreenUnLockButton.setEnabled(true);
				} else {
					ScreenLockButton.setEnabled(true);
					ScreenUnLockButton.setEnabled(false);
				}
			} else if (e.getSource() == LandMarkAdd) {
				if (LandMarkName.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("地标名称为空"),
							LanguageDic.GetWords("输入数据不完整"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (LandMarkType.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("地标类型为空"),
							LanguageDic.GetWords("输入数据不完整"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				double x0, y0;
				try {
					x0 = java.lang.Double.parseDouble(LandMarkLongitude
							.getText());
					y0 = java.lang.Double.parseDouble(LandMarkLatitude
							.getText());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							LanguageDic.GetWords("地标GPS数据有误"),
							LanguageDic.GetWords("GPS数据异常"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				Screen.XYCount = 1;
				Screen.Xlist[0] = x0;
				Screen.Ylist[0] = y0;
				Screen.MoveMiddle(x0, y0);
				Screen.repaint();
				int n = JOptionPane.showConfirmDialog(
						null,
						LanguageDic.GetWords("是否要将") + "【"
								+ LandMarkName.getText() + "】"
								+ LanguageDic.GetWords("加入地标序列"),
						LanguageDic.GetWords("确认是否加入"),
						JOptionPane.YES_NO_OPTION);
				if (n != JOptionPane.YES_OPTION)
					return;
				Screen.IsLandMarkSelected[GPSPoints.LandMarkNum] = false;
				GPSPoints.AddLandMark(x0, y0, LandMarkName.getText(),
						LandMarkType.getText(), LandMarkScript.getText());
				Screen.repaint();
			} else if (e.getSource() == DeleteSelectedLandMark) {
				int tot = 0;
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					if (Screen.IsLandMarkSelected[i])
						continue;
					GPSPoints.LandMarkLongitude[tot] = GPSPoints.LandMarkLongitude[i];
					GPSPoints.LandMarkLatitude[tot] = GPSPoints.LandMarkLatitude[i];
					GPSPoints.LandMarkName[tot] = GPSPoints.LandMarkName[i];
					GPSPoints.LandMarkType[tot] = GPSPoints.LandMarkType[i];
					GPSPoints.LandMarkScript[tot] = GPSPoints.LandMarkScript[i];
					tot++;
				}
				GPSPoints.LandMarkNum = tot;
				for (int i = 0; i < GPSPoints.LandMarkNum; i++)
					Screen.IsLandMarkSelected[i] = false;
				Screen.repaint();
			}
		}
	}

	// --------------------------------------
	public class ScreenCanvas extends Canvas implements MouseListener,
			MouseWheelListener, MouseMotionListener {
		// 将卫星图和种种地图元素显示在屏幕上，并且解读和处理用户的鼠标操作
		Image image;
		Toolkit tool;
		double rate;
		boolean lock;
		boolean ShowCenter = false;
		public double ScreenLongitude, ScreenLatitude;
		public double LongitudeScale, LatitudeScale;
		public int ScreenWidth, ScreenHeight;
		boolean IsShowDirection = false;
		boolean ShowLargeRegion = false;
		boolean ShowScreenHint = false;
		String ScreenHint = null;
		double LargeRegionLongitude, LargeRegionLatitude,
				LargeRegionLongitudeScale, LargeRegionLatitudeScale;
		String[] TempString = new String[100000];
		int ScreenDeltaX = 0, ScreenDeltaY = 0;

		public void setShowLargeRegion(double x0, double y0, double w, double h) {// 允许在地图上显示矩形
			LargeRegionLongitude = x0;
			LargeRegionLatitude = y0;
			LargeRegionLongitudeScale = w;
			LargeRegionLatitudeScale = h;
		}

		public void setLock(boolean t) {// 设置锁屏
			lock = t;
		}

		double showDirectionX1, showDirectionX2, showDirectionY1,
				showDirectionY2;

		public void showDirection(double x1, double y1, double x2, double y2) {// 在地图上画箭头
			showDirectionX1 = x1;
			showDirectionY1 = y1;
			showDirectionX2 = x2;
			showDirectionY2 = y2;
			IsShowDirection = true;
			repaint();
		}

		public boolean CheckInGeoScreen(double x, double y) {
			if (x < ScreenLongitude)
				return false;
			if (x > ScreenLongitude + LongitudeScale)
				return false;
			if (y > ScreenLatitude)
				return false;
			if (y < ScreenLatitude - LatitudeScale)
				return false;
			return true;
		}

		public boolean CheckInGeoScreen(double x1, double y1, double x2,
				double y2) {
			if (CheckInGeoScreen(x1, y1))
				return true;
			if (CheckInGeoScreen(x1, y2))
				return true;
			if (CheckInGeoScreen(x2, y1))
				return true;
			if (CheckInGeoScreen(x2, y2))
				return true;
			return false;
		}
		
		public boolean CheckInScreen(int x, int y, int ScreenWidth, int ScreenHeight) {
			if (x < 0)
				return false;
			if (x > ScreenWidth)
				return false;
			if (y > ScreenHeight)
				return false;
			if (y < 0)
				return false;
			return true;
		}

		public boolean CheckInScreen(int x1, int y1, int x2, int y2, int ScreenWidth, int ScreenHeight) {
			if (Math.max(Math.min(x1, x2), 0) > Math.min(Math.max(x1, x2),
					ScreenWidth))
				return false;
			if (Math.max(Math.min(y1, y2), 0) > Math.min(Math.max(y1, y2),
					ScreenHeight))
				return false;
			return true;
		}

		// Extended Component
		// Definition--------------------------------------------------
		boolean IsTextArea1Visible = false, IsTextArea2Visible = false;
		String TextArea1Content = null, TextArea2Content = null;
		boolean IsTextArea1BackGround = false, IsTextArea2BackGround = false;

		boolean IsExtendedPointVisible = false;
		double[] ExtendedPointX = new double[10000];
		double[] ExtendedPointY = new double[10000];
		String[] ExtendedPointHint = new String[10000];
		int ExtendedPointSelectCount = 0;
		int[] ExtendedPointSelectList = new int[10000];
		int ExtendedPointCount = 0;
		boolean IsConsecutiveLink = false;// 是否顺序相连
		boolean IsHeadTailLink = false;// 是否首位相连
		boolean IsExtendedPointHintVisible = false;

		public void ExtendedPointPush(double x, double y) {
			ExtendedPointX[ExtendedPointCount] = x;
			ExtendedPointY[ExtendedPointCount] = y;
			ExtendedPointHint[ExtendedPointCount] = "";
			ExtendedPointCount++;
		}

		public void ExtendedPointPush(double x, double y, String name) {
			ExtendedPointX[ExtendedPointCount] = x;
			ExtendedPointY[ExtendedPointCount] = y;
			ExtendedPointHint[ExtendedPointCount] = name;
			ExtendedPointCount++;
		}

		public void ExtendedPointPop() {
			ExtendedPointCount = (ExtendedPointCount == 0 ? 0
					: ExtendedPointCount - 1);
		}

		public void ExtendedPointEmpty() {
			ExtendedPointCount = 0;
		}

		public int ExtendedPointMoveBack(int st, int en, int newst) {
			if (en < st)
				return newst;
			for (int i = st; i <= en; i++) {
				ExtendedPointX[newst] = ExtendedPointX[i];
				ExtendedPointY[newst] = ExtendedPointY[i];
				ExtendedPointHint[newst] = ExtendedPointHint[i];
				newst++;
			}
			return newst;
		}

		public void ExtendedPointSelectDelete() {
			ReTitle(LanguageDic.GetWords("删除成功"));
			if (ExtendedPointSelectCount == 0)
				return;
			int temp = 0;
			temp = ExtendedPointMoveBack(0, ExtendedPointSelectList[0] - 1,
					temp);
			for (int i = 1; i < ExtendedPointSelectCount; i++) {
				temp = ExtendedPointMoveBack(
						ExtendedPointSelectList[i - 1] + 1,
						ExtendedPointSelectList[i] - 1, temp);
			}
			temp = ExtendedPointMoveBack(
					ExtendedPointSelectList[ExtendedPointSelectCount - 1] + 1,
					ExtendedPointCount - 1, temp);
			ExtendedPointCount = temp;
			ExtendedPointSelectCount = 0;
		}

		public void ExtendedPointSelect(double x1, double y1, double x2,
				double y2) {
			ExtendedPointSelectCount = 0;
			for (int i = 0; i < ExtendedPointCount; i++) {
				if (Handle.AccurateInsideRectangle(ExtendedPointX[i],
						ExtendedPointY[i], x1, y1, x2, y2)) {
					ExtendedPointSelectList[ExtendedPointSelectCount] = i;
					ExtendedPointSelectCount++;
				}
			}
			ReTitle(ExtendedPointSelectCount + LanguageDic.GetWords("个点被选中"));
		}

		public void ExtendedPointSelectCancel() {
			ExtendedPointSelectCount = 0;
			ReTitle(LanguageDic.GetWords("释放选中点"));
		}

		public void ExtendedPointReHint(int k, String Hint) {
			if (k < 0)
				return;
			if (k >= ExtendedPointCount)
				return;
			ExtendedPointHint[k] = Hint;
		}

		public void ExtendedPointReHint(String Prefix) {
			for (int i = 0; i < ExtendedPointCount; i++) {
				ExtendedPointHint[i] = Prefix + i;
			}
		}

		void ExtendedComponentReset() {
			IsTextArea1Visible = false;
			IsTextArea2Visible = false;
			TextArea1Content = null;
			TextArea2Content = null;
			IsTextArea1BackGround = false;
			IsTextArea2BackGround = false;

			IsExtendedPointVisible = false;
			ExtendedPointCount = 0;
			IsConsecutiveLink = false;
			IsHeadTailLink = false;
			IsExtendedPointHintVisible = false;
			ExtendedPointSelectCount = 0;
		}

		private double[] AlignVectorX = new double[10000];
		private double[] AlignVectorY = new double[10000];
		private double[] AlignPosX = new double[10000];
		private double[] AlignPosY = new double[1000];
		private double[] OffsetCounter = new double[10000];
		private int AlignVectorCounter = 0;

		private int setAlignTagVector(Database.LineDataSet LineDB, String str,
				int index, double scale) {
			AlignVectorCounter = 1;
			AlignVectorX[0] = LineDB.AllPointX[index];
			AlignVectorY[0] = LineDB.AllPointY[index];
			OffsetCounter[0] = 0;
			index = LineDB.AllPointNext[index];
			while (index != -1) {
				AlignVectorX[AlignVectorCounter] = LineDB.AllPointX[index];
				AlignVectorY[AlignVectorCounter] = LineDB.AllPointY[index];
				OffsetCounter[AlignVectorCounter] = OffsetCounter[AlignVectorCounter - 1]
						+ Handle.AccurateDistance(
								AlignVectorX[AlignVectorCounter - 1],
								AlignVectorY[AlignVectorCounter - 1],
								LineDB.AllPointX[index],
								LineDB.AllPointY[index]);
				AlignVectorCounter++;
				index = LineDB.AllPointNext[index];
			}
			double lenspan = OffsetCounter[AlignVectorCounter - 1] * 3 / 4
					/ str.length();
			double lenpos = OffsetCounter[AlignVectorCounter - 1] / 8 + lenspan
					/ 2;
			double screenwidth = lenspan * scale;
			int ptr = 0;
			for (int i = 0; i < str.length(); i++, lenpos += lenspan) {
				while (OffsetCounter[ptr + 1] < lenpos)
					ptr++;
				AlignPosX[i] = AlignVectorX[ptr]
						+ (AlignVectorX[ptr + 1] - AlignVectorX[ptr])
						* (lenpos - OffsetCounter[ptr])
						/ (OffsetCounter[ptr + 1] - OffsetCounter[ptr]);
				AlignPosY[i] = AlignVectorY[ptr]
						+ (AlignVectorY[ptr + 1] - AlignVectorY[ptr])
						* (lenpos - OffsetCounter[ptr])
						/ (OffsetCounter[ptr + 1] - OffsetCounter[ptr]);
			}
			return Math.min(16, (int) (screenwidth / str.length() * 2));
		}

		// --------------------------------------------------------------------
		private double VectorYmax, VectorYmin, VectorXmax, VectorXmin, VectorX,
				VectorY;
		private double[] CenterPosX = new double[10000];
		private double[] CenterPosY = new double[1000];

		private int setCenterTagVector(Database.PolygonDataSet PolyDB,
				String str, int index, double scale) {
			VectorX = PolyDB.AllPointX[index];
			VectorY = PolyDB.AllPointY[index];
			index = PolyDB.AllPointNext[index];
			VectorXmin = VectorX;
			VectorYmin = VectorY;
			VectorXmax = VectorX;
			VectorYmax = VectorY;
			while (index != -1) {
				VectorX = PolyDB.AllPointX[index];
				VectorY = PolyDB.AllPointY[index];
				VectorXmin = Math.min(VectorXmin, VectorX);
				VectorXmax = Math.max(VectorXmax, VectorX);
				VectorYmin = Math.min(VectorYmin, VectorY);
				VectorYmax = Math.max(VectorYmax, VectorY);
				index = PolyDB.AllPointNext[index];
			}
			double offsetdis = Handle.AccurateDistance(VectorXmin, VectorYmin,
					VectorXmax, VectorYmax);
			double screenwidth = (VectorXmax - VectorXmin) * scale;
			double lenspan = offsetdis / 2 / str.length();
			double lenpos = offsetdis / 4 + lenspan / 2;
			if ((VectorYmax - VectorYmin) / (VectorXmax - VectorXmin) < 1.25) {
				for (int i = 0; i < str.length(); i++, lenpos += lenspan) {
					CenterPosX[i] = VectorXmin + lenpos / offsetdis
							* (VectorXmax - VectorXmin);
					CenterPosY[i] = (VectorYmax + VectorYmin) / 2;
				}
			} else {
				for (int i = 0; i < str.length(); i++, lenpos += lenspan) {
					CenterPosX[i] = VectorXmax - lenpos / offsetdis
							* (VectorXmax - VectorXmin);
					CenterPosY[i] = VectorYmax - lenpos / offsetdis
							* (VectorYmax - VectorYmin);
				}
			}
			return Math.min(16, (int) (screenwidth / str.length() / 2.5));
		}

		// --------------------------------------------------------------------
		public ScreenCanvas() {// 初始化各种数据
			setLock(false);
			tool = getToolkit();
			ScreenWidth = 720;
			ScreenHeight = 680;
			rate = 1.1;
			addMouseListener(this);
			addMouseWheelListener(this);
			addMouseMotionListener(this);
			XYCount = 0;
			Xlist = new double[1000];
			Ylist = new double[1000];
			LineXlist = new double[10000];
			LineYlist = new double[10000];
			LineCount = 0;

			IsShowDirection = false;
			SelectedPointList[0] = 0;
			for (int i = 0; i < 10000; i++)
				IsLandMarkSelected[i] = false;
		}

		double Xlist[], Ylist[];
		int XYCount;
		double LineXlist[], LineYlist[];
		int LineCount;
		boolean IsLandMarkOnScreen = false, IsLandMarkNameOnScreen = false;
		boolean[] IsLandMarkSelected = new boolean[10000];
		float[][] AlphaGridsCounter = new float[3000][3000];
		float[][] RadiationGridsCounter=new float[3000][3000];
		float[][] AlphaGridsValue = new float[3000][3000];
		public double LastScreenLongitude = -1000, LastScreenLatitude = -1000,
				LastLongitudeScale = -1, LastLatitudeScale = -1;
		public int LastAlphaPercentScale = 0;
		public int LastRadiationDistance = 0;

		public void AlphaDrawer(String FilePath) {
			try {
				boolean BeforeVisible = IsAllPointInvisible;
				boolean BeforeShow = IsShowAlphaDistribution;
				IsTextArea1Visible = false;
				IsTextArea2Visible = false;
				IsAllPointInvisible = true;
				IsShowAlphaDistribution = false;
				BufferedImage PNGimage = new BufferedImage(Screen.ScreenWidth,
						Screen.ScreenHeight, BufferedImage.TYPE_INT_RGB);
				Graphics2D g_2d = PNGimage.createGraphics();
				Screen.paint(g_2d);
				IsAllPointInvisible = BeforeVisible;
				IsShowAlphaDistribution = BeforeShow;
				if (AlphaGridsRow > ScreenHeight)
					AlphaGridsRow = ScreenHeight;
				if (AlphaGridsColumn > ScreenWidth)
					AlphaGridsColumn = ScreenWidth;
				for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
					for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
						AlphaGridsCounter[Row_i][Col_i] = 0;
						RadiationGridsCounter[Row_i][Col_i] = 0;
					}
				}
				double Xstep = LongitudeScale / AlphaGridsColumn;
				double Ystep = LatitudeScale / AlphaGridsRow;
				int pos_row, pos_col;
				for (int ptr_i = 0; ptr_i < PointDatabase.PointNum; ptr_i++) {
					pos_col = (int) ((PointDatabase.AllPointX[ptr_i] - ScreenLongitude) / Xstep);
					if (pos_col < 0)
						continue;
					if (pos_col >= AlphaGridsColumn)
						continue;
					pos_row = (int) ((ScreenLatitude - PointDatabase.AllPointY[ptr_i]) / Ystep);
					if (pos_row < 0)
						continue;
					if (pos_row >= AlphaGridsRow)
						continue;
					AlphaGridsCounter[pos_row][pos_col]++;
				}
				int AllCounter = 0;
				float MaxCounter = 0;
				int dx=0;
				int dy=0;
				float M_dist=0;
				float power=0;
				for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
					for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
							power=AlphaGridsCounter[Row_i][Col_i];
							RadiationGridsCounter[Row_i][Col_i]+=power;
						if(RadiationDistance!=0)
						for(dx=0; dx<= RadiationDistance; dx++){
							for(dy=0;dy<=RadiationDistance-dx;dy++){
							M_dist=dx+dy;
							if(dx==0){
								if(Row_i-dy>=0) 
									RadiationGridsCounter[Row_i-dy][Col_i]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								if(Row_i+dy<AlphaGridsRow)
									RadiationGridsCounter[Row_i+dy][Col_i]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
							}else if(dy==0){
								if(Col_i-dx>=0)
									RadiationGridsCounter[Row_i][Col_i-dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								if(Col_i+dx<AlphaGridsColumn)
									RadiationGridsCounter[Row_i][Col_i+dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
							}else{
								if(Row_i-dy>=0) if(Col_i-dx>=0)
									RadiationGridsCounter[Row_i-dy][Col_i-dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								if(Row_i-dy>=0) if(Col_i+dx<AlphaGridsColumn)
									RadiationGridsCounter[Row_i-dy][Col_i+dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								if(Row_i+dy<AlphaGridsRow) if(Col_i-dx>=0)
									RadiationGridsCounter[Row_i+dy][Col_i-dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								if(Row_i+dy<AlphaGridsRow) if(Col_i+dx<AlphaGridsColumn)
									RadiationGridsCounter[Row_i+dy][Col_i+dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
							}
							}
						}
					}
				}
				for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
					for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
						AlphaGridsCounter[Row_i][Col_i]+=RadiationGridsCounter[Row_i][Col_i];
						AllCounter += AlphaGridsCounter[Row_i][Col_i];
						MaxCounter = Math.max(MaxCounter,
								AlphaGridsCounter[Row_i][Col_i]);
					}
				}
				
				if (AlphaPercentScale > 0)
					MaxCounter = AlphaPercentScale;
				else MaxCounter*=0.5;

				Handle.ShowTextArea1(LanguageDic.GetWords("总计 ") + AllCounter
						+ " pts", true);
				Handle.ShowTextArea2(LanguageDic.GetWords("网格浓度100%对应 ")
						+ MaxCounter + " pts", true);
				for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
					for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
						AlphaGridsValue[Row_i][Col_i] = (AlphaGridsCounter[Row_i][Col_i])
								/ (MaxCounter+0.01f);
						AlphaGridsValue[Row_i][Col_i] = AlphaGridsValue[Row_i][Col_i] > 0.99f ? 0.99f
								: (AlphaGridsValue[Row_i][Col_i]+0.001f);
					}
				}
				double ScreenXstep = ((double) ScreenWidth) / AlphaGridsColumn;
				double ScreenYstep = ((double) ScreenHeight) / AlphaGridsRow;
				for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
					for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
						if (AlphaGridsValue[Row_i][Col_i] < 0.01)
							continue;
						AlphaComposite ac = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER,
								AlphaGridsValue[Row_i][Col_i]>0.1f?0.99f:AlphaGridsValue[Row_i][Col_i]*9.99f);
						g_2d.setComposite(ac);
						g_2d.setColor(new Color(Color.HSBtoRGB((AlphaGridsValue[Row_i][Col_i])*0.5f+0.1f, 1.0f, 1.0f)));
						g_2d.fillRect((int) (Col_i * ScreenXstep),
								(int) (Row_i * ScreenYstep), (int) ScreenXstep,
								(int) ScreenYstep);
					}
				}
				AlphaComposite ac = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1);
				g_2d.setComposite(ac);
				ImageIO.write(PNGimage, "png", new File(FilePath));
				System.gc();
			} catch (Exception ee) {
				System.err.println("OutErr====>" + FilePath);
			}
		}

		// bs_temp=new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_MITER, 10.0f, dash, 0);
		public BasicStroke GetVisualLineStroke(String info) {// LineWidth,DashLine,
			try {
				if (info == null)
					return new BasicStroke(1, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND);
				float width = 1;
				int p1;
				if ((p1 = info.indexOf("[LineWidth:")) != -1)
					width = Float.parseFloat(info.substring(p1 + 11,
							info.indexOf(']', p1 + 11)));
				if (info.indexOf("[DashLine:") != -1)
					return new BasicStroke(width, BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER, 10.0f,
							new float[] { 5, 5 }, 0);
				else
					return new BasicStroke(1, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND);
			} catch (Exception ex) {
				System.err.println("GetVisualLineStroke_Err");
				ex.printStackTrace();
				return new BasicStroke(1, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND);
			}
		}

		public Color GetVisualColor(String info, String Prefix) {// PointRGB,PointAlpha,LineRGB,LinrAlpha,PolygonRGB,PolygonAlpha
			try {
				if (info == null)
					return Color.white;
				Color color_defult = Color.white;
				String RGB_str = null;
				Float Alpha = 0.99f;
				int p1;
				if ((p1 = info.indexOf("[" + Prefix + "RGB:")) != -1)
					RGB_str = info.substring(p1 + 5 + Prefix.length(),
							info.indexOf(']', p1 + 5 + Prefix.length()));
				if ((p1 = info.indexOf("[" + Prefix + "Alpha:")) != -1)
					Alpha = Float.parseFloat(info.substring(
							p1 + 7 + Prefix.length(),
							info.indexOf(']', p1 + 7 + Prefix.length())));
				if (RGB_str != null) {
					if (RGB_str.startsWith("0x")) {
						color_defult = new Color(Integer.parseInt(
								RGB_str.substring(2), 16));
						color_defult = new Color(
								(float) color_defult.getRed() / 256,
								(float) color_defult.getGreen() / 256,
								(float) color_defult.getBlue() / 256, Alpha);
					} else {
						String[] v = RGB_str.split(",");
						if (v.length > 3)
							Alpha = Float.parseFloat(v[3]);
						color_defult = new Color(Float.parseFloat(v[0]) / 256,
								Float.parseFloat(v[1]) / 256,
								Float.parseFloat(v[2]) / 256, Alpha);
					}
				}
				return color_defult;
			} catch (Exception ex) {
				System.err.println("GetVisualColor_Err");
				ex.printStackTrace();
				return Color.white;
			}
		}

		public boolean GetVisualArrow(String info) {// ArrowLine
			try {
				if (info == null)
					return false;
				if (info.indexOf("[ArrowLine:") != -1)
					return true;
				else
					return false;
			} catch (Exception ex) {
				System.err.println("GetVisualArrow_Err");
				ex.printStackTrace();
				return false;
			}
		}

		public int GetVisualPointSize(String info) {// PointSize
			try {
				if (info == null)
					return 6;
				int p1;
				if ((p1 = info.indexOf("[PointSize:")) != -1)
					return Integer.parseInt(info.substring(p1 + 11,
							info.indexOf(']', p1 + 11)));
				else
					return 6;
			} catch (Exception ex) {
				System.err.println("GetVisualPointSize_Err");
				ex.printStackTrace();
				return 6;
			}
		}
		public void DBpaint(Graphics2D g_2d,double ScreenLongitude,double ScreenLatitude,double LongitudeScale,double LatitudeScale,int ScreenWidth,int ScreenHeight){
			BasicStroke bs = new BasicStroke(2, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			g_2d.setStroke(bs);
			g_2d.setColor(Color.green);
			scale = System.currentTimeMillis() / 500 % 100;
			scale = scale > 50 ? 100 - scale : scale;
			int j = 1;
			boolean Selected;
			// 画动态的临时点
			double tempsize = 0.33 - LongitudeScale
					/ (LongitudeEnd - LongitudeStart);
			tempsize = tempsize <= 0 ? 0 : tempsize;
			g_2d.setFont(new Font("黑体", 0, (int) (90 * tempsize)));

			for (int i = 0; i < XYCount; i++) {
				Selected = false;
				int xx = (int) ((Xlist[i] - ScreenLongitude) / LongitudeScale * ScreenWidth);
				int yy = (int) ((ScreenLatitude - Ylist[i]) / LatitudeScale * ScreenHeight);
				if (j <= SelectedPointList[0]) {
					if (i < SelectedPointList[j]) {
					} else if (i == SelectedPointList[j]) {
						Selected = true;
						j++;
					} else if (i > SelectedPointList[j])
						j++;
				}
				if (Selected)
					g_2d.setColor(Color.blue);
				Ellipse2D pp = new Ellipse2D.Double(xx - 4 + ScreenDeltaX, yy
						- 4 + ScreenDeltaY, 8, 8);
				g_2d.draw(pp);
				if (Selected)
					g_2d.setColor(Color.green);
				// 显示出租车编号
				if (NowPanel == TaxiSearchPane) {
					if (ConcentrateTaxiWizard.VeilTaxiCode.isEnabled()) {
						g_2d.drawString(TempString[i], xx + 8 + ScreenDeltaX,
								yy - 8 + ScreenDeltaY);
					}
				}
			}
			// 画大圆形
			if (ShowLargeRegion) {
				int x0 = (int) ((LargeRegionLongitude - ScreenLongitude)
						/ LongitudeScale * ScreenWidth);
				int y0 = (int) ((ScreenLatitude - LargeRegionLatitude)
						/ LatitudeScale * ScreenHeight);
				int w = (int) ((LargeRegionLongitudeScale / LongitudeScale) * ScreenWidth);
				int h = (int) ((LargeRegionLatitudeScale / LatitudeScale) * ScreenHeight);
				scale = System.currentTimeMillis() / 200 % 200;
				scale = scale > 100 ? 200 - scale : scale;
				if (ClockWizard.pic.getSecond() % 2 == 0)
					g_2d.setColor(Color.blue);
				else
					g_2d.setColor(Color.red);
				g_2d.fillOval(x0 - 6, y0 - 6, 12, 12);
				Ellipse2D pp = new Ellipse2D.Double(
						x0 - w / 2.0 + ScreenDeltaX, y0 - h / 2.0
								+ ScreenDeltaY, w, h);
				g_2d.draw(pp);
				g_2d.setColor(Color.green);
			}
			// 画箭头
			if (IsShowDirection) {
				if ((NowPanel == TwoPointPane) || (NowPanel == TaxiSearchPane)) {
					int x1 = (int) ((showDirectionX1 - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y1 = (int) ((ScreenLatitude - showDirectionY1)
							/ LatitudeScale * ScreenHeight);
					int x2 = (int) ((showDirectionX2 - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y2 = (int) ((ScreenLatitude - showDirectionY2)
							/ LatitudeScale * ScreenHeight);
					Line2D l1 = new Line2D.Double(x1 + ScreenDeltaX, y1
							+ ScreenDeltaY, x2 + ScreenDeltaX, y2
							+ ScreenDeltaY);
					int x3 = (x1 + x2) / 2 + (y2 - y1) / 4;
					int y3 = (y1 + y2) / 2 - (x2 - x1) / 4;
					x3 = x3 + 3 * (x2 - x3) / 4;
					y3 = y3 + 3 * (y2 - y3) / 4;
					Line2D l2 = new Line2D.Double(x3 + ScreenDeltaX, y3
							+ ScreenDeltaY, x2 + ScreenDeltaX, y2
							+ ScreenDeltaY);
					x3 = (x1 + x2) / 2 - (y2 - y1) / 4;
					y3 = (y1 + y2) / 2 + (x2 - x1) / 4;
					x3 = x3 + 3 * (x2 - x3) / 4;
					y3 = y3 + 3 * (y2 - y3) / 4;
					Line2D l3 = new Line2D.Double(x3 + ScreenDeltaX, y3
							+ ScreenDeltaY, x2 + ScreenDeltaX, y2
							+ ScreenDeltaY);
					g_2d.draw(l1);
					g_2d.draw(l2);
					g_2d.draw(l3);
				} else if (NowPanel == CalibratePane) {
					for (int i = 0; i < XYCount; i++) {
						if (i % 2 == 1) {
							int x1 = (int) ((Xlist[i - 1] - ScreenLongitude)
									/ LongitudeScale * ScreenWidth);
							int y1 = (int) ((ScreenLatitude - Ylist[i - 1])
									/ LatitudeScale * ScreenHeight);
							int x2 = (int) ((Xlist[i] - ScreenLongitude)
									/ LongitudeScale * ScreenWidth);
							int y2 = (int) ((ScreenLatitude - Ylist[i])
									/ LatitudeScale * ScreenHeight);
							x1 += ScreenDeltaX;
							y1 += ScreenDeltaY;
							x2 += ScreenDeltaX;
							y2 += ScreenDeltaY;
							Line2D l1 = new Line2D.Double(x1, y1, x2, y2);
							int x3 = (x1 + x2) / 2 + (y2 - y1) / 4;
							int y3 = (y1 + y2) / 2 - (x2 - x1) / 4;
							x3 = x3 + 3 * (x2 - x3) / 4;
							y3 = y3 + 3 * (y2 - y3) / 4;
							Line2D l2 = new Line2D.Double(x3, y3, x2, y2);
							x3 = (x1 + x2) / 2 - (y2 - y1) / 4;
							y3 = (y1 + y2) / 2 + (x2 - x1) / 4;
							x3 = x3 + 3 * (x2 - x3) / 4;
							y3 = y3 + 3 * (y2 - y3) / 4;
							Line2D l3 = new Line2D.Double(x3, y3, x2, y2);
							g_2d.draw(l1);
							g_2d.draw(l2);
							g_2d.draw(l3);
						}
					}
				} else if (NowPanel == RouteSearchPane) {
					int x1 = (int) ((Xlist[0] - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y1 = (int) ((ScreenLatitude - Ylist[0]) / LatitudeScale * ScreenHeight);
					int x2 = (int) ((Xlist[1] - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y2 = (int) ((ScreenLatitude - Ylist[1]) / LatitudeScale * ScreenHeight);
					x1 += ScreenDeltaX;
					y1 += ScreenDeltaY;
					x2 += ScreenDeltaX;
					y2 += ScreenDeltaY;
					Line2D l1 = new Line2D.Double(x1, y1, x2, y2);
					int x3 = (x1 + x2) / 2 + (y2 - y1) / 4;
					int y3 = (y1 + y2) / 2 - (x2 - x1) / 4;
					x3 = x3 + 3 * (x2 - x3) / 4;
					y3 = y3 + 3 * (y2 - y3) / 4;
					Line2D l2 = new Line2D.Double(x3, y3, x2, y2);
					x3 = (x1 + x2) / 2 - (y2 - y1) / 4;
					y3 = (y1 + y2) / 2 + (x2 - x1) / 4;
					x3 = x3 + 3 * (x2 - x3) / 4;
					y3 = y3 + 3 * (y2 - y3) / 4;
					Line2D l3 = new Line2D.Double(x3, y3, x2, y2);
					g_2d.draw(l1);
					g_2d.draw(l2);
					g_2d.draw(l3);
				}
			}
			// 画不规则的折线，模拟路径显示
			if (LineCount != 0) {
				bs = new BasicStroke(4, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND);
				g_2d.setStroke(bs);
				g_2d.setColor(Color.orange);
				if (RouteSearchPane.PathOnScreen) {
					Line2D line;
					for (int i = 1; i < LineCount; i++) {
						int x1 = (int) ((LineXlist[i - 1] - ScreenLongitude)
								/ LongitudeScale * ScreenWidth);
						int y1 = (int) ((ScreenLatitude - LineYlist[i - 1])
								/ LatitudeScale * ScreenHeight);
						int x2 = (int) ((LineXlist[i] - ScreenLongitude)
								/ LongitudeScale * ScreenWidth);
						int y2 = (int) ((ScreenLatitude - LineYlist[i])
								/ LatitudeScale * ScreenHeight);
						x1 += ScreenDeltaX;
						y1 += ScreenDeltaY;
						x2 += ScreenDeltaX;
						y2 += ScreenDeltaY;
						line = new Line2D.Double(x1, y1, x2, y2);
						g_2d.draw(line);
					}
				}
				bs = new BasicStroke(2, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND);
				g_2d.setStroke(bs);
				g_2d.setColor(Color.green);
			}
			if (CanDrawRect) {// 画矩形
				Rectangle2D rect = new Rectangle2D.Double(DrawRectX1
						+ ScreenDeltaX, DrawRectY1 + ScreenDeltaY, DrawRectX2
						- DrawRectX1, DrawRectY2 - DrawRectY1);
				g_2d.draw(rect);
			}
			if (ShowCenter) {// 画中心选中标识
				g_2d.setColor(Color.red);
				Ellipse2D rect = new Ellipse2D.Double(ScreenWidth / 2 - 15
						+ ScreenDeltaX, ScreenHeight / 2 - 15 + ScreenDeltaY,
						30, 30);
				g_2d.draw(rect);
				bs = new BasicStroke(2, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND);
				g_2d.setStroke(bs);
				g_2d.setColor(Color.green);
			}
			if (IsLandMarkOnScreen) {// 显示地标点，静态点
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					if (IsLandMarkSelected[i]) {
						g_2d.setColor(Color.orange);
					} else {
						g_2d.setColor(Color.white);
					}
					int x0 = (int) ((GPSPoints.LandMarkLongitude[i] - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y0 = (int) ((ScreenLatitude - GPSPoints.LandMarkLatitude[i])
							/ LatitudeScale * ScreenHeight);
					x0 += ScreenDeltaX;
					y0 += ScreenDeltaY;
					g_2d.fillRect(x0 - 3, y0 - 3, 6, 6);
					if (IsLandMarkNameOnScreen) {
						g_2d.drawString(GPSPoints.LandMarkName[i], x0 + 8,
								y0 - 8);
					}
				}
				g_2d.setColor(Color.green);
			}
			// 显示屏幕提示;
			if (ShowScreenHint) {
				g_2d.setFont(new Font("黑体", Font.BOLD, (int) (20)));
				if (ScreenHint == null)
					ScreenHint = LanguageDic.GetWords("暂无信息");

				Rectangle2D _rect = new Rectangle2D.Double(0, 0, ScreenWidth,
						34);
				g_2d.setPaint(Color.black);
				g_2d.fill(_rect);

				g_2d.setColor(Color.red);
				g_2d.drawString(ScreenHint, 10, 25);
				g_2d.setColor(Color.green);
			}
			// Draw for Extended Components
			bs = new BasicStroke(2, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			g_2d.setStroke(bs);
			if ((IsExtendedPointVisible) && (ExtendedPointCount > 0)) {

				g_2d.setFont(new Font("黑体", 0, (int) (90 * tempsize)));
				for (int i = 0; i < ExtendedPointCount; i++) {
					int xx = (int) ((ExtendedPointX[i] - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int yy = (int) ((ScreenLatitude - ExtendedPointY[i])
							/ LatitudeScale * ScreenHeight);
					xx += ScreenDeltaX;
					yy += ScreenDeltaY;
					g_2d.setColor(Color.yellow);
					Ellipse2D pp = new Ellipse2D.Double(xx - 4, yy - 4, 8, 8);
					g_2d.draw(pp);

					if (IsExtendedPointHintVisible) {
						g_2d.drawString(ExtendedPointHint[i], xx + 8, yy - 8);
					}
				}
				Line2D line;
				g_2d.setColor(Color.green);
				if (IsConsecutiveLink)
					for (int i = 1; i < ExtendedPointCount; i++) {
						int x1 = (int) ((ExtendedPointX[i - 1] - ScreenLongitude)
								/ LongitudeScale * ScreenWidth);
						int y1 = (int) ((ScreenLatitude - ExtendedPointY[i - 1])
								/ LatitudeScale * ScreenHeight);
						int x2 = (int) ((ExtendedPointX[i] - ScreenLongitude)
								/ LongitudeScale * ScreenWidth);
						int y2 = (int) ((ScreenLatitude - ExtendedPointY[i])
								/ LatitudeScale * ScreenHeight);
						x1 += ScreenDeltaX;
						y1 += ScreenDeltaY;
						x2 += ScreenDeltaX;
						y2 += ScreenDeltaY;
						line = new Line2D.Double(x1, y1, x2, y2);
						g_2d.draw(line);

						int x3 = (x1 + x2) / 2 + (y2 - y1) / 4;
						int y3 = (y1 + y2) / 2 - (x2 - x1) / 4;
						x3 = x3 + 3 * (x2 - x3) / 4;
						y3 = y3 + 3 * (y2 - y3) / 4;
						line = new Line2D.Double(x3, y3, x2, y2);
						g_2d.draw(line);
						x3 = (x1 + x2) / 2 - (y2 - y1) / 4;
						y3 = (y1 + y2) / 2 + (x2 - x1) / 4;
						x3 = x3 + 3 * (x2 - x3) / 4;
						y3 = y3 + 3 * (y2 - y3) / 4;
						line = new Line2D.Double(x3, y3, x2, y2);
						g_2d.draw(line);
					}
				if (IsHeadTailLink) {
					g_2d.setColor(Color.red);
					int x1 = (int) ((ExtendedPointX[0] - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y1 = (int) ((ScreenLatitude - ExtendedPointY[0])
							/ LatitudeScale * ScreenHeight);
					int x2 = (int) ((ExtendedPointX[ExtendedPointCount - 1] - ScreenLongitude)
							/ LongitudeScale * ScreenWidth);
					int y2 = (int) ((ScreenLatitude - ExtendedPointY[ExtendedPointCount - 1])
							/ LatitudeScale * ScreenHeight);
					x1 += ScreenDeltaX;
					y1 += ScreenDeltaY;
					x2 += ScreenDeltaX;
					y2 += ScreenDeltaY;
					line = new Line2D.Double(x1, y1, x2, y2);
					g_2d.draw(line);
				}
				if (ExtendedPointSelectCount != 0) {
					for (int i = 0; i < ExtendedPointSelectCount; i++) {
						int xx = (int) ((ExtendedPointX[ExtendedPointSelectList[i]] - ScreenLongitude)
								/ LongitudeScale * ScreenWidth);
						int yy = (int) ((ScreenLatitude - ExtendedPointY[ExtendedPointSelectList[i]])
								/ LatitudeScale * ScreenHeight);
						xx += ScreenDeltaX;
						yy += ScreenDeltaY;
						g_2d.setColor(Color.red);
						Ellipse2D pp = new Ellipse2D.Double(xx - 4, yy - 4, 8,
								8);
						g_2d.draw(pp);
					}
				}
			}
			if (IsAllElementInvisible)
				return;
			// Draw for Extended Database--------------------------
			SetAREA(ScreenLongitude, ScreenLongitude + LongitudeScale,
					ScreenLatitude - LatitudeScale, ScreenLatitude);
			if (!IsAllLineInvisible)
				if (LineDatabaseFile != null) {
					int binary, choose, now, p1, p2;
					Line2D line;
					tempsize = 0.2 - LongitudeScale
							/ (LongitudeEnd - LongitudeStart);
					tempsize = tempsize <= 0 ? 0 : tempsize;
					g_2d.setFont(new Font("黑体", 0, (int) (200 * tempsize)));
					int DrawCount = 0;
					for (int i = 0; i < LineDatabase.LineNum; i++) {
						binary = LineDatabase.LineVisible[i];
						if (binary < 0)
							continue;
						if (DrawCount < VisualObjectMaxNum)
							if ((binary & Ox("1")) != 0) {
								if (LineDatabase.LineHint[i]
										.indexOf("[Info:Cache]") != -1)
									if (LineDatabase.LineHint[i]
											.indexOf("[Info:Path]") != -1) {
										bs = new BasicStroke(1,
												BasicStroke.CAP_ROUND,
												BasicStroke.JOIN_ROUND);
										g_2d.setStroke(bs);
									}
								if (((!ShowVisualFeature)&&((binary & Ox("1000")) != 0))
									||(ShowVisualFeature&&(LineDatabase.LineHint[i].indexOf("[LineVisible:]")!=-1)))
								{// For
																	// Line----------------------------
									choose = (binary >> 10) & Ox("111");
									g_2d.setColor(getChooseColor(choose));

									BasicStroke bs_temp = null;
									Color color_temp = null;
									if (ShowVisualFeature) {
										// bs_temp=new BasicStroke(1.0f,
										// BasicStroke.CAP_BUTT,
										// BasicStroke.JOIN_MITER, 10.0f, dash,
										// 0);
										bs_temp = GetVisualLineStroke(LineDatabase.LineHint[i]);
										g_2d.setStroke(bs_temp);
										color_temp = g_2d.getColor();
										g_2d.setColor(GetVisualColor(LineDatabase.LineHint[i],"Line"));
									}

									now = LineDatabase.LineHead[i];
									p1 = now;

									while (true) {
										p2 = LineDatabase.AllPointNext[p1];
										if (p2 == -1)
											break;
										if (!CheckInAREA(
												LineDatabase.AllPointX[p1],
												LineDatabase.AllPointY[p1],
												LineDatabase.AllPointX[p2],
												LineDatabase.AllPointY[p2])) {
											p1 = p2;
											continue;
										}
										int x1 = (int) ((LineDatabase.AllPointX[p1] - ScreenLongitude)
												/ LongitudeScale * ScreenWidth);
										int y1 = (int) ((ScreenLatitude - LineDatabase.AllPointY[p1])
												/ LatitudeScale * ScreenHeight);
										int x2 = (int) ((LineDatabase.AllPointX[p2] - ScreenLongitude)
												/ LongitudeScale * ScreenWidth);
										int y2 = (int) ((ScreenLatitude - LineDatabase.AllPointY[p2])
												/ LatitudeScale * ScreenHeight);
										x1 += ScreenDeltaX;
										y1 += ScreenDeltaY;
										x2 += ScreenDeltaX;
										y2 += ScreenDeltaY;
										line = new Line2D.Double(x1, y1, x2, y2);

										if (CheckInScreen(x1, y1, ScreenWidth, ScreenHeight)
												|| CheckInScreen(x2, y2, ScreenWidth, ScreenHeight)
												|| CheckInScreen(x1, y1, x2, y2, ScreenWidth, ScreenHeight)) {
											if (ShowVisualFeature)
												g_2d.setStroke(bs_temp);
											g_2d.draw(line);
											if ((ShowVisualFeature)
													&& (GetVisualArrow(LineDatabase.LineHint[i]))) {
												g_2d.setStroke(bs);
												g_2d.draw(new Line2D.Double(
														(double) x2,
														(double) y2,
														x2
																+ 0.2
																* (0.87 * (x1 - x2) - (y1 - y2) * 0.34),
														y2
																+ 0.2
																* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))));
												g_2d.draw(new Line2D.Double(
														(double) x2,
														(double) y2,
														x2
																+ 0.2
																* (0.87 * (x1 - x2) + (y1 - y2) * 0.34),
														y2
																+ 0.2
																* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))));
											}

											DrawCount++;
										}
										p1 = p2;
									}

									if (ShowVisualFeature) {
										g_2d.setColor(color_temp);
										g_2d.setStroke(bs);
									}
								}
								if ( ((!ShowVisualFeature)&&((binary & Ox("100")) != 0))
										||(ShowVisualFeature&&(LineDatabase.LineHint[i].indexOf("[PointVisible:]")!=-1))
										) {// For
																// Point-------------------------
									choose = (binary >> 7) & Ox("111");
									g_2d.setColor(getChooseColor(choose));

									Color color_temp = null;
									int PointSize = 6;
									if (ShowVisualFeature) {
										color_temp = g_2d.getColor();
										g_2d.setColor(GetVisualColor(
												LineDatabase.LineHint[i],
												"Point"));
									}

									now = LineDatabase.LineHead[i];
									while (now != -1) {
										if (!CheckInAREA(
												LineDatabase.AllPointX[now],
												LineDatabase.AllPointY[now])) {
											now = LineDatabase.AllPointNext[now];
											continue;
										}
										int xx = (int) ((LineDatabase.AllPointX[now] - ScreenLongitude)
												/ LongitudeScale * ScreenWidth);
										int yy = (int) ((ScreenLatitude - LineDatabase.AllPointY[now])
												/ LatitudeScale * ScreenHeight);
										xx += ScreenDeltaX;
										yy += ScreenDeltaY;
										if (CheckInScreen(xx, yy, ScreenWidth, ScreenHeight)) {
											if (ShowVisualFeature)
												PointSize = GetVisualPointSize(LineDatabase.LineHint[i]);
											else
												PointSize = 6;
											g_2d.fillOval(xx - PointSize / 2,
													yy - PointSize / 2,
													PointSize, PointSize);
											DrawCount++;
										}
										now = LineDatabase.AllPointNext[now];
									}

								}
								if (
										((!ShowVisualFeature)&&((binary & Ox("10")) != 0))
										||
										(ShowVisualFeature&&(LineDatabase.LineHint[i].indexOf("[WordVisible:]")!=-1))
									){// For
																// Word--------------------------
									choose = (binary >> 4) & Ox("111");
									g_2d.setColor(getChooseColor(choose));
									now = LineDatabase.LineHead[i];
									int xx = (int) ((LineDatabase.AllPointX[now]
											+ LineDatabase.dx[i] - ScreenLongitude)
											/ LongitudeScale * ScreenWidth);
									int yy = (int) ((ScreenLatitude
											- LineDatabase.AllPointY[now] - LineDatabase.dy[i])
											/ LatitudeScale * ScreenHeight);
									xx += ScreenDeltaX;
									yy += ScreenDeltaY;
									if (IsAlignLinesTag) {
										String str = LineDatabase.getTitle(i);
										int size = setAlignTagVector(
												LineDatabase,
												str,
												now,
												(ScreenWidth / 2 / LongitudeScale)
														+ (ScreenHeight / 2 / LatitudeScale));
										g_2d.setFont(new Font("黑体", 0, size));
										for (int ii = 0; ii < str.length(); ii++) {
											xx = (int) ((AlignPosX[ii] - ScreenLongitude)
													/ LongitudeScale * ScreenWidth);
											yy = (int) ((ScreenLatitude - AlignPosY[ii])
													/ LatitudeScale * ScreenHeight);
											xx += ScreenDeltaX;
											yy += ScreenDeltaY;
											if (!CheckInScreen(xx, yy, ScreenWidth, ScreenHeight))
												continue;
											g_2d.drawString(
													str.substring(ii, ii + 1),
													xx, yy);
										}
										DrawCount++;
										continue;
									}
									if (!CheckInScreen(xx - 50, yy - 50,
											xx + 50, yy + 50, ScreenWidth, ScreenHeight))
										continue;
									DrawCount++;
									if (LineDatabase.isVertical[i] == false)
										g_2d.drawString(
												LineDatabase.getTitle(i), xx,
												yy);
									else {
										String str = LineDatabase.getTitle(i);
										for (int ii = 0; ii < str.length(); ii++) {
											g_2d.drawString(
													str.substring(ii, ii + 1),
													xx,
													yy
															+ (((int) (tempsize * 200)) + 5)
															* ii);
										}
									}
								}
								if (LineDatabase.LineHint[i]
										.indexOf("[Info:Cache]") != -1)
									if (LineDatabase.LineHint[i]
											.indexOf("[Info:Path]") != -1) {
										bs = new BasicStroke(2,
												BasicStroke.CAP_ROUND,
												BasicStroke.JOIN_ROUND);
										g_2d.setStroke(bs);
									}
							}
					}
				}
			if (!IsAllPolygonInvisible)
				if (PolygonDatabaseFile != null) {
					int binary, choose, now, p1, p2;
					Line2D line;
					tempsize = 0.20 - LongitudeScale
							/ (LongitudeEnd - LongitudeStart);
					tempsize = tempsize <= 0 ? 0 : tempsize;
					g_2d.setFont(new Font("黑体", 0, (int) (200 * tempsize)));
					int DrawCount = 0;
					for (int i = 0; i < PolygonDatabase.PolygonNum; i++) {
						binary = PolygonDatabase.PolygonVisible[i];
						if (binary < 0)
							continue;
						if (DrawCount < VisualObjectMaxNum)
							if ((binary & Ox("1")) != 0) {
								if (
										((!ShowVisualFeature)&&((binary & Ox("1000")) != 0))
										||
										(ShowVisualFeature&&(PolygonDatabase.PolygonHint[i].indexOf("[LineVisible:]")!=-1))
									){// For
																	// Line----------------------------
									choose = (binary >> 10) & Ox("111");
									g_2d.setColor(getChooseColor(choose));

									java.awt.Polygon ColorPolygon = new java.awt.Polygon();
									BasicStroke bs_temp = null;
									Color color_default = null;
									Color color_Line = null;
									Color color_Polygon = null;
									if (ShowVisualFeature) {
										// bs_temp=new BasicStroke(1.0f,
										// BasicStroke.CAP_BUTT,
										// BasicStroke.JOIN_MITER, 10.0f, dash,
										// 0);
										bs_temp = GetVisualLineStroke(PolygonDatabase.PolygonHint[i]);
										g_2d.setStroke(bs_temp);
										color_default = g_2d.getColor();
										color_Line = GetVisualColor(
												PolygonDatabase.PolygonHint[i],
												"Line");
										color_Polygon = GetVisualColor(
												PolygonDatabase.PolygonHint[i],
												"Polygon");
										g_2d.setColor(color_Line);
									}

									now = PolygonDatabase.PolygonHead[i];
									p1 = now;

									while (true) {
										p2 = PolygonDatabase.AllPointNext[p1];
										if (p2 == -1)
											p2 = now;
										if (!CheckInAREA(
												PolygonDatabase.AllPointX[p1],
												PolygonDatabase.AllPointY[p1],
												PolygonDatabase.AllPointX[p2],
												PolygonDatabase.AllPointY[p2])) {
											p1 = p2;
											if (p1 == now)
												break;
											continue;
										}
										int x1 = (int) ((PolygonDatabase.AllPointX[p1] - ScreenLongitude)
												/ LongitudeScale * ScreenWidth);
										int y1 = (int) ((ScreenLatitude - PolygonDatabase.AllPointY[p1])
												/ LatitudeScale * ScreenHeight);
										int x2 = (int) ((PolygonDatabase.AllPointX[p2] - ScreenLongitude)
												/ LongitudeScale * ScreenWidth);
										int y2 = (int) ((ScreenLatitude - PolygonDatabase.AllPointY[p2])
												/ LatitudeScale * ScreenHeight);
										x1 += ScreenDeltaX;
										y1 += ScreenDeltaY;
										x2 += ScreenDeltaX;
										y2 += ScreenDeltaY;
										line = new Line2D.Double(x1, y1, x2, y2);
										ColorPolygon.addPoint(x1, y1);
										if (CheckInScreen(x1, y1, ScreenWidth, ScreenHeight)
												|| CheckInScreen(x2, y2, ScreenWidth, ScreenHeight)
												|| CheckInScreen(x1, y1, x2, y2, ScreenWidth, ScreenHeight)) {
											if (ShowVisualFeature)
												g_2d.setStroke(bs_temp);
											g_2d.draw(line);
											if ((ShowVisualFeature)
													&& (GetVisualArrow(PolygonDatabase.PolygonHint[i]))) {
												g_2d.setStroke(bs);
												g_2d.draw(new Line2D.Double(
														(double) x2,
														(double) y2,
														x2
																+ 0.2
																* (0.87 * (x1 - x2) - (y1 - y2) * 0.34),
														y2
																+ 0.2
																* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))));
												g_2d.draw(new Line2D.Double(
														(double) x2,
														(double) y2,
														x2
																+ 0.2
																* (0.87 * (x1 - x2) + (y1 - y2) * 0.34),
														y2
																+ 0.2
																* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))));
											}
											DrawCount++;
										}
										p1 = p2;
										if (p1 == now)
											break;
									}

									if (ShowVisualFeature) {
										g_2d.setColor(color_Polygon);
										if(PolygonDatabase.PolygonHint[i].indexOf("[PolygonVisible:]")!=-1)
											g_2d.fillPolygon(ColorPolygon);
										g_2d.setColor(color_default);
										g_2d.setStroke(bs);
									}
								}
								if (
										((!ShowVisualFeature)&&((binary & Ox("100")) != 0))
										||
										(ShowVisualFeature&&(PolygonDatabase.PolygonHint[i].indexOf("[PointVisible:]")!=-1))
									){// For
																// Point-------------------------
									choose = (binary >> 7) & Ox("111");
									g_2d.setColor(getChooseColor(choose));

									Color color_temp = null;
									int PointSize = 6;
									if (ShowVisualFeature) {
										color_temp = g_2d.getColor();
										g_2d.setColor(GetVisualColor(
												PolygonDatabase.PolygonHint[i],
												"Point"));
									}

									now = PolygonDatabase.PolygonHead[i];
									while (now != -1) {
										if (!CheckInAREA(
												PolygonDatabase.AllPointX[now],
												PolygonDatabase.AllPointY[now])) {
											now = PolygonDatabase.AllPointNext[now];
											continue;
										}
										int xx = (int) ((PolygonDatabase.AllPointX[now] - ScreenLongitude)
												/ LongitudeScale * ScreenWidth);
										int yy = (int) ((ScreenLatitude - PolygonDatabase.AllPointY[now])
												/ LatitudeScale * ScreenHeight);
										xx += ScreenDeltaX;
										yy += ScreenDeltaY;
										if (CheckInScreen(xx, yy, ScreenWidth, ScreenHeight)) {
											if (ShowVisualFeature)
												PointSize = GetVisualPointSize(PolygonDatabase.PolygonHint[i]);
											else
												PointSize = 6;
											g_2d.fillOval(xx - PointSize / 2,
													yy - PointSize / 2,
													PointSize, PointSize);
											DrawCount++;
										}
										now = PolygonDatabase.AllPointNext[now];
									}

								}
								if (
										((!ShowVisualFeature)&&((binary & Ox("10")) != 0))
										||
										(ShowVisualFeature&&(PolygonDatabase.PolygonHint[i].indexOf("[WordVisible:]")!=-1))
									){// For
																// Word--------------------------
									choose = (binary >> 4) & Ox("111");
									g_2d.setColor(getChooseColor(choose));
									now = PolygonDatabase.PolygonHead[i];
									int xx, yy;
									if (IsAlignPolygonsTag) {
										String str = PolygonDatabase
												.getTitle(i);
										int size = Screen.setCenterTagVector(
												PolygonDatabase, str, now,
												ScreenWidth / LongitudeScale);
										g_2d.setFont(new Font("黑体", 0, size));
										for (int ii = 0; ii < str.length(); ii++) {
											xx = (int) ((Screen.CenterPosX[ii] - ScreenLongitude)
													/ LongitudeScale * ScreenWidth);
											yy = (int) ((ScreenLatitude - Screen.CenterPosY[ii])
													/ LatitudeScale * ScreenHeight);
											xx += ScreenDeltaX;
											yy += ScreenDeltaY;
											if (!CheckInScreen(xx, yy, ScreenWidth, ScreenHeight))
												continue;
											g_2d.drawString(
													str.substring(ii, ii + 1),
													xx, yy);
										}
										continue;
									}
									xx = (int) ((PolygonDatabase.AllPointX[now]
											+ PolygonDatabase.dx[i] - ScreenLongitude)
											/ LongitudeScale * ScreenWidth);
									yy = (int) ((ScreenLatitude
											- PolygonDatabase.AllPointY[now] - PolygonDatabase.dy[i])
											/ LatitudeScale * ScreenHeight);
									xx += ScreenDeltaX;
									yy += ScreenDeltaY;
									if (!CheckInScreen(xx - 50, yy - 50,
											xx + 50, yy + 50, ScreenWidth, ScreenHeight))
										continue;
									DrawCount++;
									if (PolygonDatabase.isVertical[i] == false)
										g_2d.drawString(
												PolygonDatabase.getTitle(i),
												xx, yy);
									else {
										String str = PolygonDatabase
												.getTitle(i);
										for (int ii = 0; ii < str.length(); ii++) {
											g_2d.drawString(
													str.substring(ii, ii + 1),
													xx,
													yy
															+ (((int) (tempsize * 200)) + 5)
															* ii);
										}
									}
								}
							}
					}
				}
			if ((!IsAllPointInvisible) && (!IsShowAlphaDistribution))
				if (PointDatabaseFile != null) {
					int binary, choose, now, p1, p2;
					Point2D Point;
					tempsize = 0.2 - LongitudeScale
							/ (LongitudeEnd - LongitudeStart);
					tempsize = tempsize <= 0 ? 0 : tempsize;
					g_2d.setFont(new Font("黑体", 0, (int) (200 * tempsize)));
					int DrawCount = 0;
					for (int i = 0; i < PointDatabase.PointNum; i++) {
						binary = PointDatabase.PointVisible[i];
						if (DrawCount > VisualObjectMaxNum)
							break;
						if ((binary & Ox("1")) != 0) {
							if (binary < 0)
								continue;
							if (!CheckInAREA(PointDatabase.AllPointX[i],
									PointDatabase.AllPointY[i])) {
								continue;
							}
							if (
									((!ShowVisualFeature)&&((binary & Ox("100")) != 0))
									||
									(ShowVisualFeature&&(PointDatabase.PointHint[i].indexOf("[PointVisible:]")!=-1))
								){// For
															// Point-------------------------
								choose = (binary >> 7) & Ox("111");
								g_2d.setColor(getChooseColor(choose));

								Color color_temp = null;
								int PointSize = 6;
								if (ShowVisualFeature) {
									color_temp = g_2d.getColor();
									g_2d.setColor(GetVisualColor(
											PointDatabase.PointHint[i], "Point"));
								}

								int xx = (int) ((PointDatabase.AllPointX[i] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int yy = (int) ((ScreenLatitude - PointDatabase.AllPointY[i])
										/ LatitudeScale * ScreenHeight);
								xx += ScreenDeltaX;
								yy += ScreenDeltaY;
								if (!CheckInScreen(xx, yy, ScreenWidth, ScreenHeight))
									continue;
								if (!IsEngravePointShape) {
									if (ShowVisualFeature)
										PointSize = GetVisualPointSize(PointDatabase.PointHint[i]);
									else
										PointSize = 6;
									g_2d.fillOval(xx - PointSize / 2, yy
											- PointSize / 2, PointSize,
											PointSize);
								} else
									g_2d.drawRect(xx, yy, 1, 1);
								DrawCount++;
							}
							if (
									((!ShowVisualFeature)&&((binary & Ox("10")) != 0))
									||
									(ShowVisualFeature&&(PointDatabase.PointHint[i].indexOf("[WordVisible:]")!=-1))
									) {// For
															// Word--------------------------
								choose = (binary >> 4) & Ox("111");
								g_2d.setColor(getChooseColor(choose));
								if (IsAlignPointsTag > 0) {
									g_2d.setFont(new Font("黑体", 0,
											IsAlignPointsTag));
								}
								int xx = (int) ((PointDatabase.AllPointX[i] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int yy = (int) ((ScreenLatitude - PointDatabase.AllPointY[i])
										/ LatitudeScale * ScreenHeight);
								xx += ScreenDeltaX;
								yy += ScreenDeltaY;
								if (!CheckInScreen(xx, yy, ScreenWidth, ScreenHeight))
									continue;
								g_2d.drawString(PointDatabase.getTitle(i),
										xx + 3, yy + 3);
								DrawCount++;
							}
						}
					}
				}
			// AlphaGridsDistribution----------------------------------------------
			if (IsShowAlphaDistribution) {
				if (Math.abs(LastScreenLongitude - ScreenLongitude)
						+ Math.abs(LastScreenLatitude - ScreenLatitude)
						+ Math.abs(LastLongitudeScale - LongitudeScale)
						+ Math.abs(LastLatitudeScale - LatitudeScale)
						+ Math.abs(LastAlphaPercentScale - AlphaPercentScale)
						+ Math.abs(LastRadiationDistance-RadiationDistance)> 1e-6) {
					LastScreenLongitude = ScreenLongitude;
					LastScreenLatitude = ScreenLatitude;
					LastLongitudeScale = LongitudeScale;
					LastLatitudeScale = LatitudeScale;
					LastAlphaPercentScale = AlphaPercentScale;
					LastRadiationDistance=RadiationDistance;
					if (AlphaGridsRow > ScreenHeight)
						AlphaGridsRow = ScreenHeight;
					if (AlphaGridsColumn > ScreenWidth)
						AlphaGridsColumn = ScreenWidth;
					for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
						for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
							AlphaGridsCounter[Row_i][Col_i] = 0;
							RadiationGridsCounter[Row_i][Col_i] = 0;
						}
					}
					double Xstep = LongitudeScale / AlphaGridsColumn;
					double Ystep = LatitudeScale / AlphaGridsRow;
					int pos_row, pos_col;
					for (int ptr_i = 0; ptr_i < PointDatabase.PointNum; ptr_i++) {
						pos_col = (int) ((PointDatabase.AllPointX[ptr_i] - ScreenLongitude) / Xstep);
						if (pos_col < 0)
							continue;
						if (pos_col >= AlphaGridsColumn)
							continue;
						pos_row = (int) ((ScreenLatitude - PointDatabase.AllPointY[ptr_i]) / Ystep);
						if (pos_row < 0)
							continue;
						if (pos_row >= AlphaGridsRow)
							continue;
						AlphaGridsCounter[pos_row][pos_col]++;
					}
					float AllCounter = 0;
					float MaxCounter = 0;
					int dx=0;
					int dy=0;
					float M_dist=0;
					float power=0;
					for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
						for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
								power=AlphaGridsCounter[Row_i][Col_i];
								RadiationGridsCounter[Row_i][Col_i]+=power;
							if(RadiationDistance!=0)
							for(dx=0; dx<= RadiationDistance; dx++){
								for(dy=0;dy<=RadiationDistance-dx;dy++){
								M_dist=dx+dy;
								if(dx==0){
									if(Row_i-dy>=0) 
										RadiationGridsCounter[Row_i-dy][Col_i]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
									if(Row_i+dy<AlphaGridsRow)
										RadiationGridsCounter[Row_i+dy][Col_i]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								}else if(dy==0){
									if(Col_i-dx>=0)
										RadiationGridsCounter[Row_i][Col_i-dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
									if(Col_i+dx<AlphaGridsColumn)
										RadiationGridsCounter[Row_i][Col_i+dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								}else{
									if(Row_i-dy>=0) if(Col_i-dx>=0)
										RadiationGridsCounter[Row_i-dy][Col_i-dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
									if(Row_i-dy>=0) if(Col_i+dx<AlphaGridsColumn)
										RadiationGridsCounter[Row_i-dy][Col_i+dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
									if(Row_i+dy<AlphaGridsRow) if(Col_i-dx>=0)
										RadiationGridsCounter[Row_i+dy][Col_i-dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
									if(Row_i+dy<AlphaGridsRow) if(Col_i+dx<AlphaGridsColumn)
										RadiationGridsCounter[Row_i+dy][Col_i+dx]+=(RadiationDistance-M_dist+0.001f)*power/RadiationDistance;
								}
								}
							}
						}
					}
					for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
						for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
							AlphaGridsCounter[Row_i][Col_i]+=RadiationGridsCounter[Row_i][Col_i];
							AllCounter += AlphaGridsCounter[Row_i][Col_i];
							MaxCounter = Math.max(MaxCounter,
									AlphaGridsCounter[Row_i][Col_i]);
						}
					}
					
					if (AlphaPercentScale > 0)
						MaxCounter = AlphaPercentScale;
					else MaxCounter*=0.5;

					Handle.ShowTextArea1(LanguageDic.GetWords("总计 ")
							+ AllCounter + " pts", true);
					Handle.ShowTextArea2(LanguageDic.GetWords("网格浓度100%对应 ")
							+ MaxCounter + " pts", true);
					for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
						for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
							AlphaGridsValue[Row_i][Col_i] = (AlphaGridsCounter[Row_i][Col_i])
									/ (MaxCounter+0.01f);
							AlphaGridsValue[Row_i][Col_i] = AlphaGridsValue[Row_i][Col_i] > 0.99f ? 0.99f
									: (AlphaGridsValue[Row_i][Col_i]+0.001f);
						}
					}
				}
				double ScreenXstep = ((double) ScreenWidth) / AlphaGridsColumn;
				double ScreenYstep = ((double) ScreenHeight) / AlphaGridsRow;

				for (int Row_i = 0; Row_i < AlphaGridsRow; Row_i++) {
					for (int Col_i = 0; Col_i < AlphaGridsColumn; Col_i++) {
						if (AlphaGridsValue[Row_i][Col_i] < 0.05)
							continue;
						AlphaComposite ac = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER,
								AlphaGridsValue[Row_i][Col_i]);
						g_2d.setComposite(ac);
						g_2d.setColor(new Color(Color.HSBtoRGB((1f-AlphaGridsValue[Row_i][Col_i])*0.67f, 1.0f, 1.0f)));
						g_2d.fillRect((int) (Col_i * ScreenXstep),
								(int) (Row_i * ScreenYstep), (int) ScreenXstep,
								(int) ScreenYstep);
					}
				}

			}
			AlphaComposite ac = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1);
			g_2d.setComposite(ac);
			// ScreenBottomHint----------------------------------------------------
			if (IsTextArea1Visible) {
				g_2d.setFont(new Font("黑体", Font.BOLD, (int) (20)));
				if (TextArea1Content == null)
					TextArea1Content = LanguageDic.GetWords("暂无信息");

				if (IsTextArea1BackGround) {
					Rectangle2D _rect = new Rectangle2D.Double(0,
							ScreenHeight - 34, ScreenWidth * 0.4, 34);
					g_2d.setPaint(Color.black);
					g_2d.fill(_rect);
				}

				g_2d.setColor(Color.red);
				g_2d.drawString(TextArea1Content, 10, ScreenHeight - 9);
				g_2d.setColor(Color.green);
			}
			if (IsTextArea2Visible) {
				g_2d.setFont(new Font("黑体", Font.BOLD, (int) (20)));
				if (TextArea2Content == null)
					TextArea2Content = LanguageDic.GetWords("暂无信息");

				if (IsTextArea2BackGround) {
					Rectangle2D _rect = new Rectangle2D.Double(
							ScreenWidth * 0.4, ScreenHeight - 34,
							ScreenWidth * 0.6, 34);
					g_2d.setPaint(Color.black);
					g_2d.fill(_rect);
				}

				g_2d.setColor(Color.red);
				g_2d.drawString(TextArea2Content,
						(int) (ScreenWidth * 0.4) + 2, ScreenHeight - 9);
				g_2d.setColor(Color.green);
			}
			// ----------------------------------------------------
		}
		public void paint(Graphics g) {
			if (DIR == null) {// 没打开文件，不显示
				setVisible(false);
				return;
			} else
				setVisible(true);
			if (image == null) {
				setBackground(Color.black);
			} else if (ShowBackGround) {
				// ------------------------------------------------------------------------------------------
				// ScreenBackGroundMove----------------------------------------------------------------------
				double MoveX = 0;
				double MoveY = 0;
				if (BackGroundMoveVectorNum != 0) {
					int min_1 = -1;
					double min_1_dis = 1e100;
					int min_2 = -1;
					double min_2_dis = 1e100;
					int min_3 = -1;
					double min_3_dis = 1e100;
					double dis = 1e100;
					double center_x = ScreenLongitude + LongitudeScale / 2;
					double center_y = ScreenLatitude - LatitudeScale / 2;
					for (int i = 0; i < BackGroundMoveVectorNum; i++) {
						dis = Math.abs(center_x - BackGroundMoveX[i])
								+ Math.abs(center_y - BackGroundMoveY[i]);
						if (dis < min_1_dis) {
							min_3 = min_2;
							min_3_dis = min_2_dis;
							min_2 = min_1;
							min_2_dis = min_1_dis;
							min_1 = i;
							min_1_dis = dis;
						} else if (dis < min_2_dis) {
							min_3 = min_2;
							min_3_dis = min_2_dis;
							min_2 = i;
							min_2_dis = dis;
						} else if (dis < min_3_dis) {
							min_3 = i;
							min_3_dis = dis;
						}
					}
					double dis_sum = 1 / min_1_dis + 1 / min_2_dis + 1
							/ min_3_dis;
					MoveX = BackGroundMoveDx[min_1] / min_1_dis
							+ BackGroundMoveDx[min_2] / min_2_dis
							+ BackGroundMoveDx[min_3] / min_3_dis;
					MoveX /= dis_sum;
					MoveY = BackGroundMoveDy[min_1] / min_1_dis
							+ BackGroundMoveDy[min_2] / min_2_dis
							+ BackGroundMoveDy[min_3] / min_3_dis;
					MoveY /= dis_sum;
				}
				// ------------------------------------------------------------------------------------------
				double Xst = ((ScreenLongitude - MoveX) - LongitudeStart)
						/ (LongitudeEnd - LongitudeStart)
						* image.getWidth(this);
				double Yst = (LatitudeEnd - (ScreenLatitude - MoveY))
						/ (LatitudeEnd - LatitudeStart) * image.getHeight(this);
				double Xlen = (LongitudeScale)
						/ (LongitudeEnd - LongitudeStart)
						* image.getWidth(this);
				double Ylen = (LatitudeScale) / (LatitudeEnd - LatitudeStart)
						* image.getHeight(this);
				// 将需要显示的经纬度范围转化为窗口界面中像素值
				g.drawImage(image, 0, 0, ScreenWidth, ScreenHeight, (int) Xst,
						(int) Yst, (int) (Xst + Xlen), (int) (Yst + Ylen), this);
			}
			Graphics2D g_2d = (Graphics2D) g;
			DBpaint(g_2d, ScreenLongitude, ScreenLatitude, LongitudeScale, LatitudeScale, ScreenWidth, ScreenHeight);
		}

		public void MoveMiddle(double midx, double midy) {// 将屏幕的中心位置移动到给定的经纬度位置
			double nowx = ScreenLongitude + LongitudeScale / 2;
			double nowy = ScreenLatitude - LatitudeScale / 2;
			double dx = midx - nowx;
			double dy = midy - nowy;
			ScreenLongitude += dx;
			ScreenLatitude += dy;
			if (CheckScreen())
				repaint();
		}

		public void MiddleReSize(double width, double height) {// 实现屏幕的中心放大
			double rate1 = width / LongitudeScale;
			double rate2 = height / LatitudeScale;
			double rate = rate1 > rate2 ? rate1 : rate2;
			double midx = ScreenLongitude + LongitudeScale / 2;
			double midy = ScreenLatitude - LatitudeScale / 2;
			LongitudeScale *= rate;
			LatitudeScale *= rate;
			ScreenLongitude = midx - LongitudeScale / 2;
			ScreenLatitude = midy + LatitudeScale / 2;
			if (CheckScreen())
				repaint();
		}

		long scale = 0;
		int PressedX, PressedY;
		double PressedLongitude, PressedLatitude;

		public void mousePressed(MouseEvent e) {
			PressedX = e.getX();
			PressedY = e.getY();
			PressedLongitude = ScreenLongitude + (PressedX * LongitudeScale)
					/ ScreenWidth;
			PressedLatitude = ScreenLatitude - (PressedY * LatitudeScale)
					/ ScreenHeight;
			if (lock)
				return;
		}

		int[] SelectedPointList = new int[1000];

		boolean InsideSelectedRectangle(double x, double y) {// 判断是否在鼠标拖框的范围之内
			int xx = (int) ((x - ScreenLongitude) / LongitudeScale * ScreenWidth);
			int yy = (int) ((ScreenLatitude - y) / LatitudeScale * ScreenWidth);
			if (xx < DrawRectX1)
				return false;
			if (xx > DrawRectX2)
				return false;
			if (yy < DrawRectY1)
				return false;
			if (yy > DrawRectY2)
				return false;
			return true;
		}

		public void mouseReleased(MouseEvent e) {
			LandMarkSelectedNum = 0;

			if (NowPanel instanceof ExtendedToolPaneInterface) {
				ExtendedPointSelectCount = 0;
				CanDrawRect = false;
				repaint();
				if (!lock)
					return;
				int dx = e.getX() - PressedX;
				int dy = e.getY() - PressedY;
				if (Math.abs(dx) + Math.abs(dy) < 10) {
					return;
				}
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				((ExtendedToolPaneInterface) NowPanel).convey(PressedLongitude,
						PressedLatitude, x, y);
				return;
			}

			if ((NowPanel == LandMarkEditPane) || (NowPanel == BasicInfoPane)) {
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					IsLandMarkSelected[i] = false;
				}
				repaint();
			}
			if (lock && (NowPanel == CalibratePane) && (!CalibratePane.CanAdd)) {
				CanDrawRect = false;
				int dx = e.getX() - PressedX;
				int dy = e.getY() - PressedY;
				if (Math.abs(dx) + Math.abs(dy) < 10) {
					return;
				}
				DrawRect(PressedX, PressedY, e.getX(), e.getY());
				SelectedPointList[0] = 0;
				for (int i = 0; i < XYCount; i++) {
					if (InsideSelectedRectangle(Xlist[i], Ylist[i])) {
						SelectedPointList[0]++;
						SelectedPointList[SelectedPointList[0]] = i;
					}
				}
				repaint();
			} else if (lock
					&& ((NowPanel == LandMarkEditPane) || (NowPanel == BasicInfoPane))) {
				CanDrawRect = false;
				int dx = e.getX() - PressedX;
				int dy = e.getY() - PressedY;
				if (Math.abs(dx) + Math.abs(dy) < 10) {
					return;
				}
				DrawRect(PressedX, PressedY, e.getX(), e.getY());
				for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
					if (InsideSelectedRectangle(GPSPoints.LandMarkLongitude[i],
							GPSPoints.LandMarkLatitude[i])) {
						IsLandMarkSelected[i] = true;
						LandMarkSelectedNum++;
					}
				}
				repaint();
				if (LandMarkSelectedNum == 0)
					return;
				if (!IsLandMarkOnScreen)
					return;
				if (NowPanel == BasicInfoPane) {
					int n = JOptionPane.showConfirmDialog(null,
							LanguageDic.GetWords("根据选中的地标点顺序播放地标点的详细资料"),
							LanguageDic.GetWords("演示播放"),
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						for (int i = 0; i < GPSPoints.LandMarkNum; i++) {
							if (IsLandMarkSelected[i]) {
								BasicInfoPane.SlideID = i;
								break;
							}
						}
						BasicInfoPane.FullFillSlide();
					} else {
						BasicInfoPane.SlideID = -1;
					}
				}
			} else if (lock
					&& ((NowPanel == TaxiSearchPane) && (TaxiSearchPane.CanShowTaxi))) {
				CanDrawRect = false;
				int dx = e.getX() - PressedX;
				int dy = e.getY() - PressedY;
				if (Math.abs(dx) + Math.abs(dy) < 10)
					return;
				DrawRect(PressedX, PressedY, e.getX(), e.getY());
				for (int i = 0; i < XYCount; i++) {
					if (InsideSelectedRectangle(Xlist[i], Ylist[i])) {
						ConcentrateTaxiWizard.Code.setText(TempString[i]);
						ConcentrateTaxiWizard.emerge();
						break;
					}
				}
			}
			if (lock)
				return;
		}

		int LandMarkSelectedNum = 0;

		public void mouseEntered(MouseEvent e) {
			// ---------------------------------
			if (lock)
				return;
		}

		public void mouseExited(MouseEvent e) {
			// ----------------------------------
			if (lock)
				return;
		}

		public boolean ShowBackGround = true;

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 3) {
				ForbidenOperationSwitch();
				return;
			}
			int mods = e.getModifiers();
			if ((mods & InputEvent.BUTTON1_MASK) == 0) {
				if (e.getClickCount() == 1) {
					if (NowPanel instanceof ExtendedToolPaneInterface)
						((ExtendedToolPaneInterface) NowPanel).confirm();
				}
				return;
			}
			if (NowPanel instanceof ExtendedToolPaneInterface) {
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				((ExtendedToolPaneInterface) NowPanel).convey(x, y);
				return;
			}

			if (NowPanel == TwoPointPane) {
				if (!TwoPointPane.CanChange)
					return;
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				NowPanel.setLongitude(x);
				NowPanel.setLatitude(y);
				TwoPointPane.setClickLongitude(x);
				TwoPointPane.setClickLatitude(y);
				if (TwoPointPane.SetPoint1.isSelected()) {
					TwoPointPane.SetPoint1.setSelected(false);
					TwoPointPane.SetPoint2.setSelected(true);
				} else {
					TwoPointPane.SetPoint1.setSelected(true);
					TwoPointPane.SetPoint2.setSelected(false);
				}
				Xlist[XYCount] = x;
				Ylist[XYCount] = y;
				XYCount++;
				repaint();
			} else if (NowPanel == CalibratePane) {
				if (!CalibratePane.CanAdd)
					return;
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				NowPanel.setLongitude(x);
				NowPanel.setLatitude(y);
				Xlist[XYCount] = x;
				Ylist[XYCount] = y;
				XYCount++;
				repaint();
			} else if (NowPanel == TaxiSearchPane) {
				if (TaxiSearchPane.CanShowTaxi)
					return;
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				XYCount = 1;
				Xlist[0] = x;
				Ylist[0] = y;
				TaxiSearchPane.Trace = -1;
				TaxiSearchPane.TraceTheNearest.setEnabled(true);
				TaxiSearchPane.StopTrace.setEnabled(false);
				TaxiSearchPane.CenterLongitude.setText(java.lang.Double
						.toString(x));
				TaxiSearchPane.CenterLatitude.setText(java.lang.Double
						.toString(y));
				repaint();
			} else if (NowPanel == RouteSearchPane) {
				if (RouteSearchPane.PathOnScreen)
					return;
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				Xlist[XYCount] = x;
				Ylist[XYCount] = y;
				XYCount++;
				if (RouteSearchPane.FocusSource) {
					RouteSearchPane.SourceLongitude.setText(java.lang.Double
							.toString(x));
					RouteSearchPane.SourceLatitude.setText(java.lang.Double
							.toString(y));
				} else {
					RouteSearchPane.TerminalLongitude.setText(java.lang.Double
							.toString(x));
					RouteSearchPane.TerminalLatitude.setText(java.lang.Double
							.toString(y));
				}
				if (RouteSearchPane.FocusSource) {
					RouteSearchPane.FocusSource = false;
					RouteSearchPane.setSource.setSelected(false);
					RouteSearchPane.setTerminal.setSelected(true);
				} else {
					RouteSearchPane.FocusSource = true;
					RouteSearchPane.setSource.setSelected(true);
					RouteSearchPane.setTerminal.setSelected(false);
				}
				repaint();
			} else if (NowPanel == LandMarkEditPane) {
				double x = ScreenLongitude + e.getX() * LongitudeScale
						/ ScreenWidth;
				double y = ScreenLatitude - e.getY() * LatitudeScale
						/ ScreenHeight;
				Xlist[0] = x;
				Ylist[0] = y;
				XYCount = 1;
				LandMarkEditPane.LandMarkLongitude.setText(java.lang.Double
						.toString(x));
				LandMarkEditPane.LandMarkLatitude.setText(java.lang.Double
						.toString(y));
				LandMarkEditPane.LandMarkName.setText("");
				LandMarkEditPane.LandMarkType.setText("");
				LandMarkEditPane.LandMarkScript.setText("");
				repaint();
			}
			// -----------------------------------
			if (lock)
				return;
		}

		double DrawRectX1, DrawRectX2, DrawRectY1, DrawRectY2;
		boolean CanDrawRect = false;

		void DrawRect(int x1, int y1, int x2, int y2) {// 根据四个坐标在地图上画出选中框
			DrawRectX1 = x1 < x2 ? x1 : x2;
			DrawRectX2 = x2 > x1 ? x2 : x1;
			DrawRectY1 = y1 < y2 ? y1 : y2;
			DrawRectY2 = y2 > y1 ? y2 : y1;
		}

		public void mouseDragged(MouseEvent e) {
			double lastScreenLongitude;
			double lastScreenLatitude;

			if (NowPanel instanceof ExtendedToolPaneInterface) {
				if (lock) {
					CanDrawRect = true;
					DrawRect(PressedX, PressedY, e.getX(), e.getY());
					repaint();
					return;
				}
				int dx = e.getX() - PressedX;
				int dy = e.getY() - PressedY;
				if (Math.abs(dx) + Math.abs(dy) < 10) {
					return;
				}
				lastScreenLongitude = ScreenLongitude;
				lastScreenLatitude = ScreenLatitude;
				ScreenLongitude = PressedLongitude - e.getX() * LongitudeScale
						/ ScreenWidth;
				ScreenLatitude = PressedLatitude + e.getY() * LatitudeScale
						/ ScreenHeight;
				if (ScreenLongitude < LongitudeStart)
					ScreenLongitude = LongitudeStart;
				if (ScreenLongitude + LongitudeScale > LongitudeEnd)
					ScreenLongitude = LongitudeEnd - LongitudeScale;
				if (ScreenLatitude > LatitudeEnd)
					ScreenLatitude = LatitudeEnd;
				if (ScreenLatitude - LatitudeScale < LatitudeStart)
					ScreenLatitude = LatitudeStart + LatitudeScale;
				repaint();
				return;
			}

			if (lock && (NowPanel == CalibratePane) && (!CalibratePane.CanAdd)) {
				CanDrawRect = true;
				DrawRect(PressedX, PressedY, e.getX(), e.getY());
				repaint();
			} else if (lock
					&& ((NowPanel == LandMarkEditPane) || (NowPanel == BasicInfoPane))) {
				CanDrawRect = true;
				DrawRect(PressedX, PressedY, e.getX(), e.getY());
				repaint();
			} else if (lock
					&& ((NowPanel == TaxiSearchPane) && (TaxiSearchPane.CanShowTaxi))) {
				CanDrawRect = true;
				DrawRect(PressedX, PressedY, e.getX(), e.getY());
				repaint();
			}
			if (lock)
				return;
			int dx = e.getX() - PressedX;
			int dy = e.getY() - PressedY;
			if (Math.abs(dx) + Math.abs(dy) < 10) {
				return;
			}
			lastScreenLongitude = ScreenLongitude;
			lastScreenLatitude = ScreenLatitude;
			ScreenLongitude = PressedLongitude - e.getX() * LongitudeScale
					/ ScreenWidth;
			ScreenLatitude = PressedLatitude + e.getY() * LatitudeScale
					/ ScreenHeight;
			if (ScreenLongitude < LongitudeStart)
				ScreenLongitude = LongitudeStart;
			if (ScreenLongitude + LongitudeScale > LongitudeEnd)
				ScreenLongitude = LongitudeEnd - LongitudeScale;
			if (ScreenLatitude > LatitudeEnd)
				ScreenLatitude = LatitudeEnd;
			if (ScreenLatitude - LatitudeScale < LatitudeStart)
				ScreenLatitude = LatitudeStart + LatitudeScale;
			repaint();
		}

		public void mouseMoved(MouseEvent e) {
			double x = ScreenLongitude + e.getX() * LongitudeScale
					/ ScreenWidth;
			double y = ScreenLatitude - e.getY() * LatitudeScale / ScreenHeight;

			if (NowPanel instanceof ExtendedToolPaneInterface) {
				((ExtendedToolPaneInterface) NowPanel).setLongitudeLatitude(x,
						y);
				return;
			}

			NowPanel.setLongitude(x);
			NowPanel.setLatitude(y);
			// -----------------------------------
			if (lock)
				return;
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (lock)
				return;
			int num = e.getWheelRotation();
			if (num < 0) {
				CenterLarger(e.getX(), e.getY());
			} else if (num > 0) {
				CenterSmaller(e.getX(), e.getY());
			}
		}

		void initScreen() {// 一旦屏幕缩放出现异常就自动转为默认的视角，全屏显示地图
			ScreenLongitude = LongitudeStart;
			ScreenLatitude = LatitudeEnd;
			LongitudeScale = (LongitudeEnd - LongitudeStart);
			LatitudeScale = (LatitudeEnd - LatitudeStart);
			repaint();
		}

		void CenterLarger(double x0, double y0) {// 以给定的位置作为不动点放大屏幕视野
			if (LongitudeScale / (LongitudeEnd - LongitudeStart) < 1.01 / HighestRate)
				return;
			double CenterLongitude = ScreenLongitude + x0 / ScreenWidth
					* LongitudeScale;
			double CenterLatitude = ScreenLatitude - y0 / ScreenHeight
					* LatitudeScale;
			double dx = ScreenLongitude - CenterLongitude;
			double dy = ScreenLatitude - CenterLatitude;
			dx /= rate;
			dy /= rate;
			ScreenLongitude = dx + CenterLongitude;
			ScreenLatitude = dy + CenterLatitude;
			LongitudeScale /= rate;
			LatitudeScale /= rate;
			if (CheckScreen())
				repaint();
		}

		void CenterSmaller(double x0, double y0) {// 以给定的位置作为不动点缩小屏幕视野
			double CenterLongitude = ScreenLongitude + x0 / ScreenWidth
					* LongitudeScale;
			double CenterLatitude = ScreenLatitude - y0 / ScreenHeight
					* LatitudeScale;
			double dx = ScreenLongitude - CenterLongitude;
			double dy = ScreenLatitude - CenterLatitude;
			dx *= rate;
			dy *= rate;
			ScreenLongitude = dx + CenterLongitude;
			ScreenLatitude = dy + CenterLatitude;
			LongitudeScale *= rate;
			LatitudeScale *= rate;
			if (CheckScreen())
				repaint();
		}

		boolean CheckScreen() {// 检查屏幕坐标是否有异常
			if (ScreenLongitude < LongitudeStart) {
				initScreen();
				return false;
			}
			if (ScreenLatitude > LatitudeEnd) {
				initScreen();
				return false;
			}
			if (ScreenLongitude + LongitudeScale > LongitudeEnd) {
				initScreen();
				return false;
			}
			if (ScreenLatitude - LatitudeScale < LatitudeStart) {
				initScreen();
				return false;
			}
			if (LongitudeScale / (LongitudeEnd - LongitudeStart) < 1.01 / HighestRate / 4) {
				initScreen();
				return false;
			}
			return true;
		}
	}

	// Extended
	// Definition---------------------------------------------------------------
	JMenu ExtendedAbility;

	PolygonAddPaneClass PolygonAddPane;
	JMenuItem ShowPolygonAddPane;
	public Database.PolygonDataSet PolygonDatabase;
	public FreeWizard.PolygonDatabaseWizard PolygonDatabaseView;
	JMenuItem ShowPolygonDatabaseView;
	public FreeWizard.PolygonPreferenceWizard PolygonPreferenceView;
	File PolygonDatabaseFile;
	public FreeWizard.PolygonHintReLocationWizard PolygonHintReLocationView;

	LineAddPaneClass LineAddPane;
	JMenuItem ShowLineAddPane;
	public Database.LineDataSet LineDatabase;
	public FreeWizard.LineDatabaseWizard LineDatabaseView;
	JMenuItem ShowLineDatabaseView;
	public FreeWizard.LinePreferenceWizard LinePreferenceView;
	File LineDatabaseFile;
	public FreeWizard.LineHintReLocationWizard LineHintReLocationView;

	PointAddPaneClass PointAddPane;
	JMenuItem ShowPointAddPane;
	public Database.PointDataSet PointDatabase;
	public FreeWizard.PointDatabaseWizard PointDatabaseView;
	JMenuItem ShowPointDatabaseView;
	public FreeWizard.PointPreferenceWizard PointPreferenceView;
	File PointDatabaseFile;

	AutoCrossLinkPaneClass AutoCrossLinkPane;
	JMenuItem ShowAutoCrossLinkPane;
	ConnectTestPaneClass ConnectTestPane;
	JMenuItem ShowConnectTestPane;
	public CacheRoadNetworkDatabaseClass CacheRoadNetworkDatabase;

	MapElementsEditorPaneClass MapElementsEditorPane;
	JMenuItem MapElementsEditorPaneItem;

	GISCompletionPaneClass GISCompletionPane;
	JMenuItem GISCompletionPaneItem;

	ServerSocketPaneClass ServerSocketPane;
	JMenuItem ServerSocketPaneItem;

	ClientSocketPaneClass ClientSocketPane;
	JMenuItem ClientSocketPaneItem;

	JMenuItem AlignPointsTagItem;
	int IsAlignPointsTag = 0;
	JMenuItem AlignLinesTagItem;
	boolean IsAlignLinesTag = false;
	JMenuItem AlignPolygonsTagItem;
	boolean IsAlignPolygonsTag = false;

	JMenuItem ShowPointsAlphaDistribution;
	public boolean IsShowAlphaDistribution = false;
	public int AlphaGridsRow = 100;
	public int AlphaGridsColumn = 100;
	JMenuItem SetAlphaPercentScale;
	public int AlphaPercentScale = 0;
	public int RadiationDistance = 0;
	

	JMenuItem CaptureScreenItem;
	JMenuItem ExtractLineDBItem;
	JMenuItem ExtractPointDBItem;
	JMenuItem ExtractPolygonDBItem;
	JMenuItem AppendLineDBItem;
	JMenuItem AppendPointDBItem;
	JMenuItem AppendPolygonDBItem;
	JMenuItem AppendAllLineDBItem;
	JMenuItem AppendAllPointDBItem;
	JMenuItem AppendAllPolygonDBItem;
	JMenuItem CoverLineDBItem;
	JMenuItem CoverPolygonDBItem;
	JMenuItem CoverPointDBItem;
	JMenuItem VisualObjectMaxNumSetItem;
	JMenuItem PolygonsToGridsItem;

	public Database.TaxiTrajectoryDatabaseClass TaxiTrajectoryDatabase;
	File TaxiTrajectoryDatabaseFile;
	public TaxiTrajectoryViewPaneClass TaxiTrajectoryViewPane;
	JMenuItem ShowTaxiTrajectoryViewPane;

	AutoDrivePaneClass AutoDrivePane;
	JMenuItem ShowAutoDrivePane;

	JMenuItem CreateJPGImage;

	public FreeWizard.RoadConditionWizard RoadConditionView;
	JMenuItem ShowRoadConditionWizard;

	HtmlMapOutputPaneClass HtmlMapOutputPane;
	JMenuItem HtmlMapOutputPaneItem;

	public JMenuItem PointDBDeviationItem;
	public JMenuItem LineDBDeviationItem;
	public JMenuItem PolygonDBDeviationItem;
	public JMenuItem BackGroundMoveItem;
	public JMenuItem BackGroundMoveResetItem;
	public int BackGroundMoveVectorNum = 0;
	public String BackGroundMoveVectorFilePath = "BackGroundMoveVector.csv";
	public double[] BackGroundMoveX = new double[1000];
	public double[] BackGroundMoveY = new double[1000];
	public double[] BackGroundMoveDx = new double[1000];
	public double[] BackGroundMoveDy = new double[1000];

	public static String Language = "None";
	public static LanguageResources LanguageDic = new LanguageResources();

	public static boolean ShowVisualFeature = true;
	// Screen End-----------------------------------------------------
	// Preference Elements--------------------------------------------
	FreeWizard.GlobalPreferenceWizard Preference;
	JMenuItem ShowPreferenceWizard;

	// ---------------------------------------------------------------
	public MapWizard() {
		SingleItem=this;
		Face SoftFace = new Face();
		Idle(3000);
		SoftFace.dispose();
		// Rewrite the scale of Database-----------------
		Database.PointDataSet.PointMaxNum = -1;
		Database.LineDataSet.PointMaxNum = -1;
		Database.LineDataSet.LineMaxNum = -1;
		Database.PolygonDataSet.PointMaxNum = -1;
		Database.PolygonDataSet.PolygonMaxNum = -1;
		try {
			BufferedReader Configfin = new BufferedReader(new FileReader(
					new File("DB.config")));
			String buf;
			while ((buf = Configfin.readLine()) != null) {
				String[] pair = buf.split("=");
				if (pair[0].equals("Database.PointDataSet.PointMaxNum"))
					Database.PointDataSet.PointMaxNum = Integer
							.parseInt(pair[1]);
				else if (pair[0].equals("Database.LineDataSet.PointMaxNum"))
					Database.LineDataSet.PointMaxNum = Integer
							.parseInt(pair[1]);
				else if (pair[0].equals("Database.LineDataSet.LineMaxNum"))
					Database.LineDataSet.LineMaxNum = Integer.parseInt(pair[1]);
				else if (pair[0].equals("Database.PolygonDataSet.PointMaxNum"))
					Database.PolygonDataSet.PointMaxNum = Integer
							.parseInt(pair[1]);
				else if (pair[0]
						.equals("Database.PolygonDataSet.PolygonMaxNum"))
					Database.PolygonDataSet.PolygonMaxNum = Integer
							.parseInt(pair[1]);
				else if (pair[0].equals("Language"))
					Language = pair[1];
			}
			Configfin.close();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Please Check the setting in DB.config");
			System.exit(0);
		}
		// Create All the Frame--------------------------
		AboutFrame = new About();
		LandMarkQueryFrame = new LandMarkQueryFrameClass();
		LandMarkSpotFrame = new LandMarkSpotFrameClass();
		Handle = new MapHandle();
		Handle.setKernel(this);
		PolygonDatabaseView = new FreeWizard.PolygonDatabaseWizard();
		PolygonDatabaseView.setHandle(Handle);
		PolygonPreferenceView = new FreeWizard.PolygonPreferenceWizard();
		PolygonPreferenceView.setHandle(Handle);
		PolygonHintReLocationView = new FreeWizard.PolygonHintReLocationWizard();
		PolygonHintReLocationView.setHandle(Handle);

		LineDatabaseView = new FreeWizard.LineDatabaseWizard();
		LineDatabaseView.setHandle(Handle);
		LinePreferenceView = new FreeWizard.LinePreferenceWizard();
		LinePreferenceView.setHandle(Handle);
		LineHintReLocationView = new FreeWizard.LineHintReLocationWizard();
		LineHintReLocationView.setHandle(Handle);

		PointDatabaseView = new FreeWizard.PointDatabaseWizard();
		PointDatabaseView.setHandle(Handle);
		PointPreferenceView = new FreeWizard.PointPreferenceWizard();
		PointPreferenceView.setHandle(Handle);

		RoadConditionView = new FreeWizard.RoadConditionWizard();
		RoadConditionView.setHandle(Handle);

		Preference = new FreeWizard.GlobalPreferenceWizard();
		Preference.setHandle(Handle);
		// ----------------------------------------------
		init();
		setBounds(0, 0, 1005, 733);
		this.getContentPane().setBackground(Color.black);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ExecuteExit();
			}
		});
		setVisible(true);
		setTitle("CityGeoInfo");
		setResizable(false);
		ClockWizard = new ClockWizardClass();
		myTimer.start();
		setLocationRelativeTo(null);
	}

	JMenuItem ChangeMapBackground, setDefaultMapBackground,
			ScreenLocationMicroDelta, ScreenLocationReset;
	JMenuItem ClearPointDBItem, AllElementInvisible, AllPointInvisible,
			AllLineInvisible, AllPolygonInvisible;
	JMenuItem EngravePointShape, ClearLineDBItem, ClearPolygonDBItem,
			VisualFeatureSwitchItem;
	JMenuItem SplitJPGItem,OpenSecondaryScreenItem;

	boolean IsEngravePointShape = false;
	boolean IsAllElementInvisible = false;
	boolean IsAllPointInvisible = false;
	boolean IsAllLineInvisible = false;
	boolean IsAllPolygonInvisible = false;

	void ForbidenOperationSwitch() {
		this.Screen.setVisible(!this.Screen.isVisible());
		this.getContentPane().setVisible(this.Screen.isVisible());
		
	}
	void OperationSwitch(boolean status){
		this.getContentPane().setVisible(status);
		this.Screen.setVisible(status);
	}
	public boolean IsForbidenOperation(){
		return (!this.getContentPane().isVisible());
	}
	
	public void init(){
//Basic Elements --------------------------------
		menubar=new JMenuBar();
		Screen=new ScreenCanvas();
		Tool=new JPanel();
		FileDialog=new JFileChooser();
		menubar.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getClickCount()>=3){
					int mods = e.getModifiers();
					if((mods & InputEvent.BUTTON1_MASK)==0) 
						Screen.setVisible(false);
					else {
						Screen.ShowBackGround=!Screen.ShowBackGround;
						Screen.repaint();
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
//All the Database and the Dataset--------------------
		GPSPoints=new GPSSet();
		PolygonDatabase=new Database.PolygonDataSet();
		LineDatabase=new Database.LineDataSet();
		PointDatabase=new Database.PointDataSet();
		CacheRoadNetworkDatabase=new CacheRoadNetworkDatabaseClass();
		CacheRoadNetworkDatabase.setHandle(Handle);
		TaxiTrajectoryDatabase=new Database.TaxiTrajectoryDatabaseClass();
//MenuItemInit---------------------------------------------------------------
		OpenItem=new JMenuItem(LanguageDic.GetWords("打开地图                       "));
		OpenItem.addActionListener(this);
		
		ExitItem=new JMenuItem(LanguageDic.GetWords("退出                   "));
		ExitItem.addActionListener(this);
		
		SaveItem=new JMenuItem(LanguageDic.GetWords("保存                 "));
		SaveItem.addActionListener(this);

		BasicInfoItem=new JMenuItem(LanguageDic.GetWords("基本信息列表                  "));
		BasicInfoItem.addActionListener(this);
		
		ChangeMapBackground=new JMenuItem(LanguageDic.GetWords("更改地图背景"));
		ChangeMapBackground.addActionListener(this);
		
		setDefaultMapBackground=new JMenuItem(LanguageDic.GetWords("还原默认地图背景"));
		setDefaultMapBackground.addActionListener(this);
		
		TwoPointItem=new JMenuItem(LanguageDic.GetWords("两点矩形区域设定                  "));
		TwoPointItem.addActionListener(this);
		
		ClearAllStaticPoint=new JMenuItem(LanguageDic.GetWords("清除地图上所有鼠标点"));
		ClearAllStaticPoint.addActionListener(this);
		
		ClearLastPoint=new JMenuItem(LanguageDic.GetWords("清除上一个鼠标点     "));
		ClearLastPoint.addActionListener(this);
		
		ClearDirection=new JMenuItem(LanguageDic.GetWords("清除地图上的箭头      "));
		ClearDirection.addActionListener(this);
		
		CalibrateItem=new JMenuItem(LanguageDic.GetWords("地图GPS偏差校准     "));
		CalibrateItem.addActionListener(this);
		
		ShowClockItem=new JMenuItem(LanguageDic.GetWords("显示并调整时钟                "));
		ShowClockItem.addActionListener(this);
		
		ShowTaxiSearchItem=new JMenuItem(LanguageDic.GetWords("显示出租车信息"));
		ShowTaxiSearchItem.addActionListener(this);
		
		RouteSearchItem=new JMenuItem(LanguageDic.GetWords("路径规划                    "));
		RouteSearchItem.addActionListener(this);
		
		WashScreenItem=new JMenuItem(LanguageDic.GetWords("清洗屏幕"));
		WashScreenItem.addActionListener(this);		
		
		JMenuItem WizardForbidenOperationSwitch=new JMenuItem(LanguageDic.GetWords("可视化开关"));
		WizardForbidenOperationSwitch.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					if(!Screen.isVisible())
						if(SecondaryScreen.SwtHtmlBrowser.SingleItemThread!=null)
							SecondaryScreen.SwtHtmlBrowser.Running=false;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				if(SecondaryScreen.SwtHtmlBrowser.SingleItemThread==null) ForbidenOperationSwitch();
				else JOptionPane.showMessageDialog(null, "Secondary Screen Closed");
			}
		});
		
		ShowCenterItem=new JMenuItem(LanguageDic.GetWords("显示中心区域"));
		ShowCenterItem.addActionListener(this);
		
		VeilCenterItem=new JMenuItem(LanguageDic.GetWords("隐去中心区域"));
		VeilCenterItem.addActionListener(this);
		
		LandMarkEditItem=new JMenuItem(LanguageDic.GetWords("地标编辑"));
		LandMarkEditItem.addActionListener(this);
		
		AboutFrameItem=new JMenuItem(LanguageDic.GetWords("关于软件"));
		AboutFrameItem.addActionListener(this);
		
		LandMarkOnScreenItem=new JMenuItem(LanguageDic.GetWords("在地图上显示地标点"));
		LandMarkOnScreenItem.addActionListener(this);
		
		LandMarkVeilItem=new JMenuItem(LanguageDic.GetWords("在地图上隐去地标点"));
		LandMarkVeilItem.addActionListener(this);
		
		LandMarkNameOnScreenItem=new JMenuItem(LanguageDic.GetWords("在地图上显示地标名称"));
		LandMarkNameOnScreenItem.addActionListener(this);
		
		LandMarkNameVeilItem=new JMenuItem(LanguageDic.GetWords("在地图上隐去地标名称"));
		LandMarkNameVeilItem.addActionListener(this);
		
		LandMarkQueryItem=new JMenuItem(LanguageDic.GetWords("地标检索服务"));
		LandMarkQueryItem.addActionListener(this);
		
		MyTimerOn=new JMenuItem(LanguageDic.GetWords("开启时钟脉冲动态效果"));
		MyTimerOn.addActionListener(this);
		
		MyTimerOff=new JMenuItem(LanguageDic.GetWords("关闭时钟脉冲动态效果"));
		MyTimerOff.addActionListener(this);
		
		ClearMemory=new JMenuItem(LanguageDic.GetWords("强制内存清理"));
		ClearMemory.addActionListener(this);
		
		MapElementsEditorPaneItem=new JMenuItem(LanguageDic.GetWords("MapElementsEditorPane"));
		MapElementsEditorPaneItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				NowPanel=MapElementsEditorPane;
				ClearStateAfterSwitchPane();
				ToolCard.show(Tool,"MapElementsEditorPane");
				MapElementsEditorPane.emerge();
			}
		});
		
		ShowPolygonAddPane=new JMenuItem(LanguageDic.GetWords("区域新建面板"));
		ShowPolygonAddPane.addActionListener(this);
		
		ShowLineAddPane=new JMenuItem(LanguageDic.GetWords("线路新建面板"));
		ShowLineAddPane.addActionListener(this);
		
		ShowPointAddPane=new JMenuItem(LanguageDic.GetWords("兴趣点批量插入面板"));
		ShowPointAddPane.addActionListener(this);
		
		ShowPolygonDatabaseView=new JMenuItem(LanguageDic.GetWords("显示多边形区域数据库视窗"));
		ShowPolygonDatabaseView.addActionListener(this);
		
		ShowLineDatabaseView=new JMenuItem(LanguageDic.GetWords("显示线路数据库视窗"));
		ShowLineDatabaseView.addActionListener(this);
		
		CreateJPGImage=new JMenuItem(LanguageDic.GetWords("利用数据库创建JPG文件"));
		CreateJPGImage.addActionListener(this);
		
		ShowPointDatabaseView=new JMenuItem(LanguageDic.GetWords("显示兴趣点数据库视窗"));
		ShowPointDatabaseView.addActionListener(this);
		
		ShowAutoCrossLinkPane=new JMenuItem(LanguageDic.GetWords("ShowAutoCrossLinkPane"));
		ShowAutoCrossLinkPane.addActionListener(this);
		
		ShowConnectTestPane=new JMenuItem(LanguageDic.GetWords("ShowConnectTestPane"));
		ShowConnectTestPane.addActionListener(this);
		
		ShowTaxiTrajectoryViewPane=new JMenuItem(LanguageDic.GetWords("ShowTaxiTrajectoryViewPane"));
		ShowTaxiTrajectoryViewPane.addActionListener(this);
		
		ShowAutoDrivePane=new JMenuItem(LanguageDic.GetWords("Road Network Application"));
		ShowAutoDrivePane.addActionListener(this);
		
		ShowRoadConditionWizard=new JMenuItem(LanguageDic.GetWords("ShowRoadConditionWizard"));
		ShowRoadConditionWizard.addActionListener(this);
		
		ShowPreferenceWizard=new JMenuItem(LanguageDic.GetWords("[Preference Wizard]"));
		ShowPreferenceWizard.addActionListener(this);
		
		ScreenLocationMicroDelta=new JMenuItem(LanguageDic.GetWords("ScreenLocationMicroDelta"));
		ScreenLocationMicroDelta.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFrame Changer=new ScreenLocationChanger();
				Changer.setVisible(true);
				};
			});
		ScreenLocationReset=new JMenuItem(LanguageDic.GetWords("ScreenLocationReset"));
		ScreenLocationReset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Screen.ScreenDeltaX=0;
				Screen.ScreenDeltaY=0;
			}
		});
		
		GISCompletionPaneItem=new JMenuItem(LanguageDic.GetWords("ShowGISCompletionPane"));
		GISCompletionPaneItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				NowPanel=GISCompletionPane;
				ClearStateAfterSwitchPane();
				ToolCard.show(Tool,"GISCompletionPane");
				GISCompletionPane.emerge();
			}
		});
		
		ServerSocketPaneItem=new JMenuItem(LanguageDic.GetWords("ServerSocketPane"));
		ServerSocketPaneItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				NowPanel=ServerSocketPane;
				ClearStateAfterSwitchPane();
				ToolCard.show(Tool,"ServerSocketPane");
				ServerSocketPane.emerge();
			}
		});
		
		ClientSocketPaneItem=new JMenuItem(LanguageDic.GetWords("ClientSocketPane"));
		ClientSocketPaneItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				NowPanel=ClientSocketPane;
				ClearStateAfterSwitchPane();
				ToolCard.show(Tool,"ClientSocketPane");
				ClientSocketPane.emerge();
			}
		});
		
		ClearPointDBItem=new JMenuItem(LanguageDic.GetWords("Clear the PointDB"));
		ClearPointDBItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int state=JOptionPane.showConfirmDialog(null,"Do you want to clear the PointDB?");
				if(state!=JOptionPane.YES_OPTION) return;
				for(int i=0;i<PointDatabase.PointNum;i++)
					PointDatabase.DatabaseRemove(i);
				PointDatabase.DatabaseResize();
				Screen.repaint();
				JOptionPane.showMessageDialog(null,"PointDB has been cleared!");
			}
		});
		
		ClearLineDBItem=new JMenuItem(LanguageDic.GetWords("Clear the LineDB"));
		ClearLineDBItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int state=JOptionPane.showConfirmDialog(null,"Do you want to clear the LineDB?");
				if(state!=JOptionPane.YES_OPTION) return;
				for(int i=0;i<LineDatabase.LineNum;i++)
					LineDatabase.DatabaseRemove(i);
				LineDatabase.DatabaseResize();
				Screen.repaint();
				JOptionPane.showMessageDialog(null,"LineDB has been cleared!");				
			}
		});
		
		ClearPolygonDBItem=new JMenuItem(LanguageDic.GetWords("Clear the PolygonDB"));
		ClearPolygonDBItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				int state=JOptionPane.showConfirmDialog(null,"Do you want to clear the PolygonDB");
				if(state!=JOptionPane.YES_OPTION) return;
				for(int i=0;i<PolygonDatabase.PolygonNum;i++)
					PolygonDatabase.DatabaseRemove(i);
				PolygonDatabase.DatabaseResize();
				Screen.repaint();
				JOptionPane.showMessageDialog(null,"PolygonDB has been cleared");
			}
		});
		
		AllElementInvisible=new JMenuItem(LanguageDic.GetWords("All Element Invisible"));
		AllElementInvisible.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsAllElementInvisible=!IsAllElementInvisible;
				Screen.repaint();
			}
		});
		
		AllPointInvisible=new JMenuItem(LanguageDic.GetWords("All Point Invisible"));
		AllPointInvisible.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsAllPointInvisible=!IsAllPointInvisible;
				Screen.repaint();
			}
		});
		
		AllLineInvisible=new JMenuItem(LanguageDic.GetWords("All Line Invisible"));
		AllLineInvisible.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsAllLineInvisible=!IsAllLineInvisible;
				Screen.repaint();
			}
		});
		
		AllPolygonInvisible=new JMenuItem(LanguageDic.GetWords("All Polygon Invisible"));
		AllPolygonInvisible.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsAllPolygonInvisible=!IsAllPolygonInvisible;
				Screen.repaint();
			}
		});
		
		EngravePointShape=new JMenuItem(LanguageDic.GetWords("Engrave Point Shape"));
		EngravePointShape.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsEngravePointShape=!IsEngravePointShape;
				Screen.repaint();
			}
		});
		
		AlignPointsTagItem=new JMenuItem(LanguageDic.GetWords("AlignPointsTagItem"));
		AlignPointsTagItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try{
					String str=JOptionPane.showInputDialog(null,"Input the Point Font Size,0 for unset");
					IsAlignPointsTag=Integer.parseInt(str);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Error Setting!");
					return;
				}
				Screen.repaint();
			}
		});
		
		AlignLinesTagItem=new JMenuItem(LanguageDic.GetWords("AlignLinesTagItem"));
		AlignLinesTagItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				IsAlignLinesTag=!IsAlignLinesTag;
				Screen.repaint();
			}
		});
		
		AlignPolygonsTagItem=new JMenuItem(LanguageDic.GetWords("AlignPolygonsTagItem"));
		AlignPolygonsTagItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				IsAlignPolygonsTag=!IsAlignPolygonsTag;
				Screen.repaint();
			}
		});
		
		CaptureScreenItem=new JMenuItem(LanguageDic.GetWords("捕捉当前窗口到PNG文件"));
		CaptureScreenItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				try{
					ScreenPNGOutput();
					}catch(Exception ex){
						System.gc();
						JOptionPane.showMessageDialog(null,LanguageDic.GetWords("输入的信息有误，请重试"),LanguageDic.GetWords("JPG生成失败"),JOptionPane.WARNING_MESSAGE);
					}
			}
		});
		
		ExtractLineDBItem=new JMenuItem(LanguageDic.GetWords("导出折线数据库"));
		ExtractLineDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("导出折线数据库"));
				Handle.LineOutput();
			}
		});
		
		ExtractPointDBItem=new JMenuItem(LanguageDic.GetWords("导出兴趣点数据库"));
		ExtractPointDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("导出兴趣点数据库"));
				Handle.PointOutput();
			}
		});
		
		ExtractPolygonDBItem=new JMenuItem(LanguageDic.GetWords("导出多边形数据库"));
		ExtractPolygonDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("导出多边形数据库"));
				Handle.PolygonOutput();
			}
		});
		
		AppendLineDBItem=new JMenuItem(LanguageDic.GetWords("追加折线数据库"));
		AppendLineDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("追加折线数据库"));
				Handle.LineAppend();
				Handle.ScreenFlush();
			}
		});
		
		AppendPointDBItem=new JMenuItem(LanguageDic.GetWords("追加兴趣点数据库"));
		AppendPointDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("追加兴趣点数据库"));
				Handle.PointAppend();
				Handle.ScreenFlush();
			}
		});
		
		AppendPolygonDBItem=new JMenuItem(LanguageDic.GetWords("追加多边形数据库"));
		AppendPolygonDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("追加多边形数据库"));
				Handle.PolygonAppend();
				Handle.ScreenFlush();
			}
		});
		
		AppendAllLineDBItem=new JMenuItem(LanguageDic.GetWords("载入追加折线数据库文件夹"));
		AppendAllLineDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("载入追加折线数据库文件夹"));
				Handle.LineFolderAppend();
				Handle.ScreenFlush();
			}
		});
		
		AppendAllPointDBItem=new JMenuItem(LanguageDic.GetWords("载入追加兴趣点数据库文件夹"));
		AppendAllPointDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("载入追加兴趣点数据库文件夹"));
				Handle.PointFolderAppend();
				Handle.ScreenFlush();
			}
		});
		
		AppendAllPolygonDBItem=new JMenuItem(LanguageDic.GetWords("载入追加多边形数据库文件夹"));
		AppendAllPolygonDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("载入追加多边形数据库文件夹"));
				Handle.PolygonFolderAppend();
				Handle.ScreenFlush();
			}
		});
		CoverLineDBItem=new JMenuItem(LanguageDic.GetWords("覆盖折线数据库"));
		CoverLineDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("覆盖折线数据库"));
				Handle.LineInput();
				Handle.ScreenFlush();
			}
		});
		
		CoverPointDBItem=new JMenuItem(LanguageDic.GetWords("覆盖兴趣点数据库"));
		CoverPointDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("覆盖兴趣点数据库"));
				Handle.PointInput();
				Handle.ScreenFlush();
			}
		});
		
		CoverPolygonDBItem=new JMenuItem(LanguageDic.GetWords("覆盖多边形数据库"));
		CoverPolygonDBItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Handle.ChangeTitle(LanguageDic.GetWords("覆盖多边形数据库"));
				Handle.PolygonInput();
				Handle.ScreenFlush();
			}
		});
		
		VisualObjectMaxNumSetItem=new JMenuItem(LanguageDic.GetWords("设置图形显示数量上限"));
		VisualObjectMaxNumSetItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String str=JOptionPane.showInputDialog(null,LanguageDic.GetWords("请输出上限，空则放弃"),LanguageDic.GetWords("设置图形显示数量上限"),JOptionPane.PLAIN_MESSAGE);
				if((str==null)||(str.equals(""))){
					ReTitle(LanguageDic.GetWords("放弃了图形显示数量上限重设"));
					return;
				}
				try{
					VisualObjectMaxNum=Integer.parseInt(str);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,LanguageDic.GetWords("输入有误"));
				}
				Handle.ScreenFlush();
			}
		});
		
		PolygonsToGridsItem=new JMenuItem(LanguageDic.GetWords("多边形网格化导出"));
		PolygonsToGridsItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String Row_str=JOptionPane.showInputDialog(null,"Row Number");
				String Column_str=JOptionPane.showInputDialog(null,"Colume Number");
				try{
					int Row_num=Integer.parseInt(Row_str);
					int Column_num=Integer.parseInt(Column_str);
				Database.PolygonDataSet PolyDB=Handle.getPolygonDatabase();				
				//FreeFileOutput---------------------------
				String OutputFilePath=JOptionPane.showInputDialog(null,"Output File Path");
				File fout=new File(OutputFilePath);
				BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout),"UTF-8"));
				out.write("Row "+Row_num+" "+LatitudeStart+" "+LatitudeEnd+"\n");
				out.write("Column "+Column_num+" "+LongitudeStart+" "+LongitudeEnd+"\n");
				//-----------------------------------------
				if(PolyDB.GetIndexPermission()!=null)
					PolyDB.GetIndexPermission().WriteBack();
				Database.RTreeIndex PolygonRTree=new Database.RTreeIndex();
				PolygonRTree.IndexInit(PolyDB);
				int PolygonNum=PolygonDatabase.PolygonNum;
				double Xstep=(LongitudeEnd-LongitudeStart)/Column_num;
				double Ystep=(LatitudeEnd-LatitudeStart)/Row_num;
				for(int i=0;i<PolygonNum;i++){
					System.out.println("Polygon "+(i+1)+" / "+PolygonNum+" ["+PolyDB.getTitle(i)+"] Processing");
					double Xmin=PolygonDatabase.GetMBRX1(i);
					double Xmax=PolygonDatabase.GetMBRX2(i);
					double Ymin=PolygonDatabase.GetMBRY1(i);
					double Ymax=PolygonDatabase.GetMBRY2(i);
					int Column_start=(int)((Xmin-LongitudeStart)/Xstep);
					int Column_end=(int)((Xmax-LongitudeStart)/Xstep);
					int Row_start=(int)((Ymin-LatitudeStart)/Ystep);
					int Row_end=(int)((Ymax-LatitudeStart)/Ystep);
					for(int row_i=Row_start;row_i<=Row_end;row_i++){
						for(int col_i=Column_start;col_i<=Column_end;col_i++){
							double center_X=col_i*Xstep+Xstep/2+LongitudeStart;
							double center_Y=row_i*Ystep+Ystep/2+LatitudeStart;
							if(PolyDB.CheckInsidePolygon(i,center_X,center_Y)){
								out.write(row_i+" "+col_i+" "+PolyDB.getTitle(i)+"\n");
							}
						}
					}
				}
				//FreeFileOutput---------------------------
				out.flush();
				out.close();
				//-----------------------------------------
				PolyDB.SetIndexPermission(null);
				PolygonRTree=null;
				System.gc();
				JOptionPane.showMessageDialog(null,"Finished!");
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,LanguageDic.GetWords("设置有误"));
				}
			}
		});
		
		ShowPointsAlphaDistribution=new JMenuItem(LanguageDic.GetWords("显示/关闭点分布的浓度"));
		ShowPointsAlphaDistribution.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(IsShowAlphaDistribution){
					IsShowAlphaDistribution=false;
					Handle.VeilTextArea1();
					Handle.VeilTextArea2();
					Screen.LastLatitudeScale=-1;
					Screen.LastLongitudeScale=-1;
					Screen.LastScreenLatitude=-1000;
					Screen.LastScreenLongitude=-1000;
					Screen.LastAlphaPercentScale=0;
					Screen.LastRadiationDistance=0;
				}else{
					String str_row=JOptionPane.showInputDialog(null,"AlphaDistributionRow");
					String str_col=JOptionPane.showInputDialog(null,"AlphaDistributionColumn");
					try{
						AlphaGridsRow=Integer.parseInt(str_row);
						AlphaGridsColumn=Integer.parseInt(str_col);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Setting Error!");
						return;
					}
					IsShowAlphaDistribution=true;
					Handle.ScreenFlush();
				}
			}
		});
		
		SetAlphaPercentScale=new JMenuItem(LanguageDic.GetWords("手工设定浓度图"));
		SetAlphaPercentScale.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
					String str=JOptionPane.showInputDialog(null,"AlphaPercentScale");
					try{
						AlphaPercentScale=Integer.parseInt(str);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Setting Error!");
						return;
					}
					str=JOptionPane.showInputDialog(null,"RadiationDistance");
					try{
						RadiationDistance=Integer.parseInt(str);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Setting Error!");
						return;
					}
					if(AlphaPercentScale<=0) ReTitle("Now in Auto Alpha Model");
					else ReTitle("Now in Manual Alpha Model");
					Handle.ScreenFlush();
			}
		});
		
		HtmlMapOutputPaneItem=new JMenuItem(LanguageDic.GetWords("导出数据库于网页地图上"));
		HtmlMapOutputPaneItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				NowPanel=HtmlMapOutputPane;
				ClearStateAfterSwitchPane();
				ToolCard.show(Tool,"HtmlMapOutputPane");
				HtmlMapOutputPane.emerge();
			}
		});
		
		PointDBDeviationItem=new JMenuItem(LanguageDic.GetWords("平移PointDB数据"));
		PointDBDeviationItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String str=JOptionPane.showInputDialog("[Longitude,Latitude]平移PointDB数据");
				if((str==null)||(str.equals(""))){
					JOptionPane.showMessageDialog(null,"Setting Error");
					return;
				}else{
					try{
						String[] str_list=str.split(",");
						double longitude_delta=java.lang.Double.parseDouble(str_list[0]);
						double latitude_delta=java.lang.Double.parseDouble(str_list[1]);
						PointDatabase.MoveEntireData(longitude_delta, latitude_delta);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Setting Error");
					}
				}
			}
		});
		
		LineDBDeviationItem=new JMenuItem(LanguageDic.GetWords("平移LineDB数据"));
		LineDBDeviationItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String str=JOptionPane.showInputDialog("[Longitude,Latitude]平移LineDB数据");
				if((str==null)||(str.equals(""))){
					JOptionPane.showMessageDialog(null,"Setting Error");
					return;
				}else{
					try{
						String[] str_list=str.split(",");
						double longitude_delta=java.lang.Double.parseDouble(str_list[0]);
						double latitude_delta=java.lang.Double.parseDouble(str_list[1]);
						LineDatabase.MoveEntireData(longitude_delta, latitude_delta);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Setting Error");
					}
				}
			}
		});
		
		PolygonDBDeviationItem=new JMenuItem(LanguageDic.GetWords("平移PolygonDB数据"));
		PolygonDBDeviationItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String str=JOptionPane.showInputDialog("[Longitude,Latitude]平移PolygonDB数据");
				if((str==null)||(str.equals(""))){
					JOptionPane.showMessageDialog(null,"Setting Error");
					return;
				}else{
					try{
						String[] str_list=str.split(",");
						double longitude_delta=java.lang.Double.parseDouble(str_list[0]);
						double latitude_delta=java.lang.Double.parseDouble(str_list[1]);
						PolygonDatabase.MoveEntireData(longitude_delta, latitude_delta);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Setting Error");
					}
				}
			}
		});
		
		BackGroundMoveItem=new JMenuItem(LanguageDic.GetWords("背景图片平移矢量载入"));
		BackGroundMoveItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(LineDatabase==null){
					JOptionPane.showMessageDialog(null,"NO_LINEDATABASE_EXIST");
					return;
				}
				ReTitle("Open BackGroundMoveVectorFile in Root");
				LineDatabase.DatabaseFileInput(new File(DIR,BackGroundMoveVectorFilePath));
				int ptr=-1;
				BackGroundMoveVectorNum=0;
				for(int i=0;i<LineDatabase.LineNum;i++){
					if(LineDatabase.LineHint[i].indexOf("[Info:MapCheckPoint]")!=-1){
						ptr=LineDatabase.LineHead[i];
						BackGroundMoveX[BackGroundMoveVectorNum]=LineDatabase.AllPointX[ptr];
						BackGroundMoveY[BackGroundMoveVectorNum]=LineDatabase.AllPointY[ptr];
						BackGroundMoveDx[BackGroundMoveVectorNum]=
								LineDatabase.AllPointX[LineDatabase.AllPointNext[ptr]]-LineDatabase.AllPointX[ptr];
						BackGroundMoveDy[BackGroundMoveVectorNum]=
								LineDatabase.AllPointY[LineDatabase.AllPointNext[ptr]]-LineDatabase.AllPointY[ptr];
						BackGroundMoveVectorNum++;
					}
				}
				LineDatabase.AttributeDelete("MapCheckPoint", null, null, null, null);
				Handle.ScreenFlush();
			}
		});
		
		BackGroundMoveResetItem=new JMenuItem(LanguageDic.GetWords("背景图片矢量位移还原"));
		BackGroundMoveResetItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				LineDatabase.AttributeDelete("MapCheckPoint", null, null, null, null);
				BackGroundMoveVectorNum=0;
				Handle.ScreenFlush();
			}
		});
		
		VisualFeatureSwitchItem=new JMenuItem(LanguageDic.GetWords("元素可视化纹理开关"));
		VisualFeatureSwitchItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ShowVisualFeature=!ShowVisualFeature;
				Handle.ScreenFlush();
			}
		});
		
		SplitJPGItem=new JMenuItem(LanguageDic.GetWords("DeepZoom高清分割"));
		SplitJPGItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JPGSplitOutput(null,-1,true);
			}
		});
		
		OpenSecondaryScreenItem=new JMenuItem(LanguageDic.GetWords("打开网页第二屏幕"));
		OpenSecondaryScreenItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(SecondaryScreen.SwtHtmlBrowser.SingleItemThread==null)
				{
					if(Screen.isVisible()) Screen.setVisible(false);
				}else return;
				// TODO Auto-generated method stub
				String str_width=JOptionPane.showInputDialog(null,"Secondary Screen Width");
				String str_height=JOptionPane.showInputDialog(null,"Secondary Screnn Height");
				try{
					SecondaryScreen.SwtHtmlBrowser.InitiateBrowser(Integer.parseInt(str_width),Integer.parseInt(str_height));
				}catch(Exception ex){
					ex.printStackTrace();
					return;
				}
			}
		});
//MenuAdd---------------------------------------------------------
		FileMenu=new JMenu(LanguageDic.GetWords("文件      "));
		EditMenu=new JMenu(LanguageDic.GetWords("编辑      "));
		MapControlMenu=new JMenu(LanguageDic.GetWords("控制      "));
		MapDataMenu=new JMenu(LanguageDic.GetWords("地图数据      "));
		ExtendedAbility=new JMenu(LanguageDic.GetWords("功能扩展      "));
		HelpMenu=new JMenu(LanguageDic.GetWords("帮助"));
		//---------------------------------
		FileMenu.add(OpenItem);
		FileMenu.add(SaveItem);
		FileMenu.add(ChangeMapBackground);
		FileMenu.add(setDefaultMapBackground);
		FileMenu.add(ExtractLineDBItem);
		FileMenu.add(ExtractPointDBItem);
		FileMenu.add(ExtractPolygonDBItem);
		FileMenu.add(CoverLineDBItem);
		FileMenu.add(CoverPointDBItem);
		FileMenu.add(CoverPolygonDBItem);
		FileMenu.add(AppendLineDBItem);
		FileMenu.add(AppendPointDBItem);
		FileMenu.add(AppendPolygonDBItem);
		FileMenu.add(AppendAllLineDBItem);
		FileMenu.add(AppendAllPointDBItem);
		FileMenu.add(AppendAllPolygonDBItem);
		FileMenu.add(CreateJPGImage);
		FileMenu.add(SplitJPGItem);
		FileMenu.add(CaptureScreenItem);
		FileMenu.add(ExitItem);
		//----------------------------------
		EditMenu.add(MapElementsEditorPaneItem);
		EditMenu.add(ShowPolygonAddPane);
		EditMenu.add(ShowLineAddPane);
		EditMenu.add(ShowPointAddPane);
		EditMenu.add(LandMarkEditItem);
		EditMenu.add(BackGroundMoveItem);
		EditMenu.add(BackGroundMoveResetItem);
		EditMenu.add(PointDBDeviationItem);
		EditMenu.add(LineDBDeviationItem);
		EditMenu.add(PolygonDBDeviationItem);
		EditMenu.add(ServerSocketPaneItem);
		EditMenu.add(ClientSocketPaneItem);
		EditMenu.add(HtmlMapOutputPaneItem);
		EditMenu.add(OpenSecondaryScreenItem);
		//----------------------------------
		MapControlMenu.add(TwoPointItem);
		MapControlMenu.add(CalibrateItem);
		MapControlMenu.add(ClearAllStaticPoint);
		MapControlMenu.add(ClearLastPoint);
		MapControlMenu.add(ClearDirection);
		MapControlMenu.add(WashScreenItem);
		MapControlMenu.add(WizardForbidenOperationSwitch);
		MapControlMenu.add(ShowCenterItem);
		MapControlMenu.add(VeilCenterItem);
		MapControlMenu.add(LandMarkOnScreenItem);
		MapControlMenu.add(LandMarkVeilItem);
		MapControlMenu.add(LandMarkNameOnScreenItem);
		MapControlMenu.add(LandMarkNameVeilItem);
		MapControlMenu.add(ScreenLocationMicroDelta);
		MapControlMenu.add(ScreenLocationReset);
		MapControlMenu.add(AllElementInvisible);
		MapControlMenu.add(VisualFeatureSwitchItem);
		MapControlMenu.add(VisualObjectMaxNumSetItem);
		//--------------------------------
		MapDataMenu.add(BasicInfoItem);
		MapDataMenu.add(LandMarkQueryItem);
		MapDataMenu.add(ShowPolygonDatabaseView);
		MapDataMenu.add(ShowLineDatabaseView);
		MapDataMenu.add(ShowPointDatabaseView);
		MapDataMenu.add(ShowAutoDrivePane);
		MapDataMenu.add(AllPointInvisible);
		MapDataMenu.add(AllLineInvisible);
		MapDataMenu.add(AllPolygonInvisible);
		MapDataMenu.add(ClearPointDBItem);
		MapDataMenu.add(ClearLineDBItem);
		MapDataMenu.add(ClearPolygonDBItem);
		MapDataMenu.add(EngravePointShape);
		MapDataMenu.add(AlignPointsTagItem);
		MapDataMenu.add(AlignLinesTagItem);
		MapDataMenu.add(AlignPolygonsTagItem);
		MapDataMenu.add(ShowPointsAlphaDistribution);
		MapDataMenu.add(SetAlphaPercentScale);
		//------------------------------
		HelpMenu.add(ShowClockItem);
		HelpMenu.add(ClearMemory);
		HelpMenu.add(AboutFrameItem);
		//---------------------------------
		ExtendedAbility.add(GISCompletionPaneItem);
		ExtendedAbility.add(ShowTaxiSearchItem);
		ExtendedAbility.add(RouteSearchItem);
		ExtendedAbility.add(MyTimerOn);
		ExtendedAbility.add(MyTimerOff);
		ExtendedAbility.add(ShowAutoCrossLinkPane);
		ExtendedAbility.add(ShowConnectTestPane);
		ExtendedAbility.add(ShowTaxiTrajectoryViewPane);
		ExtendedAbility.add(ShowRoadConditionWizard);
		ExtendedAbility.add(PolygonsToGridsItem);
		ExtendedAbility.add(ShowPreferenceWizard);
//MenuBarAdd------------------------------------------------------
		menubar.add(FileMenu);
		menubar.add(EditMenu);
		menubar.add(MapControlMenu);
		menubar.add(MapDataMenu);
		menubar.add(ExtendedAbility);
		menubar.add(HelpMenu);
		setJMenuBar(menubar);
//LayOut------------------------------------------------------------
		setLayout(null);
		add(Screen);
		add(Tool);
		Screen.setBounds(0,0,Screen.ScreenWidth,Screen.ScreenHeight);
		Screen.setBackground(Color.black);
		Tool.setBounds(722,0,280,680);
		Tool.setBackground(Color.blue);
		ToolCard=new CardLayout();
		Tool.setLayout(ToolCard);

//PaneDefine:---------------------------------------------------
		NULL=new NULLPaneClass();
		Tool.add("NULL",NULL);
		
		BasicInfoPane=new BasicInfoPaneClass();
		Tool.add("BasicInfoPane",BasicInfoPane);
		
		TwoPointPane=new TwoPointPaneClass();
		Tool.add("TwoPointPane",TwoPointPane);

		CalibratePane=new CalibratePaneClass();
		Tool.add("CalibratePane",CalibratePane);
		
		TaxiSearchPane=new TaxiSearchPaneClass();
		Tool.add("TaxiSearchPane",TaxiSearchPane);
		
		RouteSearchPane=new RouteSearchPaneClass();
		Tool.add("RouteSearchPane",RouteSearchPane);
		
		LandMarkEditPane=new LandMarkEditPaneClass();
		Tool.add("LandMarkEditPane",LandMarkEditPane);
		
		MapElementsEditorPane=new MapElementsEditorPaneClass();
		MapElementsEditorPane.setHandle(Handle);
		Tool.add("MapElementsEditorPane",MapElementsEditorPane);
		
		PolygonAddPane=new PolygonAddPaneClass();
		PolygonAddPane.setHandle(Handle);
		Tool.add("PolygonAddPane",PolygonAddPane);		
		
		LineAddPane=new LineAddPaneClass();
		LineAddPane.setHandle(Handle);
		Tool.add("LineAddPane",LineAddPane);
		
		PointAddPane=new PointAddPaneClass();
		PointAddPane.setHandle(Handle);
		Tool.add("PointAddPane",PointAddPane);
		
		AutoCrossLinkPane=new AutoCrossLinkPaneClass();
		AutoCrossLinkPane.setHandle(Handle);
		Tool.add("AutoCrossLinkPane",AutoCrossLinkPane);
		
		TaxiTrajectoryViewPane=new TaxiTrajectoryViewPaneClass();
		TaxiTrajectoryViewPane.setHandle(Handle);
		Tool.add("TaxiTrajectoryViewPane",TaxiTrajectoryViewPane);
		
		ConnectTestPane=new ConnectTestPaneClass();
		ConnectTestPane.setHandle(Handle);
		Tool.add("ConnectTestPane",ConnectTestPane);
		
		AutoDrivePane=new AutoDrivePaneClass();
		AutoDrivePane.setHandle(Handle);
		Tool.add("AutoDrivePane",AutoDrivePane);
		
		GISCompletionPane=new GISCompletionPaneClass();
		GISCompletionPane.setHandle(Handle);
		Tool.add("GISCompletionPane",GISCompletionPane);
		
		ServerSocketPane=new ServerSocketPaneClass();
		ServerSocketPane.setHandle(Handle);
		Tool.add("ServerSocketPane",ServerSocketPane);
		
		ClientSocketPane=new ClientSocketPaneClass();
		ClientSocketPane.setHandle(Handle);
		Tool.add("ClientSocketPane",ClientSocketPane);
		
		HtmlMapOutputPane=new HtmlMapOutputPaneClass();
		HtmlMapOutputPane.setHandle(Handle);
		Tool.add("HtmlMapOutputPane",HtmlMapOutputPane);
	//final----------------------------
		NowPanel=NULL;
		ToolCard.show(Tool,"NULL");
		
//The Rest Work:
	}

	void MapInput() {// 读取init文件，根据里面的内容打开各个相关文件
		try {
			File f = new File(DIR, "init.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF-8"));
			String s, s1, s2;
			while ((s = in.readLine()) != null) {
				System.out.println(s);
				if ((s.length() > 0) && (s.charAt(0) == '#')) {
					System.out.println("Omit This Line");
					continue;
				}
				if (s.indexOf('[') == -1)
					continue;
				s1 = s.substring(s.indexOf('[') + 1, s.indexOf(']'));
				s2 = s.substring(s.indexOf(']') + 1);
				if (s1.equals("ImageDir")) {
					ImageDir = new File(DIR, s2);
				} else if (s1.equals("TrafficFile")) {
					TrafficFile = new File(DIR, s2);
				} else if (s1.equals("LongitudeStart")) {
					LongitudeStart = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("LongitudeEnd")) {
					LongitudeEnd = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("LatitudeStart")) {
					LatitudeStart = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("LatitudeEnd")) {
					LatitudeEnd = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("HighestRate")) {
					HighestRate = Integer.parseInt(s2);
				} else if (s1.equals("TaxiDir")) {
					TaxiDir = new File(DIR, s2);
				} else if (s1.equals("RoadDeltaLongitude")) {
					RoadDeltaLongitude = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("RoadDeltaLatitude")) {
					RoadDeltaLatitude = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("GPSDeltaLongitude")) {
					GPSDeltaLongitude = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("GPSDeltaLatitude")) {
					GPSDeltaLatitude = java.lang.Double.parseDouble(s2);
				} else if (s1.equals("LandMarkFile")) {
					LandMarkFile = new File(DIR, s2);
				} else if (s1.equals("MaxTaxiNum")) {
					GPSPoints.MaxTaxiNum = Integer.parseInt(s2);
				} else if (s1.equals("MaxTaxiInfoLength")) {
					GPSPoints.MaxTaxiInfoLength = Integer.parseInt(s2);
				} else if (s1.equals("PolygonDatabaseFile")) {
					PolygonDatabaseFile = new File(DIR, s2);
				} else if (s1.equals("LineDatabaseFile")) {
					LineDatabaseFile = new File(DIR, s2);
				} else if (s1.equals("PointDatabaseFile")) {
					PointDatabaseFile = new File(DIR, s2);
				} else if (s1.equals("TaxiTrajectoryDatabaseFile")) {
					TaxiTrajectoryDatabaseFile = new File(DIR, s2);
				} else if (s1.equals("BackGroundMoveVectorFilePath")) {
					BackGroundMoveVectorFilePath = s2;
				}
			}
			GPSPoints.LongitudeStart = LongitudeStart;
			GPSPoints.LatitudeStart = LatitudeStart;
			GPSPoints.LongitudeEnd = LongitudeEnd;
			GPSPoints.LatitudeEnd = LatitudeEnd;
			Screen.ScreenLongitude = LongitudeStart;
			Screen.ScreenLatitude = LatitudeEnd;
			Screen.LongitudeScale = (LongitudeEnd - LongitudeStart);
			Screen.LatitudeScale = (LatitudeEnd - LatitudeStart);

			GPSPoints.CleanUp();
			if (TrafficFile != null)
				GPSPoints.Input(TrafficFile, RoadDeltaLongitude,
						RoadDeltaLatitude);// 路网数据流
			if (TaxiDir != null)
				GPSPoints.TaxiStream(TaxiDir, GPSDeltaLongitude,
						GPSDeltaLatitude);// 出租车数据流
			if (LandMarkFile != null)
				GPSPoints.LandMarkStream(LandMarkFile);// 地标数据流

			if (PolygonDatabaseFile != null) {
				PolygonDatabase.DatabaseInit();
				PolygonDatabase.DatabaseFileInput(PolygonDatabaseFile);
			}

			if (LineDatabaseFile != null) {
				LineDatabase.DatabaseInit();
				LineDatabase.DatabaseFileInput(LineDatabaseFile);
			}

			if (PointDatabaseFile != null) {
				PointDatabase.DatabaseInit();
				PointDatabase.DatabaseFileInput(PointDatabaseFile);
			}

			if (TaxiTrajectoryDatabaseFile != null) {
				TaxiTrajectoryDatabase.DatabaseInit();
				TaxiTrajectoryDatabase.deltaX = GPSDeltaLongitude;
				TaxiTrajectoryDatabase.deltaY = GPSDeltaLatitude;
				System.out.println("DeltaX:" + GPSDeltaLongitude + "\nDeltaY:"
						+ GPSDeltaLatitude);
				TaxiTrajectoryDatabase
						.DatabaseFileInput(TaxiTrajectoryDatabaseFile);
			}
			OperationSwitch(true);
			in.close();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == OpenItem) {
			int state = FileDialog.showOpenDialog(this);
			if (state == JFileChooser.APPROVE_OPTION) {
				File f = new File(FileDialog.getCurrentDirectory(), "init.txt");
				if (!f.exists()) {
					JOptionPane.showMessageDialog(null,
							"Error in Open [init.txt]");
					return;
				}
				if (DIR != null)
					ChangeDirPrompt();
				CleanUp();
				DIR = FileDialog.getCurrentDirectory();
				MapInput();
			}
		} else if (e.getSource() == SaveItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			WriteBack();
		} else if (e.getSource() == ExitItem) {
			ExecuteExit();
		} else if (e.getSource() == BasicInfoItem) {
			if (LandMarkQueryFrame.isVisible()) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标演示和地标数据库检索窗口两者不能并存"),
						LanguageDic.GetWords("数据库并发读写隐患"),
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			if (LandMarkFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标点数据不存在"),
						"DATA DOES NOT EXIST", JOptionPane.WARNING_MESSAGE);
			}
			NowPanel = BasicInfoPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "BasicInfoPane");
		} else if (e.getSource() == ChangeMapBackground) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			FileDialog.setCurrentDirectory(ImageDir);
			int state = FileDialog.showOpenDialog(this);
			if (state == JFileChooser.APPROVE_OPTION) {
				Screen.image = this.getToolkit().createImage(
						FileDialog.getSelectedFile().toString());
				Screen.repaint();
			}
		} else if (e.getSource() == setDefaultMapBackground) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!(new File(ImageDir, "Map.jpg").exists())) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("Map.jpg 数据不存在"),
						"Map.jpg DOES NOT EXIST", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.image = this.getToolkit().createImage(
					new File(ImageDir, "Map.jpg").toString());
			Screen.repaint();
		} else if (e.getSource() == TwoPointItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			NowPanel = TwoPointPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "TwoPointPane");
		} else if (e.getSource() == ClearAllStaticPoint) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.XYCount = 0;
			Screen.SelectedPointList[0] = 0;
			Screen.repaint();
		} else if (e.getSource() == ClearLastPoint) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (Screen.SelectedPointList[0] > 0) {
				if (Screen.SelectedPointList[Screen.SelectedPointList[0]] == Screen.XYCount - 1) {
					Screen.SelectedPointList[0]--;
				}
			}
			if (Screen.XYCount > 0)
				Screen.XYCount--;
			Screen.repaint();
		} else if (e.getSource() == myTimer) {
			// if(MyTimerEnable) Screen.repaint();//终止自动刷新
			ClockWizard.pic.spur();
			NowPanel.ClockImpulse();
			if (NowPanel == TaxiSearchPane) {
				if (!ClockWizard.pic.lock) {
					int k = ClockWizard.pic.getSecond();
					if (k % 5 == 0) {
						if (TaxiSearchPane.CanShowTaxi)
							TaxiSearchPane.GetGPSInfo();
					}
				}
			}
		} else if (e.getSource() == ClearDirection) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.IsShowDirection = false;
			Screen.repaint();
		} else if (e.getSource() == CalibrateItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			NowPanel = CalibratePane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "CalibratePane");
		} else if (e.getSource() == ShowClockItem) {
			ClockWizard.setVisible(true);
			ClockWizard.setLocationRelativeTo(null);
		} else if (e.getSource() == ShowTaxiSearchItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			if (TaxiDir == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("出租车数据不存在"),
						"DATA DOES NOT EXIST", JOptionPane.WARNING_MESSAGE);
			}
			TaxiSearchPane.CanShowTaxi = false;
			NowPanel = TaxiSearchPane;
			TaxiSearchPane.Trace = -1;
			ClearStateAfterSwitchPane();
			if (TaxiSearchPane.CanShowTaxi == true) {
				TaxiSearchPane.ShowSelectedTaxi.setEnabled(false);
				TaxiSearchPane.NotShowSelectedTaxi.setEnabled(true);
			} else {
				TaxiSearchPane.ShowSelectedTaxi.setEnabled(true);
				TaxiSearchPane.NotShowSelectedTaxi.setEnabled(false);
			}
			TaxiSearchPane.TheNearestDis.setEnabled(false);
			ToolCard.show(Tool, "TaxiSearchPane");
		} else if (e.getSource() == RouteSearchItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			if (TrafficFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("路网数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			RouteSearchPane.PathOnScreen = false;
			RouteSearchPane.ShowPath.setEnabled(true);
			RouteSearchPane.VeilPath.setEnabled(false);
			NowPanel = RouteSearchPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "RouteSearchPane");
		} else if (e.getSource() == WashScreenItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			ClearStateAfterSwitchPane();
			PointDatabase.DatabaseDelete("[Info:Cache]");
			LineDatabase.DatabaseDelete("[Info:Cache]");
			PolygonDatabase.DatabaseDelete("[Info:Cache]");
			Screen.setVisible(true);
			Screen.repaint();
			if (NowPanel instanceof ExtendedToolPaneInterface)
				((ExtendedToolPaneInterface) NowPanel).emerge();
		} else if (e.getSource() == ShowCenterItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.ShowCenter = true;
			Screen.repaint();
		} else if (e.getSource() == VeilCenterItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.ShowCenter = false;
			Screen.repaint();
		} else if (e.getSource() == LandMarkEditItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			if (LandMarkFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
			}
			if (LandMarkQueryFrame.isVisible()) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标工具栏和地标数据库检索窗口两者不能并存"),
						LanguageDic.GetWords("数据库并发读写隐患"),
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			NowPanel = LandMarkEditPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "LandMarkEditPane");
		} else if (e.getSource() == AboutFrameItem) {
			AboutFrame.setVisible(true);
			AboutFrame.setLocationRelativeTo(null);
		} else if (e.getSource() == LandMarkOnScreenItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (LandMarkFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.IsLandMarkOnScreen = true;
			Screen.XYCount = 0;
			Screen.repaint();
		} else if (e.getSource() == LandMarkVeilItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (LandMarkFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.IsLandMarkOnScreen = false;
			Screen.XYCount = 0;
			Screen.repaint();
		} else if (e.getSource() == LandMarkNameOnScreenItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (LandMarkFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.IsLandMarkNameOnScreen = true;
			Screen.XYCount = 0;
			Screen.repaint();
		} else if (e.getSource() == LandMarkNameVeilItem) {
			if (DIR == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地图数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (LandMarkFile == null) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标数据不存在"), "DATA DOES NOT EXIST",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Screen.XYCount = 0;
			Screen.IsLandMarkNameOnScreen = false;
			Screen.repaint();
		} else if (e.getSource() == LandMarkQueryItem) {
			if (NowPanel == LandMarkEditPane) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标工具栏和地标数据库检索窗口两者不能并存"),
						LanguageDic.GetWords("数据库并发读写隐患"),
						JOptionPane.WARNING_MESSAGE);
				int n = JOptionPane.showConfirmDialog(null,
						LanguageDic.GetWords("为了操作数据库，是否允许进入默认面板"),
						LanguageDic.GetWords("进入默认面板"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					NowPanel = NULL;
					ClearStateAfterSwitchPane();
					ToolCard.show(Tool, "NULL");
				} else
					return;
			}
			if (NowPanel == BasicInfoPane) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("地标演示和地标数据库检索窗口两者不能并存"),
						LanguageDic.GetWords("数据库并发读写隐患"),
						JOptionPane.WARNING_MESSAGE);
				int n = JOptionPane.showConfirmDialog(null,
						LanguageDic.GetWords("为了操作数据库，是否允许进入默认面板"),
						LanguageDic.GetWords("进入默认面板"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					NowPanel = NULL;
					ClearStateAfterSwitchPane();
					ToolCard.show(Tool, "NULL");
				} else
					return;
			}
			LandMarkQueryFrame.Pic.Handle.setVisible(false);
			LandMarkQueryFrame.Pic.DeleteRow.setVisible(false);
			LandMarkQueryFrame.Pic.Delete.setVisible(false);
			LandMarkQueryFrame.Pic.UpdateRow.setVisible(false);
			LandMarkQueryFrame.Pic.Update.setVisible(false);
			LandMarkQueryFrame.Pic.MoreInfo.setVisible(false);
			LandMarkQueryFrame.Pic.Transit.setVisible(false);
			LandMarkQueryFrame.Pic.TransitAll.setVisible(false);
			LandMarkQueryFrame.setVisible(true);
		} else if (e.getSource() == MyTimerOn) {
			MyTimerEnable = true;
		} else if (e.getSource() == MyTimerOff) {
			MyTimerEnable = false;
		} else if (e.getSource() == ClearMemory) {
			Idle(100);
			System.gc();
			Idle(100);
			System.gc();
			// Extended Component
		} else if (e.getSource() == ShowPolygonAddPane) {
			NowPanel = PolygonAddPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "PolygonAddPane");
			PolygonAddPane.emerge();
		} else if (e.getSource() == ShowPolygonDatabaseView) {
			PolygonDatabaseView.emerge();
		} else if (e.getSource() == ShowLineAddPane) {
			NowPanel = LineAddPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "LineAddPane");
			LineAddPane.emerge();
		} else if (e.getSource() == ShowPointAddPane) {
			NowPanel = PointAddPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "PointAddPane");
			PointAddPane.emerge();
		} else if (e.getSource() == ShowLineDatabaseView) {
			LineDatabaseView.emerge();
		} else if (e.getSource() == CreateJPGImage) {
			try {
				JPGOutput();
			} catch (Exception ex) {
				System.gc();
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("输入的信息有误，请重试"),
						LanguageDic.GetWords("JPG生成失败"),
						JOptionPane.WARNING_MESSAGE);
			}
		} else if (e.getSource() == ShowPointDatabaseView) {
			PointDatabaseView.emerge();
		} else if (e.getSource() == ShowAutoCrossLinkPane) {
			NowPanel = AutoCrossLinkPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "AutoCrossLinkPane");
			AutoCrossLinkPane.emerge();
		} else if (e.getSource() == ShowConnectTestPane) {
			NowPanel = ConnectTestPane;
			ClearStateAfterSwitchPane();
			ToolCard.show(Tool, "ConnectTestPane");
			ConnectTestPane.emerge();
		} else if (e.getSource() == ShowTaxiTrajectoryViewPane) {
			NowPanel = TaxiTrajectoryViewPane;
			ClearStateAfterSwitchPane();
			TaxiTrajectoryViewPane.emerge();
			ToolCard.show(Tool, "TaxiTrajectoryViewPane");
		} else if (e.getSource() == ShowAutoDrivePane) {
			NowPanel = AutoDrivePane;
			ClearStateAfterSwitchPane();
			AutoDrivePane.emerge();
			ToolCard.show(Tool, "AutoDrivePane");
		} else if (e.getSource() == ShowRoadConditionWizard) {
			RoadConditionView.emerge();
		} else if (e.getSource() == ShowPreferenceWizard) {
			Preference.emerge();
		}
	}

	public class ClockWizardClass extends JFrame implements ActionListener,
			ItemListener {
		// 显示和调节时钟的窗口
		public ClockComponent pic;
		JTextField ShowHour, ShowMinute, ShowSecond;
		JLabel t1, t2, t3;
		JRadioButton am, pm;
		JButton Play;

		public ClockWizardClass() {
			pic = new ClockComponent();
			setResizable(false);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setBounds(400, 200, 320, 385);
			setLayout(null);
			add(pic);
			setVisible(false);
			setTitle(LanguageDic.GetWords("时钟设定"));
			ShowHour = new JTextField(5);
			ShowMinute = new JTextField(5);
			ShowSecond = new JTextField(5);
			t1 = new JLabel(LanguageDic.GetWords("时"));
			t2 = new JLabel(LanguageDic.GetWords("分"));
			t3 = new JLabel(LanguageDic.GetWords("秒"));
			add(ShowHour);
			add(t1);
			add(ShowMinute);
			add(t2);
			add(ShowSecond);
			add(t3);
			ShowHour.setBounds(5, 335, 30, 20);
			t1.setBounds(35, 335, 15, 20);
			ShowMinute.setBounds(50, 335, 30, 20);
			t2.setBounds(80, 335, 15, 20);
			ShowSecond.setBounds(95, 335, 30, 20);
			t3.setBounds(125, 335, 15, 20);
			am = new JRadioButton("AM");
			pm = new JRadioButton("PM");
			Play = new JButton(LanguageDic.GetWords("开关"));
			ButtonGroup group = new ButtonGroup();
			group.add(am);
			group.add(pm);
			add(am);
			add(pm);
			add(Play);
			am.setBounds(150, 335, 50, 20);
			pm.setBounds(200, 335, 50, 20);
			Play.setBounds(250, 335, 60, 20);
			Play.addActionListener(this);
			am.addItemListener(this);
			pm.addItemListener(this);
			setLocationRelativeTo(null);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				if (pic.lock) {
					pic.setHour(Integer.parseInt(ShowHour.getText()));
					pic.setMinute(Integer.parseInt(ShowMinute.getText()));
					pic.setSecond(Integer.parseInt(ShowSecond.getText()));
					pic.lock = false;
					GPSPoints.FreshTaxiHeap();
					PointDatabase.DatabaseDelete("[Info:ClockDepend]");
					LineDatabase.DatabaseDelete("[Info:ClockDepend]");
					PolygonDatabase.DatabaseDelete("[Info:ClockDepend]");
				} else {
					if (NowPanel == TaxiSearchPane) {
						if (TaxiSearchPane.CanShowTaxi) {
							JOptionPane.showMessageDialog(null,
									LanguageDic.GetWords("出租车定位程序正在读取时间"),
									LanguageDic.GetWords("时间读写冲突"),
									JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					pic.lock = true;
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("您设置的时间格式有误，请重新设置"),
						LanguageDic.GetWords("时间异常"),
						JOptionPane.WARNING_MESSAGE);
			}
		}

		public void itemStateChanged(ItemEvent e) {
			try {
				if (am.isSelected()) {
					int k = Integer.parseInt(ShowHour.getText());
					if (k >= 12)
						k -= 12;
					ShowHour.setText(Integer.toString(k));
				} else {
					int k = Integer.parseInt(ShowHour.getText());
					if (k < 12)
						k += 12;
					ShowHour.setText(Integer.toString(k));
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("您设置的时间格式有误，请重新设置"),
						LanguageDic.GetWords("时间异常"),
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------
	public class ClockComponent extends JPanel implements MouseListener,
			MouseMotionListener, MouseWheelListener {
		// 显示和操纵时钟的面板
		int offsetx = 5, offsety = 6;
		Date date;
		int hour = 9, minute = 0, second = 0;
		Line2D secondLine, minuteLine, hourLine;
		boolean lock = false;
		int a, b, c;
		double pointSX[] = new double[60], pointSY[] = new double[60],
				pointMX[] = new double[60], pointMY[] = new double[60],
				pointHX[] = new double[60], pointHY[] = new double[60];

		public ClockComponent() {
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
			setBounds(0, 0, 320, 320);
			pointSX[0] = 0;
			pointSY[0] = -150;
			pointMX[0] = 0;
			pointMY[0] = -100;
			pointHX[0] = 0;
			pointHY[0] = -50;
			double angle = 6 * Math.PI / 180;
			for (int i = 0; i < 59; i++) {
				pointSX[i + 1] = pointSX[i] * Math.cos(angle) - Math.sin(angle)
						* pointSY[i];
				pointSY[i + 1] = pointSY[i] * Math.cos(angle) + pointSX[i]
						* Math.sin(angle);
				pointMX[i + 1] = pointMX[i] * Math.cos(angle) - Math.sin(angle)
						* pointMY[i];
				pointMY[i + 1] = pointMY[i] * Math.cos(angle) + pointMX[i]
						* Math.sin(angle);
				pointHX[i + 1] = pointHX[i] * Math.cos(angle) - Math.sin(angle)
						* pointHY[i];
				pointHY[i + 1] = pointHY[i] * Math.cos(angle) + pointHX[i]
						* Math.sin(angle);
			}
			for (int i = 0; i < 60; i++) {
				pointSX[i] += 150 + offsetx;
				pointSY[i] += 150 + offsety;
				pointMX[i] += 150 + offsetx;
				pointMY[i] += 150 + offsety;
				pointHX[i] += 150 + offsetx;
				pointHY[i] += 150 + offsety;
			}
			secondLine = new Line2D.Double(0, 0, 0, 0);
			minuteLine = new Line2D.Double(0, 0, 0, 0);
			hourLine = new Line2D.Double(0, 0, 0, 0);
		}

		public void paint(Graphics g) {
			Toolkit kit = getToolkit();
			Image img = kit.getImage("time.jpg");
			g.drawImage(img, 0, 0, 325, 400, this);
			for (int i = 0; i < 60; i++) {
				int m = (int) pointSX[i];
				int n = (int) pointSY[i];
				if (i % 5 == 0) {
					g.setColor(Color.red);
					g.fillOval(m - 4, n - 4, 8, 8);
				} else {
					g.setColor(Color.blue);
					g.fillOval(m - 2, n - 2, 4, 4);
				}
			}
			g.fillOval(145 + offsetx, 145 + offsety, 10, 10);
			Graphics2D g_2d = (Graphics2D) g;
			g_2d.setColor(Color.red);
			g_2d.draw(secondLine);
			BasicStroke bs = new BasicStroke(3f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_MITER);
			g_2d.setStroke(bs);
			g_2d.setColor(Color.blue);
			g_2d.draw(minuteLine);
			bs = new BasicStroke(6f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
			g_2d.setStroke(bs);
			g_2d.setColor(Color.orange);
			g_2d.draw(hourLine);
		}

		public void setHour(int hour) {
			this.hour = hour;
		}

		public void setMinute(int minute) {
			this.minute = minute;
		}

		public void setSecond(int second) {
			this.second = second;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}

		public int getSecond() {
			return second;
		}

		public void spur() {
			if (lock)
				return;
			second++;
			if (second == 60) {
				second = 0;
				minute++;
				if (minute == 60) {
					minute = 0;
					hour++;
					if (hour == 24) {
						hour = 0;
					}
				}
			}
			ClockWizard.ShowHour.setText(Integer.toString(hour));
			ClockWizard.ShowMinute.setText(Integer.toString(minute));
			ClockWizard.ShowSecond.setText(Integer.toString(second));
			if (hour >= 12) {
				ClockWizard.am.setSelected(false);
				ClockWizard.pm.setSelected(true);
			} else {
				ClockWizard.am.setSelected(true);
				ClockWizard.pm.setSelected(false);
			}
			int h = hour % 12;
			a = second;
			b = minute;
			c = h * 5 + minute / 12;
			secondLine.setLine(150 + offsetx, 150 + offsety, (int) pointSX[a],
					(int) pointSY[a]);
			minuteLine.setLine(150 + offsetx, 150 + offsety, (int) pointMX[b],
					(int) pointMY[b]);
			hourLine.setLine(150 + offsetx, 150 + offsety, (int) pointHX[c],
					(int) pointHY[c]);
			repaint();
		}

		public void mouseClicked(MouseEvent e) {
		}

		int Catch = 0;

		public void mousePressed(MouseEvent e) {
			if (!lock)
				return;
			int a, b;
			a = Integer.parseInt(ClockWizard.ShowSecond.getText());
			b = Integer.parseInt(ClockWizard.ShowMinute.getText());
			if (Math.abs(e.getX() - pointSX[a])
					+ Math.abs(e.getY() - pointSY[a]) < 20)
				Catch = 1;
			else if (Math.abs(e.getX() - pointMX[b])
					+ Math.abs(e.getY() - pointMY[b]) < 20)
				Catch = 2;
		}

		public void mouseReleased(MouseEvent e) {
			if (!lock)
				return;
			if (Catch != 0) {
				double l1 = 150 + offsety;
				double l2 = Math.sqrt((150 + offsetx - e.getX())
						* (150 + offsetx - e.getX())
						+ (150 + offsety - e.getY())
						* (150 + offsety - e.getY()));
				double l3 = Math.sqrt((150 + offsetx - e.getX())
						* (150 + offsetx - e.getX()) + e.getY() * e.getY());
				double sita = Math.acos((l1 * l1 + l2 * l2 - l3 * l3) / 2 / l1
						/ l2)
						/ Math.PI * 180;
				if (e.getX() < 150 + offsetx)
					sita = 360 - sita;
				if (Catch == 1) {
					secondLine.setLine(150 + offsetx, 150 + offsety,
							(int) pointSX[(int) (sita / 6)],
							(int) pointSY[(int) (sita / 6)]);
					ClockWizard.ShowSecond.setText(Integer
							.toString((int) (sita / 6)));
				} else if (Catch == 2) {
					minuteLine.setLine(150 + offsetx, 150 + offsety,
							(int) pointMX[(int) (sita / 6)],
							(int) pointMY[(int) (sita / 6)]);
					ClockWizard.ShowMinute.setText(Integer
							.toString((int) (sita / 6)));
				}
				repaint();
			}
			Catch = 0;
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			if (!lock)
				return;
			if (Catch != 0) {
				double l1 = 150 + offsety;
				double l2 = Math.sqrt((150 + offsetx - e.getX())
						* (150 + offsetx - e.getX())
						+ (150 + offsety - e.getY())
						* (150 + offsety - e.getY()));
				double l3 = Math.sqrt((150 + offsetx - e.getX())
						* (150 + offsetx - e.getX()) + e.getY() * e.getY());
				double sita = Math.acos((l1 * l1 + l2 * l2 - l3 * l3) / 2 / l1
						/ l2)
						/ Math.PI * 180;
				if (e.getX() < 150 + offsetx)
					sita = 360 - sita;
				if (Catch == 1) {
					secondLine.setLine(150 + offsetx, 150 + offsety,
							(int) pointSX[(int) (sita / 6)],
							(int) pointSY[(int) (sita / 6)]);
					ClockWizard.ShowSecond.setText(Integer
							.toString((int) (sita / 6)));
				} else if (Catch == 2) {
					minuteLine.setLine(150 + offsetx, 150 + offsety,
							(int) pointMX[(int) (sita / 6)],
							(int) pointMY[(int) (sita / 6)]);
					ClockWizard.ShowMinute.setText(Integer
							.toString((int) (sita / 6)));
				}
				repaint();
			}
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			try {
				if (!lock)
					return;
				if (ClockWizard.ShowMinute.isFocusOwner()) {
					int temp = Integer.parseInt(ClockWizard.ShowMinute
							.getText()) - e.getWheelRotation();
					temp = temp + 6000;
					temp = temp % 60;
					minuteLine.setLine(150 + offsetx, 150 + offsety,
							(int) pointMX[temp], (int) pointMY[temp]);
					ClockWizard.ShowMinute.setText(Integer.toString(temp));
					repaint();
					return;
				} else if (ClockWizard.ShowSecond.isFocusOwner()) {
					int temp = Integer.parseInt(ClockWizard.ShowSecond
							.getText()) - e.getWheelRotation();
					temp = temp + 6000;
					temp = temp % 60;
					secondLine.setLine(150 + offsetx, 150 + offsety,
							(int) pointSX[temp], (int) pointSY[temp]);
					ClockWizard.ShowSecond.setText(Integer.toString(temp));
					repaint();
					return;
				}
				int temp = Integer.parseInt(ClockWizard.ShowHour.getText())
						- e.getWheelRotation();
				temp = temp + 12000;
				temp = temp % 12;
				hourLine.setLine(150 + offsetx, 150 + offsety,
						(int) pointHX[temp * 5], (int) pointHY[temp * 5]);
				ClockWizard.ShowHour.setText(Integer.toString(ClockWizard.am
						.isSelected() ? temp : temp + 12));
				repaint();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						LanguageDic.GetWords("XXX您设置的时间格式有误，请重新设置"),
						LanguageDic.GetWords("时间异常"),
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public class MapHandle implements MapControl {
		public ExtendedToolPane.ServerSocketPaneClass GetServerPane() {
			return ServerSocketPane;
		}

		public ExtendedToolPane.ClientSocketPaneClass GetClientPane() {
			return ClientSocketPane;
		}

		private Calendar StandardCalendar = Calendar.getInstance();

		public String GetInternationalTimeSignature() {
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			return "[" + df.format(new Date()) + "]";
		}

		public double MetertoLongitude(double Meter) {
			return Meter / 6371.0 / 1000.0 / Math.PI * 180.0
					/ Math.cos((LatitudeStart + LatitudeEnd) / 360.0 * Math.PI);
		}

		public double MetertoLatitude(double Meter) {
			return Meter / 6371.0 / 1000.0 / Math.PI * 180.0;
		}

		public double LongitudetoMeter(double Longitude) {
			return Longitude / 180.0 * Math.PI * 6371.0 * 1000.0
					* Math.cos((LatitudeStart + LatitudeEnd) / 360.0 * Math.PI);
		}

		public double LatitudetoMeter(double Latitude) {
			return Latitude / 180.0 * Math.PI * 6371.0 * 1000.0;
		}

		public void SolveException(Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.toString());
		}

		// [ACMCUP2014]PointOutputTransform
		public void PointOutputTransform() {
			try {
				String FilterTag = JOptionPane.showInputDialog(null,
						"Choose the Filter Tag");
				BufferedWriter BOUT = new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(new File(
										FileDialog.getCurrentDirectory(),
										GetInternationalTimeSignature()
												+ "Point.txt")), "UTF-8"));
				for (int i = 0; i < PointDatabase.PointNum; i++) {
					if ((FilterTag == null)
							|| (FilterTag.equals(""))
							|| (PointDatabase.PointHint[i].indexOf(FilterTag) == -1))
						continue;
					BOUT.write((i + 1)
							+ ":<gml:Point srsName=\"EPSG:54004\" xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates decimal=\".\" cs=\",\" ts=\" \">");
					BOUT.write(PointDatabase.AllPointX[i] + ","
							+ PointDatabase.AllPointY[i]
							+ " </gml:coordinates></gml:Point>\n");
				}
				BOUT.flush();
				BOUT.close();
				JOptionPane.showMessageDialog(null, "Finished");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// [ACMCUP2014]LineOutputTransform
		public void LineOutputTransform() {
			try {
				String FilterTag = JOptionPane.showInputDialog(null,
						"Choose the Filter Tag");
				BufferedWriter BOUT = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(new File(
								FileDialog.getCurrentDirectory(),
								GetInternationalTimeSignature() + "Line.txt")),
								"UTF-8"));
				for (int i = 0; i < LineDatabase.LineNum; i++) {
					if ((FilterTag != null)
							&& (!FilterTag.equals(""))
							&& (LineDatabase.LineHint[i].indexOf(FilterTag) == -1))
						continue;
					BOUT.write((i + 1)
							+ ":<gml:LineString srsName=\"EPSG:54004\" xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates decimal=\".\" cs=\",\" ts=\" \">");
					int temp = LineDatabase.LineHead[i];
					while (temp != -1) {
						BOUT.write(LineDatabase.AllPointX[temp] + ","
								+ LineDatabase.AllPointY[temp] + " ");
						temp = LineDatabase.AllPointNext[temp];
					}
					BOUT.write("</gml:coordinates></gml:LineString>\n");
				}
				BOUT.flush();
				BOUT.close();
				JOptionPane.showMessageDialog(null, "Finished");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void LineOutput() {
			String str = JOptionPane.showInputDialog(null,
					LanguageDic.GetWords("请输出文件名前缀，不能为空"),
					LanguageDic.GetWords("导出折线数据库到文件"),
					JOptionPane.PLAIN_MESSAGE);
			if ((str == null) || (str.equals(""))) {
				ReTitle(LanguageDic.GetWords("放弃了导出"));
				return;
			}
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			Handle.getLineDatabase().DatabaseFileOutput(
					new File(str + "[LineDB][" + df.format(new Date())
							+ "].csv"));
		}

		// [ACMCUP2014]PolygonOutputTransform
		public void PolygonOutputTransform() {
			if (PolygonDatabaseFile != null) {
				PolygonDatabase.DatabaseFileOutput(PolygonDatabaseFile);
				JOptionPane.showMessageDialog(null, "Finished");
			} else
				JOptionPane.showMessageDialog(null,
						"Please Open Polygon File First!");
		}

		public void PolygonOutput() {
			String str = JOptionPane.showInputDialog(null,
					LanguageDic.GetWords("请输出文件名前缀，不能为空"),
					LanguageDic.GetWords("导出多边形数据库到文件"),
					JOptionPane.PLAIN_MESSAGE);
			if ((str == null) || (str.equals(""))) {
				ReTitle("放弃了导出");
				return;
			}
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			Handle.getPolygonDatabase().DatabaseFileOutput(
					new File(str + "[PolygonDB][" + df.format(new Date())
							+ "].csv"));
		}

		// [ACMCUP2014]PointInputTransform
		public void PointInputTransform() {
			BufferedReader BIN;
			BufferedWriter BOUT;
			String s1;
			int i, j, k;
			try {
				int state = FileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					System.out.println(FileDialog.getCurrentDirectory());
					BIN = new BufferedReader(new InputStreamReader(
							new FileInputStream(FileDialog.getSelectedFile()),
							"UTF-8"));
					BOUT = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(new File(
									FileDialog.getCurrentDirectory(),
									GetInternationalTimeSignature()
											+ "_Point.txt")), "UTF-8"));
				} else
					return;
				while ((s1 = BIN.readLine()) != null) {
					i = s1.indexOf(":");
					BOUT.write("[PointStart]-----------------------\n");
					BOUT.write("[Info:BasicLandMark][Title:"
							+ s1.substring(0, i) + "]\n");
					BOUT.write("903\n");
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf(">", i + 1);
					i++;
					while (i < s1.length()) {
						j = s1.indexOf(",", i);
						k = s1.indexOf(" ", i);
						if (j < 0)
							break;
						BOUT.write(s1.substring(i, j) + "/");
						BOUT.write(s1.substring(j + 1, k) + "\n");
						i = k + 1;
					}
					BOUT.write("[PointEnd]-----------------------\n");
				}
				BOUT.flush();
				JOptionPane.showMessageDialog(null, "Finished!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void PointOutput() {
			String str = JOptionPane.showInputDialog(null,
					LanguageDic.GetWords("请输出文件名前缀，不能为空"),
					LanguageDic.GetWords("导出兴趣点数据库到文件"),
					JOptionPane.PLAIN_MESSAGE);
			if ((str == null) || (str.equals(""))) {
				ReTitle(LanguageDic.GetWords("放弃了导出"));
				return;
			}
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			Handle.getPointDatabase().DatabaseFileOutput(
					new File(str + "[PointDB][" + df.format(new Date())
							+ "].csv"));
		}

		void OtherFormatPointInput(File PointDatabaseFile) {
			BufferedReader BIN;
			String s1 = "";
			int i, j, k;
			int count = 0;
			double[] xx = new double[100000];
			double[] yy = new double[100000];
			try {
				BIN = new BufferedReader(new InputStreamReader(
						new FileInputStream(PointDatabaseFile), "UTF-8"));
				while ((s1 = BIN.readLine()) != null) {
					if (s1.equals("-1")) {
						PointDatabase.add(xx[0], yy[0],
								"[Info:ManualAdd][Title:]");
						count = 0;
					} else {
						i = s1.indexOf(" ");
						j = s1.indexOf(" ", i + 1);
						k = s1.indexOf(" ", j + 1);
						double y = java.lang.Double.parseDouble(s1.substring(
								i + 1, j));
						double x = java.lang.Double.parseDouble(s1.substring(
								j + 1, k));
						xx[count] = x;
						yy[count] = y;
						count++;
					}
				}
				HighestRate = (int) 1e9;
				// Resize();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(s1);
			}
		}

		// [ACMCUP2014]PointInput
		public void PointInput() {
			BufferedReader BIN;
			String s1;
			int i, j, k;
			try {
				int state = FileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					if (FileDialog.getSelectedFile().getName().endsWith("csv")) {
						PointDatabase.DatabaseInit();
						PointDatabase.DatabaseFileInput(FileDialog
								.getSelectedFile());
						return;
					}
					BIN = new BufferedReader(new InputStreamReader(
							new FileInputStream(FileDialog.getSelectedFile()),
							"UTF-8"));
				} else
					return;
				PointDatabase.DatabaseInit();
				PointDatabaseFile = FileDialog.getSelectedFile();
				while ((s1 = BIN.readLine()) != null) {
					if (s1.indexOf("[PointStart]") != -1) {
						PointDatabase.DatabaseInit();
						PointDatabase.DatabaseFileInput(PointDatabaseFile);
						break;
					} else if (s1.indexOf('<') == -1) {
						OtherFormatPointInput(PointDatabaseFile);
						return;
					}
					i = s1.indexOf(":");
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf(">", i + 1);
					i++;
					while (i < s1.length()) {
						j = s1.indexOf(",", i);
						k = s1.indexOf(" ", i);
						if (j < 0)
							break;
						double x, y;
						x = java.lang.Double.parseDouble(s1.substring(i, j));
						y = java.lang.Double
								.parseDouble(s1.substring(j + 1, k));
						i = k + 1;
						PointDatabase.add(x, y, "[Info:ManualAdd][Title:]");
					}
				}
				HighestRate = 10000;
				// DIR=FileDialog.getCurrentDirectory();
				// Resize();
				BIN.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Handle.ScreenFlush();
		}

		public void PointAppend() {
			BufferedReader BIN;
			String s1;
			int i, j, k;
			try {
				int state = FileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					if (FileDialog.getSelectedFile().getName().endsWith(".csv")) {
						PointDatabase.DatabaseFileInput(FileDialog
								.getSelectedFile());
						return;
					}
					BIN = new BufferedReader(new InputStreamReader(
							new FileInputStream(FileDialog.getSelectedFile()),
							"UTF-8"));
				} else
					return;
				while ((s1 = BIN.readLine()) != null) {
					if (s1.indexOf("[PointStart]") != -1) {
						PointDatabase.DatabaseFileInput(FileDialog
								.getSelectedFile());
						break;
					} else if (s1.indexOf('<') == -1) {
						OtherFormatPointInput(FileDialog.getSelectedFile());
						return;
					}
					i = s1.indexOf(":");
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf(">", i + 1);
					i++;
					while (i < s1.length()) {
						j = s1.indexOf(",", i);
						k = s1.indexOf(" ", i);
						if (j < 0)
							break;
						double x, y;
						x = java.lang.Double.parseDouble(s1.substring(i, j));
						y = java.lang.Double
								.parseDouble(s1.substring(j + 1, k));
						i = k + 1;
						PointDatabase.add(x, y, "[Info:ManualAdd][Title:]");
					}
				}
				HighestRate = 10000;
				// DIR=FileDialog.getCurrentDirectory();
				// Resize();
				BIN.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Handle.ScreenFlush();
		}

		// [ACMCUP2014]LineInputTransform
		public void LineInputTransform() {
			BufferedReader BIN;
			BufferedWriter BOUT;
			String s1;
			double left = 1e100;
			double right = -1e100;
			double up = -1e100;
			double down = 1e100;
			int i, j, k;
			try {
				int state = FileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					BIN = new BufferedReader(new InputStreamReader(
							new FileInputStream(FileDialog.getSelectedFile()),
							"UTF-8"));
					BOUT = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(new File(
									FileDialog.getCurrentDirectory(),
									GetInternationalTimeSignature()
											+ "_Line.txt")), "UTF-8"));
				} else
					return;
				while ((s1 = BIN.readLine()) != null) {
					i = s1.indexOf(":");
					BOUT.write("[LineStart]-----------------------\n");
					BOUT.write("[Info:Road][Title:" + s1.substring(0, i)
							+ "]\n");
					BOUT.write("9\n0\n0\n0\n");
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf(">", i + 1);
					i++;
					while (i < s1.length()) {
						j = s1.indexOf(",", i);
						k = s1.indexOf(" ", i);
						if (j < 0)
							break;
						double xx, yy;
						xx = java.lang.Double.parseDouble(s1.substring(i, j));
						yy = java.lang.Double.parseDouble(s1
								.substring(j + 1, k));
						left = Math.min(left, xx);
						right = Math.max(right, xx);
						down = Math.min(down, yy);
						up = Math.max(up, yy);
						i = k + 1;
					}
				}
				BOUT.flush();
				s1 = "[LongitudeStart]" + (left - (right - left) * 0.1) + "\n";
				s1 += "[LongitudeEnd]" + (right + (right - left) * 0.1) + "\n";
				s1 += "[LatitudeStart]" + (down - (up - down) * 0.1) + "\n";
				s1 += "[LatitudeEnd]" + (up + (up - down) * 0.1) + "\n";
				JOptionPane.showMessageDialog(null, s1);
				Preference.bulletin.setText(s1);
				LongitudeStart = Math.min(LongitudeStart, left);
				LongitudeEnd = Math.max(LongitudeEnd, right);
				LatitudeStart = Math.min(LatitudeStart, down);
				LatitudeEnd = Math.max(LatitudeEnd, up);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void OtherFormatLineInput(File LineDatabaseFile) {
			int LineNum = 0;
			BufferedReader BIN;
			String s1 = "";
			int i, j, k;
			int count = 0;
			double[] xx = new double[100000];
			double[] yy = new double[100000];
			try {
				BIN = new BufferedReader(new InputStreamReader(
						new FileInputStream(LineDatabaseFile), "UTF-8"));
				while ((s1 = BIN.readLine()) != null) {
					if (s1.equals("-1")) {
						LineNum++;
						LineDatabase.add(xx, yy, count,
								"[Info:ManualAdd][Title:]");
						count = 0;
					} else {
						i = s1.indexOf(" ");
						j = s1.indexOf(" ", i + 1);
						k = s1.indexOf(" ", j + 1);
						double y = java.lang.Double.parseDouble(s1.substring(
								i + 1, j));
						double x = java.lang.Double.parseDouble(s1.substring(
								j + 1, k));
						xx[count] = x;
						yy[count] = y;
						count++;
					}
				}
				HighestRate = (int) 1e9;
				// Resize();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(">>>>>>>>>>>>LineNum：" + LineNum);
				System.out.println(s1);
			}
		}

		// [ACMCUP2014]LineInput
		public void LineInput() {
			int LineNum = 0;
			BufferedReader BIN;
			String s1 = "";
			int i, j, k;
			int count;
			double[] xx = new double[100000];
			double[] yy = new double[100000];
			try {
				int state = FileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					if (FileDialog.getSelectedFile().getName().endsWith("csv")) {
						LineDatabase.DatabaseInit();
						LineDatabase.DatabaseFileInput(FileDialog
								.getSelectedFile());
						return;
					}
					BIN = new BufferedReader(new InputStreamReader(
							new FileInputStream(FileDialog.getSelectedFile()),
							"UTF-8"));
				} else
					return;
				LineDatabase.DatabaseInit();
				LineDatabaseFile = FileDialog.getSelectedFile();
				while ((s1 = BIN.readLine()) != null) {
					if (s1.indexOf("[LineStart]") != -1) {
						LineDatabase.DatabaseInit();
						LineDatabase.DatabaseFileInput(LineDatabaseFile);
						break;
					} else if (s1.indexOf('<') == -1) {
						OtherFormatLineInput(LineDatabaseFile);
						return;
					}
					LineNum++;
					count = 0;
					i = s1.indexOf(":");
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf(">", i + 1);
					i++;
					while (i < s1.length()) {
						j = s1.indexOf(",", i);
						k = s1.indexOf(" ", i);
						if (j < 0)
							break;
						double x = java.lang.Double.parseDouble(s1.substring(i,
								j));
						double y = java.lang.Double.parseDouble(s1.substring(
								j + 1, k));
						xx[count] = x;
						yy[count] = y;
						count++;
						i = k + 1;
					}
					LineDatabase.add(xx, yy, count, "[Info:ManualAdd][Title:]");
				}
				HighestRate = (int) 1e9;
				// DIR=FileDialog.getCurrentDirectory();
				// Resize();
				BIN.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(">>>>>>>>>>>>LineNum：" + LineNum);
				System.out.println(s1);
			}
			Handle.ScreenFlush();
		}

		public void LineAppend() {
			int LineNum = 0;
			BufferedReader BIN;
			String s1 = "";
			int i, j, k;
			int count;
			double[] xx = new double[100000];
			double[] yy = new double[100000];
			try {
				int state = FileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					if (FileDialog.getSelectedFile().getName().endsWith(".csv")) {
						LineDatabase.DatabaseFileInput(FileDialog
								.getSelectedFile());
						return;
					}
					BIN = new BufferedReader(new InputStreamReader(
							new FileInputStream(FileDialog.getSelectedFile()),
							"UTF-8"));
				} else
					return;
				while ((s1 = BIN.readLine()) != null) {
					if (s1.indexOf("[LineStart]") != -1) {
						LineDatabase.DatabaseFileInput(FileDialog
								.getSelectedFile());
						break;
					} else if (s1.indexOf('<') == -1) {
						OtherFormatLineInput(FileDialog.getSelectedFile());
						return;
					}
					LineNum++;
					count = 0;
					i = s1.indexOf(":");
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf("<", i + 1);
					i = s1.indexOf(">", i + 1);
					i++;
					while (i < s1.length()) {
						j = s1.indexOf(",", i);
						k = s1.indexOf(" ", i);
						if (j < 0)
							break;
						double x = java.lang.Double.parseDouble(s1.substring(i,
								j));
						double y = java.lang.Double.parseDouble(s1.substring(
								j + 1, k));
						xx[count] = x;
						yy[count] = y;
						count++;
						i = k + 1;
					}
					LineDatabase.add(xx, yy, count, "[Info:ManualAdd][Title:]");
				}
				HighestRate = (int) 1e9;
				// DIR=FileDialog.getCurrentDirectory();
				// Resize();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(">>>>>>>>>>>>LineNum：" + LineNum);
				System.out.println(s1);
			}
			Handle.ScreenFlush();
		}

		// [ACMCUP2014]PolygonInput
		public void PolygonInput() {
			int state = FileDialog.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				PolygonDatabase.DatabaseInit();
				PolygonDatabaseFile = FileDialog.getSelectedFile();
				PolygonDatabase.DatabaseFileInput(PolygonDatabaseFile);
				HighestRate = 10000;
				// DIR=FileDialog.getCurrentDirectory();
				// Resize();
				Screen.repaint();
			} else
				return;
			Handle.ScreenFlush();
		}

		public void PointFolderAppend() {
			JFileChooser FileDialog = new JFileChooser();
			int state = FileDialog.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				MapKernel.FileAccept File_Accept = new FileAccept();
				File_Accept.setExtendName("csv");
				File[] File_list = FileDialog.getCurrentDirectory().listFiles(
						File_Accept);
				for (File fin : File_list) {
					// Single File------------------------------------
					PointDatabase.DatabaseFileInput(fin);
					// ------------------------------------------------
				}
			}
			Handle.ScreenFlush();
		}

		public void LineFolderAppend() {
			JFileChooser FileDialog = new JFileChooser();
			int state = FileDialog.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				MapKernel.FileAccept File_Accept = new FileAccept();
				File_Accept.setExtendName("csv");
				File[] File_list = FileDialog.getCurrentDirectory().listFiles(
						File_Accept);
				for (File fin : File_list) {
					// Single File------------------------------------
					LineDatabase.DatabaseFileInput(fin);
					// ------------------------------------------------
				}
			}
			Handle.ScreenFlush();
		}

		public void PolygonAppend() {
			int state = FileDialog.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				PolygonDatabase.DatabaseFileInput(FileDialog.getSelectedFile());
				HighestRate = 10000;
				// DIR=FileDialog.getCurrentDirectory();
				// Resize();
				Screen.repaint();
			} else
				return;
			Handle.ScreenFlush();
		}

		public void PolygonFolderAppend() {
			JFileChooser FileDialog = new JFileChooser();
			int state = FileDialog.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				MapKernel.FileAccept File_Accept = new FileAccept();
				File_Accept.setExtendName("csv");
				File[] File_list = FileDialog.getCurrentDirectory().listFiles(
						File_Accept);
				for (File fin : File_list) {
					// Single File------------------------------------
					PolygonDatabase.DatabaseFileInput(fin);
					// ------------------------------------------------
				}
			}
			Handle.ScreenFlush();
		}

		// [ACMCUP2014]Resize
		public void Resize() {
			double left = 1e100;
			double right = -1e100;
			double up = -1e100;
			double down = 1e100;
			for (int i = 0; i < PointDatabase.PointNum; i++) {
				left = Math.min(left, PointDatabase.AllPointX[i]);
				right = Math.max(right, PointDatabase.AllPointX[i]);
				down = Math.min(down, PointDatabase.AllPointY[i]);
				up = Math.max(up, PointDatabase.AllPointY[i]);
			}
			for (int i = 0; i < LineDatabase.LineNum; i++) {
				int j = LineDatabase.LineHead[i];
				while (j != -1) {
					left = Math.min(left, LineDatabase.AllPointX[j]);
					right = Math.max(right, LineDatabase.AllPointX[j]);
					down = Math.min(down, LineDatabase.AllPointY[j]);
					up = Math.max(up, LineDatabase.AllPointY[j]);
					j = LineDatabase.AllPointNext[j];
				}
			}
			for (int i = 0; i < PolygonDatabase.PolygonNum; i++) {
				int j = PolygonDatabase.PolygonHead[i];
				while (j != -1) {
					left = Math.min(left, PolygonDatabase.AllPointX[j]);
					right = Math.max(right, PolygonDatabase.AllPointX[j]);
					down = Math.min(down, PolygonDatabase.AllPointY[j]);
					up = Math.max(up, PolygonDatabase.AllPointY[j]);
					j = PolygonDatabase.AllPointNext[j];
				}
			}
			left -= (right - left) * 0.1;
			right += (right - left) * 0.1;
			down -= (up - down) * 0.1;
			up += (up - down) * 0.1;
			String s1;
			s1 = "[LongitudeStart]" + left + "\n";
			s1 += "[LongitudeEnd]" + right + "\n";
			s1 += "[LatitudeStart]" + down + "\n";
			s1 += "[LatitudeEnd]" + up + "\n";
			Preference.bulletin.setText(s1);
			LongitudeStart = left;
			LongitudeEnd = right;
			LatitudeStart = down;
			LatitudeEnd = up;
			if (LongitudeStart > LongitudeEnd) {
				LongitudeStart = -100;
				LongitudeEnd = 100;
			}
			if (LatitudeStart > LatitudeEnd) {
				LatitudeStart = -100;
				LatitudeEnd = 100;
			}
			Screen.ScreenLongitude = LongitudeStart;
			Screen.ScreenLatitude = LatitudeEnd;
			Screen.LongitudeScale = (LongitudeEnd - LongitudeStart);
			Screen.LatitudeScale = (LatitudeEnd - LatitudeStart);
			Screen.setVisible(true);
			Screen.repaint();
			JOptionPane.showMessageDialog(null, s1);
		}

		// [ACMCUP2014]ImageDirChange
		public void ImageDirChange() {
			int state = FileDialog.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				ImageDir = FileDialog.getCurrentDirectory();
			} else
				return;
		}

		// [ACMCUP2014]To Trap the Line [Info:Trap]
		public void PendingTrap() {
			if (!IsPolygonLoaded()) {
				JOptionPane.showMessageDialog(null, "PolygonDatabase is NULL!");
				return;
			}
			int PolygonNum = PolygonDatabase.PolygonNum;
			if (!IsLineLoaded()) {
				JOptionPane.showMessageDialog(null, "LineDatabase is NULL!");
				return;
			}
			if (getLineDatabase().GetIndexPermission() != null)
				getLineDatabase().GetIndexPermission().WriteBack();
			Database.RTreeIndex LineRTree = new Database.RTreeIndex();
			LineRTree.IndexInit(getLineDatabase());
			for (int i = 0; i < PolygonNum; i++) {
				if (PolygonDatabase.PolygonHint[i].indexOf("[Info:Trap]") == -1)
					continue;
				ArrayList<Integer> res = LineRTree.Search(
						PolygonDatabase.GetMBRX1(i),
						PolygonDatabase.GetMBRY1(i),
						PolygonDatabase.GetMBRX2(i),
						PolygonDatabase.GetMBRY2(i));
				for (int j : res)
					LineDatabase.LineHint[j] += "[Info:Trap]";
			}
			getLineDatabase().SetIndexPermission(null);
			LineRTree = null;
			// -----------------------------------------------------------------------
			if (!IsPointLoaded()) {
				JOptionPane.showMessageDialog(null, "PointDatabase is NULL!");
				return;
			}
			if (getPointDatabase().GetIndexPermission() != null)
				getPointDatabase().GetIndexPermission().WriteBack();
			Database.RTreeIndex PointRTree = new Database.RTreeIndex();
			PointRTree.IndexInit(getPointDatabase());
			for (int i = 0; i < PolygonNum; i++) {
				if (PolygonDatabase.PolygonHint[i].indexOf("[Info:Trap]") == -1)
					continue;
				ArrayList<Integer> res = PointRTree.Search(
						PolygonDatabase.GetMBRX1(i),
						PolygonDatabase.GetMBRY1(i),
						PolygonDatabase.GetMBRX2(i),
						PolygonDatabase.GetMBRY2(i));
				for (int j : res) {
					if (!PolygonDatabase.CheckInsidePolygon(i,
							PointDatabase.AllPointX[j],
							PointDatabase.AllPointY[j]))
						continue;
					PointDatabase.PointHint[j] += "[Info:Trap]";
				}
			}
			getPointDatabase().SetIndexPermission(null);
			PointRTree = null;
			// ------------------------------------------------------------------------
			if (getPolygonDatabase().GetIndexPermission() != null)
				getPolygonDatabase().GetIndexPermission().WriteBack();
			Database.RTreeIndex PolygonRTree = new Database.RTreeIndex();
			PolygonRTree.IndexInit(getPolygonDatabase());
			PolygonNum = PolygonDatabase.PolygonNum;
			for (int i = 0; i < PolygonNum; i++) {
				if (PolygonDatabase.PolygonHint[i].indexOf("[Info:Trap]") == -1)
					continue;
				ArrayList<Integer> res = PolygonRTree.Search(
						PolygonDatabase.GetMBRX1(i),
						PolygonDatabase.GetMBRY1(i),
						PolygonDatabase.GetMBRX2(i),
						PolygonDatabase.GetMBRY2(i));
				for (int j : res)
					PolygonDatabase.PolygonHint[j] += "[Info:Trap++]";
			}
			getPolygonDatabase().SetIndexPermission(null);
			PolygonRTree = null;
			System.gc();
			JOptionPane.showMessageDialog(null, "Finished!");
		}

		// [ACMCUP2014]This function is to Warning the Crossing Line and mark
		// the [Info:Delete]
		public void CheckDelete() {
			if (!IsLineLoaded()) {
				JOptionPane.showMessageDialog(null, "LineDatabase is NULL!");
				return;
			}
			if (!IsPolygonLoaded()) {
				JOptionPane.showMessageDialog(null, "PolygonDatabase is NULL!");
				return;
			}
			int LineNum = LineDatabase.LineNum;
			int temp = -1;
			ArrayList<Integer> res;
			if (getPolygonDatabase().GetIndexPermission() != null)
				getPolygonDatabase().GetIndexPermission().WriteBack();
			Database.RTreeIndex PolygonRTree = new Database.RTreeIndex();
			PolygonRTree.IndexInit(getPolygonDatabase());
			for (int i = 0; i < LineNum; i++) {
				if (LineDatabase.LineHint[i].indexOf("[Info:LinkRegion]") == -1)
					continue;
				double XStep = (LongitudeEnd - LongitudeStart) / 100;
				double YStep = (LatitudeEnd - LatitudeEnd) / 100;
				// Check
				// Head-----------------------------------------------------------------------
				temp = LineDatabase.LineHead[i];
				res = PolygonRTree.Search(LineDatabase.AllPointX[temp] - XStep,
						LineDatabase.AllPointY[temp] - YStep,
						LineDatabase.AllPointX[temp] + XStep,
						LineDatabase.AllPointY[temp] + YStep);
				while (!res.isEmpty()) {
					if (PolygonDatabase.CheckInsidePolygon(res.get(0),
							LineDatabase.AllPointX[temp],
							LineDatabase.AllPointY[temp]))
						break;
					res.remove(0);
				}
				if (res.isEmpty()) {
					LineDatabase.LineHint[i] += "[Info:Delete]";
					continue;
				}
				// Check Middle
				// Point----------------------------------------------------------------
				temp = LineDatabase.AllPointNext[temp];
				res.clear();
				while (temp != -1) {
					if (LineDatabase.AllPointNext[temp] == -1)
						break;
					res = PolygonRTree.Search(LineDatabase.AllPointX[temp]
							- XStep, LineDatabase.AllPointY[temp] - YStep,
							LineDatabase.AllPointX[temp] + XStep,
							LineDatabase.AllPointY[temp] + YStep);
					while (!res.isEmpty()) {
						if (PolygonDatabase.CheckInsidePolygon(res.get(0),
								LineDatabase.AllPointX[temp],
								LineDatabase.AllPointY[temp]))
							break;
						res.remove(0);
					}
					if (!res.isEmpty())
						break;
					temp = LineDatabase.AllPointNext[temp];
				}
				if (!res.isEmpty()) {
					LineDatabase.LineHint[i] += "[Info:Delete]";
					continue;
				}
				// Check
				// Tail-------------------------------------------------------------------------
				res = PolygonRTree.Search(LineDatabase.AllPointX[temp] - XStep,
						LineDatabase.AllPointY[temp] - YStep,
						LineDatabase.AllPointX[temp] + XStep,
						LineDatabase.AllPointY[temp] + YStep);
				while (!res.isEmpty()) {
					if (PolygonDatabase.CheckInsidePolygon(res.get(0),
							LineDatabase.AllPointX[temp],
							LineDatabase.AllPointY[temp]))
						break;
					res.remove(0);
				}
				if (res.isEmpty()) {
					LineDatabase.LineHint[i] += "[Info:Delete]";
					continue;
				}
			}
			// Check Cross With
			// EachOther----------------------------------------------------------
			if (getLineDatabase().GetIndexPermission() != null)
				getLineDatabase().GetIndexPermission().WriteBack();
			Database.RTreeIndex LineRTree = new Database.RTreeIndex();
			LineRTree.IndexInit(getLineDatabase());
			LineNum = getLineDatabase().LineNum;
			for (int i = 0; i < LineNum; i++) {
				if (i == 6729) {
					System.out.println("6729");
				}
				if (i == 6730) {
					System.out.println("6730");
				}
				res = LineRTree.Search(LineDatabase.GetMBRX1(i),
						LineDatabase.GetMBRY1(i), LineDatabase.GetMBRX2(i),
						LineDatabase.GetMBRY2(i));
				while (!res.isEmpty()) {
					if (LineDatabase.CheckCross(i, res.get(0)))
						break;
					res.remove(0);
				}
				if (!res.isEmpty()) {
					LineDatabase.LineHint[i] += "[Info:Delete]";
					continue;
				}
			}
			// -------------------------------------------------------------------------------------
			getLineDatabase().SetIndexPermission(null);
			LineRTree = null;
			getPolygonDatabase().SetIndexPermission(null);
			PolygonRTree = null;
			System.gc();
			JOptionPane.showMessageDialog(null, "Finished!");
		}

		// [ACMCUP2014]This function is to clone the points lines and polygons
		// in several rows and columns
		public void MultipleData(int row, int col) {
			int PointNum = PointDatabase.PointNum;
			int LineNum = LineDatabase.LineNum;
			int PolygonNum = PolygonDatabase.PolygonNum;
			double[] tempx = new double[10000];
			double[] tempy = new double[10000];
			int temp, tot;
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					ChangeTitle("=============>>>>>>>>>>>>>Clone On Row[" + i
							+ "]/Col[" + j + "]");
					if ((i == 0) && (j == 0))
						continue;
					for (int k = 0; k < PointNum; k++) {
						// Clone the
						// Points---------------------------------------------------------------
						PointDatabase.add(PointDatabase.AllPointX[k] + j
								* (LongitudeEnd - LongitudeStart),
								PointDatabase.AllPointY[k] + i
										* (LatitudeEnd - LatitudeStart),
								PointDatabase.PointVisible[k],
								PointDatabase.PointHint[k]);
					}
					for (int k = 0; k < LineNum; k++) {
						// Clone the
						// Lines-----------------------------------------------------------------
						temp = LineDatabase.LineHead[k];
						tot = 0;
						while (temp != -1) {
							tempx[tot] = LineDatabase.AllPointX[temp] + j
									* (LongitudeEnd - LongitudeStart);
							tempy[tot] = LineDatabase.AllPointY[temp] + i
									* (LatitudeEnd - LatitudeStart);
							tot++;
							temp = LineDatabase.AllPointNext[temp];
						}
						LineDatabase.add(tempx, tempy, tot,
								LineDatabase.LineHint[k]);
					}
					for (int k = 0; k < PolygonNum; k++) {
						// Clone the
						// Polygons-----------------------------------------------------------------
						temp = PolygonDatabase.PolygonHead[k];
						tot = 0;
						while (temp != -1) {
							tempx[tot] = PolygonDatabase.AllPointX[temp] + j
									* (LongitudeEnd - LongitudeStart);
							tempy[tot] = PolygonDatabase.AllPointY[temp] + i
									* (LatitudeEnd - LatitudeStart);
							tot++;
							temp = PolygonDatabase.AllPointNext[temp];
						}
						PolygonDatabase.add(tempx, tempy, tot,
								PolygonDatabase.PolygonHint[k]);
					}
				}
			}
			String Info = "[LongitudeStart]" + LongitudeStart + "\n";
			Info += "[LongitudeEnd]"
					+ (LongitudeStart + col * (LongitudeEnd - LongitudeStart))
					+ "\n";
			Info += "[LatitudeStart]" + LatitudeStart + "\n";
			Info += "[LatitudeEnd]"
					+ (LatitudeStart + row * (LatitudeEnd - LatitudeStart))
					+ "\n";
			Preference.bulletin.setText(Info);
			Screen.setVisible(false);
			JOptionPane.showMessageDialog(null, Info);
		}

		public FreeWizard.GlobalPreferenceWizard getPreference() {
			return Preference;
		}

		public int getSecond() {
			return ClockWizard.pic.getHour() * 3600
					+ ClockWizard.pic.getMinute() * 60
					+ ClockWizard.pic.getSecond();
		}

		public double AccurateMeterDistance(double x0, double y0, double x1,
				double y1) {
			double alpha = Math.cos((y1 + y0) / 360.0 * Math.PI);
			double dx = alpha * (x0 - x1) / 180.0 * Math.PI * 6371 * 1000;
			double dy = (y0 - y1) / 180.0 * Math.PI * 6371 * 1000;
			return Math.sqrt(dx * dx + dy * dy);
		}

		public void ShowTextArea1(String Content, boolean BackGround) {
			Screen.IsTextArea1Visible = true;
			Screen.TextArea1Content = Content;
			Screen.IsTextArea1BackGround = BackGround;
			ScreenFlush();
		}

		public void ShowTextArea2(String Content, boolean BackGround) {
			Screen.IsTextArea2Visible = true;
			Screen.TextArea2Content = Content;
			Screen.IsTextArea2BackGround = BackGround;
			ScreenFlush();
		}

		public void VeilTextArea1() {
			Screen.IsTextArea1Visible = false;
			ScreenFlush();
		}

		public void VeilTextArea2() {
			Screen.IsTextArea2Visible = false;
			ScreenFlush();
		}

		public void ChangeTitle(String temp) {
			ReTitle(temp);
		}

		public void ScreenFlush() {
			Screen.repaint();
		}

		public void ScreenLock(boolean isLock) {
			Screen.setLock(isLock);
		}

		public boolean IsScreenLock() {
			return Screen.lock;
		}

		public void PointPush(double x, double y) {
			Screen.ExtendedPointPush(x, y);
			ScreenFlush();
		}

		public void PointPush(double x, double y, String Hint) {
			Screen.ExtendedPointPush(x, y, Hint);
			ScreenFlush();
		}

		public void PointPop() {
			Screen.ExtendedPointPop();
			ScreenFlush();
		}

		public void PointEmpty() {
			Screen.ExtendedPointEmpty();
			ScreenFlush();
		}

		public void PointSelectDelete() {
			Screen.ExtendedPointSelectDelete();
			ScreenFlush();
		}

		@Override
		public void setPointVisible(boolean visible) {
			// TODO Auto-generated method stub
			Screen.IsExtendedPointVisible = visible;
			ScreenFlush();
		}

		@Override
		public void setPointHintVisible(boolean visible) {
			// TODO Auto-generated method stub
			Screen.IsExtendedPointHintVisible = visible;
			ScreenFlush();
		}

		@Override
		public void setPointConsecutiveLinkVisible(boolean visible) {
			// TODO Auto-generated method stub
			Screen.IsConsecutiveLink = visible;
			ScreenFlush();
		}

		@Override
		public void setPointHeadTailLinkVisible(boolean visible) {
			// TODO Auto-generated method stub
			Screen.IsHeadTailLink = visible;
			ScreenFlush();
		}

		@Override
		public boolean getPointVisible() {
			// TODO Auto-generated method stub
			return Screen.IsExtendedPointVisible;
		}

		@Override
		public boolean getPointHintVisible() {
			// TODO Auto-generated method stub
			return Screen.IsExtendedPointHintVisible;
		}

		@Override
		public boolean getPointConsecutiveVisible() {
			// TODO Auto-generated method stub
			return Screen.IsConsecutiveLink;
		}

		@Override
		public boolean getPointHeadTailLinkVisible() {
			// TODO Auto-generated method stub
			return Screen.IsHeadTailLink;
		}

		public int getPointCount() {
			return Screen.ExtendedPointCount;
		}

		public void PointSelect(double x1, double y1, double x2, double y2) {
			Screen.ExtendedPointSelect(x1, y1, x2, y2);
			ScreenFlush();
		}

		public void PointSelectCancel() {
			Screen.ExtendedPointSelectCancel();
			ScreenFlush();
		}

		public boolean AccurateInsideRectangle(double x0, double y0, double x1,
				double y1, double x2, double y2) {
			if (x0 < Math.min(x1, x2))
				return false;
			if (x0 > Math.max(x1, x2))
				return false;
			if (y0 < Math.min(y1, y2))
				return false;
			if (y0 > Math.max(y1, y2))
				return false;
			return true;
		}

		public boolean InsideStretchRegion(double Stretch, double x0,
				double y0, double x1, double y1, double x2, double y2) {
			if (x0 < Math.min(x1, x2) - Stretch)
				return false;
			if (x0 > Math.max(x1, x2) + Stretch)
				return false;
			if (y0 < Math.min(y1, y2) - Stretch)
				return false;
			if (y0 > Math.max(y1, y2) + Stretch)
				return false;
			return true;
		}

		public void ResetPointHint(int k, String Hint) {
			Screen.ExtendedPointReHint(k, Hint);
			ScreenFlush();
		}

		public void ResetPointHint(String Prefix) {
			Screen.ExtendedPointReHint(Prefix);
			ScreenFlush();
		}

		public boolean IsDataLoaded() {
			return DIR != null;
		}

		public ToolPanel getNowPanel() {
			return NowPanel;
		}

		public boolean IsPolygonLoaded() {
			return PolygonDatabaseFile != null;
		}

		public void ForbidOperate() {
			ToolCard.show(Tool, "NULL");
			menubar.setVisible(false);
			ShrinkALittle();
		}

		public void AllowOperate() {
			ToolCard.show(Tool, NowPanel.getString());
			menubar.setVisible(true);
			RecoverSize();
		}

		public void PolygonDatabaseAppend(String Hint) {
			PolygonDatabase.add(Screen.ExtendedPointX, Screen.ExtendedPointY,
					Screen.ExtendedPointCount, Hint);
		}

		public void LineDatabaseAppend(String Hint) {
			LineDatabase.add(Screen.ExtendedPointX, Screen.ExtendedPointY,
					Screen.ExtendedPointCount, Hint);
		}

		public void PointDatabaseAppend(String Hint) {
			for (int i = 0; i < Screen.ExtendedPointCount; i++)
				PointDatabase.add(Screen.ExtendedPointX[i],
						Screen.ExtendedPointY[i], Hint);
		}

		public Database.PolygonDataSet getPolygonDatabase() {
			return PolygonDatabase;
		}

		public void setKernel(MapWizard obj) {
			kernel = obj;
		}

		public MapWizard getKernel() {
			return kernel;
		}

		private MapWizard kernel = null;

		@Override
		public boolean IsLineLoaded() {
			// TODO Auto-generated method stub
			return LineDatabaseFile != null;
		}

		@Override
		public LineDataSet getLineDatabase() {
			// TODO Auto-generated method stub
			return LineDatabase;
		}

		@Override
		public boolean IsPointLoaded() {
			// TODO Auto-generated method stub
			return PointDatabaseFile != null;
		}

		@Override
		public PointDataSet getPointDatabase() {
			// TODO Auto-generated method stub
			return PointDatabase;
		}

		@Override
		public double AccurateDistance(double x0, double y0, double x1,
				double y1) {
			// TODO Auto-generated method stub
			return Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
		}

		@Override
		public double getLongitudeStart() {
			// TODO Auto-generated method stub
			return LongitudeStart;
		}

		@Override
		public double getLongitudeEnd() {
			// TODO Auto-generated method stub
			return LongitudeEnd;
		}

		@Override
		public double getLatitudeStart() {
			// TODO Auto-generated method stub
			return LatitudeStart;
		}

		@Override
		public double getLatitudeEnd() {
			// TODO Auto-generated method stub
			return LatitudeEnd;
		}
	}

	public int Ox(String str) {
		int ans = 0;
		for (int i = 0; i < str.length(); i++) {
			ans = ans * 2 + (str.charAt(i) == '1' ? 1 : 0);
		}
		return ans;
	}

	public Color getChooseColor(int k) {
		if (k == 0)
			return Color.white;
		else if (k == 1)
			return Color.red;
		else if (k == 2)
			return Color.yellow;
		else if (k == 3)
			return Color.blue;
		else if (k == 4)
			return Color.green;
		else if (k == 5)
			return Color.cyan;
		else if (k == 6)
			return Color.pink;
		else if (k == 7)
			return Color.orange;
		else
			return Color.white;
	}

	public void ReTitle(String temp) {
		this.setTitle("CityGeoInfo[" + temp + "]");
	}

	public void WriteBack() {
		GPSPoints.LandMarkSave(LandMarkFile);
		PolygonDatabase.DatabaseFileOutput(PolygonDatabaseFile);
		LineDatabase.DatabaseFileOutput(LineDatabaseFile);
		PointDatabase.DatabaseFileOutput(PointDatabaseFile);
	}

	public void ExecuteExit() {
		if (DIR == null) {
			dispose();
			System.exit(0);
		}
		int n = JOptionPane.showConfirmDialog(null,
				LanguageDic.GetWords("离开之前是否使所有更改生效，是则缓存写回数据库，否则放弃所有更改"),
				LanguageDic.GetWords("数据完整性提示"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			WriteBack();
		} else if (n == JOptionPane.CANCEL_OPTION) {
			return;
		}
		dispose();
		System.exit(0);
	}

	public void ChangeDirPrompt() {
		int n = JOptionPane.showConfirmDialog(null,
				LanguageDic.GetWords("改换目录前是否使所有更改生效，是则缓存写回数据库，否则放弃所有更改"),
				LanguageDic.GetWords("数据完整性提示"), JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			WriteBack();
		}
	}

	public void ShrinkALittle() {
		this.setSize(1005, 708);
	}

	public void RecoverSize() {
		this.setSize(1005, 733);
	}

	private double AREALEFT = 0;
	private double AREARIGHT = 0;
	private double AREAUP = 0;
	private double AREADOWN = 0;

	public void SetAREA(double x1, double x2, double y1, double y2) {
		AREALEFT = Math.min(x1, x2);
		AREARIGHT = Math.max(x1, x2);
		AREADOWN = Math.min(y1, y2);
		AREAUP = Math.max(y1, y2);
	}

	public boolean CheckInAREA(double x, double y) {
		if (x < AREALEFT)
			return false;
		if (x > AREARIGHT)
			return false;
		if (y > AREAUP)
			return false;
		if (y < AREADOWN)
			return false;
		return true;
	}

	public boolean CheckInAREA(double x1, double y1, double x2, double y2) {
		if (Math.max(Math.min(x1, x2), AREALEFT) > Math.min(Math.max(x1, x2),
				AREARIGHT))
			return false;
		if (Math.max(Math.min(y1, y2), AREADOWN) > Math.min(Math.max(y1, y2),
				AREAUP))
			return false;
		return true;
	}

	public void ScreenPNGOutput() {
		String str = JOptionPane.showInputDialog(null,
				LanguageDic.GetWords("请输出文件名前缀，不能为空"),
				LanguageDic.GetWords("捕捉当前屏幕为PNG"), JOptionPane.PLAIN_MESSAGE);
		if ((str == null) || (str.equals(""))) {
			ReTitle(LanguageDic.GetWords("放弃了PNG生成"));
			return;
		}
		// ----------------------------------------------------------------------------------------
		try {
			BufferedImage PNGimage = new BufferedImage(Screen.ScreenWidth,
					Screen.ScreenHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g_2d = PNGimage.createGraphics();
			// -----------------------------------------------
			Screen.paint(g_2d);
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			ImageIO.write(PNGimage, "png",
					new File(str + "[" + df.format(new Date()) + "]" + ".png"));
			// -----------------------------------------------
			System.gc();
			ReTitle(LanguageDic.GetWords("PNG生成成功"));
		} catch (Exception ee) {
			ReTitle("OutErr!!!");
		}
	}

	public void ScreenPNGOutput(String FilePath) {
		// ----------------------------------------------------------------------------------------
		try {
			BufferedImage PNGimage = new BufferedImage(Screen.ScreenWidth,
					Screen.ScreenHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g_2d = PNGimage.createGraphics();
			// -----------------------------------------------
			Screen.paint(g_2d);
			ImageIO.write(PNGimage, "png", new File(FilePath));
			// -----------------------------------------------
			System.gc();
		} catch (Exception ee) {
			System.err.println("OutErr====>" + FilePath);
		}
	}

	public BufferedImage JPGOutput(double ScreenLongitudeStart,
			double ScreenLongitudeEnd, double ScreenLatitudeStart,
			double ScreenLatitudeEnd, int JPGWidth, int JPGHeight, int bold,
			String FileName) {
			BufferedImage JPGimage;
		// ----------------------------------------------------------------------------------------
		try {
			SetAREA(ScreenLongitudeStart, ScreenLongitudeEnd,
					ScreenLatitudeStart, ScreenLatitudeEnd);
			System.gc();
			JPGimage = new BufferedImage(JPGWidth, JPGHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g_2d = JPGimage.createGraphics();
			// -----------------------------------------------
			// 将需要显示的经纬度范围转化为窗口界面中像素值
			double LongitudeScale = ScreenLongitudeEnd - ScreenLongitudeStart;
			double LatitudeScale = ScreenLatitudeEnd - ScreenLatitudeStart;
			double ScreenLongitude = ScreenLongitudeStart;
			double ScreenLatitude = ScreenLatitudeEnd;
			int ScreenWidth = JPGWidth;
			int ScreenHeight = JPGHeight;
			double tempsize;
			if (Screen.image == null) {
				setBackground(Color.black);
			} else if (Screen.ShowBackGround) {
//-------------------------------------------------------------------------
				double MoveX = 0;
				double MoveY = 0;
				if (BackGroundMoveVectorNum != 0) {
					for (int i = 0; i < BackGroundMoveVectorNum; i++) {
						MoveX+=BackGroundMoveDx[i];
						MoveY+=BackGroundMoveDy[i];	
					}
					MoveX/=BackGroundMoveVectorNum;
					MoveY/=BackGroundMoveVectorNum;
				}
//-------------------------------------------------------------------------
				double Xst = ((ScreenLongitude - MoveX) - LongitudeStart)
						/ (LongitudeEnd - LongitudeStart)
						* Screen.image.getWidth(this);
				double Yst = (LatitudeEnd - (ScreenLatitude - MoveY))
						/ (LatitudeEnd - LatitudeStart)
						* Screen.image.getHeight(this);
				double Xlen = (LongitudeScale)
						/ (LongitudeEnd - LongitudeStart)
						* Screen.image.getWidth(this);
				double Ylen = (LatitudeScale) / (LatitudeEnd - LatitudeStart)
						* Screen.image.getHeight(this);
				// 将需要显示的经纬度范围转化为窗口界面中像素值
				g_2d.drawImage(Screen.image, 0, 0, ScreenWidth, ScreenHeight,
						(int) Xst, (int) Yst, (int) (Xst + Xlen),
						(int) (Yst + Ylen), this);
			}
			BasicStroke bs = new BasicStroke(2, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			g_2d.setStroke(bs);
			g_2d.setColor(Color.green);
			// Draw for Extended Database--------------------------
			if (LineDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				Line2D line;
				tempsize = 0.2 - LongitudeScale
						/ (LongitudeEnd - LongitudeStart);
				tempsize = tempsize <= 0 ? 0 : tempsize;
				g_2d.setFont(new Font("黑体", 0, bold + 10));
				for (int i = 0; i < LineDatabase.LineNum; i++) {
					binary = LineDatabase.LineVisible[i];
					if ((binary & Ox("1")) != 0) {
						if (((!ShowVisualFeature)&&((binary & Ox("1000")) != 0))
								||(ShowVisualFeature&&(LineDatabase.LineHint[i].indexOf("[LineVisible:]")!=-1)))
							{// For
																// Line----------------------------							
							choose = (binary >> 10) & Ox("111");
							g_2d.setColor(getChooseColor(choose));

							BasicStroke bs_temp = null;
							Color color_temp = null;
							if (ShowVisualFeature) {
								// bs_temp=new BasicStroke(1.0f,
								// BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
								// 10.0f, dash, 0);
								bs_temp = Screen
										.GetVisualLineStroke(LineDatabase.LineHint[i]);
								g_2d.setStroke(bs_temp);
								color_temp = g_2d.getColor();
								g_2d.setColor(Screen.GetVisualColor(
										LineDatabase.LineHint[i], "Line"));
							}

							now = LineDatabase.LineHead[i];
							p1 = now;

							while (true) {
								p2 = LineDatabase.AllPointNext[p1];
								if (p2 == -1)
									break;
								if (!CheckInAREA(LineDatabase.AllPointX[p1],
										LineDatabase.AllPointY[p1],
										LineDatabase.AllPointX[p2],
										LineDatabase.AllPointY[p2])) {
									p1 = p2;
									continue;
								}
								int x1 = (int) ((LineDatabase.AllPointX[p1] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int y1 = (int) ((ScreenLatitude - LineDatabase.AllPointY[p1])
										/ LatitudeScale * ScreenHeight);
								int x2 = (int) ((LineDatabase.AllPointX[p2] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int y2 = (int) ((ScreenLatitude - LineDatabase.AllPointY[p2])
										/ LatitudeScale * ScreenHeight);
								line = new Line2D.Double(x1, y1, x2, y2);
								if (ShowVisualFeature)
									g_2d.setStroke(bs_temp);
								g_2d.draw(line);
								if ((ShowVisualFeature)
										&& (Screen
												.GetVisualArrow(LineDatabase.LineHint[i]))) {
									g_2d.setStroke(bs);
									g_2d.draw(new Line2D.Double(
											(double) x2,
											(double) y2,
											x2
													+ 0.2
													* (0.87 * (x1 - x2) - (y1 - y2) * 0.34),
											y2
													+ 0.2
													* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))));
									g_2d.draw(new Line2D.Double(
											(double) x2,
											(double) y2,
											x2
													+ 0.2
													* (0.87 * (x1 - x2) + (y1 - y2) * 0.34),
											y2
													+ 0.2
													* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))));
								}

								p1 = p2;
							}

							if (ShowVisualFeature) {
								g_2d.setColor(color_temp);
								g_2d.setStroke(bs);
							}
						}
						if ( ((!ShowVisualFeature)&&((binary & Ox("100")) != 0))
								||(ShowVisualFeature&&(LineDatabase.LineHint[i].indexOf("[PointVisible:]")!=-1))
								) {// For
														// Point-------------------------
							choose = (binary >> 7) & Ox("111");
							g_2d.setColor(getChooseColor(choose));

							Color color_temp = null;
							int PointSize = 6;
							if (ShowVisualFeature) {
								color_temp = g_2d.getColor();
								g_2d.setColor(Screen.GetVisualColor(
										LineDatabase.LineHint[i], "Point"));
							}

							now = LineDatabase.LineHead[i];
							while (now != -1) {
								if (!CheckInAREA(LineDatabase.AllPointX[now],
										LineDatabase.AllPointY[now])) {
									now = LineDatabase.AllPointNext[now];
									continue;
								}
								int xx = (int) ((LineDatabase.AllPointX[now] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int yy = (int) ((ScreenLatitude - LineDatabase.AllPointY[now])
										/ LatitudeScale * ScreenHeight);
								if (ShowVisualFeature)
									PointSize = Screen
											.GetVisualPointSize(LineDatabase.LineHint[i]);
								else
									PointSize = 6;
								g_2d.fillOval(xx - PointSize / 2, yy
										- PointSize / 2, PointSize, PointSize);
								now = LineDatabase.AllPointNext[now];
							}

						}
						if (
								((!ShowVisualFeature)&&((binary & Ox("10")) != 0))
								||
								(ShowVisualFeature&&(LineDatabase.LineHint[i].indexOf("[WordVisible:]")!=-1))
							){// For
														// Word--------------------------
						choose = (binary >> 4) & Ox("111");
							g_2d.setColor(getChooseColor(choose));
							now = LineDatabase.LineHead[i];
							if (!CheckInAREA(LineDatabase.AllPointX[now]
									- LongitudeScale * 0.1,
									LineDatabase.AllPointY[now] - LatitudeScale
											* 0.1, LineDatabase.AllPointX[now]
											+ LongitudeScale * 0.1,
									LineDatabase.AllPointY[now] + LatitudeScale
											* 0.1)) {
								continue;
							}
							int xx, yy;
							if (IsAlignLinesTag) {
								String str = LineDatabase.getTitle(i);
								Screen.setAlignTagVector(
										LineDatabase,
										str,
										now,
										(ScreenWidth / 2 / LongitudeScale)
												+ (ScreenHeight / 2 / LatitudeScale));
								for (int ii = 0; ii < str.length(); ii++) {
									xx = (int) ((Screen.AlignPosX[ii] - ScreenLongitude)
											/ LongitudeScale * ScreenWidth);
									yy = (int) ((ScreenLatitude - Screen.AlignPosY[ii])
											/ LatitudeScale * ScreenHeight);
									g_2d.drawString(str.substring(ii, ii + 1),
											xx, yy);
								}
								continue;
							}
							xx = (int) ((LineDatabase.AllPointX[now]
									+ LineDatabase.dx[i] - ScreenLongitude)
									/ LongitudeScale * ScreenWidth);
							yy = (int) ((ScreenLatitude
									- LineDatabase.AllPointY[now] - LineDatabase.dy[i])
									/ LatitudeScale * ScreenHeight);
							if (LineDatabase.isVertical[i] == false)
								g_2d.drawString(LineDatabase.getTitle(i), xx,
										yy);
							else {
								String str = LineDatabase.getTitle(i);
								for (int ii = 0; ii < str.length(); ii++) {
									g_2d.drawString(str.substring(ii, ii + 1),
											xx, yy + (bold + 12) * ii);
								}
							}
						}
					}
				}
			}
			if (PolygonDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				Line2D line;
				tempsize = 0.20 - LongitudeScale
						/ (LongitudeEnd - LongitudeStart);
				tempsize = tempsize <= 0 ? 0 : tempsize;
				g_2d.setFont(new Font("黑体", 0, bold + 10));
				for (int i = 0; i < PolygonDatabase.PolygonNum; i++) {
					binary = PolygonDatabase.PolygonVisible[i];
					if ((binary & Ox("1")) != 0) {
						if (
								((!ShowVisualFeature)&&((binary & Ox("1000")) != 0))
								||
								(ShowVisualFeature&&(PolygonDatabase.PolygonHint[i].indexOf("[LineVisible:]")!=-1))
							){// For
															// Line----------------------------
							choose = (binary >> 10) & Ox("111");
							g_2d.setColor(getChooseColor(choose));

							java.awt.Polygon ColorPolygon = new java.awt.Polygon();
							BasicStroke bs_temp = null;
							Color color_default = null;
							Color color_Line = null;
							Color color_Polygon = null;
							if (ShowVisualFeature) {
								// bs_temp=new BasicStroke(1.0f,
								// BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
								// 10.0f, dash, 0);
								bs_temp = Screen
										.GetVisualLineStroke(PolygonDatabase.PolygonHint[i]);
								g_2d.setStroke(bs_temp);
								color_default = g_2d.getColor();
								color_Line = Screen.GetVisualColor(
										PolygonDatabase.PolygonHint[i], "Line");
								color_Polygon = Screen.GetVisualColor(
										PolygonDatabase.PolygonHint[i],
										"Polygon");
								g_2d.setColor(color_Line);
							}

							now = PolygonDatabase.PolygonHead[i];
							p1 = now;

							while (true) {
								p2 = PolygonDatabase.AllPointNext[p1];
								if (p2 == -1)
									p2 = now;
								if (!CheckInAREA(PolygonDatabase.AllPointX[p1],
										PolygonDatabase.AllPointY[p1],
										PolygonDatabase.AllPointX[p2],
										PolygonDatabase.AllPointY[p2])) {
									p1 = p2;
									if (p1 == now)
										break;
									continue;
								}
								int x1 = (int) ((PolygonDatabase.AllPointX[p1] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int y1 = (int) ((ScreenLatitude - PolygonDatabase.AllPointY[p1])
										/ LatitudeScale * ScreenHeight);
								int x2 = (int) ((PolygonDatabase.AllPointX[p2] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int y2 = (int) ((ScreenLatitude - PolygonDatabase.AllPointY[p2])
										/ LatitudeScale * ScreenHeight);
								line = new Line2D.Double(x1, y1, x2, y2);
								ColorPolygon.addPoint(x1, y1);
								if (ShowVisualFeature)
									g_2d.setStroke(bs_temp);
								g_2d.draw(line);
								if ((ShowVisualFeature)
										&& (Screen
												.GetVisualArrow(PolygonDatabase.PolygonHint[i]))) {
									g_2d.setStroke(bs);
									g_2d.draw(new Line2D.Double(
											(double) x2,
											(double) y2,
											x2
													+ 0.2
													* (0.87 * (x1 - x2) - (y1 - y2) * 0.34),
											y2
													+ 0.2
													* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))));
									g_2d.draw(new Line2D.Double(
											(double) x2,
											(double) y2,
											x2
													+ 0.2
													* (0.87 * (x1 - x2) + (y1 - y2) * 0.34),
											y2
													+ 0.2
													* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))));
								}
								p1 = p2;
								if (p1 == now)
									break;
							}

							if (ShowVisualFeature) {
								g_2d.setColor(color_Polygon);
								if(PolygonDatabase.PolygonHint[i].indexOf("[PolygonVisible:]")!=-1)
									g_2d.fillPolygon(ColorPolygon);
								g_2d.setColor(color_default);
								g_2d.setStroke(bs);
							}
						}
						if (
								((!ShowVisualFeature)&&((binary & Ox("100")) != 0))
								||
								(ShowVisualFeature&&(PolygonDatabase.PolygonHint[i].indexOf("[PointVisible:]")!=-1))
							){// For
														// Point-------------------------
							choose = (binary >> 7) & Ox("111");
							g_2d.setColor(getChooseColor(choose));

							Color color_temp = null;
							int PointSize = 6;
							if (ShowVisualFeature) {
								color_temp = g_2d.getColor();
								g_2d.setColor(Screen
										.GetVisualColor(
												PolygonDatabase.PolygonHint[i],
												"Point"));
							}

							now = PolygonDatabase.PolygonHead[i];
							while (now != -1) {
								if (!CheckInAREA(
										PolygonDatabase.AllPointX[now],
										PolygonDatabase.AllPointY[now])) {
									now = PolygonDatabase.AllPointNext[now];
									continue;
								}
								int xx = (int) ((PolygonDatabase.AllPointX[now] - ScreenLongitude)
										/ LongitudeScale * ScreenWidth);
								int yy = (int) ((ScreenLatitude - PolygonDatabase.AllPointY[now])
										/ LatitudeScale * ScreenHeight);
								if (ShowVisualFeature)
									PointSize = Screen
											.GetVisualPointSize(PolygonDatabase.PolygonHint[i]);
								else
									PointSize = 6;
								g_2d.fillOval(xx - PointSize / 2, yy
										- PointSize / 2, PointSize, PointSize);
								now = PolygonDatabase.AllPointNext[now];
							}

						}
						if (
								((!ShowVisualFeature)&&((binary & Ox("10")) != 0))
								||
								(ShowVisualFeature&&(PolygonDatabase.PolygonHint[i].indexOf("[WordVisible:]")!=-1))
							){// For
														// Word--------------------------
							choose = (binary >> 4) & Ox("111");
							g_2d.setColor(getChooseColor(choose));
							now = PolygonDatabase.PolygonHead[i];
							if (!CheckInAREA(PolygonDatabase.AllPointX[now]
									- LongitudeScale * 0.1,
									PolygonDatabase.AllPointY[now]
											- LatitudeScale * 0.1,
									PolygonDatabase.AllPointX[now]
											+ LongitudeScale * 0.1,
									PolygonDatabase.AllPointY[now]
											+ LatitudeScale * 0.1)) {
								continue;
							}
							int xx, yy;
							if (IsAlignPolygonsTag) {
								String str = PolygonDatabase.getTitle(i);
								Screen.setCenterTagVector(PolygonDatabase, str,
										now, ScreenWidth / LongitudeScale);
								for (int ii = 0; ii < str.length(); ii++) {
									xx = (int) ((Screen.CenterPosX[ii] - ScreenLongitude)
											/ LongitudeScale * ScreenWidth);
									yy = (int) ((ScreenLatitude - Screen.CenterPosY[ii])
											/ LatitudeScale * ScreenHeight);
									g_2d.drawString(str.substring(ii, ii + 1),
											xx, yy);
								}
								continue;
							}
							xx = (int) ((PolygonDatabase.AllPointX[now]
									+ PolygonDatabase.dx[i] - ScreenLongitude)
									/ LongitudeScale * ScreenWidth);
							yy = (int) ((ScreenLatitude
									- PolygonDatabase.AllPointY[now] - PolygonDatabase.dy[i])
									/ LatitudeScale * ScreenHeight);
							if (PolygonDatabase.isVertical[i] == false)
								g_2d.drawString(PolygonDatabase.getTitle(i),
										xx, yy);
							else {
								String str = PolygonDatabase.getTitle(i);
								for (int ii = 0; ii < str.length(); ii++) {
									g_2d.drawString(str.substring(ii, ii + 1),
											xx, yy + (bold + 12) * ii);
								}
							}
						}
					}
				}
			}
			if (PointDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				Point2D Point;
				tempsize = 0.2 - LongitudeScale
						/ (LongitudeEnd - LongitudeStart);
				tempsize = tempsize <= 0 ? 0 : tempsize;
				g_2d.setFont(new Font("黑体", 0, bold + 10));
				for (int i = 0; i < PointDatabase.PointNum; i++) {
					binary = PointDatabase.PointVisible[i];
					if ((binary & Ox("1")) != 0) {
						if (!CheckInAREA(PointDatabase.AllPointX[i],
								PointDatabase.AllPointY[i]))
							continue;
						if (
								((!ShowVisualFeature)&&((binary & Ox("100")) != 0))
								||
								(ShowVisualFeature&&(PointDatabase.PointHint[i].indexOf("[PointVisible:]")!=-1))
							){// For
														// Point-------------------------
							choose = (binary >> 7) & Ox("111");
							g_2d.setColor(getChooseColor(choose));

							Color color_temp = null;
							int PointSize = 6;
							if (ShowVisualFeature) {
								color_temp = g_2d.getColor();
								g_2d.setColor(Screen.GetVisualColor(
										PointDatabase.PointHint[i], "Point"));
							}

							int xx = (int) ((PointDatabase.AllPointX[i] - ScreenLongitude)
									/ LongitudeScale * ScreenWidth);
							int yy = (int) ((ScreenLatitude - PointDatabase.AllPointY[i])
									/ LatitudeScale * ScreenHeight);
							if (!IsEngravePointShape) {
								if (ShowVisualFeature)
									PointSize = Screen
											.GetVisualPointSize(PointDatabase.PointHint[i]);
								else
									PointSize = 6;
								g_2d.fillOval(xx - PointSize / 2, yy
										- PointSize / 2, PointSize, PointSize);
							} else
								g_2d.drawRect(xx, yy, 0, 0);
						}
						if (
								((!ShowVisualFeature)&&((binary & Ox("10")) != 0))
								||
								(ShowVisualFeature&&(PointDatabase.PointHint[i].indexOf("[WordVisible:]")!=-1))
								) {// For
														// Word--------------------------
							choose = (binary >> 4) & Ox("111");
							g_2d.setColor(getChooseColor(choose));
							int xx = (int) ((PointDatabase.AllPointX[i] - ScreenLongitude)
									/ LongitudeScale * ScreenWidth);
							int yy = (int) ((ScreenLatitude - PointDatabase.AllPointY[i])
									/ LatitudeScale * ScreenHeight);
							g_2d.drawString(PointDatabase.getTitle(i), xx + 3,
									yy + 3);
						}
					}
				}
			}
			// -----------------------------------------------
			FileOutputStream out = new FileOutputStream(FileName + ".jpg");
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(JPGimage);
			out.close();
			System.gc();
			ReTitle(LanguageDic.GetWords("JPG生成成功"));
		} catch (Exception ee) {
			ReTitle("OutErr!!!");
			return null;
		}
		return JPGimage;
	}

	public void JPGOutput() {
		if (DIR == null)
			return;
		String str = JOptionPane.showInputDialog(null,
				LanguageDic.GetWords("请以下列各式输入信息：宽度;高度;粗度"),
				LanguageDic.GetWords("JPG生成在Image目录下"),
				JOptionPane.PLAIN_MESSAGE);
		if ((str == null) || (str.equals(""))) {
			ReTitle(LanguageDic.GetWords("放弃了JPG生成"));
			return;
		}
		ReTitle(LanguageDic.GetWords("开始生成JPG"));
		String s1 = str.substring(0, str.indexOf(';'));
		String s2 = str.substring(str.indexOf(';') + 1);
		String s3 = s2.substring(s2.indexOf(';') + 1);
		s2 = s2.substring(0, s2.indexOf(';'));
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
				"yyyy-MM-dd-HH-mm-ss");
		JPGOutput(LongitudeStart, LongitudeEnd, LatitudeStart, LatitudeEnd,
				Integer.parseInt(s1), Integer.parseInt(s2),
				Integer.parseInt(s3),
				(new File(ImageDir, "[" + df.format(new Date()) + "]"))
						.toString());
	}

	public void JPGSplitOutput(String Prefix,int bold,boolean Split){
		if (DIR == null)
			return;
		if((Prefix==null)||Prefix.isEmpty()){
			Prefix = JOptionPane.showInputDialog(null,
					"Please Input Traget Directory Path",
					"PrefixPath",
					JOptionPane.PLAIN_MESSAGE);
			if ((Prefix == null) || (Prefix.equals(""))) {
					ReTitle(LanguageDic.GetWords("放弃了JPG生成"));
					return;
			}
		}
		if(bold<=0){
			bold=Integer.parseInt(JOptionPane.showInputDialog(null,
							"Please Input Font Bold Number",
							"FontBold",
							JOptionPane.PLAIN_MESSAGE));
		}
		File PrefixDir=new File(Prefix);
		PrefixDir.mkdirs();
		//================================================
		System.out.println("EntireMapDrawing");
		BufferedImage ImageData;
		try{
			//ImageData=this.getToolkit().createImage(JPGOutput(LongitudeStart, LongitudeEnd, LatitudeStart, LatitudeEnd,
			//20000, 20000, bold,Prefix+"\\Overall.jpg"));
			ImageData=JPGOutput(LongitudeStart, LongitudeEnd, LatitudeStart, LatitudeEnd,
					20000, 20000, bold,Prefix+"\\Overall.jpg");
			if(ImageData==null) return;
		}catch(Exception ex){
			ex.printStackTrace();return;
		}
		if(!Split) return;
		//================================================
		File Dir1=new File(PrefixDir,"1");
		Dir1.mkdirs();
		File Dir2=new File(PrefixDir,"2");
		Dir2.mkdirs();
		File Dir3=new File(PrefixDir,"3");
		Dir3.mkdirs();
		File Dir4=new File(PrefixDir,"4");
		Dir4.mkdirs();
		File Dir5=new File(PrefixDir,"5");
		Dir5.mkdirs();
		File Dir6=new File(PrefixDir,"6");
		Dir6.mkdirs();
		File Dir7=new File(PrefixDir,"7");
		Dir7.mkdirs();
		File Dir8=new File(PrefixDir,"8");
		Dir8.mkdirs();
		JPGMatrixOutput(1,1,256,256,Dir8,ImageData);
		File Dir9=new File(PrefixDir,"9");
		Dir9.mkdirs();
		JPGMatrixOutput(2,2,256,256,Dir9,ImageData);
		File Dir10=new File(PrefixDir,"10");
		Dir10.mkdirs();
		JPGMatrixOutput(4,4,256,256,Dir10,ImageData);
		File Dir11=new File(PrefixDir,"11");
		Dir11.mkdirs();
		JPGMatrixOutput(8,8,256,256,Dir11,ImageData);
		File Dir12=new File(PrefixDir,"12");
		Dir12.mkdirs();
		JPGMatrixOutput(16,16,256,256,Dir12,ImageData);
		File Dir13=new File(PrefixDir,"13");
		Dir13.mkdirs();
		JPGMatrixOutput(32,32,256,256,Dir13,ImageData);
		File Dir14=new File(PrefixDir,"14");
		Dir14.mkdirs();
		JPGMatrixOutput(64,64,256,256,Dir14,ImageData);
	}
	public void JPGMatrixOutput(int Row,int Col,int SingleWidth,int SingleHeight,File LocalDir,BufferedImage ImageData){
		BufferedImage ImageOutput;
		Graphics2D g_2d;
		FileOutputStream out;
		int col_step=ImageData.getWidth(this)/Col;
		int row_step=ImageData.getHeight(this)/Row;
		try{
		for(int Col_ptr=0;Col_ptr<Col;Col_ptr++){
			for(int Row_ptr=0;Row_ptr<Row;Row_ptr++){
				ImageOutput = new BufferedImage(SingleWidth, SingleHeight,BufferedImage.TYPE_INT_RGB);
				g_2d=ImageOutput.createGraphics();
				g_2d.drawImage(ImageData, 0, 0, SingleWidth, SingleHeight,
						Col_ptr*col_step, Row_ptr*row_step, (Col_ptr+1)*col_step,
						(Row_ptr+1)*row_step, this);
				out = new FileOutputStream(LocalDir+"\\"+Col_ptr+"_"+Row_ptr+".jpg");
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(ImageOutput);
				out.close();
				System.out.println(LocalDir+"\\"+Col_ptr+"_"+Row_ptr+".jpg");
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void JPGOutput(double LongitudeStart, double LongitudeEnd,
			double LatitudeStart, double LatitudeEnd) {
		if (DIR == null)
			return;
		try {
			String str = JOptionPane.showInputDialog(null,
					LanguageDic.GetWords("请以下列各式输入信息：宽度;高度;粗度"),
					LanguageDic.GetWords("JPG生成在Image目录下"),
					JOptionPane.PLAIN_MESSAGE);
			if ((str == null) || (str.equals(""))) {
				ReTitle(LanguageDic.GetWords("放弃了JPG生成"));
				return;
			}
			ReTitle(LanguageDic.GetWords("开始生成JPG"));
			String s1 = str.substring(0, str.indexOf(';'));
			String s2 = str.substring(str.indexOf(';') + 1);
			String s3 = s2.substring(s2.indexOf(';') + 1);
			s2 = s2.substring(0, s2.indexOf(';'));
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			JPGOutput(Math.min(LongitudeStart, LongitudeEnd), Math.max(
					LongitudeStart, LongitudeEnd), Math.min(LatitudeStart,
					LatitudeEnd), Math.max(LatitudeStart, LatitudeEnd),
					Integer.parseInt(s1), Integer.parseInt(s2),
					Integer.parseInt(s3),
					(new File(ImageDir, "[" + df.format(new Date()) + "]"))
							.toString());
		} catch (Exception ex) {
			Handle.SolveException(ex);
		}
	}

	public class ScreenLocationChanger extends JFrame {
		FacePic Pic;
		MovePane MoveTool;
		JLabel l0;
		int ddx, ddy;

		class MovePane extends Canvas implements MouseListener,
				MouseMotionListener {
			public void paint(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("BackGround30.jpg");
				g.drawImage(img, 0, 0, this);
			}

			public MovePane() {
				setBounds(0, 0, 275, 275);
				addMouseListener(this);
				addMouseMotionListener(this);
			}

			public void mouseDragged(MouseEvent e) {
				ddx = e.getX() - PressedX;
				ddy = e.getY() - PressedY;
				Screen.ScreenDeltaX = ddx;
				Screen.ScreenDeltaY = ddy;
				Screen.repaint();
			}

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			int PressedX, PressedY;

			public void mousePressed(MouseEvent e) {
				PressedX = e.getX();
				PressedY = e.getY();
				Screen.ScreenDeltaX = ddx;
				Screen.ScreenDeltaY = ddy;
				Screen.repaint();
			}

			public void mouseReleased(MouseEvent e) {
				ddx = e.getX() - PressedX;
				ddy = e.getY() - PressedY;
				Screen.ScreenDeltaX = ddx;
				Screen.ScreenDeltaY = ddy;
				Screen.repaint();
			}
		}

		class FacePic extends JPanel implements MouseListener,
				MouseMotionListener {
			public FacePic() {
				l0 = new JLabel(LanguageDic.GetWords("屏幕元素位置教调"));
				l0.setFont(new Font("华文新魏", Font.BOLD, 36));
				add(l0);
				setBounds(0, 0, 330, 360);
				MoveTool = new MovePane();
				add(MoveTool);
				addMouseListener(this);
				addMouseMotionListener(this);
			}

			public void paintComponent(Graphics g) {
				Toolkit kit = getToolkit();
				Image img = kit.getImage("Gear.jpg");
				g.drawImage(img, 0, 0, 330, 360, this);
			}

			public void mouseDragged(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 3)
					dispose();
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			int PressedX, PressedY;

			public void mousePressed(MouseEvent e) {
				PressedX = e.getX();
				PressedY = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				Move(e.getX() - PressedX, e.getY() - PressedY);
			}
		}

		// Above
		// Pic---------------------------------------------------------------------------------
		public ScreenLocationChanger() {
			setBounds(0, 0, 330, 360);
			setVisible(false);
			setUndecorated(true);
			setLocationRelativeTo(null);
			Pic = new FacePic();
			add(Pic, BorderLayout.CENTER);
		}

		public void Move(int dx, int dy) {
			int x = this.getLocation().x;
			int y = this.getLocation().y;
			this.setLocation(x + dx, y + dy);
		}
	};
}
