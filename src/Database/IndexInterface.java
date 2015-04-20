package Database;
import java.util.ArrayList;
public interface IndexInterface {
	abstract public void IndexInit(DatabaseInterface DB);
	abstract public ArrayList<Integer> Search(double x1,double y1,double x2,double y2);
	abstract public void Delete(double x1,double y1,double x2,double y2);
	abstract public void add(int index);
	abstract public void WriteBack();
}
