package com.upic.coroutine.workthread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.upic.coroutine.UpicContinuation;
import com.upic.coroutine.UpicContinuationContext;
import com.upic.coroutine.enums.StateEnum;
import com.upic.coroutine.pool.CommonPool;
import com.upic.coroutine.start.StandaloneCoroutine;
import com.upic.uploadTest.UploadPath;
import com.upic.workQueue.Constant;

/**
 * Task运行环境
 * 
 * @author DTZ
 *
 */
public class UploadTask extends RecursiveTask<List<String>> {
	private List<String> result;
	private List<UploadTask> mTasks;
	private UpicContinuationContext continuationContext;
	private CommonPool commonPool;

	public UploadTask(CommonPool commonPool, UpicContinuationContext continuationContext) {
		this.commonPool = commonPool;
		this.continuationContext = continuationContext;
	}
	@Override
	protected List<String> compute() {
		result = new ArrayList<>();
		UpicContinuation interceptContinuation = null;
		if (continuationContext != null && this.continuationContext.state().equals(StateEnum.RUN)) {
			try {
				interceptContinuation = commonPool.interceptContinuation(new StandaloneCoroutine(continuationContext));
				String uploadFile = uploadFile(((UploadPath) interceptContinuation.getContext()).getPath());
				interceptContinuation.resume(uploadFile);
				System.out.println(Thread.currentThread().getName() + ":上传文件地址:"
						+ ((UploadPath) interceptContinuation.getContext()).getPath() + " :用时:" + uploadFile);
				result.add(uploadFile);
				// throw new Exception(); //测试异常时异常返回
			} catch (Exception e) {
				interceptContinuation.resumeWithException(e);
			}
		} else {
			mTasks = new ArrayList<>();
			//此操作 线程不安全，只是为了测试
			int size = Constant.upWorkQueue.size();
			for (int i = 0; i < size; i++) {
				UploadTask uploadTask = Constant.upWorkQueue.get();
				switch (uploadTask.continuationContext.state()) {
				case RUN:
					mTasks.add(uploadTask);
					break;
				case YIELD:
					// 其实1是为了简便观察，如果剩下的任务都是YIELD，其实也是可以都去执行
					// 其实这个设计是存在问题的，YEILD应该等其他Task工作完毕后再去工作，
					// 按常理应该还要个时间调度器去调度任务
					// 事实上，Task自身已经实现了这些方法
					if (size == 1) {
						((UploadPath) uploadTask.continuationContext).setState(StateEnum.RUN);
						mTasks.add(uploadTask);
					} else {
						mTasks.add(uploadTask);
						// 放到队尾巴
						Constant.upWorkQueue.put(uploadTask);
					}
					break;
				case CANECL:
					// ...
					uploadTask.cancel(true);
					break;
				case JOIN:
					uploadTask.join();
					mTasks.add(uploadTask);
					// ...
					break;
				default:
					break;
				}

			}
			//开始执行所有Task
			invokeAll(mTasks);
		}
		return result;
	}

	// 模拟上传时间
	public static String uploadFile(String path) {
		System.out.println("upload to：" + path);
		// 暂时用这个模拟耗时
		long needTime = (long) ((Math.random() + 1) * 2.5 * 1000);
		try {
			Thread.sleep(needTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 假设返回上传所需时间
		return needTime + "";
	}
}