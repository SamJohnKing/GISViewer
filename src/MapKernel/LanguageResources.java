package MapKernel;
import java.util.HashMap;
public class LanguageResources {
	HashMap<String, String> ChineseToEnglish=new HashMap<String, String>();
	HashMap<String, String> EnglishToChinese=new HashMap<String, String>();
	public LanguageResources(){
		EnglishToChinese.put("MapElementsEditorPane","地图元素编辑器");
		EnglishToChinese.put("ShowAutoCrossLinkPane","路段自动连接面板");
		EnglishToChinese.put("ShowConnectTestPane","路网连通性测试面板");
		EnglishToChinese.put("ShowTaxiTrajectoryViewPane","出租车轨迹绘制面板");
		EnglishToChinese.put("Road Network Application","道路网络应用");
		EnglishToChinese.put("ShowRoadConditionWizard","显示路况信息表格");
		EnglishToChinese.put("[Preference Wizard]","全局首选项");
		EnglishToChinese.put("ScreenLocationMicroDelta","屏幕元素位移微调");
		EnglishToChinese.put("ScreenLocationReset","屏幕元素位移还原");
		EnglishToChinese.put("ShowGISCompletionPane","地理信息系统补全面板");
		EnglishToChinese.put("ServerSocketPane","服务器套接字面板");
		EnglishToChinese.put("ClientSocketPane","客户端套接字面板");
		EnglishToChinese.put("Clear the PointDB","清空兴趣点数据库");
		EnglishToChinese.put("Clear the LineDB","清空折线数据库");
		EnglishToChinese.put("Clear the PolygonDB","清空多边形数据库");
		EnglishToChinese.put("All Element Invisible","隐藏/显示所有地图元素");
		EnglishToChinese.put("All Point Invisible","隐藏/显示兴趣点元素");
		EnglishToChinese.put("All Line Invisible","隐藏/显示折线元素");
		EnglishToChinese.put("All Polygon Invisible","隐藏/显示多边形元素");
		EnglishToChinese.put("Engrave Point Shape","改变兴趣点外形");
		EnglishToChinese.put("AlignPointsTagItem","对齐兴趣点标签");
		EnglishToChinese.put("AlignLinesTagItem","对齐折线标签");
		EnglishToChinese.put("AlignPolygonsTagItem","对齐多边形标签");
		EnglishToChinese.put("AutoCrossLinkPane","路段自动连接面板");
		EnglishToChinese.put("Cross-Cross Distance Upper Limit/m","路口-路口距离上限/米");
		EnglishToChinese.put("Cross-Skeleton Distance Upper Limit/m","路口-路段距离上限/米");
		EnglishToChinese.put("Cross-Skeleton Angle Upper Limit/360","路段-路段夹角上线/度");
		EnglishToChinese.put("Confirm to Operate","确认执行");
		EnglishToChinese.put("Sequence","次序");
		EnglishToChinese.put("Identifier","标识");
		EnglishToChinese.put("Record","记录");
		EnglishToChinese.put("LBS AutoDriver Panel","LBS自动驾驶服务面板");
		EnglishToChinese.put("IPADDRESS:","服务器IP");
		EnglishToChinese.put("PORT:","端口");
		EnglishToChinese.put("Start Server","开启服务器");
		EnglishToChinese.put("Stop Server","终止服务器");
		EnglishToChinese.put("Start Client","开启客户端");
		EnglishToChinese.put("Stop Client","中止客户端");
		EnglishToChinese.put("Click to Select","启用");
		EnglishToChinese.put("Origin","起点");
		EnglishToChinese.put("Terminal","终点");
		EnglishToChinese.put("Auto Km/h","码速");
		EnglishToChinese.put("Hotspot Recommend","热点推荐");
		EnglishToChinese.put("Client Shadowing","跟踪车辆");
		EnglishToChinese.put("Cross Hint Switch","路口提示");
		EnglishToChinese.put("Start Auto Drive","开启自动驾驶");
		EnglishToChinese.put("Stop Auto Drive","停止自动驾驶");
		EnglishToChinese.put("IDENTIFIERFILTER1","车辆标识符过滤字段1");
		EnglishToChinese.put("IDENTIFIERFILTER2","车辆标识符过滤字段2");
		EnglishToChinese.put("ToLandmark","距离地标");
		EnglishToChinese.put("in","有");
		EnglishToChinese.put("Meters","米距离");
		EnglishToChinese.put("Shadowing Switch","车辆跟踪开关");
		EnglishToChinese.put("Record Switch","记录车辆轨迹开关");
		EnglishToChinese.put("Clear Record","清空被记录轨迹车辆");
		EnglishToChinese.put("Global Record Switch","全局记录车辆轨迹开关");
		EnglishToChinese.put("Please Click the Permission","请在对应的打勾位置打勾，以给予权限");
		EnglishToChinese.put("The Velocity Value is not a Positive Real Number","您所设定的速度不是一个正实数");
		EnglishToChinese.put("Format Error","格式错误");
		EnglishToChinese.put("The Velocity Value is not a Proper Positive Real Number","您所设定的速度不是一个合适的正实数");
		EnglishToChinese.put("Impossible","不可能");
		EnglishToChinese.put("Origin is not set","开始位置未设定");
		EnglishToChinese.put("Origin Invalid","开始位置非法");
		EnglishToChinese.put("Terminal is not set","终点位置未设定");
		EnglishToChinese.put("Terminal Invalid","终点位置非法");
		EnglishToChinese.put("Input Your Identifier","输入您的车辆ID");
		EnglishToChinese.put("Invalid Identifier","非法的车辆ID");
		EnglishToChinese.put("Give Up Shadowing","放弃跟踪车辆");
		EnglishToChinese.put("Please Choose One Row","请您选择一行");
		EnglishToChinese.put("The Terminal Target Arrived","车辆已经到达预设的目的地");
		EnglishToChinese.put("Congratulations!!!","祝贺您！");
		EnglishToChinese.put("The Condition of Road NetWork is bad,Wait Here","当前网络条件较差，请耐心等待");
		EnglishToChinese.put("Wait Here","请继续等待");
		EnglishToChinese.put("ClientSocketPane","客户端套接字面板");
		EnglishToChinese.put("ClientSocketIP","客户端套接字IP");
		EnglishToChinese.put("ClientSocketPort","客户端套接字端口号");
		EnglishToChinese.put("Command Line Through Socket","通过套接字传输的命令行");
		EnglishToChinese.put("Exe","执行");
		EnglishToChinese.put("ConveyPoint","选中了一个点");
		EnglishToChinese.put("ConveyRegion","选中了一个区域");
		EnglishToChinese.put("ConfirmFunction","确认功能");
		EnglishToChinese.put("ConnectTestPane","连通性测试面板");
		EnglishToChinese.put("Choose Origin","选择起点位置");
		EnglishToChinese.put("Choose Terminal","选择终点位置");
		EnglishToChinese.put("Use SPFA to Search","使用SPFA算法搜寻最短路");
		EnglishToChinese.put("Search For the path","目前显示的道路");
		EnglishToChinese.put("Click to Back Path","点击查看上一条路线");
		EnglishToChinese.put("Click to Forward Path","点击查看下一条路线");
		EnglishToChinese.put("Warning","警告");
		EnglishToChinese.put("Please complete the Output File Name!!!","请把输出文件名补充完整");
		EnglishToChinese.put("OutputFinished!","输出终了");
		EnglishToChinese.put("WARNING::LineDB OverFlowed!!!","警告:折线数据库容量溢出");
		EnglishToChinese.put("InputFinished!","输入终了");
		EnglishToChinese.put("MapMatchingFinished!","地图匹配终了");
		EnglishToChinese.put("Input Joint Point Number","输入接口点的数目");
		EnglishToChinese.put("Retrive","取回");
		EnglishToChinese.put("ClearLastResults","清理上一次的结果");
		EnglishToChinese.put("ClearAllResults","清理所有结果");
		EnglishToChinese.put("Release Road ID & Trajectories","导出道路ID与对应的轨迹段");
		EnglishToChinese.put("Convey Region to Algorithm","将一个区域传输到算法模块");
		EnglishToChinese.put("Trajectory to Points Tranformer","将轨迹分解成某一区域中的散点");
		EnglishToChinese.put("Release","导出");
		EnglishToChinese.put("Execute1","执行");
		EnglishToChinese.put("Execute2","执行");
		EnglishToChinese.put("Execute3","执行");
		EnglishToChinese.put("Trajectory To Lines Transformer","将轨迹分解成某一区域中的轨迹段");
		EnglishToChinese.put("Release","导出");
		EnglishToChinese.put("Execute1","执行");
		EnglishToChinese.put("Execute2","执行");
		EnglishToChinese.put("Execute3","执行");
		EnglishToChinese.put("Please complete the Delete Road Index File Name!!!","请补全被删区域对应的文件名");
		EnglishToChinese.put("RecordDeleteRoadFinished!","被删道路ID输出终了");
		EnglishToChinese.put("Do you want to Convey Region to Algorithm?","您想把某个区域传输到算法模块吗");
		EnglishToChinese.put("Hint","提示");
		EnglishToChinese.put("Yes To Choose Points;No To Choose Polygons","选是选中点，否选择多边形");
		EnglishToChinese.put("HtmlMapOutput","地理元素导出到网页");
		EnglishToChinese.put("Please Set The Directory","请设定目录");
		EnglishToChinese.put("ServerSocketPane","服务器套接字面板");
		EnglishToChinese.put("ServerSocketPort","服务器套接字端口");
		EnglishToChinese.put("Order Server to Process Transaction","允许服务器处理网络请求");
		EnglishToChinese.put("ServerStart","服务器开启");
		EnglishToChinese.put("ServerStop","服务器中止");
		EnglishToChinese.put("ConveyRectangle","选中了矩形区域");
		EnglishToChinese.put("TaxiTrajectoryView","出租车轨迹查看");
		EnglishToChinese.put("Interval Time Per impulse to Query","脉冲式询问周期");
		EnglishToChinese.put("Upper Number Limit of Taxi to Trace","绘制轨迹的车辆上限");
		EnglishToChinese.put("Click to Trace the Taxi","点击描绘出租车轨迹");
		EnglishToChinese.put("Click to Stop the Tracing Process","点击停止描绘出租车轨迹");
		EnglishToChinese.put("Notice the Format","请注意格式");
		EnglishToChinese.put("Directory","文件夹");
		EnglishToChinese.put("FileName","文件名");
		//=================================================================
		ChineseToEnglish.put("出租车自由追踪", "Focus on taxi");
		ChineseToEnglish.put("显示出租车标识","Show The Taxi ID");
		ChineseToEnglish.put("隐藏出租车标识","Veil the Taxi ID");
		ChineseToEnglish.put("出租车标识","The Taxi ID");
		ChineseToEnglish.put("开始追踪","Follow it");
		ChineseToEnglish.put("放弃追踪","Not Follow");
		ChineseToEnglish.put("返回","Back");
		ChineseToEnglish.put("动态显示/隐藏周遭信息","Show Surrounding or not");
		ChineseToEnglish.put("输入的数据未命中","Input Data Not Hits");
		ChineseToEnglish.put("请检查后重新输入","Reinput After Check");
		ChineseToEnglish.put("快速定位工具窗","Position Fast Pane");
		ChineseToEnglish.put("定位","Position");
		ChineseToEnglish.put("写回缓存","Cache Writeback");
		ChineseToEnglish.put("起点终点选取","Set Origin/Terminal");
		ChineseToEnglish.put("您现在正处于路径规划阶段，选为起点按是,选为终点按否，放弃按取消","Yes for Origin, No for Terminal and Cancel for Giving up");
		ChineseToEnglish.put("成功设置为起点","Origin Gotten");
		ChineseToEnglish.put("起点设置成功","Origin Gotten");
		ChineseToEnglish.put("成功设置为终点","Terminal Gotten");
		ChineseToEnglish.put("终点设置成功","Terminal Gotten");
		ChineseToEnglish.put("您正在应用出租车搜寻功能，选为搜寻源点则按是,否则放弃","Searching for Taxi, Yes for starting, No for cancel");
		ChineseToEnglish.put("出租车搜寻源点设置","Source Point Setting");
		ChineseToEnglish.put("成功设置为源点","Source Point Gotten");
		ChineseToEnglish.put("源点设置成功","Source Point Gotten");
		ChineseToEnglish.put("序号","Sequence");
		ChineseToEnglish.put("经度","Longitude");
		ChineseToEnglish.put("纬度","Latitude");
		ChineseToEnglish.put("名称","Name");
		ChineseToEnglish.put("类型","Type");
		ChineseToEnglish.put("名称关键字","Name Keyword");
		ChineseToEnglish.put("类型关键字","Type Keyword");
		ChineseToEnglish.put("查询","Query");
		ChineseToEnglish.put("【在下面的输入框内输入地标关键字】","Input Landmark Keyword in below");
		ChineseToEnglish.put("删除选中行","Del Selection");
		ChineseToEnglish.put("删除选中","Del Selection");
		ChineseToEnglish.put("删除全部行","Delete All");
		ChineseToEnglish.put("删除全部","Delete All");
		ChineseToEnglish.put("写回选中行","WB Selection");
		ChineseToEnglish.put("写回选中","WB Selection");
		ChineseToEnglish.put("写回全部行","WriteBack All");
		ChineseToEnglish.put("写回全部","WriteBack All");
		ChineseToEnglish.put("详细","Detail");
		ChineseToEnglish.put("导出","Extract");
		ChineseToEnglish.put("全部导出","Extract All");
		ChineseToEnglish.put("已经没有匹配的结果了","No Matching Result");
		ChineseToEnglish.put("未命中","No Hitting");
		ChineseToEnglish.put("对不起没要您要的结果","No Hitting");
		ChineseToEnglish.put("您正在编辑单元格,为了数据安全请提前确认","The cell is been editing, Please commit it first");
		ChineseToEnglish.put("更改内容时不可编辑单元格","No Change When editing");
		ChineseToEnglish.put("请您选中一行","Please Choose One Line");
		ChineseToEnglish.put("数据库检查到异常格式","Finding Exceptional Format in DB");
		ChineseToEnglish.put("数据格式异常","Exceptional Data Format");
		ChineseToEnglish.put("选中行异常","Exception in selection line");
		ChineseToEnglish.put("GPS偏差校准工具栏","Deviation Elimination");
		ChineseToEnglish.put("锁住屏幕","Lock Screen");
		ChineseToEnglish.put("解锁屏幕","UnLoock SCR");
		ChineseToEnglish.put("鼠标指向经度","Mouse to Lon");
		ChineseToEnglish.put("鼠标指向纬度","Mouse to Lat");
		ChineseToEnglish.put("禁止插入新点","Point not Allowed");
		ChineseToEnglish.put("允许插入新点","Point Allowed");
		ChineseToEnglish.put("删除被选中的点","Del Selection Pt");
		ChineseToEnglish.put("删除被影响的点","Del Influenced Pt");
		ChineseToEnglish.put("显示偏差向量","Show Deviation");
		ChineseToEnglish.put("显示统计结果","Show Statistics");
		ChineseToEnglish.put("[温馨提示]拖拽选中","Hint:Drag to Select");
		ChineseToEnglish.put("需要锁屏和禁止加点","Lock, NewPt Denied");
		ChineseToEnglish.put("平均GPS经度差  ","Avg Delta Lon");
		ChineseToEnglish.put("平均GPS纬度差  ","Avg Delta Lat");
		ChineseToEnglish.put("球面投影北偏/米","Delta North /M");
		ChineseToEnglish.put("球面投影东偏/米","Delat East /M");
		ChineseToEnglish.put("地理信息工具栏","GeoInfo Pane");
		ChineseToEnglish.put("上一个","Last One");
		ChineseToEnglish.put("下一个","Next One");
		ChineseToEnglish.put("已经到尾元素","Has been in Tail");
		ChineseToEnglish.put("遭遇尾元素","Has been in Tail");
		ChineseToEnglish.put("已经到首元素","Has been in Head");
		ChineseToEnglish.put("遭遇首元素","Has been in Head");
		ChineseToEnglish.put("出租车定位工具栏","Position Taxi Pane");
		ChineseToEnglish.put("在地图上显示出租车","Show taxi on map");
		ChineseToEnglish.put("在地图上清除出租车","Veil taxi on map");
		ChineseToEnglish.put("利用数据库进行经纬度坐标选择","Use the Database to Search");
		ChineseToEnglish.put("所在点经度     ","Lon of Focus");
		ChineseToEnglish.put("所在点纬度     ","Lat of Focus");
		ChineseToEnglish.put("查询的半径/米","Query Radius/M");
		ChineseToEnglish.put("当前区域内的出租车数量    ","Taxi Volume of this Region");
		ChineseToEnglish.put("最近出租车/米","Neareast Taxi/M");
		ChineseToEnglish.put("跟踪此刻离你最近的一辆出租车","Follow the nearest taxi");
		ChineseToEnglish.put("放弃跟踪最近的一辆出租车","Now Follow the neareast taxi");
		ChineseToEnglish.put("打开/关闭出租车自由追踪面板","Open/Close Taxi Free Seaching");
		ChineseToEnglish.put("时","H");
		ChineseToEnglish.put("分","M");
		ChineseToEnglish.put("秒状况：","S State");
		ChineseToEnglish.put("距离","To");
		ChineseToEnglish.put("有 ","is");
		ChineseToEnglish.put(" 米"," M");
		ChineseToEnglish.put("未收到返回信号","No Feedback");
		ChineseToEnglish.put("两点定位工具栏","Position Tool Pane");
		ChineseToEnglish.put("第一个点的经度","The Former Lon");
		ChineseToEnglish.put("第一个点的纬度","The Former Lat");
		ChineseToEnglish.put("第二个点的经度","The Latter Lon");
		ChineseToEnglish.put("第二个点的纬度","The Latter Lat");
		ChineseToEnglish.put("当前修改第一个点","Editing the Former");
		ChineseToEnglish.put("当前修改第二个点","Editing the Latter");
		ChineseToEnglish.put("以第一点为终点","Former as Terminal");
		ChineseToEnglish.put("以第二点为终点","Latter as Terminal");
		ChineseToEnglish.put("终点-起点经度差","Delta Lon Origin");
		ChineseToEnglish.put("终点-起点纬度差","Delta Lat Origin");
		ChineseToEnglish.put("终点在起点北/米","North to Origin/M");
		ChineseToEnglish.put("终点在起点东/米","East to Origin/M");
		ChineseToEnglish.put("两点间的距离/米","Distance between");
		ChineseToEnglish.put("锁定/解锁起点终点","Lock/Unlock");
		ChineseToEnglish.put("计算位置参数","Calc Parameters");
		ChineseToEnglish.put("移动视角到选择的两点区域内","Move to the Selection Two Pts");
		ChineseToEnglish.put("您的经纬度输入不正确","Errors in input Lon/Lat");
		ChineseToEnglish.put("数据安全提示","Hint of Data Security");
		ChineseToEnglish.put("您没有锁定两点，请按锁定按钮！！！","No Locking, Please Lock!");
		ChineseToEnglish.put("路径规划工具栏","Path Plan Pane");
		ChineseToEnglish.put("路径起点经度","Origin Longitude");
		ChineseToEnglish.put("路径起点纬度","Origin Latitude");
		ChineseToEnglish.put("路径终点经度","Terminal Lon");
		ChineseToEnglish.put("路径终点纬度","Terminal Lon");
		ChineseToEnglish.put("当前设置路径起点","Editing on Origin");
		ChineseToEnglish.put("当前设置路径终点","Editing on Terminal");
		ChineseToEnglish.put("最短路","TSP");
		ChineseToEnglish.put("显示路径","Show Path");
		ChineseToEnglish.put("隐藏路径","Veil Path");
		ChineseToEnglish.put("当前总里程/米","Total Distance/M");
		ChineseToEnglish.put("起点终点格式设置有误","Errors in Origin/Terminal");
		ChineseToEnglish.put("GPS数据异常","Exception in GPS Data");
		ChineseToEnglish.put("起点终点已经在地图上标识，请确认","Origin/Terminal has been in Map");
		ChineseToEnglish.put("即将开始规划路径","Starting Path Planning");
		ChineseToEnglish.put("两点之间距离小于500米，建议直接步行","The distance is lower than 500m, please walking");
		ChineseToEnglish.put("建议步行","please walking");
		ChineseToEnglish.put("由于路网信息骨架不完整，查询未能命中","Data missing in Road Network, No hitting in query");
		ChineseToEnglish.put("查询未能命中","No hitting in query");
		ChineseToEnglish.put("请不要擅自改动","Please don't change");
		ChineseToEnglish.put("地标编辑工具栏","Landmark Editor");
		ChineseToEnglish.put("地标点经度位置","Lon of Landmark");
		ChineseToEnglish.put("地标点纬度位置","Lat of Landmark");
		ChineseToEnglish.put("地标点指代名称","Fullname");
		ChineseToEnglish.put("地标点建筑类型","Category");
		ChineseToEnglish.put("请输入地标点的相关简介","Please input the landmark's introduction");
		ChineseToEnglish.put("确认加入此地标","commit to add this landmark");
		ChineseToEnglish.put("删除选中的地标","delete the selection landmark");
		ChineseToEnglish.put("地标名称为空","NULL in Landmark's name");
		ChineseToEnglish.put("输入数据不完整","Input is not complete");
		ChineseToEnglish.put("地标类型为空","Type is NULL");
		ChineseToEnglish.put("地标GPS数据有误","Errors in GPS data");
		ChineseToEnglish.put("GPS数据异常","Exceptional GPS data");
		ChineseToEnglish.put("是否要将","Will you add ");
		ChineseToEnglish.put("加入地标序列"," into Landmark dataset");
		ChineseToEnglish.put("确认是否加入"," commit to add");
		ChineseToEnglish.put("删除成功"," delete finished");
		ChineseToEnglish.put("个点被选中","Pts has been selected");
		ChineseToEnglish.put("释放选中点","Release selection Pts");
		ChineseToEnglish.put("总计 ","Total");
		ChineseToEnglish.put("网格浓度100%对应 ","100% Alpha represents");
		ChineseToEnglish.put("暂无信息","No Information");
		ChineseToEnglish.put("根据选中的地标点顺序播放地标点的详细资料","Slide the Landmarks in Selection");
		ChineseToEnglish.put("演示播放","Slide Start");
		ChineseToEnglish.put("打开地图                       ","Open Map Set       ");
		ChineseToEnglish.put("退出                   ","Exit          ");
		ChineseToEnglish.put("保存                 ","Save          ");
		ChineseToEnglish.put("基本信息列表                  ","Basic Information Pane");
		ChineseToEnglish.put("更改地图背景","Change the Map Background");
		ChineseToEnglish.put("还原默认地图背景","Use the Default Background");
		ChineseToEnglish.put("两点矩形区域设定                  ","Rectangle Region Setting");
		ChineseToEnglish.put("清除地图上所有鼠标点","Clear All Points in map");
		ChineseToEnglish.put("清除上一个鼠标点     ","Clear last one point");
		ChineseToEnglish.put("清除地图上的箭头      ","Clear the arrows in map");
		ChineseToEnglish.put("地图GPS偏差校准     ","Deviation Elimination");
		ChineseToEnglish.put("显示并调整时钟                ","Show and change the Clock");
		ChineseToEnglish.put("显示出租车信息","Show the taxi infomation");
		ChineseToEnglish.put("路径规划                    ","Path Plan      ");
		ChineseToEnglish.put("清洗屏幕","Screen Flush");
		ChineseToEnglish.put("可视化开关","Visibility Switch");
		ChineseToEnglish.put("显示中心区域","Show The Center Region");
		ChineseToEnglish.put("隐去中心区域","Veil The Center Region");
		ChineseToEnglish.put("地标编辑","Landmark Editor");
		ChineseToEnglish.put("关于软件","About Software");
		ChineseToEnglish.put("在地图上显示地标点","Show Landmarks on map");
		ChineseToEnglish.put("在地图上隐去地标点","Veil Landmarks on map");
		ChineseToEnglish.put("在地图上显示地标名称","Show Landmarks' Name on map");
		ChineseToEnglish.put("在地图上隐去地标名称","Viel Landmarks' Name on map");
		ChineseToEnglish.put("地标检索服务","Landmark Query Service");
		ChineseToEnglish.put("开启时钟脉冲动态效果","Opne Clock impulse effect");
		ChineseToEnglish.put("关闭时钟脉冲动态效果","Close Clock impulse effect");
		ChineseToEnglish.put("强制内存清理","Force to clear Memory");
		ChineseToEnglish.put("区域新建面板","Polygon Editor Pane");
		ChineseToEnglish.put("线路新建面板","Line Editor Pane");
		ChineseToEnglish.put("兴趣点批量插入面板","POI Editor Pane");
		ChineseToEnglish.put("显示多边形区域数据库视窗","Show PolygonDB Wizard");
		ChineseToEnglish.put("显示线路数据库视窗","Show LineDB Wizard");
		ChineseToEnglish.put("利用数据库创建JPG文件","Use DB to create JPG File");
		ChineseToEnglish.put("显示兴趣点数据库视窗","Show PointDB Wizard");
		ChineseToEnglish.put("捕捉当前窗口到PNG文件","Capture Screen to PNG File");
		ChineseToEnglish.put("输入的信息有误，请重试","Error in Input datam try again");
		ChineseToEnglish.put("JPG生成失败","Failed to generate JPG");
		ChineseToEnglish.put("导出折线数据库","Extract LineDB");
		ChineseToEnglish.put("导出兴趣点数据库","Extract PointDB");
		ChineseToEnglish.put("导出多边形数据库","Extract PolygonDB");
		ChineseToEnglish.put("追加折线数据库","Append LineDB");
		ChineseToEnglish.put("追加兴趣点数据库","Append PointDB");
		ChineseToEnglish.put("追加多边形数据库","Append PolygonDB");
		ChineseToEnglish.put("载入追加折线数据库文件夹","Loading LineDB files");
		ChineseToEnglish.put("载入追加兴趣点数据库文件夹","Loading PointDB files");
		ChineseToEnglish.put("载入追加多边形数据库文件夹","Loading PolygonDB files");
		ChineseToEnglish.put("覆盖折线数据库","Replace LineDB");
		ChineseToEnglish.put("覆盖兴趣点数据库","Replace PointDB");
		ChineseToEnglish.put("覆盖多边形数据库","Replace PolygonDB");
		ChineseToEnglish.put("设置图形显示数量上限","Upper limit of visible objects");
		ChineseToEnglish.put("请输出上限，空则放弃","Please input upper limit, none for giving up");
		ChineseToEnglish.put("设置图形显示数量上限","Setting the Upper limit of visible objects");
		ChineseToEnglish.put("放弃了图形显示数量上限重设","Giving up the resetting of upper limit");
		ChineseToEnglish.put("输入有误","Errors in input");
		ChineseToEnglish.put("多边形网格化导出","Polygons to Grids");
		ChineseToEnglish.put("设置有误","Errors in configuration");
		ChineseToEnglish.put("显示/关闭点分布的浓度","Show/Veil the Alpha Distribution");
		ChineseToEnglish.put("手工设定浓度图","Mannual Setting the Alpha level & Radiation");
		ChineseToEnglish.put("导出数据库于网页地图上","Extract DB into Webpage");
		ChineseToEnglish.put("平移PointDB数据","Offset the PointDB");
		ChineseToEnglish.put("平移LineDB数据","Offset the LineDB");
		ChineseToEnglish.put("平移PolygonDB数据","Offset the PolygonDB");
		ChineseToEnglish.put("背景图片平移矢量载入","Load the Background Move Vectors");
		ChineseToEnglish.put("背景图片矢量位移还原","Unload the Background Move Vectors");
		ChineseToEnglish.put("文件      ","File    ");
		ChineseToEnglish.put("编辑      ","Edit    ");
		ChineseToEnglish.put("控制      ","Control    ");
		ChineseToEnglish.put("地图数据      ","Map Data    ");
		ChineseToEnglish.put("功能扩展      ","Utility Extension    ");
		ChineseToEnglish.put("帮助","Help");
		ChineseToEnglish.put("地图数据不存在","No Map Data Exists");
		ChineseToEnglish.put("地标演示和地标数据库检索窗口两者不能并存","Landmark Slide and Landmark Query Wizard cannot exist concurrently");
		ChineseToEnglish.put("数据库并发读写隐患","Risk in DB Parallelization");
		ChineseToEnglish.put("地标点数据不存在","No Landmark Point Exists");
		ChineseToEnglish.put("出租车数据不存在","No TaxiData Exists");
		ChineseToEnglish.put("路网数据不存在","No RoadNetwork Data Exists");
		ChineseToEnglish.put("地标数据不存在","No Landmark Data Exists");
		ChineseToEnglish.put("地标工具栏和地标数据库检索窗口两者不能并存","Landmark Editor and Landmark Query Wizard cannot exist concurrently");
		ChineseToEnglish.put("为了操作数据库，是否允许进入默认面板","In order to change data, Transform Pane into Default type?");
		ChineseToEnglish.put("进入默认面板","In Default Pane");
		ChineseToEnglish.put("输入的信息有误，请重试","Errors in Input Information, Please try again");
		ChineseToEnglish.put("JPG生成失败","Failed to Generate JPG");
		ChineseToEnglish.put("时钟设定","Setting in Clock");
		ChineseToEnglish.put("时","H");
		ChineseToEnglish.put("分","M");
		ChineseToEnglish.put("秒","S");
		ChineseToEnglish.put("开关","SW");
		ChineseToEnglish.put("出租车定位程序正在读取时间","Taxi Position Program is reading clock now");
		ChineseToEnglish.put("时间读写冲突","Conflict when reading and writing on clock concurrently");
		ChineseToEnglish.put("您设置的时间格式有误，请重新设置","The Time Format you set is error, please try again");
		ChineseToEnglish.put("时间异常","Exception in Time");
		ChineseToEnglish.put("XXX您设置的时间格式有误，请重新设置","The Time Format you set is error, please try again");
		ChineseToEnglish.put("请输出文件名前缀，不能为空","Please give the prefix of FileName, Null are not allowed");
		ChineseToEnglish.put("导出折线数据库到文件","Extract LineDB into File");
		ChineseToEnglish.put("放弃了导出","Give up the extracting");
		ChineseToEnglish.put("导出多边形数据库到文件","Extract PolygonDB into File");
		ChineseToEnglish.put("导出兴趣点数据库到文件","Extract PointDB into File");
		ChineseToEnglish.put("离开之前是否使所有更改生效，是则缓存写回数据库，否则放弃所有更改","Before exit, dou you want to save? Yes for saving, No for giving up");
		ChineseToEnglish.put("数据完整性提示","Data Security Hint");
		ChineseToEnglish.put("捕捉当前屏幕为PNG","Capture Screen to PNG");
		ChineseToEnglish.put("放弃了PNG生成","Give up the generation of PNG");
		ChineseToEnglish.put("PNG生成成功","PNG Generated");
		ChineseToEnglish.put("请以下列各式输入信息：宽度;高度;粗度","Please input info as follow: Width; Height; Weight");
		ChineseToEnglish.put("JPG生成在Image目录下","JPG is generated under Image Directory");
		ChineseToEnglish.put("放弃了JPG生成","Give up the generation of JPG");
		ChineseToEnglish.put("开始生成JPG","Start to generate JPG File");
		ChineseToEnglish.put("屏幕元素位置教调","Adjustment");
		ChineseToEnglish.put("无名称线路","Unnamed Line");
		ChineseToEnglish.put("无名称点","Unnamed Point");
		ChineseToEnglish.put("无名称区域","Unnamed Region");
		ChineseToEnglish.put("格式错误","Error Format");
		ChineseToEnglish.put("进入","Into");
		ChineseToEnglish.put("请先生成路网","Please Get Road Network First");
		ChineseToEnglish.put("此路径为空","This Road is Empty");
		ChineseToEnglish.put("边界提示","Boundary Hint");
		ChineseToEnglish.put("起点","Origin");
		ChineseToEnglish.put("终点","Terminal");
		ChineseToEnglish.put("电子地图补全面板","Map Complement");
		ChineseToEnglish.put("打开源文件","Open Source File");
		ChineseToEnglish.put("全部导出","Extract All");
		ChineseToEnglish.put("按照特定标签导出","by Specific Lable");
		ChineseToEnglish.put("全部可视元素导出","by Visibility");
		ChineseToEnglish.put("屏幕导出","by Screen");
		ChineseToEnglish.put("不导出","None");
		ChineseToEnglish.put("立即打开HTML","Open HTML File");
		ChineseToEnglish.put("导出HTML文件","Extract HTML File");
		ChineseToEnglish.put("线路新建工具栏","Line Editor Pane");
		ChineseToEnglish.put("少于两个点不予提交","Cannot submit without at least 2 points");
		ChineseToEnglish.put("拒绝提交","Submission Denied");
		ChineseToEnglish.put("输入地理线路标签","Input Label for Line");
		ChineseToEnglish.put("确认提交","Submission Commit");
		ChineseToEnglish.put("成功提交了","Submitted");
		ChineseToEnglish.put("放弃提交,仍然为您保留未提交数据","Submission denied, the data is still unchanged");
		ChineseToEnglish.put("勾选则开始创建线路，取消则放弃","Click to allow road generation");
		ChineseToEnglish.put("勾选则开始显示连线，取消则无连线","Click to allow Line Link");
		ChineseToEnglish.put("勾选则显示添加顺序，取消则不显示","Click to allow sequence visible");
		ChineseToEnglish.put("取消最近","Latest Cancel");
		ChineseToEnglish.put("取消全部","All Cancel");
		ChineseToEnglish.put("提交信息","Submit");
		ChineseToEnglish.put("线路生成中","Line Generating");
		ChineseToEnglish.put("放弃了线路生成","Giving up Line Generating");
		ChineseToEnglish.put("连接点","Link Point");
		ChineseToEnglish.put("不连接点","Unlink Point");
		ChineseToEnglish.put("显示轮廓点创建顺序","Show the sequence of boundary points");
		ChineseToEnglish.put("不显示轮廓点创建顺序","View the sequence of boundary points");
		ChineseToEnglish.put("没有开始构建线路，点击无效","Please click to allow line generation");
		ChineseToEnglish.put("是则删除，否则区域截图生成","Yes for delete, No for Screen Capture");
		ChineseToEnglish.put("确认删除","Delete commit");
		ChineseToEnglish.put("地图元素编辑面板","Map Editor Pane");
		ChineseToEnglish.put("选择添加地标点状地图元素","Click to add Point Element");
		ChineseToEnglish.put("选择添加连通线状地图元素","Click to add Line Element");
		ChineseToEnglish.put("选择添加多边形状地图元素","Click to all Polygon Element");
		ChineseToEnglish.put("勾选则开始编辑，取消则不能编辑","Click to allow edition");
		ChineseToEnglish.put("勾选则开始点点连接，取消则不连接","Click to allow Point Link");
		ChineseToEnglish.put("勾选则开始首尾相连，取消则不相连","Click to allow Head Tail Link");
		ChineseToEnglish.put("勾选则显示添加顺序，取消则不显示","Click to allow sequence visible");
		ChineseToEnglish.put("选中时删除地标点状数据","Click to delete Point Elements");
		ChineseToEnglish.put("选中时删除连接线状数据","Click to delete Line Elements");
		ChineseToEnglish.put("选中时删除多边形状数据","Click to delete Polygon Elements");
		ChineseToEnglish.put("取消添加序列中最近点","Cancel Latest");
		ChineseToEnglish.put("取消添加序列中全部点","Cancel All");
		ChineseToEnglish.put("提交添加序列信息到数据库","Element Submition Commit");
		ChineseToEnglish.put("从数据库撤销上一次的提交","Cancel lastest DB submission");
		ChineseToEnglish.put("元素生成中","Elements Generating");
		ChineseToEnglish.put("放弃了生成","Give up generating");
		ChineseToEnglish.put("显示连线","Show Link");
		ChineseToEnglish.put("隐去连线","Viel Link");
		ChineseToEnglish.put("首尾相连","Link Head Tail");
		ChineseToEnglish.put("首尾断开","UnLink Head Tail");
		ChineseToEnglish.put("显示顺序","Sequence Visible");
		ChineseToEnglish.put("隐去顺序","Sequence Unvisible");
		ChineseToEnglish.put("没有开始构建元素，点击无效","Please click to allow edition");
		ChineseToEnglish.put("选择【是】删除，选择【否】撒点,【取消】则放弃","Yes for delete, No for Dispersal, Cancel for giving up");
		ChineseToEnglish.put("删除或者撒点","Delete or Dispersal");
		ChineseToEnglish.put("输入随机点数量","Input number of random points");
		ChineseToEnglish.put("随机点数量","Number of random points");
		ChineseToEnglish.put("输入格式有误","Errors in Input Format");
		ChineseToEnglish.put("数量输入有误","Errors in Input Number");
		ChineseToEnglish.put("少于三个点不予提交","Cannot submit without at least 3 points");
		ChineseToEnglish.put("输入地理区域标签","Input Region Label");
		ChineseToEnglish.put("确认提交","Submit");
		ChineseToEnglish.put("成功提交了","Submitted");
		ChineseToEnglish.put("输入地理线路标签","Input Line Label");
		ChineseToEnglish.put("不允许空提交","Empty is not allowed");
		ChineseToEnglish.put("兴趣点批量插入面板","Points Editor Pane");
		ChineseToEnglish.put("输入地理线路标签","Input Line Lable");
		ChineseToEnglish.put("勾选则开始创建兴趣点，取消则放弃","Click to allow Point addition");
		ChineseToEnglish.put("勾选则显示添加顺序，取消则不显示","Click to allow sequence visible");
		ChineseToEnglish.put("提交信息","Submit Info");
		ChineseToEnglish.put("兴趣点批量插入中","Insert volume of points");
		ChineseToEnglish.put("放弃了兴趣点批量插入","Give up volume insert");
		ChineseToEnglish.put("显示创建顺序","Click to allow sequence visible");
		ChineseToEnglish.put("不显示创建顺序","Click to not allow sequence visible");
		ChineseToEnglish.put("没有开始插入兴趣点，点击无效","Please allow point insertion");
		ChineseToEnglish.put("区域新建工具栏","Polygon Editor");
		ChineseToEnglish.put("输入地理区域标签","Input Region Label");
		ChineseToEnglish.put("勾选则开始连接多边形区域，取消则放弃","Click to allow Point Insertion");
		ChineseToEnglish.put("勾选则开始显示多边形边界，取消则无边界","Click to allow Boundary visible");
		ChineseToEnglish.put("勾选则开始自动闭合多边形，取消则不闭合","Click to allow Head Tail Link");
		ChineseToEnglish.put("勾选则显示添加顺序，取消则不显示","Click to allow sequence visible");
		ChineseToEnglish.put("多边形生成中","Polygon Generating");
		ChineseToEnglish.put("放弃了多边形生成","Give up Polygon Generating");
		ChineseToEnglish.put("多边形显示边","Polygon Boundary Visible");
		ChineseToEnglish.put("多边形隐去边","Polygon Boundary Unvisible");
		ChineseToEnglish.put("多边形自动闭合","Polygon Head Tail Link");
		ChineseToEnglish.put("多边形不自动闭合","Polygon Head Tail Unlink");
		ChineseToEnglish.put("显示轮廓点创建顺序","Show Boundary Point Sequence");
		ChineseToEnglish.put("不显示轮廓点创建顺序","Veil Boundary Point Sequence");
		ChineseToEnglish.put("没有开始构建多边形，点击无效","Please Click to allow Polygon Generation");
		ChineseToEnglish.put("输入地理区域标签","Input Region Label");
		ChineseToEnglish.put("序号","Sequence");
		ChineseToEnglish.put("显示","Visible");
		ChineseToEnglish.put("备注","Hint");
		ChineseToEnglish.put("查询","Query");
		ChineseToEnglish.put("【地理折线线路排布数据库检索视窗】","Geometry Line Database Query Wizard");
		ChineseToEnglish.put("【地理折线线路排布配置视窗】","Geometry Line DB Preference");
		ChineseToEnglish.put("关键字A","KeywordA");
		ChineseToEnglish.put("关键字B","KeywordB");
		ChineseToEnglish.put("关键字","Keyword");
		ChineseToEnglish.put("特征值","Value");
		ChineseToEnglish.put("自动显示变化","Auto Change");
		ChineseToEnglish.put("是否应用于其他检索项","Applied to others");
		ChineseToEnglish.put("显示名","Name");
		ChineseToEnglish.put("允许在地图上显示","Visible on the Screen");
		ChineseToEnglish.put("在地图上显示字","Words' Visibility");
		ChineseToEnglish.put("在地图上显示点","Point's Visibility");
		ChineseToEnglish.put("在地图上显示线","Lines's Visibility");
		ChineseToEnglish.put("字色","FC");
		ChineseToEnglish.put("点色","PC");
		ChineseToEnglish.put("线色","LC");
		ChineseToEnglish.put("追加信息","Append Info");
		ChineseToEnglish.put("覆写信息而不追加","Replace Info");
		ChineseToEnglish.put("追加触发","Append Triger");
		ChineseToEnglish.put("覆写触发而不追加","Replace Triger");
		ChineseToEnglish.put("立即生效不返回","Applied Instantly");
		ChineseToEnglish.put("设置文字位置","Set Font's Loc");
		ChineseToEnglish.put("数据库检查到异常格式","Format Exception in DB");
		ChineseToEnglish.put("数据格式异常","Format Exception");
		ChineseToEnglish.put("【经纬度平面空间兴趣点数据库检索】","Geometry Point Database Query Wizard");
		ChineseToEnglish.put("【地理地标兴趣点配置视窗】","Geometry Points' Preference");
		ChineseToEnglish.put("在地图上显示兴趣点标签","Point Labels' Visibility");
		ChineseToEnglish.put("在地图上显示兴趣点GPS位置","GPS Points's Visibility Property");
		ChineseToEnglish.put("【二维多边形地理区域数据库检索视窗】","Geometry Polygon Database Wizard");
		ChineseToEnglish.put("【多边形地理区域配置视窗】","Polygon Datebase Preference");
		ChineseToEnglish.put("撤销更改并返回","Undo & Back");
		ChineseToEnglish.put("生效并返回","Apply & Back");
		ChineseToEnglish.put("删除所在行","Del Selection");
		ChineseToEnglish.put("写回所在行","WB Selection");
		ChineseToEnglish.put("配置","Detail");
		ChineseToEnglish.put("【标签重定位】","ReAlignment");
		ChineseToEnglish.put("返回","Back");
		ChineseToEnglish.put("重置","Reset");
		ChineseToEnglish.put("竖排","Verti");
		ChineseToEnglish.put("横排","Horiz");
		ChineseToEnglish.put("上一条","Last One");
		ChineseToEnglish.put("下一条","Next One");
		ChineseToEnglish.put("改换目录前是否使所有更改生效，是则缓存写回数据库，否则放弃所有更改","Please Save the Data before Change DIR");
		//=========================================================================
		ChineseToEnglish.put("Map.jpg 数据不存在","Map.jpg DOES NOT EXIST");
		EnglishToChinese.put("Map.jpg DOES NOT EXIST","Map.jpg 数据不存在");
		ChineseToEnglish.put("JPG生成成功","JPG Extraction Finished");
		EnglishToChinese.put("JPG Extraction Finished","JPG生成成功");
		ChineseToEnglish.put("元素可视化纹理开关","VisualFeatureSwitch");
		EnglishToChinese.put("VisualFeatureSwitch","元素可视化纹理开关");
		ChineseToEnglish.put("DeepZoom高清分割","DeepZoom HD Split");
		EnglishToChinese.put("DeepZoom HD Split","DeepZoom高清分割");
		ChineseToEnglish.put("打开网页第二屏幕", "Open Webpage Secondary Screen");
		EnglishToChinese.put("Open Webpage Secondary Screen", "打开网页第二屏幕");
		ChineseToEnglish.put("设定      ", "Setting    ");
		ChineseToEnglish.put("色彩Alpha值透射开关", "AlphaFeatureSwitch");
	}
	public String GetWords(String str){
		if(str==null) return "NULL";
		if (MapWizard.Language.equals("EN")) {
			if (ChineseToEnglish.containsKey(str))
				return ChineseToEnglish.get(str);
			else if(EnglishToChinese.containsKey(str))
				return str;
			else
				return str;
		}else if(MapWizard.Language.equals("CH")){
			if(EnglishToChinese.containsKey(str))
				return EnglishToChinese.get(str);
			else if(ChineseToEnglish.containsKey(str))
				return str;
			else return str;
		}else return str;
	}
}
