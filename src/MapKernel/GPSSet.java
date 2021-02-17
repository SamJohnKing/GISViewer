package MapKernel;
import java.io.*;
public class GPSSet {
	//专门用来处理数据的类
	public void CleanUp(){//清空数组和指针
		//System.out.println("MaxTaxiNum\t" + MaxTaxiNum);
		//System.out.println("MaxTaxiInfoLength\t" + MaxTaxiInfoLength);
		TaxiID=new String[MaxTaxiNum];
		TaxiPtr=new int[MaxTaxiNum];
		TaxiGPSStart=new int[MaxTaxiNum];
		TaxiGPSEnd=new int[MaxTaxiNum];
		TaxiLongitudeList=new double[MaxTaxiInfoLength];
		TaxiLatitudeList=new double[MaxTaxiInfoLength];
		TaxiHourList=new int[MaxTaxiInfoLength];
		TaxiMinuteList=new int[MaxTaxiInfoLength];
		TaxiSecondList=new int[MaxTaxiInfoLength];		
		Candidate[0]=0;
		AnsCount[0]=0;
		TaxiNum=0;
		TaxiGPSPointListLength=0;
		LandMarkNum=0;
		Grid=new LinkList[100][100];
		Edge=new LinkList[50000];
		TaxiHeap=new int[MaxTaxiNum];
		HeapResult=new int[MaxTaxiNum];
		System.gc();
	}
	class LinkListNode{//定义链表，图的边存储需要使用邻接表
		LinkListNode next;
		int value;
		public LinkListNode(){
			next=null;
			value=-1;
		}
	}
	class LinkList{//定义链表
		LinkListNode head,tail,now;
		public LinkList(){
			head=null;
			tail=null;
			now=null;
		}
		public void append(int k){
			if(head==null){
				head=new LinkListNode();
				head.next=null;
				head.value=k;
				tail=head;
			}else{
				now=new LinkListNode();
				now.value=k;
				now.next=null;
				tail.next=now;
				tail=now;
			}
		}
		public void start(){
			now=head;
		}
		public int next(){
			if(now==null) return -1;
			else{
				int k=now.value;
				now=now.next;
				return k;
			}
		}
	}
	int GPSPointAll,RoadAll;
	double LongitudeStart,LongitudeEnd,LatitudeStart,LatitudeEnd;
	LinkList[][] Grid;
	LinkList[] Edge;
	double[] Longitude,Latitude;
	void FreshHash(){
		for(int i=0;i<GPSPointAll;i++) hash[i]=false;
	}
	int MaxQueue=50000-5;
	public GPSSet(){
		Grid=new LinkList[100][100];
		Edge=new LinkList[50000];
		Candidate=new int[10000];
		hash=new boolean[50000];
		dist=new double[50000];
		queue=new int[50000];
		father=new int[50000];
		Longitude=new double[50000];
		Latitude=new double[50000];
	}
	void EdgeAppend(int u,int v){//加入路网中的边
		if(Edge[u]==null) Edge[u]=new LinkList();
		if(Edge[v]==null) Edge[v]=new LinkList();
		Edge[u].append(v);
		Edge[v].append(u);
	}
	double GetX(int k){//得到GridFile索引结构中第k个File的起始经度
		return LongitudeStart+(LongitudeEnd-LongitudeStart)*k/100;
	}
	double GetY(int k){//得到GridFile索引结构中第k个File的起始纬度
		return LatitudeStart+(LatitudeEnd-LatitudeStart)*k/100;
	}
	void GridAppend(int k,double x,double y){//GridFile数据结构中网格内容的添加，用于快速检索
	//-------------------------
		int l,r,mid;
		l=0;r=99;
		while(l!=r){
			mid=(l+r+1)/2;
			if(x>=GetX(mid)) l=mid;
			else r=mid-1;
		}
		int a=l;
		l=0;r=99;
		while(l!=r){
			mid=(l+r+1)/2;
			if(y>=GetY(mid)) l=mid;
			else r=mid-1;
		}
		int b=l;
	//--------------------------
		if(Grid[a][b]==null) Grid[a][b]=new LinkList();
		Grid[a][b].append(k);
	}
	int[] Candidate;
	public double Distance(double x1,double y1,double x2,double y2){
		double midcos=(y1+y2)/2;
		double disy=(y2-y1)*6371*1000/180*Math.PI;
		double disx=(x2-x1)*6371*1000*Math.cos(Math.PI/180*midcos)/180*Math.PI;
		return Math.sqrt(disy*disy+disx*disx);
	}
	public void Neighbor(double x,double y,double dis){//查询离给定的x和y坐标相距dis的所有点
		Candidate[0]=0;
		int l,r,mid;
		l=0;r=99;
		while(l!=r){
			mid=(l+r+1)/2;
			if(x>=GetX(mid)) l=mid;
			else r=mid-1;
		}
		int a=l;
		l=0;r=99;
		while(l!=r){
			mid=(l+r+1)/2;
			if(y>=GetY(mid)) l=mid;
			else r=mid-1;
		}
		int b=l;
		//--------------------------
		for(int i=(int)max(0,a-3);i<=(int)min(99,a+3);i++){
			//if(i<0) continue;
			//if(i==100) continue;
			for(int j=(int)max(0,b-3);j<=(int)min(99,b+3);j++){
				//if(j<0) continue;
				//if(j==100) continue;
				if(Grid[i][j]==null) continue;
				Grid[i][j].start();
				int k;
				while((k=Grid[i][j].next())!=-1){
					if(Distance(x,y,Longitude[k],Latitude[k])<dis)
					{
						Candidate[0]++;
						Candidate[Candidate[0]]=k;
					}
				}
			}
		}
	}
	double max(double a,double b){
		if(a<b) return b;
		else return a;
	}
	double min(double a,double b){
		if(a<b) return a;
		else return b;
	}
	boolean inside(double x0,double y0,double x1,double y1,double x2,double y2){//检查是否落在一个矩形区域
		if(x0<min(x1,x2)-0.015) return false;
		if(x0>max(x1,x2)+0.015) return false;
		if(y0<min(y1,y2)-0.015) return false;
		if(y0>max(y1,y2)+0.015) return false;
		return true;
	}
	boolean[] hash;
	double[] dist;
	int[] queue;
	int[] father;
	int open,closed;
	int[] AnsCount=new int[10000];
	int[] LastAns=new int[10000];
	double WalkDis=500;
	public int SearchRoute(double x1,double y1,double x2,double y2){//BFS算法找出路径
		Neighbor(x1,y1,WalkDis);
		AnsCount[0]=0;
		FreshHash();
		open=-1;
		closed=-1;
		for(int i=1;i<=Candidate[0];i++){
			open++;
			queue[open]=Candidate[i];
			hash[Candidate[i]]=true;
			father[open]=closed;
		}
		while(closed!=open){
			closed++;
			int now=queue[closed];
			Edge[now].start();
			int k;
			while((k=Edge[now].next())!=-1){
				if(hash[k]) continue;
				if(!inside(Longitude[k],Latitude[k],x1,y1,x2,y2)) continue;
				open++;
				queue[open]=k;
				hash[k]=true;
				father[open]=closed;
				if(Distance(Longitude[k],Latitude[k],x2,y2)<WalkDis){
					AnsCount[0]++;
					AnsCount[AnsCount[0]]=closed;
					LastAns[AnsCount[0]]=k;
					open--;
				}
			}
			if(AnsCount[0]>1000) break;
		}
		return AnsCount[0];
	}
	public void FreshDist(){
		for(int i=0;i<GPSPointAll;i++) dist[i]=1e100;
	}
	public int SPFA(double x1,double y1,double x2,double y2){
		Neighbor(x1,y1,WalkDis);
		AnsCount[0]=0;
		FreshDist();
		FreshHash();
		open=-1;
		closed=-1;
		for(int i=1;i<=Candidate[0];i++){
			open++;
			queue[open]=Candidate[i];
			dist[Candidate[i]]=Distance(x1,y1,Longitude[Candidate[i]],Latitude[Candidate[i]]);
			hash[Candidate[i]]=true;
			father[Candidate[i]]=-1;
		}
		while(closed!=open){
			closed++;
			closed%=MaxQueue;
			int now=queue[closed];
			hash[now]=false;
			Edge[now].start();
			int k;
			while((k=Edge[now].next())!=-1){
				if(!inside(Longitude[k],Latitude[k],x1,y1,x2,y2)) continue;
				if(dist[now]+Distance(Longitude[now],Latitude[now],Longitude[k],Latitude[k])>=dist[k]) continue;
				dist[k]=dist[now]+Distance(Longitude[now],Latitude[now],Longitude[k],Latitude[k]);
				father[k]=now;
				if(hash[k]) continue;
				open++;
				open%=MaxQueue;
				queue[open]=k;
				hash[k]=true;
			}
		}
		AnsCount[0]=0;
		Neighbor(x2,y2,WalkDis);
		for(int i=1;i<=Candidate[0];i++){
			if(dist[Candidate[i]]>1e90) continue;
			AnsCount[0]++;
			AnsCount[AnsCount[0]]=father[Candidate[i]];
			LastAns[AnsCount[0]]=Candidate[i];
		}
		//Low Level Sort in small scale
		for(int i=1;i<=AnsCount[0]-1;i++)
			for(int j=1;j<=AnsCount[0]-1;j++){
				if(dist[LastAns[j]]>dist[LastAns[j+1]]){
					LastAns[0]=LastAns[j+1];
					LastAns[j+1]=LastAns[j];
					LastAns[j]=LastAns[0];
					LastAns[0]=AnsCount[j+1];
					AnsCount[j+1]=AnsCount[j];
					AnsCount[j]=LastAns[0];
				}
			}
		for(int i=0;i<GPSPointAll;i++) queue[i]=i;
		return AnsCount[0];
	}
	public void Input(File fin,double dx,double dy){//读入路网数据流
		try{
		FileReader _in=new FileReader(fin);
		BufferedReader in=new BufferedReader(_in);
		String buf;
		buf=in.readLine();
		RoadAll=Integer.parseInt(buf);
		buf=in.readLine();
		GPSPointAll=Integer.parseInt(buf);
		for(int i=0;i<RoadAll;i++){
			buf=in.readLine();
			int st=Integer.parseInt(buf.substring(buf.indexOf(':')+1,buf.indexOf(' ')));
			int en=Integer.parseInt(buf.substring(buf.indexOf(' ')+1));
			for(int j=st;j<en-1;j++) EdgeAppend(j,j+1);
		}
		for(int i=0;i<GPSPointAll;i++){
			buf=in.readLine();
			Longitude[i]=Double.parseDouble(buf.substring(buf.indexOf(':')+1,buf.indexOf(' ')))+dx;
			Latitude[i]=Double.parseDouble(buf.substring(buf.indexOf(' ')+1))+dy;
			GridAppend(i,Longitude[i],Latitude[i]);
		}
		while((buf=in.readLine())!=null){
			if(buf.indexOf('S')==-1) continue;
			buf=in.readLine();
			int u=Integer.parseInt(buf.substring(0,buf.indexOf(' ')));
			int v=Integer.parseInt(buf.substring(buf.indexOf(' ')+1));
			EdgeAppend(u,v);
		}
		_in.close();
		in.close();
		}catch(Exception e){System.out.println(e);System.exit(0);}
	}
//---------------------------------------------------------------------------
//TaxiPart:
//---------------------------------------------------------------------------
	int MaxTaxiNum=500;
	int MaxTaxiInfoLength=10000;
	String[] TaxiID=new String[MaxTaxiNum];
	int[] TaxiPtr=new int[MaxTaxiNum];
	int[] TaxiGPSStart=new int[MaxTaxiNum],TaxiGPSEnd=new int[MaxTaxiNum];
	int TaxiNum=0;
	int TaxiGPSPointListLength=0;
	double[] TaxiLongitudeList=new double[MaxTaxiInfoLength],TaxiLatitudeList=new double[MaxTaxiInfoLength];
	int[] TaxiHourList=new int[MaxTaxiInfoLength],TaxiMinuteList=new int[MaxTaxiInfoLength],TaxiSecondList=new int[MaxTaxiInfoLength];
	
	public void TaxiStream(File Dir,double dx,double dy){//读入出租车信息
		try{
			FileAccept File_Accept = new FileAccept();
			File_Accept.setExtendName("plt");
			File[] File_list = Dir.listFiles(File_Accept);
			String str;
			int where,ptr;
			double templatitude,templongitude;
			int tempyear,tempmonth,tempday,temphour,tempminute,tempsecond;
			for(File FileItem:File_list){
				FileReader _in=new FileReader(FileItem);
				BufferedReader in=new BufferedReader(_in);
				TaxiGPSStart[TaxiNum]=TaxiGPSPointListLength;
				while((str = in.readLine())!=null){
					ptr=0;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;
					templatitude=java.lang.Double.parseDouble(str.substring(ptr,where));
					templatitude+=dy;
					
					ptr=where+1;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;
					templongitude=java.lang.Double.parseDouble(str.substring(ptr,where));
					templongitude+=dx;
					
					ptr=where+1;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;

					ptr=where+1;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;

					ptr=where+1;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;
		
					ptr=where+1;
					where = str.indexOf('-',ptr);
					if(where == -1) continue;
					tempyear=Integer.parseInt(str.substring(ptr,where));
					
					ptr=where+1;
					where = str.indexOf('-',ptr);
					if(where == -1) continue;
					tempmonth=Integer.parseInt(str.substring(ptr,where));
					
					ptr=where+1;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;
					tempday=Integer.parseInt(str.substring(ptr,where));
					
					ptr=where+1;
					where = str.indexOf(':',ptr);
					if(where == -1) continue;
					temphour=Integer.parseInt(str.substring(ptr,where));
					
					ptr=where+1;
					where = str.indexOf(':',ptr);
					if(where == -1) continue;
					tempminute=Integer.parseInt(str.substring(ptr,where));
					
					ptr=where+1;
					if(ptr>=str.length()) continue;
					tempsecond=Integer.parseInt(str.substring(ptr));
									
					TaxiLongitudeList[TaxiGPSPointListLength]=templongitude;
					TaxiLatitudeList[TaxiGPSPointListLength]=templatitude;
					TaxiHourList[TaxiGPSPointListLength]=temphour;
					TaxiMinuteList[TaxiGPSPointListLength]=tempminute;
					TaxiSecondList[TaxiGPSPointListLength]=tempsecond;
					TaxiGPSPointListLength++;
				}
				_in.close();
				in.close();
				TaxiGPSEnd[TaxiNum]=TaxiGPSPointListLength;
				TaxiID[TaxiNum]=FileItem.getName();
				TaxiNum++;
			}
			for(int i=0;i<TaxiNum;i++){
				TaxiPtr[i]=TaxiGPSStart[i];
			}
			InitTaxiHeap();
		}catch(Exception e){System.out.println(e);System.exit(0);}
	}
	public void FreshTaxiHeap(){//重置链表小根堆
		for(int i=0;i<TaxiNum;i++) TaxiPtr[i]=TaxiGPSStart[i];
		InitTaxiHeap();
	}
	int[] TaxiHeap=new int[MaxTaxiNum];
	void InitTaxiHeap(){//初始化小根堆
		TaxiHeap[0]=0;
		for(int i=0;i<TaxiNum;i++){
			if(TaxiGPSStart[i]==TaxiGPSEnd[i]) continue;
			TaxiHeapPush(i);
		}
	}
	void TaxiHeapPush(int k){//元素压入小根堆
		if(TaxiHeap[0]==0)
		{
			TaxiHeap[0]=1;
			TaxiHeap[1]=k;
		}else{
			TaxiHeap[0]++;
			TaxiHeap[TaxiHeap[0]]=k;
			TaxiHeapUp(TaxiHeap[0]);
		}
	}
	int TaxiHeapValue(int k){//元素的权值计算
		int sum=TaxiSecondList[TaxiPtr[TaxiHeap[k]]];
		sum+=TaxiMinuteList[TaxiPtr[TaxiHeap[k]]]*60;
		sum+=TaxiHourList[TaxiPtr[TaxiHeap[k]]]*3600;
		return sum;
	}
	void TaxiHeapSwap(int a,int b){//元素替换
		int temp=TaxiHeap[a];
		TaxiHeap[a]=TaxiHeap[b];
		TaxiHeap[b]=temp;
	}
	void TaxiHeapUp(int k){//小根堆上调操作
		int father=k/2;
		while(father!=0){
			if(TaxiHeapValue(father)>TaxiHeapValue(k)){
				TaxiHeapSwap(father,k);
				k=father;
				father=k/2;
			}else break;
		}
	}
	void TaxiHeapDown(int k){//小根堆下调操作
		int lch=k*2;
		int rch=lch+1;
		int candidate;
		while(lch<=TaxiHeap[0]){
			candidate=k;
			if(TaxiHeapValue(candidate)>TaxiHeapValue(lch)) candidate=lch;
			if(rch<=TaxiHeap[0])
				if(TaxiHeapValue(candidate)>TaxiHeapValue(rch)) candidate=rch;
			if(candidate==k) break;
			TaxiHeapSwap(k,candidate);
			k=candidate;
			lch=k*2;
			rch=lch+1;
		}
	}
	int[] HeapResult=new int[MaxTaxiNum];
	void TaxiHeapPop(int t){//小根堆元素出堆
		HeapResult[0]=0;
		int hh,mm,ss;
		if((TaxiHeap[0]!=0)){
			while((t>=TaxiHeapValue(1))){//Change Item
				while(((t>=TaxiHeapValue(1)))){//The Same Item
				TaxiPtr[TaxiHeap[1]]++;
				if(TaxiPtr[TaxiHeap[1]]==TaxiGPSEnd[TaxiHeap[1]]) break;
				}
				hh=TaxiHourList[TaxiPtr[TaxiHeap[1]]-1];
				mm=TaxiMinuteList[TaxiPtr[TaxiHeap[1]]-1];
				ss=TaxiSecondList[TaxiPtr[TaxiHeap[1]]-1];
				if(Math.abs(hh*3600+mm*60+ss-t)<15){
				HeapResult[0]++;
				HeapResult[HeapResult[0]]=TaxiHeap[1];
				}
				if(TaxiPtr[TaxiHeap[1]]==TaxiGPSEnd[TaxiHeap[1]]){
					TaxiHeap[1]=TaxiHeap[TaxiHeap[0]];
					TaxiHeap[0]--;
				}
				if(TaxiHeap[0]==0) break;
				TaxiHeapDown(1);
			}
		}
	}
//-----------------------------------------------------------------------
//LandMark:
public double[] LandMarkLongitude=new double[10000];
public double[] LandMarkLatitude=new double[10000];
public String[] LandMarkName=new String[10000];
public String[] LandMarkType=new String[10000];
public String[] LandMarkScript=new String[10000];
public int LandMarkNum=0;
public void AddLandMark(double x0,double y0,String strName,String strType,String strScript){//写入地标元素条目
	LandMarkLongitude[LandMarkNum]=x0;
	LandMarkLatitude[LandMarkNum]=y0;
	LandMarkName[LandMarkNum]=strName;
	LandMarkType[LandMarkNum]=strType;
	LandMarkScript[LandMarkNum]=strScript;
	LandMarkNum++;
}
public void LandMarkSave(File LandMarkFile){//将内存中的地标元素信息写入文件
	try{
		if(LandMarkFile==null) return;
		BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LandMarkFile,false),"UTF-8"));
		for(int i=0;i<LandMarkNum;i++){
			out.write("[LandMarkStart]------------------------------");
			out.newLine();
			out.write(Double.toString(LandMarkLongitude[i]));
			out.newLine();
			out.write(Double.toString(LandMarkLatitude[i]));
			out.newLine();
			out.write(LandMarkName[i]);
			out.newLine();
			out.write(LandMarkType[i]);
			out.newLine();
			out.write(LandMarkScript[i].trim());
			out.newLine();
			out.write("[LandMarkEnd]------------------------------");
			out.newLine();
		}
		out.flush();
		out.close();
	}catch(Exception e){
		System.out.println(e);
		System.exit(0);
	}
}
public void LandMarkStream(File LandMarkFile){//读入地标文件
	try{
		if(LandMarkFile==null) return;
		BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(LandMarkFile),"UTF-8"));
		String buf;
		while((buf=in.readLine())!=null){
			if(buf.indexOf("LandMarkStart")==-1) continue;
			String LandMarkLongitudeBuf;
			String LandMarkLatitudeBuf;
			String LandMarkNameBuf;
			String LandMarkTypeBuf;
			String LandMarkScriptBuf="";
			LandMarkLongitudeBuf=in.readLine();
			LandMarkLatitudeBuf=in.readLine();
			LandMarkNameBuf=in.readLine();
			LandMarkTypeBuf=in.readLine();
			while((buf=in.readLine()).indexOf("LandMarkEnd")==-1){
				LandMarkScriptBuf=LandMarkScriptBuf+buf+"\n";
			}
			LandMarkLongitude[LandMarkNum]=Double.parseDouble(LandMarkLongitudeBuf);
			LandMarkLatitude[LandMarkNum]=Double.parseDouble(LandMarkLatitudeBuf);
			LandMarkName[LandMarkNum]=LandMarkNameBuf;
			LandMarkType[LandMarkNum]=LandMarkTypeBuf;
			LandMarkScript[LandMarkNum]=LandMarkScriptBuf;
			LandMarkNum++;
		}
		in.close();
	}catch(Exception e){
		System.out.println(e);
		System.exit(0);
	}
}
public void LandMarkDeleteRow(int k){//删除一行数据库记录
	for(int i=k+1;i<LandMarkNum;i++){
		LandMarkLongitude[i-1]=LandMarkLongitude[i];
		LandMarkLatitude[i-1]=LandMarkLatitude[i];
		LandMarkName[i-1]=LandMarkName[i];
		LandMarkType[i-1]=LandMarkType[i];
		LandMarkScript[i-1]=LandMarkScript[i];
	}
	LandMarkNum--;
}
public void LandMarkUpdateRow(int k,String i1,String i2,String i3,String i4){//更新一行数据库记录
	LandMarkLongitude[k]=Double.parseDouble(i1);
	LandMarkLatitude[k]=Double.parseDouble(i2);
	LandMarkName[k]=i3;
	LandMarkType[k]=i4;
}
//-----------------------------------------------------------------------
}
