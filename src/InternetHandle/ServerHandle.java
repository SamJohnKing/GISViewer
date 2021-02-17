package InternetHandle;
import ExtendedToolPane.ServerSocketPaneClass;
import MapKernel.MapWizard;

import java.io.*;
import java.net.*;
import java.util.Calendar;

import javax.swing.JOptionPane;
public class ServerHandle implements Runnable{
	private MapKernel.MapControl MainHandle;
	private ExtendedToolPane.ExtendedToolPaneInterface Processor;
	ServerSocket server;
	Socket ClientRequest;
	int SocketNum;
	public boolean ServerOpen=false;
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
	byte[] buffer = new byte[2000000];  //缓冲区的大小
	byte[] Totalbuffer = new byte[20000000];
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

		while(ServerOpen){
		try{
			if(server!=null){
				server.close();
				server=null;
			}
			System.gc();
			//server=new ServerSocket(SocketNum,100,InetAddress.getByName("10.177.31.32"));
			server=new ServerSocket(SocketNum);
			System.out.println(server);
			while(ServerOpen){/* 接受client数据后关闭连接 */
				//SingleClient--------------------------------------
				try{
					ClientRequest=server.accept();
					ServerSocketPaneClass ServerPane = (ServerSocketPaneClass) Processor;
					boolean ShowCMDSQL = ServerPane.ShowSQLInTerminal.isSelected();
					if(ShowCMDSQL) System.out.println("-------------------------------Transaction Received--------------------------------");
					//接受数据，但不允许有中文，因为会乱码
					is = new DataInputStream(ClientRequest.getInputStream());
					int TotalbufferCounter=0;
					while(true){
						int BufferCounter=is.read(buffer);               //处理接收到的报文，转换成字符串,如果太长可能读不完
						if(BufferCounter == -1) break;
						for(int ptr = TotalbufferCounter; ptr < TotalbufferCounter + BufferCounter; ptr++){
							Totalbuffer[ptr] =buffer[ptr - TotalbufferCounter];
						}
						TotalbufferCounter += BufferCounter;
					}
					if(ShowCMDSQL) System.out.println("Recv at " + Calendar.getInstance().getTimeInMillis() + " ms of " + MapWizard.SingleItem.Handle.GetInternationalTimeSignature());
					os = new PrintStream(ClientRequest.getOutputStream());
					/**编码默认用UTF-8
					 * C++传递过来的中文字，需要转化一下。C++默认使用GBK。
					 * GB2312是GBK的子集，只有简体中文。因为数据库用GB2312，所以这里直接转为GB2312
					 * */
					//is=new BufferedReader(new InputStreamReader(ClientRequest.getInputStream()));
					//os=new PrintWriter(new OutputStreamWriter(ClientRequest.getOutputStream()));
					//ClientCommand=is.readLine();
					ClientCommand=new String(Totalbuffer,0,TotalbufferCounter,"UTF-8").trim();
					if(ShowCMDSQL) System.out.println("ClientCommand:\t"+ClientCommand.substring(0, (ClientCommand.length() > 588 ? 588 : ClientCommand.length())));
					ResultInfo=Processor.GetSocketResult(ClientCommand);
					if(ShowCMDSQL) System.out.println("\tResultInfo:\t"+ResultInfo.substring(0, (ResultInfo.length() > 588 ? 588 : ResultInfo.length())));
					ResponseBuffer=ResultInfo.getBytes("UTF-8");
					if(ShowCMDSQL) System.out.println("Send at " + Calendar.getInstance().getTimeInMillis() + " ms of " + MapWizard.SingleItem.Handle.GetInternationalTimeSignature());
					os.write(ResponseBuffer, 0,ResponseBuffer.length);
					//os.println(ResultInfo);
					os.flush();
					ClientRequest.shutdownOutput(); //关闭输出流使得客户端得到完整结果
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
