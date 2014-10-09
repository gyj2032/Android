package com.allen.uitl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen.view.HomeDialActivity;
import com.allen.view.sms.MessageBoxList;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

public class PhoneUtil {

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
	public static void receiveMessage(Context context, String address, String content) {
		ContentValues values = new ContentValues();  
	    values.put(Const.ADDRESS, address);
	    values.put(Const.READ, Const.SMS_UNREAD);
	    values.put(Const.BODY, content);
	    context.getContentResolver().insert(Uri.parse(Const.CONTENT_SMS_INBOX), values);
	}
	public static void insertMessage(Context context, String address, String content) {
		ContentValues values = new ContentValues();
		values.put(Const.ADDRESS, address);
		values.put(Const.BODY, content);
		context.getContentResolver().insert(Uri.parse(Const.CONTENT_SMS_SENT), values);
	}
	public static void updateMessageRead(Context context, String address) {
		ContentValues values = new ContentValues();
	    values.put(Const.READ, Const.SMS_READ);
		context.getContentResolver().update(Uri.parse(Const.CONTENT_SMS), values, " address=? and read=? ", 
				new String[]{address, ""+Const.SMS_UNREAD});
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
	
	public static Intent getLaunchIntent(Context context, String appName) {
	    List<ResolveInfo> list = new ArrayList<ResolveInfo>();  
	    PackageManager pm = context.getPackageManager(); 
		Intent intent = new Intent(Intent.ACTION_MAIN, null);  
        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
        list = pm.queryIntentActivities(intent, 0);  
        Collections.sort(list, new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo info : list) {
			String appLabel = (String) info.loadLabel(pm);
			if (appLabel.trim().equals(appName)) {
				String activityName = info.activityInfo.name;
				String pkgName = info.activityInfo.packageName;
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(pkgName, activityName));
				return launchIntent;
			}
		}
		return null;
	}
	public static void processEncryptMessage(Context context, String address, String content) {
		updateMessageRead(context, address);
		String text = PhoneUtil.decryptMessage(content);
		Util.showToast(context, text);
		if (text.startsWith(Const.DECRYPT_CALL_PREFIX)) {
			if (text.length() == 10) {
				HomeDialActivity.getHomeDialActivity().onDecryptCallback(text, address);
			} else if (text.length() > 22){
				content = Const.DECRYPT_CALL_PREFIX + text.substring(16, 22);
				content = PhoneUtil.encryptMessage(content);
				PhoneUtil.sendMessage(address, content, null, null);
			}
		}
	}
	public static void call(Context context, String address) {
		Intent it = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + address));
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
	}
	public static String getSMSThreadId(Context context, String adddress){
		ContentResolver contentResolver = context.getContentResolver();  
		Cursor cursor = contentResolver.query(Uri.parse(Const.CONTENT_SMS), new String[] { "thread_id" }, " address like '%" + adddress + "%' ", null, null);  
		String threadId = "";
		if (cursor == null || cursor.getCount() > 0){
			cursor.moveToFirst();
			threadId = cursor.getString(0);
			cursor.close();
			return threadId;
		}else{
			cursor.close();
			return threadId;
		}
	}
    public static String getDisplayNameByAddress(Context context, String address) {
    	String name = null;
    	if (!Util.isNull(address)) {
	        Cursor cursor = context.getContentResolver().query( 
	                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
	                ContactsContract.CommonDataKinds.Phone.DATA1 + " = ? ",
	                new String[] { address }, 
//	                null,
	                null);
	        if (cursor.moveToFirst()) {
	        	name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	        	cursor.close();  
	        }
    	}
        return name;  
//		Cursor c = null;
//		try {
//			c = context.getContentResolver().query(
//					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//					new String[] {
//							ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//							ContactsContract.CommonDataKinds.Phone.NUMBER },
//					null, null, null);
//			if (c != null && c.moveToFirst()) {
//				while (!c.isAfterLast()) {
//					if (PhoneNumberUtils.compare(address, c.getString(1))) {
//						return c.getString(0);
//					}
//					c.moveToNext();
//				}
//			}
//		} catch (Exception e) {
//		} finally {
//			if (c != null) {
//				c.close();
//			}
//		}
//		return null;
    }

	public static void showOptionDialog(final Context context, String displayName, final String address) {
		String[] items = new String[] { "拨打电话", "加密通话", "发送短信", "查看详细" };
		Util.showListDialog(context, displayName, items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					PhoneUtil.call(context, address);
					break;
				case 1:
					HomeDialActivity.getHomeDialActivity().doDecryptCall(address);
					break;
				case 2:
//					String threadId = PhoneUtil.getSMSThreadId(context, address);
					Map<String, String> map = new HashMap<String, String>();
					map.put(Const.PARAM_ADDRESS, address);
					BaseIntentUtil.intentSysDefault(context, MessageBoxList.class, map);
					break;
				case 3:
//					Uri personUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cb.getContactId());
//					Intent i = new Intent();
//					i.setAction(Intent.ACTION_VIEW);
//					i.setData(personUri);
//					startActivity(i);
					break;
				}
			}
		});
	}
}
