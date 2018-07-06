package org.com.cay.mmall.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Caychen on 2018/7/6.
 */
public class TaskUtil {

	private static ConcurrentHashMap<Long, Future> futureMap = new ConcurrentHashMap<>();

	private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

	public static ConcurrentHashMap<Long, Future> getFutureMap(){
		return futureMap;
	}

	public static ScheduledExecutorService getExecutorService(){
		return executorService;
	}
}
