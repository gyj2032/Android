package com.allen.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.allen.R;
import com.allen.bean.SMSBean;
import com.allen.uitl.BaseIntentUtil;
import com.allen.uitl.Const;
import com.allen.uitl.RexseeSMS;
import com.allen.view.adapter.HomeSMSAdapter;
import com.allen.view.sms.MessageBoxList;
import com.allen.view.sms.NewSMSActivity;

public class HomeSMSActivity extends Activity {

	private ListView listView;
	private HomeSMSAdapter adapter;
	private RexseeSMS rsms;
	private Button newSms;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		init();
	}

	public void init(){

		setContentView(R.layout.home_sms_page);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		listView = (ListView) findViewById(R.id.list);
		adapter = new HomeSMSAdapter(HomeSMSActivity.this);
		
		rsms = new RexseeSMS(HomeSMSActivity.this);
		List<SMSBean> listSms = rsms.getThreadsNum(rsms.getThreads(0));
		adapter.assignment(listSms);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> map = new HashMap<String, String>();
				SMSBean sb = adapter.getItem(position);
				map.put(Const.PHONE_NUM, sb.getAddress());
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, MessageBoxList.class, map);
			}
		});
		
		newSms = (Button) findViewById(R.id.newSms);
		newSms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, NewSMSActivity.class, null);
			}
		});
		
	}






}
