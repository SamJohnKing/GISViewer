package SecondaryScreen;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Timer;

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


public class SwtHtmlBrowser implements Runnable{
	static int MapWidth=0;
	static int MapHeight=0;
	static double WebLongitudeStart,WebLongitudeEnd,WebLatitudeStart,WebLatitudeEnd;
	static double DeviationLongitude,DeviationLatitude;
	static Browser browser=null;
	public static Thread SingleItemThread=null;
	public static boolean Running=false;
	public static byte AlphaSettingValue=10;
	public static int FlushInterval=500;
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
				}
				return null;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}
	public static void InitiateBrowser(int Width,int Height){
		if(SingleItemThread!=null) return;
		FlushInterval=500;
		DeviationLongitude=0;
		DeviationLatitude=0;
		MapWidth=Width;
		MapHeight=Height;
		SingleItemThread=new Thread(new SwtHtmlBrowser());
		Running=true;
		SingleItemThread.start();
	}
	public static void DBPaint(PaintEvent e){
		browser.execute("MapBoundsToJavaWeb();");
		BufferedImage Alpha_Image=new BufferedImage(MapWidth,MapHeight,BufferedImage.TYPE_INT_ARGB);
		BufferedImage Source_Image=new BufferedImage(MapWidth,MapHeight,BufferedImage.TYPE_INT_RGB);
		Graphics2D g_alpha=Alpha_Image.createGraphics();
		Graphics2D g_2d = Source_Image.createGraphics();
		MapKernel.MapWizard.SingleItem.Screen.DBpaint(g_alpha, WebLongitudeStart-DeviationLongitude, WebLatitudeEnd-DeviationLatitude, 
				WebLongitudeEnd-WebLongitudeStart, WebLatitudeEnd-WebLatitudeStart, MapWidth, MapHeight);
		MapKernel.MapWizard.SingleItem.Screen.DBpaint(g_2d, WebLongitudeStart-DeviationLongitude, WebLatitudeEnd-DeviationLatitude, 
				WebLongitudeEnd-WebLongitudeStart, WebLatitudeEnd-WebLatitudeStart, MapWidth, MapHeight);
		ImageData SWT_ImageData=convertToSWT(Source_Image);
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
					if(Math.abs(r)+Math.abs(g)+Math.abs(b)+Math.abs(a)==0) alphaValues[i*SWT_ImageData.width+j]=(byte)0;
					else alphaValues[i*SWT_ImageData.width+j]=(byte)a;
					
					//if (Math.abs(r) + Math.abs(g) + Math.abs(b) == 0)
					//	alphaValues[i * SWT_ImageData.width + j] = AlphaSettingValue;
					//else
					//	alphaValues[i * SWT_ImageData.width + j] = (byte) a;
				}
				offset += SWT_ImageData.bytesPerLine;
		}
		SWT_ImageData.alphaData=alphaValues;
		//-------------------------------------------------------------
		Image SWT_Image=new Image(null,SWT_ImageData);
		e.gc.drawImage(SWT_Image, 0, 0, MapWidth, MapHeight, 0, 0, MapWidth, MapHeight);
	}
	public static void InitiateBrowser(){
		try{
	       	Display display=new Display(); 
	        final Shell shell=new Shell(display); 
	        shell.setText("GeoCity Secondary Screen"); 
	        //----------------------------------------------------
	        //Canvas
	        final Canvas canvaspane=new Canvas(shell,SWT.TRANSPARENT);
	        canvaspane.setVisible(false);
	        canvaspane.setBounds(0, 0, MapWidth, MapHeight);
	        canvaspane.addPaintListener(new PaintListener(){
				@Override
				public void paintControl(PaintEvent e) {
					// TODO Auto-generated method stub
					DBPaint(e);
				}
	        });
	        canvaspane.addMouseListener(new MouseListener(){
	        	long DownTimestamp=0;
				@Override
				public void mouseDoubleClick(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mouseDown(MouseEvent arg0) {
					// TODO Auto-generated method stub
					DownTimestamp=java.util.Calendar.getInstance().getTimeInMillis();
				}
				@Override
				public void mouseUp(MouseEvent arg0) {
					// TODO Auto-generated method stub
					if(java.util.Calendar.getInstance().getInstance().getTimeInMillis()-DownTimestamp<1000){
						if(MapKernel.MapWizard.SingleItem.NowPanel.getString().equals("MapElementsEditorPane")){
							ExtendedToolPane.ExtendedToolPaneInterface DBEditor=(ExtendedToolPane.ExtendedToolPaneInterface)
									(MapKernel.MapWizard.SingleItem.NowPanel);
							if(arg0.button==1){
								double DBLongitude=WebLongitudeStart+(WebLongitudeEnd-WebLongitudeStart)*arg0.x/MapWidth-DeviationLongitude;
								double DBLatitude=WebLatitudeEnd-(WebLatitudeEnd-WebLatitudeStart)*arg0.y/MapHeight-DeviationLatitude;
								DBEditor.convey(DBLongitude, DBLatitude);
							}else{
								DBEditor.confirm();
							}
						}
					}
				}
	        });
	        
	        final Canvas Mask=new Canvas(shell,SWT.TRANSPARENT);
	        Mask.setVisible(false);
	        Mask.setBounds(0, 0, MapWidth, MapHeight);
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
	       	UnLockButton.addListener(SWT.Selection, new Listener(){
	            public void handleEvent(Event event) 
	            { 
	            	canvaspane.setVisible(false);
	            	Mask.setVisible(false);
	            } 
	       	});
	       	UnLockButton.setBounds(MapWidth+10, 5, 100, 30);
	       	UnLockButton.setText("UnLock Map");
	       	
	       	Button LockButton=new Button(shell,SWT.NONE);
	       	LockButton.addListener(SWT.Selection, new Listener(){
	            public void handleEvent(Event event) 
	            { 
	            	canvaspane.setVisible(true);
	            	Mask.setVisible(true);
	            } 
	       	});
	       	LockButton.setBounds(MapWidth+120, 5, 160, 30);
	       	LockButton.setText("Lock Map Show Data");
	       	
	       	Button Execute_JSCode=new Button(shell,SWT.NONE);
	       	Execute_JSCode.addListener(SWT.Selection, new Listener(){
	            public void handleEvent(Event event) 
	            { 
	            	browser.execute(JScode.getText());
	            } 
	       	});
	       	Execute_JSCode.setBounds(MapWidth+10,165,200,30);
	       	Execute_JSCode.setText("Execute JavaScript Code");
	       	
	       	Button Setting_Alpha=new Button(shell,SWT.NONE);
	       	Setting_Alpha.addListener(SWT.Selection, new Listener(){
	            public void handleEvent(Event event) 
	            { 
	            	try{
	            		AlphaSettingValue=(byte)(Integer.parseInt(AlphaSettingText.getText())%256);
	            		Mask.setVisible(true);
	            		Mask.redraw();
	            	}catch(Exception ex){
	            		ex.printStackTrace();
	            		return;
	            	}
	            } 
	       	});
	       	Setting_Alpha.setBounds(MapWidth+10,200,200,30);
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
	        browser=new Browser(shell,SWT.NONE); 
	        browser.setBounds(0,0,MapWidth,MapHeight); 
	        shell.setSize(browser.getSize().x+310,browser.getSize().y+40);
	        new CallJava(browser,"CallJava");
	        //----------------------------------------------------------------
	        java.io.File HTML_fin=new java.io.File("./OpenStreetMap_Sample.html");
	        browser.setUrl(HTML_fin.getAbsolutePath());
	        shell.open();
	        JScode.setText("ResizeMap("+MapWidth+","+MapHeight+")");
	        
	        long TimerCounter=java.util.Calendar.getInstance().getTimeInMillis();
	        while ((Running)&&(!shell.isDisposed())) { 
	            if (!display.readAndDispatch()) 
	              display.sleep(); 
	            	if(ScreenFlushCheckbox.getSelection()){
	            		if(java.util.Calendar.getInstance().getTimeInMillis()-TimerCounter>FlushInterval){
	            			TimerCounter=java.util.Calendar.getInstance().getTimeInMillis();
	            			canvaspane.redraw();
	            		}
	            	}
	          } 
	          display.dispose(); 
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			SingleItemThread=null;
		}
	}
	public static void GoUrlAction(String URL_str){
		browser.setUrl(URL_str);
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
