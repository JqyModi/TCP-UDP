package com.modi.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpClient {
//	private static final String HOSTNAME1 = "localhost";
	private static final String HOSTNAME = "192.168.1.173";
	public static final String BroadcastGroup = "228.6.7.8";  
	private static final int PORT = 8888;
	public static void main(String[] args) {
		createClient();
	}
	private static void createClient() {
		byte[] buf = new byte[1024];
		// TODO Auto-generated method stub
		DatagramPacket datagramPacket = null;
		DatagramSocket datagramSocket = null;
		Scanner scanner = null;
		String msg = null;
		InetAddress address = null;
		try {
			address = InetAddress.getByName(HOSTNAME);
//			datagramPacket = new DatagramPacket(buf , buf.length);
			//通过DatagramPacket来指定服务端的主机和端口号
			datagramPacket = new DatagramPacket(buf, buf.length, address, PORT);
			datagramSocket = new DatagramSocket();
			System.out.println("客户端启动成功");
			
			//接收服务端发送过来的消息
			DatagramPacket datagramPacket2 = new DatagramPacket(buf, buf.length);
			/*datagramSocket.receive(datagramPacket2);
//			msg = datagramPacket.getData().toString();
			msg = new String(buf,0,datagramPacket2.getLength());
			System.out.println("我是客户端,服务端发来消息："+msg);*/
			receiveMsg(datagramSocket, datagramPacket2);
			
			//向服务器发送消息
			/*scanner = new Scanner(System.in);
			msg = scanner.next();
			if(msg!=null){
				datagramPacket.setData(msg.getBytes());
				datagramSocket.send(datagramPacket);
				//
				System.out.println("客户端消息发送成功");
			}*/
			
			scanner = new Scanner(System.in);
			while(scanner.hasNext()){
				msg = scanner.next();
				if(msg!=null){
					datagramPacket.setData(msg.getBytes());
					datagramSocket.send(datagramPacket);
					//
					System.out.println("客户端消息发送成功");
				}
			}
			
			//通过线程池来循环监听服务端发送过来的消息
			//receiveMsg(datagramSocket,datagramPacket);
			
			//接收服务端发送过来的消息
			/*DatagramPacket datagramPacket2 = new DatagramPacket(buf, buf.length);
			datagramSocket.receive(datagramPacket2);
//			msg = datagramPacket.getData().toString();
			msg = new String(buf,0,datagramPacket2.getLength());
			System.out.println("我是客户端,服务端发来消息："+msg);
			
			receiveMsg(datagramSocket, datagramPacket2);*/
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void receiveMsg(DatagramSocket datagramSocket, DatagramPacket datagramPacket) {
		//创建一个只有一个线程的线程池
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				// 执行业务逻辑
//				System.out.println("线程池开始执行");
				String msg = null;
				while(true){
					//接收服务端发送过来的消息
					datagramSocket.receive(datagramPacket);
//					msg = datagramPacket.getData().toString();
					msg = new String(datagramPacket.getData(),0,datagramPacket.getLength());
					System.out.println("我是客户端,服务端发来消息："+msg);
				}
//				return msg;
			}
		});
	}

}
