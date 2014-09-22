package com.linxcool.push;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends Activity implements PushCallback{

	private int notifyId = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		PushService.registPushCallback(notifyId, this);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			PushService.setDelayNotify(
					this, 2000, notifyId, 
					"您有一条新的通知，请注意查收！",
					"临克斯提醒", 
					"精彩好礼送不停，快来签到吧！");
			System.out.println("setDelayNotify");
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onNotificationShowed(int id) {
		System.out.println("onNotificationShowed");
	}

	@Override
	public void onNotificationClicked(int id) {
		System.out.println("onNotificationClicked");
	}


}
