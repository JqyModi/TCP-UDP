package com.modi.socketclient;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
	private static final String HOSTNAME = "23.83.250.56";
	private static final int PORT = 8888;
	private TextView tvTitle = null;
	private RecyclerView rvContent = null;
	private List<Map> datas = null;

	public enum CHAT_TYPE{
		TYPE_TCP,TYPE_UDP
	};

	private int []icons = {
			R.mipmap.icon_tcp,R.mipmap.icon_udp
	};
	private String []names = {
			"TCP聊天室","UDP聊天室"
	};
	private static Typeface typeface;
	private MyRvAdapter rvAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		initEvent();
	}

	private void initEvent() {

	}

	private void initData() {
		datas = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("icon",icons[0]);
		map.put("name",names[0]);
		datas.add(map);
		Map<String, Object> map1 = new HashMap<>();
		map1.put("icon",icons[1]);
		map1.put("name",names[1]);
		datas.add(map1);
	}

	private void initView() {
		rvContent = (RecyclerView) findViewById(R.id.rv_content);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		//设置字体样式：类型
		AssetManager assetManager = getAssets();
		typeface = Typeface.createFromAsset(assetManager, "fonts/yhl.ttf");
		tvTitle.setTypeface(typeface);
		rvContent.setLayoutManager(new GridLayoutManager(this,2));
		rvAdapter = new MyRvAdapter(this, datas);
		rvAdapter.setOnItemClickListener(new MyRvAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				int id = view.getId();
				Toast.makeText(MainActivity.this,names[position],Toast.LENGTH_SHORT).show();
				if (id == R.id.iv_icon){
					//开启连接Socket服务端操作
					if (position == 0){
						// 开始连接TCP聊天室
						//开启聊天界面
						Intent intent = new Intent(MainActivity.this, ChatActivity.class);
						intent.putExtra("title",names[0]);
						intent.putExtra("chattype",0);
						startActivity(intent);
					}else if (position == 1){
						// 开始连接UDP聊天室
						Intent intent = new Intent(MainActivity.this, ChatActivity.class);
						intent.putExtra("title",names[1]);
						intent.putExtra("chattype",1);
						startActivity(intent);
					}
				}else if (id == R.id.tv_name){

				}

			}
		});
		rvContent.setAdapter(rvAdapter);
		rvContent.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
		// rvContent.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
	}


	private static class MyRvAdapter extends RecyclerView.Adapter{
		private List<Map> datas = null;
		private LayoutInflater inflater = null;
		private Context context = null;

		//自顶一个接口回调来为Item设置点击事件
		private interface OnItemClickListener{
			void onItemClick(View view,int position);
		}
		private OnItemClickListener onItemClickListener;

		public void setOnItemClickListener(MyRvAdapter.OnItemClickListener onItemClickListener) {
			this.onItemClickListener = onItemClickListener;
		}

		public MyRvAdapter(Context context, List<Map> datas){
			this.context = context;
			this.datas = datas;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = inflater.inflate(R.layout.rv_item, null);
			ItemHolder itemHolder = new ItemHolder(view);
			return itemHolder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
			ItemHolder itemHolder = (ItemHolder) holder;
			Map map = datas.get(position);
			int icon = (int) map.get("icon");
			String name = (String) map.get("name");
			itemHolder.icon.setImageDrawable(context.getResources().getDrawable(icon));
			itemHolder.tvName.setText(name);
			itemHolder.tvName.setTypeface(typeface);

			//设置点击事件
			itemHolder.icon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (onItemClickListener !=null){
						onItemClickListener.onItemClick(view,position);
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return datas.size();
		}

		class ItemHolder extends RecyclerView.ViewHolder{
			private ImageView icon;
			private TextView tvName;
			public ItemHolder(View itemView) {
				super(itemView);
				icon = (ImageView) itemView.findViewById(R.id.iv_icon);
				tvName = (TextView) itemView.findViewById(R.id.tv_name);
			}
		}
	}

}

