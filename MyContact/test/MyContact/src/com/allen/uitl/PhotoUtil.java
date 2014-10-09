package com.allen.uitl;

import java.util.List;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.SmsManager;

public class PhotoUtil {


	public static void sendMessage(String phone, String content, PendingIntent sentIntent, PendingIntent deliveryIntent) {
		SmsManager smsManager = SmsManager.getDefault();
        if(content.length() > 70) {
            List<String> contents = smsManager.divideMessage(content);
            for(String sms : contents) {
            	smsManager.sendTextMessage(phone, null, sms, sentIntent, deliveryIntent);
            }
        } else {
         smsManager.sendTextMessage(phone, null, content, sentIntent, deliveryIntent);
        }
	}
	public static void insertMessage(Context context, String address, String content, int type) {
//		ContentValues values = new ContentValues();
//		values.put("address", address);
//		values.put("body", content);
//		context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
		
		ContentValues values = new ContentValues();  
	    values.put(Const.ADDRESS, address);  
//	    values.put(Const.READ, 1);
//	    values.put(Const.STATUS, -1);
	    values.put(Const.TYPE, type);
	    values.put(Const.BODY, content);
	    context.getContentResolver().insert(Uri.parse("content://sms"), values);  
	}
	public static String encryptMessage(String text) {
		if (!Util.isNull(text)) {
			text = Const.ENCRPT_PREFIX + AESHelper.encrypt(text, Util.PASSWORD);
		}
		return text;
	}
	public static String decryptMessage(String text) {
		if (!Util.isNull(text) && text.startsWith(Const.ENCRPT_PREFIX)) {
			text = text.substring(Const.ENCRPT_PREFIX.length());
			text = AESHelper.decrypt(text, Util.PASSWORD);
		}
		return text;
	}
}
