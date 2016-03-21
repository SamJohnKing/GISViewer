package SecondaryScreen;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import ExtendedToolPane.ServerSocketPaneClass;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser; 
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Button; 
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display; 
import org.eclipse.swt.widgets.Event; 
import org.eclipse.swt.widgets.Label; 
import org.eclipse.swt.widgets.Listener; 
import org.eclipse.swt.widgets.Shell; 
import org.eclipse.swt.widgets.Text; 

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import MapKernel.MapWizard;

public class SwtHtmlBrowser implements Runnable{
	static int MapWidth=0;
	static int MapHeight=0;
	static double WebLongitudeStart,WebLongitudeEnd,WebLatitudeStart,WebLatitudeEnd;
	static double DeviationLongitude,DeviationLatitude;
	public static Browser browser = null;
	public static Canvas canvaspane = null;
	public static Canvas Mask = null;
	public static Shell shell = null;
	public static String MapInitCommand = null;
	public static boolean Accessed = false;
	static long DownTimestamp = 0;
	static int PressedX = -1;
	static int PressedY = -1;
	public static Thread SingleItemThread=null;
	public static boolean Running=false;
	public static byte AlphaSettingValue=10;
	public static int FlushInterval=500;
	public static String JavaScriptInputData="";
	public static final class CallJava extends BrowserFunction{
		public CallJava (Browser JavaWeb,String FunctionName){
			super(JavaWeb,FunctionName);
		}
		public Object function(Object[] Results){
			try {
				String FunctionType=Results[0].toString();
				if(FunctionType.equals("Bounds_x1_x2_y1_y2")){
					WebLongitudeStart = Double.parseDouble(Results[1].toString());
					WebLongitudeEnd = Double.parseDouble(Results[2].toString());
					WebLatitudeStart = Double.parseDouble(Results[3].toString());
					WebLatitudeEnd = Double.parseDouble(Results[4].toString());
				}else if(FunctionType.equals("DataInput")){
					JavaScriptInputData+=Results[1].toString();
				}else if(FunctionType.equals("DataClear")){
					JavaScriptInputData="";
					System.gc();
				}else if(FunctionType.equals("DataSave")){
					FileOutputStream fostream=new FileOutputStream(Results[1].toString(),false);
					BufferedWriter BFout=new BufferedWriter(new OutputStreamWriter(fostream,"UTF-8"));
					try{
						BFout.write(JavaScriptInputData+"\n");
					}catch(Exception ex){
						ex.printStackTrace();
					}finally{
						BFout.close();
					}
				}else if(FunctionType.equals("SetDataDeviation")){
					DeviationLongitude = Double.parseDouble(Results[1].toString());
					DeviationLatitude = Double.parseDouble(Results[2].toString());
					ScreenFlush();
				}else if(FunctionType.equals("MapAccessed")){
					Accessed = true;
				}
				return null;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}
	public static void GetInstance(){
		if(SingleItemThread!=null) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					if(shell.isVisible()) javax.swing.JOptionPane.showMessageDialog(null, "Another Instance is Running");
					else{
						shell.setVisible(true);
						Accessed = true;
						shell.setFocus();
					}
				}
			});
			return;
		}
		// TODO Auto-generated method stub
		String str_width=JOptionPane.showInputDialog(null,"Secondary Screen Width");
		String str_height=JOptionPane.showInputDialog(null,"Secondary Screnn Height");
		GetInstance(Integer.parseInt(str_width), Integer.parseInt(str_height));
	}

	public static void GetInstance(int Width, int Height){
		if(SingleItemThread!=null) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					if(shell.isVisible()) javax.swing.JOptionPane.showMessageDialog(null, "Another Instance is Running");
					else{
						shell.setVisible(true);
						Accessed = true;
						shell.setFocus();
					}
				}
			});
			return;
		}
		// TODO Auto-generated method stub
		FlushInterval=500;
		DeviationLongitude=0;
		DeviationLatitude=0;
		MapWidth=Width;
		MapHeight=Height;
		SingleItemThread=new Thread(new SwtHtmlBrowser());
		SingleItemThread.start();
	}
	public static int GetOpenGLPointThickness(String info) {
		try {
			if (info == null)
				return 4;
			int p1;
			if ((p1 = info.indexOf("[PointSize:")) != -1)
				return Integer.parseInt(info.substring(p1 + 11,
						info.indexOf(']', p1 + 11)));
			else
				return 4;
		} catch (Exception ex) {
			System.err.println("GetVisualPointSize_Err");
			ex.printStackTrace();
			return 4;
		}
	}
	public static int GetOpenGLLineThickness(String info) {
		try {
			if (info == null) return 1;
			int p1;
			if ((p1 = info.indexOf("[LineWidth:")) != -1)
				return Integer.parseInt(info.substring(p1 + 11, info.indexOf(']', p1 + 11)));
			else return 1;
		} catch (Exception ex) {
			System.err.println("GetVisualLineStroke_Err");
			ex.printStackTrace();
			return 1;
		}
	}
	public static Vector<Integer> CoorVector = new Vector<Integer>();
	public static int[] Coor = null;
	public static void setVisualDashLine(GC gc, String info) {
		if (info.indexOf("[DashLine:") != -1) {
			gc.setLineDash(new int[]{5, 5});
		} else gc.setLineDash(null);
	}
	public static void DBPaint(PaintEvent e){
		browser.execute("MapBoundsToJavaWeb();");
		if(MapWizard.SingleItem.IsShowAlphaDistribution) {
			BufferedImage Alpha_Image = new BufferedImage(MapWidth, MapHeight, BufferedImage.TYPE_INT_ARGB);
			BufferedImage Source_Image = new BufferedImage(MapWidth, MapHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g_alpha = Alpha_Image.createGraphics();
			Graphics2D g_2d = Source_Image.createGraphics();
			MapKernel.MapWizard.SingleItem.Screen.DBpaint(g_alpha, WebLongitudeStart - DeviationLongitude, WebLatitudeEnd - DeviationLatitude,
					WebLongitudeEnd - WebLongitudeStart, WebLatitudeEnd - WebLatitudeStart, MapWidth, MapHeight);
			MapKernel.MapWizard.SingleItem.Screen.DBpaint(g_2d, WebLongitudeStart - DeviationLongitude, WebLatitudeEnd - DeviationLatitude,
					WebLongitudeEnd - WebLongitudeStart, WebLatitudeEnd - WebLatitudeStart, MapWidth, MapHeight);
			ImageData SWT_ImageData = convertToSWT(Source_Image);
			//-----------------------------------------------------------
			byte[] alphaValues = new byte[SWT_ImageData.height
					* SWT_ImageData.width];
			byte[] RGB_List = SWT_ImageData.data;
			int offset = 0;
			int k = 0;
			int r, g, b, a;
			for (int i = 0; i < SWT_ImageData.height; i++) {
				for (int j = 0; j < SWT_ImageData.width; j++) {
					k = offset + j * 3;
					b = (RGB_List[k] = (byte) ((Alpha_Image
							.getRGB(j, i) >> 16) & 0xff));
					g = (RGB_List[k + 1] = (byte) ((Alpha_Image.getRGB(
							j, i) >> 8) & 0xff));
					r = (RGB_List[k + 2] = (byte) ((Alpha_Image.getRGB(
							j, i) >> 0) & 0xff));
					a = (byte) ((Alpha_Image.getRGB(j, i) >> 24) & 0xff);
					// b=RGB_List[k] & 0xff;
					// g=RGB_List[k+1] & 0xff;
					// r=RGB_List[k+2] & 0xff;
					if (Math.abs(r) + Math.abs(g) + Math.abs(b) + Math.abs(a) == 0)
						alphaValues[i * SWT_ImageData.width + j] = (byte) 0;
					else alphaValues[i * SWT_ImageData.width + j] = (byte) a;

					//if (Math.abs(r) + Math.abs(g) + Math.abs(b) == 0)
					//	alphaValues[i * SWT_ImageData.width + j] = AlphaSettingValue;
					//else
					//	alphaValues[i * SWT_ImageData.width + j] = (byte) a;
				}
				offset += SWT_ImageData.bytesPerLine;
			}
			SWT_ImageData.alphaData = alphaValues;
			//-------------------------------------------------------------
			Image SWT_Image = new Image(null, SWT_ImageData);
			e.gc.drawImage(SWT_Image, 0, 0, MapWidth, MapHeight, 0, 0, MapWidth, MapHeight);
			return;
		}
		// Draw for Extended Components
		e.gc.setLineDash(new int[]{5,5});
		if ((MapWizard.SingleItem.Screen.IsExtendedPointVisible) && (MapWizard.SingleItem.Screen.ExtendedPointCount > 0)) {
			for (int i = 0; i < MapWizard.SingleItem.Screen.ExtendedPointCount; i++) {
				int xx = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[i]);
				int yy = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[i]);
				xx += MapWizard.SingleItem.Screen.ScreenDeltaX;
				yy += MapWizard.SingleItem.Screen.ScreenDeltaY;
				e.gc.setForeground(new Color(null, 255, 255, 0));
				e.gc.setBackground(new Color(null, 255, 255, 0));
				e.gc.setAlpha(255);
				e.gc.fillOval(xx - 4, yy - 4, 8, 8);
				if (MapWizard.SingleItem.Screen.IsExtendedPointHintVisible) {
					e.gc.drawString(MapWizard.SingleItem.Screen.ExtendedPointHint[i], xx + 1, yy + 1, true);
				}
			}
			e.gc.setForeground(new Color(null, 0, 255, 0));
			e.gc.setAlpha(255);
			if (MapWizard.SingleItem.Screen.IsConsecutiveLink)
				for (int i = 1; i < MapWizard.SingleItem.Screen.ExtendedPointCount; i++) {
					int x1 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[i - 1]);
					int y1 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[i - 1]);
					int x2 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[i]);
					int y2 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[i]);
					x1 += MapWizard.SingleItem.Screen.ScreenDeltaX;
					y1 += MapWizard.SingleItem.Screen.ScreenDeltaY;
					x2 += MapWizard.SingleItem.Screen.ScreenDeltaX;
					y2 += MapWizard.SingleItem.Screen.ScreenDeltaY;
					e.gc.setLineDash(new int[]{5,5});
					e.gc.setLineWidth(2);
					e.gc.drawLine(x1, y1, x2, y2);
					e.gc.setLineDash(null);

					int x3 = (x1 + x2) / 2 + (y2 - y1) / 4;
					int y3 = (y1 + y2) / 2 - (x2 - x1) / 4;
					x3 = x3 + 3 * (x2 - x3) / 4;
					y3 = y3 + 3 * (y2 - y3) / 4;
					e.gc.drawLine(x3, y3, x2, y2);

					x3 = (x1 + x2) / 2 - (y2 - y1) / 4;
					y3 = (y1 + y2) / 2 + (x2 - x1) / 4;
					x3 = x3 + 3 * (x2 - x3) / 4;
					y3 = y3 + 3 * (y2 - y3) / 4;
					e.gc.drawLine(x3, y3, x2, y2);
				}
			if (MapWizard.SingleItem.Screen.IsHeadTailLink) {
				e.gc.setForeground(new Color(null, 255, 0, 0));
				e.gc.setAlpha(255);
				int x1 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[0]);
				int y1 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[0]);
				int Ptr = MapWizard.SingleItem.Screen.ExtendedPointCount - 1;
				if (Ptr < 0) Ptr = 0;
				int x2 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[Ptr]);
				int y2 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[Ptr]);
				x1 += MapWizard.SingleItem.Screen.ScreenDeltaX;
				y1 += MapWizard.SingleItem.Screen.ScreenDeltaY;
				x2 += MapWizard.SingleItem.Screen.ScreenDeltaX;
				y2 += MapWizard.SingleItem.Screen.ScreenDeltaY;
				e.gc.setLineDash(new int[]{5, 5});
				e.gc.drawLine(x1, y1, x2, y2);
				e.gc.setLineDash(null);
			}
			if (MapWizard.SingleItem.Screen.ExtendedPointSelectCount != 0) {
				for (int i = 0; i < MapWizard.SingleItem.Screen.ExtendedPointSelectCount; i++) {
					int xx = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[MapWizard.SingleItem.Screen.ExtendedPointSelectList[i]]);
					int yy = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[MapWizard.SingleItem.Screen.ExtendedPointSelectList[i]]);
					xx += MapWizard.SingleItem.Screen.ScreenDeltaX;
					yy += MapWizard.SingleItem.Screen.ScreenDeltaY;
					e.gc.setForeground(new Color(null, 255, 0, 0));
					e.gc.setAlpha(255);
					e.gc.fillOval(xx - 4, yy - 4, 8, 8);
				}
			}
		}
		e.gc.setLineDash(null);

		//Draw OpenGL Elements
		if (MapWizard.SingleItem.IsAllElementInvisible) return;
		while (!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, true)) {
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		};


		if (!MapWizard.SingleItem.IsAllLineInvisible)
			if (MapWizard.SingleItem.LineDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				int DrawCount = 0;
				for (int i = 0; i < MapWizard.SingleItem.LineDatabase.LineNum; i++) {
					binary = MapWizard.SingleItem.LineDatabase.LineVisible[i];
					if (binary < 0)
						continue;
					if (DrawCount < MapWizard.SingleItem.VisualObjectMaxNum)
						if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
							if (!MapWizard.SingleItem.LineDatabase.CheckInRegion(i, WebLongitudeStart - DeviationLongitude,
									WebLatitudeStart - DeviationLatitude, WebLongitudeEnd - DeviationLongitude, WebLatitudeEnd - DeviationLatitude))
								continue;
							/** Check in Screen */
							if (((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("1000")) != 0))
									|| (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[LineVisible:]") != -1))) {// For Line----------------------------
								choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
								e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
								e.gc.setAlpha(Origin.getAlpha());
								int thickness = 1;
								if (MapWizard.SingleItem.ShowVisualFeature) {
									setVisualDashLine(e.gc, MapWizard.SingleItem.LineDatabase.LineHint[i]);
									thickness = GetOpenGLLineThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
									Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i], "Line");
									e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
									e.gc.setAlpha(Origin.getAlpha());
								}

								now = MapWizard.SingleItem.LineDatabase.LineHead[i];
								p1 = now;

								while (true) {
									p2 = MapWizard.SingleItem.LineDatabase.AllPointNext[p1];
									if (p2 == -1)
										break;

									int x1 = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[p1]);
									int y1 = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[p1]);
									int x2 = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[p2]);
									int y2 = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[p2]);

									e.gc.setLineWidth(thickness);
									e.gc.drawLine(x1, y1, x2, y2);

									if ((MapWizard.SingleItem.ShowVisualFeature)
											&& (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.LineDatabase.LineHint[i]))) {
										e.gc.setLineWidth(2);
										double Scale = ((Math.abs(x2 - x1) + Math.abs(y2 - y1) > 0.1 * (MapWidth + MapHeight)) ?
												(0.2 / (Math.abs(x2 - x1) + Math.abs(y2 - y1)) * 0.1 * (MapWidth + MapHeight)) : 0.2);
										e.gc.drawLine(x2, y2,
												(int) (x2
														+ Scale
														* (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
												(int) (y2
														+ Scale
														* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))));
										e.gc.drawLine(x2, y2,
												(int) (x2
														+ Scale
														* (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
												(int) (y2
														+ Scale
														* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))));
									}
									DrawCount++;
									p1 = p2;
								}
							}
							if (((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("100")) != 0))
									|| (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[PointVisible:]") != -1))
									) {// For Point-------------------------
								choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
								e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
								e.gc.setAlpha(Origin.getAlpha());
								int thickness = 1;
								if (MapWizard.SingleItem.ShowVisualFeature) {
									thickness = GetOpenGLPointThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
									Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i], "Point");
									e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
									e.gc.setAlpha(Origin.getAlpha());
								}

								now = MapWizard.SingleItem.LineDatabase.LineHead[i];
								while (now != -1) {
									int xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
									int yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
									e.gc.fillOval(xx - thickness / 2, yy - thickness / 2, thickness, thickness);
									DrawCount++;
									now = MapWizard.SingleItem.LineDatabase.AllPointNext[now];
								}
							}
							if (!MapWizard.SingleItem.IsAllFontInvisible)
								if (
										((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("10")) != 0))
												||
												(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[WordVisible:]") != -1))
										) {// For Word--------------------------
									choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
									java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
									e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
									e.gc.setAlpha(Origin.getAlpha());
									now = MapWizard.SingleItem.LineDatabase.LineHead[i];
									int xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
									int yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
									DrawCount++;
									e.gc.drawString(MapWizard.SingleItem.LineDatabase.getTitle(i), xx + 1, yy + 1, true);
								}
						}
				}
			}
		if (!MapWizard.SingleItem.IsAllPolygonInvisible)
			if (MapWizard.SingleItem.PolygonDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				int DrawCount = 0;
				for (int i = 0; i < MapWizard.SingleItem.PolygonDatabase.PolygonNum; i++) {
					binary = MapWizard.SingleItem.PolygonDatabase.PolygonVisible[i];
					if (binary < 0)
						continue;
					if (DrawCount < MapWizard.SingleItem.VisualObjectMaxNum)
						if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
							if (!MapWizard.SingleItem.PolygonDatabase.CheckInRegion(i, WebLongitudeStart - DeviationLongitude,
									WebLatitudeStart - DeviationLatitude, WebLongitudeEnd - DeviationLongitude, WebLatitudeEnd - DeviationLatitude))
								continue;
							/** Check in Screen */
							if (
									((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("1000")) != 0))
											||
											(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[LineVisible:]") != -1))
									) {// For Line----------------------------
								int thickness = 1;
								choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
								e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
								e.gc.setAlpha(Origin.getAlpha());
								if (MapWizard.SingleItem.ShowVisualFeature) {
									setVisualDashLine(e.gc, MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
									thickness = GetOpenGLLineThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
									Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Line");
									e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
									e.gc.setAlpha(Origin.getAlpha());
								}

								now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
								p1 = now;
								while (true) {
									p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
									if (p2 == -1) p2 = now;
									int x1 = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
									int y1 = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
									int x2 = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p2]);
									int y2 = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p2]);
									e.gc.setLineWidth(thickness);
									e.gc.drawLine(x1, y1, x2, y2);
									if ((MapWizard.SingleItem.ShowVisualFeature)
											&& (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]))) {
										e.gc.setLineWidth(2);
										double Scale = ((Math.abs(x2 - x1) + Math.abs(y2 - y1) > 0.1 * (MapWidth + MapHeight)) ?
												(0.2 / (Math.abs(x2 - x1) + Math.abs(y2 - y1)) * 0.1 * (MapWidth + MapHeight)) : 0.2);
										e.gc.drawLine(x2, y2,
												(int) (x2
														+ Scale
														* (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
												(int) (y2
														+ Scale
														* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))));
										e.gc.drawLine(x2, y2,
												(int) (x2
														+ Scale
														* (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
												(int) (y2
														+ Scale
														* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))));
									}
									DrawCount++;
									p1 = p2;
									if (p1 == now) break;
								}
							}
							if (MapWizard.SingleItem.ShowVisualFeature)
								if (!MapWizard.SingleItem.IsAllPolygonColorInvisible)
									if (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PolygonVisible:]") != -1) {//For Polygon
										java.awt.Color Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Polygon");
										e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
										e.gc.setAlpha(Origin.getAlpha());
										now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
										p1 = now;

										CoorVector.clear();
										while (true) {
											p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
											if (p2 == -1) p2 = now;
											CoorVector.add(GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]));
											CoorVector.add(GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]));
											p1 = p2;
											if (p1 == now) break;
										}
										/** DrawPolygon */
										Coor = new int[CoorVector.size()];
										int counter = 0;
										for(Integer Item : CoorVector){
											Coor[counter] = CoorVector.get(counter);
											counter++;
										}
										e.gc.fillPolygon(Coor);
									}
							if (
									((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("100")) != 0))
											||
											(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PointVisible:]") != -1))
									) {// For Point-------------------------
								choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
								e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
								e.gc.setAlpha(Origin.getAlpha());
								int thickness = 1;
								if (MapWizard.SingleItem.ShowVisualFeature) {
									thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
									Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Point");
									e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
									e.gc.setAlpha(Origin.getAlpha());
								}
								now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
								while (now != -1) {
									int xx = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
									int yy = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
									e.gc.fillOval(xx - thickness / 2, yy - thickness / 2, thickness, thickness);
									DrawCount++;
									now = MapWizard.SingleItem.PolygonDatabase.AllPointNext[now];
								}

							}
							if (!MapWizard.SingleItem.IsAllFontInvisible)
								if (
										((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("10")) != 0))
												||
												(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[WordVisible:]") != -1))
										) {// For Word--------------------------
									choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
									java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
									e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
									e.gc.setAlpha(Origin.getAlpha());
									now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
									int xx = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
									int yy = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
									DrawCount++;
									e.gc.drawString(MapWizard.SingleItem.PolygonDatabase.getTitle(i), xx + 1, yy + 1, true);
								}
						}
				}
			}
		if ((!MapWizard.SingleItem.IsAllPointInvisible) && (!MapWizard.SingleItem.IsShowAlphaDistribution))
			if (MapWizard.SingleItem.PointDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				int DrawCount = 0;
				for (int i = 0; i < MapWizard.SingleItem.PointDatabase.PointNum; i++) {
					if (!MapWizard.SingleItem.PointDatabase.CheckInRegion(i, WebLongitudeStart - DeviationLongitude,
							WebLatitudeStart - DeviationLatitude, WebLongitudeEnd - DeviationLongitude, WebLatitudeEnd - DeviationLatitude))
						continue;
					/** Check in Screen */
					binary = MapWizard.SingleItem.PointDatabase.PointVisible[i];
					if (DrawCount > MapWizard.SingleItem.VisualObjectMaxNum) break;
					if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
						if (binary < 0) continue;
						if (
								((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("100")) != 0))
										||
										(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[PointVisible:]") != -1))
								) {// For Point-------------------------
							choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
							java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
							e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
							e.gc.setAlpha(Origin.getAlpha());

							int thickness = 1;
							if (MapWizard.SingleItem.ShowVisualFeature) {
								thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PointDatabase.PointHint[i]);
								Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PointDatabase.PointHint[i], "Point");
								e.gc.setBackground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
								e.gc.setAlpha(Origin.getAlpha());
							}
							int xx = GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
							int yy = GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
							if (MapWizard.SingleItem.IsEngravePointShape) thickness = 1;
								e.gc.fillOval(xx - thickness / 2, yy - thickness / 2, thickness, thickness);
							DrawCount++;
						}
						if (!MapWizard.SingleItem.IsAllFontInvisible)
							if (
									((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("10")) != 0))
											||
											(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[WordVisible:]") != -1))
									) {// For Word--------------------------
								choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
								e.gc.setForeground(new Color(null, Origin.getRed(), Origin.getGreen(), Origin.getBlue()));
								e.gc.setAlpha(Origin.getAlpha());
								int xx = GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
								int yy = GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
								e.gc.drawString(MapWizard.SingleItem.PointDatabase.getTitle(i), xx + 1, yy + 1, true);
							}
					}
				}
			}

		while (!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, false)) {
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		;
		if (MapWizard.SingleItem.Screen.IsTextArea2Visible) {
			e.gc.setForeground(new Color(null, 0, 255, 0));
			e.gc.setAlpha(255);
			e.gc.setBackground(new Color(null, 0, 0, 0));
			e.gc.fillRectangle(0, 0, MapWidth, 21);
			e.gc.drawString(MapWizard.SingleItem.Screen.TextArea2Content, 10, 2, true);
		}
		if (MapWizard.SingleItem.Screen.IsTextArea1Visible) {
			e.gc.setForeground(new Color(null, 255, 0, 0));
			e.gc.setAlpha(255);
			e.gc.setBackground(new Color(null, 0, 0, 0));
			e.gc.fillRectangle(0, MapHeight - 20, MapWidth, 20);
			e.gc.drawString(MapWizard.SingleItem.Screen.TextArea1Content, 10, MapHeight - 18, true);
		}
	}
	public static double GetLogicalX(int x){
		return WebLongitudeStart + (WebLongitudeEnd - WebLongitudeStart) * x / MapWidth - DeviationLongitude;
	}
	public static double GetLogicalY(int y){
		return WebLatitudeEnd - (WebLatitudeEnd - WebLatitudeStart) * y / MapHeight - DeviationLatitude;
	}
	public static double GetLongitudeStart() { return WebLongitudeStart - DeviationLongitude; };
	public static double GetLongitudeEnd() { return WebLongitudeEnd - DeviationLongitude; };
	public static double GetLatitudeStart() { return WebLatitudeStart - DeviationLatitude; };
	public static double GetLatitudeEnd() { return WebLatitudeEnd - DeviationLatitude; };
	public static int GetScreenX(double x){
		return (int) (((x + DeviationLongitude) - WebLongitudeStart) / (WebLongitudeEnd - WebLongitudeStart) * MapWidth + 0.5);
	}
	public static int GetScreenY(double y){
		return (int) ((WebLatitudeEnd - (y + DeviationLatitude)) / (WebLatitudeEnd - WebLatitudeStart) * MapHeight + 0.5);
	}
	/** Input the Logical Coordinate */
	public static void MoveMiddle(double x, double y){
		browser.execute("map.setView(L.latLng(" + (y + DeviationLatitude) + "," + (x + DeviationLongitude) + "),map.getZoom(), {reset : false, animate : true, pan : {animate : true}, zoom : {animate : true}});");
		browser.execute("MapBoundsToJavaWeb();");
		Mask.redraw();
		canvaspane.redraw();
	}
	public static void ScreenFlush(){
		SwtHtmlBrowser.Mask.redraw();
		SwtHtmlBrowser.canvaspane.redraw();
	}

	public static boolean IsReady(){
		return Accessed;
	}
	public static void InitiateBrowser(){
		Display display=new Display();
		try{
			shell=new Shell(display);

			shell.addShellListener(new ShellListener() {
				@Override
				public void shellActivated(ShellEvent shellEvent) {

				}

				@Override
				public void shellClosed(ShellEvent shellEvent) {
					Accessed = false;
					if(ServerSocketPaneClass.ServerHandle.ServerOpen) { // 网络连接开启时不允许关闭
						shellEvent.doit = false;
						shell.setVisible(false);
					}
				}

				@Override
				public void shellDeactivated(ShellEvent shellEvent) {

				}

				@Override
				public void shellDeiconified(ShellEvent shellEvent) {

				}

				@Override
				public void shellIconified(ShellEvent shellEvent) {

				}
			});

	        shell.setText("GeoCity Secondary Screen");
	        //----------------------------------------------------
	        //Canvas
	        canvaspane=new Canvas(shell,SWT.TRANSPARENT);
	        canvaspane.setVisible(false);
	        canvaspane.setBounds(0, 0, MapWidth, MapHeight);

			Mask=new Canvas(shell,SWT.TRANSPARENT);
			Mask.setVisible(false);
			Mask.setBounds(0, 0, MapWidth, MapHeight);

	        canvaspane.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					// TODO Auto-generated method stub
					DBPaint(e);
				}
			});

			canvaspane.addMouseWheelListener(new MouseWheelListener() {
				@Override
				public void mouseScrolled(MouseEvent mouseEvent) {
					double StayX = GetLogicalX(mouseEvent.x) + DeviationLongitude;
					double StayY = GetLogicalY(mouseEvent.y) + DeviationLatitude;
					if (mouseEvent.count > 0) {
						browser.execute("map.setZoomAround(L.latLng(" + StayY + "," + StayX + "),  map.getZoom() + 1, {animate : true});");
					} else if (mouseEvent.count < 0) {
						browser.execute("map.setZoomAround(L.latLng(" + StayY + "," + StayX + "),  map.getZoom() - 1, {animate : true});");
					}
					browser.execute("MapBoundsToJavaWeb();");
					Mask.redraw();
					canvaspane.redraw();

					/*
					double LogicalX_0 = GetLogicalX(mouseEvent.x);
					double LogicalY_0 = GetLogicalY(mouseEvent.y);
					double Middle_x = (WebLongitudeStart + WebLongitudeEnd) / 2 - DeviationLongitude;
					double Middle_y = (WebLatitudeStart + WebLatitudeEnd) / 2 - DeviationLatitude;
					if (mouseEvent.count > 0) {
						browser.execute("map.zoomIn();");
					} else if (mouseEvent.count < 0) {
						browser.execute("map.zoomOut();");
					}
					browser.execute("MapBoundsToJavaWeb();");
					MoveMiddle(Middle_x + (LogicalX_0 - GetLogicalX(mouseEvent.x)), Middle_y + (LogicalY_0 - GetLogicalY(mouseEvent.y)));
					*/
				}
			});

	        canvaspane.addMouseListener(new MouseListener() {

				@Override
				public void mouseDoubleClick(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseDown(MouseEvent arg0) {
					// TODO Auto-generated method stub
					DownTimestamp = java.util.Calendar.getInstance().getTimeInMillis();
					PressedX = arg0.x;
					PressedY = arg0.y;
				}

				@Override
				public void mouseUp(MouseEvent arg0) {
					// TODO Auto-generated method stub
					int dx = arg0.x - PressedX;
					int dy = arg0.y - PressedY;
					if ((java.util.Calendar.getInstance().getInstance().getTimeInMillis() - DownTimestamp < 200)
							&&(Math.abs(dx) + Math.abs(dy) == 0 )) {
						if (MapWizard.SingleItem.BehaviorListener != null) {
							MapKernel.MapWizard.SingleItem.BehaviorListener.MousePressedListener(GetLogicalX(arg0.x), GetLogicalY(arg0.y));
						}else if (MapKernel.MapWizard.SingleItem.NowPanel.getString().equals("MapElementsEditorPane")) {
							ExtendedToolPane.ExtendedToolPaneInterface DBEditor = (ExtendedToolPane.ExtendedToolPaneInterface)
									(MapKernel.MapWizard.SingleItem.NowPanel);
							if (arg0.button == 1) {
								double DBLongitude = WebLongitudeStart + (WebLongitudeEnd - WebLongitudeStart) * arg0.x / MapWidth - DeviationLongitude;
								double DBLatitude = WebLatitudeEnd - (WebLatitudeEnd - WebLatitudeStart) * arg0.y / MapHeight - DeviationLatitude;
								DBEditor.convey(DBLongitude, DBLatitude);
							} else {
								DBEditor.confirm();
							}
						}

					} else {
						double DLongitude = -dx * (WebLongitudeEnd - WebLongitudeStart) / MapWidth;
						double DLatitude = +dy * (WebLatitudeEnd - WebLatitudeStart) / MapHeight;
						MoveMiddle((WebLongitudeStart + WebLongitudeEnd) / 2 - DeviationLongitude + DLongitude, (WebLatitudeStart + WebLatitudeEnd) / 2 - DeviationLatitude + DLatitude);
						return;
					}
					Mask.redraw();
					canvaspane.redraw();
				}
			});

	        Mask.addPaintListener(new PaintListener(){
				@Override
				public void paintControl(PaintEvent e) {
					// TODO Auto-generated method stub
					e.gc.setBackground(new Color(null,0,0,0));
					e.gc.setAlpha(AlphaSettingValue);
					e.gc.fillRectangle(0, 0, MapWidth, MapHeight);
				}
	        });

	        //------------------------------------------------------
	        //JSEditor
	        Label lb1=new Label(shell,SWT.CENTER);
	        lb1.setBounds(MapWidth+10,35,275,25);
	        lb1.setText("JavaScript Editors");
	        lb1.setFont(new Font(display,"Consolas",16,SWT.BOLD));//设置文字的字体字号
	        lb1.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

	        final Text JScode=new Text(shell,SWT.BORDER|SWT.WRAP);
	        JScode.setBounds(MapWidth+10, 60 , 275, 100);

	        final Text AlphaSettingText=new Text(shell,SWT.BORDER);
	        AlphaSettingText.setBounds(MapWidth+215, 200 , 70, 30);

	        final Text UrlAddressText=new Text(shell,SWT.BORDER);
	        UrlAddressText.setBounds(MapWidth+60, 235, 225, 30);

	        final Text LongitudeEastText=new Text(shell,SWT.BORDER);
	        LongitudeEastText.setBounds(MapWidth+10,270,70,30);

	        final Text LatitudeNorthText=new Text(shell,SWT.BORDER);
	        LatitudeNorthText.setBounds(MapWidth+215,270,70,30);
	        //------------------------------------------------------
	        //Buttons
	       	Button UnLockButton=new Button(shell,SWT.NONE);
	       	UnLockButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					canvaspane.setVisible(false);
					Mask.setVisible(false);
				}
			});
	       	UnLockButton.setBounds(MapWidth + 10, 5, 100, 30);
	       	UnLockButton.setText("UnLock Map");

	       	Button LockButton=new Button(shell,SWT.NONE);
	       	LockButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					canvaspane.setVisible(true);
					Mask.setVisible(true);
				}
			});
	       	LockButton.setBounds(MapWidth + 120, 5, 160, 30);
	       	LockButton.setText("Lock Map Show Data");

	       	Button Execute_JSCode=new Button(shell,SWT.NONE);
	       	Execute_JSCode.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					browser.execute(JScode.getText());
				}
			});
	       	Execute_JSCode.setBounds(MapWidth + 10, 165, 200, 30);
	       	Execute_JSCode.setText("Execute JavaScript Code");

	       	Button Setting_Alpha=new Button(shell,SWT.NONE);
	       	Setting_Alpha.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					try {
						AlphaSettingValue = (byte) (Integer.parseInt(AlphaSettingText.getText()) % 256);
						Mask.setVisible(true);
						Mask.redraw();
					} catch (Exception ex) {
						ex.printStackTrace();
						return;
					}
				}
			});
	       	Setting_Alpha.setBounds(MapWidth + 10, 200, 200, 30);
	       	Setting_Alpha.setText("Setting Mask Alpha Value");

	       	Button GoUrl=new Button(shell,SWT.NONE);
	       	GoUrl.addListener(SWT.Selection,new Listener(){
	            public void handleEvent(Event event)
	            {
	            	GoUrlAction(UrlAddressText.getText());
	            }
	       	});
	        GoUrl.setBounds(MapWidth+10, 235, 45, 30);
	        GoUrl.setText("GoUrl");

	        Button LongitudeLatitudeDelta=new Button(shell,SWT.NONE);
	        LongitudeLatitudeDelta.setBounds(MapWidth+85,270,125,30);
	        LongitudeLatitudeDelta.addListener(SWT.Selection, new Listener(){
				@Override
				public void handleEvent(Event arg0) {
					// TODO Auto-generated method stub
					try{
						DeviationLongitude=Double.parseDouble(LongitudeEastText.getText());
						DeviationLatitude=Double.parseDouble(LatitudeNorthText.getText());
					}catch(Exception ex){
						ex.printStackTrace();
						return;
					}
				}
	        });
	        LongitudeLatitudeDelta.setText("Data::(Lng→ || Lat↑)");



	        final Text ScreenFlushSetting=new Text(shell,SWT.BORDER);
	        ScreenFlushSetting.setBounds(MapWidth+215,305,70,30);

	        final Button ScreenFlushCheckbox=new Button(shell,SWT.CHECK);
	        ScreenFlushCheckbox.setBounds(MapWidth+10,305,205,30);
	        ScreenFlushCheckbox.setText("Editable:: Flush Screen in [ms]");
	        ScreenFlushCheckbox.addSelectionListener(new SelectionListener(){
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					if(ScreenFlushCheckbox.getSelection()){
						try{
							FlushInterval=Integer.parseInt(ScreenFlushSetting.getText());
						}catch(Exception ex){
							ex.printStackTrace();
							ScreenFlushCheckbox.setSelection(false);
							ScreenFlushCheckbox.redraw();
						}
					}
				}
	        });
	        //----------------------------------------------------------------
	        //HeatMap Compnent

	        final Label GridsRowNumberLabel=new Label(shell,SWT.LEFT);
	        GridsRowNumberLabel.setBounds(MapWidth+10,375,150,20);
	        GridsRowNumberLabel.setText("GridsRowNumber::");
	        GridsRowNumberLabel.setFont(new Font(display,"Consolas",12,SWT.BOLD));//设置文字的字体字号
	        GridsRowNumberLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
	        final Text GridsRowNumberText=new Text(shell,SWT.BORDER);
	        GridsRowNumberText.setBounds(MapWidth+175,375,110,20);

	        final Label GridsColumnNumberLabel=new Label(shell,SWT.LEFT);
	        GridsColumnNumberLabel.setBounds(MapWidth+10,400,150,20);
	        GridsColumnNumberLabel.setText("GridsColNumber::");
	        GridsColumnNumberLabel.setFont(new Font(display,"Consolas",12,SWT.BOLD));//设置文字的字体字号
	        GridsColumnNumberLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
	        final Text GridsColumnNumberText=new Text(shell,SWT.BORDER);
	        GridsColumnNumberText.setBounds(MapWidth+175,400,110,20);

	        final Label RadiationLabel=new Label(shell,SWT.LEFT);
	        RadiationLabel.setBounds(MapWidth+10,425,150,20);
	        RadiationLabel.setText("RadiationLevel::");
	        RadiationLabel.setFont(new Font(display,"Consolas",12,SWT.BOLD));//设置文字的字体字号
	        RadiationLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
	        final Text RadiationText=new Text(shell,SWT.BORDER);
	        RadiationText.setBounds(MapWidth+175,425,110,20);

	        Label FullAlphaLevelLabel=new Label(shell,SWT.LEFT);
	        FullAlphaLevelLabel.setBounds(MapWidth+10,450,150,20);
	        FullAlphaLevelLabel.setText("100% Alpha Level::");
	        FullAlphaLevelLabel.setFont(new Font(display,"Consolas",12,SWT.BOLD));//设置文字的字体字号
	        FullAlphaLevelLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
	        final Text FullAlphaLevelText=new Text(shell,SWT.BORDER);
	        FullAlphaLevelText.setBounds(MapWidth+175,450,110,20);

	        final Button HeatMapCheckBox=new Button(shell,SWT.CHECK);
	        HeatMapCheckBox.setBounds(MapWidth+10,340,250,30);
	        HeatMapCheckBox.setText("Show HeatMap of Points in Screen");
	        HeatMapCheckBox.addSelectionListener(new SelectionListener(){
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub

					if(HeatMapCheckBox.getSelection()){
						try{
							MapKernel.MapWizard.SingleItem.AlphaGridsRow=Integer.parseInt(GridsRowNumberText.getText());
							MapKernel.MapWizard.SingleItem.AlphaGridsColumn=Integer.parseInt(GridsColumnNumberText.getText());
							MapKernel.MapWizard.SingleItem.AlphaPercentScale=Integer.parseInt(FullAlphaLevelText.getText());
							MapKernel.MapWizard.SingleItem.RadiationDistance=Integer.parseInt(RadiationText.getText());
							Mask.redraw();
							canvaspane.redraw();
						}catch(Exception ex){
							ex.printStackTrace();
							HeatMapCheckBox.setSelection(false);
							return;
						}
						MapKernel.MapWizard.SingleItem.IsShowAlphaDistribution=true;
					}else{
						MapKernel.MapWizard.SingleItem.IsShowAlphaDistribution=false;
						MapKernel.MapWizard.SingleItem.Handle.VeilTextArea1();
						MapKernel.MapWizard.SingleItem.Handle.VeilTextArea2();
						MapKernel.MapWizard.SingleItem.Screen.LastLatitudeScale=-1;
						MapKernel.MapWizard.SingleItem.Screen.LastLongitudeScale=-1;
						MapKernel.MapWizard.SingleItem.Screen.LastScreenLatitude=-1000;
						MapKernel.MapWizard.SingleItem.Screen.LastScreenLongitude=-1000;
						MapKernel.MapWizard.SingleItem.Screen.LastAlphaPercentScale=0;
						MapKernel.MapWizard.SingleItem.Screen.LastRadiationDistance=0;
					}
				}
	        });
	        //----------------------------------------------------------------
	        browser=new Browser(shell,SWT.NONE);
	        browser.setBounds(0,0,MapWidth,MapHeight);
	        shell.setSize(browser.getSize().x+310,browser.getSize().y+40);
	        new CallJava(browser,"CallJava");
	        //----------------------------------------------------------------
	        java.io.File HTML_fin=new java.io.File(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("OpenStreetMap_Sample.html"));
	        browser.setUrl(HTML_fin.getAbsolutePath());
	        shell.open();
	        JScode.setText(MapInitCommand = "ResizeMap("+MapWidth+","+MapHeight+");\nmap.fitBounds([[31.05, 121.25],[31.45, 121.70]]);\nMapBoundsToJavaWeb();\nSetDataDeviation();\n");

	        long TimerCounter=java.util.Calendar.getInstance().getTimeInMillis();
			Running = true;
			Accessed = true;
	        while ((Running)&&(!shell.isDisposed())) {
	            if (!display.readAndDispatch()) 
	              display.sleep(); 
	            	if(ScreenFlushCheckbox.getSelection()){
	            		if(java.util.Calendar.getInstance().getTimeInMillis()-TimerCounter>FlushInterval){
	            			TimerCounter=java.util.Calendar.getInstance().getTimeInMillis();
							Mask.redraw();
	            			canvaspane.redraw();
	            		}
	            	}
	          }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			SingleItemThread=null;
			Running = false;
			Accessed = false;
			browser.dispose();
			canvaspane.dispose();
			Mask.dispose();
			shell.dispose();
			display.dispose();
		}
	}
	public static void GoUrlAction(final String URL_str){
		Accessed = false;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				browser.setUrl(URL_str);
			}
		});
		new Thread(new Runnable(){
			public void run(){
				try{
					while(!IsReady()) {
						Thread.sleep(1000);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						browser.execute(MapInitCommand);
					}
				});
			}
		}).start();
	}
	
	public static BufferedImage convertToAWT(ImageData data) {
        ColorModel colorModel = null;
        PaletteData palette = data.palette;
        if (palette.isDirect) {
          colorModel = new DirectColorModel(data.depth, palette.redMask,
              palette.greenMask, palette.blueMask);
          BufferedImage bufferedImage = new BufferedImage(colorModel,
              colorModel.createCompatibleWritableRaster(data.width,
                  data.height), false, null);
          WritableRaster raster = bufferedImage.getRaster();
          int[] pixelArray = new int[3];
          for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
              int pixel = data.getPixel(x, y);
              RGB rgb = palette.getRGB(pixel);
              pixelArray[0] = rgb.red;
              pixelArray[1] = rgb.green;
              pixelArray[2] = rgb.blue;
              raster.setPixels(x, y, 1, 1, pixelArray);
            }
          }
          return bufferedImage;
        } else {
          RGB[] rgbs = palette.getRGBs();
          byte[] red = new byte[rgbs.length];
          byte[] green = new byte[rgbs.length];
          byte[] blue = new byte[rgbs.length];
          for (int i = 0; i < rgbs.length; i++) {
            RGB rgb = rgbs[i];
            red[i] = (byte) rgb.red;
            green[i] = (byte) rgb.green;
            blue[i] = (byte) rgb.blue;
          }
          if (data.transparentPixel != -1) {
            colorModel = new IndexColorModel(data.depth, rgbs.length, red,
                green, blue, data.transparentPixel);
          } else {
            colorModel = new IndexColorModel(data.depth, rgbs.length, red,
                green, blue);
          }
          BufferedImage bufferedImage = new BufferedImage(colorModel,
              colorModel.createCompatibleWritableRaster(data.width,
                  data.height), false, null);
          WritableRaster raster = bufferedImage.getRaster();
          int[] pixelArray = new int[1];
          for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
              int pixel = data.getPixel(x, y);
              pixelArray[0] = pixel;
              raster.setPixel(x, y, pixelArray);
            }
          }
          return bufferedImage;
        }
      }

      public static ImageData convertToSWT(BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
          DirectColorModel colorModel = (DirectColorModel) bufferedImage
              .getColorModel();
          PaletteData palette = new PaletteData(colorModel.getRedMask(),
              colorModel.getGreenMask(), colorModel.getBlueMask());
          ImageData data = new ImageData(bufferedImage.getWidth(),
              bufferedImage.getHeight(), colorModel.getPixelSize(),
              palette);
          WritableRaster raster = bufferedImage.getRaster();
          int[] pixelArray = new int[3];
          for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
              raster.getPixel(x, y, pixelArray);
              int pixel = palette.getPixel(new RGB(pixelArray[0],
                  pixelArray[1], pixelArray[2]));
              data.setPixel(x, y, pixel);
            }
          }
          return data;
        } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
          IndexColorModel colorModel = (IndexColorModel) bufferedImage
              .getColorModel();
          int size = colorModel.getMapSize();
          byte[] reds = new byte[size];
          byte[] greens = new byte[size];
          byte[] blues = new byte[size];
          colorModel.getReds(reds);
          colorModel.getGreens(greens);
          colorModel.getBlues(blues);
          RGB[] rgbs = new RGB[size];
          for (int i = 0; i < rgbs.length; i++) {
            rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF,
                blues[i] & 0xFF);
          }
          PaletteData palette = new PaletteData(rgbs);
          ImageData data = new ImageData(bufferedImage.getWidth(),
              bufferedImage.getHeight(), colorModel.getPixelSize(),
              palette);
          data.transparentPixel = colorModel.getTransparentPixel();
          WritableRaster raster = bufferedImage.getRaster();
          int[] pixelArray = new int[1];
          for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
              raster.getPixel(x, y, pixelArray);
              data.setPixel(x, y, pixelArray[0]);
            }
          }
          return data;
        }
        return null;
      }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		InitiateBrowser();
	}
}
