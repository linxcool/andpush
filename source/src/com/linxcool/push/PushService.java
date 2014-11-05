package com.linxcool.push;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.IBinder;

/**
 * 推送服务
 * @author 胡昌海(linxcool)
 */
public class PushService extends Service {

	public static final String ACTION_ALARM = "com.linxcool.push.alarm";
	public static final String ACTION_NOTIFICATION = "com.linxcool.push.notification";
	public static final int REQUEST_CODE = PushService.class.getName().hashCode();

	public static final String KEY_PACKAGE_NAME = "package_name";
	public static final String KEY_NOTIFY_ID = "notify_id";
	private static final String KEY_TICKER = "notify_ticker";
	private static final String KEY_TITLE = "notify_title";
	private static final String KEY_BODY = "notify_body";

	public static final String KEY_BROADCAST_TYPE = "broadcast_type";
	public static final int BROADCAST_TYPE_SHOWED_NOTIFY = 0;
	public static final int BROADCAST_TYPE_CLICKED_NOTIFY = 1;
	
	/**
	 * 注册重复通知
	 * @param context 上下文对象
	 * @param firstTime 第一次通知的时间（毫秒）
	 * @param intervalMillis 重复间隔（毫秒）
	 */
	public static void setRepeatingNotify(
			Context context, 
			long firstTime, long intervalMillis, 
			int id, String ticker, String title, String body){
		
		Intent service = new Intent(ACTION_ALARM);
		service.putExtra(KEY_PACKAGE_NAME, context.getPackageName());

		service.putExtra(KEY_NOTIFY_ID, id);
		service.putExtra(KEY_TICKER, ticker);
		service.putExtra(KEY_TITLE, title);
		service.putExtra(KEY_BODY, body);
		
		PendingIntent operation = PendingIntent.getService(
				context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, intervalMillis, operation);
	}
	
	/**
	 * 注册延时通知
	 * @param context 上下文对象
	 * @param delayMillis 延时间隔（毫秒）
	 */
	public static void setDelayNotify(Context context, long delayMillis, 
			int id, String ticker, String title, String body){
		
		Intent service = new Intent(ACTION_ALARM);
		service.putExtra(KEY_PACKAGE_NAME, context.getPackageName());
		
		service.putExtra(KEY_NOTIFY_ID, id);
		service.putExtra(KEY_TICKER, ticker);
		service.putExtra(KEY_TITLE, title);
		service.putExtra(KEY_BODY, body);
		PendingIntent operation = PendingIntent.getService(
				context, REQUEST_CODE, service, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, operation);
	}
	
	/**
	 * 取消通知
	 * @param context
	 * @param id
	 */
	public static void cancelNotify(Context context, int id){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(id);
	}
	
	/**
	 * 取消所有通知
	 * @param context
	 */
	public static void cancelAllNotify(Context context){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
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
		
		if(action == null){
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		
		if(ACTION_ALARM.equals(action)){
			showNotification(this, intent);
			sendBroadcast(this, intent, BROADCAST_TYPE_SHOWED_NOTIFY);
		} else if(ACTION_NOTIFICATION.equals(action)){
			sendBroadcast(this, intent, BROADCAST_TYPE_CLICKED_NOTIFY);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressWarnings("deprecation")
	protected void showNotification(Context context, Intent intent){
		int id = intent.getIntExtra(KEY_NOTIFY_ID, 0);
		String ticker = intent.getStringExtra(KEY_TICKER);
		String title = intent.getStringExtra(KEY_TITLE);
		String body = intent.getStringExtra(KEY_BODY);
		
		Notification notification = new Notification();
		notification.icon = getIconId(context, intent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.tickerText = ticker;
		
		intent.setAction(ACTION_NOTIFICATION);
		PendingIntent pending = PendingIntent.getService(context, REQUEST_CODE, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, title, body, pending);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(id, notification);
	}
	
	/**
	 * 返回默认图标的Id
	 * @param context 上下文对象
	 * @return
	 */
	protected int getIconId(Context context, Intent intent){
		try {
			context = context.createPackageContext(
					intent.getStringExtra(KEY_PACKAGE_NAME), Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		Resources res = context.getResources();
		String pkg = context.getPackageName();
		int icon = res.getIdentifier("ic_launcher", "drawable", pkg);
		if(icon == 0){
			icon = res.getIdentifier("icon", "drawable", pkg);
		}
		
		return icon;
	}
	
	/**
	 * 广播消息
	 * @param context
	 * @param id
	 * @param broadcastType
	 */
	protected void sendBroadcast(Context context, Intent intent, int broadcastType){
		intent.setAction(ACTION_NOTIFICATION);
		intent.putExtra(KEY_BROADCAST_TYPE, broadcastType);
		context.sendBroadcast(intent);
	}
}
