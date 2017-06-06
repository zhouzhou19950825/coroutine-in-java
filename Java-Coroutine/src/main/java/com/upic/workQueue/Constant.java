package com.upic.workQueue;

import com.upic.coroutine.workthread.UploadTask;

public class Constant {

	public static WorkQueue<UploadTask> upWorkQueue=new WorkQueue<UploadTask>(10);
}
