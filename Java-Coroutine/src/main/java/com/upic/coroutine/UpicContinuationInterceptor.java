package com.upic.coroutine;
/**
 * 拦截器
 * @author DTZ
 *
 */
public interface UpicContinuationInterceptor extends Element{
	public  UpicContinuation interceptContinuation(UpicContinuation continuation);

}
