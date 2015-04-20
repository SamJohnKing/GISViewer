package Database;

public interface LineDatabaseInterface extends DatabaseInterface{
	abstract public void add(double[] element1,double[] element2,int Num,String Hint);
	abstract public void update(int index,int visible,String Hint);
	abstract public void update(String index,String visible,String Hint);
}
