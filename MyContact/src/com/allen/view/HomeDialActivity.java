package com.allen.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.allen.R;
import com.allen.application.MyApplication;
import com.allen.bean.CallLogBean;
import com.allen.uitl.BaseIntentUtil;
import com.allen.uitl.Const;
import com.allen.uitl.PhoneUtil;
import com.allen.uitl.Util;
import com.allen.view.adapter.HomeDialAdapter;
import com.allen.view.adapter.T9Adapter;
import com.allen.view.sms.MessageBoxList;

public class HomeDialActivity extends Activity implements OnClickListener {
	
	private static int Count_Down_Max = 30;
	private Handler countDownHandler;
	private int downCount;
	private String randomCode;
	private String decrptCallAddres;
	
	private AsyncQueryHandler asyncQuery;
	
	private HomeDialAdapter adapter;
	private ListView callLogList;
	
	private List<CallLogBean> list;
	
	private LinearLayout bohaopan;
//	private LinearLayout keyboard_show_ll;
	private View keyboard_show;
	
	private ProgressDialog progressDialog;
//	private Button phone_view;
	private View delete;
	private TextView numberTextView;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private SoundPool spool;
	private AudioManager am = null;
	
	private MyApplication application;
	private ListView listView;
	private T9Adapter t9Adapter;
	
	private static HomeDialActivity instance;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_dial_page);
		
		application = (MyApplication)getApplication();
		listView = (ListView) findViewById(R.id.contact_list);
		
		bohaopan = (LinearLayout) findViewById(R.id.bohaopan);
//		keyboard_show_ll = (LinearLayout) findViewById(R.id.keyboard_show_ll);
		keyboard_show = findViewById(R.id.keyboard_show);
		callLogList = (ListView)findViewById(R.id.call_log_list);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		
		keyboard_show.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialPadShow();
			}
		});
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		spool = new SoundPool(11, AudioManager.STREAM_SYSTEM, 5);
		map.put(0, spool.load(this, R.raw.dtmf0, 0));
		map.put(1, spool.load(this, R.raw.dtmf1, 0));
		map.put(2, spool.load(this, R.raw.dtmf2, 0));
		map.put(3, spool.load(this, R.raw.dtmf3, 0));
		map.put(4, spool.load(this, R.raw.dtmf4, 0));
		map.put(5, spool.load(this, R.raw.dtmf5, 0));
		map.put(6, spool.load(this, R.raw.dtmf6, 0));
		map.put(7, spool.load(this, R.raw.dtmf7, 0));
		map.put(8, spool.load(this, R.raw.dtmf8, 0));
		map.put(9, spool.load(this, R.raw.dtmf9, 0));
		map.put(11, spool.load(this, R.raw.dtmf11, 0));
		map.put(12, spool.load(this, R.raw.dtmf12, 0));
		
		findViewById(R.id.btn_call).setOnClickListener(this);
		findViewById(R.id.btn_decrypt_call).setOnClickListener(this);
		findViewById(R.id.btn_send_message).setOnClickListener(this);
		
		numberTextView = (TextView) findViewById(R.id.text_number);
		numberTextView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(null == application.getContactBeanList() || application.getContactBeanList().size()<1 || "".equals(s.toString())){
					listView.setVisibility(View.INVISIBLE);
					callLogList.setVisibility(View.VISIBLE);
				}else{
					if(null == t9Adapter){
						t9Adapter = new T9Adapter(HomeDialActivity.this);
						t9Adapter.assignment(application.getContactBeanList());
//						TextView tv = new TextView(HomeDialActivity.this);
//						tv.setBackgroundResource(R.drawable.dial_input_bg2);
//						listView.addFooterView(tv);
						listView.setAdapter(t9Adapter);
						listView.setTextFilterEnabled(true);
						listView.setOnScrollListener(new OnScrollListener() {
							public void onScrollStateChanged(AbsListView view, int scrollState) {
								if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
									if(bohaopan.getVisibility() == View.VISIBLE){
										bohaopan.setVisibility(View.GONE);
//										keyboard_show_ll.setVisibility(View.VISIBLE);
									}
								}
							}
							public void onScroll(AbsListView view, int firstVisibleItem,
									int visibleItemCount, int totalItemCount) {
							}
						});

					}else{
						callLogList.setVisibility(View.INVISIBLE);
						listView.setVisibility(View.VISIBLE);
						t9Adapter.getFilter().filter(s);
						listView.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								PhoneUtil.call(getApplicationContext(), t9Adapter.getList().get(position).getPhoneNum());
							}
						});
						listView.setOnItemLongClickListener(new OnItemLongClickListener() {
							@Override
							public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
								PhoneUtil.showOptionDialog(HomeDialActivity.this, t9Adapter.getList().get(position).getDisplayName(), 
										application.getContactBeanList().get(position).getPhoneNum());
								return true;
							}
						});
						}
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {

			}
		});
		delete = findViewById(R.id.delete);
		delete.setOnClickListener(this);
		delete.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				numberTextView.setText("");
				return false;
			}
		});
		
		for (int i = 0; i < 12; i++) {
			View v = findViewById(R.id.dialNum1 + i);
			v.setOnClickListener(this);
		}
		
		init();
		
//		String text = "你       　　　　　　　　好";
//		String pwd = "000";
//		
//		String encrypt = AESHelper.encrypt(text, pwd);
//		Util.log(encrypt);
//		// 加密完后的字符 密钥
//		String decrypt = AESHelper.decrypt(encrypt, pwd);
//		
//		Util.log(decrypt);

//		Util.showMeassageNotification(this, "10001", "1");
//		PhotoUtil.insertMessage(this, "10001", "1", Const.SMS_TYPE_INBOX);
//		PhotoUtil.insertMessage(this, "10001", "2", Const.SMS_TYPE_SENT);
		
//		Util.showToast(this, "" + Integer.MAX_VALUE);
//		PhotoUtil.updateMessageRead(this, "10001");
	}
	public static HomeDialActivity getHomeDialActivity() {
		return instance;
	}
	private void init(){
		instance = this;
		
		Uri uri = CallLog.Calls.CONTENT_URI;
		
		String[] projection = { 
				CallLog.Calls.DATE,
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls._ID
		}; // 查询的列
		asyncQuery.startQuery(0, null, uri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);  
		
		countDownHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case -1:
					dismissProcessDialog();
					break;
				case 0:
					if (--downCount > 0) {
						progressDialog.setProgress(downCount);
						sendEmptyMessageDelayed(0, 1000);
					} else {
						dismissProcessDialog();
					}
					break;
				case 1:
					call(decrptCallAddres);
					break;

				default:
					break;
				}
			};
		};
	}
	

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<CallLogBean>();
				SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
				Date date;
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
//					String date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
					String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
					int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
					String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));//缓存的名称与电话号码，如果它的存在
					int id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));

					CallLogBean clb = new CallLogBean();
					clb.setId(id);
					clb.setNumber(number);
					clb.setName(cachedName);
					if(null == cachedName || "".equals(cachedName)){
						clb.setName(number);
					}
					clb.setType(type);
					clb.setDate(sfd.format(date));
					
					list.add(clb);
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}


	private void setAdapter(final List<CallLogBean> list) {
		adapter = new HomeDialAdapter(this, list);
//		TextView tv = new TextView(this);
//		tv.setBackgroundResource(R.drawable.dial_input_bg2);
//		callLogList.addFooterView(tv);
		callLogList.setAdapter(adapter);
		callLogList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					if(bohaopan.getVisibility() == View.VISIBLE){
						bohaopan.setVisibility(View.GONE);
//						keyboard_show_ll.setVisibility(View.VISIBLE);
					}
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		callLogList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PhoneUtil.call(getApplicationContext(), list.get(position).getNumber());
			}
		});
		callLogList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				PhoneUtil.showOptionDialog(HomeDialActivity.this, list.get(position).getName(), list.get(position).getNumber());
				return true;
			}
		});
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialNum0:
			if (numberTextView.getText().length() < 12) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum1:
			if (numberTextView.getText().length() < 12) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum2:
			if (numberTextView.getText().length() < 12) {
				play(2);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum3:
			if (numberTextView.getText().length() < 12) {
				play(3);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum4:
			if (numberTextView.getText().length() < 12) {
				play(4);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum5:
			if (numberTextView.getText().length() < 12) {
				play(5);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum6:
			if (numberTextView.getText().length() < 12) {
				play(6);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum7:
			if (numberTextView.getText().length() < 12) {
				play(7);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum8:
			if (numberTextView.getText().length() < 12) {
				play(8);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum9:
			if (numberTextView.getText().length() < 12) {
				play(9);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialx:
			if (numberTextView.getText().length() < 12) {
				play(11);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialj:
			if (numberTextView.getText().length() < 12) {
				play(12);
				input(v.getTag().toString());
			}
			break;
		case R.id.delete:
			delete();
			break;
		case R.id.btn_call:
			if (numberTextView.getText().toString().length() >= 4) {
				PhoneUtil.call(HomeDialActivity.this, numberTextView.getText().toString());
			}
			break;
		case R.id.btn_decrypt_call:
			decrptCallAddres = numberTextView.getText().toString();
			if (decrptCallAddres.length() >= 4) {
				doDecryptCall(decrptCallAddres);
			}
			break;
		case R.id.btn_send_message:
			decrptCallAddres = numberTextView.getText().toString();
			if (decrptCallAddres.length() >= 4) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(Const.PARAM_ADDRESS, decrptCallAddres);
				BaseIntentUtil.intentSysDefault(HomeDialActivity.this, MessageBoxList.class, map);
			}
			break;
		default:
			break;
		}
	}
	public void doDecryptCall(String address) {
		Count_Down_Max = MyApplication.getApplication().getSharedPreferences().getInt(Const.SHARED_WAIT_TIME, Count_Down_Max);
		showProcessDialog();
		randomCode = Util.createRandomCharData(6);
		String content = Const.DECRYPT_CALL_PREFIX + "请求加密通话，验证码是：" + randomCode + ", "+ Count_Down_Max +"秒内有效";
		content = PhoneUtil.encryptMessage(content);
//		PhoneUtil.sendMessage(address, content, null, null);
	}
	public void onDecryptCallback(String text, String address) {
		dismissProcessDialog();
		if (address.endsWith(decrptCallAddres) && text.contains(randomCode)) {
			countDownHandler.sendEmptyMessage(1);
		}
	}

	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}
	private void input(String str) {
		String p = numberTextView.getText().toString();
		numberTextView.setText(p + str);
	}
	private void delete() {
		String p = numberTextView.getText().toString();
		if(p.length()>0){
			numberTextView.setText(p.substring(0, p.length()-1));
		}
	}
	private void call(String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		startActivity(it);
	}

	public boolean dialPadShow(){
		if(bohaopan.getVisibility() == View.VISIBLE){
			bohaopan.setVisibility(View.GONE);
			return false;
//			keyboard_show_ll.setVisibility(View.VISIBLE);
		}else{
			bohaopan.setVisibility(View.VISIBLE);
			return true;
//			keyboard_show_ll.setVisibility(View.INVISIBLE);
		}
	}
	public void dialPadShow(boolean show){
		if(!show){
			bohaopan.setVisibility(View.GONE);
		}else{
			bohaopan.setVisibility(View.VISIBLE);
		}
	}
	private void showProcessDialog() {
		progressDialog = createProcessDialog(this);
		progressDialog.show();
		downCount = Count_Down_Max;
		countDownHandler.sendEmptyMessage(0);
	}
	private void dismissProcessDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	private ProgressDialog createProcessDialog(Context context) {
		
		ProgressDialog progressDialog = new ProgressDialog(context, R.style.dialog);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("请稍后");
		progressDialog.setMessage("\n正在建立通信  ..");
		progressDialog.setMax(Count_Down_Max);
		progressDialog.setProgressNumberFormat("剩余 %1d 秒");
		progressDialog.setCancelable(false);
		return progressDialog;
	}
	
}
