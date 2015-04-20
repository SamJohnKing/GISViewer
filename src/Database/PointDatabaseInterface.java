package Database;

public interface PointDatabaseInterface extends DatabaseInterface{
	abstract public void add(double XPoint,double YPoint,String Hint);
	abstract public void add(double XPoint,double YPoint,int Attribute,String Hint);
	abstract public void update(int index,int visible,String Hint);
	abstract public void update(String index,String visible,String Hint);
}
