#!/usr/bin/python 
# -*- coding: utf-8 -*-
from socket import *

HOST = '127.0.0.1' # or 'localhost'
PORT = 1234
BUFSIZ =1024
ADDR = (HOST,PORT)

while True:
	tcpCliSock = socket(AF_INET,SOCK_STREAM)
	tcpCliSock.connect(ADDR)
	data1 = "msg"
	tcpCliSock.send(data1.encode())
	print(data1)
	data1 = tcpCliSock.recv(BUFSIZ)
	print(data1.decode('utf-8'))

tcpCliSock.close()