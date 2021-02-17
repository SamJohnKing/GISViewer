package ExtendedToolPane;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import LWJGLPackage.OriginalOpenGLWizard;
import MapKernel.*;
import SecondaryScreen.SwtHtmlBrowser;
import com.sun.javafx.css.Stylesheet;
import org.eclipse.swt.widgets.Display;
import sun.plugin.liveconnect.OriginNotAllowedException;

public class ServerSocketPaneClass extends ToolPanel implements ExtendedToolPaneInterface, ActionListener, ItemListener {
    MapControl MainHandle;
    double CursorLongitude, CursorLatitude;

    public String getString() {
        return "ServerSocketPane";
    }

    public ServerSocketPaneClass GetSelf(){
        return this;
    }

    public void setHandle(MapControl MainHandle) {
        this.MainHandle = MainHandle;
        ServerHandle.setHandle(MainHandle);
        ServerHandle.setProcessor(this);
    }

    public ServerSocketPaneClass() {
        JLabel Title = new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("ServerSocketPane"));
        Title.setFont(new Font("华文新魏", Font.BOLD, 30));
        Title.setForeground(Color.orange);
        add(Title);
        add(ScreenLockButton);
        ScreenLockButton.addActionListener(this);
        add(ScreenUnLockButton);
        ScreenUnLockButton.addActionListener(this);
        SpecificProcess();
    }

    public void paintComponent(Graphics g) {
        Toolkit kit = getToolkit();
        Image img = kit.getImage(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("BackGround21.jpg"));
        g.drawImage(img, 0, 0, 280, 680, this);
    }

    public void setLongitudeLatitude(double x, double y) {
    }

    public void setLongitude(double x) {
    }

    ;

    public void setLatitude(double y) {
    }

    ;

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == ScreenLockButton) {
                MainHandle.ScreenLock(true);
                ScreenLockButton.setEnabled(false);
                ScreenUnLockButton.setEnabled(true);
            } else if (e.getSource() == ScreenUnLockButton) {
                MainHandle.ScreenLock(false);
                ScreenLockButton.setEnabled(true);
                ScreenUnLockButton.setEnabled(false);
            } else if (e.getSource() == StartButton) {
                try {
                    // TODO Auto-generated method stub
                    ServerHandle.StartSocket(Integer.parseInt(SocketNumField.getText()));
                } catch (Exception ex) {
                    MainHandle.SolveException(ex);
                }
            } else if (e.getSource() == EndButton) {
                ServerHandle.EndSocket();
            }
        } catch (Exception ex) {
            MainHandle.SolveException(ex);
        }
    }

    //Specific Part--------------------------------------------
    public static InternetHandle.ServerHandle ServerHandle;
    JButton StartButton, EndButton;
    JTextField SocketNumField;
    JCheckBox IsProcessingTransaction;
    public JCheckBox ShowSQLInTerminal;

    public void SpecificProcess() {
        ServerHandle = new InternetHandle.ServerHandle();
        ServerHandle.setHandle(MainHandle);
        ServerHandle.setProcessor(this);
        JLabel port = new JLabel(MapKernel.MapWizard.LanguageDic.GetWords("ServerSocketPort"));
        port.setFont(new Font("华文新魏", Font.BOLD, 16));
        port.setForeground(Color.orange);
        add(port);
        SocketNumField = new JTextField(10);
        add(SocketNumField);
        IsProcessingTransaction = new JCheckBox(MapKernel.MapWizard.LanguageDic.GetWords("Order Server to Process Transaction"));
        IsProcessingTransaction.setSelected(true);
        IsProcessingTransaction.setOpaque(false);
        IsProcessingTransaction.setForeground(Color.orange);
        add(IsProcessingTransaction);
        ShowSQLInTerminal = new JCheckBox(MapWizard.LanguageDic.GetWords("Show CMD SQL Query & Result"));
        ShowSQLInTerminal.setSelected(true);
        ShowSQLInTerminal.setOpaque(false);
        ShowSQLInTerminal.setForeground(Color.orange);
        add(ShowSQLInTerminal);
        StartButton = new JButton(MapKernel.MapWizard.LanguageDic.GetWords("ServerStart"));
        add(StartButton);
        StartButton.addActionListener(this);
        EndButton = new JButton(MapKernel.MapWizard.LanguageDic.GetWords("ServerStop"));
        add(EndButton);
        EndButton.addActionListener(this);
    }

    public void itemStateChanged(ItemEvent e) {
    }

    public void emerge() {
        //-----------------
        //-----------------
    }

    public void convey(double x, double y) {
        JOptionPane.showMessageDialog(null,MapKernel.MapWizard.LanguageDic.GetWords("ConveyPoint ( " + x + " , " + y + " )"));
    }

    public void convey(double x1, double y1, double x2, double y2) {
        JOptionPane.showMessageDialog(null, MapKernel.MapWizard.LanguageDic.GetWords("ConveyRectangle"));
    }

    @Override
    public void confirm() {
        JOptionPane.showMessageDialog(null, MapKernel.MapWizard.LanguageDic.GetWords("ConfirmFunction"));
        // TODO Auto-generated method stub
    }

    private int SocketTransactionCounter = 0;
    private double[] SocketX = new double[100000];
    private double[] SocketY = new double[100000];
    private int SocketXYCounter;

    public String GetSocketResult(String SocketQuery) {
        SocketQuery = SocketQuery.trim();
        if (!IsProcessingTransaction.isSelected()) return "Success::NotProcessing";
        if (MainHandle.CheckAdminBackendFixing()) return "Success::AdminBackendFixing";
        SocketTransactionCounter++;
        SocketTransactionCounter %= 100000000;
        // TODO Auto-generated method stub
        if(SocketQuery.startsWith("CommandSequence>>")){
            SocketQuery = SocketQuery.substring("CommandSequence>>".length());
            String[] SocketQueryList = SocketQuery.split("CommandSequence>>");
            StringBuilder ResultList = new StringBuilder("");
            for(String QueryPtr : SocketQueryList){
                ResultList.append((ResultList.length() == 0 ? "ResultSequence>>" : "\nResultSequence>>"));
                ResultList.append(CommandLineExec(QueryPtr));
            }
            return ResultList.toString();
        } else return CommandLineExec(SocketQuery);
    }

    public String CommandLineExec(String SocketQuery) {
        SocketQuery = SocketQuery.trim();
        int pos = SocketQuery.indexOf("::"); /** SocketQuery Format: Command::Param1#Param2#Param3#.... */
        int _pos;
        if (pos == -1) return "Fail::No Command before ::";
        if (SocketQuery.equals("Ping::")) return "Ping::";
        Database.PointDataSet PointDB = MainHandle.getPointDatabase();
        Database.LineDataSet LineDB = MainHandle.getLineDatabase();
        Database.PolygonDataSet PolygonDB = MainHandle.getPolygonDatabase();
        try {
            String Command = SocketQuery.substring(0, pos).trim();
            String str = SocketQuery.substring(pos + 2).trim();
            if (Command.equals("InsertPoint")) {
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                double x = Double.parseDouble(str.substring(0, pos));
                double y = Double.parseDouble(str.substring(pos + 1, _pos));
                PointDB.add(x, y, "[Title:][SocketInsert:][Transaction:" + SocketTransactionCounter + "]");
                return "Success::";
            } else if (Command.equals("InsertStylePoint")) { /** Command::Style#Data */
                int pos1 = str.indexOf('#');
                int pos2 = str.indexOf('#', pos1 + 1);
                int pos3 = str.indexOf('#', pos2 + 1);
                String Style = str.substring(0, pos1) + "[SocketInsert:][Transaction:" + SocketTransactionCounter + "]";
                double x = Double.parseDouble(str.substring(pos1 + 1, pos2));
                double y = Double.parseDouble(str.substring(pos2 + 1, pos3));
                PointDB.add(x, y, Style);
                return "Success::";
            } else if (Command.equals("InsertLine")) {
                _pos = -1;
                SocketXYCounter = 0;
                while ((pos = str.indexOf('#', _pos + 1)) != -1) {
                    SocketX[SocketXYCounter] = Double.parseDouble(str.substring(_pos + 1, pos));
                    _pos = str.indexOf('#', pos + 1);
                    SocketY[SocketXYCounter] = Double.parseDouble(str.substring(pos + 1, _pos));
                    SocketXYCounter++;
                }
                LineDB.add(SocketX, SocketY, SocketXYCounter, "[Title:][SocketInsert:][Transaction:" + SocketTransactionCounter + "]");
                return "Success::";
            } else if (Command.equals("InsertStyleLine")) { /** Command::Style#Data */
                int pos1 = str.indexOf('#');
                String Style = str.substring(0, pos1) + "[SocketInsert:][Transaction:" + SocketTransactionCounter + "]";
                _pos = pos1;
                SocketXYCounter = 0;
                while ((pos = str.indexOf('#', _pos + 1)) != -1) {
                    SocketX[SocketXYCounter] = Double.parseDouble(str.substring(_pos + 1, pos));
                    _pos = str.indexOf('#', pos + 1);
                    SocketY[SocketXYCounter] = Double.parseDouble(str.substring(pos + 1, _pos));
                    SocketXYCounter++;
                }
                LineDB.add(SocketX, SocketY, SocketXYCounter, Style);
                return "Success::";
            } else if (Command.equals("InsertPolygon")) {
                _pos = -1;
                SocketXYCounter = 0;
                while ((pos = str.indexOf('#', _pos + 1)) != -1) {
                    SocketX[SocketXYCounter] = Double.parseDouble(str.substring(_pos + 1, pos));
                    _pos = str.indexOf('#', pos + 1);
                    SocketY[SocketXYCounter] = Double.parseDouble(str.substring(pos + 1, _pos));
                    SocketXYCounter++;
                }
                PolygonDB.add(SocketX, SocketY, SocketXYCounter, "[Title:][SocketInsert:][Transaction:" + SocketTransactionCounter + "]");
                return "Success::";
            } else if (Command.equals("InsertStylePolygon")) { /** Command::Style#Data */
                int pos1 = str.indexOf('#');
                String Style = str.substring(0, pos1) + "[SocketInsert:][Transaction:" + SocketTransactionCounter + "]";
                _pos = pos1;
                SocketXYCounter = 0;
                while ((pos = str.indexOf('#', _pos + 1)) != -1) {
                    SocketX[SocketXYCounter] = Double.parseDouble(str.substring(_pos + 1, pos));
                    _pos = str.indexOf('#', pos + 1);
                    SocketY[SocketXYCounter] = Double.parseDouble(str.substring(pos + 1, _pos));
                    SocketXYCounter++;
                }
                PolygonDB.add(SocketX, SocketY, SocketXYCounter, Style);
                return "Success::";
            } else if(Command.equals("SetDefaultVisible")){
                int PointVisible = 7; int LineVisible = 27; int PolygonVisible = 11;
                if(!str.isEmpty()) {
                    String[] vis = str.split("#");
                    if(!vis[0].isEmpty()) PointVisible = Integer.parseInt(vis[0]);
                    if((vis.length > 1) && (!vis[1].isEmpty())) LineVisible = Integer.parseInt(vis[1]);
                    if((vis.length > 2) && (!vis[2].isEmpty())) PolygonVisible = Integer.parseInt(vis[2]);
                }
                PointDB.DefaultVisible = PointVisible; LineDB.DefaultVisible = LineVisible; PolygonDB.DefaultVisible = PolygonVisible;
                return "Success::";
            } else if (Command.equals("DeletePointAll")) {
                PointDB.AttributeDelete("[SocketInsert:]", null, null, null, null);
                return "Success::";
            } else if (Command.equals("DeleteLineAll")) {
                LineDB.AttributeDelete("[SocketInsert:]", null, null, null, null);
                return "Success::";
            } else if (Command.equals("DeletePolygonAll")) {
                PolygonDB.AttributeDelete("[SocketInsert:]", null, null, null, null);
                return "Success::";
            } else if (Command.equals("DeletePoint")) {  /** SocketQuery Format: Command::x1#y1#x2#y2#Key1:Value1#Key2:Value2#Key3:Value3#... */
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String[] KeyValueParams = new String[]{null, null, null, null, null};
                for(int KeyValuePtr = 4; KeyValuePtr < Params.length; KeyValuePtr++) {
                    if(KeyValuePtr == 9) break;
                    KeyValueParams[KeyValuePtr - 4] = (Params[KeyValuePtr] == null ? null : Params[KeyValuePtr].trim());
                }
                PointDB.KeyValueDelete(x1, y1, x2, y2, KeyValueParams[0], KeyValueParams[1], KeyValueParams[2], KeyValueParams[3], KeyValueParams[4]);
                return "Success::";
            } else if (Command.equals("DeleteLine")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String[] KeyValueParams = new String[]{null, null, null, null, null};
                for(int KeyValuePtr = 4; KeyValuePtr < Params.length; KeyValuePtr++) {
                    if(KeyValuePtr == 9) break;
                    KeyValueParams[KeyValuePtr - 4] = (Params[KeyValuePtr] == null ? null : Params[KeyValuePtr].trim());
                }
                LineDB.KeyValueDelete(x1, y1, x2, y2, KeyValueParams[0], KeyValueParams[1], KeyValueParams[2], KeyValueParams[3], KeyValueParams[4]);
                return "Success::";
            } else if (Command.equals("DeletePolygon")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String[] KeyValueParams = new String[]{null, null, null, null, null};
                for(int KeyValuePtr = 4; KeyValuePtr < Params.length; KeyValuePtr++) {
                    if(KeyValuePtr == 9) break;
                    KeyValueParams[KeyValuePtr - 4] = (Params[KeyValuePtr] == null ? null : Params[KeyValuePtr].trim());
                }
                PolygonDB.KeyValueDelete(x1, y1, x2, y2, KeyValueParams[0], KeyValueParams[1], KeyValueParams[2], KeyValueParams[3], KeyValueParams[4]);
                return "Success::";
            } else if (Command.equals("UpdatePoint")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String UpdateWhat = Params[4];
                if(UpdateWhat.isEmpty()) return "Success";
                PointDB.KeyValueUpdate(x1, y1, x2, y2, UpdateWhat);
                return "Success::";
            } else if(Command.equals("PrimaryKeyUpdatePoint")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String PrimaryKey = Params[4];
                String UpdateWhat = Params[5];
                if(UpdateWhat.isEmpty()) return "Success";
                PointDB.PrimaryKeyValueUpdate(x1, y1, x2, y2, PrimaryKey, UpdateWhat);
                return "Success::";
            } else if (Command.equals("UpdateLine")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String UpdateWhat = Params[4];
                if(UpdateWhat.isEmpty()) return "Success";
                LineDB.KeyValueUpdate(x1, y1, x2, y2, UpdateWhat);
                return "Success::";
            } else if (Command.equals("PrimaryKeyUpdateLine")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String PrimaryKey = Params[4];
                String UpdateWhat = Params[5];
                if(UpdateWhat.isEmpty()) return "Success";
                LineDB.PrimaryKeyValueUpdate(x1, y1, x2, y2, PrimaryKey, UpdateWhat);
                return "Success::";
            } else if (Command.equals("UpdatePolygon")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String UpdateWhat = Params[4];
                if(UpdateWhat.isEmpty()) return "Success";
                PolygonDB.KeyValueUpdate(x1, y1, x2, y2, UpdateWhat);
                return "Success::";
            } else if (Command.equals("PrimaryKeyUpdatePolygon")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String PrimaryKey = Params[4];
                String UpdateWhat = Params[5];
                if(UpdateWhat.isEmpty()) return "Success";
                PolygonDB.PrimaryKeyValueUpdate(x1, y1, x2, y2, PrimaryKey, UpdateWhat);
                return "Success::";
            } else if (Command.equals("QueryPoint")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String[] KeyValueParams = new String[]{null, null, null, null, null};
                for(int KeyValuePtr = 4; KeyValuePtr < Params.length; KeyValuePtr++) {
                    if(KeyValuePtr == 9) break;
                    KeyValueParams[KeyValuePtr - 4] = (Params[KeyValuePtr] == null ? null : Params[KeyValuePtr].trim());
                }
                Vector<HashMap<String, Object>> res = PointDB.KeyValueQuery(x1, y1, x2, y2, KeyValueParams[0], KeyValueParams[1], KeyValueParams[2], KeyValueParams[3], KeyValueParams[4]);
                StringBuilder ResStr = new StringBuilder("IDHashCode # Hint # X # Y #\n");
                for (HashMap<String, Object> HashItem : res) {
                    ResStr.append(HashItem.get("IDHashCode") + "#" + HashItem.get("Hint") + "#" + HashItem.get("X") + "#" + HashItem.get("Y") + "#\n");
                }
                ResStr.append("End::");
                return ResStr.toString();
            } else if (Command.equals("QueryLine")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String[] KeyValueParams = new String[]{null, null, null, null, null};
                for(int KeyValuePtr = 4; KeyValuePtr < Params.length; KeyValuePtr++) {
                    if(KeyValuePtr == 9) break;
                    KeyValueParams[KeyValuePtr - 4] = (Params[KeyValuePtr] == null ? null : Params[KeyValuePtr].trim());
                }
                Vector<HashMap<String, Object>> res = LineDB.KeyValueQuery(x1, y1, x2, y2, KeyValueParams[0], KeyValueParams[1], KeyValueParams[2], KeyValueParams[3], KeyValueParams[4]);
                StringBuilder ResStr = new StringBuilder("IDHashCode # Hint # X # Y #\n");
                for (HashMap<String, Object> HashItem : res) {
                    ResStr.append(HashItem.get("IDHashCode") + "#" + HashItem.get("Hint") + "#");
                    Vector<Double> XArr = (Vector<Double>)HashItem.get("X");
                    Vector<Double> YArr = (Vector<Double>)HashItem.get("Y");
                    int XYLength = XArr.size();
                    for(int ptr = 0; ptr < XYLength; ptr++) {
                        ResStr.append(XArr.elementAt(ptr) + "#" + YArr.elementAt(ptr) + "#");
                    }
                    ResStr.append("\n");
                }
                ResStr.append("End::");
                return ResStr.toString();
            } else if (Command.equals("QueryPolygon")) {
                String[] Params = str.split("#");
                double x1 = Double.parseDouble(Params[0]);
                double y1 = Double.parseDouble(Params[1]);
                double x2 = Double.parseDouble(Params[2]);
                double y2 = Double.parseDouble(Params[3]);
                String[] KeyValueParams = new String[]{null, null, null, null, null};
                for(int KeyValuePtr = 4; KeyValuePtr < Params.length; KeyValuePtr++) {
                    if(KeyValuePtr == 9) break;
                    KeyValueParams[KeyValuePtr - 4] = (Params[KeyValuePtr] == null ? null : Params[KeyValuePtr].trim());
                }
                Vector<HashMap<String, Object>> res = PolygonDB.KeyValueQuery(x1, y1, x2, y2, KeyValueParams[0], KeyValueParams[1], KeyValueParams[2], KeyValueParams[3], KeyValueParams[4]);
                StringBuilder ResStr = new StringBuilder("IDHashCode # Hint # X # Y #\n");
                for (HashMap<String, Object> HashItem : res) {
                    ResStr.append(HashItem.get("IDHashCode") + "#" + HashItem.get("Hint") + "#");
                    Vector<Double> XArr = (Vector<Double>)HashItem.get("X");
                    Vector<Double> YArr = (Vector<Double>)HashItem.get("Y");
                    int XYLength = XArr.size();
                    for(int ptr = 0; ptr < XYLength; ptr++) {
                        ResStr.append(XArr.elementAt(ptr) + "#" + YArr.elementAt(ptr) + "#");
                    }
                    ResStr.append("\n");
                }
                ResStr.append("End::");
                return ResStr.toString();
            } else if (Command.equals("ScreenFlush")) {
                MainHandle.ScreenFlush();
                if ((SwtHtmlBrowser.SingleItemThread != null) && SwtHtmlBrowser.Accessed) {
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (SwtHtmlBrowser.shell.isVisible())
                                    SwtHtmlBrowser.ScreenFlush();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    //Thread.sleep(500);
                }
                return "Success::";
            } else if (Command.equals("MoveMiddle")) { //Move前先设定Scale,否则失焦！
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                final double x = Double.parseDouble(str.substring(0, pos));
                final double y = Double.parseDouble(str.substring(pos + 1, _pos));
                MainHandle.getKernel().Screen.MoveMiddle(x, y);
//                if (OriginalOpenGLWizard.SingleItem != null)
//                    OriginalOpenGLWizard.SingleItem.MoveMiddle(x, y);
//                if ((SwtHtmlBrowser.SingleItemThread != null) && SwtHtmlBrowser.Accessed) {
//                    Display.getDefault().syncExec(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                if (SwtHtmlBrowser.shell.isVisible())
//                                    SwtHtmlBrowser.MoveMiddle(x, y);
//                            } catch (Exception ex){
//                                ex.printStackTrace();
//                            }
//                        }
//                    });
//                    Thread.sleep(500);
//                }
                return "Success::";
            } else if (Command.equals("OpenLWJGLScreen")) {
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                final int width = Integer.parseInt(str.substring(0, pos));
                final int height = Integer.parseInt(str.substring(pos + 1, _pos));
                OriginalOpenGLWizard.SocketGetInstance = true;
                String res = OriginalOpenGLWizard.GetInstance(width, height);
                OriginalOpenGLWizard.SocketGetInstance = false;
                return res;
            } else if (Command.equals("SetSwingXScaleYScale")){
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                double XScale = Double.parseDouble(str.substring(0, pos));
                double YScale = Double.parseDouble(str.substring(pos + 1, _pos));
                MainHandle.getKernel().Screen.LongitudeScale = XScale;
                MainHandle.getKernel().Screen.LatitudeScale = YScale;
                return "Success::";
            } else if (Command.equals("SetOpenGLFPS")) {
                if(OriginalOpenGLWizard.SingleItem != null) OriginalOpenGLWizard.DefaultFPS = Integer.parseInt(str);
                return "Success::";
            } else if (Command.equals("SetOpenGLXScaleYScale")){
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                double XScale = Double.parseDouble(str.substring(0, pos));
                double YScale = Double.parseDouble(str.substring(pos + 1, _pos));
                if (OriginalOpenGLWizard.SingleItem != null){
                    OriginalOpenGLWizard.SingleItem.LongitudeScale = XScale;
                    OriginalOpenGLWizard.SingleItem.LatitudeScale = YScale;
                }
                return "Success::";
            } else if (Command.equals("SetOpenGLBackgroundColor")) { //RGB 0-255 0-255 0-255
                int pos1 = str.indexOf('#');
                if(pos1 == -1) {
                    OriginalOpenGLWizard.DefaultBackgroundColor = new Color(0, 0, 0);
                    return "Success::";
                }
                int pos2 = str.indexOf('#', pos1 + 1);
                int pos3 = str.indexOf('#', pos2 + 1);
                Integer r = Integer.parseInt(str.substring(0, pos1));
                Integer g = Integer.parseInt(str.substring(pos1 + 1, pos2));
                Integer b = Integer.parseInt(str.substring(pos2 + 1, pos3));
                OriginalOpenGLWizard.DefaultBackgroundColor = new Color(r, g, b);
                return "Success::";
            } else if (Command.equals("SetOpenGLForegroundColor")) { //RGB 0-255 0-255 0-255 if none then use the hint color
                int pos1 = str.indexOf('#');
                if(pos1 == -1) {
                    OriginalOpenGLWizard.UsingDefaultForegroundColor = false;
                    return "Success::";
                }
                int pos2 = str.indexOf('#', pos1 + 1);
                int pos3 = str.indexOf('#', pos2 + 1);
                Integer r = Integer.parseInt(str.substring(0, pos1));
                Integer g = Integer.parseInt(str.substring(pos1 + 1, pos2));
                Integer b = Integer.parseInt(str.substring(pos2 + 1, pos3));
                OriginalOpenGLWizard.DefaultForegroundColor = new Color(r, g, b);
                OriginalOpenGLWizard.UsingDefaultForegroundColor = true;
                return "Success::";
            } else if (Command.equals("SetOpenGLScreenCacheLife")){
                OriginalOpenGLWizard.DefaultScreenCacheLife = Integer.parseInt(str);
                return "Success::";
            } else if (Command.equals("PrintlnOpenGLRenderTimeSwitch")){
                OriginalOpenGLWizard.PrintlnOpenGLRenderTime = !OriginalOpenGLWizard.PrintlnOpenGLRenderTime;
                return "Success::" + OriginalOpenGLWizard.PrintlnOpenGLRenderTime;
            } else if (Command.equals("SetOpenGLPointSize")){
                OriginalOpenGLWizard.DefaultPointSize = Integer.parseInt(str);
                return "Success::";
            } else if (Command.equals("SetOpenGLLineWidth")){
                OriginalOpenGLWizard.DefaultLineWidth = Integer.parseInt(str);
                return "Success::";
            } else if (Command.equals("ShowTextArea1")) {
                MainHandle.ShowTextArea1(str, true);
                return "Success::";
            } else if (Command.equals("ShowTextArea2")) {
                MainHandle.ShowTextArea2(str, true);
                return "Success::";
            } else if (Command.equals("VeilTextArea1")) {
                MainHandle.VeilTextArea1();
                return "Success::";
            } else if (Command.equals("VeilTextArea2")) {
                MainHandle.VeilTextArea2();
                return "Success::";
            } else if (Command.equals("ShowMessageDialog")) {
                JOptionPane.showMessageDialog(null, str);
                return "Success::";
            } else if (Command.equals("ShowConfirmDialog")) {
                int n = JOptionPane.showConfirmDialog(null, str, "做出选择", JOptionPane.OK_OPTION);
                if (n == JOptionPane.OK_OPTION) return "Success::";
                else return "Abort::";
            } else if (Command.equals("ShowInputDialog")) {
                return "Success::" + JOptionPane.showInputDialog(null, str, "输入信息", JOptionPane.PLAIN_MESSAGE);
            } else if (Command.equals("InputPoints")) {
                MainHandle.getPointDatabase().DatabaseFileInput(new File(str));
                return "Success::";
            } else if (Command.equals("InputLines")) {
                MainHandle.getLineDatabase().DatabaseFileInput(new File(str));
                return "Success::";
            } else if (Command.equals("InputPolygons")) {
                MainHandle.getPolygonDatabase().DatabaseFileInput(new File(str));
                return "Success::";
            } else if (Command.equals("OutputPoints")) {
                MainHandle.getPointDatabase().DatabaseFileOutput(new File(str));
                return "Success::";
            } else if (Command.equals("OutputLines")) {
                MainHandle.getLineDatabase().DatabaseFileOutput(new File(str));
                return "Success::";
            } else if (Command.equals("OutputPolygons")) {
                MainHandle.getPolygonDatabase().DatabaseFileOutput(new File(str));
                return "Success::";
            } else if (Command.equals("OutputScreen")) {
                MainHandle.getKernel().ScreenPNGOutput(str);
                return "Success::";
            } else if (Command.equals("AlphaDrawer")) { //热力图绘制会抢占Screen数组资源，造成多线程冲突，慎用！
                MainHandle.getKernel().Screen.AlphaDrawer(str);
                return "Success::";
            } else if (Command.equals("BrowserPNGCapture")){
                if ((SwtHtmlBrowser.SingleItemThread != null) && SwtHtmlBrowser.Accessed) {
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (SwtHtmlBrowser.shell.isVisible())
                                    System.out.println(SwtHtmlBrowser.CaptureMaptoPNG(false));
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    return "Success::" + SwtHtmlBrowser.PNGPath;
                } else return "Fail::NoBrowser";
            } else if (Command.equals("OpenBrowser")) {
                if(SwtHtmlBrowser.SingleItemThread != null) return "Fail::Another Instance!";
                int width = 800;
                int height = 600;
                pos = str.indexOf("#");
                _pos = str.indexOf("#", pos + 1);
                if(pos != -1){
                    width = Integer.parseInt(str.substring(0, pos));
                    height = Integer.parseInt(str.substring(pos + 1, _pos));
                }
                SwtHtmlBrowser.SocketOpenWaiting = true;
                SwtHtmlBrowser.GetInstance(width, height);
                return "Success::";
            } else if (Command.equals(("BrowserExecJs"))) {
                final String JsCode = str;
                if ((SwtHtmlBrowser.SingleItemThread != null) && SwtHtmlBrowser.Accessed) {
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (SwtHtmlBrowser.shell.isVisible()) SwtHtmlBrowser.browser.execute(JsCode);
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    return "Success::";
                } else return "Fail::NoBrowser";
            } else if (Command.equals("CloseBrowser")) {
                if(SwtHtmlBrowser.SingleItemThread == null) return "Fail::NoBrowser";
                SwtHtmlBrowser.CloseHandle.shellClosed(null);
                SwtHtmlBrowser.SocketOpenWaiting = false;
                return "Success::";
            } else if (Command.equals("OpenGLPNGCapture")){
                if(OriginalOpenGLWizard.SingleItem == null) return "Fail::No LWJGL Instance!";
                OriginalOpenGLWizard.SingleItem.GetScreenShot = true;
                OriginalOpenGLWizard.SingleItem.ScreenShotFilePath = str;
                while (OriginalOpenGLWizard.SingleItem.GetScreenShot) Thread.sleep(20);
                return "Success::" + OriginalOpenGLWizard.SingleItem.ScreenShotFilePath;
            } else if (Command.equals("SetBufferImageNumber")) {
                OriginalOpenGLWizard.DefaultBufferImageNumber = Integer.parseInt(str);
                return "Success::";
            } else if (Command.equals("SetOpenGLAllowAbsPixel")) {
                OriginalOpenGLWizard.AllowAbsPixelPoint = Boolean.parseBoolean(str);
                return "Success::" + OriginalOpenGLWizard.AllowAbsPixelPoint;
            } else if (Command.equals("SetOpenGLAllowPNG")) {
                OriginalOpenGLWizard.AllowPNGPoint = Boolean.parseBoolean(str);
                return "Success::" + OriginalOpenGLWizard.AllowPNGPoint;
            } else if (Command.equals("Sleep")) {
                Thread.sleep(Integer.parseInt(str));
                return "Success::";
            } else if (Command.equals("SetTitle")) {
                MapWizard.SingleItem.setTitle(str);
                return "Success::";
            } else if (Command.equals("ListenLWJGLKey")) {
                Double WaitSecond = 30.0;
                if(!str.isEmpty()) WaitSecond = Double.parseDouble(str);
                if(OriginalOpenGLWizard.SingleItem == null) return "Fail::No LWJGL Instance!";
                OriginalOpenGLWizard.SingleItem.isSocketKeyListen = true;
                int SleepCount = 0;
                while(OriginalOpenGLWizard.SingleItem.isSocketKeyListen) {
                    SleepCount++;
                    Thread.sleep(20);
                    if(SleepCount * 20 > WaitSecond * 1000) break;
                }
                String res = "NoKey::";
                if(SleepCount * 20 <= WaitSecond * 1000) res = "Key::" + OriginalOpenGLWizard.SingleItem.SocketKeyID + "#" + OriginalOpenGLWizard.SingleItem.SocketKeyChar;
                OriginalOpenGLWizard.SingleItem.SocketKeyChar = '!';
                OriginalOpenGLWizard.SingleItem.SocketKeyID = -1;
                return res ;
            } else if (Command.equals("ListenClick")) {
                Double WaitSecond = 30.0;
                if(!str.isEmpty()) WaitSecond = Double.parseDouble(str);
                ExternalListener OriginalListener = MapWizard.SingleItem.BehaviorListener;
                MapWizard.SingleItem.ClickBehaviorListened = null;
                MapWizard.SingleItem.BehaviorListener = new ExternalListener() {
                    @Override
                    public void MousePressedListener(double x, double y, int xPixel, int yPixel, boolean isLeft) {
                        MapWizard.SingleItem.ClickBehaviorListened = Double.toString(x) + "#" + Double.toString(y) + "#" + Integer.toString(xPixel) + "#" + Integer.toString(yPixel) + "#" + (isLeft?"L":"R") + "#";
                    }
                };
                int SleepCount = 0;
                while(MapWizard.SingleItem.ClickBehaviorListened == null) {
                    SleepCount++;
                    Thread.sleep(20);
                    if(SleepCount * 20 > WaitSecond * 1000) break;
                }
                String res = "NoClick::";
                MapWizard.SingleItem.BehaviorListener = OriginalListener;
                if(SleepCount * 20 <= WaitSecond * 1000) res = "Click::" + MapWizard.SingleItem.ClickBehaviorListened;
                MapWizard.SingleItem.ClickBehaviorListened = null;
                return res;
            } else if (Command.equals("ListenClick&Key")) {
                Double WaitSecond = 30.0;
                if(!str.isEmpty()) WaitSecond = Double.parseDouble(str);
                ExternalListener OriginalListener = MapWizard.SingleItem.BehaviorListener;
                MapWizard.SingleItem.ClickBehaviorListened = null;

                if(OriginalOpenGLWizard.SingleItem != null) OriginalOpenGLWizard.SingleItem.isSocketKeyListen = true;

                MapWizard.SingleItem.BehaviorListener = new ExternalListener() {
                    @Override
                    public void MousePressedListener(double x, double y, int xPixel, int yPixel, boolean isLeft) {
                        MapWizard.SingleItem.ClickBehaviorListened = Double.toString(x) + "#" + Double.toString(y) + "#" + Integer.toString(xPixel) + "#" + Integer.toString(yPixel) + "#" + (isLeft?"L":"R") + "#";
                    }
                };
                int SleepCount = 0;
                while((MapWizard.SingleItem.ClickBehaviorListened == null) && ((OriginalOpenGLWizard.SingleItem == null) ? true : OriginalOpenGLWizard.SingleItem.isSocketKeyListen)) {
                    SleepCount++;
                    Thread.sleep(20);
                    if(SleepCount * 20 > WaitSecond * 1000) break;
                }
                String res = "NoClick&Key::";
                MapWizard.SingleItem.BehaviorListener = OriginalListener;
                if(SleepCount * 20 <= WaitSecond * 1000) {
                    if(MapWizard.SingleItem.ClickBehaviorListened != null) res = "Click::" + MapWizard.SingleItem.ClickBehaviorListened;
                    else res = "Key::" + OriginalOpenGLWizard.SingleItem.SocketKeyID + "#" + OriginalOpenGLWizard.SingleItem.SocketKeyChar;
                }
                MapWizard.SingleItem.ClickBehaviorListened = null;
                if(OriginalOpenGLWizard.SingleItem != null) OriginalOpenGLWizard.SingleItem.SocketKeyChar = '!';
                if(OriginalOpenGLWizard.SingleItem != null) OriginalOpenGLWizard.SingleItem.SocketKeyID = -1;
                return res;
            } else return "Fail::No Command Matched For '" + Command + "'";
        } catch (Exception ex) {
            //PointDB.AttributeDelete("SocketInsert", "Transaction" + SocketTransactionCounter, null, null, null);
            //LineDB.AttributeDelete("SocketInsert", "Transaction" + SocketTransactionCounter, null, null, null);
            //PolygonDB.AttributeDelete("SocketInsert", "Transaction" + SocketTransactionCounter, null, null, null);
            System.out.println("Transaction" + SocketTransactionCounter + " Failed!");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return "Fail::"+sw.toString();
        }
    }
}
