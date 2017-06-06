package com.upic.coroutine.start;

import com.upic.coroutine.UpicContinuation;
import com.upic.coroutine.UpicContinuationContext;
/**
 * 启动类
 * @author DTZ
 *
 */
public class StandaloneCoroutine implements UpicContinuation {

	private UpicContinuationContext context;
	
	@Override
	public void resume(Object value) {
//		System.out.println(Thread.currentThread().getName()+"已消费："+value);
	}
	@Override
	public void resumeWithException(Throwable exception) {
		System.out.println("出错啦！！！！");
		Thread currentThread = Thread.currentThread();
		currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, exception);
	}

	public UpicContinuationContext getContext() {
		return context;
	}

	public StandaloneCoroutine(UpicContinuationContext context) {
		super();
		this.context = context;
	}
}