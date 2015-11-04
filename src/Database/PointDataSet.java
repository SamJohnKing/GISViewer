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
		if(Input==null) return;
		BufferedReader in=null;
		try{
			in=new BufferedReader(new InputStreamReader(new FileInputStream(Input),"UTF-8"));
			double DeltaX=0,DeltaY=0;
			String buf;
			if(Input.getName().endsWith(".csv")){
				buf=in.readLine();
				if(buf==null) return;
				buf=buf.substring(buf.lastIndexOf('\uFEFF')+1);
				String[] AttributionList=buf.split(",");
				while((buf=in.readLine())!=null){
					if(buf.isEmpty()||buf.equals("-1")) continue;
					String[] ValueList=buf.split(",",-1);
					AllPointY[PointNum]=0;
					AllPointX[PointNum]=0;
					PointHint[PointNum]="";
					PointVisible[PointNum]=7;
					for(int i=0;i<AttributionList.length;i++){
						if(i>=ValueList.length) break;
						String signal=AttributionList[i].toLowerCase();
						if(signal.endsWith("latitude")){
							AllPointY[PointNum]=Double.parseDouble(ValueList[i]);
						}else if(signal.endsWith("longitude")){
							AllPointX[PointNum]=Double.parseDouble(ValueList[i]);
						}else if(signal.equals("hint")){
							PointHint[PointNum]=ValueList[i];
						}else if(signal.equals("visible")){
							PointVisible[PointNum]=Integer.parseInt(ValueList[i]);
						}else{
							if(i<ValueList.length){
								PointHint[PointNum]+="["+AttributionList[i]+":"+ValueList[i]+"]";
							}
						}
					}
					PointNum++;
				}
			}else
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
				out.write("Latitude,Longitude,Hint,Visible");
				out.newLine();
				for (int i = 0; i < PointNum; i++) {
					out.write(AllPointY[i] + "," + AllPointX[i] + ","
							+ PointHint[i].trim() + "," + PointVisible[i]);
					out.newLine();
				}
			} else {
				for (int i = 0; i < PointNum; i++) {
					 out.write("[PointStart]------------------------------");
					 out.newLine(); out.write(PointHint[i].trim());
					 out.newLine();
					 out.write(Integer.toString(PointVisible[i]));
					 out.newLine();
					 out.write(AllPointX[i]+"/"+AllPointY[i]); out.newLine();
					 out.write("[PointEnd]------------------------------");
					 out.newLine();
				}
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
		for(int i=0;i<PointNum;i++){
			AllPointX[i]+=longitude_delta;
			AllPointY[i]+=latitude_delta;
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
		for(int i=count;i<PointNum;i++){
			PointHint[i]=null;
			PointVisible[i]=0;
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
		if(st==-1) return MapKernel.MapWizard.LanguageDic.GetWords("无名称点");
		if(en==-1) return MapKernel.MapWizard.LanguageDic.GetWords("无名称点");
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
