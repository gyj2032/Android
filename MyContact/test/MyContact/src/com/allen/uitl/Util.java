package com.allen.uitl;

import com.allen.R;
import com.allen.view.sms.MessageBoxList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Util {
	public static final String TAG = "yjguo";
	public static final String PASSWORD = "123456";
	public static boolean equals(String str1, String str2) {
		if (str1 != null && str2 != null && !str1.equals("")) {
			return str1.equals(str2);
		}
		return false;
	}
	public static void log(String msg) {
		Log.i(TAG, msg);
	}
	public static boolean isNull(String text) {
		if (text == null || text.equals("")) {
			return true;
		}
		return false;
	}
	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void showNotification(Context context, String title, String text, int icon, PendingIntent pendingIntent) {

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
//                .addAction(R.drawable.tab_sms_normal, "Call", pIntent)
//                .addAction(R.drawable.tab_sms_normal, "More", pIntent)
//                .addAction(R.drawable.tab_sms_normal, "And more", pIntent)
                .build();
          
        NotificationManager notificationManager = 
          (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n); 
	}
	
	public static void showMeassageNotification(Context context, String phone, String content) {
		
        Intent intent = new Intent(context, MessageBoxList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.PHONE_NUM, phone);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        showNotification(context, phone, content, R.drawable.tab_sms_normal, pIntent);

	}
	
//	public static void showNotification(Context context, String title, String msg) {
//        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent intent = new Intent(context, MessageBoxList.class);
//        intent.putExtra("isNotify", true);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Notification notif = new Notification(R.drawable.ic_launcher, msg, System.currentTimeMillis());
//        notif.setLatestEventInfo(context, title, msg, contentIntent);
//        notif.defaults = Notification.DEFAULT_ALL;
//        notif.flags = Notification.FLAG_ONGOING_EVENT;
//        nm.notify(R.string.app_name, notif);
//    }
}
