package com.allen.uitl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.R;
import com.allen.view.sms.MessageBoxList;

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
        intent.putExtra(Const.PARAM_ADDRESS, phone);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        showNotification(context, phone, content, R.drawable.tab_sms_normal, pIntent);

	}
	public static void showListDialog(Context context, String title, String items[], OnClickListener listener) {
		TextView titleView = new TextView(context);
		titleView.setTextAppearance(context, android.R.style.TextAppearance_Large);
		titleView.setText(title);
		titleView.setBackgroundResource(R.drawable.txl_main_tab_bg_normal);
		titleView.setGravity(Gravity.CENTER_VERTICAL);
		new AlertDialog.Builder(context)
			.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_list_item, items), 
					listener)
			.setCustomTitle(titleView)
			.show();
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
	
	public static String createTempFile(String prefix, String suffix) {
		try {
			String dir = Environment.getExternalStorageDirectory().getPath()+"/tmp/";
			File dirFile = new File(dir);
			if (!dirFile.exists() && !dirFile.isDirectory()) {
				dirFile.mkdirs();
			}
			
			File tmpFile = File.createTempFile(prefix, suffix, dirFile);
			return tmpFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
	public static boolean initDirs(String path) {
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return dirFile.mkdirs();
		}
		return true;
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	public static String getStoreFileName() {
		return dateFormat.format(new Date());
	}
	public static String createRandomCharData(int length) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		Random randdata = new Random();
		int data = 0;
		for (int i = 0; i < length; i++) {
			int index = rand.nextInt(3);
			switch (index) {
			case 0:
				data = randdata.nextInt(10);
				sb.append(data);
				break;
			case 1:
				data = randdata.nextInt(26) + 65;
				sb.append((char) data);
				break;
			case 2:
				data = randdata.nextInt(26) + 97;
				sb.append((char) data);
				break;
			}
		}
		return sb.toString();
	}
}
