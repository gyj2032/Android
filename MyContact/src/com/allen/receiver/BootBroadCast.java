package com.allen.receiver;

import com.allen.uitl.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadCast extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Util.log("BootBroadCast onReceive");
	}

}