package Database;
import java.util.*;
public class ConvexHull{
    point[] A;//已知的平面上的点集
    boolean[] F;//F[i]标记A[i]是否已在凸包中
    Queue< Integer> Q=new LinkedList< Integer>();//点集A的凸包,
    int n;
	class point implements Comparable {//平面上的一个点
	    double x;
	    double y;
	    public int compareTo(Object o) {//按y升序排列,y相同按x升序  
	       point b=(point)o; 
	      
	       if(this.y>b.y) return 1;
	       else if(this.y==b.y ){ 
	         if(this.x>b.x) return 1;
	         else if(this.x==b.x) return 0;
	         else return -1;
	       }
	       
	       else return -1;
	                 
	   }      
	}
  public ConvexHull(){}
   double cross(point c,point a,point b) {
	   return (c.x-a.x)*(a.y-b.y)-(c.y-a.y)*(a.x-b.x);
   }
   public  double distance(point p1, point p2) {    
       return (Math.sqrt((p1.x - p2.x) * (p1.x- p2.x) + (p1.y - p2.y)* (p1.y - p2.y)));    
   }    	
  public void print(){
     for(int i=0;i< A.length;i++){
           System.out.println("["+A[i].x+","+A[i].y+"]  ");
     }
  }
  public int Process(double[] PointX,double[] PointY,int tot){
	  
	  Scanner in=new Scanner(System.in);
      n=tot;
      A=new point[n+1];
      F=new boolean[n+1];
      for(int i=1;i<=n;i++){
          A[i]=new point();
          A[i].x=PointX[i-1];
          A[i].y=PointY[i-1];
      }
      Arrays.sort(A,1,A.length-1);//注意这个排序从1开始
      //确定一个肯定在凸包上的点
      F[1]=true;//注意这里将A[1]标记为放进凸包,并不真正放进
      int last=1;
      while(true){
        int Minn=-1;
        for(int i=1;i<=n;i++) if(!F[i]) {//找一个不在凸包上的点
            Minn=i;
            break;
        }
        if(Minn==-1) break;//找不到,结束
        for(int i=1;i<=n;i++) //遍历所有点, 每个点都和现有最外侧的点比较,得到新的最外侧的点
          if((cross(A[last],A[i],A[Minn])>0)|| (cross(A[last],A[i],A[Minn]) == 0) && 
           (distance(A[last], A[i]) > distance(A[last], A[Minn])))  
             Minn=i;
        if(F[Minn]) break;//找不到最外侧的点了
        Q.offer(Minn);//最外侧的点进凸包
        F[Minn]=true;//标记这点已放进凸包了
        last=Minn;
    }
    last=1;
    Q.offer(1);//最后将A[1]放进凸包
    tot=0;
    while(!Q.isEmpty())//计算圆周长+凸包周长
    {
        int tmp=Q.poll();
        PointX[tot]=A[tmp].x;
        PointY[tot]=A[tmp].y;
        tot++;
    }
    return tot;
  }
 }