package com.upic.coroutine;
/**
 * 
 * @author DTZ
 *
 * @param <T>
 */
public interface UpicContinuation {
public UpicContinuationContext getContext(); 	
//回调方法
public void resume(Object t);
//发生异常时调用的方法
public void resumeWithException(Throwable exception);
}
