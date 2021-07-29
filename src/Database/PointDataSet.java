package Database;

import LWJGLPackage.OriginalOpenGLWizard;
import MapKernel.MapWizard;

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
import java.util.HashMap;
import java.util.Vector;

public class PointDataSet implements PointDatabaseInterface{
	public double[] AllPointX;
	public double[] AllPointY;
	public int PointNum;
	public String[] PointHint;
	public int[] PointVisible;
	
	public PointDataSet(){
		DatabaseInit();
	}
	public PointDataSet(int PredefinePointMaxNum) {
		AllPointX=new double[PredefinePointMaxNum];
		AllPointY=new double[PredefinePointMaxNum];
		PointNum=0;
		PointVisible=new int[PredefinePointMaxNum];
		for(int i=0;i<PredefinePointMaxNum;i++){
			PointVisible[i]=0;
		}
		PointHint=new String[PredefinePointMaxNum];
		System.gc();
	}
	public void DatabaseFileInput(File Input){
		if(Input==null) return;
		BufferedReader in=null;
		while(!MapWizard.SingleItem.SingleItem.Set_DB_Read_Write_Lock(true, true));
		try{
			in=new BufferedReader(new InputStreamReader(new FileInputStream(Input),"UTF-8"));
			double DeltaX=0,DeltaY=0;
			String buf;
			if(Input.getName().endsWith(".csv")){
				buf=in.readLine();
				if(buf==null) return;
				buf=buf.substring(buf.lastIndexOf('\uFEFF')+1);
				String[] AttributionList=buf.split(",|\t");
				while((buf=in.readLine())!=null){
					if(buf.isEmpty()||buf.equals("-1")) continue;
					String[] ValueList=buf.split(",|\t",-1);
					AllPointY[PointNum]=0;
					AllPointX[PointNum]=0;
					PointHint[PointNum]="";
					PointVisible[PointNum]=7;
					for(int i=0;i<AttributionList.length;i++){
						if(i>=ValueList.length) break;
						String signal=AttributionList[i].toLowerCase().trim();
						ValueList[i]=ValueList[i].trim();
						if(signal.endsWith("latitude")){
							AllPointY[PointNum]=Double.parseDouble(ValueList[i]);
						}else if(signal.endsWith("longitude")){
							AllPointX[PointNum]=Double.parseDouble(ValueList[i]);
						}else if(signal.equals("hint")){
							PointHint[PointNum]+=ValueList[i];
						}else if(signal.equals("visible")){
							PointVisible[PointNum]=Integer.parseInt(ValueList[i]);
						}else{
							if(i<ValueList.length){
								PointHint[PointNum]+="["+AttributionList[i]+":"+ValueList[i]+"]";
							}
						}
					}
					if(PointHint[PointNum].indexOf("AlignedMap_") != -1) PointVisible[PointNum] = 0;
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
			while(!MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
		}
	}
	public void DatabaseFileOutput(File Output){ /** 现在都改成了线程安全的形式 */
		if(Output==null) return;
		FileOutputStream fostream=null;
		BufferedWriter out=null;
		while(!MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, true));
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
			while(!MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, false));
		}
	}
	public void MoveEntireData(double longitude_delta,double latitude_delta){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		for(int i=0;i<PointNum;i++){
			AllPointX[i]+=longitude_delta;
			AllPointY[i]+=latitude_delta;
		}
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
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
	public int DefaultVisible = 7;
	public void add(double PointX,double PointY,String Hint){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		AllPointX[PointNum]=PointX;
		AllPointY[PointNum]=PointY;
		PointHint[PointNum]=Hint;
		PointVisible[PointNum]=Hint.indexOf("AlignedMap_") == -1 ? DefaultVisible : 0;
		PointNum++;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void add(double PointX,double PointY,int Attribute,String Hint){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		AllPointX[PointNum]=PointX;
		AllPointY[PointNum]=PointY;
		PointHint[PointNum]=Hint;
		PointVisible[PointNum]=Hint.indexOf("AlignedMap_") == -1 ? Attribute : 0;
		PointNum++;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseDelete(int k){
		if(k>=PointNum) return;
		if(k<0) return;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		for(int i=k+1;i<PointNum;i++){
			AllPointX[i-1]=AllPointX[i];
			AllPointY[i-1]=AllPointY[i];
			PointHint[i-1]=PointHint[i];
			PointVisible[i-1]=PointVisible[i];
		}
		PointNum--;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void DatabaseRemove(int k){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		UnsafeDatabaseRemove(k);
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void UnsafeDatabaseRemove(int k){
		PointVisible[k]=-1;
	}
	public void DatabaseResize(){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		UnsafeDatabaseResize();
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void UnsafeDatabaseResize(){
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
		while(!MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		System.out.println("初始化PointDB\nPointMaxNum = " + PointMaxNum);
		if(AllPointX == null) AllPointX=new double[PointMaxNum];
		if(AllPointY == null) AllPointY=new double[PointMaxNum];
		PointNum=0;
		if(PointVisible == null) PointVisible=new int[PointMaxNum];
		for(int i=0;i<PointMaxNum;i++){
			PointVisible[i]=0;
		}
		if(PointHint == null) PointHint=new String[PointMaxNum];
		while(!MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
		System.gc();
	}
	public void Clear(int index){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		PointHint[index]=null;
		PointVisible[index]=0;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void update(int index,int visible,String Hint){
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		PointVisible[index]=visible;
		PointHint[index]=Hint;
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
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
	public void DatabaseDelete(String KeyWord) { /**线程安全*/
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		// TODO Auto-generated method stub
		int ptr=PointNum-1;
		while(ptr!=-1){
			if(PointHint[ptr].indexOf(KeyWord)!=-1){
				UnsafeDatabaseRemove(ptr);
			}
			ptr--;
		}
		UnsafeDatabaseResize();
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	/** 老版本删除函数，现在建议用KeyValueDelete */
	public void AttributeDelete(String Info0,String Info1,String Info2,String Info3,String Info4){ /**线程安全*/
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		if(Info0==null) Info0="";else Info0= (Info0.indexOf(":") == -1) ? ("[Info:"+Info0+"]") : Info0;
		if(Info1==null) Info1="";else Info1= (Info1.indexOf(":") == -1) ? ("[Info:"+Info1+"]") : Info1;
		if(Info2==null) Info2="";else Info2= (Info2.indexOf(":") == -1) ? ("[Info:"+Info2+"]") : Info2;
		if(Info3==null) Info3="";else Info3= (Info3.indexOf(":") == -1) ? ("[Info:"+Info3+"]") : Info3;
		if(Info4==null) Info4="";else Info4= (Info4.indexOf(":") == -1) ? ("[Info:"+Info4+"]") : Info4;
		for(int i=PointNum-1;i>=0;i--){
			if(PointHint[i].indexOf(Info0)==-1) continue;
			if(PointHint[i].indexOf(Info1)==-1) continue;
			if(PointHint[i].indexOf(Info2)==-1) continue;
			if(PointHint[i].indexOf(Info3)==-1) continue;
			if(PointHint[i].indexOf(Info4)==-1) continue;
			UnsafeDatabaseRemove(i);
		}
		UnsafeDatabaseResize();
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	private void UnsafeHintUpdate(int k, String UpdateWhat) {
		HashMap<String, String> KeyValueHash = new HashMap<String, String>();
		String[] OriginalKeyValue = PointHint[k].split("\\]\\[");
		int OriginalLen = OriginalKeyValue.length;
		OriginalKeyValue[0] = OriginalKeyValue[0].substring(1);
		OriginalKeyValue[OriginalLen - 1] = OriginalKeyValue[OriginalLen - 1].substring(0, OriginalKeyValue[OriginalLen - 1].length() - 1);
		String Key = null;
		String Value =  null;
		int pos = -1;
		for(String str : OriginalKeyValue) {
			pos = str.indexOf(":");
			if(pos == -1) continue;
			Key = str.substring(0 , pos);
			Value = str.substring(pos + 1);
			KeyValueHash.put(Key, Value);
		}

		String[] UpdateKeyValue = UpdateWhat.split("\\]\\[");
		int UpdateLen = UpdateKeyValue.length;
		UpdateKeyValue[0] = UpdateKeyValue[0].substring(1);
		UpdateKeyValue[UpdateLen - 1] = UpdateKeyValue[UpdateLen - 1].substring(0, UpdateKeyValue[UpdateLen - 1].length() - 1);
		for(String str : UpdateKeyValue) {
			pos = str.indexOf(":");
			if(pos == -1) continue;
			Key = str.substring(0 , pos);
			Value = str.substring(pos + 1);
			KeyValueHash.put(Key, Value);
		}

		StringBuilder NewKeyValue = new StringBuilder();
		for(String str : KeyValueHash.keySet()) {
			NewKeyValue.append("[" + str + ":" + KeyValueHash.get(str) + "]");
		}
		PointHint[k] = NewKeyValue.toString();
	}
	public void KeyValueUpdate(double x1, double y1, double x2, double y2, String UpdateWhat){ /**线程安全*/
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		for(int i= PointNum-1;i>=0;i--){
			if(CheckInRegion(i, x1, y1, x2, y2)) UnsafeHintUpdate(i, UpdateWhat);
		}
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void PrimaryKeyValueUpdate(double x1, double y1, double x2, double y2, String PrimaryKey, String UpdateWhat){ /**线程安全*/
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		for(int i= PointNum-1;i>=0;i--){
			if(CheckInRegion(i, x1, y1, x2, y2) && PointHint[i].contains(PrimaryKey)) UnsafeHintUpdate(i, UpdateWhat);
		}
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public void KeyValueDelete(double x1, double y1, double x2, double y2, String Info0,String Info1,String Info2,String Info3,String Info4){ /**线程安全*/
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, true));
		if(Info0==null) Info0="";
		if(Info1==null) Info1="";
		if(Info2==null) Info2="";
		if(Info3==null) Info3="";
		if(Info4==null) Info4="";
		for(int i=PointNum-1;i>=0;i--){
			if(PointHint[i].indexOf(Info0)==-1) continue;
			if(PointHint[i].indexOf(Info1)==-1) continue;
			if(PointHint[i].indexOf(Info2)==-1) continue;
			if(PointHint[i].indexOf(Info3)==-1) continue;
			if(PointHint[i].indexOf(Info4)==-1) continue;
			if(CheckInRegion(i, x1, y1, x2, y2)) UnsafeDatabaseRemove(i);
		}
		UnsafeDatabaseResize();
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(true, false));
	}
	public Vector<HashMap<String, Object>> KeyValueQuery(double x1, double y1, double x2, double y2, String Info0,String Info1,String Info2,String Info3,String Info4){ /**线程安全*/
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, true));
		if(Info0==null) Info0="";
		if(Info1==null) Info1="";
		if(Info2==null) Info2="";
		if(Info3==null) Info3="";
		if(Info4==null) Info4="";
		Vector<HashMap<String, Object>> QueryResult = new Vector<HashMap<String, Object>>();
		for (int i =  PointNum -1; i>=0; i--){
			if(PointHint[i].indexOf(Info0)==-1) continue;
			if(PointHint[i].indexOf(Info1)==-1) continue;
			if(PointHint[i].indexOf(Info2)==-1) continue;
			if(PointHint[i].indexOf(Info3)==-1) continue;
			if(PointHint[i].indexOf(Info4)==-1) continue;
			if(CheckInRegion(i, x1, y1, x2, y2)){
				String IDHashCode = Integer.toString(System.identityHashCode(PointHint[i]));
				String Hint = PointHint[i];
				Double X = AllPointX[i];
				Double Y = AllPointY[i];
				HashMap<String, Object> Result = new HashMap<String, Object>();
				Result.put("IDHashCode", IDHashCode);
				Result.put("Hint", Hint);
				Result.put("X", X);
				Result.put("Y", Y);
				QueryResult.add(Result);
			}
		}
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, false));
		System.out.println(">>>>>>" + QueryResult.size() + " items");
		return QueryResult;
	}
	public boolean CheckInRegion(int ID, double RegionX1, double RegionY1, double RegionX2, double RegionY2){ /** 线程不安全 */
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
