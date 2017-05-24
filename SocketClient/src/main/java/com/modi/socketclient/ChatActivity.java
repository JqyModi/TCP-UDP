package com.modi.socketclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.modi.socketclient.udputils.Receiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/19.
 */
public class ChatActivity extends Activity{
	private static final String HOSTNAME = "23.83.250.56";
	private static final String HOSTNAME1 = "192.168.1.173";
	private static final int PORT = 8888;
	Socket socket = null;

	DatagramPacket datagramPacket = null;
	DatagramSocket datagramSocket = null;
	InetAddress address = null;


	//定义播段地址,接收端口,发送端口
	public static final String BroadcastGroup = "228.6.7.8";
	public static final int BroadcastRecvPort = 8888;
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

	private ListView lvContent;
	private ImageView ivIcon;
	private AppCompatEditText aetChatContent;
	private AppCompatButton abSend;
	private List<Map<String, Object>> datas = null;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			OutputStream os = null;
			BufferedOutputStream bos = null;
			BufferedWriter bw = null;
			PrintWriter pw = null;
			int what = msg.what;
			final String content = (String) msg.obj;
			if (what == 1){
				if (socket.isConnected()){
					// Toast.makeText(ChatActivity.this,"与TCP服务端成功建立连接，可以开始愉快的聊天了",Toast.LENGTH_SHORT).show();
					Log.e("client ==>","与TCP服务端成功建立连接，可以开始愉快的聊天了");
					try {
						os = socket.getOutputStream();
						bos = new BufferedOutputStream(os);
						pw = new PrintWriter(os);
						bw = new BufferedWriter(pw);
						if (!content.equals("")){
							bw.write(content);
							bw.newLine();
							bw.flush();
							//关闭资源
							// bw.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else if (what == 2){
				//刷新listView数据
				Log.e("我是客户端","刷新数据");
				Toast.makeText(ChatActivity.this,"刷新数据",Toast.LENGTH_SHORT).show();
				lvAdapter.notifyDataSetChanged();
			}else if (what == 3){
				//处理udp聊天室业务逻辑：向UDP服务端发送消息
				if (!content.equals("")){
					sendUdpMsg(content);
				}
			}

		}
	};
	private Receiver recv;

	private void sendUdpMsg(final String content) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					address = InetAddress.getByName(HOSTNAME1);
					//指定接收1024大小的数据
					byte[] buf = new byte[1024];
					//通过DatagramPacket来指定服务端的主机和端口号
					datagramPacket = new DatagramPacket(buf, buf.length, address, PORT);
					//填充数据并发送数据
					datagramPacket.setData(content.getBytes());
					datagramSocket.send(datagramPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private MyLvAdapter lvAdapter;
	private int chattype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		abSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String content = aetChatContent.getText().toString();
				if (content.equals("")){
					Log.e("client ==>","请输入消息内容");
					aetChatContent.requestFocus();
				}
				sendMsgToSocketServer(content);
				//清空输入框
				aetChatContent.setText("");
			}
		});

	}

	/**
	 * 发送消息到服务端
	 * @param content
	 */
	private void sendMsgToSocketServer(final String content) {
		ExecutorService singleThreadExecutor = null;
		if (chattype==0){
			//走TCP业务逻辑
			if (socket == null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							socket = new Socket(HOSTNAME1,PORT);
							Log.e("client ==>","客户端创建成功");
							receiveTcpMsg(socket);
							//通过handler发送客户端创建成功消息
							Message message = new Message();
							message.what = 1;
							message.obj = content;
							handler.sendMessage(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}else {
				//通过handler发送客户端创建成功消息
				Message message = new Message();
				message.what = 1;
				message.obj = content;
				handler.sendMessage(message);
			}
		}else {
			//走UDP业务逻辑
			// 方式一：使用传统方式实现
			if (datagramSocket==null){
				ExecutorService sExecutor = Executors.newSingleThreadExecutor();
				sExecutor.submit(new Runnable() {
					@Override
					public void run() {
						try {
							//createClient();
							//创建udp客户端
							// datagramSocket = new DatagramSocket();
							datagramSocket = new DatagramSocket();
							// datagramSocket.setReuseAddress(true);
							// String hostAddress = datagramSocket.getInetAddress().getHostAddress();
							int port = datagramSocket.getPort();
							// datagramSocket.bind(new InetSocketAddress(PORT));
							Log.e("createClient","udp客户端启动成功主机名:端口号为："+port);
							//循环接收消息
							// receiveUdpMsg(datagramSocket);
							receiveUdpMsg();
							//通过handler发送客户端创建成功消息
							Message message = new Message();
							message.what = 3;
							message.obj = content;
							handler.sendMessage(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}else {
				//通过handler发送客户端创建成功消息
				Message message = new Message();
				message.what = 3;
				message.obj = content;
				handler.sendMessage(message);
			}

			//方式二：使用组播/多播实现
			//创建一个新的接收线程并运行
			/*recv = new Receiver(datas);
			recv.start();
			//使用多播完成UDP的聊天室的创建
			ExecutorService sExecutor = Executors.newSingleThreadExecutor();
			sExecutor.submit(new Runnable() {
				@Override
				public void run() {
					recv.SendMessage(content);
				}
			});
			//获取到最新数据刷新界面
			datas = recv.getDatas();
			lvAdapter.notifyDataSetChanged();*/
		}
	}

	private void initData() {
		datas = new ArrayList<>();
		HashMap<String, Object> map = new HashMap<>();
		map.put("icon",R.mipmap.icon_tcp);
		map.put("msg","你好我是机器人魔笛");
		datas.add(map);
		lvAdapter = new MyLvAdapter(this, datas);
		lvContent.setAdapter(lvAdapter);
	}

	private void initView() {
		lvContent = (ListView) findViewById(R.id.lv_content);
		ivIcon = (ImageView) findViewById(R.id.iv_icon);
		aetChatContent = (AppCompatEditText) findViewById(R.id.aet_chat_content);
		abSend = (AppCompatButton) findViewById(R.id.ab_send);
		//获取intent信息
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		setTitle(title);
		chattype = intent.getIntExtra("chattype",-1);
	}

	private void receiveTcpMsg(final Socket socket) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//开启子线程循环接受消息
					InputStream is = null;
					BufferedInputStream bis = null;
					BufferedReader br = null;
					while(true){
						// socket.bind(new InetSocketAddress(HOSTNAME, PORT));
						is = socket.getInputStream();
						bis = new BufferedInputStream(is);
						br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
						String msg = null;
						while((msg = br.readLine())!=null){
							System.out.println("receiveMsg 我是客户端,收到服务端消息："+msg);
							Log.e("我是客户端,收到服务端消息：",msg);
							JSONObject jobj = new JSONObject(msg);
							HashMap<String, Object> map = new HashMap<>();
							map.put("hostName",jobj.get("hostName"));
							map.put("icon",R.mipmap.icon_tcp);
							map.put("msg",jobj.get("msg"));
							// map.put("count",jobj.get("count"));
							datas.add(map);
							if (datas.size()>0){
								Log.e("我是客户端,收到服务端消息：","并将消息添加到服务端成功");
								handler.sendEmptyMessage(2);
							}
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					/*if (socket!=null) {
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}*/
				}
			}
		}).start();
	}

	private void receiveUdpMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 执行业务逻辑
				String msg = null;
				while(true){
					/*try {
						//指定接收1024大小的数据
						byte[] buf = new byte[1024];
						DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
						//接收服务端发送过来的消息
						Log.e("开启新的客户端接收数据","1");
						DatagramSocket datagramSocket1 = new DatagramSocket();
						datagramSocket1.connect(InetAddress.getByName(HOSTNAME1),PORT);
						datagramSocket1.receive(datagramPacket);
						Log.e("开启新的客户端接收数据","2");
						//msg = datagramPacket.getData().toString();
						msg = new String(datagramPacket.getData(),0,datagramPacket.getLength());
						System.out.println("我是客户端,服务端发来消息："+msg);
						Log.e("我是客户端,服务端发来消息",msg);

						JSONObject jobj = new JSONObject(msg);
						HashMap<String, Object> map = new HashMap<>();
						map.put("hostName",jobj.get("hostName"));
						map.put("icon",R.mipmap.icon_tcp);
						map.put("msg",jobj.get("msg"));
						// map.put("count",jobj.get("count"));
						datas.add(map);
						if (datas.size()>0){
							Log.e("我是客户端,收到服务端消息：","并将消息添加到服务端成功");
							handler.sendEmptyMessage(2);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}*/
					msg = GetData();
					try {
						if (!msg.equals("")){
							JSONObject jobj = new JSONObject(msg);
							HashMap<String, Object> map = new HashMap<>();
		 					map.put("hostName",jobj.get("hostName"));
							map.put("icon",R.mipmap.icon_tcp);
							map.put("msg",jobj.get("msg"));
							// map.put("count",jobj.get("count"));
							datas.add(map);
							if (datas.size()>0){
								Log.e("我是客户端,收到服务端消息：","并将消息添加到服务端成功");
								handler.sendEmptyMessage(2);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	//接收数据并返回
	public String GetData()
	{
		try
		{
			//接收数据并送与接收缓冲区
			DataRecvPacket = new DatagramPacket(RecvBuf,1024);
			datagramSocket.receive(DataRecvPacket);
			//取得数据并返回数据
			Message = new String(DataRecvPacket.getData(),0,DataRecvPacket.getLength());
			Message = Message + " from " + DataRecvPacket.getAddress().getHostName();
			Message = Message + " 端口 " + DataRecvPacket.getPort();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		Log.e("Message === ",Message);
		return Message;
	}

}
