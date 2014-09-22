package com.linxcool.push;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

/**
 * 推送服务
 * @author 胡昌海(linxcool)
 */
public class PushService extends Service {

	public static final String ACTION_ALARM = "com.linxcool.push.alarm";
	public static final String ACTION_NOTIFICATION = "com.linxcool.push.notification";
	public static final int REQUEST_CODE = PushService.class.getName().hashCode();

	private static final String DATA_FILE_NAME = "pushService";
	private static final String KEY_NOTIFY_ID = "notify_id";
	private static final String KEY_TICKER = "notify_ticker";
	private static final String KEY_TITLE = "notify_title";
	private static final String KEY_BODY = "notify_body";

	private static SparseArray<PushCallback> callbacks;
	
	/**
	 * 注册push回调
	 * @param notifyId
	 * @param callback
	 */
	public static void registPushCallback(int notifyId, PushCallback callback){
		if(callbacks == null){
			callbacks = new SparseArray<PushCallback>();
		}
		callbacks.put(notifyId, callback);
	}
	
	/**
	 * 取消push回调
	 * @param notifyId
	 */
	public static void unregistPushCallback(int notifyId){
		if(callbacks == null){
			return;
		}
		callbacks.remove(notifyId);
	}
	
	private static PushCallback getPushCallback(int notifyId){
		if(callbacks == null){
			return null;
		}
		return callbacks.get(notifyId);
	}
	
	/**
	 * 注册重复通知
	 * @param context 上下文对象
	 * @param intervalMillis 重复间隔（毫秒）
	 */
	public static void setRepeatingNotify(Context context, long intervalMillis, 
			int id, String ticker, String title, String body){
		saveData(context, id, KEY_TICKER, ticker);
		saveData(context, id, KEY_TITLE, title);
		saveData(context, id, KEY_BODY, body);
		
		Intent service = new Intent(ACTION_ALARM);
		service.putExtra(KEY_NOTIFY_ID, id);
		PendingIntent operation = PendingIntent.getService(context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, operation);
	}
	
	/**
	 * 注册延时通知
	 * @param context 上下文对象
	 * @param delayMillis 延时间隔（毫秒）
	 */
	public static void setDelayNotify(Context context, long delayMillis, 
			int id, String ticker, String title, String body){
		saveData(context, id, KEY_TICKER, ticker);
		saveData(context, id, KEY_TITLE, title);
		saveData(context, id, KEY_BODY, body);
		
		Intent service = new Intent(ACTION_ALARM);
		service.putExtra(KEY_NOTIFY_ID, id);
		PendingIntent operation = PendingIntent.getService(context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, operation);
	}
	
	/**
	 * 保存数据导私有目录
	 * @param context
	 * @param id
	 * @param key
	 * @param value
	 */
	private static void saveData(Context context, int id, String key, String value){
		if(context == null || key == null || value == null){
			Log.e(DATA_FILE_NAME, "save data fial as args is null");
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(
				DATA_FILE_NAME, Context.MODE_PRIVATE);
		preferences.edit().putString(id + "_" + key, value).commit();
	}
	
	/**
	 * 从私有目录获取数据
	 * @param context
	 * @param id
	 * @param key
	 * @return
	 */
	private static String getData(Context context, int id, String key){
		SharedPreferences preferences = context.getSharedPreferences(
				DATA_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(id + "_" + key, null);
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
		
		int id = intent.getIntExtra(KEY_NOTIFY_ID, 0);
		PushCallback callback = getPushCallback(id);
		
		if(ACTION_ALARM.equals(action)){
			String ticker = getData(this, id, KEY_TICKER);
			String title = getData(this, id, KEY_TITLE);
			String body = getData(this, id, KEY_BODY);
			showNotification(this, id, ticker, title, body);
			if(callback != null) callback.onNotificationShowed(id);
		} else if(ACTION_NOTIFICATION.equals(action)){
			if(callback != null) callback.onNotificationClicked(id);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressWarnings("deprecation")
	protected void showNotification(Context context, int id, String ticker, String title, String body){
		Notification notification = new Notification();
		notification.icon = getIconId(context);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.tickerText = ticker;
		Intent service = new Intent(ACTION_NOTIFICATION);
		service.putExtra(KEY_NOTIFY_ID, id);
		PendingIntent pending = PendingIntent.getService(context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, title, body, pending);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(id, notification);
	}
	
	/**
	 * 返回默认图标的ID
	 * @param context 上下文对象
	 * @return
	 */
	protected int getIconId(Context context){
		Resources res = context.getResources();
		String pkg = context.getPackageName();
		int icon = res.getIdentifier("ic_launcher", "drawable", pkg);
		if(icon == 0){
			icon = res.getIdentifier("icon", "drawable", pkg);
		}
		return icon;
	}
}
