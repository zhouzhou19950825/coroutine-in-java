package com.upic.coroutine.start;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.upic.coroutine.UpicContinuationContext;
import com.upic.coroutine.pool.CommonPool;
import com.upic.coroutine.workthread.UploadTask;
import com.upic.workQueue.Constant;

/**
 * 开启一个协程
 * 
 * @author DTZ
 *
 */
public final class StartCoroutine {

	public static final void launch(CommonPool commonPool, UpicContinuationContext... context) {
		// 实现异步启动
		new Thread("CoroutineThread") {
			@Override
			public void run() {
				UploadTask start = new UploadTask(commonPool, null);
				for (UpicContinuationContext c : context) {
					UploadTask uploadTask = new UploadTask(commonPool, c);
					Constant.upWorkQueue.put(uploadTask);
				}
				CommonPool.JOINPOOL.invoke(start);
			}

		}.start();
	}

}