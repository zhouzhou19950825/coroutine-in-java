package com.upic.coroutine.pool;

import java.util.concurrent.ForkJoinPool;

/**
 * 实现单例
 * @author DTZ
 *
 */
public  class CommonPool extends Pool
{

	private static CommonPool COMMONPOOL;
	static Object obj=new Object();
	public static ForkJoinPool JOINPOOL = ForkJoinPool.commonPool();
	private CommonPool()
	{
		super(JOINPOOL);
		
	}

	public static CommonPool getCommonPool(){
		synchronized (obj) {
			if(COMMONPOOL==null){
				COMMONPOOL=new CommonPool();
			}
		}
		return COMMONPOOL;
	}
}