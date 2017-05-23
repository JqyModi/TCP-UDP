package com.modi.server;

import javax.swing.*;  
import java.awt.*;  
import java.awt.event.*;  
  
public class ChatClient extends JFrame implements ActionListener  
{  
 //接收线程  
 Receiver recv;  
 //发送文本框  
 JTextField Sendtxt;  
   public ChatClient()  
 {  
       //客户端界面简单初始化  
       Sendtxt = new JTextField();  
       Sendtxt.addActionListener(this);  
       JTextArea DisplayArea = new JTextArea();   
       JScrollPane js =new JScrollPane();  
        JViewport port = js.getViewport();  
     port.add(DisplayArea);  
       // System.out.println(js.getLayout());  
       //js.add(DisplayArea);   
       this.getContentPane().add(BorderLayout.CENTER,js);  
       this.getContentPane().add(BorderLayout.SOUTH,Sendtxt);  
       this.setSize(300,300);  
       this.show();  
       //Broadcast brd = new Broadcast();  
       //brd.SendData("客户端初始化");  
       //创建一个新的接收线程并运行  
       recv = new Receiver(DisplayArea);  
       recv.start();   
 }  
   
 //事件响应代码  
 public void actionPerformed(ActionEvent e)  
 {  
  //文本框有内容才发送数据  
  if (Sendtxt.getText().length() != 0)  
  {  
   recv.SendMessage(Sendtxt.getText());  
  }  
 }  
   
 public static void main(String args[])  
 {  
  new ChatClient();  
 }  
}  
