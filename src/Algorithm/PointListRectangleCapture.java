package Algorithm;

import java.util.ArrayList;

import MapKernel.MapControl;

public class PointListRectangleCapture implements AlgorithmInterface{
	MapKernel.MapControl MainHandle;
	@Override
	public void setHandle(MapControl Handle) {
		// TODO Auto-generated method stub
		MainHandle=Handle;
	}
	private double Xmin,Xmax,Ymin,Ymax;
	public void SetParameter(double x1,double y1,double x2,double y2){
		Xmin=Math.min(x1,x2);
		Xmax=Math.max(x1,x2);
		Ymin=Math.min(y1,y2);
		Ymax=Math.max(y1,y2);
	}
	public void Fresh(){
		TrajectoryList.clear();
	}
	private ArrayList<Database.TimeStampPointStructure> TrajectoryPart;
	private ArrayList<ArrayList<Database.TimeStampPointStructure>> TrajectoryList=
			new ArrayList<ArrayList<Database.TimeStampPointStructure>>();
	@Override
	public void setInput(Object Input) {
		// TODO Auto-generated method stub
		try{
			TrajectoryPart=new ArrayList<Database.TimeStampPointStructure>();
			ArrayList<Database.TimeStampPointStructure> Trajectory=(ArrayList<Database.TimeStampPointStructure>) Input;
			boolean Hit=true;
			for(Database.TimeStampPointStructure p:Trajectory){
				if(p.x<Xmin) {Hit=false;continue;}
				if(p.x>Xmax) {Hit=false;continue;}
				if(p.y<Ymin) {Hit=false;continue;}
				if(p.y>Ymax) {Hit=false;continue;}
				if(!Hit){
					if(TrajectoryPart.size()>0){
						TrajectoryList.add(TrajectoryPart);
						TrajectoryPart=new ArrayList<Database.TimeStampPointStructure>();
					}
				}
				Hit=true;
				TrajectoryPart.add(p);
			}
			if(TrajectoryPart.size()>0){
				TrajectoryList.add(TrajectoryPart);
			}
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
	}
	@Override
	public void setInput(Object Input1, Object Input2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object getOutput() {
		// TODO Auto-generated method stub
		return TrajectoryList;
	}
	@Override
	public void AlgorithmProcessor() {
		// TODO Auto-generated method stub
		
	}
}
