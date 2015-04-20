package Database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class PointDataSet implements PointDatabaseInterface{
	public double[] AllPointX;
	public double[] AllPointY;
	public int PointNum;
	public String[] PointHint;
	public int[] PointVisible;
	
	public PointDataSet(){
		DatabaseInit();
	}
	public void DatabaseFileInput(File Input){
		try{
			if(Input==null) return;
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(Input),"UTF-8"));
			double DeltaX=0,DeltaY=0;
			String buf;
			while((buf=in.readLine())!=null){
				if(buf.indexOf("Delta:")!=-1){
					int i,j;
					i=buf.indexOf(':');
					j=buf.indexOf(":",i+1);
					DeltaX=Double.parseDouble(buf.substring(i+1,j));
					DeltaY=Double.parseDouble(buf.substring(j+1));
				}
				if(buf.indexOf("PointStart")==-1) continue;
				String Longitude,Latitude;
				String Hint;
				Hint=in.readLine();
				PointHint[PointNum]=Hint.trim();
				Hint=in.readLine();
				PointVisible[PointNum]=Integer.parseInt(Hint);
				
				buf=in.readLine();
				Longitude=buf;
				Latitude=Longitude.substring(Longitude.indexOf('/')+1);
				Longitude=Longitude.substring(0,Longitude.indexOf('/'));
				AllPointX[PointNum]=Double.parseDouble(Longitude)+DeltaX;
				AllPointY[PointNum]=Double.parseDouble(Latitude)+DeltaY;
				PointNum++;
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void DatabaseFileOutput(File Output){
		try{
			if(Output==null) return;
			BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Output,false),"UTF-8"));
			for(int i=0;i<PointNum;i++){
				out.write("[PointStart]------------------------------");
				out.newLine();
				out.write(PointHint[i].trim());
				out.newLine();
				out.write(Integer.toString(PointVisible[i]));
				out.newLine();
				
				out.write(AllPointX[i]+"/"+AllPointY[i]);
				out.newLine();

				out.write("[PointEnd]------------------------------");
				out.newLine();
			}
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void add(java.util.ArrayList arr,String Hint){
		Object p=arr.get(arr.size()-1);
		if(p instanceof PointStructure) add((PointStructure)p,Hint);
		if(p instanceof TimeStampPointStructure) add((TimeStampPointStructure)p,Hint);
	}
	public void add(PointStructure p,String Hint){
		add(p.x,p.y,Hint);
	}
	public void add(TimeStampPointStructure p,String Hint){
		add(p.x,p.y,Hint);
	}
	public void add(double PointX,double PointY,String Hint){
		AllPointX[PointNum]=PointX;
		AllPointY[PointNum]=PointY;
		PointHint[PointNum]=Hint;
		PointVisible[PointNum]=7;
		PointNum++;
	}
	public void add(double PointX,double PointY,int Attribute,String Hint){
		AllPointX[PointNum]=PointX;
		AllPointY[PointNum]=PointY;
		PointHint[PointNum]=Hint;
		PointVisible[PointNum]=Attribute;
		PointNum++;
	}
	public void DatabaseDelete(int k){
		if(k>=PointNum) return;
		if(k<0) return;
		for(int i=k+1;i<PointNum;i++){
			AllPointX[i-1]=AllPointX[i];
			AllPointY[i-1]=AllPointY[i];
			PointHint[i-1]=PointHint[i];
			PointVisible[i-1]=PointVisible[i];
		}
		PointNum--;
	}
	public void DatabaseRemove(int k){
		PointVisible[k]=-1;
	}
	public void DatabaseResize(){
		int count=0;
		for(int i=0;i<PointNum;i++){
			if(PointVisible[i]<0) continue;
			AllPointX[count]=AllPointX[i];
			AllPointY[count]=AllPointY[i];
			PointHint[count]=PointHint[i];
			PointVisible[count]=PointVisible[i];
			count++;
		}
		PointNum=count;
	}
	public static int PointMaxNum=10000000;
	public void DatabaseInit(){
		AllPointX=new double[PointMaxNum];
		AllPointY=new double[PointMaxNum];
		PointNum=0;
		PointVisible=new int[PointMaxNum];
		for(int i=0;i<PointMaxNum;i++){
			PointVisible[i]=0;
		}
		PointHint=new String[PointMaxNum];
		System.gc();
	}
	public void Clear(int index){
		PointHint[index]=null;
		PointVisible[index]=0;
	}
	public void update(int index,int visible,String Hint){
		PointVisible[index]=visible;
		PointHint[index]=Hint;
	}
	public void update(String index,String visible,String Hint){
		update(Integer.parseInt(index),Integer.parseInt(visible),Hint);
	}
	public String getTitle(int k){
		int st=PointHint[k].indexOf("[Title:");
		int en=PointHint[k].indexOf("]",st);
		if(st==-1) return "无名称点";
		if(en==-1) return "无名称点";
		return PointHint[k].substring(st+7,en);
	}
	public void DatabaseDelete(String KeyWord) {
		// TODO Auto-generated method stub
		int ptr=PointNum-1;
		while(ptr!=-1){
			if(PointHint[ptr].indexOf(KeyWord)!=-1){
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
		for(int i=PointNum-1;i>=0;i--){
			if(PointHint[i].indexOf(Info0)==-1) continue;
			if(PointHint[i].indexOf(Info1)==-1) continue;
			if(PointHint[i].indexOf(Info2)==-1) continue;
			if(PointHint[i].indexOf(Info3)==-1) continue;
			if(PointHint[i].indexOf(Info4)==-1) continue;
			DatabaseDelete(i);
		}
	}
	@Override
	public double GetMBRX1(int index) {
		// TODO Auto-generated method stub
		return AllPointX[index];
	}
	@Override
	public double GetMBRX2(int index) {
		// TODO Auto-generated method stub
		return AllPointX[index];
	}
	@Override
	public double GetMBRY1(int index) {
		// TODO Auto-generated method stub
		return AllPointY[index];
	}
	@Override
	public double GetMBRY2(int index) {
		// TODO Auto-generated method stub
		return AllPointY[index];
	}
	@Override
	public int GetElementNum() {
		// TODO Auto-generated method stub
		return PointNum;
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
