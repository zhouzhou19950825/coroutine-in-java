package com.upic.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.upic.coroutine.enums.StateEnum;
import com.upic.coroutine.pool.CommonPool;
import com.upic.coroutine.start.StartCoroutine;
import com.upic.uploadTest.UploadPath;
/**
 * 测试类
 * @author DTZ
 *
 */
public class Test {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("开始上传");
		CommonPool commonPool = CommonPool.getCommonPool();
		UploadPath uploadPath = new UploadPath("www.upic123.com");
		//设置任务取消
		uploadPath.setState(StateEnum.RUN);
		//启动协程
		StartCoroutine.launch( commonPool,uploadPath,new UploadPath("www.upic1234.com")
				,new UploadPath("www.upic12345.com"));
		System.out.println(Thread.getAllStackTraces());
		System.out.println("正在上传...");
		CommonPool.JOINPOOL.awaitTermination(1000, TimeUnit.MILLISECONDS);
	}
}
