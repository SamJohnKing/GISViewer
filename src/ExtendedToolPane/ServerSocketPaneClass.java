package ExtendedToolPane;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import LWJGLPackage.OriginalOpenGLWizard;
import MapKernel.FileAccept;
import MapKernel.MapControl;
import MapKernel.ToolPanel;
import SecondaryScreen.SwtHtmlBrowser;
import org.eclipse.swt.widgets.Display;

public class ServerSocketPaneClass extends ToolPanel implements ExtendedToolPaneInterface, ActionListener, ItemListener {
    MapControl MainHandle;
    double CursorLongitude, CursorLatitude;

    public String getString() {
        return "ServerSocketPane";
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
        JOptionPane.showMessageDialog(null, MapKernel.MapWizard.LanguageDic.GetWords("ConveyPoint"));
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
        if (!IsProcessingTransaction.isSelected()) return "Success::NotProcessing";
        // TODO Auto-generated method stub
        int pos = SocketQuery.indexOf("::");
        int _pos;
        if (pos == -1) return "Fail::";
        if (SocketQuery.equals("Ping::")) return "Ping::";
        Database.PointDataSet PointDB = MainHandle.getPointDatabase();
        Database.LineDataSet LineDB = MainHandle.getLineDatabase();
        Database.PolygonDataSet PolygonDB = MainHandle.getPolygonDatabase();
        SocketTransactionCounter++;
        SocketTransactionCounter %= 100000000;
        try {
            String Command = SocketQuery.substring(0, pos);
            String str = SocketQuery.substring(pos + 2).trim();
            if (Command.equals("InsertPoint")) {
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                double x = Double.parseDouble(str.substring(0, pos));
                double y = Double.parseDouble(str.substring(pos + 1, _pos));
                PointDB.add(x, y, "[Title:][Info:SocketInsert][Info:Transaction" + SocketTransactionCounter + "]");
                return "Success::";
            } else if (Command.equals("InsertStylePoint")) { /** Command::Style#Data */
                int pos1 = str.indexOf('#');
                int pos2 = str.indexOf('#', pos1 + 1);
                int pos3 = str.indexOf('#', pos2 + 1);
                String Style = str.substring(0, pos1) + "[Info:SocketInsert][Info:Transaction " + SocketTransactionCounter + "]";
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
                LineDB.add(SocketX, SocketY, SocketXYCounter, "[Title:][Info:SocketInsert][Info:Transaction" + SocketTransactionCounter + "]");
                return "Success::";
            } else if (Command.equals("InsertStyleLine")) { /** Command::Style#Data */
                int pos1 = str.indexOf('#');
                String Style = str.substring(0, pos1) + "[Info:SocketInsert][Info:Transaction " + SocketTransactionCounter + "]";
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
                PolygonDB.add(SocketX, SocketY, SocketXYCounter, "[Title:][Info:SocketInsert][Info:Transaction" + SocketTransactionCounter + "]");
                return "Success::";
            } else if (Command.equals("InsertStylePolygon")) { /** Command::Style#Data */
                int pos1 = str.indexOf('#');
                String Style = str.substring(0, pos1) + "[Info:SocketInsert][Info:Transaction " + SocketTransactionCounter + "]";
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
            } else if (Command.equals("DeletePointAll")) {
                PointDB.AttributeDelete("SocketInsert", null, null, null, null);
                return "Success::";
            } else if (Command.equals("DeleteLineAll")) {
                LineDB.AttributeDelete("SocketInsert", null, null, null, null);
                return "Success::";
            } else if (Command.equals("DeletePolygonAll")) {
                PolygonDB.AttributeDelete("SocketInsert", null, null, null, null);
                return "Success::";
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
                    Thread.sleep(500);
                }
                return "Success::";
            } else if (Command.equals("MoveMiddle")) {
                pos = str.indexOf('#');
                _pos = str.indexOf('#', pos + 1);
                final double x = Double.parseDouble(str.substring(0, pos));
                final double y = Double.parseDouble(str.substring(pos + 1, _pos));
                MainHandle.getKernel().Screen.MoveMiddle(x, y);
                if (OriginalOpenGLWizard.SingleItem != null)
                    OriginalOpenGLWizard.SingleItem.MoveMiddle(x, y);
                if ((SwtHtmlBrowser.SingleItemThread != null) && SwtHtmlBrowser.Accessed) {
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (SwtHtmlBrowser.shell.isVisible())
                                    SwtHtmlBrowser.MoveMiddle(x, y);
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    Thread.sleep(500);
                }
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
                int n = JOptionPane.showConfirmDialog(null, str, "GIS Info Upheval", JOptionPane.OK_CANCEL_OPTION);
                if (n == JOptionPane.OK_OPTION) return "Success::";
                else return "Abort::";
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
            } else if (Command.equals("AlphaDrawer")) {
                MainHandle.getKernel().Screen.AlphaDrawer(str);
                return "Success::";
            } else return "Fail::";
        } catch (Exception ex) {
            PointDB.AttributeDelete("SocketInsert", "Transaction" + SocketTransactionCounter, null, null, null);
            LineDB.AttributeDelete("SocketInsert", "Transaction" + SocketTransactionCounter, null, null, null);
            PolygonDB.AttributeDelete("SocketInsert", "Transaction" + SocketTransactionCounter, null, null, null);
            return "Fail::";
        }
    }
}
