
package Algorithm;
import java.util.*;
public class ClassicalMapMatchingAlgorithmClass implements AlgorithmInterface{
	private MapKernel.MapControl MainHandle;
	private Database.LineDataSet LineDB;
	private Database.PointDataSet PointDB;
	private  Database.RTreeIndex RTree;
	private ArrayList<Integer> res;
	public void setInput(Object Input) {
		try{
			ArrayList<Database.PointStructure> Trajectory=(ArrayList<Database.PointStructure>) Input;
			int ii=Trajectory.size();
			Database.PointStructure p;
			double dd=50.0*180.0/6371.0/1000.0/Math.PI;
			for(int i=0;i<ii;i++){
				p=Trajectory.get(i);
				if(p.x<MainHandle.getLongitudeStart()) continue;
				if(p.x>MainHandle.getLongitudeEnd()) continue;
				if(p.y<MainHandle.getLatitudeStart()) continue;
				if(p.y>MainHandle.getLatitudeEnd()) continue;
				res=RTree.Search(p.x-dd,p.y-dd,p.x+dd,p.y+dd);
				if(res.isEmpty()){
					PointDB.add(p.x,p.y,"[Info:MissingGPSPoint]");
					PointDB.PointVisible[PointDB.PointNum-1]=133;
					continue;
				}
				while(!res.isEmpty()){
					if(LineDB.PointLineCheck(res.get(0),p.x,p.y)>dd){
						res.remove(0);
						continue;
					}
					else break;
				}
				if(res.isEmpty()){
					PointDB.add(p.x,p.y,"[Info:MissingGPSPoint]");
					PointDB.PointVisible[PointDB.PointNum-1]=133;
				}
			}
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
	}
	public Object getOutput() {
		// TODO Auto-generated method stub
		return null;
	}
	public void AlgorithmProcessor() {
		// TODO Auto-generated method stub
	}
	public void setHandle(MapKernel.MapControl Handle){
		MainHandle=Handle;
		LineDB=MainHandle.getLineDatabase();
		PointDB=MainHandle.getPointDatabase();
		RTree=new Database.RTreeIndex();
		RTree.IndexInit(LineDB);
	}
	@Override
	public void setInput(Object Input1, Object Input2) {
		// TODO Auto-generated method stub
		
	}
}
