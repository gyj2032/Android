package com.allen.application;

import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.allen.bean.ContactBean;
import com.allen.receiver.SMSObserver;
import com.allen.service.RecordService;
import com.allen.service.RecordService.MyBinder;
import com.allen.service.T9Service;
import com.allen.uitl.Const;
import com.allen.uitl.Util;

public class MyApplication extends android.app.Application {

	private static final String SharedPreferenceName = "Config";
	private static MyApplication application;
	private SharedPreferences sharedPreferences;
	private List<ContactBean> contactBeanList;
	
	private Intent phoneIntent;
	private ServiceConnection phoneServiceConn;
	private RecordService recordService;
	
	public List<ContactBean> getContactBeanList() {
		return contactBeanList;
	}
	public void setContactBeanList(List<ContactBean> contactBeanList) {
		this.contactBeanList = contactBeanList;
	}

	public void onCreate() {

		sharedPreferences = getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
		
		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);
		
		phoneIntent = new Intent(getApplicationContext(), RecordService.class);
		phoneServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				recordService = ((MyBinder)service).getService();
			}
		};
		bindService(phoneIntent, phoneServiceConn, Service.BIND_AUTO_CREATE);
		
		getContentResolver().registerContentObserver(Uri.parse(Const.CONTENT_SMS), true, 
				new SMSObserver(getApplicationContext(), new Handler()));
		
		application = this;
		
		Util.log("My Application on create");
	}
	public static MyApplication getApplication() {
		return application;
	}
	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	public RecordService getRecordService() {
		return recordService;
	}
}
