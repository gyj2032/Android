package com.allen.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.allen.R;
import com.allen.application.MyApplication;
import com.allen.uitl.AESHelper;
import com.allen.uitl.Const;
import com.allen.uitl.Util;

public class FileAssociatedActivity extends Activity implements OnClickListener {

	private String playPath;
	private String sourcePath;
	private int requestCode = 0;
	private String type = "audio/*";
	private View mainLayout;
	
	private EditText passwordEditText;
	private Button okButton;
	private Button cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_activity_main);
		
		sourcePath = getIntent().getDataString().substring(7);
		String prefix = "file://";
		if (sourcePath.startsWith(prefix)) {
			sourcePath = sourcePath.substring(prefix.length());
		}

		
		initView();

		super.onCreate(savedInstanceState);
	}
	private void initView() {
		mainLayout = findViewById(R.id.dialog_activity_main);
		passwordEditText = (EditText) findViewById(R.id.input_password);
		okButton = (Button) findViewById(R.id.button_ok);
		cancelButton = (Button) findViewById(R.id.button_cancel);
		
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Util.deleteFile(playPath);
		finish();
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onClick(View v) {
		String password = MyApplication.getApplication().getSharedPreferences().getString(Const.SHARED_PASSWORD, "");
		switch (v.getId()) {
		case R.id.button_ok:
			String inputPwd = passwordEditText.getText().toString();
			if (Util.equals(password, inputPwd)) {
				play(inputPwd);
			} else {
				Util.showToast(this, "密码错误");
			}
			break;
		case R.id.button_cancel:
			finish();
			break;
		}
	}
	private void play(String password) {
		playPath = Util.createTempFile("audio", "");

		AESHelper.decrypt(sourcePath, playPath, AESHelper.updatePassword(password));
		
		Uri uri = Uri.parse("file://" + playPath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, type);
		startActivityForResult(intent, requestCode);
//		mainLayout.setVisibility(View.INVISIBLE);
	}
}
