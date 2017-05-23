package com.modi.socketclient;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19.
 */

public class MyRvAdapter extends RecyclerView.Adapter {
	private List<Map> datas = null;
	private LayoutInflater inflater = null;
	private Context context = null;
	private static Typeface typeface;

	//自顶一个接口回调来为Item设置点击事件
	private interface OnItemClickListener{
		void onItemClick(View view, int position);
	}
	private OnItemClickListener onItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public MyRvAdapter(Context context, List<Map> datas){
		this.context = context;
		this.datas = datas;
		this.inflater = LayoutInflater.from(context);
		//设置字体样式：类型
		AssetManager assetManager = context.getAssets();
		typeface = Typeface.createFromAsset(assetManager, "fonts/yhl.ttf");
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
