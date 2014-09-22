package com.linxcool.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent == null){
			return;
		}
		
		String action = intent.getAction();
		
		if(action == null){
			return;
		}
		
		if(PushService.ACTION_NOTIFICATION.equals(action)){
			int type = intent.getIntExtra(PushService.KEY_BROADCAST_TYPE, 0);
			int id = intent.getIntExtra(PushService.KEY_NOTIFY_ID, 0);
			switch (type) {
			case PushService.BROADCAST_TYPE_SHOWED_NOTIFY:
				System.out.println("notification showed " + id);
				break;
			case PushService.BROADCAST_TYPE_CLICKED_NOTIFY:
				SimplePushHelper.startLauncherActivity(context);
				System.out.println("notification clicked " + id);
				break;
			default:
				break;
			}
		}
	}

}
