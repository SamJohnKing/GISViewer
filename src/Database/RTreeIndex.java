package Database;
import java.util.ArrayList;
import javax.swing.JOptionPane;
public class RTreeIndex implements IndexInterface{
	//-----------------------------------------------
	//Share the basic Definition 
	final static int MaxTreeNodeNum=100000;
	final static int MAXCH=4;
	class RTreeNode{
		double x1,y1,x2,y2;
		RTreeNode[] next;
		int num,id;
		RTreeNode(){
			x1=1e100;
			y1=1e100;
			x2=-1e100;
			y2=-1e100;
			num=-2;
			
			id=-1;
			next=new RTreeNode[MAXCH];
		}
	}
	RTreeNode Root;
	RTreeNode FreeNode=new RTreeNode();
	ArrayList<Integer> Result=new ArrayList<Integer>();
	double[] RepresentX=new double[MaxTreeNodeNum];
	double[] RepresentY=new double[MaxTreeNodeNum];
	int[] AllPointNext=new int[MaxTreeNodeNum];
	int volume,ValidCount;
	DatabaseInterface DB;
	//The QuadTree---------------------------------------------------
	class QuadTreeNode{
		double x1,y1,x2,y2;
		QuadTreeNode[] next=new QuadTreeNode[4];
		int count,head;
		public QuadTreeNode(){
			x1=1e100;
			x2=-1e100;
			y1=1e100;
			y2=-1e100;
			next[0]=null;
			next[1]=null;
			next[2]=null;
			next[3]=null;
			count=0;
			head=-1;
		}
	}
	static final int MaxElementNumber=16;
	QuadTreeNode QuadRoot;
	//The QuadTreeCode-----------------------------------------------
	void QuadSort(){
		QuadRoot=new QuadTreeNode();
		int UpperLimit=Math.min(MaxTreeNodeNum,DB.GetElementNum());
		for(int i=0;i<UpperLimit;i++){
			RepresentX[i]=(DB.GetMBRX1(i)+DB.GetMBRX2(i))/2;
			RepresentY[i]=(DB.GetMBRY1(i)+DB.GetMBRY2(i))/2;
			QuadRoot.x1=Math.min(QuadRoot.x1,RepresentX[i]);
			QuadRoot.x2=Math.max(QuadRoot.x2,RepresentX[i]);
			QuadRoot.y1=Math.min(QuadRoot.y1,RepresentY[i]);
			QuadRoot.y2=Math.max(QuadRoot.y2,RepresentY[i]);
		}
		System.gc();
		for(int i=0;i<UpperLimit;i++)
			Quadadd(QuadRoot,RepresentX[i],RepresentY[i],i);
	}
	void Quadadd(QuadTreeNode me,double x,double y,int id){
		int ptr,index,nextptr;
		me.count++;
		if(me.head>=0){//Leave Point
			AllPointNext[id]=me.head;
			me.head=id;
			if(me.count>MaxElementNumber){
				me.next[0]=new QuadTreeNode();
				me.next[1]=new QuadTreeNode();
				me.next[2]=new QuadTreeNode();
				me.next[3]=new QuadTreeNode();
				//------------------------------------
				me.next[0].x1=me.x1;
				me.next[0].x2=(me.x1+me.x2)/2;
				me.next[0].y1=me.y1;
				me.next[0].y2=(me.y1+me.y2)/2;
				//------------------------------------
				me.next[1].x1=me.x1;
				me.next[1].x2=(me.x1+me.x2)/2;
				me.next[1].y1=(me.y1+me.y2)/2;
				me.next[1].y2=me.y2;
				//-------------------------------------
				me.next[2].x1=(me.x1+me.x2)/2;
				me.next[2].x2=me.x2;
				me.next[2].y1=me.y1;
				me.next[2].y2=(me.y1+me.y2)/2;
				//-------------------------------------
				me.next[3].x1=(me.x1+me.x2)/2;
				me.next[3].x2=me.x2;
				me.next[3].y1=(me.y1+me.y2)/2;
				me.next[3].y2=me.y2;
				//-------------------------------------
				ptr=me.head;
				while(ptr!=-1){
					nextptr=AllPointNext[ptr];
					if(RepresentX[ptr]<(me.x1+me.x2)/2) index=0;else index=2;
					if(RepresentY[ptr]>(me.y1+me.y2)/2) index++;
					Quadadd(me.next[index],RepresentX[ptr],RepresentY[ptr],ptr);
					ptr=nextptr;
				}
				me.head=-1;
			}
		}else if(me.count>1){//NoLeave Point
			if(x<(me.x1+me.x2)/2) index=0; else index=2;
			if(y>(me.y1+me.y2)/2) index++;
			Quadadd(me.next[index],x,y,id);
		}else{//NULL Point
			AllPointNext[id]=-1;
			me.head=id;
		}
	}
	void DFSAdd(QuadTreeNode me){
		if(me.head>=0){
			int ptr=me.head;
			while(ptr!=-1){
				add(ptr);
				ptr=AllPointNext[ptr];
			}
		}else if(me.count>0){
			DFSAdd(me.next[0]);
			DFSAdd(me.next[1]);
			DFSAdd(me.next[2]);
			DFSAdd(me.next[3]);
		}else return;
	}
	//The RTreeCode:-------------------------------------------------
	void re(RTreeNode tmp)
	{
	    RTreeNode t1=tmp.next[0];
		double xx1=t1.x1,xx2=t1.x2,yy1=t1.y1,yy2=t1.y2;
	    for(int i=1;i<tmp.num;i++)
		{
			t1=tmp.next[i];
			if(t1.x1<xx1) xx1=t1.x1;
			if(t1.x2>xx2) xx2=t1.x2;
			if(t1.y1<yy1) yy1=t1.y1;
			if(t1.y2>yy2) yy2=t1.y2;
		}
		tmp.x1=xx1;
		tmp.y1=yy1;
		tmp.x2=xx2;
		tmp.y2=yy2;
	}
	RTreeNode NodeCopy()
	{
		RTreeNode temp=new RTreeNode();
		int i;
		temp.id=FreeNode.id;
		temp.num=FreeNode.num;
		temp.x1=FreeNode.x1;
		temp.x2=FreeNode.x2;
		temp.y1=FreeNode.y1;
		temp.y2=FreeNode.y2;
	    for(i=0;i<temp.num;i++)
		temp.next[i]=FreeNode.next[i];
		return temp;
	}
	double change_x1,change_y1,change_x2,change_y2;
	double change_rec(RTreeNode p1,RTreeNode p2)
	{
		if(p1.x1<p2.x1) change_x1=p1.x1; else change_x1=p2.x1;
		if(p1.x2>p2.x2) change_x2=p1.x2; else change_x2=p2.x2;
		if(p1.y1<p2.y1) change_y1=p1.y1; else change_y1=p2.y1;
		if(p1.y2>p2.y2) change_y2=p1.y2; else change_y2=p2.y2;
		return ((change_x2-change_x1)*(change_y2-change_y1)-(p1.x2-p1.x1)*(p1.y2-p1.y1));
	}
	boolean insertRT(RTreeNode father,RTreeNode me)
	{
		if(me.num==0)
		{
			return false;
		}else
		{
			double temp=1e100,tmp;
			int best=-1;
			for(int i=0;i<me.num;i++)
			{
				tmp=change_rec(me.next[i],FreeNode);
				if(tmp<temp){temp=tmp;best=i;}
			}
			if(!insertRT(me,me.next[best]))
			{
				if(me.num<MAXCH)
				{
					me.num++;
					me.next[(me.num)-1]=NodeCopy();
					re(me);
					return true;
				}else
				{
					RTreeNode t=new RTreeNode();
					t.num=0;
	                for(int i=MAXCH/2;i<MAXCH;i++)
					{
						t.next[t.num]=me.next[i];
						t.num++;
					}
					me.num=MAXCH/2;
					re(me);
					t.next[t.num]=NodeCopy();
					t.num++;
					re(t);
					FreeNode.id=t.id;
					FreeNode.num=t.num;
					FreeNode.x1=t.x1;
					FreeNode.y1=t.y1;
					FreeNode.x2=t.x2;
					FreeNode.y2=t.y2;
					for(int i=0;i<t.num;i++) FreeNode.next[i]=t.next[i];
					t=null;
					return false;
				}
			}else
			{
				re(me);
				return true;
			}
		}
	}
//Above is the basic operation of RTree
	@Override
	public void IndexInit(DatabaseInterface DB) {
		// TODO Auto-generated method stub
		volume=0;
		ValidCount=0;
		Root=new RTreeNode();
		this.DB=DB;
		QuadSort();
		DFSAdd(QuadRoot);
		System.gc();
	}
	void rec_search(boolean Remove,RTreeNode me,double x1,double y1,double x2,double y2)
	{
		if(me==null) return;
		if(me.num==0)
		{
			if(me.num>=0) Result.add(me.id);
			if(Remove && (me.num>=0)){
			}
			return;
		}else
		{
			double xx1,xx2,yy1,yy2;
			for(int i=0;i<me.num;i++)
			{
				RTreeNode t1=me.next[i];
				if(x2<t1.x2) xx2=x2; else xx2=t1.x2;
				if(x1>t1.x1) xx1=x1; else xx1=t1.x1;
				if(xx1>xx2) continue;
				if(y2<t1.y2) yy2=y2; else yy2=t1.y2;
				if(y1>t1.y1) yy1=y1; else yy1=t1.y1;
				if(yy1>yy2) continue;
				rec_search(Remove,me.next[i],xx1,yy1,xx2,yy2);
			}
			return;
		}
	}
	@Override
	public ArrayList<Integer> Search(double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		if(DB.GetElementNum()==0){
			Result.clear();
			return Result;
		}
		double xx1=Math.min(x1,x2);
		double xx2=Math.max(x1,x2);
		double yy1=Math.min(y1,y2);
		double yy2=Math.max(y1,y2);
		Rebuild();
		Result.clear();
		rec_search(false,Root.next[0],xx1,yy1,xx2,yy2);//表头不要当成树根
		return Result;
	}
	@Override
	public void Delete(double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		double xx1=Math.min(x1,x2);
		double xx2=Math.max(x1,x2);
		double yy1=Math.min(y1,y2);
		double yy2=Math.max(y1,y2);
		Rebuild();
		Result.clear();
		rec_search(true,Root.next[0],xx1,yy1,xx2,yy2);
		for(int i:Result){
			DB.DatabaseRemove(i);
			ValidCount--;
		}
	}
	@Override
	public void add(int index) {
		// TODO Auto-generated method stub
		volume++;
		ValidCount++;
		if(Root.num==-2)//表头没用过
		{
			RTreeNode t1=new RTreeNode();
			Root.num=-1;//开始用表头
			Root.next[0]=t1;
			t1.num=0;
			t1.id=index;
			t1.x1=DB.GetMBRX1(index);
			t1.x2=DB.GetMBRX2(index);
			t1.y1=DB.GetMBRY1(index);
			t1.y2=DB.GetMBRY2(index);
		}else
		{
			FreeNode.id=index;
			FreeNode.num=0;
			FreeNode.x1=DB.GetMBRX1(index);
			FreeNode.x2=DB.GetMBRX2(index);
			FreeNode.y1=DB.GetMBRY1(index);
			FreeNode.y2=DB.GetMBRY2(index);
			if(!insertRT(Root,Root.next[0]))
			{
				RTreeNode root=new RTreeNode();
				root.num=2;
				root.next[0]=Root.next[0];
				root.next[1]=NodeCopy();
				re(root);
				Root.next[0]=root;
			}
		}
	}
	@Override
	public void WriteBack() {
		// TODO Auto-generated method stub
		DB.DatabaseResize();
		Root=null;
		QuadRoot=null;
		RepresentX=null;
		RepresentY=null;
		AllPointNext=null;
		System.gc();
	}
	void Rebuild(){
		if(((double)(ValidCount)/(double)(volume))>0.5) return;
		WriteBack();
		IndexInit(DB);
	}
}
