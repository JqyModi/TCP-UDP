package com.modi.socketclient.udputils;

import android.util.Log;
import android.widget.ListView;

import com.modi.socketclient.MyLvAdapter;
import com.modi.socketclient.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Receiver extends Thread
{  
 //接收的信息  
 private String Message;  
 Broadcast Recv = new Broadcast();
    private ListView lv = null;
    private List<Map<String, Object>> datas = null;
    private MyLvAdapter adapter = null;
 //构造函数,  
   public Receiver(ListView lv, List<Map<String, Object>> datas, MyLvAdapter adapter)
   {
       this.lv = lv;
       this.datas = datas;
       this.adapter = adapter;
   }
    public Receiver(List<Map<String, Object>> datas)
    {
        this.datas = datas;
    }
     
   public void run()  
   {  
    while (true)  
    {  
     //监听接收数据包  
     Message = Recv.GetData();  
     //System.out.println(Message);  
     //displayArea.setText("11111");
        HashMap<String, Object> map = new HashMap<>();
        map.put("icon", R.mipmap.icon_tcp);
        map.put("msg",Message);
        // map.put("count",jobj.get("count"));
        if (datas!=null){
            Log.e("我是客户端,收到服务端消息：","并将消息添加到服务端成功");
            //刷新数据
            for (int i = 0; i < datas.size(); i++) {
                Map<String, Object> map1 = datas.get(i);
                if (map1.get("msg").equals(Message)){
                    datas.remove(map1);
                }
            }
            datas.add(map);
        }
    }
   }  
     
   //发送数据  
   public void SendMessage(String InMsg)  
   {  
    Recv.SendData(InMsg);  
   }
    public List<Map<String, Object>> getDatas(){
        if (datas!=null&&datas.size()>0){
            return datas;
        }
        return null;
    }
} 
