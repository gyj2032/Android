package com.allen.uitl;

import android.os.Environment;

public class Const {

	public static final String PARAM_ADDRESS = "param-address";
	public static final String PARAM_CONTENT = "param-content";

	public static final String CONTENT_SMS = "content://sms";
	public static final String CONTENT_SMS_SENT = "content://sms/sent";
	public static final String CONTENT_SMS_INBOX = "content://sms/inbox";
	
	public static final String ADDRESS = "address";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String BODY = "body";
	
	public static final int SMS_TYPE_INBOX = 1;
	public static final int SMS_TYPE_SENT = 2;
	
	public static final int SMS_UNREAD = 0;
	public static final int SMS_READ = 1;
	
	public static final String ENCRPT_PREFIX = "####";
	public static final String DECRYPT_CALL_PREFIX = "!!!!";

	public static final String SMS_SENT_ACTION = "SENT_SMS_ACTION";
	public static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

	public static final String SHARED_PASSWORD = "shared-password";
	public static final String SHARED_RECORD_STATE = "shared-record-state";
	public static final String SHARED_WAIT_TIME = "shared-wait-time";
	public static final String SHARED_WAIT_TIME_INDEX = "shared-wait-time-index";
	
	public static final String RECORD_DIR = Environment.getExternalStorageDirectory().getPath() + "/Recordfile/";
}
