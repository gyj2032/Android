package com.allen.view.sms;

import com.allen.uitl.Const;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class SmsBaseActivity extends Activity {
	protected PendingIntent sentIntent;
	protected PendingIntent deliveryIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Intent itSend = new Intent(Const.SMS_SENT_ACTION);  
        Intent itDeliver = new Intent(Const.SMS_DELIVERED_ACTION);  
          
        sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);  
          
        deliveryIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);  
        
        
        
        registerReceiver(new BroadcastReceiver(){  
			@Override
			public void onReceive(Context context, Intent intent) {
				onSentStatus(getResultCode());
			}  
        }, new IntentFilter(Const.SMS_SENT_ACTION));  
        
        registerReceiver(new BroadcastReceiver(){  
			@Override
			public void onReceive(Context context, Intent intent) {
				onDeliveryStatus(getResultCode());
			}
        }, new IntentFilter(Const.SMS_DELIVERED_ACTION));  
		super.onCreate(savedInstanceState);
	}
	protected void onSentStatus(int status) {
		
	}
	protected void onDeliveryStatus(int status) {
		
	}
}
