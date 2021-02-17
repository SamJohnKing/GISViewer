package Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import MapKernel.FileAccept;

public class TaxiTrajectoryDatabaseClass implements DatabaseInterface{
	public double[] AllTaxiLongitude,AllTaxiLatitude;
	public int[] AllTaxiID,AllTaxiSecond,TaxiPointPtr;
	public int TaxiPointNum,TaxiNum;
	public int[] TaxiST=new int[20000];
	public int[] TaxiEN=new int[20000];
	public double deltaX,deltaY;
	public String[] TrajectoryFile=new String[20000];
	public double[] TaxiVelocity;
	public TaxiTrajectoryDatabaseClass(){
	}
	@Override
	public void DatabaseFileInput(File Input) {
		try{
			MapKernel.FileAccept File_Accept = new FileAccept();
			File_Accept.setExtendName("plt");
			File[] File_list = Input.listFiles(File_Accept);
			String str;
			int where,ptr;
			double templatitude,templongitude;
			int tempyear,tempmonth,tempday,temphour,tempminute,tempsecond;
			for(File FileItem:File_list){
				FileReader _in=new FileReader(FileItem);
				BufferedReader in=new BufferedReader(_in);
				TaxiST[TaxiNum]=TaxiPointNum;
				TrajectoryFile[TaxiNum]=FileItem.toString();
				while((str = in.readLine())!=null){
					ptr=0;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;
					templatitude=java.lang.Double.parseDouble(str.substring(ptr,where));
					templatitude+=deltaY;
					
					ptr=where+1;
					where = str.indexOf(',',ptr);
					if(where == -1) continue;
					templongitude=java.lang.Double.parseDouble(str.substring(ptr,where));
					templongitude+=deltaX;
					
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
					
					temphour=temphour%4+8;//Concentrate all the Trajectory into four hour
					
					AllTaxiSecond[TaxiPointNum]=temphour*3600+tempminute*60+tempsecond;
					AllTaxiID[TaxiPointNum]=TaxiNum;
					AllTaxiLongitude[TaxiPointNum]=templongitude;
					AllTaxiLatitude[TaxiPointNum]=templatitude;
				TaxiPointNum++;
				}
				TaxiEN[TaxiNum]=TaxiPointNum;
				_in.close();
				in.close();
				TaxiNum++;
				TrajectorySplit();
			}
		}catch(Exception e){System.out.println(e);}
		for(int i=0;i<TaxiPointNum;i++){
			TaxiPointPtr[i]=i;
		}
		sort(0,TaxiPointNum-1);
		double ds;
		int dt;
		for(int i=0;i<TaxiNum;i++){
			TaxiVelocity[TaxiST[i]]=-1;
			for(int j=TaxiST[i]+1;j<TaxiEN[i];j++){
				ds=AccurateMeterDistance(AllTaxiLongitude[j-1],AllTaxiLatitude[j-1],AllTaxiLongitude[j],AllTaxiLatitude[j]);
				dt=AllTaxiSecond[j]-AllTaxiSecond[j-1];
				if(dt<0){
					System.out.println("negative");
					System.exit(0);
				}
				if(dt>0) TaxiVelocity[j]=ds/(double)dt;
				else if(dt==0) TaxiVelocity[j]=TaxiVelocity[j-1];
			}
		}
	}
	public double AccurateMeterDistance(double x0,double y0,double x1,double y1){
		double alpha=Math.cos((y1+y0)/360.0*Math.PI);
		double dx=alpha*(x0-x1)/180.0*Math.PI*6371*1000;
		double dy=(y0-y1)/180.0*Math.PI*6371*1000;
		return Math.sqrt(dx*dx+dy*dy);
	}
	int[] interval=new int[1000];
	public void TrajectorySplit(){
		interval[0]=0;
		for(int i=TaxiST[TaxiNum-1]+1;i<TaxiEN[TaxiNum-1];i++){
			if(AllTaxiSecond[i-1]>AllTaxiSecond[i]){
				interval[0]++;
				interval[interval[0]]=i;
			}
		}
		for(int i=1;i<=interval[0];i++){
			TaxiEN[TaxiNum-1]=interval[i];
			TaxiST[TaxiNum]=interval[i];
			TrajectoryFile[TaxiNum]=TrajectoryFile[TaxiNum-1];
			AllTaxiID[interval[i]]=TaxiNum;
			TaxiNum++;
		}
		interval[interval[0]+1]=TaxiPointNum;
		for(int i=1;i<=interval[0];i++){
			for(int j=interval[i]+1;j<interval[i+1];j++){
				AllTaxiID[j]=AllTaxiID[j-1];
			}
		}
		TaxiEN[TaxiNum-1]=TaxiPointNum;
	}
	int getValue(int k){
		return AllTaxiSecond[k];
	}
	public void sort(int l,int r){
		if(l>=r) return;
		int mid=TaxiPointPtr[(l+r)/2];
		int ll=l,rr=r,temp;
		while(ll<=rr){
			while(getValue(TaxiPointPtr[ll])<getValue(mid)) ll++;
			while(getValue(TaxiPointPtr[rr])>getValue(mid)) rr--;
			if(ll<=rr){
			temp=TaxiPointPtr[ll];
			TaxiPointPtr[ll]=TaxiPointPtr[rr];
			TaxiPointPtr[rr]=temp;
			ll++;
			rr--;
			}
		}
		sort(l,rr);
		sort(ll,r);
	}
	public int Iter(int k){
		return TaxiPointPtr[k];
	}
	public int STPtr,ENPtr;
	public void query(int sttime,int entime){
		int l=0,r=TaxiPointNum-1,mid;
		while(l!=r){
			mid=(l+r)/2;
			if(AllTaxiSecond[Iter(mid)]<=entime) l=mid+1;
			else r=mid;
		}
		ENPtr=r;
		l=0;r=TaxiPointNum-1;
		while(l!=r){
			mid=(l+r)/2;
			if(AllTaxiSecond[Iter(mid)]<sttime) l=mid+1;
			else r=mid;
		}
		STPtr=l;
		System.out.println(STPtr+"-->"+ENPtr);
	}
	@Override
	public void DatabaseFileOutput(File Output) {
	}
	@Override
	public void DatabaseInit() {
		int TaxiPointLength=25000;
		TaxiNum=0;
		TaxiPointNum=0;
		AllTaxiLongitude=new double[TaxiPointLength];
		AllTaxiLatitude=new double[TaxiPointLength];
		AllTaxiID=new int[TaxiPointLength];
		AllTaxiSecond=new int[TaxiPointLength];
		TaxiPointPtr=new int[TaxiPointLength];
		TaxiVelocity=new double[TaxiPointLength];
		System.gc();
	}
	@Override
	public void DatabaseDelete(int index) {
	}
	@Override
	public void DatabaseDelete(String KeyWord) {
	}
	public void AttributeDelete(String Info0,String Info1,String Info2,String Info3,String Info4){
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
	public double GetMBRX1(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double GetMBRX2(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double GetMBRY1(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double GetMBRY2(int index) {
		// TODO Auto-generated method stub
		return 0;
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
		return 0;
	}
	@Override
	public IndexInterface GetIndexPermission() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void SetIndexPermission(IndexInterface obj) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Clear(int index) {
		// TODO Auto-generated method stub
		
	}
}
