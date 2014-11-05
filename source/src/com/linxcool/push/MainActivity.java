package com.linxcool.push;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends Activity{

	public int notifyId = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			
			String ticker = "您有一条新的通知，请注意查收！";
			String title = "临克斯提醒";
			String body = "精彩好礼送不停，快来签到吧！";
			
			long sampleTime = System.currentTimeMillis() + 2000;
			
			//SimplePushHelper.setRepeatingNotify(this, sampleTime, 5000, ticker, title, body);
			SimplePushHelper.setOneTimeNotify(this, sampleTime, ticker, title, body);
			
			System.out.println("setRepeatingNotify");
			
		}
		return super.onTouchEvent(event);
	}

}
