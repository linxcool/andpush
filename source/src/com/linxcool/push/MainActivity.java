package com.linxcool.push;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			PushService.setDelayNotify(this, 3000, "临克斯提醒", "精彩好礼送不停，快来签到吧！");
		}
		return super.onTouchEvent(event);
	}
	

}
