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
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
	private static final String HOSTNAME = "localhost";
	private static final String HOSTNAME1 = "23.83.250.56";
	private static final int PORT = 8888;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		createClient();
	}

	private static void createClient() {
		Socket socket = null;
		
		OutputStream os =null;
		BufferedOutputStream bos =null;
		PrintWriter pw =null;
		BufferedWriter bw =null;
		Scanner scanner = null;
		String msg = "欢迎访问魔笛服务端";
		try {
			socket = new Socket(HOSTNAME, PORT);
//			开启子线程循环接受消息
			receiveMsg(socket);
			//循环监听控制台消息
			while(true){
//				socket.bind(new InetSocketAddress(HOSTNAME, PORT));
				os = socket.getOutputStream();
				bos = new BufferedOutputStream(os);
				pw = new PrintWriter(bos);
				bw = new BufferedWriter(pw);
				scanner = new Scanner(System.in);
				if(scanner.hasNext()){
					msg = scanner.nextLine();
					System.out.println("我是客户端,收到服务端消息："+msg);
					bw.write(msg);
					bw.newLine();
					bw.flush();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (socket!=null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void receiveMsg(Socket socket) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
//					开启子线程循环接受消息
					InputStream is = null;
					BufferedInputStream bis = null;
					BufferedReader br = null;
					while(true){
//						socket.bind(new InetSocketAddress(HOSTNAME, PORT));
						is = socket.getInputStream();
						bis = new BufferedInputStream(is);
						br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
						String msg = null;
						while((msg = br.readLine())!=null){
							System.out.println("receiveMsg 我是客户端,收到服务端消息："+msg);
						}
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					if (socket!=null) {
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
	

}
