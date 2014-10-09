package com.allen.view.sms;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.allen.R;
import com.allen.bean.ContactBean;
import com.allen.bean.MessageBean;
import com.allen.uitl.BaseIntentUtil;
import com.allen.uitl.Const;
import com.allen.uitl.PhoneUtil;
import com.allen.uitl.Util;
import com.allen.view.adapter.MessageBoxListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NewSMSActivity extends SmsBaseActivity {

	private ImageButton backButton;
	private Button addButton;
	private Button sendButton;

	private EditText inputPhoneNum;
	private EditText msgEditText;
	private LinearLayout phoneNumLayout;
	private ListView talkListView;
	
	private MessageBoxListAdapter adapter;

	private String[] chars = new String[] {" ", ","};
	private MyAsyncQueryHandler asyncQuery;
	private List<ContactBean> selectedContacts;
	
	private List<MessageBean> messageLists;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_sms);

		initView();
	
		if (null != getIntent().getStringExtra("list")) {
			String data = getIntent().getStringExtra("list");
			Gson gson = new Gson();
			Type listRet = new TypeToken<List<ContactBean>>() {
			}.getType();
			selectedContacts = gson.fromJson(data, listRet);
			for (ContactBean cb : selectedContacts) {
				createView2(cb.getDisplayName().trim());
				final View child = phoneNumLayout.getChildAt(phoneNumLayout.getChildCount() - 1);
				autoHeight(child);
			}
		}

	}

	private void initView() {
		msgEditText = (EditText) findViewById(R.id.edit_msg_text);
		talkListView = (ListView) findViewById(R.id.talk_list);
		backButton = (ImageButton) findViewById(R.id.btn_back);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				NewSMSActivity.this.finish();
			}
		});
		addButton = (Button) findViewById(R.id.btn_add);
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (null == inputPhoneNum || "".equals(inputPhoneNum.getText().toString())) {
				} else {
					String phoneNum = inputPhoneNum.getText().toString();
					if (isNum(inputPhoneNum.getText().toString().trim())) {
						createView1(phoneNum, phoneNum);
						inputPhoneNum.setText("");
					} else {
						inputPhoneNum.setText("");
					}
				}

				if (null == selectedContacts || selectedContacts.size() < 1) {
					BaseIntentUtil.intentSysDefault(NewSMSActivity.this,
							SelectContactsToSendActivity.class, null);
				} else {
					Gson gson = new Gson();
					String data = gson.toJson(selectedContacts);
					Map<String, String> map = new HashMap<String, String>();
					map.put("data", data);
					BaseIntentUtil.intentSysDefault(NewSMSActivity.this,
							SelectContactsToSendActivity.class, map);
				}
			}
		});

		sendButton = (Button) findViewById(R.id.btn_send);
		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (null == inputPhoneNum || "".equals(inputPhoneNum.getText().toString())) {
				} else {
					String phoneNum = inputPhoneNum.getText().toString();
					if (isNum(inputPhoneNum.getText().toString().trim())) {
						createView1(phoneNum, phoneNum);
						inputPhoneNum.setText("");
					} else {
						inputPhoneNum.setText("");
					}
				}

				if (selectedContacts != null) {
					for (ContactBean cb : selectedContacts) {
						String name = cb.getDisplayName();
						String address = cb.getPhoneNum();
						String content = msgEditText.getText().toString();
//						Util.showToast(getApplicationContext(), "name:" + name + ", number:" + address
//								+ ", text:" + content);
						
//						PhoneUtil.insertMessage(getApplicationContext(), address, content);
//						PhoneUtil.receiveMessage(getApplicationContext(), address, content+"  [receive]");
					}
					if (selectedContacts.size() > 0) {
						asyncQuery = new MyAsyncQueryHandler(getContentResolver());
						asyncQuery.query(selectedContacts.get(0).getPhoneNum());
					}
					msgEditText.setText("");
				} else {
					Util.showToast(NewSMSActivity.this, "请输入发送目标");
				}
				
			}
		});

		initMyGroupView();
	}

	private void initMyGroupView() {
		phoneNumLayout = (LinearLayout) findViewById(R.id.l1);
		inputPhoneNum = (EditText) findViewById(R.id.edit_phone_num);
		inputPhoneNum.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (isNum(s.toString())) {
					if (s.length() >= 1) {
						boolean bool = false;
						// length() == 15直接生成按钮
						if (s.length() == 15) {
							bool = true;
						}
						// 字数没有满足15个验证是否有空格
						if (!bool) {
							String c = s.toString().substring(start,
									start + count);
							for (int i = 0; i < chars.length; i++) {
								if (chars[i].equals(c)) {
									bool = true;
									break;
								}
							}
						}
						// bool == true 生成Button
						if (bool) {
							createView1(s.toString(), s.toString());
							inputPhoneNum.setText("");
						}
						// 检测输入框数据是否已经换行
						final View child = phoneNumLayout
								.getChildAt(phoneNumLayout.getChildCount() - 1);
						autoHeight(child);
					}
				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		inputPhoneNum.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (isNum(inputPhoneNum.getText().toString().trim())) {
						createView1(inputPhoneNum.getText().toString().trim(),
								inputPhoneNum.getText().toString().trim());
						inputPhoneNum.setText("");
					} else {
						inputPhoneNum.setText("");
//						queryListView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}

	/**
	 * 为MyViewGroup自动计算高度
	 * 
	 * @param child
	 */
	private void autoHeight(final View child) {
		if (child != null) {
			new Handler() {
			}.postDelayed(new Runnable() {
				public void run() {
					if (child.getBottom() > phoneNumLayout.getBottom()
							|| phoneNumLayout.getBottom() - child.getBottom() >= child.getHeight()) {
						LayoutParams l = phoneNumLayout.getLayoutParams();
						l.height = child.getBottom();
						phoneNumLayout.setLayoutParams(l);
					}
				}
			}, 500);
		}
	}

	/**
	 * 生成MyViewGroup的子元素
	 * 
	 * @param text
	 */
	private void createView1(String text, String number) {

		if (inputPhoneNum.getText().toString().equals(" ")
				|| inputPhoneNum.getText().toString().equals("")) {
		} else {
			TextView t = new TextView(this);
			t.setText(text);
			t.setTextColor(Color.BLACK);
			t.setGravity(Gravity.CENTER);
			t.setBackgroundResource(R.drawable.bg_sms_contact_btn);
			t.setHeight(60);
			t.setPadding(2, 0, 2, 0);
			t.setOnClickListener(new MyListener());
			t.setTag(number);
			phoneNumLayout.addView(t, phoneNumLayout.getChildCount() - 1);

			ContactBean cb = new ContactBean();
			cb.setDisplayName(text);
			cb.setPhoneNum(number);
			if (null == selectedContacts) {
				selectedContacts = new ArrayList<ContactBean>();
			}
			selectedContacts.add(cb);
//			queryListView.setVisibility(View.INVISIBLE);
		}
	}

	private void createView2(String text) {

		TextView t = new TextView(this);
		t.setText(text);
		t.setTextColor(Color.BLACK);
		t.setGravity(Gravity.CENTER);
		t.setHeight(60);
		t.setPadding(2, 0, 2, 0);
		t.setBackgroundResource(R.drawable.bg_sms_contact_btn);
		t.setOnClickListener(new MyListener());
		t.setTag(text);
		phoneNumLayout.addView(t, phoneNumLayout.getChildCount() - 1);
	}

	/**
	 * MyViewGroup子元素的事件
	 * 
	 * @author LDM
	 */
	private class MyListener implements OnClickListener {
		public void onClick(View v) {
			phoneNumLayout.removeView(v);
			String number = (String) v.getTag();
			for (ContactBean cb : selectedContacts) {
				if (cb.getPhoneNum().equals(number)) {
					selectedContacts.remove(cb);
					break;
				}
			}
			autoHeight(phoneNumLayout.getChildAt(phoneNumLayout.getChildCount() - 1));
		}
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		public void query(String address) {
			messageLists = new ArrayList<MessageBean>();
			Uri uri = Uri.parse("content://sms");
			String[] projection = new String[] { "date", "address", "person", "body", "type" };// 查询的列
			asyncQuery.startQuery(0, null, uri, projection, "address = "
					+ address, null, "date asc");
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
						messageLists.add(d);
					} else { 
						MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_me_item);
						messageLists.add(d);
					}
				}
				if (messageLists.size() > 0) {
					adapter = new MessageBoxListAdapter(NewSMSActivity.this, messageLists);
					talkListView.setAdapter(adapter);
					talkListView.setDivider(null);
					talkListView.setSelection(messageLists.size());
				}
			}
		}
	}
//	/**
//	 * 数据库异步查询类AsyncQueryHandler
//	 * 
//	 * @author administrator
//	 * 
//	 */
//	private class MyAsyncQueryHandler extends AsyncQueryHandler {
//
//		public MyAsyncQueryHandler(ContentResolver cr) {
//			super(cr);
//		}
//		protected void query() {
//			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
//			String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
//					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//					ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
//					ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//					ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
//					ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY }; // 查询的列
//			asyncQuery.startQuery(0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
//		}
//		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//			if (cursor != null && cursor.getCount() > 0) {
//
//				list = new ArrayList<ContactBean>();
//				cursor.moveToFirst();
//				for (int i = 0; i < cursor.getCount(); i++) {
//					cursor.moveToPosition(i);
//					String name = cursor.getString(1);
//					String number = cursor.getString(2);
//					String sortKey = cursor.getString(3);
//					int contactId = cursor.getInt(4);
//					Long photoId = cursor.getLong(5);
//					String lookUpKey = cursor.getString(6);
//
//					ContactBean cb = new ContactBean();
//					cb.setDisplayName(name);
//					cb.setPhoneNum(number);
//					cb.setSortKey(sortKey);
//					cb.setContactId(contactId);
//					cb.setPhotoId(photoId);
//					cb.setLookUpKey(lookUpKey);
//
//					list.add(cb);
//				}
//				if (list.size() > 0) {
//					setAdapter(list);
//				}
//			}
//		}
//	}
//
//	private void setAdapter(List<ContactBean> list) {
//		adapter = new NewSmsAdapter(this);
//		adapter.assignment(list);
//		queryListView.setAdapter(adapter);
//		queryListView.setTextFilterEnabled(true);
//		queryListView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				ContactBean cb = (ContactBean) adapter.getItem(position);
//				boolean b = true;
//				if (null == selectedContacts || selectedContacts.size() < 1) {
//				} else {
//					for (ContactBean cbean : selectedContacts) {
//						if (cbean.getPhoneNum().equals(cb.getPhoneNum())) {
//							b = false;
//							break;
//						}
//					}
//				}
//				if (b) {
//					inputPhoneNum.setText(cb.getDisplayName());
//					createView1(inputPhoneNum.getText().toString().trim(),
//							cb.getPhoneNum());
//					inputPhoneNum.setText("");
//				} else {
//					queryListView.setVisibility(View.INVISIBLE);
//					inputPhoneNum.setText("");
//				}
//			}
//		});
//		queryListView.setOnScrollListener(new OnScrollListener() {
//
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
//							.hideSoftInputFromWindow(NewSMSActivity.this
//									.getCurrentFocus().getWindowToken(),
//									InputMethodManager.HIDE_NOT_ALWAYS);
//				}
//			}
//
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//			}
//		});
//	}

	private boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

}
