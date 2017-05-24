package com.modi.socketclient.udputils;

import java.io.*;  
import java.net.*;  
  
public class Broadcast  
{  
 //定义播段地址,接收端口,发送端口  
 public static final String BroadcastGroup = "228.6.7.8";  
 public static final int BroadcastRecvPort = 4567;  
 public static final int BroadcastSendPort = 4566;  
   
 //接收字符串  
 private String Message;  
 //接收包和发送包  
 private DatagramPacket DataSendPacket;  
 private DatagramPacket DataRecvPacket;  
 //private DatagramSocket SendSocket;  
 //接收缓冲区和发送缓冲区  
 private byte[] SendBuf = new byte[1024];   
 private byte[] RecvBuf = new byte[1024];  
 //发送Socket  
 private DatagramSocket SendSocket;  
 //多播类  
 MulticastSocket BroadcastClass;  
 //构造函数  
 public Broadcast()  
 {  
  try  
  {  
      //发送Socket初始化  
      SendSocket = new DatagramSocket();  
      //多播段初始化  
      BroadcastClass = new MulticastSocket(BroadcastRecvPort);  
      //确定多报播地址  
      BroadcastClass.joinGroup(InetAddress.getByName(BroadcastGroup));  
  }  
  catch(IOException e)  
  {  
   e.printStackTrace();  
  }  
 }  
   
 //发送数据  
 public void SendData(String InMsg)  
 {  
  try  
  {  
      //发送数据包初始化  
      SendBuf = InMsg.getBytes();  
      int length = InMsg.getBytes().length;  
      DataSendPacket = new DatagramPacket(SendBuf,length,InetAddress.getByName(BroadcastGroup),BroadcastRecvPort);  
        //数据发送  
         SendSocket.send(DataSendPacket);  
       }  
       catch(IOException ioe)  
       {  
        ioe.printStackTrace();  
       }  
 }  
   
 //接收数据并返回  
 public String GetData()  
 {  
  try  
  {  
      //接收数据并送与接收缓冲区  
      DataRecvPacket = new DatagramPacket(RecvBuf,1024);   
      BroadcastClass.receive(DataRecvPacket);  
      //取得数据并返回数据  
      Message = new String(DataRecvPacket.getData(),0,DataRecvPacket.getLength());  
      Message = Message + " from " + DataRecvPacket.getAddress().getHostName();  
      Message = Message + " 端口 " + DataRecvPacket.getPort();  
     }  
     catch (IOException ioe)  
     {  
      ioe.printStackTrace();  
     }  
     return Message;  
 }  
}  