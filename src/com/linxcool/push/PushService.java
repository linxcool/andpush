package com.linxcool.push;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * 推送服务
 * @author 胡昌海(linxcool)
 */
public class PushService extends Service {

	public static final String ACTION_ALARM = "com.linxcool.push.alarm";
	public static final String ACTION_NOTIFICATION = "com.linxcool.push.notification";

	private static final int REQUEST_CODE = 0;
	
	private static final String KEY_NOTIFY_TYPE = "push_notify_type";
	private static final int NOTIFY_TYPE_REPEATING = 0;
	private static final int NOTIFY_TYPE_DELAY = 1;
	
	private static final String DATA_FILE_NAME = "pushService";
	private static final String KEY_TITLE_REPEATING = "push_title_repeating";
	private static final String KEY_BODY_REPEATING = "push_body_repeating";
	private static final String KEY_TITLE_DELAY = "push_title_delay";
	private static final String KEY_BODY_DELAY = "push_body_delay";
	
	/**
	 * 注册重复通知
	 * @param context 上下文对象
	 * @param intervalMillis 重复间隔（毫秒）
	 */
	public static void setRepeatingNotify(Context context, long intervalMillis, String title, String body){
		saveData(context, KEY_TITLE_REPEATING, title);
		saveData(context, KEY_BODY_REPEATING, body);
		
		Intent service = new Intent(ACTION_ALARM);
		service.putExtra(KEY_NOTIFY_TYPE, NOTIFY_TYPE_REPEATING);
		PendingIntent operation = PendingIntent.getService(context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, operation);
	}
	
	/**
	 * 注册延时通知
	 * @param context 上下文对象
	 * @param delayMillis 延时间隔（毫秒）
	 */
	public static void setDelayNotify(Context context, long delayMillis, String title, String body){
		saveData(context, KEY_TITLE_DELAY, title);
		saveData(context, KEY_BODY_DELAY, body);
		
		Intent service = new Intent(ACTION_ALARM);
		service.putExtra(KEY_NOTIFY_TYPE, NOTIFY_TYPE_DELAY);
		PendingIntent operation = PendingIntent.getService(context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, operation);
	}
	
	/**
	 * 保存数据导私有目录
	 * @param context
	 * @param key
	 * @param value
	 */
	private static void saveData(Context context, String key, String value){
		if(context == null || key == null || value == null){
			Log.e(DATA_FILE_NAME, "save data fial as args is null");
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(
				DATA_FILE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}
	
	private static String getData(Context context, String key){
		SharedPreferences preferences = context.getSharedPreferences(
				DATA_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, null);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null){
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		
		String action = intent.getAction();
		
		if(intent.getAction() == null){
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		
		if(ACTION_ALARM.equals(action)){
			int type = intent.getIntExtra(KEY_NOTIFY_TYPE, NOTIFY_TYPE_DELAY);
			String title = getData(this, type == NOTIFY_TYPE_DELAY ? KEY_TITLE_DELAY : KEY_TITLE_REPEATING);
			String body = getData(this, type == NOTIFY_TYPE_DELAY ? KEY_BODY_DELAY : KEY_BODY_REPEATING);
			showNotification(this, title, body);
		} else if(ACTION_NOTIFICATION.equals(action)){
			
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressWarnings("deprecation")
	protected void showNotification(Context context, String title, String body){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		Intent service = new Intent(ACTION_NOTIFICATION);
		PendingIntent pending = PendingIntent.getService(context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, title, body, pending);
		nm.notify(context.getPackageName().hashCode(), notification);
	}
	
}
