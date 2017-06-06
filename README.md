# Kotlin-Coroutine-In-Java

>继上次研究kotlin字节码生成以后，接触了下kotlin的协程，并想通过kotlin的协程设计实现原理在java当中也简单能实现，毕竟大家都是跑在jvm上的程序代码。关于kotlin实现协程的基本库是放在kotlin-runtime.jar中的kotlin.coroutines.experimental包下面，kotlin中有个特殊的关键字suspend 是来修饰挂起函数（实质上是其实就是一个Task）。
    
- **kotlin的协程设计实现**
- **Continuation和CoroutineContext**
- **场景**
- **测试结果**


-------------------
##kotlin的协程设计实现
    CoroutineContext(kotlin.coroutines.experimental)，协程的上下文，这个上下文可以是多个的组合，组合的上下文可以通过 key 来获取。

EmptyCoroutineContext 是一个空实现，没有任何功能，如果我们在使用协程时不需要上下文，那么我们就用这个对象作为一个占位即可。

>上下文这个东西，不管大家做什么应用，总是能遇到，比如 Android 里面的 Context，JSP 里面的 PageContext 等等，他们扮演的角色都大同小异：资源管理，数据持有等等，协程的上下文也基本上是如此。


Continuation:
 继续、持续的意思。协程提供了一种暂停的能力，可继续执行才是最终的目的，Continuation 有两个方法，一个是 resume，如果我们的程序没有任何异常，那么直接调用这个方法并传入需要返回的值；另一个是 resumeWithException，如果程序出了异常，那我们可以通过调用这个方法把异常传递出去。
 
    协程的基本操作，包括创建、启动、暂停和继续，继续的操作在 Continuation 当中，剩下的三个都是包级函数或扩展方法：

![这里写图片描述](http://img.blog.csdn.net/20170606135003458?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzg3MjQyOTU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
##java中实现
>Continuation和CoroutineContext

回调接口结构(Continuation)：

![这里写图片描述](http://img.blog.csdn.net/20170606131854114?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzg3MjQyOTU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


上下文管理结构图(CoroutineContext)：

![这里写图片描述](http://img.blog.csdn.net/20170606131950786?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzg3MjQyOTU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
 
## 场景

> 模拟上传图片到一个网址并且模拟等待不同时间,我们可以把每一个上传或者动作看成一个Task

Java:

```
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

```

kotlin：

```
fun uploadFile(path: String): String {
	println("upload to $path.")
	//暂时用这个模拟耗时
	var needTime:Long=((Math.random()+1)*2.5*1000).toLong()
	Thread.sleep(needTime)
	//假设返回上传所需时间
	return needTime.toString()
}

```

Java运行Task的核心代码：

```
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
```


### 测试结果

Kotlin执行测试代码结果：

```
before coroutine
upload to http://www.upic.com.
upload to http://www.upic1234.com.
upload to http://www.upic123.com.

{Thread[Reference Handler,10,system]=[Ljava.lang.StackTraceElement;@372f7a8d, Thread[ForkJoinPool.commonPool-worker-3,5,main]=[Ljava.lang.StackTraceElement;@2f92e0f4, Thread[ForkJoinPool.commonPool-worker-1,5,main]=[Ljava.lang.StackTraceElement;@28a418fc, Thread[main,5,main]=[Ljava.lang.StackTraceElement;@5305068a, Thread[Attach Listener,5,system]=[Ljava.lang.StackTraceElement;@1f32e575, Thread[Signal Dispatcher,9,system]=[Ljava.lang.StackTraceElement;@279f2327, Thread[Finalizer,8,system]=[Ljava.lang.StackTraceElement;@2ff4acd0, Thread[ForkJoinPool.commonPool-worker-2,5,main]=[Ljava.lang.StackTraceElement;@54bedef2}

Second:ForkJoinPool.commonPool-worker-3
three in coroutine. After suspend. result = 3380
firstNameForkJoinPool.commonPool-worker-1
in coroutine. After suspend. result = 4579
Second:ForkJoinPool.commonPool-worker-2
second in coroutine. After suspend. result = 4997

```
Java运行测试结果

```
开始上传
{Thread[Signal Dispatcher,9,system]=[Ljava.lang.StackTraceElement;@42a57993, Thread[CoroutineThread,5,main]=[Ljava.lang.StackTraceElement;@75b84c92, Thread[Finalizer,8,system]=[Ljava.lang.StackTraceElement;@6bc7c054, Thread[Attach Listener,5,system]=[Ljava.lang.StackTraceElement;@232204a1, Thread[Reference Handler,10,system]=[Ljava.lang.StackTraceElement;@4aa298b7, Thread[main,5,main]=[Ljava.lang.StackTraceElement;@7d4991ad}

正在上传...
upload to：www.upic12345.com
upload to：www.upic1234.com
ForkJoinPool.commonPool-worker-2已消费：3606
ForkJoinPool.commonPool-worker-2:上传文件地址:www.upic12345.com :用时:3606
ForkJoinPool.commonPool-worker-1已消费：3765
ForkJoinPool.commonPool-worker-1:上传文件地址:www.upic1234.com :用时:3765

```
如果多运行几次会发现：

```
...
CoroutineThread:上传文件地址:www.upic12345.com :用时:4838
...
```
>CoroutineThread这个是我异步启动的线程，通过协程未被利用的线程可以充分利用起来，不阻塞。


这是我个人分析kotlin协程设计然后实现java的，可能会存在很多瑕疵问题，需要大家多多指教~~
大家也可以下载代码，在本地运行。
