package MapKernel;
public class CacheRoadNetworkDatabaseClass {
	final static int MaxPointVolume=500000;
	MapControl MainHandle;
	public int PointNum,LinkNum;
	public boolean Loaded=false;
	public void DBFresh(){
		PointNum=0;
		LinkNum=0;
		System.gc();
	}
	public double[] AllPointX=new double[MaxPointVolume];
	public double[] AllPointY=new double[MaxPointVolume];
	public int[] AllPointReference=new int[MaxPointVolume];
	public int[] PointST=new int[MaxPointVolume];
	public int[] PointEN=new int[MaxPointVolume];
	public int[] LinkST=new int[5*MaxPointVolume];
	public int[] LinkEN=new int[5*MaxPointVolume];
	public int[] _LinkST=new int[5*MaxPointVolume];
	public int[] _LinkEN=new int[5*MaxPointVolume];
	public double[] LinkLength=new double[5*MaxPointVolume];
	public CacheRoadNetworkDatabaseClass(){
	}
	public void setHandle(MapControl MainHandle){
		this.MainHandle=MainHandle;
	}
	public void LinkAppend(int a,int b){
		LinkST[LinkNum]=a;
		LinkEN[LinkNum]=b;
		LinkNum++;
	}
	public double Angle(double x1,double y1,double x2,double y2,double x3,double y3){
		double dx1=x2-x1;
		double dy1=y2-y1;
		double dx2=x3-x2;
		double dy2=y3-y2;
		double l1=Math.sqrt(dx1*dx1+dy1*dy1);
		double l2=Math.sqrt(dx2*dx2+dy2*dy2);
		return Math.acos((dx1*dx2+dy1*dy2)/l1/l2);
	}
	public int[] Queue=new int[MaxPointVolume];
	public int[] Father=new int[MaxPointVolume];
	boolean[] Hash=new boolean[MaxPointVolume];
	public int[] AnsTail=new int[MaxPointVolume];
	int open,closed;
	final static double ConnectDis=500*360/(2*Math.PI*6371*1000);
	public void BFS(double OriginLongitude,double OriginLatitude,double TerminalLongitude,double TerminalLatitude){
		if(MainHandle.AccurateDistance(OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude)<ConnectDis){
			AnsTail[1]=-1;
			AnsTail[0]=1;
			return;
		}
		open=-1;
		closed=-1;
		AnsTail[0]=0;
		for(int i=0;i<MaxPointVolume;i++){
			Hash[i]=false;
		}
		for(int i=0;i<PointNum;i++){
			if(MainHandle.AccurateDistance(AllPointX[i],AllPointY[i],OriginLongitude,OriginLatitude)<ConnectDis){
				open++;
				Queue[open]=i;
				Father[open]=-1;
				Hash[i]=true;
			}
		}
		while(open!=closed){
			closed++;
			if(MainHandle.AccurateDistance(AllPointX[Queue[closed]],AllPointY[Queue[closed]],TerminalLongitude,TerminalLatitude)<ConnectDis){
				AnsTail[0]++;
				AnsTail[AnsTail[0]]=closed;
				continue;
			}
			for(int i=PointST[Queue[closed]];i<=PointEN[Queue[closed]];i++){
				if(!MainHandle.InsideStretchRegion(ConnectDis*5,AllPointX[LinkEN[i]],AllPointY[LinkEN[i]],
						OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude)) continue;
				if(!Hash[LinkEN[i]]){
					open++;
					Queue[open]=LinkEN[i];
					Father[open]=closed;
					Hash[LinkEN[i]]=true;
				}
			}
		}
	}
	double[] Dist=new double[MaxPointVolume];
	//This Father Array is set for the Dist Array
	public void SPFA(double OriginLongitude,double OriginLatitude,double TerminalLongitude,double TerminalLatitude){
		if(MainHandle.AccurateDistance(OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude)<ConnectDis){
			AnsTail[1]=-1;
			AnsTail[0]=1;
			return;
		}
		open=-1;
		closed=-1;
		AnsTail[0]=0;
		for(int i=0;i<MaxPointVolume;i++){
			Hash[i]=false;
			Dist[i]=1e100;
			Father[i]=-1;
		}
		for(int i=0;i<PointNum;i++){
			if(MainHandle.AccurateDistance(AllPointX[i],AllPointY[i],OriginLongitude,OriginLatitude)<ConnectDis){
				open++;
				open%=MaxPointVolume;
				Queue[open]=i;
				Father[open]=-1;
				Hash[i]=true;
				Dist[i]=MainHandle.AccurateDistance(AllPointX[i],AllPointY[i],OriginLongitude,OriginLatitude);
			}
		}
		while(open!=closed){
			closed++;
			closed%=MaxPointVolume;
			Hash[Queue[closed]]=false;
			for(int i=PointST[Queue[closed]];i<=PointEN[Queue[closed]];i++){
				if(!MainHandle.InsideStretchRegion(ConnectDis*5,AllPointX[LinkEN[i]],AllPointY[LinkEN[i]],
						OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude)) continue;
				if(Dist[Queue[closed]]+LinkLength[i]<Dist[LinkEN[i]]){
					Dist[LinkEN[i]]=Dist[Queue[closed]]+LinkLength[i];
					Father[LinkEN[i]]=Queue[closed];
				if(!Hash[LinkEN[i]]){
					open++;
					open%=MaxPointVolume;
					Queue[open]=LinkEN[i];
					Hash[LinkEN[i]]=true;
				}
				}
			}
		}
		double mindist=1e100;
		int best=-1;
		for(int i=0;i<PointNum;i++){
			Queue[i]=i;
			if(MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i])>ConnectDis) continue;
			if(Dist[i]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i])<mindist){
				mindist=Dist[i]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i]);
				best=i;
			}
		}
		if(best==-1){
			AnsTail[0]=0;
			return;
		}
		AnsTail[0]=1;
		AnsTail[1]=best;
		return;
	}
	public boolean CheckCrowdedness(double x,double y){
		Database.PolygonDataSet PolyDB=MainHandle.getPolygonDatabase();
		for(int i=0;i<PolyDB.PolygonNum;i++){
			if(PolyDB.PolygonHint[i].indexOf("[Info:Crowdedness]")==-1) continue;
			if(PolyDB.CheckInsidePolygon(i,x,y)) return true;
		}
		return false;
	}
	boolean[] IsCrowdednessStatus=new boolean[MaxPointVolume];
	public void FreshCrowdednessStatus(){
		MainHandle.getPolygonDatabase().GenerateMBR();
		for(int i=0;i<PointNum;i++){
			if(CheckCrowdedness(AllPointX[i],AllPointY[i]))
				IsCrowdednessStatus[i]=true;
			else IsCrowdednessStatus[i]=false;
		}
		//In Fact The Map Matching Algorithm is more accurate but complicating to implements
		//RoadConditionAnalysis--------------------------------
		for(int i=0;i<PointNum;i++){
			if(!IsCrowdednessStatus[i]) continue;
			if(AllPointReference[i]==-1) continue;
			MainHandle.getKernel().RoadConditionView.TransSignal(AllPointReference[i]);
		}
		MainHandle.getKernel().RoadConditionView.UpdateChart();
		MainHandle.getKernel().RoadConditionView.ShowChart();
		//-----------------------------------------------------
		for(int i=1;i<PointNum;i++){
			if(AllPointReference[i]==-1) continue;
			if(AllPointReference[i]!=AllPointReference[i-1]) continue;
			if(!IsCrowdednessStatus[i-1]) continue;
			if(!IsCrowdednessStatus[i]) continue;
			String str="[Info:Cache][Info:ClockDepend][Info:Crowdedness][Info:"+i+"]";
			MainHandle.getLineDatabase().DynamicAdd(AllPointX[i-1],AllPointY[i-1],str);
			MainHandle.getLineDatabase().DynamicAdd(AllPointX[i],AllPointY[i],str);
			MainHandle.getLineDatabase().DynamicSetVisible(1033,str);
		}
	}
	public void DynamicSPFA(double OriginLongitude,double OriginLatitude,double TerminalLongitude,double TerminalLatitude){
		if(MainHandle.AccurateDistance(OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude)<ConnectDis){
			AnsTail[1]=-1;
			AnsTail[0]=1;
			return;
		}
		FreshCrowdednessStatus();
		open=-1;
		closed=-1;
		AnsTail[0]=0;
		for(int i=0;i<MaxPointVolume;i++){
			Hash[i]=false;
			Dist[i]=1e100;
			Father[i]=-1;
		}
		for(int i=0;i<PointNum;i++){
			if(MainHandle.AccurateDistance(AllPointX[i],AllPointY[i],OriginLongitude,OriginLatitude)<ConnectDis){
				open++;
				open%=MaxPointVolume;
				Queue[open]=i;
				Father[open]=-1;
				Hash[i]=true;
				Dist[i]=MainHandle.AccurateDistance(AllPointX[i],AllPointY[i],OriginLongitude,OriginLatitude);
			}
		}
		for(int i=1;i<PointNum;i++){
			if(AllPointReference[i]==-1) continue;
			if(AllPointReference[i]!=AllPointReference[i-1]) continue;
			double x1=AllPointX[i-1];
			double y1=AllPointY[i-1];
			double x3=AllPointX[i];
			double y3=AllPointY[i];
			double x2=OriginLongitude;
			double y2=OriginLatitude;
			if(((x2-x1)*(x3-x2)<0)&&((y2-y1)*(y3-y2)<0)) continue;
			if(Angle(x1,y1,x2,y2,x3,y3)>15*Math.PI/180.0) continue;
			open++;
			open%=MaxPointVolume;
			Queue[open]=i;
			Father[open]=-1;
			Hash[i]=true;
			Dist[i]=MainHandle.AccurateDistance(AllPointX[i],AllPointY[i],OriginLongitude,OriginLatitude);
			//-------------------------------------------------------
			open++;
			open%=MaxPointVolume;
			Queue[open]=i-1;
			Father[open]=-1;
			Hash[i-1]=true;
			Dist[i-1]=MainHandle.AccurateDistance(AllPointX[i-1],AllPointY[i-1],OriginLongitude,OriginLatitude);
		}
		while(open!=closed){
			closed++;
			closed%=MaxPointVolume;
			Hash[Queue[closed]]=false;
			for(int i=PointST[Queue[closed]];i<=PointEN[Queue[closed]];i++){
				if(!MainHandle.InsideStretchRegion(ConnectDis*40,AllPointX[LinkEN[i]],AllPointY[LinkEN[i]],
						OriginLongitude,OriginLatitude,TerminalLongitude,TerminalLatitude)) continue;
				if(IsCrowdednessStatus[LinkEN[i]]) continue;
				if(Dist[Queue[closed]]+LinkLength[i]<Dist[LinkEN[i]]){
					Dist[LinkEN[i]]=Dist[Queue[closed]]+LinkLength[i];
					Father[LinkEN[i]]=Queue[closed];
				if(!Hash[LinkEN[i]]){
					open++;
					open%=MaxPointVolume;
					Queue[open]=LinkEN[i];
					Hash[LinkEN[i]]=true;
				}
				}
			}
		}
		double mindist=1e100;
		int best=-1;
		for(int i=0;i<PointNum;i++){
			Queue[i]=i;
			if(MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i])>ConnectDis) continue;
			if(Dist[i]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i])<mindist){
				mindist=Dist[i]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i]);
				best=i;
			}
		}
		for(int i=1;i<PointNum;i++){
			if(AllPointReference[i]==-1) continue;
			if(AllPointReference[i]!=AllPointReference[i-1]) continue;
			double x1=AllPointX[i-1];
			double y1=AllPointY[i-1];
			double x3=AllPointX[i];
			double y3=AllPointY[i];
			double x2=TerminalLongitude;
			double y2=TerminalLatitude;
			if(((x2-x1)*(x3-x2)<0)&&((y2-y1)*(y3-y2)<0)) continue;
			if(Angle(x1,y1,x2,y2,x3,y3)>20*Math.PI/180.0) continue;
			if(Dist[i-1]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i-1],AllPointY[i-1])<mindist){
				mindist=Dist[i-1]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i-1],AllPointY[i-1]);
				best=i-1;
			}
			//-----------------------------------------------------------------------------------------
			if(Dist[i]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i])<mindist){
				mindist=Dist[i]+MainHandle.AccurateDistance(TerminalLongitude,TerminalLatitude,AllPointX[i],AllPointY[i]);
				best=i;
			}
		}
		if(best==-1){
			AnsTail[0]=0;
			return;
		}
		AnsTail[0]=1;
		AnsTail[1]=best;
		return;
	}
	int[] TankVolume=new int[MaxPointVolume];
	int[] Temp;
	public void Init(){
		DBFresh();
		Database.LineDataSet RoadDB=MainHandle.getLineDatabase();
		RoadDB.DatabaseDelete("[Info:Cache]");
		for(int i=0;i<RoadDB.LineNum;i++){
			if(RoadDB.LineHint[i].indexOf("[Info:Road]")==-1) continue;
			int ptr=RoadDB.LineHead[i];
			while(ptr!=-1){
				AllPointX[PointNum]=RoadDB.AllPointX[ptr];
				AllPointY[PointNum]=RoadDB.AllPointY[ptr];
				AllPointReference[PointNum]=i;
				ptr=RoadDB.AllPointNext[ptr];
				PointNum++;
			}
		}
		if(PointNum==0) return;
		Loaded=true;
		//Link the Road Point in Single Road
		for(int i=1;i<PointNum;i++){
			if(AllPointReference[i-1]==AllPointReference[i]){
				LinkAppend(i,i-1);
				LinkAppend(i-1,i);
			}
		}
		//Try to Link The Road Matually
		for(int i=0;i<PointNum;i++){
			for(int j=i+1;j<PointNum;j++){
				if(Math.abs(AllPointX[i]-AllPointX[j])+Math.abs(AllPointY[i]-AllPointY[j])<1e-6){
					LinkAppend(i,j);
					LinkAppend(j,i);
				}
			}
		}
		//To sort the Link
				for(int i=0;i<MaxPointVolume;i++) TankVolume[i]=0;
				for(int i=0;i<LinkNum;i++) TankVolume[LinkST[i]]++;
				for(int i=1;i<MaxPointVolume;i++) TankVolume[i]+=TankVolume[i-1];
				for(int i=MaxPointVolume-1;i>0;i--) TankVolume[i]=TankVolume[i-1];
				TankVolume[0]=0;
				for(int i=0;i<LinkNum;i++){
					_LinkST[TankVolume[LinkST[i]]]=LinkST[i];
					_LinkEN[TankVolume[LinkST[i]]]=LinkEN[i];
					TankVolume[LinkST[i]]++;
				}
				Temp=LinkST;
				LinkST=_LinkST;
				_LinkST=Temp;
				Temp=LinkEN;
				LinkEN=_LinkEN;
				_LinkEN=Temp;
				for(int i=0;i<PointNum;i++) PointST[i]=-1;
				PointST[LinkST[0]]=0;
				for(int i=1;i<LinkNum;i++){
					if(LinkST[i-1]!=LinkST[i]){
						PointEN[LinkST[i-1]]=i-1;
						PointST[LinkST[i]]=i;
					}
				}
				PointEN[LinkST[LinkNum-1]]=LinkNum-1;
				double[] tx,ty;
				tx=new double[5];
				ty=new double[5];
				MainHandle.ChangeTitle("========>Point:"+PointNum+"======>Edge:"+LinkNum);
				//To Generate the Length of Link
				for(int i=0;i<LinkNum;i++){
					LinkLength[i]=MainHandle.AccurateDistance(AllPointX[LinkST[i]],AllPointY[LinkST[i]],AllPointX[LinkEN[i]],AllPointY[LinkEN[i]]);
				}
	}
	// This Dis and Angle should be transform to suit the format here
	/*
	public void process(double CrossCrossDisLimit,double CrossRoadDisLimit,double CrossAngleLimit){
		CrossCrossDisLimit/=(Math.PI*6371*1000);
		CrossCrossDisLimit*=360;
		CrossRoadDisLimit/=(Math.PI*6371*1000);
		CrossRoadDisLimit*=360;
		CrossAngleLimit/=360;
		CrossAngleLimit*=Math.PI;
		DBFresh();
		Loaded=true;
		//Import All the Road Point and  All the Cross Point
		Database.LineDataSet RoadDB=MainHandle.getLineDatabase();
		Database.PointDataSet CrossDB=MainHandle.getPointDatabase();
		RoadDB.DatabaseDelete("[Info:Cache]");
		for(int i=0;i<RoadDB.LineNum;i++){
			if(RoadDB.LineHint[i].indexOf(":Road")==-1) continue;
			int ptr=RoadDB.LineHead[i];
			while(ptr!=-1){
				AllPointX[PointNum]=RoadDB.AllPointX[ptr];
				AllPointY[PointNum]=RoadDB.AllPointY[ptr];
				AllPointReference[PointNum]=i;
				ptr=RoadDB.AllPointNext[ptr];
				PointNum++;
			}
		}
		int CrossST=PointNum;
		for(int i=0;i<CrossDB.PointNum;i++){
			if(CrossDB.PointHint[i].indexOf(":Cross")==-1) continue;
			AllPointX[PointNum]=CrossDB.AllPointX[i];
			AllPointY[PointNum]=CrossDB.AllPointY[i];
			AllPointReference[PointNum]=-1;
			PointNum++;
		}
		//Link the Road Point in Single Road
		for(int i=1;i<CrossST;i++){
			if(AllPointReference[i-1]==AllPointReference[i]){
				LinkAppend(i,i-1);
				LinkAppend(i-1,i);
			}
		}
		//Try to Link Cross and Cross
		for(int i=CrossST;i<PointNum;i++){
			for(int j=i+1;j<PointNum;j++){
				if(Math.abs(AllPointX[i]-AllPointX[j])+Math.abs(AllPointY[i]-AllPointY[j])<CrossCrossDisLimit){
					LinkAppend(i,j);
					LinkAppend(j,i);
				}
			}
		}
		//Try to Link Cross and Road Skeleton Point With Dis
		for(int i=0;i<CrossST;i++){
			for(int j=CrossST;j<PointNum;j++){
				if(Math.abs(AllPointX[i]-AllPointX[j])+Math.abs(AllPointY[i]-AllPointY[j])<CrossRoadDisLimit){
					LinkAppend(i,j);
					LinkAppend(j,i);
				}
			}
		}
		//Try to Link Cross and Road Skeleton Point With Angle
		for(int i=1;i<CrossST;i++){
			if(AllPointReference[i]!=AllPointReference[i-1]) continue;
			double x1=AllPointX[i-1];
			double y1=AllPointY[i-1];
			double x3=AllPointX[i];
			double y3=AllPointY[i];
			for(int j=CrossST;j<PointNum;j++){
				double x2=AllPointX[j];
				double y2=AllPointY[j];
				if(((x2-x1)*(x3-x2)<0)&&((y2-y1)*(y3-y2)<0)) continue;
				if(Angle(x1,y1,x2,y2,x3,y3)>CrossAngleLimit) continue;
				LinkAppend(i-1,j);
				LinkAppend(j,i-1);
				LinkAppend(j,i);
				LinkAppend(i,j);
			}
		}
		//To sort the Link Simple Algorithm should be refined
		int temp;
		for(int i=0;i<LinkNum;i++){
			for(int j=1;j<LinkNum;j++){
				if(LinkST[j-1]>LinkST[j]){
					temp=LinkST[j-1];
					LinkST[j-1]=LinkST[j];
					LinkST[j]=temp;
					temp=LinkEN[j-1];
					LinkEN[j-1]=LinkEN[j];
					LinkEN[j]=temp;
				}
			}
		}
		for(int i=0;i<PointNum;i++) PointST[i]=-1;
		PointST[LinkST[0]]=0;
		for(int i=1;i<LinkNum;i++){
			if(LinkST[i-1]!=LinkST[i]){
				PointEN[LinkST[i-1]]=i-1;
				PointST[LinkST[i]]=i;
			}
		}
		PointEN[LinkST[LinkNum-1]]=LinkNum-1;
		double[] tx,ty;
		tx=new double[5];
		ty=new double[5];
		MainHandle.ChangeTitle("========>Point:"+PointNum+"======>Edge:"+LinkNum);
		//To Generate the Length of Link
		for(int i=0;i<LinkNum;i++){
			LinkLength[i]=MainHandle.AccurateDistance(AllPointX[LinkST[i]],AllPointY[LinkST[i]],AllPointX[LinkEN[i]],AllPointY[LinkEN[i]]);
		}
		//To Show The extended Link Segment in Screen Canvas
		for(int i=CrossST;i<PointNum;i++){
			if(PointST[i]==-1) continue;
			for(int j=PointST[i];j<=PointEN[i];j++){
				tx[0]=AllPointX[LinkST[j]];
				ty[0]=AllPointY[LinkST[j]];
				tx[1]=AllPointX[LinkEN[j]];
				ty[1]=AllPointY[LinkEN[j]];
				RoadDB.add(tx,ty,2,"[Info:Cache]");
			}
		}
	}
	*/
}
