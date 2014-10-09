package com.allen.view.sms;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.allen.uitl.Const;
import com.allen.uitl.PhoneUtil;

public class SmsBaseActivity extends Activity {
	protected SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	protected PendingIntent sentIntent;
	protected PendingIntent deliveryIntent;
	
	protected BroadcastReceiver sentReceiver;
	protected BroadcastReceiver deliveryReceiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {  
		sentReceiver = new BroadcastReceiver(){  
			@Override
			public void onReceive(Context context, Intent intent) {
				String address = intent.getStringExtra(Const.PARAM_ADDRESS);
				String content = intent.getStringExtra(Const.PARAM_CONTENT);
				switch(getResultCode()){  
		        case Activity.RESULT_OK:
		    		PhoneUtil.insertMessage(context, address, content);
		            break;
		        default:
		            break;  
				}
				onSentStatus(getResultCode());
			}  
        };
        deliveryReceiver = new BroadcastReceiver(){  
			@Override
			public void onReceive(Context context, Intent intent) {
				onDeliveryStatus(getResultCode());
			}
        };
        
        
        registerReceiver(sentReceiver, new IntentFilter(Const.SMS_SENT_ACTION));  
        registerReceiver(deliveryReceiver, new IntentFilter(Const.SMS_DELIVERED_ACTION));  
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(sentReceiver);
		unregisterReceiver(deliveryReceiver);
		super.onDestroy();
	}
	protected void sendMessage(String address, String content) {

		Intent itSend = new Intent(Const.SMS_SENT_ACTION);
		itSend.putExtra(Const.PARAM_ADDRESS, address);
		itSend.putExtra(Const.PARAM_CONTENT, content);
		sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);

//		Intent itDeliver = new Intent(Const.SMS_DELIVERED_ACTION);
//		deliveryIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);

		content = PhoneUtil.encryptMessage(content);
		PhoneUtil.sendMessage(address, content, sentIntent, deliveryIntent);
	}
	protected void onSentStatus(int status) {
		
	}
	protected void onDeliveryStatus(int status) {
		
	}
}
