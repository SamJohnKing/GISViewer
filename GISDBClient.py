#!/usr/bin/python 
# -*- coding: utf-8 -*-
from socket import *
from time import *

def Command(HOST = '127.0.0.1', PORT = 1234, BUFSIZ = 1024, CommandStr = 'Ping::'):
	ADDR = (HOST,PORT)
	tcpCliSock = socket(AF_INET,SOCK_STREAM)
	tcpCliSock.connect(ADDR)
	data1 = CommandStr
	tcpCliSock.send(data1.encode("utf-8"))
	tcpCliSock.shutdown(SHUT_WR)
	print(data1)
	totaldata = bytes()
	while True:
		data1 = tcpCliSock.recv(BUFSIZ)
		if not data1: break
		totaldata += data1;
	
	result = totaldata.decode('utf-8');
	print(result)
	tcpCliSock.close()
	return result

#Command('10.177.31.32', 1234, 1024, 'DeletePoint::121.50129137581943#31.191666309227813#121.5213075916386#31.172032632157247')
#Command('10.177.31.32', 1234, 1024, 'DeletePoint::0#0#180#90')
#Command('10.177.31.32', 1234, 1024 * 128, 'QueryPoint::121.50129137581943#31.191666309227813#121.5213075916386#31.172032632157247')

changex = 0.003
changey = 0.001
counter = -1
while True:
	counter+=1
	counter%= 50
	x1 = 121.50129137581943 + changex * counter;
	y1 = 31.172032632157247 + changey * counter;
	x2 = 121.5213075916386 + changex * counter;
	y2 = 31.191666309227813 + changey * counter;
	# Command('127.0.0.1', 1234, 1024, 'DeletePolygonAll::')
	# Command('127.0.0.1', 1234, 1024, 'DeleteLine::0#0#180#90#[Info:try]')
	# Command('127.0.0.1', 1234, 1024, 'DeletePoint::0#0#180#90#[Info:try]############')
	# StyleStr = "[Info:try][ArrowLine:1][DashLine:1][PointRGB:0xff8080][LineRGB:0xd9fd28][PolygonRGB:0x123456][PointAlpha:0.6965][LineAlpha:0.88200004][PolygonAlpha:0.57][WordVisible:][PointVisible:][LineVisible:][PolygonVisible:][PointSize:10][LineWidth:5][Title:查询区域" + str(x1) + "/" + str(y1) + "]"
	# Command('127.0.0.1', 1234, 1024 * 128, "InsertStylePolygon::" + StyleStr + "#" + str(x1) + "#" + str(y1) + "#" + str(x2) + "#" + str(y1) + "#" + str(x2) + "#" + str(y2) + "#" + str(x1) + "#" + str(y2) + "#")
	# Command('127.0.0.1', 1234, 1024 * 128, "InsertStyleLine::" + StyleStr + "#" + str((x1 + x2) / 2) + "#" + str((y1 + y2) / 2) + "#" + str(x2) + "#" + str(y2) + "#")
	# Command('127.0.0.1', 1234, 1024 * 128, "InsertStylePoint::" + StyleStr + "#" + str(x1 + (x2 - x1) / 3) + "#" + str(y1 + (y2 - y1) * 3 / 4) + "#")
	# Command('127.0.0.1', 1234, 1024 * 128, "ShowTextArea2::xxxDear Tom, Welcome to GISDB This is a cross-platform spatial Database\nThe Java code based on LWJGL 2.8.5 and eclipse.swt\n We recommand use Winx64 JRE!\n欢迎来到时空数据库$$$$#####&&^^%%````······、、、真不戳！真不戳！!++-=")
	# Command('127.0.0.1', 1234, 1024 * 128, "ShowTextArea1::xxx欢迎\t\t\t希望bug free！!")
	# Command('127.0.0.1', 1234, 1024, 'SetSwingXScaleYScale::' + str((x2 - x1) * (counter % 5 + 2)) + "#" + str((y2 - y1) * (counter % 5 + 2)) + "#")
	# Command('127.0.0.1', 1234, 1024, 'SetOpenGLXScaleYScale::' + str((x2 - x1) * (counter % 5 + 2)) + "#" + str((y2 - y1) * (counter % 5 + 2)) + "#")
	# Command('127.0.0.1', 1234, 1024, 'MoveMiddle::' + str(x1) + "#" + str(y1) + "#")
	
	# Command('127.0.0.1', 1234, 1024, 'ScreenFlush::')
	# Command('127.0.0.1', 1234, 1024, 'Sleep::300')


	#命令序列最多发送18mb的流数据
	CMDSeq = 'CommandSequence>>' + 'DeletePolygonAll::'
	CMDSeq += '\nCommandSequence>>' + 'DeleteLine::0#0#180#90#[Info:try]'
	CMDSeq += '\nCommandSequence>>' + 'DeletePoint::0#0#180#90#[Info:try]############'
	StyleStr = "[Info:try][ArrowLine:1][DashLine:1][PointRGB:0xff8080][LineRGB:0xd9fd28][PolygonRGB:0x123456][PointAlpha:0.6965][LineAlpha:0.88200004][PolygonAlpha:0.57][WordVisible:][PointVisible:][LineVisible:][PolygonVisible:][PointSize:10][LineWidth:5][Title:查询区域" + str(x1) + "/" + str(y1) + "]"
	CMDSeq += '\nCommandSequence>>' + "InsertStylePolygon::" + StyleStr + "#" + str(x1) + "#" + str(y1) + "#" + str(x2) + "#" + str(y1) + "#" + str(x2) + "#" + str(y2) + "#" + str(x1) + "#" + str(y2) + "#"
	CMDSeq += '\nCommandSequence>>' + "InsertStyleLine::" + StyleStr + "#" + str((x1 + x2) / 2) + "#" + str((y1 + y2) / 2) + "#" + str(x2) + "#" + str(y2) + "#"
	CMDSeq += '\nCommandSequence>>' + "InsertStylePoint::" + StyleStr + "#" + str(x1 + (x2 - x1) / 3) + "#" + str(y1 + (y2 - y1) * 3 / 4) + "#"
	CMDSeq += '\nCommandSequence>>' + "ShowTextArea2::xxxDear Tom, Welcome to GISDB This is a cross-platform spatial Database\nThe Java code based on LWJGL 2.8.5 and eclipse.swt\n We recommand use Winx64 JRE!\n欢迎来到时空数据库$$$$#####&&^^%%````······、、、真不戳！真不戳！!++-="
	CMDSeq += '\nCommandSequence>>' + "ShowTextArea1::xxx欢迎\t\t\t希望bug free！!"
	CMDSeq += '\nCommandSequence>>' + 'SetSwingXScaleYScale::' + str((x2 - x1) * (counter % 5 + 2)) + "#" + str((y2 - y1) * (counter % 5 + 2)) + "#"
	CMDSeq += '\nCommandSequence>>' + 'SetOpenGLXScaleYScale::' + str((x2 - x1) * (counter % 5 + 2)) + "#" + str((y2 - y1) * (counter % 5 + 2)) + "#"
	CMDSeq += '\nCommandSequence>>' + 'MoveMiddle::' + str(x1) + "#" + str(y1) + "#"
	CMDSeq += '\nCommandSequence>>' + 'ScreenFlush::'
	CMDSeq += '\nCommandSequence>>' + 'Sleep::300'
	Command('127.0.0.1', 1234, 1024, CMDSeq)


# Command('127.0.0.1', 1234, 1024, 'MoveMiddle::121.47326#31.2139#')
# Command('127.0.0.1', 1234, 1024, 'ScreenFlush::')
# Command('127.0.0.1', 1234, 1024, 'BrowserPNGCapture::')
