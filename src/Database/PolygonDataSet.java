package Database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class PolygonDataSet implements PolygonDatabaseInterface{
	public double[] AllPointX;
	public double[] AllPointY;
	public double[] dx;
	public double[] dy;
	public boolean[] isVertical;
	public int[] AllPointNext;
	public int PolygonNum;
	public String[] PolygonHint;
	public int[] PolygonHead,PolygonTail;
	public int FreeHead;
	public int[] PolygonVisible;
	public ConvexHull ConvexHullGenerator=new ConvexHull();
	public int PointUse;
	public PolygonDataSet(){
		DatabaseInit();
	}
	public void DatabaseFileInput(File Input){
		if(Input==null) return;
		BufferedReader in=null;
		try{
			in=new BufferedReader(new InputStreamReader(new FileInputStream(Input),"UTF-8"));
			double DeltaX=0,DeltaY=0;
			String buf;
			if (Input.getName().endsWith(".csv")) {
				buf = in.readLine();
				if(buf==null) return;
				buf=buf.substring(buf.lastIndexOf('\uFEFF')+1);
				String[] AttributionList = buf.split(",|\t");
				while ((buf = in.readLine()) != null) {
					if(buf.isEmpty()||buf.equals("-1")) continue;
					String[] ValueList = buf.split(",|\t",-1);
					String Latitude_Str = null;
					String Longitude_Str = null;
					Latitude_Str = "0";
					Longitude_Str = "0";
					PolygonHint[PolygonNum] = "";
					PolygonVisible[PolygonNum] = 11;
					isVertical[PolygonNum] = false;
					dx[PolygonNum] = 0;
					dy[PolygonNum] = 0;
					for (int i = 0; i < AttributionList.length; i++) {
						if (i >= ValueList.length)
							break;
						String signal=AttributionList[i].toLowerCase().trim();
						ValueList[i]=ValueList[i].trim();
						if (signal.endsWith("latitude")) {
							Latitude_Str = ValueList[i];
						} else if (signal.endsWith("longitude")) {
							Longitude_Str = ValueList[i];
						} else if (signal.equals("hint")) {
							PolygonHint[PolygonNum] = ValueList[i];
						} else if (signal.equals("visible")) {
							PolygonVisible[PolygonNum] = Integer
									.parseInt(ValueList[i]);
						} else if (signal.equals("vertical")) {
							isVertical[PolygonNum] = ValueList[i].equals("0") ? false
									: true;
						} else if (signal.equals("dx")) {
							dx[PolygonNum] = Double.parseDouble(ValueList[i]);
						} else if (signal.equals("dy")) {
							dy[PolygonNum] = Double.parseDouble(ValueList[i]);
						} else {
							if (i < ValueList.length) {
								PolygonHint[PolygonNum] += "["
										+ AttributionList[i] + ":"
										+ ValueList[i] + "]";
							}
						}
					}
					append(PolygonNum, Double.parseDouble(Longitude_Str),
							Double.parseDouble(Latitude_Str));
					buf = in.readLine();
					while ((buf!=null)&&(!buf.isEmpty())&&(!buf.equals("-1"))) {
						ValueList = buf.split(",");
						Latitude_Str = "0";
						Longitude_Str = "0";
						for (int i = 0; i < AttributionList.length; i++) {
							String signal = AttributionList[i].toLowerCase();
							if (signal.endsWith("latitude")) {
								Latitude_Str = ValueList[i];
							} else if (signal.endsWith("longitude")) {
								Longitude_Str = ValueList[i];
							}
						}
						append(PolygonNum, Double.parseDouble(Longitude_Str),
								Double.parseDouble(Latitude_Str));
						buf = in.readLine();
					}
					PolygonNum++;
				}
			} else {
				while ((buf = in.readLine()) != null) {
					if (buf.indexOf("Delta:") != -1) {
						int i, j;
						i = buf.indexOf(':');
						j = buf.indexOf(":", i + 1);
						DeltaX = Double.parseDouble(buf.substring(i + 1, j));
						DeltaY = Double.parseDouble(buf.substring(j + 1));
					}
					if (buf.indexOf("PolygonStart") == -1)
						continue;
					String Longitude, Latitude;
					String Hint;
					Hint = in.readLine();
					PolygonHint[PolygonNum] = Hint.trim();
					Hint = in.readLine();
					PolygonVisible[PolygonNum] = Integer.parseInt(Hint);
					Hint = in.readLine();
					isVertical[PolygonNum] = Hint.equals("0") ? false : true;
					Hint = in.readLine();
					dx[PolygonNum] = Double.parseDouble(Hint);
					Hint = in.readLine();
					dy[PolygonNum] = Double.parseDouble(Hint);
					while ((buf = in.readLine()).indexOf("PolygonEnd") == -1) {
						Longitude = buf;
						Latitude = Longitude
								.substring(Longitude.indexOf('/') + 1);
						Longitude = Longitude.substring(0,
								Longitude.indexOf('/'));
						append(PolygonNum, Double.parseDouble(Longitude)
								+ DeltaX, Double.parseDouble(Latitude) + DeltaY);
					}
					PolygonNum++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				in.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public void DatabaseFileOutput(File Output){
		if(Output==null) return;
		FileOutputStream fostream=null;
		BufferedWriter out=null;
		try{
			fostream=new FileOutputStream(Output,false);
			out=new BufferedWriter(new OutputStreamWriter(fostream,"UTF-8"));
			//-------------------------------------------------------------
			if (Output.getName().endsWith(".csv")) {
				fostream.write(new byte[] { (byte) 0xEF, (byte) 0xBB,
						(byte) 0xBF });
				out.write("Latitude,Longitude,Hint,Visible,Vertical,dx,dy");
				out.newLine();
				for (int i = 0; i < PolygonNum; i++) {
					out.write(AllPointY[PolygonHead[i]] + "," + AllPointX[PolygonHead[i]] + ","
							+ PolygonHint[i].trim() + "," + PolygonVisible[i]+","+(isVertical[i]?"1":"0")+","+dx[i]+","+dy[i]);
					out.newLine();
					int temp=AllPointNext[PolygonHead[i]];
					while(temp!=-1){
						out.write(AllPointY[temp]+","+AllPointX[temp]+",,,,,");
						out.newLine();
						temp=AllPointNext[temp];
					}
					out.newLine();
				}
			}else for(int i=0;i<PolygonNum;i++){
				out.write("[PolygonStart]------------------------------");
				out.newLine();
				out.write(PolygonHint[i].trim());
				out.newLine();
				out.write(Integer.toString(PolygonVisible[i]));
				out.newLine();
				out.write(isVertical[i]?"1":"0");
				out.newLine();
				out.write(Double.toString(dx[i]));
				out.newLine();
				out.write(Double.toString(dy[i]));
				out.newLine();
				int temp=PolygonHead[i];
				while(temp!=-1){
					out.write(AllPointX[temp]+"/"+AllPointY[temp]);
					out.newLine();
					temp=AllPointNext[temp];
				}
				out.write("[PolygonEnd]------------------------------");
				out.newLine();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				out.flush();
				out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public void MoveEntireData(double longitude_delta,double latitude_delta){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		for(int i=0;i<PolygonNum;i++){
			int ptr=PolygonHead[i];
			while(ptr!=-1){
				AllPointX[ptr]+=longitude_delta;
				AllPointY[ptr]+=latitude_delta;
				ptr=AllPointNext[ptr];
			}
		}
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public int PointAllocate(){
		PointUse++;
		int k=FreeHead;
		FreeHead=AllPointNext[FreeHead];
		return k;
	}
	public void PointFree(int k){
		PointUse--;
		AllPointNext[k]=FreeHead;
		FreeHead=k;
	}
	public void append(int k,double x,double y){
		if(PolygonHead[k]==-1){
			PolygonHead[k]=PointAllocate();
			AllPointX[PolygonHead[k]]=x;
			AllPointY[PolygonHead[k]]=y;
			AllPointNext[PolygonHead[k]]=-1;
			PolygonTail[k]=PolygonHead[k];
		}else{
			int temp=PointAllocate();
			AllPointNext[temp]=-1;
			AllPointX[temp]=x;
			AllPointY[temp]=y;
			AllPointNext[PolygonTail[k]]=temp;
			PolygonTail[k]=temp;
		}
	}
	public void add(double[] PointX,double[] PointY,int PointCount,String Hint){
		if(PointCount==0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		PolygonHead[PolygonNum]=-1;
		PolygonTail[PolygonNum]=-1;
		for(int i=0;i<PointCount;i++){
			append(PolygonNum,PointX[i],PointY[i]);
		}
		PolygonHint[PolygonNum]=Hint;
		PolygonVisible[PolygonNum]=11;
		isVertical[PolygonNum]=false;
		dx[PolygonNum]=0;
		dy[PolygonNum]=0;
		PolygonNum++;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void GenerateConvexHull(double[] PointX,double[] PointY,int PointCount,String Hint){
		int n=ConvexHullGenerator.Process(PointX,PointY,PointCount);
		add(PointX,PointY,n,Hint);
	}
	public void Clear(int index){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		PolygonHead[index]=-1;
		PolygonTail[index]=-1;
		PolygonHint[index]=null;
		PolygonVisible[index]=0;
		isVertical[index]=false;
		dx[index]=0;
		dy[index]=0;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseDelete(int k){
		if(k>=PolygonNum) return;
		if(k<0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		int temp=PolygonHead[k];
		PolygonHead[k]=-1;
		int tmp;
		while(temp!=-1){
			tmp=temp;
			temp=AllPointNext[temp];
			PointFree(tmp);
		}
		for(int i=k+1;i<PolygonNum;i++){
			PolygonHead[i-1]=PolygonHead[i];
			PolygonTail[i-1]=PolygonTail[i];
			PolygonHint[i-1]=PolygonHint[i];
			PolygonVisible[i-1]=PolygonVisible[i];
			isVertical[i-1]=isVertical[i];
			dx[i-1]=dx[i];
			dy[i-1]=dy[i];
		}
		PolygonNum--;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseRemove(int k){
		if(k>=PolygonNum) return;
		if(k<0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		int temp=PolygonHead[k];
		int tmp;
		while(temp!=-1){
			tmp=temp;
			temp=AllPointNext[temp];
			PointFree(tmp);
		}
		PolygonVisible[k]=-1;
		PolygonHead[k]=-1;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseResize(){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		int count=0;
		for(int i=0;i<PolygonNum;i++){
			if(PolygonVisible[i]<0) continue;
			PolygonHead[count]=PolygonHead[i];
			PolygonTail[count]=PolygonTail[i];
			PolygonHint[count]=PolygonHint[i];
			PolygonVisible[count]=PolygonVisible[i];
			isVertical[count]=isVertical[i];
			dx[count]=dx[i];
			dy[count]=dy[i];
			count++;
		}
		for(int i=count;i<PolygonNum;i++){
			PolygonHead[i]=-1;
			PolygonTail[i]=-1;
			PolygonHint[i]=null;
			PolygonVisible[i]=0;
			isVertical[i]=false;
			dx[i]=0;
			dy[i]=0;
		}
		PolygonNum=count;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public static int PolygonMaxNum=100000;
	public static int PointMaxNum=10000000;
	public void DatabaseInit(){
		AllPointX=new double[PointMaxNum];
		AllPointY=new double[PointMaxNum];
		AllPointNext=new int[PointMaxNum];
		PointUse=0;
		PolygonNum=0;
		PolygonHead=new int[PolygonMaxNum];
		PolygonTail=new int[PolygonMaxNum];
		PolygonVisible=new int[PolygonMaxNum];
		isVertical=new boolean[PolygonMaxNum];
		dx=new double[PolygonMaxNum];
		dy=new double[PolygonMaxNum];
		for(int i=0;i<PolygonMaxNum;i++){
			PolygonHead[i]=-1;
			PolygonTail[i]=-1;
			PolygonVisible[i]=0;
		}
		FreeHead=0;
		for(int i=0;i<PointMaxNum-1;i++){
			AllPointNext[i]=i+1;
		}
		AllPointNext[PointMaxNum-1]=-1;
		PolygonHint=new String[PolygonMaxNum];
		System.gc();
	}
	public void update(int index,int visible,String Hint){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		PolygonVisible[index]=visible;
		PolygonHint[index]=Hint;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void update(String index,String visible,String Hint){
		update(Integer.parseInt(index),Integer.parseInt(visible),Hint);
	}
	public String getTitle(int k){
		int st=PolygonHint[k].indexOf("[Title:");
		int en=PolygonHint[k].indexOf("]",st);
		if(st==-1) return MapKernel.MapWizard.LanguageDic.GetWords("无名称区域");
		if(en==-1) return MapKernel.MapWizard.LanguageDic.GetWords("无名称区域");
		return PolygonHint[k].substring(st+7,en);
	}
	public void DatabaseDelete(String KeyWord) {
		// TODO Auto-generated method stub
		int ptr=PolygonNum-1;
		while(ptr!=-1){
			if(PolygonHint[ptr].indexOf(KeyWord)!=-1){
				DatabaseDelete(ptr);
			}
			ptr--;
		}
	}
	public double[] MBRMinX=new double[10000];
	public double[] MBRMinY=new double[10000];
	public double[] MBRMaxX=new double[10000];
	public double[] MBRMaxY=new double[10000];
	public void GenerateMBR(){
		for(int i=0;i<PolygonNum;i++){
			MBRMinX[i]=1e100;
			MBRMinY[i]=1e100;
			MBRMaxX[i]=-1e100;
			MBRMaxY[i]=-1e100;
			int ptr=PolygonHead[i];
			while(ptr!=-1){
				MBRMinX[i]=Math.min(MBRMinX[i],AllPointX[ptr]);
				MBRMinY[i]=Math.min(MBRMinY[i],AllPointY[ptr]);
				MBRMaxX[i]=Math.max(MBRMaxX[i],AllPointX[ptr]);
				MBRMaxY[i]=Math.max(MBRMaxY[i],AllPointY[ptr]);
				ptr=AllPointNext[ptr];
			}
		}
	}
	public double Angle(double x1,double y1,double x2,double y2,double x3,double y3){
		double dx1=x1-x2;
		double dy1=y1-y2;
		double dx2=x3-x2;
		double dy2=y3-y2;
		double l1=Math.sqrt(dx1*dx1+dy1*dy1);
		double l2=Math.sqrt(dx2*dx2+dy2*dy2);
		double tmp=Math.acos((dx1*dx2+dy1*dy2)/l1/l2);
		dx1=x1-x2;
		dy1=y1-y2;
		dx2=x3-x1;
		dy2=y3-y1;
		double dir=dx2*dy1-dy2*dx1;
		return tmp*dir/Math.abs(dir);
	}
	public boolean CheckInsideMBR(int PolygonID,double x,double y){
		if(x<MBRMinX[PolygonID]) return false;
		if(y<MBRMinY[PolygonID]) return false;
		if(x>MBRMaxX[PolygonID]) return false;
		if(y>MBRMaxY[PolygonID]) return false;
		return true;
	}
	public boolean CheckInsidePolygon(int PolygonID,double x,double y){
		//if(!CheckInsideMBR(PolygonID,x,y)) return false;
		/* Naive Method
		int ptr=PolygonHead[PolygonID];
		int nextptr=-1;
		double AngleSum=0;
		while(ptr!=-1){
			nextptr=AllPointNext[ptr];
			if(nextptr==-1) nextptr=PolygonHead[PolygonID];
			AngleSum+=Angle(AllPointX[ptr],AllPointY[ptr],x,y,AllPointX[nextptr],AllPointY[nextptr]);
			ptr=nextptr;
			if(ptr==PolygonHead[PolygonID]) break;
		}
		AngleSum=Math.abs(AngleSum);
		if(AngleSum>Math.PI){//Inside 2*PI
			return true;
		}else return false; //Outside 0
		*/
		int ptr=PolygonHead[PolygonID];
		int nextptr=-1;
		int left_counter=0;
		int right_counter=0;
		while(ptr!=-1){
			nextptr=AllPointNext[ptr];
			if(nextptr==-1) nextptr=PolygonHead[PolygonID];
			if((AllPointY[ptr]-y)*(AllPointY[nextptr]-y)<0){
				double midx=AllPointX[ptr]+(AllPointX[nextptr]-AllPointX[ptr])*(y-AllPointY[ptr])/(AllPointY[nextptr]-AllPointY[ptr]);
				if(midx<x) left_counter++;
				else right_counter++;
			}
			ptr=nextptr;
			if(ptr==PolygonHead[PolygonID]) break;
		}
		if(left_counter%2==0) return false;
		if(right_counter%2==0) return false;
		return true;
	}
	public ArrayList<Double> PreciseMatchingAveragePoint(int PolygonID){
		ArrayList<Double> Ans=new ArrayList();
		int ptr=PolygonHead[PolygonID];
		double x=0,y=0;
		int tot=0;
		double AngleSum=0;
		while(ptr!=-1){
			tot++;
			x+=AllPointX[ptr];
			y+=AllPointY[ptr];
			ptr=AllPointNext[ptr];
		}
		x/=tot;
		y/=tot;
		Ans.add(x);
		Ans.add(y);
		return Ans;
	}
	public void AttributeDelete(String Info0,String Info1,String Info2,String Info3,String Info4){
		if(Info0==null) Info0="";else Info0="[Info:"+Info0+"]";
		if(Info1==null) Info1="";else Info1="[Info:"+Info1+"]";
		if(Info2==null) Info2="";else Info2="[Info:"+Info2+"]";
		if(Info3==null) Info3="";else Info3="[Info:"+Info3+"]";
		if(Info4==null) Info4="";else Info4="[Info:"+Info4+"]";
		for(int i=PolygonNum-1;i>=0;i--){
			if(PolygonHint[i].indexOf(Info0)==-1) continue;
			if(PolygonHint[i].indexOf(Info1)==-1) continue;
			if(PolygonHint[i].indexOf(Info2)==-1) continue;
			if(PolygonHint[i].indexOf(Info3)==-1) continue;
			if(PolygonHint[i].indexOf(Info4)==-1) continue;
			DatabaseDelete(i);
		}
	}
	@Override
	public double GetMBRX1(int index) {
		// TODO Auto-generated method stub
		int p=PolygonHead[index];
		double x1=1e100;
		while(p!=-1){
			if(AllPointX[p]<x1) x1=AllPointX[p];
			p=AllPointNext[p];
		}
		return x1;
	}
	@Override
	public double GetMBRX2(int index) {
		// TODO Auto-generated method stub
		int p=PolygonHead[index];
		double x2=-1e100;
		while(p!=-1){
			if(AllPointX[p]>x2) x2=AllPointX[p];
			p=AllPointNext[p];
		}
		return x2;
	}
	@Override
	public double GetMBRY1(int index) {
		// TODO Auto-generated method stub
		int p=PolygonHead[index];
		double y1=1e100;
		while(p!=-1){
			if(AllPointY[p]<y1) y1=AllPointY[p];
			p=AllPointNext[p];
		}
		return y1;
	}
	@Override
	public double GetMBRY2(int index) {
		// TODO Auto-generated method stub
		int p=PolygonHead[index];
		double y2=-1e100;
		while(p!=-1){
			if(AllPointY[p]>y2) y2=AllPointY[p];
			p=AllPointNext[p];
		}
		return y2;
	}
	@Override
	public int GetElementNum() {
		// TODO Auto-generated method stub
		return PolygonNum;
	}
	public IndexInterface IndexObj=null;
	@Override
	public IndexInterface GetIndexPermission() {
		// TODO Auto-generated method stub
		return IndexObj;
	}
	@Override
	public void SetIndexPermission(IndexInterface obj) {
		// TODO Auto-generated method stub
		this.IndexObj=obj;
	}
}
