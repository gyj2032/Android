package com.allen.view.sms;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.R;
import com.allen.bean.MessageBean;
import com.allen.uitl.Const;
import com.allen.uitl.PhotoUtil;
import com.allen.view.adapter.MessageBoxListAdapter;

public class MessageBoxList extends SmsBaseActivity {

	private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	private ListView talkListView;
	private BaseAdapter adapter;
	private List<MessageBean> list = null;
	private Button sendButton;
	private Button returnButton;
	private Button callButton;
	private EditText contentText;
	private AsyncQueryHandler asyncQuery;
	private String address;
	
	private Handler handler;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_messageboxlist);

		returnButton = (Button) findViewById(R.id.btn_return);
		callButton = (Button) findViewById(R.id.btn_call);
		sendButton = (Button) findViewById(R.id.fasong);
		contentText = (EditText) findViewById(R.id.neirong);

		Intent intent = getIntent();
		address = getIntent().getStringExtra(Const.PHONE_NUM);
		TextView tv = (TextView) findViewById(R.id.topbar_title);
		tv.setText(getPersonName(address));

		init(address);

		returnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageBoxList.this.setResult(RESULT_OK);
				MessageBoxList.this.finish();
			}
		});
		callButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:" + address);
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				startActivity(it);
			}
		});

//		handler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				updateStatue();
//			}
//		};
		sendButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String content = contentText.getText().toString();
				content = PhotoUtil.encryptMessage(content);
				PhotoUtil.sendMessage(address, content, sentIntent, deliveryIntent);
				PhotoUtil.insertMessage(MessageBoxList.this, address, content, Const.SMS_TYPE_SENT);

				updateData(address, content);
				

				contentText.setText("");
			}
		});
	}
	@Override
	protected void onSentStatus(int status) {
		updateStatue(status);
	}
	private void updateData(String address, String content) {
		MessageBean m = new MessageBean(address, "正在发送 ..", content, R.layout.list_say_me_item);
		list.add(m);
		talkListView.setSelection(talkListView.getCount() - 1);
		
		adapter.notifyDataSetInvalidated();
	}
	private void updateStatue(int status) {
		String date = "";
		
		switch(status){  
        case Activity.RESULT_OK:
        	date = sdf.format(new Date(System.currentTimeMillis()));
            break;  
        default:
        	date = "未发送";
            break;  
		}
		list.get(list.size()-1).setDate(date);
		talkListView.setSelection(talkListView.getCount() - 1);
		
		adapter.notifyDataSetInvalidated();
	}
	
	private void init(String phoneNum){
		
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		
		talkListView = (ListView) findViewById(R.id.list);
		list = new ArrayList<MessageBean>();
		
		Uri uri = Uri.parse("content://sms"); 
		String[] projection = new String[] {
				"date",
				"address",
				"person",
				"body",
				"type"
		};// 查询的列
		asyncQuery.startQuery(0, null, uri, projection, "address = " + phoneNum, null, "date asc");
	}
//	private void init(String thread){
//		
//		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
//		
//		talkListView = (ListView) findViewById(R.id.list);
//		list = new ArrayList<MessageBean>();
//		
//		Uri uri = Uri.parse("content://sms"); 
//		String[] projection = new String[] {
//				"date",
//				"address",
//				"person",
//				"body",
//				"type"
//		};// 查询的列
//		asyncQuery.startQuery(0, null, uri, projection, "thread_id = " + thread, null, "date asc");
//	}
	
	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String date = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
					if(cursor.getInt(cursor.getColumnIndex("type")) == 1) {
						MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")), 
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_he_item);
						list.add(d);
					} else { 
						MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_me_item);
						list.add(d);
					}
				}
				if (list.size() > 0) {
					adapter = new MessageBoxListAdapter(MessageBoxList.this, list);
					talkListView.setAdapter(adapter);
					talkListView.setDivider(null);
					talkListView.setSelection(list.size());
				} else {
					Toast.makeText(MessageBoxList.this, "没有短信进行操作", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	public String getPersonName(String number) {
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, };   
		Cursor cursor = this.getContentResolver().query(   
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
				projection,    
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",  
				null,        
				null);  
		if( cursor == null ) {   
			return number;   
		}   
		String name = number;
		for( int i = 0; i < cursor.getCount(); i++ ) {   
			cursor.moveToPosition(i);   
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));   
		}
		cursor.close();
		return name;   
	} 
	
}
