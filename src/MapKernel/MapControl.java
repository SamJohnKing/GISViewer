package MapKernel;

import java.util.Calendar;

public interface MapControl {
//-------------------------------Command Line Link
	abstract public void MultipleData(int row,int col);
	abstract public void CheckDelete();
	abstract public void PendingTrap();
	abstract public void PointInputTransform();
	abstract public void LineInputTransform();
	abstract public void PointOutputTransform();
	abstract public void LineOutputTransform();
	abstract public void PolygonOutputTransform();
	abstract public void PointInput();
	abstract public void LineInput();
	abstract public void PolygonInput();
	abstract public void Resize();
	abstract public void ImageDirChange();
//-------------------------------GlobalPreference
	abstract public ExtendedToolPane.ServerSocketPaneClass GetServerPane();
	abstract public ExtendedToolPane.ClientSocketPaneClass GetClientPane();
	abstract public String GetInternationalTimeSignature();
	abstract public FreeWizard.GlobalPreferenceWizard getPreference();
//-------------------------------For Multiple Public use
	abstract public double MetertoLongitude(double Meter);
	abstract public double MetertoLatitude(double Meter);
	abstract public double LongitudetoMeter(double Longitude);
	abstract public double LatitudetoMeter(double Latitude);
	abstract public void SolveException(Exception ex);
	abstract public boolean AccurateInsideRectangle(double x0,double y0,double x1,double y1,double x2,double y2);
	abstract public double AccurateMeterDistance(double x0,double y0,double x1,double y1);
	abstract public boolean InsideStretchRegion(double Stretch,double x0,double y0,double x1,double y1,double x2,double y2);
	abstract public double AccurateDistance(double x0,double y0,double x1,double y1);
	abstract public boolean IsDataLoaded();
	abstract public boolean IsPolygonLoaded();
	abstract public boolean IsLineLoaded();
	abstract public boolean IsPointLoaded();
	abstract public ToolPanel getNowPanel();
	abstract public void ForbidOperate();
	abstract public void AllowOperate();
	abstract public void setKernel(MapWizard obj);
	abstract public MapWizard getKernel();
	abstract public int getSecond();
	abstract public double getLongitudeStart();
	abstract public double getLongitudeEnd();
	abstract public double getLatitudeStart();
	abstract public double getLatitudeEnd();
//-------------------------------屏幕控制
	abstract public void ScreenFlush();
	abstract public void ScreenLock(boolean IsLock);
	abstract public boolean IsScreenLock();
//-------------------------------屏幕文本控制
	abstract public void ChangeTitle(String NewTitle);
	abstract public void ShowTextArea1(String Content,boolean BackGround);
	abstract public void VeilTextArea1();
	abstract public void ShowTextArea2(String Content,boolean BackGround);
	abstract public void VeilTextArea2();
//-------------------------------点态控制+线态控制
	abstract public void PointPush(double x,double y);
	abstract public void PointPush(double x,double y,String Hint);
	abstract public void PointPop();
	abstract public void PointEmpty();
	abstract public void PointSelect(double x1,double y1,double x2,double y2);
	abstract public void PointSelectDelete();
	abstract public void PointSelectCancel();
	abstract public void setPointVisible(boolean visible);
	abstract public boolean getPointVisible();
	abstract public void setPointHintVisible(boolean visible);
	abstract public boolean getPointHintVisible();
	abstract public void setPointConsecutiveLinkVisible(boolean visible);
	abstract public boolean getPointConsecutiveVisible();
	abstract public void setPointHeadTailLinkVisible(boolean visible);
	abstract public boolean getPointHeadTailLinkVisible();
	abstract public int getPointCount();
	abstract public void ResetPointHint(int k,String Hint);
	abstract public void ResetPointHint(String Prefix);
//-------------------------------数据库控制
	abstract public void PolygonDatabaseAppend(String Hint);
	abstract public void LineDatabaseAppend(String Hint);
	abstract public void PointDatabaseAppend(String Hint);
	abstract public Database.PolygonDataSet getPolygonDatabase();
	abstract public Database.LineDataSet getLineDatabase();
	abstract public Database.PointDataSet getPointDatabase();
}
