package com.modi.server;

import java.net.*;  
import java.io.*;  
import javax.swing.*;  
  
public class Receiver extends Thread  
{  
 //接收的信息  
 private String Message;  
 private JTextArea displayArea;  
 Broadcast Recv = new Broadcast();  
 //构造函数,  
   public Receiver(JTextArea RecvArea)  
   {  
    displayArea = RecvArea;   
   }  
     
   public void run()  
   {  
    while (true)  
    {  
     //监听接收数据包  
     Message = Recv.GetData();  
     //System.out.println(Message);  
     //displayArea.setText("11111");  
     displayArea.append(Message + "\r\n");  
    }   
   }  
     
   //发送数据  
   public void SendMessage(String InMsg)  
   {  
    Recv.SendData(InMsg);  
   }  
} 
