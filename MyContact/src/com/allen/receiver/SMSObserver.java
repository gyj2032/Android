package com.allen.receiver;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.allen.uitl.Const;
import com.allen.uitl.PhoneUtil;
import com.allen.uitl.Util;

public class SMSObserver extends ContentObserver{
	
	private Context mContext;
    private Handler mHandler;
    
	public SMSObserver(Context context, Handler handler) {
		super(handler);
		mContext = context;
		mHandler = handler;
	}
	@Override
	public void onChange(boolean selfChange) {
		Uri uri = Uri.parse(Const.CONTENT_SMS_INBOX);  
        Cursor c = mContext.getContentResolver().query(uri, null, "read=?", new String[]{""+Const.SMS_UNREAD}, "date desc");  
        if (c != null) {  
            while (c.moveToNext()) {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
//                Date d = new Date(c.getLong(c.getColumnIndex("date")));  
//                String date = dateFormat.format(d);  
//                StringBuilder sb = new StringBuilder();  
//                sb.append("发件人手机号码: " + c.getString(c.getColumnIndex("address")))  
//                        .append("信息内容: " + c.getString(c.getColumnIndex("body")))  
//                        .append(" 是否查看: " + c.getInt(c.getColumnIndex("read")))  
//                        .append(" 类型： " + c.getInt(c.getColumnIndex("type"))).append(date);  
//                Log.i("xxx", sb.toString());  
            	
            	String address = c.getString(c.getColumnIndex(Const.ADDRESS));
            	String content = c.getString(c.getColumnIndex(Const.BODY));
            	
//				Util.showMeassageNotification(mContext, address, "New message .");
//				PhoneUtil.receiveMessage(mContext, address, content);
				
				if (content.startsWith(Const.ENCRPT_PREFIX)) {
					PhoneUtil.processEncryptMessage(mContext, address, content);
				}
            }  
            c.close();  
        }  
		super.onChange(selfChange);
	}

}
