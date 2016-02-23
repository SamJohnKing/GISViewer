package Database;
import java.io.File;
public interface DatabaseInterface {
	abstract public void DatabaseFileInput(File Input);
	abstract public void DatabaseFileOutput(File Output);
	abstract public void DatabaseInit();
	abstract public void DatabaseDelete(int index);
	abstract public void DatabaseDelete(String KeyWord);
	abstract public void DatabaseRemove(int index);
	abstract public void DatabaseResize();
	abstract public void AttributeDelete(String Info0,String Info1,String Info2,String Info3,String Info4);
	abstract public double GetMBRX1(int index);
	abstract public double GetMBRX2(int index);
	abstract public double GetMBRY1(int index);
	abstract public double GetMBRY2(int index);
	abstract public boolean CheckInRegion(int ID, double RegionX1, double RegionY1, double RegionX2, double RegionY2);
	abstract public int GetElementNum();
	abstract public void Clear(int index);
	abstract public IndexInterface GetIndexPermission();
	abstract public void SetIndexPermission(IndexInterface obj);
}
