package InternetHandle;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
public class ServerHandle implements Runnable{
	private MapKernel.MapControl MainHandle;
	private ExtendedToolPane.ExtendedToolPaneInterface Processor;
	ServerSocket server;
	Socket ClientRequest;
	int SocketNum;
	boolean ServerOpen=false;
	public void setHandle(MapKernel.MapControl Handle){
		this.MainHandle=Handle;
	}
	public void setProcessor(ExtendedToolPane.ExtendedToolPaneInterface Processor){
		this.Processor=Processor;
	}
	public void StartSocket(int SocketNum){
		if(ServerOpen){
			JOptionPane.showMessageDialog(null,"IsRunning");
			return;
		}
		System.out.println("ServerStarting......");
		this.SocketNum=SocketNum;
		Thread t=new Thread(this);
		t.start();
	}
	public void EndSocket(){
		if(server==null) return;
		try{
			server.close();
			server=null;
			ServerOpen=false;
			System.gc();
			System.out.println("ServerEnding......");
		}catch(Exception ex){
			MainHandle.SolveException(ex);
		}
	}
	byte[] buffer = new byte[10000000];  //缓冲区的大小
	byte[] ResponseBuffer=null;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerOpen=true;
		DataInputStream is=null;
		PrintStream os=null;
		//BufferedReader is=null;
		//PrintWriter os=null;
		String ClientCommand;
		String ResultInfo;
		int FailCount=0;
		int ValidByteCounter=0;
		while(ServerOpen){
		try{
			if(server!=null){
				server.close();
				server=null;
			}
			System.gc();
			server=new ServerSocket(SocketNum);
			while(ServerOpen){
				//SingleClient--------------------------------------
				try{
				ClientRequest=server.accept();
				System.out.println("-------------------------------Transaction Received--------------------------------");
				//接受数据，但不允许有中文，因为会乱码
				is = new DataInputStream(ClientRequest.getInputStream());
				ValidByteCounter=is.read(buffer);               //处理接收到的报文，转换成字符串
				os = new PrintStream(ClientRequest.getOutputStream());
				/**
				 * C++传递过来的中文字，需要转化一下。C++默认使用GBK。
				 * GB2312是GBK的子集，只有简体中文。因为数据库用GB2312，所以这里直接转为GB2312
				 * */
				//is=new BufferedReader(new InputStreamReader(ClientRequest.getInputStream()));
				//os=new PrintWriter(new OutputStreamWriter(ClientRequest.getOutputStream()));
				//ClientCommand=is.readLine();
				ClientCommand=new String(buffer,0,ValidByteCounter,"UTF-8").trim();
				System.out.println("ClientCommand:\t"+ClientCommand);
				ResultInfo=Processor.GetSocketResult(ClientCommand);
				System.out.println("\tResultInfo:\t"+ResultInfo);
				ResponseBuffer=ResultInfo.getBytes("UTF-8");
				os.write(ResponseBuffer,0,ResponseBuffer.length);
				//os.println(ResultInfo);
				os.flush();
				//------------------------------------------------------
				}catch(Exception ex){
					ex.printStackTrace();
					if(server.isClosed()) break;
				}finally{
					if(is!=null) is.close();
					if(os!=null) os.close();
					if(ClientRequest!=null) ClientRequest.close();
				}
				//--------------------------------------------------
			}
		}catch(Exception ex){
			FailCount++;
			if(FailCount>4) ServerOpen=false;
			if(ServerOpen) MainHandle.SolveException(ex);
		}
		}
		JOptionPane.showMessageDialog(null,"Server["+SocketNum+"]Closed!");
	}
}
