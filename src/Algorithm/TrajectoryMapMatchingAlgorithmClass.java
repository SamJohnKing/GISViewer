package Algorithm;
import java.util.*;
public class TrajectoryMapMatchingAlgorithmClass implements AlgorithmInterface{
	private MapKernel.MapControl MainHandle;
	private Database.LineDataSet LineDB;
	private Database.PolygonDataSet PolygonDB;
	private ArrayList<Integer> SelectedPolygonList;
	private  Database.RTreeIndex RTree;
	private ArrayList<Integer> res;
	private ArrayList<ArrayList<Database.TimeStampPointStructure>> TrajectoryList=new ArrayList<ArrayList<Database.TimeStampPointStructure>>();
	private ArrayList<Database.TimeStampPointStructure> TrajectoryPart;
	public void setExtraInput(Object Input){
		if(Input instanceof Integer){
			dd=((Integer) Input)*180.0/6371.0/1000.0/Math.PI;
		}else if(Input instanceof ArrayList){
			SelectedPolygonList=(ArrayList<Integer>)Input;
		}
	}
	public void setInput(Object Input1){}
	public short[] SelectedArray=new short[100000];
	public void Fresh(){
		TrajectoryList.clear();
	}
	double dd;
	public void setInput(Object Input1,Object Input2) {
		try{
			boolean IsLastHit=false;
			TrajectoryPart=new ArrayList<Database.TimeStampPointStructure>();
			ArrayList<Database.TimeStampPointStructure> Trajectory=(ArrayList<Database.TimeStampPointStructure>) Input1;
			int SelectedDis=((Integer) Input2);
			int ii=Trajectory.size();
			for(int i=0;i<ii;i++) SelectedArray[i]=0;
			Database.TimeStampPointStructure p;
			PolygonDB.GenerateMBR();
			int HitPolygonID;
			for(int i=0;i<ii;i++){
				p=Trajectory.get(i);
				if(p.x<MainHandle.getLongitudeStart()) continue;
				if(p.x>MainHandle.getLongitudeEnd()) continue;
				if(p.y<MainHandle.getLatitudeStart()) continue;
				if(p.y>MainHandle.getLatitudeEnd()) continue;
				HitPolygonID=-1;
				for(int j:SelectedPolygonList){
					if(!PolygonDB.CheckInsideMBR(j,p.x,p.y)) continue;
					if(!PolygonDB.CheckInsidePolygon(j,p.x,p.y)) continue;
					HitPolygonID=j;
					break;
				}
				if(HitPolygonID==-1){
					if(TrajectoryPart.size()>0)
					{
						TrajectoryList.add(TrajectoryPart);
						TrajectoryPart=new ArrayList<Database.TimeStampPointStructure>();
					}
					continue;
				}
				if(dd>0) res=RTree.Search(p.x-dd,p.y-dd,p.x+dd,p.y+dd);
				else res=null;
				if((res==null)||(res.isEmpty())){
					TrajectoryPart.add(p);
					SelectedArray[i]=1;
					//PointDB.add(p.x,p.y,"[Info:MissingGPSPoint]");
					//PointDB.PointVisible[PointDB.PointNum-1]=133;
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
					TrajectoryPart.add(p);
					SelectedArray[i]=1;
					//PointDB.add(p.x,p.y,"[Info:MissingGPSPoint]");
					//PointDB.PointVisible[PointDB.PointNum-1]=133;
					continue;
				}else{
					if(TrajectoryPart.size()>0)
					{
						TrajectoryList.add(TrajectoryPart);
						TrajectoryPart=new ArrayList<Database.TimeStampPointStructure>();
					}
				}
			}
			for(int pos=0;pos<ii;pos++){
				for(int dis=1;dis<=SelectedDis;dis++){
					if(SelectedArray[pos]!=1) continue;
					if(pos-dis<0) continue;
					if(SelectedArray[pos-dis]==0) SelectedArray[pos-dis]=-1; 
					if(pos+dis>=ii) continue;
					if(SelectedArray[pos+dis]==0) SelectedArray[pos+dis]=-1;
				}
			}
			TrajectoryPart.clear();
			for(int pos=0;pos<ii;pos++){
				if(SelectedArray[pos]==-1){
					TrajectoryPart.add(Trajectory.get(pos));
				}else{
					if(TrajectoryPart.size()>0)
					{
						TrajectoryList.add(TrajectoryPart);
						TrajectoryPart=new ArrayList<Database.TimeStampPointStructure>();
					}
				}
			}
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
	}
	public Object getOutput() {
		// TODO Auto-generated method stub
		return TrajectoryList;
	}
	public void AlgorithmProcessor() {
		// TODO Auto-generated method stub
	}
	public void setHandle(MapKernel.MapControl Handle){
		MainHandle=Handle;
		LineDB=MainHandle.getLineDatabase();
		PolygonDB=MainHandle.getPolygonDatabase();
		//PointDB=MainHandle.getPointDatabase();
		RTree=new Database.RTreeIndex();
		RTree.IndexInit(LineDB);
	}
}
