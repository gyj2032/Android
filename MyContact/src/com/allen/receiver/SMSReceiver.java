package com.allen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.allen.uitl.Const;
import com.allen.uitl.PhoneUtil;
import com.allen.uitl.Util;

public class SMSReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			abortBroadcast();

			Bundle bundle = intent.getExtras();
			Object[] obj = (Object[]) bundle.get("pdus");
			SmsMessage[] smss = new SmsMessage[obj.length];
			for (int i = 0; i < obj.length; i++) {
				smss[i] = SmsMessage.createFromPdu((byte[]) obj[i]);
			}

			for (SmsMessage sms : smss) {
				String address = sms.getDisplayOriginatingAddress();
				String content = sms.getDisplayMessageBody();

				Util.showMeassageNotification(context, address, "New message .");
				PhoneUtil.receiveMessage(context, address, content);
				
//				if (content.startsWith(Const.ENCRPT_PREFIX)) {
//					PhoneUtil.processEncryptMessage(context, address, content);
//				}
			}
		}
	}

}