package InternetHandle;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
public class ClientHandle{
	MapKernel.MapControl MainHandle;
	Socket ClientSocket;
	byte[] buffer = new byte[1000000];
	byte[] Totalbuffer = new byte[2000000];
	DataInputStream is;
	PrintStream  os;
	public void setHandle(MapKernel.MapControl Handle){
		this.MainHandle=Handle;
	}
	public String SendMsg(String IP,int SocketNum,String str) throws Exception{
		try{/*发送query后关闭client*/
			ClientSocket=new Socket(IP,SocketNum);
			//is=new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
			is = new DataInputStream(ClientSocket.getInputStream());
			//os=new PrintWriter(new OutputStreamWriter(ClientSocket.getOutputStream()));
			os = new PrintStream(ClientSocket.getOutputStream());
			byte[] strbytes = str.getBytes("UTF-8");
			os.write(strbytes, 0, strbytes.length);
			ClientSocket.shutdownOutput();  //关闭写以方便服务器读取完整指令
			//os.println(str);
			os.flush();
			//String Response=is.readLine();
			int TotalbufferCounter = 0;
			while(true){
				int BufferCounter = is.read(buffer);
				if(BufferCounter == -1) break;
				for(int ptr = TotalbufferCounter; ptr < TotalbufferCounter + BufferCounter; ptr++){
					Totalbuffer[ptr] =buffer[ptr - TotalbufferCounter];
				}
				TotalbufferCounter += BufferCounter;
			}
			if(is!=null) is.close();
			if(os!=null) os.close();
			if(!ClientSocket.isClosed()) ClientSocket.close();
			ClientSocket=null;
			String Response=new String(Totalbuffer,0,TotalbufferCounter,"UTF-8").trim();
			System.out.println(Response);
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
