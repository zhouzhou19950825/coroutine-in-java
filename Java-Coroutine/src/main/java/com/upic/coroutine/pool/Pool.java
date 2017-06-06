package com.upic.coroutine.pool;

import java.util.concurrent.ForkJoinPool;

import com.upic.coroutine.AbstractUpicContinuationContext;
import com.upic.coroutine.UpicContinuation;
import com.upic.coroutine.UpicContinuationInterceptor;
/**
 * 协程拦截池
 * @author DTZ
 *
 */
public class Pool extends AbstractUpicContinuationContext implements UpicContinuationInterceptor {

	private ForkJoinPool pool;

	public Pool(ForkJoinPool pool) {
		super(thisKey);
		this.pool = pool;
	}

	/**
	 * 未完善
	 */
	@Override
	public UpicContinuation interceptContinuation(UpicContinuation continuation) {
		return new PoolUpicContinuation(pool, continuation);
	}
	
	
	public static class ThisKey implements Key {

		public ThisKey() {
		}
	}

	public static  Key thisKey = new ThisKey();
}
