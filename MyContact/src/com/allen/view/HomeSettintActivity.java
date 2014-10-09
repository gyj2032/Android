package com.allen.view;

import java.io.File;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.allen.R;
import com.allen.application.MyApplication;
import com.allen.uitl.Const;
import com.allen.uitl.Util;

public class HomeSettintActivity extends Activity implements Preference.OnPreferenceClickListener, 
	OnPreferenceChangeListener {

	private LinearLayout oldPasswordLayout;
	private EditText oldPasswordText;
	private EditText newPasswordText;
	private EditText confirmPasswordText;
	

	private SharedPreferences sharedPreferences;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_setting_page);
		sharedPreferences = MyApplication.getApplication().getSharedPreferences();
		
		getFragmentManager().beginTransaction()
			.replace(R.id.setting_main, new PrefsFragement()).commit();
	}

	private void alertSetPasswordDialog() {
		View view = getLayoutInflater().inflate(R.layout.set_password, null);
		final String savePassword = sharedPreferences.getString(Const.SHARED_PASSWORD, null);

		oldPasswordLayout = (LinearLayout) view.findViewById(R.id.old_password_layout);
		oldPasswordText = (EditText) view.findViewById(R.id.old_password);
		newPasswordText = (EditText) view.findViewById(R.id.new_password);
		confirmPasswordText = (EditText) view.findViewById(R.id.new_password_confirm);
		if (savePassword == null) {
			oldPasswordLayout.setVisibility(View.GONE);
		} else {
			oldPasswordLayout.setVisibility(View.VISIBLE);
		}
		new AlertDialog.Builder(this).setTitle(R.string.set_password_title)
		.setView(view)
		.setNegativeButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String oldPassword = null;
				updateALertDialog(dialog, false);
				if (oldPasswordLayout.getVisibility() == View.VISIBLE) {
					oldPassword = oldPasswordText.getText().toString();
				}

				String newPassword = newPasswordText.getText().toString();
				String confirmPassword = confirmPasswordText.getText().toString();
				
				if (savePassword == null) {
					if (updatePassword(newPassword, confirmPassword)) {
						setPasswordPreference.setSummary("已设置");
						updateALertDialog(dialog, true);
					} else {
						setPasswordPreference.setSummary("未设置");
					}
				} else {
					if (Util.equals(savePassword, oldPassword)) {
						if (updatePassword(newPassword, confirmPassword)) {
							setPasswordPreference.setSummary("已设置");
							updateALertDialog(dialog, true);
						}
					} else {
						Util.showToast(getContext(), "原密码错误 !");
					}
				}
			}
		})
		.setPositiveButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateALertDialog(dialog, true);
			}
		})
		.show();

	}
	private boolean updatePassword(String newPassword, String confirmPassword) {
		if (Util.equals(newPassword, confirmPassword)) {
			sharedPreferences.edit().putString(Const.SHARED_PASSWORD, newPassword).commit();
			Util.showToast(getContext(), "修改成功 !");
			return true;
		} else {
			Util.showToast(getContext(), "密码不一致 !");
			return false;
		}
	}
	private Context getContext() {
		return this;
	}
	private void updateALertDialog(DialogInterface dialog, boolean isDismiss) {
		try {  
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
            field.setAccessible( true);  
            field.set(dialog, isDismiss);
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	private Preference setPasswordPreference;
	private Preference openRecordPreference;
	private Preference waitingTimePreference;
	private SwitchPreference switchRecordPreference;
	
	private String[] waitTimeEntries;
	private String[] waitTimeValues;
	public class PrefsFragement extends PreferenceFragment{
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            addPreferencesFromResource(R.xml.preference);
            setPasswordPreference = findPreference("set_password");
            openRecordPreference = findPreference("open_record_dir");
            switchRecordPreference = (SwitchPreference) findPreference("switch_record_function");
            waitingTimePreference = findPreference("wait_time_preference");
            
            setPasswordPreference.setOnPreferenceClickListener(HomeSettintActivity.this);
            openRecordPreference.setOnPreferenceClickListener(HomeSettintActivity.this);
//            switchRecordPreference.setOnPreferenceClickListener(HomeSettintActivity.this);
            switchRecordPreference.setOnPreferenceChangeListener(HomeSettintActivity.this);
            waitingTimePreference.setOnPreferenceClickListener(HomeSettintActivity.this);
            
            waitTimeEntries = getResources().getStringArray(R.array.wait_entries);
            waitTimeValues = getResources().getStringArray(R.array.wait_values);
            
            boolean isChecked = MyApplication.getApplication().getSharedPreferences().getBoolean(Const.SHARED_RECORD_STATE, false);
            switchRecordPreference.setChecked(isChecked);
            String savePassword = sharedPreferences.getString(Const.SHARED_PASSWORD, null);
            if (savePassword != null) {
            	setPasswordPreference.setSummary("已设置");
            }
            int waitTimeIndex = sharedPreferences.getInt(Const.SHARED_WAIT_TIME_INDEX, -1);
            if (waitTimeIndex != -1) {
                waitingTimePreference.setSummary(waitTimeEntries[waitTimeIndex]);
            }
            
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
        		Bundle savedInstanceState) {
        	View v = super.onCreateView(inflater, container, savedInstanceState);
            if(v != null) {
                ListView lv = (ListView) v.findViewById(android.R.id.list);
                lv.setPadding(0, 0, 0, 0);
                lv.setDivider(new ColorDrawable(getResources().getColor(R.color.calllog_division)));
                lv.setDividerHeight(2);
            }
            return v;
        }
    }
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == setPasswordPreference) {
			alertSetPasswordDialog();
		} else if (preference == openRecordPreference) {
			try {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(
						"com.estrongs.android.pop",
						"com.estrongs.android.pop.view.FileExplorerActivity"));
				//			intent = PhotoUtil.getLaunchIntent(this, "文件管家");
				intent.setData(Uri.fromFile(new File(Const.RECORD_DIR)));
				startActivity(intent);
			} catch (Exception e) {
				Util.showToast(getApplicationContext(), e.getMessage());
			}
		} else if (preference == waitingTimePreference) {
			Util.showListDialog(this, "超时时间", waitTimeEntries, dialogListener);
		}
		return false;
	}
	private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			waitingTimePreference.setSummary(waitTimeEntries[which]);
			Editor editor = MyApplication.getApplication().getSharedPreferences().edit();
			editor.putInt(Const.SHARED_WAIT_TIME, Integer.valueOf(waitTimeValues[which]))
				.putInt(Const.SHARED_WAIT_TIME_INDEX, which)
				.commit();
		}
	};

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		boolean isChecked = (Boolean) newValue;
		MyApplication.getApplication().getRecordService().switchRecrodState(isChecked);
		return true;
	}

}
