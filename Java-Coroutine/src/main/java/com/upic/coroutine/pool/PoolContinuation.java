package com.upic.coroutine.pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import com.upic.coroutine.UpicContinuation;
import com.upic.coroutine.UpicContinuationContext;
/**
 * 回调处理
 * @author DTZ
 *
 */
class PoolUpicContinuation implements UpicContinuation {

	private ForkJoinPool pool;
	private UpicContinuation upicContinuation;
	@Override
	public void resume(Object value) {
		if (isPoolThread()) {
			upicContinuation.resume(value);
		} else {
			pool.execute((Runnable) new Runnable() {
				@Override
				public void run() {
					getUpicContinuation().resume(value);
					System.out.println(value);
				}

			});
		}
	}
	@Override
	public void resumeWithException(Throwable exception) {
		if (isPoolThread()) {
			upicContinuation.resumeWithException(exception);
		} else {
			pool.execute((Runnable) new Runnable() {
				@Override
				public void run() {
					getUpicContinuation().resumeWithException(exception);
				}
			});
		}
	}

	public boolean isPoolThread() {
		Thread thread = Thread.currentThread();
		ForkJoinWorkerThread forkjoinworkerthread = (ForkJoinWorkerThread) ((thread instanceof ForkJoinWorkerThread)
				? thread : null);
		return forkjoinworkerthread == null ? false : true;
	}

	public ForkJoinPool getPool() {
		return pool;
	}

	public UpicContinuation getUpicContinuation() {
		return upicContinuation;
	}

	public PoolUpicContinuation(ForkJoinPool pool, UpicContinuation UpicContinuation) {
		super();
		this.pool = pool;
		this.upicContinuation = UpicContinuation;
	}

	public UpicContinuationContext getContext() {
		return upicContinuation.getContext();
	}
}