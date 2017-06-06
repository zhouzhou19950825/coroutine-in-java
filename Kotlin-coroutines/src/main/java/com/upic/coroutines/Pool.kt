package com.upic.test

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinWorkerThread
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.ContinuationInterceptor

open class Pool(val pool: ForkJoinPool)
	: AbstractCoroutineContextElement(ContinuationInterceptor),
		ContinuationInterceptor {
	override fun <T> interceptContinuation(continuation: Continuation<T>)
			: Continuation<T> =
			PoolContinuation(pool,
					//下面这段代码是要查找其他拦截器，并保证能调用它们的拦截方法 
					continuation.context.fold(continuation, { cont, element ->
						if (element != this@Pool && element is ContinuationInterceptor)
							element.interceptContinuation(cont) else cont
					}))
}

private class PoolContinuation<T>(
		val pool: ForkJoinPool,
		val continuation: Continuation<T>
) : Continuation<T> by continuation {
	override fun resume(value: T) {
		if (isPoolThread()) continuation.resume(value)
		else pool.execute { continuation.resume(value) }
	}

	override fun resumeWithException(exception: Throwable) {
		if (isPoolThread()) continuation.resumeWithException(exception)
		else pool.execute { continuation.resumeWithException(exception) }
	}

	fun isPoolThread(): Boolean = (Thread.currentThread() as? ForkJoinWorkerThread)?.pool == pool
}

object CommonPool : Pool(ForkJoinPool.commonPool())