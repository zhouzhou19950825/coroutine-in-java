package com.upic.coroutines

import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine
import com.sun.org.apache.bcel.internal.generic.CPInstruction
fun main(args: Array<String>) {
	println("before coroutine")
//	val a=UploadPath("http://www.upic.com").plus(CommonPool);
	//启动我们的协程 
	launch(UploadPath("http://www.upic.com") + CommonPool) {
//		println("in coroutine. Before suspend.")
		//暂停我们的线程，并开始执行一段耗时操作 
		val result: String = suspendCoroutine {
			continuation ->
//			println("in suspend block.")
			continuation.resume(uploadFile(continuation.context[UploadPath]!!.path))
//			println("after resume.")
			println("firstName"+Thread.currentThread().getName());
		}
		println("in coroutine. After suspend. result = $result")
	}
	launch(UploadPath("http://www.upic123.com") + CommonPool) {
//		println("second in coroutine. Before suspend.")
		//暂停我们的线程，并开始执行一段耗时操作 
		val result: String = suspendCoroutine {
			continuation ->
//			println("second in suspend block.")
			continuation.resume(uploadFile(continuation.context[UploadPath]!!.path))
//			println("second after resume.")
			println("Second:"+Thread.currentThread().getName());
		}
		println("second in coroutine. After suspend. result = $result")
	}
	launch(UploadPath("http://www.upic1234.com") + CommonPool) {
//		println("second in coroutine. Before suspend.")
		//暂停我们的线程，并开始执行一段耗时操作 
		val result: String = suspendCoroutine {
			continuation ->
//			println("second in suspend block.")
			continuation.resume(uploadFile(continuation.context[UploadPath]!!.path))
//			println("second after resume.")
			println("Second:"+Thread.currentThread().getName());
		}
		println("three in coroutine. After suspend. result = $result")
	}
	
//	println("after coroutine")
//	println("Main:"+Thread.currentThread().getName());
	println(Thread.getAllStackTraces());
	 //加这句的原因是防止程序在协程运行完之前停止 
     CommonPool.pool.awaitTermination(10000, TimeUnit.MILLISECONDS) 
}

/**
 * 上下文，用来存放我们需要的信息，可以灵活的自定义
 */
class UploadPath(val path: String) : AbstractCoroutineContextElement(UploadPath) {
	companion object Key : CoroutineContext.Key<UploadPath>
}


fun uploadFile(path: String): String {
	println("upload to $path.")
	//暂时用这个模拟耗时
	var needTime:Long=((Math.random()+1)*2.5*1000).toLong()
	Thread.sleep(needTime)
	//假设返回上传所需时间
	return needTime.toString()
}
