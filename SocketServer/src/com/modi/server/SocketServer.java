package com.modi.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

import com.google.gson.JsonObject;

public class SocketServer {
	private static final String HOSTNAME1 = "localhost";
	private static final String HOSTNAME = "192.168.1.173";
	private static final int PORT = 8888;
	static List<Socket> clients = null;
	
	private static void findClient(){
		try {
			clients = new ArrayList<>();
			ServerSocket serverSocket = null;
			serverSocket = new ServerSocket(PORT);
//			serverSocket.bind(new InetSocketAddress(HOSTNAME, PORT));
			System.out.println("~~~服务端已经启动，等待客户端的连接~~~");
			
			while(true){
				//监听客户端的连接
				Socket accept = serverSocket.accept();
				//开启子线程监听消息
				System.out.println("客户端"+accept.getInetAddress().getHostName()+"接入服务器");
				clients.add(accept);
				System.out.println("在线人数："+clients.size());
				receiveMsg(accept);
				/*OutputStream os = accept.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(os);
				PrintWriter pw = new PrintWriter(bos);
				BufferedWriter bw = new BufferedWriter(pw);
				String msg = "欢迎访问魔笛服务端";
				bw.write(msg);
				bw.newLine();
				bw.flush();*/
				String msg = "欢迎访问魔笛服务端";
				JsonObject jobj = new JsonObject();
				jobj.addProperty("hostName", accept.getInetAddress().getHostName());
				jobj.addProperty("msg", msg);
				jobj.addProperty("count", clients.size());
				
				String jsonStr = jobj.toString();
				
				System.out.println(jsonStr);
				
				OutputStream os = accept.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(os);
				PrintWriter pw = new PrintWriter(bos);
				BufferedWriter bw = new BufferedWriter(pw);
				bw.write(jsonStr);
				bw.newLine();
				bw.flush();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static <V> void receiveMsg(Socket socket) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
//					开启子线程循环接受消息
					InputStream is = null;
					BufferedInputStream bis = null;
					BufferedReader br = null;
					
					is = socket.getInputStream();
					bis = new BufferedInputStream(is);
					br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
					String msg = null;
					System.out.println("br.readLine 我是服务端,收到客户端消息："+br.readLine());
					while((msg = br.readLine())!=null){
						System.out.println("receiveMsg 我是服务端,收到客户端消息："+msg);
						//将收到的消息推送到所以设备
						if (msg.equalsIgnoreCase("bye")) {
							clients.remove(socket);
						}else {
							for(int i=0;i<clients.size();i++){
								Socket socket2 = clients.get(i);
//								{ \"type\":\"Label\", \"text\":\"设置\", \"size\":25,\"align\":\"center\",\"color\":\"0,0,255\"}
								JsonObject jobj = new JsonObject();
								jobj.addProperty("hostName", socket2.getInetAddress().getHostName());
								jobj.addProperty("msg", msg);
								jobj.addProperty("count", clients.size());
								
								String jsonStr = jobj.toString();
								
								System.out.println(jsonStr);
								
								OutputStream os = socket2.getOutputStream();
								BufferedOutputStream bos = new BufferedOutputStream(os);
								PrintWriter pw = new PrintWriter(bos);
								BufferedWriter bw = new BufferedWriter(pw);
								bw.write(jsonStr);
								bw.newLine();
								bw.flush();
							}
						}
					}
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	public static void main(String[] args) {
//		sendRespToClient();
		findClient();
		
//		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);
//		new ThreadPoolExecutor(1, 2, 5000, TimeUnit.SECONDS, workQueue);
	}
}
