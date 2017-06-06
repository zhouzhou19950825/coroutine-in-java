package com.upic.test

import kotlin.coroutines.experimental.*

class StandaloneCoroutine(override val context: CoroutineContext): Continuation<Unit> { 
     override fun resume(value: Unit) {} 
     override fun resumeWithException(exception: Throwable) { 
        //处理异常 
         val currentThread = Thread.currentThread() 
         currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, exception) 
     } 
 }

fun launch(context: CoroutineContext, block: suspend () -> Unit) = 
         block.startCoroutine(StandaloneCoroutine(context))