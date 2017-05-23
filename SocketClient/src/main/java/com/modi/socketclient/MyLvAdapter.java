package com.modi.socketclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19.
 */

public class MyLvAdapter extends BaseAdapter {
	private List<Map<String,Object>> datas = null;
	private Context context = null;
	private LayoutInflater inflater = null;

	public MyLvAdapter(Context context,List datas){
		this.context = context;
		this.datas = datas;
		this.inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int i) {
		return datas.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		Map<String, Object> map = datas.get(i);
		ItemHolder itemHolder;
		if (view == null){
			view = inflater.inflate(R.layout.chat_item, null);
			itemHolder = new ItemHolder(view);
			view.setTag(itemHolder);
		}else {
			itemHolder = (ItemHolder) view.getTag();
		}

		//显示数据
		itemHolder.ivIcon.setImageDrawable(context.getResources().getDrawable((Integer) map.get("icon")));
		itemHolder.tvMsg.setText((String) map.get("msg"));
		return view;
	}

	private class ItemHolder{
		private ImageView ivIcon = null;
		private TextView tvMsg = null;
		public ItemHolder(View view){
			ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
			tvMsg = (TextView) view.findViewById(R.id.atv_chat_content);

		}
	}

}
