package com.modi.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;

public class UdpServer {
//	private static final String HOSTNAME1 = "localhost";
	private static final String HOSTNAME = "192.168.1.173";
	public static final String BroadcastGroup = "228.6.7.8";  
	private static final int PORT = 8888;
	static List<DatagramPacket> clients = null;
	private static List<Integer> ports = null;
	private static InetAddress address = null;
    private static int port = 0;
    private static MulticastSocket mSocket = null;
	
    
	public static void main(String[] args) {
		clients = new ArrayList<>();
		ports = new ArrayList<>();
		createUdpServer();
	}

	private static void createUdpServer() {
		// TODO Auto-generated method stub
		DatagramPacket datagramPacket = null; 
		DatagramSocket datagramSocket = null;
		
		Scanner scanner = null;
		String msg = null;
		try {
			datagramSocket = new DatagramSocket(PORT);
//			mSocket = new MulticastSocket(PORT);
//			mSocket.joinGroup(InetAddress.getByName(BroadcastGroup));
			System.out.println("服务端启动成功");
			//开启子线程接收客户端数据
			receiveMsg(datagramSocket);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.err.println("port### === "+port);
			//循环监听并发送控制台消息
			scanner = new Scanner(System.in);
			while(scanner.hasNext()){
				msg = scanner.next();
				if (msg!=null) {
			        byte[] data2 = (msg+"欢迎您!").getBytes();
			        // 2.创建数据报，包含响应的数据信息
			        DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
			        datagramSocket.send(packet2);
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void receiveMsg(DatagramSocket datagramSocket) {
		//创建一个只有一个线程的线程池
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.submit(new Callable<String>() {

			@Override
			public String call() {
				String msg = null;
				while(true){
					try {
						//指定接收1024大小的数据
						byte[] buf = new byte[1024];
						DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
//					datagramPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(HOSTNAME), PORT);
						//接收服务端发送过来的消息
						// 此方法在接收到数据报之前会一直阻塞
						datagramSocket.receive(datagramPacket);
						address = datagramPacket.getAddress();
						port = datagramPacket.getPort();
						ports.add(datagramPacket.getPort());
						
						System.err.println("客户端port:"+datagramPacket.getPort());
						
						
						//删除重复项
						for (int i = 0; i < ports.size(); i++) {
							int port = ports.get(i);
							for (int j = i+1; j < ports.size(); j++) {
								int port1 = ports.get(j);
								if (port == port1) {
									ports.remove(j);
								}
							}
						}
						//得到一条条不同数据报文
						for (int i = 0; i < ports.size(); i++) {
							msg = new String(datagramPacket.getData(),0,datagramPacket.getLength());
							System.out.println("我是服务端,客户端发来消息："+msg);
							JsonObject jobj = new JsonObject();
							jobj.addProperty("hostName", HOSTNAME);
							jobj.addProperty("msg", msg);
							jobj.addProperty("count", ports.size());
							
							String jsonStr = jobj.toString();
							System.out.println("jsonStr"+jsonStr);
							System.out.println(jsonStr);
							datagramPacket.setData(jsonStr.getBytes());
							datagramPacket.setAddress(InetAddress.getByName(HOSTNAME));
							datagramPacket.setPort(ports.get(i));
							datagramSocket.send(datagramPacket);
						}
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
}
