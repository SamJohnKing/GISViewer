package Database;

import javax.swing.plaf.synth.Region;
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

public class LineDataSet implements LineDatabaseInterface{
	public double[] AllPointX;
	public double[] AllPointY;
	public double[] dx;
	public double[] dy;
	public boolean[] isVertical;
	public int[] AllPointNext;
	public int LineNum;
	public String[] LineHint;
	public int[] LineHead,LineTail;
	public int FreeHead;
	public int[] LineVisible;
	public int PointUse;
	public LineDataSet(){
		DatabaseInit();
	}
	public void DatabaseFileInput(File Input){
		BufferedReader in=null;
		try{
			if(Input==null) return;
			in=new BufferedReader(new InputStreamReader(new FileInputStream(Input),"UTF-8"));
			String buf;
			double DeltaX=0,DeltaY=0;
			if (Input.getName().endsWith(".csv")) {
				buf=in.readLine();
				if(buf==null) return;
				buf=buf.substring(buf.lastIndexOf('\uFEFF')+1);
				String[] AttributionList=buf.split(",|\t");
				while((buf=in.readLine())!=null){
					if(buf.isEmpty()||buf.equals("-1")) continue;
					String[] ValueList=buf.split(",|\t",-1);
					String Latitude_Str=null;
					String Longitude_Str=null;
					Latitude_Str="0";
					Longitude_Str="0";
					LineHint[LineNum]="";
					LineVisible[LineNum]=27;
					isVertical[LineNum]=false;
					dx[LineNum]=0;
					dy[LineNum]=0;
					for(int i=0;i<AttributionList.length;i++){
						if(i>=ValueList.length) break;
						String signal=AttributionList[i].toLowerCase().trim();
						ValueList[i]=ValueList[i].trim();
						if(signal.endsWith("latitude")){
							Latitude_Str=ValueList[i];
						}else if(signal.endsWith("longitude")){
							Longitude_Str=ValueList[i];
						}else if(signal.equals("hint")){
							LineHint[LineNum]=ValueList[i];
						}else if(signal.equals("visible")){
							LineVisible[LineNum]=Integer.parseInt(ValueList[i]);
						}else if(signal.equals("vertical")){
							isVertical[LineNum] = ValueList[i].equals("0") ? false : true;
						}else if(signal.equals("dx")){
							dx[LineNum] = Double.parseDouble(ValueList[i]);
						}else if(signal.equals("dy")){
							dy[LineNum] = Double.parseDouble(ValueList[i]);
						}else{
							if(i<ValueList.length){
								LineHint[LineNum]+="["+AttributionList[i]+":"+ValueList[i]+"]";
							}
						}
					}
					append(LineNum, Double.parseDouble(Longitude_Str),
							Double.parseDouble(Latitude_Str));
					buf = in.readLine();
					while ((buf!=null)&&(!buf.isEmpty())&&(!buf.equals("-1"))) {
						ValueList=buf.split(",");
						Latitude_Str="0";
						Longitude_Str="0";
						for(int i=0;i<AttributionList.length;i++){
							String signal=AttributionList[i].toLowerCase();
							if(signal.endsWith("latitude")){
								Latitude_Str=ValueList[i];
							}else if(signal.endsWith("longitude")){
								Longitude_Str=ValueList[i];
							}
						}
						append(LineNum, Double.parseDouble(Longitude_Str),
								Double.parseDouble(Latitude_Str));
						buf=in.readLine();
					}
					LineNum++;
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
					if (buf.indexOf("LineStart") == -1)
						continue;
					String Longitude, Latitude;
					String Hint;
					Hint = in.readLine();
					LineHint[LineNum] = Hint.trim();
					Hint = in.readLine();
					LineVisible[LineNum] = Integer.parseInt(Hint);
					Hint = in.readLine();
					isVertical[LineNum] = Hint.equals("0") ? false : true;
					Hint = in.readLine();
					dx[LineNum] = Double.parseDouble(Hint);
					Hint = in.readLine();
					dy[LineNum] = Double.parseDouble(Hint);
					while ((buf = in.readLine()).indexOf("LineEnd") == -1) {
						Longitude = buf;
						Latitude = Longitude
								.substring(Longitude.indexOf('/') + 1);
						Longitude = Longitude.substring(0,
								Longitude.indexOf('/'));
						append(LineNum, Double.parseDouble(Longitude) + DeltaX,
								Double.parseDouble(Latitude) + DeltaY);
					}
					LineNum++;
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
				for (int i = 0; i < LineNum; i++) {
					out.write(AllPointY[LineHead[i]] + "," + AllPointX[LineHead[i]] + ","
							+ LineHint[i].trim() + "," + LineVisible[i]+","+(isVertical[i]?"1":"0")+","+dx[i]+","+dy[i]);
					out.newLine();
					int temp=AllPointNext[LineHead[i]];
					while(temp!=-1){
						out.write(AllPointY[temp]+","+AllPointX[temp]+",,,,,");
						out.newLine();
						temp=AllPointNext[temp];
					}
					out.newLine();
				}
			}else
			for(int i=0;i<LineNum;i++){
				out.write("[LineStart]------------------------------");
				out.newLine();
				out.write(LineHint[i].trim());
				out.newLine();
				out.write(Integer.toString(LineVisible[i]));
				out.newLine();
				out.write(isVertical[i]?"1":"0");
				out.newLine();
				out.write(Double.toString(dx[i]));
				out.newLine();
				out.write(Double.toString(dy[i]));
				out.newLine();
				int temp=LineHead[i];
				while(temp!=-1){
					out.write(AllPointX[temp]+"/"+AllPointY[temp]);
					out.newLine();
					temp=AllPointNext[temp];
				}
				out.write("[LineEnd]------------------------------");
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
		for(int i=0;i<LineNum;i++){
			int ptr=LineHead[i];
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
	public void Clear(int index){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		LineHead[index]=-1;
		LineTail[index]=-1;
		LineHint[index]=null;
		LineVisible[index]=0;
		isVertical[index]=false;
		dx[index]=0;
		dy[index]=0;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void append(int k,double x,double y){
		if(LineHead[k]==-1){
			LineHead[k]=PointAllocate();
			AllPointX[LineHead[k]]=x;
			AllPointY[LineHead[k]]=y;
			AllPointNext[LineHead[k]]=-1;
			LineTail[k]=LineHead[k];
		}else{
			int temp=PointAllocate();
			AllPointNext[temp]=-1;
			AllPointX[temp]=x;
			AllPointY[temp]=y;
			AllPointNext[LineTail[k]]=temp;
			LineTail[k]=temp;
		}
	}
	public void add(double[] PointX,double[] PointY,int PointCount,String Hint){
		if(PointCount==0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		LineHead[LineNum]=-1;
		LineTail[LineNum]=-1;
		for(int i=0;i<PointCount;i++){
			append(LineNum,PointX[i],PointY[i]);
		}
		LineHint[LineNum]=Hint;
		LineVisible[LineNum]=27;
		isVertical[LineNum]=false;
		dx[LineNum]=0;
		dy[LineNum]=0;
		LineNum++;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void add(java.util.ArrayList arr,String Hint){
		if(arr.size()==0) return;
		java.util.Iterator iter=arr.iterator();
		Object p;
		PointStructure PSP;
		TimeStampPointStructure TSPSP;
		int Counter=0;
		while(iter.hasNext()){
			p=iter.next();
			if(p instanceof PointStructure){
				PSP=(PointStructure)p;
				StaticX[Counter]=PSP.x;
				StaticY[Counter]=PSP.y;
			}else{
				TSPSP=(TimeStampPointStructure)p;
				StaticX[Counter]=TSPSP.x;
				StaticY[Counter]=TSPSP.y;
			}
			Counter++;
		}
		add(StaticX,StaticY,Counter,Hint);
	}
	double[] StaticX=new double[10000];
	double[] StaticY=new double[10000];
	double[] DynamicX=new double[10];
	double[] DynamicY=new double[10];
	public void DynamicAdd(double x,double y,String Hint){
		boolean Hit=false;
		for(int i=0;i<LineNum;i++){
			if(!LineHint[i].equals(Hint)) continue;
			Hit=true;
			append(i,x,y);
			break;
		}
		if(Hit==false){
			DynamicX[0]=x;
			DynamicY[0]=y;
			add(DynamicX,DynamicY,1,Hint);
		}
	}
	public void DynamicSetVisible(int value,String Hint){
		for(int i=0;i<LineNum;i++){
			if(!LineHint[i].equals(Hint)) continue;
			LineVisible[i]=value;
			break;
		}
	}
	public void DatabaseDelete(int k){
		if(k>=LineNum) return;
		if(k<0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		int temp=LineHead[k];
		LineHead[k]=-1;
		int tmp;
		while(temp!=-1){
			tmp=temp;
			temp=AllPointNext[temp];
			PointFree(tmp);
		}
		for(int i=k+1;i<LineNum;i++){
			LineHead[i-1]=LineHead[i];
			LineTail[i-1]=LineTail[i];
			LineHint[i-1]=LineHint[i];
			LineVisible[i-1]=LineVisible[i];
			isVertical[i-1]=isVertical[i];
			dx[i-1]=dx[i];
			dy[i-1]=dy[i];
		}
		LineNum--;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseRemove(int k){
		if(k>=LineNum) return;
		if(k<0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		int temp=LineHead[k];
		int tmp;
		while(temp!=-1){
			tmp=temp;
			temp=AllPointNext[temp];
			PointFree(tmp);
		}
		LineVisible[k]=-1;
		LineHead[k]=-1;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseResize(){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		int count=0;
		for(int i=0;i<LineNum;i++){
			if(LineVisible[i]<0) continue;
			LineHead[count]=LineHead[i];
			LineTail[count]=LineTail[i];
			LineHint[count]=LineHint[i];
			LineVisible[count]=LineVisible[i];
			isVertical[count]=isVertical[i];
			dx[count]=dx[i];
			dy[count]=dy[i];
			count++;
		}
		for(int i=count;i<LineNum;i++){
			LineHead[i]=-1;
			LineTail[i]=-1;
			LineHint[i]=null;
			LineVisible[i]=0;
			isVertical[i]=false;
			dx[i]=0;
			dy[i]=0;
		}
		LineNum=count;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public static int LineMaxNum=20000000;
	public static int PointMaxNum=50000000;
	public void DatabaseInit(){
		AllPointX=new double[PointMaxNum];
		AllPointY=new double[PointMaxNum];
		AllPointNext=new int[PointMaxNum];
		PointUse=0;
		LineNum=0;
		LineHead=new int[LineMaxNum];
		LineTail=new int[LineMaxNum];
		LineVisible=new int[LineMaxNum];
		isVertical=new boolean[LineMaxNum];
		dx=new double[LineMaxNum];
		dy=new double[LineMaxNum];
		for(int i=0;i<LineMaxNum;i++){
			LineHead[i]=-1;
			LineTail[i]=-1;
			LineVisible[i]=0;
		}
		FreeHead=0;
		for(int i=0;i<PointMaxNum-1;i++){
			AllPointNext[i]=i+1;
		}
		AllPointNext[PointMaxNum-1]=-1;
		LineHint=new String[LineMaxNum];
		System.gc();
	}
	public void update(int index,int visible,String Hint){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		LineVisible[index]=visible;
		LineHint[index]=Hint;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void update(String index,String visible,String Hint){
		update(Integer.parseInt(index),Integer.parseInt(visible),Hint);
	}
	public String getTitle(int k){
		int st=LineHint[k].indexOf("[Title:");
		int en=LineHint[k].indexOf("]",st);
		if(st==-1) return MapKernel.MapWizard.LanguageDic.GetWords("无名称线路");
		if(en==-1) return MapKernel.MapWizard.LanguageDic.GetWords("无名称线路");
		return LineHint[k].substring(st+7,en);
	}
	@Override
	public void DatabaseDelete(String KeyWord) {
		// TODO Auto-generated method stub
		int ptr=LineNum-1;
		while(ptr!=-1){
			if(LineHint[ptr].indexOf(KeyWord)!=-1){
				DatabaseDelete(ptr);
			}
			ptr--;
		}
	}
	public void AttributeDelete(String Info0,String Info1,String Info2,String Info3,String Info4){
		if(Info0==null) Info0="";else Info0="[Info:"+Info0+"]";
		if(Info1==null) Info1="";else Info1="[Info:"+Info1+"]";
		if(Info2==null) Info2="";else Info2="[Info:"+Info2+"]";
		if(Info3==null) Info3="";else Info3="[Info:"+Info3+"]";
		if(Info4==null) Info4="";else Info4="[Info:"+Info4+"]";
		for(int i=LineNum-1;i>=0;i--){
			if(LineHint[i].indexOf(Info0)==-1) continue;
			if(LineHint[i].indexOf(Info1)==-1) continue;
			if(LineHint[i].indexOf(Info2)==-1) continue;
			if(LineHint[i].indexOf(Info3)==-1) continue;
			if(LineHint[i].indexOf(Info4)==-1) continue;
			DatabaseDelete(i);
		}
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
	public double PointLineCheck(int id,double x,double y){
		int ptr=LineHead[id];
		int lastptr=ptr;
	    double x1,y1,x2,y2,x3,y3,px,py;
	    double som,u,xx,yy,dx,dy,dist;
	    dist=1e100;
	    x3=x;
	    y3=y;
		ptr=AllPointNext[ptr];
		while(ptr!=-1){  
			x1=AllPointX[lastptr];
			y1=AllPointY[lastptr];
			x2=AllPointX[ptr];
			y2=AllPointY[ptr];
		    px = x2 - x1;
		    py = y2 - y1;
		    som = px * px + py * py;
		    u =  ((x3 - x1) * px + (y3 - y1) * py) / som;
		    if (u > 1) {
		        u = 1;
		    }
		    if (u < 0) {
		        u = 0;
		    }
		    //the closest point
		    xx = x1 + u * px;
		    yy = y1 + u * py;
		    dx = xx - x3;
		    dy = yy - y3;      
		    dist =Math.min(dist,Math.sqrt(dx*dx + dy*dy));
			lastptr=ptr;
			ptr=AllPointNext[ptr];
		}
		return dist;
	}
	@Override
	public double GetMBRX1(int index) {
		// TODO Auto-generated method stub
		int p=LineHead[index];
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
		int p=LineHead[index];
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
		int p=LineHead[index];
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
		int p=LineHead[index];
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
		return LineNum;
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
	public boolean CheckCross(int p,int q){
		if(p==q) return false;
		int p_ptr=LineHead[p];
		int q_ptr=LineHead[q];
		int p_next_ptr,q_next_ptr;
		double p_x1,p_x2,p_y1,p_y2,q_x1,q_x2,q_y1,q_y2;
		while(p_ptr!=-1){
			p_next_ptr=AllPointNext[p_ptr];
			if(p_next_ptr==-1) break;
			p_x1=AllPointX[p_ptr];
			p_y1=AllPointY[p_ptr];
			p_x2=AllPointX[p_next_ptr];
			p_y2=AllPointY[p_next_ptr];
			while(q_ptr!=-1){
				q_next_ptr=AllPointNext[q_ptr];
				if(q_next_ptr==-1) break;
				q_x1=AllPointX[q_ptr];
				q_y1=AllPointY[q_ptr];
				q_x2=AllPointX[q_next_ptr];
				q_y2=AllPointY[q_next_ptr];
				q_ptr=q_next_ptr;
				if(Math.max(Math.min(p_x1,p_x2),Math.min(q_x1,q_x2))>
						Math.min(Math.max(p_x1,p_x2),Math.max(q_x1,q_x2))) continue;
				if(Math.max(Math.min(p_y1,p_y2),Math.min(q_y1,q_y2))>
						Math.min(Math.max(p_y1,p_y2),Math.max(q_y1,q_y2))) continue;
				if(CrossProduct((p_x2-p_x1),(p_y2-p_y1),(q_x1-p_x1),(q_y1-p_y1))*
					CrossProduct((p_x2-p_x1),(p_y2-p_y1),(q_x2-p_x1),(q_y2-p_y1))>=0) continue;
				if(CrossProduct((q_x2-q_x1),(q_y2-q_y1),(p_x1-q_x1),(p_y1-q_y1))*
					CrossProduct((q_x2-q_x1),(q_y2-q_y1),(p_x2-q_x1),(p_y2-q_y1))>=0) continue;
				return true;
			}
			p_ptr=p_next_ptr;
		}
		return false;
	}
	double CrossProduct(double x1,double y1,double x2,double y2){
		return x1*y2-y1*x2;
	}
}
