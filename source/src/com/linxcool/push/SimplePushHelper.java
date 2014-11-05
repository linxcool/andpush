package com.linxcool.push;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class SimplePushHelper {

	/**
	 * 生成通知Id
	 * @param ticker
	 * @param title
	 * @param body
	 * @return
	 */
	private static int generateId(Context context, String ticker, String title, String body){
		final int prime = 31;
		int result = 1;
		result = prime * result + context.getPackageName().hashCode();
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/**
	 * 启动主Activity
	 * @param context
	 */
	public static void startLauncherActivity(Context context){
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
		context.startActivity(intent);
	}

	/**
	 * 返回在指定时间点的时间戳（忽略日期的下一个时间重复点）
	 * @param hour 时
	 * @param minute 分
	 * @param second 秒
	 * @return
	 */
	public static long getTargetTimeMillis(int hour, int minute, int second){
		long systemTime = System.currentTimeMillis();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(systemTime);

		// 这里时区需要设置一下，不然会有8个小时的时间差
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);

		// 选择的定时时间  
		long targetTime = calendar.getTimeInMillis();
		// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
		if(systemTime > targetTime) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			targetTime = calendar.getTimeInMillis();
		}
		return targetTime;
	}
	
	/**
	 * 返回在指定时间点的时间戳（忽略日期的下一个时间重复点）
	 * @param sampleTime 样例时间戳
	 * @return
	 */
	public static long getTargetTimeMillis(long sampleTime){
		Calendar calendar = Calendar.getInstance();  
		calendar.setTimeInMillis(sampleTime);
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		
		return getTargetTimeMillis(hour, minute, second);
	}
	
	/**
	 * 于指定时间点通知一次
	 * @param context 上下文对象
	 * @param sampleTime 指定时间（时间戳）
	 * @param ticker 提示
	 * @param title 标题
	 * @param body 内容
	 */
	public static void setOneTimeNotify(final Context context, long sampleTime,
			String ticker, String title, String body){
		long targetTimeMillis = getTargetTimeMillis(sampleTime);
		int notifyId = generateId(context, ticker, title, body);
		long delayMillis = targetTimeMillis - System.currentTimeMillis();
		
		PushService.setDelayNotify(context, delayMillis, notifyId, ticker, title, body);
	}
	
	/**
	 * 每日通知
	 * @param context
	 * @param sampleTime
	 * @param ticker
	 * @param title
	 * @param body
	 */
	public static void setEverydayNotify(Context context, 
			long sampleTime, String ticker, String title, String body){
		setRepeatingNotify(context, sampleTime, 24 * 3600 * 1000, ticker, title, body);
	}
	
	/**
	 * 每周通知
	 * @param context
	 * @param sampleTime
	 * @param ticker
	 * @param title
	 * @param body
	 */
	public static void setEveryweekNotify(Context context, 
			long sampleTime, String ticker, String title, String body){
		setRepeatingNotify(context, sampleTime, 7 * 24 * 3600 * 1000, ticker, title, body);
	}
	
	/**
	 * 于指定时间点重复通知
	 * @param context
	 * @param sampleTime
	 * @param intervalMillis
	 * @param ticker
	 * @param title
	 * @param body
	 */
	public static void setRepeatingNotify(final Context context, 
			long sampleTime, long intervalMillis, 
			String ticker, String title, String body){
		
		long targetTimeMillis = getTargetTimeMillis(sampleTime);
		
		int notifyId = generateId(context, ticker, title, body) + Long.valueOf(intervalMillis).intValue();

		PushService.setRepeatingNotify(context, targetTimeMillis, intervalMillis, notifyId, ticker, title, body);
		
	}


}
