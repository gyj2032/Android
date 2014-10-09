package com.allen.view.adapter;

import java.util.List;

import com.allen.bean.CallLogBean;
import com.allen.uitl.PhoneUtil;
import com.allen.view.HomeDialActivity;

import com.allen.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeDialAdapter extends BaseAdapter {

	private Context context;
	private List<CallLogBean> list;
	private LayoutInflater inflater;
	
	public HomeDialAdapter(Context context, List<CallLogBean> list) {

		this.context = context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_dial_calllog_list_item, null);
			holder = new ViewHolder();
			holder.call_type = (ImageView) convertView.findViewById(R.id.call_type);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.call_btn = (ImageView) convertView.findViewById(R.id.call_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CallLogBean clb = list.get(position);
		switch (clb.getType()) {
		case 1:
			holder.call_type.setBackgroundResource(R.drawable.call_log_type_incoming);
			break;
		case 2:
			holder.call_type.setBackgroundResource(R.drawable.call_log_type_outgoing);
			break;
		case 3:
			holder.call_type.setBackgroundResource(R.drawable.call_log_type_missed);
			break;
		}
		holder.name.setText(clb.getName());
		holder.number.setText(clb.getNumber());
		holder.time.setText(clb.getDate());
		
		addViewListener(holder.call_btn, clb, position);
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView call_type;
		TextView name;
		TextView number;
		TextView time;
		ImageView call_btn;
	}
	
	private void addViewListener(View view, final CallLogBean clb, final int position){
		view.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				PhoneUtil.showOptionDialog(context, list.get(position).getName(), list.get(position).getNumber());
			}
		});
	}

}
