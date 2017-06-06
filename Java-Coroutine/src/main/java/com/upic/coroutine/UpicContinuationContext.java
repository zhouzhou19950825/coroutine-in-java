package com.upic.coroutine;

import com.upic.coroutine.Element.Key;
import com.upic.coroutine.enums.StateEnum;
/**
 * 上下文管理器
 * @author DTZ
 *
 */
public interface UpicContinuationContext {
	//获取状态
	public StateEnum state();
	
	public  Element get(Key key);

	public  Object fold(Object obj);

	//返回包含此上下文中的元素和其他上下文的元素。
	//此上下文中的元素具有与另一个相同键的元素被丢弃。
	public  UpicContinuationContext plus(UpicContinuationContext coroutinecontext);

	//移除包涵在此上下文中存在这个key的元素并且返回
	public  UpicContinuationContext minusKey(Key key);
	
}
