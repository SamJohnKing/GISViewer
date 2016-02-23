package Database;
import java.io.File;
import java.util.ArrayList;

public class PointDataRecordSet implements PointDatabaseInterface{
	private RTreeIndex IndexObj=null;
	private ArrayList<PointStructure> RecordArray=null;
	public PointStructure GetElement(int index){
		if(RecordArray==null) return null;
		if(index<0) return null;
		if(index>=RecordArray.size()) return null;
		return RecordArray.get(index);
	}
	@Override
	public void DatabaseFileInput(File Input) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void DatabaseFileOutput(File Output) {
		// TODO Auto-generated method stub
	}
	@Override
	public void DatabaseInit() {
		// TODO Auto-generated method stub	
	}
	public void DatabaseInit(ArrayList<PointStructure> PointArray){
		RecordArray=PointArray;
		IndexObj=new RTreeIndex();
		IndexObj.IndexInit(this);
	}
	@Override
	public void DatabaseDelete(int index) {
		// TODO Auto-generated method stub
	}
	@Override
	public void DatabaseDelete(String KeyWord) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void DatabaseRemove(int index) {
		// TODO Auto-generated method stub
	}
	@Override
	public void DatabaseResize() {
		// TODO Auto-generated method stub	
	}
	@Override
	public void AttributeDelete(String Info0, String Info1, String Info2,
			String Info3, String Info4) {
		// TODO Auto-generated method stub
	}
	@Override
	public double GetMBRX1(int index) {
		// TODO Auto-generated method stub
		return RecordArray.get(index).x;
	}
	@Override
	public double GetMBRX2(int index) {
		// TODO Auto-generated method stub
		return RecordArray.get(index).x;
	}
	@Override
	public double GetMBRY1(int index) {
		// TODO Auto-generated method stub
		return RecordArray.get(index).y;
	}
	@Override
	public double GetMBRY2(int index) {
		// TODO Auto-generated method stub
		return RecordArray.get(index).y;
	}
	public boolean CheckInRegion(int ID, double RegionX1, double RegionY1, double RegionX2, double RegionY2){
		double Left		= GetMBRX1(ID);
		double Right 	= GetMBRX2(ID);
		double Up		= GetMBRY2(ID);
		double Down		= GetMBRY1(ID);
		if(Left 	> Math.max(RegionX1, RegionX2)) return false;
		if(Right 	< Math.min(RegionX1, RegionX2)) return false;
		if(Down		> Math.max(RegionY1, RegionY2)) return false;
		if(Up		< Math.min(RegionY1, RegionY2)) return false;
		return true;
	}
	@Override
	public int GetElementNum() {
		// TODO Auto-generated method stub
		return RecordArray.size();
	}
	@Override
	public IndexInterface GetIndexPermission() {
		// TODO Auto-generated method stub
		return IndexObj;
	}
	@Override
	public void SetIndexPermission(IndexInterface obj) {
		// TODO Auto-generated method stub
		this.IndexObj=(RTreeIndex)obj;
	}
	@Override
	public void add(double XPoint, double YPoint, String Hint) {
		// TODO Auto-generated method stub
	}
	@Override
	public void add(double XPoint, double YPoint, int Attribute, String Hint) {
		// TODO Auto-generated method stub
	}
	@Override
	public void update(int index, int visible, String Hint) {
		// TODO Auto-generated method stub
	}
	@Override
	public void update(String index, String visible, String Hint) {
		// TODO Auto-generated method stub
	}
	@Override
	public void Clear(int index) {
		// TODO Auto-generated method stub
		
	}
}
