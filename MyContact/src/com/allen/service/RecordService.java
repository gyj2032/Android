package com.allen.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.allen.application.MyApplication;
import com.allen.uitl.AESHelper;
import com.allen.uitl.Const;
import com.allen.uitl.Util;

public class RecordService extends Service {

	private TelephonyManager manager;
	private PhoneStateListener listener;
	private MediaRecorder recorder = null;

	protected String tmpPath;
	
	private boolean recordState;
	private boolean isRecording;
	
	private int phoneState;

	public void onCreate() {
		super.onCreate();
		manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		Util.initDirs(Const.RECORD_DIR);
		
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			phoneState = state;
			recordState = MyApplication.getApplication().getSharedPreferences().getBoolean(Const.SHARED_RECORD_STATE, false);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if (recordState) {
					stopRecord();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (recordState) {
					startRecord();
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Util.showToast(getApplicationContext(), "正在响铃");
				break;
			default:
				break;
			}
		}
	}
	
	public void startRecord() {
		try {
			if (!isRecording && recorder == null) {
				recorder = new MediaRecorder();
	//			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	//			recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
	//			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	//			recorder.setOutputFile(File.createTempFile("record_", ".amr").getAbsolutePath());
//				File file = new File(Const.RECORD_DIR, Util.getStoreFileName() + ".3GP");
//				recorder.setOutputFile(file.getAbsolutePath());
				tmpPath = Util.createTempFile("audio", "");
				recorder.setOutputFile(tmpPath);
				recorder.prepare();
				recorder.start();
				
				isRecording = true;
				Util.showToast(getApplicationContext(), "开始录音");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void stopRecord() {
		if (isRecording && recorder != null) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
			isRecording = false;
			String outputFile = Const.RECORD_DIR + Util.getStoreFileName() + ".3GP";
			String password = MyApplication.getApplication().getSharedPreferences().getString(Const.SHARED_PASSWORD, "");
			AESHelper.encrypt(tmpPath, outputFile, AESHelper.updatePassword(password));
			
			Util.deleteFile(tmpPath);
			Util.showToast(getApplicationContext(), "录音停止");
		}
	}
	public void switchRecrodState(boolean isChecked) {
		if (isChecked) {
			if (!isRecording && phoneState == TelephonyManager.CALL_STATE_OFFHOOK) {
				startRecord();
			}
		} else {
			if (isRecording) {
				stopRecord();
			}
		}
		
		this.recordState = isChecked;
		SharedPreferences.Editor editor = MyApplication.getApplication().getSharedPreferences().edit();
		editor.putBoolean(Const.SHARED_RECORD_STATE, recordState);
		editor.commit();
	}
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}
	public class MyBinder extends Binder {
		public RecordService getService() {
			return RecordService.this;
		}
	}
}