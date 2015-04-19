package InternetHandle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.JOptionPane;
public class ClientHandle{
	MapKernel.MapControl MainHandle;
	Socket ClientSocket;
	BufferedReader is;
	PrintWriter os;
	public void setHandle(MapKernel.MapControl Handle){
		this.MainHandle=Handle;
	}
	public String SendMsg(String IP,int SocketNum,String str) throws Exception{
		try{
			ClientSocket=new Socket(IP,SocketNum);
			is=new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
			os=new PrintWriter(new OutputStreamWriter(ClientSocket.getOutputStream()));
			os.println(str);
			os.flush();
			String Response=is.readLine();
			if(is!=null) is.close();
			if(os!=null) os.close();
			if(!ClientSocket.isClosed()) ClientSocket.close();
			ClientSocket=null;
			return Response;
		}catch(Exception ex){
			return "Fail::";
		}finally{
			if(is!=null) is.close();
			if(os!=null) os.close();
			if((ClientSocket!=null)&&(!ClientSocket.isClosed())) ClientSocket.close();
		}
	}
}
