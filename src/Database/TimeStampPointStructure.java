package Database;
public class TimeStampPointStructure extends PointStructure{
	public TimeStampPointStructure(double x,double y,int t){
		super(x,y);
		this.t=t;
	}
	public int t;
}
