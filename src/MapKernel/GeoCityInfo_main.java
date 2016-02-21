package MapKernel;

public class GeoCityInfo_main {
	public static String Folder_Prefix=null;
	public static void main(String args[]){
		new MapWizard(true);
		//LWJGLPackage.OriginalOpenGLWizard.GetInstance();
	}
	public static String Append_Folder_Prefix(String path){
		if(Folder_Prefix==null) return path;
		return Folder_Prefix+"/"+path;
	}
}
